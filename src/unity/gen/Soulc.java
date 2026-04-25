package unity.gen;

import mindustry.gen.BlockUnitc;
import mindustry.gen.Buildingc;
import mindustry.world.blocks.ControlBlock;
import unity.entities.Soul;
import unity.world.meta.DynamicProgression;

public interface Soulc extends Stemc {
    int maxSouls();

    void maxSouls(int var1);

    float efficiencyFrom();

    void efficiencyFrom(float var1);

    float efficiencyTo();

    void efficiencyTo(float var1);

    boolean requireSoul();

    void requireSoul(boolean var1);

    DynamicProgression progression();

    void progression(DynamicProgression var1);

    public interface SoulBuildc extends ControlBlock, Soul, Stemc.StemBuildc, Buildingc {
        boolean disabled();

        void unit(BlockUnitc var1);
    }
}
