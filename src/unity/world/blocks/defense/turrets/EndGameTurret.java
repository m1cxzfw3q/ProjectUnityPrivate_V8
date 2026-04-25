package unity.world.blocks.defense.turrets;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.entities.Units;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.gen.Entityc;
import mindustry.gen.Groups;
import mindustry.gen.Healthc;
import mindustry.gen.Player;
import mindustry.gen.Posc;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.world.blocks.defense.turrets.PowerTurret;
import mindustry.world.consumers.ConsumeType;
import unity.Unity;
import unity.content.UnityFx;
import unity.content.effects.ShootFx;
import unity.content.effects.SpecialFx;
import unity.entities.effects.SlowLightningType;
import unity.gen.UnitySounds;
import unity.mod.AntiCheat;
import unity.util.Utils;

public class EndGameTurret extends PowerTurret {
    private static int shouldLaser = 0;
    protected static float damageFull;
    protected static float damageB;
    protected static int totalFrags;
    private static final float[] ringProgresses = new float[]{0.013F, 0.035F, 0.024F};
    private static final int[] ringDirections = new int[]{1, -1, 1};
    private static final Seq<Entityc> entitySeq = new Seq(512);
    protected int eyeTime;
    protected int bulletTime;
    private final SlowLightningType lightning;
    public TextureRegion baseLightsRegion;
    public TextureRegion bottomLightsRegion;
    public TextureRegion eyeMainRegion;
    public TextureRegion ringABottomRegion;
    public TextureRegion ringAEyesRegion;
    public TextureRegion ringARegion;
    public TextureRegion ringALightsRegion;
    public TextureRegion ringBBottomRegion;
    public TextureRegion ringBEyesRegion;
    public TextureRegion ringBRegion;
    public TextureRegion ringBLightsRegion;
    public TextureRegion ringCRegion;
    public TextureRegion ringCLightsRegion;

    public EndGameTurret(String name) {
        super(name);
        this.eyeTime = this.timers++;
        this.bulletTime = this.timers++;
        this.lightning = new SlowLightningType() {
            {
                this.colorFrom = Color.red;
                this.colorTo = Color.black;
                this.damage = 520.0F;
                this.splitChance = 0.045F;
                this.range = 810.0F;
            }
        };
        this.health = 68000;
        this.powerUse = 320.0F;
        this.reloadTime = 300.0F;
        this.absorbLasers = true;
        this.shootShake = 2.2F;
        this.outlineIcon = false;
        this.noUpdateDisabled = false;
        this.loopSound = UnitySounds.endgameActive;
        this.shootSound = UnitySounds.endgameShoot;
    }

    public void load() {
        super.load();
        this.baseRegion = Core.atlas.find(this.name + "-base");
        this.baseLightsRegion = Core.atlas.find(this.name + "-base-lights");
        this.bottomLightsRegion = Core.atlas.find(this.name + "-bottom-lights");
        this.eyeMainRegion = Core.atlas.find(this.name + "-eye");
        this.ringABottomRegion = Core.atlas.find(this.name + "-ring1-bottom");
        this.ringAEyesRegion = Core.atlas.find(this.name + "-ring1-eyes");
        this.ringARegion = Core.atlas.find(this.name + "-ring1");
        this.ringALightsRegion = Core.atlas.find(this.name + "-ring1-lights");
        this.ringBBottomRegion = Core.atlas.find(this.name + "-ring2-bottom");
        this.ringBEyesRegion = Core.atlas.find(this.name + "-ring2-eyes");
        this.ringBRegion = Core.atlas.find(this.name + "-ring2");
        this.ringBLightsRegion = Core.atlas.find(this.name + "-ring2-lights");
        this.ringCRegion = Core.atlas.find(this.name + "-ring3");
        this.ringCLightsRegion = Core.atlas.find(this.name + "-ring3-lights");
    }

    public class EndGameTurretBuilding extends PowerTurret.PowerTurretBuild {
        protected float charge = 0.0F;
        protected float resist = 1.0F;
        protected float resistTime = 10.0F;
        protected float threatLevel = 1.0F;
        protected float lastHealth = 0.0F;
        protected float eyeResetTime = 0.0F;
        protected float eyesAlpha = 0.0F;
        protected float lightsAlpha = 0.0F;
        protected float[] ringProgress = new float[]{0.0F, 0.0F, 0.0F};
        protected float[] eyeReloads = new float[]{0.0F, 0.0F};
        protected int eyeSequenceA = 0;
        protected int eyeSequenceB = 0;
        protected Vec2 eyeOffset = new Vec2();
        protected Vec2 eyeOffsetB = new Vec2();
        protected Vec2 eyeTargetOffset = new Vec2();
        protected Vec2[] eyesVecArray = new Vec2[16];
        protected Posc[] targets = new Posc[16];

