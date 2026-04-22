package unity.content;

import mindustry.type.SectorPreset;
import unity.map.ScriptedSector;

public class UnitySectorPresets {
    public static SectorPreset accretion;
    public static SectorPreset salvagedLab;

    public static void load() {
        accretion = new ScriptedSector("accretion", UnityPlanets.megalith, 200) {
            {
                this.alwaysUnlocked = true;
                this.addStartingItems = true;
                this.difficulty = 3.0F;
                this.captureWave = 15;
            }
        };
        salvagedLab = new ScriptedSector("salvaged-laboratory", UnityPlanets.megalith, 100) {
            {
                this.difficulty = 4.0F;
                this.captureWave = 30;
            }
        };
    }
}
