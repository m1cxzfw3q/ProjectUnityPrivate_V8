package unity.entities.bullet.exp;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.Rand;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.content.Liquids;
import mindustry.content.StatusEffects;
import mindustry.entities.Effect;
import mindustry.entities.Fires;
import mindustry.entities.Lightning;
import mindustry.entities.Puddles;
import mindustry.entities.Units;
import mindustry.gen.Bullet;
import mindustry.graphics.Pal;
import mindustry.type.Liquid;
import mindustry.world.Tile;
import unity.content.UnityFx;
import unity.world.blocks.exp.turrets.OmniLiquidTurret;

public class GeyserBulletType extends ExpBulletType {
    protected static final Rand rand = new Rand();
    public float radius;
    public float radiusInc;
    public Effect spawnEffect;
    public Effect hotSmokeEffect;
    public Effect coldSmokeEffect;
    public float puddleSpeed;
    public float smallEffectChance;
    public float smokeChance;
    public float a;
    public int particles;
    public float particleLife;
    public float particleLen;
    public float shake;

    public GeyserBulletType(float lifetime, float damage) {
        super(1.0E-4F, damage);
        this.radius = 25.0F;
        this.radiusInc = 0.2F;
        this.spawnEffect = UnityFx.giantSplash;
        this.hotSmokeEffect = UnityFx.hotSteam;
        this.coldSmokeEffect = UnityFx.iceSheet;
        this.puddleSpeed = 60.0F;
        this.smallEffectChance = 0.5F;
        this.smokeChance = 0.08F;
        this.a = 1.0F;
        this.particles = 25;
        this.particleLife = 50.0F;
        this.particleLen = 9.0F;
        this.shake = 1.0F;
        this.lifetime = lifetime;
        this.collides = false;
        this.collidesAir = this.collidesGround = this.collidesTiles = false;
        this.absorbable = this.hittable = false;
        this.knockback = 10.0F;
        this.statusDuration = 60.0F;
        this.despawnEffect = Fx.none;
        this.hitEffect = Fx.none;
        this.expChance = 0.07F;
        this.expOnHit = false;
    }

    public GeyserBulletType() {
        this(400.0F, 10.0F);
    }

    public void init() {
        super.init();
        this.drawSize = this.radius * 2.0F + 8.0F;
        this.despawnHit = false;
    }

    public void init(Bullet b) {
        super.init(b);
        Effect.shake(this.shake, b.lifetime, b);
        this.spawnEffect.at(b.x, b.y, 0.0F, this.getLiquid(b).color);
    }

    public Liquid getLiquid(Bullet b) {
        Object var3 = b.data;
        Liquid var10000;
        if (var3 instanceof Liquid) {
            Liquid l = (Liquid)var3;
            var10000 = l;
        } else {
            var10000 = Liquids.water;
        }

        return var10000;
    }

    public float getRad(Bullet b) {
        return ((float)this.getLevel(b) * this.radiusInc + this.radius) * Mathf.clamp(15.0F * b.fout());
    }

    public static float damageScale(Liquid l) {
        return 0.2F + (l.explosiveness + Math.abs(l.temperature - 0.5F)) * 1.8F;
    }

    public static float knockbackScale(Liquid l) {
        return Math.max(0.03F, (0.3F - damageScale(l)) * 5.0F) * l.viscosity * 3.0F;
    }

    public static boolean hasLightning(Liquid l) {
        if (l.effect == StatusEffects.none) {
            return false;
        } else {
            return l.heatCapacity > 1.0F && l.temperature > 0.25F || l.effect.buildSpeedMultiplier > 1.1F || l.effect.speedMultiplier > 1.3F || l.effect.dragMultiplier < 0.1F;
        }
    }

