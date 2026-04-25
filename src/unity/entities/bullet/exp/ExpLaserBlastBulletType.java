package unity.entities.bullet.exp;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.entities.Damage;
import mindustry.entities.Lightning;
import mindustry.entities.bullet.LaserBulletType;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.gen.Entityc;
import mindustry.gen.Hitboxc;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import unity.content.UnityFx;
import unity.graphics.UnityPal;
import unity.world.blocks.exp.ExpTurret;

public class ExpLaserBlastBulletType extends LaserBulletType {
    public Color[] fromColors;
    public Color[] toColors;
    public float damageInc;
    public float lengthInc;
    public float widthInc;
    public float lightningSpacingInc;
    public float lightningDamageInc;
    public Color lightningFromColor;
    public Color lightningToColor;
    public int hitUnitExpGain;
    public int hitBuildingExpGain;

    public ExpLaserBlastBulletType(float length, float damage) {
        super(damage);
        this.fromColors = new Color[]{Pal.lancerLaser.cpy().mul(1.0F, 1.0F, 1.0F, 0.4F), Pal.lancerLaser, Color.white};
        this.toColors = new Color[]{UnityPal.exp.cpy().mul(1.0F, 1.0F, 1.0F, 0.4F), UnityPal.exp, Color.white};
        this.lightningFromColor = Pal.lancerLaser;
        this.lightningToColor = UnityPal.exp;
        this.length = length;
        this.ammoMultiplier = 1.0F;
        this.drawSize = length * 2.0F;
        this.hitEffect = Fx.hitLiquid;
        this.shootEffect = Fx.hitLiquid;
        this.lifetime = 18.0F;
        this.despawnEffect = Fx.none;
        this.keepVelocity = false;
        this.collides = false;
        this.pierce = true;
        this.hittable = false;
        this.absorbable = false;
    }

    public ExpLaserBlastBulletType() {
        this(120.0F, 1.0F);
    }

    public void handleExp(Bullet b, float x, float y, int amount) {
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

    public void setColors(Bullet b) {
        float f = this.getLevelf(b);
        Color[] data = new Color[]{Tmp.c1.set(this.fromColors[0]).lerp(this.toColors[0], f).cpy(), Tmp.c2.set(this.fromColors[1]).lerp(this.toColors[1], f).cpy(), Tmp.c3.set(this.fromColors[2]).lerp(this.toColors[2], f).cpy()};
        b.data = data;
    }

    public Color getLightningColor(Bullet b) {
        return Tmp.c1.set(this.lightningFromColor).lerp(this.lightningToColor, this.getLevelf(b)).cpy();
    }

    public float getLength(Bullet b) {
        return this.length + this.lengthInc * (float)this.getLevel(b);
    }

    public float getWidth(Bullet b) {
        return this.width + this.widthInc * (float)this.getLevel(b);
    }

    public float getLightningSpacing(Bullet b) {
        return this.lightningSpacing + this.lightningSpacingInc * (float)this.getLevel(b);
    }

    public float getLightningDamage(Bullet b) {
        return this.lightningDamage + this.lightningDamageInc * (float)this.getLevel(b);
    }

    public float range() {
        return Math.max(this.length, this.maxRange);
    }

    public void init(Bullet b) {
        this.setDamage(b);
        float resultLength = Damage.collideLaser(b, this.getLength(b), this.largeHit);
        float rot = b.rotation();
        this.laserEffect.at(b.x, b.y, rot, resultLength * 0.75F);
        if (this.getLightningSpacing(b) > 0.0F) {
            int idx = 0;

            for(float i = 0.0F; i <= resultLength; i += this.getLightningSpacing(b)) {
                float cx = b.x + Angles.trnsx(rot, i);
                float cy = b.y + Angles.trnsy(rot, i);
                int f = idx++;

                for(int s : Mathf.signs) {
                    Time.run((float)f * this.lightningDelay, () -> {
                        if (b.isAdded() && b.type == this) {
                            Lightning.create(b, this.getLightningColor(b), this.getLightningDamage(b) < 0.0F ? this.damage : this.getLightningDamage(b), cx, cy, rot + (float)(90 * s) + Mathf.range(this.lightningAngleRand), this.lightningLength + Mathf.random(this.lightningLengthRand));
                        }

                    });
                }
            }
        }

        this.setColors(b);
    }

    public void hitTile(Bullet b, Building build, float initialHealth, boolean direct) {
        this.handleExp(b, build.x, build.y, this.hitBuildingExpGain);
        super.hitTile(b, build, initialHealth, direct);
    }

    public void hitEntity(Bullet b, Hitboxc other, float initialHealth) {
        this.handleExp(b, other.x(), other.y(), this.hitUnitExpGain);
        super.hitEntity(b, other, initialHealth);
    }

    public void draw(Bullet b) {
        float realLength = b.fdata;
        float f = Mathf.curve(b.fin(), 0.0F, 0.2F);
        float baseLen = realLength * f;
        float cwidth = this.getWidth(b);
        float compound = 1.0F;
        Lines.lineAngle(b.x, b.y, b.rotation(), baseLen);

        for(Color color : (Color[])b.data) {
            Draw.color(color);
            Lines.stroke((cwidth *= this.lengthFalloff) * b.fout());
            Lines.lineAngle(b.x, b.y, b.rotation(), baseLen, false);
            Tmp.v1.trns(b.rotation(), baseLen);
            Drawf.tri(b.x + Tmp.v1.x, b.y + Tmp.v1.y, Lines.getStroke() * 1.22F, cwidth * 2.0F + this.getWidth(b) / 2.0F, b.rotation());
            Fill.circle(b.x, b.y, 1.0F * cwidth * b.fout());

            for(int i : Mathf.signs) {
                Drawf.tri(b.x, b.y, this.sideWidth * b.fout() * cwidth, this.sideLength * compound, b.rotation() + this.sideAngle * (float)i);
            }

            compound *= this.lengthFalloff;
        }

        Draw.reset();
        Tmp.v1.trns(b.rotation(), baseLen * 1.1F);
        Drawf.light(b.team, b.x, b.y, b.x + Tmp.v1.x, b.y + Tmp.v1.y, cwidth * 1.4F * b.fout(), ((Color[])b.data)[0], 0.6F);
    }

    public void drawLight(Bullet b) {
    }
}
