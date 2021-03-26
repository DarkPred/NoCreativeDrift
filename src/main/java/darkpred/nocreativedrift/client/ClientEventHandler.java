package darkpred.nocreativedrift.client;

import darkpred.nocreativedrift.NoCreativeDrift;
import darkpred.nocreativedrift.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import stormedpanda.simplyjetpacks.items.JetpackItem;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientEventHandler {

    @SubscribeEvent
    public static void onPlayerTickEvent(PlayerTickEvent event) {
        //Ensures that the code is only run once on the logical client
        if (event.phase == Phase.END && event.side == LogicalSide.CLIENT) {
            // Making sure that the player is creative flying
            if (event.player.isCreative() && event.player.abilities.isFlying && !event.player.isElytraFlying()) {
                stopFlight(event);
            }
            if (!ClientConfig.disableJetpackDrift.get()) {
                ItemStack itemStack = event.player.getItemStackFromSlot(EquipmentSlotType.CHEST);
                if (NoCreativeDrift.isSimplyJetpacksLoaded()) {
                    if (itemStack.getItem().getClass() == JetpackItem.class) {
                        if (((JetpackItem) itemStack.getItem()).isEngineOn(itemStack)) {
                            stopFlight(event);
                        }
                    }
                }
            }
        }
    }

    private static void stopFlight(PlayerTickEvent event) {
        Minecraft mc = Minecraft.getInstance();
        // False if any horizontal movement key is being pressed
        if (!(mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown())) {
            // Sets the players horizontal motion to 0
            event.player.setMotion(0, event.player.getMotion().getY(), 0);
        }
    }
}