package com.github.darkpred.nocreativedrift.client.config;

public class BoolEntry extends Entry {
    public BoolEntry(String id, boolean val) {
        this(null, id, val);
    }

    public BoolEntry(String comment, String id, boolean val) {
        super(comment, id, String.valueOf(val));
    }

    @Override
    public boolean isValidValue(String val) {
        return "true".equals(val) || "false".equals(val);
    }
}