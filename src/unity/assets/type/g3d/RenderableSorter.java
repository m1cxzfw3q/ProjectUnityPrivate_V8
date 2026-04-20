package unity.assets.type.g3d;

import arc.graphics.g3d.Camera3D;
import arc.math.geom.Mat3D;
import arc.math.geom.Vec3;
import arc.struct.Seq;
import java.util.Comparator;
import unity.assets.type.g3d.attribute.type.BlendingAttribute;

public class RenderableSorter implements Comparator<Renderable> {
    private Camera3D camera;
    private final Vec3 tmpV1 = new Vec3();
    private final Vec3 tmpV2 = new Vec3();

    public void sort(Camera3D camera, Seq<Renderable> renderables) {
        this.camera = camera;
        renderables.sort(this);
        this.camera = null;
    }

    private void getTranslation(Mat3D worldTransform, Vec3 center, Vec3 output) {
        if (center.isZero()) {
            worldTransform.getTranslation(output);
        } else if (!worldTransform.hasRotationOrScaling()) {
            worldTransform.getTranslation(output).add(center);
        } else {
            Mat3D.prj(output.set(center), worldTransform);
        }

    }

    public int compare(Renderable o1, Renderable o2) {
        boolean b1 = o1.material.has(BlendingAttribute.blend) && ((BlendingAttribute)o1.material.get(BlendingAttribute.blend)).blended;
        boolean b2 = o2.material.has(BlendingAttribute.blend) && ((BlendingAttribute)o2.material.get(BlendingAttribute.blend)).blended;
        if (b1 != b2) {
            return b1 ? 1 : -1;
        } else {
            this.getTranslation(o1.worldTransform, o1.meshPart.center, this.tmpV1);
            this.getTranslation(o2.worldTransform, o2.meshPart.center, this.tmpV2);
            float dst = (float)((int)(1000.0F * this.camera.position.dst2(this.tmpV1)) - (int)(1000.0F * this.camera.position.dst2(this.tmpV2)));
            int result = dst < 0.0F ? -1 : (dst > 0.0F ? 1 : 0);
            return b1 ? -result : result;
        }
    }
}
