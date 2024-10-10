package com.github.darkpred.nocreativedrift;

import eu.midnightdust.lib.config.MidnightConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class FabricNoCreativeDriftMod implements ModInitializer {
    @Override
    public void onInitialize() {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            MidnightConfig.init(NoCreativeDriftMod.MOD_ID, FabricClientConfig.class);
        }
    }
}
