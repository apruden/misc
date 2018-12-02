package misc;

public class Type {

    public static final Type NONE = new Type();

    public static final Type BAD = new Type();

    public static final Type BOTTOM = new Type();

    public static final Type UNIT = new Type();

    public static final Type INT = new Type();

    public static final Type NIL = new ListType(BOTTOM);

    public static boolean areComparable(Type thiz, Type that) {
        return thiz.isSubtypeOf(that) || that.isSubtypeOf(thiz);
    }

    public static Type max(Type thiz, Type that) {
        if (thiz.isSubtypeOf(that)) return that;
        if (that.isSubtypeOf(thiz)) return thiz;
        return BAD;
    }

    public boolean equals(Object that) {
        return this == that;
    }

    public boolean isSubtypeOf(Type that) {
        if (this == BAD) return true;
        if (this == BOTTOM) return that != BAD;
        return this.equals(that);
    }

    public String toString() {
        if (this == NONE) return "<none>";
        if (this == BAD) return "<bad>";
        if (this == BOTTOM) return "<bottom>";
        if (this == UNIT) return "Unit";
        if (this == INT) return "Int";
        throw new Error("type inconnu");
    }

    public static class ListType extends Type {
        public final Type type;

        public ListType(Type type) {
            this.type = type;
        }

        public boolean equals(Type that) {
            if (that instanceof Type.ListType) {
                return this.type.equals(((Type.ListType) that).type);
            } else {
                return false;
            }
        }

        public boolean isSubtypeOf(Type that) {
            if (that instanceof Type.ListType) {
                return this.type.isSubtypeOf(((Type.ListType) that).type);
            } else {
                return false;
            }

        }

        public String toString() {
            String s = "List[" + this.type.toString() + "]";
            return s;
        }
    }

    public static class FunType extends Type {
        public final Type[] args;
        public final Type type;

        public FunType(Type[] args, Type type) {
            this.args = args;
            this.type = type;
        }

        public boolean equals(Type that) {
            if (that instanceof Type.FunType) {
                if (this.args.length == ((Type.FunType) that).args.length) {
                    for (int i = 0; i < ((Type.FunType) that).args.length; i++) {
                        if (!this.args[i].equals(((Type.FunType) that).args[i])) return false;
                    }
                } else {
                    return false;
                }
                return ((Type.FunType) that).type.equals(this.type);
            } else {
                return false;
            }
        }

        public boolean isSubtypeOf(Type that) {
            if (that instanceof Type.FunType) {
                if (this.args.length == ((Type.FunType) that).args.length) {
                    for (int i = 0; i < ((Type.FunType) that).args.length; i++) {
                        if (!this.args[i].equals(((Type.FunType) that).args[i])) return false;
                    }
                } else {
                    return false;
                }
                return ((Type.FunType) that).type.equals(this.type);
            } else {
                return false;
            }
        }

        public String toString() {
            StringBuilder s = new StringBuilder("(");

            for (int i = 0; i < args.length; i++) {
                s.append(args[i].toString());
                if (i != args.length - 1) s.append(',');
            }

            s.append(")").append(type.toString());

            return s.toString();
        }
    }
}
