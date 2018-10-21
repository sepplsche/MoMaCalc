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
import de.seppl.momacalc.domain.tyre.TyreType;
import de.seppl.momacalc.domain.tyre.TyreWear;

public class Arguments {

    public Argument<Integer> runden() {
        return new MandatoryArgument<>("-r", this::argToInt);
    }

    public Argument<Integer> tyreCount() {
        return new OptionalArgument<>("-tc", this::argToInt, 0);
    }

    public Argument<SortedSet<TyreType>> tyreTypes() {
        return new OptionalArgument<>("-tt", this::argToTyreTypes,
                new TreeSet<TyreType>(Arrays.asList(TyreType.SOFT, TyreType.MEDIUM, TyreType.HARD)));
    }

    public Argument<List<TyreWear>> tyreWears() {
        return new MandatoryArgument<>("-tw", this::argToTyreWears);
    }

    private int argToInt(Collection<String> arg) {
        return arg.stream() //
                .map(Integer::parseInt) //
                .findFirst() //
                .get();
    }

    private SortedSet<TyreType> argToTyreTypes(List<String> args) {
        String arg = args.stream().reduce("", (a, b) -> a + b);
        SortedSet<TyreType> types = new TreeSet<>();
        if (arg.contains("sss")) {
            types.add(TyreType.SUPERSOFT);
            types.add(TyreType.SOFT);
        } else if (arg.contains("ss")) {
            types.add(TyreType.SUPERSOFT);
        } else if (arg.contains("s")) {
            types.add(TyreType.SOFT);
        }
        types.addAll(Stream.of(TyreType.values()) //
                .filter(type -> type != TyreType.SUPERSOFT) //
                .filter(type -> type != TyreType.SOFT) //
                .filter(type -> arg.contains(type.abr())) //
                .collect(toSet()));
        return types;
    }

    private List<TyreWear> argToTyreWears(List<String> args) {
        return args.stream() //
                .map(Integer::parseInt) //
                .map(TyreWear::new) //
                .collect(toList());
    }
}
