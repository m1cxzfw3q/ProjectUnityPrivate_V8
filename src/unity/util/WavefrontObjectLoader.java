package unity.util;

import arc.assets.AssetDescriptor;
import arc.assets.AssetLoaderParameters;
import arc.assets.AssetManager;
import arc.assets.loaders.AsynchronousAssetLoader;
import arc.assets.loaders.FileHandleResolver;
import arc.files.Fi;
import arc.struct.Seq;
import arc.util.Nullable;

public class WavefrontObjectLoader extends AsynchronousAssetLoader<WavefrontObject, WavefrontObjectParameters> {
    private WavefrontObject object;

    public WavefrontObjectLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    public void loadAsync(AssetManager assetManager, String fileName, Fi file, WavefrontObjectParameters parameter) {
        Fi material = file.parent().child(file.nameWithoutExtension() + ".mtl");
        if (!material.exists()) {
            material = null;
        }

        if (parameter != null && parameter.object != null) {
            (this.object = parameter.object).load(file, material);
        } else {
            this.object = new WavefrontObject();
            this.object.load(file, material);
        }

    }

    public WavefrontObject loadSync(AssetManager assetManager, String fileName, Fi file, WavefrontObjectParameters parameter) {
        WavefrontObject object = this.object;
        this.object = null;
        return object;
    }

    public Seq<AssetDescriptor> getDependencies(String fileName, Fi file, WavefrontObjectParameters parameter) {
        return null;
    }

    public static class WavefrontObjectParameters extends AssetLoaderParameters<WavefrontObject> {
        @Nullable
        public WavefrontObject object;

        public WavefrontObjectParameters(@Nullable WavefrontObject object) {
            this.object = object;
        }
    }
}
