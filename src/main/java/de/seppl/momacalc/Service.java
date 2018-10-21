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

import de.seppl.momacalc.domain.Session;
import de.seppl.momacalc.domain.SessionType;
import de.seppl.momacalc.domain.Strategy;
import de.seppl.momacalc.domain.Tyre;
import de.seppl.momacalc.domain.TyreType;
import de.seppl.momacalc.domain.TyreWear;

/**
 * @author Seppl
 */
public class Service {

    private final Collection<Collection<Tyre>> superTyres;
    private final int count;

    public Service(Collection<Collection<Tyre>> superTyres, int count) {
        this.superTyres = superTyres;
        this.count = count;
        checkArgument(!superTyres.isEmpty());
        checkArgument(!superTyres.iterator().next().isEmpty());
    }

    public Collection<Strategy> strategies(int runden) {
        Collection<Strategy> strategies = superTyres.stream() //
                .map(tyres -> strategy(runden, tyres)) //
                .collect(toList());

        List<TyreType> tTypes = strategies.stream() //
                .map(Strategy::sessions) //
                .flatMap(Collection::stream) //
                .filter(session -> session.type() == SessionType.TRAINING) //
                .map(Session::tyreTypes) //
                .flatMap(Collection::stream) //
                .distinct() //
                .collect(toList());

        strategies.stream() //
                .map(Strategy::sessions) //
                .flatMap(Collection::stream) //
                .filter(session -> session.type() == SessionType.TRAINING) //
                .forEach(session -> {
                    session.tyreTypes().clear();
                    session.tyreTypes().addAll(tTypes);
                });

        return strategies;
    }

    private Strategy strategy(int runden, Collection<Tyre> tyres) {

        TyreType qTyre = qTyreType(tyres);
        List<TyreType> rTyres = rTyres(Optional.empty(), new ArrayList<Tyre>(), runden, tyres).stream() //
                .map(Tyre::type) //
                .collect(toList());
        List<TyreType> tTyres = tTyreType(tyres, qTyre, rTyres);

        Collection<Session> sessions = new ArrayList<>();
        sessions.add(new Session(SessionType.QUALIFYING, Arrays.asList(qTyre)));
        sessions.add(new Session(SessionType.RACE, rTyres));
        sessions.add(new Session(SessionType.TRAINING, tTyres));

        return new Strategy(sessions, count);
    }

    private TyreType qTyreType(Collection<Tyre> tyres) {
        return tyres.stream() //
                .map(Tyre::type) //
                .sorted() //
                .findFirst() //
                .get();
    }

    // auf jeder ebene gibts tyres.size() (3) möglichkeiten
    // der kürzeste weg der am nächsten an 0 rankommt wird genommen,
    // wenns mehrere gibt, dann der mit den meisten weichsten reifen
    private List<Tyre> rTyres(Optional<Tyre> lastTyre, List<Tyre> strategy, int runden, Collection<Tyre> tyres) {
        lastTyre.ifPresent(strategy::add);
        int strategyRunden = strategy.stream() //
                .map(Tyre::wear) //
                .map(TyreWear::runden) //
                .reduce(0, (a, b) -> a + b);
        if (strategy.size() >= 10 || strategyRunden >= runden) {
            return strategy;
        }

        // List<List<Tyre>> strats = tyres.stream() //
        // .map(tyre -> rTyres(Optional.of(tyre), new ArrayList<>(strategy), runden, tyres)) //
        // .collect(toList());
        //
        // System.out.println("step");
        // strats.stream() //
        // .distinct() //
        // .forEach(strat -> {
        // strat.forEach(System.out::print);
        // System.out.println();
        // });

        return tyres.stream() //
                .map(tyre -> rTyres(Optional.of(tyre), new ArrayList<>(strategy), runden, tyres)) //
                .sorted((a, b) -> ComparisonChain.start() //
                        .compare(a.size(), b.size()) //
                        .compare(tyreTypes(a), tyreTypes(b)) //
                        .result()) //
                .findFirst() //
                .get();
    }

    private int tyreTypes(Collection<Tyre> tyres) {
        return tyres.stream() //
                .map(Tyre::type) //
                .map(Enum::ordinal) //
                .reduce(0, (a, b) -> a + b);
    }

    private List<TyreType> tTyreType(Collection<Tyre> tyres, TyreType qTyreType, List<TyreType> rTyres) {
        Set<TyreType> tTyreTypes = rTyres.stream() //
                .distinct() //
                .collect(toSet());
        tTyreTypes.add(qTyreType);
        return tyres.stream() //
                .map(Tyre::type) //
                .filter(tyreType -> tTyreTypes.contains(tyreType)) //
                .collect(toList());
    }
}
