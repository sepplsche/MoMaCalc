package de.seppl.momacalc.domain.session;

import java.util.List;
import java.util.Objects;

import com.google.common.base.MoreObjects;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import de.seppl.momacalc.domain.tyre.TireType;

/**
 * @author Seppl
 */
public class Session {

    private final SessionType type;
    private final List<TireType> tireTypes;

    public Session(SessionType type, List<TireType> tireTypes) {
        this.type = checkNotNull(type);
        this.tireTypes = tireTypes;
        checkArgument(!tireTypes.isEmpty());
    }

    public SessionType type() {
        return type;
    }

    public List<TireType> tireTypes() {
        return tireTypes;
    }

    public String formattedTireTypes() {
        return "tyres for session " + type + ": "
                + tireTypes.stream() //
                        .sorted() //
                        .map(TireType::abr) //
                        .reduce("", (a, b) -> a + " " + b);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).addValue(type).addValue(tireTypes).toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, tireTypes);
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
                && tireTypes.equals(other.tireTypes());
    }
}