        public EndGameTurretBuilding() {
            super(EndGameTurret.this);
        }

        protected void effects() {
            EndGameTurret.this.shootSound.at(this.x, this.y);
        }

        public void damage(float damage) {
            if (!this.verify()) {
                if (damage > 10000.0F) {
                    this.charge += Mathf.clamp(damage - 10000.0F, 0.0F, 2000000.0F) / 150.0F;
                }

                if (this.charge > 15.0F) {
                    this.charge = 15.0F;
                }

                float trueAmount = Mathf.clamp(damage / this.resist, 0.0F, 410.0F);
                super.damage(trueAmount);
                this.resist += 0.125F + Mathf.clamp(damage - 520.0F, 0.0F, (float)Integer.MAX_VALUE) / 70.0F;
                if (Float.isNaN(this.resist)) {
                    this.resist = Float.MAX_VALUE;
                }

                this.resistTime = 0.0F;
            }
        }

        protected float baseReloadSpeed() {
            return Mathf.clamp(this.efficiency() + this.charge, 0.0F, 1.2F);
        }

        float trueEfficiency() {
            return Mathf.clamp(this.efficiency() + this.charge);
        }

        float deltaB() {
            return this.delta() * this.baseReloadSpeed();
        }

        public boolean consValid() {
            boolean valid = false;
            if (this.block.consumes.hasPower()) {
                valid = this.block.consumes.getPower().valid(this);
            }

            valid |= this.charge > 0.001F;
            if (this.block.consumes.has(ConsumeType.item)) {
                valid &= this.block.consumes.getItem().valid(this);
            }

            return valid;
        }

        public void read(Reads read, byte revision) {
            super.read(read, revision);
            this.charge = read.f();
            this.resist = read.f();
        }

        public void write(Writes write) {
            super.write(write);
            write.f(this.charge);
            write.f(this.resist);
        }

        public void draw() {
            float oz = Draw.z();
            Draw.rect(EndGameTurret.this.baseRegion, this.x, this.y);
            Draw.z(oz + 0.01F);
            Draw.rect(EndGameTurret.this.ringABottomRegion, this.x, this.y, this.ringProgress[0]);
            Draw.rect(EndGameTurret.this.ringBBottomRegion, this.x, this.y, this.ringProgress[1]);
            Draw.z(oz + 0.02F);
            Drawf.spinSprite(EndGameTurret.this.ringARegion, this.x, this.y, this.ringProgress[0]);
            Drawf.spinSprite(EndGameTurret.this.ringBRegion, this.x, this.y, this.ringProgress[1]);
            Drawf.spinSprite(EndGameTurret.this.ringCRegion, this.x, this.y, this.ringProgress[2]);
            Draw.blend(Blending.additive);
            Draw.z(oz + 0.005F);
            Draw.color(1.0F, Utils.offsetSin(0.0F, 5.0F), Utils.offsetSin(90.0F, 5.0F), this.eyesAlpha);
            Draw.rect(EndGameTurret.this.bottomLightsRegion, this.x, this.y);
            Draw.color(1.0F, Utils.offsetSin(0.0F, 5.0F), Utils.offsetSin(90.0F, 5.0F), this.lightsAlpha * Utils.offsetSin(0.0F, 12.0F));
            Draw.rect(EndGameTurret.this.baseLightsRegion, this.x, this.y);
            TextureRegion[] regions = new TextureRegion[]{EndGameTurret.this.ringAEyesRegion, EndGameTurret.this.ringBEyesRegion, EndGameTurret.this.eyeMainRegion};
            TextureRegion[] regionsB = new TextureRegion[]{EndGameTurret.this.ringALightsRegion, EndGameTurret.this.ringBLightsRegion, EndGameTurret.this.ringCLightsRegion};
            float[] trnsScl = new float[]{1.0F, 0.9F, 2.0F};

            for(int i = 0; i < 3; ++i) {
                int h = i + 1;
                Draw.z(oz + 0.015F);
                Draw.color(1.0F, Utils.offsetSin(10.0F * (float)h, 5.0F), Utils.offsetSin(90.0F + 10.0F * (float)h, 5.0F), this.eyesAlpha);
                Draw.rect(regions[i], this.x + this.eyeOffset.x * trnsScl[i], this.y + this.eyeOffset.y * trnsScl[i], this.ringProgress[i]);
                Draw.z(oz + 0.025F);
                Draw.color(1.0F, Utils.offsetSin(10.0F * (float)h, 5.0F), Utils.offsetSin(90.0F + 10.0F * (float)h, 5.0F), this.lightsAlpha * Utils.offsetSin((float)(5 * h), 12.0F));
                Draw.rect(regionsB[i], this.x, this.y, this.ringProgress[i]);
            }

            Draw.blend();
            Draw.z(oz);
        }

