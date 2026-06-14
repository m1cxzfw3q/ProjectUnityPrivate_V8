package unity.world.blocks.defense.turrets;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.draw.DrawTurret;
import unity.util.*;

public class BigLaserTurret extends LaserTurret{
    public BigLaserTurret(String name){
        super(name);
        drawer = new DrawTurret("unity-block-") {
            @Override
            public void drawHeat(Turret block, TurretBuild build) {
                if(build.heat <= 0.00001f) return;

                float r = Interp.pow2Out.apply(build.heat);
                float g = Interp.pow3In.apply(build.heat) + ((1 - Interp.pow3In.apply(build.heat)) * 0.12f);
                float b = Utils.pow6In.apply(build.heat);
                float a = Interp.pow2Out.apply(build.heat);

                Tmp.c1.set(r, g, b, a);
                Draw.color(Tmp.c1);
                Draw.blend(Blending.additive);
                Draw.rect(heat, build.x + build.recoilOffset.x, build.y + build.recoilOffset.y, build.rotation - 90f);
                Draw.color();
                Draw.blend();
            }
        };
    }
}
