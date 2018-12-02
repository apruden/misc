package misc;

import java.util.List;

/**
 * AST
 */
public abstract class Tree {

    public static Tree[] toArray(List list) {
        return (Tree[]) list.toArray(new Tree[list.size()]);
    }

    public final int pos;

    public Type typ;

    public Tree(int pos) {
        this.pos = pos;
    }


    public abstract void apply(Visitor v);

    public static class Bad extends Tree {

        public Bad(int pos) {
            super(pos);
        }

        public void apply(Visitor v) {
            v.caseBad(this);
        }
    }

    /**
     * Program { D } E
     */
    public static class Program extends Tree {
        public final Tree[] decls;
        public final Tree expr;

        public Program(int pos, Tree[] decls, Tree expr) {
            super(pos);
            this.decls = decls;
            this.expr = expr;
        }

        public void apply(Visitor v) {
            v.caseProgram(this);
        }
    }

    /**
     * FunDecl ident { F } T E
     */
    public static class FunDecl extends Tree {
        public final String name;
        public final Tree[] args;
        public final Tree result;
        public final Tree body;
        public Symbol sym;

        public FunDecl(int pos, String name, Tree[] args, Tree result, Tree body) {
            super(pos);
            this.name = name;
            this.args = args;
            this.result = result;
            this.body = body;
        }

        public void apply(Visitor v) {
            v.caseFunDecl(this);
        }
    }

    /**
     * VarDecl ident T E
     */
    public static class VarDecl extends Tree {
        public final String name;
        public final Tree type;
        public final Tree value;
        public Symbol sym;

        public VarDecl(int pos, String name, Tree type, Tree value) {
            super(pos);
            this.name = name;
            this.type = type;
            this.value = value;
        }

        public void apply(Visitor v) {
            v.caseVarDecl(this);
        }
    }

    /**
     * Formal ident T
     */
    public static class Formal extends Tree {
        public final String name;
        public final Tree type;
        public Symbol sym;

        public Formal(int pos, String name, Tree type) {
            super(pos);
            this.name = name;
            this.type = type;

        }

        public void apply(Visitor v) {
            v.caseFormal(this);
        }
    }

    /**
     * UnitType
     */
    public static class UnitType extends Tree {

        public UnitType(int pos) {
            super(pos);
        }

        public void apply(Visitor v) {
            v.caseUnitType(this);
        }
    }

    /**
     * IntType
     */
    public static class IntType extends Tree {
        public IntType(int pos) {
            super(pos);
        }

        public void apply(Visitor v) {
            v.caseIntType(this);
        }
    }

    /**
     * ListType T
     */
    public static class ListType extends Tree {
        public final Tree elements;

        public ListType(int pos, Tree elements) {
            super(pos);
            this.elements = elements;
        }

        public void apply(Visitor v) {
            v.caseListType(this);
        }
    }

    /**
     * FunType { T } T
     */
    public static class FunType extends Tree {
        public final Tree[] args;
        public final Tree result;

        public FunType(int pos, Tree[] args, Tree result) {
            super(pos);
            this.args = args;
            this.result = result;
        }

        public void apply(Visitor v) {
            v.caseFunType(this);
        }
    }

    /**
     * Exec E
     */
    public static class Exec extends Tree {
        public final Tree expr;

        public Exec(int pos, Tree expr) {
            super(pos);
            this.expr = expr;
        }

        public void apply(Visitor v) {
            v.caseExec(this);
        }
    }

    /**
     * While E E
     */
    public static class While extends Tree {
        public final Tree cond;
        public final Tree body;

        public While(int pos, Tree cond, Tree body) {
            super(pos);
            this.cond = cond;
            this.body = body;
        }

        public void apply(Visitor v) {
            v.caseWhile(this);
        }
    }

    /**
     * If E E E
     */
    public static class If extends Tree {
        public final Tree cond;
        public final Tree thenp;
        public final Tree elsep;

        public If(int pos, Tree cond, Tree thenp, Tree elsep) {
            super(pos);
            this.cond = cond;
            this.thenp = thenp;
            this.elsep = elsep;
        }

        public void apply(Visitor v) {
            v.caseIf(this);
        }
    }

    /**
     * Assign ident E
     */
    public static class Assign extends Tree {
        public final String name;
        public final Tree value;
        public Symbol sym;

        public Assign(int pos, String name, Tree value) {
            super(pos);
            this.name = name;
            this.value = value;
        }

        public void apply(Visitor v) {
            v.caseAssign(this);
        }
    }

    /**
     * Ident ident
     */
    public static class Ident extends Tree {
        public final String name;
        public Symbol sym;

        public Ident(int pos, String name) {
            super(pos);
            this.name = name;
        }

        public void apply(Visitor v) {
            v.caseIdent(this);
        }
    }

    /**
     * UnitLit
     */
    public static class UnitLit extends Tree {
        public UnitLit(int pos) {
            super(pos);
        }

        public void apply(Visitor v) {
            v.caseUnitLit(this);
        }
    }

    /**
     * IntLit int
     */
    public static class IntLit extends Tree {
        public final int value;

        public IntLit(int pos, int value) {
            super(pos);
            this.value = value;
        }

        public void apply(Visitor v) {
            v.caseIntLit(this);
        }
    }

    /**
     * NilLit
     */
    public static class NilLit extends Tree {
        public NilLit(int pos) {
            super(pos);
        }

        public void apply(Visitor v) {
            v.caseNilLit(this);
        }
    }

    /**
     * Operation O E [ E ]
     */
    public static class Operation extends Tree {
        public final int operator;
        public final Tree left;
        public final Tree right;

        public Operation(int pos, int operator, Tree left, Tree right) {
            super(pos);
            this.operator = operator;
            this.left = left;
            this.right = right;
        }

        public void apply(Visitor v) {
            v.caseOperation(this);
        }
    }

    /**
     * FunCall E { E }
     */
    public static class FunCall extends Tree {
        public final Tree expression;
        public final Tree[] args;

        public FunCall(int pos, Tree expression, Tree[] args) {
            super(pos);
            this.expression = expression;
            this.args = args;
        }

        public void apply(Visitor v) {
            v.caseFunCall(this);
        }
    }

    /**
     * Block { S } E
     */
    public static class Block extends Tree {
        public final Tree[] statements;
        public final Tree expression;

        public Block(int pos, Tree[] statements, Tree expression) {
            super(pos);
            this.statements = statements;
            this.expression = expression;
        }

        public void apply(Visitor v) {
            v.caseBlock(this);
        }
    }


    public interface Visitor {
        void caseBad(Bad tree);

        void caseProgram(Program tree);

        void caseFunDecl(FunDecl tree);

        void caseVarDecl(VarDecl tree);

        void caseFormal(Formal tree);

        void caseUnitType(UnitType tree);

        void caseIntType(IntType tree);

        void caseListType(ListType tree);

        void caseFunType(FunType tree);

        void caseExec(Exec tree);

        void caseWhile(While tree);

        void caseIf(If tree);

        void caseAssign(Assign tree);

        void caseIdent(Ident tree);

        void caseUnitLit(UnitLit tree);

        void caseIntLit(IntLit tree);

        void caseNilLit(NilLit tree);

        void caseOperation(Operation tree);

        void caseFunCall(FunCall tree);

        void caseBlock(Block tree);
    }
}
