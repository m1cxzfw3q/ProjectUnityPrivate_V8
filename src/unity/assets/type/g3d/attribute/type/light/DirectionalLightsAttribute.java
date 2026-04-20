package unity.assets.type.g3d.attribute.type.light;

import arc.struct.Seq;
import unity.assets.type.g3d.attribute.Attribute;
import unity.assets.type.g3d.attribute.light.DirectionalLight;

public class DirectionalLightsAttribute extends Attribute {
    public static final String lightAlias = "directionalLights";
    public static final long light = register("directionalLights");
    public final Seq<DirectionalLight> lights;

    public static boolean is(long mask) {
        return (mask & light) == mask;
    }

    public DirectionalLightsAttribute() {
        super(light);
        this.lights = new Seq(1);
    }

    public DirectionalLightsAttribute(DirectionalLightsAttribute copyFrom) {
        this();
        this.lights.addAll(copyFrom.lights);
    }

    public DirectionalLightsAttribute copy() {
        return new DirectionalLightsAttribute(this);
    }

    public int hashCode() {
        int result = super.hashCode();

        for(DirectionalLight light : this.lights) {
            result = 1229 * result + (light == null ? 0 : light.hashCode());
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
