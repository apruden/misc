package misc;

import java.io.InputStream;
import java.io.PrintWriter;

/**
 * usage: java misc.ParserTest <options> [source [objet]]
 */
public class ParserTest extends AbstractMain implements Tokens {

    public void run(Global global, InputStream in, PrintWriter out) {
        Parser parser = new Parser(global, in);
        parser.parse();
    }

    public static void main(String[] args) {
        new ParserTest().run(args);
    }

}
