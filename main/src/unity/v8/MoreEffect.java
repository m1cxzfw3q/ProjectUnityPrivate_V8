package unity.v8;

import arc.util.Time;
import mindustry.entities.Effect;

public class MoreEffect extends Effect {
    public int effects;
    public float delay;
    public Effect effect;

    public MoreEffect(int effects, float delay, Effect effect) {
        this.effects = effects;
        this.delay = delay;
        this.effect = effect;
    }

    @Override
    public void render(EffectContainer e) {
        for (int i = 0; i < effects; i++) {
            Time.run(delay * i, () -> effect.render(e));
        }
    }
}
