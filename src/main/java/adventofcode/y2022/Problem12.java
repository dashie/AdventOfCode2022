package adventofcode.y2022;

import adventofcode.commons.AOCPoint;
import adventofcode.commons.AOCProblem;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Day 12: Hill Climbing Algorithm
 * https://adventofcode.com/2022/day/12
 */
public class Problem12 extends AOCProblem<Integer> {

    public static void main(String[] args) throws Exception {
        new Problem12().solve(false);
    }

    int[][] board;
    int[][] stepsBoard;
    int N, M;
    AOCPoint start;
    AOCPoint end;

    @Override
    public void processInput(BufferedReader reader) throws Exception {

        String[] lines = reader.lines().toArray(String[]::new);
        N = lines[0].length();
        M = lines.length;
        board = new int[M][N];
        stepsBoard = new int[M][N];
        for (int m = 0; m < M; ++m) {
            for (int n = 0; n < N; ++n) {
                char c = lines[m].charAt(n);
                if ('S' == c) {
                    board[m][n] = 0;
                    start = new AOCPoint(n, m);
                } else if ('E' == c) {
                    board[m][n] = 'z' - 'a';
                    end = new AOCPoint(n, m);
                } else {
                    board[m][n] = c - 'a';
                }
                stepsBoard[m][n] = -1;
            }
        }
    }

    /**
     * What is the fewest steps required to move from your current position
     * to the location that should get the best signal?
     */
    @Override
    protected Integer partOne() throws Exception {

        LinkedList<AOCPoint> points = new LinkedList<>();
        points.add(end);
        stepsBoard[end.y][end.x] = 0;

        while (!points.isEmpty()) {
            AOCPoint p0 = points.removeFirst();
            int steps = stepsBoard[p0.y][p0.x];
            List<AOCPoint> nexts = checkPoints(p0, steps + 1);
            points.addAll(nexts);
        }
        // dumpStepsBoard();

        return stepsBoard[start.y][start.x];
    }

    /**
     * What is the fewest steps required to move starting from any square
     * with elevation a to the location that should get the best signal?
     */
    @Override
    protected Integer partTwo() throws Exception {

        // fill steps board
        partOne();

        // search nearest 'a'
        int nearest = Integer.MAX_VALUE;
        for (int m = 0; m < M; ++m) {
            for (int n = 0; n < N; ++n) {
                if (board[m][n] == 0 && stepsBoard[m][n] < nearest && stepsBoard[m][n] != -1) {
                    nearest = stepsBoard[m][n];
                }
            }
        }

        return nearest;
    }

    private List<AOCPoint> checkPoints(AOCPoint p0, int step) {
        List<AOCPoint> points = new ArrayList<>(4);
        points.add(checkPoint(p0, p0.traslateNew(0, -1), step));
        points.add(checkPoint(p0, p0.traslateNew(0, 1), step));
        points.add(checkPoint(p0, p0.traslateNew(-1, 0), step));
        points.add(checkPoint(p0, p0.traslateNew(1, 0), step));
        points.removeIf(p -> p == null);
        return points;
    }

    private AOCPoint checkPoint(AOCPoint p0, AOCPoint p1, int step) {
        // check out of board
        if (p1.x < 0 || p1.x >= N || p1.y < 0 || p1.y >= M)
            return null;

        // check already visited
        if (stepsBoard[p1.y][p1.x] != -1)
            return null;

        // check height
        if (p0 != null) {
            int dh = board[p0.y][p0.x] - board[p1.y][p1.x];
            if (dh > 1)
                return null;
        }

        stepsBoard[p1.y][p1.x] = step;
        //
        return p1;
    }

    private void dumpStepsBoard() {
        System.out.println();
        for (int m = 0; m < M; ++m) {
            for (int n = 0; n < N; ++n) {
                String suffix = " ";
                if (start.equals(n, m)) {
                    suffix = "@";
                } else if (end.equals(n, m)) {
                    suffix = "*";
                }
                System.out.printf("%4d%s", stepsBoard[m][n], suffix);
            }
            System.out.println();
        }
        System.out.println();
    }

}
