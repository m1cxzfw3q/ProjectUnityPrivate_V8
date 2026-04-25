package unity.entities.bullet.monolith.energy;

import arc.math.geom.Vec2;
import mindustry.entities.Predict;
import mindustry.entities.Units;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.gen.Hitboxc;
import mindustry.gen.Posc;
import mindustry.gen.Teamc;
import mindustry.graphics.Trail;

/** @deprecated */
@Deprecated
public class RicochetBulletType extends BasicBulletType {
    public int trailLength;

    public RicochetBulletType(float speed, float damage) {
        this(speed, damage, "bullet");
    }

    public RicochetBulletType(float speed, float damage, String spriteName) {
        super(speed, damage, spriteName);
        this.trailLength = 6;
        this.pierce = true;
        this.pierceBuilding = true;
        this.pierceCap = 3;
        this.trailChance = 1.0F;
    }

    public void init(Bullet b) {
        super.init(b);
        b.data = new RicochetBulletData();
    }

    public void hitEntity(Bullet b, Hitboxc other, float initialHealth) {
        this.ricochet(b, other);
    }

    public void hitTile(Bullet b, Building build, float initialHealth, boolean direct) {
        super.hitTile(b, build, initialHealth, direct);
        if (direct) {
            this.ricochet(b, build);
        }

    }

    public void update(Bullet b) {
        super.update(b);
        RicochetBulletData data = (RicochetBulletData)b.data;
        if (data.trail != null) {
            data.trail.update(b.x, b.y);
        }

    }

    public void draw(Bullet b) {
        RicochetBulletData data = (RicochetBulletData)b.data;
        if (data.trail != null) {
            data.trail.draw(this.backColor, this.width * 0.18F);
        }

        super.draw(b);
    }

    public void ricochet(Bullet b, Posc entity) {
        RicochetBulletData data = (RicochetBulletData)b.data;
        if (data != null) {
            if (data.hit != entity.id()) {
                data.hit = entity.id();
                b.collided.clear();
                if (data.ricochet < this.pierceCap) {
                    data.findEnemy(b);
                    if (data.target != null) {
                        Teamc var5 = data.target;
                        if (var5 instanceof Hitboxc) {
                            Hitboxc v = (Hitboxc)var5;
                            Vec2 out = Predict.intercept(b.x, b.y, v.x(), v.y(), v.deltaX(), v.deltaY(), b.vel.len());
                            float rot = out.sub(b.x, b.y).angle();
                            b.vel.setAngle(rot);
                        } else {
                            b.vel.setAngle(b.angleTo(data.target));
                        }
                    } else {
                        this.despawned(b);
                    }
                }

            }
        }
    }

    public class RicochetBulletData {
        protected int ricochet;
        protected Teamc target;
        protected int hit;
        protected Trail trail;

        protected RicochetBulletData() {
            this.trail = new Trail(RicochetBulletType.this.trailLength);
        }

        protected void findEnemy(Bullet b) {
            this.target = Units.closestTarget(b.team, b.x, b.y, RicochetBulletType.this.range() * b.fout(), (u) -> u.isValid() && u.id != this.hit && (u.isFlying() && RicochetBulletType.this.collidesAir || u.isGrounded() && RicochetBulletType.this.collidesGround), (t) -> t.isValid() && t.id != this.hit && RicochetBulletType.this.collidesGround);
        }
    }
}
