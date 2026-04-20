package unity.ai;

import mindustry.entities.units.UnitController;
import mindustry.gen.Unit;

public class EmptyAI implements UnitController {
    protected Unit unit;

    public Unit unit() {
        return this.unit;
    }

    public void unit(Unit unit) {
        this.unit = unit;
    }
}
