package unity.entities.abilities;

import arc.audio.Sound;
import arc.func.Boolf;
import arc.util.Time;
import mindustry.entities.Effect;
import mindustry.gen.Sounds;
import mindustry.gen.Unit;
import unity.content.effects.SpecialFx;
import unity.gen.UnitySounds;
import unity.graphics.UnityPal;
import unity.mod.TimeStop;

public class TimeStopAbility extends BaseAbility {
    float duration;
    Sound timeStopSound;
    Sound timeStopSoundShort;
    Effect timeStopEffect;
    boolean update;

    public TimeStopAbility(Boolf<Unit> able, float duration, float rechargeTime) {
        super(able, true, true);
        this.timeStopSound = UnitySounds.stopTime;
        this.timeStopSoundShort = Sounds.none;
        this.timeStopEffect = SpecialFx.timeStop;
        this.update = true;
        this.useSlots = false;
        this.color = UnityPal.scarColor;
        this.duration = duration;
        this.rechargeTime = rechargeTime;
    }

    protected boolean shouldRecharge(Unit unit) {
        return !TimeStop.inTimeStop();
    }

    public void use(Unit unit, float x, float y) {
        if (this.update) {
            super.use(unit, x, y);
            if (!unit.isPlayer() && !TimeStop.inTimeStop()) {
                this.timeStopSoundShort.at(unit.x, unit.y);
                this.timeStopEffect.at(unit.x, unit.y);
                float delta = Time.delta;
                Time.delta = 3.0F;
                this.update = false;

                for(float i = 0.0F; i < this.duration; i += Time.delta) {
                    unit.update();
                }

                Time.delta = delta;
                this.update = true;
            } else {
                this.timeStopSound.at(unit.x, unit.y);
                TimeStop.addEntity(unit, this.duration);
                this.timeStopEffect.at(unit.x, unit.y);
            }

        }
    }
}
