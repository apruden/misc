package misc.risc;

public abstract class Item implements RISC {

    /**
     * loads the value represented by this item in a register then returns an item representing this register.
     */
    public abstract RegisterItem load(Code code);

    /**
     * transforms the value into a condition then returns it.
     */
    public CondItem makeCondItem(Code code) {
        return new CondItem(load(code), BEQ);
    }

    /**
     * call the function represented by this item
     */
    public void call(Code code) {
        RegisterItem target = load(code);
        code.freeRegister(target);
        code.emit(ADDI, LNK, ZERO, code.pc() + WORD_SIZE * 2);
        code.emit(RET, target.register);
    }

    /**
     * free used registers by this item.
     */
    public abstract void freeRegisters(Code code);

    /**
     * value saved in a register
     */
    public static class RegisterItem extends Item {
        public final int register;

        public RegisterItem(int register) {
            this.register = register;
        }

        public RegisterItem load(Code code) {
            return this;
        }

        public void freeRegisters(Code code) {
            code.freeRegister(this);
        }
    }

    /**
     * Constant value
     */
    public static class ImmediateItem extends Item {
        public int value;

        public ImmediateItem(int value) {
            this.value = value;
        }

        public RegisterItem load(Code code) {
            RegisterItem ritem = code.getRegister();
            if (-32768 < value && value < 32768) //<=16 bits
                code.emit(ADDI, ritem.register, ZERO, value);
            else {
                code.emit(ADDI, ritem.register, ZERO, value / 32768);
                code.emit(LSHI, ritem.register, ritem.register, 15);
                code.emit(ADDI, ritem.register, ritem.register, value % 32768);
            }
            return ritem;
        }

        public void freeRegisters(Code code) {
        }
    }

    /**
     * in-memory value
     */
    public static class StackItem extends Item {
        public int offset;

        public StackItem(int offset) {
            this.offset = offset;
        }

        public RegisterItem load(Code code) {
            RegisterItem ritem = code.getRegister();
            int stacksize = code.getStackSize();
            code.emit(LDW, ritem.register, SP, stacksize - offset);
            return ritem;
        }

        public void freeRegisters(Code code) {
        }
    }

    /**
     * condition
     */
    public static class CondItem extends Item {
        public int jmpCode;
        public RegisterItem ritem;

        public CondItem(RegisterItem ritem, int jmpCode) {
            this.ritem = ritem;
            this.jmpCode = jmpCode;
        }

        public RegisterItem load(Code code) {
            int jmpAdr1 = code.pc();
            code.emit(jmpCode, ritem.register, 0);
            code.emit(ADDI, ritem.register, 0, 1);
            int jmpAdr2 = code.pc();
            code.emit(BEQ, 0, 0);
            code.fixup(jmpAdr1, code.pc());
            code.emit(ADDI, ritem.register, 0, 0);
            code.fixup(jmpAdr2, code.pc());
            return ritem;
        }

        public void freeRegisters(Code code) {
            code.freeRegister(this.ritem);
        }

        public int jumpIfFalse(Code code) {
            int jmpAdr = code.pc();
            code.emit(jmpCode, ritem.register, 0);
            code.freeRegister(ritem);
            return jmpAdr;
        }

        public CondItem negate() {
            CondItem negCond = null;
            switch (jmpCode) {
                case BEQ:
                    negCond = new CondItem(ritem, BNE);
                    break;
                case BNE:
                    negCond = new CondItem(ritem, BEQ);
                    break;
                case BLT:
                    negCond = new CondItem(ritem, BGE);
                    break;
                case BLE:
                    negCond = new CondItem(ritem, BGT);
                    break;
                case BGT:
                    negCond = new CondItem(ritem, BLE);
                    break;
                case BGE:
                    negCond = new CondItem(ritem, BLT);
                    break;
                default:
                    negCond = new CondItem(ritem, jmpCode);
                    break;
            }
            return negCond;
        }

        public CondItem makeCondItem(Code code) {
            return this;
        }

    }
}
