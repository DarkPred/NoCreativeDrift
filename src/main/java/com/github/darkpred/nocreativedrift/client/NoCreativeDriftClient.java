package com.github.darkpred.nocreativedrift.client;

import com.blakebr0.ironjetpacks.item.JetpackItem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import team.reborn.energy.Energy;

public class NoCreativeDriftClient implements ClientModInitializer {

    private final SimpleConfig config = SimpleConfig.of("nocreativedrift").provider(namespace ->
            "#Disable the drift during vertical flight\ndisableVerticalDrift=false\n" +
                    "#Disable the drift on jetpacks\ndisableJetpackDrift=false\n"
    ).request();
    private boolean keyJumpPressed = false;
    private boolean keySneakPressed = false;

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
            if (ironJetpacksLoaded && config.getOrDefault("disableJetpackDrift", false)) {
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
        Vec3d velocity = player.getVelocity();
        if (!(mc.options.keyForward.isPressed() || mc.options.keyBack.isPressed() || mc.options.keyLeft.isPressed() || mc.options.keyRight.isPressed())) {
            player.setVelocity(0, velocity.getY(), 0);
        }
        if (config.getOrDefault("disableVerticalDrift", false)) {
            if (keyJumpPressed && !mc.options.keyJump.isPressed()) {
                player.setVelocity(velocity.getX(), 0, velocity.getZ());
                keyJumpPressed = false;
            } else if (mc.options.keyJump.isPressed()) {
                keyJumpPressed = true;
            }
            if (keySneakPressed && !mc.options.keySneak.isPressed()) {
                player.setVelocity(velocity.getX(), 0, velocity.getZ());
                keySneakPressed = false;
            } else if (mc.options.keySneak.isPressed()) {
                keySneakPressed = true;
            }
        }
    }
}
