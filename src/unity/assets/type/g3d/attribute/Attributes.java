package unity.assets.type.g3d.attribute;

import arc.struct.Seq;
import java.util.Comparator;
import java.util.Iterator;

public class Attributes implements Iterable<Attribute>, Comparator<Attribute>, Comparable<Attributes> {
    protected long mask;
    protected final Seq<Attribute> attributes = new Seq();
    protected boolean sorted = true;

    public void sort() {
        if (!this.sorted) {
            this.attributes.sort(this);
            this.sorted = true;
        }

    }

    public long mask() {
        return this.mask;
    }

    public <T extends Attribute> T get(long type) {
        if (this.has(type)) {
            for(int i = 0; i < this.attributes.size; ++i) {
                if (((Attribute)this.attributes.get(i)).type == type) {
                    return (T)(this.attributes.get(i));
                }
            }
        }

        return null;
    }

    public Seq<Attribute> get(Seq<Attribute> out, long type) {
        for(int i = 0; i < this.attributes.size; ++i) {
            if ((((Attribute)this.attributes.get(i)).type & type) != 0L) {
                out.add((Attribute)this.attributes.get(i));
            }
        }

        return out;
    }

    public void clear() {
        this.mask = 0L;
        this.attributes.clear();
    }

    public int size() {
        return this.attributes.size;
    }

    private void enable(long mask) {
        this.mask |= mask;
    }

    private void disable(long mask) {
        this.mask &= ~mask;
    }

    public void set(Attribute attribute) {
        int idx = this.indexOf(attribute.type);
        if (idx < 0) {
            this.enable(attribute.type);
            this.attributes.add(attribute);
            this.sorted = false;
        } else {
            this.attributes.set(idx, attribute);
        }

        this.sort();
    }

    public void set(Attribute... attributes) {
        for(Attribute attr : attributes) {
            this.set(attr);
        }

    }

    public void set(Iterable<Attribute> attributes) {
        for(Attribute attr : attributes) {
            this.set(attr);
        }

    }

    public void remove(long mask) {
        for(int i = this.attributes.size - 1; i >= 0; --i) {
            long type = ((Attribute)this.attributes.get(i)).type;
            if ((mask & type) == type) {
                this.attributes.remove(i);
                this.disable(type);
                this.sorted = false;
            }
        }

        this.sort();
    }

    public boolean has(long type) {
        return type != 0L && (this.mask & type) == type;
    }

    protected int indexOf(long type) {
        if (this.has(type)) {
            for(int i = 0; i < this.attributes.size; ++i) {
                if (((Attribute)this.attributes.get(i)).type == type) {
                    return i;
                }
            }
        }

        return -1;
    }

    public boolean same(Attributes other) {
        return this.same(other, false);
    }

    public boolean same(Attributes other, boolean compare) {
        if (other == this) {
            return true;
        } else if (other != null && this.mask == other.mask) {
            if (!compare) {
                return true;
            } else {
                this.sort();
                other.sort();

                for(int i = 0; i < this.attributes.size; ++i) {
                    if (!((Attribute)this.attributes.get(i)).equals((Attribute)other.attributes.get(i))) {
                        return false;
                    }
                }

                return true;
            }
        } else {
            return false;
        }
    }

    public int compare(Attribute attr1, Attribute attr2) {
        return (int)(attr1.type - attr2.type);
    }

    public Iterator<Attribute> iterator() {
        return this.attributes.iterator();
    }

    public int attributesHash() {
        this.sort();
        int n = this.attributes.size;
        long result = 71L + this.mask;
        int m = 1;

        for(int i = 0; i < n; ++i) {
            result += this.mask * (long)((Attribute)this.attributes.get(i)).hashCode() * (long)(m = m * 7 & '\uffff');
        }

        return (int)(result ^ result >> 32);
    }

    public int hashCode() {
        return this.attributesHash();
    }

    public boolean equals(Object other) {
        if (other instanceof Attributes) {
            Attributes attr = (Attributes)other;
            return other == this ? true : this.same(attr, true);
        } else {
            return false;
        }
    }

    public int compareTo(Attributes other) {
        if (other == this) {
            return 0;
        } else if (this.mask != other.mask) {
            return this.mask < other.mask ? -1 : 1;
        } else {
            this.sort();
            other.sort();

            for(int i = 0; i < this.attributes.size; ++i) {
                int c = ((Attribute)this.attributes.get(i)).compareTo((Attribute)other.attributes.get(i));
                if (c != 0) {
                    return Integer.compare(c, 0);
                }
            }

            return 0;
        }
    }
}
