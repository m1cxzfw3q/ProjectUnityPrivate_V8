package unity.entities.bullet.anticheat;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.entities.Units;
import mindustry.gen.Bullet;
import mindustry.graphics.Drawf;
import unity.content.effects.SpecialFx;
import unity.util.Utils;

public class EndPointBlastLaserBulletType extends AntiCheatBulletTypeBase implements SpecialFx.PointBlastInterface {
    public float damageRadius = 20.0F;
    public float auraDamage = 10.0F;
    public float length = 100.0F;
    public float width = 12.0F;
    public float widthReduction = 2.0F;
    public float auraWidthReduction = 3.0F;
    public Color[] laserColors;
    private static boolean available = false;

    public EndPointBlastLaserBulletType(float damage) {
        this.laserColors = new Color[]{Color.white};
        this.speed = 0.0F;
        this.damage = damage;
        this.hitEffect = Fx.hitLancer;
        this.despawnEffect = Fx.none;
        this.shootEffect = Fx.none;
        this.smokeEffect = Fx.none;
        this.hitSize = 4.0F;
        this.lifetime = 16.0F;
        this.keepVelocity = false;
        this.collides = false;
        this.pierce = true;
        this.hittable = false;
        this.absorbable = false;
    }

    public Color[] colors() {
        return this.laserColors;
    }

    public float widthReduction() {
        return this.auraWidthReduction;
    }

    public float estimateDPS() {
        return super.estimateDPS() * 3.0F + this.auraDamage;
    }

    public float range() {
        return this.length;
    }

    public void init() {
        super.init();
        this.drawSize = Math.max(this.drawSize, this.length + this.damageRadius * 2.0F);
    }

    public void init(Bullet b) {
        super.init(b);
        b.fdata = this.length;
        Tmp.v1.trns(b.rotation(), this.length).add(b);
        available = false;
        Utils.collideLineRawEnemy(b.team, b.x, b.y, Tmp.v1.x, Tmp.v1.y, this.width / 3.0F, (bd, direct) -> {
            if (direct) {
                available = true;
                b.fdata = b.dst(bd);
                Tmp.v2.trns(b.rotation(), b.fdata).add(b.x, b.y);
                this.hitBuildingAntiCheat(b, bd);
            }

            return bd.block.absorbLasers;
        }, (u) -> {
            available = true;
            Tmp.v2.trns(b.rotation(), b.dst(u)).add(b.x, b.y);
            this.hitUnitAntiCheat(b, u);
            return false;
        }, (entity) -> b.dst(entity) / 2.0F - entity.health(), (ex, ey) -> this.hitEffect.at(ex, ey, b.rotation()), true);
        if (available) {
            b.fdata = b.dst(Tmp.v2);
            Utils.trueEachBlock(Tmp.v2.x, Tmp.v2.y, this.damageRadius, (building) -> {
                if (building.team != b.team) {
                    this.hitBuildingAntiCheat(b, building, this.auraDamage * (b.damage / this.damage));
                }

            });
            Units.nearby(Tmp.v2.x - this.damageRadius, Tmp.v2.y - this.damageRadius, this.damageRadius * 2.0F, this.damageRadius * 2.0F, (unit) -> {
                if (unit.team != b.team && unit.within(Tmp.v2.x, Tmp.v2.y, this.damageRadius)) {
                    this.hitUnitAntiCheat(b, unit, this.auraDamage * (b.damage / this.damage));
                }

            });
            SpecialFx.pointBlastLaserEffect.at(Tmp.v2.x, Tmp.v2.y, this.damageRadius, this);
        }

    }

    public void draw(Bullet b) {
        float realLength = b.fdata;
        float f = Mathf.curve(b.fin(), 0.0F, 0.2F);
        float baseLen = realLength * f;

        for(int i = 0; i < this.laserColors.length; ++i) {
            float wReduced = (float)i * this.widthReduction;
            Draw.color(this.laserColors[i]);
            Fill.circle(b.x, b.y, (this.width - wReduced) / 2.0F * b.fout());
            Lines.stroke((this.width - wReduced) * b.fout());
            Lines.lineAngle(b.x, b.y, b.rotation(), baseLen, false);
            Tmp.v1.trns(b.rotation(), baseLen).add(b);
            Drawf.tri(Tmp.v1.x, Tmp.v1.y, Lines.getStroke() * 1.22F, this.width * 2.0F, b.rotation());
            Draw.reset();
            Tmp.v1.trns(b.rotation(), baseLen + this.width / 1.5F).add(b);
        }

        Drawf.light(b.team, b.x, b.y, Tmp.v1.x, Tmp.v1.y, this.width * 1.4F * b.fout(), this.laserColors[0], 0.5F);
    }

    public void drawLight(Bullet b) {
    }
}
