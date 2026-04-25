package unity.entities.effects;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.gl.FrameBuffer;
import arc.math.Mathf;
import arc.util.pooling.Pools;
import mindustry.Vars;
import mindustry.entities.Effect;
import mindustry.gen.Drawc;
import mindustry.gen.EffectState;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;
import unity.assets.list.UnityShaders;

public class FragmentationShaderEffect extends Effect {
    public float fragOffset = 0.2F;
    public float heatOffset = 0.3F;
    public float windPower = -1.0F;

    public FragmentationShaderEffect(float lifetime) {
        this.lifetime = lifetime;
    }

    public void at(float x, float y, float rotation, Object data) {
        if (!Vars.headless) {
            FragEffectState e = (FragEffectState)Pools.obtain(FragEffectState.class, FragEffectState::new);
            e.x = x;
            e.y = y;
            e.rotation = rotation;
            e.lifetime = this.lifetime;
            e.type = this;
            e.data = data;
            if (data instanceof Drawc) {
                e.clipSize = ((Drawc)data).clipSize();
            } else {
                e.clipSize = this.clip;
            }

            e.add();
        }

    }

    static class FragEffectState extends EffectState {
        FragmentationShaderEffect type;
        float clipSize;

        public void draw() {
            if (this.data instanceof Drawc) {
                Drawc draw = (Drawc)this.data;
                Unit unit = draw instanceof Unit ? (Unit)draw : null;
                float z = 90.0F;
                if (unit != null) {
                    UnitType t = unit.type;
                    z = unit.elevation > 0.5F ? (t.lowAltitude ? 90.0F : 115.0F) : t.groundLayer + Mathf.clamp(t.hitSize / 4000.0F, 0.0F, 0.01F);
                }

                Draw.draw(z, () -> {
                    UnityShaders.FragmentationShader s = UnityShaders.fragmentShader;
                    if (unit != null) {
                        unit.hitTime = 0.0F;
                        s.direction.trns(this.rotation, this.type.windPower >= 0.0F ? this.type.windPower : unit.hitSize / 14.0F);
                        s.source.set(unit);
                        s.size = unit.hitSize / 4.0F;
                    } else {
                        s.source.set(this.x, this.y);
                        s.direction.trns(this.rotation, this.type.windPower >= 0.0F ? this.type.windPower : this.clipSize / 14.0F);
                        s.size = 0.0F;
                    }

                    float heat = this.type.heatOffset > 0.0F ? Mathf.curve(this.fin(), 0.0F, this.type.heatOffset) : 1.0F;
                    s.heatColor.set(Pal.lightFlame).lerp(Pal.darkFlame, heat);
                    s.fragProgress = Mathf.curve(this.fin(), this.type.fragOffset, 1.0F);
                    s.heatProgress = heat;
                    FrameBuffer buffer = UnityShaders.bufferAlt;
                    buffer.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
                    buffer.begin(Color.clear);
                    draw.draw();
                    buffer.end();
                    Draw.blit(buffer, s);
                });
            }

        }

        public float clipSize() {
            return this.clipSize * 2.0F;
        }
    }
}
