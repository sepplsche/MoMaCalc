package de.seppl.momacalc;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import de.seppl.momacalc.argument.Argument;
import de.seppl.momacalc.argument.Argument.MandatoryArgument;
import de.seppl.momacalc.argument.Argument.OptionalArgument;
import de.seppl.momacalc.domain.tire.TireType;
import de.seppl.momacalc.domain.tire.TireWear;

public class Arguments {

    public Argument<Integer> runden() {
        return new MandatoryArgument<>("-r", this::argToInt);
    }

    public Argument<Integer> tireCount() {
        return new OptionalArgument<>("-tc", this::argToInt, 0);
    }

    public Argument<SortedSet<TireType>> tireTypes() {
        return new OptionalArgument<>("-tt", this::argToTireTypes,
                new TreeSet<TireType>(Arrays.asList(TireType.SOFT, TireType.MEDIUM, TireType.HARD)));
    }

    public Argument<List<TireWear>> tireWears() {
        return new MandatoryArgument<>("-tw", this::argToTireWears);
    }

    private int argToInt(Collection<String> args) {
        return args.stream().findFirst().map(Integer::parseInt).get();
    }

    private SortedSet<TireType> argToTireTypes(List<String> args) {
        String arg = args.stream().reduce("", String::concat);
        SortedSet<TireType> types = new TreeSet<>();
        if (arg.toLowerCase().contains("sss")) {
            types.add(TireType.SUPERSOFT);
            types.add(TireType.SOFT);
        } else if (arg.toLowerCase().contains("ss")) {
            types.add(TireType.SUPERSOFT);
        } else if (arg.toLowerCase().contains("s")) {
            types.add(TireType.SOFT);
        }
        types.addAll(Stream.of(TireType.values()) //
                .filter(type -> type != TireType.SUPERSOFT) //
                .filter(type -> type != TireType.SOFT) //
                .filter(type -> arg.toLowerCase().contains(type.abr().toLowerCase())) //
                .collect(toSet()));
        return types;
    }

    private List<TireWear> argToTireWears(List<String> args) {
        return args.stream() //
                .map(Integer::valueOf) //
                .map(TireWear::new) //
                .collect(toList());
    }
}
