package de.seppl.momacalc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import com.google.common.collect.ComparisonChain;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import de.seppl.momacalc.domain.Strategy;
import de.seppl.momacalc.domain.session.Session;
import de.seppl.momacalc.domain.session.SessionType;
import de.seppl.momacalc.domain.tire.Tire;
import de.seppl.momacalc.domain.tire.TireWear;

/**
 * @author Seppl
 */
public class StrategyService {

    private final Collection<Set<Tire>> driverTires;
    private final int runden;
    private final int count;

    public StrategyService(Collection<Set<Tire>> driverTires, int runden, int count) {
        this.driverTires = checkNotNull(driverTires);
        this.runden = runden;
        this.count = count;
        checkArgument(!driverTires.isEmpty());
        checkArgument(!driverTires.iterator().next().isEmpty());
    }

    public int runden() {
        return runden;
    }

    public List<Strategy> strategies(int rundenSparen) {
        List<Strategy> strategies = driverTires.stream() //
                .map(tires -> strategy(rundenSparen, tires)) //
                .collect(toList());

        List<Tire> tTires = strategies.stream() //
                .map(Strategy::sessions) //
                .flatMap(Collection::stream) //
                .filter(session -> session.type() == SessionType.TRAINING) //
                .map(Session::tires) //
                .flatMap(Collection::stream) //
                .distinct() //
                .collect(toList());

        strategies.stream() //
                .map(Strategy::sessions) //
                .flatMap(Collection::stream) //
                .filter(session -> session.type() == SessionType.TRAINING) //
                .forEach(session -> {
                    session.tires().clear();
                    session.tires().addAll(tTires);
                });

        return strategies;
    }

    private Strategy strategy(int rundenSparen, Set<Tire> tires) {
        Tire qTire = qTyre(tires);
        List<Tire> rTires = rTires(Optional.empty(), new ArrayList<Tire>(), rundenSparen, tires).stream() //
                .collect(toList());
        Set<Tire> tTires = tTire(tires, qTire, rTires);

        Collection<Session> sessions = new ArrayList<>();
        sessions.add(new Session(SessionType.QUALIFYING, Arrays.asList(qTire)));
        sessions.add(new Session(SessionType.RACE, rTires));
        sessions.add(new Session(SessionType.TRAINING, tTires));

        return new Strategy(sessions, count);
    }

    private Tire qTyre(Collection<Tire> tires) {
        return tires.stream() //
                .sorted() //
                .findFirst() //
                .get();
    }

    // auf jeder ebene gibts tyres.size() (3) möglichkeiten
    // der kürzeste weg der am nächsten an 0 rankommt wird genommen,
    // wenns mehrere gibt, dann der mit den meisten weichsten reifen
    private List<Tire> rTires(Optional<Tire> lastTire, List<Tire> strategy, int rundenSparen, Collection<Tire> tires) {
        lastTire.ifPresent(strategy::add);
        int strategyRunden = strategy.stream() //
                .map(Tire::wear) //
                .map(TireWear::runden) //
                .reduce(0, Integer::sum);
        if (strategy.size() >= 10 || strategyRunden >= runden - rundenSparen) {
            return strategy;
        }
        return tires.stream() //
                .map(tire -> rTires(Optional.of(tire), new ArrayList<>(strategy), rundenSparen, tires)) //
                .sorted((a, b) -> ComparisonChain.start() //
                        .compare(a.size(), b.size()) //
                        .compare(tyreTypes(a), tyreTypes(b)) //
                        .result()) //
                .findFirst() //
                .get();
    }

    private int tyreTypes(Collection<Tire> tires) {
        return tires.stream() //
                .map(Tire::type) //
                .map(Enum::ordinal) //
                .reduce(0, Integer::sum);
    }

    private Set<Tire> tTire(Collection<Tire> tires, Tire qTire, List<Tire> rTires) {
        Set<Tire> tTires = rTires.stream() //
                .distinct() //
                .collect(toSet());
        tTires.add(qTire);
        return tTires;
    }
}
