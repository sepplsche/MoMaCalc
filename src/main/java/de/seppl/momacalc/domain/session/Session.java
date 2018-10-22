package de.seppl.momacalc.domain.session;

import java.util.Collection;
import java.util.Objects;

import com.google.common.base.MoreObjects;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import de.seppl.momacalc.domain.tire.Tire;

/**
 * @author Seppl
 */
public class Session {

    private final SessionType type;
    private final Collection<Tire> tires;

    public Session(SessionType type, Collection<Tire> tires) {
        this.type = checkNotNull(type);
        this.tires = tires;
        checkArgument(!tires.isEmpty());
    }

    public SessionType type() {
        return type;
    }

    public Collection<Tire> tires() {
        return tires;
    }

    public String formattedTires() {
        return "tires for session " + type + ": " + tires.stream() //
                .sorted() //
                .map(tire -> type == SessionType.RACE ? tire.formatted() : tire.type().abr()) //
                .reduce("", (a, b) -> a + " " + b);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).addValue(type).addValue(tires).toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, tires);
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
                && tires.equals(other.tires());
    }
}
