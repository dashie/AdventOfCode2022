package adventofcode.y2022;

import adventofcode.commons.AOCProblem;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Day 2: Rock Paper Scissors
 * https://adventofcode.com/2022/day/2
 */
public class Problem02 extends AOCProblem<Integer> {

    public static void main(String[] args) throws Exception {
        new Problem02().solve();
    }

    List<String> lines;

    @Override
    public void processInput(BufferedReader reader) throws Exception {
        lines = reader
            .lines()
            .collect(Collectors.toList());
    }

    /**
     * What would your total score be if everything goes exactly
     * according to your strategy guide?
     */
    @Override
    protected Integer partOne() {

        Map<String, Integer> map = new HashMap<>();
        map.put("A X", 4); // rock rock            1+3
        map.put("A Y", 8); // rock paper           2+6
        map.put("A Z", 3); // rock scissors        3+0
        map.put("B X", 1); // paper rock           1+0
        map.put("B Y", 5); // paper paper          2+3
        map.put("B Z", 9); // paper scissors       3+6
        map.put("C X", 7); // scissors rock        1+6
        map.put("C Y", 2); // scissors paper       2+0
        map.put("C Z", 6); // scissors scissors    3+3

        return lines
            .stream()
            .map(s -> map.get(s))
            .reduce(0, (a, b) -> a + b);
    }

    /**
     * Following the Elf's instructions for the second column,
     * what would your total score be if everything goes exactly
     * according to your strategy guide?
     */
    @Override
    protected Integer partTwo() {

        Map<String, Integer> map = new HashMap<>();
        map.put("A X", 3); // rock loose scissors     3+0
        map.put("A Y", 4); // rock draw rock          1+3
        map.put("A Z", 8); // rock win paper          2+6
        map.put("B X", 1); // paper loose rock        1+0
        map.put("B Y", 5); // paper draw paper        2+3
        map.put("B Z", 9); // paper win scissors      3+6
        map.put("C X", 2); // scissors loose paper    2+0
        map.put("C Y", 6); // scissors draw scissors  3+3
        map.put("C Z", 7); // scissors win rock       1+6

        return lines
            .stream()
            .map(s -> map.get(s))
            .reduce(0, (a, b) -> a + b);
    }

}
