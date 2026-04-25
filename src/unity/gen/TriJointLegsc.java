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
import unity.entities.TriJointLeg;

public interface TriJointLegsc extends Statusc, Entityc, Drawc, Hitboxc, Physicsc, Itemsc, Flyingc, Shieldc, Posc, Commanderc, Syncc, Weaponsc, Teamc, Unitc, Healthc, Rotc, Boundedc, Builderc, Minerc, Velc {
    float legAngle(float var1, int var2);

    TriJointLeg[] legs();

    void legs(TriJointLeg[] var1);

    float baseRotation();

    void baseRotation(float var1);

    float moveSpace();

    void moveSpace(float var1);

    float totalLength();

    void totalLength(float var1);
}
