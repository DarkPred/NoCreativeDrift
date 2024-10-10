package com.github.darkpred.nocreativedrift;

import com.google.auto.service.AutoService;
import net.minecraftforge.common.ForgeConfigSpec;

/**
 * Class holding the client config entries
 */
@AutoService(ClientConfig.class)
public class ForgeClientConfig implements ClientConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.BooleanValue DISABLE_JETPACK_DRIFT;
    public static final ForgeConfigSpec.BooleanValue ENABLE_TOGGLE_KEY_BIND;
    public static final ForgeConfigSpec.BooleanValue SAVE_CONFIG_ON_TOGGLE;
    public static final ForgeConfigSpec.BooleanValue ENABLE_HUD_MESSAGE;
    public static final ForgeConfigSpec.BooleanValue ENABLE_HUD_FADING;
    public static final ForgeConfigSpec.DoubleValue HUD_OFFSET;
    public static final ForgeConfigSpec.BooleanValue DISABLE_NON_CREATIVE_DRIFT;
    public static final ForgeConfigSpec.BooleanValue DISABLE_VERTICAL_DRIFT;
    public static final ForgeConfigSpec.IntValue DRIFT_STRENGTH;

    static {
        DISABLE_VERTICAL_DRIFT = entry("disableVerticalDrift", false,
                "Disable the drift during vertical flight");

        BUILDER.push("keybinding");

        ENABLE_TOGGLE_KEY_BIND = entry("enableToggleKeyBind", false,
                "Enable the option to toggle between different drift strengths");
        SAVE_CONFIG_ON_TOGGLE = entry("saveConfigOnToggle", true,
                "If enabled every change of drift strength will trigger a config save");
        ENABLE_HUD_MESSAGE = entry("enableHudMessage", false,
                "Enable a hud message that displays the current drift strength", "I recommend this if enableToggleKeyBind is set to true");
        ENABLE_HUD_FADING = entry("enableHudFading", false,
                "If enabled the hud message will only be visible for a few seconds after changing the strength");
        HUD_OFFSET = entry("hudOffset", 0.4, 0, 0.95,
                "Vertical position of the hud message on the left side (0 is top, 0.95 is bottom)");

        BUILDER.pop();
        BUILDER.push("jetpack");

        DISABLE_JETPACK_DRIFT = entry("disableJetpackDrift", true,
                "Disable the drift on jetpacks");
        DISABLE_NON_CREATIVE_DRIFT = entry("disableNonCreativeDrift", true,
                "Enable the drift during non creative flight (e.g. Angel Ring mod)");

        BUILDER.pop();
        BUILDER.push("other");

        DRIFT_STRENGTH = entry("driftStrength", 3, 0, 3,
                "The current drift strength. {Vanilla:0, Strong:1, Weak:2, Disabled:3}");
        BUILDER.pop();

        SPEC = BUILDER.build();
    }

    private static ForgeConfigSpec.BooleanValue entry(String path, boolean defaultValue, String... comments) {
        return BUILDER.comment(comments).translation(NoCreativeDriftMod.MOD_ID + ".midnightconfig." + path).define(path, defaultValue);
    }

    private static ForgeConfigSpec.IntValue entry(String path, int defaultValue, int min, int max, String... comments) {
        return BUILDER.comment(comments).translation(NoCreativeDriftMod.MOD_ID + ".midnightconfig." + path).defineInRange(path, defaultValue, min, max);
    }

    private static ForgeConfigSpec.DoubleValue entry(String path, double defaultValue, double min, double max, String... comments) {
        return BUILDER.comment(comments).translation(NoCreativeDriftMod.MOD_ID + ".midnightconfig." + path).defineInRange(path, defaultValue, min, max);
    }

    @Override
    public boolean disableVerticalDrift() {
        return DISABLE_VERTICAL_DRIFT.get();
    }

    @Override
    public boolean enableToggleKeyBind() {
        return ENABLE_TOGGLE_KEY_BIND.get();
    }

    @Override
    public boolean enableHudMessage() {
        return ENABLE_HUD_MESSAGE.get();
    }

    @Override
    public boolean enableHudFading() {
        return ENABLE_HUD_FADING.get();
    }

    @Override
    public boolean enableNonCreativeDrift() {
        return !DISABLE_NON_CREATIVE_DRIFT.get();
    }

    @Override
    public boolean disableJetpackDrift() {
        return DISABLE_JETPACK_DRIFT.get();
    }

    @Override
    public int driftStrength() {
        return DRIFT_STRENGTH.get();
    }

    @Override
    public void setDriftStrength(int driftStrength) {
        DRIFT_STRENGTH.set(driftStrength);
        if (Boolean.TRUE.equals(SAVE_CONFIG_ON_TOGGLE.get())) {
            SPEC.save();
        }
    }
}
