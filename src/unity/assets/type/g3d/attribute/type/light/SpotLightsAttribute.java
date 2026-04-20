package unity.assets.type.g3d.attribute.type.light;

import arc.struct.Seq;
import unity.assets.type.g3d.attribute.Attribute;
import unity.assets.type.g3d.attribute.light.SpotLight;

public class SpotLightsAttribute extends Attribute {
    public static final String lightAlias = "spotLights";
    public static final long light = register("spotLights");
    public final Seq<SpotLight> lights;

    public static boolean is(long mask) {
        return (mask & light) == mask;
    }

    public SpotLightsAttribute() {
        super(light);
        this.lights = new Seq(1);
    }

    public SpotLightsAttribute(SpotLightsAttribute copyFrom) {
        this();
        this.lights.addAll(copyFrom.lights);
    }

    public SpotLightsAttribute copy() {
        return new SpotLightsAttribute(this);
    }

    public int hashCode() {
        int result = super.hashCode();

        for(SpotLight light : this.lights) {
            result = 1237 * result + (light == null ? 0 : light.hashCode());
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
