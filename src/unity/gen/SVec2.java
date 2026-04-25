package unity.gen;

import arc.math.Mat;
import arc.math.Rand;
import arc.math.geom.Position;
import arc.math.geom.Vec2;
import arc.math.geom.Vec3;

public final class SVec2 {
    private static final Vec2 STRUCT_LOCK = new Vec2();

    public static float x(long svec2) {
        return Float.intBitsToFloat((int)(svec2 >>> 0 & 4294967295L));
    }

    public static long x(long svec2, float value) {
        return svec2 & -4294967296L | (long)Float.floatToIntBits(value) << 0;
    }

    public static float y(long svec2) {
        return Float.intBitsToFloat((int)(svec2 >>> 32 & 4294967295L));
    }

    public static long y(long svec2, float value) {
        return svec2 & 4294967295L | (long)Float.floatToIntBits(value) << 32;
    }

    public static long construct(Vec2 svec2) {
        return construct(svec2.x, svec2.y);
    }

    public static long trns(long svec2, float pangle, float pamount) {
        synchronized(STRUCT_LOCK) {
            STRUCT_LOCK.x = x(svec2);
            STRUCT_LOCK.y = y(svec2);
            return construct(STRUCT_LOCK.trns(pangle, pamount));
        }
    }

    public static long trns(long svec2, float pangle, float px, float py) {
        synchronized(STRUCT_LOCK) {
            STRUCT_LOCK.x = x(svec2);
            STRUCT_LOCK.y = y(svec2);
            return construct(STRUCT_LOCK.trns(pangle, px, py));
        }
    }

    public static long trnsExact(long svec2, float pangle, float pamount) {
        synchronized(STRUCT_LOCK) {
            STRUCT_LOCK.x = x(svec2);
            STRUCT_LOCK.y = y(svec2);
            return construct(STRUCT_LOCK.trnsExact(pangle, pamount));
        }
    }

    public static long snap(long svec2) {
        synchronized(STRUCT_LOCK) {
            STRUCT_LOCK.x = x(svec2);
            STRUCT_LOCK.y = y(svec2);
            return construct(STRUCT_LOCK.snap());
        }
    }

    public static float len(long svec2) {
        synchronized(STRUCT_LOCK) {
            STRUCT_LOCK.x = x(svec2);
            STRUCT_LOCK.y = y(svec2);
            return STRUCT_LOCK.len();
        }
    }

    public static float len2(long svec2) {
        synchronized(STRUCT_LOCK) {
            STRUCT_LOCK.x = x(svec2);
            STRUCT_LOCK.y = y(svec2);
            return STRUCT_LOCK.len2();
        }
    }

    public static long set(long svec2, Position pv) {
        synchronized(STRUCT_LOCK) {
            STRUCT_LOCK.x = x(svec2);
            STRUCT_LOCK.y = y(svec2);
            return construct(STRUCT_LOCK.set(pv));
        }
    }

    public static long set(long svec2, float px, float py) {
        synchronized(STRUCT_LOCK) {
            STRUCT_LOCK.x = x(svec2);
            STRUCT_LOCK.y = y(svec2);
            return construct(STRUCT_LOCK.set(px, py));
        }
    }

    public static long set(long svec2, Vec3 pother) {
        synchronized(STRUCT_LOCK) {
            STRUCT_LOCK.x = x(svec2);
            STRUCT_LOCK.y = y(svec2);
            return construct(STRUCT_LOCK.set(pother));
        }
    }

    public static long sub(long svec2, Position pv) {
        synchronized(STRUCT_LOCK) {
            STRUCT_LOCK.x = x(svec2);
            STRUCT_LOCK.y = y(svec2);
            return construct(STRUCT_LOCK.sub(pv));
        }
    }

    public static long sub(long svec2, float px, float py) {
        synchronized(STRUCT_LOCK) {
            STRUCT_LOCK.x = x(svec2);
            STRUCT_LOCK.y = y(svec2);
            return construct(STRUCT_LOCK.sub(px, py));
        }
    }

    public static long sub(long svec2, Vec3 pv) {
        synchronized(STRUCT_LOCK) {
            STRUCT_LOCK.x = x(svec2);
            STRUCT_LOCK.y = y(svec2);
            return construct(STRUCT_LOCK.sub(pv));
        }
    }

    public static long nor(long svec2) {
        synchronized(STRUCT_LOCK) {
            STRUCT_LOCK.x = x(svec2);
            STRUCT_LOCK.y = y(svec2);
            return construct(STRUCT_LOCK.nor());
        }
    }

