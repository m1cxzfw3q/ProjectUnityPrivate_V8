package unity.entities.abilities;

import arc.Core;
import arc.audio.Sound;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Healthc;
import mindustry.gen.Sounds;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import unity.content.effects.ParticleFx;
import unity.content.effects.SpecialFx;
import unity.graphics.UnityPal;

public class LightningSpawnAbility extends Ability {
    public float rotateSpeed;
    public int sectors = 6;
    public float phase;
    public float phaseSpeed;
    public float lightningRange;
    public float lightningOffset;
    public float lightningDamage;
    public float lightningRadius = 8.0F;
    public float lightningOuterRadius = 48.0F;
    public float trailChance = 0.3F;
    protected int lightningCount;
    protected boolean useAmmo = true;
    protected Sound shootSound;
    protected Color backColor;
    protected Color frontColor;
    protected Effect damageEffect;
    protected Effect hitEffect;
    protected Effect trailEffect;
    protected float timer;
    protected float reload;

    public LightningSpawnAbility(int lightningCount, float reload, float rotateSpeed, float phaseSpeed, float lightningRange, float lightningOffset, float lightningDamage) {
        this.shootSound = Sounds.spark;
        this.backColor = UnityPal.monolithDark.cpy().a(0.5F);
        this.frontColor = UnityPal.monolithLight.cpy().a(0.5F);
        this.damageEffect = SpecialFx.chainLightningActive;
        this.hitEffect = Fx.hitLaserBlast;
        this.trailEffect = ParticleFx.monolithSpark;
        this.reload = reload;
        this.rotateSpeed = rotateSpeed;
        this.phaseSpeed = phaseSpeed;
        this.lightningCount = lightningCount;
        this.lightningRange = lightningRange;
        this.lightningOffset = lightningOffset;
        this.lightningDamage = lightningDamage;
    }

    public void update(Unit unit) {
        this.timer += Time.delta;
        boolean can = this.timer >= this.reload;

        for(int i = 0; i < this.lightningCount; ++i) {
            Tmp.v1.trns((Time.time + this.rotateSpeed + 360.0F * (float)i / (float)this.lightningCount + Mathf.randomSeed((long)unit.id)) * (float)Mathf.signs[unit.id % 2], this.lightningOffset * this.phase).add(unit);
            float x = Tmp.v1.x;
            float y = Tmp.v1.y;
            if (can) {
                this.timer = 0.0F;
                Teamc t = Units.closestTarget(unit.team, x, y, this.lightningRange);
                if (t instanceof Healthc) {
                    Healthc h = (Healthc)t;
                    h.damage(this.lightningDamage);
                    this.hitEffect.at(h.x(), h.y(), unit.angleTo(h), this.backColor);
                    this.damageEffect.at(x, y, 2.0F, this.frontColor, h);
                    this.hitEffect.at(x, y, unit.angleTo(h), this.backColor);
                    this.shootSound.at(x, y, Mathf.random(0.8F, 1.2F));
                    if (this.useAmmo && Vars.state.rules.unitAmmo) {
                        --unit.ammo;
                    }
                }
            }

            if (Mathf.chanceDelta((double)this.trailChance)) {
                this.trailEffect.at(x, y, this.lightningRadius);
            }
        }

        this.phase = Mathf.lerpDelta(this.phase, this.useAmmo && Vars.state.rules.unitAmmo ? unit.ammof() : 1.0F, this.phaseSpeed);
    }

    public void draw(Unit unit) {
        float z = Draw.z();
        Draw.z(100.0F);
        TextureRegion shade = Core.atlas.find("circle-shadow");

        for(int i = 0; i < this.lightningCount; ++i) {
            Tmp.v1.trns((Time.time + this.rotateSpeed + 360.0F * (float)i / (float)this.lightningCount + Mathf.randomSeed((long)unit.id)) * (float)Mathf.signs[unit.id % 2], this.lightningOffset * this.phase).add(unit);
            float out = this.lightningOuterRadius + Mathf.absin(8.0F, 0.5F);
            float in = this.lightningRadius + Mathf.absin(6.0F, 0.4F);
            float bet = Mathf.lerp(in, out, 0.2F);
            float x = Tmp.v1.x;
            float y = Tmp.v1.y;
            Draw.color(this.backColor);
            Draw.rect(shade, x, y, this.lightningOuterRadius * 2.0F, this.lightningOuterRadius * 1.8F);
            Draw.color(this.frontColor);
            Fill.circle(x, y, this.lightningRadius);
            Lines.stroke(2.0F + Mathf.absin(15.0F, 0.7F), Tmp.c1.set(this.backColor).lerp(this.frontColor, 0.7F));

            for(int s = 0; s < this.sectors; ++s) {
                Lines.arc(x, y, bet - 2.0F, 0.1F, (float)s * 360.0F / (float)this.sectors + Time.time * this.rotateSpeed * (float)Mathf.signs[unit.id % 2]);
            }

            Lines.stroke(Lines.getStroke() - 1.0F, this.frontColor);

            for(int s = 0; s < this.sectors; ++s) {
                Lines.arc(x, y, bet, 0.14F, (float)s * 360.0F / (float)this.sectors + Time.time * this.rotateSpeed * (float)Mathf.signs[(unit.id + 1) % 2]);
            }

            Drawf.light(x, y, this.lightningOuterRadius * 2.0F, this.frontColor, this.phase);
        }

        Draw.z(z);
    }

    public String localized() {
        return Core.bundle.get("ability.lightningspawn");
    }
}
