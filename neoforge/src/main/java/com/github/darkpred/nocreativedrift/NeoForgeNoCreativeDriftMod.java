package com.github.darkpred.nocreativedrift;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

@Mod(value = NoCreativeDriftMod.MOD_ID, dist = Dist.CLIENT)
public class NeoForgeNoCreativeDriftMod {

    public NeoForgeNoCreativeDriftMod(ModContainer container) {
        container.registerConfig(ModConfig.Type.CLIENT, NeoForgeConfig.CONFIG.getValue());
    }
}
