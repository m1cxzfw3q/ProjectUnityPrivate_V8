package unity.graphics;

import arc.graphics.Color;
import arc.math.Mathf;
import arc.math.geom.Vec3;
import arc.util.Tmp;
import arc.util.noise.Simplex;
import mindustry.graphics.Shaders;
import mindustry.graphics.g3d.HexMesh;
import mindustry.graphics.g3d.HexMesher;
import mindustry.type.Planet;

public class ColorMesh extends HexMesh {
    public ColorMesh(Planet planet, int divisions, final double octaves, final double persistence, final double scl, final double pow, final double mag, final float colorScale, final Color... colors) {
        super(planet, new HexMesher() {
            public float getHeight(Vec3 position) {
                return 0.0F;
            }

            public Color getColor(Vec3 position) {
                double height = Math.pow((double)Simplex.noise3d(0, octaves, persistence, scl, (double)position.x, (double)position.y, (double)position.z), pow) * mag;
                return Tmp.c1.set(colors[Mathf.clamp((int)(height * (double)colors.length), 0, colors.length - 1)]).mul(colorScale);
            }
        }, divisions, Shaders.planet);
    }
}
