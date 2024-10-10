package com.github.darkpred.nocreativedrift;

import com.google.auto.service.AutoService;
import net.minecraft.world.entity.player.Player;

@AutoService(DriftUtil.class)
public class FabricDriftUtil extends DriftUtil {

    public FabricDriftUtil() {
    }

    @Override
    protected boolean isJetpackOn(Player player) {
        return false;
    }
}
