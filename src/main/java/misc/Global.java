package misc;

import java.io.*;
import java.util.HashSet;

public class Global {

    public static Global instance;

    public final String source;

    public final boolean debug;

    private int errorCounter;

    private final HashSet<Integer> errorPositions;

    private final Error abort;

    public Global(AbstractMain main) {
        if (instance != null) throw new Error("second instance of Global");
        instance = this;
        this.source = main.source;
        this.debug = main.debug;
        this.abort = main.abort;
        this.errorCounter = 0;
        this.errorPositions = new HashSet<>();
    }

    public void debug(String message) {
        if (debug) System.err.println("[debug] " + message);
    }

    public void debug(int position, String message) {
        if (debug) print(position, "[debug] " + message);
    }

    public int errors() {
        return errorCounter;
    }

    public void error(String message) {
        error(Position.UNDEFINED, message);
    }

    public void error(int position, String message) {
        Integer key = position;
        if (position == Position.UNDEFINED) {
            errorCounter++;
            print(position, message);
        } else if (!errorPositions.contains(key)) {
            errorCounter++;
            print(position, message);
            errorPositions.add(key);
        } else if (debug) {
            errorCounter++;
            print(position, message + " [debug]");
        }
    }

    public Error fatal(String message) {
        return fatal(Position.UNDEFINED, message);
    }

    public Error fatal(int position, String message) {
        errorCounter++;
        print(position, message);
        throw abort;
    }

    public Error fatal(Exception exception) {
        return fatal(Position.UNDEFINED, exception);
    }

    public Error fatal(int position, Exception exception) {
        String message = exception.getMessage();
        if (message == null)
            message = "Fatal error" + "(" + exception.getClass() + ")";
        fatal(position, message);
        throw abort;
    }

    private void print(int position, String message) {
        if (source != null && !source.equals("-")) {
            System.out.print(source);
            System.out.print(":");
        }
        System.err.print(Position.line(position));
        System.err.print(":");
        System.err.print(Position.column(position));
        System.err.print(": ");
        System.err.print(message);
        System.err.println();
    }

    public InputStream openInput(String filename) {
        if (filename == null || filename.equals("-")) return System.in;

        try {
            return new FileInputStream(filename);
        } catch (FileNotFoundException exception) {
            throw fatal(0, "Cannot open file " + filename);
        }
    }

    public OutputStream openOutput(String filename) {
        if (filename == null || filename.equals("-")) return System.out;
        try {
            return new FileOutputStream(filename);
        } catch (FileNotFoundException exception) {
            throw fatal(0, "Cannot open file " + filename);
        }
    }

    public void close(InputStream in) {
        try {
            in.close();
        } catch (IOException exception) {
            fatal(0, exception);
        }
    }

    public void close(OutputStream in) {
        try {
            in.close();
        } catch (IOException exception) {
            fatal(0, exception);
        }
    }
}

