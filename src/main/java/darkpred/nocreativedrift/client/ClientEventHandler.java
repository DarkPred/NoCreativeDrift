package darkpred.nocreativedrift.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import darkpred.nocreativedrift.NoCreativeDrift;
import darkpred.nocreativedrift.config.ClientConfig;
import mekanism.common.item.gear.ItemJetpack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import stormedpanda.simplyjetpacks.items.JetpackItem;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientEventHandler {
    private static final IEnergyStorage EMPTY_ENERGY_STORAGE = new EnergyStorage(0);
    private static boolean keyJumpPressed = false;
    private static boolean keySneakPressed = false;
    private static boolean keyToggleDriftPressed = false;
    private static boolean driftDisabled = true;

    @SubscribeEvent
    public static void onPlayerTickEvent(PlayerTickEvent event) {
        //Ensures that the code is only run once on the logical client
        if (event.phase == Phase.END && event.side == LogicalSide.CLIENT && driftDisabled) {
            // Making sure that the player is creative flying
            if ((ClientConfig.isRuleEnabled(ClientConfig.disableNonCreativeDrift) || event.player.isCreative()) && event.player.abilities.isFlying) {
                stopDrift(event);
            }
            if (ClientConfig.isRuleEnabled(ClientConfig.disableJetpackDrift)) {
                ItemStack itemStack = event.player.getItemStackFromSlot(EquipmentSlotType.CHEST);
                if (isSimplyJetpackOn(itemStack) || isIronJetpackOn(itemStack) || isMekanismJetpackOn(itemStack)) {
                    stopDrift(event);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onKeyInputEvent(InputEvent.KeyInputEvent event) {
        if (KeyBindList.toggleDrift.isKeyDown() != keyToggleDriftPressed && ClientConfig.isRuleEnabled(ClientConfig.enableToggleKeyBind)) {
            if (keyToggleDriftPressed) {
                driftDisabled = !driftDisabled;
            }
            keyToggleDriftPressed = KeyBindList.toggleDrift.isKeyDown();
        }
    }

    @SubscribeEvent
    public static void renderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR && driftDisabled && ClientConfig.isRuleEnabled(
                ClientConfig.enableHudMessage)) {
            RenderSystem.pushMatrix();
            FontRenderer font = Minecraft.getInstance().fontRenderer;
            float yPosition = (float) (ClientConfig.hudOffset.get() * event.getWindow().getScaledHeight());
            font.drawStringWithShadow(new StringTextComponent("Drift disabled").getText(), 2, yPosition, 0xC8C8C8);
            RenderSystem.popMatrix();
        }
    }

    private static boolean isEngineOn(ItemStack itemStack) {
        return itemStack.getTag() != null && itemStack.getTag().getBoolean("Engine");
    }

    private static void stopDrift(PlayerTickEvent event) {
        Minecraft mc = Minecraft.getInstance();
        Vec3d motion = event.player.getMotion();
        // False if any horizontal movement key is being pressed
        if (!(mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown())) {
            // Sets the players horizontal motion to 0
            event.player.setMotion(0, motion.getY(), 0);
        }
        if ((ClientConfig.isRuleEnabled(ClientConfig.disableVerticalDrift))) {
            if ((ClientConfig.isRuleEnabled(ClientConfig.disableJetpackDrift))) {
                ItemStack itemStack = event.player.getItemStackFromSlot(EquipmentSlotType.CHEST);
                if (isMekanismJetpackOn(itemStack) || isIronJetpackOn(itemStack) || isSimplyJetpackOn(itemStack)) {
                    return;
                }
            }

            if (keyJumpPressed && !mc.gameSettings.keyBindJump.isKeyDown()) {
                event.player.setMotion(motion.getX(), 0, motion.getZ());
                keyJumpPressed = false;
            } else if (mc.gameSettings.keyBindJump.isKeyDown()) {
                keyJumpPressed = true;
            }
            if (keySneakPressed && !mc.gameSettings.keyBindSneak.isKeyDown()) {
                event.player.setMotion(motion.getX(), 0, motion.getZ());
                keySneakPressed = false;
            } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                keySneakPressed = true;
            }
        }
    }

    private static boolean isMekanismJetpackOn(ItemStack itemStack) {
        if (NoCreativeDrift.isMekanismLoaded()) {
            ItemJetpack.JetpackMode mode = ((ItemJetpack) itemStack.getItem()).getMode(itemStack);
            return mode == ItemJetpack.JetpackMode.NORMAL || mode == ItemJetpack.JetpackMode.HOVER;
        }
        return false;
    }

    private static boolean isIronJetpackOn(ItemStack itemStack) {
        if (NoCreativeDrift.isIronJetpacksLoaded()) {
            if (itemStack.getItem() instanceof com.blakebr0.ironjetpacks.item.JetpackItem && isEngineOn(itemStack)) {
                return itemStack.getCapability(CapabilityEnergy.ENERGY).orElse(EMPTY_ENERGY_STORAGE).getEnergyStored() > 0;
            }
        }
        return false;
    }

    private static boolean isSimplyJetpackOn(ItemStack itemStack) {
        if (NoCreativeDrift.isSimplyJetpacksLoaded()) {
            if (itemStack.getItem() instanceof JetpackItem && isEngineOn(itemStack)) {
                return itemStack.getTag() != null && itemStack.getTag().getInt("Energy") > 0;
            }
        }
        return false;
    }
}
