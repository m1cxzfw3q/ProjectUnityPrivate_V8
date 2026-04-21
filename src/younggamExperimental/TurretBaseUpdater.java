package younggamExperimental;

import arc.scene.ui.layout.Table;
import mindustry.type.Item;
import unity.world.modules.GraphHeatModule;
import younggamExperimental.blocks.ModularTurret;

public class TurretBaseUpdater {
    ModularTurret.ModularTurretBuild build;
    PartInfo basePart;
    float reload;
    float reloadTime;

    public static void attachBaseUpdate() {
    }

    float reloadMultiplier() {
        return 1.0F;
    }

    PartInfo getBasePart() {
        return this.basePart;
    }

    void updateShooting() {
        GraphHeatModule hgraph = this.build.heat();
        float temp = hgraph.getTemp();
        if (!(this.reload >= this.reloadTime)) {
            this.reload += this.build.delta();
        }

    }

    boolean canShoot() {
        return true;
    }

    void onShoot() {
    }

    void applyStats(StatContainer total) {
    }

    void processConfig() {
    }

    boolean acceptItem(Item item) {
        return false;
    }

    void displayAmmoStats(Table table) {
    }

    void draw(float x, float y) {
    }
}
