package unity.gen;

import arc.Core;
import arc.func.Cons;
import arc.func.Cons2;
import arc.func.Func;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.util.Nullable;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.content.Bullets;
import mindustry.content.Fx;
import mindustry.content.UnitTypes;
import mindustry.entities.Effect;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.BlockUnitc;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.gen.Entityc;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.type.Liquid;
import mindustry.world.Block;
import mindustry.world.blocks.defense.turrets.LaserTurret;
import mindustry.world.blocks.defense.turrets.LiquidTurret;
import mindustry.world.blocks.defense.turrets.PowerTurret;
import mindustry.world.blocks.defense.turrets.Turret;
import mindustry.world.meta.Stat;
import unity.content.UnityBullets;
import unity.world.blocks.defense.turrets.GenericTractorBeamTurret;
import unity.world.meta.DynamicProgression;
import unity.world.meta.StemData;

public class SoulTurretPowerTurret extends PowerTurret implements Soulc, Stemc, Turretc {
    public int maxSouls = 3;
    public float efficiencyFrom = 0.3F;
    public float efficiencyTo = 1.0F;
    public boolean requireSoul = true;
    public DynamicProgression progression = new DynamicProgression();
    protected Cons<Stemc.StemBuildc> drawStem = (e) -> {
    };
    protected Cons<Stemc.StemBuildc> updateStem = (e) -> {
    };
    public Color fromColor;
    public Color toColor;
    public boolean lerpColor;
    public Color rangeColor;
    public boolean omni;
    public BulletType defaultBullet;
    public float basicFieldRadius;
    protected Func<Turretc.TurretBuildc, Object> bulletData;
    protected Cons2<BulletType, Bullet> bulletCons;

    public SoulTurretPowerTurret(String name) {
        super(name);
        this.fromColor = Pal.lancerLaser;
        this.toColor = Pal.sapBullet;
        this.lerpColor = false;
        this.omni = false;
        this.defaultBullet = UnityBullets.standardCopper;
        this.basicFieldRadius = -1.0F;
        this.bulletData = (b) -> null;
        this.bulletCons = (type, b) -> {
        };
        this.update = true;
        this.destructible = true;
        this.sync = true;
    }

    public <T extends BulletType> void bulletCons(Cons2<T, Bullet> bulletCons) {
        this.bulletCons = bulletCons;
    }

    public <T extends Turretc.TurretBuildc> void bulletData(Func<T, Object> bulletData) {
        this.bulletData = bulletData;
    }

    public void setStats() {
        super.setStats();
        this.stats.add(Stat.abilities, (cont) -> {
            cont.row();
            cont.table((bt) -> {
                bt.left().defaults().padRight(3.0F).left();
                bt.row();
                bt.add(this.requireSoul ? "@soul.require" : "@soul.optional");
                if (this.maxSouls > 0) {
                    bt.row();
                    bt.add(Core.bundle.format("soul.max", new Object[]{this.maxSouls}));
                }

            });
        });
    }

    public <T extends Stemc.StemBuildc> void draw(Cons<T> draw) {
        this.drawStem = draw;
    }

    public <T extends Stemc.StemBuildc> void update(Cons<T> update) {
        this.updateStem = update;
    }

    public int maxSouls() {
        return this.maxSouls;
    }

    public void maxSouls(int maxSouls) {
        this.maxSouls = maxSouls;
    }

    public float efficiencyFrom() {
        return this.efficiencyFrom;
    }

    public void efficiencyFrom(float efficiencyFrom) {
        this.efficiencyFrom = efficiencyFrom;
    }

    public float efficiencyTo() {
        return this.efficiencyTo;
    }

    public void efficiencyTo(float efficiencyTo) {
        this.efficiencyTo = efficiencyTo;
    }

    public boolean requireSoul() {
        return this.requireSoul;
    }

    public void requireSoul(boolean requireSoul) {
        this.requireSoul = requireSoul;
    }

    public DynamicProgression progression() {
        return this.progression;
    }

    public void progression(DynamicProgression progression) {
        this.progression = progression;
    }

