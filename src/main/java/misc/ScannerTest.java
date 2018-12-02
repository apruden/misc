package misc;

import java.io.InputStream;
import java.io.PrintWriter;

/**
 * usage: java misc.ScannerTest <options> [source [objet]]
 */
class ScannerTest extends AbstractMain implements Tokens {

    public void run(Global global, InputStream in, PrintWriter out) {
        Scanner scanner = new Scanner(global, in);
        while (scanner.token != EOF) {
            out.println(scanner.representation());
            scanner.nextToken();
        }
    }

    public static void main(String[] args) {
        new ScannerTest().run(args);
    }

}
