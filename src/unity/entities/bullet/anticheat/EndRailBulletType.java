package unity.entities.bullet.anticheat;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.geom.Vec2;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.gen.Bullet;
import mindustry.graphics.Drawf;
import unity.content.effects.SpecialFx;
import unity.content.effects.TrailFx;
import unity.graphics.UnityPal;
import unity.mod.AntiCheat;
import unity.util.Utils;

public class EndRailBulletType extends AntiCheatBulletTypeBase {
    public Color[] colors;
    public float length;
    public float collisionWidth;
    public Effect updateEffect;
    public float updateEffectSeg;
    public float pierceDamageFactor;
    static float len;
    static float dam;

    public EndRailBulletType() {
        this.colors = new Color[]{UnityPal.scarColorAlpha, UnityPal.scarColor, UnityPal.endColor, Color.black};
        this.length = 340.0F;
        this.collisionWidth = 4.0F;
        this.updateEffect = TrailFx.endRailTrail;
        this.updateEffectSeg = 20.0F;
        this.pierceDamageFactor = 1.0F;
        this.speed = 0.0F;
        this.pierceBuilding = true;
        this.pierce = true;
        this.reflectable = false;
        this.absorbable = false;
        this.hittable = false;
        this.hitEffect = Fx.none;
        this.despawnEffect = Fx.none;
        this.collides = false;
        this.lifetime = 20.0F;
    }

    public float range() {
        return this.length;
    }

    public void init() {
        super.init();
        this.drawSize = this.length * 2.0F;
    }

    public void init(Bullet b) {
        super.init(b);
        Tmp.v1.trns(b.rotation(), this.length).add(b);
        dam = this.damage;
        len = this.length;
        Utils.collideLineRawEnemy(b.team, b.x, b.y, Tmp.v1.x, Tmp.v1.y, this.collisionWidth, (build, direct) -> {
            if (direct && dam > 0.0F) {
                float lh = build.health;
                this.hitBuildingAntiCheat(b, build, dam - this.damage);
                dam -= lh * this.pierceDamageFactor;
                if (dam <= 0.0F) {
                    len = b.dst(build);
                }
            }

            return dam <= 0.0F;
        }, (unit) -> {
            if (dam > 0.0F) {
                float lh = unit.health;
                boolean wasAdded = unit.isAdded();
                this.hitUnitAntiCheat(b, unit, dam - this.damage);
                if (unit.dead && wasAdded) {
                    if (Vars.renderer.animateShields) {
                        SpecialFx.fragmentation.at(unit.x, unit.y, unit.angleTo(b), unit);
                    } else {
                        unit.type.deathExplosionEffect.at(unit.x, unit.y, unit.bounds() / 2.0F / 8.0F);
                        unit.type.deathSound.at(unit);
                    }

                    if (unit.isAdded()) {
                        AntiCheat.annihilateEntity(unit, false);
                    }
                }

                dam -= lh * this.pierceDamageFactor;
                if (dam <= 0.0F) {
                    len = b.dst(unit);
                }
            }

            return dam <= 0.0F;
        }, (ex, ey) -> this.hit(b, ex, ey), true);
        Vec2 nor = Tmp.v1.trns(b.rotation(), 1.0F).nor();

        for(float i = 0.0F; i <= len; i += this.updateEffectSeg) {
            this.updateEffect.at(b.x + nor.x * i, b.y + nor.y * i, b.rotation());
        }

        b.fdata = len;
    }

    public void drawLight(Bullet b) {
    }

    public void draw(Bullet b) {
        float stroke = 3.0F * b.fout();
        Vec2 v = Tmp.v1.trns(b.rotation(), b.fdata).add(b);

        for(Color c : this.colors) {
            Draw.color(c);
            Drawf.tri(b.x, b.y, stroke * this.collisionWidth, stroke * 1.22F * this.length * 0.02F, b.rotation() + 180.0F);
            Lines.stroke(stroke * this.collisionWidth);
            Lines.line(b.x, b.y, v.x, v.y);
            Drawf.tri(v.x, v.y, stroke * this.collisionWidth, stroke * 1.22F * this.length * 0.07F, b.rotation());
            stroke /= 1.5F;
        }

        Draw.reset();
    }
}
