package unity.world.blocks.defense.turrets;

import arc.Core;
import arc.audio.Sound;
import arc.graphics.Blending;
import arc.graphics.g2d.Draw;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.entities.Effect;
import mindustry.entities.Lightning;
import mindustry.entities.Units;
import mindustry.entities.bullet.BulletType;
import mindustry.graphics.Pal;
import mindustry.type.Liquid;
import mindustry.world.consumers.ConsumeLiquidBase;
import mindustry.world.consumers.ConsumeType;
import unity.content.UnityFx;
import unity.gen.Regions;
import unity.gen.SVec2;
import unity.gen.SoulLaserTurret;
import unity.gen.UnitySounds;
import unity.graphics.UnityDrawf;
import unity.graphics.UnityPal;
import unity.util.PitchedSoundLoop;
import unity.util.Utils;

public class SupernovaTurret extends SoulLaserTurret {
    public float chargeWarmup = 0.002F;
    public float chargeCooldown = 0.01F;
    public Sound chargeSound;
    public float chargeSoundVolume;
    public float attractionStrength;
    public float attractionDamage;
    private static final Vec2[] phases = new Vec2[]{new Vec2(), new Vec2(), new Vec2(), new Vec2(), new Vec2(), new Vec2()};
    public float starRadius;
    public float starOffset;
    public final int timerChargeStar;
    public Effect chargeStarEffect;
    public Effect chargeStar2Effect;
    public Effect starDecayEffect;
    public Effect heatWaveEffect;
    public Effect pullEffect;

    public SupernovaTurret(String name) {
        super(name);
        this.chargeSound = UnitySounds.supernovaCharge;
        this.chargeSoundVolume = 1.0F;
        this.attractionStrength = 6.0F;
        this.attractionDamage = 60.0F;
        this.starRadius = 8.0F;
        this.starOffset = -2.25F;
        this.timerChargeStar = this.timers++;
        this.chargeStarEffect = UnityFx.supernovaChargeStar;
        this.chargeStar2Effect = UnityFx.supernovaChargeStar2;
        this.starDecayEffect = UnityFx.supernovaStarDecay;
        this.heatWaveEffect = UnityFx.supernovaStarHeatwave;
        this.pullEffect = UnityFx.supernovaPullEffect;
        this.drawer = (b) -> {
            if (b instanceof SupernovaTurretBuild) {
                SupernovaTurretBuild tile = (SupernovaTurretBuild)b;
                phases[0].trns(tile.rotation, -tile.recoil + Mathf.curve(tile.phase, 0.0F, 0.3F) * -2.0F);
                phases[1].trns(tile.rotation - 90.0F, Mathf.curve(tile.phase, 0.2F, 0.5F) * -2.0F, -tile.recoil + Mathf.curve(tile.phase, 0.2F, 0.5F) * 2.0F + Mathf.curve(tile.phase, 0.5F, 0.8F) * 3.0F);
                phases[2].trns(tile.rotation - 90.0F, Mathf.curve(tile.phase, 0.0F, 0.3F) * -1.5F + Mathf.curve(tile.phase, 0.6F, 1.0F) * -2.0F, -tile.recoil + Mathf.curve(tile.phase, 0.0F, 0.3F) * 1.5F + Mathf.curve(tile.phase, 0.6F, 1.0F) * -1.0F);
                phases[3].trns(tile.rotation, -tile.recoil + Mathf.curve(tile.phase, 0.0F, 0.6F) * -4.0F);
                phases[4].trns(tile.rotation - 90.0F, Mathf.curve(tile.phase, 0.2F, 0.5F) * 2.0F, -tile.recoil + Mathf.curve(tile.phase, 0.2F, 0.5F) * 2.0F + Mathf.curve(tile.phase, 0.5F, 0.8F) * 3.0F);
                phases[5].trns(tile.rotation - 90.0F, Mathf.curve(tile.phase, 0.0F, 0.3F) * 1.5F + Mathf.curve(tile.phase, 0.6F, 1.0F) * 2.0F, -tile.recoil + Mathf.curve(tile.phase, 0.0F, 0.3F) * 1.5F + Mathf.curve(tile.phase, 0.6F, 1.0F) * -1.0F);
                Draw.rect(Regions.supernovaWingLeftBottomOutlineRegion, tile.x + phases[2].x, tile.y + phases[2].y, tile.rotation - 90.0F);
                Draw.rect(Regions.supernovaWingRightBottomOutlineRegion, tile.x + phases[5].x, tile.y + phases[5].y, tile.rotation - 90.0F);
                Draw.rect(Regions.supernovaWingLeftOutlineRegion, tile.x + phases[1].x, tile.y + phases[1].y, tile.rotation - 90.0F);
                Draw.rect(Regions.supernovaWingRightOutlineRegion, tile.x + phases[4].x, tile.y + phases[4].y, tile.rotation - 90.0F);
                Draw.rect(Regions.supernovaBottomOutlineRegion, tile.x + phases[3].x, tile.y + phases[3].y, tile.rotation - 90.0F);
                Draw.rect(Regions.supernovaHeadOutlineRegion, tile.x + this.tr2.x, tile.y + this.tr2.y, tile.rotation - 90.0F);
                Draw.rect(Regions.supernovaCoreOutlineRegion, tile.x + phases[0].x, tile.y + phases[0].y, tile.rotation - 90.0F);
                Draw.rect(Regions.supernovaWingLeftBottomRegion, tile.x + phases[2].x, tile.y + phases[2].y, tile.rotation - 90.0F);
                Draw.rect(Regions.supernovaWingRightBottomRegion, tile.x + phases[5].x, tile.y + phases[5].y, tile.rotation - 90.0F);
                Draw.rect(Regions.supernovaWingLeftRegion, tile.x + phases[1].x, tile.y + phases[1].y, tile.rotation - 90.0F);
                Draw.rect(Regions.supernovaWingRightRegion, tile.x + phases[4].x, tile.y + phases[4].y, tile.rotation - 90.0F);
                Draw.rect(Regions.supernovaBottomRegion, tile.x + phases[3].x, tile.y + phases[3].y, tile.rotation - 90.0F);
                Draw.rect(Regions.supernovaHeadRegion, tile.x + this.tr2.x, tile.y + this.tr2.y, tile.rotation - 90.0F);
                float z = Draw.z();
                Draw.z(z + 0.001F);
                Draw.rect(Regions.supernovaCoreRegion, tile.x + phases[0].x, tile.y + phases[0].y, tile.rotation - 90.0F);
                Draw.z(z);
            } else {
                throw new IllegalStateException("building isn't an instance of SupernovaTurretBuild");
            }
        };
        this.heatDrawer = (tile) -> {
            if (!(tile.heat <= 1.0E-5F)) {
                float r = Utils.pow6In.apply(tile.heat);
                float g = Interp.pow3In.apply(tile.heat);
                float b = Interp.pow2Out.apply(tile.heat);
                float a = Interp.pow2In.apply(tile.heat);
                Draw.color(Tmp.c1.set(r, g, b, a));
                Draw.blend(Blending.additive);
                Draw.rect(this.heatRegion, tile.x + this.tr2.x, tile.y + this.tr2.y, tile.rotation - 90.0F);
                Draw.color();
                Draw.blend();
            }
        };
    }

