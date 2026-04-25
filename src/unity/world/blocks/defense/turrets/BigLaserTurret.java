package unity.world.blocks.defense.turrets;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.g2d.Draw;
import arc.math.Interp;
import arc.util.Tmp;
import mindustry.world.blocks.defense.turrets.LaserTurret;
import unity.util.Utils;

public class BigLaserTurret extends LaserTurret {
    public BigLaserTurret(String name) {
        super(name);
        this.heatDrawer = (tile) -> {
            if (!(tile.heat <= 1.0E-5F)) {
                float r = Interp.pow2Out.apply(tile.heat);
                float g = Interp.pow3In.apply(tile.heat) + (1.0F - Interp.pow3In.apply(tile.heat)) * 0.12F;
                float b = Utils.pow6In.apply(tile.heat);
                float a = Interp.pow2Out.apply(tile.heat);
                Tmp.c1.set(r, g, b, a);
                Draw.color(Tmp.c1);
                Draw.blend(Blending.additive);
                Draw.rect(this.heatRegion, tile.x + this.tr2.x, tile.y + this.tr2.y, tile.rotation - 90.0F);
                Draw.color();
                Draw.blend();
            }
        };
    }

    public void load() {
        super.load();
        this.baseRegion = Core.atlas.find("unity-block-" + this.size);
    }
}
