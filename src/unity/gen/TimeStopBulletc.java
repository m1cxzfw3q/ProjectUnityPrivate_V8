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

public interface TimeStopBulletc extends Entityc, Drawc, Timedc, Ownerc, Posc, Hitboxc, Damagec, Bulletc, Teamc, Timerc, Shielderc, Velc {
    boolean updateVel();
}
