package de.seppl.momacalc.domain.tire;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Seppl
 */
public enum TireType {

    ULTRASOFT("u"), //
    SUPERSOFT("ss"), //
    SOFT("s"), //
    MEDIUM("m"), //
    HARD("h");

    private final String abr;

    private TireType(String abr) {
        this.abr = checkNotNull(abr);
    }

    public String abr() {
        return abr;
    }
}
