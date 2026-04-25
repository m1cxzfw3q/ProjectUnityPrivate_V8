package unity.entities.effects;

import arc.Core;
import arc.func.Cons;
import arc.func.Prov;
import arc.graphics.Color;
import arc.math.geom.Position;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.entities.Effect;
import mindustry.gen.EffectState;
import mindustry.gen.Posc;

public class CustomStateEffect extends Effect {
    public Prov<? extends EffectState> stateProvider;

    public CustomStateEffect(float lifetime, Cons<Effect.EffectContainer> container) {
        this(EffectState::create, lifetime, container);
    }

    public CustomStateEffect(Prov<? extends EffectState> prov, float lifetime, Cons<Effect.EffectContainer> container) {
        this(prov, lifetime, 50.0F, container);
    }

    public CustomStateEffect(Prov<? extends EffectState> prov, float lifetime, float clip, Cons<Effect.EffectContainer> container) {
        super(lifetime, clip, container);
        this.stateProvider = prov;
    }

    public void at(Position pos) {
        this.create(pos.getX(), pos.getY(), 0.0F, Color.white, (Object)null);
    }

    public void at(Position pos, boolean parentize) {
        this.create(pos.getX(), pos.getY(), 0.0F, Color.white, parentize ? pos : null);
    }

    public void at(Position pos, float rotation) {
        this.create(pos.getX(), pos.getY(), rotation, Color.white, (Object)null);
    }

    public void at(float x, float y) {
        this.create(x, y, 0.0F, Color.white, (Object)null);
    }

    public void at(float x, float y, float rotation) {
        this.create(x, y, rotation, Color.white, (Object)null);
    }

    public void at(float x, float y, float rotation, Color color) {
        this.create(x, y, rotation, color, (Object)null);
    }

    public void at(float x, float y, Color color) {
        this.create(x, y, 0.0F, color, (Object)null);
    }

    public void at(float x, float y, float rotation, Color color, Object data) {
        this.create(x, y, rotation, color, data);
    }

    public void at(float x, float y, float rotation, Object data) {
        this.create(x, y, rotation, Color.white, data);
    }

    protected void create(float x, float y, float rotation, Color color, Object data) {
        if (!Vars.headless && Core.settings.getBool("effects")) {
            if (Core.camera.bounds(Tmp.r1).overlaps(Tmp.r2.setCentered(x, y, this.clip))) {
                this.inst(x, y, rotation, color, data).add();
            }

        }
    }

    protected EffectState inst(float x, float y, float rotation, Color color, Object data) {
        EffectState e = (EffectState)this.stateProvider.get();
        e.effect = this;
        e.rotation = this.baseRotation + rotation;
        e.data = data;
        e.lifetime = this.lifetime;
        e.set(x, y);
        e.color.set(color);
        if (this.followParent && data instanceof Posc) {
            Posc p = (Posc)data;
            e.parent = p;
            e.rotWithParent = this.rotWithParent;
        }

        return e;
    }
}
