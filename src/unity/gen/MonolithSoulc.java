package unity.gen;

import arc.struct.Seq;
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
import mindustry.world.Tile;

public interface MonolithSoulc extends Statusc, Entityc, Drawc, Hitboxc, CTrailc, Physicsc, Flyingc, Shieldc, Posc, Commanderc, Teamc, Weaponsc, Itemsc, Factionc, Unitc, Healthc, Rotc, Velc, Boundedc, Builderc, Minerc, Syncc {
    void join(Teamc var1);

    void form(Tile var1);

    boolean joinValid(Teamc var1);

    boolean formValid(Tile var1);

    boolean joining();

    boolean forming();

    float lifeDelta();

    boolean corporeal();

    float joinTime();

    Teamc joinTarget();

    float ringRotation();

    Seq<Tile> forms();

    float formProgress();
}
