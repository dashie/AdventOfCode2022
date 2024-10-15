package adventofcode.y2022;

import adventofcode.commons.AOCProblem;

import java.io.BufferedReader;
import java.util.stream.IntStream;

/**
 * Day 20: Grove Positioning System
 * https://adventofcode.com/2022/day/20
 */
public class Problem20 extends AOCProblem<Long> {

    public static void main(String[] args) throws Exception {
        new Problem20().solve(false);
    }

    long[] numbers;

    @Override
    public void processInput(BufferedReader reader) throws Exception {

        numbers = reader
            .lines()
            .mapToLong(Long::parseLong)
            .toArray();
    }

    /**
     * Mix your encrypted file exactly once.
     * What is the sum of the three numbers that form the grove coordinates?
     */
    @Override
    protected Long partOne() throws Exception {

        long[][] copy = decrypt(1, 1);
        long result = evalCoordinates(copy);
        return result;
    }

    /**
     * Apply the decryption key and mix your encrypted file ten times.
     * What is the sum of the three numbers that form the grove coordinates?
     */
    @Override
    protected Long partTwo() throws Exception {

        long[][] copy = decrypt(811589153, 10);
        long result = evalCoordinates(copy);
        return result;
    }

    private static long evalCoordinates(long[][] copy) {
        int zeroOffset = 0;
        for (; zeroOffset < copy.length; ++zeroOffset) {
            if (copy[zeroOffset][0] == 0) break;
        }
        long result =
            copy[(zeroOffset + 1000) % copy.length][0]
                + copy[(zeroOffset + 2000) % copy.length][0]
                + copy[(zeroOffset + 3000) % copy.length][0];
        return result;
    }

    private long[][] decrypt(long key, int n) {

        int[] indexes = IntStream.range(0, numbers.length)
                                 .toArray();

        long[][] copy = new long[numbers.length][2];
        for (int i = 0; i < numbers.length; ++i) {
            copy[i][0] = numbers[i] * key;
            copy[i][1] = i; // add original index
        }

        for (int i = 0; i < n; ++i) {
            mix(copy, indexes);
        }

        return copy;
    }

    private void mix(long[][] copy, int[] indexes) {

        for (int ki = 0; ki < indexes.length; ++ki) {
            int k = indexes[ki];
            long[] pair = copy[k];
            long n = pair[0];

            int len1 = copy.length - 1;
            int k2 = (int) ((k + n) % len1);
            if (k2 < 0) {
                k2 = k2 + len1;
            }

            if (k2 != k) {
                if (k2 > k) {
                    for (int i = k; i < k2; ++i) {
                        copy[i] = copy[i + 1];
                        indexes[(int) copy[i + 1][1]] = i;
                    }
                } else {
                    for (int i = k; i > k2; --i) {
                        copy[i] = copy[i - 1];
                        indexes[(int) copy[i - 1][1]] = i;
                    }
                }
                copy[k2] = pair;
                indexes[(int) pair[1]] = k2;
            }
            // System.out.printf("%5d: %5d   %s - %s%n", ki, n, Arrays.deepToString(copy), Arrays.toString(indexes));
        }
    }

}
