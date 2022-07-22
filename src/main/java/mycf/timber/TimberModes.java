package mycf.timber;

public enum TimberModes {
    AXES("MYCFTimberMode"),
    PLAYERS("MYCFTimberModeNever");

    public final String id;

    TimberModes(String str) {
        this.id = str;
    }
}
