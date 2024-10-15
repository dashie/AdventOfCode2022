package adventofcode.y2022;

import adventofcode.commons.AOCPoint;
import adventofcode.commons.AOCProblem;

import java.io.BufferedReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Day 17: Pyroclastic Flow
 * https://adventofcode.com/2022/day/17
 */
public class Problem17 extends AOCProblem<Long> {

    public static void main(String[] args) throws Exception {
        new Problem17().solve(false);
    }

    class Rock {

        AOCPoint p;
        int[] shape;
        int height = 0;
        int length = 0;

        public Rock(int[] shape, int l) {
            this.shape = shape;
            length = l;
            height = shape.length;
        }

        private boolean move(int dx, int dy) {
            int x1 = p.x + dx;
            if (x1 < 0 || x1 + length > 7) return false;
            int y1 = p.y + dy;
            if (y1 < 0) return false;
            if (y1 < board.size()) {
                int shapeOffset = shape.length - 1;
                for (int sy = shapeOffset; sy >= 0; sy--) {
                    int boardIndex = y1 + shapeOffset - sy;
                    if (boardIndex >= board.size()) break;
                    int boardLine = board.get(boardIndex);
                    int shapeMask = shape[sy] >> x1;
                    if ((shapeMask & boardLine) != 0) return false;
                }
            }
            p.x += dx;
            p.y += dy;

            // System.out.println("1234567-");
            // for (int i = 0; i < shape.length; ++i) {
            //     System.out.println(boardRowToString(shape[i] >> p.x));
            // }
            // System.out.println();
            return true;
        }

        public void rest() {
            int shapeOffset = shape.length - 1;
            for (int sy = shapeOffset; sy >= 0; sy--) {
                int boardIndex = p.y + shapeOffset - sy;
                int boardLine = 0;
                if (boardIndex < board.size()) {
                    boardLine = board.get(boardIndex);
                } else {
                    board.add(0);
                }
                int shapeMask = shape[sy] >> p.x;
                boardLine = boardLine | shapeMask;
                board.set(boardIndex, boardLine);
            }
        }

        public int top() {
            return p.y + height;
        }
    }

    class SequenceData {

        long n;
        long height;

        public SequenceData(long n, long height) {
            this.n = n;
            this.height = height;
        }
    }

    public static final int BOARD_INIT_CAPACITY = 10_000_000;
    private ArrayList<Integer> board;
    private char[] windJets;
    private Rock[] rocks = new Rock[]{
        new Rock(new int[]{0b11110000}, 4),
        new Rock(new int[]{0b01000000, 0b11100000, 0b01000000}, 3),
        new Rock(new int[]{0b00100000, 0b00100000, 0b11100000}, 3),
        new Rock(new int[]{0b10000000, 0b10000000, 0b10000000, 0b10000000}, 1),
        new Rock(new int[]{0b11000000, 0b11000000}, 2),};

    @Override
    public void processInput(BufferedReader reader) throws Exception {
        windJets = reader
            .readLine()
            .toCharArray();
    }

    /**
     * How many units tall will the tower of rocks be after
     * 2022 rocks have stopped falling?
     */
    @Override
    protected Long partOne() throws Exception {
        board = new ArrayList<>(BOARD_INIT_CAPACITY);

        int n = 0;
        int steps = 0;
        while (n < 2022) {
            Rock rock = rocks[n % rocks.length];
            rock.p = new AOCPoint(2, board.size() + 3);
            do {
                int wind = windOffset(steps++);
                rock.move(wind, 0);
            } while (rock.move(0, -1));
            rock.rest();
            // dumpBoard();
            ++n;
        }

        // dumpBoard(false);
        return (long) board.size();
    }

    /**
     * How tall will the tower be after 1000000000000 rocks have stopped?
     */
    @Override
    protected Long partTwo() throws Exception {
        board = new ArrayList<>(BOARD_INIT_CAPACITY);

        // check for a repetition large as windJets input
        Map<String, SequenceData> sequences = new HashMap<>();
        ArrayDeque<String> sequenceWindow = new ArrayDeque<>(windJets.length);
        boolean searchForRepetitions = true;

        long maxN = 1_000000_000000L;
        int cutLine = -1;
        int cutBitset = 0;
        long heightOffset = 0;

        long t0 = System.currentTimeMillis();
        long n = 0;
        long steps = 0;
        while (n < maxN) {
            Rock rock = rocks[(int) (n % rocks.length)];
            rock.p = new AOCPoint(2, board.size() + 3);
            do {
                int wind = windOffset(steps++);
                rock.move(wind, 0);
            } while (rock.move(0, -1));
            rock.rest();
            // dumpBoard();
            ++n;

            // find for repetition pattern
            if (searchForRepetitions) {
                SequenceData sequenceData = matchRepetition(n, sequenceWindow, sequences);
                if (sequenceData != null) {
                    while ((n + sequenceData.n) < maxN) {
                        n += sequenceData.n;
                        heightOffset += sequenceData.height;
                    }
                    searchForRepetitions = false;
                }
            }
        }

        // dumpBoard(true);
        return heightOffset + board.size();
    }

    private SequenceData matchRepetition(
        long n,
        ArrayDeque<String> sequenceWindow,
        Map<String, SequenceData> sequences) {

        long height = board.size();

        String hex = Integer.toHexString(board.getLast());
        sequenceWindow.add(hex);
        if (sequenceWindow.size() > windJets.length) {
            sequenceWindow.removeFirst();
        }

        if (sequenceWindow.size() == windJets.length) {
            String sequenceString = sequenceWindow.stream()
                                                  .collect(Collectors.joining());
            SequenceData sequenceData = new SequenceData(n, height);
            SequenceData match = sequences.put(sequenceString, sequenceData);
            if (match != null) {
                // System.out.println("pattern at " + height);
                return new SequenceData(
                    n - match.n,
                    height - match.height);
            }
        }
        return null;
    }

    private void cutBoard(int cutLine) {
        ArrayList<Integer> tmpBoard = new ArrayList<>(BOARD_INIT_CAPACITY);
        while (cutLine < board.size()) {
            tmpBoard.add(board.get(cutLine));
            cutLine++;
        }
        board = tmpBoard;
    }

    private int windOffset(long time) {
        return switch (windJets[(int) (time % windJets.length)]) {
            case '>' -> 1;
            default -> -1;
        };
    }

    public void dumpBoard(boolean rownumber) {
        System.out.println("--------------");
        for (int i = board.size() - 1; i >= 0; --i) {
            int row = board.get(i);
            if (rownumber) System.out.printf("%5d", i);
            System.out.printf("|");
            System.out.print(boardRowToString(row));
            System.out.println('|');
        }
        System.out.println("--------------");
    }

    public String boardRowToString(int n) {
        StringBuilder sb = new StringBuilder(8);
        sb.append((n & 0b10000000) != 0 ? '#' : ' ');
        sb.append((n & 0b01000000) != 0 ? '#' : ' ');
        sb.append((n & 0b00100000) != 0 ? '#' : ' ');
        sb.append((n & 0b00010000) != 0 ? '#' : ' ');
        sb.append((n & 0b00001000) != 0 ? '#' : ' ');
        sb.append((n & 0b00000100) != 0 ? '#' : ' ');
        sb.append((n & 0b00000010) != 0 ? '#' : ' ');
        return sb.toString();
    }

}
