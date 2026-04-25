package unity.gen;

import arc.struct.FloatSeq;
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

public interface KamiBulletc extends Entityc, Drawc, Timedc, Ownerc, Posc, Hitboxc, Damagec, Bulletc, Teamc, Timerc, Shielderc, Velc {
    boolean isTelegraph();

    float turn();

    void turn(float var1);

    float width();

    void width(float var1);

    float length();

    void length(float var1);

    float resetTime();

    void resetTime(float var1);

    float lastTime();

    void lastTime(float var1);

    float fdata2();

    void fdata2(float var1);

    int telegraph();

    void telegraph(int var1);

    FloatSeq lastPositions();

    void lastPositions(FloatSeq var1);

    KamiBulletDatas.KamiBulletData bdata();

    void bdata(KamiBulletDatas.KamiBulletData var1);
}
