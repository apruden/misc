package misc.risc;

interface RISC {

    /**
     * The word size
     */
    public static final int WORD_SIZE = 4;

    /**
     * <p>Opcode: integer addition.</p>
     * <b><code>ADD a b c</code></b><br>
     * <code>R.a = R.b + R.c</code>
     */
    public static final int ADD = 0;

    /**
     * <p>Opcode: integer subtraction.</p>
     * <b><code>SUB a b c</code></b><br>
     * <code>R.a = R.b - R.c</code>
     */
    public static final int SUB = 1;

    /**
     * <p>Opcode: integer multiplication.</p>
     * <b><code>MUL a b c</code></b><br>
     * <code>R.a = R.b * R.c</code>
     */
    public static final int MUL = 2;

    /**
     * <p>Opcode: integer division.</p>
     * <b><code>DIV a b c</code></b><br>
     * <code>R.a = R.b / R.c</code>
     */
    public static final int DIV = 3;

    /**
     * <p>Opcode: integer modulo.</p>
     * <b><code>MOD a b c</code></b><br>
     * <code>R.a = R.b % R.c</code>
     */
    public static final int MOD = 4;

    /**
     * <p>Opcode: integer comparison.</p>
     * <b><code>CMP a b c</code></b><br>
     * <code>R.a = &lt;a value with the same sign as (R.b - R.c)
     * but with a possibly different magnitude&gt;</code>
     */
    public static final int CMP = 5;

    /**
     * <p>Opcode: logical or.</p>
     * <b><code>OR a b c</code></b><br>
     * <code>R.a = R.b | R.c</code>
     */
    public static final int OR = 8;

    /**
     * <p>Opcode: logical and.</p>
     * <b><code>AND a b c</code></b><br>
     * <code>R.a = R.b & R.c</code>
     */
    public static final int AND = 9;

    /**
     * <p>Opcode: logical bic.</p>
     * <b><code>BIC a b c</code></b><br>
     * <code>R.a = R.b & ~R.c</code>
     */
    public static final int BIC = 10;

    /**
     * <p>Opcode: logical xor.</p>
     * <b><code>XOR a b c</code></b><br>
     * <code>R.a = R.b ^ R.c</code>
     */
    public static final int XOR = 11;

    /**
     * <p>Opcode: logical shift.</p>
     * <b><code>LSH a b c</code></b><br>
     * <code>R.a = (R.c &gt; 0) ?
     * (R.b &lt;&lt; R.c) : (R.b &gt;&gt;&gt; -R.c)</code>
     */
    public static final int LSH = 12;

    /**
     * <p>Opcode: arithmetic shift.</p>
     * <b><code>ASH a b c</code></b><br>
     * <code>R.a = (R.c &gt; 0) ?
     * (R.b &lt;&lt; R.c) : (R.b &gt;&gt; -R.c)</code>
     */
    public static final int ASH = 13;

    /**
     * <p>Opcode: bound check.</p>
     * <b><code>CHK a c</code></b><br>
     * raise an error if not <code>(0 &lt;= R.a < R.c)</code>
     */
    public static final int CHK = 14;

    //########################################################################
    // Opcodes - instructions with register and signed immediate

    /**
     * <p>Opcode: integer addition with signed immediate.</p>
     * <b><code>ADDI a b ic</code></b><br>
     * <code>R.a = R.b + ic</code>
     */
    public static final int ADDI = 16;

    /**
     * <p>Opcode: integer subtraction with signed immediate.</p>
     * <b><code>SUBI a b ic</code></b><br>
     * <code>R.a = R.b - ic</code>
     */
    public static final int SUBI = 17;

    /**
     * <p>Opcode: integer multiplication with signed immediate.</p>
     * <b><code>MULI a b ic</code></b><br>
     * <code>R.a = R.b * ic</code>
     */
    public static final int MULI = 18;

    /**
     * <p>Opcode: integer division with signed immediate.</p>
     * <b><code>DIVI a b ic</code></b><br>
     * <code>R.a = R.b / ic</code>
     */
    public static final int DIVI = 19;

    /**
     * <p>Opcode: integer modulo with signed immediate.</p>
     * <b><code>MODI a b ic</code></b><br>
     * <code>R.a = R.b % ic</code>
     */
    public static final int MODI = 20;

