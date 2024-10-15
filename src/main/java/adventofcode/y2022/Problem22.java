package adventofcode.y2022;

import adventofcode.commons.*;

import java.io.BufferedReader;
import java.util.function.Function;

/**
 * Day 22: Monkey Map
 * https://adventofcode.com/2022/day/22
 */
public class Problem22 extends AOCProblem<Integer> {

    static boolean USE_SAMPLE = false;
    static int FACE_SIZE = USE_SAMPLE ? 4 : 50;

    public static void main(String[] args) throws Exception {
        new Problem22().solve(USE_SAMPLE);
    }

    static final AOCVector UP = new AOCVector(0, -1);
    static final AOCVector DOWN = new AOCVector(0, 1);
    static final AOCVector RIGHT = new AOCVector(1, 0);
    static final AOCVector LEFT = new AOCVector(-1, 0);

    PatternEx MOVES_PATTERN = PatternEx.compile("\\d+|\\w");
    AOCBoard initialBoard = new AOCBoard(Character.class, FACE_SIZE * 4, FACE_SIZE * 4);
    AOCBoard board = null;
    AOCPoint currentPos = null;
    AOCVector currentDirection = null;
    Object[] moves;
    FaceRef[] faceRefs = null;

    @Override
    public void processInput(BufferedReader reader) throws Exception {

        initialBoard.fill(' ');

        int y = 0;
        String line;
        while (!(line = reader.readLine()).isEmpty()) {
            initialBoard.fillRow(y, line.chars().mapToObj(c -> (char) c).toArray(Character[]::new));
            y++;
        }

        line = reader.readLine();
        moves = MOVES_PATTERN.results(line, 0).map(this::toMove).toArray();

        // find first cell
        int x = 0;
        firstCellSearch:
        for (y = 0; y < initialBoard.M; ++y) {
            for (x = 0; x < initialBoard.N; ++x) {
                if (initialBoard.get(x, y).equals('.')) break firstCellSearch;
            }
        }
        currentPos = new AOCPoint(x, y);
        currentDirection = new AOCVector(1, 0);

        if (USE_SAMPLE) {
            faceRefs = faceRefsSample;
        } else {
            faceRefs = faceRefsPart2;
        }
    }

    /**
     * Follow the path given in the monkeys' notes.
     * What is the final password?
     */
    @Override
    protected Integer partOne() throws Exception {

        board = initialBoard.clone();

        long result = 0;
        for (Object move : moves) {
            next2DMove(move);
        }
        board.set(currentPos, '⚑');

        int facing = evalFacing();
        // board.dumpBoard("%c");
        return 1000 * (currentPos.y + 1) + 4 * (currentPos.x + 1) + facing;
    }

    /**
     * Fold the map into a cube, then follow the path given in the monkeys' notes.
     * What is the final password?
     */
    @Override
    public Integer partTwo() throws Exception {

        board = initialBoard.clone();

        for (Object move : moves) {
            // System.out.println(move);
            next3DMove(move);
            // board.dumpBoard("%c");
        }
        board.set(currentPos, '⚑');

        int facing = evalFacing();

        // board.dumpBoard("%c");
        // System.out.println("pos: " + currentPos + " facing: " + facing);
        return 1000 * (currentPos.y + 1) + 4 * (currentPos.x + 1) + facing;
    }

    /**
     *
     */
    boolean next2DMove(Object move) {
        if (move instanceof String str) {
            switch (str) {
                case "R" ->
                    currentDirection = currentDirection.rotate90L(); // inverted because the matrix coordinates are inverted
                case "L" -> currentDirection = currentDirection.rotate90R(); //
            }
            return true;
        } else if (move instanceof Integer n) {
            for (int i = 0; i < n; ++i) {
                board.set(currentPos, directionChar(currentDirection));
                AOCPoint p = currentPos.traslateNew(currentDirection);
                if (board.isValidCell(p) && board.get(p).equals('#')) {
                    return false;
                } else if (board.isValidCell(p) && !board.get(p).equals(' ')) {
                    currentPos = p;
                } else {
                    // search for opposite edge
                    AOCVector vs = currentDirection.rotate90R().rotate90R();
                    p = currentPos;
                    AOCPoint ps = p.clone(); // ?? if we have only one line ??
                    do {
                        if (!board.get(p).equals(' ')) {
                            ps = p;
                        }
                        p = p.traslateNew(vs);
                    } while (board.isValidCell(p));
                    if (board.get(ps).equals('#')) {
                        return false;
                    } else {
                        currentPos = ps;
                    }
                }
            }
            return true;
        }
        throw new IllegalStateException();
    }

