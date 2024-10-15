package adventofcode.y2022;

import adventofcode.commons.AOCPoint;
import adventofcode.commons.AOCProblem;
import adventofcode.commons.AOCVector;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Day 15: Beacon Exclusion Zone
 * https://adventofcode.com/2022/day/15
 */
public class Problem15 extends AOCProblem<Long> {

    public static void main(String[] args) throws Exception {
        new Problem15().solve(false);
    }

    private Map<Integer, Map<Integer, Character>> board = new HashMap<>();
    private List<AOCPoint[]> data = new ArrayList<>();
    private int minX = Integer.MIN_VALUE;
    private int maxX = Integer.MAX_VALUE;

    // Sensor at x=2899860, y=3122031: closest beacon is at x=2701269, y=3542780
    private Pattern POINT_PATTERN = Pattern.compile("x=(-?\\d+), y=(-?\\d+)");

    @Override
    public void processInput(BufferedReader reader) throws Exception {

        reader.lines().forEach(line -> {
            AOCPoint[] points = POINT_PATTERN
                .matcher(line)
                .results()
                .map(m -> AOCPoint.valueOf(m.group(1), m.group(2)))
                .toArray(AOCPoint[]::new);

            data.add(points);
            // setCell(points[0], 'S');
            setCell(points[1], 'B');
        });
    }

    /**
     * Consult the report from the sensors you just deployed.
     * In the row where y=2000000, how many positions cannot contain a beacon?
     */
    @Override
    protected Long partOne() throws Exception {

        long result = 0;
        for (AOCPoint[] pts : data) {
            result += fillFreeCells(pts[0], pts[1], 2000000);
        }
        return result;
    }

    /**
     * Find the only possible position for the distress beacon.
     * What is its tuning frequency?
     */
    @Override
    protected Long partTwo() throws Exception {

        for (int y = 0; y < 4000001; ++y) {
            AOCPoint free = findFreeCell(y);
            if (free != null) {
                return free.x * 4000000L + free.y;
            }
        }

        return 0L;
    }

    private AOCPoint findFreeCell(int y) {
        // System.out.println("search Y: " + y);
        searchx:
        for (int x = 0; x < 4000001; ++x) {
            for (AOCPoint[] pair : data) {
                AOCPoint s = pair[0];
                AOCPoint b = pair[1];
                int coveredDistance = s.distanceVector(b).manhattam();
                AOCVector dist = s.distanceVector(x, y).absolute();
                int dm = dist.manhattam();
                if (dm <= coveredDistance) {
                    x = s.x + coveredDistance - dist.y; // skip
                    continue searchx;
                }
            }
            if (x < 4000001) return new AOCPoint(x, y);
        }
        return null;
    }

    private long fillFreeCells(AOCPoint s, AOCPoint b, int y) {

        long filled = 0;

        AOCVector dv = s.distanceVector(b).absolute();
        int dist = (int) (dv.x + dv.y);
        int dy = Math.abs(s.y - y);
        for (int x = s.x - (dist - dy); x <= s.x + (dist - dy); ++x) {
            AOCPoint p = new AOCPoint(x, y);
            if (getCell(p) == ' ') {
                setCell(p, '#');
                filled++;
            }
        }

        return filled;
    }

    private Character getCell(AOCPoint p) {
        Map<Integer, Character> row = board.get(p.y);
        if (row == null) {
            return ' ';
        }
        Character c = row.get(p.x);
        if (c == null) {
            return ' ';
        }
        return c;
    }

    private Character setCell(AOCPoint p, Character c) {
        Map<Integer, Character> row = board.get(p.y);
        if (row == null) {
            row = new HashMap<>();
            board.put(p.y, row);
        }
        return row.put(p.x, c);
    }
}