    /**
     * <p>Opcode: integer comparison with signed immediate.</p>
     * <b><code>CMPI a b ic</code></b><br>
     * <code>R.a = &lt;a value with the same sign as (R.b - ic)
     * but with a possibly different magnitude&gt;</code>
     */
    public static final int CMPI = 21;

    /**
     * <p>Opcode: logical or with signed immediate.</p>
     * <b><code>ORI a b ic</code></b><br>
     * <code>R.a = R.b | ic</code>
     */
    public static final int ORI = 24;

    /**
     * <p>Opcode: logical and with signed immediate.</p>
     * <b><code>ANDI a b ic</code></b><br>
     * <code>R.a = R.b & ic</code>
     */
    public static final int ANDI = 25;

    /**
     * <p>Opcode: logical bic with signed immediate.</p>
     * <b><code>BICI a b ic</code></b><br>
     * <code>R.a = R.b & ~ic</code>
     */
    public static final int BICI = 26;

    /**
     * <p>Opcode: logical xor with signed immediate.</p>
     * <b><code>XORI a b ic</code></b><br>
     * <code>R.a = R.b ^ ic</code>
     */
    public static final int XORI = 27;

    /**
     * <p>Opcode: logical shift with signed immediate.</p>
     * <b><code>LSHI a b ic</code></b><br>
     * <code>R.a = (ic &gt; 0) ?
     * (R.b &lt;&lt; ic) : (R.b &gt;&gt;&gt; -ic)</code>
     */
    public static final int LSHI = 28;

    /**
     * <p>Opcode: arithmetic shift with signed immediate.</p>
     * <b><code>ASHI a b ic</code></b><br>
     * <code>R.a = (ic &gt; 0) ?
     * (R.b &lt;&lt; ic) : (R.b &gt;&gt; -ic)</code>
     */
    public static final int ASHI = 29;

    /**
     * <p>Opcode: bound check with signed immediate.</p>
     * <b><code>CHKI a ic</code></b><br>
     * raise an error if not <code>(0 &lt;= R.a < ic)</code>
     */
    public static final int CHKI = 30;

    //########################################################################
    // Opcodes - instructions with register and unsigned immediate

    /**
     * <p>Opcode: integer addition with unsigned immediate.</p>
     * <b><code>ADDIU a b uc</code></b><br>
     * <code>R.a = R.b + uc</code>
     */
    public static final int ADDIU = 54;

    /**
     * <p>Opcode: integer subtraction with unsigned immediate.</p>
     * <b><code>SUBIU a b uc</code></b><br>
     * <code>R.a = R.b - uc</code>
     */
    public static final int SUBIU = 55;

    /**
     * <p>Opcode: integer multiplication with unsigned immediate.</p>
     * <b><code>MULIU a b uc</code></b><br>
     * <code>R.a = R.b * uc</code>
     */
    public static final int MULIU = 56;

    /**
     * <p>Opcode: integer division with unsigned immediate.</p>
     * <b><code>DIVIU a b uc</code></b><br>
     * <code>R.a = R.b / uc</code>
     */
    public static final int DIVIU = 57;

    /**
     * <p>Opcode: integer modulo with unsigned immediate.</p>
     * <b><code>MODIU a b uc</code></b><br>
     * <code>R.a = R.b % uc</code>
     */
    public static final int MODIU = 58;

    /**
     * <p>Opcode: integer comparison with unsigned immediate.</p>
     * <b><code>CMPIU a b uc</code></b><br>
     * <code>R.a = &lt;a value with the same sign as (R.b - uc)
     * but with a possibly different magnitude&gt;</code>
     */
    public static final int CMPIU = 59;

    /**
     * <p>Opcode: logical or with unsigned immediate.</p>
     * <b><code>ORIU a b uc</code></b><br>
     * <code>R.a = R.b | uc</code>
     */
    public static final int ORIU = 60;

    /**
     * <p>Opcode: logical and with unsigned immediate.</p>
     * <b><code>ANDIU a b uc</code></b><br>
     * <code>R.a = R.b & uc</code>
     */
    public static final int ANDIU = 61;

