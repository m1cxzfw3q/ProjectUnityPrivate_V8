package unity.entities.abilities;

import arc.func.Boolf;
import arc.graphics.Color;
import arc.util.Time;
import mindustry.Vars;
import mindustry.entities.Effect;
import mindustry.entities.abilities.Ability;
import mindustry.entities.units.UnitController;
import mindustry.gen.Player;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;
import unity.Unity;
import unity.content.UnityFx;
import unity.mod.TapHandler;

public abstract class BaseAbility extends Ability implements TapHandler.TapListener {
    public boolean useSlots = true;
    public boolean interactive = false;
    public boolean useTap = false;
    public int slots = 3;
    public int slot = 0;
    public Effect slotEffect;
    public Color color;
    public Effect waitEffect;
    public boolean chargeVisible;
    public float rechargeTime;
    public float rechargeProgress;
    public Effect rechargeEffect;
    public float delayTime;
    public float delayProgress;
    public Effect delayEffect;
    protected Boolf<Unit> able;
    protected boolean waited;
    protected boolean added;
    protected UnitController controller;

    protected BaseAbility(Boolf<Unit> able, boolean interactive, boolean useTap) {
        this.color = Pal.lancerLaser;
        this.waitEffect = UnityFx.waitEffect;
        this.chargeVisible = true;
        this.rechargeTime = 60.0F;
        this.rechargeProgress = 0.0F;
        this.rechargeEffect = UnityFx.ringFx;
        this.delayTime = 30.0F;
        this.delayProgress = 0.0F;
        this.delayEffect = UnityFx.smallRingFx;
        this.added = false;
        this.able = able;
        this.interactive = interactive;
        this.useTap = useTap;
    }

    public boolean able(Unit unit) {
        return unit.isPlayer() && this.useTap ? false : this.able.get(unit);
    }

    public void tap(Player player, float x, float y) {
        Unit unit = player.unit();
        if (unit.isValid() && unit.controller() == this.controller) {
            if (this.useSlots && this.delayProgress >= this.delayTime) {
                this.use(unit, x, y);
                return;
            }

            if (this.rechargeProgress >= this.rechargeTime) {
                this.use(unit, x, y);
                return;
            }

            this.hold(unit);
        }

    }

    public void use(Unit unit) {
        this.use(unit, Float.NaN, Float.NaN);
    }

    public void use(Unit unit, float x, float y) {
        if (this.useSlots) {
            this.delayProgress = 0.0F;
            this.waited = false;
            ++this.slot;
        } else {
            this.rechargeProgress = 0.0F;
        }

    }

    public void hold(Unit unit) {
        if (this.chargeVisible || unit.isPlayer() && unit.getPlayer() == Vars.player) {
            this.waitEffect.at(unit.x, unit.y, unit.rotation, this.color, new WaitEffectData(unit));
        }

    }

    public void update(Unit unit) {
        if (this.useTap && this.controller != unit.controller()) {
            if (unit.getPlayer() == Vars.player) {
                if (!this.added) {
                    Unity.tap.addListener(this);
                    this.added = true;
                }
            } else if (this.added) {
                Unity.tap.removeListener(this);
                this.added = false;
            }

            this.controller = unit.controller();
        }

        if (this.interactive) {
            this.updateInteractive(unit);
        } else {
            this.updatePassive(unit);
        }

    }

    public void updateInteractive(Unit unit) {
        if (this.useSlots) {
            if (this.slot >= this.slots) {
                if (this.shouldRecharge(unit)) {
                    this.rechargeProgress = Math.min(this.rechargeProgress + Time.delta, this.rechargeTime);
                }

                if (this.rechargeProgress >= this.rechargeTime) {
                    this.delayProgress = this.delayTime;
                    this.rechargeProgress = 0.0F;
                    this.slot = 0;
                    if (this.chargeVisible || unit.isPlayer() && unit.getPlayer() == Vars.player) {
                        this.rechargeEffect.at(unit.x, unit.y, 0.0F, this.color, unit);
                    }
                }
            } else {
                this.delayProgress = Math.min(this.delayProgress + Time.delta, this.delayTime);
                if (this.delayProgress >= this.delayTime) {
                    if (!this.waited && (this.chargeVisible || unit.isPlayer() && unit.getPlayer() == Vars.player)) {
                        this.waited = true;
                        this.delayEffect.at(unit.x, unit.y, 0.0F, this.color, unit);
                    }

                    if (this.able(unit)) {
                        this.delayProgress = 0.0F;
                        ++this.slot;
                        this.use(unit);
                    }
                }
            }
        } else {
            if (this.shouldRecharge(unit)) {
                this.rechargeProgress = Math.min(this.rechargeProgress + Time.delta, this.rechargeTime);
            }

            if (this.rechargeProgress >= this.rechargeTime && this.able(unit)) {
                this.rechargeProgress = 0.0F;
                this.use(unit);
            }
        }

    }

    public void updatePassive(Unit unit) {
    }

    protected boolean shouldRecharge(Unit unit) {
        return true;
    }

    public class WaitEffectData {
        protected Unit unit;

        public WaitEffectData(Unit unit) {
            this.unit = unit;
        }

        public float progress() {
            if (BaseAbility.this.useSlots) {
                return BaseAbility.this.slot >= BaseAbility.this.slots ? BaseAbility.this.rechargeProgress / BaseAbility.this.rechargeTime : BaseAbility.this.delayProgress / BaseAbility.this.delayTime;
            } else {
                return BaseAbility.this.rechargeProgress / BaseAbility.this.rechargeTime;
            }
        }

        public Unit unit() {
            return this.unit;
        }
    }
}
