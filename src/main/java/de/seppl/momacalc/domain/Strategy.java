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

        return "needed tyres: " + anteilTyres.stream() //
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
