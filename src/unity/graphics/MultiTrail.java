package unity.graphics;

import arc.graphics.Color;
import arc.math.Angles;
import arc.util.Tmp;
import mindustry.gen.Rotc;
import mindustry.graphics.Trail;

public class MultiTrail extends Trail {
    public TrailHold[] trails;
    public RotationHandler rotation;
    protected float lastX;
    protected float lastY;

    public MultiTrail(RotationHandler rotation, TrailHold... trails) {
        this(trails);
        this.rotation = rotation;
    }

    public MultiTrail(TrailHold... trails) {
        super(0);
        this.rotation = MultiTrail::calcRot;
        this.trails = trails;

        for(TrailHold trail : trails) {
            this.length = Math.max(this.length, trail.trail.length);
        }

    }

    public static RotationHandler rot(Rotc e) {
        return (trail, x, y) -> e.isAdded() ? e.rotation() : trail.calcRot(x, y);
    }

    public MultiTrail copy() {
        TrailHold[] mapped = new TrailHold[this.trails.length];

        for(int i = 0; i < mapped.length; ++i) {
            mapped[i] = this.trails[i].copy();
        }

        MultiTrail out = new MultiTrail(this.rotation, mapped);
        out.lastX = this.lastX;
        out.lastY = this.lastY;
        return out;
    }

    public void clear() {
        for(TrailHold trail : this.trails) {
            trail.trail.clear();
        }

    }

    public int size() {
        int size = 0;

        for(TrailHold trail : this.trails) {
            size = Math.max(size, trail.trail.size());
        }

        return size;
    }

    public void drawCap(Color color, float width) {
        for(TrailHold trail : this.trails) {
            trail.trail.drawCap(trail.color == null ? color : trail.color, width);
        }

    }

    public void draw(Color color, float width) {
        for(TrailHold trail : this.trails) {
            trail.trail.draw(trail.color == null ? color : trail.color, width);
        }

    }

    public void shorten() {
        for(TrailHold trail : this.trails) {
            trail.trail.shorten();
        }

    }

    public void update(float x, float y, float width) {
        float angle = this.rotation.get(this, x, y) - 90.0F;

        for(TrailHold trail : this.trails) {
            Tmp.v1.trns(angle, trail.x, trail.y);
            trail.trail.update(x + Tmp.v1.x, y + Tmp.v1.y, width * trail.width);
        }

        this.lastX = x;
        this.lastY = y;
    }

    public float calcRot(float x, float y) {
        return Angles.angle(this.lastX, this.lastY, x, y);
    }

    public static class TrailHold {
        public Trail trail;
        public float x;
        public float y;
        public float width;
        public Color color;

        public TrailHold(Trail trail) {
            this(trail, 0.0F, 0.0F, 1.0F, (Color)null);
        }

        public TrailHold(Trail trail, Color color) {
            this(trail, 0.0F, 0.0F, 1.0F, color);
        }

        public TrailHold(Trail trail, float x, float y) {
            this(trail, x, y, 1.0F, (Color)null);
        }

        public TrailHold(Trail trail, float x, float y, float width) {
            this.trail = trail;
            this.x = x;
            this.y = y;
            this.width = width;
        }

        public TrailHold(Trail trail, float x, float y, float width, Color color) {
            this.trail = trail;
            this.x = x;
            this.y = y;
            this.width = width;
            this.color = color;
        }

        public TrailHold copy() {
            return new TrailHold(this.trail.copy(), this.x, this.y, this.width, this.color);
        }
    }

    public interface RotationHandler {
        float get(MultiTrail var1, float var2, float var3);
    }
}
