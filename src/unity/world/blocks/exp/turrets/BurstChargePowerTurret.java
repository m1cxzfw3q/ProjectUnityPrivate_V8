package unity.world.blocks.exp.turrets;

import arc.math.Mathf;
import arc.util.Time;
import mindustry.entities.bullet.BulletType;

public class BurstChargePowerTurret extends ExpPowerTurret {
    public BurstChargePowerTurret(String name) {
        super(name);
    }

    public class BurstChargeTurretBuild extends ExpPowerTurret.ExpPowerTurretBuild {
        public BurstChargeTurretBuild() {
            super(BurstChargePowerTurret.this);
        }

        protected void shootCharge(BulletType type, float rotation) {
            float rx = Mathf.range(BurstChargePowerTurret.this.xRand);
            BurstChargePowerTurret.this.tr.trns(rotation, BurstChargePowerTurret.this.shootLength, rx);
            BurstChargePowerTurret.this.chargeBeginEffect.at(this.x + BurstChargePowerTurret.this.tr.x, this.y + BurstChargePowerTurret.this.tr.y, rotation);
            BurstChargePowerTurret.this.chargeSound.at(this.x + BurstChargePowerTurret.this.tr.x, this.y + BurstChargePowerTurret.this.tr.y, 1.0F);

            for(int i = 0; i < BurstChargePowerTurret.this.chargeEffects; ++i) {
                Time.run(Mathf.random(BurstChargePowerTurret.this.chargeMaxDelay), () -> {
                    if (!this.dead) {
                        BurstChargePowerTurret.this.tr.trns(rotation, BurstChargePowerTurret.this.shootLength, rx);
                        BurstChargePowerTurret.this.chargeEffect.at(this.x + BurstChargePowerTurret.this.tr.x, this.y + BurstChargePowerTurret.this.tr.y, rotation);
                    }
                });
            }

            Time.run(BurstChargePowerTurret.this.chargeTime, () -> {
                if (!this.dead) {
                    BurstChargePowerTurret.this.tr.trns(rotation, BurstChargePowerTurret.this.shootLength, rx);
                    this.heat = 1.0F;
                    this.effects();
                    this.useAmmo();
                    this.recoil = BurstChargePowerTurret.this.recoilAmount;
                    this.bullet(type, rotation + Mathf.range(BurstChargePowerTurret.this.inaccuracy + type.inaccuracy));
                }
            });
        }

        protected void shoot(BulletType type) {
            if (BurstChargePowerTurret.this.chargeTime <= 0.0F) {
                super.shoot(type);
            } else {
                if (BurstChargePowerTurret.this.burstSpacing > 1.0E-4F) {
                    this.charging = true;

                    for(int i = 0; i < BurstChargePowerTurret.this.shots; ++i) {
                        Time.run(BurstChargePowerTurret.this.burstSpacing * (float)i, () -> {
                            if (!this.dead && this.hasAmmo()) {
                                BurstChargePowerTurret.this.tr.trns(this.rotation, BurstChargePowerTurret.this.shootLength, Mathf.range(BurstChargePowerTurret.this.xRand));
                                this.shootCharge(this.peekAmmo(), this.rotation + Mathf.range(BurstChargePowerTurret.this.inaccuracy + this.peekAmmo().inaccuracy) + (float)(i - (int)((float)BurstChargePowerTurret.this.shots / 2.0F)) * BurstChargePowerTurret.this.spread);
                            }
                        });
                    }

                    Time.run(BurstChargePowerTurret.this.burstSpacing * (float)BurstChargePowerTurret.this.shots + BurstChargePowerTurret.this.chargeTime, () -> this.charging = false);
                } else {
                    this.charging = true;
                    if (BurstChargePowerTurret.this.alternate) {
                        float i = (float)(this.shotCounter % BurstChargePowerTurret.this.shots) - (float)(BurstChargePowerTurret.this.shots - 1) / 2.0F;
                        BurstChargePowerTurret.this.tr.trns(this.rotation - 90.0F, BurstChargePowerTurret.this.spread * i + Mathf.range(BurstChargePowerTurret.this.xRand), BurstChargePowerTurret.this.shootLength);
                        this.shootCharge(type, this.rotation + Mathf.range(BurstChargePowerTurret.this.inaccuracy + type.inaccuracy));
                    } else {
                        BurstChargePowerTurret.this.tr.trns(this.rotation, BurstChargePowerTurret.this.shootLength, Mathf.range(BurstChargePowerTurret.this.xRand));

                        for(int i = 0; i < BurstChargePowerTurret.this.shots; ++i) {
                            this.shootCharge(type, this.rotation + Mathf.range(BurstChargePowerTurret.this.inaccuracy + type.inaccuracy) + (float)(i - (int)((float)BurstChargePowerTurret.this.shots / 2.0F)) * BurstChargePowerTurret.this.spread);
                        }
                    }

                    Time.run(BurstChargePowerTurret.this.chargeTime, () -> this.charging = false);
                    ++this.shotCounter;
                }

            }
        }
    }
}
