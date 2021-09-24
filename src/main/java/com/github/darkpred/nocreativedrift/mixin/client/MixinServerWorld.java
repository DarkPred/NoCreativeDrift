package com.github.darkpred.nocreativedrift.mixin.client;

import com.github.darkpred.nocreativedrift.client.WorldSaveCallback;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ProgressListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public class MixinServerWorld {

    @Inject(method = "save", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerChunkManager;save(Z)V"))
    private void save(ProgressListener progressListener, boolean flush, boolean bl, CallbackInfo ci) {
        WorldSaveCallback.EVENT.invoker().onSave();
    }
}
