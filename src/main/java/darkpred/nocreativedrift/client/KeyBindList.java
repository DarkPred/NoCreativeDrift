package darkpred.nocreativedrift.client;

import darkpred.nocreativedrift.NoCreativeDrift;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class KeyBindList {

    public static final KeyBinding toggleDrift = new KeyBinding(
            Util.makeTranslationKey("key", new ResourceLocation(NoCreativeDrift.MOD_ID, "toggle_drift")),
            KeyConflictContext.IN_GAME, InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_C, "No Creative Drift");
}
