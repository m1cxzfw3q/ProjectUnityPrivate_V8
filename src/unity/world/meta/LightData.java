package unity.world.meta;

import arc.graphics.Color;

public class LightData {
    private boolean initialized;
    public int angle;
    public int length;
    public float strength;
    public Color color;

    public LightData() {
        this.initialized = false;
        this.angle = 0;
        this.length = 50;
        this.strength = 100.0F;
        this.color = Color.white;
    }

    public LightData(int length, Color color) {
        this.initialized = false;
        this.angle = 0;
        this.length = 50;
        this.strength = 100.0F;
        this.color = Color.white;
        this.length = length;
        this.color = color;
        this.initialized = true;
    }

    public LightData(int angle, float strength, int length, Color color) {
        this(length, color);
        this.angle = angle;
        this.strength = strength;
    }

    public LightData(LightData ld) {
        this(ld.angle, ld.strength, ld.length, ld.color);
    }

    public LightData set(int angle, float strength, int length, Color color) {
        this.angle = angle;
        this.strength = strength;
        this.length = length;
        this.color = color;
        this.initialized = true;
        return this;
    }

    public LightData set(LightData ld) {
        this.angle = ld.angle;
        this.strength = ld.strength;
        this.length = ld.length;
        this.color = ld.color;
        this.initialized = true;
        return this;
    }

    public LightData angle(int angle) {
        this.angle = angle;
        return this;
    }

    public LightData strength(float strength) {
        this.strength = strength;
        return this;
    }

    public LightData length(int length) {
        this.length = length;
        return this;
    }

    public LightData color(Color color) {
        this.color = color;
        return this;
    }

    public boolean isIntialized() {
        return this.initialized;
    }
}
