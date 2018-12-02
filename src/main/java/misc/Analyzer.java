package misc;

import misc.Tree.*;

public class Analyzer implements Tree.Visitor, Tokens, Kinds {

    private final Global global;
    private Scope scope;

    public Analyzer(Global global) {
        this.global = global;
        this.scope = null;
    }

    public Scope createGlobalScope() {
        Scope scope = new Scope(global);
        Type intToUnit = new Type.FunType(new Type[]{Type.INT}, Type.UNIT);
        Type toInt = new Type.FunType(new Type[]{}, Type.INT);
        scope.enter(new Symbol(0, "printInt", FUNCTION, intToUnit));
        scope.enter(new Symbol(0, "printChar", FUNCTION, intToUnit));
        scope.enter(new Symbol(0, "readInt", FUNCTION, toInt));
        scope.enter(new Symbol(0, "readChar", FUNCTION, toInt));
        return scope;
    }

    public Type analyze(Tree tree, Scope scope) {
        Scope backup = this.scope;
        this.scope = scope;
        Type type = analyze(tree);
        this.scope = backup;
        return type;
    }

    private Type analyze(Tree tree) {
        if (tree.typ != null) throw new Error("type already assigned.");
        tree.apply(this);
        if (tree.typ == null) throw new Error("type not assigned.");
        return tree.typ;
    }

    private Type[] analyze(Tree[] trees, Scope scope) {
        Type[] types = new Type[trees.length];
        for (int i = 0; i < trees.length; i++)
            types[i] = analyze(trees[i], scope);
        return types;
    }

    private Type[] analyze(Tree[] trees) {
        return analyze(trees, scope);
    }

    private boolean checkIfEqual(int pos, Type expected, Type found, String msg) {
        if (!found.equals(expected)) {
            error(pos, expected, found, msg);
            return false;
        }
        return true;
    }

    private boolean checkIfSubtype(int pos, Type expected, Type found, String msg) {
        if (!found.isSubtypeOf(expected)) {
            error(pos, expected, found, msg);
            return false;
        }
        return true;
    }

    private void error(int pos, Type expected, Type found, String message) {
        error(pos, expected.toString(), found, message);
    }

    private void error(int pos, String expected, Type found, String message) {
        error(pos, message + "\n" +
                "type expected : " + expected + "\n" +
                "type found    : " + found);
    }

    private void error(int pos, String message) {
        global.error(pos, message);
    }


    /**
     * ?(t) = Bad
     * t  = Type.BAD
     */
    public void caseBad(Bad tree) {
        tree.typ = Type.BAD;
    }

    /**
     * P    = Program { D } E(te)
     * Creates the program root scope, and uses it to analyse
     * declarations {D} and the main expression E.
     */
    public void caseProgram(Program tree) {
        Scope scope = new Scope(this.scope);
        analyze(tree.decls, scope);
        analyze(tree.expr, scope);
        tree.typ = Type.NONE;
    }

    /**
     * D    = FunDecl ident { F }({ta}) T(tt) E(te)
     * Analyses arguments {F} in a new scope, nested in the current scope
     * Adds a new symbol in the current scope with name 'ident' and type 'tf'. Analyses then the body E
     * in the scope of the arguments.
     * tf = Type.FunType({ta},tt)
     * te <: tt
     */
    public void caseFunDecl(FunDecl tree) {
        Scope scopeArgs = new Scope(scope);
        Type[] ta = analyze(tree.args, scopeArgs);
        Type tt = analyze(tree.result);
        Type.FunType tf = new Type.FunType(ta, tt);
        tree.sym = new Symbol(tree.pos, tree.name, FUNCTION, tf);
        scope.enter(tree.sym);
        Type te = analyze(tree.body, scopeArgs);
        this.checkIfSubtype(tree.pos, tt, te, "Invalid Type.");
        tree.typ = Type.NONE;
    }

    /**
     * S    = VarDecl ident T(tt) E(te)
     * Analyse expression E in the current scope. Adds a new symbol
     * in the current scope, with name 'ident' and type 'tt'.
     * te <: tt
     */
    public void caseVarDecl(VarDecl tree) {
        Type tt = analyze(tree.type);
        Type te = analyze(tree.value);
        checkIfSubtype(tree.pos, tt, te, " Incompatible types.");
        tree.sym = new Symbol(tree.pos, tree.name, VARIABLE, tt);
        scope.enter(tree.sym);
        tree.typ = Type.NONE;
    }

