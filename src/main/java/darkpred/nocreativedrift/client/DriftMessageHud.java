package darkpred.nocreativedrift.client;

import com.mojang.blaze3d.vertex.PoseStack;
import darkpred.nocreativedrift.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class DriftMessageHud implements IGuiOverlay {

    @Override
    public void render(ForgeGui gui, PoseStack poseStack, float partialTick, int width, int height) {
        if (!ClientConfig.isRuleEnabled(ClientConfig.ENABLE_HUD_MESSAGE)) {
            return;
        }
        if (ClientConfig.isRuleEnabled(ClientConfig.ENABLE_HUD_FADING) && ClientEventHandler.hudOpacity <= 0) {
            return;
        }
        poseStack.pushPose();
        float yPosition = (float) (ClientConfig.HUD_OFFSET.get() * Minecraft.getInstance().getWindow().getGuiScaledHeight());
        Component text = Component.translatable("hud.nocreativedrift.drift_strength", ClientEventHandler.getCurDrift().getTextComponent());
        int color = addOpacityToColor(ClientEventHandler.hudOpacity, "EEEBF0");
        Minecraft.getInstance().font.drawShadow(poseStack, text, 2, yPosition, color);
        poseStack.popPose();
    }

    /**
     * Adds opacity to a given hex color. Does not work with opacity of 0
     */
    private static int addOpacityToColor(float opacity, String hexColor) {
        return Integer.parseUnsignedInt(Integer.toHexString((int) (Math.min(opacity, 1) * 255)) + hexColor, 16);
    }
}
