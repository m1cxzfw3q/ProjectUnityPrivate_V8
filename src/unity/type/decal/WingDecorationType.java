package unity.type.decal;

import arc.Core;
import arc.func.Func;
import arc.graphics.Pixmap;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.struct.Seq;
import mindustry.gen.Unit;
import unity.util.Utils;

public class WingDecorationType extends UnitDecorationType {
    public float flapScl = 120.0F;
    public Interp flapAnimation;
    public Interp flapInterp;
    public TextureRegion[] textures;
    public String name;
    public Seq<Wing> wings;
    public int textureVariants;

    public WingDecorationType(String name, int variants) {
        this.flapAnimation = Interp.pow3Out;
        this.flapInterp = Interp.sine;
        this.wings = new Seq();
        this.decalType = WingDecoration::new;
        this.name = name;
        this.textureVariants = variants;
    }

    public void load() {
        this.textures = new TextureRegion[this.textureVariants];

        for(int i = 0; i < this.textureVariants; ++i) {
            this.textures[i] = Core.atlas.find(this.name + "-" + i);
        }

    }

    public void draw(Unit unit, UnitDecorationType.UnitDecoration deco) {
        WingDecoration wd = (WingDecoration)deco;
        unit.type.applyColor(unit);

        for(Wing wing : this.wings) {
            float l = this.slope((wd.left / this.flapScl + wing.offset) % 1.0F) * -wing.mag;
            float r = this.slope((wd.right / this.flapScl + wing.offset) % 1.0F) * -wing.mag;

            for(int s : Mathf.signs) {
                float side = s > 0 ? r : l;
                float rotation = unit.rotation - 90.0F;
                float x = Angles.trnsx(rotation, wing.x * (float)s, wing.y) + unit.x;
                float y = Angles.trnsy(rotation, wing.x * (float)s, wing.y) + unit.y;
                float wingAngle = rotation + side * (float)s;
                TextureRegion region = this.textures[wing.textureIndex];
                Draw.rect(region, x, y, (float)(region.width * s) * Draw.scl, (float)region.height * Draw.scl, wingAngle);
            }
        }

        Draw.reset();
    }

    public void drawIcon(Func<TextureRegion, Pixmap> prov, Pixmap icon, Func<TextureRegion, TextureRegion> outliner) {
        for(Wing w : this.wings) {
            TextureRegion region = (TextureRegion)outliner.get(this.textures[w.textureIndex]);
            float scl = Draw.scl / 4.0F;
            Pixmap pix = (Pixmap)prov.get(region);
            icon.draw(pix, (int)(w.x / scl + (float)icon.width / 2.0F - (float)pix.width / 2.0F), (int)(-w.y / scl + (float)icon.height / 2.0F - (float)pix.height / 2.0F), true);
            icon.draw(pix.flipX(), (int)(-w.x / scl + (float)icon.width / 2.0F - (float)pix.width / 2.0F), (int)(-w.y / scl + (float)icon.height / 2.0F - (float)pix.height / 2.0F), true);
        }

    }

    float slope(float in) {
        return this.flapInterp.apply((0.5F - Math.abs(this.flapAnimation.apply(in) - 0.5F)) * 2.0F);
    }

    public void update(Unit unit, UnitDecorationType.UnitDecoration deco) {
        WingDecoration wd = (WingDecoration)deco;
        if (unit.moving()) {
            float len = unit.deltaLen();
            wd.left += len;
            wd.right += len;
        }

        float angDst = Utils.angleDistSigned(unit.rotation, wd.lastRot);
        if (Math.abs(angDst) > 1.0E-4F) {
            if (angDst > 0.0F) {
                wd.right += angDst;
            } else {
                wd.left += -angDst;
            }
        } else {
            float mid = Math.max(wd.left % this.flapScl, wd.right % this.flapScl);
            wd.left = Mathf.lerpDelta(wd.left, Mathf.round(wd.left, this.flapScl) + mid, 0.08F);
            wd.right = Mathf.lerpDelta(wd.right, Mathf.round(wd.right, this.flapScl) + mid, 0.08F);
        }

        wd.lastRot = unit.rotation;
    }

    public void added(Unit unit, UnitDecorationType.UnitDecoration deco) {
        WingDecoration wd = (WingDecoration)deco;
        wd.lastRot = unit.rotation;
    }

    public static class Wing {
        float offset;
        float mag;
        float x;
        float y;
        int textureIndex;

        public Wing(int idx, float x, float y, float offset, float mag) {
            this.textureIndex = idx;
            this.x = x;
            this.y = y;
            this.offset = offset;
            this.mag = mag;
        }
    }

    public static class WingDecoration extends UnitDecorationType.UnitDecoration {
        float left = 0.0F;
        float right = 0.0F;
        float lastRot;

        public WingDecoration(UnitDecorationType type) {
            super(type);
        }
    }
}
