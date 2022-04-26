package darkpred.nocreativedrift.client;

import com.mojang.blaze3d.platform.InputConstants;
import darkpred.nocreativedrift.NoCreativeDrift;
import net.minecraft.Util;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.settings.KeyConflictContext;

public class KeyBindList {

    public static final KeyMapping toggleDrift = new KeyMapping(
            Util.makeDescriptionId("key", new ResourceLocation(NoCreativeDrift.MOD_ID, "toggle_drift")),
            KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, InputConstants.KEY_C, "No Creative Drift");
}
