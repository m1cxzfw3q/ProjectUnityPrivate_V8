package unity.entities.bullet.laser;

import arc.graphics.Color;
import unity.graphics.UnityPal;

public class WavefrontLaser extends AcceleratingLaserBulletType {
    public WavefrontLaser(float damage) {
        super(damage);
        this.lifetime = 90.0F;
        this.collisionWidth = 22.0F;
        this.width = 50.0F;
        this.maxLength = 450.0F;
        this.accel = 40.0F;
        this.laserSpeed = 40.0F;
        this.colors = new Color[]{UnityPal.advanceDark.cpy().mul(0.9F, 1.0F, 1.0F, 0.4F), UnityPal.advanceDark, UnityPal.advance, Color.white};
    }
}
