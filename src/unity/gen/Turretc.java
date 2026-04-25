package unity.gen;

import arc.func.Cons2;
import arc.func.Func;
import arc.graphics.Color;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Buildingc;
import mindustry.gen.Bullet;

public interface Turretc extends Stemc {
    <T extends TurretBuildc> void bulletData(Func<T, Object> var1);

    <T extends BulletType> void bulletCons(Cons2<T, Bullet> var1);

    Color fromColor();

    void fromColor(Color var1);

    Color toColor();

    void toColor(Color var1);

    boolean lerpColor();

    void lerpColor(boolean var1);

    Color rangeColor();

    void rangeColor(Color var1);

    boolean omni();

    void omni(boolean var1);

    BulletType defaultBullet();

    void defaultBullet(BulletType var1);

    float basicFieldRadius();

    void basicFieldRadius(float var1);

    Func<TurretBuildc, Object> bulletData();

    Cons2<BulletType, Bullet> bulletCons();

    public interface TurretBuildc extends Stemc.StemBuildc, Buildingc {
        Object bulletData();

        void bulletCons(BulletType var1, Bullet var2);

        Color getShootColor(float var1);
    }
}
