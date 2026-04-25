package unity.entities;

import arc.math.geom.Vec2;

public class TriJointLeg {
    public Vec2[] joints = new Vec2[3];
    public int group;
    public boolean moving;
    public float legScl = 1.0F;
    public float jointLerp = 1.0F;
    public float stage;

    public TriJointLeg() {
        for(int i = 0; i < this.joints.length; ++i) {
            this.joints[i] = new Vec2();
        }

    }
}
