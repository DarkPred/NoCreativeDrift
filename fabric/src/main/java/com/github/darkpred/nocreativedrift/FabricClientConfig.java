package com.github.darkpred.nocreativedrift;

import com.google.auto.service.AutoService;
import eu.midnightdust.lib.config.MidnightConfig;

@AutoService(ClientConfig.class)
public class FabricClientConfig extends MidnightConfig implements ClientConfig {

    @Entry
    public static boolean disableVerticalDrift;
    @Entry
    public static boolean enableToggleKeyBind;
    @Entry
    public static boolean saveConfigOnToggle;
    @Entry
    public static boolean enableHudMessage;
    @Entry
    public static boolean enableHudFading;
    @Entry
    public static boolean disableNonCreativeDrift;
    @Entry(min = 0, max = 3)
    public static int driftStrength = 3;

    @Override
    public boolean disableVerticalDrift() {
        return disableVerticalDrift;
    }

    @Override
    public boolean enableToggleKeyBind() {
        return enableToggleKeyBind;
    }

    @Override
    public boolean enableHudMessage() {
        return enableHudMessage;
    }

    @Override
    public boolean enableHudFading() {
        return enableHudFading;
    }

    @Override
    public boolean enableNonCreativeDrift() {
        return !disableNonCreativeDrift;
    }

    @Override
    public boolean disableJetpackDrift() {
        return false;
    }

    @Override
    public int driftStrength() {
        return driftStrength;
    }

    @Override
    public void setDriftStrength(int driftStrength) {
        this.driftStrength = driftStrength;
        if (saveConfigOnToggle) {
            MidnightConfig.write(NoCreativeDriftMod.MOD_ID);
        }
    }
}
