package unity.assets.type.g3d.model;

import arc.math.geom.Quat;
import arc.math.geom.Vec3;
import arc.struct.Seq;

public class ModelNodeAnimation {
    public String nodeId;
    public Seq<ModelNodeKeyframe<Vec3>> translation;
    public Seq<ModelNodeKeyframe<Quat>> rotation;
    public Seq<ModelNodeKeyframe<Vec3>> scaling;
}