    public void load() {
        super.load();
        this.baseRegion = Core.atlas.find("unity-block-" + this.size);
    }

    public class SupernovaTurretBuild extends SoulLaserTurret.SoulLaserTurretBuild {
        public float charge;
        public float phase;
        public float starHeat;
        protected PitchedSoundLoop sound;

        public SupernovaTurretBuild() {
            super(SupernovaTurret.this);
            this.sound = new PitchedSoundLoop(SupernovaTurret.this.chargeSound, SupernovaTurret.this.chargeSoundVolume);
        }

        public void updateTile() {
            if (!this.isShooting() || !this.validateTarget() || !this.consValid()) {
                this.charge = Mathf.lerpDelta(this.charge, 0.0F, SupernovaTurret.this.chargeCooldown);
                this.charge = this.charge > 0.001F ? this.charge : 0.0F;
            }

            if (this.isShooting() && this.bulletLife <= 0.0F && this.bullet == null) {
                this.attractUnits();
            }

            if (!this.isShooting() && (!(this.bulletLife > 0.0F) || this.bullet == null)) {
                this.phase = Mathf.lerpDelta(this.phase, 0.0F, SupernovaTurret.this.chargeCooldown);
                this.phase = this.phase > 0.001F ? this.phase : 0.0F;
            } else {
                this.phase = Mathf.clamp(this.phase + SupernovaTurret.this.chargeWarmup * this.edelta(), 0.0F, 1.0F);
            }

            super.updateTile();
            if (this.isShooting() && this.bulletLife <= 0.0F && this.bullet == null) {
                Liquid liquid = this.liquids.current();
                float maxUsed = ((ConsumeLiquidBase)SupernovaTurret.this.consumes.get(ConsumeType.liquid)).amount;
                float used = this.baseReloadSpeed() * (this.cheating() ? maxUsed : Math.min(this.liquids.get(liquid), maxUsed * Time.delta)) * liquid.heatCapacity * SupernovaTurret.this.coolantMultiplier;
                this.charge = Mathf.clamp(this.charge + 120.0F * SupernovaTurret.this.chargeWarmup * used);
            }

            float prog = this.charge * 1.5F + 0.5F;
            this.sound.update(this.x, this.y, Mathf.curve(this.charge, 0.0F, 0.4F) * 1.2F, prog);
            boolean notShooting = this.bulletLife <= 0.0F || this.bullet == null;
            boolean tick = Mathf.chanceDelta((double)1.0F);
            boolean tickCharge = Mathf.chanceDelta((double)this.charge);
            this.starHeat = Mathf.approachDelta(this.starHeat, notShooting ? this.charge : 1.0F, SupernovaTurret.this.chargeWarmup * 60.0F);
            Tmp.v1.trns(this.rotation, -this.recoil + SupernovaTurret.this.starOffset + Mathf.curve(this.phase, 0.0F, 0.3F) * -2.0F);
            if (notShooting) {
                if (this.charge > 0.1F && this.timer(SupernovaTurret.this.timerChargeStar, 20.0F)) {
                    SupernovaTurret.this.chargeStarEffect.at(this.x + Tmp.v1.x, this.y + Tmp.v1.y, this.rotation, this.charge);
                }

                if (!Mathf.zero(this.charge) && tickCharge) {
                    SupernovaTurret.this.chargeStar2Effect.at(this.x + Tmp.v1.x, this.y + Tmp.v1.y, this.rotation, this.charge);
                }

                if (tickCharge) {
                    SupernovaTurret.this.chargeBeginEffect.at(this.x + Angles.trnsx(this.rotation, -this.recoil + SupernovaTurret.this.shootLength), this.y + Angles.trnsy(this.rotation, -this.recoil + SupernovaTurret.this.shootLength), this.rotation, this.charge);
                }
            } else {
                if (tick) {
                    SupernovaTurret.this.starDecayEffect.at(this.x + Tmp.v1.x, this.y + Tmp.v1.y, this.rotation);
                }

                if (this.timer(SupernovaTurret.this.timerChargeStar, 20.0F)) {
                    SupernovaTurret.this.heatWaveEffect.at(this.x + Tmp.v1.x, this.y + Tmp.v1.y, this.rotation);
                }
            }

            if (Mathf.chanceDelta(notShooting ? (double)this.charge : (double)1.0F)) {
                Tmp.v1.trns(this.rotation, -this.recoil + SupernovaTurret.this.starOffset + Mathf.curve(this.phase, 0.0F, 0.3F) * -2.0F).add(this);
                Lightning.create(this.team, Pal.lancerLaser, 60.0F, Tmp.v1.x, Tmp.v1.y, Mathf.randomSeed((long)((float)this.id + Time.time), 360.0F), Mathf.round(Mathf.randomTriangular(12.0F, 18.0F) * (notShooting ? this.charge : 1.0F)));
            }

        }

