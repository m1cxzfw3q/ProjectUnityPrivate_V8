package unity.v8;

import arc.graphics.Color;

public class UnitDecal {
    public String region = "error";
    public float x;
    public float y;
    public float rotation;
    public float layer = 116.0F;
    public float xScale = 1.0F;
    public float yScale = 1.0F;
    public Color color = Color.white;

    public UnitDecal(String region, float x, float y, float rotation, float layer, Color color) {
        this.region = region;
        this.x = x;
        this.y = y;
        this.rotation = rotation;
        this.layer = layer;
        this.color = color;
    }

    public UnitDecal() {

    }
}
