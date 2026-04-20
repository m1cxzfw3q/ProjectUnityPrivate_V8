package unity.assets.list;

import arc.Core;
import arc.Events;
import arc.files.Fi;
import arc.func.Boolp;
import arc.func.Cons;
import arc.graphics.Color;
import arc.graphics.Texture;
import arc.graphics.VertexAttribute;
import arc.graphics.Texture.TextureFilter;
import arc.graphics.Texture.TextureWrap;
import arc.graphics.g2d.Draw;
import arc.graphics.g3d.Camera3D;
import arc.graphics.gl.FrameBuffer;
import arc.graphics.gl.Shader;
import arc.math.geom.Vec2;
import arc.math.geom.Vec3;
import arc.struct.LongMap;
import arc.util.Disposable;
import arc.util.Structs;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.game.EventType.Trigger;
import mindustry.type.Planet;
import unity.assets.type.g3d.Models;
import unity.assets.type.g3d.Renderable;
import unity.assets.type.g3d.attribute.Environment;
import unity.assets.type.g3d.attribute.Material;
import unity.assets.type.g3d.attribute.light.DirectionalLight;
import unity.assets.type.g3d.attribute.type.BlendingAttribute;
import unity.assets.type.g3d.attribute.type.ColorAttribute;
import unity.assets.type.g3d.attribute.type.FloatAttribute;
import unity.assets.type.g3d.attribute.type.TextureAttribute;
import unity.assets.type.g3d.attribute.type.light.DirectionalLightsAttribute;
import unity.assets.type.g3d.attribute.type.light.PointLightsAttribute;

public class UnityShaders {
    public static StencilShader stencilShader;
    public static MegalithRingShader megalithRingShader;
    public static Graphics3DShaderProvider graphics3DProvider;
    public static VapourizeShader vapourizeShader;
    public static FragmentationShader fragmentShader;
    public static FrameBuffer bufferAlt;
    protected static FrameBuffer buffer;

    public static void load() {
        if (!Vars.headless) {
            buffer = new FrameBuffer();
            bufferAlt = new FrameBuffer();
            CondShader[] conds = new CondShader[0];
            vapourizeShader = new VapourizeShader();
            fragmentShader = new FragmentationShader();
            stencilShader = new StencilShader();
            megalithRingShader = new MegalithRingShader();
            graphics3DProvider = new Graphics3DShaderProvider();
            if (conds.length != 0) {
                for(int i = 0; i < conds.length; ++i) {
                    CondShader shader = conds[i];
                    shader.layer = 127.0F + (float)i * (1.0F / (float)conds.length - 0.01F);
                }

                float range = 1.0F / (float)conds.length / 2.0F;
                Events.run(Trigger.drawOver, () -> {
                    buffer.resize(Core.graphics.getWidth(), Core.graphics.getHeight());

                    for(CondShader shader : conds) {
                        if (shader.apply.get()) {
                            Draw.drawRange(shader.getLayer() + range / 2.0F, range, () -> buffer.begin(Color.clear), () -> {
                                buffer.end();
                                Draw.blit((Texture)buffer.getTexture(), shader);
                            });
                        }
                    }

                });
            }
        }
    }

    public static class CondShader extends Shader {
        public final Boolp apply;
        protected float layer;

        public CondShader(Fi vert, Fi frag, Boolp apply) {
            super(vert, frag);
            this.apply = apply;
        }

        public float getLayer() {
            return this.layer;
        }
    }

    public static class FragmentationShader extends Shader {
        public Texture noise;
        public Vec2 source = new Vec2();
        public Vec2 direction = new Vec2();
        public Color heatColor = new Color();
        public float heatProgress;
        public float fragProgress;
        public float size;

        public FragmentationShader() {
            super(Core.files.internal("shaders/screenspace.vert"), Vars.tree.get("shaders/fragmentation.frag"));
            if (this.noise == null) {
                Fi path = Vars.tree.get("shaders/fragmentnoise.png");
                this.noise = new Texture(path);
                this.noise.setFilter(TextureFilter.linear);
                this.noise.setWrap(TextureWrap.repeat);
            }

        }

