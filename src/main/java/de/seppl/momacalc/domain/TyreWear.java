package de.seppl.momacalc.domain;

import java.util.Objects;

import com.google.common.base.MoreObjects;

/**
 * @author Seppl
 */
public class TyreWear {

    private final int runden;

    public TyreWear(int runden) {
        this.runden = runden;
    }

    public int runden() {
        return runden;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).addValue(runden).toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(runden);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        TyreWear other = (TyreWear) obj;
        return runden == other.runden;
    }
}
