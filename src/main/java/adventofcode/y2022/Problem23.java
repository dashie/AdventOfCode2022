package adventofcode.y2022;

import adventofcode.commons.AOCPoint;
import adventofcode.commons.AOCProblem;

import java.io.BufferedReader;
import java.util.*;

/**
 * Day 23: Unstable Diffusion
 * https://adventofcode.com/2022/day/23
 */
public class Problem23 extends AOCProblem<Integer> {

    public static void main(String[] args) throws Exception {
        new Problem23().solve(false);
    }

    class Elf {

        int id;
        AOCPoint p;
        AOCPoint nextP;
        List<String> moves;
        boolean freezed;

        public Elf(int id, AOCPoint p) {
            this.id = id;
            this.p = p;
            this.moves = new LinkedList<>(List.of("N", "S", "W", "E"));
        }
    }

    List<Elf> elves = new ArrayList<>();
    Map<String, Elf> elvesMap = new HashMap<>();
    Map<String, Elf> intentionMoves = new HashMap<>();

    @Override
    public void processInput(BufferedReader reader) throws Exception {

        int elfCount = 1;

        int y = 0;
        String line;
        while ((line = reader.readLine()) != null) {
            char[] chars = line.toCharArray();
            for (int x = 0; x < chars.length; ++x) {
                if (chars[x] == '#') {
                    AOCPoint p = new AOCPoint(x, y);
                    Elf elf = new Elf(elfCount++, p);
                    elves.add(elf);
                    moveElf(elf, p);
                }
            }
            y++;
        }
    }

    /**
     * Simulate the Elves' process and find the smallest rectangle
     * that contains the Elves after 10 rounds.
     * How many empty ground tiles does that rectangle contain?
     */
    @Override
    protected Integer partOne() throws Exception {

        int n = 10;
        while (n-- > 0) {
            intentionMoves.clear();
            // first part of round
            for (Elf elf : elves) {
                checkElfMove(elf);
            }
            // second part of round
            for (Elf elf : elves) {
                if (elf.nextP != null && !elf.freezed) {
                    moveElf(elf, elf.nextP);
                }
                String pos = elf.moves.removeFirst();
                elf.moves.addLast(pos);
            }
        }
        // dumpBoard();

        int result = countFreeCells();
        return result;
    }

    /**
     * Figure out where the Elves need to go.
     * What is the number of the first round where no Elf moves?
     */
    @Override
    protected Integer partTwo() throws Exception {

        // keep in mind that in the part1 we already simulated 10 rounds
        // that we need to add to the count at the end

        int n = 0;
        int moves = 0;
        do {
            intentionMoves.clear();
            // first part of round
            for (Elf elf : elves) {
                checkElfMove(elf);
            }
            // second part of round
            moves = 0;
            for (Elf elf : elves) {
                if (elf.nextP != null && !elf.freezed) {
                    moveElf(elf, elf.nextP);
                    moves++;
                }
                String pos = elf.moves.removeFirst();
                elf.moves.addLast(pos);
            }
            n++;
            // System.out.printf("%4d: %4d moves%n", n, moves);
        } while (moves != 0);
        // dumpBoard();

        return n + 10; // +10 moves of the first part
    }

