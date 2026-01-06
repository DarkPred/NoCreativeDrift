package com.github.darkpred.nocreativedrift;

/**
 * Interface holding the client config entries
 */
public interface ClientConfig {
    boolean disableVerticalDrift();

    boolean enableToggleKeyBind();

    boolean enableHudMessage();

    boolean enableHudFading();

    boolean enableNonCreativeDrift();

    boolean disableJetpackDrift();

    int driftStrength();

    void setDriftStrength(int driftStrength);
}
