package unity.entities.units;

import arc.math.Mathf;
import arc.util.Time;
import mindustry.gen.LegsUnit;
import mindustry.type.UnitType;
import unity.Unity;
import unity.gen.UnityEntityMapping;

public class EndLegsUnit extends LegsUnit implements AntiCheatBase {
    private float lastHealth = 0.0F;
    private float lastMaxHealth = 0.0F;
    private float invTime = 0.0F;
    private final float[] invTimeB = new float[5];
    private float immunity = 1.0F;

    public void setType(UnitType type) {
        super.setType(type);
        this.lastHealth = this.lastMaxHealth = type.health;
    }

    public void update() {
        if (this.lastHealth > this.health) {
            this.health = this.lastHealth;
        }

        if (this.lastMaxHealth > this.maxHealth) {
            this.maxHealth = this.lastMaxHealth;
        }

        if (this.lastHealth > 0.0F) {
            this.dead = false;
        }

        this.lastHealth = this.health;
        this.lastMaxHealth = this.maxHealth;
        super.update();
        this.invTime += Time.delta;

        for(int i = 0; i < this.invTimeB.length; ++i) {
            float[] var10000 = this.invTimeB;
            var10000[i] += Time.delta;
        }

        this.immunity = Math.max(1.0F, this.immunity - Time.delta / 4.0F);
    }

    public void destroy() {
        if (this.lastHealth > 0.0F) {
            this.immunity += 3500.0F;
        } else {
            super.destroy();
        }
    }

    public void kill() {
        if (this.lastHealth > 0.0F) {
            this.immunity += 3500.0F;
        } else {
            super.kill();
        }
    }

    public void add() {
        if (!this.added) {
            Unity.antiCheat.addUnit(this);
            super.add();
        }
    }

    public void remove() {
        if (this.lastHealth > 0.0F) {
            this.immunity += 3500.0F;
        } else if (this.added) {
            Unity.antiCheat.removeUnit(this);
            super.remove();
        }
    }

    public int classId() {
        return UnityEntityMapping.classId(EndLegsUnit.class);
    }

    public void damage(float amount) {
        if (!(this.invTime < 30.0F)) {
            this.invTime = 0.0F;
            float max = Math.max(220.0F, this.lastMaxHealth / 700.0F);
            float trueDamage = Mathf.clamp(amount / this.immunity, 0.0F, max);
            max *= 1.5F;
            this.immunity = (float)((double)this.immunity + Math.pow((double)(Math.max(amount - max, 0.0F) / max), (double)2.0F) * (double)2.0F);
            this.lastHealth -= trueDamage;
            super.damage(trueDamage);
        }
    }

    public float lastHealth() {
        return this.lastHealth;
    }

    public void lastHealth(float v) {
        this.lastHealth = v;
    }

    public void overrideAntiCheatDamage(float v, int priority) {
        if (!(this.invTimeB[Mathf.clamp(priority, 0, this.invTimeB.length - 1)] < 30.0F)) {
            this.hitTime = 1.0F;
            this.invTimeB[Mathf.clamp(priority, 0, this.invTimeB.length - 1)] = 0.0F;
            this.lastHealth(this.lastHealth() - v);
            if (this.health() > this.lastHealth()) {
                this.health(this.lastHealth());
            }

        }
    }
}
