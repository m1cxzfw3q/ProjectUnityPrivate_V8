package unity.content;

import arc.graphics.Color;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.content.StatusEffects;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;
import mindustry.type.StatusEffect;
import unity.graphics.UnityPal;

public class UnityStatusEffects {
    public static StatusEffect disabled;
    public static StatusEffect weaken;
    public static StatusEffect plasmaed;
    public static StatusEffect radiation;
    public static StatusEffect reloadFatigue;
    public static StatusEffect speedFatigue;
    public static StatusEffect sagittariusFatigue;
    public static StatusEffect blueBurn;
    public static StatusEffect molten;
    public static StatusEffect tpCoolDown;
    public static StatusEffect teamConverted;
    public static StatusEffect boosted;
    public static StatusEffect distort;
    public static StatusEffect plated;

    public static void load() {
        disabled = new StatusEffect("disabled") {
            {
                this.reloadMultiplier = 0.0F;
                this.speedMultiplier = 0.0F;
                this.disarm = true;
            }
        };
        weaken = new StatusEffect("weaken") {
            {
                this.damageMultiplier = 0.75F;
                this.healthMultiplier = 0.75F;
                this.speedMultiplier = 0.5F;
            }
        };
        plasmaed = new StatusEffect("plasmaed") {
            {
                this.effectChance = 0.15F;
                this.damage = 0.5F;
                this.reloadMultiplier = 0.8F;
                this.healthMultiplier = 0.9F;
                this.damageMultiplier = 0.8F;
                this.effect = UnityFx.plasmaedEffect;
            }
        };
        radiation = new StatusEffect("radiation") {
            {
                this.damage = 1.6F;
            }

            public void update(Unit unit, float time) {
                super.update(unit, time);
                if (Mathf.chanceDelta((double)(0.008F * Mathf.clamp(time / 120.0F)))) {
                    unit.damage(unit.maxHealth * 0.125F);
                }

                for(int i = 0; i < unit.mounts.length; ++i) {
                    float strength = Mathf.clamp(time / 120.0F);
                    WeaponMount temp = unit.mounts[i];
                    if (temp != null) {
                        if (Mathf.chanceDelta((double)0.12F)) {
                            temp.reload = Math.min(temp.reload + Time.delta * 1.5F * strength, temp.weapon.reload);
                        }

                        temp.rotation += Mathf.range(12.0F * strength);
                    }
                }

            }
        };
        blueBurn = new StatusEffect("blue-burn") {
            {
                this.damage = 0.14F;
                this.effect = UnityFx.blueBurnEffect;
                this.init(() -> this.opposite(new StatusEffect[]{StatusEffects.wet, StatusEffects.freezing}));
            }
        };
        reloadFatigue = new StatusEffect("reload-fatigue") {
            {
                this.reloadMultiplier = 0.75F;
            }
        };
        speedFatigue = new StatusEffect("speed-fatigue") {
            {
                this.speedMultiplier = 0.6F;
            }
        };
        sagittariusFatigue = new StatusEffect("sagittarius-fatigue") {
            {
                this.speedMultiplier = 0.1F;
                this.healthMultiplier = 0.6F;
                this.color = Color.valueOf("62ae7f");
            }
        };
        molten = new StatusEffect("molten") {
            {
                this.color = UnityPal.lava;
                this.speedMultiplier = 0.6F;
                this.healthMultiplier = 0.5F;
                this.damage = 1.0F;
                this.effect = UnityFx.ahhimaLiquidNow;
            }
        };
        tpCoolDown = new StatusEffect("tpcooldonw") {
            {
                this.color = UnityPal.diriumLight;
                this.effect = Fx.none;
            }
        };
        teamConverted = new StatusEffect("team-converted") {
            {
                this.healthMultiplier = 0.35F;
                this.damageMultiplier = 0.4F;
                this.permanent = true;
                this.effect = UnityFx.teamConvertedEffect;
                this.color = Color.valueOf("a3e3ff");
            }
        };
        boosted = new StatusEffect("boosted") {
            {
                this.color = Pal.lancerLaser;
                this.effect = Fx.none;
                this.speedMultiplier = 2.0F;
            }
        };
        distort = new StatusEffect("distort") {
            {
                this.speedMultiplier = 0.35F;
                this.color = Pal.lancerLaser;
                this.effect = UnityFx.distortFx;
            }

            public void update(Unit unit, float time) {
                if (this.damage > 0.0F) {
                    unit.damageContinuousPierce(this.damage);
                } else if (this.damage < 0.0F) {
                    unit.heal(-1.0F * this.damage * Time.delta);
                }

                if (this.effect != Fx.none && Mathf.chanceDelta((double)this.effectChance)) {
                    Tmp.v1.rnd(unit.type.hitSize / 2.0F);
                    this.effect.at(unit.x + Tmp.v1.x, unit.y + Tmp.v1.y, 0.0F, 45.0F);
                }

            }
        };
        plated = new StatusEffect("plated") {
            {
                this.speedMultiplier = 0.75F;
                this.damageMultiplier = 1.5F;
                this.healthMultiplier = 2.0F;
                this.reloadMultiplier = 1.2F;
                this.permanent = true;
                this.effect = UnityFx.plated;
                this.effectChance = 0.4F;
            }

            public void update(Unit unit, float time) {
                if (Mathf.chanceDelta((double)this.effectChance) && (!unit.isFlying() || unit.moving())) {
                    Tmp.v1.rnd(unit.type.hitSize / 2.0F);
                    this.effect.at(unit.x + Tmp.v1.x, unit.y + Tmp.v1.y, 0.0F, Mathf.chance((double)0.5F) ? Pal.accent : Items.surgeAlloy.color, Mathf.random() + 0.1F);
                }

            }
        };
    }
}
