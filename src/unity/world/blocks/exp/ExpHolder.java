package unity.world.blocks.exp;

import mindustry.gen.Building;

public interface ExpHolder {
    int getExp();

    int handleExp(int var1);

    default int unloadExp(int amount) {
        return 0;
    }

    default boolean handleOrb(int orbExp) {
        return this.handleExp(orbExp) > 0;
    }

    default boolean acceptOrb() {
        return false;
    }

    default int handleTower(int amount, float angle) {
        return this.handleExp(amount);
    }

    default boolean hubbable() {
        return false;
    }

    default boolean canHub(Building build) {
        return false;
    }

    default void setHub(ExpHub.ExpHubBuild build) {
    }
}
