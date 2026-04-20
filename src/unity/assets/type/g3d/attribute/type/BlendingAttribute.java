package unity.assets.type.g3d.attribute.type;

import arc.math.Mathf;
import unity.assets.type.g3d.attribute.Attribute;

public class BlendingAttribute extends Attribute {
    public static final String blendAlias = "blended";
    public static final long blend = register("blended");
    public boolean blended;
    public int sourceFunction;
    public int destFunction;
    public float opacity;

    public static boolean is(long mask) {
        return (mask & blend) == mask;
    }

    public BlendingAttribute() {
        this((BlendingAttribute)null);
    }

    public BlendingAttribute(boolean blended, int sourceFunc, int destFunc, float opacity) {
        super(blend);
        this.opacity = 1.0F;
        this.blended = blended;
        this.sourceFunction = sourceFunc;
        this.destFunction = destFunc;
        this.opacity = opacity;
    }

    public BlendingAttribute(int sourceFunc, int destFunc, float opacity) {
        this(true, sourceFunc, destFunc, opacity);
    }

    public BlendingAttribute(int sourceFunc, int destFunc) {
        this(sourceFunc, destFunc, 1.0F);
    }

    public BlendingAttribute(boolean blended, float opacity) {
        this(blended, 770, 771, opacity);
    }

    public BlendingAttribute(float opacity) {
        this(true, opacity);
    }

    public BlendingAttribute(BlendingAttribute copyFrom) {
        this(copyFrom == null || copyFrom.blended, copyFrom == null ? 770 : copyFrom.sourceFunction, copyFrom == null ? 771 : copyFrom.destFunction, copyFrom == null ? 1.0F : copyFrom.opacity);
    }

    public BlendingAttribute copy() {
        return new BlendingAttribute(this);
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 947 * result + (this.blended ? 1 : 0);
        result = 947 * result + this.sourceFunction;
        result = 947 * result + this.destFunction;
        result = 947 * result + Float.floatToRawIntBits(this.opacity);
        return result;
    }

    public int compareTo(Attribute o) {
        if (this.type != o.type) {
            return (int)(this.type - o.type);
        } else {
            BlendingAttribute other = (BlendingAttribute)o;
            if (this.blended != other.blended) {
                return this.blended ? 1 : -1;
            } else if (this.sourceFunction != other.sourceFunction) {
                return this.sourceFunction - other.sourceFunction;
            } else if (this.destFunction != other.destFunction) {
                return this.destFunction - other.destFunction;
            } else {
                return Mathf.equal(this.opacity, other.opacity) ? 0 : (this.opacity < other.opacity ? 1 : -1);
            }
        }
    }
}
