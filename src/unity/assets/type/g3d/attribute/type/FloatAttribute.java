package unity.assets.type.g3d.attribute.type;

import arc.math.Mathf;
import unity.assets.type.g3d.attribute.Attribute;

public class FloatAttribute extends Attribute {
    public static final String shininessAlias = "shininess";
    public static final long shininess = register("shininess");
    public static final String alphaTestAlias = "alphaTest";
    public static final long alphaTest = register("alphaTest");
    public float value;

    public static FloatAttribute createShininess(float value) {
        return new FloatAttribute(shininess, value);
    }

    public static FloatAttribute createAlphaTest(float value) {
        return new FloatAttribute(alphaTest, value);
    }

    public FloatAttribute(long type) {
        super(type);
    }

    public FloatAttribute(long type, float value) {
        super(type);
        this.value = value;
    }

    public FloatAttribute copy() {
        return new FloatAttribute(this.type, this.value);
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 977 * result + Float.floatToRawIntBits(this.value);
        return result;
    }

    public int compareTo(Attribute o) {
        if (this.type != o.type) {
            return (int)(this.type - o.type);
        } else {
            float v = ((FloatAttribute)o).value;
            return Mathf.equal(this.value, v) ? 0 : (this.value < v ? -1 : 1);
        }
    }
}
