package com.github.darkpred.nocreativedrift.client.config;

/**
 * Config Entry with optional comment
 */
public class Entry {
    private final String comment;
    private final String id;
    private String val;

    public Entry(String id, String val) {
        this.comment = null;
        this.id = id;
        this.val = val;
    }

    public Entry(String comment, String id, String val) {
        this.comment = comment;
        this.id = id;
        this.val = val;
    }

    public String getFullEntry() {
        String ret = "";
        if (comment != null) {
            ret = comment + "\n";
        }
        return ret + id + "=" + val;
    }

    public String getId() {
        return id;
    }

    public void setVal(String val) {
        this.val = val;
    }
}
