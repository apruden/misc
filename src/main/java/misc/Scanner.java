package misc;

import java.io.IOException;
import java.io.InputStream;

public class Scanner implements Tokens {

    private static final char EOF_CH = (char) -1;

    public final Global global;

    /**
     * current token
     */
    public int token;

    /**
     * position of first char of current token
     */
    public int start;

    /**
     * current token representation (valid only if multiple representations are possible)
     */
    public String chars;

    /**
     * buffer to create tokens
     */
    private StringBuffer buf;

    /**
     * current char
     */
    private char ch;

    /**
     * line and column of current char
     */
    private int line = 1;
    private int column = 0;

    /**
     * input
     */
    private final InputStream in;

    /**
     * used by nextCh()
     */
    private char oldch;

    public Scanner(Global global, InputStream in) {
        this.global = global;
        this.in = in;
        this.buf = new StringBuffer();
        nextCh();
        nextToken();
    }

    public void nextToken() {
        boolean test_div = false;

        //skip comments and whitespaces
        while ((ch == '\n' || ch == '\f' || ch == '\t' || ch == ' ' ||
                ch == '/') && (ch != EOF_CH)) {
            if (ch == '/') {
                nextCh();
                if (ch == '/') {
                    do {
                        nextCh();
                    } while ((ch != '\n') && (ch != EOF_CH));
                } else {
                    test_div = true;
                    break;
                }
            }
            nextCh();
        }

        start = Position.encode(line, column);
        if (test_div) token = DIV;
        else token = readToken();
    }

    private int readToken() {
        int token = 0;
        buf.setLength(0);
        switch (ch) {
            case ',':
                token = COMMA;
                nextCh();
                break;
            case ';':
                token = SEMICOLON;
                nextCh();
                break;
            case ':':
                nextCh();
                if (ch == ':') {
                    token = CONS;
                    nextCh();
                } else token = COLON;
                break;
            case '(':
                token = LPAREN;
                nextCh();
                break;
            case ')':
                token = RPAREN;
                nextCh();
                break;
            case '[':
                token = LBRACK;
                nextCh();
                break;
            case ']':
                token = RBRACK;
                nextCh();
                break;
            case '{':
                token = LBRACE;
                nextCh();
                break;
            case '}':
                token = RBRACE;
                nextCh();
                break;
            case '+':
                token = PLUS;
                nextCh();
                break;
            case '-':
                token = MINUS;
                nextCh();
                break;
            case '*':
                token = MUL;
                nextCh();
                break;
            case '%':
                token = MOD;
                nextCh();
                break;
            case '/':
                token = DIV;
                nextCh();
                break;
            case '>':
                nextCh();
                if (ch == '=') {
                    token = GREATEROREQ;
                    nextCh();
                } else token = GREATER;
                break;
            case '<':
                nextCh();
                if (ch == '=') {
                    token = LESSOREQ;
                    nextCh();
                } else token = LESS;
                break;
            case '!':
                nextCh();
                if (ch == '=') {
                    token = NOTEQ;
                    nextCh();
                } else token = NOT;
                break;
            case '&':
                token = AND;
                nextCh();
                break;
            case '|':
                token = OR;
                nextCh();
                break;
            case '=':
                nextCh();
                if (ch == '=') {
                    token = EQU;
                    nextCh();
                } else token = AFFECT;
                break;
            //Numbers
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                do {
                    buf.append(ch);
                    nextCh();
                } while (ch >= '0' && ch <= '9');
                chars = buf.toString();
                token = NUMBER;
                break;
            //variables and tokens
            case 'a':
            case 'b':
            case 'c':
            case 'd':
            case 'e':
            case 'f':
            case 'g':
            case 'h':
            case 'i':
            case 'j':
            case 'k':
            case 'l':
            case 'm':
            case 'n':
            case 'o':
            case 'p':
            case 'q':
            case 'r':
            case 's':
            case 't':
            case 'u':
            case 'v':
            case 'w':
            case 'x':
            case 'y':
            case 'z':
            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F':
            case 'G':
            case 'H':
            case 'I':
            case 'J':
            case 'K':
            case 'L':
            case 'M':
            case 'N':
            case 'O':
            case 'P':
            case 'Q':
            case 'R':
            case 'S':
            case 'T':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
                do {
                    buf.append(ch);
                    nextCh();
                } while (('0' <= ch && ch <= '9') ||
                        ('a' <= ch && ch <= 'z') ||
                        ('A' <= ch && ch <= 'Z') || (ch == '_'));
                chars = buf.toString();
                if (chars.equals("if")) token = IF;
                else if (chars.equals("else")) token = ELSE;
                else if (chars.equals("Int")) token = INT;
                else if (chars.equals("List")) token = LIST;
                else if (chars.equals("head")) token = HEAD;
                else if (chars.equals("tail")) token = TAIL;
                else if (chars.equals("isEmpty")) token = ISEMPTY;
                else if (chars.equals("Unit")) token = UNIT;
                else if (chars.equals("var")) token = VAR;
                else if (chars.equals("while")) token = WHILE;
                else if (chars.equals("def")) token = DEF;
                else if (chars.equals("true")) token = TRUE;
                else if (chars.equals("false")) token = FALSE;
                else token = IDENT;
                break;
            //String
            case '\"':
                nextCh();
                while (ch != '\n' && ch != '\"' && ch != EOF_CH) {
                    buf.append(ch);
                    nextCh();
                }
                chars = buf.toString();
                if (ch == '\"') token = STRING;
                else throw new Error("Unknown token " + token);
                nextCh();
                break;
            case EOF_CH:
                token = EOF;
                break;
            default:
                token = BAD;
                nextCh();
        }
        return token;
    }

