package younggamExperimental;

import arc.func.Cons;

public class PartStat {
    public final PartStatType category;
    final Object value;
    final Cons mod;

    public PartStat(PartStatType category, Object value, Cons mod) {
        this.category = category;
        this.value = value;
        this.mod = mod;
    }

    public PartStat(PartStatType category, Object value) {
        this(category, value, null);
    }

    public String asString() {
        return (String)this.value;
    }

    public float asFloat() {
        return (Float)this.value;
    }

    public int asInt() {
        return (Integer)this.value;
    }

    public boolean asBool() {
        return (Boolean)this.value;
    }
}