        public void apply() {
            this.noise.bind(1);
            ((Texture)UnityShaders.bufferAlt.getTexture()).bind(0);
            this.setUniformi("u_noise", 1);
            this.setUniformf("u_texsize", Core.camera.width, Core.camera.height);
            this.setUniformf("u_invsize", 1.0F / Core.camera.width, 1.0F / Core.camera.height);
            this.setUniformf("u_campos", Core.camera.position.x - Core.camera.width / 2.0F, Core.camera.position.y - Core.camera.height / 2.0F);
            this.setUniformf("u_blastpos", this.source);
            this.setUniformf("u_blastforce", this.direction);
            this.setUniformf("heatcolor", this.heatColor);
            this.setUniformf("heatprogress", this.heatProgress);
            this.setUniformf("fragprogress", this.fragProgress);
            this.setUniformf("size", this.size);
        }
    }

    public static class VapourizeShader extends Shader {
        public Texture noise;
        public Vec2 windSource = new Vec2();
        public Color toColor = new Color();
        public float progress;
        public float colorProgress;
        public float fragProgress;
        public float size;

        public VapourizeShader() {
            super(Core.files.internal("shaders/screenspace.vert"), Vars.tree.get("shaders/vapourize.frag"));
            if (this.noise == null) {
                Fi path = Vars.tree.get("shaders/vapourizenoise.png");
                this.noise = new Texture(path);
                this.noise.setWrap(TextureWrap.mirroredRepeat);
            }

        }

        public void apply() {
            this.noise.bind(1);
            ((Texture)UnityShaders.bufferAlt.getTexture()).bind(0);
            this.setUniformi("u_noise", 1);
            this.setUniformf("position", this.windSource);
            this.setUniformf("progress", this.progress);
            this.setUniformf("fragprogress", this.fragProgress);
            this.setUniformf("tocolor", this.toColor);
            this.setUniformf("colorprog", this.colorProgress);
            this.setUniformf("size", this.size);
            this.setUniformf("u_texsize", Core.camera.width, Core.camera.height);
            this.setUniformf("u_invsize", 1.0F / Core.camera.width, 1.0F / Core.camera.height);
            this.setUniformf("u_offset", Core.camera.position.x - Core.camera.width / 2.0F, Core.camera.position.y - Core.camera.height / 2.0F);
        }
    }

    public static class StencilShader extends Shader {
        public Color stencilColor = new Color();
        public Color heatColor = new Color();

        public StencilShader() {
            super(Core.files.internal("shaders/screenspace.vert"), Vars.tree.get("shaders/unitystencil.frag"));
        }

        public void apply() {
            this.setUniformf("stencilcolor", this.stencilColor);
            this.setUniformf("heatcolor", this.heatColor);
            this.setUniformf("u_invsize", 1.0F / Core.camera.width, 1.0F / Core.camera.height);
        }
    }

    public static class PlanetObjectShader extends Shader {
        public Vec3 lightDir = (new Vec3(1.0F, 1.0F, 1.0F)).nor();
        public Color ambientColor;
        public Vec3 camDir;

        public PlanetObjectShader(Fi vert, Fi frag) {
            super(vert, frag);
            this.ambientColor = Color.white.cpy();
            this.camDir = new Vec3();
        }

        public void apply() {
            this.camDir.set(Vars.renderer.planets.cam.direction).rotate(Vec3.Y, Vars.ui.planet.state.planet.getRotation());
            this.setUniformf("u_lightdir", this.lightDir);
            this.setUniformf("u_ambientColor", this.ambientColor.r, this.ambientColor.g, this.ambientColor.b);
            this.setUniformf("u_camdir", this.camDir);
        }

        public <T extends PlanetObjectShader> Cons<T> cons(Planet planet) {
            return (s) -> {
                s.lightDir.set(planet.solarSystem.position).sub(planet.position).rotate(Vec3.Y, planet.getRotation()).nor();
                s.ambientColor.set(planet.solarSystem.lightColor);
            };
        }
    }

    public static class MegalithRingShader extends PlanetObjectShader {
        protected final Texture texture;

        public MegalithRingShader() {
            super(Vars.tree.get("shaders/megalithring.vert"), Vars.tree.get("shaders/megalithring.frag"));
            this.texture = new Texture(Vars.tree.get("models/megalithring.png"));
            this.texture.setFilter(TextureFilter.linear);
            this.texture.setWrap(TextureWrap.repeat);
        }

        public void apply() {
            super.apply();
            this.texture.bind(1);
            ((Texture)Vars.renderer.effectBuffer.getTexture()).bind(0);
            this.setUniformi("u_ringTexture", 1);
        }
    }

