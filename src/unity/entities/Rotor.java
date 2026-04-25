package unity.entities;

import arc.Core;
import arc.graphics.g2d.TextureRegion;
import mindustry.io.JsonIO;

public class Rotor {
    public final String name;
    public TextureRegion bladeRegion;
    public TextureRegion bladeOutlineRegion;
    public TextureRegion bladeGhostRegion;
    public TextureRegion bladeShadeRegion;
    public TextureRegion topRegion;
    public boolean mirror;
    public float x;
    public float y;
    public float rotOffset = 0.0F;
    public float speed = 29.0F;
    public float shadeSpeed = 3.0F;
    public float ghostAlpha = 0.6F;
    public float shadowAlpha = 0.4F;
    public float bladeFade = 1.0F;
    public int bladeCount = 4;

    public Rotor(String name) {
        this.name = name;
    }

    public void load() {
        this.bladeRegion = Core.atlas.find(this.name + "-blade");
        this.bladeOutlineRegion = Core.atlas.find(this.name + "-blade-outline");
        this.bladeGhostRegion = Core.atlas.find(this.name + "-blade-ghost");
        this.bladeShadeRegion = Core.atlas.find(this.name + "-blade-shade");
        this.topRegion = Core.atlas.find(this.name + "-top");
    }

    public Rotor copy() {
        return (Rotor)JsonIO.copy(this, new Rotor(this.name));
    }
}
