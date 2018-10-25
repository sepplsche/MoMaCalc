package de.seppl.momacalc.domain.race;

/**
 * @author Seppl
 */
public enum MotorMode {

    UEBERHOLEN(4), //
    HOCH(3), //
    MITTEL(2), //
    NIEDRIG(1);

    private final int level;

    private MotorMode(int level) {
        this.level = level;
    }

    public int level() {
        return level;
    }
}
