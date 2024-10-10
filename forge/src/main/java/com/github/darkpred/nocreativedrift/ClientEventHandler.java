package com.github.darkpred.nocreativedrift;

import com.github.darkpred.nocreativedrift.platform.Services;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientEventHandler {

    @SubscribeEvent
    public static void onPlayerTickEvent(PlayerTickEvent event) {
        //Ensures that the code is only run once on the logical client
        if (event.phase == Phase.END && event.side.isClient()) {
            Services.DRIFT_UTIL.onClientPlayerTick((LocalPlayer) event.player);
        }
    }

    @SubscribeEvent
    public static void onKeyInputEvent(InputEvent.Key event) {
        Services.DRIFT_UTIL.toggleDrift(KeyBindList.TOGGLE_DRIFT.isDown());
    }
}
