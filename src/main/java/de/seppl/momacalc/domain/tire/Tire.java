package de.seppl.momacalc.domain.tire;

import java.util.Objects;

import com.google.common.base.MoreObjects;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Seppl
 */
public class Tire implements Comparable<Tire> {

    private final TireType type;
    private final TireWear wear;

    public Tire(TireType type, TireWear wear) {
        this.type = checkNotNull(type);
        this.wear = checkNotNull(wear);
    }

    public TireType type() {
        return type;
    }

    public TireWear wear() {
        return wear;
    }

    public String formatted() {
        return String.format("%s (%d)", type.abr(), wear.runden());
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).addValue(type).addValue(wear).toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, wear);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Tire other = (Tire) obj;
        return type.equals(other.type) //
                && wear.equals(other.wear);
    }

    @Override
    public int compareTo(Tire o) {
        return type.compareTo(o.type);
    }
}
