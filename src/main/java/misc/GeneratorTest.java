package misc;

import misc.risc.Code;
import misc.risc.Generator;

import java.io.InputStream;
import java.io.PrintWriter;

/**
 * usage: java misc.GeneratorTest <options> [source [objet]]
 * options :
 * -debug    Print debug messages
 * -? -help  Print help
 */
public class GeneratorTest extends AbstractMain {

    public void run(Global global, InputStream in, PrintWriter out) {
        Parser parser = new Parser(global, in);
        Tree tree = parser.parse();
        Analyzer analyzer = new Analyzer(global);
        Scope scope = analyzer.createGlobalScope();
        analyzer.analyze(tree, scope);
        if (global.errors() > 0) return;
        Generator generator = new Generator(global);
        Code code = generator.generate(scope, tree);
        code.write(out);
    }

    public static void main(String[] args) {
        new GeneratorTest().run(args);
    }
}
