package adventofcode.y2022;

import adventofcode.commons.AOCPoint;
import adventofcode.commons.AOCProblem;

import java.io.BufferedReader;
import java.util.*;

import static java.lang.Integer.parseInt;

/**
 * Day 18: Boiling Boulders
 * https://adventofcode.com/2022/day/18
 */
public class Problem18 extends AOCProblem<Long> {

    public static void main(String[] args) throws Exception {
        new Problem18().solve(false);
    }

    Map<String, AOCPoint> cubes = new HashMap<>();
    Set<String> bubbles = new HashSet<>();
    int boxX0 = 0;
    int boxX1 = 0;
    int boxY0 = 0;
    int boxY1 = 0;
    int boxZ0 = 0;
    int boxZ1 = 0;

    @Override
    public void processInput(BufferedReader reader) throws Exception {

        reader.lines()
              .forEach(s -> {
                  AOCPoint p = parseCube(s);
                  cubes.put(s, p);

                  // find box limits
                  if (p.x < boxX0) boxX0 = p.x;
                  if (p.x > boxX1) boxX1 = p.x;
                  if (p.y < boxY0) boxY0 = p.y;
                  if (p.y > boxY1) boxY1 = p.y;
                  if (p.z < boxZ0) boxZ0 = p.z;
                  if (p.z > boxZ1) boxZ1 = p.z;
              });
    }

    /**
     * What is the surface area of your scanned lava droplet?
     */
    @Override
    protected Long partOne() throws Exception {

        long result = 0;

        for (AOCPoint cube : cubes.values()) {
            long freeSides = 6;
            for (String n : generateNeighborKeys(cube)) {
                if (cubes.containsKey(n)) freeSides--;
            }
            result += freeSides;
        }
        return result;
    }

    /**
     * What is the exterior surface area of your scanned lava droplet?
     */
    @Override
    protected Long partTwo() throws Exception {

        long n = 0;
        long result = 0;
        for (AOCPoint cube : cubes.values()) {
            long freeSides = 0;
            for (String neighbor : generateNeighborKeys(cube)) {
                if (!cubes.containsKey(neighbor)) {
                    AOCPoint bubble = parseCube(neighbor);
                    if (!checkIfBubble(bubble)) freeSides++;
                }
            }
            result += freeSides;
        }
        return result;
    }

    private boolean checkIfBubble(AOCPoint bubble) {
        Set<String> visited = new HashSet<>();
        Queue<AOCPoint> queue = new LinkedList<>();
        queue.add(bubble);

        boolean isBubble = true;
        while (!queue.isEmpty()) {
            AOCPoint p = queue.poll();
            String pid = (p.x) + "," + (p.y) + "," + (p.z);
            visited.add(pid);
            if (isFree(p)) {
                isBubble = false;
                break;
            }
            for (String n : generateNeighborKeys(p)) {
                if (!cubes.containsKey(n) && !visited.contains(n)) {
                    AOCPoint np = parseCube(n);
                    queue.add(np);
                    visited.add(n);
                }
            }
        }

        if (isBubble) {
            bubbles.addAll(visited);
        }
        return isBubble;
    }

    private boolean isFree(AOCPoint bubble) {
        return bubble.x < boxX0 || bubble.x > boxX1 || bubble.y < boxY0 || bubble.y > boxY1 || bubble.x < boxZ0 || bubble.z > boxZ1;
    }

    private List<String> generateNeighborKeys(AOCPoint p) {
        List<String> keys = new ArrayList<>(6);
        keys.add((p.x - 1) + "," + (p.y) + "," + (p.z));
        keys.add((p.x + 1) + "," + (p.y) + "," + (p.z));
        keys.add((p.x) + "," + (p.y - 1) + "," + (p.z));
        keys.add((p.x) + "," + (p.y + 1) + "," + (p.z));
        keys.add((p.x) + "," + (p.y) + "," + (p.z - 1));
        keys.add((p.x) + "," + (p.y) + "," + (p.z + 1));
        return keys;
    }

    private static AOCPoint parseCube(String s) {
        String[] parts = s.split(",");
        AOCPoint p = new AOCPoint(parseInt(parts[0]), parseInt(parts[1]), parseInt(parts[2]));
        return p;
    }
}
