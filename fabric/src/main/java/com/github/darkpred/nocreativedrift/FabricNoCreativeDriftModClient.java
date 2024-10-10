package com.github.darkpred.nocreativedrift;

import com.github.darkpred.nocreativedrift.platform.Services;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.player.LocalPlayer;
import org.lwjgl.glfw.GLFW;

public class FabricNoCreativeDriftModClient implements ClientModInitializer {
    private static final KeyMapping toggleDrift = new KeyMapping(
            "key.nocreativedrift.toggle_drift", GLFW.GLFW_KEY_C, "No Creative Drift");

    @Override
    public void onInitializeClient() {

        if (Services.CONFIG.enableToggleKeyBind()) {
            KeyBindingHelper.registerKeyBinding(toggleDrift);
        }
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            LocalPlayer player = client.player;
            if (player == null) {
                return;
            }
            Services.DRIFT_UTIL.toggleDrift(toggleDrift.isDown());
            Services.DRIFT_UTIL.onClientPlayerTick(player);
        });

        HudRenderCallback.EVENT.register(Services.DRIFT_UTIL::render);
    }
}
