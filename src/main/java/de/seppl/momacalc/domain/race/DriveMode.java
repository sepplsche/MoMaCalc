package de.seppl.momacalc.domain.race;

/**
 * @author Seppl
 */
public enum DriveMode {

    ANGREIFEN(5), //
    AGGRESSIV(4), //
    NEUTRAL(3), //
    KONSERVATIV(2), //
    AUFHALTEN(1);

    private final int level;

    private DriveMode(int level) {
        this.level = level;
    }

    public int level() {
        return level;
    }
}
