package com.github.darkpred.nocreativedrift.client.config;

/**
 * Config entry with optional comment
 */
public abstract class Entry {
    private final String comment;
    private final String id;
    private String val;
    private boolean dirty;

    /**
     * Constructs a config entry without a comment above it
     *
     * @param id  the id of the entry
     * @param val the default value of the entry
     */
    protected Entry(String id, String val) {
        this(null, id, val);
    }

    /**
     * Constructs a config entry with a comment above it
     *
     * @param comment the comment above the entry. Every line needs to begin with #
     * @param id      the id of the entry
     * @param val     the default value of the entry
     */
    protected Entry(String comment, String id, String val) {
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
     * Returns the current value of the entry. Might not be written to file yet
     *
     * @return the current value of the entry
     */
    public String getVal() {
        return val;
    }

    /**
     * Updates the value of the entry for the current minecraft session
     *
     * @param val the new value of the entry
     */
    public void setVal(String val) {
        this.val = val;
    }

    public abstract boolean isValidValue(String val);

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }
}
