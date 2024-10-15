package adventofcode.y2022;

import adventofcode.commons.AOCProblem;

import java.io.BufferedReader;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Day 3: Rucksack Reorganization
 * https://adventofcode.com/2022/day/3
 */
public class Problem03 extends AOCProblem<Long> {

    public static void main(String[] args) throws Exception {
        new Problem03().solve();
    }

    List<String> lines;

    @Override
    public void processInput(BufferedReader reader) throws Exception {
        lines = reader
            .lines()
            .collect(Collectors.toList());
    }

    /**
     * Find the item type that appears in both compartments of each rucksack.
     * What is the sum of the priorities of those item types?
     */
    @Override
    protected Long partOne() {

        long score = 0;
        for (String line : lines) {
            String p1 = line.substring(0, line.length() / 2);
            String p2 = line.substring(line.length() / 2, line.length());
            Pattern p = Pattern.compile("[" + p1 + "]");
            MatchResult match = p.matcher(p2)
                                 .results()
                                 .findFirst()
                                 .get();
            // System.out.println(p1 + "|" + p2 + "     " + match.group());
            score += toPriority(match.group().charAt(0));
        }
        return score;
    }

    /**
     * Find the item type that corresponds to the badges of each three-Elf group.
     * What is the sum of the priorities of those item types?
     */
    @Override
    protected Long partTwo() {

        long score = 0;
        for (int i = 0; i < lines.size(); i += 3) {
            boolean[] bs1 = toBitset(lines.get(i));
            boolean[] bs2 = toBitset(lines.get(i + 1));
            boolean[] bs3 = toBitset(lines.get(i + 2));
            for (int j = 0; j < bs1.length; ++j) {
                if (bs1[j] && bs2[j] && bs3[j]) {
                    score += j + 1;
                    break;
                }
            }
        }
        return score;
    }

    private boolean[] toBitset(String line) {
        boolean[] bitset = new boolean[52];
        for (char c : line.toCharArray()) {
            bitset[toPriority(c) - 1] = true;
        }
        return bitset;
    }

    private int toPriority(char c) {
        if (Character.isUpperCase(c)) {
            return c - 'A' + 27;
        } else {
            return c - 'a' + 1;
        }
    }
}
