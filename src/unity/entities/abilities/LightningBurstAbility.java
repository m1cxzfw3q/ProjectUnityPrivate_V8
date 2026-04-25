package unity.entities.abilities;

import arc.Core;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.Lightning;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Sounds;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;
import unity.content.UnityFx;

public class LightningBurstAbility extends Ability {
    protected float rechargeTime = 60.0F;
    protected int bolts = 8;
    protected float maxDelay = 8.0F;
    protected float damage = 17.0F;
    protected int length = 14;
    protected Color color;
    protected boolean rechargeVisible;
    protected boolean chargingVisible;
    protected Effect rechargeFx;
    protected float timer;
    protected boolean used;
    protected boolean check;
    protected int left;

    public LightningBurstAbility() {
        this.color = Pal.lancerLaser;
        this.rechargeVisible = true;
        this.chargingVisible = false;
        this.rechargeFx = UnityFx.ringFx;
    }

    public LightningBurstAbility(float rechargeTime, int bolts, float maxDelay, float damage, int length, Color color) {
        this.color = Pal.lancerLaser;
        this.rechargeVisible = true;
        this.chargingVisible = false;
        this.rechargeFx = UnityFx.ringFx;
        this.rechargeTime = rechargeTime;
        this.bolts = bolts;
        this.maxDelay = maxDelay;
        this.damage = damage;
        this.length = length;
    }

    public LightningBurstAbility(float rechargeTime, int bolts, float maxDelay, float damage, int length, Color color, boolean rechargeVisible, boolean chargingVisible, Effect rechargeFx) {
        this.color = Pal.lancerLaser;
        this.rechargeVisible = true;
        this.chargingVisible = false;
        this.rechargeFx = UnityFx.ringFx;
        this.rechargeTime = rechargeTime;
        this.bolts = bolts;
        this.maxDelay = maxDelay;
        this.damage = damage;
        this.length = length;
        this.color = color;
        this.rechargeVisible = rechargeVisible;
        this.chargingVisible = chargingVisible;
        this.rechargeFx = rechargeFx;
    }

    public boolean able(Unit u) {
        return !u.isFlying();
    }

    public void used(Unit u) {
        Effect.shake(1.0F, 1.0F, u);
        Fx.landShock.at(u);

        for(int i = 0; i < this.bolts; ++i) {
            Time.run(Mathf.random(this.maxDelay), () -> {
                Lightning.create(u.team, Pal.lancerLaser, this.damage, u.x, u.y, (float)Mathf.random(360), this.length);
                Effect.shake((float)i * 0.25F, (float)i * 0.25F, u);
                Sounds.spark.at(u.x, u.y, 1.25F, 0.75F);
            });
        }

    }

    public void notYet(Unit u, float whenReady) {
        Object[] data = new Object[]{this.rechargeTime, u};
        if (this.chargingVisible || u.isPlayer() && u.getPlayer() == Vars.player) {
            UnityFx.waitFx.at(u.x, u.y, whenReady, this.color, data);
        }

    }

    public float getCool() {
        return this.timer;
    }

    public void usedCool(float a) {
        this.timer = a;
        this.used = true;
    }

    public boolean getUse() {
        if (this.used) {
            this.used = false;
            return true;
        } else {
            return false;
        }
    }

    public boolean useCheck(boolean b) {
        if (b) {
            if (!this.check) {
                this.check = true;
                return true;
            }
        } else {
            this.check = false;
        }

        return false;
    }

    public void tryUse(Unit u) {
        if (this.getCool() + this.rechargeTime > Time.time) {
            this.notYet(u, this.getCool() + this.rechargeTime);
        } else {
            this.usedCool(Time.time);
            this.used(u);
        }

    }

    public void update(Unit u) {
        if (this.useCheck(this.able(u))) {
            this.tryUse(u);
        }

        if (this.rechargeFx != Fx.none && this.getCool() + this.rechargeTime < Time.time && this.getUse() && (this.rechargeVisible || u.isPlayer() && u.getPlayer() == Vars.player)) {
            this.rechargeFx.at(u.x, u.y, 0.0F, this.color, u);
        }

    }

    public String localized() {
        return Core.bundle.get("ability.lightning-burst");
    }
}
