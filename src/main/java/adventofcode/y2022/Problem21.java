package adventofcode.y2022;

import adventofcode.commons.AOCProblem;
import adventofcode.commons.PatternEx;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Day 21: Monkey Math
 * https://adventofcode.com/2022/day/21
 */
public class Problem21 extends AOCProblem<Long> {

    public static void main(String[] args) throws Exception {
        new Problem21().solve(false);
    }

    // root: pppw + sjmn
    static final PatternEx JOB_PATTERN = PatternEx.compile("(\\w+): ((\\d+)|(\\w+))( (.) (\\w+))?");

    class Job {

        String id;
        String p1;
        String p2;
        Long value;

        BiFunction<Long, Long, Long> op;

        Long solve() {
            if (op != null) {
                Job j1 = jobs.get(p1);
                Job j2 = jobs.get(p2);
                return op.apply(j1.solve(), j2.solve());
            } else {
                return value;
            }
        }

        static Job parse(Problem21 p, String line) {
            Job job = p.new Job();
            String[] groups = JOB_PATTERN.findGroups(line);
            job.id = groups[1];
            if (groups[3] != null) {
                job.value = Long.parseLong(groups[3]);
            } else {
                job.p1 = groups[4];
                job.p2 = groups[7];
                job.op = switch (groups[6]) {
                    case "+" -> (a, b) -> a + b;
                    case "-" -> (a, b) -> a - b;
                    case "*" -> (a, b) -> a * b;
                    case "/" -> (a, b) -> a / b;
                    default -> throw new IllegalStateException("Invalid op");
                };
            }
            return job;
        }
    }

    private Map<String, Job> jobs = new HashMap<>();

    @Override
    public void processInput(BufferedReader reader) throws Exception {

        reader.lines()
              .map(l -> Job.parse(this, l))
              .forEach(j -> jobs.put(j.id, j));
    }

    /**
     * However, your actual situation involves considerably more monkeys.
     * What number will the monkey named root yell?
     */
    @Override
    protected Long partOne() throws Exception {

        long result = solveJob(jobs.get("root"));
        return result;
    }

    /**
     * What number do you yell to pass root's equality test?
     */
    @Override
    protected Long partTwo() throws Exception {

        Job root = jobs.get("root");
        Job humn = jobs.remove("humn");

        long r1 = jobs.get(root.p2)
                      .solve();

        jobs.put(humn.id, humn);
        long n = 0, r2 = -1;
        long lastDiff = Long.MAX_VALUE;
        long lastSign = 0;
        long inc = (long) Math.pow(10, (long) Math.log10(r1)) / 10;
        while (r2 != r1) { // objective function
            humn.value = n;
            r2 = jobs.get(root.p1)
                     .solve();

            if (r2 == r1) {
                return n;
            } else {
                long diff = r2 - r1;
                // System.out.println(diff);
                long sign = diff >= 0 ? 1 : -1;
                if (lastSign != 0 && (sign != lastSign || Math.abs(diff) > lastDiff)) {
                    inc = -(inc / 10);
                }
                lastDiff = Math.abs(diff);
                lastSign = sign;
                n += inc;
            }
        }

        throw new IllegalStateException("No solution");
    }

    private long solveJob(Job job) {
        if (job.value == null) {
            long p1value = solveJob(jobs.get(job.p1));
            long p2value = solveJob(jobs.get(job.p2));
            job.value = job.op.apply(p1value, p2value);
        }
        return job.value;
    }
}
