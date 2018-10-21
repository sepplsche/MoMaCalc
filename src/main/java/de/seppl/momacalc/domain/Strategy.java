package de.seppl.momacalc.domain;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

import com.google.common.base.MoreObjects;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Seppl
 */
public class Strategy {

    private final Collection<Session> sessions;
    private final int totalTyreCount;

    public Strategy(Collection<Session> sessions, int totalTyreCount) {
        this.sessions = checkNotNull(sessions);
        this.totalTyreCount = totalTyreCount;
    }

    public Collection<Session> sessions() {
        return sessions;
    }

    public Collection<TyreType> tyreTypes() {
        return sessions.stream() //
                .map(Session::tyreTypes) //
                .flatMap(Collection::stream) //
                .sorted() //
                .collect(toList());
    }

    public String formattedTyreTypes() {
        // anteilsmässig erhöhen bis auf total tyrecount
        List<Integer> tyres = tyreTypes().stream() //
                .distinct() //
                .map(type -> {
                    long typeCount = tyreTypes().stream() //
                            .filter(tType -> tType == type) //
                            .count();
                    return typeCount;
                }) //
                .map(Long::intValue) //
                .collect(toList());

        MathContext mc = new MathContext(0, RoundingMode.HALF_UP);
        BigDecimal summe = new BigDecimal(tyres.stream().reduce(0, (a, b) -> a + b));
        tyres.stream().map(tyre -> new BigDecimal(tyre).divide(summe)).collect(to);

        return "needed tyres: " + tyreTypes().stream() //
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
