package misc;

/**
 * Encodes the position in a source file (line & column) with one integer.
 */
public final class Position {
    private static final int columnBits = 12;

    private static final int columnMask = (1 << columnBits) - 1;

    public static final int UNDEFINED = 0;

    public static final int FIRST = (1 << columnBits) | 1;

    public static int encode(int line, int col) {
        return (line << columnBits) | (col & columnMask);
    }

    public static int line(int pos) {
        return pos >>> columnBits;
    }

    public static int column(int pos) {
        return pos & columnMask;
    }
}
