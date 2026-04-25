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

public interface Oppressionc extends Statusc, Entityc, Drawc, Hitboxc, Physicsc, Flyingc, Shieldc, Posc, Commanderc, Teamc, Weaponsc, Itemsc, Wormc, Unitc, Healthc, Rotc, Velc, Boundedc, Builderc, Minerc, Syncc {
    void updateLaserSpeed();
}
