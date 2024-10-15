package adventofcode.y2022;

import adventofcode.commons.AOCProblem;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * Day 6: Tuning Trouble
 * https://adventofcode.com/2022/day/6
 */
public class Problem06 extends AOCProblem<Integer> {

    public static void main(String[] args) throws Exception {
        new Problem06().solve();
    }

    String data;

    @Override
    public void processInput(BufferedReader reader) throws Exception {
        data = reader.readLine();
    }

    /**
     * How many characters need to be processed before the
     * first start-of-packet marker is detected?
     */
    @Override
    protected Integer partOne() throws Exception {

        StringReader reader = new StringReader(data);
        LinkedHashSet set = new LinkedHashSet();

        LinkedList<Character> buffer = new LinkedList<>();
        int n = 0;
        for (; n < 4 - 1; ++n) {
            buffer.add((char) reader.read());
        }

        int c;
        while ((c = reader.read()) != -1) {
            ++n;
            buffer.add((char) c);
            if (checkAllDifferent(buffer)) {
                return n;
            }
            buffer.removeFirst();
        }

        throw new IllegalStateException("No match found");
    }

    /**
     * How many characters need to be processed before the
     * first start-of-message marker is detected?
     */
    @Override
    protected Integer partTwo() throws Exception {

        StringReader reader = new StringReader(data);
        LinkedHashSet set = new LinkedHashSet();

        LinkedList<Character> buffer = new LinkedList<>();
        int n = 0;
        for (; n < 14 - 1; ++n) {
            buffer.add((char) reader.read());
        }

        int c;
        while ((c = reader.read()) != -1) {
            ++n;
            buffer.add((char) c);
            if (checkAllDifferent(buffer)) {
                return n;
            }
            buffer.removeFirst();
        }

        throw new IllegalStateException("No match found");
    }

    private boolean checkAllDifferent(LinkedList<Character> buffer) {
        Set<Character> set = new HashSet<>(buffer);
        return set.size() == buffer.size();
    }
}
