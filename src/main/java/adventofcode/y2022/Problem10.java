package adventofcode.y2022;

import adventofcode.commons.AOCProblem;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.stream.Stream;

/**
 * Day 10: Cathode-Ray Tube
 * https://adventofcode.com/2022/day/10
 */
public class Problem10 extends AOCProblem<String> {

    public static void main(String[] args) throws Exception {
        new Problem10().solve(false);
    }

    record Instruction(String id, int n) {

    }

    private Instruction[] instructions;

    @Override
    public void processInput(BufferedReader reader) throws Exception {

        instructions = reader
            .lines()
            .flatMap(l -> {
                String[] parts = l.split(" ");
                if (parts[0].startsWith("addx")) {
                    return Stream.of(null, new Instruction(parts[0], Integer.parseInt(parts[1])));
                } else {
                    return Stream.of(new Instruction(parts[0], 0));
                }
            }).toArray(Instruction[]::new);
    }

    /**
     * Find the signal strength during the 20th, 60th, 100th, 140th, 180th, and 220th cycles.
     * What is the sum of these six signal strengths?
     */
    @Override
    protected String partOne() throws Exception {

        long score = 0;

        long X = 1;
        long cycle = 0;
        for (Instruction i : instructions) {
            cycle++;
            if (cycle > 220) {
                //System.out.println("-- break --");
                break;
            }
            if ((cycle + 20) % 40 == 0) {
                score += cycle * X;
                //System.out.println("signal strength: " + cycle + " " + X);
            }
            if (i != null && i.id.equals("addx")) {
                X += i.n;
            }
        }

        return Long.toString(score);
    }

    /**
     * Render the image given by your program.
     * What eight capital letters appear on your CRT?
     */
    @Override
    protected String partTwo() throws Exception {

        StringWriter buffer = new StringWriter();
        PrintWriter out = new PrintWriter(buffer);

        long X = 1;
        long cycle = 0;
        out.println();
        for (Instruction i : instructions) {
            cycle++;
            long offset = (cycle - 1) % 40;
            if (offset >= X - 1 && offset <= X + 1) {
                out.print("#");
            } else {
                out.print(" ");
            }
            if (cycle % 40 == 0) out.println();

            if (i != null && i.id.equals("addx")) {
                X += i.n;
            }
        }
        out.println();
        out.flush();
        return buffer.toString();
    }

}
