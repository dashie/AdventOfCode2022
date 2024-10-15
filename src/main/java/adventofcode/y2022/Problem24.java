package adventofcode.y2022;

import adventofcode.commons.AOCBoard;
import adventofcode.commons.AOCPoint;
import adventofcode.commons.AOCProblem;
import adventofcode.commons.AOCVector;

import java.io.BufferedReader;
import java.util.*;

/**
 * Day 24: Blizzard Basin
 * https://adventofcode.com/2022/day/24
 */
public class Problem24 extends AOCProblem<Integer> {

    public static void main(String[] args) throws Exception {
        new Problem24().solve(false);
    }

    class Scenario {

        AOCPoint p;

        public Scenario(AOCPoint p) {
            this.p = p;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Scenario scenario = (Scenario) o;
            return Objects.equals(p, scenario.p);
        }

        @Override
        public int hashCode() {
            return Objects.hash(p);
        }

        @Override
        public String toString() {
            return "Scenario{" +
                "p=" + p +
                '}';
        }
    }

    AOCBoard<Character> board;
    AOCPoint START, END;

    @Override
    public void processInput(BufferedReader reader) throws Exception {

        Character[][] data = reader
            .lines()
            .map(l -> l.chars().mapToObj(c -> (char) c).toArray(Character[]::new))
            .toArray(Character[][]::new);

        board = new AOCBoard<>(data);
        START = new AOCPoint(1, 0);
        END = new AOCPoint(board.N - 2, board.M - 1);
    }

    /**
     * What is the fewest number of minutes required to avoid
     * the blizzards and reach the goal?
     */
    @Override
    protected Integer partOne() throws Exception {
        int round = 1;
        return goToFrom(START, END, round);
    }

    /**
     * What is the fewest number of minutes required to reach the goal,
     * go back to the start, then reach the goal again?
     */
    @Override
    protected Integer partTwo() throws Exception {
        int round = 1;
        round = goToFrom(START, END, round);
        round = goToFrom(END, START, round + 1);
        return goToFrom(START, END, round + 1);
    }

    private int goToFrom(AOCPoint start, AOCPoint end, int round) {
        // board.dumpBoard("%c", (c) -> start.equals(c.n, c.m) ? '@' : c.v);

        Set<Scenario> nextScenarios = new HashSet<>();
        nextScenarios.add(new Scenario(start));

        int result = -1;
        int bestDistance = Integer.MAX_VALUE;
        found:
        do {
            Set<Scenario> scenarios = nextScenarios;
            nextScenarios = new HashSet<>();
            for (Scenario s : scenarios) {
                List<AOCPoint> moves = nextMoves(s.p, round);
                if (!moves.isEmpty()) {
                    for (AOCPoint move : moves) {

                        int dist = move.distanceVector(end).manhattam();
                        if (dist < bestDistance) {
                            bestDistance = dist;
                        }
                        // System.out.printf("%5d: %s%n", round, move);
                        if (move.equals(end)) {
                            result = round;
                            break found;
                        }

                        nextScenarios.add(new Scenario(move));
                    }
                }
                if (!meetsBlizzard(s.p, round)) // I can also wait
                    nextScenarios.add(s);
            }
            ++round;

            // if (round % 100 == 0) {
            //     System.out.printf("%5d: %d %d%n", round, scenarios.size(), bestDistance);
            // }

            // dumpBoard(nextScenarios);

        } while (nextScenarios.size() > 0 && result == -1);

        if (result == -1) {
            throw new IllegalStateException();
        }
        return result;
    }

    private void dumpBoard(Queue<Scenario> nextScenarios) {
        board.dumpBoard("%c", (c) -> nextScenarios
            .stream().map(s -> s.p).anyMatch(sp -> sp.equals(c.n, c.m))
            ? '@' : c.v);
    }

    List<AOCPoint> nextMoves(AOCPoint p, int round) {

        List<AOCPoint> nextPts;
        // sort by priority, which is distance from END
        AOCVector d = p.distanceVector(p);
        if (d.x < d.y) {
            nextPts = new ArrayList<>(List.of(
                new AOCPoint(p.x, p.y + 1), // D
                new AOCPoint(p.x + 1, p.y), // L
                new AOCPoint(p.x - 1, p.y), // R
                new AOCPoint(p.x, p.y - 1)  // U
            ));
        } else {
            nextPts = new ArrayList<>(List.of(
                new AOCPoint(p.x + 1, p.y), // L
                new AOCPoint(p.x, p.y + 1), // D
                new AOCPoint(p.x - 1, p.y), // R
                new AOCPoint(p.x, p.y - 1)  // U
            ));
        }

        // remove invalid moves (hits wall or meets blizzard)
        nextPts.removeIf(nextPt -> nextPt.y < 0 || nextPt.y >= board.M
            || board.get(nextPt) == '#'
            || (meetsBlizzard(nextPt, round)));

        return nextPts;
    }

    boolean meetsBlizzard(AOCPoint p, int round) {
        if (p.y >= board.M - 1 || p.y <= 0)
            return false;

        AOCPoint bxL = p.traslateNew(-1, -1) // blizzard from left
                        .traslate(-round, 0)
                        .module(board.N - 2, board.M - 2)
                        .traslate(1, 1);
        if (board.get(bxL) == '>')
            return true;

        AOCPoint bxR = p.traslateNew(-1, -1) // blizzard from right
                        .traslate(round, 0)
                        .module(board.N - 2, board.M - 2)
                        .traslate(1, 1);
        if (board.get(bxR) == '<')
            return true;

        AOCPoint byU = p.traslateNew(-1, -1) // blizzard from left
                        .traslate(0, -round)
                        .module(board.N - 2, board.M - 2)
                        .traslate(1, 1);
        if (board.get(byU) == 'v')
            return true;

        AOCPoint byD = p.traslateNew(-1, -1) // blizzard from left
                        .traslate(0, round)
                        .module(board.N - 2, board.M - 2)
                        .traslate(1, 1);
        if (board.get(byD) == '^')
            return true;

        return false;
    }


    // 012345
    // >.....
    // .>....
    // ..>...
    // ...>..
    // ....>.
    // .....>

}