    public Cons<Stemc.StemBuildc> drawStem() {
        return this.drawStem;
    }

    public Cons<Stemc.StemBuildc> updateStem() {
        return this.updateStem;
    }

    public Color fromColor() {
        return this.fromColor;
    }

    public void fromColor(Color fromColor) {
        this.fromColor = fromColor;
    }

    public Color toColor() {
        return this.toColor;
    }

    public void toColor(Color toColor) {
        this.toColor = toColor;
    }

    public boolean lerpColor() {
        return this.lerpColor;
    }

    public void lerpColor(boolean lerpColor) {
        this.lerpColor = lerpColor;
    }

    public Color rangeColor() {
        return this.rangeColor;
    }

    public void rangeColor(Color rangeColor) {
        this.rangeColor = rangeColor;
    }

    public boolean omni() {
        return this.omni;
    }

    public void omni(boolean omni) {
        this.omni = omni;
    }

    public BulletType defaultBullet() {
        return this.defaultBullet;
    }

    public void defaultBullet(BulletType defaultBullet) {
        this.defaultBullet = defaultBullet;
    }

    public float basicFieldRadius() {
        return this.basicFieldRadius;
    }

    public void basicFieldRadius(float basicFieldRadius) {
        this.basicFieldRadius = basicFieldRadius;
    }

    public Func<Turretc.TurretBuildc, Object> bulletData() {
        return this.bulletData;
    }

    public Cons2<BulletType, Bullet> bulletCons() {
        return this.bulletCons;
    }

    public class SoulTurretPowerTurretBuild extends PowerTurret.PowerTurretBuild implements Soulc.SoulBuildc, Turretc.TurretBuildc, Stemc.StemBuildc {
        @Nullable
        public transient BlockUnitc unit;
        private int souls;
        protected transient StemData data = new StemData();

        public SoulTurretPowerTurretBuild() {
            super(SoulTurretPowerTurret.this);
        }

        public String toString() {
            return "SoulTurretPowerTurretBuild#" + this.id;
        }

        public void onRemoved() {
            super.onRemoved();
            if (Vars.net.server() || !Vars.net.active()) {
                this.spreadSouls();
            }

        }

        public void effects() {
            if (this instanceof Expc.ExpBuildc && SoulTurretPowerTurret.this.lerpColor) {
                Turret.TurretBuild exp = (Turret.TurretBuild)((Expc.ExpBuildc)this);
                this.recoil = SoulTurretPowerTurret.this.recoilAmount;
                Effect shoot = SoulTurretPowerTurret.this.shootEffect == Fx.none ? this.peekAmmo().shootEffect : SoulTurretPowerTurret.this.shootEffect;
                Effect smoke = SoulTurretPowerTurret.this.smokeEffect == Fx.none ? this.peekAmmo().smokeEffect : SoulTurretPowerTurret.this.smokeEffect;
                shoot.at(this.x + SoulTurretPowerTurret.this.tr.x, this.y + SoulTurretPowerTurret.this.tr.y, this.rotation, this.getShootColor(((Expc.ExpBuildc)exp).levelf()));
                smoke.at(this.x + SoulTurretPowerTurret.this.tr.x, this.y + SoulTurretPowerTurret.this.tr.y, this.rotation);
                SoulTurretPowerTurret.this.shootSound.at(this.x + SoulTurretPowerTurret.this.tr.x, this.y + SoulTurretPowerTurret.this.tr.y, Mathf.random(0.9F, 1.1F));
                if (SoulTurretPowerTurret.this.shootShake > 0.0F) {
                    Effect.shake(SoulTurretPowerTurret.this.shootShake, SoulTurretPowerTurret.this.shootShake, this);
                }
            } else {
                super.effects();
            }

        }

        public void unjoin() {
            if (this.souls > 0) {
                --this.souls;
            }

        }

        public Unit unit() {
            return (Unit)this.unit.as();
        }

        public boolean hasAmmo() {
            if (this.self() instanceof LiquidTurret.LiquidTurretBuild && SoulTurretPowerTurret.this.omni) {
                return this.liquids.total() >= 1.0F / this.peekAmmo().ammoMultiplier;
            } else {
                return super.hasAmmo();
            }
        }

