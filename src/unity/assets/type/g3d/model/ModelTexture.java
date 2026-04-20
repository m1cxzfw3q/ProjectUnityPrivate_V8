package unity.assets.type.g3d.model;

import arc.math.geom.Vec2;

public class ModelTexture {
    public static final int unknown = 0;
    public static final int none = 1;
    public static final int diffuse = 2;
    public static final int emissive = 3;
    public static final int ambient = 4;
    public static final int specular = 5;
    public static final int shininess = 6;
    public static final int normal = 7;
    public static final int bump = 8;
    public static final int transparency = 9;
    public static final int reflection = 10;
    public String id;
    public String fileName;
    public Vec2 uvTranslation;
    public Vec2 uvScaling;
    public int usage;
}
