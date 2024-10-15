package adventofcode.y2022;

import adventofcode.commons.AOCProblem;

import java.io.BufferedReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Day 4: Camp Cleanup
 * https://adventofcode.com/2022/day/4
 */
public class Problem04 extends AOCProblem<Long> {

    public static void main(String[] args) throws Exception {
        new Problem04().solve();
    }

    List<int[][]> pairs;

    @Override
    public void processInput(BufferedReader reader) throws Exception {
        Pattern pattern = Pattern.compile("([0-9]+)-([0-9]+),([0-9]+)-([0-9]+)");
        pairs = reader
            .lines()
            .map(pattern::matcher)
            .filter(Matcher::find)
            .map(m -> new int[][]{{
                Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2))
            }, {
                Integer.parseInt(m.group(3)), Integer.parseInt(m.group(4))
            }})
            .collect(Collectors.toList());
    }

    /**
     * In how many assignment pairs does one range fully contain the other?
     */
    @Override
    protected Long partOne() {
        return pairs
            .stream()
            .filter(p -> (p[0][0] >= p[1][0] && p[0][1] <= p[1][1])
                || (p[1][0] >= p[0][0] && p[1][1] <= p[0][1]))
            .count();
    }

    /**
     * In how many assignment pairs do the ranges overlap?
     */
    @Override
    protected Long partTwo() {
        return pairs
            .stream()
            .filter(p -> (p[0][0] >= p[1][0] && p[0][0] <= p[1][1])
                || (p[0][1] >= p[1][0] && p[0][1] <= p[1][1])
                || (p[1][0] >= p[0][0] && p[1][0] <= p[0][1])
                || (p[1][1] >= p[0][0] && p[1][1] <= p[0][1]))
            .count();
    }

}
