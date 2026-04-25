package unity.entities.legs;

import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.world.blocks.environment.Floor;

abstract class CLeg {
    protected static Vec2 v1 = new Vec2();
    protected static Vec2 v2 = new Vec2();
    public int id;
    public int group;
    public boolean moving;
    public float stage;
    public CLegType<?> type;
    public boolean side;
    protected Vec2 foot = new Vec2();

    public void reset(CLegGroup g, Unit unit) {
        int side = Mathf.sign(this.side);
        v1.trns(g.baseRotation - 90.0F, (this.type.x + this.type.targetX) * (float)side, this.type.y + this.type.targetY).add(unit);
        this.foot.set(v1);
    }

    public void update(Unit unit, float baseRotation, boolean uMoving, CLegGroup g) {
        int side = Mathf.sign(this.side);
        int div = Math.max(2, g.legs.length / g.type.legGroupSize);
        v1.trns(baseRotation - 90.0F, this.type.x * (float)side, this.type.y).add(unit);
        float stageF = g.totalLength;
        int stage = (int)stageF;
        int group = stage % div;
        float trns = g.moveSpace * 0.85F * this.type.legTrns;
        this.moving = this.id % div == group;
        this.stage = uMoving ? stageF % 1.0F : Mathf.lerpDelta((float)stage, 0.0F, 0.1F);
        v2.trns(baseRotation - 90.0F, (this.type.x + this.type.targetX) * (float)side, this.type.y + this.type.targetY + trns).add(unit);
        this.updateLeg(unit, g, this.moving, v1.x, v1.y, v2.x, v2.y);
        Floor floor = Vars.world.floorWorld(this.foot.x, this.foot.y);
        if (floor.isDeep()) {
            ++g.deep;
            g.lastFloor = floor;
        }

        if (this.group != group) {
            if (!this.moving && this.id % div == this.group) {
                this.step(unit, floor);
            }

            this.group = group;
        }

    }

    abstract void updateLeg(Unit var1, CLegGroup var2, boolean var3, float var4, float var5, float var6, float var7);

    public void step(Unit unit, Floor floor) {
        if (floor.isLiquid) {
            floor.walkEffect.at(this.foot.x, this.foot.y, unit.type.rippleScale, floor.mapColor);
            floor.walkSound.at(this.foot.x, this.foot.y, 1.0F, floor.walkSoundVolume);
        } else {
            Fx.unitLandSmall.at(this.foot.x, this.foot.y, unit.type.rippleScale, floor.mapColor);
        }

        if (unit.type.landShake > 0.0F) {
            Effect.shake(unit.type.landShake, unit.type.landShake, this.foot);
        }

    }

    protected Vec2 setBase(Unit unit, CLegGroup legGroup) {
        int side = Mathf.sign(this.side);
        return v1.trns(legGroup.baseRotation - 90.0F, this.type.x * (float)side, this.type.y).add(unit);
    }

    abstract void draw(Unit var1, CLegGroup var2);

    void drawShadow(Unit unit, CLegGroup legGroup) {
        Vec2 base = this.setBase(unit, legGroup);
        float ssize = (float)this.type.footRegion.width * Draw.scl * 1.5F;
        float invDrown = 1.0F - unit.drownTime;
        float ang = base.angleTo(this.foot);
        Drawf.shadow(this.foot.x, this.foot.y, ssize, invDrown);
        if (this.moving && unit.type.visualElevation > 0.0F) {
            float scl = unit.type.visualElevation * invDrown;
            float elev = Mathf.slope(1.0F - Mathf.clamp(this.stage)) * scl;
            Draw.color(Pal.shadow);
            Draw.rect(this.type.footRegion, this.foot.x + -12.0F * elev, this.foot.y + -13.0F * elev, ang);
            Draw.color();
        }

    }
}
