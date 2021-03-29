package darkpred.nocreativedrift;

import darkpred.nocreativedrift.config.ClientConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import org.apache.commons.lang3.tuple.Pair;

@Mod(NoCreativeDrift.MOD_ID)
public class NoCreativeDrift {
    public static final String MOD_ID = "nocreativedrift";
    private static boolean simplyJetpacksLoaded = false;
    private static boolean ironJetpacksLoaded = false;

    public NoCreativeDrift() {
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST,
                () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));
        MinecraftForge.EVENT_BUS.register(this);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);

        simplyJetpacksLoaded = ModList.get().getModFileById("simplyjetpacks") != null;
        ironJetpacksLoaded = ModList.get().getModFileById("ironjetpacks") != null;

    }

    public static boolean isSimplyJetpacksLoaded() {
        return simplyJetpacksLoaded;
    }

    public static boolean isIronJetpacksLoaded() {
        return ironJetpacksLoaded;
    }
}