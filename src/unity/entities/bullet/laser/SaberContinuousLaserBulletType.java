package unity.entities.bullet.laser;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.entities.Damage;
import mindustry.entities.Lightning;
import mindustry.entities.bullet.ContinuousLaserBulletType;
import mindustry.gen.Bullet;
import mindustry.gen.Entityc;
import mindustry.gen.Velc;
import mindustry.graphics.Drawf;
import unity.content.UnityFx;
import unity.entities.SaberData;
import unity.graphics.UnityPal;
import unity.util.Utils;

public class SaberContinuousLaserBulletType extends ContinuousLaserBulletType {
    protected boolean swipe;
    protected float swipeTime;
    protected float swipeDamageMultiplier;

    public SaberContinuousLaserBulletType(float damage) {
        super(damage);
        this.swipeTime = 40.0F;
        this.swipeDamageMultiplier = 1.0F;
    }

    public SaberContinuousLaserBulletType() {
        this(0.0F);
    }

    float chargedDamage(Bullet b, float val) {
        return b.time < this.swipeTime ? this.swipeDamageMultiplier * val * (this.swipeTime - b.time) : 0.0F;
    }

    public void update(Bullet b) {
        if (!(b.data instanceof SaberData)) {
            b.data = new SaberData(0.0F, 3, b.rotation(), 10);
        }

        SaberData temp = (SaberData)b.data;
        if (this.swipe) {
            float angDst = Angles.angleDist(b.rotation(), temp.rot) / Time.delta;
            temp.mean.add(angDst);
            angDst = temp.mean.rawMean();
            Entityc var5 = b.owner;
            if (var5 instanceof Velc) {
                Velc v = (Velc)var5;
                temp.f = Mathf.clamp(temp.f + v.vel().len() / 2.0F + angDst, 0.0F, this.length + angDst * 7.0F);
            }

            float damageC = this.chargedDamage(b, angDst);
            float realLength = Damage.findLaserLength(b, temp.f);
            float fout = Mathf.clamp(b.time > b.lifetime - this.fadeTime ? 1.0F - (b.time - (this.lifetime - this.fadeTime)) / this.fadeTime : 1.0F);
            float baseLen = realLength * fout;
            if (b.timer(1, 5.0F)) {
                Damage.collideLine(b, b.team, Fx.none, b.x, b.y, b.rotation(), temp.f, this.largeHit);
                if (angDst > 1.0E-4F) {
                    Utils.collideLineDamageOnly(b.team, (angDst + damageC) * 2.0F, b.x, b.y, b.rotation(), temp.f, b);
                }
            }

            if (b.time < 25.0F) {
                float c = (25.0F - b.time) * (angDst / 25.0F) / 25.0F;

                for(int i = 0; i < 3; ++i) {
                    float lenRangedB = baseLen + Mathf.range(16.0F);
                    if (Mathf.chanceDelta((double)c) && lenRangedB >= 8.0F) {
                        Lightning.create(b, UnityPal.scarColor, 3.0F + damageC / 2.0F, b.x, b.y, b.rotation(), Mathf.round(lenRangedB / 8.0F));
                    }
                }
            }

            float lenRanged = baseLen + Mathf.range(16.0F);
            if (Mathf.chanceDelta((double)((0.1F + Mathf.clamp(angDst / 25.0F)) * b.fout())) && Mathf.round(lenRanged / 8.0F) >= 1) {
                Lightning.create(b, UnityPal.scarColor, 6.0F + angDst * 1.7F + damageC * 2.0F, b.x, b.y, b.rotation(), Mathf.round(lenRanged / 8.0F));
            }

            if (Mathf.chanceDelta((double)(0.12F * b.fout()))) {
                UnityFx.falseLightning.at(b.x, b.y, b.rotation(), UnityPal.scarColor, baseLen);
            }

            temp.rot = b.rotation();
            Tmp.v1.trns(b.rotation(), baseLen * this.lenscales[this.lenscales.length - 1] / 2.0F);
            temp.fT.update(b.x + Tmp.v1.x, b.y + Tmp.v1.y, b.rotation() + 90.0F);
        } else {
            temp.f = this.length;
            Entityc var14 = b.owner;
            if (var14 instanceof Velc) {
                Velc v = (Velc)var14;
                temp.f = Mathf.clamp(v.vel().len() * 19.0F, 0.0F, this.length);
            }

            if (b.timer(1, 5.0F)) {
                Damage.collideLine(b, b.team, Fx.none, b.x, b.y, b.rotation(), temp.f, this.largeHit);
            }
        }

    }

    public void draw(Bullet b) {
        Object var3 = b.data;
        if (var3 instanceof SaberData) {
            SaberData temp = (SaberData)var3;
            float var8 = Damage.findLaserLength(b, temp.f);
            float fout = Mathf.clamp(b.time > b.lifetime - this.fadeTime ? 1.0F - (b.time - (this.lifetime - this.fadeTime)) / this.fadeTime : 1.0F);
            float baseLen = var8 * fout;
            temp.fT.draw(UnityPal.scarColor, baseLen * this.lenscales[this.lenscales.length - 1] * 0.5F);
            Lines.lineAngle(b.x, b.y, b.rotation(), baseLen);

            for(int s = 0; s < this.colors.length; ++s) {
                Draw.color(Tmp.c1.set(this.colors[s]).mul(1.0F + Mathf.absin(1.0F, 0.1F)));

                for(int i = 0; i < this.tscales.length; ++i) {
                    Tmp.v1.trns(b.rotation() + 180.0F, (this.lenscales[i] - 1.0F) * this.spaceMag);
                    Lines.stroke((this.width + Mathf.absin(this.oscScl, this.oscMag)) * fout * this.strokes[s] * this.tscales[i]);
                    Lines.lineAngle(b.x + Tmp.v1.x, b.y + Tmp.v1.y, b.rotation(), baseLen * this.lenscales[i], false);
                }
            }

            Tmp.v1.trns(b.rotation(), baseLen * 1.1F);
            Drawf.light(b.team, b.x, b.y, b.x + Tmp.v1.x, b.y + Tmp.v1.y, this.lightStroke, this.lightColor, 0.7F);
            Draw.reset();
        }
    }
}
