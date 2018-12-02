package misc;

import misc.Tree.*;

import java.io.PrintWriter;

public class Printer implements Visitor {

    private final PrintWriter out;

    private String step;

    private int level;

    public Printer(PrintWriter out) {
        this.out = out;
        this.step = "  ";
        this.level = 0;
    }

    public Printer print(Tree tree) {
        tree.apply(this);
        return this;
    }

    public Printer print(Tree[] trees) {
        for (int i = 0; i < trees.length; i++) {
            if (i > 0) print(", ");
            print(trees[i]);
        }

        return this;
    }

    public Printer print(int value) {
        return print(String.valueOf(value));
    }

    public Printer print(String value) {
        out.print(value);
        return this;
    }

    public Printer println() {
        out.println();
        for (int i = 0; i < level; i++) out.print(step);
        return this;
    }

    public Printer indent() {
        level++;
        return this;
    }

    public Printer undent() {
        level--;
        return this;
    }

    public void caseBad(Bad tree) {
        print("<<bad>>");
    }

    /**
     * Program { D } E
     */
    public void caseProgram(Program tree) {
        for (int i = 0; i < tree.decls.length; i++) {
            if (i > 0) println();
            print(tree.decls[i]).print(";");
            println();
        }
        print(tree.expr);
    }

    /**
     * FunDecl ident { F } T E
     */
    public void caseFunDecl(FunDecl tree) {
        print("def ").print(tree.name).print("(").print(tree.args).print(")").
                print(": ").print(tree.result).print(" =");
        indent();
        println().print(tree.body);
        undent();
    }

    /**
     * VarDecl ident T E
     */
    public void caseVarDecl(VarDecl tree) {
        print("var ").print(tree.name).print(": ").print(tree.type).
                print(" = ").indent().print(tree.value).undent();
    }

    /**
     * Formal ident T
     */
    public void caseFormal(Formal tree) {
        print(tree.name).print(": ").print(tree.type);
    }

    /**
     * UnitType
     */
    public void caseUnitType(UnitType tree) {
        print("Unit");
    }

    /**
     * IntType
     */
    public void caseIntType(IntType tree) {
        print("Int");
    }

    /**
     * ListType T
     */
    public void caseListType(ListType tree) {
        print("List[").print(tree.elements).print("]");
    }

    /**
     * FunType { T } T
     */
    public void caseFunType(FunType tree) {
        print("(").print(tree.args).print(") ").print(tree.result);
    }

    /**
     * Exec E
     */
    public void caseExec(Exec tree) {
        print(tree.expr);
    }

    /**
     * While E E
     */
    public void caseWhile(While tree) {
        print("while (").indent().print(tree.cond).undent().print(")");
        indent();
        println().print(tree.body);
        undent();
    }

    /**
     * If E E E
     */
    public void caseIf(If tree) {
        print("if (").indent().print(tree.cond).undent().print(")");
        indent();
        println().print(tree.thenp);
        undent();
        println().print("else");
        indent();
        println().print(tree.elsep);
        undent();
    }

    /**
     * Assign ident E
     */
    public void caseAssign(Assign tree) {
        print(tree.name).print(" = ").indent().print(tree.value).undent();
    }

    /**
     * Ident ident
     */
    public void caseIdent(Ident tree) {
        print(tree.name);
    }

    /**
     * UnitLit
     */
    public void caseUnitLit(UnitLit tree) {
        print("()");
    }

    /**
     * IntLit int
     */
    public void caseIntLit(IntLit tree) {
        print(tree.value);
    }

    /**
     * NilLit
     */
    public void caseNilLit(NilLit tree) {
        print("[]");
    }

    /**
     * Operation O E [ E ]
     */
    public void caseOperation(Operation tree) {
        String operator = Scanner.tokenClass(tree.operator);
        if (tree.right == null) {
            print(operator);
            print("(").indent().print(tree.left).undent().print(")");
        } else {
            print("(").indent().print(tree.left).undent().print(")");
            print(" ").print(operator).print(" ");
            print("(").indent().print(tree.right).undent().print(")");
        }
    }

    /**
     * Block { S } E
     */
    public void caseBlock(Block tree) {
        print("{").indent();
        for (int i = 0; i < tree.statements.length; i++) {
            println().print(tree.statements[i]).print(";");
        }
        println().print(tree.expression).undent();
        println().print("}");
    }

    /**
     * FunCall E { E }
     */
    public void caseFunCall(FunCall tree) {
        print(tree.expression);
        print("(").print(tree.args).print(")");
    }
}
