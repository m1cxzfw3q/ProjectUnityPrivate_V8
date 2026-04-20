package unity.assets.type.g3d.attribute;

import arc.struct.Seq;

public abstract class Attribute implements Comparable<Attribute> {
    private static final Seq<String> types = new Seq();
    public final long type;
    private final int typeBit;

    protected Attribute(long type) {
        this.type = type;
        this.typeBit = Long.numberOfTrailingZeros(type);
    }

    public static long getAttributeType(String alias) {
        for(int i = 0; i < types.size; ++i) {
            if (((String)types.get(i)).compareTo(alias) == 0) {
                return 1L << i;
            }
        }

        return 0L;
    }

    public static String getAttributeAlias(long type) {
        int idx;
        for(idx = -1; type != 0L && idx < 63 && (type >> idx & 1L) == 0L; ++idx) {
        }

        return idx >= 0 && idx < types.size ? (String)types.get(idx) : null;
    }

    protected static long register(String alias) {
        long result = getAttributeType(alias);
        if (result > 0L) {
            return result;
        } else {
            types.add(alias);
            return 1L << types.size - 1;
        }
    }

    public abstract Attribute copy();

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (obj == this) {
            return true;
        } else if (obj instanceof Attribute) {
            Attribute other = (Attribute)obj;
            return this.type != other.type ? false : this.equals(other);
        } else {
            return false;
        }
    }

    public boolean equals(Attribute other) {
        return other.hashCode() == this.hashCode();
    }

    public String toString() {
        return getAttributeAlias(this.type);
    }

    public int hashCode() {
        return 7489 * this.typeBit;
    }
}
