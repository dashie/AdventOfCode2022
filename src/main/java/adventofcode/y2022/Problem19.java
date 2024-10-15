package adventofcode.y2022;

import adventofcode.commons.AOCProblem;
import adventofcode.commons.PatternEx;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;

/**
 * Day 19: Not Enough Minerals
 * https://adventofcode.com/2022/day/19
 */
public class Problem19 extends AOCProblem<Long> {

    public static void main(String[] args) throws Exception {
        new Problem19().solve(false);
    }

    static PatternEx INPUT_PATTERN = PatternEx.compile("Blueprint (\\d+): " +
        "Each ore robot costs (\\d+) ore. " +
        "Each clay robot costs (\\d+) ore. " +
        "Each obsidian robot costs (\\d+) ore and (\\d+) clay. " +
        "Each geode robot costs (\\d+) ore and (\\d+) obsidian.");

    // Blueprint
    //           ore cla obs geo
    // ore      [1  ,2  ,3  ,4  ]
    // clay     [1  ,2  ,3  ,4  ]
    // obsidian [1  ,2  ,3  ,4  ]
    // geodes   [1  ,2  ,3  ,4  ]
    // max      [1  ,2  ,3  ,4  ] // max cost
    static int ORE = 0;
    static int CLAY = 1;
    static int OBSIDIAN = 2;
    static int GEODE = 3;
    static int MAX = 4;
    static int NOTHING = -1;
    List<int[][]> blueprints = new ArrayList<>();
    public int maxT = 0;
    public int bestT = Integer.MAX_VALUE;


    @Override
    public void processInput(BufferedReader reader) throws Exception {

        reader.lines()
              .map(INPUT_PATTERN::findGroups)
              .forEach(g -> {
                  int[][] blueprint = new int[5][4];

                  blueprint[ORE][ORE] = parseInt(g[2]);
                  blueprint[CLAY][ORE] = parseInt(g[3]);
                  blueprint[OBSIDIAN][ORE] = parseInt(g[4]);
                  blueprint[OBSIDIAN][CLAY] = parseInt(g[5]);
                  blueprint[GEODE][ORE] = parseInt(g[6]);
                  blueprint[GEODE][OBSIDIAN] = parseInt(g[7]);

                  blueprint[MAX][ORE] = max(blueprint, ORE);
                  blueprint[MAX][CLAY] = max(blueprint, CLAY);
                  blueprint[MAX][OBSIDIAN] = max(blueprint, OBSIDIAN);
                  blueprint[MAX][GEODE] = max(blueprint, GEODE);

                  blueprints.add(blueprint);
              });
    }

    /**
     * Determine the quality level of each blueprint using the largest number
     * of geodes it could produce in 24 minutes.
     * What do you get if you add up the quality level of all of the blueprints
     * in your list?
     */
    @Override
    protected Long partOne() throws Exception {

        long result = 0;
        for (int id = 1; id <= blueprints.size(); ++id) {
            int[][] blueprint = blueprints.get(id - 1);
            long geodes = solveBlueprint(blueprint, 24);
            // System.out.printf("%3d: geodes=%d%n", id, geodes);
            result += geodes * id;
        }
        return result;
    }

    /**
     * Don't worry about quality levels; instead, just determine the largest
     * number of geodes you could open using each of the first three blueprints.
     * What do you get if you multiply these numbers together?
     */
    @Override
    protected Long partTwo() throws Exception {

        long result = 1;
        for (int id = 1; id <= 3; ++id) {
            int[][] blueprint = blueprints.get(id - 1);
            long geodes = solveBlueprint(blueprint, 32);
            // System.out.printf("%3d: geodes=%d%n", id, geodes);
            result *= geodes;
        }
        return result;
    }

    private long solveBlueprint(int[][] blueprint, int t) {

        int[] robots = new int[]{1, 0, 0, 0};
        int[] points = new int[4];
        maxT = t;
        bestT = Integer.MAX_VALUE;
        int geodes = solveBlueprint(1, blueprint, robots, points);
        return geodes;
    }

    private int solveBlueprint(int t, int[][] blueprint, int[] robots, int[] points) {
        if (t > maxT) {
            int geodes = points[GEODE];
            // System.out.printf("%3d: %-5d %s %s%n", t, geodes, Arrays.toString(points), Arrays.toString(robots));
            return geodes;
        }

        if (robots[GEODE] == 0 && t > bestT) {
            // System.out.printf("%3d: %s %s X%n", t, Arrays.toString(points), Arrays.toString(robots));
            return points[GEODE];
        }

        int geodes = 0;
        for (int option : optionsPriority(t, blueprint, robots)) {
            if (option == NOTHING) {

                // do not build anything
                int[] tmpPoints = sum(points, robots);
                int tmpGeodes = solveBlueprint(t + 1, blueprint, robots, tmpPoints);
                if (tmpGeodes > geodes) {
                    geodes = tmpGeodes;
                }

            } else if (cmpGE(points, blueprint[option])) {
                if (option == GEODE) {
                    if (points[GEODE] == 0 && t < bestT) {
                        bestT = t;
                    }
                }
                int[] tmpPoints = sum(points, robots);
                tmpPoints = subtract(tmpPoints, blueprint[option]);
                int[] tmpRobots = inc(robots, option);
                int tmpGeodes = solveBlueprint(t + 1, blueprint, tmpRobots, tmpPoints);
                if (tmpGeodes > geodes) {
                    geodes = tmpGeodes;
                }
                if (option == GEODE) {
                    return geodes;
                }
            }
        }
        return geodes;
    }

    private int[] optionsPriority(int t, int[][] blueprint, int[] robots) {
        if (robots[ORE] < blueprint[MAX][ORE]) {
            return new int[]{ORE, CLAY, OBSIDIAN, GEODE, NOTHING};
        } else if (robots[CLAY] < blueprint[MAX][CLAY]) {
            return new int[]{CLAY, OBSIDIAN, GEODE, NOTHING};
        } else if (robots[OBSIDIAN] < blueprint[MAX][OBSIDIAN]) {
            return new int[]{OBSIDIAN, GEODE, NOTHING};
        } else {
            return new int[]{GEODE, NOTHING};
        }
    }

    private boolean cmpGE(int[] v1, int[] v2) {
        return v1[0] >= v2[0]
            && v1[1] >= v2[1]
            && v1[2] >= v2[2]
            && v1[3] >= v2[3];
    }

    private int[] subtract(int[] v1, int[] v2) {
        return new int[]{v1[0] - v2[0],
            v1[1] - v2[1],
            v1[2] - v2[2],
            v1[3] - v2[3]};
    }

    private int[] sum(int[] v1, int[] v2) {
        return new int[]{v1[0] + v2[0],
            v1[1] + v2[1],
            v1[2] + v2[2],
            v1[3] + v2[3]};
    }

    private int[] inc(int[] v1, int index) {
        int[] tmp = v1.clone();
        tmp[index]++;
        return tmp;
    }

    private int max(int[][] m1, int col) {
        int max = 0;
        for (int i = 0; i < m1.length; ++i) {
            if (m1[i][col] > max) {
                max = m1[i][col];
            }
        }
        return max;
    }

}
