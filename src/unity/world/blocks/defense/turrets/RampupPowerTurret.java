package unity.world.blocks.defense.turrets;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Time;
import mindustry.entities.Lightning;
import mindustry.entities.bullet.BulletType;
import mindustry.graphics.Drawf;
import mindustry.world.blocks.defense.turrets.PowerTurret;

public class RampupPowerTurret extends PowerTurret {
    public float barBaseY;
    public float barLength;
    public float barStroke = 1.5F;
    public Color[] barColors = new Color[]{Color.valueOf("00d9ff"), Color.valueOf("ccffff")};
    public float maxSpeedMul = 13.0F;
    public float speedInc = 0.2F;
    public float speedDec = 0.05F;
    public float accInc = 4.0F;
    public boolean lightning;
    public Color lightningColor = Color.valueOf("a9d8ff");
    public int baseLightningLength;
    public int lightningLengthDec;
    public float lightningThreshold;
    public float baseLightningDamage;
    public float lightningDamageDec;
    public TextureRegion topRegion;
    protected Vec2 tr3 = new Vec2();

    public RampupPowerTurret(String name) {
        super(name);
    }

    public void load() {
        super.load();
        this.topRegion = Core.atlas.find(this.name + "-top");
    }

    public class RampupPowerTurretBuild extends PowerTurret.PowerTurretBuild {
        public float speed = 1.0F;

        public RampupPowerTurretBuild() {
            super(RampupPowerTurret.this);
        }

        public void updateTile() {
            if (!this.isShooting() || !this.consValid()) {
                this.changeSpeed(-RampupPowerTurret.this.speedDec * Time.delta);
            }

            super.updateTile();
        }

        public void draw() {
            Draw.rect(RampupPowerTurret.this.baseRegion, this.x, this.y);
            Draw.z(50.0F);
            RampupPowerTurret.this.tr2.trns(this.rotation, -this.recoil);
            Drawf.shadow(RampupPowerTurret.this.region, this.x + RampupPowerTurret.this.tr2.x - RampupPowerTurret.this.elevation, this.y + RampupPowerTurret.this.tr2.y - RampupPowerTurret.this.elevation, this.rotation - 90.0F);
            Draw.rect(RampupPowerTurret.this.region, this.x + RampupPowerTurret.this.tr2.x, this.y + RampupPowerTurret.this.tr2.y, this.rotation - 90.0F);
            if (this.speed > 1.001F) {
                RampupPowerTurret.this.tr3.trns(this.rotation, -this.recoil + RampupPowerTurret.this.barBaseY);
                Draw.color(RampupPowerTurret.this.barColors[0], RampupPowerTurret.this.barColors[1], this.heat);
                Lines.stroke(RampupPowerTurret.this.barStroke);
                Lines.lineAngle(this.x + RampupPowerTurret.this.tr3.x, this.y + RampupPowerTurret.this.tr3.y, this.rotation, this.speedf() * RampupPowerTurret.this.barLength, false);
                Draw.reset();
            }

            Draw.rect(RampupPowerTurret.this.topRegion, this.x + RampupPowerTurret.this.tr2.x, this.y + RampupPowerTurret.this.tr2.y, this.rotation - 90.0F);
            if (RampupPowerTurret.this.heatRegion != Core.atlas.find("error")) {
                RampupPowerTurret.this.heatDrawer.get(this);
            }

        }

