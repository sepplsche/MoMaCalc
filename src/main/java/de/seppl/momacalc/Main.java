package de.seppl.momacalc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

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

        Service service = initService(args);
        calc(service);
    }

    private static void calc(Service service) {
        List<List<Strategy>> strategies = IntStream.of(-2, 0, 2) //
                .mapToObj(service::strategies) //
                .collect(toList());
        int drivers = strategies.get(0).size();
        List<List<Strategy>> driverStrategies = IntStream.range(0, drivers) //
                .mapToObj(driver -> strategies.stream() //
                        .map(strat -> strat.get(driver)) //
                        .collect(toList())) //
                .collect(toList());

        AtomicInteger driver = new AtomicInteger(0);
        driverStrategies.forEach(driverStrats -> {
            System.out.println("=================================");
            System.out.println("Strategies for driver " + driver.incrementAndGet());
            System.out.println("=================================");

            driverStrats.forEach(strategy -> {
                int diffRounds = strategy.raceRounds() - service.runden();
                System.out.println("Strategy with " + diffRounds + " rounds spare");
                System.out.println("---------------------------------");
                System.out.println(strategy.formattedTyreTypes());
                System.out.println(strategy.formattedTotalTyreTypes());
                strategy.sessions().stream() //
                        .sorted((a, b) -> a.type().compareTo(b.type())) //
                        .map(Session::formattedTyreTypes) //
                        .forEach(System.out::println);
                System.out.println("---------------------------------");
            });
        });
    }

    private static Service initService(String[] args) {
        Arguments arguments = new Arguments();
        ArgumentParser argsParser = new ArgumentParser(args);

        int runden = argsParser.parse(arguments.runden());
        int count = argsParser.parse(arguments.tyreCount());

        SortedSet<TyreType> types = argsParser.parse(arguments.tyreTypes());
        List<TyreWear> wears = argsParser.parse(arguments.tyreWears());
        checkArgument(wears.size() % types.size() == 0,
                "Es müssen entsprechend viele Wears angegeben werden, wie Types!");

        Collection<Set<Tyre>> tyres = new ArrayList<>();
        for (int i = 0; i < wears.size(); i = i + 3) {
            Iterator<TyreType> iterator = types.iterator();
            int j = 0;
            Set<Tyre> innerTyres = new HashSet<>();
            while (iterator.hasNext()) {
                innerTyres.add(new Tyre(iterator.next(), wears.get(i + j)));
                j++;
            }
            tyres.add(innerTyres);
        }
        return new Service(tyres, runden, count);
    }
}
