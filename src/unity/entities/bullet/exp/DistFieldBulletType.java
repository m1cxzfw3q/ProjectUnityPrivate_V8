package unity.entities.bullet.exp;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.gen.Bullet;
import mindustry.gen.Entityc;
import mindustry.gen.Groups;
import mindustry.graphics.Pal;
import mindustry.type.StatusEffect;
import unity.content.UnityFx;
import unity.gen.Kami;
import unity.world.blocks.exp.ExpTurret;

public class DistFieldBulletType extends ExpBulletType {
    public Color centerColor;
    public Color edgeColor;
    public Effect distSplashFx;
    public Effect distStart;
    public StatusEffect distStatus;
    public float radius;
    public float radiusInc;
    public float damageLimit;
    public float distDamage;
    public float bulletSlow;
    public float bulletSlowInc;
    public float expChance;

    public DistFieldBulletType(float speed, float damage) {
        super(speed, damage);
    }

    public void draw(Bullet b) {
        float radius = this.getRadius(b);
        Draw.color(Pal.lancerLaser);
        Lines.stroke(1.0F);
        Lines.circle(b.x, b.y, Mathf.clamp((1.0F - b.fin()) * 20.0F) * radius);
        float centerf = this.centerColor.toFloatBits();
        float edgef = this.edgeColor.cpy().a(0.3F + 0.25F * Mathf.sin(b.time() * 0.05F)).toFloatBits();
        float sides = (float)(Mathf.ceil((float)Lines.circleVertices(radius) / 2.0F) * 2);
        float space = 360.0F / sides;
        float dp = 5.0F;

        for(int i = 0; (float)i < sides; i += 2) {
            float px = Angles.trnsx(space * (float)i, Mathf.clamp((1.0F - b.fin()) * dp) * radius);
            float py = Angles.trnsy(space * (float)i, Mathf.clamp((1.0F - b.fin()) * dp) * radius);
            float px2 = Angles.trnsx(space * (float)(i + 1), Mathf.clamp((1.0F - b.fin()) * dp) * radius);
            float py2 = Angles.trnsy(space * (float)(i + 1), Mathf.clamp((1.0F - b.fin()) * dp) * radius);
            float px3 = Angles.trnsx(space * (float)(i + 2), Mathf.clamp((1.0F - b.fin()) * dp) * radius);
            float py3 = Angles.trnsy(space * (float)(i + 2), Mathf.clamp((1.0F - b.fin()) * dp) * radius);
            Fill.quad(b.x, b.y, centerf, b.x + px, b.y + py, edgef, b.x + px2, b.y + py2, edgef, b.x + px3, b.y + py3, edgef);
        }

        Draw.color();
    }

    public void hit(Bullet b, float x, float y) {
    }

    public void despawned(Bullet b) {
    }

    float getRadius(Bullet b) {
        return this.radius + this.radiusInc * (float)this.getLevel(b) * b.damageMultiplier();
    }

    float getBulletSlow(Bullet b) {
        return this.bulletSlow + this.bulletSlowInc * (float)this.getLevel(b) * b.damageMultiplier();
    }

    public void update(Bullet b) {
        float temp = b.lifetime / 4.0F;
        float radius = this.getRadius(b);
        if (b.time() % temp <= 1.0F && b.lifetime() - b.time() > 100.0F) {
            this.distSplashFx.at(b.x, b.y, 0.0F, new Float[]{radius, temp});
        }

        Units.nearbyEnemies(b.team, b.x, b.y, radius, (e) -> {
            Entityc block$temp = b.owner;
            if (block$temp instanceof ExpTurret.ExpTurretBuild) {
                ExpTurret.ExpTurretBuild block = (ExpTurret.ExpTurretBuild)block$temp;
                if (block.levelf() < 1.0F && Mathf.randomBoolean(this.expChance)) {
                    if (Core.settings.getBool("hitexpeffect")) {
                        for(int i = 0; i < this.expGain; ++i) {
                            UnityFx.expGain.at(e.x, e.y, 0.0F, block);
                        }
                    }

                    block.handleExp(this.expGain);
                }
            }

            e.apply(this.distStatus, 2.0F);
            e.damage(this.distDamage);
        });
        Groups.bullet.intersect(b.x - radius, b.y - radius, radius * 2.0F, radius * 2.0F, (e) -> {
            if (e.team != b.team && e.damage() <= this.damageLimit && !(e.owner instanceof Kami)) {
                Entityc block$temp = b.owner;
                if (block$temp instanceof ExpTurret.ExpTurretBuild) {
                    ExpTurret.ExpTurretBuild block = (ExpTurret.ExpTurretBuild)block$temp;
                    if (block.levelf() < 1.0F && Mathf.randomBoolean(this.expChance / 2.0F)) {
                        if (Core.settings.getBool("hitexpeffect")) {
                            for(int i = 0; i < this.expGain; ++i) {
                                UnityFx.expGain.at(e.x, e.y, 0.0F, block);
                            }
                        }

                        block.handleExp(this.expGain);
                    }
                }

                e.vel.scl(this.getBulletSlow(b));
            }

        });
    }

    public void init(Bullet b) {
        if (b != null) {
            float radius = this.getRadius(b);
            this.distStart.at(b.x, b.y, 0.0F, radius);
        }
    }
}
