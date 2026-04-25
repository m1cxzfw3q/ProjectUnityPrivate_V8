package unity.entities.bullet.misc;

import arc.Core;
import arc.audio.Sound;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Tmp;
import mindustry.entities.Sized;
import mindustry.entities.Units;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Bullet;
import mindustry.gen.Healthc;
import mindustry.gen.Sounds;
import unity.content.UnityBullets;
import unity.content.effects.HitFx;
import unity.content.effects.ShootFx;

public class ShootingBulletType extends BulletType {
    public float targetRange = 220.0F;
    public float minTargetRange = 90.0F;
    public float smoothness = 35.0F;
    public float reloadTime = 20.0F;
    public float shootInaccuracy = 0.0F;
    public BulletType shootBullet;
    public Sound shootSound;
    public String name;
    protected TextureRegion region;

    public ShootingBulletType(String name, float speed, float damage) {
        super(speed, damage);
        this.shootBullet = UnityBullets.standardCopper;
        this.shootSound = Sounds.none;
        this.name = name;
        this.hittable = true;
        this.absorbable = false;
        this.collides = this.collidesTiles = false;
        this.drag = 0.05F;
        this.trailLength = 4;
        this.shootEffect = ShootFx.plagueShootSmokeLarge;
        this.hitEffect = HitFx.plagueLargeHit;
        this.trailChance = 0.2F;
        this.splashDamage = 30.0F;
        this.splashDamageRadius = 40.0F;
    }

    public void load() {
        this.region = Core.atlas.find(this.name);
    }

    public float estimateDPS() {
        float sum = this.splashDamage * 0.75F;
        if (this.fragBullet != null && this.fragBullet != this) {
            sum += this.fragBullet.estimateDPS() * (float)this.fragBullets / 2.0F;
        }

        return sum;
    }

    public void init(Bullet b) {
        super.init(b);
        b.fdata = b.rotation();
    }

    public void update(Bullet b) {
        super.update(b);
        if (b.timer(1, 5.0F)) {
            b.data = Units.closestTarget(b.team, b.x, b.y, this.targetRange);
        }

        if (b.data instanceof Healthc && Units.invalidateTarget((Healthc)b.data, b.team, b.x, b.y, this.targetRange * 1.1F)) {
            b.data = null;
        }

        if (b.data instanceof Sized) {
            Sized t = (Sized)b.data;
            float angTo = b.angleTo(t);
            int side = Mathf.randomSeed((long)b.id * 913L + (long)((int)(b.time / 90.0F)), 0, 1) == 0 ? -1 : 1;
            b.fdata = Angles.moveToward(b.fdata, angTo, 3.0F);
            Tmp.v2.trns(angTo + 180.0F + (float)side * this.speed / 2.0F, this.minTargetRange + t.hitSize() / 2.0F).add(t);
            Tmp.v1.set(Tmp.v2).sub(b).limit(this.speed).scl(1.0F / this.smoothness);
            b.vel.add(Tmp.v1).limit(this.speed);
            if (Angles.within(b.fdata, angTo, 2.0F) && b.within(t, this.shootBullet.range()) && b.timer(2, this.reloadTime)) {
                this.shootBullet.shootEffect.at(b.x, b.y, b.fdata);
                this.shootSound.at(b);
                this.shootBullet.create(b, b.team, b.x, b.y, b.fdata + Mathf.range(this.shootInaccuracy));
            }
        } else {
            b.fdata = Mathf.slerpDelta(b.fdata, b.rotation(), 0.1F);
        }

    }

    public void draw(Bullet b) {
        super.draw(b);
        Draw.rect(this.region, b.x, b.y, b.fdata - 90.0F);
    }
}
