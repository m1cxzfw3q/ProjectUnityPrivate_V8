package unity.world.blocks.defense.turrets;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.struct.EnumSet;
import arc.struct.Seq;
import arc.util.Tmp;
import mindustry.entities.Units;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.gen.Groups;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.world.blocks.defense.turrets.ReloadTurret;
import mindustry.world.meta.BlockFlag;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import unity.content.UnityBullets;
import unity.entities.bullet.misc.BlockStatusEffectBulletType;
import unity.graphics.UnityPal;
import unity.world.blocks.exp.ExpHolder;

public class BlockOverdriveTurret extends ReloadTurret {
    public final int timerBullet;
    public float buffRange;
    public float buffReload;
    public float phaseBoost;
    public float phaseRangeBoost;
    public float phaseExpBoost;
    public TextureRegion baseRegion;
    public TextureRegion laserRegion;
    public TextureRegion laserEndRegion;
    public BlockStatusEffectBulletType bullet;

    public BlockOverdriveTurret(String name) {
        super(name);
        this.timerBullet = this.timers++;
        this.buffRange = 50.0F;
        this.buffReload = 180.0F;
        this.phaseBoost = 3.0F;
        this.phaseRangeBoost = 1.5F;
        this.phaseExpBoost = 2.0F;
        this.bullet = (BlockStatusEffectBulletType)UnityBullets.statusEffect;
        this.hasPower = this.hasItems = this.update = this.solid = this.outlineIcon = true;
        this.flags = EnumSet.of(new BlockFlag[]{BlockFlag.turret});
        this.group = BlockGroup.projectors;
        this.canOverdrive = false;
    }

    public void load() {
        super.load();
        this.baseRegion = Core.atlas.find(this.name + "-base");
        this.laserRegion = Core.atlas.find("exp-laser");
        this.laserEndRegion = Core.atlas.find("exp-laser-end");
    }

    public void setStats() {
        super.setStats();
        this.stats.add(Stat.range, this.buffRange / 8.0F, StatUnit.blocks);
    }

    public TextureRegion[] icons() {
        return new TextureRegion[]{this.baseRegion, this.region};
    }

    public void drawPlace(int x, int y, int rotation, boolean valid) {
        Drawf.dashCircle((float)(x * 8), (float)(y * 8), this.buffRange, Pal.accent);
        Draw.reset();
    }

    public class BlockOverdriveTurretBuild extends ReloadTurret.ReloadTurretBuild {
        public Building target;
        public float buffingTime;
        public float phaseHeat;
        public float targetTime;
        public boolean buffing;
        public boolean isExp;

        public BlockOverdriveTurretBuild() {
            super(BlockOverdriveTurret.this);
        }

        public void drawSelect() {
            Drawf.circles(this.x, this.y, BlockOverdriveTurret.this.buffRange, Pal.accent);
            if (this.buffing) {
                Drawf.selected(this.target, this.isExp ? UnityPal.exp.a(Mathf.absin(6.0F, 1.0F)) : Tmp.c1.set(Pal.heal).lerp(Color.valueOf("feb380"), Mathf.absin(9.0F, 1.0F)).a(Mathf.absin(6.0F, 1.0F)));
            }

        }

        public void draw() {
            Draw.rect(BlockOverdriveTurret.this.baseRegion, this.x, this.y);
            Draw.z(50.0F);
            Drawf.shadow(BlockOverdriveTurret.this.region, this.x - (float)BlockOverdriveTurret.this.size / 2.0F, this.y - (float)BlockOverdriveTurret.this.size / 2.0F, this.rotation - 90.0F);
            Draw.rect(BlockOverdriveTurret.this.region, this.x, this.y, this.rotation - 90.0F);
            if (this.buffing) {
                float angle = this.angleTo(this.target);
                float len = 5.0F;
                Draw.color(this.isExp ? UnityPal.exp : Tmp.c2.set(Color.valueOf("feb380")).lerp(Pal.heal, Mathf.absin(10.0F, 1.0F)));
                Draw.alpha(1.0F);
                Draw.z(31.0F);
                Drawf.laser(this.team, BlockOverdriveTurret.this.laserRegion, BlockOverdriveTurret.this.laserEndRegion, this.x + Angles.trnsx(angle, len), this.y + Angles.trnsy(angle, len), this.target.x, this.target.y, BlockOverdriveTurret.this.bullet.strength / 4.0F);
                Draw.color();
            }

        }

        public void updateTile() {
            this.phaseHeat = Mathf.lerpDelta(this.phaseHeat, (float)Mathf.num(BlockOverdriveTurret.this.hasItems && !this.items.empty()), 0.1F);
            float radius = BlockOverdriveTurret.this.buffRange + this.phaseHeat * BlockOverdriveTurret.this.phaseRangeBoost;
            this.buffing = false;
            if (this.target != null) {
                this.isExp = this.target instanceof ExpHolder;
                if (!this.targetValid(this.target)) {
                    this.target = null;
                } else if (this.consValid() && this.enabled) {
                    if (this.timer(BlockOverdriveTurret.this.timerBullet, BlockOverdriveTurret.this.buffReload)) {
                        BlockOverdriveTurret.this.bullet.create(this, this.target.x, this.target.y, 0.0F);
                        this.timer.reset(BlockOverdriveTurret.this.timerBullet, 0.0F);
                    }

                    this.rotation = Mathf.slerpDelta(this.rotation, this.angleTo(this.target), 0.5F);
                    this.buffing = true;
                }

                this.targetTime = 0.0F;
            }

            if (this.cons.optionalValid() && this.efficiency() > 0.0F) {
                this.buffingTime += this.edelta();
                if (this.buffingTime >= BlockOverdriveTurret.this.buffReload) {
                    this.consume();
                    this.buffingTime = 0.0F;
                }
            }

            if (this.consValid()) {
                this.targetTime += this.edelta();
                if (this.targetTime >= BlockOverdriveTurret.this.buffReload) {
                    this.target = Units.closestBuilding(this.team, this.x, this.y, radius, this::targetValid);
                    this.targetTime = 0.0F;
                }
            }

        }

        public boolean shouldConsume() {
            return this.target != null && this.enabled;
        }

        public boolean targetValid(Building b) {
            return b.isValid() && b.block.canOverdrive && b != this && !this.proximity.contains(b) && !this.isBeingBuffed(b) && b.enabled;
        }

        public boolean isBeingBuffed(Building b) {
            Seq<Bullet> bullets = Groups.bullet.intersect(b.x, b.y, (float)(b.block.size * 8), (float)(b.block.size * 8));
            if (bullets.size > 0) {
                return ((Bullet)bullets.get(0)).owner != this;
            } else {
                return false;
            }
        }
    }
}
