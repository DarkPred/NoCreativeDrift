package com.github.darkpred.nocreativedrift.client;

/**
 * The strength categories with their multiplier
 */
public enum Drift {
    VANILLA(1),
    STRONG(0.90),
    WEAK(0.7),
    DISABLED(0);

    private final double multi;

    Drift(double multi) {
        this.multi = multi;
    }

    /**
     * Returns the strength multiplier of this category. Needs to be high because the vanilla drift is exponential
     *
     * @return the strength multiplier of this category
     */
    public double getMulti() {
        return multi;
    }
}
