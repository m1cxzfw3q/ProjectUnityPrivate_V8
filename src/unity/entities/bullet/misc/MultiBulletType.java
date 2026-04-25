package unity.entities.bullet.misc;

import arc.audio.Sound;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.units.WeaponMount;
import mindustry.game.Team;
import mindustry.gen.Bullet;
import mindustry.gen.Entityc;
import mindustry.gen.Sounds;
import mindustry.gen.Unit;
import mindustry.type.Weapon;

public class MultiBulletType extends BulletType {
    public boolean mirror = true;
    public Sound shootSound;
    public MultiBulletData[] bullets;

    public MultiBulletType() {
        this.shootSound = Sounds.none;
        this.bullets = new MultiBulletData[0];
        this.hittable = false;
        this.absorbable = false;
        this.despawnEffect = this.hitEffect = this.shootEffect = Fx.none;
        this.collides = this.collidesTiles = false;
        this.lifetime = 0.0F;
        this.speed = 0.0F;
        this.damage = 0.0F;
    }

    public float range() {
        float max = 0.0F;

        for(MultiBulletData b : this.bullets) {
            max = Math.max(max, b.type.range());
        }

        return max;
    }

    public float estimateDPS() {
        float sum = 0.0F;

        for(MultiBulletData b : this.bullets) {
            float x = !this.mirror || b.x == 0.0F && b.rotation == 0.0F ? 1.0F : 2.0F;
            sum += b.type.estimateDPS() * x;
        }

        return sum;
    }

    public void init(Bullet b) {
        if (this.lifetime <= 0.0F) {
            b.remove();
        } else {
            Entityc var3 = b.owner;
            if (var3 instanceof Unit) {
                Unit e = (Unit)var3;
                b.data = (new Vec2(b.x - e.x, b.y - e.y)).rotate(-e.rotation);

                for(WeaponMount mount : e.mounts) {
                    if (mount.weapon.bullet == this) {
                        Weapon w = mount.weapon;
                        float mx = e.x + Angles.trnsx(e.rotation - 90.0F, w.x, w.y);
                        float my = e.y + Angles.trnsy(e.rotation - 90.0F, w.x, w.y);
                        float weaponRotation = e.rotation - 90.0F + (w.rotate ? mount.rotation : 0.0F);
                        float bx = mx + Angles.trnsx(weaponRotation, w.shootX, w.shootY);
                        float by = my + Angles.trnsy(weaponRotation, w.shootX, w.shootY);
                        b.fdata = Mathf.dst(bx, by, mount.aimX, mount.aimY);
                        break;
                    }
                }
            }
        }

    }

    public void update(Bullet b) {
        Object var4 = b.data;
        if (var4 instanceof Vec2) {
            Vec2 v = (Vec2)var4;
            Entityc var5 = b.owner;
            if (var5 instanceof Unit) {
                Unit e = (Unit)var5;
                Tmp.v1.set(v).rotate(e.rotation).add(e);
                b.set(Tmp.v1);
                b.rotation(e.rotation);
            }
        }

    }

    public void despawned(Bullet b) {
        if (this.bullets.length > 0) {
            Entityc var3 = b.owner;
            if (var3 instanceof Unit) {
                Unit e = (Unit)var3;
                if (e.isAdded()) {
                    for(MultiBulletData data : this.bullets) {
                        float scl = data.type.scaleVelocity ? Mathf.clamp(b.fdata / data.type.range()) : 1.0F;
                        Tmp.v1.trns(b.rotation(), data.x, data.y).add(b);
                        data.type.create(b.owner, b.team, Tmp.v1.x, Tmp.v1.y, b.rotation() + data.rotation, 1.0F, scl);
                        data.type.shootEffect.at(Tmp.v1.x, Tmp.v1.y, b.rotation() + data.rotation);
                        if (this.mirror && (data.x != 0.0F || data.rotation != 0.0F)) {
                            Tmp.v1.trns(b.rotation(), -data.x, data.y).add(b);
                            data.type.create(b.owner, b.team, Tmp.v1.x, Tmp.v1.y, b.rotation() - data.rotation, 1.0F, scl);
                            data.type.shootEffect.at(Tmp.v1.x, Tmp.v1.y, b.rotation() - data.rotation);
                        }
                    }

                    this.shootSound.at(b, Mathf.random(0.9F, 1.1F));
                }
            }
        }

    }

    public void hit(Bullet b, float x, float y) {
    }

    public void drawLight(Bullet b) {
    }

    public Bullet create(Entityc owner, Team team, float x, float y, float angle, float damage, float velocityScl, float lifetimeScl, Object data) {
        Bullet b = super.create(owner, team, x, y, angle, damage, velocityScl, lifetimeScl, data);
        b.vel.setZero();
        return b;
    }

    public static class MultiBulletData {
        BulletType type;
        float x;
        float y;
        float rotation;

        public MultiBulletData(BulletType type, float x, float y, float rotation) {
            this.type = type;
            this.x = x;
            this.y = y;
            this.rotation = rotation;
        }
    }
}
