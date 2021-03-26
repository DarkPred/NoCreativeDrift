package darkpred.nocreativedrift.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {
    private ClientConfig() {
    }

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.BooleanValue disableJetpackDrift;

    static {
        BUILDER.push("jetpack");
        disableJetpackDrift = BUILDER
                .comment("Disable the drift on jetpacks.")
                .define("disableJetpackDrift", true);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}
