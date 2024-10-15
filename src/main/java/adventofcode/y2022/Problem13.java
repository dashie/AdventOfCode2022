package adventofcode.y2022;

import adventofcode.commons.AOCProblem;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Day 13: Distress Signal
 * https://adventofcode.com/2022/day/13
 */
public class Problem13 extends AOCProblem<Long> {

    public static void main(String[] args) throws Exception {
        new Problem13().solve(false);
    }

    private static final Pattern PACKET_PATTERN = Pattern.compile("\\[|\\]|[0-9]+");

    static class Packet implements Comparable<Packet> {

        List<Object> items = new ArrayList<>();

        public Packet() {
        }

        public Packet(Long n) {
            items.add(n);
        }

        @Override
        public int compareTo(Packet p) {
            for (int i = 0; i < items.size(); ++i) {
                if (i >= p.items.size()) {
                    return 1;
                }
                int order = compare(items.get(i), p.items.get(i));
                if (order < 0) {
                    return -1;
                } else if (order > 0) {
                    return 1;
                }
            }
            if (p.items.size() > items.size()) {
                return -1;
            }
            return 0;
        }

        private static int compare(Object o1, Object o2) {
            if (o1 instanceof Packet p1 && o2 instanceof Packet p2) {
                return p1.compareTo(p2);
            } else if (o1 instanceof Packet p1 && o2 instanceof Long n2) {
                return p1.compareTo(new Packet(n2));
            } else if (o1 instanceof Long n1 && o2 instanceof Packet p2) {
                return new Packet(n1).compareTo(p2);
            } else if (o1 instanceof Long n1 && o2 instanceof Long n2) {
                return n1.compareTo(n2);
            } else {
                throw new IllegalStateException();
            }
        }

        public static Packet parsePacket(String str) {
            Matcher matcher = PACKET_PATTERN.matcher(str);
            Stack<Packet> packets = new Stack<>();

            Packet root = null;
            while (matcher.find()) {
                String token = matcher.group();
                switch (token) {
                    case "[" -> packets.push(new Packet());
                    case "]" -> {
                        root = packets.pop();
                        if (packets.size() > 0) packets.peek().items.add(root);
                    }
                    default -> packets.peek().items.add(Long.parseLong(token));
                }
            }

            return root;
        }
    }

    List<Packet[]> packets;

    @Override
    public void processInput(BufferedReader reader) throws Exception {

        packets = new ArrayList<>();

        String line;
        while ((line = reader.readLine()) != null) {
            Packet[] pair = new Packet[]{
                    Packet.parsePacket(line),
                    Packet.parsePacket(reader.readLine())
            };
            packets.add(pair);
            reader.readLine(); // skip blank line
        }
    }

    /**
     * Determine which pairs of packets are already in the right order.
     * What is the sum of the indices of those pairs?
     */
    @Override
    protected Long partOne() throws Exception {

        long result = 0;
        for (int i = 0; i < packets.size(); ++i) {
            Packet[] pair = packets.get(i);
            if (pair[0].compareTo(pair[1]) < 0) {
                result += i + 1;
            }
        }

        return result;
    }

    /**
     * Organize all of the packets into the correct order.
     * What is the decoder key for the distress signal?
     */
    @Override
    protected Long partTwo() throws Exception {

        Packet d1 = new Packet(2L);
        Packet d2 = new Packet(6L);

        List<Packet> flatList = packets
                .stream()
                .flatMap(p -> Stream.of(p[0], p[1]))
                .collect(Collectors.toCollection(ArrayList::new));

        flatList.add(d1);
        flatList.add(d2);
        flatList.sort(Comparator.naturalOrder());

        long result = 1;
        for (int i = 0; i < flatList.size(); ++i) {
            Packet p = flatList.get(i);
            if (p == d1 || p == d2) {
                result *= (i + 1);
            }
        }

        return result;
    }
}
