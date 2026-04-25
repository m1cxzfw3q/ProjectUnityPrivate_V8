package unity.type;

import arc.Core;
import arc.audio.Sound;
import arc.graphics.g2d.TextureRegion;
import arc.struct.Seq;
import mindustry.entities.bullet.BulletType;

public class TentacleType implements Cloneable {
    public String name;
    public float x;
    public float y;
    public float rotationOffset;
    public float segmentLength;
    public float speed;
    public float accel;
    public float rotationSpeed;
    public float drag = 0.06F;
    public float angleLimit = 65.0F;
    public float firstSegmentAngleLimit = 35.0F;
    public int segments = 10;
    public boolean automatic = true;
    public boolean mirror = true;
    public boolean top = false;
    public boolean flipSprite;
    public float swayScl = 110.0F;
    public float swayMag = 0.6F;
    public float swaySegmentOffset = 1.5F;
    public float swayOffset = 0.0F;
    public BulletType bullet;
    public Sound shootSound;
    public float tentacleDamage = -1.0F;
    public float startVelocity = 2.0F;
    public float bulletDuration = -1.0F;
    public boolean continuous = false;
    public float reload = 60.0F;
    public float range = 220.0F;
    public float shootCone = 15.0F;
    public TextureRegion region;
    public TextureRegion tipRegion;

    public TentacleType(String name) {
        this.name = name;
    }

    public float range() {
        return this.segmentLength * (float)this.segments - 5.0F + (this.bullet != null ? this.bullet.range() * 0.75F : 0.0F);
    }

    public static void set(Seq<TentacleType> seq) {
        Seq<TentacleType> mapped = new Seq();

        for(TentacleType t : seq) {
            mapped.add(t);
            if (t.mirror) {
                TentacleType copy = t.copy();
                copy.rotationOffset *= -1.0F;
                copy.x *= -1.0F;
                copy.flipSprite = !copy.flipSprite;
                mapped.add(copy);
            }
        }

        seq.set(mapped);
    }

    public void load() {
        this.region = Core.atlas.find(this.name);
        this.tipRegion = Core.atlas.find(this.name + "-tip", Core.atlas.find(this.name));
    }

    public TentacleType copy() {
        try {
            return (TentacleType)this.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
