package darkpred.nocreativedrift.client;

import darkpred.nocreativedrift.NoCreativeDrift;
import darkpred.nocreativedrift.config.ClientConfig;
import mekanism.common.CommonPlayerTickHandler;
import mekanism.common.item.gear.ItemJetpack;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import stormedpanda.simplyjetpacks.items.JetpackItem;
import net.minecraft.util.math.vector.Vector3d;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientEventHandler {
    private static final IEnergyStorage EMPTY_ENERGY_STORAGE = new EnergyStorage(0);
    private static boolean keyJumpPressed = false;
    private static boolean keySneakPressed = false;

    @SubscribeEvent
    public static void onPlayerTickEvent(PlayerTickEvent event) {
        //Ensures that the code is only run once on the logical client
        if (event.phase == Phase.END && event.side == LogicalSide.CLIENT) {
            // Making sure that the player is creative flying
            if ((ClientConfig.disableNonCreativeDrift.get() || event.player.isCreative()) && event.player.abilities.isFlying) {
                stopDrift(event);
            }
            if (Boolean.TRUE.equals(ClientConfig.disableJetpackDrift.get())) {
                ItemStack itemStack = event.player.getItemStackFromSlot(EquipmentSlotType.CHEST);
                if (NoCreativeDrift.isSimplyJetpacksLoaded()) {
                    if (itemStack.getItem() instanceof JetpackItem && isEngineOn(itemStack)) {
                        if (itemStack.getTag() != null && itemStack.getTag().getInt("Energy") > 0) {
                            stopDrift(event);
                        }
                    }
                }
                if (NoCreativeDrift.isIronJetpacksLoaded()) {
                    if (itemStack.getItem() instanceof com.blakebr0.ironjetpacks.item.JetpackItem && isEngineOn(itemStack)) {
                        if (itemStack.getCapability(CapabilityEnergy.ENERGY).orElse(EMPTY_ENERGY_STORAGE).getEnergyStored() > 0) {
                            stopDrift(event);
                        }
                    }
                }
                if (NoCreativeDrift.isMekanismLoaded()) {
                    ItemJetpack.JetpackMode mode = CommonPlayerTickHandler.getJetpackMode(itemStack);
                    if (mode == ItemJetpack.JetpackMode.NORMAL || mode == ItemJetpack.JetpackMode.HOVER) {
                        stopDrift(event);
                    }
                }
            }
        }
    }

    private static boolean isEngineOn(ItemStack itemStack) {
        return itemStack.getTag() != null && itemStack.getTag().getBoolean("Engine");
    }

    private static void stopDrift(PlayerTickEvent event) {
        Minecraft mc = Minecraft.getInstance();
        Vector3d motion = event.player.getMotion();
        // False if any horizontal movement key is being pressed
        if (!(mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown())) {
            // Sets the players horizontal motion to 0
            event.player.setMotion(0, motion.getY(), 0);
        }
        if (Boolean.TRUE.equals(ClientConfig.disableVerticalDrift.get())) {
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
}