package unity.assets.type.g3d.model;

import arc.math.geom.Quat;
import arc.math.geom.Vec3;

public class ModelNode {
    public String id;
    public Vec3 translation;
    public Quat rotation;
    public Vec3 scale;
    public String meshId;
    public ModelNodePart[] parts;
    public ModelNode[] children;
}
