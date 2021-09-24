package com.github.darkpred.nocreativedrift.client.config;

import java.util.ArrayList;
import java.util.List;

/**
 * Class holding the client config entries and latest version
 */
public class ClientConfig {
    public static final List<Entry> ENTRIES = new ArrayList<>();
    public static final int LATEST_VERSION = 4; //The latest version of the config file to check updates

    public static final SimpleConfig CONFIG;

    static {
        ENTRIES.add(new IntEntry("Version", LATEST_VERSION));
        ENTRIES.add(new BoolEntry("#Disable the drift during vertical flight", "disableVerticalDrift", false));
        ENTRIES.add(new BoolEntry("#Disable the drift on jetpacks", "disableJetpackDrift", false));
        ENTRIES.add(new BoolEntry("#Enable a key bind that toggles drift in game", "enableToggleKeyBind", false));
        ENTRIES.add(new BoolEntry("#Enable a hud message that displays the current drift strength " +
                "\n#I recommend this if enableToggleKeyBind is set to true", "enableHudMessage", false));

        CONFIG = SimpleConfig.of("nocreativedrift").provider(namespace ->
                ENTRIES
        ).request(LATEST_VERSION);
    }
}
