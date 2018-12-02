package misc;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;

abstract class AbstractMain {

    public final Error abort = new Error();

    public String source = null;

    public String object = null;

    public boolean debug = false;

    public abstract void run(Global global, InputStream in, PrintWriter out);

    public void run(String[] args) {
        Global global = null;
        try {
            handle(args);
            global = global();
            run(global);
        } catch (Error exception) {
            if (exception != abort) throw exception;
        }
        if (global != null) {
            int errors = global.errors();
            if (errors > 0) {
                System.err.println();
                System.err.print(errors + " error");
                if (errors > 1) System.err.print("s");
                System.err.println();
            }
        }
    }

    public void run(Global global) {
        InputStream in = global.openInput(source);
        OutputStream out = global.openOutput(object);
        PrintWriter writer = new PrintWriter(out, true);
        run(global, in, writer);
        writer.close();
        global.close(out);
        global.close(in);
    }

    public Global global() {
        return new Global(this);
    }

    public Error abort(String message) {
        System.err.println("error : " + message);
        printUsage(System.err);
        throw abort;
    }

    public void handle(String[] args) {
        for (int i = 0; i < args.length; ) {
            if (args[i].startsWith("-") && args[i].length() > 1) {
                i += handleOption(args, i);
            } else {
                handleFile(args[i]);
                i += 1;
            }
        }
    }

    public int handleOption(String[] args, int i) {
        if (args[i].equals("-debug")) {
            debug = true;
            return 1;
        } else if (args[i].equals("-?") || args[i].equals("-help")) {
            printHelp(System.out);
            throw abort;
        } else {
            throw abort("unknown option " + args[i]);
        }
    }

    public void handleFile(String filename) {
        if (source == null) {
            source = filename;
        } else if (object == null) {
            object = filename;
        } else {
            throw abort("invalid argument " + filename);
        }
    }

    public void printUsage(PrintStream out) {
        out.println("usage: java " + getClass().getName() +
                " <options> [source [object]]");
    }

    public void printHelp(PrintStream out) {
        printUsage(out);
        out.println("options :");
        out.println("  -debug    Prints debug messages");
        out.println("  -? -help  Prints help");
    }
}
