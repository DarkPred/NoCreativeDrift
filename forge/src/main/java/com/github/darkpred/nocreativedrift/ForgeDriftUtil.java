package com.github.darkpred.nocreativedrift;

import com.blakebr0.ironjetpacks.util.JetpackUtils;
import com.google.auto.service.AutoService;
import mekanism.common.item.interfaces.IJetpackItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import stormedpanda.simplyjetpacks.item.JetpackItem;
import stormedpanda.simplyjetpacks.util.JetpackUtil;

@AutoService(DriftUtil.class)
public class ForgeDriftUtil extends DriftUtil {
    private final boolean simplyJetpacksLoaded;
    private final boolean ironJetpacksLoaded;
    private final boolean mekanismLoaded;

    public ForgeDriftUtil() {
        simplyJetpacksLoaded = ModList.get().getModFileById("simplyjetpacks") != null;
        ironJetpacksLoaded = ModList.get().getModFileById("ironjetpacks") != null;
        mekanismLoaded = ModList.get().getModFileById("mekanism") != null;
    }

    @Override
    protected boolean isJetpackOn(Player player) {
        return isMekanismJetpackOn(player) || isIronJetpackOn(player) || isSimplyJetpackOn(player);
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

    private boolean isIronJetpackOn(Player player) {
        if (ironJetpacksLoaded) {
            return JetpackUtils.isFlying(player);
        }
        return false;
    }

    private boolean isSimplyJetpackOn(Player player) {
        if (simplyJetpacksLoaded) {
            ItemStack itemStack = JetpackUtil.getFromBothSlots(player);
            if (itemStack.getItem() instanceof JetpackItem jetpackItem && jetpackItem.isEngineOn(itemStack)) {
                return (itemStack.getTag() != null && itemStack.getTag().getInt("Energy") > 0) || player.isCreative();
            }
        }

        return false;
    }
}