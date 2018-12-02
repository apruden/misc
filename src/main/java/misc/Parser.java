package misc;

import misc.Tree.*;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;


public class Parser extends Scanner {

    private int nb_tokens = 0;

    public Parser(Global global, InputStream in) {
        super(global, in);
    }

    private Error error(int expected) {
        return error("token of class " + tokenClass(expected));
    }

    private Error error(String expected) {
        String message = "Syntax error\n" +
                "expected : " + expected + "\n" +
                "found  : token " + representation();
        return global.fatal(start, message);
    }

    private boolean acceptIf(int expected) {
        if (token == expected) {
            nextToken();
            nb_tokens++;
            return true;
        } else {
            return false;
        }
    }

    private void accept(int expected) {
        if (!acceptIf(expected)) error(expected);
    }

    public Tree parse() {
        return parseProgram();
    }

    /**
     * Program  = { Declaration ";" } Expression.
     */
    private Tree parseProgram() {
        int pos = start;
        List list = new LinkedList();

        while (token == DEF) {
            list.add(parseDeclaration());
            accept(SEMICOLON);
        }
        Tree main = parseExpression();
        Tree[] funs = Tree.toArray(list);
        accept(EOF);
        return new Program(pos, funs, main);
    }


    /**
     * Declaration    = "def" ident "(" [ Formals ] ")" ":" Type "="Expression.
     */

    private Tree parseDeclaration() {
        int pos = start;
        List list = new LinkedList();
        Tree[] args = Tree.toArray(list);

        accept(DEF);
        String name = chars;
        accept(IDENT);
        accept(LPAREN);
        if (token == IDENT) {
            args = parseFormals();
        }
        accept(RPAREN);
        accept(COLON);
        Tree result = parseType();
        accept(AFFECT);
        Tree body = parseExpression();
        return new FunDecl(pos, name, args, result, body);
    }


    /**
     * Formals  = Formal { "," Formal }.
     */

    private Tree[] parseFormals() {
        int pos = start;
        List list = new LinkedList();

        list.add(parseFormal());
        while (acceptIf(COMMA)) {
            list.add(parseFormal());
        }
        Tree[] args = Tree.toArray(list);
        return args;
    }

    /**
     * Formal         = ident ":" Type.
     */

    private Tree parseFormal() {
        int pos = start;
        String name = chars;
        accept(IDENT);
        accept(COLON);
        Tree type = parseType();
        return new Formal(pos, name, type);
    }

    /**
     * Type           = "Unit"
     * | "Int"
     * | "List" "[" Type "]"
     * | "(" [ Type { "," Type } ] ")" Type.
     */

    private Tree parseType() {
        int pos = start;
        Tree typeT = null;
        switch (token) {
            case UNIT:
                accept(UNIT);
                typeT = new UnitType(pos);
                break;
            case INT:
                accept(INT);
                typeT = new IntType(pos);
                break;
            case LIST:
                accept(LIST);
                accept(LBRACK);
                Tree elements = parseType();
                accept(RBRACK);
                typeT = new ListType(pos, elements);
                break;
            case LPAREN:
                List list = new LinkedList();
                accept(LPAREN);
                if (token == UNIT || token == INT || token == LIST ||
                        token == LPAREN) {
                    list.add(parseType());
                    while (acceptIf(COMMA)) {
                        list.add(parseType());
                    }
                }
                accept(RPAREN);
                Tree result = parseType();
                Tree[] args = Tree.toArray(list);
                typeT = new FunType(pos, args, result);
                break;
            default:
                error("Type");
        }
        return typeT;
    }

    /* Statement      = "var" Formal "=" Expression
     *                | "while" "(" Expression ")" Expression
     *                | Expression.
     */
    private Tree parseStatement() {
        int pos = start;
        Tree statementT = null;
        switch (token) {
            case VAR:
                accept(VAR);
                Formal formal = (Formal) parseFormal();
                accept(AFFECT);
                Tree value = parseExpression();
                statementT = new VarDecl(pos, formal.name, formal.type, value);
                break;
            case WHILE:
                accept(WHILE);
                accept(LPAREN);
                Tree cond = parseExpression();
                accept(RPAREN);
                Tree body = parseExpression();
                statementT = new While(pos, cond, body);
                break;
            case IF:
            case IDENT:
            case NUMBER:
            case STRING:
            case LPAREN:
            case LBRACE:
            case NOT:
            case HEAD:
            case TAIL:
            case ISEMPTY:
            case LBRACK:
            case MINUS:
                Tree exp = parseExpression();
                statementT = new Exec(pos, exp);
                break;
            default:
                error("Statement");
        }
        return statementT;
    }

