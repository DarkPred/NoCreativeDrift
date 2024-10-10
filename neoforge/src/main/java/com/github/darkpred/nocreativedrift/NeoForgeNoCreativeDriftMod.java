package com.github.darkpred.nocreativedrift;

import net.neoforged.fml.IExtensionPoint;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

@Mod(value = NoCreativeDriftMod.MOD_ID)
public class NeoForgeNoCreativeDriftMod {

    public NeoForgeNoCreativeDriftMod() {
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class,
                () -> new IExtensionPoint.DisplayTest(() -> "any", (remoteVersion, isFromServer) -> true));
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, NeoForgeConfig.CONFIG.getValue());
    }
}
