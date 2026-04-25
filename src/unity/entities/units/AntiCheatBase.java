package unity.entities.units;

import mindustry.gen.Unitc;

public interface AntiCheatBase extends Unitc {
    float lastHealth();

    void lastHealth(float var1);

    default void overrideAntiCheatDamage(float v) {
        this.overrideAntiCheatDamage(v, 0);
    }

    default void overrideAntiCheatDamage(float v, int priority) {
        this.lastHealth(this.lastHealth() - v);
        if (this.health() > this.lastHealth()) {
            this.health(this.lastHealth());
        }

    }
}
