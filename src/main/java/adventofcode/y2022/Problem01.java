package adventofcode.y2022;

import adventofcode.commons.AOCProblem;

import java.io.BufferedReader;
import java.util.ArrayList;

/**
 * Day 1: Calorie Counting
 * https://adventofcode.com/2022/day/1
 */
public class Problem01 extends AOCProblem<Integer> {

    public static void main(String[] args) throws Exception {
        new Problem01().solve();
    }

    ArrayList<Integer> list;

    @Override
    public void processInput(BufferedReader reader) throws Exception {

        list = new ArrayList<>();

        int calories = 0;
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.isEmpty()) {
                list.add(calories);
                calories = 0;
            } else {
                calories += Integer.parseInt(line);
            }
        }
        list.add(calories);
    }

    /**
     * Find the Elf carrying the most Calories.
     * How many total Calories is that Elf carrying?
     */
    @Override
    protected Integer partOne() {
        int maxValue = list.get(0);
        int maxIndex = 0;
        for (int i = 1; i < list.size(); ++i) {
            if (list.get(i) > maxValue) {
                maxValue = list.get(i);
                maxIndex = i;
            }
        }
        return maxValue;
    }

    /**
     * Find the top three Elves carrying the most Calories.
     * How many Calories are those Elves carrying in total?
     */
    @Override
    protected Integer partTwo() {
        return list
            .stream()
            .sorted((a, b) -> Integer.compare(b, a))
            .limit(3)
            .reduce(0, (a, b) -> a + b);
    }

}