    public static class Graphics3DShaderProvider implements Disposable {
        protected final String vertSource;
        protected final String fragSource;
        protected LongMap<Graphics3DShader> shaders = new LongMap();

        public Graphics3DShaderProvider() {
            this.vertSource = Vars.tree.get("shaders/g3d.vert").readString();
            this.fragSource = Vars.tree.get("shaders/g3d.frag").readString();
        }

        public Graphics3DShader get(Renderable render) {
            return this.get(render.material.mask() | Models.environment.mask(), render.meshPart.mesh.attributes);
        }

        public Graphics3DShader get(long mask, VertexAttribute[] attributes) {
            if (!this.shaders.containsKey(mask)) {
                String prefix = "\n";
                if (Structs.indexOf(attributes, VertexAttribute.color) != -1) {
                    prefix = prefix + this.define("color");
                }

                if (Models.environment.mask() != 0L) {
                    prefix = prefix + this.define("lighting");
                }

                if ((mask & ColorAttribute.ambientLight) != 0L) {
                    prefix = prefix + this.define("ambientLight");
                }

                if ((mask & DirectionalLightsAttribute.light) != 0L) {
                    prefix = prefix + this.defineRaw("numDirectionalLights " + ((DirectionalLightsAttribute)Models.environment.get(DirectionalLightsAttribute.light)).lights.size);
                } else {
                    prefix = prefix + this.defineRaw("numDirectionalLights 0");
                }

                if ((mask & PointLightsAttribute.light) != 0L) {
                    prefix = prefix + this.defineRaw("numPointLights " + ((PointLightsAttribute)Models.environment.get(PointLightsAttribute.light)).lights.size);
                } else {
                    prefix = prefix + this.defineRaw("numPointLights 0");
                }

                if ((mask & TextureAttribute.diffuse) != 0L) {
                    prefix = prefix + this.define("diffuseTexture");
                }

                if ((mask & ColorAttribute.diffuse) != 0L) {
                    prefix = prefix + this.define("diffuseColor");
                }

                if ((mask & TextureAttribute.specular) != 0L) {
                    prefix = prefix + this.define("specularTexture");
                }

                if ((mask & ColorAttribute.specular) != 0L) {
                    prefix = prefix + this.define("specularColor");
                }

                if ((mask & TextureAttribute.emissive) != 0L) {
                    prefix = prefix + this.define("emissiveTexture");
                }

                if ((mask & ColorAttribute.emissive) != 0L) {
                    prefix = prefix + this.define("emissiveColor");
                }

                if ((mask & FloatAttribute.shininess) != 0L) {
                    prefix = prefix + this.define("shininess");
                }

                if ((mask & FloatAttribute.alphaTest) != 0L) {
                    prefix = prefix + this.define("alphaTest");
                }

                this.shaders.put(mask, new Graphics3DShader(prefix + this.vertSource, prefix + this.fragSource));
            }

            return (Graphics3DShader)this.shaders.get(mask);
        }

        public String define(String alias) {
            return "#define " + alias + "Flag\n";
        }

        public String defineRaw(String alias) {
            return "#define " + alias + "\n";
        }

        public void dispose() {
            LongMap.Entries<Graphics3DShader> it = this.shaders.entries();

            while(it.hasNext) {
                ((Graphics3DShader)it.next().value).dispose();
                it.remove();
            }

        }
    }

    public static class Graphics3DShader extends Shader {
        protected Graphics3DShader(String vertexShader, String fragmentShader) {
            super(vertexShader, fragmentShader);
        }

