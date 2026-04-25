package unity.world.blocks.defense.turrets;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Bullet;
import mindustry.world.blocks.defense.turrets.PowerTurret;
import unity.gen.Regions;

public class EndLaserTurret extends PowerTurret {
    public float minDamage = 170.0F;
    public float minDamageTaken = 600.0F;
    public float resistScl = 0.25F;
    public TextureRegion[] lightRegions;
    protected static float turretRotation = 0.0F;

    public EndLaserTurret(String name) {
        super(name);
        this.drawer = (tile) -> {
            Draw.rect(Regions.tenmeikiriBaseOutlineRegion, tile.x + this.tr2.x, tile.y + this.tr2.y, tile.rotation - 90.0F);
            Draw.blend(Blending.additive);

            for(int i = 0; i < this.lightRegions.length; ++i) {
                float offset = Time.time + 360.0F / (float)this.lightRegions.length * (float)i;
                float alpha = 1.0F;
                if (tile instanceof EndLaserTurretBuild) {
                    alpha = ((EndLaserTurretBuild)tile).lightsAlpha;
                }

                Draw.color(1.0F, Mathf.absin(offset, 5.0F, 0.5F) + 0.5F, Mathf.absin(offset + 5156.62F, 5.0F, 0.5F) + 0.5F, alpha);
                Draw.rect(this.lightRegions[i], tile.x + this.tr2.x, tile.y + this.tr2.y, tile.rotation - 90.0F);
            }

            Draw.blend();
            Draw.color();
        };
        this.unitSort = (e, x, y) -> e.dst2(x, y) + (float)Math.pow((double)Angles.angleDist(e.rotation, turretRotation), (double)2.0F);
    }

    public void load() {
        super.load();
        this.baseRegion = Core.atlas.find("unity-block-" + this.size);
        this.lightRegions = new TextureRegion[7];

        for(int i = 0; i < 7; ++i) {
            this.lightRegions[i] = Core.atlas.find(this.name + "-lights-" + i);
        }

    }

    public class EndLaserTurretBuild extends PowerTurret.PowerTurretBuild {
        float resistance = 1.0F;
        float lastHealth;
        float lightsAlpha = 0.0F;
        boolean rotate = true;
        Bullet bullet;
        private float invFrame = 0.0F;

        public EndLaserTurretBuild() {
            super(EndLaserTurret.this);
        }

        protected void shoot(BulletType type) {
            if (EndLaserTurret.this.chargeTime > 0.0F) {
                this.useAmmo();
                EndLaserTurret.this.tr.trns(this.rotation, EndLaserTurret.this.shootLength);
                EndLaserTurret.this.chargeBeginEffect.at(this.x + EndLaserTurret.this.tr.x, this.y + EndLaserTurret.this.tr.y, 0.0F, this);
                EndLaserTurret.this.chargeSound.at(this.x + EndLaserTurret.this.tr.x, this.y + EndLaserTurret.this.tr.y, 1.0F);

                for(int i = 0; i < EndLaserTurret.this.chargeEffects; ++i) {
                    Time.run(Mathf.random(EndLaserTurret.this.chargeMaxDelay), () -> {
                        if (this.isValid()) {
                            EndLaserTurret.this.tr.trns(this.rotation, EndLaserTurret.this.shootLength);
                            EndLaserTurret.this.chargeEffect.at(this.x + EndLaserTurret.this.tr.x, this.y + EndLaserTurret.this.tr.y, this.rotation);
                        }
                    });
                }

                this.charging = true;
                Time.run(EndLaserTurret.this.chargeTime, () -> {
                    if (this.isValid()) {
                        EndLaserTurret.this.tr.trns(this.rotation, EndLaserTurret.this.shootLength);
                        this.recoil = EndLaserTurret.this.recoilAmount;
                        this.heat = 1.0F;
                        this.bullet(type, this.rotation + Mathf.range(EndLaserTurret.this.inaccuracy));
                        this.effects();
                        this.charging = false;
                    }
                });
            }

        }

        public void updateTile() {
            if (this.health < this.lastHealth) {
                this.health = this.lastHealth;
            }

            if (this.invFrame < 30.0F) {
                this.invFrame += Time.delta;
            }

            super.updateTile();
            boolean b = this.power.status > 1.0E-4F;
            this.lightsAlpha = Mathf.lerpDelta(this.lightsAlpha, b ? this.power.status : 0.0F, !b ? 0.07F : Math.max(this.power.status * 0.1F, 0.07F));
            this.resistance = Math.max(1.0F, this.resistance - Time.delta / 20.0F);
            if (this.bullet != null) {
                this.rotate = false;
                EndLaserTurret.this.tr.trns(this.rotation, EndLaserTurret.this.shootLength);
                this.bullet.rotation(this.rotation);
                this.bullet.set(this.x + EndLaserTurret.this.tr.x, this.y + EndLaserTurret.this.tr.y);
                this.heat = 1.0F;
                this.recoil = EndLaserTurret.this.recoilAmount;
                if (this.bullet.time >= this.bullet.lifetime || this.bullet.owner != this) {
                    this.bullet = null;
                }
            } else {
                this.rotate = true;
            }

        }

        protected void findTarget() {
            EndLaserTurret.turretRotation = this.rotation;
            super.findTarget();
        }

        public void damage(float damage) {
            if (damage > EndLaserTurret.this.minDamage) {
                this.resistance += (damage - EndLaserTurret.this.minDamage) * EndLaserTurret.this.resistScl;
            }

            if (!(this.invFrame < 30.0F)) {
                float trueDamage = Mathf.clamp(damage, 0.0F, EndLaserTurret.this.minDamageTaken) / this.resistance;
                this.lastHealth -= trueDamage;
                super.damage(trueDamage);
            }
        }

        public void add() {
            if (!this.added) {
                super.add();
                if (this.lastHealth <= 0.0F) {
                    this.lastHealth = (float)this.block.health;
                }

            }
        }

        public void read(Reads read, byte revision) {
            super.read(read, revision);
            this.lastHealth = read.f();
        }

        public void write(Writes write) {
            super.write(write);
            write.f(this.lastHealth);
        }

        public boolean shouldTurn() {
            return true;
        }

        protected void bullet(BulletType type, float angle) {
            this.bullet = type.create(this.tile.build, this.team, this.x + EndLaserTurret.this.tr.x, this.y + EndLaserTurret.this.tr.y, angle);
        }

        protected void turnToTarget(float targetRot) {
            float speed = this.rotate ? EndLaserTurret.this.rotateSpeed * this.delta() * this.baseReloadSpeed() : 0.0F;
            this.rotation = Angles.moveToward(this.rotation, targetRot, speed);
        }

        protected void updateCooling() {
            if (this.bullet == null) {
                super.updateCooling();
            }

        }

        protected void updateShooting() {
            if (this.consValid() && !this.charging) {
                super.updateShooting();
            }

        }

        protected float baseReloadSpeed() {
            return this.bullet == null ? super.baseReloadSpeed() : 0.0F;
        }

        public boolean shouldActiveSound() {
            return this.bullet != null;
        }
    }
}
