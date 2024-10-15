package adventofcode.y2022;

import adventofcode.commons.AOCProblem;

import java.io.BufferedReader;

/**
 * Day 8: Treetop Tree House
 * https://adventofcode.com/2022/day/8
 */
public class Problem08 extends AOCProblem<Long> {

    public static void main(String[] args) throws Exception {
        new Problem08().solve(false);
    }

    private int[][] trees;
    private long[][] scores;
    private int N;
    private int M;

    @Override
    public void processInput(BufferedReader reader) throws Exception {

        trees = reader
            .lines()
            .map(l -> l.chars()
                       .map(c -> c - '0')
                       .toArray())
            .toArray(int[][]::new);

        N = trees[0].length;
        M = trees.length;
        scores = new long[M][N];
    }

    /**
     * Consider your map; how many trees are visible from outside the grid?
     */
    @Override
    protected Long partOne() throws Exception {

        boolean[][] visibility = new boolean[M][N];

        long count = 0;
        long max;

        // from N
        max = 0;
        for (int n = 0; n < N; ++n) {
            for (int m = 0; m < M; ++m) {
                if (m == 0 || trees[m][n] > max) {
                    max = trees[m][n];
                    if (!visibility[m][n]) {
                        visibility[m][n] = true;
                        count++;
                    }
                }
            }
        }

        // from S
        max = 0;
        for (int n = 0; n < N; ++n) {
            for (int m = M - 1; m >= 0; --m) {
                if (m == M - 1 || trees[m][n] > max) {
                    max = trees[m][n];
                    if (!visibility[m][n]) {
                        visibility[m][n] = true;
                        count++;
                    }
                }
            }
        }

        // from E
        max = 0;
        for (int m = 0; m < M; ++m) {
            for (int n = 0; n < N; ++n) {
                if (n == 0 || trees[m][n] > max) {
                    max = trees[m][n];
                    if (!visibility[m][n]) {
                        visibility[m][n] = true;
                        count++;
                    }
                }
            }
        }

        // from W
        max = 0;
        for (int m = 0; m < M; ++m) {
            for (int n = N - 1; n >= 0; --n) {
                if (n == N - 1 || trees[m][n] > max) {
                    max = trees[m][n];
                    if (!visibility[m][n]) {
                        visibility[m][n] = true;
                        count++;
                    }
                }
            }
        }

        return count;
    }

    /**
     * Consider each tree on your map.
     * What is the highest scenic score possible for any tree?
     */
    @Override
    protected Long partTwo() throws Exception {

        long maxScore = 0;
        for (int m = 0; m < M; ++m) {
            for (int n = 0; n < N; ++n) {
                long score = evalScore(m, n);
                if (score > maxScore) {
                    maxScore = score;
                }
            }
        }

        return maxScore;
    }

    private long evalScore(int m, int n) {

        long score = 1;
        long h0 = trees[m][n];
        long count;

        // N
        count = 0;
        for (int i = m - 1; i >= 0; i--) {
            count++;
            if (trees[i][n] >= h0) break;
        }
        score *= count;

        // S
        count = 0;
        for (int i = m + 1; i < M; i++) {
            count++;
            if (trees[i][n] >= h0) break;
        }
        score *= count;

        // W
        count = 0;
        for (int i = n - 1; i >= 0; i--) {
            count++;
            if (trees[m][i] >= h0) break;
        }
        score *= count;

        // E
        count = 0;
        for (int i = n + 1; i < N; i++) {
            count++;
            if (trees[m][i] >= h0) break;
        }
        score *= count;

        scores[m][n] = score;
        return score;
    }
}
