package unity.world.meta;

import arc.func.Boolc;
import arc.func.Cons;
import arc.func.Floatc;
import arc.math.Interp;
import arc.math.Mathf;
import arc.struct.Seq;

public class DynamicProgression {
    public final Seq<Floatc> listeners = new Seq();

    public void apply(float progress) {
        this.listeners.each((p) -> p.get(progress));
    }

    public Floatc add(Floatc listener) {
        this.listeners.add(listener);
        return listener;
    }

    public Floatc linear(float start, float intensity, Floatc setter) {
        return this.add((progress) -> setter.get(start + progress * intensity));
    }

    public Floatc exponential(float start, float intensity, Floatc setter) {
        return this.add((progress) -> setter.get(start * Mathf.pow(intensity, progress)));
    }

    public Floatc root(float start, float intensity, Floatc setter) {
        return this.add((progress) -> setter.get(start + Mathf.sqrt(intensity * progress)));
    }

    public Floatc bool(boolean start, float threshold, Boolc setter) {
        return this.add((progress) -> setter.get(start == progress < threshold));
    }

    public <T> Floatc list(T[] array, float scale, Interp interp, Cons<T> setter) {
        return this.add((progress) -> setter.get(array[Mathf.clamp(Mathf.floor(interp.apply(progress * scale)), 0, array.length - 1)]));
    }
}
