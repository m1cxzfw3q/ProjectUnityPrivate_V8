package unity.graphics;

import arc.func.Cons;
import arc.graphics.Blending;
import arc.graphics.Mesh;
import arc.graphics.gl.Shader;
import arc.math.geom.Mat3D;
import arc.math.geom.Vec3;
import arc.struct.Seq;
import mindustry.graphics.Shaders;
import mindustry.graphics.g3d.MeshBuilder;
import mindustry.graphics.g3d.PlanetMesh;
import mindustry.graphics.g3d.PlanetParams;
import mindustry.type.Planet;

public class CompositeMesh extends PlanetMesh {
    public Seq<MeshComp> comps = new Seq();

    public CompositeMesh(Planet planet, Object... objects) {
        super(planet, (Mesh)null, (Shader)null);

        for(int i = 0; i < objects.length - 2; i += 3) {
            this.comps.add(new MeshComp((Mesh)objects[i], (ShaderRef)objects[i + 1], (Blending)objects[i + 2]));
        }

    }

    public static Mesh defMesh(Planet planet, int divisions) {
        return MeshBuilder.buildHex(planet.generator, divisions, false, planet.radius, 0.2F);
    }

    public static ShaderRef<Shaders.PlanetShader> defShader(Planet planet) {
        return new ShaderRef<Shaders.PlanetShader>(Shaders.planet, (s) -> {
            s.lightDir.set(planet.solarSystem.position).sub(planet.position).rotate(Vec3.Y, planet.getRotation()).nor();
            s.ambientColor.set(planet.solarSystem.lightColor);
        });
    }

    public void render(PlanetParams params, Mat3D projection, Mat3D transform) {
        for(MeshComp e : this.comps) {
            e.preRender();
        }

        for(MeshComp e : this.comps) {
            e.render(projection, transform);
        }

        Blending.normal.apply();
    }

    public static class MeshComp {
        public final Mesh mesh;
        public final ShaderRef<?> shader;
        public final Blending blend;

        MeshComp(Mesh mesh, ShaderRef<?> shader, Blending blend) {
            this.mesh = mesh;
            this.shader = shader;
            this.blend = blend;
        }

        public void preRender() {
            this.shader.apply();
        }

        public void render(Mat3D projection, Mat3D transform) {
            Shader s = this.shader.shader;
            s.bind();
            s.setUniformMatrix4("u_proj", projection.val);
            s.setUniformMatrix4("u_trans", transform.val);
            s.apply();
            this.blend.apply();
            this.mesh.render(s, 4);
        }
    }

    public static class ShaderRef<T extends Shader> {
        public final T shader;
        public final Cons<T> apply;

        public ShaderRef(T shader, Cons<T> apply) {
            this.shader = shader;
            this.apply = apply;
        }

        public void apply() {
            this.apply.get(this.shader);
        }
    }
}
