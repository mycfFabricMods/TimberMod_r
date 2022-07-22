package mycf.timber;

public interface Toggleable {
    boolean getToggleMode$mycftimber();
    void setTimberMode$mycftimber(boolean mode);
    default boolean toggleMode$mycftimber() {
        this.setTimberMode$mycftimber(!this.getToggleMode$mycftimber());
        return this.getToggleMode$mycftimber();
    }
}
