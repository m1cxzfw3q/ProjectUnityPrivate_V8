package unity.entities.units;

import arc.math.Mathf;
import arc.util.Interval;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.gen.Groups;
import mindustry.gen.UnitEntity;
import unity.gen.UnityEntityMapping;
import unity.util.Utils;

public class EndInvisibleUnit extends UnitEntity implements AntiCheatBase {
    protected boolean isInvisible = false;
    protected float disabledTime = 0.0F;
    protected Interval scanInterval = new Interval(2);
    protected float invFrame = 0.0F;
    public float alphaLerp = 0.0F;
    protected float lastHealth = 0.0F;

    public float lastHealth() {
        return this.lastHealth;
    }

    public void lastHealth(float v) {
        this.lastHealth = v;
    }

    public void overrideAntiCheatDamage(float v) {
        if (!(this.invFrame < 15.0F)) {
            this.invFrame = 0.0F;
            this.hitTime = 1.0F;
            this.lastHealth -= v;
            super.damage(v);
        }
    }

    public void add() {
        if (!this.added) {
            super.add();
            this.lastHealth = this.health;
        }
    }

    public void update() {
        if (this.health < this.lastHealth) {
            this.health = this.lastHealth;
        }

        this.lastHealth = this.health;
        super.update();
        this.invFrame += Time.delta;
        this.disabledTime = Math.max(this.disabledTime - Time.delta, 0.0F);
        if (this.scanInterval.get(10.0F) && this.isInvisible) {
            this.hitbox(Tmp.r1);
            Groups.bullet.intersect(Tmp.r1.x, Tmp.r1.y, Tmp.r1.width, Tmp.r1.height, (b) -> {
                if (b.team != this.team) {
                    this.disabledTime = 72.0F;
                }

            });
        }

        if (this.scanInterval.get(1, 30.0F)) {
            float size = this.hitSize * 3.0F;
            Tmp.r1.setCentered(this.x, this.y, size * 2.0F);
            Groups.unit.intersect(Tmp.r1.x, Tmp.r1.y, Tmp.r1.width, Tmp.r1.height, (u) -> {
                if (u.team != this.team && Mathf.within(this.x, this.y, u.x, u.y, this.hitSize * 3.0F)) {
                    this.disabledTime = 72.0F;
                }

            });
            if (Utils.hasBuilding(this.x, this.y, size, (build) -> build.team != this.team)) {
                this.disabledTime = 72.0F;
            }
        }

        if (!this.isShooting && this.health > this.maxHealth / 2.0F && this.disabledTime <= 0.0F) {
            this.alphaLerp = Mathf.lerpDelta(this.alphaLerp, 1.0F, 0.1F);
        } else {
            this.alphaLerp = Mathf.lerpDelta(this.alphaLerp, 0.0F, 0.1F);
        }

        if (this.alphaLerp < 0.5F) {
            this.setVisible();
        } else {
            this.setInvisible();
            if (this.physref != null) {
                this.physref.x = this.x;
                this.physref.y = this.y;
                this.physref.body.x = this.x;
                this.physref.body.y = this.y;
            }
        }

    }

    void setInvisible() {
        if (!this.isInvisible) {
            Groups.unit.remove(this);
            this.isInvisible = true;
        }

    }

    void setVisible() {
        if (this.isInvisible) {
            Groups.unit.add(this);
            this.isInvisible = false;
        }

    }

    protected void superDamage(float amount) {
        super.damage(amount);
    }

    public void damage(float amount) {
        if (!(this.invFrame < 15.0F)) {
            this.invFrame = 0.0F;
            float trueDamage = Math.min(amount, 700.0F);
            this.disabledTime = Math.max(84.0F, trueDamage / 25.0F);
            this.lastHealth -= trueDamage;
            super.damage(trueDamage);
        }
    }

    public int classId() {
        return UnityEntityMapping.classId(EndInvisibleUnit.class);
    }
}
