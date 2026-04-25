package unity.gen;

import arc.func.Cons;
import mindustry.gen.Buildingc;
import unity.world.meta.StemData;

public interface Stemc {
    <T extends StemBuildc> void draw(Cons<T> var1);

    <T extends StemBuildc> void update(Cons<T> var1);

    Cons<StemBuildc> drawStem();

    Cons<StemBuildc> updateStem();

    public interface StemBuildc extends Buildingc {
        StemData data();
    }
}
