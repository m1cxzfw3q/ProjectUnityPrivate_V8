package unity.entities.bullet.physical;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Mathf;
import arc.util.Tmp;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.gen.Bullet;
import unity.content.effects.TrailFx;
import unity.util.Utils;

public class SlowRailBulletType extends BasicBulletType {
    public float trailSpacing = 5.0F;
    public float collisionWidth = 3.0F;
    public float pierceDamageFactor = 0.0F;
    private static boolean hit = false;

    public SlowRailBulletType(float speed, float damage) {
        super(speed, damage);
        this.collides = this.collidesTiles = this.backMove = this.reflectable = false;
        this.pierce = this.pierceBuilding = true;
        this.trailEffect = TrailFx.coloredRailgunTrail;
    }

    public void init() {
        super.init();
        this.drawSize = Math.max(this.drawSize, (Math.max(this.height, this.width) + this.speed * this.lifetime * 0.75F) * 2.0F);
        this.trailColor = this.backColor;
        this.trailLength = Math.max((int)this.lifetime, 2);
    }

    public void init(Bullet b) {
        super.init(b);
        RailData data = new RailData();
        data.x = b.x;
        data.y = b.y;
        b.data = data;
    }

    public void update(Bullet b) {
        this.updateTrail(b);
        hit = false;
        Utils.collideLineRawEnemy(b.team, b.lastX, b.lastY, b.x, b.y, this.collisionWidth, this.collisionWidth, (building, direct) -> {
            if (direct && this.collidesGround && !b.collided.contains(building.id) && b.damage > 0.0F) {
                float h = building.health;
                float sub = Math.max(building.health * this.pierceDamageFactor, 0.0F);
                building.collision(b);
                this.hitTile(b, building, h, true);
                b.collided.add(building.id);
                b.damage -= sub;
            }

            return hit = building.block.absorbLasers || this.pierceCap > 0 && b.collided.size >= this.pierceCap || b.damage <= 0.0F;
        }, (unit) -> {
            if (unit.checkTarget(this.collidesAir, this.collidesGround) && !b.collided.contains(unit.id) && b.damage > 0.0F) {
                float sub = Math.max(unit.health * this.pierceDamageFactor, 0.0F);
                this.hitEntity(b, unit, unit.health);
                b.collided.add(unit.id);
                b.damage -= sub;
            }

            return hit = this.pierceCap > 0 && b.collided.size >= this.pierceCap || b.damage <= 0.0F;
        }, (x, y) -> {
            if (hit) {
                Tmp.v1.trns(b.rotation(), Mathf.dst(b.lastX, b.lastY, x, y)).add(b.lastX, b.lastY);
                b.set(Tmp.v1);
                b.vel.setZero();
            }

            this.hit(b, x, y);
        }, true);
        if (b.data instanceof RailData) {
            RailData data = (RailData)b.data;

            for(data.lastLen += Mathf.dst(b.lastX, b.lastY, b.x, b.y); data.len < data.lastLen; data.len += this.trailSpacing) {
                Tmp.v1.trns(b.rotation(), data.len).add(data.x, data.y);
                this.trailEffect.at(Tmp.v1.x, Tmp.v1.y, b.rotation(), this.trailColor);
            }
        }

    }

    public void draw(Bullet b) {
        this.drawTrail(b);
        float height = this.height * (1.0F - this.shrinkY + this.shrinkY * b.fout());
        float width = this.width * (1.0F - this.shrinkX + this.shrinkX * b.fout()) / 1.5F;
        Tmp.v1.trns(b.rotation(), height / 2.0F);
        Draw.color(this.backColor);

        for(int s : Mathf.signs) {
            Tmp.v2.trns(b.rotation() - 90.0F, width * (float)s, -height);
            Draw.color(this.backColor);
            Fill.tri(Tmp.v1.x + b.x, Tmp.v1.y + b.y, -Tmp.v1.x + b.x, -Tmp.v1.y + b.y, Tmp.v2.x + b.x, Tmp.v2.y + b.y);
            Draw.color(this.frontColor);
            Fill.tri(Tmp.v1.x / 2.0F + b.x, Tmp.v1.y / 2.0F + b.y, -Tmp.v1.x / 2.0F + b.x, -Tmp.v1.y / 2.0F + b.y, Tmp.v2.x / 2.0F + b.x, Tmp.v2.y / 2.0F + b.y);
        }

    }

    static class RailData {
        float x;
        float y;
        float len;
        float lastLen;
    }
}
