package darkpred.nocreativedrift.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {
    private ClientConfig() {
    }

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.BooleanValue disableJetpackDrift;
    public static final ForgeConfigSpec.BooleanValue disableNonCreativeDrift;
    public static final ForgeConfigSpec.BooleanValue disableVerticalDrift;

    static {
        disableVerticalDrift = BUILDER
                .comment("Disable the drift during vertical flight")
                .define("disableVerticalDrift", false);
        BUILDER.push("jetpack");
        disableJetpackDrift = BUILDER
                .comment("Disable the drift on jetpacks.")
                .define("disableJetpackDrift", true);
        disableNonCreativeDrift = BUILDER
                .comment("Disable the drift during non creative flight (e.g. Angel Ring mod)")
                .define("disableNonCreativeDrift", false);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}
