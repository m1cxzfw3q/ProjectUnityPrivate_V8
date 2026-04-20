package unity.assets.type.g3d.attribute.type;

import arc.graphics.Texture;
import arc.graphics.Texture.TextureFilter;
import arc.graphics.Texture.TextureWrap;
import arc.math.Mathf;
import unity.assets.type.g3d.attribute.Attribute;

public class TextureAttribute extends Attribute {
    public static final String diffuseAlias = "diffuseTexture";
    public static final long diffuse = register("diffuseTexture");
    public static final String specularAlias = "specularTexture";
    public static final long specular = register("specularTexture");
    public static final String bumpAlias = "bumpTexture";
    public static final long bump = register("bumpTexture");
    public static final String normalAlias = "normalTexture";
    public static final long normal = register("normalTexture");
    public static final String ambientAlias = "ambientTexture";
    public static final long ambient = register("ambientTexture");
    public static final String emissiveAlias = "emissiveTexture";
    public static final long emissive = register("emissiveTexture");
    public static final String reflectionAlias = "reflectionTexture";
    public static final long reflection = register("reflectionTexture");
    protected static long Mask;
    public final Texture texture;
    public float offsetU;
    public float offsetV;
    public float scaleU;
    public float scaleV;

    public static boolean is(long mask) {
        return (mask & Mask) != 0L;
    }

    public static TextureAttribute createDiffuse(Texture texture) {
        return new TextureAttribute(diffuse, texture);
    }

    public static TextureAttribute createSpecular(Texture texture) {
        return new TextureAttribute(specular, texture);
    }

    public static TextureAttribute createNormal(Texture texture) {
        return new TextureAttribute(normal, texture);
    }

    public static TextureAttribute createBump(Texture texture) {
        return new TextureAttribute(bump, texture);
    }

    public static TextureAttribute createAmbient(Texture texture) {
        return new TextureAttribute(ambient, texture);
    }

    public static TextureAttribute createEmissive(Texture texture) {
        return new TextureAttribute(emissive, texture);
    }

    public static TextureAttribute createReflection(Texture texture) {
        return new TextureAttribute(reflection, texture);
    }

    public TextureAttribute(long type, Texture texture) {
        super(type);
        this.offsetU = 0.0F;
        this.offsetV = 0.0F;
        this.scaleU = 1.0F;
        this.scaleV = 1.0F;
        texture.setFilter(TextureFilter.linear);
        texture.setWrap(TextureWrap.repeat);
        this.texture = texture;
    }

    public TextureAttribute(long type, Texture texture, float offsetU, float offsetV, float scaleU, float scaleV) {
        this(type, texture);
        this.offsetU = offsetU;
        this.offsetV = offsetV;
        this.scaleU = scaleU;
        this.scaleV = scaleV;
    }

    public TextureAttribute(TextureAttribute copyFrom) {
        this(copyFrom.type, copyFrom.texture, copyFrom.offsetU, copyFrom.offsetV, copyFrom.scaleU, copyFrom.scaleV);
    }

    public TextureAttribute copy() {
        return new TextureAttribute(this);
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 991 * result + this.texture.getTextureObjectHandle();
        result = 991 * result + Float.floatToRawIntBits(this.offsetU);
        result = 991 * result + Float.floatToRawIntBits(this.offsetV);
        result = 991 * result + Float.floatToRawIntBits(this.scaleU);
        result = 991 * result + Float.floatToRawIntBits(this.scaleV);
        return result;
    }

    public int compareTo(Attribute o) {
        if (this.type != o.type) {
            return this.type < o.type ? -1 : 1;
        } else {
            TextureAttribute other = (TextureAttribute)o;
            int c = Integer.compare(this.texture.getTextureObjectHandle(), other.texture.getTextureObjectHandle());
            if (c != 0) {
                return c;
            } else if (!Mathf.equal(this.scaleU, other.scaleU)) {
                return this.scaleU > other.scaleU ? 1 : -1;
            } else if (!Mathf.equal(this.scaleV, other.scaleV)) {
                return this.scaleV > other.scaleV ? 1 : -1;
            } else if (!Mathf.equal(this.offsetU, other.offsetU)) {
                return this.offsetU > other.offsetU ? 1 : -1;
            } else if (!Mathf.equal(this.offsetV, other.offsetV)) {
                return this.offsetV > other.offsetV ? 1 : -1;
            } else {
                return 0;
            }
        }
    }

    static {
        Mask = diffuse | specular | bump | normal | ambient | emissive | reflection;
    }
}
