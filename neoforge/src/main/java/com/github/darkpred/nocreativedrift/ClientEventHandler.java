package com.github.darkpred.nocreativedrift;

import com.github.darkpred.nocreativedrift.platform.Services;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(Dist.CLIENT)
public class ClientEventHandler {

    @SubscribeEvent
    public static void onPlayerTickEvent(PlayerTickEvent.Post event) {
        //Ensures that the code is only run once on the logical client
        if (event.getEntity() instanceof LocalPlayer localPlayer) {
            Services.DRIFT_UTIL.onClientPlayerTick(localPlayer);
        }
    }

    @SubscribeEvent
    public static void onKeyInputEvent(InputEvent.Key event) {
        Services.DRIFT_UTIL.toggleDrift(KeyBindList.TOGGLE_DRIFT.isDown());
    }
}
