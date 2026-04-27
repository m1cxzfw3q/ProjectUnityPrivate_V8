package unity.type;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.gen.Unit;
import mindustry.graphics.Trail;
import mindustry.type.UnitType;
import unity.gen.CTrailc;

public class Engine {
    public float offset = 5.0F;
    public float size = 2.5F;
    public float trailScale = 1.0F;
    public Color color = null;
    public Color innerColor;
    public boolean drawTrail;

    public Engine() {
        this.innerColor = Color.white;
        this.drawTrail = true;
    }

    public Engine apply(UnitType type) {
        type.engineOffset = this.offset;
        type.engineSize = this.size;
        type.trailScl = this.trailScale;
        type.engineColor = this.color;
        type.engineColorInner = Color.white;
        return this;
    }

    public void draw(Unit unit) {
        this.draw(unit, unit.x, unit.y);
    }

    public void draw(Unit unit, float x, float y) {
        float scale = unit.elevation;
        float offset = this.offset / 2.0F + this.offset / 2.0F * scale;
        Trail var10000;
        if (!this.drawTrail) {
            var10000 = null;
        } else if (unit instanceof CTrailc t) {
            var10000 = t.trail();
        } else {
            var10000 = null;
        }

        Trail trail = var10000;
        if (trail != null) {
            float trailSize = (this.size + Mathf.absin(Time.time, 2.0F, this.size / 4.0F) * scale) * this.trailScale;
            trail.drawCap(unit.team.color, trailSize);
            trail.draw(unit.team.color, trailSize);
        }

        Draw.color(this.color == null ? unit.team.color : this.color);
        Fill.circle(x + Angles.trnsx(unit.rotation + 180.0F, offset), y + Angles.trnsy(unit.rotation + 180.0F, offset), (this.size + Mathf.absin(Time.time, 2.0F, this.size / 4.0F)) * scale);
        Draw.color(this.innerColor);
        Fill.circle(x + Angles.trnsx(unit.rotation + 180.0F, offset - 1.0F), y + Angles.trnsy(unit.rotation + 180.0F, offset - 1.0F), (this.size + Mathf.absin(Time.time, 2.0F, this.size / 4.0F)) / 2.0F * scale);
        Draw.color();
    }

    public static class MultiEngine extends Engine {
        public EngineHold[] engines;

        public MultiEngine(EngineHold... engines) {
            this.engines = engines;

            for(EngineHold engine : this.engines) {
                engine.engine.drawTrail = false;
            }

        }

        public void draw(Unit unit, float x, float y) {
            Trail var10000;
            if (!this.drawTrail) {
                var10000 = null;
            } else if (unit instanceof CTrailc t) {
                var10000 = t.trail();
            } else {
                var10000 = null;
            }

            Trail trail = var10000;
            if (trail != null) {
                float trailSize = (this.size + Mathf.absin(Time.time, 2.0F, this.size / 4.0F) * unit.elevation) * this.trailScale;
                trail.drawCap(unit.team.color, trailSize);
                trail.draw(unit.team.color, trailSize);
            }

            for(EngineHold engine : this.engines) {
                float ox = Angles.trnsx(unit.rotation - 90.0F, engine.offsetX);
                float oy = Angles.trnsy(unit.rotation - 90.0F, engine.offsetX);
                engine.engine.draw(unit, x + ox, y + oy);
            }

        }

        public static class EngineHold {
            public final Engine engine;
            public final float offsetX;

            public EngineHold(Engine engine, float offsetX) {
                this.engine = engine;
                this.offsetX = offsetX;
            }
        }
    }
}
