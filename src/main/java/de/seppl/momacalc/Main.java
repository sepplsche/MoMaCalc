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
import de.seppl.momacalc.domain.Strategy;
import de.seppl.momacalc.domain.session.Session;
import de.seppl.momacalc.domain.tire.Tire;
import de.seppl.momacalc.domain.tire.TireType;
import de.seppl.momacalc.domain.tire.TireWear;

public class Main {

    public static void main(String[] args) {
        System.out.println();
        System.out.println(String.format("service called with: '%s'", StringUtils.join(args, " ")));

        StrategyService service = initService(args);
        calc(service);
    }

    private static void calc(StrategyService service) {
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
            System.out.println();
            System.out.println("=================================");
            System.out.println("Strategies for driver " + driver.incrementAndGet());
            System.out.println("=================================");

            driverStrats.forEach(strategy -> {
                int diffRounds = strategy.raceRounds() - service.runden();
                System.out.println();
                System.out.println("---------------------------------");
                System.out.println("Strategy with " + diffRounds + " rounds spare");
                System.out.println("---------------------------------");
                System.out.println(strategy.formattedTireTypes());
                System.out.println(strategy.formattedTotalTireTypes());
                System.out.println();
                strategy.sessions().stream() //
                        .sorted((a, b) -> a.type().compareTo(b.type())) //
                        .map(Session::formattedTires) //
                        .forEach(System.out::println);
            });
            System.out.println();
        });
    }

    private static StrategyService initService(String[] args) {
        Arguments arguments = new Arguments();
        ArgumentParser argsParser = new ArgumentParser(args);

        int runden = argsParser.parse(arguments.runden());
        int count = argsParser.parse(arguments.tireCount());

        SortedSet<TireType> types = argsParser.parse(arguments.tireTypes());
        List<TireWear> wears = argsParser.parse(arguments.tireWears());
        checkArgument(wears.size() % types.size() == 0,
                "Es müssen entsprechend viele Wears angegeben werden, wie Types!");

        Collection<Set<Tire>> driverTires = new ArrayList<>();
        for (int i = 0; i < wears.size(); i = i + 3) {
            Iterator<TireType> iterator = types.iterator();
            int j = 0;
            Set<Tire> tires = new HashSet<>();
            while (iterator.hasNext()) {
                tires.add(new Tire(iterator.next(), wears.get(i + j)));
                j++;
            }
            driverTires.add(tires);
        }
        return new StrategyService(driverTires, runden, count);
    }
}