        public void update() {
            SoulTurretPowerTurret.this.progression.apply((float)this.souls);
            super.update();
        }

        public Object bulletData() {
            return SoulTurretPowerTurret.this.bulletData.get(this);
        }

        public void drawSelect() {
            Drawf.dashCircle(this.x, this.y, SoulTurretPowerTurret.this.range, SoulTurretPowerTurret.this.rangeColor == null ? this.team.color : SoulTurretPowerTurret.this.rangeColor);
        }

        public void write(Writes write) {
            super.write(write);
            write.i(this.souls);
            this.data.write(write);
        }

        public boolean apply(MonolithSoul soul, int index, boolean transferred) {
            if (this.isControlled() && !transferred && (Mathf.chance((double)(1.0F / (float)this.souls)) || index == this.souls - 1)) {
                soul.controller(this.unit.getPlayer());
                transferred = true;
            }

            return transferred;
        }

        public void updateTile() {
            super.updateTile();
            SoulTurretPowerTurret.this.updateStem.get(this);
        }

        public int maxSouls() {
            return SoulTurretPowerTurret.this.maxSouls;
        }

        public BulletType useAmmo() {
            if (this.self() instanceof LiquidTurret.LiquidTurretBuild && SoulTurretPowerTurret.this.omni) {
                BulletType b = this.peekAmmo();
                this.liquids.remove(this.liquids.current(), 1.0F / b.ammoMultiplier);
                return b;
            } else {
                return super.useAmmo();
            }
        }

        public int souls() {
            return this.souls;
        }

        public void shoot(BulletType type) {
            if (SoulTurretPowerTurret.this.chargeTime > 0.0F && this instanceof Expc.ExpBuildc) {
                Turret.TurretBuild exp = (Turret.TurretBuild)((Expc.ExpBuildc)this);
                this.useAmmo();
                float lvl = ((Expc.ExpBuildc)exp).levelf();
                SoulTurretPowerTurret.this.tr.trns(this.rotation, SoulTurretPowerTurret.this.shootLength);
                SoulTurretPowerTurret.this.chargeBeginEffect.at(this.x + SoulTurretPowerTurret.this.tr.x, this.y + SoulTurretPowerTurret.this.tr.y, this.rotation, this.getShootColor(lvl));
                SoulTurretPowerTurret.this.chargeSound.at(this.x + SoulTurretPowerTurret.this.tr.x, this.y + SoulTurretPowerTurret.this.tr.y, 1.0F);

                for(int i = 0; i < SoulTurretPowerTurret.this.chargeEffects; ++i) {
                    Time.run(Mathf.random(SoulTurretPowerTurret.this.chargeMaxDelay), () -> {
                        if (this.isValid()) {
                            SoulTurretPowerTurret.this.tr.trns(this.rotation, SoulTurretPowerTurret.this.shootLength);
                            SoulTurretPowerTurret.this.chargeEffect.at(this.x + SoulTurretPowerTurret.this.tr.x, this.y + SoulTurretPowerTurret.this.tr.y, this.rotation, this.getShootColor(lvl));
                        }

                    });
                }

                this.charging = true;

                for(int i = 0; i < SoulTurretPowerTurret.this.shots; ++i) {
                    Time.run(SoulTurretPowerTurret.this.burstSpacing * (float)i, () -> Time.run(SoulTurretPowerTurret.this.chargeTime, () -> {
                        if (this.isValid()) {
                            SoulTurretPowerTurret.this.tr.trns(this.rotation, SoulTurretPowerTurret.this.shootLength, Mathf.range(SoulTurretPowerTurret.this.xRand));
                            this.recoil = SoulTurretPowerTurret.this.recoilAmount;
                            this.heat = 1.0F;
                            this.bullet(type, this.rotation + Mathf.range(SoulTurretPowerTurret.this.inaccuracy));
                            this.effects();
                            this.charging = false;
                        }

                    }));
                }
            } else {
                super.shoot(type);
            }

        }

