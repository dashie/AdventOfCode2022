package adventofcode.y2022;

import adventofcode.commons.AOCProblem;

import java.io.BufferedReader;

/**
 * Day 25: Full of Hot Air
 * https://adventofcode.com/2022/day/25
 */
public class Problem25 extends AOCProblem<String> {

    public static void main(String[] args) throws Exception {
        new Problem25().solve(false);
    }

    private String[] data;

    @Override
    public void processInput(BufferedReader reader) throws Exception {
        data = reader
            .lines()
            .toArray(String[]::new);
    }

    /**
     * The Elves are starting to get cold.
     * What SNAFU number do you supply to Bob's console?
     */
    @Override
    protected String partOne() throws Exception {

        long result = 0;

        for (String str : data) {
            long n = decodeSnafu(str);
            result += n;
            String check = encodeSnafu(n);
            String error = "";
            if (!check.equals(str))
                error = " ERROR!!!";
            // System.out.printf("%22s %22d %22s  %s%n", str, n, check, error);
        }

        return encodeSnafu(result);
    }

    private long decodeSnafu(String snafu) {

        long n = 0;
        long pow = (long) Math.pow(5, snafu.length() - 1);
        for (int i = 0; i < snafu.length(); ++i) {
            char c = snafu.charAt(i);
            long d = switch (c) {
                case '0' -> 0;
                case '1' -> 1;
                case '2' -> 2;
                case '-' -> -1;
                case '=' -> -2;
                default -> throw new IllegalStateException();
            };
            n += d * pow;
            pow /= 5;
        }
        return n;
    }

    private String encodeSnafu(long n) {
        StringBuilder sb = new StringBuilder(8);
        long v = n;
        long r = 0;
        while (v != 0) {
            int d = (int) (v % 5);
            if (d < 3) {
                r = 0;
                sb.insert(0, d);
            } else if (d == 3) {
                sb.insert(0, '=');
                r = 1;
            } else if (d == 4) {
                sb.insert(0, '-');
                r = 1;
            }
            v = v / 5 + r;
        }
        return sb.toString();
    }

}
