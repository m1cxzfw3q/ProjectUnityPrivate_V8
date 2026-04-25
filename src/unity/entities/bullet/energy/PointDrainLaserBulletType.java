package unity.entities.bullet.energy;

import arc.func.Floatf;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Damage;
import mindustry.entities.bullet.BulletType;
import mindustry.game.Team;
import mindustry.gen.Bullet;
import mindustry.gen.Entityc;
import mindustry.gen.Healthc;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Trail;
import unity.graphics.UnityPal;
import unity.util.Utils;

public class PointDrainLaserBulletType extends BulletType {
    public float drainPercent = 0.1F;
    public float maxLength = 140.0F;
    public float width = 6.0F;
    public float area = 9.0F;
    public float fadeTime = 16.0F;
    public Color backColor;
    public Color frontColor;

    public PointDrainLaserBulletType(float damage) {
        super(0.001F, damage);
        this.backColor = UnityPal.plagueDark;
        this.frontColor = UnityPal.plague;
        this.despawnEffect = Fx.none;
        this.hitSize = 4.0F;
        this.drawSize = 420.0F;
        this.keepVelocity = false;
        this.collides = false;
        this.pierce = true;
        this.hittable = false;
        this.absorbable = false;
    }

    public float range() {
        return this.maxRange;
    }

    public void update(Bullet b) {
        Object var4 = b.data;
        if (var4 instanceof DrainLaserData) {
            DrainLaserData dld = (DrainLaserData)var4;
            Entityc var6 = b.owner;
            if (var6 instanceof Healthc) {
                Healthc hOwner = (Healthc)var6;
                Entityc var5 = b.owner;
                if (var5 instanceof Unit) {
                    Unit e = (Unit)var5;
                    dld.pos.trns(b.rotation(), e.dst(e.aimX, e.aimY)).limit(this.maxLength);
                }

                float length = Utils.findLaserLength(b.x, b.y, dld.pos.x + b.x, dld.pos.y + b.y, (tile) -> tile.team() != b.team && tile.block().absorbLasers);
                dld.pos.setLength(length).add(b);
                dld.trail.update(dld.pos.x, dld.pos.y);
                if (b.timer(1, 5.0F)) {
                    Utils.collideLineRawEnemy(b.team, b.x, b.y, dld.pos.x, dld.pos.y, (building, direct) -> {
                        if (direct) {
                            building.damage(this.damage * this.buildingDamageMultiplier);
                        }

                        return false;
                    }, (unit) -> {
                        unit.damage(this.damage);
                        if (this.knockback != 0.0F) {
                            Tmp.v1.trns(b.rotation(), this.knockback * 80.0F);
                            unit.impulse(Tmp.v1);
                        }

                    }, (Floatf)null, (ex, ey) -> this.hit(b, ex, ey));
                    Damage.damageUnits(b.team, dld.pos.x, dld.pos.y, this.area, this.damage, (unit) -> unit.within(dld.pos, this.area), (unit) -> hOwner.heal(this.damage * this.drainPercent));
                    Vars.indexer.eachBlock((Team)null, dld.pos.x, dld.pos.y, this.area, (build) -> build.team != b.team, (build) -> {
                        build.damage(this.damage * this.buildingDamageMultiplier);
                        hOwner.heal(this.damage * this.drainPercent);
                    });
                }

                return;
            }
        }

    }

    public void draw(Bullet b) {
        Object var3 = b.data;
        if (var3 instanceof DrainLaserData) {
            DrainLaserData dld = (DrainLaserData)var3;
            float fade = Mathf.clamp(b.time > b.lifetime - this.fadeTime ? 1.0F - (b.time - (b.lifetime - this.fadeTime)) / this.fadeTime : 1.0F) * Mathf.clamp(b.time / this.fadeTime);
            Draw.color(this.backColor);
            dld.trail.draw(this.backColor, fade * this.area / 2.0F);

            for(int i = 0; i < 2; ++i) {
                float size = Math.max(this.width * fade - (float)i * this.width / 2.0F, 0.0F);
                Draw.color(i == 0 ? this.backColor : this.frontColor);
                Fill.circle(b.x, b.y, size / 2.0F);
                Lines.stroke(size);
                Lines.line(b.x, b.y, dld.pos.x, dld.pos.y, false);
                Fill.circle(dld.pos.x, dld.pos.y, Math.max(this.area * fade - (float)i * this.area / 2.0F, 0.0F));
            }

            Drawf.light(b.team, b.x, b.y, dld.pos.x, dld.pos.y, fade * this.width * 2.0F, this.backColor, 0.5F);
            Draw.reset();
        }
    }

    public void drawLight(Bullet b) {
    }

    public void init(Bullet b) {
        super.init(b);
        b.data = new DrainLaserData();
        ((DrainLaserData)b.data).pos.set(b);
    }

    public void init() {
        super.init();
        this.drawSize = this.maxLength * 2.0F;
    }

    private static class DrainLaserData {
        Trail trail;
        Vec2 pos;

        private DrainLaserData() {
            this.trail = new Trail(6);
            this.pos = new Vec2();
        }
    }
}
