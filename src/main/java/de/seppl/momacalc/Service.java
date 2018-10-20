package de.seppl.momacalc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
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

/**
 * @author Seppl
 */
public class Service {

    private Collection<Collection<Tyre>> superTyres;

    public Service(Collection<Collection<Tyre>> superTyres) {
        this.superTyres = superTyres;
        checkArgument(!superTyres.isEmpty());
        checkArgument(!superTyres.iterator().next().isEmpty());
    }

    public Collection<Strategy> strategies(int runden) {
        return superTyres.stream() //
                .map(tyres -> strategy(runden, tyres)) //
                .collect(toList());
    }

    private Strategy strategy(int runden, Collection<Tyre> tyres) {

        Tyre qTyre = qTyre(tyres);
        List<Tyre> rTyres = rTyres(new ArrayList<Tyre>(), runden, tyres);
        List<Tyre> tTyres = tTyres(tyres, qTyre, rTyres);

        Collection<Session> sessions = new ArrayList<>();
        sessions.add(new Session(SessionType.QUALIFYING, Arrays.asList(qTyre)));
        sessions.add(new Session(SessionType.RACE, rTyres));
        sessions.add(new Session(SessionType.TRAINING, tTyres));

        return new Strategy(sessions);
    }

    private Tyre qTyre(Collection<Tyre> tyres) {
        return tyres.stream() //
                .sorted((a, b) -> a.type().compareTo(b.type())) //
                .findFirst().get();
    }

    // auf jeder ebene gibts tyres.size() (3) möglichkeiten
    // der kürzeste weg der am nächsten an 0-2 rankommt wird genommen,
    // wenns mehrere gibt, dann der am nächsten an 0,
    // wenns mehrere gibt, dann der mit den meisten weichsten reifen
    private List<Tyre> rTyres(List<Tyre> strategy, int restRunden, Collection<Tyre> tyres) {
        if (restRunden <= 2) {
            return strategy;
        }
        return tyres.stream() //
                .map(tyre -> {
                    strategy.add(tyre);
                    return rTyres(strategy, restRunden - tyre.wear().runden(), tyres);
                }) //
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

    private List<Tyre> tTyres(Collection<Tyre> tyres, Tyre qTyre, List<Tyre> rTyres) {
        Set<TyreType> tTyreTypes = rTyres.stream() //
                .map(Tyre::type) //
                .distinct() //
                .collect(toSet());
        tTyreTypes.add(qTyre.type());
        return tyres.stream() //
                .filter(tyre -> tTyreTypes.contains(tyre.type())) //
                .collect(toList());
    }
}
