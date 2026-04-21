package unity.util;

import arc.func.Prov;
import arc.util.pooling.Pool;

public class BasicPool<T> extends Pool<T> {
    Prov<T> prov;

    public BasicPool(int initialCapacity, Prov<T> prov) {
        super(initialCapacity, 5000);
        this.prov = prov;
    }

    public BasicPool(int initialCapacity, int max, Prov<T> prov) {
        super(initialCapacity, max);
        this.prov = prov;
    }

    protected T newObject() {
        return (T)this.prov.get();
    }
}
