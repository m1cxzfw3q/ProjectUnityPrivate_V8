package unity.entities.bullet.anticheat;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Units;
import mindustry.gen.Bullet;
import unity.graphics.UnityBlending;
import unity.graphics.UnityPal;

public class VoidAreaBulletType extends AntiCheatBulletTypeBase {
    public float fadeInTime = 15.0F;
    public float fadeOutTime = 15.0F;
    public float radius = 150.0F;

    public VoidAreaBulletType(float damage) {
        super(0.0F, damage);
        this.collides = false;
        this.collidesTiles = false;
        this.despawnEffect = this.hitEffect = Fx.none;
        this.layer = 116.0F;
        this.keepVelocity = false;
    }

    public void init() {
        super.init();
        this.drawSize = this.radius * 2.0F;
    }

    public void update(Bullet b) {
        if (b.timer(1, 5.0F)) {
            float fin = Mathf.clamp(b.time / this.fadeInTime) * Mathf.clamp(b.time > b.lifetime - this.fadeOutTime ? 1.0F - (b.time - (this.lifetime - this.fadeOutTime)) / this.fadeOutTime : 1.0F);
            Units.nearbyEnemies(b.team, b.x, b.y, this.radius * fin, (u) -> this.hitUnitAntiCheat(b, u));
            Vars.indexer.allBuildings(b.x, b.y, this.radius * fin, (building) -> {
                if (building.team != b.team) {
                    this.hitBuildingAntiCheat(b, building);
                }

            });
        }

    }

    public void drawLight(Bullet b) {
    }

    public void draw(Bullet b) {
        float fin = Mathf.clamp(b.time / this.fadeInTime) * Mathf.clamp(b.time > b.lifetime - this.fadeOutTime ? 1.0F - (b.time - (this.lifetime - this.fadeOutTime)) / this.fadeOutTime : 1.0F);
        float osc = Mathf.absin(b.time, 8.0F, 1.0F);
        Tmp.c1.set(UnityPal.scarColor).lerp(UnityPal.endColor, osc);
        Draw.color(Tmp.c1);
        Draw.blend(UnityBlending.shadowRealm);
        Fill.circle(b.x, b.y, fin * this.radius);
        Draw.blend();
        Lines.stroke(4.0F - osc * 1.5F);
        Lines.circle(b.x, b.y, fin * this.radius);
        Draw.reset();
    }
}
