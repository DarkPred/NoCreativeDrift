package darkpred.nocreativedrift.client;

public enum Drift {
    VANILLA(1),
    STRONG(0.90),
    WEAK(0.7),
    DISABLED(0);

    private final double multi;

    Drift(double multi) {
        this.multi = multi;
    }

    public double getMulti() {
        return multi;
    }
}