        public void draw() {
            super.draw();
            boolean notShooting = this.bulletLife <= 0.0F || this.bullet == null;
            Tmp.v1.trns(this.rotation, -this.recoil + SupernovaTurret.this.starOffset + Mathf.curve(this.phase, 0.0F, 0.3F) * -2.0F);
            float z = Draw.z();
            Draw.z(110.0F);
            Draw.color(UnityPal.monolith);
            UnityDrawf.shiningCircle(this.id, Time.time, this.x + Tmp.v1.x, this.y + Tmp.v1.y, this.starHeat * SupernovaTurret.this.starRadius, 6, 20.0F, this.starHeat * SupernovaTurret.this.starRadius, this.starHeat * SupernovaTurret.this.starRadius * 1.5F, 120.0F);
            if (notShooting && !Mathf.zero(this.charge)) {
                UnityDrawf.shiningCircle(this.id + 1, Time.time, this.x + Angles.trnsx(this.rotation, -this.recoil + SupernovaTurret.this.shootLength), this.y + Angles.trnsy(this.rotation, -this.recoil + SupernovaTurret.this.shootLength), this.charge * 4.0F, 6, 12.0F, this.charge * 4.0F, this.charge * 8.0F, 120.0F);
            }

            Draw.reset();
            Draw.z(z);
        }

        public boolean isShooting() {
            return super.isShooting() && this.efficiency() > 0.0F;
        }

        protected void updateShooting() {
            if (!(this.bulletLife > 0.0F) || this.bullet == null) {
                if (this.charge >= 1.0F && this.phase >= 1.0F && (this.consValid() || this.cheating())) {
                    BulletType type = this.peekAmmo();
                    this.shoot(type);
                    this.charge = 0.0F;
                }

            }
        }

        public void remove() {
            this.sound.stop();
            super.remove();
        }

        protected void attractUnits() {
            float rad = this.range() * 2.0F;
            Units.nearby(this.x - rad, this.y - rad, rad * 2.0F, rad * 2.0F, (unit) -> {
                if (unit.isValid() && unit.within(this, rad)) {
                    float dst = unit.dst(this);
                    float strength = 1.0F - dst / rad;
                    Tmp.v1.set(this.x - unit.x, this.y - unit.y).rotate(10.0F * (1.0F - this.charge)).setLength(SupernovaTurret.this.attractionStrength * this.charge * Time.delta).scl(strength);
                    unit.impulseNet(Tmp.v1);
                    if (unit.team != this.team) {
                        unit.damageContinuous(SupernovaTurret.this.attractionDamage / 60.0F * this.charge * strength);
                    }

                    if (Mathf.chanceDelta((double)0.1F)) {
                        Tmp.v1.trns(this.rotation, -this.recoil + SupernovaTurret.this.starOffset + Mathf.curve(this.phase, 0.0F, 0.3F) * -2.0F).add(this);
                        SupernovaTurret.this.pullEffect.at(unit.x, unit.y, this.charge * (3.0F + Mathf.range(0.2F)), SVec2.construct(Tmp.v1.x, Tmp.v1.y));
                    }
                }

            });
        }
    }
}
