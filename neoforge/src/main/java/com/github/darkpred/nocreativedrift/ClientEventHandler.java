package com.github.darkpred.nocreativedrift;

import com.github.darkpred.nocreativedrift.platform.Services;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.event.TickEvent;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientEventHandler {

    @SubscribeEvent
    public static void onPlayerTickEvent(TickEvent.PlayerTickEvent event) {
        //Ensures that the code is only run once on the logical client
        if (event.phase == TickEvent.Phase.END && event.side.isClient()) {
            Services.DRIFT_UTIL.onClientPlayerTick((LocalPlayer) event.player);
        }
    }

    @SubscribeEvent
    public static void onKeyInputEvent(InputEvent.Key event) {
        Services.DRIFT_UTIL.toggleDrift(KeyBindList.TOGGLE_DRIFT.isDown());
    }
}
