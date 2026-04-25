package unity.entities.bullet.exp;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Tmp;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Bullet;
import mindustry.gen.Entityc;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import unity.content.UnityFx;
import unity.graphics.UnityPal;
import unity.world.blocks.exp.ExpTurret;

public class ExpBulletType extends BulletType {
    public Color fromColor;
    public Color toColor;
    public float damageInc;
    public int expGain;
    public boolean expOnHit;
    public float expChance;
    public boolean overrideTrail;
    public boolean overrideLight;

    public ExpBulletType(float speed, float damage) {
        super(speed, damage);
        this.fromColor = Pal.lancerLaser;
        this.toColor = UnityPal.expLaser;
        this.expGain = 1;
        this.expOnHit = false;
        this.expChance = 1.0F;
        this.overrideTrail = true;
        this.overrideLight = true;
    }

    public void hit(Bullet b, float x, float y) {
        if (this.expOnHit) {
            this.handleExp(b, x, y, this.expGain);
        }

        this.fragExp(b, x, y);
        BulletType f = this.fragBullet;
        this.fragBullet = null;
        super.hit(b, x, y);
        this.fragBullet = f;
    }

    public void drawTrail(Bullet b) {
        if (this.trailLength > 0 && b.trail != null) {
            float z = Draw.z();
            Draw.z(z - 1.0E-4F);
            b.trail.draw(this.overrideTrail ? this.getColor(b).mul(this.trailColor) : this.trailColor, this.trailWidth);
            Draw.z(z);
        }

    }

    public void drawLight(Bullet b) {
        if (!(this.lightOpacity <= 0.0F) && !(this.lightRadius <= 0.0F)) {
            Drawf.light(b.team, b, this.lightRadius, this.overrideLight ? this.getColor(b).mul(this.lightColor) : this.lightColor, this.lightOpacity);
        }
    }

    public void handleExp(Bullet b, float x, float y, int amount) {
        if (Mathf.chance((double)this.expChance)) {
            Entityc var6 = b.owner;
            if (var6 instanceof ExpTurret.ExpTurretBuild) {
                ExpTurret.ExpTurretBuild exp = (ExpTurret.ExpTurretBuild)var6;
                if (exp.level() < exp.maxLevel() && Core.settings.getBool("hitexpeffect")) {
                    for(int i = 0; (double)i < Math.ceil((double)amount); ++i) {
                        UnityFx.expGain.at(x, y, 0.0F, b.owner);
                    }
                }

                exp.handleExp(amount);
            }

        }
    }

    public int getLevel(Bullet b) {
        Entityc var3 = b.owner;
        if (var3 instanceof ExpTurret.ExpTurretBuild) {
            ExpTurret.ExpTurretBuild exp = (ExpTurret.ExpTurretBuild)var3;
            return exp.level();
        } else {
            return 0;
        }
    }

    public float getLevelf(Bullet b) {
        Entityc var3 = b.owner;
        if (var3 instanceof ExpTurret.ExpTurretBuild) {
            ExpTurret.ExpTurretBuild exp = (ExpTurret.ExpTurretBuild)var3;
            return exp.levelf();
        } else {
            return 0.0F;
        }
    }

    public void setDamage(Bullet b) {
        b.damage += this.damageInc * (float)this.getLevel(b) * b.damageMultiplier();
    }

    public Color getColor(Bullet b) {
        return Tmp.c2.set(this.fromColor).lerp(this.toColor, this.getLevelf(b));
    }

    public void fragExp(Bullet b, float x, float y) {
        if (this.fragBullet != null) {
            for(int i = 0; i < this.fragBullets; ++i) {
                float len = Mathf.random(1.0F, 7.0F);
                float a = b.rotation() + Mathf.range(this.fragCone / 2.0F) + this.fragAngle;
                this.fragBullet.create(b.owner, b.team, x + Angles.trnsx(a, len), y + Angles.trnsy(a, len), a, Mathf.random(this.fragVelocityMin, this.fragVelocityMax), Mathf.random(this.fragLifeMin, this.fragLifeMax));
            }
        }

    }
}
