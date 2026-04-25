package unity.entities.effects;

import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.graphics.gl.FrameBuffer;
import arc.math.geom.Vec2;
import mindustry.Vars;
import mindustry.entities.EntityGroup;
import mindustry.game.EventType;
import mindustry.game.EventType.Trigger;
import mindustry.gen.Drawc;
import mindustry.gen.Rotc;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;
import unity.assets.list.UnityShaders;
import unity.gen.CutEffect;

public class CutEffects {
    private static final Vec2 tmp = new Vec2();
    private static final Vec2 tmp2 = new Vec2();
    private static TextureRegion white;
    private static final FrameBuffer buffer = new FrameBuffer();
    private static final float minCut = 10.0F;
    private static final float minFragments = 5.0F;
    public static EntityGroup<CutEffect> group = new EntityGroup(CutEffect.class, true, false);
    public float[] stencil;
    public float[] drawStencil;

    public static void cutUnit(Unit unit, float x, float y, float x2, float y2) {
        unit.remove();
    }

    public static void draw(CutEffect effect) {
        buffer.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
        Draw.draw(effect.z(), () -> {
            Drawc subject = effect.other;
            boolean rotate = subject instanceof Rotc;
            Rotc rot = rotate ? (Rotc)subject : null;
            float ox = effect.x - effect.originX;
            float oy = effect.y - effect.originY;
            float lx = subject.x();
            float ly = subject.y();
            float lr = rotate ? rot.rotation() : 0.0F;
            UnityShaders.stencilShader.stencilColor.set(Color.green);
            UnityShaders.stencilShader.heatColor.set(Pal.lightFlame).lerp(Pal.darkFlame, effect.fin());
            buffer.begin(UnityShaders.stencilShader.stencilColor);
            subject.trns(ox, oy);
            if (rotate) {
                tmp2.set(subject).sub(effect).rotate(effect.rotation).add(effect).sub(subject);
                subject.trns(tmp2);
                rot.rotation(rot.rotation() + effect.rotation);
            }

            subject.draw();
            Draw.color(UnityShaders.stencilShader.stencilColor);

            for(CutEffects stencil : effect.stencils) {
                stencil.draw(effect.x, effect.y, effect.rotation);
            }

            subject.set(lx, ly);
            if (rotate) {
                rot.rotation(lr);
            }

            buffer.end();
            Draw.blit(buffer, UnityShaders.stencilShader);
            Draw.reset();
        });
    }

    public void draw(float x, float y, float rotation) {
        for(int i = 0; i < this.stencil.length; i += 2) {
            tmp.set(this.stencil[i], this.stencil[i + 1]).rotate(rotation).add(x, y);
            this.drawStencil[i] = tmp.x;
            this.drawStencil[i + 1] = tmp.y;
        }

        if (white == null) {
            white = Core.atlas.white();
        }

        Draw.vert(white.texture, this.drawStencil, 0, this.drawStencil.length);
    }

    static {
        Events.on(EventType.ResetEvent.class, (e) -> group.clear());
        Events.run(Trigger.update, () -> {
            if (Vars.state.isPlaying()) {
                Vars.collisions.updatePhysics(group);
            }

        });
    }
}
