package com.github.darkpred.nocreativedrift.client.config;

/*
 * Copyright (c) 2021 magistermaks
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

import com.github.darkpred.nocreativedrift.client.WorldSaveCallback;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Copied from <a href="https://github.com/magistermaks/fabric-simplelibs/tree/master/simple-config">Here</a>
 * and slightly modified to automatically update outdated configs and save on world save
 */
public class SimpleConfig {

    private static final Logger LOGGER = LogManager.getLogger("SimpleConfig");
    private final HashMap<String, String> config = new HashMap<>();
    private final ConfigRequest request;
    private boolean broken = false;
    private boolean dirty = false;

    private SimpleConfig(ConfigRequest request, int version) {
        this.request = request;
        String identifier = "Config '" + request.filename + "'";

        if (!request.file.exists()) {
            LOGGER.info("{} is missing, generating default one...", identifier);

            try {
                createConfig();
            } catch (IOException e) {
                LOGGER.error("{} failed to generate!", identifier);
                LOGGER.trace(e);
                broken = true;
            }
        }

        if (!broken) {
            try {
                loadConfig();
            } catch (IOException e) {
                LOGGER.error("{} failed to load!", identifier);
                LOGGER.error(e);
                broken = true;
            }
            if (getOrDefault("Version", 0) != version) {
                LOGGER.info("{} is outdated, updating lines...", identifier);
                try {
                    updateConfig();
                    loadConfig();
                } catch (IOException e) {
                    LOGGER.error("{} failed to update! Config not loaded", identifier);
                    LOGGER.error(e);
                    broken = true;
                }
            }
        }
        WorldSaveCallback.EVENT.register(() -> {
            if (dirty) {
                dirty = false;
                try {
                    PrintWriter writer = new PrintWriter(request.file, "UTF-8");
                    for (Entry entry : request.getConfig()) {
                        if (entry.isDirty()) {
                            entry.setDirty(false);
                        } else {
                            entry.setVal(config.get(entry.getId()));
                        }
                        writer.write(entry.getFullEntry() + "\n");
                    }
                    writer.close();
                    loadConfig();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Creates new config request object, ideally `namespace`
     * should be the name of the mod id of the requesting mod
     *
     * @param filename - name of the config file
     * @return new config request object
     */
    public static ConfigRequest of(String filename) {
        Path path = FabricLoader.getInstance().getConfigDir();
        return new ConfigRequest(path.resolve(filename + ".properties").toFile(), filename);
    }

    private void createConfig() throws IOException {

        // try creating missing files
        request.file.getParentFile().mkdirs();
        Files.createFile(request.file.toPath());

        // write default config data
        PrintWriter writer = new PrintWriter(request.file, "UTF-8");
        for (Entry entry : request.getConfig()) {
            writer.write(entry.getFullEntry() + "\n");
        }
        writer.close();

    }

    /**
     * Updates the config by saving values that should be copied over to the updated file and then rewriting the entire file
     *
     * @throws IOException if the file is not found
     */
    private void updateConfig() throws IOException {
        //Finds all correct config entries that should be copied over to updated file
        Map<String, String> ids = new HashMap<>();
        try (Scanner reader = new Scanner(request.file)) {
            for (int line = 1; reader.hasNextLine(); line++) {
                try {
                    parseConfigEntry(reader.nextLine(), line, ids);
                } catch (IOException e) {
                    //skip
                }
            }
        }
        //Update current list of entries with old values
        for (Entry entry : request.getConfig()) {
            String id = entry.getId();
            String val = ids.get(id);
            if (ids.containsKey(id) && !id.equals("Version")) {
                if (entry.isValidValue(val)) {
                    entry.setVal(val);
                }
            }
        }
        //Rewrites config file with old values and new options
        PrintWriter writer = new PrintWriter(request.file, "UTF-8");
        writer.write(request.getConfig().stream().map(Entry::getFullEntry).collect(Collectors.joining("\n")));
        writer.close();
    }

    private void loadConfig() throws IOException {
        try (Scanner reader = new Scanner(request.file)) {
            for (int line = 1; reader.hasNextLine(); line++) {
                parseConfigEntry(reader.nextLine(), line, config);
            }
        }
    }

    private void parseConfigEntry(String entry, int line, Map<String, String> map) throws IOException {
        if (!entry.isEmpty() && !entry.startsWith("#")) {
            String[] parts = entry.split("=", 2);
            if (parts.length == 2) {
                map.put(parts[0], parts[1]);
            } else {
                throw new IOException("Syntax error in config file on line " + line + "!");
            }
        }
    }

    /**
     * Queries a value from config, returns `null` if the
     * key does not exist.
     *
     * @return value corresponding to the given key
     * @see SimpleConfig#getOrDefault
     */
    public String get(String key) {
        return config.get(key);
    }

    /**
     * Returns string value from config corresponding to the given
     * key, or the default string if the key is missing.
     *
     * @return value corresponding to the given key, or the default value
     */
    public String getOrDefault(String key, String def) {
        String val = get(key);
        return val == null ? def : val;
    }

    /**
     * Returns integer value from config corresponding to the given
     * key, or the default integer if the key is missing or invalid.
     *
     * @return value corresponding to the given key, or the default value
     */
    public int getOrDefault(String key, int def) {
        try {
            return Integer.parseInt(get(key));
        } catch (Exception e) {
            return def;
        }
    }

    /**
     * Returns boolean value from config corresponding to the given
     * key, or the default boolean if the key is missing.
     *
     * @return value corresponding to the given key, or the default value
     */
    public boolean getOrDefault(String key, boolean def) {
        String val = get(key);
        if (val != null) {
            return val.equalsIgnoreCase("true");
        }

        return def;
    }

    /**
     * Returns double value from config corresponding to the given
     * key, or the default string if the key is missing or invalid.
     *
     * @return value corresponding to the given key, or the default value
     */
    public double getOrDefault(String key, double def) {
        try {
            return Double.parseDouble(get(key));
        } catch (Exception e) {
            return def;
        }
    }

    public void set(String key, boolean def) {
        if (def != Boolean.parseBoolean(get(key))) {
            config.put(key, String.valueOf(def));
            dirty = true;
            for (Entry entry : request.getConfig()) {
                if (entry.getId().equals(key)) {
                    entry.setVal(String.valueOf(def));
                    entry.setDirty(true);
                }
            }
        }
    }

    public void set(String key, int def) {
        if (def != Integer.parseInt(get(key))) {
            config.put(key, String.valueOf(def));
            dirty = true;
            for (Entry entry : request.getConfig()) {
                if (entry.getId().equals(key)) {
                    entry.setVal(String.valueOf(def));
                    entry.setDirty(true);
                }
            }
        }
    }

    /**
     * If any error occurred during loading or reading from the config
     * a 'broken' flag is set, indicating that the config's state
     * is undefined and should be discarded using `delete()`
     *
     * @return the 'broken' flag of the configuration
     */
    public boolean isBroken() {
        return broken;
    }

    /**
     * deletes the config file from the filesystem
     *
     * @return true if the operation was successful
     */
    public boolean delete() {
        LOGGER.warn("Config '{}' was removed from existence! Restart the game to regenerate it.", request.filename);
        return request.file.delete();
    }

    public interface DefaultConfig {
        static List<Entry> empty(String namespace) {
            return new ArrayList<>();
        }

        List<Entry> get(String namespace);
    }

    public static class ConfigRequest {

        private final File file;
        private final String filename;
        private DefaultConfig provider;

        private ConfigRequest(File file, String filename) {
            this.file = file;
            this.filename = filename;
            this.provider = DefaultConfig::empty;
        }

        /**
         * Sets the default config provider, used to generate the
         * config if it's missing.
         *
         * @param provider default config provider
         * @return current config request object
         * @see DefaultConfig
         */
        public ConfigRequest provider(DefaultConfig provider) {
            this.provider = provider;
            return this;
        }

        /**
         * Loads the config from the filesystem.
         *
         * @return config object
         * @see SimpleConfig
         */
        public SimpleConfig request(int version) {
            return new SimpleConfig(this, version);
        }

        private List<Entry> getConfig() {
            return provider.get(filename);
        }

    }

}