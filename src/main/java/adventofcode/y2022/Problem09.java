package adventofcode.y2022;

import adventofcode.commons.AOCPoint;
import adventofcode.commons.AOCProblem;
import adventofcode.commons.AOCVector;

import java.io.BufferedReader;
import java.util.HashSet;
import java.util.Set;

/**
 * Day 9: Rope Bridge
 * https://adventofcode.com/2022/day/9*
 */
public class Problem09 extends AOCProblem<Integer> {

    public static void main(String[] args) throws Exception {
        new Problem09().solve(false);
    }

    private int N = 1000;
    private int M = 1000;
    private char[][] board = new char[M][N];

    private int hx = N / 2;
    private int hy = M / 2;
    private int tx = hx;
    private int ty = hy;

    private int[][] moves;

    @Override
    public void processInput(BufferedReader reader) throws Exception {
        moves = reader
            .lines()
            .map(l -> l.split(" "))
            .map(p -> new int[]{p[0].charAt(0), Integer.parseInt(p[1])}).toArray(int[][]::new);
    }

    /**
     * Simulate your complete hypothetical series of motions.
     * How many positions does the tail of the rope visit at least once?
     */
    @Override
    protected Integer partOne() throws Exception {

        int count = 1;
        board[hy][hx] = '#';

        for (int[] move : moves) {
            for (int i = 0; i < move[1]; ++i) {
                count += moveSingleKnot(move[0]);
            }
        }

        return count;
    }

    /**
     * Simulate your complete series of motions on a larger rope with ten knots.
     * How many positions does the tail of the rope visit at least once?
     */
    @Override
    protected Integer partTwo() throws Exception {

        // init rope
        AOCPoint[] rope = AOCPoint.newArray(10, 0, 0, 0);

        Set<String> cells = new HashSet<>();
        cells.add(rope[9].toString());

        for (int[] move : moves) {
            for (int i = 0; i < move[1]; ++i) {
                moveRopeWithNKnots(rope, move[0]);
                cells.add(rope[9].toString());
            }
        }

        return cells.size();
    }

    private int moveSingleKnot(int direction) {
        switch (direction) {
            case 'U':
                if (ty > hy) {
                    tx = hx;
                    ty--;
                }
                hy--;
                break;
            case 'D':
                if (ty < hy) {
                    tx = hx;
                    ty++;
                }
                hy++;
                break;
            case 'R':
                if (tx < hx) {
                    ty = hy;
                    tx++;
                }
                hx++;
                break;
            case 'L':
                if (tx > hx) {
                    ty = hy;
                    tx--;
                }
                hx--;
                break;
        }
        if (board[ty][tx] != '#') {
            board[ty][tx] = '#';
            return 1;
        }
        return 0;
    }


    private void moveRopeWithNKnots(AOCPoint[] rope, int direction) {
        switch (direction) {
            case 'U' -> rope[0].y++;
            case 'D' -> rope[0].y--;
            case 'R' -> rope[0].x++;
            case 'L' -> rope[0].x--;
        }

        for (int i = 1; i < rope.length; ++i) {
            AOCVector dv = rope[i - 1].distanceVector(rope[i]);
            if (Math.abs(dv.x) > 1 || Math.abs(dv.y) > 1) {
                AOCVector signs = dv.signs();
                rope[i].traslate(signs.x, signs.y);
            }
        }
    }
}
