package unity.assets.type.g3d;

import arc.math.Mathf;
import arc.math.geom.Mat3D;
import arc.math.geom.Quat;
import arc.math.geom.Vec3;
import arc.struct.Seq;
import arc.util.Tmp;
import unity.util.Utils;

public class AnimControl {
    public static final Mat3D tmp = new Mat3D();
    public final ModelInstance model;
    private boolean applying = false;

    public AnimControl(ModelInstance model) {
        this.model = model;
    }

    public void begin() {
        if (this.applying) {
            throw new IllegalStateException("Do not begin() twice");
        } else {
            this.applying = true;
            this.model.nodes.each((node) -> node.isAnimated = true);
        }
    }

    public void end() {
        if (!this.applying) {
            throw new IllegalStateException("Do not end() twice");
        } else {
            this.applying = false;
            this.model.calculateTransforms();
            this.model.nodes.each((node) -> node.isAnimated = false);
        }
    }

    public void check() {
        if (!this.applying) {
            throw new IllegalStateException("Call begin() first");
        }
    }

    public void apply(String id, float time) {
        this.apply(this.model.getAnimation(id), time);
    }

    public void apply(Animation animation, float time) {
        this.check();
        time = Mathf.clamp(time, 0.0F, animation.duration);

        for(NodeAnimation anim : animation.nodeAnimations) {
            Vec3 trns = Tmp.v31.setZero();
            Quat quat = Utils.q1.idt();
            Vec3 scl = Tmp.v32.set(1.0F, 1.0F, 1.0F);
            if (anim.translation != null && anim.translation.any()) {
                trns.set((Vec3)((NodeKeyframe)anim.translation.get(index(anim.translation, time))).value);
            }

            if (anim.rotation != null && anim.rotation.any()) {
                quat.set((Quat)((NodeKeyframe)anim.rotation.get(index(anim.rotation, time))).value);
            }

            if (anim.scaling != null && anim.scaling.any()) {
                scl.set((Vec3)((NodeKeyframe)anim.scaling.get(index(anim.scaling, time))).value);
            }

            anim.node.isAnimated = true;
            anim.node.localTransform.mul(tmp.set(trns, quat, scl));
        }

    }

    public static <T> int index(Seq<NodeKeyframe<T>> arr, float time) {
        time = Math.max(time, 0.0F);
        int lastIndex = arr.size - 1;
        int minIndex = 0;
        int maxIndex = lastIndex;

        while(minIndex < maxIndex) {
            int i = (minIndex + maxIndex) / 2;
            if (time > ((NodeKeyframe)arr.get(i + 1)).keytime) {
                minIndex = i + 1;
            } else {
                if (!(time < ((NodeKeyframe)arr.get(i)).keytime)) {
                    return i;
                }

                maxIndex = i - 1;
            }
        }

        return minIndex;
    }
}
