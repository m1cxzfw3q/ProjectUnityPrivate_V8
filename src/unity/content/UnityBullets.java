package unity.content;

import arc.func.Cons;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Position;
import arc.math.geom.Vec2;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.content.Bullets;
import mindustry.content.Fx;
import mindustry.content.Liquids;
import mindustry.content.StatusEffects;
import mindustry.content.UnitTypes;
import mindustry.entities.Damage;
import mindustry.entities.Effect;
import mindustry.entities.Lightning;
import mindustry.entities.Puddles;
import mindustry.entities.Units;
import mindustry.entities.bullet.ArtilleryBulletType;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.bullet.ContinuousLaserBulletType;
import mindustry.entities.bullet.LaserBulletType;
import mindustry.entities.bullet.LightningBulletType;
import mindustry.entities.bullet.MissileBulletType;
import mindustry.entities.bullet.PointBulletType;
import mindustry.entities.bullet.ShrapnelBulletType;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.gen.Entityc;
import mindustry.gen.Healthc;
import mindustry.gen.Hitboxc;
import mindustry.gen.Sounds;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.type.Liquid;
import mindustry.type.Weapon;
import mindustry.world.Tile;
import unity.content.effects.ChargeFx;
import unity.content.effects.HitFx;
import unity.content.effects.ShootFx;
import unity.entities.bullet.anticheat.AntiCheatBulletTypeBase;
import unity.entities.bullet.anticheat.EndBasicBulletType;
import unity.entities.bullet.anticheat.EndContinuousLaserBulletType;
import unity.entities.bullet.anticheat.EndPointBlastLaserBulletType;
import unity.entities.bullet.anticheat.VoidAreaBulletType;
import unity.entities.bullet.anticheat.modules.AbilityDamageModule;
import unity.entities.bullet.anticheat.modules.AntiCheatBulletModule;
import unity.entities.bullet.anticheat.modules.ArmorDamageModule;
import unity.entities.bullet.anticheat.modules.ForceFieldDamageModule;
import unity.entities.bullet.energy.BeamBulletType;
import unity.entities.bullet.energy.FlameBulletType;
import unity.entities.bullet.energy.GluonOrbBulletType;
import unity.entities.bullet.energy.GluonWhirlBulletType;
import unity.entities.bullet.energy.ShieldBulletType;
import unity.entities.bullet.energy.SingularityBulletType;
import unity.entities.bullet.energy.SmokeBulletType;
import unity.entities.bullet.energy.TriangleBulletType;
import unity.entities.bullet.exp.DistFieldBulletType;
import unity.entities.bullet.exp.ExpBasicBulletType;
import unity.entities.bullet.exp.ExpBulletType;
import unity.entities.bullet.exp.ExpLaserBlastBulletType;
import unity.entities.bullet.exp.ExpLaserBulletType;
import unity.entities.bullet.exp.ExpLaserFieldBulletType;
import unity.entities.bullet.exp.GeyserBulletType;
import unity.entities.bullet.kami.CircleBulletType;
import unity.entities.bullet.kami.KamiAltLaserBulletType;
import unity.entities.bullet.kami.KamiBulletType;
import unity.entities.bullet.kami.KamiLaserBulletType;
import unity.entities.bullet.kami.NewKamiLaserBulletType;
import unity.entities.bullet.laser.SparkingContinuousLaserBulletType;
import unity.entities.bullet.misc.BlockStatusEffectBulletType;
import unity.entities.bullet.monolith.energy.RicochetBulletType;
import unity.gen.UnitySounds;
import unity.graphics.FixedTrail;
import unity.graphics.UnityPal;
import unity.world.blocks.exp.ExpTurret;

public class UnityBullets {
    public static BulletType laser;
    public static BulletType shardLaserFrag;
    public static BulletType shardLaser;
    public static BulletType frostLaser;
    public static BulletType branchLaserFrag;
    public static BulletType branchLaser;
    public static BulletType distField;
    public static BulletType smallDistField;
    public static BulletType fractalLaser;
    public static BulletType kelvinWaterLaser;
    public static BulletType kelvinSlagLaser;
    public static BulletType kelvinOilLaser;
    public static BulletType kelvinCryofluidLaser;
    public static BulletType kelvinLiquidLaser;
    public static BulletType celsiusSmoke;
    public static BulletType kelvinSmoke;
    public static BulletType breakthroughLaser;
    public static BulletType laserGeyser;
    public static BulletType basicMissile;
    public static BulletType citadelFlame;
    public static BulletType sapLaser;
    public static BulletType sapArtilleryFrag;
    public static BulletType continuousSapLaser;
    public static BulletType coalBlaze;
    public static BulletType pyraBlaze;
    public static BulletType falloutLaser;
    public static BulletType catastropheLaser;
    public static BulletType calamityLaser;
    public static BulletType extinctionLaser;
    public static BulletType plagueMissile;
    public static BulletType gluonWhirl;
    public static BulletType gluonEnergyBall;
    public static BulletType singularityBlackHole;
    public static BulletType singularityEnergyBall;
    public static BulletType orb;
    public static BulletType shockBeam;
    public static BulletType currentStroke;
    public static BulletType shielderBullet;
    public static BulletType plasmaFragTriangle;
    public static BulletType plasmaTriangle;
    public static BulletType surgeBomb;
    public static BulletType pylonLightning;
    public static BulletType pylonLaser;
    public static BulletType pylonLaserSmall;
    public static BulletType monumentRailBullet;
    public static BulletType scarShrapnel;
    public static BulletType scarMissile;
    public static BulletType kamiBullet1;
    public static BulletType kamiBullet2;
    public static BulletType kamiBullet3;
    public static BulletType kamiLaser;
    public static BulletType kamiLaser2;
    public static BulletType kamiVariableLaser;
    public static BulletType kamiSmallLaser;
    public static BulletType ricochetSmall;
    public static BulletType ricochetMedium;
    public static BulletType ricochetBig;
    public static BulletType stopLead;
    public static BulletType stopMonolite;
    public static BulletType stopSilicon;
    public static BulletType supernovaLaser;
    public static BulletType endLightning;
    public static BulletType ravagerLaser;
    public static BulletType ravagerArtillery;
    public static BulletType oppressionArea;
    public static BulletType oppressionShell;
    public static BulletType missileAntiCheat;
    public static BulletType endLaserSmall;
    public static BulletType endLaser;
    public static BulletType laserZap;
    public static BulletType plasmaBullet;
    public static BulletType phantasmalBullet;
    public static BulletType teleportLightning;
    public static BulletType statusEffect;
    public static BulletType upgradeEffect;
    public static BasicBulletType standardDenseLarge;
    public static BasicBulletType standardHomingLarge;
    public static BasicBulletType standardIncendiaryLarge;
    public static BasicBulletType standardThoriumLarge;
    public static BasicBulletType standardDenseHeavy;
    public static BasicBulletType standardHomingHeavy;
    public static BasicBulletType standardIncendiaryHeavy;
    public static BasicBulletType standardThoriumHeavy;
    public static BasicBulletType standardDenseMassive;
    public static BasicBulletType standardHomingMassive;
    public static BasicBulletType standardIncendiaryMassive;
    public static BasicBulletType standardThoriumMassive;
    public static BasicBulletType reignBulletWeakened;
    public static ArtilleryBulletType artilleryExplosiveT2;

    private static <T extends BulletType> T copy(BulletType from, Cons<T> setter) {
        T bullet = from.copy();
        setter.get(bullet);
        return bullet;
    }

    private static <T extends BulletType> T deepCopy(BulletType from, Cons<T> setter) {
        T bullet = from.copy();
        if (from.fragBullet != null) {
            bullet.fragBullet = deepCopy(bullet.fragBullet, (b) -> {
            });
        }

        setter.get(bullet);
        return bullet;
    }

