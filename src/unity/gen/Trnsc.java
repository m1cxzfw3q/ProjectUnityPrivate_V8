package unity.gen;

import mindustry.gen.Entityc;
import mindustry.gen.Posc;
import mindustry.gen.Rotc;

public interface Trnsc extends Rotc, Posc, Entityc {
    Posc parent();

    void parent(Posc var1);

    float offsetX();

    void offsetX(float var1);

    float offsetY();

    void offsetY(float var1);

    float offsetRot();

    void offsetRot(float var1);
}
