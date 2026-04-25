package unity.world.blocks.defense.turrets;

import arc.Core;
import arc.audio.Sound;
import arc.func.Floatf;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Nullable;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.UnitTypes;
import mindustry.core.World;
import mindustry.entities.Units;
import mindustry.gen.BlockUnitc;
import mindustry.gen.Building;
import mindustry.gen.Posc;
import mindustry.gen.Sounds;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.logic.LAccess;
import mindustry.logic.Senseable;
import mindustry.world.blocks.ControlBlock;
import mindustry.world.blocks.defense.turrets.BaseTurret;
import unity.graphics.UnityPal;

public abstract class GenericTractorBeamTurret<T extends Teamc> extends BaseTurret {
    public final int timerTarget;
    public float retargetTime;
    public TextureRegion baseRegion;
    public float shootCone;
    public float shootLength;
    public float powerUse;
    public float powerUseThreshold;
    public Sound shootSound;
    public float shootSoundVolume;
    public Color laserColor;
    public float laserWidth;
    public TextureRegion laser;
    public TextureRegion laserEnd;
    protected Vec2 tr;
    protected Vec2 drawTargetPos;
    protected Floatf<GenericTractorBeamTurret<T>.GenericTractorBeamTurretBuild> laserAlpha;

    protected GenericTractorBeamTurret(String name) {
        super(name);
        this.timerTarget = this.timers++;
        this.retargetTime = 5.0F;
        this.shootCone = 6.0F;
        this.shootLength = -1.0F;
        this.powerUse = 1.0F;
        this.powerUseThreshold = 0.0F;
        this.shootSound = Sounds.tractorbeam;
        this.shootSoundVolume = 0.9F;
        this.laserColor = UnityPal.monolith;
        this.laserWidth = 0.4F;
        this.tr = new Vec2();
        this.drawTargetPos = new Vec2();
        this.laserAlpha = Building::efficiency;
        this.rotateSpeed = 20.0F;
        this.hasItems = this.hasLiquids = false;
        this.hasPower = this.consumesPower = true;
        this.acceptCoolant = false;
    }

    public <E extends GenericTractorBeamTurret<T>.GenericTractorBeamTurretBuild> void laserAlpha(Floatf<E> laserAlpha) {
        this.laserAlpha = laserAlpha;
    }

    public void init() {
        this.consumes.powerCond(this.powerUse, (build) -> build.target != null);
        this.clipSize = Math.max(this.clipSize, this.range * 2.0F + (float)(this.size * 8));
        super.init();
        if (this.shootLength < 0.0F) {
            this.shootLength = (float)(this.size * 8) / 2.0F;
        }

    }

    public void load() {
        super.load();
        this.baseRegion = Core.atlas.find("block-" + this.size, "unity-block-" + this.size);
        this.laser = Core.atlas.find("laser");
        this.laserEnd = Core.atlas.find("laser-end");
    }

    protected TextureRegion[] icons() {
        return new TextureRegion[]{this.baseRegion, this.region};
    }

    public abstract class GenericTractorBeamTurretBuild extends BaseTurret.BaseTurretBuild implements ControlBlock, Senseable {
        @Nullable
        public BlockUnitc unit;
        public T target;
        public Vec2 targetPos = new Vec2();
        public float strength;
        public float logicControlTime = -1.0F;
        public boolean logicShooting = false;

        public GenericTractorBeamTurretBuild() {
            super(GenericTractorBeamTurret.this);
        }

        public void created() {
            super.created();
            this.unit = (BlockUnitc)UnitTypes.block.create(this.team).as();
            this.unit.tile(this);
        }

        public Unit unit() {
            return (Unit)this.unit.as();
        }

        public void control(LAccess type, double p1, double p2, double p3, double p4) {
            if (type == LAccess.shoot && !this.unit.isPlayer()) {
                this.targetPos.set(World.unconv((float)p1), World.unconv((float)p2));
                this.logicControlTime = 120.0F;
                this.logicShooting = !Mathf.zero(p3);
            }

            super.control(type, p1, p2, p3, p4);
        }

        public void control(LAccess type, Object p1, double p2, double p3, double p4) {
            if (type == LAccess.shootp && !this.unit.isPlayer()) {
                this.logicControlTime = 120.0F;
                this.logicShooting = !Mathf.zero(p2);
                if (p1 instanceof Posc) {
                    Posc pos = (Posc)p1;
                    this.targetPos.set(pos.x(), pos.y());
                }
            }

            super.control(type, p1, p2, p3, p4);
        }

        public double sense(LAccess sensor) {
            double var10000;
            switch (sensor) {
                case rotation:
                    var10000 = (double)this.rotation;
                    break;
                case shootX:
                    var10000 = (double)World.conv(this.targetPos.x);
                    break;
                case shootY:
                    var10000 = (double)World.conv(this.targetPos.y);
                    break;
                case shooting:
                    var10000 = this.isShooting() ? (double)1.0F : (double)0.0F;
                    break;
                default:
                    var10000 = super.sense(sensor);
            }

            return var10000;
        }

