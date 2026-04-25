package unity.gen;

import arc.struct.FloatSeq;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.core.World;
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
import mindustry.gen.Unitc;
import mindustry.gen.Velc;
import mindustry.gen.Weaponsc;
import mindustry.world.Tile;
import mindustry.world.blocks.defense.turrets.Turret;

public interface Worldc extends Statusc, Entityc, Drawc, Hitboxc, Physicsc, Itemsc, Flyingc, Shieldc, Posc, Commanderc, Syncc, Weaponsc, Teamc, Unitc, Healthc, Rotc, Boundedc, Builderc, Minerc, Velc {
    void setup();

    float cwX(float var1);

    float cwY(float var1);

    int conX(int var1);

    int conY(int var1);

    boolean validPlace(Tile var1);

    boolean valid(int var1, int var2);

    Seq<Time.DelayRun> runs();

    void runs(Seq<Time.DelayRun> var1);

    World unitWorld();

    void unitWorld(World var1);

    Seq<Building> buildings();

    void buildings(Seq<Building> var1);

    Seq<Turret.TurretBuild> turrets();

    void turrets(Seq<Turret.TurretBuild> var1);

    FloatSeq positions();

    void positions(FloatSeq var1);
}
