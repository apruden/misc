package misc;

public final class Scope {
    private final Global global;

    private final Scope outer;

    public Symbol first;

    public Scope(Global global) {
        this.global = global;
        this.outer = null;
    }

    public Scope(Scope outer) {
        this.global = outer.global;
        this.outer = outer;
    }

    public void enter(Symbol symbol) {
        Symbol other = lookupLocal(symbol.name);
        if (other != null)
            global.error(symbol.pos, symbol.name + " already defined in line " +
                    Position.line(other.pos));
        symbol.next = first;
        first = symbol;
    }

    public Symbol lookup(String name) {
        for (Scope scope = this; scope != null; scope = scope.outer) {
            Symbol symbol = scope.lookupLocal(name);
            if (symbol != null) return symbol;
        }
        return null;
    }

    private Symbol lookupLocal(String name) {
        for (Symbol symbol = first; symbol != null; symbol = symbol.next)
            if (symbol.name.equals(name)) return symbol;
        return null;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("{");
        for (Symbol symbol = first; symbol != null; symbol = symbol.next) {
            if (symbol != first) buffer.append(", ");
            buffer.append(symbol);
        }
        if (outer != null) {
            if (first != null) buffer.append(", ");
            buffer.append(outer);
        }
        buffer.append("}");
        return buffer.toString();
    }
}
