package unity.entities.bullet.energy;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.content.StatusEffects;
import mindustry.entities.Units;
import mindustry.entities.bullet.EmpBulletType;
import mindustry.gen.Bullet;
import mindustry.graphics.Drawf;
import mindustry.type.StatusEffect;
import unity.graphics.UnityDrawf;

public class CygnusBulletType extends EmpBulletType {
    public float size = 8.0F;
    public float allyStatusDuration = 120.0F;
    public StatusEffect allyStatus;

    public CygnusBulletType() {
        this.allyStatus = StatusEffects.overclock;
    }

    public void hit(Bullet b, float x, float y) {
        super.hit(b, x, y);
        if (this.hitUnits) {
            Units.nearby(b.team, x, y, this.radius, (other) -> {
                if (other.team == b.team && other != b.owner) {
                    other.heal(this.healPercent / 100.0F * other.maxHealth);
                    other.apply(this.allyStatus, this.allyStatusDuration);
                }

            });
        }

    }

    public void drawLight(Bullet b) {
        Drawf.light(b.team, b.x, b.y, this.size * 3.0F, this.backColor, 0.3F);
    }

    public void draw(Bullet b) {
        this.drawTrail(b);
        Draw.color(this.backColor);

        for(int i = 0; i < 2; ++i) {
            float r = b.rotation() + 180.0F * (float)i;
            Drawf.tri(b.x + Angles.trnsx(r, this.size - 2.0F), b.y + Angles.trnsy(r, this.size - 2.0F), this.size, this.size * 1.5F + Mathf.sin(Time.time, 15.0F, this.size / 2.0F), r);
        }

        UnityDrawf.shiningCircle(b.id, Time.time, b.x, b.y, this.size, 7, 30.0F, 17.0F, 12.0F, 180.0F);
        Draw.color(Color.white);
        UnityDrawf.shiningCircle(b.id, Time.time, b.x, b.y, this.size * 0.65F, 7, 30.0F, 23.0F, 11.0F, 180.0F);
    }
}