        public boolean shouldActiveSound() {
            return (double)this.trueEfficiency() >= 1.0E-4;
        }

        void killUnits() {
            EndGameTurret.entitySeq.clear();
            Units.nearbyEnemies(this.team, this.x - EndGameTurret.this.range, this.y - EndGameTurret.this.range, EndGameTurret.this.range * 2.0F, EndGameTurret.this.range * 2.0F, (e) -> {
                if (Mathf.within(this.x, this.y, e.x, e.y, EndGameTurret.this.range) && !e.dead) {
                    Object[] data = new Object[]{new Vec2(this.x + this.eyeOffset.x, this.y + this.eyeOffset.y), e, 1.0F};
                    UnityFx.endgameLaser.at(this.x, this.y, 0.0F, data);
                    EndGameTurret.entitySeq.add(e);
                }

            });
            EndGameTurret.entitySeq.each((e) -> {
                AntiCheat.annihilateEntity(e, true, false);
                Unit u = (Unit)e;
                SpecialFx.endgameVapourize.at(u.x, u.y, this.angleTo(u), new Object[]{this, u});
            });
            EndGameTurret.entitySeq.clear();
        }

        void killTiles() {
            EndGameTurret.entitySeq.clear();
            EndGameTurret.damageB = 0.0F;
            EndGameTurret.shouldLaser = 0;
            Utils.trueEachBlock(this.x, this.y, EndGameTurret.this.range + 5.0F, (build) -> build.team != this.team, (building) -> {
                if (!building.dead && building != this) {
                    if (building.block.size >= 3) {
                        UnityFx.vapourizeTile.at(building.x, building.y, (float)building.block.size, building);
                    }

                    if (EndGameTurret.shouldLaser % 5 == 0 || building.block.size >= 5) {
                        Object[] data = new Object[]{new Vec2(this.x + this.eyeOffset.x * 2.0F, this.y + this.eyeOffset.y * 2.0F), building, 1.0F};
                        UnityFx.endgameLaser.at(this.x, this.y, 0.0F, data);
                    }

                    EndGameTurret.entitySeq.add(building);
                    EndGameTurret.shouldLaser++;
                }

            });
            EndGameTurret.entitySeq.each((e) -> {
                EndGameTurret.damageB = Math.max(((Posc)e).dst2(this), EndGameTurret.damageB);
                AntiCheat.annihilateEntity(e, true, false);
            });
            EndGameTurret.damageB = Mathf.sqrt(EndGameTurret.damageB) * 2.0F;
            SpecialFx.endgameVapourize.at(this.x, this.y, EndGameTurret.damageB, new Object[]{this, EndGameTurret.entitySeq.toArray(Building.class), this.hitSize() / 4.0F});
            EndGameTurret.entitySeq.clear();
        }

        public void kill() {
            if (this.lastHealth < 10.0F) {
                super.kill();
            }

        }

