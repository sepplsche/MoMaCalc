package de.seppl.momacalc.domain;

import java.util.List;
import java.util.Objects;

import com.google.common.base.MoreObjects;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Seppl
 */
public class Session {

    private final SessionType type;
    private final List<Tyre> tyres;

    public Session(SessionType type, List<Tyre> tyres) {
        this.type = checkNotNull(type);
        this.tyres = tyres;
        checkArgument(!tyres.isEmpty());
    }

    public SessionType type() {
        return type;
    }

    public List<Tyre> tyres() {
        return tyres;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).addValue(type).addValue(tyres).toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, tyres);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Session other = (Session) obj;
        return type.equals(other.type()) //
                && tyres.equals(other.tyres());
    }
}
