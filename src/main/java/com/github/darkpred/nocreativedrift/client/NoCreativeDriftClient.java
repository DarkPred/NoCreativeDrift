package com.github.darkpred.nocreativedrift.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public class NoCreativeDriftClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ClientPlayerEntity player = client.player;
            if (player == null) return;
            if (player.getAbilities().flying) {
                stopDrift(player);
            }
        });
    }

    private void stopDrift(ClientPlayerEntity player) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (!(mc.options.keyForward.isPressed() || mc.options.keyBack.isPressed() || mc.options.keyLeft.isPressed() || mc.options.keyRight.isPressed())) {
            player.setVelocity(0, player.getVelocity().getY(), 0);
        }
    }
}
