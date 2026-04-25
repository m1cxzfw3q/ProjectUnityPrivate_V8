package unity.gen;

import arc.struct.Seq;
import mindustry.gen.Building;
import mindustry.gen.Buildingc;
import unity.world.LightAcceptor;
import unity.world.LightAcceptorType;

public interface LightHoldc extends Stemc {
    float getRotation(Building var1);

    Seq<LightAcceptorType> acceptors();

    void acceptors(Seq<LightAcceptorType> var1);

    public interface LightHoldBuildc extends Stemc.StemBuildc, Buildingc {
        boolean acceptLight(Light var1, int var2, int var3);

        void add(Light var1, int var2, int var3);

        void remove(Light var1);

        void interact(Light var1);

        float lightStatus();

        boolean requiresLight();

        LightAcceptor[] slots();

        void slots(LightAcceptor[] var1);

        boolean needsReinteract();
    }
}
