package com.github.darkpred.nocreativedrift.client.config;

public class IntEntry extends Entry {
    public IntEntry(String id, int val) {
        this(null, id, val);
    }

    public IntEntry(String comment, String id, int val) {
        super(comment, id, String.valueOf(val));
    }

    @Override
    public boolean isValidValue(String val) {
        try {
            Integer.parseInt(val);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static class Range extends IntEntry {
        private int min;
        private int max;

        public Range(String id, int val, int min, int max) {
            this(null, id, val, min, max);
        }

        public Range(String comment, String id, int val, int min, int max) {
            super(comment, id, val);
            this.min = min;
            this.max = max;
        }

        @Override
        public boolean isValidValue(String val) {
            try {
                int v = Integer.parseInt(val);
                if (v >= min && v <= max) {
                    return true;
                }
            } catch (NumberFormatException e) {
                return false;
            }
            return false;
        }
    }
}