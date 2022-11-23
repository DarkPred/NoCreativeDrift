package darkpred.nocreativedrift.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.controllable.Controllable;
import com.mrcrayfish.controllable.client.ButtonBindings;
import com.mrcrayfish.controllable.client.Controller;
import darkpred.nocreativedrift.NoCreativeDrift;
import darkpred.nocreativedrift.config.ClientConfig;
import mekanism.common.CommonPlayerTickHandler;
import mekanism.common.item.gear.ItemJetpack;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import stormedpanda.simplyjetpacks.items.JetpackItem;

import java.util.ArrayDeque;
import java.util.Deque;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientEventHandler {
    private static final IEnergyStorage EMPTY_ENERGY_STORAGE = new EnergyStorage(0);
    private static final Deque<Drift> DRIFT_QUEUE = new ArrayDeque<>();
    private static boolean keyJumpPressed = false;
    private static boolean keySneakPressed = false;
    private static boolean keyToggleDriftPressed = false;
    private static boolean dirty;

    public static float hudOpacity = 5.0f;

    static {
        DRIFT_QUEUE.add(Drift.VANILLA);
        DRIFT_QUEUE.add(Drift.STRONG);
        DRIFT_QUEUE.add(Drift.WEAK);
        DRIFT_QUEUE.add(Drift.DISABLED);
    }

    @SubscribeEvent
    public static void onWorldTickEvent(WorldEvent.Save event) {
        IntegratedServer mcs = Minecraft.getInstance().getIntegratedServer();
        if (mcs != null && dirty) {
            ClientConfig.driftStrength.set(DRIFT_QUEUE.peek().ordinal());
            dirty = false;
        }
    }

    @SubscribeEvent
    public static void onPlayerTickEvent(PlayerTickEvent event) {
        hudOpacity = Math.max(hudOpacity - 0.05f, 0);
        //Ensures that the code is only run once on the logical client
        if (event.phase == Phase.END && event.side == LogicalSide.CLIENT) {
            // Making sure that the player is creative flying
            if ((ClientConfig.isRuleEnabled(ClientConfig.disableNonCreativeDrift) || event.player.isCreative()) && event.player.abilities.isFlying) {
                stopDrift(event);
            }
            if (ClientConfig.isRuleEnabled(ClientConfig.disableJetpackDrift)) {
                if (isJetpackOn(event.player)) {
                    stopDrift(event);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onKeyInputEvent(InputEvent.KeyInputEvent event) {
        if (keyToggleDriftPressed != KeyBindList.toggleDrift.isKeyDown()) {
            if (!keyToggleDriftPressed) {
                DRIFT_QUEUE.add(DRIFT_QUEUE.pop());
                hudOpacity = 5.0f;
                dirty = true;
            }
            keyToggleDriftPressed = KeyBindList.toggleDrift.isKeyDown();
        }
    }

    @SubscribeEvent
    public static void renderOverlay(RenderGameOverlayEvent.Post event) {
        if (ClientConfig.isRuleEnabled(ClientConfig.enableHudFading)) {
            if (hudOpacity <= 0) {
                return;
            }
        }
        if (event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR && ClientConfig.isRuleEnabled(ClientConfig.enableHudMessage)) {
            MatrixStack matrix = event.getMatrixStack();
            matrix.push();
            float yPosition = (float) (ClientConfig.hudOffset.get() * event.getWindow().getScaledHeight());
            TranslationTextComponent text = new TranslationTextComponent("hud.nocreativedrift.drift_strength", getCurDrift().getTextComponent());
            int color = addOpacityToColor(hudOpacity, "EEEBF0");
            Minecraft.getInstance().fontRenderer.func_243246_a(matrix, text, 2, yPosition, color);
            matrix.pop();
        }
    }

    /**
     * Adds opacity to a given hex color. Does not work with opacity of 0
     */
    private static int addOpacityToColor(float opacity, String hexColor) {
        opacity = Math.min(opacity, 1);
        return Integer.parseUnsignedInt(Integer.toHexString((int) (opacity * 255)) + hexColor, 16);
    }

    private static boolean isEngineOn(ItemStack itemStack) {
        return itemStack.getTag() != null && itemStack.getTag().getBoolean("Engine");
    }

    private static void stopDrift(PlayerTickEvent event) {
        Minecraft mc = Minecraft.getInstance();
        Vector3d motion = event.player.getMotion();
        //If no movement keys are pressed slow down player. Seems to work fine with pistons and stuff
        if (!horizontalControlsUsed()) {
            // Sets the players horizontal motion to current * drift multiplier
            event.player.setMotion(motion.getX() * getCurDrift().getMulti(), motion.getY(), motion.getZ() * getCurDrift().getMulti());
        }
        if ((ClientConfig.isRuleEnabled(ClientConfig.disableVerticalDrift))) {
            if ((ClientConfig.isRuleEnabled(ClientConfig.disableJetpackDrift))) {
                if (isJetpackOn(event.player)) {
                    return;
                }
            }

            if (keyJumpPressed && !isJumpPressed()) {
                //Multiplier only applied once but that's fine because there is barely no drift anyway
                event.player.setMotion(motion.getX(), motion.getY() * getCurDrift().getMulti(), motion.getZ());
                keyJumpPressed = false;
            } else if (isJumpPressed()) {
                keyJumpPressed = true;
            }
            if (keySneakPressed && !isSneakPressed()) {
                event.player.setMotion(motion.getX(), motion.getY() * getCurDrift().getMulti(), motion.getZ());
                keySneakPressed = false;
            } else if (isSneakPressed()) {
                keySneakPressed = true;
            }
        }
    }

    private static boolean isJumpPressed() {
        boolean pressed = false;
        if (NoCreativeDrift.isControllableLoaded() && ClientConfig.isRuleEnabled(ClientConfig.enableControllerSupport)) {
            pressed = ButtonBindings.JUMP.isButtonDown();
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.gameSettings.keyBindJump.isKeyDown()) {
            pressed = true;
        }
        return pressed;
    }
    private static boolean isSneakPressed() {
        boolean pressed = false;
        if (NoCreativeDrift.isControllableLoaded() && ClientConfig.isRuleEnabled(ClientConfig.enableControllerSupport)) {
            pressed = ButtonBindings.SNEAK.isButtonDown();
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.gameSettings.keyBindSneak.isKeyDown()) {
            pressed = true;
        }
        return pressed;
    }
    private static boolean horizontalControlsUsed() {
        boolean pressed = false;
        if (NoCreativeDrift.isControllableLoaded() && ClientConfig.isRuleEnabled(ClientConfig.enableControllerSupport)) {
            Controller controller = Controllable.getController();
            if (controller != null) {
                float deadZone = com.mrcrayfish.controllable.Config.CLIENT.options.deadZone.get().floatValue();
                pressed = Math.abs(controller.getLThumbStickXValue()) >= deadZone || Math.abs(controller.getLThumbStickYValue()) >= deadZone;
            }
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown()) {
            pressed = true;
        }
        return pressed;
    }

    private static boolean isJetpackOn(PlayerEntity player) {
        return isMekanismJetpackOn(player) || isIronJetpackOn(player) || isSimplyJetpackOn(player);
    }

    private static boolean isMekanismJetpackOn(PlayerEntity player) {
        if (NoCreativeDrift.isMekanismLoaded()) {
            ItemStack itemStack = player.getItemStackFromSlot(EquipmentSlotType.CHEST);
            ItemJetpack.JetpackMode mode = CommonPlayerTickHandler.getJetpackMode(itemStack);
            return mode == ItemJetpack.JetpackMode.NORMAL || mode == ItemJetpack.JetpackMode.HOVER;
        }
        return false;
    }

    private static boolean isIronJetpackOn(PlayerEntity player) {
        if (NoCreativeDrift.isIronJetpacksLoaded()) {
            ItemStack itemStack = player.getItemStackFromSlot(EquipmentSlotType.CHEST);
            if (itemStack.getItem() instanceof com.blakebr0.ironjetpacks.item.JetpackItem && isEngineOn(itemStack)) {
                return itemStack.getCapability(CapabilityEnergy.ENERGY).orElse(EMPTY_ENERGY_STORAGE).getEnergyStored() > 0;
            }
        }
        return false;
    }

    private static boolean isSimplyJetpackOn(PlayerEntity player) {
        if (NoCreativeDrift.isSimplyJetpacksLoaded()) {
            ItemStack itemStack = player.getItemStackFromSlot(EquipmentSlotType.CHEST);
            if (itemStack.getItem() instanceof JetpackItem && isEngineOn(itemStack)) {
                return itemStack.getTag() != null && itemStack.getTag().getInt("Energy") > 0;
            }
        }
        return false;
    }

    public static Drift getCurDrift() {
        Drift ret = DRIFT_QUEUE.peek();
        if (ret == null) {
            ret = Drift.DISABLED;
        }
        return ret;
    }

    public static void setCurDrift(Drift drift) {
        for (int i = 0; i < DRIFT_QUEUE.size(); i++) {
            if (DRIFT_QUEUE.peek() != drift) {
                DRIFT_QUEUE.add(DRIFT_QUEUE.pop());
            }
        }
    }
}
