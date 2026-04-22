package unity.content;

import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.Mesh;
import mindustry.content.Planets;
import mindustry.graphics.Pal;
import mindustry.graphics.g3d.HexMesh;
import mindustry.type.Planet;
import unity.assets.list.UnityShaders;
import unity.gen.UnityModels;
import unity.graphics.ColorMesh;
import unity.graphics.CompositeMesh;
import unity.graphics.UnityPal;
import unity.map.planets.ElectrodePlanetGenerator;
import unity.map.planets.MegalithPlanetGenerator;
import unity.util.GraphicUtils;

public class UnityPlanets {
    public static Planet electrode;
    public static Planet inert;
    public static Planet megalith;

    public static void load() {
        megalith = new Planet("megalith", Planets.sun, 1.0F, 3) {
            {
                this.generator = new MegalithPlanetGenerator();
                this.meshLoader = () -> new CompositeMesh(this, new Object[]{CompositeMesh.defMesh(this, 6), CompositeMesh.defShader(this), Blending.normal, GraphicUtils.copy((Mesh)UnityModels.megalithring.meshes.first()), new CompositeMesh.ShaderRef(UnityShaders.megalithRingShader, UnityShaders.megalithRingShader.cons(this)), Blending.additive});
                this.accessible = true;
                this.atmosphereColor = UnityPal.monolithAtmosphere;
                this.startSector = 200;
                this.atmosphereRadIn = 0.04F;
                this.atmosphereRadOut = 0.35F;
            }
        };
        electrode = new Planet("electrode", Planets.sun, 1.0F, 3) {
            {
                this.generator = new ElectrodePlanetGenerator();
                this.meshLoader = () -> new HexMesh(this, 6);
                this.accessible = true;
                this.atmosphereColor = Pal.surge;
                this.startSector = 30;
            }
        };
        inert = new Planet("inert", electrode, 0.5F) {
            {
                this.atmosphereColor = Color.white.cpy();
                this.accessible = false;
                this.meshLoader = () -> new ColorMesh(this, 3, (double)4.0F, 0.3, 1.7, 1.2, (double)1.0F, 0.9F, new Color[]{Color.valueOf("121211"), Color.valueOf("141414"), Color.valueOf("131313"), Color.valueOf("181617"), Color.valueOf("191415"), Color.valueOf("101111")});
            }
        };
    }
}
