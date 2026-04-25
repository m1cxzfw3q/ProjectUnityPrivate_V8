package unity.entities.bullet.anticheat.modules;

import mindustry.entities.abilities.Ability;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.gen.Unit;

public interface AntiCheatBulletModule {
    default void hitUnit(Unit unit, Bullet bullet) {
    }

    default void hitBuilding(Building build, Bullet bullet) {
    }

    default void handleAbility(Ability ability, Unit unit, Bullet bullet) {
    }

    default float getUnitData(Unit unit) {
        return 0.0F;
    }

    default void handleUnitPost(Unit unit, Bullet bullet, float data) {
    }
}
