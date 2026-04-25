package unity.entities.bullet.energy;

import mindustry.entities.bullet.BasicBulletType;
import mindustry.gen.Bullet;
import unity.content.UnityFx;
import unity.entities.Emp;

public class EmpBasicBulletType extends BasicBulletType {
    public float empRange = 100.0F;
    public float empMaxRange = 470.0F;
    public float empDuration = 120.0F;
    public float empDisconnectRange = 0.0F;
    public float empLogicDamage = 0.0F;
    public float empBatteryDamage = 7000.0F;
    public int powerGridIteration = 7;

    public EmpBasicBulletType(float speed, float damage) {
        super(speed, damage, "unity-electric-shell");
        this.trailLength = 7;
    }

    public void hit(Bullet b, float x, float y) {
        super.hit(b, x, y);
        Emp.hitTile(x, y, b.team, this.empRange, this.empDuration, this.empBatteryDamage, this.empLogicDamage, 10, this.empDisconnectRange, this.empMaxRange, this.powerGridIteration);
        UnityFx.empShockwave.at(b.x, b.y, this.empRange);
        if (Emp.hitDisconnect) {
            UnityFx.empShockwave.at(b.x, b.y, this.empDisconnectRange);
        }

        if (Emp.hitPowerGrid) {
            UnityFx.empShockwave.at(b.x, b.y, this.empMaxRange);
        }

    }
}
