package com.github.darkpred.nocreativedrift.client.config;

import java.util.ArrayList;
import java.util.List;

public class ClientConfig {
    public static final List<Entry> ENTRIES = new ArrayList<>();
    public static final int LATEST_VERSION = 1; //The latest version of the config file to check updates

    public static final SimpleConfig CONFIG;

    static {
        ENTRIES.add(new Entry("Version", "1"));
        ENTRIES.add(new Entry("#Disable the drift during vertical flight", "disableVerticalDrift", "false"));
        ENTRIES.add(new Entry("#Disable the drift on jetpacks", "disableJetpackDrift", "false"));
        CONFIG = SimpleConfig.of("nocreativedrift").provider(namespace ->
                ENTRIES
        ).request(LATEST_VERSION);
    }
}
