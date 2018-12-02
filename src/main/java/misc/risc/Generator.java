package misc.risc;

import misc.*;
import misc.Tree.*;
import misc.risc.Item.CondItem;
import misc.risc.Item.ImmediateItem;
import misc.risc.Item.RegisterItem;
import misc.risc.Item.StackItem;


public class Generator implements Visitor, RISC {

    private final Global global;

    private Code code;

    private Scope initialScope;

    private Item item;

    public Generator(Global global) {
        this.global = global;
    }

    public Code generate(Scope scope, Tree tree) {
        code = new Code(global);
        initialScope = scope;
        tree.apply(this);
        if (code.rc() != Code.RC_MIN) throw new Error(
                "registers have not been freed: rc = " + code.rc());
        return code;
    }

    private Item generate(Tree tree) {
        Item backup = item;
        item = null;
        tree.apply(this);
        Item result = item;
        item = backup;
        return result;
    }

    private void generate(Tree[] trees) {
        for (int i = 0; i < trees.length; i++) generate(trees[i]);
    }

    public void caseBad(Bad tree) {
        throw new Error("invalid node");
    }

    public void caseProgram(Program tree) {
        // init stack pointer
        code.emit(SYSCALL, SP, 0, SYS_GET_TOTAL_MEM_SIZE);

        // init GC
        code.emit(DIVI, 1, SP, 4);
        code.emit(DIVI, 2, 1, 2);
        code.emit(ADDI, 3, 0, SP);
        code.emit(LSHI, 3, 3, 27);
        code.emit(ADD, 2, 2, 3);
        code.emit(SYSCALL, 1, 2, SYS_GC_INIT);

        // goto main expression
        int labelBranchToStart = code.emit(BEQ, ZERO, 0);

        // predef functions

        //printInt(Int)
        Symbol sym = initialScope.lookup("printInt");
        sym.offset = code.pc();
        RegisterItem ritem = code.getRegister();
        code.freeRegister(ritem);
        code.emit(LDW, ritem.register, SP, 0);
        code.emit(SYSCALL, ritem.register, 0, SYS_IO_WR_INT);
        code.emit(ADDI, SP, SP, 4);
        code.emit(RET, LNK);

        //printChar(Int)
        sym = initialScope.lookup("printChar");
        sym.offset = code.pc();
        ritem = code.getRegister();
        code.freeRegister(ritem);
        code.emit(LDW, ritem.register, SP, 0);
        code.emit(SYSCALL, ritem.register, 0, SYS_IO_WR_CHR);
        code.emit(ADDI, SP, SP, 4);
        code.emit(RET, LNK);

        //readInt()
        sym = initialScope.lookup("readInt");
        sym.offset = code.pc();
        code.emit(SYSCALL, RES, 0, SYS_IO_RD_INT);
        code.emit(RET, LNK);

        //readChar()
        sym = initialScope.lookup("readChar");
        sym.offset = code.pc();
        code.emit(SYSCALL, RES, 0, SYS_IO_RD_CHR);
        code.emit(RET, LNK);

        // generate func
        generate(tree.decls);

        // generate main expression
        code.fixup(labelBranchToStart, code.pc());
        generate(tree.expr).freeRegisters(code);

        // exit
        code.emit(RET, ZERO);
    }


    public void caseFunDecl(FunDecl tree) {
        //function prologue
        tree.sym.offset = code.pc();

        //alloc params
        for (int i = 0; i < tree.args.length; i++) {
            generate(tree.args[i]);
            ((Tree.Formal) tree.args[i]).sym.offset = (i + 1) * WORD_SIZE;
        }

        code.incStackSize(WORD_SIZE);
        code.emit(PSH, LNK, SP, WORD_SIZE);

        //body
        RegisterItem ritem = generate(tree.body).load(code);
        code.emit(ADD, RES, ritem.register, ZERO);
        code.freeRegister(ritem);

        //epilogue
        code.emit(POP, LNK, SP, code.getStackSize());
        code.decStackSize(code.getStackSize());
        code.emit(RET, LNK);
    }

    public void caseVarDecl(VarDecl tree) {
        RegisterItem value = generate(tree.value).load(code);
        code.incStackSize(WORD_SIZE);
        code.emit(PSH, value.register, SP, WORD_SIZE);
        code.freeRegister(value);
        tree.sym.offset = code.getStackSize();
    }

