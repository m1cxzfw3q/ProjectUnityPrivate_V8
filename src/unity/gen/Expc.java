package unity.gen;

import arc.audio.Sound;
import arc.graphics.Color;
import arc.struct.Seq;
import mindustry.entities.Effect;
import mindustry.gen.Buildingc;
import mindustry.world.Block;
import unity.type.ExpUpgrade;
import unity.world.meta.DynamicProgression;

public interface Expc extends Stemc {
    void setUpgrades();

    float requiredExp(int var1);

    <T extends Block> void addUpgrade(T var1, int var2, boolean var3);

    <T extends Block> void addUpgrade(T var1, int var2, int var3, boolean var4);

    int maxLevel();

    void maxLevel(int var1);

    float maxExp();

    void maxExp(float var1);

    float orbRefund();

    void orbRefund(float var1);

    Color minLevelColor();

    void minLevelColor(Color var1);

    Color maxLevelColor();

    void maxLevelColor(Color var1);

    Color minExpColor();

    void minExpColor(Color var1);

    Color maxExpColor();

    void maxExpColor(Color var1);

    Color upgradeColor();

    void upgradeColor(Color var1);

    DynamicProgression progression();

    void progression(DynamicProgression var1);

    Seq<ExpUpgrade> upgrades();

    void upgrades(Seq<ExpUpgrade> var1);

    ExpUpgrade[][] upgradesPerLevel();

    boolean enableUpgrade();

    void enableUpgrade(boolean var1);

    boolean hasUpgradeEffect();

    void hasUpgradeEffect(boolean var1);

    float sparkleChance();

    void sparkleChance(float var1);

    Effect sparkleEffect();

    void sparkleEffect(Effect var1);

    Effect upgradeEffect();

    void upgradeEffect(Effect var1);

    Sound upgradeSound();

    void upgradeSound(Sound var1);

    boolean hasExp();

    void hasExp(boolean var1);

    boolean hub();

    void hub(boolean var1);

    boolean conveyor();

    void conveyor(boolean var1);

    boolean noOrbCollision();

    void noOrbCollision(boolean var1);

    float orbMultiplier();

    void orbMultiplier(float var1);

    boolean condConfig();

    void condConfig(boolean var1);

    public interface ExpBuildc extends Stemc.StemBuildc, Buildingc {
        float expf();

        int level();

        float levelf();

        void incExp(float var1);

        void upgradeDefault();

        void sparkle();

        void upgrade(int var1);

        ExpUpgrade[] currentUpgrades(int var1);

        float spreadAmount();

        boolean consumesOrb();

        int maxLevel();

        float exp();

        void exp(float var1);

        boolean checked();

        void checked(boolean var1);
    }
}
