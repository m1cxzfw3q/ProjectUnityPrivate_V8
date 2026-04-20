package unity.ai;

import arc.util.Time;
import mindustry.ai.types.FlyingAI;
import mindustry.gen.Unit;
import unity.type.UnityUnitType;

public class LinkedAI extends FlyingAI {
    public Unit spawner;
    public float angle = 0.0F;

    public void updateUnit() {
        super.updateUnit();
        if (this.spawner != null && this.spawner.dead) {
            this.unit.kill();
        }

        if (this.spawner != null && this.unit.dead) {
            this.spawner.kill();
        }

    }

    public void updateMovement() {
        super.updateMovement();
        this.unit.rotation = this.angle;
        this.angle += ((UnityUnitType)this.unit.type).rotationSpeed / 60.0F * Time.delta;
    }
}