        void playerShoot(int index) {
            float rnge = 15.0F;
            float ux = this.unit.aimX();
            float uy = this.unit.aimY();
            if (Mathf.within(this.x, this.y, ux, uy, EndGameTurret.this.range * 1.5F)) {
                Utils.trueEachBlock(ux, uy, 15.0F, (b) -> b.team() != this.team && !b.dead(), (building) -> {
                    building.damage(490.0F);
                    Object[] data = new Object[]{new Vec2(ux, uy), building, 0.525F};
                    UnityFx.endgameLaser.at(this.x, this.y, 0.0F, data);
                });
                Units.nearbyEnemies(this.team, ux - 15.0F, uy - EndGameTurret.this.range, EndGameTurret.this.range * 2.0F, EndGameTurret.this.range * 2.0F, (e) -> {
                    if (e.within(ux, uy, 15.0F + e.hitSize) && !e.dead) {
                        e.damage(490.0F * this.threatLevel);
                        if (e.dead) {
                            SpecialFx.endgameVapourize.at(e.x, e.y, this.angleTo(e), new Object[]{this, e});
                            AntiCheat.annihilateEntity(e, true, false);
                        }

                        UnityFx.endgameLaser.at(this.x, this.y, 0.0F, new Object[]{new Vec2(ux, uy), e, 0.525F});
                    }

                });
                Tmp.v1.set(this.eyesVecArray[index]);
                Tmp.v1.add(ux, uy);
                Tmp.v1.scl(0.5F);
                Object[] dataB = new Object[]{this.eyesVecArray[index], new Vec2(ux, uy), 0.625F};
                UnityFx.endgameLaser.at(Tmp.v1.x, Tmp.v1.y, 0.0F, dataB);
                UnitySounds.endgameSmallShoot.at(this.x, this.y);
            }
        }

        void eyeShoot(int index) {
            Healthc e = (Healthc)this.targets[index];
            if (e != null) {
                e.damage(350.0F * this.threatLevel);
                if (e.dead()) {
                    if (e instanceof Unit) {
                        Unit ut = (Unit)e;
                        SpecialFx.endgameVapourize.at(ut.x, ut.y, this.angleTo(ut), new Object[]{this, ut});
                    }

                    if (e instanceof Building) {
                        Building build = (Building)e;
                        UnityFx.vapourizeTile.at(build.x, build.y, (float)build.block.size);
                    }

                    AntiCheat.annihilateEntity(e, true, false);
                }

                Object[] data = new Object[]{this.eyesVecArray[index], e, 0.625F};
                UnityFx.endgameLaser.at(this.x, this.y, 0.0F, data);
                UnitySounds.endgameSmallShoot.at(this.x, this.y);
            }

        }

        void updateThreats() {
            this.threatLevel = 1.0F;
            Units.nearbyEnemies(this.team, this.x - EndGameTurret.this.range, this.y - EndGameTurret.this.range, EndGameTurret.this.range * 2.0F, EndGameTurret.this.range * 2.0F, (e) -> {
                if (this.within(e, EndGameTurret.this.range) && e.isAdded()) {
                    this.threatLevel += Math.max((e.maxHealth() + e.type.dpsEstimate - 450.0F) / 1300.0F, 0.0F);
                    if (e.speed() >= 18.0F) {
                        e.vel.setLength(0.0F);
                    }
                }

            });
        }

        void updateEyesTargeting() {
            for(int i = 0; i < 16; ++i) {
                if (Units.invalidateTarget(this.targets[i], this.team, this.x, this.y)) {
                    this.targets[i] = null;
                }
            }

            this.updateThreats();
            if (this.timer.get(EndGameTurret.this.eyeTime, 15.0F) && this.target != null && !this.isControlled()) {
                Seq<Healthc> nTargets = Utils.nearbyEnemySorted(this.team, this.x, this.y, EndGameTurret.this.range, 8.0F);
                if (!nTargets.isEmpty()) {
                    for(int i = 0; i < this.targets.length; ++i) {
                        this.targets[i] = (Posc)nTargets.get(i % nTargets.size);
                    }
                }
            }

        }

        void updateEyesOffset() {
            for(int i = 0; i < 16; ++i) {
                float angleC = 45.0F * ((float)i % 8.0F);
                if (i >= 8) {
                    Tmp.v1.trns(angleC + 22.5F + this.ringProgress[1], 25.75F);
                } else {
                    Tmp.v1.trns(angleC + this.ringProgress[0], 36.75F);
                }

                this.eyesVecArray[i].set(Tmp.v1.x, Tmp.v1.y).add(this.x, this.y);
            }

        }