    public String representation() {
        String representation = tokenClass(token);
        if (token == NUMBER || token == IDENT || token == STRING)
            representation += "(" + chars + ")";
        return representation;
    }

    public static String tokenClass(int token) {
        switch (token) {
            case EOF:
                return "<eof>";
            case BAD:
                return "<bad>";
            case IDENT:
                return "ident";
            case NUMBER:
                return "number";
            case STRING:
                return "string";
            case LPAREN:
                return "(";
            case RPAREN:
                return ")";
            case LBRACE:
                return "{";
            case RBRACE:
                return "}";
            case LBRACK:
                return "[";
            case RBRACK:
                return "]";
            case SEMICOLON:
                return ";";
            case COLON:
                return ":";
            case COMMA:
                return ",";
            case CONS:
                return "::";
            case AFFECT:
                return "=";
            case PLUS:
                return "+";
            case MINUS:
                return "-";
            case MUL:
                return "*";
            case DIV:
                return "/";
            case MOD:
                return "%";
            case EQU:
                return "==";
            case NOTEQ:
                return "!=";
            case LESS:
                return "<";
            case GREATER:
                return ">";
            case LESSOREQ:
                return "<=";
            case GREATEROREQ:
                return ">=";
            case NOT:
                return "!";
            case OR:
                return "|";
            case AND:
                return "&";
            case HEAD:
                return "head";
            case TAIL:
                return "tail";
            case ISEMPTY:
                return "isEmpty";
            case DEF:
                return "def";
            case UNIT:
                return "Unit";
            case INT:
                return "Int";
            case LIST:
                return "List";
            case VAR:
                return "var";
            case WHILE:
                return "while";
            case IF:
                return "if";
            case ELSE:
                return "else";
            case TRUE:
                return "true";
            case FALSE:
                return "false";
            default:
                throw new Error("Unknown token " + token);
        }
    }

    private int nextCh(int token) {
        nextCh();
        return token;
    }

    private void nextCh() {
        switch (ch) {
            case EOF_CH:
                return;
            case '\n':
                column = 1;
                line++;
                break;
            default:
                column++;
        }
        try {
            ch = (char) in.read();
            oldch = ((oldch == '\r') && (ch == '\n')) ? (char) in.read() : ch;
            ch = (oldch == '\r') ? '\n' : oldch;
        } catch (IOException exception) {
            global.fatal(Position.encode(line, column), exception);
        }
    }
}
