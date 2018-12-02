package misc;

public class Symbol implements Kinds {
    public final int pos;

    public final String name;

    public final int kind;

    public final Type type;

    public int offset;

    public Symbol next;

    public Symbol(int pos, String name, int kind, Type type) {
        if (name == null)
            throw new IllegalArgumentException("name is null");
        this.pos = pos;
        this.name = name;
        this.kind = kind;
        this.type = type;
        this.offset = 0;
    }

    public boolean isFunction() {
        return kind == FUNCTION;
    }

    public boolean isVariable() {
        return kind == VARIABLE;
    }

    public String toString() {
        return name + ": " + type;
    }

}
