package unity.assets.type.g3d.attribute.light;

import arc.graphics.Color;
import arc.math.Mathf;
import arc.math.geom.Vec3;

public class PointLight extends BaseLight<PointLight> {
    public final Vec3 position = new Vec3();
    public float intensity;

    public PointLight setPosition(float positionX, float positionY, float positionZ) {
        this.position.set(positionX, positionY, positionZ);
        return this;
    }

    public PointLight setPosition(Vec3 position) {
        this.position.set(position);
        return this;
    }

    public PointLight setIntensity(float intensity) {
        this.intensity = intensity;
        return this;
    }

    public PointLight set(PointLight copyFrom) {
        return this.set(copyFrom.color, copyFrom.position, copyFrom.intensity);
    }

    public PointLight set(Color color, Vec3 position, float intensity) {
        if (color != null) {
            this.color.set(color);
        }

        if (position != null) {
            this.position.set(position);
        }

        this.intensity = intensity;
        return this;
    }

    public PointLight set(float r, float g, float b, Vec3 position, float intensity) {
        this.color.set(r, g, b, 1.0F);
        if (position != null) {
            this.position.set(position);
        }

        this.intensity = intensity;
        return this;
    }

    public PointLight set(Color color, float x, float y, float z, float intensity) {
        if (color != null) {
            this.color.set(color);
        }

        this.position.set(x, y, z);
        this.intensity = intensity;
        return this;
    }

    public PointLight set(float r, float g, float b, float x, float y, float z, float intensity) {
        this.color.set(r, g, b, 1.0F);
        this.position.set(x, y, z);
        this.intensity = intensity;
        return this;
    }

    public boolean equals(Object other) {
        boolean var10000;
        if (other instanceof PointLight) {
            PointLight light = (PointLight)other;
            if (this.equals(light)) {
                var10000 = true;
                return var10000;
            }
        }

        var10000 = false;
        return var10000;
    }

    public boolean equals(PointLight other) {
        return other != null && (other == this || this.color.equals(other.color) && this.position.equals(other.position) && Mathf.equal(this.intensity, other.intensity));
    }
}
