package unity.ai;

import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.ai.types.FlyingAI;
import unity.type.UnityUnitType;

public class LinkerAI extends FlyingAI {
    public Seq<LinkedAI> links = new Seq();
    private boolean first = true;
    public float angle = 0.0F;
    public Vec2 center;

    public void init() {
        super.init();
    }

    public void updateUnit() {
        super.updateUnit();
        if (this.first && (this.unit.x != 0.0F || this.unit.y != 0.0F)) {
            this.first = false;
            int linkCount = ((UnityUnitType)this.unit.type).linkCount;

            for(int i = 0; i < linkCount; ++i) {
                LinkedAI link = new LinkedAI();
                link.spawner = this.unit;
                Tmp.v1.set(0.0F, 0.0F).trns(360.0F / (float)linkCount * (float)i, 20.0F);
                link.unit(((UnityUnitType)this.unit.type).linkType.spawn(this.unit.team, this.unit.x + Tmp.v1.x, this.unit.y + Tmp.v1.y));
                this.links.add(link);
            }

            this.center = new Vec2(this.unit.x, this.unit.y);
            Tmp.v1.set(0.0F, 0.0F).trns(360.0F, 20.0F);
            this.unit.set(this.unit.x + Tmp.v1.x, this.unit.y + Tmp.v1.y);
        }
    }

    public void updateMovement() {
        super.updateMovement();
        this.unit.rotation = this.angle;
        this.angle += ((UnityUnitType)this.unit.type).rotationSpeed / 60.0F * Time.delta;
    }
}
