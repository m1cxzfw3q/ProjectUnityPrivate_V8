package unity.entities.legs;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Tmp;
import mindustry.gen.Unit;
import mindustry.graphics.InverseKinematics;

public class BasicLeg extends CLeg {
    float jointX;
    float jointY;

    public void reset(CLegGroup g, Unit unit) {
        super.reset(g, unit);
        BasicLegType type = (BasicLegType)this.type;
        int side = Mathf.sign(this.side);
        float scl = type.baseLength / (type.baseLength + type.endLength);
        v1.trns(g.baseRotation - 90.0F, (type.targetX + type.x) * (float)side, type.targetY + type.y).scl(scl).add(unit);
        this.jointX = v1.x;
        this.jointY = v1.y;
    }

    void updateLeg(Unit unit, CLegGroup legGroup, boolean moving, float baseX, float baseY, float targetX, float targetY) {
        float stageF = legGroup.totalLength;
        BasicLegType type = (BasicLegType)this.type;
        Vec2 j = Tmp.v1.set(this.jointX, this.jointY).sub(baseX, baseY).limit(type.baseLength * legGroup.type.maxStretch).add(baseX, baseY);
        this.jointX = j.x;
        this.jointY = j.y;
        this.foot.sub(baseX, baseY).limit(type.length() * legGroup.type.maxStretch).add(baseX, baseY);
        Vec2 jDest = Tmp.v2;
        Vec2 end = Tmp.v3;
        InverseKinematics.solve(type.baseLength, type.endLength, end.set(this.foot).sub(baseX, baseY), this.side == type.flipped, jDest);
        jDest.add(baseX, baseY);
        if (moving) {
            float fract = stageF % 1.0F;
            this.foot.lerpDelta(targetX, targetY, fract);
            Tmp.v1.set(this.jointX, this.jointY).lerpDelta(jDest, fract / 2.0F);
            this.jointX = Tmp.v1.x;
            this.jointY = Tmp.v1.y;
        }

        Tmp.v1.set(this.jointX, this.jointY).lerpDelta(jDest, unit.type.legSpeed / 4.0F);
        this.jointX = Tmp.v1.x;
        this.jointY = Tmp.v1.y;
    }

    void draw(Unit unit, CLegGroup legGroup) {
        BasicLegType type = (BasicLegType)this.type;
        Vec2 base = this.setBase(unit, legGroup);
        Vec2 off = Tmp.v2.setZero();
        int flips = Mathf.sign(this.side == type.flipped);
        if (type.endOffset != 0.0F) {
            off.set(this.foot).sub(this.jointX, this.jointY).setLength(type.endOffset);
        }

        float ang = base.angleTo(this.foot);
        Draw.mixcol(Tmp.c3, Tmp.c3.a);
        Draw.rect(type.footRegion, this.foot.x, this.foot.y, ang);
        Lines.stroke((float)type.baseRegion.height * Draw.scl * (float)flips);
        Lines.line(type.baseRegion, base.x, base.y, this.jointX, this.jointY, false);
        Lines.stroke((float)type.endRegion.height * Draw.scl * (float)flips);
        Lines.line(type.endRegion, this.jointX + off.x, this.jointY + off.y, this.foot.x, this.foot.y, false);
        if (type.kneeJoint.found()) {
            Draw.rect(type.kneeJoint, this.jointX, this.jointY);
        }

        if (type.baseJoint.found()) {
            Draw.rect(type.baseRegion, base.x, base.y, legGroup.baseRotation - 90.0F);
        }

    }

    public static class BasicLegType extends CLegType<BasicLeg> {
        public float baseLength = 10.0F;
        public float endLength = 10.0F;
        public float endOffset = 0.0F;
        TextureRegion baseRegion;
        TextureRegion endRegion;
        TextureRegion baseJoint;
        TextureRegion kneeJoint;

        public BasicLegType(String name) {
            super(BasicLeg::new, name);
        }

        public void load() {
            super.load();
            this.baseRegion = Core.atlas.find(this.name + "-base");
            this.endRegion = Core.atlas.find(this.name + "-end");
            this.baseJoint = Core.atlas.find(this.name + "-base-joint");
            this.kneeJoint = Core.atlas.find(this.name + "-joint");
        }

        public float length() {
            return this.baseLength + this.endLength;
        }
    }
}
