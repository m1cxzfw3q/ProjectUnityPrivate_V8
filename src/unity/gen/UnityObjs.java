package unity.gen;

import arc.Core;
import arc.assets.AssetDescriptor;
import arc.graphics.Color;
import arc.util.Log;
import mindustry.Vars;
import unity.graphics.UnityPal;
import unity.util.WavefrontObject;
import unity.util.WavefrontObjectLoader;

public final class UnityObjs {
    public static WavefrontObject cube = new WavefrontObject();
    public static WavefrontObject wavefront = new WavefrontObject();

    private UnityObjs() {
        throw new AssertionError();
    }

    protected static WavefrontObject load(String name) {
        String n = "objects/" + name;
        String path = n + ".obj";
        WavefrontObject object = new WavefrontObject();
        AssetDescriptor<WavefrontObject> desc = Core.assets.load(path, WavefrontObject.class, new WavefrontObjectLoader.WavefrontObjectParameters(object));
        desc.errored = (e) -> Log.err(e);
        return object;
    }

    public static void load() {
        if (!Vars.headless) {
            cube = load("cube");
            cube.drawLayer = 50.0F;
            cube.lightColor = UnityPal.advance;
            cube.shadeColor = UnityPal.advanceDark;
            cube.size = 4.0F;
            wavefront = load("wavefront");
            wavefront.shadingSmoothness = 1.0F;
            wavefront.lightColor = Color.white;
            wavefront.drawLayer = 50.0F;
            wavefront.shadeColor = UnityPal.wavefrontDark;
            wavefront.size = 4.0F;
        }
    }
}