    public void caseFunCall(FunCall tree) {
        int nbrReg = code.rc() - 1;

        //save registers
        for (int i = nbrReg; i > 0; i--) {
            code.incStackSize(WORD_SIZE);
            code.emit(PSH, i, SP, WORD_SIZE);
            code.freeRegister(new RegisterItem(i));
        }

        //stack params
        for (int i = 0; i < tree.args.length; i++) {
            RegisterItem left = generate(tree.args[i]).load(code);
            code.freeRegister(left);
            code.incStackSize(WORD_SIZE);
            code.emit(PSH, left.register, SP, WORD_SIZE);
        }

        //call
        Item fun = generate(tree.expression);
        fun.call(code);

        code.decStackSize(tree.args.length * WORD_SIZE);

        //restore registers
        for (int i = 0; i < nbrReg; i++) {
            RegisterItem ritem = code.getRegister();
            code.decStackSize(WORD_SIZE);
            code.emit(POP, ritem.register, SP, WORD_SIZE);
        }

        //return result
        RegisterItem ritem = code.getRegister();
        code.emit(ADD, ritem.register, RES, ZERO);
        item = ritem;
    }

    public void caseFormal(Formal tree) {
        code.incStackSize(WORD_SIZE);
    }

    public void caseExec(Exec tree) {
        Item ritem = generate(tree.expr);
        ritem.freeRegisters(code);
        item = new ImmediateItem(0);
    }

    public void caseWhile(While tree) {
        int jmpAdrCond = code.pc();
        CondItem cond = generate(tree.cond).makeCondItem(code);
        int jmpAdr1 = cond.jumpIfFalse(code);
        RegisterItem ritem = generate(tree.body).load(code);
        int jmpAdr2 = code.pc();
        code.emit(BEQ, ZERO, 0);
        code.fixup(jmpAdr2, jmpAdrCond);
        code.fixup(jmpAdr1, code.pc());
        item = ritem;
    }

    public void caseIf(If tree) {
        CondItem cond = generate(tree.cond).makeCondItem(code);
        int jmpAdr1 = cond.jumpIfFalse(code);

        RegisterItem ritem = code.getRegister();
        RegisterItem thenR = generate(tree.thenp).load(code);
        code.emit(ADDI, ritem.register, thenR.register, 0);
        code.freeRegister(thenR);

        int jmpAdr2 = code.pc();
        code.emit(BEQ, 0, 0);
        code.fixup(jmpAdr1, code.pc());
        RegisterItem elseR = generate(tree.elsep).load(code);
        code.emit(ADDI, ritem.register, elseR.register, 0);
        code.freeRegister(elseR);
        code.fixup(jmpAdr2, code.pc());

        item = ritem;
    }

    public void caseAssign(Assign tree) {
        RegisterItem ritem = generate(tree.value).load(code);
        int diff = code.getStackSize() - tree.sym.offset;
        code.emit(STW, ritem.register, SP, diff);
        code.freeRegister(ritem);
        item = new ImmediateItem(0);
    }

    public void caseIdent(Ident tree) {
        switch (tree.sym.kind) {
            case Kinds.FUNCTION:
                item = new ImmediateItem(tree.sym.offset);
                break;
            case Kinds.VARIABLE:
                item = new StackItem(tree.sym.offset);
                break;
        }
    }

    public void caseBlock(Block tree) {
        int old_stack_size = code.getStackSize();
        for (int i = 0; i < tree.statements.length; i++) {
            Item item = generate(tree.statements[i]);
            if (item != null) item.freeRegisters(code);
        }
        RegisterItem ritem = generate(tree.expression).load(code);
        int new_stack_size = code.getStackSize();
        code.decStackSize(new_stack_size - old_stack_size);
        code.emit(ADDI, SP, SP, new_stack_size - old_stack_size);
        item = ritem;
    }

    public void caseUnitLit(UnitLit tree) {
        item = new ImmediateItem(0);
    }

    public void caseIntLit(IntLit tree) {
        item = new ImmediateItem(tree.value);
    }

    public void caseNilLit(NilLit tree) {
        item = new ImmediateItem(0);
    }

