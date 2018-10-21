package de.seppl.momacalc.domain.session;

import java.util.List;
import java.util.Objects;

import com.google.common.base.MoreObjects;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import de.seppl.momacalc.domain.tyre.TyreType;

/**
 * @author Seppl
 */
public class Session {

    private final SessionType type;
    private final List<TyreType> tyreTypes;

    public Session(SessionType type, List<TyreType> tyreTypes) {
        this.type = checkNotNull(type);
        this.tyreTypes = tyreTypes;
        checkArgument(!tyreTypes.isEmpty());
    }

    public SessionType type() {
        return type;
    }

    public List<TyreType> tyreTypes() {
        return tyreTypes;
    }

    public String formattedTyreTypes() {
        return "tyres for session " + type + ": "
                + tyreTypes.stream() //
                        .sorted() //
                        .map(TyreType::abr) //
                        .reduce("", (a, b) -> a + " " + b);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).addValue(type).addValue(tyreTypes).toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, tyreTypes);
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
                && tyreTypes.equals(other.tyreTypes());
    }
}
