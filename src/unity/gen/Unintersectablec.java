package unity.gen;

import mindustry.gen.Entityc;
import mindustry.gen.Healthc;
import mindustry.gen.Hitboxc;
import mindustry.gen.Posc;

public interface Unintersectablec extends Entityc, Hitboxc, Posc, Healthc {
    boolean intersects();
}
