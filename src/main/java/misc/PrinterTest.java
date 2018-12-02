package misc;

import java.io.InputStream;
import java.io.PrintWriter;

/**
 * usage: java misc.PrinterTest <options> [source [objet]]
 */
public class PrinterTest extends AbstractMain implements Tokens {

    public void run(Global global, InputStream in, PrintWriter out) {
        Parser parser = new Parser(global, in);
        Printer printer = new Printer(out);
        printer.print(parser.parse()).println();
    }

    public static void main(String[] args) {
        new PrinterTest().run(args);
    }
}
