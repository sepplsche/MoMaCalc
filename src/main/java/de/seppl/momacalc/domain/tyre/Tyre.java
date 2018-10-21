package de.seppl.momacalc.domain.tyre;

import java.util.Objects;

import com.google.common.base.MoreObjects;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Seppl
 */
public class Tyre {

    private final TyreType type;
    private final TyreWear wear;

    public Tyre(TyreType type, TyreWear wear) {
        this.type = checkNotNull(type);
        this.wear = checkNotNull(wear);
    }

    public TyreType type() {
        return type;
    }

    public TyreWear wear() {
        return wear;
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
        Tyre other = (Tyre) obj;
        return type.equals(other.type) //
                && wear.equals(other.wear);
    }
}
