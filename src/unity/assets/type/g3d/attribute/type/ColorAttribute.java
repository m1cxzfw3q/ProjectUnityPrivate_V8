package unity.assets.type.g3d.attribute.type;

import arc.graphics.Color;
import unity.assets.type.g3d.attribute.Attribute;

public class ColorAttribute extends Attribute {
    public static final String diffuseAlias = "diffuseColor";
    public static final long diffuse = register("diffuseColor");
    public static final String specularAlias = "specularColor";
    public static final long specular = register("specularColor");
    public static final String ambientAlias = "ambientColor";
    public static final long ambient = register("ambientColor");
    public static final String emissiveAlias = "emissiveColor";
    public static final long emissive = register("emissiveColor");
    public static final String reflectionAlias = "reflectionColor";
    public static final long reflection = register("reflectionColor");
    public static final String ambientLightAlias = "ambientLightColor";
    public static final long ambientLight = register("ambientLightColor");
    public static final String fogAlias = "fogColor";
    public static final long fog = register("fogColor");
    protected static long Mask;
    public final Color color;

    public static boolean is(long mask) {
        return (mask & Mask) != 0L;
    }

    public static ColorAttribute createAmbient(Color color) {
        return new ColorAttribute(ambient, color);
    }

    public static ColorAttribute createAmbient(float r, float g, float b, float a) {
        return new ColorAttribute(ambient, r, g, b, a);
    }

    public static ColorAttribute createDiffuse(Color color) {
        return new ColorAttribute(diffuse, color);
    }

    public static ColorAttribute createDiffuse(float r, float g, float b, float a) {
        return new ColorAttribute(diffuse, r, g, b, a);
    }

    public static ColorAttribute createSpecular(Color color) {
        return new ColorAttribute(specular, color);
    }

    public static ColorAttribute createSpecular(float r, float g, float b, float a) {
        return new ColorAttribute(specular, r, g, b, a);
    }

    public static ColorAttribute createReflection(Color color) {
        return new ColorAttribute(reflection, color);
    }

    public static ColorAttribute createReflection(float r, float g, float b, float a) {
        return new ColorAttribute(reflection, r, g, b, a);
    }

    public static ColorAttribute createEmissive(Color color) {
        return new ColorAttribute(emissive, color);
    }

    public static ColorAttribute createEmissive(float r, float g, float b, float a) {
        return new ColorAttribute(emissive, r, g, b, a);
    }

    public static ColorAttribute createAmbientLight(Color color) {
        return new ColorAttribute(ambientLight, color);
    }

    public static ColorAttribute createAmbientLight(float r, float g, float b, float a) {
        return new ColorAttribute(ambientLight, r, g, b, a);
    }

    public static ColorAttribute createFog(Color color) {
        return new ColorAttribute(fog, color);
    }

    public static ColorAttribute createFog(float r, float g, float b, float a) {
        return new ColorAttribute(fog, r, g, b, a);
    }

    public ColorAttribute(long type) {
        super(type);
        this.color = new Color();
        if (!is(type)) {
            throw new IllegalArgumentException("Invalid type specified");
        }
    }

    public ColorAttribute(long type, Color color) {
        this(type);
        if (color != null) {
            this.color.set(color);
        }

    }

    public ColorAttribute(long type, float r, float g, float b, float a) {
        this(type);
        this.color.set(r, g, b, a);
    }

    public ColorAttribute(ColorAttribute copyFrom) {
        this(copyFrom.type, copyFrom.color);
    }

    public ColorAttribute copy() {
        return new ColorAttribute(this);
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 953 * result + this.color.abgr();
        return result;
    }

    public int compareTo(Attribute o) {
        return this.type != o.type ? (int)(this.type - o.type) : ((ColorAttribute)o).color.abgr() - this.color.abgr();
    }

    static {
        Mask = ambient | diffuse | specular | emissive | reflection | ambientLight | fog;
    }
}