    /**
     * <p>Opcode: logical bic with unsigned immediate.</p>
     * <b><code>BICIU a b uc</code></b><br>
     * <code>R.a = R.b & ~uc</code>
     */
    public static final int BICIU = 62;

    /**
     * <p>Opcode: logical xor with unsigned immediate.</p>
     * <b><code>XORIU a b uc</code></b><br>
     * <code>R.a = R.b ^ uc</code>
     */
    public static final int XORIU = 63;

    /**
     * <p>Opcode: bound check with unsigned immediate.</p>
     * <b><code>CHKIU a uc</code></b><br>
     * raise an error if not <code>(0 &lt;= R.a < uc)</code>
     */
    public static final int CHKIU = 39;

    //########################################################################
    // Opcodes - load/store instructions

    /**
     * <p>Opcode: load word from memory. The address <code>R.b</code>
     * must be aligned on a word boundary.</p>
     *
     * <b><code>LDW a b ic</code></b><br>
     * <code>R.a = &lt;word at address R.b + ic&gt;</code>
     */
    public static final int LDW = 32;

    /**
     * <p>Opcode: load byte from memory.</p>
     *
     * <b><code>LDB a b ic</code></b><br>
     * <code>R.a = &lt;byte at address R.b + ic&gt;</code>
     */
    public static final int LDB = 33;

    /**
     * <p>Opcode: pop word from stack. The address <code>R.b</code> must
     * be aligned on a word boundary.</p>
     *
     * <b><code>POP a b ic</code></b><br>
     * <code>R.a = &lt;word at address R.b&gt;</code><br>
     * <code>R.b = R.b + ic</code>
     */
    public static final int POP = 34;

    /**
     * <p>Opcode: store word into memory. The address <code>R.b</code>
     * must be aligned on a word boundary.</p>
     *
     * <b><code>STW a b ic</code></b><br>
     * <code>&lt;word at address R.b + ic&gt; = R.a</code>
     */
    public static final int STW = 36;

    /**
     * <p>Opcode: store byte into memory.</p>
     *
     * <b><code>STB a b ic</code></b><br>
     * <code>&lt;byte at address R.b + ic&gt; = (byte)R.a</code>
     */
    public static final int STB = 37;

    /**
     * <p>Opcode: push word on stack. The address <code>R.b</code> must
     * be aligned on a word boundary.</p>
     *
     * <b><code>PSH a b ic</code></b><br>
     * <code>R.b = R.b - ic</code><br>
     * <code>&lt;word at address R.b&gt; = R.a</code>
     */
    public static final int PSH = 38;

    //########################################################################
    // Opcodes - control instructions

    /**
     * <p>Opcode: branch if equal.</p>
     * <b><code>BEQ a oc</code></b><br>
     * branch to <code>(PC + 4*oc)</code> if <code>(R.a == 0)</code>
     */
    public static final int BEQ = 40;

    /**
     * <p>Opcode: branch if not equal.</p>
     * <b><code>BNE a oc</code></b><br>
     * branch to <code>(PC + 4*oc)</code> if <code>(R.a != 0)</code>
     */
    public static final int BNE = 41;

    /**
     * <p>Opcode: branch if less than.</p>
     * <b><code>BLT a oc</code></b><br>
     * branch to <code>(PC + 4*oc)</code> if <code>(R.a < 0)</code>
     */
    public static final int BLT = 42;

    /**
     * <p>Opcode: branch if greater or equal.</p>
     * <b><code>BGE a oc</code></b><br>
     * branch to <code>(PC + 4*oc)</code> if <code>(R.a >= 0)</code>
     */
    public static final int BGE = 43;

    /**
     * <p>Opcode: branch if equal less or equal.</p>
     * <b><code>BLE a oc</code></b><br>
     * branch to <code>(PC + 4*oc)</code> if <code>(R.a <= 0)</code>
     */
    public static final int BLE = 44;

    /**
     * <p>Opcode: branch if greater than.</p>
     * <b><code>BGT a oc</code></b><br>
     * branch to <code>(PC + 4*oc)</code> if <code>(R.a > 0)</code>
     */
    public static final int BGT = 45;

