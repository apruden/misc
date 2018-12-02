package misc.risc;

import misc.Global;
import misc.risc.Item.RegisterItem;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Code implements RISC {

    public static final int RC_MIN = 1;

    public static final int RC_MAX = 28;

    private final Global global;

    private final RegisterItem[] registers;

    private final List code;

    private int rc;

    private int stackSize;

    public Code(Global global) {
        this.global = global;
        this.registers = new RegisterItem[32];
        for (int i = 0; i < registers.length; i++)
            registers[i] = new RegisterItem(i);
        this.code = new ArrayList();
        this.rc = RC_MIN;
        this.stackSize = 0;
    }

    public int rc() {
        return rc;
    }

    public RegisterItem getRegister() {
        if (rc <= RC_MAX) return registers[rc++];
        global.error(0, "not enough registers");
        return registers[ZERO];
    }

    public void freeRegister(RegisterItem item) {
        if (item.register < RC_MIN || item.register > RC_MAX) return;
        if (item.register == --rc) return;
        throw new Error("last register allocated " + rc + ", " +
                "trying to free register " + item.register);
    }

    public int pc() {
        return WORD_SIZE * code.size();
    }

    public void fixup(int pc, int to) {
        ((Instruction) code.get(pc / WORD_SIZE)).c = (to - pc) / WORD_SIZE;
    }

    public void incStackSize(int bytes) {
        stackSize += bytes;
    }

    public void decStackSize(int bytes) {
        stackSize -= bytes;
    }

    public int getStackSize() {
        return stackSize;
    }

    public int emit(int opcode) {
        return emit(opcode, Integer.MIN_VALUE);
    }

    public int emit(int opcode, int c) {
        return emit(opcode, Integer.MIN_VALUE, c);
    }

    public int emit(int opcode, int a, int c) {
        return emit(opcode, a, Integer.MIN_VALUE, c);
    }

    public int emit(int opcode, int a, int b, int c) {
        int pc = pc();
        code.add(new Instruction(opcode, a, b, c));
        return pc;
    }

    public void write(PrintWriter out) {
        for (int i = 0; i < code.size(); i++) {
            String label = Integer.toString(WORD_SIZE * i);
            while (label.length() < 4) label = '0' + label;
            out.print("/* " + label + " */ ");
            out.print(code.get(i));
            out.println();
        }
    }

    private static class Instruction {
        public final int opcode;
        public final int a;
        public final int b;
        public int c;

        public Instruction(int opcode, int a, int b, int c) {
            this.opcode = opcode;
            this.a = a;
            this.b = b;
            this.c = c;
        }

        public String toString() {
            StringBuffer buffer = new StringBuffer();
            buffer.append(mnemonics[opcode]);
            if (a != Integer.MIN_VALUE) buffer.append(' ').append(a);
            if (b != Integer.MIN_VALUE) buffer.append(' ').append(b);
            if (c != Integer.MIN_VALUE) buffer.append(' ').append(c);
            return buffer.toString();
        }
    }
}
