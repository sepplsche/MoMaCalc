package de.seppl.momacalc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.StringUtils;

import static com.google.common.base.Preconditions.checkArgument;

import de.seppl.momacalc.argument.ArgumentParser;
import de.seppl.momacalc.domain.Session;
import de.seppl.momacalc.domain.Strategy;
import de.seppl.momacalc.domain.Tyre;
import de.seppl.momacalc.domain.TyreType;
import de.seppl.momacalc.domain.TyreWear;

public class Main {
    public static void main(String[] args) {
        System.out.println(String.format("init service with: '%s'...", StringUtils.join(args, " ")));
        Arguments arguments = new Arguments();
        ArgumentParser argsParser = new ArgumentParser(args);
        int runden = argsParser.parse(arguments.runden());

        Service service = initService(args, arguments, argsParser);

        System.out.println();
        System.out.println("Safe:");
        calc(runden, service);
        System.out.println();
        System.out.println("Reifenschonend:");
        calc(runden - 2, service);
    }

    private static void calc(int runden, Service service) {
        Collection<Strategy> strategies = service.strategies(runden);
        System.out.println(strategies.size() + " drivers found");

        AtomicInteger counter = new AtomicInteger(0);
        strategies.forEach(strategy -> {
            System.out.println("Strategy for driver " + counter.incrementAndGet() + ": ");
            System.out.println(strategy.formattedTyreTypes());
            System.out.println(strategy.formattedTotalTyreTypes());
            strategy.sessions().stream() //
                    .sorted((a, b) -> a.type().compareTo(b.type())) //
                    .map(Session::formattedTyreTypes) //
                    .forEach(System.out::println);
        });
    }

    private static Service initService(String[] args, Arguments arguments, ArgumentParser argsParser) {
        int count = argsParser.parse(arguments.tyreCount());
        SortedSet<TyreType> types = argsParser.parse(arguments.tyreTypes());
        List<TyreWear> wears = argsParser.parse(arguments.tyreWears());
        checkArgument(wears.size() % types.size() == 0,
                "Es müssen entsprechend viele Wears angegeben werden, wie Types!");

        Collection<Collection<Tyre>> tyres = new ArrayList<>();
        for (int i = 0; i < wears.size(); i = i + 3) {
            Iterator<TyreType> iterator = types.iterator();
            int j = 0;

            List<Tyre> innerTyres = new ArrayList<>();
            while (iterator.hasNext()) {
                innerTyres.add(new Tyre(iterator.next(), wears.get(i + j)));
                j++;
            }
            tyres.add(innerTyres);
        }
        return new Service(tyres, count);
    }
}
