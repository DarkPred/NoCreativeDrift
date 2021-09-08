package com.github.darkpred.nocreativedrift.client;

import com.blakebr0.ironjetpacks.item.JetpackItem;
import com.github.darkpred.nocreativedrift.client.config.ClientConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;
import team.reborn.energy.Energy;

import java.util.ArrayDeque;
import java.util.Deque;

public class NoCreativeDriftClient implements ClientModInitializer {
    private static final Deque<Drift> DRIFT = new ArrayDeque<>();
    private static final KeyBinding toggleDrift = new KeyBinding(
            "key.nocreativedrift.toggle_drift", GLFW.GLFW_KEY_C, "No Creative Drift");

    static {
        DRIFT.add(Drift.VANILLA);
        DRIFT.add(Drift.STRONG);
        DRIFT.add(Drift.WEAK);
        DRIFT.add(Drift.DISABLED);
    }

    private boolean keyJumpPressed = false;
    private boolean keySneakPressed = false;
    private boolean keyToggleDriftPressed = false;

    private static boolean isEngineOn(ItemStack itemStack) {
        return itemStack.getTag() != null && itemStack.getTag().getBoolean("Engine");
    }

    private static Drift curDrift() {
        Drift ret = DRIFT.peek();
        if (ret == null) {
            ret = Drift.DISABLED;
        }
        return ret;
    }

    @Override
    public void onInitializeClient() {
        if (ClientConfig.CONFIG.getOrDefault("enableToggleKeyBind", false)) {
            KeyBindingHelper.registerKeyBinding(toggleDrift);
        }
        boolean ironJetpacksLoaded = FabricLoader.getInstance().isModLoaded("iron-jetpacks");
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ClientPlayerEntity player = client.player;
            if (player == null) return;
            if (toggleDrift.isPressed() != keyToggleDriftPressed) {
                if (!keyToggleDriftPressed) {
                    DRIFT.add(DRIFT.pop());
                }
                keyToggleDriftPressed = toggleDrift.isPressed();
            }

            if (player.abilities.flying) {
                stopDrift(player);
            }
            if (ironJetpacksLoaded && ClientConfig.CONFIG.getOrDefault("disableJetpackDrift", false)) {
                ItemStack itemStack = player.getEquippedStack(EquipmentSlot.CHEST);
                if (itemStack.getItem() instanceof JetpackItem && isEngineOn(itemStack)) {
                    if (Energy.of(itemStack).getEnergy() > 0) {
                        stopDrift(player);
                    }
                }
            }
        });

        HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> {
            if (ClientConfig.CONFIG.getOrDefault("enableHudMessage", false)) {
                MinecraftClient mc = MinecraftClient.getInstance();
                float yPosition = (float) (0.3 * mc.getWindow().getScaledHeight());
                mc.textRenderer.drawWithShadow(matrixStack, "Drift: " + curDrift().name(), 2, yPosition, 0xC8C8C8);//TODO: Implement Translations
            }
        });
    }

    private void stopDrift(ClientPlayerEntity player) {
        MinecraftClient mc = MinecraftClient.getInstance();
        Vec3d velocity = player.getVelocity();
        //If no movement keys are pressed slow down player. Seems to work fine with pistons and stuff
        if (!(mc.options.keyForward.isPressed() || mc.options.keyBack.isPressed() || mc.options.keyLeft.isPressed() || mc.options.keyRight.isPressed())) {
            player.setVelocity(velocity.getY() * curDrift().getMulti(), velocity.getY(), velocity.getZ() * curDrift().getMulti());
        }
        if (ClientConfig.CONFIG.getOrDefault("disableVerticalDrift", false)) {
            if (keyJumpPressed && !mc.options.keyJump.isPressed()) {
                //Multiplier only applied once but that's fine because there is barely no drift anyway
                player.setVelocity(velocity.getX(), velocity.getY() * curDrift().getMulti(), velocity.getZ());
                keyJumpPressed = false;
            } else if (mc.options.keyJump.isPressed()) {
                keyJumpPressed = true;
            }
            if (keySneakPressed && !mc.options.keySneak.isPressed()) {
                player.setVelocity(velocity.getX(), velocity.getY() * curDrift().getMulti(), velocity.getZ());
                keySneakPressed = false;
            } else if (mc.options.keySneak.isPressed()) {
                keySneakPressed = true;
            }
        }
    }
}
