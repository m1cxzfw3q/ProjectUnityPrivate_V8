package unity.entities.bullet.energy;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.Units;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Bullet;
import mindustry.graphics.Pal;
import mindustry.type.StatusEffect;
import unity.util.Utils;

public class HealingNukeBulletType extends BulletType {
    public float radius = 650.0F;
    public int rays = 180;
    public StatusEffect allyStatus;
    public float allyStatusDuration;

    public HealingNukeBulletType() {
        super(0.0F, 20.0F);
        this.allyStatus = StatusEffects.none;
        this.allyStatusDuration = 120.0F;
        this.hitEffect = Fx.none;
        this.despawnEffect = Fx.none;
        this.shootEffect = Fx.none;
        this.smokeEffect = Fx.none;
        this.buildingDamageMultiplier = 10.0F;
        this.collides = this.collidesTiles = false;
        this.hittable = this.absorbable = false;
    }

    public float range() {
        return this.radius;
    }

    public void init() {
        super.init();
        this.drawSize = this.radius * 2.0F;
    }

    public void init(Bullet b) {
        float[] data = Utils.castCircle(b.x, b.y, this.radius, this.rays, (bd) -> true, (building) -> {
            if (building.team == b.team) {
                Fx.healBlockFull.at(building.x, building.y, (float)building.block.size, Pal.heal);
                building.heal(this.healPercent / 100.0F * building.maxHealth);
            } else {
                building.damage(this.damage * b.damageMultiplier() * this.buildingDamageMultiplier);
            }

        }, (tile) -> tile.block().absorbLasers && tile.team() != b.team);
        Units.nearby(Tmp.r1.setCentered(b.x, b.y, this.radius * 2.0F), (u) -> {
            float ang = b.angleTo(u);
            float dst = u.dst2(b) - u.hitSize * u.hitSize / 2.0F;
            int idx = Mathf.mod(Mathf.round(ang % 360.0F / (360.0F / (float)data.length)), data.length);
            float d = data[idx];
            if (b.within(u, this.radius + u.hitSize / 2.0F) && dst <= d * d) {
                if (u.team == b.team) {
                    u.heal(this.healPercent / 100.0F * u.maxHealth);
                    u.apply(this.allyStatus, this.allyStatusDuration);
                } else {
                    u.damage(this.damage * b.damageMultiplier());
                    u.apply(this.status, this.statusDuration);
                }
            }

        });
        b.data = data;
    }

    public void draw(Bullet b) {
        Object var3 = b.data;
        if (var3 instanceof float[]) {
            float[] data = (float[])var3;
            Draw.color(Pal.heal);
            Draw.alpha(0.3F * b.fout());

            for(int i = 0; i < data.length; ++i) {
                float ang1 = (float)i * (360.0F / (float)data.length);
                float ang2 = ((float)i + 1.0F) * (360.0F / (float)data.length);
                float len1 = data[i];
                float len2 = data[(i + 1) % data.length];
                Vec2 v1 = Tmp.v1.trns(ang1, len1).add(b);
                Vec2 v2 = Tmp.v2.trns(ang2, len2).add(b);
                Fill.tri(b.x, b.y, v1.x, v1.y, v2.x, v2.y);
            }

            Draw.reset();
        }

    }
}