    public void caseOperation(Operation tree) {
        switch (tree.operator) {
            case Tokens.PLUS:
                RegisterItem left = generate(tree.left).load(code);
                RegisterItem right = generate(tree.right).load(code);
                code.freeRegister(right);
                code.freeRegister(left);
                RegisterItem ritem = code.getRegister();
                code.emit(ADD, ritem.register, left.register, right.register);
                item = ritem;
                break;

            case Tokens.MINUS:
                left = generate(tree.left).load(code);
                right = generate(tree.right).load(code);
                code.freeRegister(right);
                code.freeRegister(left);
                ritem = code.getRegister();
                code.emit(SUB, ritem.register, left.register, right.register);
                item = ritem;
                break;

            case Tokens.MUL:
                left = generate(tree.left).load(code);
                right = generate(tree.right).load(code);
                code.freeRegister(right);
                code.freeRegister(left);
                ritem = code.getRegister();
                code.emit(MUL, ritem.register, left.register, right.register);
                item = ritem;
                break;

            case Tokens.DIV:
                left = generate(tree.left).load(code);
                right = generate(tree.right).load(code);
                code.freeRegister(right);
                code.freeRegister(left);
                ritem = code.getRegister();
                code.emit(DIV, ritem.register, left.register, right.register);
                item = ritem;
                break;

            case Tokens.MOD:
                left = generate(tree.left).load(code);
                right = generate(tree.right).load(code);
                code.freeRegister(right);
                code.freeRegister(left);
                ritem = code.getRegister();
                code.emit(MOD, ritem.register, left.register, right.register);
                item = ritem;
                break;

            case Tokens.EQU:
                left = generate(tree.left).load(code);
                right = generate(tree.right).load(code);
                code.freeRegister(right);
                code.freeRegister(left);
                ritem = code.getRegister();
                code.emit(CMP, ritem.register, left.register, right.register);
                item = new CondItem(ritem, BNE);
                break;

            case Tokens.NOTEQ:
                left = generate(tree.left).load(code);
                right = generate(tree.right).load(code);
                code.freeRegister(right);
                code.freeRegister(left);
                ritem = code.getRegister();
                code.emit(CMP, ritem.register, left.register, right.register);
                item = new CondItem(ritem, BEQ);
                break;

            case Tokens.LESS:
                left = generate(tree.left).load(code);
                right = generate(tree.right).load(code);
                code.freeRegister(right);
                code.freeRegister(left);
                ritem = code.getRegister();
                code.emit(CMP, ritem.register, left.register, right.register);
                item = new CondItem(ritem, BGE);
                break;

            case Tokens.LESSOREQ:
                left = generate(tree.left).load(code);
                right = generate(tree.right).load(code);
                code.freeRegister(right);
                code.freeRegister(left);
                ritem = code.getRegister();
                code.emit(CMP, ritem.register, left.register, right.register);
                item = new CondItem(ritem, BGT);
                break;

            case Tokens.GREATER:
                left = generate(tree.left).load(code);
                right = generate(tree.right).load(code);
                code.freeRegister(right);
                code.freeRegister(left);
                ritem = code.getRegister();
                code.emit(CMP, ritem.register, left.register, right.register);
                item = new CondItem(ritem, BLE);
                break;

            case Tokens.GREATEROREQ:
                left = generate(tree.left).load(code);
                right = generate(tree.right).load(code);
                code.freeRegister(right);
                code.freeRegister(left);
                ritem = code.getRegister();
                code.emit(CMP, ritem.register, left.register, right.register);
                item = new CondItem(ritem, BLT);
                break;

            case Tokens.CONS:
                right = generate(tree.right).load(code);
                ritem = code.getRegister();
                RegisterItem temp = code.getRegister();
                code.freeRegister(temp);
                code.emit(ADDI, temp.register, 0, 8);
                code.emit(SYSCALL, ritem.register, temp.register, SYS_GC_ALLOC);
                code.emit(STW, right.register, ritem.register, 4);
                code.emit(ADD, right.register, ritem.register, ZERO);
                code.freeRegister(ritem);
                left = generate(tree.left).load(code);
                code.freeRegister(left);
                code.emit(STW, left.register, right.register, 0);
                item = right;
                break;

            case Tokens.HEAD:
                left = generate(tree.left).load(code);
                int jmpAdr = code.pc();
                code.emit(BNE, left.register, jmpAdr);
                RegisterItem ra = code.getRegister();
                code.freeRegister(ra);
                code.emit(ADDI, ra.register, ZERO, -1);
                code.emit(SYSCALL, ra.register, ZERO, SYS_EXIT);
                code.fixup(jmpAdr, code.pc());
                code.freeRegister(left);
                ritem = code.getRegister();
                code.emit(LDW, ritem.register, left.register, 0);
                item = ritem;
                break;

            case Tokens.TAIL:
                left = generate(tree.left).load(code);
                jmpAdr = code.pc();
                code.emit(BNE, left.register, jmpAdr);
                ra = code.getRegister();
                code.freeRegister(ra);
                code.emit(ADDI, ra.register, ZERO, -1);
                code.emit(SYSCALL, ra.register, ZERO, SYS_EXIT);
                code.fixup(jmpAdr, code.pc());
                code.freeRegister(left);
                ritem = code.getRegister();
                code.emit(LDW, ritem.register, left.register, WORD_SIZE);
                item = ritem;
                break;

            case Tokens.ISEMPTY:
                left = generate(tree.left).load(code);
                code.freeRegister(left);
                int jmpAdr1 = code.pc();
                code.emit(BEQ, left.register, jmpAdr1);
                ritem = code.getRegister();
                code.emit(ADDI, ritem.register, ZERO, 0);
                int jmpAdr2 = code.pc();
                code.emit(BEQ, ZERO, jmpAdr2);
                code.fixup(jmpAdr1, code.pc());
                code.emit(ADDI, ritem.register, ZERO, 1);
                code.fixup(jmpAdr2, code.pc());
                item = ritem;
                break;
        }
    }

    //noop

    public void caseUnitType(UnitType tree) {
    }

    public void caseIntType(IntType tree) {
    }

    public void caseListType(ListType tree) {
    }

    public void caseFunType(FunType tree) {
    }
}
