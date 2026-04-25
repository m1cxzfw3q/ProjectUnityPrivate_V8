package unity.entities.effects;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Position;
import arc.util.Tmp;
import mindustry.gen.EffectState;
import mindustry.gen.Entityc;
import mindustry.gen.Groups;
import mindustry.gen.Hitboxc;
import mindustry.gen.Posc;
import mindustry.gen.Unit;
import mindustry.type.UnitType;
import unity.content.UnityFx;
import unity.entities.ExtraEffect;
import unity.util.GraphicUtils;

public class VapourizeEffectState extends EffectState {
    protected Entityc influence;
    public float extraAlpha;

    public VapourizeEffectState() {
        this.extraAlpha = 0.0F;
        this.lifetime = 50.0F;
    }

    public VapourizeEffectState(float x, float y, Unit parent, Entityc influence) {
        this();
        this.x = x;
        this.y = y;
        this.parent = parent;
        this.influence = influence;
    }

    public void add() {
        if (!this.added) {
            Groups.all.add(this);
            Groups.draw.add(this);
            this.added = true;
        }
    }

    public void update() {
        Posc var3 = this.parent;
        if (var3 instanceof Hitboxc) {
            Hitboxc hit = (Hitboxc)var3;
            Entityc var4 = this.influence;
            if (var4 instanceof Posc) {
                Posc temp = (Posc)var4;
                float var5 = hit.hitSize();
                if (Mathf.chanceDelta((double)(0.2F * (1.0F - this.fin()) * var5 / 10.0F))) {
                    Tmp.v1.trns(Angles.angle(this.x, this.y, temp.x(), temp.y()) + 180.0F, 65.0F + Mathf.range(0.3F));
                    Tmp.v1.add(this.parent);
                    Tmp.v2.trns(Mathf.random(360.0F), Mathf.random(var5 / 1.25F));
                    UnityFx.vaporation.at(this.parent.x(), this.parent.y(), 0.0F, new Position[]{this.parent, Tmp.v1.cpy(), Tmp.v2.cpy()});
                }

                super.update();
                return;
            }
        }

    }

    public float clipSize() {
        Posc var2 = this.parent;
        if (var2 instanceof Hitboxc) {
            Hitboxc hit = (Hitboxc)var2;
            return hit.hitSize() * 2.0F;
        } else {
            return super.clipSize();
        }
    }

    public void draw() {
        Posc var2 = this.parent;
        if (var2 instanceof Unit) {
            Unit unit = (Unit)var2;
            UnitType var6 = unit.type;
            float oz = Draw.z();
            float z = (unit.elevation > 0.5F ? (var6.lowAltitude ? 90.0F : 115.0F) : var6.groundLayer + Mathf.clamp(var6.hitSize / 4000.0F, 0.0F, 0.01F)) + 0.001F;
            float slope = (0.5F - Math.abs(this.fin() - 0.5F)) * 2.0F;
            Draw.z(z);
            Tmp.c1.set(Color.black);
            Tmp.c1.a = Mathf.clamp(slope * (1.0F - unit.healthf() + this.extraAlpha) * 1.4F);
            Draw.color(Tmp.c1);
            GraphicUtils.simpleUnitDrawer(unit, false);
            Draw.z(oz);
        }
    }

    public void remove() {
        if (this.added) {
            Groups.all.remove(this);
            Groups.draw.remove(this);
            ExtraEffect.removeEvaporation(this.parent.id());
            this.added = false;
        }
    }
}
