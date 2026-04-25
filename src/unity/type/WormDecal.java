package unity.type;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import mindustry.gen.Unit;

public class WormDecal {
    private static final Vec2 v1 = new Vec2();
    public float baseX;
    public float baseY;
    public float endX;
    public float endY;
    public float baseOffset;
    public int segments = 1;
    public Color lineColor;
    public float lineWidth;
    String name;
    public TextureRegion baseRegion;
    public TextureRegion endRegion;
    public TextureRegion[] segmentRegions;

    public WormDecal(String name) {
        this.lineColor = Color.white;
        this.lineWidth = 2.0F;
        this.name = name;
    }

    public void load() {
        this.baseRegion = Core.atlas.find(this.name + "-base");
        this.endRegion = Core.atlas.find(this.name + "-end");
        this.segmentRegions = new TextureRegion[this.segments];

        for(int i = 0; i < this.segmentRegions.length; ++i) {
            this.segmentRegions[i] = Core.atlas.find(this.name + "-" + i);
        }

    }

    public void draw(Unit base, Unit other) {
        if (other != null) {
            for(int s : Mathf.signs) {
                v1.trns(base.rotation - 90.0F, this.baseX * (float)s, this.baseY).add(base);
                float bx = v1.x;
                float by = v1.y;
                v1.trns(other.rotation - 90.0F, this.endX * (float)s, this.endY).add(other);
                float ex = v1.x;
                float ey = v1.y;
                float angle = Angles.angle(bx, by, ex, ey);
                Draw.mixcol();
                Draw.color(this.lineColor);
                Fill.circle(bx, by, this.lineWidth / 2.0F);
                Fill.circle(ex, ey, this.lineWidth / 2.0F);
                Lines.stroke(this.lineWidth);
                Lines.line(bx, by, ex, ey, false);
                base.type.applyColor(base);
                v1.trns(angle + 180.0F, (float)this.endRegion.width * Draw.scl * 0.5F - this.baseOffset).add(ex, ey);
                ex = v1.x;
                ey = v1.y;
                v1.trns(angle, (float)this.baseRegion.width * Draw.scl * 0.5F - this.baseOffset).add(bx, by);
                bx = v1.x;
                by = v1.y;

                for(int i = this.segmentRegions.length - 1; i >= 0; --i) {
                    TextureRegion r = this.segmentRegions[i];
                    float p = ((float)i + 1.0F) / ((float)this.segments + 1.0F);
                    v1.set(bx, by).lerp(ex, ey, p);
                    Draw.rect(r, v1.x, v1.y, angle);
                }

                Draw.rect(this.endRegion, ex, ey, angle + 180.0F);
                Draw.rect(this.baseRegion, bx, by, angle);
            }

            Draw.reset();
        }
    }
}