    /**
     * Expression     = "if" "(" Expression ")" Expression [ "else" Expression]
     * | ident "=" Expression
     * | OrExpression.
     */
    private Tree parseExpression() {
        int pos = start;
        Tree expressionT = null;

        switch (token) {
            case IF:
                accept(IF);
                accept(LPAREN);
                Tree cond = parseExpression();
                accept(RPAREN);
                Tree thenp = parseExpression();
                Tree elsep = new UnitLit(pos);
                if (acceptIf(ELSE)) {
                    elsep = parseExpression();
                }
                expressionT = new If(pos, cond, thenp, elsep);
                break;

//replacing sub-expression :
            /*
             * Expression     = "if" "(" Expression ")" Expression [ "else" Expression]
             *                | OrExpression ["=" Expression]
             */
/*                case IDENT:case NUMBER:case STRING:case LBRACK:case NOT:
                case HEAD:case TAIL:case ISEMPTY:case LPAREN:case LBRACE:case MINUS:
                    expressionT = parseOrExpression();
                    if(token==AFFECT) {
                        if(expressionT instanceof Tree.Ident){
                            accept(AFFECT);
                            expressionT= new Assign(pos,((Ident)expressionT).name,parseExpression());
                        }
                        else{
                            error("Expression");
                        }
                    }
                    break;*/


//with token counter:

            case IDENT:
            case NUMBER:
            case STRING:
            case LBRACK:
            case NOT:
            case HEAD:
            case TAIL:
            case ISEMPTY:
            case LPAREN:
            case LBRACE:
            case MINUS:
                if (token == IDENT) {
                    int temp = nb_tokens;
                    expressionT = parseOrExpression();
                    if (token == AFFECT) {
                        if (nb_tokens == temp + 1) {
                            accept(AFFECT);
                            expressionT = new Assign(pos, ((Ident) expressionT).name, parseExpression());
                        } else {
                            error("Expression");
                        }
                    }
                } else {
                    expressionT = parseOrExpression();
                }
                break;
            default:
                error("Expression");
        }
        return expressionT;
    }

    /**
     * OrExpression   = AndExpression
     * | OrExpression "|" AndExpression.
     * <p>
     * OrExpression   = AndExpression {"|" AndExpression}
     */
    private Tree parseOrExpression() {
        int pos = start;
        Tree orexpression2 = null;
        Tree True = new IntLit(pos, 1);

        Tree orexpressionT = parseAndExpression();
        while (acceptIf(OR)) {
            orexpression2 = parseAndExpression();
            orexpressionT = new If(pos, orexpressionT, True, orexpression2);
        }
        return orexpressionT;
    }

    /* AndExpression  = CmpExpression
     *                | AndExpression "&" CmpExpression.
     *
     * AndExpression  = CmpExpression {"&" CmpExpression}
     *
     */
    private Tree parseAndExpression() {
        int pos = start;
        Tree andexpression2 = null;
        Tree False = new IntLit(pos, 0);

        Tree andexpressionT = parseCmpExpression();
        while (acceptIf(AND)) {
            andexpression2 = parseCmpExpression();
            andexpressionT = new If(pos, andexpressionT, andexpression2, False);
        }
        return andexpressionT;
    }

    /* CmpExpression  = ListExpression [ CompOp ListExpression ].
     */
    private Tree parseCmpExpression() {
        int pos = start;
        int opT = 0;
        Tree expression2 = null;

        Tree cmpT = parseListExpression();
        if (token == EQU || token == NOTEQ || token == LESS || token == GREATER
                || token == LESSOREQ || token == GREATEROREQ) {
            opT = parseCompOp();
            expression2 = parseListExpression();
            cmpT = new Operation(pos, opT, cmpT, expression2);
        }
        return cmpT;
    }

    /* CompOp         = "==" | "!=" | "<" | ">" | "<=" | ">=".
     */
    private int parseCompOp() {
        int compOp = 0;
        switch (token) {
            case EQU:
                accept(EQU);
                compOp = EQU;
                break;
            case NOTEQ:
                accept(NOTEQ);
                compOp = NOTEQ;
                break;
            case LESS:
                accept(LESS);
                compOp = LESS;
                break;
            case GREATER:
                accept(GREATER);
                compOp = GREATER;
                break;
            case LESSOREQ:
                accept(LESSOREQ);
                compOp = LESSOREQ;
                break;
            case GREATEROREQ:
                accept(GREATEROREQ);
                compOp = GREATEROREQ;
                break;
            default:
                error("CompOp");
        }
        return compOp;
    }

