package de.seppl.momacalc.domain.tire;

import java.util.Objects;

import com.google.common.base.MoreObjects;

/**
 * @author Seppl
 */
public class TireWear {

    private final int runden;

    public TireWear(int runden) {
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
        TireWear other = (TireWear) obj;
        return runden == other.runden;
    }
}
