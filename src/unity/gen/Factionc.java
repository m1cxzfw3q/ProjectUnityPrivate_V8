package unity.gen;

import mindustry.gen.Entityc;
import unity.mod.Faction;

public interface Factionc extends Entityc {
    Faction faction();

    boolean isSameFaction(Entityc var1);
}
