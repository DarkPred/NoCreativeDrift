package com.github.darkpred.nocreativedrift;

import com.google.auto.service.AutoService;

/**
 * Class holding the client config entries
 */
@AutoService(ClientConfig.class)
public class NeoForgeClientConfig implements ClientConfig {

    private static NeoForgeConfig getConfig() {
        return NeoForgeConfig.CONFIG.getKey();
    }

    @Override
    public boolean disableVerticalDrift() {
        return getConfig().DISABLE_VERTICAL_DRIFT.get();
    }

    @Override
    public boolean enableToggleKeyBind() {
        return getConfig().ENABLE_TOGGLE_KEY_BIND.get();
    }

    @Override
    public boolean enableHudMessage() {
        return getConfig().ENABLE_HUD_MESSAGE.get();
    }

    @Override
    public boolean enableHudFading() {
        return getConfig().ENABLE_HUD_FADING.get();
    }

    @Override
    public boolean enableNonCreativeDrift() {
        return !getConfig().DISABLE_NON_CREATIVE_DRIFT.get();
    }

    @Override
    public boolean disableJetpackDrift() {
        return getConfig().DISABLE_JETPACK_DRIFT.get();
    }

    @Override
    public int driftStrength() {
        return getConfig().DRIFT_STRENGTH.get();
    }

    @Override
    public void setDriftStrength(int driftStrength) {
        getConfig().DRIFT_STRENGTH.set(driftStrength);
        if (Boolean.TRUE.equals(getConfig().SAVE_CONFIG_ON_TOGGLE.get())) {
            NeoForgeConfig.CONFIG.getValue().save();
        }
    }
}
