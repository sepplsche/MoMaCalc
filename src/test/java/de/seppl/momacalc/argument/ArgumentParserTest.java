package de.seppl.momacalc.argument;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import de.seppl.momacalc.Arguments;
import de.seppl.momacalc.argument.Argument.MandatoryArgument;
import de.seppl.momacalc.domain.tire.TireWear;

public class ArgumentParserTest {

    @Test
    public void twoArgs() throws Exception {

        Collection<String> args = Arrays.asList("-f", "F:\\Seppl PF Konto", "-r", "33");

        ArgumentParser parser = new ArgumentParser(args);
        Arguments arguments = new Arguments();

        int runden = parser.parse(arguments.runden());
        assertThat(runden, equalTo(33));
    }

    @Test
    public void duplicateArgs() throws Exception {
        Collection<String> args = Arrays.asList("-arg", "1", "2", "-d", "2013-01-01", "-arg", "3", "4");
        Argument<List<Integer>> arg =
                new MandatoryArgument<>("-arg", (a -> a.stream().map(Integer::valueOf).collect(toList())));

        ArgumentParser parser = new ArgumentParser(args);

        List<Integer> ints = parser.parse(arg);
        assertThat(ints.size(), is(4));

        int summe = ints.stream().reduce(0, Integer::sum);
        assertThat(summe, is(10));
    }

    @Test
    public void argTyreWear() throws Exception {
        Collection<String> args = Arrays.asList("-tw", "7", "11", "16", "8", "15", "17");

        ArgumentParser parser = new ArgumentParser(args);
        Arguments arguments = new Arguments();

        Collection<TireWear> tireWears = parser.parse(arguments.tireWears());
        assertThat(tireWears.size(), equalTo(6));
        assertThat(tireWears.contains(new TireWear(7)), equalTo(true));
        assertThat(tireWears.contains(new TireWear(11)), equalTo(true));
        assertThat(tireWears.contains(new TireWear(16)), equalTo(true));
        assertThat(tireWears.contains(new TireWear(8)), equalTo(true));
        assertThat(tireWears.contains(new TireWear(15)), equalTo(true));
        assertThat(tireWears.contains(new TireWear(17)), equalTo(true));
    }
}
