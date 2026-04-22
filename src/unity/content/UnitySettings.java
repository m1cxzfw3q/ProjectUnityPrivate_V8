package unity.content;

import arc.Core;
import mindustry.Vars;

public class UnitySettings {
    public static void addGraphicSetting(String key, boolean def) {
        Vars.ui.settings.graphics.checkPref(key, Core.settings.getBool(key, def));
    }

    public static void init() {
        boolean tmp = Core.settings.getBool("uiscalechanged", false);
        Core.settings.put("uiscalechanged", false);
        addGraphicSetting("hitexpeffect", true);
        Core.settings.put("uiscalechanged", tmp);
    }
}