    public static long add(long svec2, Position ppos) {
        synchronized(STRUCT_LOCK) {
            STRUCT_LOCK.x = x(svec2);
            STRUCT_LOCK.y = y(svec2);
            return construct(STRUCT_LOCK.add(ppos));
        }
    }

    public static long add(long svec2, float px, float py) {
        synchronized(STRUCT_LOCK) {
            STRUCT_LOCK.x = x(svec2);
            STRUCT_LOCK.y = y(svec2);
            return construct(STRUCT_LOCK.add(px, py));
        }
    }

    public static float dot(long svec2, float pox, float poy) {
        synchronized(STRUCT_LOCK) {
            STRUCT_LOCK.x = x(svec2);
            STRUCT_LOCK.y = y(svec2);
            return STRUCT_LOCK.dot(pox, poy);
        }
    }

    public static long scl(long svec2, float pscalar) {
        synchronized(STRUCT_LOCK) {
            STRUCT_LOCK.x = x(svec2);
            STRUCT_LOCK.y = y(svec2);
            return construct(STRUCT_LOCK.scl(pscalar));
        }
    }

    public static long inv(long svec2) {
        synchronized(STRUCT_LOCK) {
            STRUCT_LOCK.x = x(svec2);
            STRUCT_LOCK.y = y(svec2);
            return construct(STRUCT_LOCK.inv());
        }
    }

    public static long scl(long svec2, float px, float py) {
        synchronized(STRUCT_LOCK) {
            STRUCT_LOCK.x = x(svec2);
            STRUCT_LOCK.y = y(svec2);
            return construct(STRUCT_LOCK.scl(px, py));
        }
    }

    public static float dst(long svec2, float px, float py) {
        synchronized(STRUCT_LOCK) {
            STRUCT_LOCK.x = x(svec2);
            STRUCT_LOCK.y = y(svec2);
            return STRUCT_LOCK.dst(px, py);
        }
    }

    public static float dst2(long svec2, float px, float py) {
        synchronized(STRUCT_LOCK) {
            STRUCT_LOCK.x = x(svec2);
            STRUCT_LOCK.y = y(svec2);
            return STRUCT_LOCK.dst2(px, py);
        }
    }

    public static long limit(long svec2, float plimit) {
        synchronized(STRUCT_LOCK) {
            STRUCT_LOCK.x = x(svec2);
            STRUCT_LOCK.y = y(svec2);
            return construct(STRUCT_LOCK.limit(plimit));
        }
    }

    public static long limit2(long svec2, float plimit2) {
        synchronized(STRUCT_LOCK) {
            STRUCT_LOCK.x = x(svec2);
            STRUCT_LOCK.y = y(svec2);
            return construct(STRUCT_LOCK.limit2(plimit2));
        }
    }

    public static long clamp(long svec2, float pmin, float pmax) {
        synchronized(STRUCT_LOCK) {
            STRUCT_LOCK.x = x(svec2);
            STRUCT_LOCK.y = y(svec2);
            return construct(STRUCT_LOCK.clamp(pmin, pmax));
        }
    }

    public static long setLength(long svec2, float plen) {
        synchronized(STRUCT_LOCK) {
            STRUCT_LOCK.x = x(svec2);
            STRUCT_LOCK.y = y(svec2);
            return construct(STRUCT_LOCK.setLength(plen));
        }
    }

    public static long setLength2(long svec2, float plen2) {
        synchronized(STRUCT_LOCK) {
            STRUCT_LOCK.x = x(svec2);
            STRUCT_LOCK.y = y(svec2);
            return construct(STRUCT_LOCK.setLength2(plen2));
        }
    }

    public static String toString(long svec2) {
        synchronized(STRUCT_LOCK) {
            STRUCT_LOCK.x = x(svec2);
            STRUCT_LOCK.y = y(svec2);
            return STRUCT_LOCK.toString();
        }
    }

    public static long clamp(long svec2, float pminx, float pminy, float pmaxy, float pmaxx) {
        synchronized(STRUCT_LOCK) {
            STRUCT_LOCK.x = x(svec2);
            STRUCT_LOCK.y = y(svec2);
            return construct(STRUCT_LOCK.clamp(pminx, pminy, pmaxy, pmaxx));
        }
    }

    public static long tryFromString(long svec2, String pv) {
        synchronized(STRUCT_LOCK) {
            STRUCT_LOCK.x = x(svec2);
            STRUCT_LOCK.y = y(svec2);
            return construct(STRUCT_LOCK.tryFromString(pv));
        }
    }

