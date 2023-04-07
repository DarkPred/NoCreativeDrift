package darkpred.nocreativedrift.config;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * Class holding the client config entries
 */
public class ClientConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.BooleanValue DISABLE_JETPACK_DRIFT;
    public static final ForgeConfigSpec.BooleanValue ENABLE_HUD_MESSAGE;
    public static final ForgeConfigSpec.BooleanValue ENABLE_HUD_FADING;
    public static final ForgeConfigSpec.DoubleValue HUD_OFFSET;
    public static final ForgeConfigSpec.BooleanValue DISABLE_NON_CREATIVE_DRIFT;
    public static final ForgeConfigSpec.BooleanValue DISABLE_VERTICAL_DRIFT;
    public static final ForgeConfigSpec.IntValue DRIFT_STRENGTH;
    public static final ForgeConfigSpec.BooleanValue ENABLE_CONTROLLER_SUPPORT;

    static {
        DISABLE_VERTICAL_DRIFT = BUILDER
                .comment("Disable the drift during vertical flight")
                .define("disableVerticalDrift", false);
        BUILDER.push("keybinding");
        ENABLE_HUD_MESSAGE = BUILDER
                .comment("Enable a hud message that displays the current drift strength", "I recommend this if enableToggleKeyBind is set to true")
                .define("enableHudMessage", false);
        ENABLE_HUD_FADING = BUILDER
                .comment("If enabled the hud message will only be visible for a few seconds after changing the strength")
                .define("enableHudFading", false);
        HUD_OFFSET = BUILDER
                .comment("Vertical position of the hud message on the left side (0 is top, 0.95 is bottom)")
                .defineInRange("hudOffset", 0.4, 0, 0.95);
        BUILDER.pop();
        BUILDER.push("jetpack");
        DISABLE_JETPACK_DRIFT = BUILDER
                .comment("Disable the drift on jetpacks.")
                .define("disableJetpackDrift", true);
        DISABLE_NON_CREATIVE_DRIFT = BUILDER
                .comment("Disable the drift during non creative flight (e.g. Angel Ring mod)")
                .define("disableNonCreativeDrift", false);
        BUILDER.pop();
        BUILDER.push("other");
        DRIFT_STRENGTH = BUILDER
                .comment("The current drift strength. {Vanilla:0, Strong:1, Weak:2, Disabled:3}")
                .defineInRange("driftStrength", 3, 0, 3);
        ENABLE_CONTROLLER_SUPPORT = BUILDER
                .comment("Enable support for controller movement. Requires the Controllable Mod")
                .define("enableControllerSupport", true);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }

    private ClientConfig() {
    }

    public static boolean isRuleEnabled(ForgeConfigSpec.BooleanValue rule) {
        return Boolean.TRUE.equals(rule.get());
    }
}
