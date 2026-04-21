package unity.mod;

import arc.Events;
import arc.func.Cons;

public enum Triggers {;
    public static <T> Cons<T> cons(Runnable run) {
        return (e) -> run.run();
    }

    public static <T> Cons<T> listen(T trigger, Runnable run) {
        Cons<T> cons = cons(run);
        listen(trigger, cons);
        return cons;
    }

    public static <T> void listen(T trigger, Cons<T> listener) {
        Events.on(trigger.getClass(), listener);
    }

    public static <T> void detach(T trigger, Cons<T> run) {
        Events.remove(trigger.getClass(), run);
    }
}
