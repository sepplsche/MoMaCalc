package de.seppl.momacalc.domain.tyre;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Seppl
 */
public enum TyreType {
    ULTRASOFT("u"), //
    SUPERSOFT("ss"), //
    SOFT("s"), //
    MEDIUM("m"), //
    HARD("h");

    private final String abr;

    private TyreType(String abr) {
        this.abr = checkNotNull(abr);
    }

    public String abr() {
        return abr;
    }
}