        void updateAntiBullets() {
            EndGameTurret.entitySeq.clear();
            if (this.trueEfficiency() > 1.0E-4F && this.timer.get(EndGameTurret.this.bulletTime, 4.0F / Math.max(this.trueEfficiency(), 0.001F))) {
                EndGameTurret.damageFull = 0.0F;
                Groups.bullet.intersect(this.x - EndGameTurret.this.range, this.y - EndGameTurret.this.range, EndGameTurret.this.range * 2.0F, EndGameTurret.this.range * 2.0F, (b) -> {
                    if (this.within(b, EndGameTurret.this.range) && b.team != this.team) {
                        EndGameTurret.damageFull += Utils.getBulletDamage(b.type);
                        BulletType current = b.type;
                        EndGameTurret.totalFrags = 1;

                        for(int i = 0; i < 16 && current.fragBullet != null; ++i) {
                            BulletType frag = current.fragBullet;
                            EndGameTurret.totalFrags *= current.fragBullets;
                            EndGameTurret.damageFull += Utils.getBulletDamage(frag) * (float)EndGameTurret.totalFrags;
                            current = frag;
                        }
                    }

                });
                Groups.bullet.intersect(this.x - EndGameTurret.this.range, this.y - EndGameTurret.this.range, EndGameTurret.this.range * 2.0F, EndGameTurret.this.range * 2.0F, (b) -> {
                    if (this.within(b, EndGameTurret.this.range) && b.team != this.team) {
                        EndGameTurret.damageB = Utils.getBulletDamage(b.type);
                        BulletType current = b.type;
                        EndGameTurret.totalFrags = 1;

                        for(int i = 0; i < 16 && current.fragBullet != null; ++i) {
                            BulletType frag = current.fragBullet;
                            EndGameTurret.totalFrags *= current.fragBullets;
                            EndGameTurret.damageB += Utils.getBulletDamage(frag) * (float)EndGameTurret.totalFrags;
                            current = frag;
                        }

                        if (EndGameTurret.damageB > 1600.0F || b.type.splashDamageRadius > 120.0F || EndGameTurret.damageFull + EndGameTurret.damageB > 13000.0F || b.owner != null && !this.within((Posc)b.owner, EndGameTurret.this.range)) {
                            EndGameTurret.entitySeq.add(b);
                            Object[] data = new Object[]{new Vec2(this.x + this.eyeOffset.x * 2.0F, this.y + this.eyeOffset.y * 2.0F), new Vec2(b.x, b.y), 0.625F};
                            UnityFx.endgameLaser.at(this.x, this.y, 0.0F, data);
                        }
                    }

                });
                if (!EndGameTurret.entitySeq.isEmpty()) {
                    UnitySounds.endgameSmallShoot.at(this.x, this.y);
                }

                EndGameTurret.entitySeq.each(Entityc::remove);
                EndGameTurret.entitySeq.clear();
            }

        }

        boolean verify() {
            return this.health < this.lastHealth - 860.0F || Float.isNaN(this.health);
        }

        void updateEyes() {
            this.updateEyesOffset();
            this.eyeOffsetB.lerpDelta(this.eyeTargetOffset, 0.12F);
            this.eyeOffset.set(this.eyeOffsetB);
            this.eyeOffset.add(Mathf.range(this.reload / EndGameTurret.this.reloadTime) / 2.0F, Mathf.range(this.reload / EndGameTurret.this.reloadTime) / 2.0F);
            this.eyeOffset.limit(2.0F);
            if ((this.target != null && !this.isControlled() || this.isControlled() && this.unit.isShooting()) && this.consValid() && this.trueEfficiency() >= 1.0E-4F) {
                float[] var10000 = this.eyeReloads;
                var10000[0] += this.deltaB();
                var10000 = this.eyeReloads;
                var10000[1] += this.deltaB();
            }

            if (this.consValid() && (double)this.trueEfficiency() > 1.0E-4) {
                this.updateEyesTargeting();
            }

            if (this.eyeReloads[0] >= 15.0F) {
                this.eyeReloads[0] = 0.0F;
                if (!this.isControlled()) {
                    if (this.targets[this.eyeSequenceA] != null) {
                        this.eyeShoot(this.eyeSequenceA);
                    }
                } else if (this.unit.isShooting()) {
                    this.playerShoot(this.eyeSequenceA);
                }

                this.eyeSequenceA = (this.eyeSequenceA + 1) % 8;
            }

            if (this.eyeReloads[1] >= 5.0F) {
                this.eyeReloads[1] = 0.0F;
                if (!this.isControlled()) {
                    if (this.targets[this.eyeSequenceB] != null) {
                        this.eyeShoot(this.eyeSequenceB + 8);
                    }
                } else if (this.unit.isShooting()) {
                    this.playerShoot(this.eyeSequenceB + 8);
                }

                this.eyeSequenceB = (this.eyeSequenceB + 1) % 8;
            }

        }

