package unity.entities.bullet.energy;

import arc.func.Boolf;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Bullet;
import mindustry.gen.Groups;
import mindustry.graphics.Pal;
import mindustry.type.StatusEffect;
import mindustry.world.Tile;
import unity.util.Utils;

public class HealingConeBulletType extends BulletType {
    public float cone = 45.0F;
    public float length = 250.0F;
    public int scanAccuracy = 30;
    public StatusEffect allyStatus;
    public float allyStatusDuration;
    public Color color;
    private int idx;

    public HealingConeBulletType(float damage) {
        this.allyStatus = StatusEffects.none;
        this.allyStatusDuration = 0.0F;
        this.color = Pal.heal;
        this.idx = 0;
        this.damage = damage;
        this.speed = 0.001F;
        this.hitEffect = Fx.none;
        this.despawnEffect = Fx.none;
        this.keepVelocity = false;
        this.collides = false;
        this.pierce = true;
        this.hittable = false;
        this.absorbable = false;
    }

    public float range() {
        return this.length;
    }

    public float continuousDamage() {
        return this.damage / 30.0F * 60.0F;
    }

    public float estimateDPS() {
        return this.damage * 100.0F / 30.0F * 3.0F;
    }

    public void init() {
        super.init();
        this.drawSize = Math.max(this.drawSize, this.length * 2.0F);
    }

    public void init(Bullet b) {
        super.init(b);
        b.data = new float[this.scanAccuracy];
    }

    public void update(Bullet b) {
        Object var3 = b.data;
        if (var3 instanceof float[]) {
            float[] data = (float[])var3;
            this.idx = 0;
            Utils.shotgunRange(this.scanAccuracy, this.cone, b.rotation(), (ang) -> {
                Tmp.v1.trns(ang, this.length).add(b);
                Vars.world.raycastEachWorld(b.x, b.y, Tmp.v1.x, Tmp.v1.y, (cx, cy) -> {
                    Tile tile = Vars.world.tile(cx, cy);
                    boolean bl = tile != null && tile.build != null && tile.team() != b.team && tile.block() != null && tile.block().absorbLasers;
                    if (bl) {
                        float dst = Math.min(b.dst((float)(cx * 8), (float)(cy * 8)), this.length);
                        data[this.idx] = dst * dst;
                    } else {
                        data[this.idx] = this.length * this.length;
                    }

                    return bl;
                });
                ++this.idx;
            });
            if (b.timer(1, 30.0F)) {
                Tmp.r1.setCentered(b.x, b.y, 1.0F);
                Utils.shotgunRange(3, this.cone, b.rotation(), (ang) -> {
                    Tmp.v1.trns(ang, this.length).add(b);
                    Tmp.r1.merge(Tmp.v1);
                });
                Groups.unit.intersect(Tmp.r1.x, Tmp.r1.y, Tmp.r1.width, Tmp.r1.height, (unit) -> {
                    if (b.within(unit, this.length + unit.hitSize / 2.0F) && Angles.within(b.rotation(), b.angleTo(unit), this.cone)) {
                        int index = Mathf.clamp(Mathf.round((Utils.angleDistSigned(b.angleTo(unit), b.rotation()) + this.cone) / (this.cone * 2.0F) * (float)(data.length - 1)), 0, data.length - 1);
                        if (b.dst2(unit) + unit.hitSize / 2.0F < data[index]) {
                            if (unit.team != b.team) {
                                unit.damage(b.damage);
                                unit.apply(this.status, this.statusDuration);
                            } else {
                                unit.heal(unit.maxHealth / 100.0F * this.healPercent);
                                unit.apply(this.allyStatus, this.allyStatusDuration);
                            }
                        }
                    }

                });
                Utils.castConeTile(b.x, b.y, this.length, b.rotation(), this.cone, (building, tile) -> {
                    if (building != null) {
                        if (building.team == b.team) {
                            if (building.damaged()) {
                                building.heal(building.maxHealth / 100.0F * this.healPercent);
                                Fx.healBlockFull.at(building.x, building.y, (float)building.block.size, Pal.heal);
                            }
                        } else {
                            building.damage(b.damage * this.buildingDamageMultiplier);
                        }
                    }

                }, (Boolf)null, data);
            }

        }
    }

    public void draw(Bullet b) {
        Object var3 = b.data;
        if (var3 instanceof float[]) {
            float[] data = (float[])var3;
            float var7 = Draw.z();
            Draw.z(122.0F);
            float fout = Mathf.clamp(b.time > b.lifetime - 16.0F ? 1.0F - (b.time - (b.lifetime - 16.0F)) / 16.0F : 1.0F) * Mathf.clamp(b.time / 16.0F) * this.length;
            Tmp.v1.trns(b.rotation() - this.cone, Math.min(Mathf.sqrt(data[0]), fout)).add(b);
            Draw.color(this.color);
            if (!Vars.renderer.animateShields) {
                Draw.alpha(0.3F);
            }

            for(int i = 1; i < this.scanAccuracy; ++i) {
                float ang = Mathf.lerp(-this.cone, this.cone, (float)i / ((float)this.scanAccuracy - 1.0F)) + b.rotation();
                Tmp.v2.trns(ang, Math.min(Mathf.sqrt(data[i]), fout)).add(b);
                Fill.tri(b.x, b.y, Tmp.v1.x, Tmp.v1.y, Tmp.v2.x, Tmp.v2.y);
                Tmp.v1.set(Tmp.v2);
            }

            Draw.color();
            Draw.z(var7);
        }
    }

    public void drawLight(Bullet b) {
    }
}
