package unity.assets.type.g3d;

import arc.func.Prov;

public interface RenderableProvider {
    void getRenderables(Prov<Renderable> var1);

    default void render() {
        Models.render(this);
    }
}