        public void shoot(BulletType type) {
            this.changeSpeed(RampupPowerTurret.this.speedInc);
            if (RampupPowerTurret.this.chargeTime > 0.0F) {
                this.useAmmo();
                RampupPowerTurret.this.tr.trns(this.rotation, RampupPowerTurret.this.shootLength);
                RampupPowerTurret.this.chargeBeginEffect.at(this.x + RampupPowerTurret.this.tr.x, this.y + RampupPowerTurret.this.tr.y, this.rotation);
                RampupPowerTurret.this.chargeSound.at(this.x + RampupPowerTurret.this.tr.x, this.y + RampupPowerTurret.this.tr.y, 1.0F);

                for(int i = 0; i < RampupPowerTurret.this.chargeEffects; ++i) {
                    Time.run(Mathf.random(RampupPowerTurret.this.chargeMaxDelay), () -> {
                        if (this.isValid()) {
                            RampupPowerTurret.this.tr.trns(this.rotation, RampupPowerTurret.this.shootLength);
                            RampupPowerTurret.this.chargeEffect.at(this.x + RampupPowerTurret.this.tr.x, this.y + RampupPowerTurret.this.tr.y, this.rotation);
                        }
                    });
                }

                this.charging = true;
                Time.run(RampupPowerTurret.this.chargeTime, () -> {
                    if (this.isValid()) {
                        RampupPowerTurret.this.tr.trns(this.rotation, RampupPowerTurret.this.shootLength);
                        this.recoil = RampupPowerTurret.this.recoilAmount;
                        this.heat = 1.0F;
                        if (RampupPowerTurret.this.lightning && this.speed < RampupPowerTurret.this.lightningThreshold) {
                            Lightning.create(this.team, RampupPowerTurret.this.lightningColor, RampupPowerTurret.this.baseLightningDamage - RampupPowerTurret.this.lightningDamageDec * this.speed, this.x + RampupPowerTurret.this.tr.x, this.y + RampupPowerTurret.this.tr.y, this.rotation, RampupPowerTurret.this.baseLightningLength - (int)((this.speed - 1.0F) * (float)RampupPowerTurret.this.lightningLengthDec));
                        }

                        this.bullet(type, this.rotation + Mathf.range(RampupPowerTurret.this.inaccuracy / (1.0F + Mathf.clamp(this.speed / RampupPowerTurret.this.accInc, 0.0F, RampupPowerTurret.this.maxSpeedMul))));
                        this.effects();
                        this.charging = false;
                    }
                });
            } else if (RampupPowerTurret.this.burstSpacing > 1.0E-4F) {
                for(int i = 0; i < RampupPowerTurret.this.shots; ++i) {
                    Time.run(RampupPowerTurret.this.burstSpacing * (float)i, () -> {
                        if (this.isValid() && this.hasAmmo()) {
                            this.recoil = RampupPowerTurret.this.recoilAmount;
                            RampupPowerTurret.this.tr.trns(this.rotation, RampupPowerTurret.this.shootLength, Mathf.range(RampupPowerTurret.this.xRand));
                            if (RampupPowerTurret.this.lightning && this.speed < RampupPowerTurret.this.lightningThreshold) {
                                Lightning.create(this.team, RampupPowerTurret.this.lightningColor, RampupPowerTurret.this.baseLightningDamage - RampupPowerTurret.this.lightningDamageDec * this.speed, this.x + RampupPowerTurret.this.tr.x, this.y + RampupPowerTurret.this.tr.y, this.rotation, RampupPowerTurret.this.baseLightningLength - (int)((this.speed - 1.0F) * (float)RampupPowerTurret.this.lightningLengthDec));
                            }

                            this.bullet(type, this.rotation + Mathf.range(RampupPowerTurret.this.inaccuracy / (1.0F + Mathf.clamp(this.speed / RampupPowerTurret.this.accInc, 0.0F, RampupPowerTurret.this.maxSpeedMul))));
                            this.effects();
                            this.useAmmo();
                            this.recoil = RampupPowerTurret.this.recoilAmount;
                            this.heat = 1.0F;
                        }
                    });
                }
            } else {
                if (RampupPowerTurret.this.alternate) {
                    float i = (float)(this.shotCounter % RampupPowerTurret.this.shots) - (float)(RampupPowerTurret.this.shots - 1) / 2.0F;
                    RampupPowerTurret.this.tr.trns(this.rotation - 90.0F, RampupPowerTurret.this.spread * i + Mathf.range(RampupPowerTurret.this.xRand), RampupPowerTurret.this.shootLength);
                    if (RampupPowerTurret.this.lightning && this.speed < RampupPowerTurret.this.lightningThreshold) {
                        Lightning.create(this.team, RampupPowerTurret.this.lightningColor, RampupPowerTurret.this.baseLightningDamage - RampupPowerTurret.this.lightningDamageDec * this.speed, this.x + RampupPowerTurret.this.tr.x, this.y + RampupPowerTurret.this.tr.y, this.rotation, RampupPowerTurret.this.baseLightningLength - (int)((this.speed - 1.0F) * (float)RampupPowerTurret.this.lightningLengthDec));
                    }

                    this.bullet(type, this.rotation + Mathf.range(RampupPowerTurret.this.inaccuracy / (1.0F + Mathf.clamp(this.speed / RampupPowerTurret.this.accInc, 0.0F, RampupPowerTurret.this.maxSpeedMul))));
                } else {
                    RampupPowerTurret.this.tr.trns(this.rotation, RampupPowerTurret.this.shootLength, Mathf.range(RampupPowerTurret.this.xRand));

                    for(int i = 0; i < RampupPowerTurret.this.shots; ++i) {
                        if (RampupPowerTurret.this.lightning && this.speed < RampupPowerTurret.this.lightningThreshold) {
                            Lightning.create(this.team, RampupPowerTurret.this.lightningColor, RampupPowerTurret.this.baseLightningDamage - RampupPowerTurret.this.lightningDamageDec * this.speed, this.x + RampupPowerTurret.this.tr.x, this.y + RampupPowerTurret.this.tr.y, this.rotation, RampupPowerTurret.this.baseLightningLength - (int)((this.speed - 1.0F) * (float)RampupPowerTurret.this.lightningLengthDec));
                        }

                        this.bullet(type, this.rotation + Mathf.range((RampupPowerTurret.this.inaccuracy + type.inaccuracy) / (1.0F + Mathf.clamp(this.speed / RampupPowerTurret.this.accInc, 0.0F, RampupPowerTurret.this.maxSpeedMul))) + (float)(i - (int)((float)RampupPowerTurret.this.shots / 2.0F)) * RampupPowerTurret.this.spread);
                    }
                }

                ++this.shotCounter;
                this.recoil = RampupPowerTurret.this.recoilAmount;
                this.heat = 1.0F;
                this.effects();
                this.useAmmo();
            }

        }

        protected float baseReloadSpeed() {
            return this.efficiency() * this.speed;
        }

        public void changeSpeed(float amount) {
            this.speed = Mathf.clamp(this.speed + amount, 1.0F, RampupPowerTurret.this.maxSpeedMul);
        }

        public float speedf() {
            return (this.speed - 1.0F) / (RampupPowerTurret.this.maxSpeedMul - 1.0F);
        }
    }
}
