package darkpred.nocreativedrift.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientEventHandler {

	@SuppressWarnings("unused")
	private static final Logger LOGGER = LogManager.getLogger(ClientEventHandler.class);

	@SubscribeEvent
	public static void onPlayerTickEvent(PlayerTickEvent event) {
		//Ensures that the code is only run once on the logical client
		if (event.phase == Phase.END && event.side == LogicalSide.CLIENT) {
			// Making sure that the player is creative flying
			if (event.player.isCreative() && event.player.abilities.isFlying && !event.player.isElytraFlying()) {
				Minecraft mc = Minecraft.getInstance();
				// False if any horizontal movement key is being pressed
				if (!(mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown())) {
					// Sets the players horizontal motion to 0
					event.player.setMotion(0, event.player.getMotion().getY(), 0);
				}
			}
		}
	}
}