    /**
     * F(t) = Formal ident T(tt)
     * Adds a new symbol in the current scope, with name 'ident' and type 'tt'.
     * t  = tt
     */
    public void caseFormal(Formal tree) {
        Type tt = analyze(tree.type);
        tree.sym = new Symbol(tree.pos, tree.name, VARIABLE, tt);
        scope.enter(tree.sym);
        tree.typ = tt;
    }

    /**
     * T(t) = UnitType
     * t  = Type.UNIT
     */
    public void caseUnitType(UnitType tree) {
        tree.typ = Type.UNIT;
    }

    /**
     * T(t) = IntType
     * t  = Type.INT
     */
    public void caseIntType(IntType tree) {
        tree.typ = Type.INT;
    }

    /**
     * T(t) = ListType T(tt)
     * t  = Type.ListType(tt)
     */
    public void caseListType(ListType tree) {
        Type tt = analyze(tree.elements);
        tree.typ = new Type.ListType(tt);
    }

    /**
     * T(t) = FunType { T }({ts}) T(tt)
     * t  = Type.FunType({ts},tt)
     */
    public void caseFunType(FunType tree) {
        Type[] ts = analyze(tree.args);
        Type tt = analyze(tree.result);
        tree.typ = new Type.FunType(ts, tt);
    }

    /**
     * S    = Exec E(te)
     */
    public void caseExec(Exec tree) {
        Type te = analyze(tree);
        tree.typ = Type.NONE;
    }

    /**
     * S    = While E(tc) E(te)
     * tc = Type.INT
     */
    public void caseWhile(While tree) {
        Type tc = analyze(tree.cond);
        this.checkIfEqual(tree.pos, Type.INT, tc, " Incompatible types.");
        Type te = analyze(tree.body);
        tree.typ = Type.NONE;
    }

    /**
     * E(t) = If E(tc) E(t1) E(t2)
     * tc = Type.INT
     * t1 <: t2 || t2 <: t1
     * t  = max(t1,t2)
     */
    public void caseIf(If tree) {
        Type tc = analyze(tree.cond);
        Type t1 = analyze(tree.thenp);
        Type t2 = analyze(tree.elsep);
        this.checkIfEqual(tree.pos, Type.INT, tc, " Incompatible types.");
        if (!Type.areComparable(t1, t2)) error(tree.pos, "THEN and ELSE blocks must be of comparable types.");
        tree.typ = Type.max(t1, t2);
    }

    /**
     * E(t) = Assign ident E(t1)
     * sym= lookup(ident)
     * sym.isVariable()
     * t1 <: sym.type
     * t  = Type.UNIT
     */
    public void caseAssign(Assign tree) {
        Symbol sym = scope.lookup(tree.name);
        if (sym == null) {
            error(tree.pos, tree.name + " not defined.");
        } else {
            if (sym.isVariable()) {
                tree.sym = sym;
                Type t1 = analyze(tree.value);
                this.checkIfSubtype(tree.pos, sym.type, t1, " Invalid affectation type");
            } else {
                error(tree.pos, "Invalid affectation.");
            }
        }
        tree.typ = Type.UNIT;
    }

    /**
     * E(t) = Ident ident
     * sym= lookup(ident)
     * t  = sym.type
     */
    public void caseIdent(Ident tree) {
        Symbol sym = scope.lookup(tree.name);
        if (sym == null) {
            error(tree.pos, tree.name + " not defined.");
            tree.typ = Type.BAD;
        } else {
            tree.sym = sym;
            tree.typ = sym.type;
        }
    }

    /**
     * E(t) = UnitLit
     * t  = Type.UNIT
     */
    public void caseUnitLit(UnitLit tree) {
        tree.typ = Type.UNIT;
    }

    /**
     * E(t) = IntLit int
     * t  = Type.INT
     */
    public void caseIntLit(IntLit tree) {
        tree.typ = Type.INT;
    }

    /**
     * E(t) = NilLit
     * t  = Type.ListType(Type.BOTTOM)
     */
    public void caseNilLit(NilLit tree) {
        tree.typ = new Type.ListType(Type.BOTTOM);
    }

