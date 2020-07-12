package darkpred.nocreativedrift;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod(NoCreativeDrift.MOD_ID)
public class NoCreativeDrift {
	public static final String MOD_ID = "nocreativedrift";
	
	public NoCreativeDrift() {
		MinecraftForge.EVENT_BUS.register(this);
	}
}
