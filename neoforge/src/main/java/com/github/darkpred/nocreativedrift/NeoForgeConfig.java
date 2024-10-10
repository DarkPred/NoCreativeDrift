package com.github.darkpred.nocreativedrift;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class NeoForgeConfig {
    public static final Pair<NeoForgeConfig, ModConfigSpec> CONFIG = new ModConfigSpec.Builder().configure(NeoForgeConfig::new);
    private final ModConfigSpec.Builder builder;
    public final ModConfigSpec.BooleanValue DISABLE_JETPACK_DRIFT;
    public final ModConfigSpec.BooleanValue ENABLE_TOGGLE_KEY_BIND;
    public final ModConfigSpec.BooleanValue SAVE_CONFIG_ON_TOGGLE;
    public final ModConfigSpec.BooleanValue ENABLE_HUD_MESSAGE;
    public final ModConfigSpec.BooleanValue ENABLE_HUD_FADING;
    public final ModConfigSpec.DoubleValue HUD_OFFSET;
    public final ModConfigSpec.BooleanValue DISABLE_NON_CREATIVE_DRIFT;
    public final ModConfigSpec.BooleanValue DISABLE_VERTICAL_DRIFT;
    public final ModConfigSpec.IntValue DRIFT_STRENGTH;

    public NeoForgeConfig(ModConfigSpec.Builder builder) {
        this.builder = builder;
        DISABLE_VERTICAL_DRIFT = entry("disableVerticalDrift", false,
                "Disable the drift during vertical flight");

        builder.push("keybinding");

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

        builder.pop();
        builder.push("jetpack");

        DISABLE_JETPACK_DRIFT = entry("disableJetpackDrift", true,
                "Disable the drift on jetpacks");
        DISABLE_NON_CREATIVE_DRIFT = entry("disableNonCreativeDrift", true,
                "Enable the drift during non creative flight (e.g. Angel Ring mod)");

        builder.pop();
        builder.push("other");

        DRIFT_STRENGTH = entry("driftStrength", 3, 0, 3,
                "The current drift strength. {Vanilla:0, Strong:1, Weak:2, Disabled:3}");
        builder.pop();
    }

    private ModConfigSpec.BooleanValue entry(String path, boolean defaultValue, String... comments) {
        return builder.comment(comments).translation(NoCreativeDriftMod.MOD_ID + ".midnightconfig." + path).define(path, defaultValue);
    }

    private ModConfigSpec.IntValue entry(String path, int defaultValue, int min, int max, String... comments) {
        return builder.comment(comments).translation(NoCreativeDriftMod.MOD_ID + ".midnightconfig." + path).defineInRange(path, defaultValue, min, max);
    }

    private ModConfigSpec.DoubleValue entry(String path, double defaultValue, double min, double max, String... comments) {
        return builder.comment(comments).translation(NoCreativeDriftMod.MOD_ID + ".midnightconfig." + path).defineInRange(path, defaultValue, min, max);
    }
}
