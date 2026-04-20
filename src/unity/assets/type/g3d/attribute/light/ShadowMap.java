package unity.assets.type.g3d.attribute.light;

import arc.graphics.Texture;
import arc.math.geom.Mat3D;

public interface ShadowMap {
    Mat3D getProjViewTrans();

    Texture getDepthMap();
}
