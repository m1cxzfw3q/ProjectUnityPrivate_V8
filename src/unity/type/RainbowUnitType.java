package unity.type;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.util.Time;
import mindustry.gen.Unit;

public class RainbowUnitType extends UnityUnitType {
    private static final Color tmpColor = new Color();
    public int segments = 6;
    public float offset = 15.0F;
    public TextureRegion[] rainbowRegions;
    public TextureRegion trailRegion;

    public RainbowUnitType(String name) {
        super(name);
    }

    public void load() {
        super.load();
        this.trailRegion = Core.atlas.find(this.name + "-trail");
        this.rainbowRegions = new TextureRegion[this.segments];

        for(int i = 0; i < this.segments; ++i) {
            this.rainbowRegions[i] = Core.atlas.find(this.name + "-rainbow-" + (i + 1));
        }

    }

    public void drawBody(Unit unit) {
        super.drawBody(unit);

        for(int i = 0; i < this.segments; ++i) {
            Draw.color(tmpColor.set(1.0F, 0.0F, 0.0F, 1.0F).shiftHue(Time.time + this.offset * (float)i));
            Draw.rect(this.rainbowRegions[i], unit.x, unit.y, unit.rotation - 90.0F);
        }

        Draw.reset();
    }
}
