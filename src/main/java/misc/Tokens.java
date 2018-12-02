package misc;

public interface Tokens {

    int EOF = -1;
    int BAD = 0;

    int IDENT = 1;
    int NUMBER = 2;
    int STRING = 3;

    int LPAREN = 4; // "("
    int RPAREN = 5; // ")"

    int LBRACE = 6; //"{"
    int RBRACE = 7; //"}"

    int LBRACK = 8; //"["
    int RBRACK = 9; //"]"

    int SEMICOLON = 10; //";"
    int COLON = 11; //":"
    int COMMA = 12; //","

    int CONS = 13; //"::"
    int AFFECT = 14; //"="

    int PLUS = 15; //"+"
    int MINUS = 16; //"-"
    int MUL = 17; //"*"
    int DIV = 18; //"/"
    int MOD = 19; //"%"

    int EQU = 20; //"=="
    int NOTEQ = 21; //"!="
    int LESS = 22; //"<"
    int GREATER = 23; //">"
    int LESSOREQ = 24; //"<="
    int GREATEROREQ = 25; //">="

    int NOT = 26; //"!"
    int OR = 27; //"|"
    int AND = 28; //"&"

    int HEAD = 29; //"head"
    int TAIL = 30; //"tail"
    int ISEMPTY = 31; //"isEmpty"

    int DEF = 32; //"def"

    int UNIT = 33; //"Unit"
    int INT = 34; //"int"
    int LIST = 35; //"List"

    int VAR = 36; //"var"

    int WHILE = 37; //"While"

    int IF = 38; //"If"
    int ELSE = 39; //"else"

    int TRUE = 40; //"true"
    int FALSE = 41; //"false"

}