    public static long fromString(long svec2, String pv) {
        synchronized(STRUCT_LOCK) {
            STRUCT_LOCK.x = x(svec2);
            STRUCT_LOCK.y = y(svec2);
            return construct(STRUCT_LOCK.fromString(pv));
        }
    }

    public static long mul(long svec2, Mat pmat) {
        synchronized(STRUCT_LOCK) {
            STRUCT_LOCK.x = x(svec2);
            STRUCT_LOCK.y = y(svec2);
            return construct(STRUCT_LOCK.mul(pmat));
        }
    }

    public static float crs(long svec2, float px, float py) {
        synchronized(STRUCT_LOCK) {
            STRUCT_LOCK.x = x(svec2);
            STRUCT_LOCK.y = y(svec2);
            return STRUCT_LOCK.crs(px, py);
        }
    }

    public static float angle(long svec2) {
        synchronized(STRUCT_LOCK) {
            STRUCT_LOCK.x = x(svec2);
            STRUCT_LOCK.y = y(svec2);
            return STRUCT_LOCK.angle();
        }
    }

    public static long rnd(long svec2, float plength) {
        synchronized(STRUCT_LOCK) {
            STRUCT_LOCK.x = x(svec2);
            STRUCT_LOCK.y = y(svec2);
            return construct(STRUCT_LOCK.rnd(plength));
        }
    }

    public static float angleRad(long svec2) {
        synchronized(STRUCT_LOCK) {
            STRUCT_LOCK.x = x(svec2);
            STRUCT_LOCK.y = y(svec2);
            return STRUCT_LOCK.angleRad();
        }
    }

    public static long setAngle(long svec2, float pdegrees) {
        synchronized(STRUCT_LOCK) {
            STRUCT_LOCK.x = x(svec2);
            STRUCT_LOCK.y = y(svec2);
            return construct(STRUCT_LOCK.setAngle(pdegrees));
        }
    }

    public static long setAngleRad(long svec2, float pradians) {
        synchronized(STRUCT_LOCK) {
            STRUCT_LOCK.x = x(svec2);
            STRUCT_LOCK.y = y(svec2);
            return construct(STRUCT_LOCK.setAngleRad(pradians));
        }
    }

    public static long rotateTo(long svec2, float pangle, float pspeed) {
        synchronized(STRUCT_LOCK) {
            STRUCT_LOCK.x = x(svec2);
            STRUCT_LOCK.y = y(svec2);
            return construct(STRUCT_LOCK.rotateTo(pangle, pspeed));
        }
    }

    public static long rotate(long svec2, float pdegrees) {
        synchronized(STRUCT_LOCK) {
            STRUCT_LOCK.x = x(svec2);
            STRUCT_LOCK.y = y(svec2);
            return construct(STRUCT_LOCK.rotate(pdegrees));
        }
    }

    public static long rotateRad(long svec2, float pradians) {
        synchronized(STRUCT_LOCK) {
            STRUCT_LOCK.x = x(svec2);
            STRUCT_LOCK.y = y(svec2);
            return construct(STRUCT_LOCK.rotateRad(pradians));
        }
    }

    public static long rotateRadExact(long svec2, float pradians) {
        synchronized(STRUCT_LOCK) {
            STRUCT_LOCK.x = x(svec2);
            STRUCT_LOCK.y = y(svec2);
            return construct(STRUCT_LOCK.rotateRadExact(pradians));
        }
    }

    public static long rotate90(long svec2, int pdir) {
        synchronized(STRUCT_LOCK) {
            STRUCT_LOCK.x = x(svec2);
            STRUCT_LOCK.y = y(svec2);
            return construct(STRUCT_LOCK.rotate90(pdir));
        }
    }

    public static long lerpDelta(long svec2, float ptx, float pty, float palpha) {
        synchronized(STRUCT_LOCK) {
            STRUCT_LOCK.x = x(svec2);
            STRUCT_LOCK.y = y(svec2);
            return construct(STRUCT_LOCK.lerpDelta(ptx, pty, palpha));
        }
    }

    public static long lerpDelta(long svec2, Position ptarget, float palpha) {
        synchronized(STRUCT_LOCK) {
            STRUCT_LOCK.x = x(svec2);
            STRUCT_LOCK.y = y(svec2);
            return construct(STRUCT_LOCK.lerpDelta(ptarget, palpha));
        }
    }

