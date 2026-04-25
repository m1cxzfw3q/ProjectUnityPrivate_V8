package unity.gen;

import mindustry.gen.Bulletc;
import mindustry.gen.Damagec;
import mindustry.gen.Drawc;
import mindustry.gen.Entityc;
import mindustry.gen.Hitboxc;
import mindustry.gen.Ownerc;
import mindustry.gen.Posc;
import mindustry.gen.Shielderc;
import mindustry.gen.Teamc;
import mindustry.gen.Timedc;
import mindustry.gen.Timerc;
import mindustry.gen.Velc;
import unity.ai.kami.KamiBulletDatas;

public interface KamiLaserc extends Entityc, Drawc, Timedc, Ownerc, Posc, Hitboxc, Damagec, Bulletc, Teamc, Timerc, Shielderc, Velc {
    void updateCollision();

    float x2();

    void x2(float var1);

    float y2();

    void y2(float var1);

    float width();

    void width(float var1);

    float lastTime();

    void lastTime(float var1);

    float collidedTime();

    void collidedTime(float var1);

    boolean intervalCollision();

    void intervalCollision(boolean var1);

    boolean ellipseCollision();

    void ellipseCollision(boolean var1);

    KamiBulletDatas.KamiLaserData bdata();

    void bdata(KamiBulletDatas.KamiLaserData var1);
}
