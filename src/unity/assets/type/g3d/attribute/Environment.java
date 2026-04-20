package unity.assets.type.g3d.attribute;

import arc.struct.Seq;
import unity.assets.type.g3d.attribute.light.BaseLight;
import unity.assets.type.g3d.attribute.light.DirectionalLight;
import unity.assets.type.g3d.attribute.light.PointLight;
import unity.assets.type.g3d.attribute.light.ShadowMap;
import unity.assets.type.g3d.attribute.light.SpotLight;
import unity.assets.type.g3d.attribute.type.light.DirectionalLightsAttribute;
import unity.assets.type.g3d.attribute.type.light.PointLightsAttribute;
import unity.assets.type.g3d.attribute.type.light.SpotLightsAttribute;

public class Environment extends Attributes {
    public ShadowMap shadowMap;

    public Environment add(BaseLight<?>... lights) {
        for(BaseLight<?> light : lights) {
            this.add(light);
        }

        return this;
    }

    public Environment add(Seq<BaseLight<?>> lights) {
        for(BaseLight<?> light : lights) {
            this.add(light);
        }

        return this;
    }

    public Environment add(BaseLight<?> light) {
        if (light instanceof DirectionalLight) {
            DirectionalLight l = (DirectionalLight)light;
            this.add(l);
        } else if (light instanceof PointLight) {
            PointLight l = (PointLight)light;
            this.add(l);
        } else {
            if (!(light instanceof SpotLight)) {
                throw new IllegalArgumentException("Unknown light type");
            }

            SpotLight l = (SpotLight)light;
            this.add(l);
        }

        return this;
    }

    public Environment add(DirectionalLight light) {
        DirectionalLightsAttribute dirLights = (DirectionalLightsAttribute)this.get(DirectionalLightsAttribute.light);
        if (dirLights == null) {
            this.set(dirLights = new DirectionalLightsAttribute());
        }

        dirLights.lights.add(light);
        return this;
    }

    public Environment add(PointLight light) {
        PointLightsAttribute pointLights = (PointLightsAttribute)this.get(PointLightsAttribute.light);
        if (pointLights == null) {
            this.set(pointLights = new PointLightsAttribute());
        }

        pointLights.lights.add(light);
        return this;
    }

    public Environment add(SpotLight light) {
        SpotLightsAttribute spotLights = (SpotLightsAttribute)this.get(SpotLightsAttribute.light);
        if (spotLights == null) {
            this.set(spotLights = new SpotLightsAttribute());
        }

        spotLights.lights.add(light);
        return this;
    }

    public Environment remove(BaseLight<?>... lights) {
        for(BaseLight<?> light : lights) {
            this.remove(light);
        }

        return this;
    }

    public Environment remove(Seq<BaseLight<?>> lights) {
        for(BaseLight<?> light : lights) {
            this.remove(light);
        }

        return this;
    }

    public Environment remove(BaseLight<?> light) {
        if (light instanceof DirectionalLight) {
            DirectionalLight l = (DirectionalLight)light;
            this.remove(l);
        } else if (light instanceof PointLight) {
            PointLight l = (PointLight)light;
            this.remove(l);
        } else {
            if (!(light instanceof SpotLight)) {
                throw new IllegalArgumentException("Unknown light type");
            }

            SpotLight l = (SpotLight)light;
            this.remove(l);
        }

        return this;
    }

    public Environment remove(DirectionalLight light) {
        if (this.has(DirectionalLightsAttribute.light)) {
            DirectionalLightsAttribute dirLights = (DirectionalLightsAttribute)this.get(DirectionalLightsAttribute.light);
            dirLights.lights.remove(light, false);
            if (dirLights.lights.size == 0) {
                this.remove(DirectionalLightsAttribute.light);
            }
        }

        return this;
    }

    public Environment remove(PointLight light) {
        if (this.has(PointLightsAttribute.light)) {
            PointLightsAttribute pointLights = (PointLightsAttribute)this.get(PointLightsAttribute.light);
            pointLights.lights.remove(light, false);
            if (pointLights.lights.size == 0) {
                this.remove(PointLightsAttribute.light);
            }
        }

        return this;
    }

    public Environment remove(SpotLight light) {
        if (this.has(SpotLightsAttribute.light)) {
            SpotLightsAttribute spotLights = (SpotLightsAttribute)this.get(SpotLightsAttribute.light);
            spotLights.lights.remove(light, false);
            if (spotLights.lights.size == 0) {
                this.remove(SpotLightsAttribute.light);
            }
        }

        return this;
    }
}
