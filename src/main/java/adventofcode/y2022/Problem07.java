package adventofcode.y2022;

import adventofcode.commons.AOCProblem;

import java.io.BufferedReader;
import java.util.*;

/**
 * Day 7: No Space Left On Device
 * https://adventofcode.com/2022/day/7
 */
public class Problem07 extends AOCProblem<Long> {

    public static void main(String[] args) throws Exception {
        new Problem07().solve();
    }

    static class File {

        String name;
        boolean isDirectory;
        long size;
        Map<String, File> children = new HashMap<>();

        public File(String name, boolean isDirectory) {
            this.name = name;
            this.isDirectory = isDirectory;
        }

        public File(String name, long size) {
            this.name = name;
            this.size = size;
        }
    }

    File root;

    @Override
    public void processInput(BufferedReader reader) throws Exception {

        root = new File("/", true);

        Stack<File> directory = new Stack<>();
        directory.push(root);
        reader.readLine(); // skip cd /

        String line;
        while ((line = reader.readLine()) != null) {
            File wd = directory.peek();
            if (!line.startsWith("$")) {
                String[] parts = line.split(" ");
                String name = parts[1];
                if (parts[0].equals("dir")) {
                    wd.children.put(name, new File(name, true));
                } else {
                    long size = Long.parseLong(parts[0]);
                    wd.children.put(name, new File(name, size));
                }
            } else if (line.equals("$ ls")) {
                continue; // skip
            } else if (line.equals("$ cd ..")) {
                long total = wd.children.values()
                                        .stream()
                                        .map(f -> f.size)
                                        .reduce(0L, (a, b) -> a + b);
                wd.size = total;
                directory.pop();
            } else if (line.startsWith("$ cd")) {
                String dirname = line.substring(5);
                File dir = wd.children.get(dirname);
                if (dir == null)
                    throw new IllegalStateException("Directory does not exists: " + dirname);
                directory.push(dir);
            }
        }

        while (!directory.isEmpty()) {
            File wd = directory.pop();
            long total = wd.children.values()
                                    .stream()
                                    .map(f -> f.size)
                                    .reduce(0L, (a, b) -> a + b);
            wd.size = total;
        }
    }

    /**
     * Find all of the directories with a total size of at most 100000.
     * What is the sum of the total sizes of those directories?
     */
    @Override
    protected Long partOne() throws Exception {

        List<File> matches = new ArrayList<>();
        searchMost(root, matches);
        return matches
            .stream()
            .map(f -> f.size)
            .reduce(0L, (a, b) -> a + b);
    }

    /**
     * Find the smallest directory that, if deleted, would free up enough space
     * on the filesystem to run the update. What is the total size of that directory?
     */
    @Override
    protected Long partTwo() throws Exception {

        // 70000000
        // 30000000

        long freeSpace = 70000000 - root.size;
        long spaceToFreeUp = 30000000 - freeSpace;

        List<File> matches = new ArrayList<>();
        searchMin(root, matches, spaceToFreeUp);

        return matches
            .stream()
            .map(f -> f.size)
            .sorted()
            .findFirst()
            .get();
    }

    private void searchMost(File file, List<File> set) {
        if (!file.isDirectory)
            throw new IllegalStateException("File is not directory: " + file.name);
        if (file.size <= 100000)
            set.add(file);
        file.children
            .values()
            .stream()
            .filter(f -> f.isDirectory)
            .forEach(f -> searchMost(f, set));
    }

    private void searchMin(File file, List<File> set, long min) {
        if (!file.isDirectory)
            throw new IllegalStateException("File is not directory: " + file.name);
        if (file.size >= min)
            set.add(file);
        file.children
            .values()
            .stream()
            .filter(f -> f.isDirectory)
            .forEach(f -> searchMin(f, set, min));
    }
}
