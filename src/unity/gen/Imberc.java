package unity.gen;

import arc.struct.Seq;
import mindustry.gen.Boundedc;
import mindustry.gen.Builderc;
import mindustry.gen.Building;
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
import mindustry.gen.Unit;
import mindustry.gen.Unitc;
import mindustry.gen.Velc;
import mindustry.gen.Weaponsc;

public interface Imberc extends Statusc, Entityc, Drawc, Hitboxc, Physicsc, Itemsc, Flyingc, Shieldc, Posc, Commanderc, Syncc, Weaponsc, Teamc, Unitc, Healthc, Rotc, Boundedc, Builderc, Minerc, Velc {
    Seq<Unit> closeImberUnits();

    void closeImberUnits(Seq<Unit> var1);

    Seq<Building> closeNodes();

    void closeNodes(Seq<Building> var1);

    float laserRange();

    void laserRange(float var1);

    int maxConnections();

    void maxConnections(int var1);

    int connections();

    void connections(int var1);
}