    char directionChar(AOCVector v) {
        if (v.x > 0) {
            return '>';
        } else if (v.x < 0) {
            return '<';
        } else if (v.y > 0) {
            return 'v';
        } else if (v.y < 0) {
            return '^';
        } else {
            return '?';
        }
    }

    Object toMove(String str) {
        if (Character.isDigit(str.charAt(0))) {
            return Integer.parseInt(str);
        } else {
            return str;
        }
    }

    Function<AOCPoint, AOCPoint> POINT_ROTATE_RIGHT = (p) -> rotate90Rmod(p);
    Function<AOCPoint, AOCPoint> POINT_ROTATE_LEFT = (p) -> rotate90Lmod(p);
    Function<AOCPoint, AOCPoint> POINT_FLIP = (p) -> flipmod(p);
    Function<AOCPoint, AOCPoint> POINT_CONTINUE = (p) -> p.moduleNew(FACE_SIZE, FACE_SIZE);
    Function<AOCVector, AOCVector> DIRECTION_ROTATE_RIGHT = (v) -> v.rotate90L();
    Function<AOCVector, AOCVector> DIRECTION_ROTATE_LEFT = (v) -> v.rotate90R();
    Function<AOCVector, AOCVector> DIRECTION_ROTATE_180 = (v) -> v.rotate180();
    Function<AOCVector, AOCVector> DIRECTION_CONTINUE = (v) -> v;

    record Point3DRef(AOCPoint p, AOCVector v) {}

    record FaceRef(int id, char from, char dir, char to, Function<AOCPoint, AOCPoint> tr,
                   Function<AOCVector, AOCVector> rot) {

        boolean matches(AOCPoint p) {
            int zn = from - 'A';
            int x0 = (zn % 4) * FACE_SIZE;
            int y0 = (zn / 4) * FACE_SIZE;
            int x1 = x0 + FACE_SIZE;
            int y1 = y0 + FACE_SIZE;
            return p.x >= x0 && p.x < x1 && p.y >= y0 && p.y < y1;
        }

        boolean matches(AOCPoint p, AOCVector v) {
            int zn = from - 'A';
            int x0 = (zn % 4) * FACE_SIZE;
            int y0 = (zn / 4) * FACE_SIZE;
            int x1 = x0 + FACE_SIZE;
            int y1 = y0 + FACE_SIZE;
            switch (dir) {
                case 'U' -> {
                    y0 -= FACE_SIZE;
                    y1 -= FACE_SIZE;
                }
                case 'D' -> {
                    y0 += FACE_SIZE;
                    y1 += FACE_SIZE;
                }
                case 'R' -> {
                    x0 += FACE_SIZE;
                    x1 += FACE_SIZE;
                }
                case 'L' -> {
                    x0 -= FACE_SIZE;
                    x1 -= FACE_SIZE;
                }
            }
            if (p.x >= x0 && p.x < x1 && p.y >= y0 && p.y < y1) {
                return switch (dir) {
                    case 'U' -> v.equals(0, -1);
                    case 'D' -> v.equals(0, 1);
                    case 'R' -> v.equals(+1, 0);
                    case 'L' -> v.equals(-1, 0);
                    default -> throw new IllegalStateException();
                };
            }
            return false;
        }

        Point3DRef translate(AOCPoint p, AOCVector v) {
            int zn = to - 'A';
            int x0 = (zn % 4) * FACE_SIZE;
            int y0 = (zn / 4) * FACE_SIZE;
            p = tr.apply(p).traslate(x0, y0);
            v = rot.apply(v);
            return new Point3DRef(p, v);
        }
    }