    /**
     * E(t) = Operation ( Eq | Ne | Lt | Le | Gt | Ge ) E(t1) E(t2)
     * t1 = Type.INT
     * t2 = Type.INT
     * t  = Type.INT
     * <p>
     * E(t) = Operation ( Add | Sub | Mul | Div | Mod ) E(t1) E(t2)
     * t1 = Type.INT
     * t2 = Type.INT
     * t  = Type.INT
     * <p>
     * E(t) = Operation Cons E(t1) E(t2)
     * t2 <: Type.ListType(t1) || Type.ListType(t1) <: t2
     * t  = max(Type.ListType(t1),t2)
     * <p>
     * E(t) = Operation Head E(t1)
     * t1 = Type.ListType(t2)
     * t  = t2
     * <p>
     * E(t) = Operation Tail E(t1)
     * t1 = Type.ListType(t2)
     * t  = t1
     * <p>
     * E(t) = Operation IsEmpty E(t1)
     * t1 = Type.ListType(t2)
     * t  = Type.INT
     */
    public void caseOperation(Operation tree) {
        Type t1 = null;
        Type t2 = null;
        switch (tree.operator) {
            case EQU:
            case NOTEQ:
            case LESS:
            case LESSOREQ:
            case GREATER:
            case GREATEROREQ:
                t1 = analyze(tree.left);
                this.checkIfEqual(tree.left.pos, Type.INT, t1, "Invalid operand type");
                t2 = analyze(tree.right);
                this.checkIfEqual(tree.right.pos, Type.INT, t2, "Invalid operand type");
                tree.typ = Type.INT;
                break;
            case PLUS:
            case MINUS:
            case MUL:
            case DIV:
            case MOD:
                t1 = analyze(tree.left);
                this.checkIfEqual(tree.left.pos, Type.INT, t1, "Invalid operand type");
                t2 = analyze(tree.right);
                this.checkIfEqual(tree.right.pos, Type.INT, t2, "Invalid operand type");
                tree.typ = Type.INT;
                break;
            case CONS:
                t1 = analyze(tree.left);
                t2 = analyze(tree.right);
                Type.ListType t = new Type.ListType(t1);
                if (!Type.areComparable(t, t2)) {
                    error(tree.pos, "Incompatible list types");
                    tree.typ = Type.BAD;
                } else {
                    tree.typ = Type.max(t, t2);
                }
                break;
            case HEAD:
                t1 = analyze(tree.left);
                if (t1 instanceof Type.ListType) {
                    tree.typ = ((Type.ListType) t1).type;
                } else {
                    error(tree.left.pos, " Invalid operand type.");
                    tree.typ = Type.BAD;
                }
                break;
            case TAIL:
                t1 = analyze(tree.left);
                if (t1 instanceof Type.ListType) {
                    tree.typ = t1;
                } else {
                    error(tree.left.pos, " Invalid operand type.");
                    tree.typ = Type.BAD;
                }
                break;
            case ISEMPTY:
                t1 = analyze(tree.left);
                if (!(t1 instanceof Type.ListType)) {
                    error(tree.left.pos, " Invalid operand type.");
                }
                tree.typ = Type.INT;
                break;
        }
    }

    /**
     * E(t) = FunCall E(tf) { E }({ta}).
     * tf = Type.FunType({ts},tt)
     * {ts}.length = {ta}.length
     * for i in 1..{ts}.length
     * {ta}[i] <: {ts}[i]
     * t=tt
     */
    public void caseFunCall(FunCall tree) {
        Type tf = analyze(tree.expression);
        Type[] ta = analyze(tree.args);
        if (tf instanceof Type.FunType) {
            Type.FunType funtype = (Type.FunType) tf;
            Type tt = funtype.type;
            if (funtype.args.length == ta.length) {
                for (int i = 0; i < ta.length; i++) {
                    if (!this.checkIfSubtype(tree.pos, funtype.args[i], ta[i], " Invalid argument type")) {
                        tree.typ = Type.BAD;
                    }
                }
                if (tree.typ == null) {
                    tree.typ = tt;
                }
            } else {
                error(tree.pos, " Invalid arguments length in function call.");
                tree.typ = Type.BAD;
            }
        } else {
            error(tree.pos, " Invalid function call.");
            tree.typ = Type.BAD;
        }
    }

    /**
     * E(t) = Block { S } E(t1)
     * Analyse {S} and E in a new scope, nested in the current scope.
     * t = t1
     */
    public void caseBlock(Block tree) {
        Scope scopeBlock = new Scope(scope);
        analyze(tree.statements, scopeBlock);
        Type t1 = analyze(tree.expression, scopeBlock);
        tree.typ = t1;
    }
}
