package com.github.darkpred.nocreativedrift.client.config;

import java.util.ArrayList;
import java.util.List;

public class ClientConfig {
    public static final List<Entry> ENTRIES = new ArrayList<>();
    public static final int LATEST_VERSION = 2; //The latest version of the config file to check updates

    public static final SimpleConfig CONFIG;

    static {
        ENTRIES.add(new Entry("Version", String.valueOf(LATEST_VERSION)));
        ENTRIES.add(new Entry("#Disable the drift during vertical flight", "disableVerticalDrift", "false"));
        ENTRIES.add(new Entry("#Enable a key bind that toggles drift in game", "enableToggleKeyBind", "false"));
        ENTRIES.add(new Entry("#Enable whether a message should be shown when the drift is disabled", "enableHudMessage", "false"));
        CONFIG = SimpleConfig.of("nocreativedrift").provider(namespace ->
                ENTRIES
        ).request(LATEST_VERSION);
    }
}