    public void effects(Bullet b, Liquid l) {
        if (Mathf.chance((double)this.smokeChance)) {
            if (l.temperature < 0.32F) {
                this.coldSmokeEffect.at(b.x, b.y, l.color);
            } else if (l.viscosity < l.temperature) {
                this.hotSmokeEffect.at(b.x, b.y, l.temperature > 1.4F ? Pal.gray : (l.temperature > 0.7F ? Color.lightGray : Color.white));
            }
        }

        if (l.effect != StatusEffects.none && Mathf.chance((double)this.smallEffectChance) && l.effect.effect != Fx.none) {
            Tmp.v1.trns(Mathf.random(360.0F), this.getRad(b)).add(b);
            l.effect.effect.at(Tmp.v1.x, Tmp.v1.y, 0.0F, l.effect.color, (Object)null);
        }

        if (l.temperature > 0.8F && Mathf.chance((double)(Mathf.clamp(l.temperature - 0.5F) * 0.2F))) {
            Tmp.v1.trns(Mathf.random(360.0F), this.getRad(b) * l.temperature * Mathf.random(0.3F, 1.0F)).add(b);
            Tile t = Vars.world.tileWorld(Tmp.v1.x, Tmp.v1.y);
            if (t != null && t.build != null) {
                Fires.create(t);
            }
        }

        if (hasLightning(l) && Mathf.chanceDelta((double)(l.heatCapacity * 0.2F))) {
            Lightning.create(b.team, l.color, b.damage * 0.5F, b.x, b.y, Mathf.random(360.0F), Mathf.random(5, 8));
        }

    }

    public void update(Bullet b) {
        super.update(b);
        float rad = this.getRad(b) * 0.6F;
        Liquid l = this.getLiquid(b);
        if (OmniLiquidTurret.friendly(l)) {
            Units.nearby(b.team, b.x, b.y, rad, (unit) -> {
                unit.apply(l.effect, this.statusDuration);
                unit.heal(Math.abs(b.damage * damageScale(l) * 0.1F * l.effect.damage));
            });
        } else {
            Units.nearbyEnemies(b.team, b.x, b.y, rad, (unit) -> {
                Tmp.v3.set(unit).sub(b).nor().scl(this.knockback * 80.0F * knockbackScale(l) * Mathf.clamp(1.0F - 0.9F * unit.dst2(b) / (rad * rad)));
                unit.impulse(Tmp.v3);
                unit.apply(l.effect, this.statusDuration);
                unit.damageContinuousPierce(b.damage * damageScale(l));
                if (Mathf.chance((double)this.expChance)) {
                    this.handleExp(b, unit.x, unit.y, this.expGain);
                }

            });
        }

        if (Vars.world.tileWorld(b.x, b.y) != null) {
            Puddles.deposit(Vars.world.tileWorld(b.x, b.y), l, 25.0F);
        }

        this.effects(b, l);
    }

    public void draw(Bullet b) {
        float rad = this.getRad(b);
        Liquid l = this.getLiquid(b);
        float finb = Mathf.clamp(b.fout() * 9.0F);
        Draw.z(19.9F);

        for(int i = 0; i < 2; ++i) {
            float fin = (Time.time + (float)i * this.puddleSpeed / 2.0F) % this.puddleSpeed / this.puddleSpeed;
            float fout = Mathf.clamp((1.0F - fin) * 5.0F);
            Draw.color(l.color, Color.white, fout * 0.5F);
            Lines.stroke(fout * 6.0F * finb);
            Draw.alpha(0.3F);
            Lines.circle(b.x, b.y, rad * fin * fin);
            Lines.stroke(fout * 5.0F * finb);
            Draw.alpha(0.3F);
            Lines.circle(b.x, b.y, rad * fin * fin);
            Lines.stroke(fout * 3.0F * finb);
            Draw.alpha(1.0F);
            Lines.circle(b.x, b.y, rad * fin * fin);
        }

        Draw.z(101.0F);
        float base = Time.time / this.particleLife;
        rand.setSeed((long)b.id);

        for(int i = 0; i < this.particles; ++i) {
            float fin = (rand.random(1.0F) + base) % 1.0F;
            float fout = 1.0F - fin;
            float angle = rand.random(360.0F);
            float len = rand.random(0.3F, 0.7F) * Interp.pow3Out.apply(fin) * rad;
            float roff = rand.random(0.8F, 1.1F);
            Draw.color(Tmp.c1.set(l.color).mul(0.5F + fout * 0.5F).lerp(Color.white, fout * 0.2F + rand.random(0.0F, 0.2F)), this.a);
            Fill.circle(b.x + Angles.trnsx(angle, len), b.y + Angles.trnsy(angle, len), this.particleLen * roff * fout * Mathf.clamp(fin * 5.0F) * finb + 0.01F);
        }

        Draw.reset();
    }
}
