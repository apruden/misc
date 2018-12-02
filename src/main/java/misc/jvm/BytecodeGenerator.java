package misc.jvm;

import misc.*;
import misc.Tree.*;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.io.FileOutputStream;
import java.util.LinkedList;

import static org.objectweb.asm.Opcodes.*;

public class BytecodeGenerator implements Visitor {
    private final Global global;
    private Scope initialScope;
    private ClassWriter cw = new ClassWriter(0);
    private MethodVisitor mv = null;
    private int variableCounter = 1;
    private String ident = null;

    public BytecodeGenerator(Global global) {
        this.global = global;
    }

    public void generate(Scope scope, Tree tree) {
        initialScope = scope;
        tree.apply(this);
    }

    private void generate(Tree tree) {
        tree.apply(this);
    }

    private void generate(Tree[] trees) {
        for (Tree tree : trees) generate(tree);
    }

    public void caseBad(Bad tree) {
        throw new Error("Invalid node");
    }

    public void caseProgram(Program tree) {
        cw.visit(V1_8, ACC_PUBLIC + ACC_SUPER, "HelloGen", null, "java/lang/Object", null);

        //default constructor
        mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0); //load the first local variable: this
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        mv.visitInsn(RETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();

        // predef functions

        //printInt(Int)
        mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "printInt", "(I)V", null, null);
        mv.visitCode();
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitVarInsn(ILOAD, 1);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "print", "(I)V", false);
        mv.visitInsn(IRETURN);
        mv.visitMaxs(2, 1);
        mv.visitEnd();

        //printChar(Int)
        mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "printChar", "(I)V", null, null);
        mv.visitCode();
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitVarInsn(ILOAD, 1);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "print", "(I)V", false);
        mv.visitInsn(IRETURN);
        mv.visitMaxs(2, 1);
        mv.visitEnd();

        // generate func
        generate(tree.decls);

        //main method
        mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
        mv.visitCode();

        // generate main expression
        generate(tree.expr);

        mv.visitInsn(RETURN);
        mv.visitMaxs(2, 1);
        mv.visitEnd();


        cw.visit(V1_8, ACC_PUBLIC + ACC_INTERFACE + ACC_ABSTRACT, "Function1", null, "java/lang/Object", null);
        mv = cw.visitMethod(ACC_PUBLIC, "apply", "(Ljava/lang/Object;)Ljava/lang/Object;", null, null);
        mv.visitEnd();
        cw.visitEnd();

        cw.visit(V1_8, ACC_PUBLIC + ACC_INTERFACE + ACC_ABSTRACT, "Function2", null, "java/lang/Object", null);
        mv = cw.visitMethod(ACC_PUBLIC, "apply", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", null, null);
        mv.visitEnd();
        cw.visitEnd();

        cw.visit(V1_8, ACC_PUBLIC + ACC_INTERFACE + ACC_ABSTRACT, "Function3", null, "java/lang/Object", null);
        mv = cw.visitMethod(ACC_PUBLIC, "apply", "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", null, null);
        mv.visitEnd();
        cw.visitEnd();

        cw.visit(V1_8, ACC_PUBLIC + ACC_INTERFACE + ACC_ABSTRACT, "Function4", null, "java/lang/Object", null);
        mv = cw.visitMethod(ACC_PUBLIC, "apply", "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", null, null);
        mv.visitEnd();
        cw.visitEnd();

        cw.visitEnd();

        //save bytecode into disk
        try (FileOutputStream out = new FileOutputStream("HelloGen.class")) {
            out.write(cw.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void caseFunDecl(FunDecl tree) {
        initialScope.enter(tree.sym);
        for (Tree arg : tree.args) generate(arg);
        mv = cw.visitMethod(ACC_STATIC + ACC_PUBLIC, tree.name, "", null, null);
        mv.visitCode();
        generate(tree.body);
        mv.visitInsn(IRETURN);
        mv.visitEnd();
    }

    public void caseVarDecl(VarDecl tree) {
        initialScope.enter(tree.sym);
        generate(tree.value);
        mv.visitVarInsn(ASTORE, variableCounter);
    }

    public void caseFunCall(FunCall tree) {
        for (Tree arg : tree.args) generate(arg);
        generate(tree.expression);
        mv.visitMethodInsn(INVOKESTATIC, "Hello", ident, "(I)V", false);
    }

    //func args
    public void caseFormal(Formal tree) {
    }

    public void caseExec(Exec tree) {
        generate(tree.expr);
    }

    public void caseWhile(While tree) {
        Label startLabel = new Label();
        Label endLabel = new Label();

        mv.visitLabel(startLabel);
        generate(tree.cond);
        mv.visitJumpInsn(IFEQ, endLabel);
        generate(tree.body);
        mv.visitJumpInsn(GOTO, startLabel);
        mv.visitLabel(endLabel);
    }

    public void caseIf(If tree) {
        Label trueLabel = new Label();
        Label endLabel = new Label();
        generate(tree.cond);
        mv.visitJumpInsn(IFEQ, endLabel);
        generate(tree.elsep);
        mv.visitLabel(trueLabel);
        generate(tree.thenp);
        mv.visitLabel(endLabel);
    }

    public void caseAssign(Assign tree) {
        generate(tree.value);
        mv.visitVarInsn(ASTORE, variableCounter);
        variableCounter++;
    }

    public void caseIdent(Ident tree) {
        switch (tree.sym.kind) {
            case Kinds.FUNCTION:
                ident = tree.name;
                break;
            case Kinds.VARIABLE:
                ident = tree.name;
                break;
        }
    }

    public void caseBlock(Block tree) {
        for (Tree statement : tree.statements) {
            generate(statement);
        }

        generate(tree.expression);
    }

    public void caseUnitLit(UnitLit tree) {
    }

    public void caseIntLit(IntLit tree) {
        mv.visitLdcInsn(tree.value);
    }

    public void caseNilLit(NilLit tree) {
        mv.visitLdcInsn(new LinkedList<>());
    }

    public void caseOperation(Operation tree) {
        switch (tree.operator) {
            case Tokens.PLUS:
                generate(tree.left);
                generate(tree.right);
                mv.visitVarInsn(ILOAD, 1);
                mv.visitVarInsn(ILOAD, 2);
                mv.visitInsn(IADD);
                break;

            case Tokens.MINUS:
                generate(tree.left);
                generate(tree.right);
                mv.visitVarInsn(ILOAD, 1);
                mv.visitVarInsn(ILOAD, 2);
                mv.visitInsn(ISUB);
                break;

            case Tokens.MUL:
                generate(tree.left);
                generate(tree.right);
                mv.visitVarInsn(ILOAD, 1);
                mv.visitVarInsn(ILOAD, 2);
                mv.visitInsn(IMUL);
                break;

            case Tokens.DIV:
                generate(tree.left);
                generate(tree.right);
                mv.visitVarInsn(ILOAD, 1);
                mv.visitVarInsn(ILOAD, 2);
                mv.visitInsn(IDIV);
                break;

            case Tokens.MOD:
                generate(tree.left);
                generate(tree.right);
                mv.visitVarInsn(ILOAD, 1);
                mv.visitVarInsn(ILOAD, 2);
                mv.visitInsn(IREM);
                break;

            case Tokens.EQU:
                break;

            case Tokens.NOTEQ:
                break;

            case Tokens.LESS:
                break;

            case Tokens.LESSOREQ:
                break;

            case Tokens.GREATER:
                break;

            case Tokens.GREATEROREQ:
                break;

            case Tokens.CONS:
                generate(tree.left);
                generate(tree.right);
                mv.visitVarInsn(ILOAD, 1);
                mv.visitVarInsn(ILOAD, 2);
                break;

            case Tokens.HEAD:
                generate(tree.left);
                mv.visitVarInsn(ILOAD, 1);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/LinkedList", "peekFirst", "()Ljava/lang/Object", false);
                break;

            case Tokens.TAIL:
                generate(tree.left);
                mv.visitVarInsn(ILOAD, 1);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/LinkedList", "subList", "(II)Ljava/util/LinkedList", false);
                break;

            case Tokens.ISEMPTY:
                generate(tree.left);
                mv.visitVarInsn(ILOAD, 1);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/LinkedList", "isEmpty", "()I", false);
                break;
        }
    }

    //no type checking

    public void caseUnitType(UnitType tree) {
    }

    public void caseIntType(IntType tree) {
    }

    public void caseListType(ListType tree) {
    }

    public void caseFunType(FunType tree) {
    }

    private String symbolToDescriptor(Symbol sym) {
        if (sym.isFunction())
            return "";
        else
            return "";
    }
}
