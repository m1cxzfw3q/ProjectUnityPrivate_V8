package unity.entities.bullet.energy;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.entities.Damage;
import mindustry.entities.Units;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.gen.Bullet;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import unity.content.UnityFx;

public class GluonOrbBulletType extends BasicBulletType {
    public float force = 8.0F;
    public float scaledForce = 7.0F;
    public float beamStroke = 0.7F;
    public float radius = 80.0F;
    protected TextureRegion laser;
    protected TextureRegion laserEnd;

    public GluonOrbBulletType(float speed, float damage) {
        super(speed, damage);
        this.pierce = this.pierceBuilding = true;
        this.despawnEffect = this.hitEffect = Fx.none;
    }

    public void load() {
        super.load();
        this.laser = Core.atlas.find("laser");
        this.laserEnd = Core.atlas.find("laser-end");
    }

    public void init(Bullet b) {
        super.init(b);
        b.data = new GluonOrbData();
    }

    public void update(Bullet b) {
        super.update(b);
        if (b.timer(0, 2.0F + b.fslope() * 1.5F)) {
            UnityFx.lightHexagonTrail.at(b.x, b.y, 1.0F + b.fslope() * 4.0F);
        }

        if (b.timer(1, 2.0F)) {
            Object var3 = b.data;
            if (var3 instanceof GluonOrbData) {
                GluonOrbData d = (GluonOrbData)var3;
                d.units.clear();
                Units.nearbyEnemies(b.team, b.x - this.radius, b.y - this.radius, this.radius * 2.0F, this.radius * 2.0F, (u) -> {
                    if (u != null && Mathf.within(b.x, b.y, u.x, u.y, this.radius)) {
                        d.units.add(u);
                    }

                });
                Damage.damage(b.team, b.x, b.y, this.hitSize, this.damage);
            }
        }

        Object var5 = b.data;
        if (var5 instanceof GluonOrbData) {
            GluonOrbData d = (GluonOrbData)var5;
            d.units.each((u) -> {
                if (!u.dead) {
                    float ang = u.angleTo(b);
                    if (Angles.angleDist(b.rotation(), ang) < 90.0F) {
                        Tmp.v1.trns(ang, this.force + (1.0F - u.dst(b) / this.radius) * this.scaledForce * (u.isFlying() ? 1.5F : 1.0F)).scl(20.0F * Time.delta);
                        u.impulse(Tmp.v1);
                    }

                }
            });
        }

    }

    public void draw(Bullet b) {
        Draw.color(Pal.lancerLaser);
        Object var3 = b.data;
        if (var3 instanceof GluonOrbData) {
            GluonOrbData d = (GluonOrbData)var3;
            d.units.each((u) -> {
                if (u != null) {
                    Drawf.laser(b.team, this.laser, this.laserEnd, b.x, b.y, u.x, u.y, this.beamStroke);
                }

            });
        }

        Fill.circle(b.x, b.y, 6.0F + b.fout() * 1.5F);
        Draw.color(Color.white);
        Fill.circle(b.x, b.y, 4.5F + b.fout());
    }

    public static class GluonOrbData {
        Seq<Unit> units = new Seq();
    }
}
