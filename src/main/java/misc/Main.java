package misc;

/**
 * usage: java misc.Main <options> [source [objet]]
 * options:
 * -debug    Prints debug messages
 * -? -help  Prints help
 */
public class Main extends GeneratorTest {

    public static final String RISC = "risc";

    public void run(Global global) {
        if (source != null && object == null) {
            int dot = source.lastIndexOf('.');
            object = (dot < 0 ? source : source.substring(0, dot)) + ".risc";
        }
        super.run(global);
    }

    public static void main(String[] args) {
        new Main().run(args);
    }
}
