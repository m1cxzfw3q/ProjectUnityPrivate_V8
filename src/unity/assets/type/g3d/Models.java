package unity.assets.type.g3d;

import arc.Core;
import arc.func.Prov;
import arc.graphics.Color;
import arc.graphics.Gl;
import arc.graphics.Texture;
import arc.graphics.g2d.Draw;
import arc.graphics.g3d.Camera3D;
import arc.graphics.gl.FrameBuffer;
import arc.struct.Seq;
import mindustry.game.EventType.Trigger;
import mindustry.graphics.Shaders;
import unity.assets.list.UnityShaders;
import unity.assets.type.g3d.attribute.Environment;
import unity.assets.type.g3d.attribute.light.DirectionalLight;
import unity.assets.type.g3d.attribute.type.ColorAttribute;
import unity.assets.type.g3d.attribute.type.TextureAttribute;
import unity.mod.Triggers;

public final class Models {
    public static final Camera3D camera = new Camera3D();
    public static final RenderableSorter sorter = new RenderableSorter();
    public static final Environment environment = new Environment();
    private static final FrameBuffer buffer = new FrameBuffer(2, 2, true);
    private static final RenderPool pool = new RenderPool();

    public static void render(RenderableProvider prov) {
        prov.getRenderables(pool);
        sorter.sort(camera, pool.renders);
        begin();

        for(int i = 0; i < pool.renders.size; ++i) {
            Renderable r = ((Renderable[])pool.renders.items)[i];
            UnityShaders.Graphics3DShader shader = UnityShaders.graphics3DProvider.get(r);
            shader.bind();
            shader.apply(r);
            r.meshPart.render(shader);
        }

        end();
        pool.available.addAll(pool.renders);
        pool.renders.size = 0;
    }

    public static int bind(TextureAttribute attr, int bind) {
        attr.texture.bind(bind);
        return bind;
    }

    static void begin() {
        buffer.begin(Color.clear);
        Gl.depthMask(true);
        Gl.clear(256);
        Gl.enable(2929);
        Gl.enable(2884);
        Gl.cullFace(1029);
    }

    static void end() {
        Gl.disable(2929);
        Gl.disable(2884);
        buffer.end();
        Draw.blit((Texture)buffer.getTexture(), Shaders.screenspace);
    }

    static {
        environment.set(ColorAttribute.createAmbientLight(0.4F, 0.4F, 0.4F, 1.0F));
        environment.add((new DirectionalLight()).set(0.56F, 0.56F, 0.56F, -1.0F, -1.0F, -0.3F));
        Triggers.listen(Trigger.preDraw, () -> buffer.resize(Core.graphics.getWidth(), Core.graphics.getHeight()));
    }

    private static class RenderPool implements Prov<Renderable> {
        private final Seq<Renderable> available;
        private final Seq<Renderable> renders;

        private RenderPool() {
            this.available = new Seq(10);
            this.renders = new Seq(Renderable.class);
        }

        public Renderable get() {
            Renderable r = this.available.any() ? (Renderable)this.available.pop() : new Renderable();
            this.renders.add(r);
            return r;
        }
    }
}
