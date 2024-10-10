package com.github.darkpred.nocreativedrift;

import com.github.darkpred.nocreativedrift.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayDeque;
import java.util.Deque;

import static com.github.darkpred.nocreativedrift.platform.Services.CONFIG;

public abstract class DriftUtil {
    private final Deque<Drift> driftDeque = new ArrayDeque<>();
    protected boolean keyJumpPressed = false;
    protected boolean keySneakPressed = false;
    protected boolean keyToggleDriftPressed = false;
    protected float hudOpacity = 5.0f;

    protected DriftUtil() {
        //Init drift strength order
        int curDrift = CONFIG.driftStrength();
        Drift[] drifts = Drift.values();
        while (driftDeque.size() < drifts.length) {
            driftDeque.add(drifts[curDrift % drifts.length]);
            curDrift++;
        }
    }

    public void toggleDrift(boolean toggleDown) {
        if (keyToggleDriftPressed != toggleDown) {
            if (!keyToggleDriftPressed) {
                driftDeque.add(driftDeque.pop());
                hudOpacity = 5.0f;
                CONFIG.setDriftStrength(driftDeque.peek().ordinal());
            }
            keyToggleDriftPressed = toggleDown;
        }
    }

    public void onClientPlayerTick(LocalPlayer player) {
        hudOpacity = Math.max(hudOpacity - 0.05f, 0);
        // Making sure that the player is creative flying
        if ((CONFIG.enableNonCreativeDrift() || player.isCreative()) && player.getAbilities().flying) {
            stopDrift(player);
        }
        if (CONFIG.disableJetpackDrift() && isJetpackOn(player)) {
            stopDrift(player);
        }
    }

    private void stopDrift(LocalPlayer player) {
        Vec3 motion = player.getDeltaMovement();
        //If no movement keys are pressed slow down player. Seems to work fine with pistons and stuff
        stopHorizontalDrift(player);
        if (!CONFIG.disableVerticalDrift()) {
            return;
        }
        if (!CONFIG.disableJetpackDrift() && isJetpackOn(player)) {
            return;
        }

        if (keyJumpPressed && !isJumpPressed(player)) {
            //Multiplier only applied once but that's fine because there is barely any drift anyway
            player.setDeltaMovement(motion.x, motion.y * getCurDrift().getMulti(), motion.z);
            keyJumpPressed = false;
        } else if (isJumpPressed(player)) {
            keyJumpPressed = true;
        }
        if (keySneakPressed && !isSneakPressed(player)) {
            player.setDeltaMovement(motion.x, motion.y * getCurDrift().getMulti(), motion.z);
            keySneakPressed = false;
        } else if (isSneakPressed(player)) {
            keySneakPressed = true;
        }
    }

    private void stopHorizontalDrift(LocalPlayer player) {
        if (!horizontalControlsUsed(player)) {
            // Sets the players horizontal motion to current * drift multiplier
            Vec3 motion = player.getDeltaMovement();
            player.setDeltaMovement(motion.x * getCurDrift().getMulti(), motion.y, motion.z * getCurDrift().getMulti());
        }
    }

    protected boolean horizontalControlsUsed(LocalPlayer player) {
        return player.input.up || player.input.down || player.input.left || player.input.right;
    }

    protected boolean isJumpPressed(LocalPlayer player) {
        return player.input.jumping;
    }

    protected boolean isSneakPressed(LocalPlayer player) {
        return player.input.shiftKeyDown;
    }

    protected abstract boolean isJetpackOn(Player player);

    public Drift getCurDrift() {
        Drift ret = driftDeque.peek();
        if (ret == null) {
            ret = Drift.DISABLED;
        }
        return ret;
    }

    public void render(GuiGraphics guiGraphics, float partialTick) {
        if (!Services.CONFIG.enableHudMessage()) {
            return;
        }
        if (Services.CONFIG.enableHudFading() && hudOpacity <= 0) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        int yPosition = (int) (0.3 * mc.getWindow().getGuiScaledHeight());
        int color = addOpacityToColor(hudOpacity, "EEEBF0");
        MutableComponent text = Component.translatable("hud.nocreativedrift.drift_strength", getCurDrift().getText());
        guiGraphics.drawString(mc.font, text, 2, yPosition, color);
    }

    /**
     * Adds opacity to a given hex color. Does not work with opacity of 0
     */
    private static int addOpacityToColor(float opacity, String hexColor) {
        return Integer.parseUnsignedInt(Integer.toHexString((int) (Math.min(opacity, 1) * 255)) + hexColor, 16);
    }

}
