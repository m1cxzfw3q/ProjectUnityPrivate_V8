package unity.gen;

import arc.Core;
import arc.util.Log;
import mindustry.Vars;
import unity.assets.loaders.ModelLoader;
import unity.assets.type.g3d.Model;

public final class UnityModels {
    public static Model cube = new Model();
    public static Model megalithring = new Model();
    public static Model prism = new Model();
    public static Model wavefront = new Model();

    private UnityModels() {
        throw new AssertionError();
    }

    protected static Model load(String name) {
        String n = "models/" + name;
        String path = Vars.tree.get(n + ".g3db").exists() ? n + ".g3db" : n + ".g3dj";
        Model model = new Model();

        try {
            ModelLoader loader = (ModelLoader)Core.assets.getLoader(Model.class, path);
            loader.load(Core.assets, path, Vars.tree.get(path), new ModelLoader.ModelParameter(model));
        } catch (Throwable t) {
            Log.err(t);
        }

        return model;
    }

    public static void load() {
        if (!Vars.headless) {
            cube = load("cube");
            megalithring = load("megalithring");
            prism = load("prism");
            wavefront = load("wavefront");
        }
    }
}
