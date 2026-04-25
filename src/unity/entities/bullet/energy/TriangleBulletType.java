package unity.entities.bullet.energy;

import arc.audio.Sound;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import mindustry.entities.Lightning;
import mindustry.entities.Units;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Bullet;
import mindustry.gen.Sounds;
import mindustry.gen.Teamc;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;

public class TriangleBulletType extends BulletType {
    public float lifetimeRand;
    public boolean castsLightning;
    public int castInterval;
    public float castRadius;
    public Sound castSound;
    public float castSoundVolume;
    public float length;
    public float width;
    public Color color;

    public TriangleBulletType(float length, float width, float speed, float damage) {
        super(speed, damage);
        this.lifetimeRand = 0.0F;
        this.castsLightning = false;
        this.castInterval = 12;
        this.castRadius = 8.0F;
        this.castSound = Sounds.spark;
        this.castSoundVolume = 0.4F;
        this.color = Pal.surge;
        this.length = length;
        this.width = width;
        this.trailColor = this.lightningColor = Pal.surge;
        this.hitColor = Color.valueOf("f2e87b");
    }

    public TriangleBulletType(float speed, float damage) {
        this(1.0F, 1.0F, speed, damage);
    }

    public TriangleBulletType() {
        this(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public void init(Bullet b) {
        super.init(b);
        b.lifetime += Mathf.random(this.lifetimeRand);
    }

    public void draw(Bullet b) {
        this.drawTrail(b);
        Draw.color(this.lightningColor);
        Drawf.tri(b.x, b.y, this.width, this.length, b.rotation());
    }

    public void update(Bullet b) {
        super.update(b);
        Teamc target = Units.closestTarget(b.team, b.x, b.y, this.castRadius * 8.0F);
        if (this.castsLightning && target != null && b.timer.get(1, (float)this.castInterval)) {
            this.castSound.at(b.x, b.y, 1.0F, this.castSoundVolume);
            Lightning.create(b.team, this.lightningColor, this.damage, b.x, b.y, b.angleTo(target), (int)(b.dst(target) / 8.0F + 2.0F));
        }

    }
}
