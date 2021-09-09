package com.github.darkpred.nocreativedrift.client;

import net.minecraft.text.TranslatableText;

/**
 * The strength categories with their multiplier
 */
public enum Drift {
    VANILLA(1, "vanilla"),
    STRONG(0.9, "strong"),
    WEAK(0.7, "weak"),
    DISABLED(0, "disabled");

    private final double multi;
    private final TranslatableText text;

    Drift(double multi, String name) {
        this.multi = multi;
        text = new TranslatableText("hud.nocreativedrift.drift_strength_" + name);
    }

    /**
     * Returns the strength multiplier of this category. Needs to be high because the vanilla drift is exponential
     *
     * @return the strength multiplier of this category
     */
    public double getMulti() {
        return multi;
    }

    /**
     * Returns the {@code TranslatableText} of this category. Key: "hud.nocreativedrift.drift_strength_categoryName"
     *
     * @return the {@code TranslatableText} of this category
     */
    public TranslatableText getText() {
        return text;
    }
}
