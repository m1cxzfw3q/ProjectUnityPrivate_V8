package unity.sync;

import mindustry.Vars;
import mindustry.entities.Effect;
import mindustry.gen.Entityc;
import mindustry.gen.Player;
import unity.Unity;
import unity.gen.MonolithSoul;

public class UnityCall {
    public static void init() {
    }

    public static void tap(Player player, float x, float y) {
        if (Vars.net.server() || !Vars.net.active()) {
            Unity.tap.tap(player, x, y);
        }

    }

    public static void effect(Effect effect, float x, float y, float rotation, Object data) {
        if (Vars.net.server() || !Vars.net.active()) {
            effect.at(x, y, rotation, data);
        }

    }

    public static void soulJoin(MonolithSoul soul, Entityc ent) {
        if (!Vars.net.server() && !Vars.net.active()) {
        }
    }
}
