package unity.entities.units;

import arc.math.Mathf;
import arc.util.Structs;
import arc.util.Time;
import mindustry.gen.Bullet;
import mindustry.gen.Hitboxc;
import mindustry.type.UnitType;
import unity.Unity;
import unity.gen.UnityEntityMapping;

public class EndWormUnit extends WormDefaultUnit implements AntiCheatBase {
    private float invTime = 0.0F;
    private final float[] invTimeB = new float[5];
    private float immunity = 1.0F;
    private float lastHealth = 0.0F;
    private float lastMaxHealth = 0.0F;
    private float rogueDamageResist = 1.0F;

    public float lastHealth() {
        return this.lastHealth;
    }

    public void lastHealth(float v) {
        this.lastHealth = v;
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
        this.lastMaxHealth = this.health;
        super.update();
        this.invTime += Time.delta;

        for(int i = 0; i < this.invTimeB.length; ++i) {
            float[] var10000 = this.invTimeB;
            var10000[i] += Time.delta;
        }

        this.immunity = Math.max(1.0F, this.immunity - Time.delta / 4.0F);
        this.rogueDamageResist = Math.max(1.0F, this.rogueDamageResist - Time.delta / 2.0F);
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

            for(WormSegmentUnit segmentUnit : this.segmentUnits) {
                if (segmentUnit instanceof EndWormSegmentUnit) {
                    EndWormSegmentUnit s = (EndWormSegmentUnit)segmentUnit;
                    s.removed = true;
                }
            }

            super.remove();
        }
    }

    public void setType(UnitType type) {
        super.setType(type);
        this.lastHealth = this.lastMaxHealth = type.health;
    }

    public void overrideAntiCheatDamage(float v, int priority) {
        if (!(this.invTimeB[Mathf.clamp(priority, 0, this.invTimeB.length - 1)] < 30.0F)) {
            this.hitTime = 1.0F;
            this.invTimeB[Mathf.clamp(priority, 0, this.invTimeB.length - 1)] = 0.0F;
            this.lastHealth -= v;
            this.health -= v;
        }
    }

    public void damage(float amount) {
        if (!(this.invTime < 30.0F)) {
            this.invTime = 0.0F;
            float max = Math.max(220.0F, this.lastMaxHealth / 700.0F);
            float trueDamage = Mathf.clamp(amount / this.immunity / this.rogueDamageResist, 0.0F, max);
            ++this.rogueDamageResist;
            max *= 1.5F;
            this.immunity = (float)((double)this.immunity + Math.pow((double)(Math.max(amount - max, 0.0F) / max), (double)2.0F) * (double)2.0F);
            this.lastHealth -= trueDamage;
            super.damage(trueDamage);
        }
    }

    public WormSegmentUnit newSegment() {
        return new EndWormSegmentUnit();
    }

    public void handleCollision(Hitboxc originUnit, Hitboxc other, float x, float y) {
        if (other instanceof Bullet) {
            Bullet b = (Bullet)other;
            if (b.owner != null) {
                this.rogueDamageResist = 1.0F;
            }
        }

    }

    public int classId() {
        return UnityEntityMapping.classId(EndWormUnit.class);
    }

    public static class EndWormSegmentUnit extends WormSegmentUnit implements AntiCheatBase {
        private boolean removed = false;

        public void remove() {
            if (!Structs.contains(this.trueParentUnit.segmentUnits, this) || this.removed) {
                super.remove();
            }

        }

        public void overrideAntiCheatDamage(float v, int priority) {
            if (this.trueParentUnit instanceof AntiCheatBase) {
                ((AntiCheatBase)this.trueParentUnit).overrideAntiCheatDamage(v / 3.0F, priority);
            }

        }

        public float lastHealth() {
            return this.trueParentUnit instanceof AntiCheatBase ? ((AntiCheatBase)this.trueParentUnit).lastHealth() : this.health;
        }

        public void lastHealth(float v) {
            if (this.trueParentUnit instanceof AntiCheatBase) {
                ((AntiCheatBase)this.trueParentUnit).lastHealth(v);
            }

        }

        public int classId() {
            return UnityEntityMapping.classId(EndWormSegmentUnit.class);
        }
    }
}
