package de.seppl.momacalc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import com.google.common.base.Strings;

import static com.google.common.base.Preconditions.checkArgument;

import de.seppl.momacalc.argument.ArgumentParser;
import de.seppl.momacalc.domain.tire.Tire;
import de.seppl.momacalc.domain.tire.TireType;
import de.seppl.momacalc.domain.tire.TireWear;

public class Main {

    private static final Path ARGS_FILE = Paths.get("C:/dev/git-repos/MoMaCalc/target/moma.args");

    public static void main(String[] argArr) {

        Collection<String> args = readFileInput();
        args = readUserInputStrategy(args);
        saveFileInput(args);

        Arguments arguments = new Arguments();
        ArgumentParser argsParser = new ArgumentParser(args);

        Service service = initStrategyService(arguments, argsParser);
        service.calc();
    }

    private static Collection<String> readUserInputStrategy(Collection<String> args) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            System.out.print("Total Race Rounds: ");
            String rounds = reader.readLine();
            System.out.print("Total Tire Count: ");
            String count = reader.readLine();
            System.out.print("Tire Types: ");
            String types = reader.readLine();
            System.out.print("Tire Wears: ");
            String wears = reader.readLine();

            if (!Strings.isNullOrEmpty(rounds)) {
                args.removeIf(arg -> arg.startsWith("-r"));
                args.add("-r " + rounds);
            }
            if (!Strings.isNullOrEmpty(count)) {
                args.removeIf(arg -> arg.startsWith("-tc"));
                args.add("-tc " + count);
            }
            if (!Strings.isNullOrEmpty(types)) {
                args.removeIf(arg -> arg.startsWith("-tt"));
                args.add("-tt " + types);
            }
            if (!Strings.isNullOrEmpty(wears)) {
                args.removeIf(arg -> arg.startsWith("-tw"));
                args.add("-tw " + wears);
            }
            return args;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Collection<String> readFileInput() {
        try {
            return Files.exists(ARGS_FILE) //
                    ? Files.readAllLines(ARGS_FILE) //
                    : new ArrayList<>();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void saveFileInput(Collection<String> args) {
        try {
            Files.write(ARGS_FILE, args, //
                    StandardOpenOption.CREATE, //
                    StandardOpenOption.WRITE, //
                    StandardOpenOption.TRUNCATE_EXISTING);
            System.out.println("saved args: " + args);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Service initStrategyService(Arguments arguments, ArgumentParser argsParser) {
        int runden = argsParser.parse(arguments.runden());
        int count = argsParser.parse(arguments.tireCount());
        SortedSet<TireType> types = argsParser.parse(arguments.tireTypes());
        List<TireWear> wears = argsParser.parse(arguments.tireWears());

        checkArgument(runden > 0, "Runden muss grösser 0 sein!");
        checkArgument(count > 0, "Tirecount muss grösser 0 sein!");
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
