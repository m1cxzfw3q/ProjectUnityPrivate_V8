package unity.mod;

import arc.Core;
import arc.graphics.Color;
import mindustry.Vars;
import mindustry.graphics.Pal;
import unity.graphics.UnityPal;

public enum Faction {
    scar("scar", Pal.remove),
    dark("dark", Color.valueOf("fc6203")),
    advance("advance", Color.sky),
    imber("imber", Pal.surge),
    plague("plague", Color.valueOf("a3f080")),
    koruh("koruh", Color.valueOf("96f7c3")),
    light("light", Color.valueOf("fffde8")),
    monolith("monolith", UnityPal.monolith),
    youngcha("youngcha", Color.valueOf("a69f95")),
    end("end", Color.gray);

    public static final Faction[] all = values();
    public final String name;
    public String localizedName;
    public final Color color;

    public static void init() {
        if (!Vars.headless) {
            for(Faction faction : all) {
                faction.localizedName = Core.bundle.format("faction." + faction.name, faction.color);
            }

        }
    }

    Faction(String name, Color color) {
        this.name = name;
        this.color = color.cpy();
    }

    public String toString() {
        return this.localizedName;
    }
}