    /**
     * map: 4x4x4
     *
     *                                   1         1
     *     0---------4---------8---------2---------6
     *   0                     ┌─────────┐
     *   ╵                     │   E ↕   │
     *   ╵      A         B    │FL  C  L↕│    D
     *   ╵                     │         │
     *   4 ┌─────────┬─────────┼─────────┤
     *   ╵ │   C ↕   │   C R   │         │
     *   ╵ │LL  E    │    F    │    G  LR│    H
     *   ╵ │   K ↕   │   K L   │         │
     *   8 └─────────┴─────────┼─────────┼─────────┐
     *   ╵                     │         │   G L   │
     *   ╵      I         J    │FL  K    │    L  C↕│
     *   ╵                     │   E ↕   │   E L   │
     *  12                     └─────────┴─────────┘
     */

    FaceRef[] faceRefsSample = new FaceRef[]{
        // C
        new FaceRef(1, 'C', 'U', 'E', POINT_FLIP, DIRECTION_ROTATE_180),
        new FaceRef(1, 'C', 'L', 'F', POINT_ROTATE_LEFT, DIRECTION_ROTATE_LEFT),
        new FaceRef(1, 'C', 'R', 'L', POINT_FLIP, DIRECTION_ROTATE_180),
        // E
        new FaceRef(2, 'E', 'U', 'C', POINT_FLIP, DIRECTION_ROTATE_180),
        new FaceRef(2, 'E', 'L', 'L', POINT_ROTATE_LEFT, DIRECTION_ROTATE_LEFT),
        new FaceRef(2, 'E', 'D', 'K', POINT_FLIP, DIRECTION_ROTATE_180),
        // F
        new FaceRef(3, 'F', 'U', 'C', POINT_ROTATE_RIGHT, DIRECTION_ROTATE_RIGHT),
        new FaceRef(3, 'F', 'D', 'K', POINT_ROTATE_LEFT, DIRECTION_ROTATE_LEFT),
        // G
        new FaceRef(4, 'G', 'R', 'L', POINT_ROTATE_RIGHT, DIRECTION_ROTATE_RIGHT),
        // M
        new FaceRef(5, 'K', 'L', 'F', POINT_ROTATE_LEFT, DIRECTION_ROTATE_LEFT),
        new FaceRef(5, 'K', 'D', 'E', POINT_FLIP, DIRECTION_ROTATE_180),
        // N
        new FaceRef(6, 'L', 'U', 'G', POINT_ROTATE_LEFT, DIRECTION_ROTATE_LEFT),
        new FaceRef(6, 'L', 'R', 'C', POINT_FLIP, DIRECTION_ROTATE_180),
        new FaceRef(6, 'L', 'D', 'E', POINT_ROTATE_LEFT, DIRECTION_ROTATE_LEFT),
    };

    // Draw this with pencil, paper and scissors, take notes on faces connection and write the FaceRef[] data
    FaceRef[] faceRefsPart2 = new FaceRef[]{
        // B
        new FaceRef(1, 'B', 'L', 'I', POINT_FLIP, DIRECTION_ROTATE_180),
        new FaceRef(1, 'B', 'U', 'M', POINT_ROTATE_RIGHT, DIRECTION_ROTATE_RIGHT),
        // C
        new FaceRef(2, 'C', 'R', 'J', POINT_FLIP, DIRECTION_ROTATE_180),
        new FaceRef(2, 'C', 'D', 'F', POINT_ROTATE_RIGHT, DIRECTION_ROTATE_RIGHT),
        new FaceRef(2, 'C', 'U', 'M', POINT_CONTINUE, DIRECTION_CONTINUE),
        // F
        new FaceRef(3, 'F', 'R', 'C', POINT_ROTATE_LEFT, DIRECTION_ROTATE_LEFT),
        new FaceRef(3, 'F', 'L', 'I', POINT_ROTATE_LEFT, DIRECTION_ROTATE_LEFT),
        // I
        new FaceRef(4, 'I', 'L', 'B', POINT_FLIP, DIRECTION_ROTATE_180),
        new FaceRef(4, 'I', 'U', 'F', POINT_ROTATE_RIGHT, DIRECTION_ROTATE_RIGHT),
        // J
        new FaceRef(5, 'J', 'R', 'C', POINT_FLIP, DIRECTION_ROTATE_180),
        new FaceRef(5, 'J', 'D', 'M', POINT_ROTATE_RIGHT, DIRECTION_ROTATE_RIGHT),
        // M
        new FaceRef(6, 'M', 'R', 'J', POINT_ROTATE_LEFT, DIRECTION_ROTATE_LEFT),
        new FaceRef(6, 'M', 'D', 'C', POINT_CONTINUE, DIRECTION_CONTINUE),
        new FaceRef(6, 'M', 'L', 'B', POINT_ROTATE_LEFT, DIRECTION_ROTATE_LEFT),
    };


