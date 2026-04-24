package unity.gen;

import mindustry.gen.Boundedc;
import mindustry.gen.Builderc;
import mindustry.gen.Commanderc;
import mindustry.gen.Drawc;
import mindustry.gen.Entityc;
import mindustry.gen.Flyingc;
import mindustry.gen.Healthc;
import mindustry.gen.Hitboxc;
import mindustry.gen.Itemsc;
import mindustry.gen.Minerc;
import mindustry.gen.Physicsc;
import mindustry.gen.Posc;
import mindustry.gen.Rotc;
import mindustry.gen.Shieldc;
import mindustry.gen.Statusc;
import mindustry.gen.Syncc;
import mindustry.gen.Teamc;
import mindustry.gen.Unitc;
import mindustry.gen.Velc;
import mindustry.gen.Weaponsc;
import unity.type.CubeUnitType;

public interface Cubec extends Statusc, Entityc, Drawc, Hitboxc, Physicsc, Itemsc, Flyingc, Shieldc, Posc, Commanderc, Syncc, Weaponsc, Teamc, Unitc, Healthc, Rotc, Boundedc, Builderc, Minerc, Velc {
    boolean isMain();

    void updateConstructing();

    CubeUnitType.CubeEntityData data();

    void data(CubeUnitType.CubeEntityData var1);

    boolean healing();

    void healing(boolean var1);

    int tier();

    void tier(int var1);

    int gx();

    void gx(int var1);

    int gy();

    void gy(int var1);

    float constructTime();

    void constructTime(float var1);

    boolean edge();

    void edge(boolean var1);
}
