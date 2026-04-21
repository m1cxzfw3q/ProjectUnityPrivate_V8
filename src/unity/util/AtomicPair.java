package unity.util;

public class AtomicPair<K, V> {
    public volatile K key = null;
    public volatile V value = null;

    public void reset() {
        this.key = null;
        this.value = null;
    }
}
