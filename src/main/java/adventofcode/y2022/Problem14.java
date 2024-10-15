package adventofcode.y2022;

import adventofcode.commons.AOCBoard;
import adventofcode.commons.AOCPoint;
import adventofcode.commons.AOCProblem;
import adventofcode.commons.AOCVector;

import java.io.BufferedReader;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Day 14: Regolith Reservoir
 * https://adventofcode.com/2022/day/14
 */
public class Problem14 extends AOCProblem<Long> {

    public static void main(String[] args) throws Exception {
        new Problem14().solve(false);
    }

    private Pattern POINT_PATTERN = Pattern.compile("(\\d+),(\\d+)");

    private AOCBoard<Character> board = new AOCBoard(Character.class, 1000, 200);
    private AOCPoint sandOrig = new AOCPoint(500, 0);
    private int maxY = 0;

    @Override
    public void processInput(BufferedReader reader) throws Exception {

        board.fill(' ');
        board.set(sandOrig, '+');

        reader
            .lines().forEach(line -> {
                List<AOCPoint> points = POINT_PATTERN
                    .matcher(line)
                    .results()
                    .map(m -> AOCPoint.valueOf(m.group(1), m.group(2)))
                    .peek(p -> maxY = Math.max(maxY, p.y))
                    .toList();
                drawPath(points);
            });
    }

    /**
     * Using your scan, simulate the falling sand. How many units of sand
     * come to rest before sand starts flowing into the abyss below?
     */
    @Override
    protected Long partOne() throws Exception {

        long result = 0;
        while (fallSand(sandOrig.clone())) {
            result++;
        }

        // board.dumpBoard("%c");
        return result;
    }

    /**
     * Using your scan, simulate the falling sand until the source of the
     * sand becomes blocked. How many units of sand come to rest?
     */
    @Override
    protected Long partTwo() throws Exception {

        board.clear('o', ' ');

        maxY += 2;
        for (int i = 0; i < board.N; ++i) {
            board.set(i, maxY, '#');
        }

        long result = 0;
        while (fallSand(sandOrig.clone())) {
            result++;
        }

        // board.dumpBoard("%c");
        return result + 1;
    }

    private boolean fallSand(AOCPoint p) {
        while (p.y < maxY) { // rest
            if (board.buffer[p.y + 1][p.x] == ' ') {
                p.traslate(0, 1);
            } else if (board.buffer[p.y + 1][p.x - 1] == ' ') {
                p.traslate(-1, 1);
            } else if (board.buffer[p.y + 1][p.x + 1] == ' ') {
                p.traslate(1, 1);
            } else {
                if (p.equals(sandOrig)) {
                    return false;
                } else {
                    board.set(p, 'o');
                    return true;
                }
            }
        }
        return false; // abyss
    }

    private void drawPath(List<AOCPoint> points) {

        AOCPoint p0 = points.getFirst();
        board.set(p0, '#');
        points.stream().skip(1).forEach(p -> {
            AOCVector v = p.distanceVector(p0).signs();
            while (!p0.equals(p)) {
                p0.traslate(v);
                board.set(p0, '#');
            }
        });
    }
}