        public boolean isShooting() {
            return this.isControlled() ? this.unit.isShooting() : (this.logicControlled() ? this.logicShooting : this.canShoot() && this.target != null);
        }

        public void updateTile() {
            if (!this.validateTarget()) {
                this.target = (T)null;
            }

            if (this.target != null && (this.target.x() != 0.0F || this.target.y() != 0.0F)) {
                GenericTractorBeamTurret.this.drawTargetPos.set(this.target.x(), this.target.y());
            }

            this.unit.health(this.health);
            this.unit.rotation(this.rotation);
            this.unit.team(this.team);
            this.unit.set(this.x, this.y);
            if (this.logicControlTime > 0.0F) {
                this.logicControlTime -= Time.delta;
            }

            boolean shot = false;
            if (this.canShoot()) {
                if (!this.logicControlled() && !this.isControlled() && this.timer(GenericTractorBeamTurret.this.timerTarget, GenericTractorBeamTurret.this.retargetTime)) {
                    this.findTarget();
                }

                if (this.validateTarget()) {
                    float targetRot = this.angleTo(this.targetPos);
                    this.turnToTarget(targetRot);
                    boolean shoot = true;
                    if (this.isControlled()) {
                        this.targetPos.set(this.unit.aimX(), this.unit.aimY());
                        shoot = this.unit.isShooting();
                    } else if (this.logicControlled()) {
                        shoot = this.logicShooting;
                    } else {
                        this.targetPos.set(this.target.x(), this.target.y());
                        if (Float.isNaN(this.rotation)) {
                            this.rotation = 0.0F;
                        }
                    }

                    this.targetPos.sub(this).limit(GenericTractorBeamTurret.this.range).add(this);
                    if (shoot && Angles.angleDist(this.rotation, targetRot) < GenericTractorBeamTurret.this.shootCone) {
                        shot = true;
                        this.updateShooting();
                    }
                }
            }

            if (shot) {
                this.strength = Mathf.lerpDelta(this.strength, Mathf.clamp(this.efficiency()), 0.1F);
                if (this.strength > 0.1F && !Vars.headless) {
                    Vars.control.sound.loop(GenericTractorBeamTurret.this.shootSound, this, GenericTractorBeamTurret.this.shootSoundVolume);
                }
            } else {
                this.strength = Mathf.lerpDelta(this.strength, 0.0F, 0.1F);
            }

        }

        protected void updateShooting() {
            if (this.logicControlled() || this.isControlled()) {
                this.findTarget(this.targetPos);
            }

            if (this.target != null && this.strength > 0.1F) {
                this.apply();
            }

        }

        protected void turnToTarget(float targetRot) {
            this.rotation = Angles.moveToward(this.rotation, targetRot, GenericTractorBeamTurret.this.rotateSpeed * this.edelta());
        }

        protected boolean validateTarget() {
            return !Units.invalidateTarget(this.target, this.team, this.x, this.y) || this.isControlled() || this.logicControlled();
        }

        public boolean logicControlled() {
            return this.logicControlTime > 0.0F;
        }

        public boolean canShoot() {
            return this.efficiency() > GenericTractorBeamTurret.this.powerUseThreshold;
        }

        protected abstract void findTarget();

        protected abstract void findTarget(Vec2 var1);

        protected abstract void apply();

        public void draw() {
            Draw.rect(GenericTractorBeamTurret.this.baseRegion, this.x, this.y);
            Drawf.shadow(GenericTractorBeamTurret.this.region, this.x - (float)GenericTractorBeamTurret.this.size / 2.0F, this.y - (float)GenericTractorBeamTurret.this.size / 2.0F, this.rotation - 90.0F);
            Draw.rect(GenericTractorBeamTurret.this.region, this.x, this.y, this.rotation - 90.0F);
            if (this.strength > 0.1F) {
                GenericTractorBeamTurret.this.tr.trns(this.rotation, GenericTractorBeamTurret.this.shootLength);
                Draw.z(100.0F);
                Draw.mixcol(GenericTractorBeamTurret.this.laserColor, Mathf.absin(4.0F, 0.6F));
                Draw.alpha(this.laserAlpha());
                Drawf.laser(this.team, GenericTractorBeamTurret.this.laser, GenericTractorBeamTurret.this.laserEnd, this.x + GenericTractorBeamTurret.this.tr.x, this.y + GenericTractorBeamTurret.this.tr.y, GenericTractorBeamTurret.this.drawTargetPos.x, GenericTractorBeamTurret.this.drawTargetPos.y, this.strength * GenericTractorBeamTurret.this.laserWidth);
                Draw.mixcol();
            }

        }

        public float laserAlpha() {
            return GenericTractorBeamTurret.this.laserAlpha.get(this);
        }
    }
}
