package unity.util;

import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Time;

public class CyclicCoordinateDescent {
    static final float epsilon = 1.0E-4F;

    static float simplifyAngle(float angle) {
        angle %= 360.0F;
        if (angle < -180.0F) {
            angle += 360.0F;
        } else if (angle > 180.0F) {
            angle -= 360.0F;
        }

        return angle;
    }

    public static void calculate(DefaultBone[] bones, float targetX, float targetY, float arrivalDist, float angleLimit, float angleLerp, boolean delta) {
        int numBones = bones.length;
        if (numBones > 0) {
            float arrivalDistSqr = arrivalDist * arrivalDist;
            Seq<WorldBone> worldBones = new Seq();
            WorldBone root = new WorldBone();
            root.x = bones[0].x;
            root.y = bones[0].y;
            root.angle = bones[0].angle;
            worldBones.add(root);

            for(int i = 1; i < numBones; ++i) {
                WorldBone prevWorldBone = (WorldBone)worldBones.get(i - 1);
                DefaultBone curLocalBone = bones[i];
                WorldBone newWorldBone = new WorldBone();
                newWorldBone.x = prevWorldBone.x + prevWorldBone.cosAngle() * curLocalBone.x - prevWorldBone.sinAngle() * curLocalBone.y;
                newWorldBone.y = prevWorldBone.y + prevWorldBone.sinAngle() * curLocalBone.x + prevWorldBone.cosAngle() * curLocalBone.y;
                newWorldBone.angle = prevWorldBone.angle + curLocalBone.angle;
                worldBones.add(newWorldBone);
            }

            float endX = ((WorldBone)worldBones.get(numBones - 1)).x;
            float endY = ((WorldBone)worldBones.get(numBones - 1)).y;

            for(int i = numBones - 2; i >= 0; --i) {
                float curToEndX = endX - ((WorldBone)worldBones.get(i)).x;
                float curToEndY = endY - ((WorldBone)worldBones.get(i)).y;
                float curToEndMag = Mathf.sqrt(curToEndX * curToEndX + curToEndY * curToEndY);
                float curToTargetX = targetX - ((WorldBone)worldBones.get(i)).x;
                float curToTargetY = targetY - ((WorldBone)worldBones.get(i)).y;
                float curToTargetMag = Mathf.sqrt(curToTargetX * curToTargetX + curToTargetY * curToTargetY);
                float endTargetMag = curToEndMag * curToTargetMag;
                float cosRotAng;
                float sinRotAng;
                if (endTargetMag <= 1.0E-4F) {
                    cosRotAng = 1.0F;
                    sinRotAng = 0.0F;
                } else {
                    cosRotAng = (curToEndX * curToTargetX + curToEndY * curToTargetY) / endTargetMag;
                    sinRotAng = (curToEndX * curToTargetY - curToEndY * curToTargetX) / endTargetMag;
                }

                float rotAng = (float)Math.acos((double)Mathf.clamp(cosRotAng, -1.0F, 1.0F));
                if (sinRotAng < 0.0F) {
                    rotAng = -rotAng;
                }

                rotAng *= (180F / (float)Math.PI);
                endX = ((WorldBone)worldBones.get(i)).x + cosRotAng * curToEndX - sinRotAng * curToEndY;
                endY = ((WorldBone)worldBones.get(i)).y + sinRotAng * curToEndX + cosRotAng * curToEndY;
                float lerpAngle = Mathf.slerp(simplifyAngle(bones[i].angle), simplifyAngle(bones[i].angle + rotAng), Mathf.clamp(angleLerp * (delta ? Time.delta : 1.0F)));
                float offAngle = angleLerp >= 1.0F ? simplifyAngle(bones[i].angle + rotAng) : lerpAngle;
                if (angleLimit < 360.0F) {
                    offAngle = Utils.clampedAngle(offAngle, bones[i + 1].angle, angleLimit);
                }

                bones[i].angle = offAngle;
                float endToTargetX = targetX - endX;
                float endToTargetY = targetY - endY;
                if (endToTargetX * endToTargetX + endToTargetY * endToTargetY <= arrivalDistSqr) {
                    break;
                }
            }

        }
    }

    public static class DefaultBone {
        public float angle;
        public float x;
        public float y;
    }

    static class WorldBone {
        float x;
        float y;
        float angle;

        float cosAngle() {
            return Mathf.cosDeg(this.angle);
        }

        float sinAngle() {
            return Mathf.sinDeg(this.angle);
        }
    }
}
