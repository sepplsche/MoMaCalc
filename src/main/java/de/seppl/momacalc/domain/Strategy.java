package de.seppl.momacalc.domain;

import java.util.Collection;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

import com.google.common.base.MoreObjects;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Seppl
 */
public class Strategy {

    private final Collection<Session> sessions;

    public Strategy(Collection<Session> sessions) {
        this.sessions = checkNotNull(sessions);
    }

    public Collection<Session> sessions() {
        return sessions;
    }

    public Collection<TyreType> tyreTypes() {
        return sessions.stream() //
                .map(Session::tyres) //
                .flatMap(Collection::stream) //
                .map(Tyre::type) //
                .sorted((a, b) -> a.ordinal() - b.ordinal()) //
                .collect(toList());
    }

    public String formattedTyreTypes() {
        return tyreTypes().stream() //
                .distinct() //
                .map(type -> {
                    long typeCount = tyreTypes().stream() //
                            .filter(tType -> tType == type) //
                            .count();
                    return typeCount + type.abr();
                }) //
                .reduce("", (a, b) -> a + " " + b);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).addValue(sessions).toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(sessions);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Strategy other = (Strategy) obj;
        return sessions == other.sessions;
    }
}
