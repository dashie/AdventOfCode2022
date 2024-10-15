package adventofcode.y2022;

import adventofcode.commons.AOCProblem;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Day 5: Supply Stacks
 * https://adventofcode.com/2022/day/5
 */
public class Problem05 extends AOCProblem<String> {

    public static void main(String[] args) throws Exception {
        new Problem05().solve();
    }

    Stack<String>[] initialStacks;
    List<int[]> moves;

    @Override
    public void processInput(BufferedReader reader) throws Exception {

        // "                                   "
        //   1   5   9   ...
        //  [B] [W] [N] [P] [D] [V] [G] [L] [T]
        initialStacks = new Stack[9];
        for (int i = 0; i < initialStacks.length; ++i)
            initialStacks[i] = new Stack<>();

        String line;

        // load stacks
        while ((line = reader.readLine()).startsWith("[")) {
            line = line + "                                  ";
            load(initialStacks, 0, line.substring(1, 2));
            load(initialStacks, 1, line.substring(5, 6));
            load(initialStacks, 2, line.substring(9, 10));
            load(initialStacks, 3, line.substring(13, 14));
            load(initialStacks, 4, line.substring(17, 18));
            load(initialStacks, 5, line.substring(21, 22));
            load(initialStacks, 6, line.substring(25, 26));
            load(initialStacks, 7, line.substring(29, 30));
            load(initialStacks, 8, line.substring(33, 34));
        }

        reader.readLine(); // skip blank line

        // load moves
        moves = new ArrayList<>();
        Pattern pattern = Pattern.compile("move ([0-9]+) from ([0-9]+) to ([0-9]+)");
        while ((line = reader.readLine()) != null) {
            Matcher m = pattern.matcher(line);
            if (m.find()) {
                int[] move = {
                    Integer.parseInt(m.group(1)),
                    Integer.parseInt(m.group(2)) - 1,
                    Integer.parseInt(m.group(3)) - 1,
                };
                moves.add(move);
            }
        }
    }

    private void load(Stack<String>[] stacks, int i, String item) {
        if (!item.equals(" ")) {
            stacks[i].add(0, item);
        }
    }

    /**
     * After the rearrangement procedure completes, what crate ends up on top of each stack?
     */
    @Override
    protected String partOne() {

        Stack<String>[] stacks = new Stack[initialStacks.length];
        for (int i = 0; i < initialStacks.length; ++i)
            stacks[i] = (Stack<String>) initialStacks[i].clone();

        for (int[] move : moves) {
            for (int i = 0; i < move[0]; ++i) {
                stacks[move[2]].push(stacks[move[1]].pop());
            }
        }

        return Arrays
            .stream(stacks)
            .map(Stack::pop)
            .collect(Collectors.joining());
    }

    /**
     * Before the rearrangement process finishes, update your simulation so that
     * the Elves know where they should stand to be ready to unload the final supplies.
     * After the rearrangement procedure completes, what crate ends up on top of each stack?
     */
    @Override
    protected String partTwo() {

        Stack<String>[] stacks = new Stack[initialStacks.length];
        for (int i = 0; i < initialStacks.length; ++i)
            stacks[i] = (Stack<String>) initialStacks[i].clone();

        for (int[] move : moves) {
            for (int i = 0; i < move[0]; ++i) {
                Stack<String> stack = stacks[move[2]];
                stack.add(stack.size() - i, stacks[move[1]].pop());
            }
        }

        return Arrays
            .stream(stacks)
            .map(Stack::pop)
            .collect(Collectors.joining());
    }

}