    private int evalFacing() {
        int facing = -1;
        if (currentDirection.equals(RIGHT)) {
            facing = 0;
        } else if (currentDirection.equals(DOWN)) {
            facing = 1;
        } else if (currentDirection.equals(LEFT)) {
            facing = 2;
        } else if (currentDirection.equals(UP)) {
            facing = 3;
        }
        if (facing == -1) {
            throw new IllegalStateException();
        }
        return facing;
    }

    boolean next3DMove(Object move) {
        try {
            if (move instanceof String str) {
                switch (str) {
                    case "R" ->
                        currentDirection = currentDirection.rotate90L(); // inverted because the matrix coordinates are inverted
                    case "L" -> currentDirection = currentDirection.rotate90R(); //
                }
                board.set(currentPos, directionChar(currentDirection));
                return true;
            } else if (move instanceof Integer n) {
                for (int i = 0; i < n; ++i) {
                    board.set(currentPos, directionChar(currentDirection));
                    AOCPoint p = currentPos.traslateNew(currentDirection);
                    if (board.isValidCell(p) && board.get(p).equals('#')) {
                        return false;
                    } else if (board.isValidCell(p) && !board.get(p).equals(' ')) {
                        currentPos = p;
                    } else {
                        // search on another cube surface
                        Point3DRef ptRef = translatePoint3DRefForSample(p, currentDirection);
                        if (board.get(ptRef.p).equals('#')) {
                            return false;
                        } else {
                            currentPos = ptRef.p;
                            currentDirection = ptRef.v;
                        }
                    }
                    board.set(currentPos, directionChar(currentDirection));
                }
                return true;
            }
            throw new IllegalStateException();
        } catch (RuntimeException ex) {
            System.err.println("ERROR: " + currentPos + " " + currentDirection);
            board.dumpBoard("%c");
            ex.printStackTrace();
            throw ex;
        }
    }

    Point3DRef translatePoint3DRefForSample(AOCPoint p, AOCVector v) {
        for (FaceRef faceRef : faceRefs) {
            if (faceRef.matches(p, v)) {
                return faceRef.translate(p, v);
            }
        }
        throw new IllegalStateException();
    }

    AOCPoint rotate90Rmod(AOCPoint p) {
        int xm = (FACE_SIZE + p.x) % FACE_SIZE;
        int ym = (FACE_SIZE + p.y) % FACE_SIZE;
        return new AOCPoint(FACE_SIZE - ym - 1, xm);
    }

    AOCPoint rotate90Lmod(AOCPoint p) {
        int xm = (FACE_SIZE + p.x) % FACE_SIZE;
        int ym = (FACE_SIZE + p.y) % FACE_SIZE;
        return new AOCPoint(ym, FACE_SIZE - xm - 1);
    }

    AOCPoint flipmod(AOCPoint p) {
        int xm = (FACE_SIZE + p.x) % FACE_SIZE;
        int ym = (FACE_SIZE + p.y) % FACE_SIZE;
        return new AOCPoint(FACE_SIZE - xm - 1, FACE_SIZE - ym - 1);
    }
}
