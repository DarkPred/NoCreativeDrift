package darkpred.nocreativedrift.client;

import darkpred.nocreativedrift.NoCreativeDrift;
import darkpred.nocreativedrift.config.ClientConfig;
import mekanism.common.CommonPlayerTickHandler;
import mekanism.common.item.gear.ItemJetpack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.OverlayRegistry;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

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

    static {
        DRIFT_QUEUE.add(Drift.VANILLA);
        DRIFT_QUEUE.add(Drift.STRONG);
        DRIFT_QUEUE.add(Drift.WEAK);
        DRIFT_QUEUE.add(Drift.DISABLED);
    }

    @SubscribeEvent
    public static void onWorldTickEvent(WorldEvent.Save event) {
        IntegratedServer mcs = Minecraft.getInstance().getSingleplayerServer();
        if (mcs != null) {
            if (dirty) {
                ClientConfig.driftStrength.set(DRIFT_QUEUE.peek().ordinal());
                dirty = false;
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTickEvent(PlayerTickEvent event) {
        driftHud.opacity = Math.max(driftHud.opacity - 0.05f, 0);
        //Ensures that the code is only run once on the logical client
        if (event.phase == Phase.END && event.side == LogicalSide.CLIENT) {
            // Making sure that the player is creative flying
            if ((ClientConfig.isRuleEnabled(ClientConfig.disableNonCreativeDrift) || event.player.isCreative()) && event.player.getAbilities().flying) {
                stopDrift(event);
            }
            if (ClientConfig.isRuleEnabled(ClientConfig.disableJetpackDrift)) {
                ItemStack itemStack = event.player.getItemBySlot(EquipmentSlot.CHEST);
                if (isIronJetpackOn(itemStack) || isMekanismJetpackOn(itemStack)) {
                    stopDrift(event);
                }
            }
        }
    }

    private static DriftMessageHud driftHud = new DriftMessageHud();

    public static void registerHudMessage() {
        OverlayRegistry.registerOverlayAbove(ForgeIngameGui.HOTBAR_ELEMENT, "Drift", driftHud);
    }

    @SubscribeEvent
    public static void onKeyInputEvent(InputEvent.KeyInputEvent event) {
        if (keyToggleDriftPressed != KeyBindList.toggleDrift.isDown()) {
            if (!keyToggleDriftPressed) {
                DRIFT_QUEUE.add(DRIFT_QUEUE.pop());
                driftHud.opacity = 5.0f;
                dirty = true;
            }
            keyToggleDriftPressed = KeyBindList.toggleDrift.isDown();
        }
    }

    private static boolean isEngineOn(ItemStack itemStack) {
        return itemStack.getTag() != null && itemStack.getTag().getBoolean("Engine");
    }

    private static void stopDrift(PlayerTickEvent event) {
        Minecraft mc = Minecraft.getInstance();
        Vec3 motion = event.player.getDeltaMovement();
        //If no movement keys are pressed slow down player. Seems to work fine with pistons and stuff
        if (!(mc.options.keyUp.isDown() || mc.options.keyDown.isDown() || mc.options.keyLeft.isDown() || mc.options.keyRight.isDown())) {
            // Sets the players horizontal motion to current * drift multiplier
            event.player.setDeltaMovement(motion.x() * getCurDrift().getMulti(), motion.y(), motion.z() * getCurDrift().getMulti());
        }
        if ((ClientConfig.isRuleEnabled(ClientConfig.disableVerticalDrift))) {
            if ((ClientConfig.isRuleEnabled(ClientConfig.disableJetpackDrift))) {
                ItemStack itemStack = event.player.getItemBySlot(EquipmentSlot.CHEST);
                if (isMekanismJetpackOn(itemStack) || isIronJetpackOn(itemStack)) {
                    return;
                }
            }

            if (keyJumpPressed && !mc.options.keyJump.isDown()) {
                //Multiplier only applied once but that's fine because there is barely no drift anyway
                event.player.setDeltaMovement(motion.x(), motion.y() * getCurDrift().getMulti(), motion.z());
                keyJumpPressed = false;
            } else if (mc.options.keyJump.isDown()) {
                keyJumpPressed = true;
            }
            if (keySneakPressed && !mc.options.keyShift.isDown()) {
                event.player.setDeltaMovement(motion.x(), motion.y() * getCurDrift().getMulti(), motion.z());
                keySneakPressed = false;
            } else if (mc.options.keyShift.isDown()) {
                keySneakPressed = true;
            }
        }
    }

    private static boolean isMekanismJetpackOn(ItemStack itemStack) {
        if (NoCreativeDrift.isMekanismLoaded()) {
            ItemJetpack.JetpackMode mode = CommonPlayerTickHandler.getJetpackMode(itemStack);
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