    private boolean checkElfMove(Elf elf) {
        elf.nextP = null;
        elf.freezed = false;
        AOCPoint p = elf.p;

        if (checkFree(p)) {
            elf.freezed = true;
            return false;
        }

        checkMove:
        for (String m : elf.moves) {
            AOCPoint[] nextPts = new AOCPoint[3];
            switch (m) {
                case "N" -> {
                    nextPts[0] = new AOCPoint(p.x - 1, p.y - 1);
                    nextPts[1] = new AOCPoint(p.x, p.y - 1);
                    nextPts[2] = new AOCPoint(p.x + 1, p.y - 1);
                }
                case "S" -> {
                    nextPts[0] = new AOCPoint(p.x - 1, p.y + 1);
                    nextPts[1] = new AOCPoint(p.x, p.y + 1);
                    nextPts[2] = new AOCPoint(p.x + 1, p.y + 1);
                }
                case "W" -> {
                    nextPts[0] = new AOCPoint(p.x - 1, p.y - 1);
                    nextPts[1] = new AOCPoint(p.x - 1, p.y);
                    nextPts[2] = new AOCPoint(p.x - 1, p.y + 1);
                }
                case "E" -> {
                    nextPts[0] = new AOCPoint(p.x + 1, p.y - 1);
                    nextPts[1] = new AOCPoint(p.x + 1, p.y);
                    nextPts[2] = new AOCPoint(p.x + 1, p.y + 1);
                }
            }

            for (AOCPoint nextP : nextPts) {
                if (elvesMap.containsKey(nextP.toString())) {
                    continue checkMove;
                }
            }

            elf.nextP = nextPts[1];
            Elf conflict = intentionMoves.put(elf.nextP.toString(), elf);
            if (conflict != null) {
                elf.freezed = true;
                conflict.freezed = true;
            }
            return true;
        }
        return false;
    }

    private boolean checkFree(AOCPoint p) {
        AOCPoint[] nextPts = new AOCPoint[8];
        nextPts[0] = new AOCPoint(p.x - 1, p.y - 1);
        nextPts[1] = new AOCPoint(p.x, p.y - 1);
        nextPts[2] = new AOCPoint(p.x + 1, p.y - 1);
        nextPts[3] = new AOCPoint(p.x - 1, p.y);
        nextPts[4] = new AOCPoint(p.x + 1, p.y);
        nextPts[5] = new AOCPoint(p.x - 1, p.y + 1);
        nextPts[6] = new AOCPoint(p.x, p.y + 1);
        nextPts[7] = new AOCPoint(p.x + 1, p.y + 1);
        for (AOCPoint n : nextPts) {
            if (elvesMap.containsKey(n.toString()))
                return false;
        }
        return true;
    }

    private void moveElf(Elf elf, AOCPoint p) {
        elvesMap.remove(elf.p.toString());
        elf.p = p;
        elvesMap.put(elf.p.toString(), elf);
    }

    private int countFreeCells() {
        int xMin = Integer.MAX_VALUE, yMin = Integer.MAX_VALUE, xMax = Integer.MIN_VALUE, yMax = Integer.MIN_VALUE;
        for (Elf elf : elves) {
            AOCPoint p = elf.p;
            if (p.x < xMin) xMin = p.x;
            if (p.x > xMax) xMax = p.x;
            if (p.y < yMin) yMin = p.y;
            if (p.y > yMax) yMax = p.y;
        }
        return (xMax - xMin + 1) * (yMax - yMin + 1) - elves.size();
    }

    private void dumpBoard() {
        int xMin = Integer.MAX_VALUE, yMin = Integer.MAX_VALUE, xMax = Integer.MIN_VALUE, yMax = Integer.MIN_VALUE;
        for (Elf elf : elves) {
            AOCPoint p = elf.p;
            if (p.x < xMin) xMin = p.x;
            if (p.x > xMax) xMax = p.x;
            if (p.y < yMin) yMin = p.y;
            if (p.y > yMax) yMax = p.y;
        }

        System.out.println("------------");

        System.out.print("      ");
        for (int x = xMin; x <= xMax; ++x) {
            System.out.print(Math.abs(x % 10));
        }
        System.out.println("");

        for (int y = yMin; y <= yMax; ++y) {
            System.out.printf("%4d: ", y);
            for (int x = xMin; x <= xMax; ++x) {
                AOCPoint p = new AOCPoint(x, y);
                if (elvesMap.get(p.toString()) != null) {
                    System.out.printf("#");
                } else {
                    System.out.printf(".");
                }
            }
            System.out.println();
        }

        System.out.println("------------");
        System.out.println("");
    }
}
