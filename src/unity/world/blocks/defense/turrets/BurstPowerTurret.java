package unity.world.blocks.defense.turrets;

import arc.audio.Sound;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Sounds;
import mindustry.world.blocks.defense.turrets.PowerTurret;

public class BurstPowerTurret extends PowerTurret {
    protected BulletType subShootType;
    protected int subShots = 1;
    protected float subBurstSpacing;
    protected Effect subShootEffect;
    protected Sound subShootSound;

    public BurstPowerTurret(String name) {
        super(name);
        this.subShootEffect = Fx.none;
        this.subShootSound = Sounds.none;
    }

    public class BurstPowerTurretBuild extends PowerTurret.PowerTurretBuild {
        public BurstPowerTurretBuild() {
            super(BurstPowerTurret.this);
        }

        public void shoot(BulletType type) {
            if (BurstPowerTurret.this.chargeTime > 0.0F) {
                this.useAmmo();
                BurstPowerTurret.this.tr.trns(this.rotation, (float)(BurstPowerTurret.this.size * 8) / 2.0F);
                BurstPowerTurret.this.chargeBeginEffect.at(this.x + BurstPowerTurret.this.tr.x, this.y + BurstPowerTurret.this.tr.y, this.rotation);
                BurstPowerTurret.this.chargeSound.at(this.x + BurstPowerTurret.this.tr.x, this.y + BurstPowerTurret.this.tr.y, 1.0F);

                for(int i = 0; i < BurstPowerTurret.this.chargeEffects; ++i) {
                    Time.run(Mathf.random(BurstPowerTurret.this.chargeMaxDelay), () -> {
                        if (this.isValid()) {
                            BurstPowerTurret.this.tr.trns(this.rotation, (float)(BurstPowerTurret.this.size * 8) / 2.0F);
                            BurstPowerTurret.this.chargeEffect.at(this.x + BurstPowerTurret.this.tr.x, this.y + BurstPowerTurret.this.tr.y, this.rotation);
                        }
                    });
                }

                this.charging = true;
                Time.run(BurstPowerTurret.this.chargeTime, () -> {
                    if (this.isValid()) {
                        BurstPowerTurret.this.tr.trns(this.rotation, (float)(BurstPowerTurret.this.size * 8) / 2.0F);
                        this.recoil = BurstPowerTurret.this.recoilAmount;
                        this.heat = 1.0F;

                        for(int i = 0; i < BurstPowerTurret.this.shots; ++i) {
                            Time.run(BurstPowerTurret.this.burstSpacing * 2.0F, () -> this.bullet(type, this.rotation + Mathf.range(BurstPowerTurret.this.inaccuracy)));
                        }

                        for(int i = 0; i < BurstPowerTurret.this.subShots; ++i) {
                            Time.run(BurstPowerTurret.this.subBurstSpacing * (float)i, () -> {
                                this.bullet(BurstPowerTurret.this.subShootType, this.rotation + Mathf.range(BurstPowerTurret.this.subShootType.inaccuracy));
                                this.subEffects();
                            });
                        }

                        this.effects();
                        this.charging = false;
                    }
                });
            } else {
                super.shoot(type);
            }

        }

        protected void subEffects() {
            BurstPowerTurret.this.subShootEffect.at(this.x + BurstPowerTurret.this.tr.x, this.y + BurstPowerTurret.this.tr.y, this.rotation);
            BurstPowerTurret.this.subShootSound.at(this.x + BurstPowerTurret.this.tr.x, this.y + BurstPowerTurret.this.tr.y, 1.0F);
        }
    }
}
