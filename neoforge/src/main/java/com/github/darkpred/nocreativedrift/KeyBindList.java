package com.github.darkpred.nocreativedrift;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.neoforged.neoforge.client.settings.KeyConflictContext;

public class KeyBindList {
    public static final KeyMapping.Category CATEGORY = new KeyMapping.Category(Identifier.fromNamespaceAndPath(NoCreativeDriftMod.MOD_ID, "main"));
    public static final KeyMapping TOGGLE_DRIFT = new KeyMapping(
            Util.makeDescriptionId("key", Identifier.fromNamespaceAndPath(NoCreativeDriftMod.MOD_ID, "toggle_drift")),
            KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, InputConstants.KEY_C, CATEGORY);
}
