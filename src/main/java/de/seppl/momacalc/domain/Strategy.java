package de.seppl.momacalc.domain;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ComparisonChain;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Seppl
 */
public class Strategy {

    private final Collection<Session> sessions;
    private final int totalTyreCount;
    private final Set<Tyre> tyres;

    public Strategy(Collection<Session> sessions, int totalTyreCount, Set<Tyre> tyres) {
        this.sessions = checkNotNull(sessions);
        this.totalTyreCount = totalTyreCount;
        this.tyres = tyres;
        checkArgument(!tyres.isEmpty());
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
        Map<TyreType, TyreWear> wearByType = tyres.stream().collect(toMap(Tyre::type, Tyre::wear));
        return session(SessionType.RACE).tyreTypes().stream() //
                .map(wearByType::get) //
                .map(TyreWear::runden).reduce(0, (a, b) -> a + b);
    }

    public Collection<TyreType> tyreTypes() {
        return sessions.stream() //
                .map(Session::tyreTypes) //
                .flatMap(Collection::stream) //
                .sorted() //
                .collect(toList());
    }

    public String formattedTyreTypes() {
        List<TyreTypeCount> tyres = tyreTypes().stream() //
                .distinct() //
                .map(type -> {
                    long count = tyreTypes().stream() //
                            .filter(tType -> tType == type) //
                            .count();
                    return new TyreTypeCount(type, Long.valueOf(count).intValue());
                }) //
                .collect(toList());

        return "needed tyres: " + tyres.stream() //
                .sorted((a, b) -> a.type.compareTo(b.type)) //
                .map(TyreTypeCount::formatted) //
                .reduce("", (a, b) -> a + " " + b);
    }

    public String formattedTotalTyreTypes() {
        // anteilsmässig erhöhen bis auf total tyrecount
        List<TyreTypeCount> tyres = tyreTypes().stream() //
                .distinct() //
                .map(type -> {
                    long count = tyreTypes().stream() //
                            .filter(tType -> tType == type) //
                            .count();
                    return new TyreTypeCount(type, Long.valueOf(count).intValue());
                }) //
                .collect(toList());

        int summe = tyres.stream().map(TyreTypeCount::count).reduce(0, (a, b) -> a + b);

        BigDecimal faktor = new BigDecimal(totalTyreCount).divide(new BigDecimal(summe), new MathContext(24));
        List<TyreTypeCount> anteilTyres = tyres.stream() //
                .map(tyre -> {
                    int anteilCount =
                            faktor.multiply(new BigDecimal(tyre.count())).round(new MathContext(0)).intValue();
                    return new TyreTypeCount(tyre.type, anteilCount);
                }) //
                .collect(toList());

        int diff = totalTyreCount - anteilTyres.stream().map(TyreTypeCount::count).reduce(0, (a, b) -> a + b);
        if (diff > 0) {
            TyreTypeCount min = anteilTyres.stream().sorted().findFirst().get();
            TyreTypeCount diffCount = new TyreTypeCount(min.type, min.count() + diff);
            anteilTyres.remove(min);
            anteilTyres.add(diffCount);
        } else {
            TyreTypeCount max = anteilTyres.stream().sorted(Collections.reverseOrder()).findFirst().get();
            TyreTypeCount diffCount = new TyreTypeCount(max.type, max.count() + diff);
            anteilTyres.remove(max);
            anteilTyres.add(diffCount);
        }

        return "total tyres: " + anteilTyres.stream() //
                .sorted((a, b) -> a.type.compareTo(b.type)) //
                .map(TyreTypeCount::formatted) //
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

    public static class TyreTypeCount implements Comparable<TyreTypeCount> {

        private final TyreType type;
        private final int count;

        public TyreTypeCount(TyreType type, int count) {
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
        public int compareTo(TyreTypeCount o) {
            return ComparisonChain.start() //
                    .compare(count, o.count) //
                    .compare(type, o.type) //
                    .result();
        }
    }
}