    public static long lerp(long svec2, Position ptarget, float palpha) {
        synchronized(STRUCT_LOCK) {
            STRUCT_LOCK.x = x(svec2);
            STRUCT_LOCK.y = y(svec2);
            return construct(STRUCT_LOCK.lerp(ptarget, palpha));
        }
    }

    public static long lerp(long svec2, float ptx, float pty, float palpha) {
        synchronized(STRUCT_LOCK) {
            STRUCT_LOCK.x = x(svec2);
            STRUCT_LOCK.y = y(svec2);
            return construct(STRUCT_LOCK.lerp(ptx, pty, palpha));
        }
    }

    public static long setToRandomDirection(long svec2) {
        synchronized(STRUCT_LOCK) {
            STRUCT_LOCK.x = x(svec2);
            STRUCT_LOCK.y = y(svec2);
            return construct(STRUCT_LOCK.setToRandomDirection());
        }
    }

    public static long setToRandomDirection(long svec2, Rand prand) {
        synchronized(STRUCT_LOCK) {
            STRUCT_LOCK.x = x(svec2);
            STRUCT_LOCK.y = y(svec2);
            return construct(STRUCT_LOCK.setToRandomDirection(prand));
        }
    }

    public static int hashCode(long svec2) {
        synchronized(STRUCT_LOCK) {
            STRUCT_LOCK.x = x(svec2);
            STRUCT_LOCK.y = y(svec2);
            return STRUCT_LOCK.hashCode();
        }
    }

    public static boolean equals(long svec2, Object pobj) {
        synchronized(STRUCT_LOCK) {
            STRUCT_LOCK.x = x(svec2);
            STRUCT_LOCK.y = y(svec2);
            return STRUCT_LOCK.equals(pobj);
        }
    }

    public static boolean epsilonEquals(long svec2, float px, float py, float pepsilon) {
        synchronized(STRUCT_LOCK) {
            STRUCT_LOCK.x = x(svec2);
            STRUCT_LOCK.y = y(svec2);
            return STRUCT_LOCK.epsilonEquals(px, py, pepsilon);
        }
    }

    public static boolean epsilonEquals(long svec2, float px, float py) {
        synchronized(STRUCT_LOCK) {
            STRUCT_LOCK.x = x(svec2);
            STRUCT_LOCK.y = y(svec2);
            return STRUCT_LOCK.epsilonEquals(px, py);
        }
    }

    public static boolean isNaN(long svec2) {
        synchronized(STRUCT_LOCK) {
            STRUCT_LOCK.x = x(svec2);
            STRUCT_LOCK.y = y(svec2);
            return STRUCT_LOCK.isNaN();
        }
    }

    public static boolean isInfinite(long svec2) {
        synchronized(STRUCT_LOCK) {
            STRUCT_LOCK.x = x(svec2);
            STRUCT_LOCK.y = y(svec2);
            return STRUCT_LOCK.isInfinite();
        }
    }

    public static boolean isUnit(long svec2) {
        synchronized(STRUCT_LOCK) {
            STRUCT_LOCK.x = x(svec2);
            STRUCT_LOCK.y = y(svec2);
            return STRUCT_LOCK.isUnit();
        }
    }

    public static boolean isUnit(long svec2, float pmargin) {
        synchronized(STRUCT_LOCK) {
            STRUCT_LOCK.x = x(svec2);
            STRUCT_LOCK.y = y(svec2);
            return STRUCT_LOCK.isUnit(pmargin);
        }
    }

    public static boolean isZero(long svec2) {
        synchronized(STRUCT_LOCK) {
            STRUCT_LOCK.x = x(svec2);
            STRUCT_LOCK.y = y(svec2);
            return STRUCT_LOCK.isZero();
        }
    }

    public static boolean isZero(long svec2, float pmargin) {
        synchronized(STRUCT_LOCK) {
            STRUCT_LOCK.x = x(svec2);
            STRUCT_LOCK.y = y(svec2);
            return STRUCT_LOCK.isZero(pmargin);
        }
    }

    public static long setZero(long svec2) {
        synchronized(STRUCT_LOCK) {
            STRUCT_LOCK.x = x(svec2);
            STRUCT_LOCK.y = y(svec2);
            return construct(STRUCT_LOCK.setZero());
        }
    }

    public static long construct(float x, float y) {
        return (long)Float.floatToIntBits(x) << 0 | (long)Float.floatToIntBits(y) << 32;
    }
}
