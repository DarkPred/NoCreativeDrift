package com.github.darkpred.nocreativedrift.client.config;

/**
 * Config entry with optional comment
 */
public class Entry {
    private final String comment;
    private final String id;
    private String val;

    /**
     * Constructs a config entry without a comment above it
     *
     * @param id  the id of the entry
     * @param val the default value of the entry
     */
    public Entry(String id, String val) {
        this(null, id, val);
    }

    /**
     * Constructs a config entry with a comment above it
     *
     * @param comment the comment above the entry. Every line needs to begin with #
     * @param id      the id of the entry
     * @param val     the default value of the entry
     */
    public Entry(String comment, String id, String val) {
        this.comment = comment;
        this.id = id;
        this.val = val;
    }

    /**
     * Returns a String of the full entry with comments. Has no line break at the beginning or end
     *
     * @return a String of the full entry with comments
     */
    public String getFullEntry() {
        String ret = "";
        if (comment != null) {
            ret = comment + "\n";
        }
        return ret + id + "=" + val;
    }

    /**
     * Returns the id of the entry. Used to update or get the value
     *
     * @return the id of the entry
     */
    public String getId() {
        return id;
    }

    /**
     * Updates the value of the entry for the current minecraft session
     *
     * @param val the new value of the entry
     */
    public void setVal(String val) {
        this.val = val;
    }
}
