package unity.assets.type.g3d.attribute.type.light;

import arc.struct.Seq;
import unity.assets.type.g3d.attribute.Attribute;
import unity.assets.type.g3d.attribute.light.PointLight;

public class PointLightsAttribute extends Attribute {
    public static final String lightAlias = "pointLights";
    public static final long light = register("pointLights");
    public final Seq<PointLight> lights;

    public static boolean is(long mask) {
        return (mask & light) == mask;
    }

    public PointLightsAttribute() {
        super(light);
        this.lights = new Seq(1);
    }

    public PointLightsAttribute(PointLightsAttribute copyFrom) {
        this();
        this.lights.addAll(copyFrom.lights);
    }

    public PointLightsAttribute copy() {
        return new PointLightsAttribute(this);
    }

    public int hashCode() {
        int result = super.hashCode();

        for(PointLight light : this.lights) {
            result = 1231 * result + (light == null ? 0 : light.hashCode());
        }

        return result;
    }

    public int compareTo(Attribute o) {
        if (this.type != o.type) {
            return this.type < o.type ? -1 : 1;
        } else {
            return 0;
        }
    }
}
