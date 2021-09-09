package darkpred.nocreativedrift.client;

import darkpred.nocreativedrift.NoCreativeDrift;
import net.minecraft.util.text.TranslationTextComponent;

/**
 * The strength categories with their multiplier
 */
public enum Drift {
    VANILLA(1, "vanilla"),
    STRONG(0.90, "strong"),
    WEAK(0.7, "weak"),
    DISABLED(0, "disabled");

    private final double multi;
    private final TranslationTextComponent textComponent;

    Drift(double multi, String name) {
        this.multi = multi;
        textComponent = new TranslationTextComponent("hud." + NoCreativeDrift.MOD_ID + ".drift_strength_" + name);
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
     * Returns the {@code TranslationTextComponent} of this category. Key: "hud.nocreativedrift.drift_strength_categoryName"
     *
     * @return the {@code TranslationTextComponent} of this category
     */
    public TranslationTextComponent getTextComponent() {
        return textComponent;
    }
}
