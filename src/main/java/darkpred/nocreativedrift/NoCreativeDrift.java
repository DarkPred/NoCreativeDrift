package darkpred.nocreativedrift;

import darkpred.nocreativedrift.client.ClientEventHandler;
import darkpred.nocreativedrift.client.Drift;
import darkpred.nocreativedrift.config.ClientConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(NoCreativeDrift.MOD_ID)
public class NoCreativeDrift {
    public static final String MOD_ID = "nocreativedrift";
    private static boolean ironJetpacksLoaded;
    private static boolean mekanismLoaded;
    private static boolean controllableLoaded;
    private static boolean invMoveLoaded;

    public NoCreativeDrift() {
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class,
                () -> new IExtensionPoint.DisplayTest(() -> "any", (remoteVersion, isFromServer) -> true));
        MinecraftForge.EVENT_BUS.register(this);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
        ironJetpacksLoaded = ModList.get().getModFileById("ironjetpacks") != null;
        mekanismLoaded = ModList.get().getModFileById("mekanism") != null;
        controllableLoaded = ModList.get().getModFileById("controllable") != null;
        invMoveLoaded = ModList.get().getModFileById("invmove") != null;
    }

    public static boolean isIronJetpacksLoaded() {
        return ironJetpacksLoaded;
    }

    public static boolean isMekanismLoaded() {
        return mekanismLoaded;
    }

    public static boolean isControllableLoaded() {
        return controllableLoaded;
    }

    public static boolean isInvMoveLoaded() {
        return invMoveLoaded;
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        ClientEventHandler.setCurDrift(Drift.values()[ClientConfig.DRIFT_STRENGTH.get()]);
    }
}
