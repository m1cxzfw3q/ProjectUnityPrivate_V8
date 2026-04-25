package unity.entities.bullet.anticheat;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.entities.Damage;
import mindustry.entities.Effect;
import mindustry.entities.Lightning;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import unity.util.Utils;

public class EndContinuousLaserBulletType extends AntiCheatBulletTypeBase {
    public float length = 220.0F;
    public float shake = 1.0F;
    public float fadeTime = 16.0F;
    public float lightStroke = 40.0F;
    public float spaceMag = 35.0F;
    public Color[] colors;
    public float[] tscales;
    public float[] strokes;
    public float[] lenscales;
    public float width;
    public float oscScl;
    public float oscMag;
    public boolean largeHit;
    public float lightningChance;

    public EndContinuousLaserBulletType(float damage) {
        this.colors = new Color[]{Color.valueOf("ec745855"), Color.valueOf("ec7458aa"), Color.valueOf("ff9c5a"), Color.white};
        this.tscales = new float[]{1.0F, 0.7F, 0.5F, 0.2F};
        this.strokes = new float[]{2.0F, 1.5F, 1.0F, 0.3F};
        this.lenscales = new float[]{1.0F, 1.12F, 1.15F, 1.17F};
        this.width = 9.0F;
        this.oscScl = 0.8F;
        this.oscMag = 1.5F;
        this.largeHit = true;
        this.lightningChance = 0.0F;
        this.damage = damage;
        this.speed = 0.0F;
        this.hitEffect = Fx.hitBeam;
        this.despawnEffect = Fx.none;
        this.hitSize = 4.0F;
        this.drawSize = 420.0F;
        this.lifetime = 16.0F;
        this.hitColor = this.colors[2];
        this.incendAmount = 1;
        this.incendSpread = 5.0F;
        this.incendChance = 0.4F;
        this.lightColor = Color.orange;
        this.impact = true;
        this.keepVelocity = false;
        this.collides = false;
        this.pierce = true;
        this.hittable = false;
        this.absorbable = false;
    }

    public float continuousDamage() {
        return this.damage / 5.0F * 60.0F;
    }

    public float estimateDPS() {
        return this.damage * 100.0F / 5.0F * 3.0F;
    }

    public float range() {
        return Math.max(this.length, this.maxRange);
    }

    public void init() {
        super.init();
        this.drawSize = Math.max(this.drawSize, this.length * 2.0F);
    }

    public void update(Bullet b) {
        if (b.timer(1, 5.0F)) {
            b.fdata = this.length;
            Vec2 v = Tmp.v1.trns(b.rotation(), this.length).add(b);
            float w = this.largeHit ? 15.0F : 3.0F;
            Utils.collideLineRawNew(b.x, b.y, v.x, v.y, w, w, (bd) -> bd.team != b.team, (u) -> u.team != b.team && u.checkTarget(this.collidesAir, this.collidesGround), this.collidesTiles && this.collidesGround, true, (h) -> h.dst2(b), (x, y, ent, direct) -> {
                boolean hit = false;
                if (ent instanceof Unit) {
                    Unit u = (Unit)ent;
                    u.collision(b, x, y);
                    this.hitUnitAntiCheat(b, u);
                }

                if (ent instanceof Building) {
                    Building bd = (Building)ent;
                    if (direct) {
                        this.hitBuildingAntiCheat(b, bd);
                    }

                    hit = bd.block.absorbLasers;
                }

                this.hit(b, x, y);
                return hit;
            }, true);
        }

        if (this.lightningChance > 0.0F && Mathf.chanceDelta((double)this.lightningChance)) {
            Lightning.create(b.team, this.lightningColor, this.lightningDamage, b.x, b.y, b.rotation(), this.lightningLength + Mathf.random(this.lightningLengthRand));
        }

        if (this.shake > 0.0F) {
            Effect.shake(this.shake, this.shake, b);
        }

    }

    public void draw(Bullet b) {
        float realLength = Damage.findLaserLength(b, this.length);
        float fout = Mathf.clamp(b.time > b.lifetime - this.fadeTime ? 1.0F - (b.time - (this.lifetime - this.fadeTime)) / this.fadeTime : 1.0F);
        float baseLen = realLength * fout;
        Lines.lineAngle(b.x, b.y, b.rotation(), baseLen);

        for(int s = 0; s < this.colors.length; ++s) {
            Draw.color(Tmp.c1.set(this.colors[s]).mul(1.0F + Mathf.absin(Time.time, 1.0F, 0.1F)));

            for(int i = 0; i < this.tscales.length; ++i) {
                Tmp.v1.trns(b.rotation() + 180.0F, (this.lenscales[i] - 1.0F) * this.spaceMag);
                Lines.stroke((this.width + Mathf.absin(Time.time, this.oscScl, this.oscMag)) * fout * this.strokes[s] * this.tscales[i]);
                Lines.lineAngle(b.x + Tmp.v1.x, b.y + Tmp.v1.y, b.rotation(), baseLen * this.lenscales[i], false);
            }
        }

        Tmp.v1.trns(b.rotation(), baseLen * 1.1F);
        Drawf.light(b.team, b.x, b.y, b.x + Tmp.v1.x, b.y + Tmp.v1.y, this.lightStroke, this.lightColor, 0.7F);
        Draw.reset();
    }

    public void drawLight(Bullet b) {
    }
}