        public void apply(Renderable render) {
            Camera3D camera = Models.camera;
            Material material = render.material;
            Environment env = Models.environment;
            this.setUniformMatrix4("u_proj", camera.combined.val);
            this.setUniformMatrix4("u_trans", render.worldTransform.val);
            this.setUniformf("u_camPos", camera.position.x, camera.position.y, camera.position.z, 1.1881F / (camera.far * camera.far));
            this.setUniformf("u_res", camera.width, camera.height);
            this.setUniformf("u_scl", (float)Core.graphics.getWidth() / camera.width);
            this.setUniformf("u_zscl", 2.0415F);
            float[] mval = Tmp.m1.val;
            float[] wval = render.worldTransform.val;
            mval[0] = wval[0];
            mval[1] = wval[1];
            mval[2] = wval[2];
            mval[3] = wval[4];
            mval[4] = wval[5];
            mval[5] = wval[6];
            mval[6] = wval[8];
            mval[7] = wval[9];
            mval[8] = wval[10];
            this.setUniformMatrix("u_normalMatrix", Tmp.m1.inv().transpose());
            BlendingAttribute blend = (BlendingAttribute)material.get(BlendingAttribute.blend);
            this.setUniformf("u_opacity", blend != null ? blend.opacity : 1.0F);
            FloatAttribute shine = (FloatAttribute)material.get(FloatAttribute.shininess);
            if (shine != null) {
                this.setUniformf("u_shininess", shine.value);
            }

            TextureAttribute diff = (TextureAttribute)material.get(TextureAttribute.diffuse);
            ColorAttribute diffCol = (ColorAttribute)material.get(ColorAttribute.diffuse);
            if (diffCol != null) {
                this.setUniformf("u_diffuseColor", diffCol.color);
            }

            if (diff != null) {
                this.setUniformi("u_diffuseTexture", Models.bind(diff, 6));
                this.setUniformf("u_diffuseUVTransform", diff.offsetU, diff.offsetV, diff.scaleU, diff.scaleV);
            }

            TextureAttribute spec = (TextureAttribute)material.get(TextureAttribute.specular);
            ColorAttribute specCol = (ColorAttribute)material.get(ColorAttribute.specular);
            if (specCol != null) {
                this.setUniformf("u_specularColor", specCol.color);
            }

            if (spec != null) {
                this.setUniformi("u_specularTexture", Models.bind(spec, 5));
                this.setUniformf("u_specularUVTransform", spec.offsetU, spec.offsetV, spec.scaleU, spec.scaleV);
            }

            TextureAttribute em = (TextureAttribute)material.get(TextureAttribute.emissive);
            ColorAttribute emCol = (ColorAttribute)material.get(ColorAttribute.emissive);
            if (emCol != null) {
                this.setUniformf("u_emissiveColor", emCol.color);
            }

            if (em != null) {
                this.setUniformi("u_emissiveTexture", Models.bind(em, 4));
                this.setUniformf("u_emissiveUVTransform", em.offsetU, em.offsetV, em.scaleU, em.scaleV);
            }

            TextureAttribute ref = (TextureAttribute)material.get(TextureAttribute.reflection);
            ColorAttribute refCol = (ColorAttribute)material.get(ColorAttribute.reflection);
            if (refCol != null) {
                this.setUniformf("u_reflectionColor", refCol.color);
            }

            if (ref != null) {
                this.setUniformi("u_reflectionTexture", Models.bind(ref, 3));
                this.setUniformf("u_reflectionUVTransform", ref.offsetU, ref.offsetV, ref.scaleU, ref.scaleV);
            }

            TextureAttribute am = (TextureAttribute)material.get(TextureAttribute.ambient);
            ColorAttribute amCol = (ColorAttribute)material.get(ColorAttribute.ambient);
            if (amCol != null) {
                this.setUniformf("u_ambientColor", amCol.color);
            }

            if (am != null) {
                this.setUniformi("u_ambientTexture", Models.bind(am, 2));
                this.setUniformf("u_ambientUVTransform", am.offsetU, am.offsetV, am.scaleU, am.scaleV);
            }

            TextureAttribute nor = (TextureAttribute)material.get(TextureAttribute.normal);
            if (nor != null) {
                this.setUniformi("u_normalTexture", Models.bind(nor, 1));
                this.setUniformf("u_normalUVTransform", nor.offsetU, nor.offsetV, nor.scaleU, nor.scaleV);
            }

            ((Texture)Vars.renderer.effectBuffer.getTexture()).bind(0);
            ColorAttribute aml = (ColorAttribute)env.get(ColorAttribute.ambientLight);
            if (aml != null) {
                this.setUniformf("u_ambientLight", aml.color);
            }

            DirectionalLightsAttribute dirl = (DirectionalLightsAttribute)env.get(DirectionalLightsAttribute.light);
            if (dirl != null) {
                for(int i = 0; i < dirl.lights.size; ++i) {
                    DirectionalLight light = (DirectionalLight)dirl.lights.get(i);
                    this.setUniformf("u_dirLights[" + i + "].color", light.color);
                    this.setUniformf("u_dirLights[" + i + "].direction", light.direction);
                }
            }

        }
    }
}
