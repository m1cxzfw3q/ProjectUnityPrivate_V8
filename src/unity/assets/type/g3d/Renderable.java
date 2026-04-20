package unity.assets.type.g3d;

import arc.graphics.Mesh;
import arc.math.geom.Mat3D;
import arc.util.pooling.Pool;
import unity.assets.type.g3d.attribute.Material;
import unity.graphics.MeshPart;

public class Renderable implements Pool.Poolable {
    public final Mat3D worldTransform = new Mat3D();
    public final MeshPart meshPart = new MeshPart();
    public Material material;
    public Mat3D[] bones;
    public Object userData;

    public Renderable set(Renderable renderable) {
        this.worldTransform.set(renderable.worldTransform);
        this.material = renderable.material;
        this.meshPart.set(renderable.meshPart);
        this.bones = renderable.bones;
        this.userData = renderable.userData;
        return this;
    }

    public void reset() {
        this.material = null;
        this.userData = null;
        this.meshPart.set("", (Mesh)null, 0, 0, 0);
    }
}
