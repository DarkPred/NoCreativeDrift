package com.github.darkpred.nocreativedrift;

import com.google.auto.service.AutoService;
import mekanism.common.item.interfaces.IJetpackItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;

@AutoService(DriftUtil.class)
public class NeoForgeDriftUtil extends DriftUtil {
    private final boolean mekanismLoaded;

    public NeoForgeDriftUtil() {
        mekanismLoaded = ModList.get().getModFileById("mekanism") != null;
    }

    @Override
    protected boolean isJetpackOn(Player player) {
        return isMekanismJetpackOn(player);
    }

    private boolean isMekanismJetpackOn(Player player) {
        if (mekanismLoaded) {
            ItemStack itemStack = IJetpackItem.getPrimaryJetpack(player);
            if (!itemStack.isEmpty()) {
                IJetpackItem.JetpackMode mode = ((IJetpackItem) itemStack.getItem()).getJetpackMode(itemStack);
                return mode == IJetpackItem.JetpackMode.NORMAL || mode == IJetpackItem.JetpackMode.HOVER;
            }
        }
        return false;
    }
}