package unity.entities.effects;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.gl.FrameBuffer;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Position;
import arc.math.geom.Vec2;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.pooling.Pools;
import mindustry.Vars;
import mindustry.entities.Effect;
import mindustry.gen.Building;
import mindustry.gen.Drawc;
import mindustry.gen.EffectState;
import mindustry.gen.Unit;
import mindustry.gen.Velc;
import mindustry.graphics.Pal;
import unity.assets.list.UnityShaders;

public class VapourizeShaderEffect extends Effect {
    boolean updateVel = true;

    public VapourizeShaderEffect(float lifetime, float clipsize) {
        super(lifetime, clipsize, (e) -> {
        });
    }

    public void at(Position pos) {
        this.at(pos.getX(), pos.getY());
    }

    public void at(float x, float y) {
        this.at(x, y, 0.0F);
    }

    public void at(Position pos, float rotation) {
        this.at(pos.getX(), pos.getY(), rotation);
    }

    public void at(float x, float y, Color color) {
        this.at(x, y);
    }

    public void at(float x, float y, float rotation) {
        this.at(x, y, rotation, (Color)null, (Object)null);
    }

    public void at(float x, float y, float rotation, Color color) {
        this.at(x, y, rotation, (Color)null, (Object)null);
    }

    public void at(float x, float y, float rotation, Color color, Object data) {
        this.at(x, y, rotation, data);
    }

    public void at(float x, float y, float rotation, Object data) {
        if (!Vars.headless && Core.settings.getBool("effects")) {
            VapourizeShaderEffectState s = (VapourizeShaderEffectState)Pools.obtain(VapourizeShaderEffectState.class, () -> new VapourizeShaderEffectState());
            s.x = x;
            s.y = y;
            s.rotation = rotation;
            float l = this.lifetime;
            if (data instanceof Object[]) {
                Object[] d = data;
                s.datab = d[0];
                if (d.length >= 3) {
                    s.clipSize = rotation * 2.0F;
                    s.windScl = (Float)d[2];
                    l /= 2.0F;
                }

                data = d[1];
            }

            s.data = data;
            s.lifetime = l;
            s.add();
        }
    }

    public VapourizeShaderEffect updateVel(boolean v) {
        this.updateVel = v;
        return this;
    }

    public class VapourizeShaderEffectState extends EffectState {
        float windScl = -1.0F;
        float clipSize;
        Object datab;

        public void reset() {
            super.reset();
            this.datab = null;
            this.windScl = -1.0F;
            this.clipSize = 0.0F;
        }

        public void update() {
            super.update();
            if (VapourizeShaderEffect.this.updateVel) {
                Object var2 = this.data;
                if (var2 instanceof Velc) {
                    Velc v = (Velc)var2;
                    v.move(v.vel());
                    v.vel().scl(1.0F - v.drag() * Time.delta);
                }
            }

        }

        public void draw() {
            Object c = this.data;
            if (c instanceof Drawc) {
                Drawc draw = (Drawc)c;
                float c = this.windScl > 0.0F ? this.windScl : draw.clipSize() / 8.0F;
                Draw.z(90.0F);
                Draw.blend(Blending.additive);
                Draw.mixcol(Color.red, 1.0F);
                Draw.alpha(this.fout());
                Object var6 = this.data;
                if (var6 instanceof Unit) {
                    Unit u = (Unit)var6;
                    u.hitTime = 0.0F;
                    Draw.rect(u.type.fullIcon, u, u.rotation - 90.0F);
                } else {
                    var6 = this.data;
                    if (var6 instanceof Building) {
                        Building b = (Building)var6;
                        Draw.rect(b.block.region, b.x, b.y);
                    }
                }

                Draw.blend();
                if (Vars.renderer.animateShields) {
                    Draw.draw(Draw.z() + 0.001F, () -> {
                        float in = Mathf.clamp(this.fin() * 2.0F);
                        UnityShaders.VapourizeShader s = UnityShaders.vapourizeShader;
                        FrameBuffer buffer = UnityShaders.bufferAlt;
                        buffer.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
                        s.toColor.set(Pal.rubble);
                        s.colorProgress = Interp.pow2In.apply(Mathf.clamp(in * 1.25F));
                        s.progress = Interp.pow2In.apply(in);
                        Vec2 var10000 = s.windSource;
                        Object p$temp = this.datab;
                        Object var10001;
                        if (p$temp instanceof Position) {
                            Position p = (Position)p$temp;
                            var10001 = p;
                        } else {
                            var10001 = draw;
                        }

                        var10000.set((Position)var10001);
                        s.fragProgress = Interp.pow3In.apply(in) * c;
                        s.size = c;
                        buffer.begin(Color.clear);
                        draw.draw();
                        buffer.end();
                        buffer.blit(UnityShaders.vapourizeShader);
                    });
                }
            } else if (Vars.renderer.animateShields) {
                c = this.data;
                if (c instanceof Building[]) {
                    Building[] drwA = (Building[])c;
                    if (this.datab != null) {
                        Draw.draw(30.001F, () -> {
                            float in = this.fin();
                            UnityShaders.VapourizeShader s = UnityShaders.vapourizeShader;
                            FrameBuffer buffer = UnityShaders.bufferAlt;
                            s.toColor.set(Pal.rubble);
                            s.colorProgress = Interp.pow2In.apply(Mathf.clamp(in * 1.25F));
                            s.progress = Interp.pow2In.apply(in);
                            s.windSource.set((Position)this.datab);
                            s.fragProgress = Interp.pow3In.apply(in) * this.windScl;
                            s.size = 0.0F;
                            buffer.begin(Color.clear);

                            for(Building d : drwA) {
                                if (Core.camera.bounds(Tmp.r1).overlaps(Tmp.r2.setCentered(d.x(), d.y(), d.block.clipSize + s.fragProgress * 2.0F))) {
                                    d.draw();
                                }
                            }

                            buffer.end();
                            buffer.blit(UnityShaders.vapourizeShader);
                        });
                    }
                }
            }

            Draw.reset();
        }

        public float clipSize() {
            return Math.max(VapourizeShaderEffect.this.clip, this.clipSize);
        }
    }
}
