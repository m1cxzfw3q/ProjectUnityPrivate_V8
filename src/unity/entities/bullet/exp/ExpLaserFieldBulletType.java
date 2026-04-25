package unity.entities.bullet.exp;

import arc.Core;
import arc.math.Mathf;
import arc.math.geom.Position;
import arc.math.geom.Vec2;
import arc.util.Time;
import mindustry.entities.Damage;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.gen.Entityc;
import mindustry.gen.Healthc;
import mindustry.gen.Sounds;
import mindustry.gen.Unit;
import unity.content.UnityFx;
import unity.gen.Expc;

public class ExpLaserFieldBulletType extends ExpLaserBulletType {
    public BulletType distField;
    public BulletType smallDistField;
    public int fields;
    public float fieldInc;

    public ExpLaserFieldBulletType(float length, float damage) {
        super(length, damage);
    }

    int getFields(Bullet b) {
        return this.fields + Mathf.floor(this.fieldInc * (float)this.getLevel(b) * b.damageMultiplier());
    }

    public void init(Bullet b) {
        Position vec;
        label50: {
            label56: {
                super.init(b);
                this.setDamage(b);
                Healthc target = Damage.linecast(b, b.x, b.y, b.rotation(), this.getLength(b));
                vec = (new Vec2()).trns(b.rotation(), this.getLength(b)).add(b.x, b.y);
                if (!(target instanceof Unit)) {
                    if (!(target instanceof Building)) {
                        break label56;
                    }

                    Building tile = (Building)target;
                    if (!tile.collide(b)) {
                        break label56;
                    }
                }

                vec = target;
                if (target instanceof Unit) {
                    Unit unit = (Unit)target;
                    unit.collision(b, target.getX(), target.getY());
                    b.collision(unit, target.getX(), target.getY());
                } else {
                    ((Building)target).collision(b);
                    this.hit(b, target.getX(), target.getY());
                }

                Entityc var6 = b.owner;
                if (var6 instanceof Expc.ExpBuildc) {
                    Expc.ExpBuildc exp = (Expc.ExpBuildc)var6;
                    if (exp.levelf() < 1.0F && Core.settings.getBool("hitexpeffect")) {
                        for(int i = 0; (double)i < Math.ceil((double)this.buildingExpGain); ++i) {
                            UnityFx.expGain.at(vec.getX(), vec.getY(), 0.0F, b.owner);
                        }
                    }

                    exp.incExp((float)this.buildingExpGain);
                }

                this.distField.create(b.owner, b.team, vec.getX(), vec.getY(), 0.0F);
                break label50;
            }

            this.smallDistField.create(b.owner, b.team, vec.getX(), vec.getY(), 0.0F);
        }

        Sounds.spark.at(vec.getX(), vec.getY(), 0.4F);
        Sounds.spray.at(vec.getX(), vec.getY(), 0.4F);
        UnityFx.chainLightning.at(b.x, b.y, 0.0F, this.getColor(b), vec);
        Position finalVec = vec;

        for(int i = 0; i < this.getFields(b); ++i) {
            Time.run(6.0F * (float)i + 1.0F + UnityFx.smallChainLightning.lifetime * 0.5F, () -> {
                float tx = finalVec.getX() + (float)(Mathf.range(8) * 8);
                float ty = finalVec.getY() + (float)(Mathf.range(8) * 8);
                UnityFx.smallChainLightning.at(finalVec.getX(), finalVec.getY(), 0.0F, this.getColor(b), new Vec2(tx, ty));
                Sounds.spark.at(tx, ty, 0.4F);
                Sounds.spray.at(tx, ty, 0.4F);
                this.smallDistField.create(b.owner, b.team, tx, ty, 0.0F);
            });
        }

    }
}