    /**
     * <p>Opcode: branch to subroutine.</p>
     * <b><code>BSR oc</code></b><br>
     * <code>R.31 = PC + 4</code><br>
     * branch to <code>(PC + 4*oc)</code>
     */
    public static final int BSR = 46;

    /**
     * <p>Opcode: jump to subroutine.</p>
     * <b><code>JSR lc</code></b><br>
     * <code>R.31 = PC + 4</code><br>
     * branch to <code>(4*lc)</code>
     */
    public static final int JSR = 48;

    /**
     * <p>Opcode: jump to return address.</p>
     * <b><code>RET c</code></b><br>
     * jump to <code>R.c</code>
     */
    public static final int RET = 49;

    //########################################################################
    // Opcodes - miscellaneous instructions

    /**
     * <p>Opcode: stop execution and return to debugger.</p>
     * <b><code>BREAK</code></b><br>
     * stop execution and return to debugger
     */
    public static final int BREAK = 6;

    /**
     * <p>Opcode: invoke a system function.</p>
     * <b><code>SYSCALL a b uc</b></code><br>
     * invoke system function <code>uc</code> with registers
     * <code>R.a</code> and <code>R.b</code>
     */
    public static final int SYSCALL = 7;

    //########################################################################
    // System calls - IO read

    /**
     * <p>System call: read one character.</p>
     * <b><code>SYSCALL a 0 SYS_IO_RD_CHR</b></code><br>
     * <code>R.a = &lt;Unicode of read character or -1 if EOF&gt;</code>
     */
    public static final int SYS_IO_RD_CHR = 1;

    /**
     * <p>System call: read an integer.</p>
     * <b><code>SYSCALL a 0 SYS_IO_RD_INT</b></code><br>
     * <code>R.a = &lt;value of read integer&gt;</code>
     */
    public static final int SYS_IO_RD_INT = 2;

    //########################################################################
    // System calls - IO write

    /**
     * <p>System call: write one character.</p>
     * <b><code>SYSCALL a 0 SYS_IO_WR_CHR</b></code><br>
     * <code>&lt;write character with Unicode R.a&gt;</code>
     */
    public static final int SYS_IO_WR_CHR = 6;

    /**
     * <p>System call: write an integer.</p>
     * <b><code>SYSCALL a b SYS_IO_WR_INT</b></code><br>
     * <code>&lt;write signed value R.a in decimal format and
     * space padded to width R.b&gt;</code>
     */
    public static final int SYS_IO_WR_INT = 7;

    //########################################################################
    // System calls - garbage collector

    /**
     * <p>System call: initialize the garbage collector.</p>
     *
     * <p>The garbage collector is initialized with an empty heap that
     * starts at address <code>R.a</cod> and with a maximum size of
     * <code>sz = R.b & 0x07FFFFFF</code> words. If <code>sz</code> is
     * zero then the heap extends to the end of the memory.</p>
     *
     * <p>The value <code>sp = R.b &gt;&gt;&gt; 27</code> determines
     * whether there is a stack and which register is the stack
     * pointer: if <code>sp</code> is non-zero, the garbage collector
     * assumes that there is a stack and that <code>R.sp</code> is the
     * stack pointer register. It is assumed that the stack starts at
     * the end of the memory and grows downwards.<p>
     *
     * <p>During a garbage collection, the garbage collector frees all
     * the memory blocks which are not referenced by any live
     * pointer. A live pointer (resp. value) is either a root pointer
     * (resp. value) or a pointer (resp. value) contained in a block
     * referenced by a live pointer. Note that as there is no way to
     * distinguish between pointers and non-pointer values, all values
     * are regarded as pointers. A root value is one that is contained
     * in a register, in the memory below the heap or in the stack. If
     * there is no stack, all the values contained in the memory above
     * the heap are also root values.<p>
     *
     * <b><code>SYSCALL a b SYS_GC_INIT</b></code><br>
     * <code>&lt;initialize the garbage collector&gt;</code>
     */
    public static final int SYS_GC_INIT = 11;