    public static void load() {
        laser = new ExpLaserBulletType(150.0F, 30.0F) {
            {
                this.damageInc = 7.0F;
                this.status = StatusEffects.shocked;
                this.statusDuration = 180.0F;
                this.expGain = this.buildingExpGain = 2;
                this.fromColor = Pal.accent;
                this.toColor = Pal.lancerLaser;
            }
        };
        shardLaserFrag = new ExpBasicBulletType(2.0F, 10.0F) {
            {
                this.lifetime = 20.0F;
                this.pierceCap = 10;
                this.pierceBuilding = true;
                this.backColor = Color.white.cpy().lerp(Pal.lancerLaser, 0.1F);
                this.frontColor = Color.white;
                this.hitEffect = Fx.none;
                this.despawnEffect = Fx.none;
                this.smokeEffect = Fx.hitLaser;
                this.hittable = false;
                this.reflectable = false;
                this.lightColor = Color.white;
                this.lightOpacity = 0.6F;
                this.expChance = 0.15F;
                this.fromColor = Pal.lancerLaser;
                this.toColor = Pal.sapBullet;
            }

            public void draw(Bullet b) {
                Draw.color(this.getColor(b));
                Lines.stroke(2.0F * b.fout(0.7F) + 0.01F);
                Lines.lineAngleCenter(b.x, b.y, b.rotation(), 8.0F);
                Lines.stroke(1.3F * b.fout(0.7F) + 0.01F);
                Draw.color(this.frontColor);
                Lines.lineAngleCenter(b.x, b.y, b.rotation(), 5.0F);
                Draw.reset();
            }
        };
        shardLaser = new ExpLaserBulletType(150.0F, 30.0F) {
            {
                this.status = StatusEffects.shocked;
                this.statusDuration = 180.0F;
                this.fragBullet = UnityBullets.shardLaserFrag;
                this.expGain = this.buildingExpGain = 2;
                this.damageInc = 5.0F;
                this.fromColor = Pal.lancerLaser;
                this.toColor = Pal.sapBullet;
            }
        };
        frostLaser = new ExpLaserBulletType(170.0F, 130.0F) {
            {
                this.status = StatusEffects.freezing;
                this.statusDuration = 180.0F;
                this.shootEffect = UnityFx.shootFlake;
                this.expGain = 2;
                this.buildingExpGain = 3;
                this.damageInc = 2.5F;
                this.fromColor = Liquids.cryofluid.color;
                this.toColor = Color.cyan;
                this.blip = true;
            }

            public void handleExp(Bullet b, float x, float y, int amount) {
                super.handleExp(b, x, y, amount);
                this.freezePos(b, x, y);
            }

            public void freezePos(Bullet b, float x, float y) {
                int lvl = this.getLevel(b);
                float rad = 3.5F;
                UnityFx.freezeEffect.at(x, y, (float)lvl / rad + 10.0F, this.getColor(b));
                UnitySounds.laserFreeze.at(x, y);
                Damage.status(b.team, x, y, 10.0F + (float)lvl / rad, this.status, 60.0F + (float)lvl * 6.0F, true, true);
                Damage.status(b.team, x, y, 10.0F + (float)lvl / rad, UnityStatusEffects.disabled, 2.0F * (float)lvl, true, true);
            }
        };
        branchLaserFrag = new ExpBulletType(3.5F, 15.0F) {
            {
                this.trailWidth = 2.0F;
                this.weaveScale = 0.6F;
                this.weaveMag = 0.5F;
                this.homingPower = 0.4F;
                this.lifetime = 30.0F;
                this.shootEffect = Fx.hitLancer;
                this.hitEffect = this.despawnEffect = HitFx.branchFragHit;
                this.pierceCap = 10;
                this.pierceBuilding = true;
                this.splashDamageRadius = 4.0F;
                this.splashDamage = 4.0F;
                this.status = UnityStatusEffects.plasmaed;
                this.statusDuration = 180.0F;
                this.trailLength = 6;
                this.trailColor = Color.white;
                this.fromColor = Pal.lancerLaser.cpy().lerp(Pal.sapBullet, 0.5F);
                this.toColor = Pal.sapBullet;
                this.expGain = 1;
                this.expOnHit = true;
            }

            public void init() {
                super.init();
                this.despawnHit = false;
            }

            public void draw(Bullet b) {
                this.drawTrail(b);
                Draw.color(this.getColor(b));
                Fill.square(b.x, b.y, this.trailWidth, b.rotation() + 45.0F);
                Draw.color();
            }
        };
        branchLaser = new ExpLaserBulletType(140.0F, 20.0F) {
            {
                this.status = StatusEffects.shocked;
                this.statusDuration = 180.0F;
                this.fragBullets = 3;
                this.fragBullet = UnityBullets.branchLaserFrag;
                this.maxRange = 210.0F;
                this.expGain = this.buildingExpGain = 1;
                this.damageInc = 6.0F;
                this.lengthInc = 2.0F;
                this.fromColor = Pal.lancerLaser.cpy().lerp(Pal.sapBullet, 0.5F);
                this.toColor = Pal.sapBullet;
                this.hitMissed = true;
            }
        };
        distField = new DistFieldBulletType(0.0F, -1.0F) {
            {
                this.centerColor = Pal.lancerLaser.cpy().a(0.0F);
                this.edgeColor = Pal.place;
                this.distSplashFx = UnityFx.distSplashFx;
                this.distStart = UnityFx.distStart;
                this.distStatus = UnityStatusEffects.distort;
                this.collidesTiles = false;
                this.collides = false;
                this.collidesAir = false;
                this.keepVelocity = false;
                this.lifetime = 360.0F;
                this.radius = 24.0F;
                this.radiusInc = 0.8F;
                this.bulletSlow = 0.1F;
                this.bulletSlowInc = 0.025F;
                this.damageLimit = 100.0F;
                this.distDamage = 0.1F;
                this.expChance = 0.0033333334F;
                this.expGain = 1;
            }
        };
        smallDistField = new DistFieldBulletType(0.0F, -1.0F) {
            {
                this.centerColor = Pal.lancerLaser.cpy().a(0.0F);
                this.edgeColor = Pal.place;
                this.distSplashFx = UnityFx.distSplashFx;
                this.distStart = UnityFx.distStart;
                this.distStatus = UnityStatusEffects.distort;
                this.collidesTiles = false;
                this.collides = false;
                this.collidesAir = false;
                this.keepVelocity = false;
                this.lifetime = 150.0F;
                this.radius = 12.0F;
                this.radiusInc = 0.4F;
                this.bulletSlow = 0.05F;
                this.bulletSlowInc = 0.015F;
                this.damageLimit = 50.0F;
                this.distDamage = 0.05F;
                this.expChance = 0.0016666667F;
                this.expGain = 1;
            }
        };
        fractalLaser = new ExpLaserFieldBulletType(170.0F, 130.0F) {
            {
                this.damageInc = 6.0F;
                this.lengthInc = 2.0F;
                this.fields = 2;
                this.fieldInc = 0.15F;
                this.width = 2.0F;
                this.expGain = this.buildingExpGain = 1;
                this.fromColor = Pal.lancerLaser.cpy().lerp(Pal.place, 0.5F);
                this.toColor = Pal.place;
                this.maxRange = 210.0F;
                this.distField = UnityBullets.distField;
                this.smallDistField = UnityBullets.smallDistField;
            }
        };
        laserGeyser = new GeyserBulletType() {
            {
                this.damageInc = 2.0F;
            }
        };
        kelvinWaterLaser = new ExpLaserBulletType(170.0F, 130.0F) {
            {
                this.damageInc = 7.0F;
                this.status = StatusEffects.wet;
                this.statusDuration = 180.0F;
                this.knockback = 10.0F;
                this.expGain = 2;
                this.buildingExpGain = 3;
                this.fromColor = Liquids.water.color;
                this.toColor = Color.sky;
            }
        };
        kelvinSlagLaser = new ExpLaserBulletType(170.0F, 130.0F) {
            {
                this.damageInc = 7.0F;
                this.status = StatusEffects.burning;
                this.statusDuration = 180.0F;
                this.expGain = 2;
                this.buildingExpGain = 3;
                this.puddles = 10;
                this.puddleRange = 4.0F;
                this.puddleAmount = 15.0F;
                this.puddleLiquid = Liquids.slag;
                this.fromColor = Liquids.slag.color;
                this.toColor = Color.orange;
            }

            public void makeLava(float x, float y, Float level) {
                for(int i = 0; i < this.puddles; ++i) {
                    Tile tile = Vars.world.tileWorld(x + Mathf.range(this.puddleRange), y + Mathf.range(this.puddleRange));
                    Puddles.deposit(tile, this.puddleLiquid, this.puddleAmount + level * 2.0F);
                }

            }

            public void init(Bullet b) {
                super.init(b);
                Object var3 = b.data;
                if (var3 instanceof Position) {
                    Position point = (Position)var3;
                    this.makeLava(point.getX(), point.getY(), this.getLevelf(b));
                }

            }
        };
        kelvinOilLaser = new ExpLaserBulletType(170.0F, 130.0F) {
            {
                this.damageInc = 7.0F;
                this.status = StatusEffects.burning;
                this.statusDuration = 180.0F;
                this.expGain = 2;
                this.buildingExpGain = 3;
                this.puddles = 10;
                this.puddleRange = 4.0F;
                this.puddleAmount = 15.0F;
                this.puddleLiquid = Liquids.oil;
                this.fromColor = Liquids.oil.color;
                this.toColor = Color.darkGray;
            }

            public void makeLava(float x, float y, Float level) {
                for(int i = 0; i < this.puddles; ++i) {
                    Tile tile = Vars.world.tileWorld(x + Mathf.range(this.puddleRange), y + Mathf.range(this.puddleRange));
                    Puddles.deposit(tile, this.puddleLiquid, this.puddleAmount + level * 2.0F);
                }

            }

            public void init(Bullet b) {
                super.init(b);
                Object var3 = b.data;
                if (var3 instanceof Position) {
                    Position point = (Position)var3;
                    this.makeLava(point.getX(), point.getY(), this.getLevelf(b));
                }

            }
        };
        kelvinCryofluidLaser = new ExpLaserBulletType(170.0F, 130.0F) {
            {
                this.damageInc = 3.0F;
                this.status = StatusEffects.freezing;
                this.statusDuration = 180.0F;
                this.expGain = 2;
                this.buildingExpGain = 3;
                this.shootEffect = UnityFx.shootFlake;
                this.fromColor = Liquids.cryofluid.color;
                this.toColor = Color.cyan;
            }

            public void freezePos(Bullet b, float x, float y) {
                int lvl = this.getLevel(b);
                float rad = 4.5F;
                if (!Vars.headless) {
                    UnityFx.freezeEffect.at(x, y, (float)lvl / rad + 10.0F, this.getColor(b));
                }

                if (!Vars.headless) {
                    UnitySounds.laserFreeze.at(x, y, 1.0F, 0.6F);
                }

                Damage.status(b.team, x, y, 10.0F + (float)lvl / rad, this.status, 60.0F + (float)lvl * 7.5F, true, true);
                Damage.status(b.team, x, y, 10.0F + (float)lvl / rad, UnityStatusEffects.disabled, 4.5F * (float)lvl, true, true);
            }

            public void init(Bullet b) {
                super.init(b);
                this.setDamage(b);
                Healthc target = Damage.linecast(b, b.x, b.y, b.rotation(), this.getLength(b));
                b.data = target;
                if (target instanceof Hitboxc) {
                    Hitboxc hit = (Hitboxc)target;
                    hit.collision(b, hit.x(), hit.y());
                    b.collision(hit, hit.x(), hit.y());
                    this.freezePos(b, hit.x(), hit.y());
                    Entityc var6 = b.owner;
                    if (var6 instanceof ExpTurret.ExpTurretBuild) {
                        ExpTurret.ExpTurretBuild exp = (ExpTurret.ExpTurretBuild)var6;
                        exp.handleExp(this.expGain);
                    }
                } else {
                    if (target instanceof Building) {
                        Building tile = (Building)target;
                        if (tile.collide(b)) {
                            tile.collision(b);
                            this.hit(b, tile.x, tile.y);
                            this.freezePos(b, tile.x, tile.y);
                            Entityc var8 = b.owner;
                            if (var8 instanceof ExpTurret.ExpTurretBuild) {
                                ExpTurret.ExpTurretBuild exp = (ExpTurret.ExpTurretBuild)var8;
                                exp.handleExp(this.buildingExpGain);
                            }

                            return;
                        }
                    }

                    b.data = (new Vec2()).trns(b.rotation(), this.length).add(b.x, b.y);
                }

            }
        };
        kelvinLiquidLaser = new ExpLaserBulletType(170.0F, 130.0F) {
            final float damageMultiplier = 150.0F;
            final float damageMultiplierInc = 10.0F;

            {
                this.status = StatusEffects.freezing;
                this.statusDuration = 180.0F;
                this.expGain = 2;
                this.buildingExpGain = 3;
                this.shootEffect = UnityFx.shootFlake;
                this.fromColor = Liquids.cryofluid.color;
                this.toColor = Color.cyan;
            }

            public void setDamage(Bullet b) {
                Liquid liquid = Liquids.cryofluid;
                Entityc var4 = b.owner;
                if (var4 instanceof Building) {
                    Building build = (Building)var4;
                    if (!build.cheating()) {
                        liquid = build.liquids.current();
                    }
                }

                float mul = 150.0F + 10.0F * (float)this.getLevel(b);
                b.damage = liquid.heatCapacity * mul * b.damageMultiplier();
            }

            void freezePos(Bullet b, float x, float y) {
                int lvl = this.getLevel(b);
                float rad = 4.5F;
                if (!Vars.headless) {
                    UnityFx.freezeEffect.at(x, y, (float)lvl / rad + 10.0F, this.getColor(b));
                }

                if (!Vars.headless) {
                    UnitySounds.laserFreeze.at(x, y, 1.0F, 0.6F);
                }

                Damage.status(b.team, x, y, 10.0F + (float)lvl / rad, this.status, 60.0F + (float)lvl * 8.0F, true, true);
                Damage.status(b.team, x, y, 10.0F + (float)lvl / rad, UnityStatusEffects.disabled, 3.0F * (float)lvl, true, true);
            }

            public void init(Bullet b) {
                super.init(b);
                this.setDamage(b);
                Healthc target = Damage.linecast(b, b.x, b.y, b.rotation(), this.getLength(b));
                b.data = target;
                if (target instanceof Hitboxc) {
                    Hitboxc hit = (Hitboxc)target;
                    hit.collision(b, hit.x(), hit.y());
                    b.collision(hit, hit.x(), hit.y());
                    this.freezePos(b, hit.x(), hit.y());
                    Entityc var6 = b.owner;
                    if (var6 instanceof ExpTurret.ExpTurretBuild) {
                        ExpTurret.ExpTurretBuild exp = (ExpTurret.ExpTurretBuild)var6;
                        exp.handleExp(this.expGain);
                    }
                } else {
                    if (target instanceof Building) {
                        Building tile = (Building)target;
                        if (tile.collide(b)) {
                            tile.collision(b);
                            this.hit(b, tile.x, tile.y);
                            this.freezePos(b, tile.x, tile.y);
                            Entityc var8 = b.owner;
                            if (var8 instanceof ExpTurret.ExpTurretBuild) {
                                ExpTurret.ExpTurretBuild exp = (ExpTurret.ExpTurretBuild)var8;
                                exp.handleExp(this.buildingExpGain);
                            }

                            return;
                        }
                    }

                    b.data = (new Vec2()).trns(b.rotation(), this.length).add(b.x, b.y);
                }

            }
        };
        breakthroughLaser = new ExpLaserBlastBulletType(500.0F, 1200.0F) {
            {
                this.damageInc = 1000.0F;
                this.lengthInc = 150.0F;
                this.largeHit = true;
                this.width = 80.0F;
                this.widthInc = 10.0F;
                this.lifetime = 65.0F;
                this.lightningSpacingInc = -5.0F;
                this.lightningDamageInc = 30.0F;
                this.hitUnitExpGain = 1;
                this.hitBuildingExpGain = 1;
                this.sideLength = 0.0F;
                this.sideWidth = 0.0F;
            }
        };
        coalBlaze = new ExpBulletType(3.35F, 32.0F) {
            {
                this.ammoMultiplier = 3.0F;
                this.hitSize = 7.0F;
                this.lifetime = 24.0F;
                this.pierce = true;
                this.statusDuration = 240.0F;
                this.shootEffect = ShootFx.shootSmallBlaze;
                this.hitEffect = Fx.hitFlameSmall;
                this.despawnEffect = Fx.none;
                this.status = StatusEffects.burning;
                this.keepVelocity = true;
                this.hittable = false;
                this.expOnHit = true;
                this.expChance = 0.5F;
            }
        };
        pyraBlaze = new ExpBulletType(3.35F, 46.0F) {
            {
                this.ammoMultiplier = 3.0F;
                this.hitSize = 7.0F;
                this.lifetime = 24.0F;
                this.pierce = true;
                this.statusDuration = 240.0F;
                this.shootEffect = ShootFx.shootPyraBlaze;
                this.hitEffect = Fx.hitFlameSmall;
                this.despawnEffect = Fx.none;
                this.status = StatusEffects.burning;
                this.keepVelocity = false;
                this.hittable = false;
                this.expOnHit = true;
                this.expChance = 0.6F;
            }
        };
        basicMissile = new MissileBulletType(4.2F, 15.0F) {
            {
                this.homingPower = 0.12F;
                this.width = 8.0F;
                this.height = 8.0F;
                this.shrinkX = this.shrinkY = 0.0F;
                this.drag = -0.003F;
                this.homingRange = 80.0F;
                this.keepVelocity = false;
                this.splashDamageRadius = 35.0F;
                this.splashDamage = 30.0F;
                this.lifetime = 62.0F;
                this.trailColor = Pal.missileYellowBack;
                this.hitEffect = Fx.blastExplosion;
                this.despawnEffect = Fx.blastExplosion;
                this.weaveScale = 8.0F;
                this.weaveMag = 2.0F;
            }
        };
        citadelFlame = new FlameBulletType(4.2F, 50.0F) {
            {
                this.lifetime = 20.0F;
                this.particleAmount = 17;
            }
        };
        sapArtilleryFrag = new ArtilleryBulletType(2.3F, 30.0F) {
            {
                this.hitEffect = Fx.sapExplosion;
                this.knockback = 0.8F;
                this.lifetime = 70.0F;
                this.width = this.height = 20.0F;
                this.collidesTiles = false;
                this.splashDamageRadius = 70.0F;
                this.splashDamage = 60.0F;
                this.backColor = Pal.sapBulletBack;
                this.frontColor = this.lightningColor = Pal.sapBullet;
                this.lightning = 2;
                this.lightningLength = 5;
                this.smokeEffect = Fx.shootBigSmoke2;
                this.hitShake = 5.0F;
                this.lightRadius = 30.0F;
                this.lightColor = Pal.sap;
                this.lightOpacity = 0.5F;
                this.status = StatusEffects.sapped;
                this.statusDuration = 600.0F;
            }
        };
        sapLaser = new LaserBulletType(80.0F) {
            {
                this.colors = new Color[]{Pal.sapBulletBack.cpy().a(0.4F), Pal.sapBullet, Color.white};
                this.length = 150.0F;
                this.width = 25.0F;
                this.sideLength = this.sideWidth = 0.0F;
                this.shootEffect = ShootFx.sapPlasmaShoot;
                this.hitColor = this.lightColor = this.lightningColor = Pal.sapBullet;
                this.status = StatusEffects.sapped;
                this.statusDuration = 80.0F;
                this.lightningSpacing = 17.0F;
                this.lightningDelay = 0.12F;
                this.lightningDamage = 15.0F;
                this.lightningLength = 4;
                this.lightningLengthRand = 2;
                this.lightningAngleRand = 15.0F;
            }
        };
        continuousSapLaser = new ContinuousLaserBulletType(60.0F) {
            {
                this.colors = new Color[]{Pal.sapBulletBack.cpy().a(0.3F), Pal.sapBullet.cpy().a(0.6F), Pal.sapBullet, Color.white};
                this.length = 190.0F;
                this.width = 5.0F;
                this.shootEffect = ShootFx.sapPlasmaShoot;
                this.hitColor = this.lightColor = this.lightningColor = Pal.sapBullet;
                this.hitEffect = HitFx.coloredHitSmall;
                this.status = StatusEffects.sapped;
                this.statusDuration = 80.0F;
                this.lifetime = 180.0F;
                this.incendChance = 0.0F;
                this.largeHit = false;
            }

            public void hitTile(Bullet b, Building build, float initialHealth, boolean direct) {
                super.hitTile(b, build, initialHealth, direct);
                Entityc var6 = b.owner;
                if (var6 instanceof Healthc) {
                    Healthc owner = (Healthc)var6;
                    owner.heal(Math.max(initialHealth - build.health(), 0.0F) * 0.2F);
                }

            }

            public void hitEntity(Bullet b, Hitboxc entity, float health) {
                super.hitEntity(b, entity, health);
                if (entity instanceof Healthc) {
                    Healthc h = (Healthc)entity;
                    Entityc var6 = b.owner;
                    if (var6 instanceof Healthc) {
                        Healthc owner = (Healthc)var6;
                        owner.heal(Math.max(health - h.health(), 0.0F) * 0.2F);
                    }
                }

            }
        };
        falloutLaser = new SparkingContinuousLaserBulletType(95.0F) {
            {
                this.length = 230.0F;
                this.fromBlockChance = 0.12F;
                this.fromBlockDamage = 23.0F;
                this.fromLaserAmount = 0;
                this.incendChance = 0.0F;
                this.fromBlockLen = 2;
                this.fromBlockLenRand = 5;
            }
        };
        catastropheLaser = new SparkingContinuousLaserBulletType(240.0F) {
            {
                this.length = 340.0F;
                this.strokes = new float[]{2.8F, 2.1F, 1.4F, 0.42000002F};
                this.incendSpread = 7.0F;
                this.incendAmount = 2;
            }
        };
        calamityLaser = new SparkingContinuousLaserBulletType(580.0F) {
            {
                this.length = 450.0F;
                this.strokes = new float[]{3.4F, 2.5500002F, 1.7F, 0.51000005F};
                this.lightStroke = 70.0F;
                this.spaceMag = 70.0F;
                this.fromBlockChance = 0.5F;
                this.fromBlockDamage = 34.0F;
                this.fromLaserChance = 0.8F;
                this.fromLaserDamage = 32.0F;
                this.fromLaserAmount = 3;
                this.fromLaserLen = 5;
                this.fromLaserLenRand = 7;
                this.incendChance = 0.6F;
                this.incendSpread = 9.0F;
                this.incendAmount = 2;
            }
        };
        extinctionLaser = new SparkingContinuousLaserBulletType(770.0F) {
            {
                this.length = 560.0F;
                this.strokes = new float[]{4.4F, 3.3000002F, 2.2F, 0.66F};
                this.lightStroke = 90.0F;
                this.spaceMag = 70.0F;
                this.fromBlockChance = 0.5F;
                this.fromBlockDamage = 76.0F;
                this.fromBlockAmount = 4;
                this.fromLaserChance = 0.8F;
                this.fromLaserDamage = 46.0F;
                this.fromLaserAmount = 4;
                this.fromLaserLen = 10;
                this.fromLaserLenRand = 7;
                this.incendChance = 0.7F;
                this.incendSpread = 9.0F;
                this.incendAmount = 2;
                this.extinction = true;
            }
        };
        plagueMissile = new MissileBulletType(3.8F, 12.0F) {
            {
                this.width = this.height = 8.0F;
                this.backColor = this.hitColor = this.lightColor = this.trailColor = UnityPal.plagueDark;
                this.frontColor = UnityPal.plague;
                this.shrinkY = 0.0F;
                this.drag = -0.01F;
                this.splashDamage = 30.0F;
                this.splashDamageRadius = 35.0F;
                this.hitEffect = Fx.blastExplosion;
                this.despawnEffect = Fx.blastExplosion;
            }
        };
        gluonWhirl = new GluonWhirlBulletType(4.0F) {
            {
                this.lifetime = 300.0F;
                this.hitSize = 12.0F;
            }
        };
        gluonEnergyBall = new GluonOrbBulletType(8.6F, 10.0F) {
            {
                this.lifetime = 50.0F;
                this.drag = 0.03F;
                this.hitSize = 9.0F;
            }

            public void despawned(Bullet b) {
                super.despawned(b);
                UnityBullets.gluonWhirl.create(b, b.x, b.y, 0.0F);
            }
        };
        singularityBlackHole = new SingularityBulletType(26.0F) {
            {
                this.lifetime = 210.0F;
                this.hitSize = 19.0F;
            }
        };
        singularityEnergyBall = new BasicBulletType(6.6F, 7.0F) {
            {
                this.lifetime = 110.0F;
                this.drag = 0.018F;
                this.pierce = this.pierceBuilding = true;
                this.hitSize = 9.0F;
                this.despawnEffect = this.hitEffect = Fx.none;
            }

            public void update(Bullet b) {
                super.update(b);
                if (Units.closestTarget(b.team, b.x, b.y, 20.0F) != null) {
                    b.remove();
                }

                if (b.timer.get(0, 2.0F + b.fslope() * 1.5F)) {
                    UnityFx.lightHexagonTrail.at(b.x, b.y, 1.0F + b.fslope() * 4.0F, this.backColor);
                }

            }

            public void despawned(Bullet b) {
                super.despawned(b);
                UnityBullets.singularityBlackHole.create(b, b.x, b.y, 0.0F);
            }

            public void draw(Bullet b) {
                Draw.color(Pal.lancerLaser);
                Fill.circle(b.x, b.y, 7.0F + b.fout() * 1.5F);
                Draw.color(Color.white);
                Fill.circle(b.x, b.y, 5.5F + b.fout());
            }
        };
        orb = new BulletType() {
            {
                this.lifetime = 240.0F;
                this.speed = 1.24F;
                this.damage = 23.0F;
                this.pierce = true;
                this.hittable = false;
                this.hitEffect = HitFx.orbHit;
                this.trailEffect = UnityFx.orbTrail;
                this.trailChance = 0.4F;
            }

            public void draw(Bullet b) {
                Drawf.light(b.x, b.y, 16.0F, Pal.surge, 0.6F);
                Draw.color(Pal.surge);
                Fill.circle(b.x, b.y, 4.0F);
                Draw.color();
                Fill.circle(b.x, b.y, 2.5F);
            }

            public void update(Bullet b) {
                super.update(b);
                if (b.timer.get(1, 7.0F)) {
                    Units.nearbyEnemies(b.team, b.x - 40.0F, b.y - 40.0F, 80.0F, 80.0F, (unit) -> Lightning.create(b.team, Pal.surge, (float)Mathf.random(17, 33), b.x, b.y, b.angleTo(unit), Mathf.random(7, 13)));
                }

            }

            public void drawLight(Bullet b) {
            }
        };
        shockBeam = new BeamBulletType(120.0F, 35.0F) {
            {
                this.status = StatusEffects.shocked;
                this.statusDuration = 180.0F;
                this.beamWidth = 0.62F;
                this.hitEffect = Fx.hitLiquid;
                this.castsLightning = true;
                this.minLightningDamage = this.damage / 1.8F;
                this.maxLightningDamage = this.damage / 1.2F;
                this.color = Pal.surge;
            }
        };
        currentStroke = new LaserBulletType(450.0F) {
            {
                this.lifetime = 65.0F;
                this.width = 20.0F;
                this.length = 430.0F;
                this.lightningSpacing = 35.0F;
                this.lightningLength = 5;
                this.lightningDelay = 1.1F;
                this.lightningLengthRand = 15;
                this.lightningDamage = 50.0F;
                this.lightningAngleRand = 40.0F;
                this.largeHit = true;
                this.lightColor = this.lightningColor = Pal.surge;
                this.sideAngle = 15.0F;
                this.sideWidth = 0.0F;
                this.sideLength = 0.0F;
                this.colors = new Color[]{Pal.surge.cpy(), Pal.surge, Color.white};
            }
        };
        shielderBullet = new ShieldBulletType(8.0F) {
            {
                this.drag = 0.03F;
                this.shootEffect = Fx.none;
                this.despawnEffect = Fx.none;
                this.collides = false;
                this.hitSize = 0.0F;
                this.hittable = false;
                this.hitEffect = Fx.hitLiquid;
                this.breakSound = Sounds.wave;
                this.maxRadius = 10.0F;
                this.shieldHealth = 3000.0F;
            }
        };
        plasmaFragTriangle = new TriangleBulletType(11.0F, 10.0F, 4.5F, 90.0F) {
            {
                this.lifetime = 160.0F;
                this.lifetimeRand = 40.0F;
                this.trailWidth = 3.0F;
                this.trailLength = 8;
                this.drag = 0.05F;
                this.collides = false;
                this.castsLightning = true;
                this.shootEffect = UnityFx.plasmaFragAppear;
                this.hitEffect = this.despawnEffect = UnityFx.plasmaFragDisappear;
            }
        };
        plasmaTriangle = new TriangleBulletType(13.0F, 10.0F, 4.0F, 380.0F) {
            {
                this.lifetime = 180.0F;
                this.trailWidth = 3.5F;
                this.trailLength = 14;
                this.homingPower = 0.06F;
                this.hitSound = Sounds.plasmaboom;
                this.hitEffect = HitFx.plasmaTriangleHit;
                this.despawnEffect = Fx.none;
                this.fragBullet = UnityBullets.plasmaFragTriangle;
                this.fragBullets = 8;
            }
        };
        surgeBomb = new BasicBulletType(7.0F, 100.0F) {
            {
                this.width = this.height = 30.0F;
                this.sprite = "large-bomb";
                this.backColor = Pal.surge;
                this.frontColor = Color.white;
                this.mixColorTo = Color.white;
                this.hitSound = Sounds.plasmaboom;
                this.despawnShake = 4.0F;
                this.collidesAir = false;
                this.lifetime = 70.0F;
                this.despawnEffect = UnityFx.surgeSplash;
                this.hitEffect = Fx.massiveExplosion;
                this.keepVelocity = false;
                this.spin = 2.0F;
                this.shrinkX = this.shrinkY = 0.7F;
                this.collides = false;
                this.splashDamage = 680.0F;
                this.splashDamageRadius = 120.0F;
                this.fragBullets = 8;
                this.fragLifeMin = 0.8F;
                this.fragLifeMax = 1.1F;
                this.scaleVelocity = true;
                this.fragBullet = UnityBullets.plasmaFragTriangle;
                this.lightning = 10;
                this.lightningDamage = 136.0F;
                this.lightningLength = 20;
            }
        };
        pylonLightning = new LightningBulletType() {
            {
                this.lightningLength = 32;
                this.lightningLengthRand = 12;
                this.damage = 56.0F;
            }
        };
        pylonLaser = new LaserBulletType(2000.0F) {
            {
                this.length = 520.0F;
                this.width = 60.0F;
                this.lifetime = 72.0F;
                this.largeHit = true;
                this.sideLength = this.sideWidth = 0.0F;
                this.shootEffect = UnityFx.pylonLaserCharge;
            }

            public void init(Bullet b) {
                super.init(b);

                for(int i = 0; i < 24; ++i) {
                    Time.run(2.0F * (float)i, () -> {
                        UnityBullets.pylonLightning.create(b, b.x, b.y, b.vel().angle());
                        Sounds.spark.at(b.x, b.y, Mathf.random(0.6F, 0.9F));
                    });
                }

            }
        };
        pylonLaserSmall = new LaserBulletType(192.0F) {
            {
                this.lifetime = 24.0F;
                this.length = 180.0F;
                this.width = 24.0F;
                this.sideAngle = 60.0F;
                this.shootEffect = ShootFx.phantasmalLaserShoot;
            }
        };
        monumentRailBullet = new PointBulletType() {
            {
                this.damage = 6000.0F;
                this.buildingDamageMultiplier = 0.8F;
                this.speed = this.maxRange = 540.0F;
                this.lifetime = 1.0F;
                this.hitShake = 6.0F;
                this.trailSpacing = 35.0F;
                this.shootEffect = ShootFx.monumentShoot;
                this.despawnEffect = UnityFx.monumentDespawn;
                this.smokeEffect = Fx.blastExplosion;
                this.trailEffect = UnityFx.monumentTrail;
            }
        };
        scarShrapnel = new ShrapnelBulletType() {
            {
                this.fromColor = UnityPal.endColor;
                this.toColor = UnityPal.scarColor;
                this.damage = 1.0F;
                this.length = 110.0F;
            }
        };
        scarMissile = new MissileBulletType(6.0F, 12.0F) {
            {
                this.lifetime = 70.0F;
                this.speed = 5.0F;
                this.width = 7.0F;
                this.height = 12.0F;
                this.shrinkY = 0.0F;
                this.backColor = this.trailColor = UnityPal.scarColor;
                this.frontColor = UnityPal.endColor;
                this.splashDamage = 36.0F;
                this.splashDamageRadius = 20.0F;
                this.weaveMag = 3.0F;
                this.weaveScale = 6.0F;
                this.pierceBuilding = true;
                this.pierceCap = 3;
            }
        };
        celsiusSmoke = new SmokeBulletType(4.7F, 32.0F) {
            {
                this.drag = 0.034F;
                this.lifetime = 18.0F;
                this.hitSize = 4.0F;
                this.shootEffect = Fx.none;
                this.smokeEffect = Fx.none;
                this.hitEffect = HitFx.hitAdvanceFlame;
                this.despawnEffect = Fx.none;
                this.collides = true;
                this.collidesTiles = true;
                this.collidesAir = true;
                this.pierce = true;
                this.statusDuration = 770.0F;
                this.status = UnityStatusEffects.blueBurn;
            }
        };
        kelvinSmoke = new SmokeBulletType(4.7F, 16.0F) {
            {
                this.drag = 0.016F;
                this.lifetime = 32.0F;
                this.hitSize = 4.0F;
                this.shootEffect = Fx.none;
                this.smokeEffect = Fx.none;
                this.hitEffect = HitFx.hitAdvanceFlame;
                this.despawnEffect = Fx.none;
                this.collides = true;
                this.collidesTiles = true;
                this.collidesAir = true;
                this.pierce = true;
                this.statusDuration = 770.0F;
                this.status = UnityStatusEffects.blueBurn;
            }
        };
        kamiBullet1 = new CircleBulletType(4.0F, 7.0F) {
            {
                this.lifetime = 240.0F;
                this.hitSize = 6.0F;
                this.despawnEffect = Fx.none;
                this.pierce = true;
                this.keepVelocity = false;
                this.color = (b) -> Tmp.c1.set(Color.red).shiftHue(b.time * 3.0F).cpy();
            }
        };
        kamiBullet2 = new KamiBulletType() {
            {
                this.lifetime = 240.0F;
                this.hitSize = 6.0F;
                this.despawnEffect = Fx.none;
                this.trailLength = 12;
            }
        };
        kamiBullet3 = new KamiBulletType() {
            {
                this.lifetime = 240.0F;
                this.hitSize = 6.0F;
                this.despawnEffect = Fx.none;
            }
        };
        kamiLaser = new KamiLaserBulletType(230.0F) {
            {
                this.lifetime = 240.0F;
                this.length = 760.0F;
                this.width = 140.0F;
                this.fadeInTime = 60.0F;
                this.drawSize = (this.length + this.width * 2.0F) * 2.0F;
            }
        };
        kamiLaser2 = new NewKamiLaserBulletType() {
            {
                this.damage = 200.0F;
                this.despawnEffect = Fx.none;
            }
        };
        kamiVariableLaser = new KamiAltLaserBulletType(60.0F);
        kamiSmallLaser = new KamiLaserBulletType(230.0F) {
            {
                this.lifetime = 120.0F;
                this.length = 760.0F;
                this.width = 20.0F;
                this.fadeInTime = 15.0F;
                this.curveScl = 3.0F;
                this.drawSize = (this.length + this.width * 2.0F) * 2.0F;
            }
        };
        ricochetSmall = new RicochetBulletType(7.0F, 80.0F) {
            {
                this.width = 9.0F;
                this.height = 12.0F;
                this.ammoMultiplier = 4.0F;
                this.lifetime = 30.0F;
                this.trailEffect = UnityFx.ricochetTrailSmall;
                this.frontColor = Color.white;
                this.backColor = this.trailColor = Pal.lancerLaser;
            }
        };
        ricochetMedium = new RicochetBulletType(8.5F, 168.0F) {
            {
                this.width = 12.0F;
                this.height = 16.0F;
                this.ammoMultiplier = 4.0F;
                this.lifetime = 35.0F;
                this.pierceCap = 5;
                this.trailLength = 7;
                this.trailEffect = UnityFx.ricochetTrailMedium;
                this.frontColor = Color.white;
                this.backColor = this.trailColor = Pal.lancerLaser;
            }
        };
        ricochetBig = new RicochetBulletType(10.0F, 528.0F) {
            {
                this.width = 14.0F;
                this.height = 18.0F;
                this.ammoMultiplier = 4.0F;
                this.lifetime = 40.0F;
                this.pierceCap = 8;
                this.trailLength = 8;
                this.trailEffect = UnityFx.ricochetTrailBig;
                this.frontColor = Color.white;
                this.backColor = this.trailColor = Pal.lancerLaser;
            }
        };
        stopLead = new BasicBulletType(3.6F, 72.0F, "shell") {
            {
                this.width = 9.0F;
                this.height = 12.0F;
                this.ammoMultiplier = 4.0F;
                this.lifetime = 60.0F;
                this.frontColor = Color.white;
                this.backColor = Pal.lancerLaser;
                this.status = StatusEffects.unmoving;
                this.statusDuration = 5.0F;
            }
        };
        stopMonolite = new BasicBulletType(4.0F, 100.0F, "shell") {
            {
                this.width = 9.0F;
                this.height = 12.0F;
                this.ammoMultiplier = 4.0F;
                this.lifetime = 60.0F;
                this.frontColor = Color.white;
                this.backColor = Pal.lancerLaser;
                this.status = StatusEffects.unmoving;
                this.statusDuration = 8.0F;
            }
        };
        stopSilicon = new BasicBulletType(4.0F, 72.0F, "shell") {
            {
                this.width = 9.0F;
                this.height = 12.0F;
                this.ammoMultiplier = 4.0F;
                this.lifetime = 60.0F;
                this.frontColor = Color.white;
                this.backColor = Pal.lancerLaser;
                this.status = StatusEffects.unmoving;
                this.statusDuration = 16.0F;
                this.homingPower = 0.08F;
            }
        };
        supernovaLaser = new ContinuousLaserBulletType(3200.0F) {
            final Effect plasmaEffect;

            {
                this.length = 280.0F;
                this.colors = new Color[]{Color.valueOf("4be3ca55"), Color.valueOf("91eedeaa"), Pal.lancerLaser.cpy(), Color.white};
                this.plasmaEffect = new Effect(36.0F, (e) -> {
                    Draw.color(Color.white, Pal.lancerLaser, e.fin());
                    Fill.circle(e.x + Angles.trnsx(e.rotation, e.fin() * 24.0F), e.y + Angles.trnsy(e.rotation, e.fin() * 24.0F), e.fout() * 5.0F);
                });
            }

            public void update(Bullet b) {
                super.update(b);
                if (b.timer(2, 1.0F)) {
                    float start = Mathf.randomSeed((long)((float)b.id + Time.time), this.length);
                    Lightning.create(b.team, Pal.lancerLaser, 12.0F, b.x + Angles.trnsx(b.rotation(), start), b.y + Angles.trnsy(b.rotation(), start), b.rotation() + Mathf.randomSeedRange((long)((float)b.id + Time.time + 1.0F), 15.0F), Mathf.randomSeed((long)((float)b.id + Time.time + 2.0F), 10, 19));
                }

                for(int i = 0; i < 2; ++i) {
                    float f = Mathf.random(this.length * b.fout());
                    this.plasmaEffect.at(b.x + Angles.trnsx(b.rotation(), f) + Mathf.range(6.0F), b.y + Angles.trnsy(b.rotation(), f) + Mathf.range(6.0F), b.rotation() + Mathf.range(85.0F));
                }

            }
        };
        endLightning = new AntiCheatBulletTypeBase(0.0F, 0.0F) {
            {
                this.lifetime = Fx.lightning.lifetime;
                this.hitColor = UnityPal.scarColor;
                this.hitEffect = HitFx.coloredHitSmall;
                this.despawnEffect = Fx.none;
                this.status = StatusEffects.shocked;
                this.statusDuration = 10.0F;
                this.hittable = false;
                this.ratioStart = 15000.0F;
                this.ratioDamage = 0.016666668F;
            }
        };
        ravagerLaser = new EndPointBlastLaserBulletType(1210.0F) {
            {
                this.length = 460.0F;
                this.width = 26.1F;
                this.lifetime = 25.0F;
                this.widthReduction = 6.0F;
                this.auraWidthReduction = 4.0F;
                this.damageRadius = 110.0F;
                this.auraDamage = 9000.0F;
                this.overDamage = 500000.0F;
                this.ratioDamage = 0.033333335F;
                this.ratioStart = 12000.0F;
                this.bleedDuration = 600.0F;
                this.hitEffect = HitFx.voidHit;
                this.laserColors = new Color[]{UnityPal.scarColorAlpha, UnityPal.scarColor, UnityPal.endColor, Color.black};
                this.modules = new AntiCheatBulletModule[]{new ArmorDamageModule(0.06666667F, 5.0F, 70.0F, 8.0F), new AbilityDamageModule(50.0F, 400.0F, 4.0F, 0.04F, 5.0F), new ForceFieldDamageModule(10.0F, 30.0F, 230.0F, 8.0F, 0.025F)};
            }
        };
        ravagerArtillery = new ArtilleryBulletType(4.0F, 130.0F) {
            {
                this.lifetime = 110.0F;
                this.splashDamage = 325.0F;
                this.splashDamageRadius = 140.0F;
                this.width = this.height = 21.0F;
                this.backColor = this.lightColor = this.trailColor = UnityPal.scarColor;
                this.frontColor = this.lightningColor = UnityPal.endColor;
                this.lightning = 5;
                this.lightningLength = 10;
                this.lightningLengthRand = 5;
                this.fragBullets = 7;
                this.fragLifeMin = 0.9F;
                this.hitEffect = HitFx.endHitRedBig;
                this.fragBullet = new EndBasicBulletType(5.6F, 180.0F) {
                    {
                        this.lifetime = 20.0F;
                        this.pierce = this.pierceBuilding = true;
                        this.pierceCap = 5;
                        this.backColor = this.lightColor = UnityPal.scarColor;
                        this.frontColor = UnityPal.endColor;
                        this.width = this.height = 16.0F;
                        this.overDamage = 950000.0F;
                        this.ratioDamage = 0.0025F;
                        this.ratioStart = 3000.0F;
                    }
                };
            }
        };
        oppressionArea = new VoidAreaBulletType(95.0F) {
            {
                this.lifetime = 300.0F;
                this.bleedDuration = 30.0F;
                this.ratioDamage = 0.005F;
                this.ratioStart = 600000.0F;
                this.status = UnityStatusEffects.weaken;
                this.statusDuration = 30.0F;
                this.radius = 120.0F;
                this.modules = new AntiCheatBulletModule[]{new ForceFieldDamageModule(5.0F, 10.0F, 1000.0F, 1.0F, 0.02F, 180.0F), new AbilityDamageModule(10.0F, 300.0F, 10.0F, 0.016666668F, 2.0F)};
            }
        };
        oppressionShell = new EndBasicBulletType(7.0F, 410.0F, "shell") {
            {
                this.lifetime = 95.0F;
                this.splashDamage = 125.0F;
                this.splashDamageRadius = 70.0F;
                this.width = 18.0F;
                this.height = 23.0F;
                this.backColor = this.lightColor = this.trailColor = UnityPal.scarColor;
                this.frontColor = this.lightningColor = UnityPal.endColor;
                this.lightning = 5;
                this.lightningLength = 10;
                this.lightningLengthRand = 5;
                this.lightningType = UnityBullets.endLightning;
                this.despawnEffect = HitFx.endHitRedBig;
                this.pierceCap = 3;
                this.pierce = this.pierceBuilding = true;
                this.bleedDuration = 300.0F;
            }
        };
        missileAntiCheat = new EndBasicBulletType(4.0F, 330.0F, "missile") {
            {
                this.lifetime = 60.0F;
                this.width = this.height = 12.0F;
                this.shrinkY = 0.0F;
                this.drag = -0.013F;
                this.splashDamageRadius = 45.0F;
                this.splashDamage = 220.0F;
                this.homingPower = 0.08F;
                this.trailChance = 0.2F;
                this.weaveScale = 6.0F;
                this.weaveMag = 1.0F;
                this.overDamage = 900000.0F;
                this.ratioDamage = 0.006666667F;
                this.ratioStart = 2000.0F;
                this.hitEffect = HitFx.endHitRedSmoke;
                this.backColor = this.lightColor = this.trailColor = UnityPal.scarColor;
                this.frontColor = UnityPal.endColor;
            }
        };
        endLaserSmall = new EndContinuousLaserBulletType(85.0F) {
            {
                this.lifetime = 120.0F;
                this.length = 230.0F;

                for(int i = 0; i < this.strokes.length; ++i) {
                    float[] var10000 = this.strokes;
                    var10000[i] *= 0.4F;
                }

                this.overDamage = 800000.0F;
                this.ratioDamage = 0.025F;
                this.ratioStart = 1000000.0F;
                this.colors = new Color[]{UnityPal.scarColorAlpha, UnityPal.scarColor, UnityPal.endColor, Color.white};
                this.modules = new AntiCheatBulletModule[]{new ArmorDamageModule(0.1F, 30.0F, 30.0F, 0.4F)};
                this.hitEffect = HitFx.endHitRedSmall;
            }
        };
        endLaser = new EndContinuousLaserBulletType(2400.0F) {
            {
                this.length = 340.0F;
                this.lifetime = 300.0F;
                this.incendChance = -1.0F;
                this.shootEffect = ChargeFx.devourerChargeEffect;
                this.keepVelocity = true;
                this.lightColor = this.lightningColor = this.hitColor = UnityPal.scarColor;
                this.colors = new Color[]{UnityPal.scarColorAlpha, UnityPal.scarColor, UnityPal.endColor, Color.white};
                this.lightningDamage = 80.0F;
                this.lightningChance = 0.8F;
                this.lightningLength = (int)(this.length / 8.0F);
                this.lightningLengthRand = 5;
                this.overDamage = 650000.0F;
                this.overDamagePower = 2.7F;
                this.overDamageScl = 4000.0F;
                this.ratioDamage = 0.0125F;
                this.ratioStart = 19000.0F;
                this.bleedDuration = 600.0F;
                this.pierceShields = true;
                this.hitEffect = HitFx.endHitRedBig;
                this.modules = new AntiCheatBulletModule[]{new ArmorDamageModule(0.001F, 3.0F, 15.0F, 2.0F), new AbilityDamageModule(50.0F, 300.0F, 4.0F, 0.001F, 3.0F), new ForceFieldDamageModule(4.0F, 20.0F, 230.0F, 8.0F, 0.025F)};
            }
        };
        laserZap = new LaserBulletType(90.0F) {
            {
                this.sideAngle = 15.0F;
                this.sideWidth = 1.5F;
                this.sideLength = 60.0F;
                this.width = 16.0F;
                this.length = 215.0F;
                this.shootEffect = Fx.shockwave;
                this.colors = new Color[]{Pal.lancerLaser.cpy().mul(1.0F, 1.0F, 1.0F, 0.7F), Pal.lancerLaser, Color.white};
            }
        };
        plasmaBullet = new BasicBulletType(3.5F, 15.0F) {
            {
                this.frontColor = Pal.lancerLaser.cpy().lerp(Color.white, 0.5F);
                this.backColor = Pal.lancerLaser.cpy().lerp(Pal.sapBullet, 0.5F).mul(0.7F);
                this.width = this.height = 2.0F;
                this.weaveScale = 0.6F;
                this.weaveMag = 0.5F;
                this.homingPower = 0.4F;
                this.lifetime = 80.0F;
                this.shootEffect = Fx.hitLancer;
                this.hitEffect = this.despawnEffect = Fx.hitLancer;
                this.pierceCap = 10;
                this.pierceBuilding = true;
                this.splashDamageRadius = 4.0F;
                this.splashDamage = 4.0F;
                this.status = UnityStatusEffects.plasmaed;
                this.statusDuration = 180.0F;
                this.inaccuracy = 25.0F;
            }

            public void init(Bullet b) {
                b.data = new FixedTrail(9);
            }

            public void draw(Bullet b) {
                Object var3 = b.data;
                if (var3 instanceof FixedTrail) {
                    FixedTrail trail = (FixedTrail)var3;
                    trail.draw(this.frontColor, this.width);
                }

                Draw.color(this.frontColor);
                Fill.square(b.x, b.y, this.width, b.rotation() + 45.0F);
                Draw.color();
            }

            public void update(Bullet b) {
                super.update(b);
                Object var3 = b.data;
                if (var3 instanceof FixedTrail) {
                    FixedTrail trail = (FixedTrail)var3;
                    trail.update(b.x, b.y, b.rotation());
                }

            }

            public void hit(Bullet b, float x, float y) {
                super.hit(b, x, y);
                Object var5 = b.data;
                if (var5 instanceof FixedTrail) {
                    FixedTrail trail = (FixedTrail)var5;
                    UnityFx.fixedTrailFade.at(b.x, b.y, this.width, this.frontColor, trail.copy());
                    trail.clear();
                }

            }
        };
        phantasmalBullet = new BasicBulletType(6.0F, 32.0F) {
            {
                this.width = 6.0F;
                this.height = 12.0F;
                this.shrinkY = 0.3F;
                this.lifetime = 45.0F;
                this.frontColor = Color.white;
                this.backColor = Pal.lancerLaser;
                this.shootEffect = Fx.shootSmall;
                this.smokeEffect = Fx.shootSmallSmoke;
                this.hitEffect = Fx.flakExplosion;
                this.lightning = 3;
                this.lightningColor = Pal.lancerLaser;
                this.lightningLength = 6;
            }
        };
        teleportLightning = new LightningBulletType() {
            {
                this.damage = 12.0F;
                this.shootEffect = Fx.hitLancer;
                this.smokeEffect = Fx.none;
                this.despawnEffect = Fx.none;
                this.hitEffect = Fx.hitLancer;
                this.keepVelocity = false;
            }
        };
        statusEffect = new BlockStatusEffectBulletType(0.0F, 0.0F) {
            {
                this.hitEffect = this.despawnEffect = Fx.none;
                this.lifetime = 180.0F;
                this.width = this.height = 1.0F;
            }
        };
        upgradeEffect = new BlockStatusEffectBulletType(0.0F, 0.0F) {
            {
                this.hitEffect = Fx.none;
                this.despawnEffect = UnityFx.expPoof;
                this.lifetime = 180.0F;
                this.width = this.height = 1.0F;
                this.upgrade = true;
            }
        };
        standardDenseLarge = (BasicBulletType)copy(Bullets.standardDenseBig, (t) -> {
            t.damage *= 1.4F;
            t.speed *= 1.1F;
            t.width *= 1.12F;
            t.height *= 1.12F;
        });
        standardHomingLarge = (BasicBulletType)copy(Bullets.standardDenseBig, (t) -> {
            t.damage *= 1.23F;
            t.reloadMultiplier = 1.3F;
            t.homingPower = 0.09F;
            t.speed *= 1.1F;
            t.width *= 1.09F;
            t.height *= 1.09F;
        });
        standardIncendiaryLarge = (BasicBulletType)copy(Bullets.standardIncendiaryBig, (t) -> {
            t.damage *= 1.4F;
            t.speed *= 1.1F;
            t.width *= 1.12F;
            t.height *= 1.12F;
        });
        standardThoriumLarge = (BasicBulletType)copy(Bullets.standardThoriumBig, (t) -> {
            t.damage *= 1.4F;
            t.speed *= 1.1F;
            t.width *= 1.12F;
            t.height *= 1.12F;
        });
        standardDenseHeavy = (BasicBulletType)copy(Bullets.standardDenseBig, (t) -> {
            t.damage *= 1.7F;
            t.speed *= 1.3F;
            t.width *= 1.32F;
            t.height *= 1.32F;
        });
        standardHomingHeavy = (BasicBulletType)copy(Bullets.standardDenseBig, (t) -> {
            t.damage *= 1.4F;
            t.reloadMultiplier = 1.3F;
            t.homingPower = 0.09F;
            t.speed *= 1.3F;
            t.width *= 1.19F;
            t.height *= 1.19F;
        });
        standardIncendiaryHeavy = (BasicBulletType)copy(Bullets.standardIncendiaryBig, (t) -> {
            t.damage *= 1.7F;
            t.speed *= 1.3F;
            t.width *= 1.32F;
            t.height *= 1.32F;
        });
        standardThoriumHeavy = (BasicBulletType)copy(Bullets.standardThoriumBig, (t) -> {
            t.damage *= 1.7F;
            t.speed *= 1.3F;
            t.width *= 1.32F;
            t.height *= 1.32F;
        });
        standardDenseMassive = (BasicBulletType)copy(Bullets.standardDenseBig, (t) -> {
            t.damage *= 1.8F;
            t.speed *= 1.3F;
            t.width *= 1.34F;
            t.height *= 1.34F;
            t.lifetime *= 1.1F;
        });
        standardHomingMassive = (BasicBulletType)copy(Bullets.standardDenseBig, (t) -> {
            t.damage *= 1.6F;
            t.reloadMultiplier = 1.3F;
            t.homingPower = 0.09F;
            t.speed *= 1.3F;
            t.width *= 1.21F;
            t.height *= 1.21F;
            t.lifetime *= 1.1F;
        });
        standardIncendiaryMassive = (BasicBulletType)copy(Bullets.standardIncendiaryBig, (t) -> {
            t.damage *= 1.8F;
            t.speed *= 1.3F;
            t.width *= 1.34F;
            t.height *= 1.34F;
            t.lifetime *= 1.1F;
        });
        standardThoriumMassive = (BasicBulletType)copy(Bullets.standardThoriumBig, (t) -> {
            t.damage *= 1.8F;
            t.speed *= 1.3F;
            t.width *= 1.34F;
            t.height *= 1.34F;
            t.lifetime *= 1.1F;
        });
        reignBulletWeakened = (BasicBulletType)copy(((Weapon)UnitTypes.reign.weapons.get(0)).bullet, (t) -> t.damage = 45.0F);
        artilleryExplosiveT2 = (ArtilleryBulletType)copy(Bullets.artilleryExplosive, (t) -> {
            t.speed = 4.5F;
            t.lifetime = 74.0F;
            t.ammoMultiplier = 2.0F;
            t.splashDamageRadius *= 1.3F;
            t.splashDamage *= 3.0F;
        });
    }
}
