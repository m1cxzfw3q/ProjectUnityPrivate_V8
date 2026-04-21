package unity.util;

public class Wrappers {
    public static class NumberWrapper<T extends Number> {
        public T val;

        public NumberWrapper() {
            this.reset();
        }

        public NumberWrapper(T val) {
            this.val = val;
        }

        public NumberWrapper(NumberWrapper<T> val) {
            this.val = val.val;
        }

        public void reset() {
            this.val = (T)0;
        }
    }

    public static class ObjectWrapper<T> {
        public T val;

        public ObjectWrapper() {
            this.reset();
        }

        public ObjectWrapper(T val) {
            this.val = val;
        }

        public ObjectWrapper(ObjectWrapper<T> val) {
            this.val = val.val;
        }

        public void reset() {
            this.val = null;
        }
    }
}