        public void updateTile() {
            this.enabled = true;
            this.lastHealth = this.health;
            this.charge = Math.max(0.0F, this.charge - Time.delta / 20.0F);
            if (this.resistTime >= 15.0F) {
                this.resist = Math.max(1.0F, this.resist - Time.delta);
            } else {
                this.resistTime += Time.delta;
            }

            this.updateEyes();
            if (this.trueEfficiency() > 1.0E-4F) {
                float value = this.eyesAlpha > this.trueEfficiency() ? 1.0F : this.trueEfficiency();
                this.eyesAlpha = Mathf.lerpDelta(this.eyesAlpha, this.trueEfficiency(), 0.06F * value);
            } else {
                this.eyesAlpha = Mathf.lerpDelta(this.eyesAlpha, 0.0F, 0.06F);
            }

            if (this.consValid()) {
                this.updateAntiBullets();
                super.updateTile();
            }

            if (this.isControlled()) {
                Player con = (Player)this.unit.controller();
                this.eyeTargetOffset.trns(this.angleTo(con.mouseX, con.mouseY), this.dst(con.mouseX, con.mouseY) / (EndGameTurret.this.range / 3.0F));
            } else if (this.target != null && this.trueEfficiency() > 1.0E-4F) {
                this.eyeTargetOffset.trns(this.angleTo(this.targetPos.x, this.targetPos.y), this.dst(this.targetPos.x, this.targetPos.y) / (EndGameTurret.this.range / 3.0F));
            }

            this.eyeTargetOffset.limit(2.0F);
            if ((this.target != null && !this.isControlled() || this.isControlled() && this.unit.isShooting()) && this.trueEfficiency() > 1.0E-4F) {
                this.eyeResetTime = 0.0F;
                float value = this.lightsAlpha > this.trueEfficiency() ? 1.0F : this.trueEfficiency();
                this.lightsAlpha = Mathf.lerpDelta(this.lightsAlpha, this.trueEfficiency(), 0.07F * value);

                for(int i = 0; i < 3; ++i) {
                    this.ringProgress[i] = Mathf.lerpDelta(this.ringProgress[i], 360.0F * (float)EndGameTurret.ringDirections[i], EndGameTurret.ringProgresses[i] * this.trueEfficiency());
                }

                float chance = (this.reload / EndGameTurret.this.reloadTime * 0.9F + 0.100000024F) * this.trueEfficiency();
                float randomAngle = Mathf.random(360.0F);
                Tmp.v1.trns(randomAngle, 18.5F);
                Tmp.v1.add(this.x, this.y);
                if (Mathf.chanceDelta((double)0.75F * (double)chance)) {
                    EndGameTurret.this.lightning.create(this.team, Tmp.v1.x, Tmp.v1.y, randomAngle, () -> 520.0F * this.trueEfficiency(), (Posc)null, this.targetPos);
                }
            } else if (this.eyeResetTime >= 60.0F) {
                this.lightsAlpha = Mathf.lerpDelta(this.lightsAlpha, 0.0F, 0.07F);

                for(int i = 0; i < 3; ++i) {
                    this.ringProgress[i] = Mathf.lerpDelta(this.ringProgress[i], 0.0F, EndGameTurret.ringProgresses[i] * this.trueEfficiency());
                }
            } else {
                this.eyeResetTime += Time.delta;
            }

        }

        protected void shoot(BulletType type) {
            this.consume();
            this.killTiles();
            this.killUnits();
            ShootFx.endGameShoot.at(this.x, this.y);
            UnitySounds.endgameShoot.at(this.x, this.y, 1.0F, 1.5F);
        }

        public boolean collision(Bullet other) {
            float amount = other.owner != null && !this.within((Posc)other.owner, EndGameTurret.this.range) ? 0.0F : other.damage() * other.type.buildingDamageMultiplier;
            this.damage(amount);
            if (other.owner != null && !this.within((Posc)other.owner, EndGameTurret.this.range)) {
                Healthc en = (Healthc)other.owner;
                en.damage(0.5F * en.maxHealth() * Math.max(this.resist / 10.0F, 1.0F));
            }

            return true;
        }

        public void add() {
            if (!this.isAdded()) {
                for(int i = 0; i < 16; ++i) {
                    this.eyesVecArray[i] = new Vec2();
                    this.targets[i] = null;
                }

                super.add();
                Unity.antiCheat.addBuilding(this);
            }
        }

        public void remove() {
            if (this.isAdded()) {
                if (this.lastHealth <= 0.0F) {
                    Unity.antiCheat.removeBuilding(this);
                }

                super.remove();
            }
        }
    }
}
