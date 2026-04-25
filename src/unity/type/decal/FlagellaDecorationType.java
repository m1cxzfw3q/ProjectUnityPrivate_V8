package unity.type.decal;

import arc.Core;
import arc.func.Func;
import arc.graphics.Pixmap;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Tmp;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.type.UnitType;
import unity.util.Utils;

public class FlagellaDecorationType extends UnitDecorationType {
    public float x;
    public float y;
    public float startIntensity = 15.0F;
    public float endIntensity = 40.0F;
    public float swayScl = 40.0F;
    public float swayOffset;
    public float angleLimit = 25.0F;
    public String name;
    float segmentLength;
    int segments;
    TextureRegion[] regions;
    TextureRegion end;

    public FlagellaDecorationType(String name, int textures, int segments, float length) {
        this.name = name;
        this.segments = segments;
        this.segmentLength = length;
        this.regions = new TextureRegion[textures];
        this.decalType = FlagellaDecoration::new;
    }

    public void load() {
        for(int i = 0; i < this.regions.length; ++i) {
            this.regions[i] = Core.atlas.find(this.name + "-" + i);
        }

        this.end = Core.atlas.find(this.name + "-end");
    }

    public void update(Unit unit, UnitDecorationType.UnitDecoration deco) {
        FlagellaDecoration d = (FlagellaDecoration)deco;
        float dLen = unit.deltaLen();
        d.progress += dLen;
        Tmp.v1.trns(unit.rotation - 90.0F, this.x, this.y).add(unit);
        FlagellaSegment c = d.root;

        for(int idx = 0; c != null; ++idx) {
            Tmp.v2.trns(c.tr, dLen);
            c.tx += Tmp.v2.x;
            c.ty += Tmp.v2.y;
            c.length = this.offset(d, idx);
            if (c.prev == null) {
                c.tr = Utils.clampedAngle(Tmp.v1.angleTo(c.tx, c.ty), unit.rotation + 180.0F, this.angleLimit);
                Tmp.v2.trns(c.tr, c.length).add(Tmp.v1);
            } else {
                FlagellaSegment p = c.prev;
                c.tr = Utils.clampedAngle(Angles.angle(p.tx, p.ty, c.tx, c.ty), p.tr, this.angleLimit);
                Tmp.v2.trns(c.tr, c.length).add(p.tx, p.ty);
            }

            c.tx = Tmp.v2.x;
            c.ty = Tmp.v2.y;
            c = c.next;
        }

        int var10 = 0;

        for(FlagellaSegment var9 = d.root; var9 != null; ++var10) {
            float rot = var9.tr + this.swayAngle(d, var10);
            if (var9.prev == null) {
                Tmp.v2.trns(rot, this.segmentLength).add(Tmp.v1);
            } else {
                FlagellaSegment p = var9.prev;
                Tmp.v2.trns(rot, this.segmentLength).add(p.x, p.y);
            }

            var9.x = Tmp.v2.x;
            var9.y = Tmp.v2.y;
            var9 = var9.next;
        }

    }

    public void draw(Unit unit, UnitDecorationType.UnitDecoration deco) {
        FlagellaDecoration d = (FlagellaDecoration)deco;
        FlagellaSegment cur = d.end;
        Tmp.v1.trns(unit.rotation - 90.0F, this.x, this.y).add(unit);
        int regL = this.regions.length - 1;
        int idx = 0;
        UnitType t = unit.type;
        float z = Draw.z();
        float sz = unit.elevation > 0.5F ? (t.lowAltitude ? 90.0F : 115.0F) : t.groundLayer + Mathf.clamp(t.hitSize / 4000.0F, 0.0F, 0.01F);

        for(float var14 = Math.min(sz - 0.01F, 99.0F); cur != null; cur = cur.prev) {
            int reg = Mathf.clamp(regL - Mathf.round((float)idx / (float)this.segments * (float)regL), 0, regL);
            TextureRegion region = cur == d.end ? this.end : this.regions[reg];
            float ssize = (float)Math.max(region.width, region.height) * Draw.scl * 1.6F;
            unit.type.applyColor(unit);
            Lines.stroke((float)region.height * Draw.scl);
            if (cur.prev == null) {
                Tmp.v2.set(cur.x, cur.y).sub(Tmp.v1).setLength((float)region.width * Draw.scl).add(Tmp.v1);
                Draw.z(var14);
                Drawf.shadow((Tmp.v1.x + Tmp.v2.x) / 2.0F, (Tmp.v1.y + Tmp.v2.y) / 2.0F, ssize, 0.6F);
                Draw.z(z);
                Lines.line(region, Tmp.v1.x, Tmp.v1.y, Tmp.v2.x, Tmp.v2.y, false);
            } else {
                FlagellaSegment pr = cur.prev;
                Tmp.v2.set(cur.x, cur.y).sub(pr.x, pr.y).setLength((float)region.width * Draw.scl).add(pr.x, pr.y);
                Draw.z(var14);
                Drawf.shadow((pr.x + Tmp.v2.x) / 2.0F, (pr.y + Tmp.v2.y) / 2.0F, ssize, 0.6F);
                Draw.z(z);
                Lines.line(region, pr.x, pr.y, Tmp.v2.x, Tmp.v2.y, false);
            }

            ++idx;
        }

        Draw.reset();
    }

    float offset(FlagellaDecoration d, int idx) {
        return Mathf.cosDeg(this.swayAngle(d, idx)) * this.segmentLength;
    }

    float swayAngle(FlagellaDecoration d, int idx) {
        return Mathf.sin(d.progress - (float)idx * this.swayOffset, this.swayScl, Mathf.lerp(this.startIntensity, this.endIntensity, (float)idx / ((float)this.segments - 1.0F)));
    }

    public void added(Unit unit, UnitDecorationType.UnitDecoration deco) {
        FlagellaDecoration d = (FlagellaDecoration)deco;
        float ox = Angles.trnsx(unit.rotation + 180.0F, this.segmentLength);
        float oy = Angles.trnsy(unit.rotation + 180.0F, this.segmentLength);
        Tmp.v1.trns(unit.rotation - 90.0F, this.x, this.y).add(unit);
        FlagellaSegment last = null;

        for(int i = 0; i < this.segments; ++i) {
            FlagellaSegment c = new FlagellaSegment();
            c.tx = ox * ((float)i + 1.0F) + Tmp.v1.x;
            c.ty = oy * ((float)i + 1.0F) + Tmp.v1.y;
            c.tr = unit.rotation + 180.0F;
            c.length = this.segmentLength;
            if (last == null) {
                d.root = c;
            } else {
                c.prev = last;
                last.next = c;
            }

            d.end = c;
            last = c;
        }

    }

    public void drawIcon(Func<TextureRegion, Pixmap> prov, Pixmap icon, Func<TextureRegion, TextureRegion> outliner) {
        for(TextureRegion region : this.regions) {
            outliner.get(region);
        }

        outliner.get(this.end);
    }

    static class FlagellaDecoration extends UnitDecorationType.UnitDecoration {
        float progress;
        FlagellaSegment root;
        FlagellaSegment end;

        public FlagellaDecoration(UnitDecorationType type) {
            super(type);
        }
    }

    static class FlagellaSegment {
        float tx;
        float ty;
        float tr;
        float length;
        float x;
        float y;
        FlagellaSegment next;
        FlagellaSegment prev;
    }
}
