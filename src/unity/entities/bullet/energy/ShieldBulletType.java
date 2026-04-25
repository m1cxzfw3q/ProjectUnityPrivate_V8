package unity.entities.bullet.energy;

import arc.Core;
import arc.audio.Sound;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.gen.Bullet;
import mindustry.gen.Entityc;
import mindustry.gen.Groups;
import mindustry.world.blocks.defense.turrets.Turret;

public class ShieldBulletType extends BasicBulletType {
    public float shieldHealth = 3000.0F;
    public float maxRadius = 10.0F;
    public Sound breakSound;
    public Effect breakFx = new Effect(5.0F, (e) -> {
        Draw.z(125.0F);
        Draw.color(e.color);
        float radius = (Float)e.data * e.fout();
        if (Core.settings.getBool("animatedshields")) {
            Fill.poly(e.x, e.y, 6, radius);
        } else {
            Lines.stroke(1.5F);
            Draw.alpha(0.09F);
            Fill.poly(e.x, e.y, 6, radius);
            Draw.alpha(1.0F);
            Lines.poly(e.x, e.y, 6, radius);
            Draw.reset();
        }

        Draw.z(30.0F);
    });

    public ShieldBulletType(float speed) {
        super(speed, 0.0F);
        this.drag = 0.3F;
        this.lifetime = 20000.0F;
        this.shootEffect = Fx.none;
        this.despawnEffect = Fx.none;
        this.collides = false;
        this.hitSize = 0.0F;
        this.hittable = false;
        this.hitEffect = Fx.none;
    }

    public void update(Bullet b) {
        if (b.data == null) {
            float[] data = new float[2];
            data[0] = this.shieldHealth;
            data[1] = 0.0F;
            b.data = data;
        }

        float radius = ((this.speed - b.vel.len()) * this.maxRadius + 1.0F) * 0.8F;
        float[] temp = (float[])b.data;
        Groups.bullet.intersect(b.x - radius, b.y - radius, radius * 2.0F, radius * 2.0F, (e) -> {
            if (e != null && e.team != b.team) {
                Entityc build$temp = e.owner;
                if (build$temp instanceof Turret.TurretBuild) {
                    Turret.TurretBuild build = (Turret.TurretBuild)build$temp;
                    if (build.block.name != "unity-shielder") {
                        float health = temp[0] - e.damage;
                        temp[0] = health;
                        temp[1] = 1.0F;
                        e.remove();
                    }
                } else {
                    float health = temp[0] - e.damage;
                    temp[0] = health;
                    temp[1] = 1.0F;
                    e.remove();
                }
            }

        });
        if (temp[0] <= 0.0F) {
            this.breakSound.at(b.x, b.y, Mathf.random(0.8F, 1.0F));
            this.breakFx.at(b.x, b.y, 0.0F, b.team.color, radius);
            b.remove();
        }

        if (temp[0] > 0.0F) {
            float hit = temp[1] - 1.0F - 0.2F * Time.delta;
            temp[1] = hit;
        }

    }

    public void draw(Bullet b) {
        Draw.z(125.0F);
        if (b.data != null) {
            float[] temp = (float[])b.data;
            Draw.color(b.team.color, Color.white, Mathf.clamp(temp[1]));
            float radius = (this.speed - b.vel.len()) * this.maxRadius + 1.0F;
            if (Core.settings.getBool("animatedshields")) {
                Fill.poly(b.x, b.y, 6, radius);
            } else {
                Lines.stroke(1.5F);
                Draw.alpha(0.09F + Mathf.clamp(0.08F * temp[1]));
                Fill.poly(b.x, b.y, 6, radius);
                Draw.alpha(1.0F);
                Lines.poly(b.x, b.y, 6, radius);
                Draw.reset();
            }

            Draw.z(30.0F);
            Draw.color();
        }
    }
}
