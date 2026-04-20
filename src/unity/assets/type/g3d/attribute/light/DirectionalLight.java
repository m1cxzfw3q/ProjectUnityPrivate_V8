package unity.assets.type.g3d.attribute.light;

import arc.graphics.Color;
import arc.math.geom.Vec3;

public class DirectionalLight extends BaseLight<DirectionalLight> {
    public final Vec3 direction = new Vec3();

    public DirectionalLight setDirection(float directionX, float directionY, float directionZ) {
        this.direction.set(directionX, directionY, directionZ);
        return this;
    }

    public DirectionalLight setDirection(Vec3 direction) {
        this.direction.set(direction);
        return this;
    }

    public DirectionalLight set(DirectionalLight copyFrom) {
        return this.set(copyFrom.color, copyFrom.direction);
    }

    public DirectionalLight set(Color color, Vec3 direction) {
        if (color != null) {
            this.color.set(color);
        }

        if (direction != null) {
            this.direction.set(direction).nor();
        }

        return this;
    }

    public DirectionalLight set(float r, float g, float b, Vec3 direction) {
        this.color.set(r, g, b, 1.0F);
        if (direction != null) {
            this.direction.set(direction).nor();
        }

        return this;
    }

    public DirectionalLight set(Color color, float dirX, float dirY, float dirZ) {
        if (color != null) {
            this.color.set(color);
        }

        this.direction.set(dirX, dirY, dirZ).nor();
        return this;
    }

    public DirectionalLight set(float r, float g, float b, float dirX, float dirY, float dirZ) {
        this.color.set(r, g, b, 1.0F);
        this.direction.set(dirX, dirY, dirZ).nor();
        return this;
    }

    public boolean equals(Object other) {
        boolean var10000;
        if (other instanceof DirectionalLight) {
            DirectionalLight light = (DirectionalLight)other;
            if (this.equals(light)) {
                var10000 = true;
                return var10000;
            }
        }

        var10000 = false;
        return var10000;
    }

    public boolean equals(DirectionalLight other) {
        return other != null && (other == this || this.color.equals(other.color) && this.direction.equals(other.direction));
    }
}
