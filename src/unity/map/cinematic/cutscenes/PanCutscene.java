package unity.map.cinematic.cutscenes;

import arc.Core;
import arc.func.Cons;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Position;
import arc.math.geom.Vec2;
import arc.util.Time;
import unity.gen.Float2;
import unity.map.cinematic.Cutscene;

public class PanCutscene extends Cutscene {
    public final Cutscene.Pos pos;
    public final Interp interp;
    public final float delay;
    public final float panDuration;
    public final float endDelay;
    private Vec2 initPos;
    private float initTime;
    private Cons<PanCutscene> moved;
    private Cons<PanCutscene> arrived;
    private float movedThreshold;
    private float arrivedThreshold;

    public PanCutscene(Cutscene.Pos pos, float delay, float panDuration, float endDelay, Interp interp) {
        this.initTime = -1.0F;
        this.movedThreshold = 5.0F;
        this.arrivedThreshold = 8.0F;
        this.pos = pos;
        this.interp = interp;
        this.delay = delay;
        this.panDuration = panDuration;
        this.endDelay = endDelay;
    }

    public PanCutscene(Cutscene.Pos pos) {
        this(pos, 15.0F, 90.0F, 60.0F, Interp.smoother);
    }

    public PanCutscene(Position pos) {
        this((Cutscene.Pos)(() -> Float2.construct(pos.getX(), pos.getY())));
    }

    public PanCutscene(Cutscene.Pos pos, float panDuration) {
        this(pos, 15.0F, panDuration, 60.0F, Interp.smooth2);
    }

    public PanCutscene(Position pos, float panDuration) {
        this((Cutscene.Pos)(() -> Float2.construct(pos.getX(), pos.getY())), 15.0F, panDuration, 60.0F, Interp.smooth2);
    }

    public PanCutscene(Cutscene.Pos pos, float panDuration, Interp interp) {
        this(pos, 15.0F, panDuration, 60.0F, interp);
    }

    public PanCutscene(Position pos, float panDuration, Interp interp) {
        this((Cutscene.Pos)(() -> Float2.construct(pos.getX(), pos.getY())), 15.0F, panDuration, 60.0F, interp);
    }

    public PanCutscene(Position pos, float delay, float panDuration, float endDelay, Interp interp) {
        this((Cutscene.Pos)(() -> Float2.construct(pos.getX(), pos.getY())), delay, panDuration, endDelay, interp);
    }

    public <T extends PanCutscene> T moved(Cons<T> moved, float threshold) {
        this.moved = moved;
        this.movedThreshold = threshold;
        return (T)this;
    }

    public <T extends PanCutscene> T arrived(Cons<T> arrived, float threshold) {
        this.arrived = arrived;
        this.arrivedThreshold = threshold;
        return (T)this;
    }

    public boolean update() {
        float elapsed = Time.time - this.startTime();
        if (this.moved != null && elapsed >= this.delay - this.movedThreshold) {
            this.moved.get(this);
            this.moved = null;
        }

        if (elapsed >= this.delay && this.initPos == null) {
            this.initPos = new Vec2(Core.camera.position);
            this.initTime = Time.time;
        }

        float progress = Time.time - this.initTime;
        if (this.initPos != null) {
            long pos = this.pos.get();
            Core.camera.position.set(this.initPos).lerp(Float2.x(pos), Float2.y(pos), this.interp.apply(Mathf.clamp(progress / this.panDuration)));
            if (this.arrived != null && progress >= this.panDuration - this.arrivedThreshold) {
                this.arrived.get(this);
                this.arrived = null;
            }
        }

        return this.initTime > -1.0F && progress > this.panDuration + this.endDelay;
    }
}
