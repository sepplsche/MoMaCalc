package de.seppl.momacalc.domain;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ComparisonChain;

import static com.google.common.base.Preconditions.checkNotNull;

import de.seppl.momacalc.domain.session.Session;
import de.seppl.momacalc.domain.session.SessionType;
import de.seppl.momacalc.domain.tire.Tire;
import de.seppl.momacalc.domain.tire.TireType;
import de.seppl.momacalc.domain.tire.TireWear;

/**
 * @author Seppl
 */
public class Strategy {

    private final Collection<Session> sessions;
    private final int totalTireCount;

    public Strategy(Collection<Session> sessions, int totalTireCount) {
        this.sessions = checkNotNull(sessions);
        this.totalTireCount = totalTireCount;
    }

    public Collection<Session> sessions() {
        return sessions;
    }

    public Session session(SessionType type) {
        return sessions.stream() //
                .filter(session -> session.type() == type) //
                .findFirst().get();
    }

    public int raceRounds() {
        return session(SessionType.RACE).tires().stream() //
                .map(Tire::wear) //
                .map(TireWear::runden) //
                .reduce(0, (a, b) -> a + b);
    }

    public Collection<TireType> tireTypes() {
        return sessions.stream() //
                .map(Session::tires) //
                .flatMap(Collection::stream) //
                .map(Tire::type) //
                .sorted() //
                .collect(toList());
    }

    public String formattedTireTypes() {
        List<TireTypeCount> tires = tireTypes().stream() //
                .distinct() //
                .map(type -> {
                    long count = tireTypes().stream() //
                            .filter(tType -> tType == type) //
                            .count();
                    return new TireTypeCount(type, Long.valueOf(count).intValue());
                }) //
                .collect(toList());

        return "needed tyres: " + tires.stream() //
                .sorted((a, b) -> a.type.compareTo(b.type)) //
                .map(TireTypeCount::formatted) //
                .reduce("", (a, b) -> a + " " + b);
    }

    public String formattedTotalTireTypes() {
        // anteilsmässig erhöhen bis auf total tyrecount
        List<TireTypeCount> tires = tireTypes().stream() //
                .distinct() //
                .map(type -> {
                    long count = tireTypes().stream() //
                            .filter(tType -> tType == type) //
                            .count();
                    return new TireTypeCount(type, Long.valueOf(count).intValue());
                }) //
                .collect(toList());

        int summe = tires.stream().map(TireTypeCount::count).reduce(0, (a, b) -> a + b);

        BigDecimal faktor = new BigDecimal(totalTireCount).divide(new BigDecimal(summe), new MathContext(24));
        List<TireTypeCount> anteilTires = tires.stream() //
                .map(tire -> {
                    int anteilCount =
                            faktor.multiply(new BigDecimal(tire.count())).round(new MathContext(0)).intValue();
                    return new TireTypeCount(tire.type, anteilCount);
                }) //
                .collect(toList());

        int diff = totalTireCount - anteilTires.stream().map(TireTypeCount::count).reduce(0, (a, b) -> a + b);
        if (diff > 0) {
            TireTypeCount min = anteilTires.stream().sorted().findFirst().get();
            TireTypeCount diffCount = new TireTypeCount(min.type, min.count() + diff);
            anteilTires.remove(min);
            anteilTires.add(diffCount);
        } else {
            TireTypeCount max = anteilTires.stream().sorted(Collections.reverseOrder()).findFirst().get();
            TireTypeCount diffCount = new TireTypeCount(max.type, max.count() + diff);
            anteilTires.remove(max);
            anteilTires.add(diffCount);
        }

        return "total tyres: " + anteilTires.stream() //
                .sorted((a, b) -> a.type.compareTo(b.type)) //
                .map(TireTypeCount::formatted) //
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

    public static class TireTypeCount implements Comparable<TireTypeCount> {

        private final TireType type;
        private final int count;

        public TireTypeCount(TireType type, int count) {
            this.type = checkNotNull(type);
            this.count = count;
        }

        public int count() {
            return count;
        }

        public String formatted() {
            return count + type.abr();
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this) //
                    .addValue(count) //
                    .addValue(type) //
                    .toString();
        }

        @Override
        public int compareTo(TireTypeCount o) {
            return ComparisonChain.start() //
                    .compare(count, o.count) //
                    .compare(type, o.type) //
                    .result();
        }
    }
}
