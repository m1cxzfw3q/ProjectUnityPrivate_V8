package unity.entities.bullet.energy;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Interp;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.entities.Damage;
import mindustry.entities.Units;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.gen.Bullet;
import mindustry.graphics.Pal;
import unity.content.UnityFx;

public class GluonWhirlBulletType extends BasicBulletType {
    public float force = 8.0F;
    public float scaledForce = 7.0F;
    public float radius = 100.0F;

    public GluonWhirlBulletType(float damage) {
        super(0.001F, damage);
        this.pierce = this.pierceBuilding = true;
        this.despawnEffect = this.hitEffect = Fx.none;
    }

    public void init(Bullet b) {
        super.init(b);
        b.data = new GluonOrbBulletType.GluonOrbData();
    }

    public void update(Bullet b) {
        super.update(b);
        Object var3 = b.data;
        if (var3 instanceof GluonOrbBulletType.GluonOrbData) {
            GluonOrbBulletType.GluonOrbData data = (GluonOrbBulletType.GluonOrbData)var3;
            if (Mathf.chance((double)(Time.delta * 0.7F * b.fout()))) {
                UnityFx.whirl.at(b);
            }

            if (b.timer(0, 2.0F)) {
                data.units.clear();
                Units.nearbyEnemies(b.team, b.x - this.radius, b.y - this.radius, this.radius * 2.0F, this.radius * 2.0F, (u) -> {
                    if (u != null && Mathf.within(b.x, b.y, u.x, u.y, this.radius)) {
                        data.units.add(u);
                    }

                });
                Damage.damage(b.team, b.x, b.y, this.hitSize, this.damage);
            }

            data.units.each((u) -> {
                if (!u.dead) {
                    Tmp.v1.trns(u.angleTo(b), this.force + (1.0F - u.dst(b) / this.radius) * this.scaledForce * b.fout(Interp.pow2In) * (u.isFlying() ? 1.5F : 1.0F)).scl(20.0F * Time.delta);
                    u.impulse(Tmp.v1);
                }

            });
        }
    }

    public void draw(Bullet b) {
        Draw.color(Pal.lancerLaser);
        Fill.circle(b.x, b.y, b.fout() * 7.5F);
        Draw.color(Color.white);
        Fill.circle(b.x, b.y, b.fout() * 5.5F);
    }
}
