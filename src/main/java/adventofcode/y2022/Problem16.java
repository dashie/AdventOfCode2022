package adventofcode.y2022;

import adventofcode.commons.AOCProblem;
import adventofcode.commons.PatternEx;

import java.io.BufferedReader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Day 16: Proboscidea Volcanium
 * https://adventofcode.com/2022/day/16
 */
public class Problem16 extends AOCProblem<Long> {

    public static void main(String[] args) throws Exception {
        new Problem16().solve(false);
    }

    static class Valve {

        String id;
        int rate;
        String[] linkIds;
        private Map<String, Integer> dists = new HashMap<>();

        public Valve(String id, int rate, String[] linkIds) {
            this.id = id;
            this.rate = rate;
            this.linkIds = linkIds;
        }

        @Override
        public String toString() {
            return id + '[' + rate + ']';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Valve valve = (Valve) o;
            return Objects.equals(id, valve.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }

    class DistancePriority implements Comparator<Map.Entry<String, Integer>> {

        private final int timeRemaining;

        public DistancePriority(int timeRemaining) {
            this.timeRemaining = timeRemaining;
        }

        @Override
        public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
            int score1 = valves.get(o1.getKey()).rate * (timeRemaining - o1.getValue() - 1);
            int score2 = valves.get(o1.getKey()).rate * (timeRemaining - o1.getValue() - 1);
            return Integer.compare(score2, score1);
        }
    }

    private static final PatternEx INPUT_PATTERN = PatternEx.compile("Valve ([A-Z]{2}) has flow rate=([0-9]+); tunnels? leads? to valves? ([A-Z]{2}(, ([A-Z]{2}))*)");
    private Map<String, Valve> valves;

    @Override
    public void processInput(BufferedReader reader) throws Exception {

        valves = reader
            .lines()
            .map(INPUT_PATTERN::findGroups)
            .map(groups -> new Valve(groups[1], Integer.parseInt(groups[2]), groups[3].split(", ")))
            .collect(Collectors.toMap(v -> v.id, v -> v));

        // eval distances
        valves.values()
              .forEach(this::evalDistances);
    }

    /**
     * Work out the steps to release the most pressure in 30 minutes.
     * What is the most pressure you can release?
     */
    @Override
    public Long partOne() throws Exception {

        Set<String> closedValves = new HashSet<>();
        valves.values()
              .stream()
              .filter(v -> v.rate > 0)
              .forEach(v -> closedValves.add(v.id));

        int time = 30;
        Valve currentValve = valves.get("AA");
        long score = evalScore(time, closedValves, currentValve);
        return score;
    }


    /**
     * With you and an elephant working together for 26 minutes,
     * what is the most pressure you could release?
     */
    @Override
    protected Long partTwo() throws Exception {

        Set<String> closedValves = new HashSet<>();
        valves.values()
              .stream()
              .filter(v -> v.rate > 0)
              .forEach(v -> closedValves.add(v.id));

        Set<String>[][] partitions = partitioning(closedValves);

        int time = 26;
        Valve currentValve = valves.get("AA");
        long score = 0;
        for (Set<String>[] partition : partitions) {
            long manScore = evalScore(time, partition[0], currentValve);
            long elephantScore = evalScore(time, partition[1], currentValve);
            long tmpScore = manScore + elephantScore;
            if (tmpScore > score) {
                score = tmpScore;
            }
        }
        return score;
    }

    private long evalScore(int time, Set<String> closedValves, Valve currentValve) {
        if (time <= 0 || closedValves.isEmpty())
            return 0;

        Map.Entry<String, Integer>[] dists = currentValve
            .dists.entrySet()
                  .stream()
                  .filter(e -> closedValves.contains(e.getKey()))
                  .sorted(new DistancePriority(time))
                  .toArray(Map.Entry[]::new);

        long score = 0;
        for (Map.Entry<String, Integer> dist : dists) {
            int cost = dist.getValue() + 1;
            if (time > cost) {
                Valve v = valves.get(dist.getKey());

                time -= cost;
                closedValves.remove(v.id);

                int tmpScore = v.rate * time;
                tmpScore += evalScore(time, closedValves, v);
                if (tmpScore > score) {
                    score = tmpScore;
                }

                time += cost;
                closedValves.add(v.id);
            }
        }
        return score;
    }

    private Set<String>[][] partitioning(Set<String> closedValves) {
        List<String> valvesList = closedValves.stream()
                                              .toList();
        int n = closedValves.size();
        int n1 = closedValves.size() / 2;
        int n2 = n - n1; // choose s2 because can be bigger

        // C(n,s2)
        List<List<String>> combinations = new ArrayList<>();
        generateCombinations(valvesList, n2, 0, new ArrayList<>(), combinations);

        return combinations
            .stream()
            .map(c -> {
                Set<String> set1 = new HashSet<>(c);
                Set<String> set2 = new HashSet<>(closedValves);
                set2.removeAll(set1);
                return new Set[]{set1, set2};
            })
            .toArray(Set[][]::new);
    }

    private void evalDistances(Valve v0) {

        Queue<Valve> queue = new LinkedList<>();
        queue.add(v0);

        while (!queue.isEmpty()) {
            Valve v = queue.poll();
            for (String link : v.linkIds) { // neighbors
                if (link.equals(v0.id)) continue;
                int newDist = v0.dists.getOrDefault(v.id, 0) + 1;
                int oldDist = v0.dists.getOrDefault(link, Integer.MAX_VALUE);
                if (newDist < oldDist) {
                    v0.dists.put(link, newDist);
                    Valve vlink = valves.get(link);
                    queue.add(vlink);
                }
            }
        }

        // keep only valve rate>0
        v0.dists.keySet()
                .removeIf(id -> valves.get(id).rate == 0);
    }

    public static <T> void generateCombinations(List<T> set, int k, int start, List<T> current, List<List<T>> combinations) {
        if (current.size() == k) {
            combinations.add(new ArrayList<>(current));
            return;
        }

        for (int i = start; i < set.size(); i++) {
            current.add(set.get(i));
            generateCombinations(set, k, i + 1, current, combinations);
            current.remove(current.size() - 1);
        }
    }

}
