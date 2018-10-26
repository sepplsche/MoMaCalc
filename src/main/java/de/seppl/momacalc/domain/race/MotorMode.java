package de.seppl.momacalc.domain.race;

import java.util.stream.Stream;

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

    public static MotorMode of(int level) {
        return Stream.of(MotorMode.values()) //
                .filter(value -> value.level() == level) //
                .findFirst() //
                .get();
    }
}
