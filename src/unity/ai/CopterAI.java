package unity.ai;

import mindustry.ai.types.FlyingAI;

public class CopterAI extends FlyingAI {
    protected void attack(float attackLength) {
        this.moveTo(this.target, this.unit.range() * 0.8F);
        this.unit.lookAt(this.target);
    }
}
