package adventofcode.commons;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *
 */
public class AOCProblem<T> {

    public AOCProblem() {
    }

    public final void solve() throws Exception {
        solve(false);
    }

    public void solve(boolean useSample) throws Exception {
        Class thisClass = this.getClass();
        String clasName = thisClass.getSimpleName();
        String useSampleSuffix = useSample ? "-sample" : "";
        String dataURL = String.format("%s%s.txt", clasName, useSampleSuffix);
        dataURL = dataURL.replaceAll("v\\d+([.-])", "$1");
        try (InputStream is = thisClass.getResourceAsStream(dataURL)) {
            if (is == null) {
                throw new IllegalStateException(String.format("Missing input data: %s", dataURL));
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            processInput(reader);
            System.out.println(getClass().getName());
            System.out.printf("  Part One: %s%n", partOne());
            System.out.printf("  Part Two: %s%n", partTwo());
            System.out.println();
        }
    }

    public void processInput(BufferedReader reader) throws Exception {

    }

    protected T partOne() throws Exception {
        return null;
    }

    protected T partTwo() throws Exception {
        return null;
    }
}
