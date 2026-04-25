package unity.entities.legs;

import arc.graphics.g2d.Draw;
import arc.math.Angles;
import arc.util.Tmp;
import mindustry.gen.Unit;
import mindustry.world.blocks.environment.Floor;

public class CLegGroup {
    public float totalLength;
    public float moveSpace;
    public float baseRotation;
    public int deep = 0;
    public CLeg[] legs;
    public CLegType.ClegGroupType type;
    public Floor lastFloor;

    public void init(CLegType.ClegGroupType type) {
        this.type = type;
        float l = 9000.0F;

        for(CLegType<? extends CLeg> leg : type.legs) {
            l = Math.min(l, leg.length());
        }

        int div = Math.max(type.legs.length * 2 / type.legGroupSize, 2);
        this.moveSpace = l / 1.6F / (float)div * type.moveSpacing;
        int len = type.legs.length;
        this.legs = new CLeg[len * 2];

        for(int i = 0; i < len * 2; ++i) {
            int s = i / 2;
            boolean flip = i % 2 == 0;
            CLeg leg = type.legs[s].create();
            leg.side = flip;
            leg.id = i % 2 == 0 ? s : len * 2 - 1 - s;
            this.legs[i] = leg;
        }

    }

    public void reset(Unit unit) {
        this.baseRotation = unit.rotation;

        for(CLeg leg : this.legs) {
            leg.reset(this, unit);
        }

    }

    public void update(Unit unit) {
        if (unit.deltaLen() > 0.001F) {
            this.baseRotation = Angles.moveToward(this.baseRotation, Angles.angle(unit.deltaX, unit.deltaY), this.type.baseRotateSpeed);
        }

        this.totalLength += unit.deltaLen() / this.moveSpace;
        this.deep = 0;

        for(CLeg leg : this.legs) {
            leg.update(unit, this.baseRotation, unit.moving(), this);
        }

        if (!this.sinking()) {
            this.lastFloor = null;
        }

    }

    public void draw(Unit unit) {
        unit.type.applyColor(unit);
        Tmp.c3.set(Draw.getMixColor());

        for(CLeg leg : this.legs) {
            leg.drawShadow(unit, this);
        }

        for(CLeg leg : this.legs) {
            leg.draw(unit, this);
        }

        if (this.type.baseRegion.found()) {
            Draw.rect(this.type.baseRegion, unit.x, unit.y, this.baseRotation - 90.0F);
        }

        Draw.reset();
    }

    public boolean sinking() {
        return this.deep >= this.legs.length;
    }
}
