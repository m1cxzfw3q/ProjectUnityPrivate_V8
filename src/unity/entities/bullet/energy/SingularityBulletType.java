package unity.entities.bullet.energy;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.TextureRegion;
import arc.math.Interp;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Damage;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.gen.Bullet;
import mindustry.graphics.Pal;
import unity.content.UnityFx;
import unity.graphics.UnityDrawf;
import unity.util.Utils;

public class SingularityBulletType extends BasicBulletType {
    public float force = 8.0F;
    public float scaledForce = 5.0F;
    public float tileDamage = 150.0F;
    public float radius = 230.0F;
    public float size = 5.0F;
    public float[] scales = new float[]{8.6F, 7.0F, 5.5F, 4.2F, 3.9F};
    public Color[] colors;

    public SingularityBulletType(float damage) {
        super(0.001F, damage);
        this.colors = new Color[]{Color.valueOf("4787ff80"), Pal.lancerLaser, Color.white, Pal.lancerLaser, Color.black};
        this.pierce = this.pierceBuilding = true;
        this.hitEffect = Fx.none;
        this.despawnEffect = UnityFx.singularityDespawn;
    }

    public void init(Bullet b) {
        super.init(b);
        b.data = new GluonOrbBulletType.GluonOrbData();
    }

    public void update(Bullet b) {
        super.update(b);
        float interp = b.fin(Interp.exp10Out);
        Effect.shake(interp, interp, b);
        if (b.timer(1, 7.0F)) {
            Utils.trueEachBlock(b.x, b.y, this.radius, (e) -> {
                if (e.isValid() && e.team != b.team) {
                    if (e.health < this.tileDamage || Mathf.within(b.x, b.y, e.x, e.y, interp * this.size * 3.9F + (float)e.block.size / 2.0F)) {
                        e.kill();
                        if (!Vars.headless) {
                            UnityFx.singularityAttraction.at(b.x, b.y, (float)e.rotation, new SingularityAbsorbEffectData(e.block.fullIcon, e.x, e.y));
                        }
                    }

                    float dst = Math.abs(1.0F - Mathf.dst(b.x, b.y, e.x, e.y) / this.radius);
                    e.damage(this.tileDamage * this.buildingDamageMultiplier * dst);
                }

            });
        }

        Object var4 = b.data;
        if (var4 instanceof GluonOrbBulletType.GluonOrbData) {
            GluonOrbBulletType.GluonOrbData data = (GluonOrbBulletType.GluonOrbData)var4;
            if (b.timer(2, 2.0F)) {
                data.units.clear();
                Units.nearbyEnemies(b.team, b.x - this.radius, b.y - this.radius, 2.0F * this.radius, 2.0F * this.radius, (u) -> {
                    if (u != null && Mathf.within(b.x, b.y, u.x, u.y, this.radius)) {
                        data.units.add(u);
                        if (Mathf.within(b.x, b.y, u.x, u.y, interp * this.size * 3.9F + u.hitSize / 2.0F)) {
                            u.damage(120.0F);
                        }
                    }

                });
                Damage.damage(b.team, b.x, b.y, this.hitSize, this.damage);
            }

            data.units.each((u) -> {
                if (!u.dead) {
                    Tmp.v1.trns(u.angleTo(b), this.force + (1.0F - u.dst(b) / this.radius) * this.scaledForce * b.fin(Interp.exp10Out) * (u.isFlying() ? 1.5F : 1.0F)).scl(20.0F * Time.delta);
                    u.impulse(Tmp.v1);
                }

            });
        }

    }

    public void draw(Bullet b) {
        float interp = b.fin(Interp.exp10Out);

        for(int i = 0; i < this.colors.length; ++i) {
            Draw.color(this.colors[i]);
            if (i != 0) {
                float extra = ((float)this.colors.length - ((float)i + 1.0F)) * (11.0F / ((float)this.colors.length - 1.0F));
                UnityDrawf.shiningCircle(b.id, Time.time - (float)i, b.x + Mathf.range(0.5F), b.y + Mathf.range(0.5F), interp * this.size * this.scales[i], 6, 30.0F, 17.0F + extra / 2.0F, (10.0F + extra) * interp, 60.0F);
            } else {
                Fill.circle(b.x + Mathf.range(0.5F), b.y + Mathf.range(0.5F), interp * this.size * this.scales[i]);
            }
        }

        Draw.color();
    }

    public static class SingularityAbsorbEffectData {
        public TextureRegion region;
        public float x;
        public float y;

        public SingularityAbsorbEffectData(TextureRegion region, float x, float y) {
            this.region = region;
            this.x = x;
            this.y = y;
        }
    }
}
