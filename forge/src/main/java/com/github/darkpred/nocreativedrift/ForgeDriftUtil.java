package com.github.darkpred.nocreativedrift;

import com.blakebr0.ironjetpacks.util.JetpackUtils;
import com.google.auto.service.AutoService;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import tomson124.simplyjetpacks.item.JetpackItem;
import tomson124.simplyjetpacks.util.JetpackUtil;

@AutoService(DriftUtil.class)
public class ForgeDriftUtil extends DriftUtil {
    private final boolean simplyJetpacksLoaded;
    private final boolean ironJetpacksLoaded;

    public ForgeDriftUtil() {
        simplyJetpacksLoaded = ModList.get().getModFileById("simplyjetpacks") != null;
        ironJetpacksLoaded = ModList.get().getModFileById("ironjetpacks") != null;
    }

    @Override
    protected boolean isJetpackOn(Player player) {
        return isIronJetpackOn(player) || isSimplyJetpackOn(player);
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
                return jetpackItem.getEnergy(itemStack) > 0 || player.isCreative();
            }
        }

        return false;
    }
}