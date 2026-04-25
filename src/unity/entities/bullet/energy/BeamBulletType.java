package unity.entities.bullet.energy;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Position;
import arc.math.geom.Vec2;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.entities.Damage;
import mindustry.entities.Lightning;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.gen.Healthc;
import mindustry.gen.Hitboxc;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;

public class BeamBulletType extends BulletType {
    public Color color;
    public float beamWidth;
    public float lightWidth;
    public float length;
    public boolean castsLightning;
    public float castInterval;
    public float minLightningDamage;
    public float maxLightningDamage;
    public String name;
    public TextureRegion region;
    public TextureRegion endRegion;

    public BeamBulletType(float length, float damage) {
        this(length, damage, "laser");
    }

    public BeamBulletType(float length, float damage, String name) {
        super(0.01F, damage);
        this.color = Pal.heal;
        this.beamWidth = 0.6F;
        this.lightWidth = 15.0F;
        this.castInterval = 5.0F;
        this.length = length;
        this.name = name;
        this.keepVelocity = false;
        this.collides = false;
        this.pierce = true;
        this.hittable = false;
        this.absorbable = false;
        this.lifetime = 16.0F;
        this.shootEffect = Fx.none;
        this.despawnEffect = Fx.none;
        this.hitSize = 0.0F;
    }

    public BeamBulletType() {
        this(1.0F, 1.0F);
    }

    public void load() {
        this.region = Core.atlas.find(this.name);
        this.endRegion = Core.atlas.find(this.name + "-end");
    }

    public void update(Bullet b) {
        super.update(b);
        Healthc target = Damage.linecast(b, b.x, b.y, b.rotation(), this.length);
        b.data = target;
        if (target instanceof Hitboxc) {
            Hitboxc hit = (Hitboxc)target;
            if (b.timer.get(1, this.castInterval)) {
                hit.collision(b, target.x(), target.y());
                b.collision(hit, target.x(), target.y());
                if (this.castsLightning) {
                    Lightning.create(b.team, this.color, Mathf.random(this.minLightningDamage, this.maxLightningDamage), b.x, b.y, b.angleTo(target), Mathf.floorPositive(b.dst(target) / 8.0F + 3.0F));
                }
            }
        } else if (target instanceof Building) {
            Building build = (Building)target;
            if (b.timer.get(1, this.castInterval)) {
                if (build.collide(b)) {
                    build.collision(b);
                    this.hit(b, target.x(), target.y());
                }

                if (this.castsLightning) {
                    Lightning.create(b.team, this.color, Mathf.random(this.minLightningDamage, this.maxLightningDamage), b.x, b.y, b.angleTo(target), Mathf.floorPositive(b.dst(target) / 8.0F + 3.0F));
                }
            }
        } else {
            b.data = (new Vec2()).trns(b.rotation(), this.length).add(b.x, b.y);
            if (b.timer.get(1, this.castInterval) && this.castsLightning) {
                Vec2 point = (Vec2)b.data;
                Lightning.create(b.team, this.color, Mathf.random(this.minLightningDamage, this.maxLightningDamage), b.x, b.y, b.angleTo(point.x, point.y), Mathf.floorPositive(b.dst(point.x, point.y) / 8.0F + 3.0F));
            }
        }

    }

    public float range() {
        return this.length;
    }

    public void draw(Bullet b) {
        Object var3 = b.data;
        if (var3 instanceof Position) {
            Position data = (Position)var3;
            Tmp.v1.set(data);
            Draw.color(this.color);
            Drawf.laser(b.team, this.region, this.endRegion, b.x, b.y, Tmp.v1.x, Tmp.v1.y, this.beamWidth * b.fout());
            Draw.reset();
            Drawf.light(b.team, b.x, b.y, Tmp.v1.x, Tmp.v1.y, this.lightWidth * b.fout(), this.color, 0.6F);
        }

    }
}
