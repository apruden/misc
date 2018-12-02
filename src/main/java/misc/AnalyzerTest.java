package misc;

import java.io.InputStream;
import java.io.PrintWriter;

/**
 * usage: java misc.AnalyzerTest <options> [source [objet]]
 */
public class AnalyzerTest extends AbstractMain implements Tokens {

    public void run(Global global, InputStream in, PrintWriter out) {
        Parser parser = new Parser(global, in);
        Tree tree = parser.parse();
        Analyzer analyzer = new Analyzer(global);
        Scope scope = analyzer.createGlobalScope();
        analyzer.analyze(tree, scope);
    }

    public static void main(String[] args) {
        new AnalyzerTest().run(args);
    }
}
