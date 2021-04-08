package com.github.darkpred.nocreativedrift.client;

import com.blakebr0.ironjetpacks.item.JetpackItem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import team.reborn.energy.Energy;

public class NoCreativeDriftClient implements ClientModInitializer {

    private static boolean isEngineOn(ItemStack itemStack) {
        return itemStack.getTag() != null && itemStack.getTag().getBoolean("Engine");
    }

    @Override
    public void onInitializeClient() {
        boolean ironJetpacksLoaded = FabricLoader.getInstance().isModLoaded("iron-jetpacks");
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ClientPlayerEntity player = client.player;
            if (player == null) return;
            if (player.abilities.flying) {
                stopDrift(player);
            }
            if (ironJetpacksLoaded) {
                ItemStack itemStack = player.getEquippedStack(EquipmentSlot.CHEST);
                if (itemStack.getItem() instanceof JetpackItem && isEngineOn(itemStack)) {
                    if (Energy.of(itemStack).getEnergy() > 0) {
                        stopDrift(player);
                    }
                }
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
