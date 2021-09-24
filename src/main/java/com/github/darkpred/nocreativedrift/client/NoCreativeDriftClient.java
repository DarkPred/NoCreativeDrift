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
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;
import team.reborn.energy.Energy;

import java.util.ArrayDeque;
import java.util.Deque;

public class NoCreativeDriftClient implements ClientModInitializer {
    private static final KeyBinding toggleDrift = new KeyBinding(
            "key.nocreativedrift.toggle_drift", GLFW.GLFW_KEY_C, "No Creative Drift");
    private final Deque<Drift> driftQueue = new ArrayDeque<>();
    private boolean keyJumpPressed = false;
    private boolean keySneakPressed = false;
    private boolean keyToggleDriftPressed = false;
    private float opacity = 5.0f;

    private static boolean isEngineOn(ItemStack itemStack) {
        return itemStack.getTag() != null && itemStack.getTag().getBoolean("Engine");
    }

    @Override
    public void onInitializeClient() {
        //Init drift strength order
        int curDrift = ClientConfig.CONFIG.getOrDefault("driftStrength", 3);
        Drift[] drifts = Drift.values();
        while (driftQueue.size() < drifts.length) {
            driftQueue.add(drifts[curDrift % 4]);
            curDrift++;
        }

        if (ClientConfig.CONFIG.getOrDefault("enableToggleKeyBind", false)) {
            KeyBindingHelper.registerKeyBinding(toggleDrift);
        }
        boolean ironJetpacksLoaded = FabricLoader.getInstance().isModLoaded("iron-jetpacks");
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ClientPlayerEntity player = client.player;
            if (player == null) {
                opacity = 5;
                return;
            }
            opacity = Math.max(opacity - 0.05f, 0);
            if (toggleDrift.isPressed() != keyToggleDriftPressed) {
                if (!keyToggleDriftPressed) {
                    driftQueue.add(driftQueue.pop());
                    ClientConfig.CONFIG.set("driftStrength", driftQueue.peek().ordinal());
                    opacity = 5.0f;
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
                        stopHorizontalDrift(player);
                    }
                }
            }
        });

        HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> {
            if (ClientConfig.CONFIG.getOrDefault("enableHudFading", false)) {
                if (opacity <= 0) {
                    return;
                }
            }
            if (ClientConfig.CONFIG.getOrDefault("enableHudMessage", false)) {
                MinecraftClient mc = MinecraftClient.getInstance();
                float yPosition = (float) (0.3 * mc.getWindow().getScaledHeight());
                int color = addOpacityToColor(opacity, "EEEBF0");
                TranslatableText text = new TranslatableText("hud.nocreativedrift.drift_strength", getCurDrift().getText());
                mc.textRenderer.drawWithShadow(matrixStack, text, 2, yPosition, color);
            }
        });
    }

    /**
     * Adds opacity to a given hex color. Does not work with opacity of 0
     */
    private int addOpacityToColor(float opacity, String hexColor) {
        opacity = Math.min(opacity, 1);
        return Integer.parseUnsignedInt(Integer.toHexString((int) (opacity * 255)) + hexColor, 16);
    }

    private void stopHorizontalDrift(ClientPlayerEntity player) {
        MinecraftClient mc = MinecraftClient.getInstance();
        Vec3d velocity = player.getVelocity();
        if (!(mc.options.keyForward.isPressed() || mc.options.keyBack.isPressed() || mc.options.keyLeft.isPressed() || mc.options.keyRight.isPressed())) {
            player.setVelocity(velocity.getX() * getCurDrift().getMulti(), velocity.getY(), velocity.getZ() * getCurDrift().getMulti());
        }
    }

    /**
     * Currently not used for jetpacks
     */
    private void stopDrift(ClientPlayerEntity player) {
        MinecraftClient mc = MinecraftClient.getInstance();
        Vec3d velocity = player.getVelocity();
        //If no movement keys are pressed slow down player. Seems to work fine with pistons and stuff
        stopHorizontalDrift(player);
        if (ClientConfig.CONFIG.getOrDefault("disableVerticalDrift", false)) {
            if (keyJumpPressed && !mc.options.keyJump.isPressed()) {
                //Multiplier only applied once but that's fine because there is barely no drift anyway
                player.setVelocity(velocity.getX(), velocity.getY() * getCurDrift().getMulti(), velocity.getZ());
                keyJumpPressed = false;
            } else if (mc.options.keyJump.isPressed()) {
                keyJumpPressed = true;
            }
            if (keySneakPressed && !mc.options.keySneak.isPressed()) {
                player.setVelocity(velocity.getX(), velocity.getY() * getCurDrift().getMulti(), velocity.getZ());
                keySneakPressed = false;
            } else if (mc.options.keySneak.isPressed()) {
                keySneakPressed = true;
            }
        }
    }

    private Drift getCurDrift() {
        Drift ret = driftQueue.peek();
        if (ret == null) {
            ret = Drift.DISABLED;
        }
        return ret;
    }
}
