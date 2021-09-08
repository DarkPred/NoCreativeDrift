package com.github.darkpred.nocreativedrift.client.config;

import java.util.ArrayList;
import java.util.List;

public class ClientConfig {
    public static final List<Entry> ENTRIES = new ArrayList<>();
    public static final int LATEST_VERSION = 4; //The latest version of the config file to check updates

    public static final SimpleConfig CONFIG;

    static {
        ENTRIES.add(new Entry("Version", String.valueOf(LATEST_VERSION)));
        ENTRIES.add(new Entry("#Disable the drift during vertical flight", "disableVerticalDrift", "false"));
        ENTRIES.add(new Entry("#Disable the drift on jetpacks", "disableJetpackDrift", "false"));
        ENTRIES.add(new Entry("#Enable a key bind that toggles drift in game", "enableToggleKeyBind", "false"));
        ENTRIES.add(new Entry("#Enable a hud message that displays the current drift strength " +
                "\n#I recommend this if enableToggleKeyBind is set to true", "enableHudMessage", "false"));

        CONFIG = SimpleConfig.of("nocreativedrift").provider(namespace ->
                ENTRIES
        ).request(LATEST_VERSION);
    }
}
