package unity.graphics;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Angles;
import arc.math.Mathf;
import arc.struct.FloatSeq;
import arc.util.Time;
import mindustry.Vars;

public class FixedTrail {
    public int length;
    private final FloatSeq points;
    private float lastX = -1.0F;
    private float lastY = -1.0F;
    private float counter = 0.0F;

    public FixedTrail(int length) {
        this.length = length;
        this.points = new FloatSeq(length * 4);
    }

    public FixedTrail copy() {
        FixedTrail out = new FixedTrail(this.length);
        out.points.addAll(this.points);
        out.lastX = this.lastX;
        out.lastY = this.lastY;
        return out;
    }

    public void clear() {
        this.points.clear();
    }

    public int size() {
        return this.points.size / 4;
    }

    public void drawCap(Color color, float width) {
        if (this.points.size > 0) {
            Draw.color(color);
            float[] items = this.points.items;
            int i = this.points.size - 4;
            float x1 = items[i];
            float y1 = items[i + 1];
            float w1 = items[i + 2];
            float ai = items[i + 3];
            float w = w1 * width / (float)(this.points.size / 4) * (float)i / 4.0F * 2.0F;
            if (w1 <= 0.001F) {
                return;
            }

            Draw.rect("hcircle", x1, y1, w, w, -57.295776F * ai + 180.0F);
            Draw.reset();
        }

    }

    public void draw(Color color, float width) {
        Draw.color(color);
        float[] items = this.points.items;

        for(int i = 0; i < this.points.size - 4; i += 4) {
            float x1 = items[i];
            float y1 = items[i + 1];
            float w1 = items[i + 2];
            float a1 = items[i + 3];
            float x2 = items[i + 4];
            float y2 = items[i + 5];
            float w2 = items[i + 6];
            float a2 = items[i + 7];
            float size = width / (float)(this.points.size / 4);
            if (!(w1 <= 0.001F) && !(w2 <= 0.001F)) {
                float cx = Mathf.sin(a1) * (float)i / 4.0F * size * w1;
                float cy = Mathf.cos(a1) * (float)i / 4.0F * size * w1;
                float nx = Mathf.sin(a2) * ((float)i / 4.0F + 1.0F) * size * w2;
                float ny = Mathf.cos(a2) * ((float)i / 4.0F + 1.0F) * size * w2;
                Fill.quad(x1 - cx, y1 - cy, x1 + cx, y1 + cy, x2 + nx, y2 + ny, x2 - nx, y2 - ny);
            }
        }

        Draw.reset();
    }

    public void shorten() {
        if (Vars.state.isPlaying() && (this.counter += Time.delta) >= 0.99F) {
            if (this.points.size >= 4) {
                this.points.removeRange(0, 3);
            }

            this.counter = 0.0F;
        }

    }

    public void update(float x, float y) {
        this.update(x, y, Angles.angle(x, y, this.lastX, this.lastY));
    }

    public void update(float x, float y, float rotation) {
        this.update(x, y, 1.0F, rotation);
    }

    public void update(float x, float y, float width, float rotation) {
        if (Vars.state.isPlaying() && (this.counter += Time.delta) >= 0.99F) {
            if (this.points.size > this.length * 4) {
                this.points.removeRange(0, 3);
            }

            this.points.add(x, y, width, -rotation * ((float)Math.PI / 180F));
            this.counter = 0.0F;
            this.lastX = x;
            this.lastY = y;
        }

    }
}
