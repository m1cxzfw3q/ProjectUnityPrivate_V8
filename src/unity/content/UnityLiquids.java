package unity.content;

import arc.Events;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.Vars;
import mindustry.game.EventType.Trigger;
import mindustry.type.Liquid;
import unity.graphics.UnityPal;

public class UnityLiquids {
    private static final Color temp = new Color(0.0F, 0.0F, 0.0F, 1.0F);
    private static final Color temp2;
    public static Liquid lava;

    public static void load() {
        lava = new Liquid("lava", UnityPal.lava) {
            {
                this.heatCapacity = 0.0F;
                this.viscosity = 0.7F;
                this.temperature = 1.5F;
                this.effect = UnityStatusEffects.molten;
                this.lightColor = UnityPal.lava2.cpy().mul(1.0F, 1.0F, 1.0F, 0.55F);
            }
        };
        if (!Vars.headless) {
            Events.run(Trigger.update, () -> {
                lava.color = temp.set(UnityPal.lava).lerp(UnityPal.lava2, Mathf.absin(Time.globalTime, 25.0F, 1.0F));
                lava.lightColor = temp2.set(temp).mul(1.0F, 1.0F, 1.0F, 0.55F);
            });
        }

    }

    static {
        temp2 = temp.cpy();
    }
}
