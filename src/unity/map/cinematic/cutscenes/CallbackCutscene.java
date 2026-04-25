package unity.map.cinematic.cutscenes;

import arc.util.Time;
import unity.map.cinematic.Cutscene;

public class CallbackCutscene extends Cutscene {
    public float startDelay;
    public float endDelay;
    public float duration;
    public Runnable callback;
    private boolean called;
    private boolean callOnce;

    public CallbackCutscene(float startDelay, float endDelay, float duration, boolean callOnce, Runnable callback) {
        this.startDelay = startDelay;
        this.endDelay = endDelay;
        this.duration = duration;
        this.callback = callback;
        this.callOnce = callOnce;
    }

    public CallbackCutscene(float duration, boolean callOnce, Runnable callback) {
        this(0.0F, 0.0F, duration, callOnce, callback);
    }

    public CallbackCutscene(float duration, Runnable callback) {
        this(0.0F, 0.0F, duration, true, callback);
    }

    public CallbackCutscene(Runnable callback) {
        this(0.0F, 0.0F, 60.0F, true, callback);
    }

    public boolean update() {
        float elapsed = Time.time - this.startTime();
        if (elapsed >= this.startDelay && elapsed <= this.startDelay + this.duration && !this.called) {
            this.callback.run();
            this.called = this.callOnce;
        }

        return elapsed >= this.startDelay + this.duration + this.endDelay;
    }
}