    /* ListExpression = SumExpression
     *                | SumExpression "::" ListExpression.
     *
     * ListExpression = SumExpression ["::" ListExpression]
     */
    private Tree parseListExpression() {
        int pos = start;

        Tree listExpressionT = parseSumExpression();
        if (acceptIf(CONS)) {
            listExpressionT = new Operation(pos, CONS, listExpressionT, parseListExpression());
        }
        return listExpressionT;
    }

    /* SumExpression  = Term
     *                | SumExpression SumOp Term.
     *
     * SumExpression = Term {SumOp Term}
     */
    private Tree parseSumExpression() {
        int pos = start;
        Tree expression2 = null;
        int op = 0;

        Tree sumT = parseTerm();
        while (token == PLUS || token == MINUS) {
            op = parseSumOp();
            expression2 = parseTerm();
            sumT = new Operation(pos, op, sumT, expression2);
        }
        return sumT;
    }

    /* SumOp          = "+" | "-".
     */
    private int parseSumOp() {

        int sumOpT = 0;
        switch (token) {
            case PLUS:
                accept(PLUS);
                sumOpT = PLUS;
                break;
            case MINUS:
                accept(MINUS);
                sumOpT = MINUS;
                break;
            default:
                error("parseSumOp");
        }
        return sumOpT;
    }

    /* Term           = [ "-" ] Factor
     *                | Term ProdOp [ "-" ] Factor.
     *
     *  Term       = ["-"] Factor {ProdOp ["-"] Factor}
     *
     */
    private Tree parseTerm() {
        int pos = start;
        Tree termT = null;
        Tree right = null;
        int op = 0;

        if (token == MINUS) {
            accept(MINUS);
            termT = new Operation(pos, MINUS, new IntLit(pos, 0), parseFactor());
        } else {
            termT = parseFactor();
        }
        while (token == MUL || token == DIV || token == MOD) {
            op = parseProdOp();
            if (token == MINUS) {
                accept(MINUS);
                right = new Operation(pos, MINUS, new IntLit(pos, 0), parseFactor());
            } else {
                right = parseFactor();
            }
            termT = new Operation(pos, op, termT, right);
        }
        return termT;
    }

    /* ProdOp         = "*" | "/" | "%".
     */
    private int parseProdOp() {
        int prodOp = 0;
        switch (token) {
            case MUL:
                accept(MUL);
                prodOp = MUL;
                break;
            case DIV:
                accept(DIV);
                prodOp = DIV;
                break;
            case MOD:
                accept(MOD);
                prodOp = MOD;
                break;
            default:
                error("ProdOp");
        }

        return prodOp;
    }

