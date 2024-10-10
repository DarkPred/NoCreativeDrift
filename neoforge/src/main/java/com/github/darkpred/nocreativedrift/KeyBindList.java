package com.github.darkpred.nocreativedrift;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.Util;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.settings.KeyConflictContext;

public class KeyBindList {

    public static final KeyMapping TOGGLE_DRIFT = new KeyMapping(
            Util.makeDescriptionId("key", new ResourceLocation(NoCreativeDriftMod.MOD_ID, "toggle_drift")),
            KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, InputConstants.KEY_C, "key.nocreativedrift.category");
}
