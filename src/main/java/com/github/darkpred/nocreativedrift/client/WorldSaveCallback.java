package com.github.darkpred.nocreativedrift.client;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface WorldSaveCallback {
    Event<WorldSaveCallback> EVENT = EventFactory.createArrayBacked(
            WorldSaveCallback.class, listeners -> () -> {
                for (WorldSaveCallback listener : listeners) {
                    listener.onSave();
                }
            });

    void onSave();
}