        public void read(Reads read, byte revision) {
            super.read(read, revision);
            this.souls = read.i();
            this.data.read(read);
        }

        public void created() {
            super.created();
            Entityc var3 = this.self();
            if (var3 instanceof Turret.TurretBuild) {
                Turret.TurretBuild build = (Turret.TurretBuild)var3;
                this.unit = build.unit;
            } else {
                var3 = this.self();
                if (var3 instanceof GenericTractorBeamTurret.GenericTractorBeamTurretBuild) {
                    GenericTractorBeamTurret<?>.GenericTractorBeamTurretBuild build = (GenericTractorBeamTurret.GenericTractorBeamTurretBuild)var3;
                    this.unit = build.unit;
                } else {
                    this.unit = (BlockUnitc)UnitTypes.block.create(this.team).as();
                    this.unit.tile(this);
                }
            }

        }

        public float efficiency() {
            float result = 1.0F;
            return SoulTurretPowerTurret.this.requireSoul && this.disabled() ? 0.0F : super.efficiency() * result * ((float)this.souls / (float)SoulTurretPowerTurret.this.maxSouls * (SoulTurretPowerTurret.this.efficiencyTo - SoulTurretPowerTurret.this.efficiencyFrom) + SoulTurretPowerTurret.this.efficiencyFrom);
        }

        public void join() {
            if (this.canJoin()) {
                ++this.souls;
            }

        }

        public BulletType peekAmmo() {
            Block var2 = this.block;
            if (var2 instanceof LiquidTurret) {
                LiquidTurret l = (LiquidTurret)var2;
                if (SoulTurretPowerTurret.this.omni) {
                    BulletType b = (BulletType)l.ammoTypes.get(this.liquids.current());
                    return b == null ? SoulTurretPowerTurret.this.defaultBullet : b;
                }
            }

            return super.peekAmmo();
        }

        public void bullet(BulletType type, float angle) {
            Bullet bullet = type.create(this, this.team, this.x + SoulTurretPowerTurret.this.tr.x, this.y + SoulTurretPowerTurret.this.tr.y, angle, type.damage, 1.0F + Mathf.range(SoulTurretPowerTurret.this.velocityInaccuracy), type.scaleVelocity ? Mathf.clamp(Mathf.dst(this.x + SoulTurretPowerTurret.this.tr.x, this.y + SoulTurretPowerTurret.this.tr.y, this.targetPos.x, this.targetPos.y) / type.range(), SoulTurretPowerTurret.this.minRange / type.range(), SoulTurretPowerTurret.this.range / type.range()) : 1.0F, this.bulletData());
            this.bulletCons(type, bullet);
            Entityc var6 = this.self();
            if (var6 instanceof LaserTurret.LaserTurretBuild) {
                LaserTurret.LaserTurretBuild laser = (LaserTurret.LaserTurretBuild)var6;
                Block var7 = this.block;
                if (var7 instanceof LaserTurret) {
                    LaserTurret turret = (LaserTurret)var7;
                    laser.bullet = bullet;
                    laser.bulletLife = turret.shootDuration;
                }
            }

        }

        public boolean acceptLiquid(Building source, Liquid liquid) {
            if (this.self() instanceof LiquidTurret.LiquidTurretBuild && SoulTurretPowerTurret.this.omni) {
                return this.liquids.current() == liquid || this.liquids.currentAmount() < 0.2F;
            } else {
                return super.acceptLiquid(source, liquid);
            }
        }

        public void draw() {
            super.draw();
            SoulTurretPowerTurret.this.drawStem.get(this);
        }

        public void bulletCons(BulletType type, Bullet b) {
            SoulTurretPowerTurret.this.bulletCons.get(type, b);
        }

        public Color getShootColor(float progress) {
            return Tmp.c1.set(SoulTurretPowerTurret.this.fromColor).lerp(SoulTurretPowerTurret.this.toColor, progress).cpy();
        }

        public boolean disabled() {
            return !this.hasSouls();
        }

        public void unit(BlockUnitc unit) {
            this.unit = unit;
        }

        public StemData data() {
            return this.data;
        }
    }
}
