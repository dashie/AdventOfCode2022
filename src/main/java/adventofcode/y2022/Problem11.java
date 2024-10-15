package adventofcode.y2022;

import adventofcode.commons.AOCProblem;
import adventofcode.commons.PatternEx;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

/**
 * Day 11: Monkey in the Middle
 * https://adventofcode.com/2022/day/11
 */
public class Problem11 extends AOCProblem<Long> {

    public static void main(String[] args) throws Exception {
        new Problem11().solve(false);
    }

    class Monkey {

        final int id;
        final List<Long> items;
        final Function<Long, Long> op;
        final long moduleTest;
        final int targetT;
        final int targetF;
        List<Long> workItems;
        long inspected = 0;

        public Monkey(int id, List<Long> items, Function<Long, Long> op, long moduleTest, int targetT, int targetF) {
            this.id = id;
            this.items = Collections.unmodifiableList(items);
            this.op = op;
            this.moduleTest = moduleTest;
            this.targetT = targetT;
            this.targetF = targetF;
        }
    }

    private List<Monkey> monkeys = new ArrayList<>();

    @Override
    public void processInput(BufferedReader reader) throws Exception {

        // Monkey 0:
        //   Starting items: 98, 89, 52
        //   Operation: new = old * 2
        //   Test: divisible by 5
        //     If true: throw to monkey 6
        //     If false: throw to monkey 1

        PatternEx nP = PatternEx.compile("\\d+");
        PatternEx itemsP = PatternEx.compile("[0-9][0-9, ]+");
        PatternEx opP = PatternEx.compile("([+*]) (\\d+|old)");

        String line;
        while ((line = reader.readLine()) != null) {

            int id = nP.toInt(line);
            List<Long> items = itemsP.matchAndSplitToLongList(reader.readLine(), ",");
            Function<Long, Long> op = parseOp(opP.findGroups(reader.readLine()));
            int moduleTest = nP.toInt(reader.readLine());
            int targetT = nP.toInt(reader.readLine());
            int targetF = nP.toInt(reader.readLine());
            monkeys.add(new Monkey(id, items, op, moduleTest, targetT, targetF));

            reader.readLine(); // skip line
        }
    }

    private Function<Long, Long> parseOp(String[] strs) {
        String op = strs[1];
        String strN = strs[2];
        if ("old".equals(strN)) {
            return switch (op) {
                case "+" -> (a) -> a + a;
                case "*" -> (a) -> a * a;
                default -> throw new IllegalStateException("Invalid op \"%s\"".formatted(op));
            };
        } else {
            long n = Long.parseLong(strs[2]);
            return switch (op) {
                case "+" -> (a) -> a + n;
                case "*" -> (a) -> a * n;
                default -> throw new IllegalStateException("Invalid op \"%s\"".formatted(op));
            };
        }
    }

    /**
     * Figure out which monkeys to chase by counting how many items they inspect over 20 rounds.
     * What is the level of monkey business after 20 rounds of stuff-slinging simian shenanigans?
     */
    @Override
    protected Long partOne() throws Exception {

        // reset
        monkeys.forEach(m -> {
            m.workItems = new LinkedList<>(m.items);
            m.inspected = 0;
        });

        long result = 0;

        for (int i = 0; i < 20; ++i) {
            monkeys.forEach(m -> {
                m.workItems.forEach(item -> {
                    m.inspected++;
                    long level = m.op.apply(item) / 3;
                    int nextMonkey = (level % m.moduleTest == 0) ? m.targetT : m.targetF;
                    Monkey next = monkeys.get(nextMonkey);
                    next.workItems.add(level);
                });
                m.workItems.clear();
            });
        }

        return monkeys
            .stream()
            .sorted((a, b) -> Long.compare(b.inspected, a.inspected))
            .limit(2)
            .map(m -> m.inspected)
            .reduce((a, b) -> a * b)
            .get();
    }

    /**
     * Worry levels are no longer divided by three after each item is inspected;
     * you'll need to find another way to keep your worry levels manageable.
     * Starting again from the initial state in your puzzle input,
     * what is the level of monkey business after 10000 rounds?
     */
    @Override
    protected Long partTwo() throws Exception {

        // reset
        monkeys.forEach(m -> {
            m.workItems = new LinkedList<>(m.items);
            m.inspected = 0;
        });


        long lcm = monkeys
            .stream()
            .map(m -> m.moduleTest)
            .reduce(1L, (a, b) -> a * b);

        long result = 0;

        for (int i = 0; i < 10000; ++i) {
            monkeys.forEach(m -> {
                m.workItems.forEach(item -> {
                    m.inspected++;
                    long level = m.op.apply(item) % lcm;
                    int nextMonkey = (level % m.moduleTest == 0) ? m.targetT : m.targetF;
                    Monkey next = monkeys.get(nextMonkey);
                    next.workItems.add(level);
                });
                m.workItems.clear();
            });
        }

        return monkeys
            .stream()
            .sorted((a, b) -> Long.compare(b.inspected, a.inspected))
            .limit(2)
            .map(m -> m.inspected)
            .reduce((a, b) -> a * b)
            .get();
    }

}
