package unity.type.decal;

import arc.Core;
import arc.func.Floatf;
import arc.graphics.Blending;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Tmp;
import mindustry.entities.Effect;
import mindustry.gen.Unit;
import unity.content.effects.TrailFx;

public class CapeDecorationType extends UnitDecorationType {
    public String name;
    public TextureRegion region;
    public Floatf<Unit> swayExt = Unit::elevation;
    public Effect trailEffect;
    public float x;
    public float y;
    public float swayAmount;
    public float swaySpeed;
    public float shakeAmount;
    public float shakeScl;
    public float alphaFrom;
    public float alphaTo;
    public float alphaShake;
    public float alphaScl;
    public float rotCone;
    public float rotSpeed;

    public CapeDecorationType(String name) {
        this.trailEffect = TrailFx.capeTrail;
        this.swayAmount = -45.0F;
        this.swaySpeed = 0.1F;
        this.shakeAmount = 2.5F;
        this.shakeScl = 5.0F;
        this.alphaFrom = 0.0F;
        this.alphaTo = 0.8F;
        this.alphaShake = 0.2F;
        this.alphaScl = 2.0F;
        this.rotCone = 60.0F;
        this.rotSpeed = 0.1F;
        this.name = name;
        this.top = true;
        this.decalType = CapeDecoration::new;
    }

    public void load() {
        this.region = Core.atlas.find(this.name);
    }

    public void update(Unit unit, UnitDecorationType.UnitDecoration deco) {
        if (deco instanceof CapeDecoration) {
            CapeDecoration cape = (CapeDecoration)deco;
            float val = this.swayExt.get(unit);
            float sway = Mathf.lerpDelta(cape.sway, val * this.swayAmount, this.swaySpeed);
            sway += Mathf.sin(this.shakeScl, this.shakeAmount * val);
            cape.alpha = Mathf.clamp(this.alphaFrom + val * (this.alphaTo - this.alphaFrom) + Mathf.sin(this.alphaScl, this.alphaShake * val));
            cape.sway = sway;
            cape.rotation = Angles.clampRange(Angles.moveToward(cape.rotation, unit.rotation, this.rotSpeed), unit.rotation, this.rotCone / 2.0F);
            this.trailEffect.at(unit.x, unit.y, cape.rotation, new CapeEffectData(this, cape.alpha * 0.25F, cape.sway));
        }
    }

    public void draw(Unit unit, UnitDecorationType.UnitDecoration deco) {
        if (deco instanceof CapeDecoration) {
            CapeDecoration cape = (CapeDecoration)deco;
            unit.type.applyColor(unit);
            Draw.alpha(cape.alpha);
            Draw.blend(Blending.additive);

            for(int sign : Mathf.signs) {
                Tmp.v1.trns(cape.rotation - 90.0F, this.x * (float)sign, this.y);
                Draw.rect(this.region, unit.x + Tmp.v1.x, unit.y + Tmp.v1.y, (float)this.region.width * Draw.scl * (float)sign, (float)this.region.height * Draw.scl, cape.rotation + cape.sway * (float)sign - 90.0F);
            }

            Draw.blend(Blending.normal);
        }
    }

    public void added(Unit unit, UnitDecorationType.UnitDecoration deco) {
        if (deco instanceof CapeDecoration) {
            CapeDecoration cape = (CapeDecoration)deco;
            cape.rotation = unit.rotation;
        }
    }

    public static class CapeDecoration extends UnitDecorationType.UnitDecoration {
        public float alpha;
        public float sway;
        public float rotation;

        public CapeDecoration(UnitDecorationType type) {
            super(type);
        }
    }

    public static class CapeEffectData {
        public CapeDecorationType type;
        public float alpha;
        public float sway;

        public CapeEffectData(CapeDecorationType type, float alpha, float sway) {
            this.type = type;
            this.alpha = alpha;
            this.sway = sway;
        }
    }
}