    /**
     * <p>System call: allocate a memory block from the heap.</p>
     *
     * <p>Allocates a memory block of, at least, <code>R.b</code>
     * bytes from the heap and returns its address in
     * <code>R.a</code>. The allocated memory is zero-initialized and
     * its address is guaranteed to be aligned on a word boundary. If
     * there is not enough free memory, a garbage collection is
     * triggered and if this doesn't free enough memory, an error is
     * raised and the execution stopped.<p>
     *
     * <b><code>SYSCALL a b SYS_GC_ALLOC</b></code><br>
     * <code>R.a = &lt;address of the newly allocated and
     * zero-initialized memory block of R.b bytes&gt;</code>
     */
    public static final int SYS_GC_ALLOC = 12;

    //########################################################################
    // System calls - miscellaneous

    /**
     * <p>System call: get memory size.</p>
     * <b><code>SYSCALL a 0 SYS_GET_TOTAL_MEM_SIZE</b></code><br>
     * <code>R.a = &lt;memory size in bytes&gt;</code>
     */
    public static final int SYS_GET_TOTAL_MEM_SIZE = 13;

    /**
     * <p>System call: terminate the emulation.</p>
     * <b><code>SYSCALL a 0 SYS_EXIT</b></code><br>
     * <code>&lt;terminates the emulation with status code R.a&gt;</code>
     */
    public static final int SYS_EXIT = 19;

    //########################################################################
    // Registers

    /**
     * Register: zero
     */
    public static final int ZERO = 0;

    /**
     * Register: return value
     */
    public static final int RES = 29;

    /**
     * Register: stack pointer
     */
    public static final int SP = 30;

    /**
     * Register: return address
     */
    public static final int LNK = 31;

    //########################################################################
    // Data structures

    /**
     * String representation of instructions
     */
    public static final String[] mnemonics = Helper.getMnemonics();

    //########################################################################
    // class Helper

    public class Helper {

        public static String[] getMnemonics() {
            String[] mnemonics = new String[64];

            mnemonics[ADD] = "add";
            mnemonics[SUB] = "sub";
            mnemonics[MUL] = "mul";
            mnemonics[DIV] = "div";
            mnemonics[MOD] = "mod";
            mnemonics[CMP] = "cmp";
            mnemonics[OR] = "or";
            mnemonics[AND] = "and";
            mnemonics[BIC] = "bic";
            mnemonics[XOR] = "xor";
            mnemonics[LSH] = "lsh";
            mnemonics[ASH] = "ash";
            mnemonics[CHK] = "chk";

            mnemonics[ADDI] = "addi";
            mnemonics[SUBI] = "subi";
            mnemonics[MULI] = "muli";
            mnemonics[DIVI] = "divi";
            mnemonics[MODI] = "modi";
            mnemonics[CMPI] = "cmpi";
            mnemonics[ORI] = "ori";
            mnemonics[ANDI] = "andi";
            mnemonics[BICI] = "bici";
            mnemonics[XORI] = "xori";
            mnemonics[LSHI] = "lshi";
            mnemonics[ASHI] = "ashi";
            mnemonics[CHKI] = "chki";

            mnemonics[ADDIU] = "addiu";
            mnemonics[SUBIU] = "subiu";
            mnemonics[MULIU] = "muliu";
            mnemonics[DIVIU] = "diviu";
            mnemonics[MODIU] = "modiu";
            mnemonics[CMPIU] = "cmpiu";
            mnemonics[ORIU] = "oriu";
            mnemonics[ANDIU] = "andiu";
            mnemonics[BICIU] = "biciu";
            mnemonics[XORIU] = "xoriu";
            mnemonics[CHKIU] = "chkiu";

            mnemonics[LDW] = "ldw";
            mnemonics[LDB] = "ldb";
            mnemonics[POP] = "pop";
            mnemonics[STW] = "stw";
            mnemonics[STB] = "stb";
            mnemonics[PSH] = "psh";

            mnemonics[BEQ] = "beq";
            mnemonics[BNE] = "bne";
            mnemonics[BLT] = "blt";
            mnemonics[BGE] = "bge";
            mnemonics[BLE] = "ble";
            mnemonics[BGT] = "bgt";
            mnemonics[BSR] = "bsr";
            mnemonics[JSR] = "jsr";
            mnemonics[RET] = "ret";

            mnemonics[BREAK] = "break";
            mnemonics[SYSCALL] = "syscall";

            return mnemonics;
        }
    }
}