    /* Factor         = ident
     *                | number
     *                | string
     *                | "true"
     *                | "false"
     *                | "[" [ Expressions ] "]"
     *                | "!" Factor
     *                | "head" "(" Expression ")"
     *                | "tail" "(" Expression ")"
     *                | "isEmpty" "(" Expression ")"
     *                | "(" ")"
     *                | "(" Expression ")"
     *                | "{" { Statement ";" } [ Expression ] "}"
     *                | Factor "(" [ Expressions ] ")".
     *
     *
     *Factor         = (ident
     *                | number
     *                | string
     *                | "true"
     *                | "false"
     *                | "[" [ Expressions ] "]"
     *                | "!" Factor
     *                | "head" "(" Expression ")"
     *                | "tail" "(" Expression ")"
     *                | "isEmpty" "(" Expression ")"
     *                | "{" { Statement ";" } [ Expression ] "}"
     *                | "("[Expression]")"
     *                ) { "(" [ Expressions ] ")"}
     */
    private Tree parseFactor() {
        int pos = start;
        Tree factorT = null;

        switch (token) {
            case IDENT:
                factorT = new Ident(pos, chars);
                accept(IDENT);
                break;
            case NUMBER:
                factorT = new IntLit(pos, Integer.parseInt(chars));
                accept(NUMBER);
                break;
            case STRING:
                Tree stringT = new NilLit(pos);
                char[] code = chars.toCharArray();
                for (int i = code.length - 1; i >= 0; i--) {
                    stringT = new Operation(pos, CONS, new IntLit(pos, (int) code[i]), stringT);
                }
                factorT = stringT;
                accept(STRING);
                break;
            case TRUE:
                accept(TRUE);
                factorT = new IntLit(pos, 1);
                break;
            case FALSE:
                accept(FALSE);
                factorT = new IntLit(pos, 0);
                break;
            case LBRACK:
                Tree listConst = new NilLit(pos);
                Tree[] arrayExpressions = null;
                accept(LBRACK);
                if (token == IF || token == IDENT || token == NUMBER || token == STRING ||
                        token == LBRACK || token == NOT || token == HEAD || token == TAIL ||
                        token == ISEMPTY || token == LPAREN || token == LBRACE || token == MINUS) {
                    arrayExpressions = parseExpressions();
                    if (arrayExpressions != null) {
                        for (int i = arrayExpressions.length - 1; i >= 0; i--) {
                            listConst = new Operation(pos, CONS, arrayExpressions[i], listConst);
                        }
                    }
                }
                factorT = listConst;
                accept(RBRACK);
                break;
            case NOT:
                accept(NOT);
                Tree cond = parseFactor();
                factorT = new If(pos, cond, new IntLit(pos, 0), new IntLit(pos, 1));
                break;
            case HEAD:
                accept(HEAD);
                accept(LPAREN);
                Tree left = parseExpression();
                factorT = new Operation(pos, HEAD, left, null);
                accept(RPAREN);
                break;
            case TAIL:
                accept(TAIL);
                accept(LPAREN);
                left = parseExpression();
                factorT = new Operation(pos, TAIL, left, null);
                accept(RPAREN);
                break;
            case ISEMPTY:
                accept(ISEMPTY);
                accept(LPAREN);
                left = parseExpression();
                factorT = new Operation(pos, ISEMPTY, left, null);
                accept(RPAREN);
                break;
            case LBRACE:
                List list = new LinkedList();
                Tree unitLit = new UnitLit(pos);
                Tree expression = unitLit;
                boolean test_brace = false;

                accept(LBRACE);
                while (token == VAR || token == WHILE || token == IF || token == IDENT ||
                        token == NUMBER || token == STRING || token == LBRACK || token == NOT ||
                        token == HEAD || token == TAIL || token == ISEMPTY || token == LPAREN ||
                        token == LBRACE || token == MINUS) {
                    if (token == VAR || token == WHILE) {
                        list.add(parseStatement());
                        accept(SEMICOLON);
                    } else {
                        expression = parseExpression();
                        if (!acceptIf(SEMICOLON)) {
                            if (acceptIf(RBRACE)) {
                                test_brace = true;
                                break;
                            } else {
                                error(SEMICOLON);
                            }
                        }
                        list.add(expression);
                        expression = unitLit;
                    }
                }
                Tree[] statements = Tree.toArray(list);
                if (!test_brace) {
                    accept(RBRACE);
                }
                factorT = new Block(pos, statements, expression);
                break;
            case LPAREN:
                factorT = new UnitLit(pos);
                accept(LPAREN);
                if (token == IF || token == IDENT || token == NUMBER || token == STRING ||
                        token == LBRACK || token == NOT || token == HEAD || token == TAIL ||
                        token == ISEMPTY || token == LPAREN || token == LBRACE || token == MINUS) {
                    factorT = parseExpression();
                }
                accept(RPAREN);
                break;
            default:
                error("Factor");
        }

        Tree[] expressions = null;

        while (token == LPAREN) {
            accept(LPAREN);

            if (token == IF || token == IDENT || token == NUMBER || token == STRING ||
                    token == LBRACK || token == NOT || token == HEAD || token == TAIL ||
                    token == ISEMPTY || token == LPAREN || token == LBRACE || token == MINUS) {
                expressions = parseExpressions();
                factorT = new FunCall(pos, factorT, expressions);
            } else {
                factorT = new FunCall(pos, factorT, new Tree[]{});
            }

            accept(RPAREN);
        }

        return factorT;
    }

    /* Expressions    = Expression { "," Expression }.
     */
    private Tree[] parseExpressions() {
        List list = new LinkedList();
        list.add(parseExpression());
        while (acceptIf(COMMA)) {
            list.add(parseExpression());
        }
        Tree[] expressions = Tree.toArray(list);
        return expressions;
    }
}
