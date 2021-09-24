package darkpred.nocreativedrift.config;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * Class holding the client config entries
 */
public class ClientConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.BooleanValue disableJetpackDrift;
    public static final ForgeConfigSpec.BooleanValue enableToggleKeyBind;
    public static final ForgeConfigSpec.BooleanValue enableHudMessage;
    public static final ForgeConfigSpec.BooleanValue enableHudFading;
    public static final ForgeConfigSpec.DoubleValue hudOffset;
    public static final ForgeConfigSpec.BooleanValue disableNonCreativeDrift;
    public static final ForgeConfigSpec.BooleanValue disableVerticalDrift;
    public static final ForgeConfigSpec.IntValue driftStrength;

    static {
        disableVerticalDrift = BUILDER
                .comment("Disable the drift during vertical flight")
                .define("disableVerticalDrift", false);
        BUILDER.push("keybinding");
        enableToggleKeyBind = BUILDER
                .comment("Enable a key bind that toggles drift in game")
                .define("enableToggleKeyBind", false);
        enableHudMessage = BUILDER
                .comment("Enable a hud message that displays the current drift strength", "I recommend this if enableToggleKeyBind is set to true")
                .define("enableHudMessage", false);
        enableHudFading = BUILDER
                .comment("If enabled the hud message will only be visible for a few seconds after changing the strength")
                .define("enableHudFading", false);
        hudOffset = BUILDER
                .comment("Vertical position of the hud message on the left side (0 is top, 0.95 is bottom)")
                .defineInRange("hudOffset", 0.4, 0, 0.95);
        BUILDER.pop();
        BUILDER.push("jetpack");
        disableJetpackDrift = BUILDER
                .comment("Disable the drift on jetpacks.")
                .define("disableJetpackDrift", true);
        disableNonCreativeDrift = BUILDER
                .comment("Disable the drift during non creative flight (e.g. Angel Ring mod)")
                .define("disableNonCreativeDrift", false);
        BUILDER.pop();
        BUILDER.push("other");
        driftStrength = BUILDER
                .comment("The current drift strength. {Vanilla:0, Strong:1, Weak:2, Disabled:3}")
                .defineInRange("driftStrength", 3, 0, 3);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }

    private ClientConfig() {
    }

    public static boolean isRuleEnabled(ForgeConfigSpec.BooleanValue rule) {
        return Boolean.TRUE.equals(rule.get());
    }
}
