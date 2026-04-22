package unity.content;

import arc.Core;
import arc.func.Func;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.ObjectSet;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.ai.types.GroundAI;
import mindustry.content.Bullets;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.content.UnitTypes;
import mindustry.entities.Damage;
import mindustry.entities.Lightning;
import mindustry.entities.Units;
import mindustry.entities.abilities.ForceFieldAbility;
import mindustry.entities.abilities.MoveLightningAbility;
import mindustry.entities.abilities.RepairFieldAbility;
import mindustry.entities.abilities.UnitSpawnAbility;
import mindustry.entities.bullet.ArtilleryBulletType;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.bullet.ContinuousLaserBulletType;
import mindustry.entities.bullet.FlakBulletType;
import mindustry.entities.bullet.LaserBulletType;
import mindustry.entities.bullet.LightningBulletType;
import mindustry.entities.bullet.MissileBulletType;
import mindustry.entities.bullet.RailBulletType;
import mindustry.entities.bullet.SapBulletType;
import mindustry.entities.bullet.ShrapnelBulletType;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Bullet;
import mindustry.gen.Groups;
import mindustry.gen.Sounds;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.type.StatusEffect;
import mindustry.type.UnitType;
import mindustry.type.Weapon;
import mindustry.type.ammo.PowerAmmoType;
import mindustry.type.weapons.RepairBeamWeapon;
import mindustry.world.meta.BlockFlag;
import unity.ai.CopterAI;
import unity.ai.DistanceGroundAI;
import unity.ai.EmptyAI;
import unity.ai.HealingDefenderAI;
import unity.ai.LinkedAI;
import unity.ai.LinkerAI;
import unity.ai.NewHealerAI;
import unity.ai.SmartGroundAI;
import unity.ai.WormAI;
import unity.content.effects.ChargeFx;
import unity.content.effects.HitFx;
import unity.content.effects.LineFx;
import unity.content.effects.ShootFx;
import unity.content.effects.SpecialFx;
import unity.content.effects.TrailFx;
import unity.content.units.MonolithUnitTypes;
import unity.entities.Rotor;
import unity.entities.abilities.DirectionShieldAbility;
import unity.entities.abilities.LightningBurstAbility;
import unity.entities.abilities.ShootArmorAbility;
import unity.entities.abilities.SlashAbility;
import unity.entities.abilities.TeleportAbility;
import unity.entities.abilities.TimeStopAbility;
import unity.entities.bullet.anticheat.ContinuousSingularityLaserBulletType;
import unity.entities.bullet.anticheat.DesolationBulletType;
import unity.entities.bullet.anticheat.EndBasicBulletType;
import unity.entities.bullet.anticheat.EndContinuousLaserBulletType;
import unity.entities.bullet.anticheat.EndCutterLaserBulletType;
import unity.entities.bullet.anticheat.EndPointBlastLaserBulletType;
import unity.entities.bullet.anticheat.EndRailBulletType;
import unity.entities.bullet.anticheat.EndSweepLaser;
import unity.entities.bullet.anticheat.OppressionLaserBulletType;
import unity.entities.bullet.anticheat.SlowLightningBulletType;
import unity.entities.bullet.anticheat.TimeStopBulletType;
import unity.entities.bullet.anticheat.VoidFractureBulletType;
import unity.entities.bullet.anticheat.VoidPelletBulletType;
import unity.entities.bullet.anticheat.VoidPortalBulletType;
import unity.entities.bullet.anticheat.modules.AbilityDamageModule;
import unity.entities.bullet.anticheat.modules.AntiCheatBulletModule;
import unity.entities.bullet.anticheat.modules.ArmorDamageModule;
import unity.entities.bullet.anticheat.modules.ForceFieldDamageModule;
import unity.entities.bullet.energy.ArrowBulletType;
import unity.entities.bullet.energy.CygnusBulletType;
import unity.entities.bullet.energy.EmpBasicBulletType;
import unity.entities.bullet.energy.FlameBulletType;
import unity.entities.bullet.energy.HealingConeBulletType;
import unity.entities.bullet.energy.HealingNukeBulletType;
import unity.entities.bullet.energy.LightningTurretBulletType;
import unity.entities.bullet.energy.PointDrainLaserBulletType;
import unity.entities.bullet.energy.TrailingEmpBulletType;
import unity.entities.bullet.laser.AcceleratingLaserBulletType;
import unity.entities.bullet.laser.AnomalyLaserBulletType;
import unity.entities.bullet.laser.ReflectingLaserBulletType;
import unity.entities.bullet.laser.SaberContinuousLaserBulletType;
import unity.entities.bullet.laser.SagittariusLaserBulletType;
import unity.entities.bullet.misc.ShootingBulletType;
import unity.entities.bullet.physical.AntiBulletFlakBulletType;
import unity.entities.bullet.physical.GuidedMissileBulletType;
import unity.entities.bullet.physical.MortarBulletType;
import unity.entities.bullet.physical.SlowRailBulletType;
import unity.entities.legs.BasicLeg;
import unity.entities.legs.CLegType;
import unity.gen.Invisiblec;
import unity.gen.UnitySounds;
import unity.graphics.UnityDrawf;
import unity.graphics.UnityPal;
import unity.type.AntiCheatVariables;
import unity.type.CloneableSetWeapon;
import unity.type.InvisibleUnitType;
import unity.type.RainbowUnitType;
import unity.type.TentacleType;
import unity.type.UnityUnitType;
import unity.type.WormDecal;
import unity.type.decal.CapeDecorationType;
import unity.type.decal.FlagellaDecorationType;
import unity.type.decal.WingDecorationType;
import unity.type.weapons.AcceleratingWeapon;
import unity.type.weapons.EnergyChargeWeapon;
import unity.type.weapons.LimitedAngleWeapon;
import unity.type.weapons.MortarWeapon;
import unity.type.weapons.MultiBarrelWeapon;
import unity.type.weapons.MultiTargetPointDefenceWeapon;
import unity.type.weapons.PointDefenceMultiBarrelWeapon;
import unity.type.weapons.SweepWeapon;
import unity.type.weapons.TractorBeamWeapon;

public class UnityUnitTypes {
    public static UnitType caelifera;
    public static UnitType schistocerca;
    public static UnitType anthophila;
    public static UnitType vespula;
    public static UnitType lepidoptera;
    public static UnitType mantodea;
    public static UnitType cherub;
    public static UnitType malakhim;
    public static UnitType seraphim;
    public static UnitType discharge;
    public static UnitType pulse;
    public static UnitType emission;
    public static UnitType waveform;
    public static UnitType ultraviolet;
    public static UnitType citadel;
    public static UnitType empire;
    public static UnitType cygnus;
    public static UnitType sagittarius;
    public static UnitType araneidae;
    public static UnitType theraphosidae;
    public static UnitType mantle;
    public static UnitType aphelion;
    public static UnitType sedec;
    public static UnitType trigintaduo;
    public static UnitType fin;
    public static UnitType blue;
    public static UnitType philinopsis;
    public static UnitType chelidonura;
    public static UnitType amphibiNaval;
    public static UnitType amphibi;
    public static UnitType craberNaval;
    public static UnitType craber;
    public static UnitType terra;
    public static UnitType hovos;
    public static UnitType ryzer;
    public static UnitType zena;
    public static UnitType sundown;
    public static UnitType rex;
    public static UnitType excelsus;
    public static UnitType whirlwind;
    public static UnitType jetstream;
    public static UnitType vortex;
    public static UnitType arcnelidia;
    public static UnityUnitType rayTest;
    public static UnityUnitType testLink;
    public static UnityUnitType test;
    public static UnitType exowalker;
    public static UnitType toxoswarmer;
    public static UnitType toxobyte;
    public static UnitType catenapede;
    public static UnitType buffer;
    public static UnitType omega;
    public static UnitType cache;
    public static UnitType dijkstra;
    public static UnitType phantasm;
    public static UnitType kami;
    public static UnitType deviation;
    public static UnitType anomaly;
    public static UnitType enigma;
    public static UnitType voidVessel;
    public static UnitType chronos;
    public static UnitType opticaecus;
    public static UnitType devourer;
    public static UnitType oppression;
    public static UnitType apocalypse;
    public static UnitType ravager;
    public static UnitType desolation;
    public static UnitType thalassophobia;
    public static UnitType charShadowcape;

    public static void load() {
        testLink = new UnityUnitType("test-link") {
            {
                this.defaultController = LinkedAI::new;
                this.rotationSpeed = 65.0F;
                this.speed = 1.0F;
                this.drag = 0.08F;
                this.accel = 0.04F;
                this.fallSpeed = 0.005F;
                this.health = 75.0F;
                this.engineSize = 0.0F;
                this.hovering = true;
                this.hitSize = 12.0F;
                this.range = 140.0F;
            }
        };
        test = new UnityUnitType("test") {
            {
                this.defaultController = LinkerAI::new;
                this.linkType = UnityUnitTypes.testLink;
                this.linkCount = 5;
                this.rotationSpeed = 65.0F;
                this.speed = 1.0F;
                this.drag = 0.08F;
                this.accel = 0.04F;
                this.fallSpeed = 0.005F;
                this.health = 75.0F;
                this.engineSize = 0.0F;
                this.hovering = true;
                this.hitSize = 12.0F;
                this.range = 140.0F;
            }
        };
        caelifera = new UnityUnitType("caelifera") {
            {
                this.defaultController = CopterAI::new;
                this.speed = 5.0F;
                this.drag = 0.08F;
                this.accel = 0.04F;
                this.fallSpeed = 0.005F;
                this.health = 75.0F;
                this.engineSize = 0.0F;
                this.flying = true;
                this.hitSize = 12.0F;
                this.range = 140.0F;
                this.weapons.add(new Weapon(this.name + "-gun") {
                    {
                        bottomWeapons.add(this);
                        this.reload = 6.0F;
                        this.x = 5.25F;
                        this.y = 6.5F;
                        this.shootY = 1.5F;
                        this.shootSound = Sounds.pew;
                        this.ejectEffect = Fx.casing1;
                        this.bullet = new BasicBulletType(5.0F, 7.0F) {
                            {
                                this.lifetime = 30.0F;
                                this.shrinkY = 0.2F;
                            }
                        };
                    }
                }, new Weapon(this.name + "-launcher") {
                    {
                        this.reload = 30.0F;
                        this.x = 4.5F;
                        this.y = 0.5F;
                        this.shootY = 2.25F;
                        this.shootSound = Sounds.shootSnap;
                        this.ejectEffect = Fx.casing2;
                        this.bullet = new MissileBulletType(3.0F, 1.0F) {
                            {
                                this.speed = 3.0F;
                                this.lifetime = 45.0F;
                                this.splashDamage = 40.0F;
                                this.splashDamageRadius = 8.0F;
                                this.drag = -0.01F;
                            }
                        };
                    }
                });
                this.rotors.add(new Rotor(this.name + "-rotor") {
                    {
                        this.x = 0.0F;
                        this.y = 6.0F;
                    }
                });
            }
        };
        schistocerca = new UnityUnitType("schistocerca") {
            {
                this.defaultController = CopterAI::new;
                this.speed = 4.5F;
                this.drag = 0.07F;
                this.accel = 0.03F;
                this.fallSpeed = 0.005F;
                this.health = 150.0F;
                this.engineSize = 0.0F;
                this.flying = true;
                this.hitSize = 13.0F;
                this.range = 165.0F;
                this.rotateSpeed = 4.6F;
                this.weapons.add(new Weapon(this.name + "-gun") {
                    {
                        bottomWeapons.add(this);
                        this.top = false;
                        this.x = 1.5F;
                        this.y = 11.0F;
                        this.shootX = -0.75F;
                        this.shootY = 3.0F;
                        this.shootSound = Sounds.pew;
                        this.ejectEffect = Fx.casing1;
                        this.reload = 8.0F;
                        this.bullet = new BasicBulletType(4.0F, 5.0F) {
                            {
                                this.lifetime = 36.0F;
                                this.shrinkY = 0.2F;
                            }
                        };
                    }
                }, new Weapon(this.name + "-gun") {
                    {
                        this.top = false;
                        this.x = 4.0F;
                        this.y = 8.75F;
                        this.shootX = -0.75F;
                        this.shootY = 3.0F;
                        this.shootSound = Sounds.shootSnap;
                        this.ejectEffect = Fx.casing1;
                        this.reload = 12.0F;
                        this.bullet = new BasicBulletType(4.0F, 8.0F) {
                            {
                                this.width = 7.0F;
                                this.height = 9.0F;
                                this.lifetime = 36.0F;
                                this.shrinkY = 0.2F;
                            }
                        };
                    }
                }, new Weapon(this.name + "-gun-big") {
                    {
                        this.x = 6.75F;
                        this.y = 5.75F;
                        this.shootX = -0.5F;
                        this.shootY = 2.0F;
                        this.shootSound = Sounds.shootSnap;
                        this.ejectEffect = Fx.casing1;
                        this.reload = 30.0F;
                        this.bullet = Bullets.standardIncendiary;
                    }
                });

                for(final int i : Mathf.signs) {
                    this.rotors.add(new Rotor(this.name + "-rotor") {
                        {
                            this.x = 0.0F;
                            this.y = 6.5F;
                            this.bladeCount = 3;
                            this.ghostAlpha = 0.4F;
                            this.shadowAlpha = 0.2F;
                            this.shadeSpeed = 3.0F * (float)i;
                            this.speed = 29.0F * (float)i;
                        }
                    });
                }

            }
        };
        anthophila = new UnityUnitType("anthophila") {
            {
                this.defaultController = CopterAI::new;
                this.speed = 4.0F;
                this.drag = 0.07F;
                this.accel = 0.03F;
                this.fallSpeed = 0.005F;
                this.health = 450.0F;
                this.engineSize = 0.0F;
                this.flying = true;
                this.hitSize = 15.0F;
                this.range = 165.0F;
                this.fallRotateSpeed = 2.0F;
                this.rotateSpeed = 3.8F;
                this.weapons.add(new Weapon(this.name + "-gun") {
                    {
                        bottomWeapons.add(this);
                        this.x = 4.25F;
                        this.y = 14.0F;
                        this.shootX = -1.0F;
                        this.shootY = 2.75F;
                        this.reload = 15.0F;
                        this.shootSound = Sounds.shootBig;
                        this.bullet = new BasicBulletType(6.0F, 60.0F) {
                            {
                                this.lifetime = 30.0F;
                                this.width = 16.0F;
                                this.height = 20.0F;
                                this.shootEffect = Fx.shootBig;
                                this.smokeEffect = Fx.shootBigSmoke;
                            }
                        };
                    }
                });
                this.weapons.add(new Weapon(this.name + "-tesla") {
                    {
                        this.x = 7.75F;
                        this.y = 8.25F;
                        this.shootY = 5.25F;
                        this.reload = 30.0F;
                        this.shots = 3;
                        this.shootSound = Sounds.spark;
                        this.bullet = new LightningBulletType() {
                            {
                                this.damage = 15.0F;
                                this.lightningLength = 12;
                                this.lightningColor = Pal.surge;
                            }
                        };
                    }
                });

                for(final int i : Mathf.signs) {
                    this.rotors.add(new Rotor(this.name + "-rotor2") {
                        {
                            this.x = 0.0F;
                            this.y = -13.0F;
                            this.bladeCount = 2;
                            this.ghostAlpha = 0.4F;
                            this.shadowAlpha = 0.2F;
                            this.shadeSpeed = 3.0F * (float)i;
                            this.speed = 29.0F * (float)i;
                        }
                    });
                }

                this.rotors.add(new Rotor(this.name + "-rotor1") {
                    {
                        this.mirror = true;
                        this.x = 13.0F;
                        this.y = 3.0F;
                        this.bladeCount = 3;
                    }
                });
            }
        };
        vespula = new UnityUnitType("vespula") {
            {
                this.defaultController = CopterAI::new;
                this.speed = 3.5F;
                this.drag = 0.07F;
                this.accel = 0.03F;
                this.fallSpeed = 0.003F;
                this.health = 4000.0F;
                this.engineSize = 0.0F;
                this.flying = true;
                this.hitSize = 30.0F;
                this.range = 165.0F;
                this.lowAltitude = true;
                this.rotateSpeed = 3.5F;
                this.weapons.add(new Weapon(this.name + "-gun-big") {
                    {
                        this.x = 8.25F;
                        this.y = 9.5F;
                        this.shootX = -1.0F;
                        this.shootY = 7.25F;
                        this.reload = 12.0F;
                        this.shootSound = Sounds.shootBig;
                        this.bullet = new BasicBulletType(6.0F, 60.0F) {
                            {
                                this.lifetime = 30.0F;
                                this.width = 16.0F;
                                this.height = 20.0F;
                                this.shootEffect = Fx.shootBig;
                                this.smokeEffect = Fx.shootBigSmoke;
                            }
                        };
                    }
                }, new Weapon(this.name + "-gun") {
                    {
                        bottomWeapons.add(this);
                        this.x = 6.5F;
                        this.y = 21.5F;
                        this.shootX = -0.25F;
                        this.shootY = 5.75F;
                        this.reload = 20.0F;
                        this.shots = 4;
                        this.shotDelay = 2.0F;
                        this.shootSound = Sounds.shootSnap;
                        this.bullet = Bullets.standardThorium;
                    }
                }, new Weapon(this.name + "-laser-gun") {
                    {
                        this.x = 13.5F;
                        this.y = 15.5F;
                        this.shootY = 4.5F;
                        this.reload = 60.0F;
                        this.shootSound = Sounds.laser;
                        this.bullet = new LaserBulletType(240.0F) {
                            {
                                this.sideAngle = 45.0F;
                                this.length = 200.0F;
                            }
                        };
                    }
                });

                for(final int i : Mathf.signs) {
                    this.rotors.add(new Rotor(this.name + "-rotor") {
                        {
                            this.mirror = true;
                            this.x = 15.0F;
                            this.y = 6.75F;
                            this.speed = 29.0F * (float)i;
                            this.ghostAlpha = 0.4F;
                            this.shadowAlpha = 0.2F;
                            this.shadeSpeed = 3.0F * (float)i;
                        }
                    });
                }

            }
        };
        lepidoptera = new UnityUnitType("lepidoptera") {
            {
                this.defaultController = CopterAI::new;
                this.speed = 3.0F;
                this.drag = 0.07F;
                this.accel = 0.03F;
                this.fallSpeed = 0.003F;
                this.health = 9500.0F;
                this.engineSize = 0.0F;
                this.flying = true;
                this.hitSize = 45.0F;
                this.range = 300.0F;
                this.lowAltitude = true;
                this.fallRotateSpeed = 0.8F;
                this.rotateSpeed = 2.7F;
                this.weapons.add(new Weapon(this.name + "-gun") {
                    {
                        bottomWeapons.add(this);
                        this.x = 14.0F;
                        this.y = 27.0F;
                        this.shootY = 5.5F;
                        this.shootSound = Sounds.shootBig;
                        this.ejectEffect = Fx.casing3Double;
                        this.reload = 10.0F;
                        this.bullet = new BasicBulletType(7.0F, 80.0F) {
                            {
                                this.lifetime = 30.0F;
                                this.width = 18.0F;
                                this.height = 22.0F;
                                this.shootEffect = Fx.shootBig;
                                this.smokeEffect = Fx.shootBigSmoke;
                            }
                        };
                    }
                }, new Weapon(this.name + "-launcher") {
                    {
                        this.x = 17.0F;
                        this.y = 14.0F;
                        this.shootY = 5.75F;
                        this.shootSound = Sounds.shootSnap;
                        this.ejectEffect = Fx.casing2;
                        this.shots = 2;
                        this.spacing = 2.0F;
                        this.reload = 20.0F;
                        this.bullet = new MissileBulletType(6.0F, 1.0F) {
                            {
                                this.width = 8.0F;
                                this.height = 14.0F;
                                this.trailColor = Pal.missileYellowBack;
                                this.weaveScale = 2.0F;
                                this.weaveMag = 2.0F;
                                this.lifetime = 35.0F;
                                this.drag = -0.01F;
                                this.splashDamage = 48.0F;
                                this.splashDamageRadius = 12.0F;
                                this.frontColor = Pal.missileYellow;
                                this.backColor = Pal.missileYellowBack;
                            }
                        };
                    }
                }, new Weapon(this.name + "-gun-big") {
                    {
                        this.rotate = true;
                        this.rotateSpeed = 3.0F;
                        this.x = 8.0F;
                        this.y = 3.0F;
                        this.shootY = 6.75F;
                        this.shootSound = Sounds.shotgun;
                        this.ejectEffect = Fx.none;
                        this.shots = 3;
                        this.spacing = 15.0F;
                        this.shotDelay = 0.0F;
                        this.reload = 45.0F;
                        this.bullet = new ShrapnelBulletType() {
                            {
                                this.toColor = Pal.accent;
                                this.damage = 150.0F;
                                this.keepVelocity = false;
                                this.length = 150.0F;
                            }
                        };
                    }
                });

                for(final int i : Mathf.signs) {
                    this.rotors.add(new Rotor(this.name + "-rotor1") {
                        {
                            this.mirror = true;
                            this.x = 22.5F;
                            this.y = 21.25F;
                            this.bladeCount = 3;
                            this.speed = 19.0F * (float)i;
                            this.ghostAlpha = 0.4F;
                            this.shadowAlpha = 0.2F;
                            this.shadeSpeed = 3.0F * (float)i;
                        }
                    }, new Rotor(this.name + "-rotor2") {
                        {
                            this.mirror = true;
                            this.x = 17.25F;
                            this.y = 1.0F;
                            this.bladeCount = 2;
                            this.speed = 23.0F * (float)i;
                            this.ghostAlpha = 0.4F;
                            this.shadowAlpha = 0.2F;
                            this.shadeSpeed = 4.0F * (float)i;
                        }
                    });
                }

            }
        };
        mantodea = new UnityUnitType("mantodea") {
            {
                this.defaultController = CopterAI::new;
                this.speed = 5.0F;
                this.drag = 0.1F;
                this.accel = 0.03F;
                this.fallSpeed = 0.0025F;
                this.health = 25000.0F;
                this.engineSize = 0.0F;
                this.flying = true;
                this.hitSize = 45.0F;
                this.lowAltitude = true;
                this.fallRotateSpeed = 0.8F;
                this.rotateSpeed = 2.2F;
                final BulletType flak = new FlakBulletType(8.0F, 20.0F) {
                    {
                        this.lifetime = 10.0F;
                        this.collidesGround = true;
                        this.lightning = 3;
                        this.lightningLength = 4;
                        this.lightningLengthRand = 2;
                        this.lightningDamage = 15.0F;
                        this.lightningColor = Pal.surge;
                    }
                };
                this.weapons.add(new Weapon(this.name + "-gun") {
                    {
                        bottomWeapons.add(this);
                        this.mirror = true;
                        this.rotate = false;
                        this.x = 14.25F;
                        this.y = 26.5F;
                        this.recoil = 2.5F;
                        this.shootY = 10.0F;
                        this.shootSound = Sounds.shoot;
                        this.shots = 3;
                        this.spacing = 0.0F;
                        this.shotDelay = 3.0F;
                        this.reload = 25.0F;
                        this.bullet = flak;
                    }
                }, new Weapon(this.name + "-gun") {
                    {
                        bottomWeapons.add(this);
                        this.mirror = true;
                        this.rotate = false;
                        this.x = 26.25F;
                        this.y = 19.5F;
                        this.recoil = 2.5F;
                        this.shootY = 10.0F;
                        this.shootSound = Sounds.shoot;
                        this.shots = 2;
                        this.spacing = 0.0F;
                        this.shotDelay = 3.0F;
                        this.reload = 15.0F;
                        this.bullet = flak;
                    }
                });

                for(final int i : Mathf.signs) {
                    this.rotors.add(new Rotor(this.name + "-rotor2") {
                        {
                            this.y = -31.25F;
                            this.bladeCount = 4;
                            this.speed = 19.0F * (float)i;
                            this.ghostAlpha = 0.4F;
                            this.shadowAlpha = 0.2F;
                            this.shadeSpeed = 4.0F * (float)i;
                        }
                    }, new Rotor(this.name + "-rotor3") {
                        {
                            this.mirror = true;
                            this.x = 28.5F;
                            this.y = -11.75F;
                            this.bladeCount = 3;
                            this.speed = 23.0F * (float)i;
                            this.ghostAlpha = 0.4F;
                            this.shadowAlpha = 0.2F;
                            this.shadeSpeed = 3.0F * (float)i;
                        }
                    });
                }

                this.rotors.add(new Rotor(this.name + "-rotor1") {
                    {
                        this.y = 9.25F;
                        this.bladeCount = 3;
                        this.speed = 29.0F;
                        this.shadeSpeed = 5.0F;
                        this.bladeFade = 0.8F;
                    }
                });
            }
        };
        cherub = new UnityUnitType("cherub") {
            {
                this.defaultController = NewHealerAI::new;
                this.flying = true;
                this.hitSize = 13.0F;
                this.health = 70.0F;
                this.drag = 0.07F;
                this.accel = 0.1F;
                this.speed = 3.5F;
                this.engineOffset = 6.75F;
                this.engineSize = 1.75F;
                this.isCounted = false;
                this.weapons.add(new RepairBeamWeapon("") {
                    {
                        this.x = 0.0F;
                        this.y = 0.0F;
                        this.shootY = 0.0F;
                        this.beamWidth = 0.75F;
                        this.mirror = false;
                        this.repairSpeed = 0.5F;
                        this.bullet = new BulletType() {
                            {
                                this.maxRange = 85.0F;
                                this.drag = 1.0F;
                            }
                        };
                    }
                });
            }
        };
        malakhim = new UnityUnitType("malakhim") {
            {
                this.defaultController = NewHealerAI::new;
                this.flying = true;
                this.lowAltitude = true;
                this.hitSize = 19.5F;
                this.health = 220.0F;
                this.drag = 0.08F;
                this.accel = 0.09F;
                this.speed = 2.5F;
                this.engineOffset = 10.25F;
                this.engineSize = 3.0F;
                this.isCounted = false;
                this.weapons.add(new RepairBeamWeapon("repair-beam-weapon-center-large") {
                    {
                        this.x = 0.0F;
                        this.y = -3.0F;
                        this.shootY = 6.0F;
                        this.beamWidth = 1.0F;
                        this.mirror = false;
                        this.repairSpeed = 1.5F;
                        this.bullet = new BulletType() {
                            {
                                this.maxRange = 130.0F;
                                this.drag = 1.0F;
                            }
                        };
                    }
                });
            }
        };
        seraphim = new UnityUnitType("seraphim") {
            {
                this.defaultController = NewHealerAI::new;
                this.flying = true;
                this.lowAltitude = true;
                this.hitSize = 34.0F;
                this.health = 670.0F;
                this.drag = 0.09F;
                this.accel = 0.095F;
                this.speed = 2.0F;
                this.engineOffset = 15.75F;
                this.engineSize = 3.5F;
                this.circleTarget = true;
                final BulletType r = new BulletType() {
                    {
                        this.maxRange = 210.0F;
                        this.drag = 1.0F;
                    }
                };
                this.weapons.add(new RepairBeamWeapon("repair-beam-weapon") {
                    {
                        this.x = 8.0F;
                        this.y = 8.25F;
                        this.shootY = 6.0F;
                        this.beamWidth = 0.8F;
                        this.mirror = true;
                        this.repairSpeed = 0.65F;
                        this.bullet = r;
                    }
                }, new RepairBeamWeapon("repair-beam-weapon") {
                    {
                        this.x = 14.25F;
                        this.y = -3.0F;
                        this.shootY = 6.0F;
                        this.beamWidth = 0.8F;
                        this.mirror = true;
                        this.repairSpeed = 0.65F;
                        this.bullet = r;
                    }
                }, new RepairBeamWeapon("repair-beam-weapon") {
                    {
                        this.x = 11.75F;
                        this.y = -12.0F;
                        this.shootY = 6.0F;
                        this.beamWidth = 0.8F;
                        this.mirror = true;
                        this.repairSpeed = 0.65F;
                        this.bullet = r;
                    }
                });
            }
        };
        discharge = new UnityUnitType("discharge") {
            {
                this.flying = true;
                this.lowAltitude = true;
                this.health = 60.0F;
                this.speed = 2.0F;
                this.accel = 0.09F;
                this.drag = 0.02F;
                this.hitSize = 11.5F;
                this.engineOffset = 7.25F;
                this.ammoType = new PowerAmmoType(1000.0F);
                this.weapons.add(new Weapon() {
                    {
                        this.rotate = false;
                        this.mirror = false;
                        this.x = 0.0F;
                        this.y = 0.0F;
                        this.shootY = 4.0F;
                        this.reload = 240.0F;
                        this.shootSound = UnitySounds.zbosonShoot;
                        this.bullet = new EmpBasicBulletType(6.0F, 3.0F) {
                            {
                                this.lifetime = 35.0F;
                                this.splashDamageRadius = 20.0F;
                                this.splashDamage = 3.0F;
                                this.shrinkY = 0.0F;
                                this.height = 14.0F;
                                this.width = 11.0F;
                                this.trailWidth = this.width / 2.0F / 2.0F;
                                this.hitEffect = Fx.hitLancer;
                                this.trailColor = this.backColor = this.lightColor = this.hitColor = Pal.lancerLaser;
                                this.frontColor = Color.white;
                            }
                        };
                    }
                });
            }
        };
        pulse = new UnityUnitType("pulse") {
            {
                this.flying = true;
                this.lowAltitude = true;
                this.health = 210.0F;
                this.speed = 1.8F;
                this.accel = 0.1F;
                this.drag = 0.06F;
                this.hitSize = 16.5F;
                this.engineOffset = 8.25F;
                this.ammoType = new PowerAmmoType(1000.0F);
                this.weapons.add(new Weapon() {
                    {
                        this.rotate = false;
                        this.mirror = false;
                        this.x = 0.0F;
                        this.y = 0.0F;
                        this.shootY = 7.0F;
                        this.reload = 180.0F;
                        this.firstShotDelay = 70.0F;
                        this.shootStatus = StatusEffects.unmoving;
                        this.shootStatusDuration = 70.0F;
                        this.shootSound = UnitySounds.zbosonShoot;
                        this.bullet = new EmpBasicBulletType(6.25F, 4.0F) {
                            {
                                this.splashDamageRadius = 25.0F;
                                this.splashDamage = 9.0F;
                                this.shrinkY = 0.0F;
                                this.height = 16.0F;
                                this.width = 12.0F;
                                this.trailWidth = this.width / 2.0F / 2.0F;
                                this.empRange = 120.0F;
                                this.empDisconnectRange = 40.0F;
                                this.empBatteryDamage = 11000.0F;
                                this.empLogicDamage = 5.0F;
                                this.hitEffect = Fx.hitLancer;
                                this.trailColor = this.backColor = this.lightColor = this.hitColor = Pal.lancerLaser;
                                this.frontColor = Color.white;
                                this.shootEffect = UnityFx.empCharge;
                            }
                        };
                    }
                });
            }
        };
        emission = new UnityUnitType("emission") {
            {
                this.flying = true;
                this.lowAltitude = true;
                this.health = 550.0F;
                this.speed = 1.2F;
                this.accel = 0.1F;
                this.drag = 0.07F;
                this.hitSize = 24.5F;
                this.engineOffset = 3.75F;
                this.ammoType = new PowerAmmoType(1000.0F);
                this.weapons.add(new Weapon("unity-emp-launcher") {
                    {
                        this.rotate = true;
                        this.mirror = true;
                        this.x = 11.75F;
                        this.y = -7.25F;
                        this.shootY = 5.0F;
                        this.shootSound = UnitySounds.zbosonShoot;
                        this.reload = 102.0F;
                        this.bullet = new EmpBasicBulletType(6.0F, 2.0F) {
                            {
                                this.lifetime = 35.0F;
                                this.splashDamageRadius = 17.0F;
                                this.splashDamage = 2.0F;
                                this.shrinkY = 0.0F;
                                this.height = 13.0F;
                                this.width = 10.0F;
                                this.trailWidth = this.width / 2.0F / 2.0F;
                                this.powerGridIteration = 5;
                                this.empDuration = 15.0F;
                                this.empBatteryDamage = 4300.0F;
                                this.empRange = 90.0F;
                                this.hitEffect = Fx.hitLancer;
                                this.trailColor = this.backColor = this.lightColor = this.hitColor = Pal.lancerLaser;
                                this.frontColor = Color.white;
                            }
                        };
                    }
                }, new Weapon() {
                    {
                        this.mirror = false;
                        this.x = 0.0F;
                        this.y = -11.5F;
                        this.shootY = 0.0F;
                        this.shootSound = UnitySounds.zbosonShoot;
                        this.shootStatus = StatusEffects.unmoving;
                        this.shootStatusDuration = 70.0F;
                        this.reload = 300.0F;
                        this.firstShotDelay = 70.0F;
                        this.bullet = new EmpBasicBulletType(6.7F, 8.0F) {
                            {
                                this.splashDamageRadius = 30.0F;
                                this.splashDamage = 12.0F;
                                this.shrinkY = 0.0F;
                                this.height = 17.0F;
                                this.width = 13.0F;
                                this.empRange = 150.0F;
                                this.empDuration = 120.0F;
                                this.empMaxRange = 800.0F;
                                this.empDisconnectRange = 95.0F;
                                this.empBatteryDamage = 26000.0F;
                                this.empLogicDamage = 12.0F;
                                this.powerGridIteration = 15;
                                this.trailLength = 13;
                                this.trailWidth = this.width / 2.0F / 2.0F;
                                this.hitEffect = Fx.hitLancer;
                                this.trailColor = this.backColor = this.lightColor = this.hitColor = Pal.lancerLaser;
                                this.frontColor = Color.white;
                                this.shootEffect = UnityFx.empCharge;
                            }
                        };
                    }
                });
            }
        };
        waveform = new UnityUnitType("waveform") {
            {
                this.flying = true;
                this.lowAltitude = true;
                this.health = 4500.0F;
                this.speed = 0.9F;
                this.accel = 0.09F;
                this.drag = 0.07F;
                this.hitSize = 41.5F;
                this.engineOffset = 24.25F;
                this.ammoType = new PowerAmmoType(2000.0F);
                CloneableSetWeapon t = UnityWeaponTemplates.waveformSmallMount;
                this.weapons.addAll(new Weapon[]{t.set((w) -> {
                    w.x = 15.75F;
                    w.y = 4.0F;
                    w.reload *= 4.0F;
                    w.otherSide = 1;
                }), t.set((w) -> {
                    w.x = -15.75F;
                    w.y = 4.0F;
                    w.reload *= 4.0F;
                    w.flipSprite = true;
                    w.otherSide = 2;
                }), t.set((w) -> {
                    w.x = -19.25F;
                    w.y = -15.25F;
                    w.reload *= 4.0F;
                    w.flipSprite = true;
                    w.otherSide = 3;
                }), t.set((w) -> {
                    w.x = 19.25F;
                    w.y = -15.25F;
                    w.reload *= 4.0F;
                    w.flipSprite = true;
                    w.otherSide = 0;
                    w.name = "unity-emp-small-mount-flipped";
                }), new Weapon("unity-emp-launcher") {
                    {
                        this.x = 10.0F;
                        this.y = -8.5F;
                        this.reload = 240.0F;
                        this.mirror = true;
                        this.rotate = true;
                        this.rotateSpeed = 3.0F;
                        this.shootY = 5.0F;
                        this.shootSound = UnitySounds.zbosonShoot;
                        this.bullet = new EmpBasicBulletType(6.8F, 8.0F) {
                            {
                                this.hitSize = 6.0F;
                                this.splashDamageRadius = 30.0F;
                                this.splashDamage = 14.0F;
                                this.shrinkY = 0.0F;
                                this.height = 18.0F;
                                this.width = 14.0F;
                                this.empRange = 160.0F;
                                this.empDuration = 120.0F;
                                this.empMaxRange = 800.0F;
                                this.empDisconnectRange = 100.0F;
                                this.empBatteryDamage = 30000.0F;
                                this.empLogicDamage = 12.0F;
                                this.powerGridIteration = 15;
                                this.trailLength = 15;
                                this.trailWidth = this.width / 2.0F / 2.0F;
                                this.hitEffect = Fx.hitLancer;
                                this.trailColor = this.backColor = this.lightColor = this.hitColor = Pal.lancerLaser;
                                this.frontColor = Color.white;
                            }
                        };
                    }
                }});
            }
        };
        ultraviolet = new UnityUnitType("ultraviolet") {
            {
                this.flying = true;
                this.lowAltitude = true;
                this.health = 12000.0F;
                this.speed = 0.53F;
                this.accel = 0.06F;
                this.drag = 0.07F;
                this.hitSize = 57.5F;
                this.engineOffset = 33.75F;
                this.engineSize = 3.5F;
                this.ammoType = new PowerAmmoType(2000.0F);
                CloneableSetWeapon t = UnityWeaponTemplates.ultravioletMount;
                this.weapons.addAll(new Weapon[]{t.set((w) -> {
                    w.x = 13.25F;
                    w.y = 20.25F;
                    w.otherSide = 2;
                }), t.set((w) -> {
                    w.x = -13.25F;
                    w.y = 20.25F;
                    w.flipSprite = true;
                    w.otherSide = 0;
                }), t.flp((w) -> {
                    w.x = 19.75F;
                    w.y = 12.0F;
                    w.otherSide = 4;
                }), t.set((w) -> {
                    w.x = -19.75F;
                    w.y = 12.0F;
                    w.flipSprite = true;
                    w.otherSide = 1;
                }), t.flp((w) -> {
                    w.x = 25.25F;
                    w.y = 0.0F;
                    w.otherSide = 6;
                }), t.set((w) -> {
                    w.x = -25.25F;
                    w.y = 0.0F;
                    w.flipSprite = true;
                    w.otherSide = 3;
                }), t.flp((w) -> {
                    w.x = 22.75F;
                    w.y = -12.0F;
                    w.otherSide = 8;
                }), t.set((w) -> {
                    w.x = -22.75F;
                    w.y = -12.0F;
                    w.flipSprite = true;
                    w.otherSide = 5;
                }), t.flp((w) -> {
                    w.x = 16.0F;
                    w.y = -19.5F;
                    w.otherSide = 9;
                }), t.set((w) -> {
                    w.x = -16.0F;
                    w.y = -19.5F;
                    w.flipSprite = true;
                    w.otherSide = 7;
                }), new Weapon("unity-emp-large-launcher") {
                    {
                        this.x = 0.0F;
                        this.y = -20.25F;
                        this.shootY = 11.0F;
                        this.mirror = false;
                        this.rotate = true;
                        this.rotateSpeed = 2.0F;
                        this.reload = 240.0F;
                        this.shootSound = UnitySounds.zbosonShoot;
                        this.bullet = new EmpBasicBulletType(6.8F, 9.0F) {
                            {
                                this.lifetime = 42.0F;
                                this.hitSize = 6.0F;
                                this.splashDamageRadius = 45.0F;
                                this.splashDamage = 23.0F;
                                this.shrinkY = 0.0F;
                                this.height = 19.0F;
                                this.width = 14.5F;
                                this.empRange = 175.0F;
                                this.empDuration = 120.0F;
                                this.empMaxRange = 800.0F;
                                this.empDisconnectRange = 125.0F;
                                this.empBatteryDamage = 40000.0F;
                                this.empLogicDamage = 26.0F;
                                this.powerGridIteration = 15;
                                this.trailLength = 15;
                                this.trailWidth = this.width / 2.0F / 2.0F;
                                this.hitEffect = Fx.hitLancer;
                                this.trailColor = this.backColor = this.lightColor = this.hitColor = Pal.lancerLaser;
                                this.frontColor = Color.white;
                            }
                        };
                    }
                }});
            }
        };
        citadel = new UnityUnitType("citadel") {
            {
                this.speed = 0.3F;
                this.hitSize = 49.0F;
                this.rotateSpeed = 1.5F;
                this.health = 60000.0F;
                this.armor = 16.0F;
                this.mechStepParticles = true;
                this.mechStepShake = 0.8F;
                this.canDrown = false;
                this.mechFrontSway = 2.0F;
                this.mechSideSway = 0.7F;
                this.mechStride = (4.0F + (this.hitSize - 8.0F) / 2.1F) / 1.25F;
                this.immunities.add(StatusEffects.burning);
                this.weapons.add(new Weapon(this.name + "-weapon") {
                    {
                        this.top = false;
                        this.x = 31.5F;
                        this.y = -6.25F;
                        this.shootY = 30.25F;
                        this.reload = 90.0F;
                        this.recoil = 7.0F;
                        this.shake = 3.0F;
                        this.ejectEffect = Fx.casing4;
                        this.shootSound = Sounds.railgun;
                        this.bullet = new SlowRailBulletType(25.0F, 250.0F) {
                            {
                                this.lifetime = 13.0F;
                                this.trailSpacing = 25.0F;
                                this.splashDamage = 95.0F;
                                this.splashDamageRadius = 50.0F;
                                this.hitEffect = Fx.hitBulletBig;
                                this.shootEffect = Fx.instShoot;
                                this.trailEffect = TrailFx.coloredRailgunSmallTrail;
                                this.width = 9.0F;
                                this.height = 17.0F;
                                this.shrinkY = 0.0F;
                                this.shrinkX = 0.0F;
                                this.pierceCap = 7;
                                this.backColor = this.hitColor = this.trailColor = Pal.bulletYellowBack;
                                this.frontColor = Color.white;
                            }
                        };
                    }
                }, new LimitedAngleWeapon(this.name + "-flamethrower") {
                    {
                        this.x = 17.75F;
                        this.y = 11.25F;
                        this.shootY = 5.5F;
                        this.reload = 5.0F;
                        this.recoil = 0.5F;
                        this.shootSound = Sounds.flame;
                        this.angleCone = 80.0F;
                        this.rotate = true;
                        this.bullet = UnityBullets.citadelFlame;
                    }
                }, new LimitedAngleWeapon(this.name + "-flamethrower") {
                    {
                        this.x = 14.0F;
                        this.y = -9.0F;
                        this.shootY = 5.5F;
                        this.reload = 4.0F;
                        this.recoil = 0.5F;
                        this.shootSound = Sounds.flame;
                        this.angleCone = 80.0F;
                        this.rotate = true;
                        this.bullet = UnityBullets.citadelFlame;
                    }
                });
            }
        };
        empire = new UnityUnitType("empire") {
            {
                this.speed = 0.2F;
                this.hitSize = 49.0F;
                this.rotateSpeed = 1.25F;
                this.health = 140000.0F;
                this.armor = 20.0F;
                this.mechStepParticles = true;
                this.mechStepShake = 0.83F;
                this.canDrown = false;
                this.mechFrontSway = 4.0F;
                this.mechSideSway = 0.7F;
                this.mechStride = (4.0F + (this.hitSize - 8.0F) / 2.1F) / 1.3F;
                this.immunities.addAll(new StatusEffect[]{StatusEffects.burning, StatusEffects.melting});
                this.weapons.add(new LimitedAngleWeapon(this.name + "-weapon") {
                    {
                        bottomWeapons.add(this);
                        this.x = 36.5F;
                        this.y = 2.75F;
                        this.shootY = 19.25F;
                        this.xRand = 4.5F;
                        this.alternate = false;
                        this.rotate = true;
                        this.rotateSpeed = 1.2F;
                        this.inaccuracy = 4.0F;
                        this.reload = 3.0F;
                        this.shots = 2;
                        this.angleCone = 20.0F;
                        this.angleOffset = -15.0F;
                        this.shootCone = 20.0F;
                        this.shootSound = Sounds.flame;
                        this.cooldownTime = 180.0F;
                        this.bullet = new FlameBulletType(6.6F, 75.0F) {
                            {
                                this.lifetime = 42.0F;
                                this.pierceCap = 6;
                                this.pierceBuilding = true;
                                this.collidesAir = true;
                                this.reflectable = false;
                                this.incendChance = 0.2F;
                                this.incendAmount = 1;
                                this.particleAmount = 23;
                                this.particleSizeScl = 8.0F;
                                this.particleSpread = 11.0F;
                                this.hitSize = 9.0F;
                                this.layer = 99.999F;
                                this.status = StatusEffects.melting;
                                this.smokeColors = new Color[]{Pal.darkFlame, Color.darkGray, Color.gray};
                                this.colors = new Color[]{Color.white, Color.valueOf("fff4ac"), Pal.lightFlame, Pal.darkFlame, Color.gray};
                            }
                        };
                    }
                }, new LimitedAngleWeapon(this.name + "-mount") {
                    {
                        this.x = 20.75F;
                        this.y = 10.0F;
                        this.shootY = 6.25F;
                        this.rotate = true;
                        this.rotateSpeed = 7.0F;
                        this.angleCone = 60.0F;
                        this.reload = 60.0F;
                        this.shootCone = 30.0F;
                        this.shootSound = Sounds.missile;
                        this.bullet = new MissileBulletType(2.5F, 22.0F) {
                            {
                                this.lifetime = 40.0F;
                                this.drag = -0.005F;
                                this.width = 14.0F;
                                this.height = 15.0F;
                                this.shrinkY = 0.0F;
                                this.splashDamageRadius = 55.0F;
                                this.splashDamage = 85.0F;
                                this.homingRange = 90.0F;
                                this.weaveMag = 2.0F;
                                this.weaveScale = 8.0F;
                                this.hitEffect = this.despawnEffect = HitFx.hitExplosionLarge;
                                this.status = StatusEffects.blasted;
                                this.statusDuration = 60.0F;
                                this.fragBullets = 5;
                                this.fragLifeMin = 0.9F;
                                this.fragLifeMax = 1.1F;
                                this.fragBullet = new ShrapnelBulletType() {
                                    {
                                        this.damage = 200.0F;
                                        this.length = 60.0F;
                                        this.width = 12.0F;
                                        this.toColor = Pal.missileYellow;
                                        this.hitColor = Pal.bulletYellow;
                                        this.hitEffect = HitFx.coloredHitSmall;
                                        this.serrationLenScl = 5.0F;
                                        this.serrationSpaceOffset = 45.0F;
                                        this.serrationSpacing = 5.0F;
                                    }
                                };
                            }
                        };
                    }
                }, new Weapon(this.name + "-cannon") {
                    {
                        this.x = 20.75F;
                        this.y = -4.0F;
                        this.shootY = 9.75F;
                        this.rotate = true;
                        this.rotateSpeed = 4.0F;
                        this.inaccuracy = 10.0F;
                        this.shots = 8;
                        this.velocityRnd = 0.2F;
                        this.shootSound = Sounds.artillery;
                        this.reload = 40.0F;
                        this.bullet = new ArtilleryBulletType(3.0F, 15.0F, "shell") {
                            {
                                this.hitEffect = Fx.blastExplosion;
                                this.knockback = 0.8F;
                                this.lifetime = 125.0F;
                                this.width = this.height = 14.0F;
                                this.collides = true;
                                this.collidesTiles = true;
                                this.splashDamageRadius = 45.0F;
                                this.splashDamage = 95.0F;
                                this.backColor = Pal.bulletYellowBack;
                                this.frontColor = Pal.bulletYellow;
                            }
                        };
                    }
                });
            }
        };
        cygnus = new UnityUnitType("cygnus") {
            {
                this.speed = 0.26F;
                this.health = 45000.0F;
                this.hitSize = 37.0F;
                this.armor = 10.0F;
                this.landShake = 1.5F;
                this.commandLimit = 8;
                this.rotateSpeed = 1.3F;
                this.legCount = 6;
                this.legLength = 29.0F;
                this.legBaseOffset = 8.0F;
                this.legMoveSpace = 0.7F;
                this.legTrns = 0.6F;
                this.hovering = true;
                this.visualElevation = 0.23F;
                this.allowLegStep = true;
                this.ammoType = new PowerAmmoType(2000.0F);
                this.groundLayer = 75.0F;
                this.weapons.add(new Weapon() {
                    {
                        this.x = 0.0F;
                        this.y = 8.25F;
                        this.mirror = false;
                        this.reload = 240.0F;
                        this.recoil = 0.0F;
                        this.shootSound = Sounds.lasershoot;
                        this.shootStatus = StatusEffects.slow;
                        this.shootStatusDuration = 80.0F;
                        this.firstShotDelay = ChargeFx.greenLaserChargeParent.lifetime;
                        this.bullet = new ReflectingLaserBulletType(500.0F) {
                            {
                                this.lifetime = 65.0F;
                                this.shootEffect = ChargeFx.greenLaserChargeParent;
                                this.healPercent = 6.0F;
                                this.splashDamage = 70.0F;
                                this.splashDamageRadius = 30.0F;
                                this.lightningDamage = 75.0F;
                                this.hitEffect = HitFx.coloredHitLarge;
                                this.hitColor = this.lightningColor = Pal.heal;
                                this.pierceCap = 3;
                                this.collidesTeam = true;
                                this.lightningLength = 12;
                                this.colors = new Color[]{Pal.heal.cpy().a(0.2F), Pal.heal.cpy().a(0.5F), Pal.heal.cpy().mul(1.2F), Color.white};
                            }
                        };
                    }
                }, new Weapon(this.name + "-mount") {
                    {
                        this.x = 22.5F;
                        this.y = -3.0F;
                        this.shootY = 8.75F;
                        this.rotate = true;
                        this.alternate = true;
                        this.rotateSpeed = 5.0F;
                        this.reload = 25.0F;
                        this.shootSound = UnitySounds.energyBolt;
                        this.heatColor = Pal.heal;
                        this.inaccuracy = 5.0F;
                        this.bullet = new CygnusBulletType() {
                            {
                                this.speed = 6.0F;
                                this.damage = 20.0F;
                                this.radius = 70.0F;
                                this.hitEffect = HitFx.empHit;
                                this.splashDamage = 5.0F;
                                this.splashDamageRadius = 70.0F;
                                this.backColor = Pal.heal;
                                this.shootEffect = Fx.hitEmpSpark;
                                this.smokeEffect = Fx.shootBigSmoke2;
                                this.trailLength = 15;
                                this.trailWidth = 6.0F;
                                this.trailColor = Pal.heal;
                                this.status = StatusEffects.electrified;
                                this.lightColor = Pal.heal;
                                this.powerSclDecrease = 0.5F;
                                this.timeIncrease = 1.25F;
                            }
                        };
                    }
                });
            }
        };
        sagittarius = new UnityUnitType("sagittarius") {
            {
                this.speed = 0.25F;
                this.health = 102500.0F;
                this.hitSize = 55.0F;
                this.armor = 12.0F;
                this.landShake = 2.0F;
                this.commandLimit = 8;
                this.rotateSpeed = 0.8F;
                this.legCount = 4;
                this.legLength = 34.36F;
                this.legBaseOffset = 11.0F;
                this.legMoveSpace = 0.7F;
                this.legTrns = 0.6F;
                this.hovering = true;
                this.visualElevation = 0.23F;
                this.allowLegStep = true;
                this.ammoType = new PowerAmmoType(2000.0F);
                this.groundLayer = 75.0F;
                this.drawShields = false;
                this.abilities.add(new ForceFieldAbility(130.0F, 3.0F, 3500.0F, 420.0F));
                this.weapons.add(new Weapon(this.name + "-laser") {
                    {
                        this.mirror = false;
                        this.x = 0.0F;
                        this.y = 0.0F;
                        this.shootY = 16.75F;
                        this.reload = 720.0F;
                        this.shootSound = Sounds.beam;
                        this.firstShotDelay = ChargeFx.sagittariusCharge.lifetime;
                        this.shootStatus = UnityStatusEffects.sagittariusFatigue;
                        this.shootStatusDuration = 600.0F + ChargeFx.sagittariusCharge.lifetime;
                        this.continuous = true;
                        this.cooldownTime = 280.0F;
                        this.bullet = new SagittariusLaserBulletType(35.0F) {
                            {
                                this.shootEffect = ChargeFx.sagittariusCharge;
                                this.lifetime = 600.0F;
                                this.collidesTeam = true;
                                this.healPercent = 0.4F;
                                this.splashDamage = 4.0F;
                                this.splashDamageRadius = 25.0F;
                                this.knockback = 3.0F;
                                this.buildingDamageMultiplier = 0.6F;
                                this.status = StatusEffects.electrified;
                                this.statusDuration = 30.0F;
                            }
                        };
                    }
                }, new AcceleratingWeapon(this.name + "-mount") {
                    {
                        this.x = 28.25F;
                        this.y = -9.25F;
                        this.shootY = 17.0F;
                        this.reload = 30.0F;
                        this.accelCooldownWaitTime = 31.0F;
                        this.minReload = 5.0F;
                        this.accelPerShot = 0.5F;
                        this.rotateSpeed = 5.0F;
                        this.inaccuracy = 5.0F;
                        this.rotate = true;
                        this.alternate = false;
                        this.shots = 2;
                        this.shootSound = UnitySounds.energyBolt;
                        this.bullet = new ArrowBulletType(7.0F, 25.0F) {
                            {
                                this.lifetime = 60.0F;
                                this.pierce = true;
                                this.pierceBuilding = true;
                                this.pierceCap = 4;
                                this.backColor = this.trailColor = this.hitColor = this.lightColor = this.lightningColor = Pal.heal;
                                this.frontColor = Color.white;
                                this.trailWidth = 4.0F;
                                this.width = 9.0F;
                                this.height = 15.0F;
                                this.splashDamage = 15.0F;
                                this.splashDamageRadius = 25.0F;
                                this.healPercent = 3.0F;
                                this.homingRange = 70.0F;
                                this.homingPower = 0.05F;
                            }
                        };
                    }
                });
            }
        };
        araneidae = new UnityUnitType("araneidae") {
            {
                this.groundLayer = 75.01F;
                this.drag = 0.1F;
                this.speed = 0.42F;
                this.hitSize = 35.5F;
                this.health = 52000.0F;
                this.rotateSpeed = 1.3F;
                this.legCount = 8;
                this.legMoveSpace = 0.76F;
                this.legPairOffset = 0.7F;
                this.legGroupSize = 2;
                this.legLength = 112.0F;
                this.legExtension = -8.25F;
                this.legBaseOffset = 8.0F;
                this.landShake = 2.4F;
                this.legLengthScl = 1.0F;
                this.rippleScale = 2.0F;
                this.legSpeed = 0.2F;
                this.legSplashDamage = 80.0F;
                this.legSplashRange = 40.0F;
                this.hovering = true;
                this.armor = 13.0F;
                this.allowLegStep = true;
                this.visualElevation = 0.95F;
                this.weapons.add(new Weapon("unity-araneidae-mount") {
                    {
                        this.x = 15.0F;
                        this.y = -1.75F;
                        this.shootY = 7.5F;
                        this.reload = 30.0F;
                        this.shake = 4.0F;
                        this.rotateSpeed = 2.0F;
                        this.rotate = true;
                        this.shadow = 15.0F;
                        this.shots = 3;
                        this.spacing = 15.0F;
                        this.shootSound = Sounds.laser;
                        this.bullet = UnityBullets.sapLaser;
                    }
                }, new MultiBarrelWeapon("unity-araneidae-cannon") {
                    {
                        this.mirror = false;
                        this.x = 0.0F;
                        this.y = -12.25F;
                        this.shootY = 22.0F;
                        this.reload = 120.0F;
                        this.shake = 10.0F;
                        this.recoil = 3.0F;
                        this.rotateSpeed = 1.0F;
                        this.ejectEffect = Fx.none;
                        this.shootSound = Sounds.railgun;
                        this.rotate = true;
                        this.shadow = 40.0F;
                        this.barrelSpacing = 11.25F;
                        this.barrelOffset = 8.5F;
                        this.barrelRecoil = 5.0F;
                        this.barrels = 2;
                        this.bullet = new SlowRailBulletType(15.0F, 95.0F) {
                            {
                                this.lifetime = 20.0F;
                                this.splashDamageRadius = 90.0F;
                                this.splashDamage = 90.0F;
                                this.hitEffect = Fx.sapExplosion;
                                this.ammoMultiplier = 4.0F;
                                this.trailEffect = TrailFx.coloredRailgunSmallTrail;
                                this.trailSpacing = 15.0F;
                                this.backColor = this.trailColor = Pal.sapBulletBack;
                                this.frontColor = this.lightningColor = Pal.sapBullet;
                                this.lightning = 3;
                                this.lightningLength = 20;
                                this.smokeEffect = Fx.shootBigSmoke2;
                                this.hitShake = 10.0F;
                                this.lightRadius = 40.0F;
                                this.lightColor = Pal.sap;
                                this.lightOpacity = 0.6F;
                                this.width = 12.0F;
                                this.height = 23.0F;
                                this.shrinkY = 0.0F;
                                this.collidesAir = false;
                                this.scaleVelocity = true;
                                this.pierceCap = 2;
                                this.status = StatusEffects.sapped;
                                this.statusDuration = 600.0F;
                                this.fragLifeMin = 0.3F;
                                this.fragBullets = 4;
                                this.fragBullet = UnityBullets.sapArtilleryFrag;
                            }
                        };
                    }
                });
            }
        };
        theraphosidae = new UnityUnitType("theraphosidae") {
            {
                this.speed = 0.4F;
                this.drag = 0.12F;
                this.hitSize = 49.0F;
                this.hovering = true;
                this.allowLegStep = true;
                this.health = 125000.0F;
                this.armor = 16.0F;
                this.rotateSpeed = 1.3F;
                this.legCount = 8;
                this.legGroupSize = 2;
                this.legMoveSpace = 0.7F;
                this.legPairOffset = 0.2F;
                this.legLength = 176.0F;
                this.legExtension = -24.0F;
                this.legBaseOffset = 9.0F;
                this.visualElevation = 1.0F;
                this.groundLayer = 75.02F;
                this.rippleScale = 3.4F;
                this.legSplashDamage = 130.0F;
                this.legSplashRange = 60.0F;
                this.targetAir = false;
                this.commandLimit = 5;
                this.weapons.add(new LimitedAngleWeapon(this.name + "-launcher") {
                    {
                        bottomWeapons.add(this);
                        this.x = 33.0F;
                        this.y = 8.5F;
                        this.shootY = 5.25F;
                        this.reload = 7.0F;
                        this.recoil = 1.0F;
                        this.rotate = true;
                        this.shootCone = 20.0F;
                        this.angleCone = 60.0F;
                        this.angleOffset = 45.0F;
                        this.inaccuracy = 25.0F;
                        this.xRand = 2.25F;
                        this.shots = 2;
                        this.shootSound = Sounds.missile;
                        this.bullet = new MissileBulletType(3.7F, 15.0F) {
                            {
                                this.width = 10.0F;
                                this.height = 12.0F;
                                this.shrinkY = 0.0F;
                                this.drag = -0.01F;
                                this.splashDamageRadius = 30.0F;
                                this.splashDamage = 55.0F;
                                this.ammoMultiplier = 5.0F;
                                this.hitEffect = Fx.blastExplosion;
                                this.despawnEffect = Fx.blastExplosion;
                                this.backColor = this.trailColor = Pal.sapBulletBack;
                                this.frontColor = this.lightningColor = this.lightColor = Pal.sapBullet;
                                this.trailLength = 13;
                                this.homingRange = 80.0F;
                                this.weaveScale = 8.0F;
                                this.weaveMag = 2.0F;
                                this.lightning = 2;
                                this.lightningLength = 2;
                                this.lightningLengthRand = 1;
                                this.lightningCone = 15.0F;
                                this.status = StatusEffects.blasted;
                                this.statusDuration = 60.0F;
                            }
                        };
                    }
                }, new LimitedAngleWeapon(this.name + "-mount") {
                    {
                        this.x = 26.75F;
                        this.y = 7.5F;
                        this.shootY = 5.25F;
                        this.reload = 120.0F;
                        this.angleCone = 60.0F;
                        this.rotate = true;
                        this.continuous = true;
                        this.alternate = false;
                        this.rotateSpeed = 1.5F;
                        this.recoil = 5.0F;
                        this.shootSound = UnitySounds.continuousLaserA;
                        this.bullet = UnityBullets.continuousSapLaser;
                    }
                }, new Weapon(this.name + "-railgun") {
                    {
                        this.x = 20.5F;
                        this.y = -10.0F;
                        this.shootY = 16.5F;
                        this.shootSound = Sounds.railgun;
                        this.rotate = true;
                        this.alternate = true;
                        this.rotateSpeed = 0.9F;
                        this.cooldownTime = 90.0F;
                        this.reload = 90.0F;
                        this.shake = 6.0F;
                        this.recoil = 8.0F;
                        this.bullet = new SlowRailBulletType(15.0F, 95.0F) {
                            {
                                this.lifetime = 23.0F;
                                this.splashDamageRadius = 110.0F;
                                this.splashDamage = 90.0F;
                                this.hitEffect = Fx.sapExplosion;
                                this.ammoMultiplier = 4.0F;
                                this.trailEffect = TrailFx.coloredRailgunSmallTrail;
                                this.trailSpacing = 15.0F;
                                this.backColor = this.trailColor = Pal.sapBulletBack;
                                this.frontColor = this.lightningColor = Pal.sapBullet;
                                this.lightning = 3;
                                this.lightningLength = 20;
                                this.smokeEffect = Fx.shootBigSmoke2;
                                this.hitShake = 10.0F;
                                this.lightRadius = 40.0F;
                                this.lightColor = Pal.sap;
                                this.lightOpacity = 0.6F;
                                this.width = 13.0F;
                                this.height = 27.0F;
                                this.shrinkY = 0.0F;
                                this.collidesAir = false;
                                this.scaleVelocity = true;
                                this.pierceCap = 3;
                                this.status = StatusEffects.sapped;
                                this.statusDuration = 600.0F;
                                this.fragLifeMin = 0.3F;
                                this.fragBullets = 4;
                                this.fragBullet = UnityBullets.sapArtilleryFrag;
                            }
                        };
                    }
                });
            }
        };
        mantle = new UnityUnitType("mantle") {
            {
                this.health = 54000.0F;
                this.armor = 17.0F;
                this.speed = 0.45F;
                this.accel = 0.04F;
                this.drag = 0.04F;
                this.rotateSpeed = 0.9F;
                this.flying = true;
                this.lowAltitude = true;
                this.destructibleWreck = false;
                this.targetFlags = new BlockFlag[]{BlockFlag.reactor, null};
                this.hitSize = 80.0F;
                this.engineOffset = 42.75F;
                this.engineSize = 5.75F;
                final BulletType b = ((Weapon)UnitTypes.scepter.weapons.get(0)).bullet.copy();
                b.speed = 6.5F;
                b.damage = 60.0F;
                b.lifetime = 47.0F;
                this.weapons.add(new Weapon() {
                    {
                        this.x = 0.0F;
                        this.y = 0.0F;
                        this.shootY = 4.0F;
                        this.mirror = false;
                        this.reload = 240.0F;
                        this.continuous = true;
                        this.recoil = 0.0F;
                        this.shootStatus = StatusEffects.slow;
                        this.shootStatusDuration = 180.0F;
                        this.bullet = new AcceleratingLaserBulletType(230.0F) {
                            {
                                this.lifetime = 180.0F;
                                this.maxLength = 380.0F;
                                this.maxRange = 330.0F;
                                this.oscOffset = 0.1F;
                                this.incendChance = 0.2F;
                                this.incendAmount = 2;
                                this.width = 27.0F;
                                this.collisionWidth = 10.0F;
                                this.pierceCap = 2;
                                this.hitEffect = HitFx.coloredHitLarge;
                                this.hitColor = Pal.meltdownHit;
                            }
                        };
                    }
                }, new Weapon(this.name + "-mount") {
                    {
                        this.x = 30.75F;
                        this.y = -6.25F;
                        this.shootY = 10.5F;
                        this.alternate = true;
                        this.rotate = true;
                        this.recoil = 5.0F;
                        this.reload = 55.0F;
                        this.shots = 4;
                        this.shotDelay = 4.0F;
                        this.rotateSpeed = 3.0F;
                        this.shadow = 22.0F;
                        this.bullet = b;
                    }
                }, new Weapon(this.name + "-mount") {
                    {
                        this.x = 19.0F;
                        this.y = -18.0F;
                        this.shootY = 10.5F;
                        this.alternate = true;
                        this.rotate = true;
                        this.recoil = 5.0F;
                        this.reload = 60.0F;
                        this.shots = 4;
                        this.shotDelay = 4.0F;
                        this.rotateSpeed = 3.0F;
                        this.shadow = 22.0F;
                        this.bullet = b;
                    }
                });
            }
        };
        aphelion = new UnityUnitType("aphelion") {
            {
                this.health = 130000.0F;
                this.armor = 16.0F;
                this.speed = 0.44F;
                this.accel = 0.04F;
                this.drag = 0.03F;
                this.rotateSpeed = 0.7F;
                this.flying = true;
                this.lowAltitude = true;
                this.destructibleWreck = false;
                this.targetFlags = new BlockFlag[]{BlockFlag.reactor, null};
                this.hitSize = 96.0F;
                this.engineOffset = 46.5F;
                this.engineSize = 6.75F;
                final BulletType b = ((Weapon)UnitTypes.scepter.weapons.get(0)).bullet.copy();
                b.speed = 6.5F;
                b.damage = 40.0F;
                b.lightning = 3;
                b.lightningDamage = 27.0F;
                b.lightningCone = 360.0F;
                b.lifetime = 50.0F;
                b.lightningLength = 14;
                b.lightningType = new BulletType(0.0F, 10.0F) {
                    {
                        this.lifetime = Fx.lightning.lifetime;
                        this.hitEffect = Fx.hitLancer;
                        this.despawnEffect = Fx.none;
                        this.status = StatusEffects.shocked;
                        this.statusDuration = 60.0F;
                        this.hittable = false;
                        this.lightningColor = b.lightningColor;
                        this.lightning = 1;
                        this.lightningCone = 65.0F;
                        this.lightningLength = 6;
                        this.lightningLengthRand = 3;
                    }

                    public void init(Bullet bx) {
                        if (Mathf.chance((double)0.3F)) {
                            Lightning.create(b.team, this.lightningColor, this.damage, b.x, b.y, b.rotation() + Mathf.range(this.lightningCone), this.lightningLength + Mathf.random(this.lightningLengthRand));
                        }

                    }

                    public void hit(Bullet bx, float x, float y) {
                    }
                };
                this.weapons.add(new Weapon(this.name + "-laser") {
                    {
                        this.x = 0.0F;
                        this.y = 0.0F;
                        this.shootY = 34.25F;
                        this.shootCone = 2.0F;
                        this.mirror = false;
                        this.reload = 420.0F;
                        this.continuous = true;
                        this.recoil = 0.0F;
                        this.cooldownTime = 360.0F;
                        this.bullet = new AcceleratingLaserBulletType(320.0F) {
                            {
                                this.lifetime = 240.0F;
                                this.maxLength = 430.0F;
                                this.maxRange = 400.0F;
                                this.oscOffset = 0.2F;
                                this.incendChance = 0.3F;
                                this.incendAmount = 2;
                                this.width = 37.0F;
                                this.collisionWidth = 16.0F;
                                this.accel = 60.0F;
                                this.laserSpeed = 20.0F;
                                this.splashDamage = 40.0F;
                                this.splashDamageRadius = 50.0F;
                                this.pierceCap = 5;
                                this.hitEffect = HitFx.coloredHitLarge;
                                this.hitColor = Pal.meltdownHit;
                            }
                        };
                        this.shootStatus = StatusEffects.slow;
                        this.shootStatusDuration = this.bullet.lifetime;
                    }
                }, new Weapon(this.name + "-mount") {
                    {
                        this.x = 30.0F;
                        this.y = -9.5F;
                        this.shootY = 14.25F;
                        this.shadow = 32.0F;
                        this.rotate = true;
                        this.rotateSpeed = 2.0F;
                        this.reload = 2.0F;
                        this.xRand = 3.0F;
                        this.inaccuracy = 4.0F;
                        this.bullet = b;
                    }
                });
            }
        };
        sedec = new UnityUnitType("sedec") {
            {
                this.defaultController = HealingDefenderAI::new;
                this.health = 45000.0F;
                this.armor = 20.0F;
                this.speed = 0.7F;
                this.rotateSpeed = 1.0F;
                this.accel = 0.04F;
                this.drag = 0.018F;
                this.flying = true;
                this.engineOffset = 48.0F;
                this.engineSize = 7.8F;
                this.rotateShooting = false;
                this.hitSize = 85.0F;
                this.payloadCapacity = 2460.16F;
                this.buildSpeed = 5.0F;
                this.drawShields = false;
                this.commandLimit = 8;
                this.buildBeamOffset = 29.5F;
                this.abilities.add(new ForceFieldAbility(190.0F, 6.0F, 8000.0F, 720.0F), new RepairFieldAbility(180.0F, 120.0F, 160.0F));
                this.weapons.add(new Weapon(this.name + "-laser") {
                    {
                        bottomWeapons.add(this);
                        this.x = 0.0F;
                        this.y = 0.0F;
                        this.shootY = 39.0F;
                        this.reload = 260.0F;
                        this.recoil = 3.0F;
                        this.continuous = this.rotate = true;
                        this.mirror = false;
                        this.rotateSpeed = 1.5F;
                        this.bullet = new HealingConeBulletType(3.0F) {
                            {
                                this.healPercent = 6.0F;
                                this.allyStatus = StatusEffects.overclock;
                                this.allyStatusDuration = 540.0F;
                                this.status = UnityStatusEffects.weaken;
                                this.statusDuration = 40.0F;
                                this.lifetime = 360.0F;
                            }
                        };
                    }
                });
            }
        };
        trigintaduo = new UnityUnitType("trigintaduo") {
            {
                this.defaultController = HealingDefenderAI::new;
                this.health = 52500.0F;
                this.armor = 22.0F;
                this.speed = 0.6F;
                this.rotateSpeed = 1.0F;
                this.accel = 0.04F;
                this.drag = 0.018F;
                this.flying = true;
                this.engineOffset = 41.25F;
                this.engineSize = 6.5F;
                this.rotateShooting = false;
                this.hitSize = 92.5F;
                this.payloadCapacity = 4199.0405F;
                this.buildSpeed = 6.0F;
                this.drawShields = false;
                this.commandLimit = 12;
                this.buildBeamOffset = 47.75F;
                this.weapons.add(new Weapon(this.name + "-heal-mount") {
                    {
                        this.x = 33.5F;
                        this.y = -7.75F;
                        this.shootY = 10.25F;
                        this.reload = 220.0F;
                        this.recoil = 3.0F;
                        this.shadow = 22.0F;
                        this.continuous = this.rotate = true;
                        this.alternate = false;
                        this.rotateSpeed = 3.5F;
                        this.bullet = new HealingConeBulletType(3.0F) {
                            {
                                this.healPercent = 3.0F;
                                this.cone = 15.0F;
                                this.scanAccuracy = 25;
                                this.allyStatus = StatusEffects.overclock;
                                this.allyStatusDuration = 540.0F;
                                this.status = UnityStatusEffects.weaken;
                                this.statusDuration = 40.0F;
                                this.lifetime = 360.0F;
                            }
                        };
                    }
                }, new EnergyChargeWeapon("") {
                    {
                        this.mirror = false;
                        this.x = 0.0F;
                        this.y = 10.75F;
                        this.shootY = 0.0F;
                        this.reload = 1800.0F;
                        this.shootCone = 360.0F;
                        this.ignoreRotation = true;
                        this.drawCharge = (unit, mount, charge) -> {
                            float rotation = unit.rotation - 90.0F;
                            float wx = unit.x + Angles.trnsx(rotation, this.x, this.y);
                            float wy = unit.y + Angles.trnsy(rotation, this.x, this.y);
                            Draw.color(Pal.heal);
                            UnityDrawf.shiningCircle(unit.id, Time.time, wx, wy, 13.0F * charge, 5, 70.0F, 15.0F, 6.0F * charge, 360.0F);
                            Draw.color(Color.white);
                            UnityDrawf.shiningCircle(unit.id, Time.time, wx, wy, 6.5F * charge, 5, 70.0F, 15.0F, 4.0F * charge, 360.0F);
                        };
                        this.bullet = new HealingNukeBulletType() {
                            {
                                this.allyStatus = StatusEffects.overclock;
                                this.allyStatusDuration = 900.0F;
                                this.status = UnityStatusEffects.disabled;
                                this.statusDuration = 120.0F;
                                this.healPercent = 20.0F;
                            }
                        };
                    }
                });
            }
        };
        fin = new UnityUnitType("fin") {
            {
                this.health = 36250.0F;
                this.speed = 0.5F;
                this.drag = 0.18F;
                this.hitSize = 77.5F;
                this.armor = 17.0F;
                this.accel = 0.19F;
                this.rotateSpeed = 0.86F;
                this.rotateShooting = false;
                this.trailLength = 70;
                this.trailX = 18.0F;
                this.trailY = -32.0F;
                this.trailScl = 3.5F;
                this.weapons.add(new Weapon(this.name + "-launcher") {
                    {
                        this.x = 19.0F;
                        this.y = 14.0F;
                        this.shootY = 8.0F;
                        this.rotate = true;
                        this.inaccuracy = 15.0F;
                        this.reload = 7.0F;
                        this.xRand = 2.25F;
                        this.shootSound = Sounds.missile;
                        this.bullet = UnityBullets.basicMissile;
                    }
                }, new Weapon(this.name + "-launcher") {
                    {
                        this.x = 24.5F;
                        this.y = -39.25F;
                        this.shootY = 8.0F;
                        this.rotate = true;
                        this.inaccuracy = 15.0F;
                        this.reload = 7.0F;
                        this.xRand = 2.25F;
                        this.shootSound = Sounds.missile;
                        this.bullet = UnityBullets.basicMissile;
                    }
                }, new MortarWeapon(this.name + "-mortar") {
                    {
                        this.x = 0.0F;
                        this.y = -13.75F;
                        this.shootY = 39.5F;
                        this.mirror = false;
                        this.rotate = true;
                        this.rotateSpeed = 1.0F;
                        this.shots = 3;
                        this.inaccuracy = 3.0F;
                        this.velocityRnd = 0.1F;
                        this.reload = 120.0F;
                        this.recoil = 2.0F;
                        this.bullet = new MortarBulletType(7.0F, 4.0F) {
                            {
                                this.width = this.height = 22.0F;
                                this.splashDamageRadius = 160.0F;
                                this.splashDamage = 160.0F;
                                this.trailWidth = 7.0F;
                                this.trailColor = Pal.bulletYellowBack;
                                this.hitEffect = HitFx.hitExplosionMassive;
                                this.lifetime = 65.0F;
                                this.fragBullet = Bullets.artilleryDense;
                                this.fragBullets = 7;
                                this.fragLifeMax = 0.15F;
                                this.fragLifeMin = 0.15F;
                                this.despawnHit = true;
                                this.collidesAir = false;
                            }
                        };
                    }
                });
            }
        };
        blue = new UnityUnitType("blue") {
            {
                this.health = 42500.0F;
                this.speed = 0.4F;
                this.drag = 0.18F;
                this.hitSize = 80.0F;
                this.armor = 18.0F;
                this.accel = 0.19F;
                this.rotateSpeed = 0.78F;
                this.rotateShooting = false;
                this.trailLength = 70;
                this.trailX = 26.0F;
                this.trailY = -42.0F;
                this.trailScl = 4.0F;
                float spawnTime = 900.0F;
                this.abilities.add(new UnitSpawnAbility(UnityUnitTypes.schistocerca, spawnTime, 24.75F, -29.5F), new UnitSpawnAbility(UnityUnitTypes.schistocerca, spawnTime, -24.75F, -29.5F));
                this.weapons.addAll(new Weapon[]{new LimitedAngleWeapon(this.name + "-front-cannon") {
                    {
                        bottomWeapons.add(this);
                        this.x = 22.25F;
                        this.y = 30.25F;
                        this.shootY = 9.5F;
                        this.recoil = 5.0F;
                        this.shots = 5;
                        this.shotDelay = 3.0F;
                        this.inaccuracy = 5.0F;
                        this.shootCone = 15.0F;
                        this.rotate = true;
                        angleLimit = 3.0F;
                        this.shootSound = Sounds.artillery;
                        this.reload = 25.0F;
                        this.bullet = Bullets.standardThoriumBig;
                    }
                }, new LimitedAngleWeapon(this.name + "-side-silo") {
                    {
                        bottomWeapons.add(this);
                        this.x = 29.75F;
                        this.y = -13.0F;
                        this.shootY = 7.0F;
                        this.xRand = 9.0F;
                        this.defaultAngle = this.angleOffset = 90.0F;
                        this.angleCone = 0.0F;
                        this.shootCone = 125.0F;
                        this.alternate = false;
                        this.rotate = true;
                        this.reload = 50.0F;
                        this.shots = 12;
                        this.shotDelay = 3.0F;
                        this.inaccuracy = 5.0F;
                        this.shootSound = Sounds.missile;
                        this.bullet = new GuidedMissileBulletType(3.0F, 20.0F) {
                            {
                                this.homingPower = 0.09F;
                                this.width = 8.0F;
                                this.height = 8.0F;
                                this.shrinkX = this.shrinkY = 0.0F;
                                this.drag = -0.003F;
                                this.keepVelocity = false;
                                this.splashDamageRadius = 40.0F;
                                this.splashDamage = 45.0F;
                                this.lifetime = 65.0F;
                                this.trailColor = Pal.missileYellowBack;
                                this.hitEffect = Fx.blastExplosion;
                                this.despawnEffect = Fx.blastExplosion;
                            }
                        };
                    }

                    protected Bullet bullet(Unit unit, float shootX, float shootY, float angle, float lifescl) {
                        Bullet b = super.bullet(unit, shootX, shootY, angle, lifescl);
                        if (b.type instanceof GuidedMissileBulletType) {
                            WeaponMount m = null;

                            for(WeaponMount mount : unit.mounts) {
                                if (mount.weapon == this) {
                                    m = mount;
                                    break;
                                }
                            }

                            if (m != null) {
                                b.data = m;
                            }
                        }

                        return b;
                    }
                }, new LimitedAngleWeapon(UnityUnitTypes.fin.name + "-launcher") {
                    {
                        this.x = 0.0F;
                        this.y = 21.0F;
                        this.shootY = 8.0F;
                        this.rotate = true;
                        this.mirror = false;
                        this.inaccuracy = 15.0F;
                        this.reload = 7.0F;
                        this.xRand = 2.25F;
                        this.shootSound = Sounds.missile;
                        this.angleCone = 135.0F;
                        this.bullet = UnityBullets.basicMissile;
                    }
                }, new PointDefenceMultiBarrelWeapon(this.name + "-flak-turret") {
                    {
                        this.x = 26.5F;
                        this.y = 15.0F;
                        this.shootY = 15.75F;
                        this.barrels = 2;
                        this.barrelOffset = 5.25F;
                        this.barrelSpacing = 6.5F;
                        this.barrelRecoil = 4.0F;
                        this.rotate = true;
                        this.mirrorBarrels = true;
                        this.alternate = false;
                        this.reload = 6.0F;
                        this.recoil = 0.5F;
                        this.shootCone = 7.0F;
                        this.shadow = 30.0F;
                        this.targetInterval = 20.0F;
                        this.autoTarget = true;
                        this.controllable = false;
                        this.bullet = new AntiBulletFlakBulletType(8.0F, 6.0F) {
                            {
                                this.lifetime = 45.0F;
                                this.splashDamage = 12.0F;
                                this.splashDamageRadius = 60.0F;
                                this.bulletRadius = 60.0F;
                                this.explodeRange = 45.0F;
                                this.bulletDamage = 18.0F;
                                this.width = 8.0F;
                                this.height = 12.0F;
                                this.scaleVelocity = true;
                                this.collidesGround = false;
                                this.status = StatusEffects.blasted;
                                this.statusDuration = 60.0F;
                            }
                        };
                    }
                }, new Weapon(this.name + "-railgun") {
                    {
                        this.x = 0.0F;
                        this.y = 0.0F;
                        this.shootY = 38.5F;
                        this.mirror = false;
                        this.rotate = true;
                        this.rotateSpeed = 0.7F;
                        this.shadow = 46.0F;
                        this.reload = 150.0F;
                        this.shootSound = Sounds.railgun;
                        this.bullet = new SlowRailBulletType(70.0F, 2100.0F) {
                            {
                                this.lifetime = 10.0F;
                                this.width = 20.0F;
                                this.height = 38.0F;
                                this.splashDamage = 50.0F;
                                this.splashDamageRadius = 30.0F;
                                this.pierceDamageFactor = 0.15F;
                                this.pierceCap = -1;
                                this.fragBullet = Bullets.standardDense;
                                this.fragBullets = 2;
                                this.fragCone = 20.0F;
                                this.fragLifeMin = 0.4F;
                                this.fragLifeMax = 0.7F;
                                this.trailSpacing = 40.0F;
                                this.trailEffect = TrailFx.coloredArrowTrail;
                                this.backColor = this.trailColor = Pal.bulletYellowBack;
                                this.frontColor = Pal.bulletYellow;
                                this.collisionWidth = 12.0F;
                            }
                        };
                    }
                }});
            }
        };
        philinopsis = new UnityUnitType("philinopsis") {
            {
                this.health = 38000.0F;
                this.speed = 0.5F;
                this.drag = 0.18F;
                this.hitSize = 77.5F;
                this.armor = 17.0F;
                this.accel = 0.19F;
                this.rotateSpeed = 0.86F;
                this.rotateShooting = false;
                this.weapons.add(new Weapon("aaa") {
                    {
                        this.rotate = true;
                        this.reload = 65.0F;
                        this.shake = 3.0F;
                        this.rotateSpeed = 2.0F;
                        this.shadow = 30.0F;
                        this.shootY = 7.0F;
                        this.recoil = 4.0F;
                        this.cooldownTime = this.reload - 10.0F;
                        this.shootSound = Sounds.laser;
                        this.bullet = new TrailingEmpBulletType() {
                            {
                                this.scaleVelocity = true;
                                this.lightOpacity = 0.7F;
                                this.unitDamageScl = 0.8F;
                                this.healPercent = 20.0F;
                                this.timeIncrease = 3.0F;
                                this.timeDuration = 1200.0F;
                                this.powerDamageScl = 3.0F;
                                this.damage = 60.0F;
                                this.hitColor = this.lightColor = Pal.heal;
                                this.lightRadius = 70.0F;
                                clipSize = 250.0F;
                                this.shootEffect = Fx.hitEmpSpark;
                                this.smokeEffect = Fx.shootBigSmoke2;
                                this.lifetime = 60.0F;
                                this.sprite = "circle-bullet";
                                this.backColor = Pal.heal;
                                this.frontColor = Color.white;
                                this.width = this.height = 12.0F;
                                this.speed = 5.0F;
                                this.trailEffect = TrailFx.spikedEnergyTrail;
                                this.trailLength = 20;
                                this.trailWidth = 6.0F;
                                this.trailColor = Pal.heal;
                                this.trailInterval = 3.0F;
                                this.trailRotation = true;
                                this.splashDamage = 70.0F;
                                this.radius = this.splashDamageRadius = 124.0F;
                                this.empRadius = 68.0F;
                                this.hitShake = 4.0F;
                                this.status = StatusEffects.electrified;
                                this.hitSound = Sounds.plasmaboom;
                                this.hitEffect = HitFx.philinopsisEmpHit;
                                this.zapEffect = HitFx.philinopsisEmpZap;
                            }
                        };
                    }
                });
            }
        };
        chelidonura = new UnityUnitType("chelidonura") {
        };
        amphibiNaval = new UnityUnitType("amphibi-naval") {
            {
                this.toTrans = (unit) -> UnityUnitTypes.amphibi;
                this.speed = 1.3F;
                this.health = 365.0F;
                this.engineSize = 5.0F;
                this.engineOffset = 12.0F;
                this.accel = 0.3F;
                this.baseRotateSpeed = 0.2F;
                this.rotateSpeed = 1.6F;
                this.hitSize = 12.0F;
                this.armor = 2.0F;
                this.immunities.add(StatusEffects.wet);
                this.trailX = 3.0F;
                this.trailY = -5.0F;
                this.trailLength = 13;
                this.trailScl = 1.75F;
                this.rotateShooting = true;
                this.transformTime = 10.0F;
                this.weapons.add(new Weapon("artillery") {
                    {
                        this.reload = 35.0F;
                        this.x = 5.5F;
                        this.y = -4.0F;
                        this.shots = 2;
                        this.shotDelay = 2.0F;
                        this.inaccuracy = 5.0F;
                        this.rotate = true;
                        this.shake = 3.0F;
                        this.rotateSpeed = 4.0F;
                        this.bullet = new ArtilleryBulletType(2.1F, 1.0F) {
                            {
                                this.collidesTiles = true;
                                this.hitEffect = Fx.blastExplosion;
                                this.knockback = 0.8F;
                                this.speed = 2.1F;
                                this.lifetime = 80.0F;
                                this.width = this.height = 11.0F;
                                this.ammoMultiplier = 4.0F;
                                this.splashDamageRadius = 35.0F;
                                this.splashDamage = 25.0F;
                                this.backColor = UnityPal.navalReddish;
                                this.frontColor = this.lightningColor = UnityPal.navalYellowish;
                                this.smokeEffect = Fx.shootBigSmoke2;
                                shake = 4.5F;
                                this.statusDuration = 600.0F;
                            }
                        };
                    }
                });
            }

            public boolean isHidden() {
                return true;
            }
        };
        amphibi = new UnityUnitType("amphibi") {
            {
                this.toTrans = (unit) -> UnityUnitTypes.amphibiNaval;
                this.speed = 0.3F;
                this.health = 365.0F;
                this.armor = 1.0F;
                this.hitSize = 12.0F;
                this.hovering = true;
                this.allowLegStep = true;
                this.visualElevation = 0.5F;
                this.legCount = 6;
                this.legLength = 16.0F;
                this.legMoveSpace = 0.7F;
                this.legSpeed = 0.06F;
                this.legPairOffset = 0.9F;
                this.legGroupSize = 4;
                this.legBaseOffset = 0.0F;
                this.legExtension = -3.0F;
                this.kinematicScl = 0.6F;
                this.groundLayer = 65.0F;
                this.rippleScale = 1.0F;
                this.transformTime = 10.0F;
                this.weapons.add((Weapon)UnityUnitTypes.amphibiNaval.weapons.get(0));
            }
        };
        craberNaval = new UnityUnitType("craber-naval") {
            {
                this.toTrans = (unit) -> UnityUnitTypes.craber;
                this.speed = 1.2F;
                this.health = 730.0F;
                this.engineSize = 5.0F;
                this.engineOffset = 12.0F;
                this.accel = 0.26F;
                this.baseRotateSpeed = 1.6F;
                this.hitSize = 16.0F;
                this.armor = 2.0F;
                this.immunities.add(StatusEffects.wet);
                this.trailX = 3.0F;
                this.trailY = -9.0F;
                this.trailLength = 16;
                this.trailScl = 1.85F;
                this.rotateShooting = true;
                this.transformTime = 30.0F;
                this.weapons.add(new Weapon("unity-laser-weapon") {
                    {
                        this.reload = 5.0F;
                        this.x = 6.0F;
                        this.y = -3.0F;
                        this.rotate = true;
                        this.shake = 1.0F;
                        this.rotateSpeed = 6.0F;
                        this.bullet = new SapBulletType() {
                            {
                                this.sapStrength = 0.0F;
                                this.color = Color.white.cpy().lerp(Pal.lancerLaser, 0.5F);
                                this.damage = 35.0F;
                                this.lifetime = 22.0F;
                                this.status = StatusEffects.shocked;
                                this.statusDuration = 300.0F;
                                this.width = 0.7F;
                                this.length = 170.0F;
                            }
                        };
                    }
                });
            }

            public boolean isHidden() {
                return true;
            }
        };
        craber = new UnityUnitType("craber") {
            {
                this.toTrans = (unit) -> UnityUnitTypes.craberNaval;
                this.speed = 0.3F;
                this.health = 730.0F;
                this.armor = 10.0F;
                this.hitSize = 16.0F;
                this.hovering = true;
                this.allowLegStep = true;
                this.visualElevation = 0.5F;
                this.legCount = 6;
                this.legLength = 18.0F;
                this.legMoveSpace = 0.7F;
                this.legSpeed = 0.06F;
                this.legPairOffset = 0.9F;
                this.legGroupSize = 4;
                this.legBaseOffset = 0.0F;
                this.legExtension = -3.0F;
                this.kinematicScl = 0.7F;
                this.groundLayer = 65.0F;
                this.rippleScale = 1.0F;
                this.transformTime = 30.0F;
                this.weapons.add((Weapon)UnityUnitTypes.craberNaval.weapons.get(0));
            }
        };
        terra = new UnityUnitType("terra") {
            {
                this.speed = 0.2F;
                this.health = 900000.0F;
                this.worldWidth = 8;
                this.worldHeight = 18;
            }
        };
        hovos = new UnityUnitType("hovos") {
            {
                this.defaultController = DistanceGroundAI::new;
                this.speed = 0.8F;
                this.health = 340.0F;
                this.hitSize = 13.175F;
                this.range = 350.0F;
                this.allowLegStep = true;
                this.legMoveSpace = 0.7F;
                this.legTrns = 0.4F;
                this.legLength = 30.0F;
                this.legExtension = -4.3F;
                this.weapons.add(new Weapon("unity-small-scar-railgun") {
                    {
                        this.reload = 120.0F;
                        this.x = 0.0F;
                        this.y = -2.0F;
                        this.shootY = 9.0F;
                        this.mirror = false;
                        this.rotate = true;
                        this.shake = 2.3F;
                        this.rotateSpeed = 2.0F;
                        this.bullet = new RailBulletType() {
                            {
                                this.damage = 500.0F;
                                this.length = 354.0F;
                                this.updateEffectSeg = 59.0F;
                                this.shootEffect = ShootFx.scarRailShoot;
                                this.pierceEffect = HitFx.scarRailHit;
                                this.updateEffect = UnityFx.scarRailTrail;
                                this.hitEffect = Fx.massiveExplosion;
                                this.pierceDamageFactor = 0.3F;
                            }
                        };
                    }
                });
            }
        };
        ryzer = new UnityUnitType("ryzer") {
            {
                this.defaultController = DistanceGroundAI::new;
                this.speed = 0.7F;
                this.health = 640.0F;
                this.hitSize = 16.15F;
                this.range = 350.0F;
                this.allowLegStep = true;
                this.legMoveSpace = 0.73F;
                this.legCount = 6;
                this.legTrns = 0.4F;
                this.legLength = 32.0F;
                this.legExtension = -4.3F;
                this.weapons.add(new Weapon() {
                    {
                        this.reload = 150.0F;
                        this.x = 0.0F;
                        this.y = 7.5F;
                        this.shootY = 2.0F;
                        this.mirror = false;
                        this.shake = 2.3F;
                        this.bullet = new RailBulletType() {
                            {
                                this.damage = 700.0F;
                                this.length = 413.0F;
                                this.updateEffectSeg = 59.0F;
                                this.shootEffect = ShootFx.scarRailShoot;
                                this.pierceEffect = HitFx.scarRailHit;
                                this.updateEffect = UnityFx.scarRailTrail;
                                this.hitEffect = Fx.massiveExplosion;
                                this.pierceDamageFactor = 0.3F;
                            }
                        };
                    }
                }, new Weapon("unity-scar-missile-launcher") {
                    {
                        this.reload = 50.0F;
                        this.x = 6.25F;
                        this.shots = 5;
                        this.shotDelay = 3.0F;
                        this.inaccuracy = 4.0F;
                        this.rotate = true;
                        this.bullet = new MissileBulletType(5.0F, 1.0F) {
                            {
                                this.speed = 5.0F;
                                this.width = 7.0F;
                                this.height = 12.0F;
                                this.shrinkY = 0.0F;
                                this.backColor = this.trailColor = UnityPal.scarColor;
                                this.frontColor = UnityPal.endColor;
                                this.splashDamage = 25.0F;
                                this.splashDamageRadius = 20.0F;
                                this.weaveMag = 3.0F;
                                this.weaveScale = 4.0F;
                            }
                        };
                    }
                });
            }
        };
        zena = new UnityUnitType("zena") {
            {
                this.defaultController = DistanceGroundAI::new;
                this.speed = 0.7F;
                this.health = 1220.0F;
                this.hitSize = 17.85F;
                this.range = 350.0F;
                this.allowLegStep = true;
                this.legMoveSpace = 0.73F;
                this.legCount = 6;
                this.legTrns = 0.4F;
                this.legLength = 40.0F;
                this.legExtension = -9.3F;
                this.weapons.add(new Weapon() {
                    {
                        this.x = 0.0F;
                        this.y = 12.0F;
                        this.shootY = 0.0F;
                        this.mirror = false;
                        this.rotate = false;
                        this.shake = 2.3F;
                        this.reload = 165.0F;
                        this.bullet = new RailBulletType() {
                            {
                                this.damage = 780.0F;
                                this.length = 420.0F;
                                this.updateEffectSeg = 60.0F;
                                this.shootEffect = ShootFx.scarRailShoot;
                                this.pierceEffect = HitFx.scarRailHit;
                                this.updateEffect = UnityFx.scarRailTrail;
                                this.hitEffect = Fx.massiveExplosion;
                                this.pierceDamageFactor = 0.2F;
                            }
                        };
                    }
                }, new Weapon() {
                    {
                        this.x = 10.25F;
                        this.y = 2.0F;
                        this.rotate = false;
                        this.shake = 1.1F;
                        this.reload = 157.5F;
                        this.bullet = new RailBulletType() {
                            {
                                this.damage = 230.0F;
                                this.length = 280.0F;
                                this.updateEffectSeg = 40.0F;
                                this.shootEffect = ShootFx.scarRailShoot;
                                this.pierceEffect = HitFx.scarRailHit;
                                this.updateEffect = UnityFx.scarRailTrail;
                                this.hitEffect = Fx.massiveExplosion;
                                this.pierceDamageFactor = 0.5F;
                            }
                        };
                    }
                }, new Weapon("unity-scar-missile-launcher") {
                    {
                        this.x = 12.25F;
                        this.y = -5.0F;
                        this.rotate = true;
                        this.shots = 5;
                        this.shotDelay = 3.0F;
                        this.inaccuracy = 4.0F;
                        this.reload = 50.0F;
                        this.bullet = new MissileBulletType(5.0F, 0.0F) {
                            {
                                this.width = 7.0F;
                                this.height = 12.0F;
                                this.shrinkY = 0.0F;
                                this.backColor = this.trailColor = UnityPal.scarColor;
                                this.frontColor = UnityPal.endColor;
                                this.splashDamage = 30.0F;
                                this.splashDamageRadius = 20.0F;
                                this.weaveMag = 3.0F;
                                this.weaveScale = 4.0F;
                            }
                        };
                    }
                });
            }
        };
        sundown = new UnityUnitType("sundown") {
            {
                this.defaultController = DistanceGroundAI::new;
                this.speed = 0.6F;
                this.health = 9400.0F;
                this.hitSize = 36.0F;
                this.range = 360.0F;
                this.allowLegStep = true;
                this.legMoveSpace = 0.53F;
                this.rotateSpeed = 2.5F;
                this.armor = 4.0F;
                this.legCount = 4;
                this.legTrns = 0.4F;
                this.legLength = 44.0F;
                this.legExtension = -9.3F;
                this.legSplashDamage = 20.0F;
                this.legSplashRange = 30.0F;
                this.groundLayer = 75.0F;
                this.visualElevation = 0.65F;
                this.weapons.add(new Weapon("unity-scar-large-launcher") {
                    {
                        this.x = 13.5F;
                        this.y = -6.5F;
                        this.shootY = 5.0F;
                        this.shadow = 8.0F;
                        this.rotateSpeed = 5.0F;
                        this.rotate = true;
                        this.reload = 80.0F;
                        this.shake = 1.0F;
                        this.shots = 12;
                        this.inaccuracy = 19.0F;
                        this.velocityRnd = 0.2F;
                        this.xRand = 1.2F;
                        this.shootSound = Sounds.missile;
                        this.bullet = UnityBullets.scarMissile;
                    }
                }, new Weapon("unity-scar-railgun") {
                    {
                        this.x = 7.0F;
                        this.y = -9.25F;
                        this.shootY = 10.75F;
                        this.rotateSpeed = 2.0F;
                        this.rotate = true;
                        this.shadow = 12.0F;
                        this.reload = 162.0F;
                        this.shootSound = Sounds.artillery;
                        this.bullet = new RailBulletType() {
                            {
                                this.damage = 880.0F;
                                this.length = 427.0F;
                                this.updateEffectSeg = 61.0F;
                                this.shootEffect = ShootFx.scarRailShoot;
                                this.pierceEffect = HitFx.scarRailHit;
                                this.updateEffect = UnityFx.scarRailTrail;
                                this.hitEffect = Fx.massiveExplosion;
                                this.pierceDamageFactor = 0.2F;
                            }
                        };
                    }
                });
                DirectionShieldAbility shield = new DirectionShieldAbility(4, 0.1F, 20.0F, 1600.0F, 2.3F, 1.3F, 32.2F);
                shield.healthBarColor = UnityPal.endColor;
                this.abilities.add(shield);
            }
        };
        rex = new UnityUnitType("rex") {
            {
                this.defaultController = DistanceGroundAI::new;
                this.speed = 0.55F;
                this.health = 23000.0F;
                this.hitSize = 47.5F;
                this.range = 390.0F;
                this.allowLegStep = true;
                this.rotateSpeed = 2.0F;
                this.armor = 12.0F;
                this.hovering = true;
                this.groundLayer = 75.01F;
                this.visualElevation = 0.95F;
                this.legCount = 4;
                this.legTrns = 1.0F;
                this.legLength = 56.0F;
                this.legExtension = -9.5F;
                this.legSplashDamage = 90.0F;
                this.legSplashRange = 65.0F;
                this.legSpeed = 0.08F;
                this.legMoveSpace = 0.57F;
                this.legPairOffset = 0.8F;
                this.weapons.add(new Weapon(this.name + "-railgun") {
                    {
                        this.x = 31.25F;
                        this.y = -12.25F;
                        this.shootY = 23.25F;
                        this.rotate = false;
                        this.top = false;
                        this.reload = 270.0F;
                        this.recoil = 4.0F;
                        this.shootSound = Sounds.artillery;
                        this.bullet = new RailBulletType() {
                            {
                                this.damage = 3300.0F;
                                this.buildingDamageMultiplier = 0.5F;
                                this.length = 488.0F;
                                this.updateEffectSeg = 61.0F;
                                this.shootEffect = ShootFx.scarRailShoot;
                                this.pierceEffect = HitFx.scarRailHit;
                                this.updateEffect = UnityFx.scarRailTrail;
                                this.hitEffect = Fx.massiveExplosion;
                                this.pierceDamageFactor = 0.35F;
                            }

                            public void init(Bullet b) {
                                b.fdata = this.length;
                                Damage.collideLine(b, b.team, b.type.hitEffect, b.x, b.y, b.rotation(), this.length, true);
                                float resultLen = b.fdata;
                                Vec2 nor = Tmp.v1.set(b.vel).nor();

                                for(float i = 0.0F; i <= resultLen; i += this.updateEffectSeg) {
                                    this.updateEffect.at(b.x + nor.x * i, b.y + nor.y * i, b.rotation());
                                }

                            }
                        };
                    }
                }, new Weapon("unity-scar-large-launcher") {
                    {
                        this.x = 12.25F;
                        this.y = 13.0F;
                        this.shootY = 5.0F;
                        this.xRand = 2.2F;
                        this.shadow = 8.0F;
                        this.rotateSpeed = 5.0F;
                        this.rotate = true;
                        this.reload = 4.0F;
                        this.inaccuracy = 5.0F;
                        this.bullet = new BasicBulletType(6.0F, 12.0F) {
                            {
                                this.lifetime = 35.0F;
                                this.width = 7.0F;
                                this.height = 12.0F;
                                this.pierce = true;
                                this.pierceBuilding = true;
                                this.pierceCap = 2;
                            }
                        };
                    }
                }, new Weapon("unity-scar-large-launcher") {
                    {
                        this.x = 15.75F;
                        this.y = -17.5F;
                        this.shootY = 5.0F;
                        this.shadow = 8.0F;
                        this.rotateSpeed = 5.0F;
                        this.rotate = true;
                        this.reload = 85.0F;
                        this.shake = 1.0F;
                        this.shots = 9;
                        this.inaccuracy = 19.0F;
                        this.velocityRnd = 0.2F;
                        this.xRand = 1.2F;
                        this.shootSound = Sounds.missile;
                        this.bullet = UnityBullets.scarMissile;
                    }
                }, new Weapon("unity-scar-large-launcher") {
                    {
                        this.x = 9.25F;
                        this.y = -13.75F;
                        this.shootY = 5.0F;
                        this.shadow = 8.0F;
                        this.rotateSpeed = 5.0F;
                        this.rotate = true;
                        this.reload = 90.0F;
                        this.shake = 1.0F;
                        this.shots = 9;
                        this.inaccuracy = 19.0F;
                        this.velocityRnd = 0.2F;
                        this.xRand = 1.2F;
                        this.shootSound = Sounds.missile;
                        this.bullet = UnityBullets.scarMissile;
                    }
                });
                DirectionShieldAbility shield = new DirectionShieldAbility(3, 0.06F, 45.0F, 3100.0F, 3.3F, 0.9F, 49.0F);
                shield.healthBarColor = UnityPal.endColor;
                this.abilities.add(shield);
            }
        };
        excelsus = new UnityUnitType("excelsus") {
            {
                this.defaultController = DistanceGroundAI::new;
                this.speed = 0.6F;
                this.health = 38000.0F;
                this.hitSize = 66.5F;
                this.range = 370.0F;
                this.allowLegStep = true;
                this.rotateSpeed = 1.4F;
                this.armor = 18.0F;
                this.customBackLegs = true;
                this.hovering = true;
                this.groundLayer = 75.03F;
                this.visualElevation = 1.1F;
                this.legCount = 6;
                this.legTrns = 1.0F;
                this.legLength = 62.0F;
                this.legExtension = -9.5F;
                this.legSplashDamage = 120.0F;
                this.legSplashRange = 85.0F;
                this.legSpeed = 0.06F;
                this.legMoveSpace = 0.57F;
                this.legPairOffset = 0.8F;
                this.kinematicScl = 0.7F;
                this.immunities = ObjectSet.with(new StatusEffect[]{StatusEffects.burning});
                this.weapons.add(new Weapon("unity-scar-large-launcher") {
                    {
                        this.x = 8.25F;
                        this.y = -18.5F;
                        this.shootY = 5.0F;
                        this.shadow = 8.0F;
                        this.rotateSpeed = 5.0F;
                        this.rotate = true;
                        this.reload = 80.0F;
                        this.shake = 1.0F;
                        this.shots = 12;
                        this.inaccuracy = 19.0F;
                        this.velocityRnd = 0.2F;
                        this.xRand = 1.2F;
                        this.shootSound = Sounds.missile;
                        this.bullet = UnityBullets.scarMissile;
                    }
                }, new Weapon("unity-scar-large-launcher") {
                    {
                        this.x = 13.75F;
                        this.y = -24.5F;
                        this.shootY = 5.0F;
                        this.shadow = 8.0F;
                        this.rotateSpeed = 5.0F;
                        this.rotate = true;
                        this.reload = 75.0F;
                        this.shake = 1.0F;
                        this.shots = 12;
                        this.inaccuracy = 19.0F;
                        this.velocityRnd = 0.2F;
                        this.xRand = 1.2F;
                        this.shootSound = Sounds.missile;
                        this.bullet = UnityBullets.scarMissile;
                    }
                }, new Weapon("unity-scar-small-laser-weapon") {
                    {
                        this.x = 18.25F;
                        this.y = 11.75F;
                        this.shootY = 4.0F;
                        this.rotateSpeed = 5.0F;
                        this.rotate = true;
                        this.reload = 180.0F;
                        this.shake = 1.2F;
                        this.continuous = true;
                        this.alternate = false;
                        this.shootSound = Sounds.none;
                        this.bullet = new ContinuousLaserBulletType(40.0F) {
                            {
                                this.length = 180.0F;
                                this.lifetime = 600.0F;
                                this.shake = 1.2F;
                                this.incendChance = 0.0F;
                                this.largeHit = false;
                                this.colors = new Color[]{UnityPal.scarColorAlpha, UnityPal.endColor, Color.white};
                                this.width = 4.0F;
                                this.hitColor = UnityPal.scarColor;
                                this.lightColor = UnityPal.scarColorAlpha;
                                this.hitEffect = HitFx.scarHitSmall;
                            }
                        };
                    }
                }, new Weapon(this.name + "-laser-weapon") {
                    {
                        this.x = 29.75F;
                        this.y = -20.5F;
                        this.shootY = 7.0F;
                        this.shadow = 19.0F;
                        this.rotateSpeed = 1.5F;
                        this.rotate = true;
                        this.reload = 420.0F;
                        this.shake = 2.0F;
                        this.continuous = true;
                        this.alternate = false;
                        this.shootSound = Sounds.none;
                        this.bullet = new ContinuousLaserBulletType(210.0F) {
                            {
                                this.length = 360.0F;
                                this.lifetime = 180.0F;
                                this.shake = 3.0F;
                                this.colors = new Color[]{UnityPal.scarColorAlpha, UnityPal.endColor, Color.white};
                                this.width = 8.0F;
                                this.hitColor = UnityPal.scarColor;
                                this.lightColor = UnityPal.scarColorAlpha;
                                this.hitEffect = HitFx.scarHitSmall;
                            }
                        };
                    }
                });
                DirectionShieldAbility shield = new DirectionShieldAbility(6, 0.04F, 29.0F, 3400.0F, 4.2F, 0.9F, 54.0F);
                shield.healthBarColor = UnityPal.endColor;
                this.abilities.add(shield);
            }
        };
        whirlwind = new UnityUnitType("whirlwind") {
            {
                this.health = 280.0F;
                this.rotateSpeed = 4.5F;
                this.faceTarget = false;
                this.flying = true;
                this.speed = 8.0F;
                this.drag = 0.019F;
                this.accel = 0.028F;
                this.hitSize = 8.0F;
                this.engineOffset = 8.0F;
                this.weapons.add(new Weapon() {
                    {
                        this.mirror = false;
                        this.x = 0.0F;
                        this.y = 4.0F;
                        this.minShootVelocity = 5.0F;
                        this.continuous = true;
                        this.shootStatus = UnityStatusEffects.reloadFatigue;
                        this.shootCone = 20.0F;
                        this.bullet = new SaberContinuousLaserBulletType(21.0F) {
                            {
                                this.lightStroke = 40.0F;
                                this.largeHit = false;
                                this.lifetime = 600.0F;
                                this.length = 160.0F;
                                this.width = 5.0F;
                                this.incendChance = 0.0F;
                                this.hitEffect = HitFx.coloredHitSmall;
                                this.lightColor = this.hitColor = UnityPal.scarColorAlpha;
                                this.colors = new Color[]{UnityPal.scarColorAlpha, UnityPal.endColor, Color.white};
                                this.strokes = new float[]{1.5F, 1.0F, 0.3F};
                            }
                        };
                        this.shootStatusDuration = this.bullet.lifetime;
                        this.reload = 120.0F;
                    }
                }, new Weapon() {
                    {
                        this.rotate = true;
                        this.x = 4.2F;
                        this.reload = 50.0F;
                        this.inaccuracy = 1.1F;
                        this.shots = 5;
                        this.shotDelay = 3.0F;
                        this.bullet = new MissileBulletType(5.0F, 1.0F) {
                            {
                                this.height = 10.0F;
                                this.shrinkY = 0.0F;
                                this.backColor = this.trailColor = UnityPal.scarColor;
                                this.frontColor = UnityPal.endColor;
                                this.splashDamage = 25.0F;
                                this.splashDamageRadius = 20.0F;
                                this.weaveMag = 3.0F;
                                this.weaveScale = 4.0F;
                            }
                        };
                    }
                });
            }
        };
        jetstream = new UnityUnitType("jetstream") {
            {
                this.health = 670.0F;
                this.rotateSpeed = 12.5F;
                this.flying = true;
                this.speed = 9.2F;
                this.drag = 0.019F;
                this.accel = 0.028F;
                this.hitSize = 11.0F;
                this.engineOffset = 11.0F;
                this.weapons.add(new Weapon() {
                    {
                        this.mirror = false;
                        this.x = 0.0F;
                        this.y = 7.0F;
                        this.continuous = true;
                        this.shootStatus = UnityStatusEffects.reloadFatigue;
                        this.shootCone = 15.0F;
                        this.bullet = new SaberContinuousLaserBulletType(35.0F) {
                            {
                                this.swipe = true;
                                this.lightStroke = 40.0F;
                                this.largeHit = false;
                                this.lifetime = 900.0F;
                                this.length = 150.0F;
                                this.width = 5.0F;
                                this.incendChance = 0.0F;
                                this.hitEffect = HitFx.coloredHitSmall;
                                this.lightColor = this.hitColor = UnityPal.scarColorAlpha;
                                this.colors = new Color[]{UnityPal.scarColorAlpha, UnityPal.endColor, Color.white};
                                this.strokes = new float[]{1.5F, 1.0F, 0.3F};
                                this.lenscales = new float[]{0.85F, 0.97F, 1.0F, 1.02F};
                            }
                        };
                        this.reload = 192.0F;
                        this.shootStatusDuration = this.bullet.lifetime;
                    }
                }, new Weapon("unity-small-scar-weapon") {
                    {
                        this.rotate = true;
                        this.x = 7.25F;
                        this.y = -3.5F;
                        this.reload = 50.0F;
                        this.inaccuracy = 1.1F;
                        this.shots = 6;
                        this.shotDelay = 4.0F;
                        this.bullet = new MissileBulletType(5.0F, 1.0F) {
                            {
                                this.width = 7.0F;
                                this.height = 12.0F;
                                this.shrinkY = 0.0F;
                                this.backColor = this.trailColor = UnityPal.scarColor;
                                this.frontColor = UnityPal.endColor;
                                this.splashDamage = 40.0F;
                                this.splashDamageRadius = 20.0F;
                                this.weaveMag = 3.0F;
                                this.weaveScale = 4.0F;
                            }
                        };
                    }
                });
            }
        };
        vortex = new UnityUnitType("vortex") {
            {
                this.health = 1200.0F;
                this.rotateSpeed = 12.5F;
                this.flying = true;
                this.speed = 9.1F;
                this.drag = 0.019F;
                this.accel = 0.028F;
                this.hitSize = 11.0F;
                this.engineOffset = 14.0F;
                this.weapons.add(new Weapon() {
                    {
                        this.mirror = false;
                        this.x = 0.0F;
                        this.continuous = true;
                        this.bullet = new SaberContinuousLaserBulletType(60.0F) {
                            {
                                this.swipe = true;
                                this.swipeDamageMultiplier = 1.2F;
                                this.largeHit = false;
                                this.lifetime = 300.0F;
                                this.length = 190.0F;
                                this.width = 5.0F;
                                this.incendChance = 0.0F;
                                this.hitEffect = HitFx.coloredHitSmall;
                                this.lightColor = this.hitColor = UnityPal.scarColorAlpha;
                                this.colors = new Color[]{UnityPal.scarColorAlpha, UnityPal.endColor, Color.white};
                                this.strokes = new float[]{1.5F, 1.0F, 0.3F};
                            }
                        };
                        this.reload = 120.0F;
                    }
                });
            }
        };
        arcnelidia = new UnityUnitType("arcnelidia") {
            {
                this.segmentOffset = 23.0F;
                this.hitSize = 17.0F;
                this.health = 800.0F;
                this.speed = 4.0F;
                this.accel = 0.035F;
                this.drag = 0.007F;
                this.rotateSpeed = 3.2F;
                this.engineSize = -1.0F;
                this.faceTarget = false;
                this.armor = 5.0F;
                this.flying = true;
                this.visualElevation = 0.8F;
                this.range = 210.0F;
                this.outlineColor = UnityPal.darkerOutline;
                this.weapons.add(new Weapon() {
                    {
                        this.x = 0.0F;
                        this.reload = 10.0F;
                        this.rotateSpeed = 50.0F;
                        this.shootSound = Sounds.laser;
                        this.mirror = this.rotate = true;
                        this.minShootVelocity = 2.1F;
                        this.bullet = new LaserBulletType(200.0F) {
                            {
                                this.colors = new Color[]{Pal.surge.cpy().mul(1.0F, 1.0F, 1.0F, 0.4F), Pal.surge, Color.white};
                                this.drawSize = 400.0F;
                                this.collidesAir = false;
                                this.length = 190.0F;
                            }
                        };
                    }
                });
                this.segWeapSeq.add(new Weapon() {
                    {
                        this.x = 0.0F;
                        this.reload = 60.0F;
                        this.rotateSpeed = 50.0F;
                        this.minShootVelocity = 0.01F;
                        this.bullet = ((Weapon)UnitTypes.horizon.weapons.first()).bullet;
                    }
                });
            }
        };
        rayTest = new UnityUnitType("ray-test") {
            {
                this.defaultController = GroundAI::new;
                this.flying = false;
                this.health = 500.0F;
                this.speed = 1.0F;
                this.accel = 0.035F;
                this.drag = 0.007F;
                this.rotateSpeed = 3.2F;
                this.range = 210.0F;
                this.laserRange = 30.0F;
                this.maxConnections = 2;
            }
        };
        exowalker = new UnityUnitType("exowalker") {
            {
                this.health = 6000.0F;
                this.speed = 0.7F;
                this.drag = 0.1F;
                this.hitSize = 33.0F;
                this.rotateSpeed = 2.0F;
                this.legCount = 8;
                this.legGroupSize = 4;
                this.legLength = 120.0F;
                this.legBaseOffset = 9.0F;
                this.legMoveSpace = 0.9F;
                this.legPairOffset = 1.5F;
                this.legTrns = 0.5F;
                this.hovering = true;
                this.allowLegStep = true;
                this.visualElevation = 0.7F;
                this.groundLayer = 75.01F;
                this.outlineColor = UnityPal.darkerOutline;
                CloneableSetWeapon t = UnityWeaponTemplates.plagueSmallMount;
                this.weapons.addAll(new Weapon[]{t.set((w) -> {
                    w.x = 9.5F;
                    w.y = 8.0F;
                    w.otherSide = 2;
                }), t.set((w) -> {
                    w.x = -9.5F;
                    w.y = 8.0F;
                    w.flipSprite = true;
                    w.otherSide = 0;
                }), t.set((w) -> {
                    w.x = 12.25F;
                    w.y = -12.25F;
                    w.name = w.name + "-flipped";
                    w.flipSprite = true;
                    w.otherSide = 3;
                }), t.set((w) -> {
                    w.x = -12.25F;
                    w.y = -12.25F;
                    w.flipSprite = true;
                    w.otherSide = 1;
                }), new Weapon("unity-drain-laser") {
                    {
                        this.x = 16.0F;
                        this.y = -2.25F;
                        this.shootY = 6.25F;
                        this.mirror = true;
                        this.rotate = true;
                        this.shots = 3;
                        this.spacing = 17.5F;
                        this.reload = 90.0F;
                        this.bullet = new ShrapnelBulletType() {
                            {
                                this.damage = 43.0F;
                                this.length = 80.0F;
                                this.toColor = UnityPal.plague;
                            }
                        };
                    }
                }});
            }
        };
        toxoswarmer = new UnityUnitType("toxoswarmer") {
            {
                this.health = 7000.0F;
                this.speed = 1.1F;
                this.drag = 0.1F;
                this.hitSize = 22.25F;
                this.rotateSpeed = 3.0F;
                this.hovering = true;
                this.allowLegStep = true;
                this.visualElevation = 0.7F;
                this.groundLayer = 75.01F;
                this.outlineColor = UnityPal.darkerOutline;
                this.legGroup.add(CLegType.createGroup(this.name + "-base", (g) -> {
                    g.baseRotateSpeed = 4.0F;
                    g.moveSpacing = 0.8F;
                }, new CLegType[]{new BasicLeg.BasicLegType(this.name + "-leg-small") {
                    {
                        this.x = 6.25F;
                        this.y = 10.75F;
                        this.targetX = 31.0F;
                        this.targetY = 53.5F;
                        this.baseLength = this.endLength = 32.0F;
                        this.legTrns = 0.8F;
                    }
                }, new BasicLeg.BasicLegType(this.name + "-leg-small") {
                    {
                        this.x = 12.5F;
                        this.y = 0.0F;
                        this.targetX = 61.75F;
                        this.targetY = 0.0F;
                        this.baseLength = this.endLength = 32.0F;
                        this.legTrns = 0.8F;
                    }
                }, new BasicLeg.BasicLegType(this.name + "-leg-small") {
                    {
                        this.x = 6.25F;
                        this.y = -10.75F;
                        this.targetX = 31.0F;
                        this.targetY = -53.5F;
                        this.baseLength = this.endLength = 32.0F;
                        this.legTrns = 0.8F;
                        this.flipped = true;
                    }
                }}), CLegType.createGroup(this.name + "-base", (g) -> {
                    g.baseRotateSpeed = 1.0F;
                    g.moveSpacing = 0.9F;
                }, new CLegType[]{new BasicLeg.BasicLegType(this.name + "-leg-large") {
                    {
                        this.x = this.y = 11.25F;
                        this.targetX = this.targetY = 77.5F;
                        this.baseLength = 55.0F;
                        this.endLength = 71.0F;
                        this.legTrns = 0.7F;
                    }
                }, new BasicLeg.BasicLegType(this.name + "-leg-large") {
                    {
                        this.x = 11.25F;
                        this.y = -11.25F;
                        this.targetX = 77.5F;
                        this.targetY = -77.5F;
                        this.baseLength = 55.0F;
                        this.endLength = 71.0F;
                        this.legTrns = 0.7F;
                        this.flipped = true;
                    }
                }}));
                this.weapons.add(new Weapon("unity-toxo-launcher") {
                    {
                        this.x = 17.0F;
                        this.y = -8.25F;
                        this.reload = 180.0F;
                        this.shots = 8;
                        this.shotDelay = 2.0F;
                        this.inaccuracy = 16.0F;
                        this.rotate = true;
                        this.rotateSpeed = 3.0F;
                        this.bullet = new ShootingBulletType("unity-toxo-missile", 4.0F, 200.0F) {
                            {
                                this.lifetime = 240.0F;
                                this.reloadTime = 4.0F;
                                this.minTargetRange = 60.0F;
                                this.maxRange = 220.0F;
                                this.trailColor = this.lightColor = this.lightningColor = UnityPal.plagueDark;
                                this.lightning = 3;
                                this.lightningLength = 3;
                                this.lightningDamage = 15.0F;
                                this.shootInaccuracy = 4.0F;
                                this.shootSound = Sounds.flame;
                                this.shootBullet = new FlameBulletType(5.0F, 15.0F) {
                                    {
                                        this.lifetime = 20.0F;
                                        this.colors = new Color[]{UnityPal.plague, UnityPal.plagueDark, Color.darkGray, Color.gray};
                                        this.pierceBuilding = this.pierce = true;
                                        this.collidesAir = true;
                                        this.knockback = 0.001F;
                                        this.pierceCap = 2;
                                        this.statusDuration = 60.0F;
                                        this.splashDamage = 4.0F;
                                        this.splashDamageRadius = 25.0F;
                                        this.particleSizeScl = 2.25F;
                                        this.particleSpread = 4.0F;
                                    }
                                };
                            }
                        };
                    }
                });
            }
        };
        toxobyte = new UnityUnitType("toxobyte") {
            {
                this.defaultController = WormAI::new;
                this.flying = true;
                this.health = 200.0F;
                this.speed = 3.0F;
                this.accel = 0.035F;
                this.drag = 0.012F;
                this.hitSize = 15.75F;
                this.segmentOffset = 16.25F;
                this.regenTime = 900.0F;
                this.splittable = true;
                this.circleTarget = true;
                this.omniMovement = false;
                this.angleLimit = 25.0F;
                this.segmentLength = 25;
                this.segmentDamageScl = 8.0F;
                this.engineSize = -1.0F;
                this.outlineColor = UnityPal.darkerOutline;
                this.weapons.add(new Weapon() {
                    {
                        this.x = 0.0F;
                        this.rotate = false;
                        this.mirror = false;
                        this.reload = 70.0F;
                        this.shots = 12;
                        this.shootCone = 90.0F;
                        this.inaccuracy = 35.0F;
                        this.xRand = 2.0F;
                        this.shotDelay = 0.5F;
                        this.bullet = new SapBulletType() {
                            {
                                this.color = UnityPal.plague;
                                this.damage = 20.0F;
                                this.length = 130.0F;
                                this.width = 1.0F;
                                this.status = StatusEffects.none;
                            }
                        };
                    }
                });
                this.segWeapSeq.add(new Weapon() {
                    {
                        this.rotate = true;
                        this.mirror = false;
                        this.reload = 60.0F;
                        this.bullet = new ArtilleryBulletType(5.0F, 7.0F) {
                            {
                                this.collidesTiles = this.collidesAir = this.collidesGround = true;
                                this.width = this.height = 11.0F;
                                this.splashDamage = 25.0F;
                                this.splashDamageRadius = 25.0F;
                                this.trailColor = this.hitColor = this.lightColor = this.backColor = UnityPal.plagueDark;
                                this.frontColor = UnityPal.plague;
                            }
                        };
                    }
                });
            }
        };
        catenapede = new UnityUnitType("catenapede") {
            {
                this.defaultController = WormAI::new;
                this.flying = true;
                this.health = 750.0F;
                this.speed = 2.4F;
                this.accel = 0.06F;
                this.drag = 0.03F;
                this.hitSize = 30.0F;
                this.segmentOffset = 31.0F;
                this.regenTime = 1800.0F;
                this.splittable = true;
                this.chainable = true;
                this.circleTarget = true;
                this.lowAltitude = true;
                this.omniMovement = false;
                this.rotateSpeed = 2.7F;
                this.angleLimit = 25.0F;
                this.segmentLength = 2;
                this.maxSegments = 15;
                this.segmentDamageScl = 12.0F;
                this.healthDistribution = 0.15F;
                this.engineSize = -1.0F;
                this.outlineColor = UnityPal.darkerOutline;
                this.range = 160.0F;
                this.weapons.add(new Weapon("unity-drain-laser") {
                    {
                        this.y = -9.0F;
                        this.x = 14.0F;
                        this.shootY = 6.75F;
                        this.rotateSpeed = 5.0F;
                        this.reload = 300.0F;
                        this.shootCone = 45.0F;
                        this.rotate = true;
                        this.continuous = true;
                        this.alternate = false;
                        this.shootSound = Sounds.respawning;
                        this.bullet = new PointDrainLaserBulletType(45.0F) {
                            {
                                this.healPercent = 0.5F;
                                this.maxLength = 160.0F;
                                this.knockback = -34.0F;
                                this.lifetime = 600.0F;
                            }
                        };
                    }
                });
                this.segWeapSeq.add(new Weapon("unity-small-plague-launcher") {
                    {
                        this.y = -8.0F;
                        this.x = 14.75F;
                        this.rotate = true;
                        this.reload = 25.0F;
                        this.shootSound = Sounds.missile;
                        this.bullet = UnityBullets.plagueMissile;
                    }
                }, new Weapon("unity-small-plague-launcher") {
                    {
                        this.y = -12.5F;
                        this.x = 7.25F;
                        this.rotate = true;
                        this.reload = 15.0F;
                        this.shootSound = Sounds.missile;
                        this.bullet = UnityBullets.plagueMissile;
                    }
                });
            }
        };
        buffer = new UnityUnitType("buffer") {
            {
                this.mineTier = 1;
                this.speed = 0.75F;
                this.boostMultiplier = 1.26F;
                this.itemCapacity = 15;
                this.health = 150.0F;
                this.buildSpeed = 0.9F;
                this.engineColor = Color.valueOf("d3ddff");
                this.canBoost = true;
                this.boostMultiplier = 1.5F;
                this.landShake = 1.0F;
                this.weapons.add(new Weapon(this.name + "-shotgun") {
                    {
                        this.top = false;
                        this.shake = 2.0F;
                        this.x = 3.0F;
                        this.y = 0.5F;
                        this.shootX = 0.0F;
                        this.shootY = 3.5F;
                        this.reload = 55.0F;
                        this.shotDelay = 3.0F;
                        this.alternate = true;
                        this.shots = 2;
                        this.inaccuracy = 0.0F;
                        this.ejectEffect = Fx.none;
                        this.shootSound = Sounds.spark;
                        this.bullet = new LightningBulletType() {
                            {
                                this.damage = 12.0F;
                                this.shootEffect = Fx.hitLancer;
                                this.smokeEffect = Fx.none;
                                this.despawnEffect = Fx.none;
                                this.hitEffect = Fx.hitLancer;
                                this.keepVelocity = false;
                            }
                        };
                    }
                });
                this.abilities.add(new LightningBurstAbility(120.0F, 8, 8.0F, 17.0F, 14, Pal.lancerLaser));
            }
        };
        omega = new UnityUnitType("omega") {
            {
                this.mineTier = 2;
                this.mineSpeed = 1.5F;
                this.itemCapacity = 80;
                this.speed = 0.4F;
                this.accel = 0.36F;
                this.canBoost = true;
                this.boostMultiplier = 0.6F;
                this.engineColor = Color.valueOf("feb380");
                this.health = 350.0F;
                this.buildSpeed = 1.5F;
                this.landShake = 4.0F;
                this.rotateSpeed = 3.0F;
                this.weapons.add(new Weapon(this.name + "-cannon") {
                    {
                        this.top = false;
                        this.x = 4.0F;
                        this.y = 0.0F;
                        this.shootX = 1.0F;
                        this.shootY = 3.0F;
                        this.recoil = 4.0F;
                        this.reload = 38.0F;
                        this.shots = 4;
                        this.spacing = 8.0F;
                        this.inaccuracy = 8.0F;
                        this.alternate = true;
                        this.ejectEffect = Fx.none;
                        this.shake = 3.0F;
                        this.shootSound = Sounds.shootBig;
                        this.bullet = new MissileBulletType(2.7F, 12.0F) {
                            {
                                this.width = this.height = 8.0F;
                                this.shrinkX = this.shrinkY = 0.0F;
                                this.drag = -0.003F;
                                this.homingRange = 60.0F;
                                this.keepVelocity = false;
                                this.splashDamageRadius = 25.0F;
                                this.splashDamage = 10.0F;
                                this.lifetime = 120.0F;
                                this.trailColor = Color.gray;
                                this.backColor = Pal.bulletYellowBack;
                                this.frontColor = Pal.bulletYellow;
                                this.hitEffect = Fx.blastExplosion;
                                this.despawnEffect = Fx.blastExplosion;
                                this.weaveScale = 8.0F;
                                this.weaveMag = 2.0F;
                                this.status = StatusEffects.blasted;
                                this.statusDuration = 60.0F;
                            }
                        };
                    }
                });
                String armorRegion = this.name + "-armor";
                this.abilities.add(new ShootArmorAbility(50.0F, 0.06F, 2.0F, 0.5F, armorRegion));
            }
        };
        cache = new UnityUnitType("cache") {
            {
                this.mineTier = -1;
                this.speed = 7.0F;
                this.drag = 0.001F;
                this.health = 560.0F;
                this.engineColor = Color.valueOf("d3ddff");
                this.flying = true;
                this.armor = 6.0F;
                this.accel = 0.02F;
                this.weapons.add(new Weapon() {
                    {
                        this.top = false;
                        this.shootY = 1.5F;
                        this.reload = 70.0F;
                        this.shots = 4;
                        this.inaccuracy = 2.0F;
                        this.alternate = true;
                        this.ejectEffect = Fx.none;
                        this.velocityRnd = 0.2F;
                        this.spacing = 1.0F;
                        this.shootSound = Sounds.missile;
                        this.bullet = new MissileBulletType(5.0F, 21.0F) {
                            {
                                this.width = 8.0F;
                                this.height = 8.0F;
                                this.shrinkY = 0.0F;
                                this.drag = -0.003F;
                                this.keepVelocity = false;
                                this.splashDamageRadius = 20.0F;
                                this.splashDamage = 1.0F;
                                this.lifetime = 60.0F;
                                this.trailColor = Color.valueOf("b6c6fd");
                                this.hitEffect = Fx.blastExplosion;
                                this.despawnEffect = Fx.blastExplosion;
                                this.backColor = Pal.bulletYellowBack;
                                this.frontColor = Pal.bulletYellow;
                                this.weaveScale = 8.0F;
                                this.weaveMag = 2.0F;
                            }
                        };
                    }
                });
                String shieldSprite = this.name + "-shield";
                this.abilities.add(new MoveLightningAbility(10.0F, 14, 0.15F, 4.0F, 3.6F, 6.0F, Pal.lancerLaser, shieldSprite));
            }
        };
        dijkstra = new UnityUnitType("dijkstra") {
            {
                this.mineTier = -1;
                this.speed = 7.5F;
                this.drag = 0.01F;
                this.health = 640.0F;
                this.flying = true;
                this.armor = 8.0F;
                this.accel = 0.01F;
                this.lowAltitude = true;
                this.range = 220.0F;
                this.abilities.add(new SlashAbility((unit) -> Units.closestEnemy(unit.team, unit.x, unit.y, 160.0F, (u) -> u.within(unit, 120.0F) && Angles.angleDist(unit.rotation, unit.angleTo(u)) < 5.0F) != null));
                this.weapons.add(new Weapon() {
                    {
                        this.rotate = true;
                        this.rotateSpeed = 8.0F;
                        this.shadow = 20.0F;
                        this.x = 0.0F;
                        this.y = 0.0F;
                        this.reload = 150.0F;
                        this.shots = 1;
                        this.alternate = false;
                        this.ejectEffect = Fx.none;
                        this.bullet = UnityBullets.laserZap;
                        this.shootSound = Sounds.laser;
                        this.mirror = false;
                    }
                }, new Weapon() {
                    {
                        this.x = 0.0F;
                        this.y = 0.0F;
                        this.reload = 7.0F;
                        this.shots = 1;
                        this.alternate = true;
                        this.ejectEffect = Fx.none;
                        this.velocityRnd = 1.5F;
                        this.spacing = 15.0F;
                        this.inaccuracy = 20.0F;
                        this.bullet = UnityBullets.plasmaBullet;
                        this.shootSound = Sounds.spark;
                    }
                });
            }
        };
        phantasm = new UnityUnitType("phantasm") {
            {
                this.mineTier = -1;
                this.speed = 5.6F;
                this.drag = 0.08F;
                this.accel = 0.08F;
                this.range = 240.0F;
                this.health = 720.0F;
                this.flying = true;
                this.hitSize = 15.0F;
                this.rotateSpeed = 12.0F;
                this.engineOffset = 4.6F;
                this.engineSize = 2.5F;
                this.abilities.add(new TeleportAbility<Bullet>((unit) -> {
                    Bullet[] should = new Bullet[]{null};
                    float rad = 24.0F + unit.hitSize() / 2.0F;
                    Groups.bullet.intersect(unit.x - rad, unit.y - rad, rad * 2.0F, rad * 2.0F, (b) -> {
                        if (unit.team.isEnemy(b.team) && b.within(unit, rad) && b.collides(unit) && should[0] == null) {
                            should[0] = b;
                        }

                    });
                    return should[0];
                }, 120.0F) {
                    {
                        this.slots = 5;
                        this.rechargeTime = 180.0F;
                        this.delayTime = 60.0F;
                        this.waitEffect = UnityFx.waitEffect2;
                        this.rechargeEffect = UnityFx.ringEffect2;
                        this.delayEffect = UnityFx.smallRingEffect2;
                    }
                });
                this.weapons.add(new Weapon("unity-phantasmal-gun") {
                    {
                        this.top = false;
                        this.x = 1.25F;
                        this.y = 3.25F;
                        this.reload = 9.0F;
                        this.inaccuracy = 2.0F;
                        this.ejectEffect = Fx.casing2;
                        this.shootSound = Sounds.shootBig;
                        this.bullet = UnityBullets.phantasmalBullet;
                    }
                });
            }
        };
        MonolithUnitTypes.load();
        kami = new RainbowUnitType("kami-mkii") {
            {
                this.defaultController = EmptyAI::new;
                this.health = 120000.0F;
                this.speed = 15.0F;
                this.hitSize = 36.0F;
                this.flying = true;
                this.drawCell = false;
                this.outlineColor = Color.valueOf("464a61");
                this.clipSize = 1200.0F;
                this.antiCheatType = new AntiCheatVariables(this.health / 15.0F, this.health / 1.5F, 10.0F, this.health / 20.0F, 0.2F, 360.0F, 180.0F, 5.0F, 1);
            }
        };
        deviation = new UnityUnitType("deviation") {
            {
                this.health = 8000.0F;
                this.speed = 2.7F;
                this.accel = 0.07F;
                this.drag = 0.04F;
                this.hitSize = 96.0F;
                this.engineOffset = 38.0F;
                this.engineSize = 4.75F;
                this.flying = true;
                this.lowAltitude = true;
                this.outlineColor = Color.valueOf("464a61");
                this.weapons.add(new Weapon(this.name + "-mount") {
                    {
                        this.x = 28.0F;
                        this.y = -17.5F;
                        this.shootY = 10.25F;
                        this.rotate = true;
                        this.rotateSpeed = 5.0F;
                        this.reload = 80.0F;
                        this.inaccuracy = 1.0F;
                        this.bullet = new LightningTurretBulletType(6.0F, 30.0F) {
                            {
                                this.range = 120.0F;
                                this.trailLength = 12;
                                this.trailColor = this.color = this.lightningColor = Pal.lancerLaser;
                                this.lightningDamage = 20.0F;
                                this.lightning = 5;
                                this.splashDamage = 20.0F;
                                this.splashDamageRadius = 35.0F;
                                this.status = StatusEffects.shocked;
                                this.reload = 30.0F;
                                this.duration = 300.0F;
                            }
                        };
                    }
                });
                this.decorations.add(new WingDecorationType(this.name + "-wing", 4) {
                    {
                        this.flapScl = 120.0F;
                        this.flapAnimation = new Interp.ExpOut(2.0F, 2.5F);
                        this.wings.add(new WingDecorationType.Wing(0, 19.0F, -35.25F, 0.75F, 19.0F), new WingDecorationType.Wing(1, 24.75F, -28.75F, 0.5F, 18.0F), new WingDecorationType.Wing(2, 24.25F, -8.0F, 0.25F, 17.0F), new WingDecorationType.Wing(3, 18.0F, 0.25F, 0.0F, 16.0F));
                    }
                });
            }
        };
        anomaly = new UnityUnitType("anomaly") {
            {
                this.health = 17000.0F;
                this.speed = 2.1F;
                this.rotateSpeed = 1.0F;
                this.accel = 0.08F;
                this.drag = 0.07F;
                this.hitSize = 137.5F;
                this.engineSize = -1.0F;
                this.flying = true;
                this.lowAltitude = true;
                this.outlineColor = Color.valueOf("464a61");
                this.decorations.add(new WingDecorationType(this.name + "-wing", 2) {
                    {
                        this.flapScl = 90.0F;
                        this.flapAnimation = new Interp.ExpOut(2.0F, 2.5F);
                        this.wings.addAll(new WingDecorationType.Wing[]{new WingDecorationType.Wing(0, 7.5F, -61.0F, 0.0F, 20.0F), new WingDecorationType.Wing(0, 10.5F, -48.25F, 0.1666F, 20.0F), new WingDecorationType.Wing(0, 13.5F, -35.5F, 0.3333F, 20.0F), new WingDecorationType.Wing(1, 13.5F, -22.75F, 0.5F, 20.0F), new WingDecorationType.Wing(1, 17.5F, -10.0F, 0.6666F, 20.0F), new WingDecorationType.Wing(1, 21.25F, 2.75F, 0.8333F, 20.0F)});
                    }
                });
                final BulletType t = new BulletType(0.0F, 5.0F) {
                    {
                        this.status = StatusEffects.slow;
                        this.statusDuration = 20.0F;
                        this.maxRange = 290.0F;
                    }
                };
                this.weapons.add(new TractorBeamWeapon(this.name + "-mount") {
                    {
                        this.x = 18.75F;
                        this.y = 22.5F;
                        this.shootY = 10.75F;
                        this.pullStrength = 40.0F;
                        this.scaledForce = 50.0F;
                        this.includeDead = true;
                        this.bullet = t;
                    }
                }, new TractorBeamWeapon(this.name + "-mount") {
                    {
                        this.x = 19.75F;
                        this.y = 63.0F;
                        this.shootY = 10.75F;
                        this.pullStrength = 40.0F;
                        this.scaledForce = 50.0F;
                        this.includeDead = true;
                        this.bullet = t;
                    }
                }, new EnergyChargeWeapon("") {
                    {
                        this.x = 0.0F;
                        this.y = 39.75F;
                        this.shootY = 0.0F;
                        this.mirror = false;
                        this.reload = 120.0F;
                        this.bullet = new AnomalyLaserBulletType(400.0F) {
                            {
                                this.lightningColor = Pal.lancerLaser;
                            }
                        };
                        float rad = 70.0F;
                        this.drawCharge = (unit, mount, charge) -> {
                            float rotation = unit.rotation - 90.0F;
                            float wx = unit.x + Angles.trnsx(rotation, this.x, this.y);
                            float wy = unit.y + Angles.trnsy(rotation, this.x, this.y);
                            float scl = Math.max(1.0F - mount.reload / this.reload, 0.0F) / 2.0F;
                            Draw.color(Pal.lancerLaser);
                            UnityDrawf.shiningCircle(unit.id, Time.time, wx, wy, 10.0F * scl, 5, 70.0F, 15.0F, 4.0F * scl, 90.0F);
                            Draw.color(Color.white);
                            UnityDrawf.shiningCircle(unit.id, Time.time, wx, wy, 5.0F * scl, 5, 70.0F, 15.0F, 3.0F * scl, 90.0F);
                            Lines.stroke(2.0F);
                            Draw.color(Pal.lancerLaser);
                            UnityDrawf.dashCircleAngle(wx, wy, rad, Mathf.sin(Time.time + Mathf.randomSeed((long)unit.id, 0.0F, 6.0F), 90.0F, 30.0F));
                            Draw.reset();
                        };
                        this.chargeCondition = (unit, mount) -> {
                            EnergyChargeWeapon.ChargeMount m = (EnergyChargeWeapon.ChargeMount)mount;
                            if (mount.reload > 0.0F) {
                                mount.reload = Math.max(mount.reload - Time.delta * unit.reloadMultiplier, 0.0F);
                            }

                            if ((m.timer += Time.delta) >= 5.0F) {
                                float rotation = unit.rotation - 90.0F;
                                float wx = unit.x + Angles.trnsx(rotation, this.x, this.y);
                                float wy = unit.y + Angles.trnsy(rotation, this.x, this.y);
                                Units.nearbyEnemies(unit.team, wx, wy, rad, (u) -> {
                                    u.damage(90.0F);
                                    if (u.dead) {
                                        m.charge += Mathf.sqrt(u.maxHealth) * (u.isFlying() ? Mathf.clamp(u.type.fallSpeed * 5.0F) : 1.0F);

                                        for(int i = 0; i < 4; ++i) {
                                            Time.run((float)i * 5.0F, () -> SpecialFx.chargeTransfer.at(u.x, u.y, 0.0F, Pal.lancerLaser, unit));
                                        }
                                    }

                                });
                                m.timer = 0.0F;
                            }

                            if (m.charge > 0.0F) {
                                float v = Math.min(m.charge, Time.delta * 2.0F);
                                m.charge -= v;
                                mount.reload -= v;
                            }

                            if (mount.reload < -250.0F) {
                                mount.reload = -250.0F;
                                m.charge = 0.0F;
                            }

                        };
                    }
                });
            }
        };
        enigma = new UnityUnitType("enigma") {
            {
                this.health = 2000.0F;
                this.speed = 4.0F;
                this.drag = 0.4F;
                this.accel = 0.5F;
                this.boostMultiplier = 0.5F;
                this.flying = true;
                this.lowAltitude = true;
                this.outlineColor = UnityPal.darkerOutline;
                this.antiCheatType = new AntiCheatVariables(900.0F, 1000.0F, this.health / 10.0F, 1000.0F, 0.2F, 360.0F, 180.0F, 15.0F, 1);
                this.weapons.add(new Weapon("") {
                    {
                        this.x = 4.25F;
                        this.y = -3.75F;
                        this.rotate = true;
                        this.reload = 4.0F;
                        this.bullet = new VoidPelletBulletType(5.5F, 200.0F) {
                            {
                                this.ratioDamage = 0.016666668F;
                                this.ratioStart = this.damage * 30.0F;
                            }
                        };
                    }
                });
            }
        };
        voidVessel = new UnityUnitType("void-vessel") {
            {
                this.health = 10000.0F;
                this.speed = 3.0F;
                this.accel = 0.1F;
                this.drag = 0.03F;
                this.hitSize = 16.0F;
                this.engineOffset = 12.5F;
                this.engineSize = 1.5F;
                this.flying = true;
                this.lowAltitude = true;
                this.outlineColor = UnityPal.darkerOutline;
                this.antiCheatType = new AntiCheatVariables(this.health / 20.0F, this.health / 1.25F, this.health / 15.0F, this.health / 25.0F, 0.2F, 360.0F, 180.0F, 15.0F, 4);
                this.weapons.add(new Weapon("unity-end-small-mount") {
                    {
                        this.x = 8.5F;
                        this.y = -4.5F;
                        this.mirror = true;
                        this.rotate = true;
                        this.reload = 30.0F;
                        this.inaccuracy = 15.0F;
                        this.shootSound = UnitySounds.spaceFracture;
                        this.bullet = new VoidFractureBulletType(32.0F, 600.0F) {
                            {
                                this.ratioDamage = 0.02F;
                                this.ratioStart = this.damage * 20.0F;
                                this.shootEffect = ShootFx.voidShoot;
                                this.modules = new AntiCheatBulletModule[]{new ArmorDamageModule(1.0F, 2.0F, 0.0F)};
                            }
                        };
                    }
                }, new Weapon("") {
                    {
                        this.x = this.y = 0.0F;
                        this.mirror = false;
                        this.continuous = true;
                        this.reload = 120.0F;
                        this.bullet = new OppressionLaserBulletType();
                        this.firstShotDelay = ChargeFx.oppressionCharge.lifetime;
                        this.parentizeEffects = true;
                    }
                });
            }
        };
        chronos = new UnityUnitType("chronos") {
            {
                this.health = 17000.0F;
                this.speed = 2.0F;
                this.accel = 0.1F;
                this.drag = 0.08F;
                this.hitSize = 36.0F;
                this.engineOffset = 19.0F;
                this.engineSize = 4.0F;
                this.flying = true;
                this.lowAltitude = true;
                this.outlineColor = UnityPal.darkerOutline;
                this.antiCheatType = new AntiCheatVariables(this.health / 20.0F, this.health / 1.25F, this.health / 15.0F, this.health / 25.0F, 0.2F, 360.0F, 180.0F, 15.0F, 4);
                this.abilities.add(new TimeStopAbility((unit) -> false, 900.0F, 600.0F));
                this.weapons.add(new Weapon("unity-end-point-defence") {
                    {
                        this.x = 12.0F;
                        this.y = -7.5F;
                        this.reload = 12.0F;
                        this.rotate = true;
                        this.rotateSpeed = 5.0F;
                        this.bullet = new TimeStopBulletType(6.0F, 510.0F);
                    }
                });
            }
        };
        opticaecus = new InvisibleUnitType("opticaecus") {
            {
                this.health = 60000.0F;
                this.speed = 1.8F;
                this.drag = 0.02F;
                this.hitSize = 60.5F;
                this.flying = true;
                this.lowAltitude = true;
                this.circleTarget = false;
                this.engineOffset = 38.0F;
                this.engineSize = 6.0F;
                this.outlineColor = UnityPal.darkerOutline;
                this.antiCheatType = new AntiCheatVariables(this.health / 15.0F, this.health / 1.5F, this.health / 12.5F, this.health / 20.0F, 0.2F, 360.0F, 180.0F, 30.0F, 4);
                this.weapons.add(new Weapon() {
                    {
                        this.mirror = false;
                        this.rotate = false;
                        this.x = 0.0F;
                        this.y = 11.25F;
                        this.shootY = 0.0F;
                        this.reload = 240.0F;
                        this.bullet = new LaserBulletType(1400.0F) {
                            {
                                this.colors = new Color[]{UnityPal.scarColor, UnityPal.endColor, Color.white};
                                this.hitColor = UnityPal.endColor;
                                this.width = 30.0F;
                                this.length = 390.0F;
                                this.largeHit = true;
                                this.hitEffect = HitFx.endHitRedBig;
                            }
                        };
                    }
                }, new Weapon("unity-doeg-launcher") {
                    {
                        this.x = 24.75F;
                        this.mirror = true;
                        this.rotate = true;
                        this.reload = 72.0F;
                        this.inaccuracy = 20.0F;
                        this.shotDelay = 2.0F;
                        this.shots = 10;
                        this.bullet = new MissileBulletType(6.0F, 170.0F) {
                            {
                                this.lifetime = 55.0F;
                                this.frontColor = UnityPal.endColor;
                                this.backColor = this.trailColor = this.lightColor = UnityPal.scarColor;
                                this.shrinkY = 0.1F;
                                this.splashDamage = 320.0F;
                                this.splashDamageRadius = 45.0F;
                                this.weaveScale = 15.0F;
                                this.weaveMag = 2.0F;
                                this.width *= 1.6F;
                                this.height *= 2.1F;
                                this.hitEffect = HitFx.endHitRedSmall;
                            }
                        };
                    }
                });
            }
        };
        devourer = new UnityUnitType("devourer-of-eldrich-gods") {
            {
                this.health = 1250000.0F;
                this.flying = true;
                this.speed = 5.0F;
                this.accel = 0.12F;
                this.drag = 0.1F;
                this.defaultController = WormAI::new;
                this.circleTarget = this.counterDrag = true;
                this.rotateShooting = false;
                this.splittable = this.chainable = false;
                this.hitSize = 60.449997F;
                this.segmentOffset = 70.55F;
                this.headOffset = 27.75F;
                this.segmentLength = 60;
                this.segmentCast = 7;
                this.barrageRange = 240.0F;
                this.lowAltitude = true;
                this.visualElevation = 2.0F;
                this.rotateSpeed = 2.2F;
                this.engineSize = -1.0F;
                this.range = 480.0F;
                this.armor = 16.0F;
                this.anglePhysicsSmooth = 0.5F;
                this.jointStrength = 1.0F;
                this.omniMovement = false;
                this.preventDrifting = true;
                this.outlineColor = UnityPal.darkerOutline;
                this.envEnabled = 3;
                this.immuneAll = true;
                this.antiCheatType = new AntiCheatVariables(this.health / 600.0F, this.health / 190.0F, this.health / 610.0F, this.health / 100.0F, 0.6F, 420.0F, 480.0F, 35.0F, 4);
                final BulletType t = new EndBasicBulletType(9.2F, 325.0F) {
                    {
                        this.hitSize = 8.0F;
                        this.shrinkY = 0.0F;
                        this.width = 19.0F;
                        this.height = 25.0F;
                        this.overDamage = 800000.0F;
                        this.ratioDamage = 0.0033333334F;
                        this.ratioStart = 15000.0F;
                        this.bleedDuration = 20.0F;
                        this.backColor = this.hitColor = this.lightColor = UnityPal.scarColor;
                        this.frontColor = UnityPal.endColor;
                        this.hitEffect = HitFx.endHitRedSmall;
                    }
                };
                this.weapons.add(new Weapon() {
                    {
                        this.mirror = false;
                        this.ignoreRotation = true;
                        this.x = 0.0F;
                        this.y = 23.0F;
                        this.reload = 900.0F;
                        this.continuous = true;
                        this.shake = 4.0F;
                        this.firstShotDelay = 41.0F;
                        this.chargeSound = UnitySounds.devourerMainLaser;
                        this.shootSound = UnitySounds.continuousLaserB;
                        this.bullet = UnityBullets.endLaser;
                    }
                }, new Weapon("unity-doeg-destroyer") {
                    {
                        this.mirror = true;
                        this.ignoreRotation = true;
                        this.rotate = true;
                        this.x = 19.25F;
                        this.y = -22.75F;
                        this.shootY = 12.0F;
                        this.shadow = 16.0F;
                        this.reload = 90.0F;
                        this.inaccuracy = 1.4F;
                        this.shots = 6;
                        this.shotDelay = 4.0F;
                        this.shootSound = UnitySounds.endBasic;
                        this.bullet = t;
                    }
                });
                this.segWeapSeq.add(new Weapon("unity-doeg-launcher") {
                    {
                        this.mirror = true;
                        this.rotate = true;
                        this.x = 19.0F;
                        this.y = 0.0F;
                        this.shootY = 8.0F;
                        this.shadow = 16.0F;
                        this.reload = 72.0F;
                        this.inaccuracy = 1.4F;
                        this.shots = 8;
                        this.shotDelay = 3.0F;
                        this.xRand = 12.0F;
                        this.shootSound = UnitySounds.endMissile;
                        this.bullet = new EndBasicBulletType(6.0F, 100.0F, "missile") {
                            {
                                this.width = 9.0F;
                                this.height = 11.0F;
                                this.shrinkY = 0.0F;
                                this.hitSound = Sounds.explosion;
                                this.trailChance = 0.2F;
                                this.lifetime = 52.0F;
                                this.homingPower = 0.08F;
                                this.splashDamage = 90.0F;
                                this.splashDamageRadius = 45.0F;
                                this.weaveMag = 18.0F;
                                this.weaveScale = 1.6F;
                                this.overDamage = 900000.0F;
                                this.ratioDamage = 0.005F;
                                this.ratioStart = 17000.0F;
                                this.backColor = this.trailColor = this.hitColor = this.lightColor = UnityPal.scarColor;
                                this.frontColor = UnityPal.endColor;
                                this.hitEffect = HitFx.endHitRedSmall;
                            }
                        };
                    }
                }, new Weapon("unity-doeg-destroyer") {
                    {
                        this.mirror = true;
                        this.ignoreRotation = true;
                        this.rotate = true;
                        this.x = 22.0F;
                        this.y = -15.75F;
                        this.shootY = 12.0F;
                        this.shadow = 16.0F;
                        this.reload = 90.0F;
                        this.inaccuracy = 1.4F;
                        this.shots = 6;
                        this.shotDelay = 4.0F;
                        this.shootSound = UnitySounds.endBasic;
                        this.bullet = t;
                    }
                }, new Weapon("unity-doeg-small-laser") {
                    {
                        this.mirror = true;
                        this.alternate = false;
                        this.rotate = true;
                        this.x = 17.5F;
                        this.y = 16.5F;
                        this.reload = 120.0F;
                        this.shadow = 14.0F;
                        this.shootSound = UnitySounds.continuousLaserA;
                        this.continuous = true;
                        this.bullet = UnityBullets.endLaserSmall;
                    }
                });
            }
        };
        oppression = new UnityUnitType("oppression") {
            {
                this.health = 2500000.0F;
                this.flying = true;
                this.speed = 4.5F;
                this.accel = 0.13F;
                this.drag = 0.12F;
                this.defaultController = WormAI::new;
                this.circleTarget = this.counterDrag = true;
                this.rotateShooting = false;
                this.wormDecal = new WormDecal(this.name + "-hydraulics") {
                    {
                        this.lineWidth = 11.5F;
                        this.lineColor = UnityPal.scarColor;
                        this.baseX = 41.25F;
                        this.baseY = 40.25F;
                        this.endX = 81.75F;
                        this.endY = -71.75F;
                        this.baseOffset = 19.5F;
                        this.segments = 2;
                    }
                };
                this.splittable = this.chainable = false;
                this.hitSize = 218.0F;
                this.angleLimit = 35.0F;
                this.segmentOffset = 228.0F;
                this.segmentLength = 55;
                this.segmentCast = 11;
                this.barrageRange = 490.0F;
                this.lowAltitude = true;
                this.visualElevation = 3.0F;
                this.rotateSpeed = 2.2F;
                this.engineSize = -1.0F;
                this.armor = 30.0F;
                this.anglePhysicsSmooth = 0.5F;
                this.jointStrength = 1.0F;
                this.omniMovement = false;
                this.preventDrifting = true;
                this.outlineColor = UnityPal.darkerOutline;
                this.envEnabled = 3;
                this.immuneAll = true;
                this.antiCheatType = new AntiCheatVariables(8000.0F, this.health / 190.0F, 10000.0F, this.health / 100.0F, 0.6F, 420.0F, 480.0F, 35.0F, 3);
                this.weapons.add(new Weapon("") {
                    {
                        this.x = 0.0F;
                        this.y = 0.0F;
                        this.shootY = 47.25F;
                        this.mirror = false;
                        this.continuous = true;
                        this.reload = 1500.0F;
                        this.firstShotDelay = ChargeFx.oppressionCharge.lifetime;
                        this.parentizeEffects = true;
                        this.bullet = new OppressionLaserBulletType();
                    }
                }, new Weapon(this.name + "-destroyer-1") {
                    {
                        this.x = 81.75F;
                        this.y = -71.5F;
                        this.shootY = 9.75F;
                        this.rotate = true;
                        this.rotateSpeed = 1.75F;
                        this.reload = 138.0F;
                        this.shots = 5;
                        this.shotDelay = 6.0F;
                        this.inaccuracy = 2.0F;
                        this.shootSound = UnitySounds.endBasic;
                        this.bullet = UnityBullets.oppressionShell;
                    }
                });
                this.segmentWeapons = new Seq[]{(new Seq()).addAll(new Weapon[]{new Weapon(this.name + "-soul-destroyer") {
                    {
                        this.mirror = false;
                        this.x = 0.0F;
                        this.y = 72.0F;
                        this.shootY = 0.0F;
                        this.layerOffset = 1.0E-4F;
                        this.rotate = true;
                        this.rotateSpeed = 1.5F;
                        this.reload = 285.0F;
                        this.bullet = new EndRailBulletType() {
                            {
                                this.damage = 15000.0F;
                                this.length = 850.0F;
                                this.updateEffectSeg = 50.0F;
                                this.pierceDamageFactor = 0.001F;
                                this.overDamage = 640000.0F;
                                this.overDamagePower = 2.7F;
                                this.overDamageScl = 4000.0F;
                                this.ratioStart = 14000.0F;
                                this.ratioDamage = 0.1F;
                                this.hitEffect = HitFx.endHitRail;
                                this.modules = new AntiCheatBulletModule[]{new ForceFieldDamageModule(150.0F, 10.0F, 500.0F, 0.5F, 0.05F, 600.0F), (new ArmorDamageModule(0.05F, 20.0F, 25.0F, 40.0F)).set(10.0F, 2.0F)};
                            }
                        };
                    }
                }, new Weapon(this.name + "-destroyer-2") {
                    {
                        this.x = 98.0F;
                        this.y = -26.25F;
                        this.shootY = 22.0F;
                        this.shootCone = 0.5F;
                        this.alternate = false;
                        this.rotate = true;
                        this.rotateSpeed = 1.75F;
                        this.continuous = true;
                        this.reload = 210.0F;
                        this.bullet = new EndContinuousLaserBulletType(550.0F) {
                            {
                                this.lifetime = 90.0F;
                                this.length = 370.0F;

                                for(int i = 0; i < this.strokes.length; ++i) {
                                    float[] var10000 = this.strokes;
                                    var10000[i] *= 0.7F;
                                }

                                this.overDamage = 800000.0F;
                                this.ratioDamage = 0.022222223F;
                                this.ratioStart = 800000.0F;
                                this.colors = new Color[]{UnityPal.scarColorAlpha, UnityPal.scarColor, UnityPal.endColor, Color.white};
                                this.modules = new AntiCheatBulletModule[]{new ForceFieldDamageModule(40.0F, 20.0F, 2000.0F, 1.0F, 0.025F, 180.0F), new ArmorDamageModule(0.1F, 30.0F, 30.0F, 0.4F)};
                            }
                        };
                    }
                }}), (new Seq()).addAll(new Weapon[]{new SweepWeapon(this.name + "-oppressor") {
                    {
                        this.mirror = false;
                        this.x = 0.0F;
                        this.y = 72.0F;
                        this.shootY = 21.0F;
                        this.layerOffset = 1.0E-4F;
                        this.rotateSpeed = 2.0F;
                        this.reload = 240.0F;
                        this.bullet = new EndSweepLaser(7000.0F) {
                            {
                                this.lifetime = 130.0F;
                                this.length = 850.0F;
                                this.overDamage = 640000.0F;
                                this.overDamagePower = 2.7F;
                                this.overDamageScl = 4000.0F;
                                this.width = 25.0F;
                                this.collisionWidth = this.width / 2.0F * this.widthLoss;
                                this.distance = 220.0F;
                                this.ratioStart = 14000.0F;
                                this.ratioDamage = 0.1F;
                                this.hitEffect = HitFx.endHitRedBig;
                                this.hitBullet = UnityBullets.oppressionArea;
                                this.pierce = true;
                                this.pierceCap = 3;
                            }
                        };
                    }
                }, new Weapon(this.name + "-destroyer-3") {
                    {
                        this.x = 98.0F;
                        this.y = -26.25F;
                        this.shootY = 6.0F;
                        this.shootSound = UnitySounds.endMissile;
                        this.rotate = true;
                        this.rotateSpeed = 4.0F;
                        this.inaccuracy = 3.0F;
                        this.xRand = 10.25F;
                        this.shots = 13;
                        this.shotDelay = 5.0F;
                        this.reload = 180.0F;
                        this.bullet = UnityBullets.missileAntiCheat.copy();
                        this.bullet.drag = -0.01F;
                        this.bullet.lifetime = 90.0F;
                        this.bullet.homingRange = 90.0F;
                    }
                }}), (new Seq()).addAll(new Weapon[]{new Weapon(this.name + "-void") {
                    {
                        this.mirror = false;
                        this.x = 0.0F;
                        this.y = 72.0F;
                        this.shootY = 21.0F;
                        this.layerOffset = 1.0E-4F;
                        this.rotate = true;
                        this.rotateSpeed = 1.3F;
                        this.reload = 360.0F;
                        this.bullet = new VoidPortalBulletType(1300.0F) {
                            {
                                this.hitEffect = HitFx.voidHit;
                                this.shootEffect = ShootFx.voidShoot;
                                this.bleedDuration = 180.0F;
                                this.modules = new AntiCheatBulletModule[]{new ForceFieldDamageModule(200.0F, 40.0F, 1500.0F, 1.0F, 0.02F, 240.0F), new ArmorDamageModule(0.1F, 70.0F, 30.0F, 0.9F)};
                            }
                        };
                    }
                }, new Weapon(this.name + "-destroyer-4") {
                    {
                        this.x = 98.0F;
                        this.y = -26.25F;
                        this.shootY = 17.5F;
                        this.shootSound = UnitySounds.oppressionLightning;
                        this.rotate = true;
                        this.rotateSpeed = 3.0F;
                        this.inaccuracy = 15.0F;
                        this.xRand = 6.0F;
                        this.shots = 5;
                        this.reload = 240.0F;
                        this.bullet = new SlowLightningBulletType(120.0F);
                    }
                }}), new Seq()};
            }
        };
        apocalypse = new InvisibleUnitType("apocalypse") {
            {
                this.health = 1725000.0F;
                this.speed = 0.75F;
                this.accel = 0.06F;
                this.drag = 0.06F;
                this.armor = 17.0F;
                this.hitSize = 205.0F;
                this.rotateSpeed = 0.3F;
                this.visualElevation = 3.0F;
                this.engineOffset = 116.5F;
                this.engineSize = 14.0F;
                this.rotateShooting = false;
                this.flying = true;
                this.lowAltitude = true;
                this.outlineColor = UnityPal.darkerOutline;
                this.immuneAll = true;
                this.antiCheatType = new AntiCheatVariables(this.health / 600.0F, this.health / 200.0F, this.health / 600.0F, this.health / 100.0F, 0.6F, 420.0F, 480.0F, 35.0F, 4);
                CloneableSetWeapon a = UnityWeaponTemplates.apocalypseSmall;
                CloneableSetWeapon b = UnityWeaponTemplates.apocalypseLaser;
                CloneableSetWeapon c = UnityWeaponTemplates.apocalypseLauncher;
                this.weapons.addAll(new Weapon[]{a.set((w) -> {
                    w.y = 47.25F;
                    w.x = 74.75F;
                }), a.set((w) -> {
                    w.y = 24.75F;
                    w.x = 80.0F;
                    w.reload += 2.0F;
                }), a.set((w) -> {
                    w.y = -76.5F;
                    w.x = 108.5F;
                    w.reload += 4.0F;
                }), a.set((w) -> {
                    w.y = -31.25F;
                    w.x = 70.25F;
                    w.reload += 6.0F;
                }), a.set((w) -> {
                    w.y = -74.5F;
                    w.x = 56.0F;
                    w.reload += 8.0F;
                }), a.set((w) -> {
                    w.y = 0.0F;
                    w.x = 51.0F;
                }), a.set((w) -> {
                    w.y = -80.25F;
                    w.x = 65.5F;
                    w.reload += 10.0F;
                }), a.set((w) -> {
                    w.y = -63.5F;
                    w.x = 36.25F;
                    w.reload += 12.0F;
                }), b.set((w) -> {
                    w.y = 37.25F;
                    w.x = 44.25F;
                }), b.set((w) -> {
                    w.y = -22.75F;
                    w.x = 51.0F;
                    w.reload += 2.0F;
                }), b.set((w) -> {
                    w.y = -51.75F;
                    w.x = 62.25F;
                    w.reload += 4.0F;
                }), c.set((w) -> {
                    w.x = 87.0F;
                    w.y = 0.0F;
                }), c.set((w) -> {
                    w.y = -23.25F;
                    w.x = 97.0F;
                    w.reload += 2.0F;
                }), c.set((w) -> {
                    w.y = -50.5F;
                    w.x = 97.5F;
                    w.reload += 4.0F;
                }), c.set((w) -> {
                    w.y = -74.75F;
                    w.x = 87.0F;
                    w.reload += 6.0F;
                }), new Weapon("unity-quetzalcoatl") {
                    {
                        this.x = this.y = 0.0F;
                        this.shootY = -8.25F;
                        this.mirror = false;
                        this.rotate = true;
                        this.continuous = true;
                        this.rotateSpeed = 0.2F;
                        this.shadow = 63.0F;
                        this.shootCone = 1.0F;
                        this.reload = 360.0F;
                        this.shootSound = UnitySounds.continuousLaserB;
                        this.bullet = new EndCutterLaserBulletType(3100.0F) {
                            {
                                this.maxLength = 1200.0F;
                                this.lifetime = 180.0F;
                                this.width = 17.0F;
                                this.antiCheatScl = 5.0F;
                                this.laserSpeed = 70.0F;
                                this.buildingDamageMultiplier = 0.4F;
                                this.lightningColor = UnityPal.scarColor;
                                this.lightningDamage = 55.0F;
                                this.lightningLength = 13;
                                this.bleedDuration = 300.0F;
                                this.overDamage = 500000.0F;
                                this.ratioDamage = 0.01F;
                                this.ratioStart = 7000.0F;
                                this.hitEffect = HitFx.endHitRedBig;
                            }
                        };
                    }
                }});
                this.tentacles.add(new TentacleType(this.name + "-tentacle") {
                    {
                        this.x = 101.75F;
                        this.y = -72.5F;
                        this.rotationOffset = 30.0F;
                        this.rotationSpeed = 3.0F;
                        this.accel = 0.2F;
                        this.speed = 8.0F;
                        this.segments = 15;
                        this.segmentLength = 37.25F;
                        this.bullet = UnityBullets.endLaserSmall;
                        this.automatic = false;
                        this.continuous = true;
                        this.reload = 240.0F;
                    }
                }, new TentacleType(this.name + "-tentacle") {
                    {
                        this.x = 56.5F;
                        this.y = -71.75F;
                        this.rotationOffset = 10.0F;
                        this.rotationSpeed = 3.0F;
                        this.accel = 0.2F;
                        this.speed = 8.0F;
                        this.segments = 10;
                        this.segmentLength = 37.25F;
                        this.swayOffset = 90.0F;
                        this.bullet = UnityBullets.endLaserSmall;
                        this.automatic = false;
                        this.continuous = true;
                        this.reload = 240.0F;
                    }
                }, new TentacleType(this.name + "-small-tentacle") {
                    {
                        this.x = 104.25F;
                        this.y = -49.0F;
                        this.rotationOffset = 35.0F;
                        this.rotationSpeed = 3.0F;
                        this.accel = 0.15F;
                        this.speed = 10.0F;
                        this.segments = 20;
                        this.segmentLength = 28.0F;
                        this.swayOffset = 120.0F;
                        this.swayMag = 0.2F;
                        this.swayScl = 120.0F;
                        this.bullet = null;
                        this.automatic = true;
                        this.tentacleDamage = 430.0F;
                    }
                }, new TentacleType(this.name + "-small-tentacle") {
                    {
                        this.x = 69.75F;
                        this.y = -74.25F;
                        this.rotationOffset = 20.0F;
                        this.rotationSpeed = 3.0F;
                        this.accel = 0.15F;
                        this.speed = 10.0F;
                        this.segments = 23;
                        this.segmentLength = 28.0F;
                        this.swayOffset = 70.0F;
                        this.swayMag = 0.2F;
                        this.swayScl = 120.0F;
                        this.bullet = null;
                        this.automatic = true;
                        this.tentacleDamage = 430.0F;
                    }
                });
            }

            public void drawEngine(Unit unit) {
                if (unit.isFlying()) {
                    super.drawEngine(unit);
                    float scale = unit.elevation;

                    for(int i : Mathf.signs) {
                        float offset = 0.5F + 0.5F * scale;
                        float engineSizeB = 3.25F;
                        Tmp.v1.trns(unit.rotation, -105.0F * offset, 73.5F * (float)i).add(unit);
                        Draw.color(unit.team.color);
                        if (unit instanceof Invisiblec) {
                            Invisiblec e = (Invisiblec)unit;
                            Draw.alpha(this.fade(e));
                        }

                        Fill.circle(Tmp.v1.x, Tmp.v1.y, (engineSizeB + Mathf.absin(Time.time + 90.0F, 2.0F, engineSizeB / 2.0F)) * scale);
                        Tmp.v1.trns(unit.rotation, -105.0F * offset + 1.0F, 74.0F * (float)i).add(unit);
                        Draw.color(Color.white);
                        if (unit instanceof Invisiblec) {
                            Invisiblec e = (Invisiblec)unit;
                            Draw.alpha(this.fade(e));
                        }

                        Fill.circle(Tmp.v1.x, Tmp.v1.y, (engineSizeB + Mathf.absin(Time.time + 90.0F, 2.0F, engineSizeB / 2.0F)) / 2.0F * scale);
                        Draw.color();
                    }

                }
            }
        };
        ravager = new UnityUnitType("ravager") {
            {
                this.health = 1650000.0F;
                this.speed = 0.65F;
                this.drag = 0.16F;
                this.armor = 15.0F;
                this.hitSize = 138.0F;
                this.rotateSpeed = 1.1F;
                this.immuneAll = true;
                this.allowLegStep = true;
                this.hovering = true;
                this.groundLayer = 81.0F;
                this.visualElevation = 3.0F;
                this.legCount = 8;
                this.legGroupSize = 4;
                this.legPairOffset = 2.0F;
                this.legMoveSpace = 0.5F;
                this.legLength = 140.0F;
                this.legExtension = -15.0F;
                this.legBaseOffset = 50.0F;
                this.legSpeed = 0.15F;
                this.legTrns = 0.2F;
                this.rippleScale = 7.0F;
                this.legSplashRange = 90.0F;
                this.legSplashDamage = 1400.0F;
                this.outlineColor = UnityPal.darkerOutline;
                this.antiCheatType = new AntiCheatVariables(this.health / 610.0F, this.health / 190.0F, this.health / 560.0F, this.health / 120.0F, 0.6F, 420.0F, 480.0F, 35.0F, 4);
                this.weapons.addAll(new Weapon[]{new Weapon(this.name + "-nightmare") {
                    {
                        bottomWeapons.add(this);
                        this.x = 80.25F;
                        this.y = -7.75F;
                        this.shootY = 75.0F;
                        this.reload = 360.0F;
                        this.recoil = 8.0F;
                        this.alternate = true;
                        this.rotate = false;
                        this.shootSound = UnitySounds.ravagerNightmareShoot;
                        this.bullet = UnityBullets.ravagerLaser;
                    }
                }, new Weapon(this.name + "-artillery") {
                    {
                        this.shootY = 11.0F;
                        this.shots = 5;
                        this.inaccuracy = 10.0F;
                        this.shadow = 26.5F;
                        this.y = -31.75F;
                        this.x = 44.25F;
                        this.rotate = true;
                        this.rotateSpeed = 2.0F;
                        this.velocityRnd = 0.2F;
                        this.reload = 100.0F;
                        this.shootSound = UnitySounds.endBasicLarge;
                        this.bullet = UnityBullets.ravagerArtillery;
                    }
                }, new Weapon(this.name + "-artillery") {
                    {
                        this.shootY = 11.0F;
                        this.shots = 5;
                        this.inaccuracy = 10.0F;
                        this.shadow = 26.5F;
                        this.y = -4.25F;
                        this.x = 51.25F;
                        this.rotate = true;
                        this.rotateSpeed = 2.0F;
                        this.velocityRnd = 0.2F;
                        this.reload = 112.5F;
                        this.shootSound = UnitySounds.endBasicLarge;
                        this.bullet = UnityBullets.ravagerArtillery;
                    }
                }, new Weapon(this.name + "-small-turret") {
                    {
                        this.shootY = 7.0F;
                        this.inaccuracy = 2.0F;
                        this.shadow = 18.5F;
                        this.y = 53.75F;
                        this.x = 34.5F;
                        this.rotate = true;
                        this.xRand = 2.0F;
                        this.reload = 7.0F;
                        this.shootSound = UnitySounds.endMissile;
                        this.bullet = UnityBullets.missileAntiCheat;
                    }
                }, new Weapon(this.name + "-small-turret") {
                    {
                        this.shootY = 7.0F;
                        this.inaccuracy = 2.0F;
                        this.shadow = 18.5F;
                        this.y = 24.25F;
                        this.x = 50.75F;
                        this.rotate = true;
                        this.xRand = 2.0F;
                        this.reload = 7.0F;
                        this.shootSound = UnitySounds.endMissile;
                        this.bullet = UnityBullets.missileAntiCheat;
                    }
                }});
            }
        };
        desolation = new UnityUnitType("desolation") {
            {
                this.defaultController = SmartGroundAI::new;
                this.health = 307300.0F;
                this.speed = 0.7F;
                this.drag = 0.16F;
                this.armor = 35.0F;
                this.hitSize = 257.0F;
                this.rotateSpeed = 0.9F;
                this.visualElevation = 8.0F;
                this.groundLayer = 91.0F;
                this.allowLegStep = this.legShadows = this.hovering = true;
                this.immuneAll = true;
                this.legTrns = 0.3F;
                this.legLength = 672.0F * (1.0F - this.legTrns * 0.85F * 0.5F);
                this.legExtension = -48.0F;
                this.legCount = 8;
                this.legGroupSize = 2;
                this.legPairOffset = 1.0F;
                this.legMoveSpace = 0.2F;
                this.legBaseOffset = 61.25F;
                this.rippleScale = 12.0F;
                this.legSplashRange = 120.0F;
                this.legSplashDamage = 1700.0F;
                this.aimDst = this.hitSize / 2.0F;
                this.bulletWidth = 190.0F;
                this.outlineColor = UnityPal.darkerOutline;
                this.antiCheatType = new AntiCheatVariables(6000.0F, 12000.0F, this.health / 560.0F, this.health / 120.0F, 0.6F, 420.0F, 480.0F, 35.0F, 4);
                final BulletType bRange = new BulletType(0.0F, 220.0F) {
                    {
                        this.maxRange = 280.0F;
                    }
                };
                Weapon w = new Weapon("unity-end-mount") {
                    {
                        this.reload = 35.0F;
                        this.shootY = 9.0F;
                        this.inaccuracy = 5.0F;
                        this.shots = 3;
                        this.shotDelay = 5.0F;
                        this.rotate = true;
                        this.rotateSpeed = 15.0F;
                        this.mirror = false;
                        this.alternate = true;
                        this.bullet = new EndBasicBulletType(5.0F, 260.0F) {
                            {
                                this.lifetime = 70.0F;
                                this.width = 19.0F;
                                this.height = 27.0F;
                                this.backColor = this.lightColor = UnityPal.scarColor;
                                this.frontColor = Color.black;
                                this.trailEffect = TrailFx.endTrail;
                                this.trailChance = 0.4F;
                                this.hitSound = UnitySounds.spaceFracture;
                                this.overDamage = 2200000.0F;
                                this.ratioDamage = 0.005882353F;
                                this.ratioStart = 4000.0F;
                                this.fragBullets = 3;
                                this.fragVelocityMax = 1.2F;
                                this.fragVelocityMin = 0.5F;
                                this.fragCone = 120.0F;
                                this.hitEffect = HitFx.endHitRedSmall;
                                this.fragBullet = new VoidFractureBulletType(15.0F, 100.0F) {
                                    {
                                        this.width = 9.5F;
                                        this.widthTo = 2.0F;
                                        this.maxTargets = 5;
                                        this.spikesRange = 90.0F;
                                        this.spikesDamage = 50.0F;
                                        this.overDamage = 1800000.0F;
                                        this.ratioDamage = 0.02F;
                                        this.ratioStart = 50000.0F;
                                        this.shootEffect = ShootFx.voidShoot;
                                        this.modules = new AntiCheatBulletModule[]{new ArmorDamageModule(1.0F, 20.0F, 2.0F)};
                                    }
                                };
                            }
                        };
                    }
                };
                Weapon w2 = new Weapon("unity-end-mount-2") {
                    {
                        this.shootY = 12.0F;
                        this.reload = 100.0F;
                        this.rotate = true;
                        this.rotateSpeed = 5.0F;
                        this.alternate = true;
                        this.mirror = false;
                        this.shots = 2;
                        this.shotDelay = 5.0F;
                        this.bullet = new EndBasicBulletType(7.0F, 380.0F, "shell") {
                            {
                                this.lifetime = 95.0F;
                                this.pierceShields = this.pierce = this.pierceBuilding = true;
                                this.pierceCap = 3;
                                this.hitShake = 1.0F;
                                this.shootEffect = Fx.shootBig;
                                this.shrinkY = 0.0F;
                                this.backColor = this.lightningColor = this.lightColor = UnityPal.scarColor;
                                this.frontColor = UnityPal.endColor;
                                this.lightning = 3;
                                this.lightningLength = 8;
                                this.lightningLengthRand = 4;
                                this.lightningDamage = 80.0F;
                                this.lightningType = UnityBullets.endLightning;
                                this.splashDamage = 220.0F;
                                this.splashDamageRadius = 80.0F;
                                this.width = 15.0F;
                                this.height = 21.0F;
                                this.hitEffect = HitFx.endHitRedSmall;
                            }
                        };
                    }
                };
                this.weapons.addAll(new Weapon[]{new EnergyChargeWeapon(this.name + "-main") {
                    {
                        this.drawRegion = false;
                        this.mirror = false;
                        this.x = 0.0F;
                        this.y = 80.0F;
                        this.shootY = 36.5F;
                        this.reload = 900.0F;
                        this.bullet = new DesolationBulletType(1.75F, 2500.0F) {
                            {
                                this.lifetime = 480.0F;
                                this.overDamage = 900000.0F;
                                this.overDamagePower = 3.0F;
                                this.overDamageScl = 3500.0F;
                                this.ratioDamage = 0.02F;
                                this.ratioStart = 200000.0F;
                                this.bleedDuration = 600.0F;
                                this.modules = new AntiCheatBulletModule[]{new ArmorDamageModule(0.033333335F, 15.0F, 30.0F, 5.0F), new AbilityDamageModule(50.0F, 600.0F, 10.0F, 0.016666668F, 15.0F), new ForceFieldDamageModule(5.0F, 15.0F, 200.0F, 7.0F, 0.025F, 300.0F)};
                            }
                        };
                        this.drawCharge = (unit, mount, charge) -> {
                            float r = unit.rotation - 90.0F;
                            float wx = Angles.trnsx(r, this.x, this.y) + unit.x;
                            float wy = Angles.trnsy(r, this.x, this.y) + unit.y;
                            Draw.color(UnityPal.scarColor);
                            Draw.blend(Blending.additive);

                            for(int i = 0; i < 4; ++i) {
                                float in = Mathf.curve(charge, (float)i / 4.0F, ((float)i + 1.0F) / 4.0F);
                                if (in > 1.0E-4F) {
                                    Draw.alpha(in);
                                    Draw.rect(this.heatRegion, wx + Mathf.range(12.0F - in * 11.3F), wy + Mathf.range(12.0F - in * 11.3F), r);
                                }
                            }

                            Draw.blend();
                            Draw.color();
                        };
                    }
                }, new MultiTargetPointDefenceWeapon("unity-end-point-defence") {
                    {
                        this.x = 96.75F;
                        this.y = 9.0F;
                        this.alternate = false;
                        this.reload = 15.0F;
                        this.shots = 7;
                        this.shootCone = 20.0F;
                        rotationSpeed = 15.0F;
                        this.beamEffect = LineFx.endPointDefence;
                        this.color = UnityPal.scarColor;
                        this.bullet = bRange;
                    }
                }, new MultiTargetPointDefenceWeapon("unity-end-point-defence") {
                    {
                        this.x = 82.0F;
                        this.y = 20.5F;
                        this.alternate = false;
                        this.reload = 10.0F;
                        this.shots = 5;
                        this.shootCone = 20.0F;
                        rotationSpeed = 15.0F;
                        this.beamEffect = LineFx.endPointDefence;
                        this.color = UnityPal.scarColor;
                        this.bullet = bRange;
                    }
                }, UnityWeaponTemplates.clnW(w, (c) -> {
                    c.x = 62.25F;
                    c.y = 6.75F;
                    c.otherSide = 6;
                }), UnityWeaponTemplates.clnW(w, (c) -> {
                    c.x = 57.0F;
                    c.y = -16.25F;
                    c.otherSide = 7;
                    c.flipSprite = true;
                }), UnityWeaponTemplates.clnW(w, (c) -> {
                    c.x = 52.0F;
                    c.y = -39.0F;
                    c.otherSide = 8;
                    c.flipSprite = true;
                }), UnityWeaponTemplates.clnW(w, (c) -> {
                    c.x = 46.75F;
                    c.y = -61.75F;
                    c.otherSide = 5;
                    c.flipSprite = true;
                }), UnityWeaponTemplates.clnW(w, (c) -> {
                    c.x = -62.25F;
                    c.y = 6.75F;
                    c.otherSide = 10;
                }), UnityWeaponTemplates.clnW(w, (c) -> {
                    c.x = -57.0F;
                    c.y = -16.25F;
                    c.otherSide = 11;
                    c.flipSprite = true;
                }), UnityWeaponTemplates.clnW(w, (c) -> {
                    c.x = -52.0F;
                    c.y = -39.0F;
                    c.otherSide = 12;
                    c.flipSprite = true;
                }), UnityWeaponTemplates.clnW(w, (c) -> {
                    c.x = -46.75F;
                    c.y = -61.75F;
                    c.otherSide = 9;
                    c.flipSprite = true;
                }), UnityWeaponTemplates.clnW(w2, (c) -> {
                    c.x = 100.75F;
                    c.y = -13.0F;
                    c.otherSide = 14;
                }), UnityWeaponTemplates.clnW(w2, (c) -> {
                    c.x = 79.0F;
                    c.y = -23.5F;
                    c.otherSide = 13;
                    c.flipSprite = true;
                }), UnityWeaponTemplates.clnW(w2, (c) -> {
                    c.x = -100.75F;
                    c.y = -13.0F;
                    c.otherSide = 16;
                }), UnityWeaponTemplates.clnW(w2, (c) -> {
                    c.x = -79.0F;
                    c.y = -23.5F;
                    c.otherSide = 15;
                    c.flipSprite = true;
                }), UnityWeaponTemplates.clnW(w2, (c) -> {
                    c.x = 85.0F;
                    c.y = -48.25F;
                    c.otherSide = 18;
                }), UnityWeaponTemplates.clnW(w2, (c) -> {
                    c.x = 68.75F;
                    c.y = -65.75F;
                    c.otherSide = 17;
                    c.flipSprite = true;
                }), UnityWeaponTemplates.clnW(w2, (c) -> {
                    c.x = -85.0F;
                    c.y = -48.25F;
                    c.otherSide = 20;
                }), UnityWeaponTemplates.clnW(w2, (c) -> {
                    c.x = -68.75F;
                    c.y = -65.75F;
                    c.otherSide = 19;
                    c.flipSprite = true;
                })});
                this.tentacles.addAll(new TentacleType[]{new TentacleType(this.name + "-tentacle") {
                    {
                        this.x = 139.0F;
                        this.y = -13.5F;
                        this.rotationSpeed = 2.5F;
                        this.accel = 0.2F;
                        this.speed = 6.0F;
                        this.top = true;
                        this.firstSegmentAngleLimit = 17.0F;
                        this.angleLimit = 30.0F;
                        this.automatic = false;
                        this.segmentLength = 44.5F;
                        this.segments = 15;
                        this.swayMag = 0.2F;
                        this.swayScl = 120.0F;
                        this.rotationOffset = 40.0F;
                        this.reload = 180.0F;
                        this.shootCone = 4.0F;
                        this.shootSound = UnitySounds.ravagerNightmareShoot;
                        this.bullet = new EndPointBlastLaserBulletType(250.0F) {
                            {
                                this.length = 320.0F;
                                this.width = 17.0F;
                                this.lifetime = 20.0F;
                                this.widthReduction = 3.0F;
                                this.auraWidthReduction = 4.0F;
                                this.damageRadius = 60.0F;
                                this.auraDamage = 1000.0F;
                                this.overDamage = 900000.0F;
                                this.ratioDamage = 0.005F;
                                this.ratioStart = 11000.0F;
                                this.bleedDuration = 600.0F;
                                this.hitEffect = HitFx.endHitRedSmall;
                                this.laserColors = new Color[]{UnityPal.scarColorAlpha, UnityPal.scarColor, UnityPal.endColor, Color.black};
                            }
                        };
                        this.range = this.bullet.range();
                    }
                }, new TentacleType("unity-apocalypse-tentacle") {
                    {
                        this.x = 122.75F;
                        this.y = -41.0F;
                        this.rotationOffset = 35.0F;
                        this.firstSegmentAngleLimit = 20.0F;
                        this.top = true;
                        this.rotationSpeed = 3.0F;
                        this.accel = 0.2F;
                        this.speed = 8.0F;
                        this.segments = 17;
                        this.segmentLength = 37.25F;
                        this.bullet = UnityBullets.endLaserSmall;
                        this.bulletDuration = 90.0F;
                        this.automatic = false;
                        this.continuous = true;
                        this.reload = 240.0F;
                    }
                }, new TentacleType("unity-apocalypse-tentacle") {
                    {
                        this.x = 111.5F;
                        this.y = -57.5F;
                        this.rotationOffset = 30.0F;
                        this.firstSegmentAngleLimit = 18.0F;
                        this.top = true;
                        this.rotationSpeed = 3.0F;
                        this.accel = 0.2F;
                        this.speed = 8.0F;
                        this.segments = 14;
                        this.segmentLength = 37.25F;
                        this.swayOffset = 45.0F;
                        this.bullet = UnityBullets.endLaserSmall;
                        this.bulletDuration = 90.0F;
                        this.automatic = false;
                        this.continuous = true;
                        this.reload = 240.0F;
                    }
                }, new TentacleType("unity-apocalypse-tentacle") {
                    {
                        this.x = 95.25F;
                        this.y = -63.0F;
                        this.rotationOffset = 25.0F;
                        this.firstSegmentAngleLimit = 16.0F;
                        this.top = true;
                        this.rotationSpeed = 3.0F;
                        this.accel = 0.2F;
                        this.speed = 8.0F;
                        this.segments = 9;
                        this.segmentLength = 37.25F;
                        this.swayOffset = 90.0F;
                        this.bullet = UnityBullets.endLaserSmall;
                        this.bulletDuration = 90.0F;
                        this.automatic = false;
                        this.continuous = true;
                        this.reload = 240.0F;
                    }
                }});
            }
        };
        thalassophobia = new UnityUnitType("thalassophobia") {
            {
                this.health = 2750000.0F;
                this.hitSize = 242.5F;
                this.speed = 1.9F;
                this.accel = 0.2F;
                this.drag = 0.16F;
                this.rotateSpeed = 0.3F;
                this.outlineColor = UnityPal.darkerOutline;
                this.immuneAll = true;
                this.antiCheatType = new AntiCheatVariables(8000.0F, 16000.0F, this.health / 520.0F, this.health / 120.0F, 0.6F, 420.0F, 480.0F, 35.0F, 4);
                this.decorations.add(new FlagellaDecorationType(this.name + "-tail", 4, 15, 45.75F) {
                    {
                        this.x = 0.0F;
                        this.y = -172.0F;
                        this.swayScl = hitSize / speed;
                        this.swayOffset = 67.0F;
                    }
                });
                Weapon w = new EnergyChargeWeapon("unity-void-fracture-turret") {
                    {
                        this.mirror = false;
                        this.alternate = true;
                        this.shadow = 47.0F;
                        this.shots = 3;
                        this.shotDelay = 6.0F;
                        this.reload = 120.0F;
                        this.inaccuracy = 20.0F;
                        this.shootCone = 7.0F;
                        this.shootY = 0.0F;
                        this.rotate = true;
                        this.rotateSpeed = 2.0F;
                        this.velocityRnd = 0.1F;
                        this.shootSound = UnitySounds.spaceFracture;
                        this.drawCharge = (unit, mount, charge) -> {
                            Weapon w = mount.weapon;
                            float rotation = unit.rotation - 90.0F;
                            float wx = unit.x + Angles.trnsx(rotation, w.x, w.y);
                            float wy = unit.y + Angles.trnsy(rotation, w.x, w.y);
                            Draw.color(Color.black);
                            UnityDrawf.shiningCircle(unit.id * 321 + Math.max(0, w.otherSide * 41), Time.time, wx, wy, 3.5F * charge, 6, 60.0F, 17.0F, 3.0F * charge, 70.0F);
                            Draw.color();
                        };
                        this.bullet = new VoidFractureBulletType(40.0F, 800.0F) {
                            {
                                this.speed = 5.0F;
                                this.delay = 50.0F;
                                this.lifetime = 60.0F;
                                this.drag = 0.09F;
                                this.nextLifetime = 13.0F;
                                this.ratioDamage = 0.005882353F;
                                this.ratioStart = 30000.0F;
                                this.bleedDuration = 40.0F;
                                this.length = 52.0F;
                                this.width = 20.0F;
                                this.widthTo = 8.0F;
                                this.spikesRand = 16.0F;
                                this.spikesDamage = 310.0F;
                                this.targetingRange = 400.0F;
                                this.maxTargets = 20;
                                this.shootEffect = ShootFx.voidShoot;
                                this.hitEffect = HitFx.voidHitBig;
                                this.smokeEffect = HitFx.voidHit;
                                this.modules = new AntiCheatBulletModule[]{new ArmorDamageModule(50.0F, 50.0F, 2.0F), new ForceFieldDamageModule(2.0F, 20.0F, 220.0F, 7.0F, 0.02F, 120.0F)};
                            }
                        };
                    }
                };
                Weapon m = new Weapon("unity-end-missile-launcher") {
                    {
                        this.shootY = 7.25F;
                        this.reload = 50.0F;
                        this.alternate = true;
                        this.mirror = false;
                        this.shotDelay = 3.0F;
                        this.shots = 5;
                        this.xRand = 5.75F;
                        this.rotate = true;
                        this.rotateSpeed = 4.0F;
                        this.inaccuracy = 7.0F;
                        this.shootSound = Sounds.missile;
                        this.bullet = new EndBasicBulletType(4.0F, 210.0F, "missile") {
                            {
                                this.lifetime = 75.0F;
                                this.width = this.height = 12.0F;
                                this.shrinkY = 0.0F;
                                this.drag = -0.01F;
                                this.splashDamageRadius = 45.0F;
                                this.splashDamage = 220.0F;
                                this.homingPower = 0.08F;
                                this.homingRange = 100.0F;
                                this.trailChance = 0.3F;
                                this.weaveScale = 6.0F;
                                this.weaveMag = 1.0F;
                                this.overDamage = 950000.0F;
                                this.ratioDamage = 0.0025F;
                                this.ratioStart = 2000.0F;
                                this.hitEffect = HitFx.endHitRedSmall;
                                this.despawnEffect = HitFx.endHitRedSmall;
                                this.backColor = this.lightColor = this.trailColor = UnityPal.scarColor;
                                this.frontColor = UnityPal.endColor;
                            }
                        };
                    }
                };
                this.weapons.addAll(new Weapon[]{UnityWeaponTemplates.clnW(w, (y) -> {
                    y.x = 79.5F;
                    y.y = -34.0F;
                    y.otherSide = 1;
                }), UnityWeaponTemplates.clnW(w, (y) -> {
                    y.x = 90.5F;
                    y.y = -71.5F;
                    y.otherSide = 2;
                    y.flipSprite = true;
                }), UnityWeaponTemplates.clnW(w, (y) -> {
                    y.x = 91.25F;
                    y.y = -104.0F;
                    y.otherSide = 0;
                    y.flipSprite = true;
                }), UnityWeaponTemplates.clnW(w, (y) -> {
                    y.x = -79.5F;
                    y.y = -34.0F;
                    y.otherSide = 4;
                }), UnityWeaponTemplates.clnW(w, (y) -> {
                    y.x = -90.5F;
                    y.y = -71.5F;
                    y.otherSide = 5;
                    y.flipSprite = true;
                }), UnityWeaponTemplates.clnW(w, (y) -> {
                    y.x = -91.25F;
                    y.y = -104.0F;
                    y.otherSide = 3;
                    y.flipSprite = true;
                }), UnityWeaponTemplates.clnW(m, (y) -> {
                    y.x = 73.5F;
                    y.y = 69.5F;
                    y.otherSide = 7;
                }), UnityWeaponTemplates.clnW(m, (y) -> {
                    y.x = 84.0F;
                    y.y = 40.0F;
                    y.otherSide = 8;
                    y.flipSprite = true;
                }), UnityWeaponTemplates.clnW(m, (y) -> {
                    y.x = 72.5F;
                    y.y = 6.25F;
                    y.otherSide = 6;
                    y.flipSprite = true;
                }), UnityWeaponTemplates.clnW(m, (y) -> {
                    y.x = -73.5F;
                    y.y = 69.5F;
                    y.otherSide = 10;
                }), UnityWeaponTemplates.clnW(m, (y) -> {
                    y.x = -84.0F;
                    y.y = 40.0F;
                    y.otherSide = 11;
                    y.flipSprite = true;
                }), UnityWeaponTemplates.clnW(m, (y) -> {
                    y.x = -72.5F;
                    y.y = 6.25F;
                    y.otherSide = 9;
                    y.flipSprite = true;
                }), new EnergyChargeWeapon("") {
                    TextureRegion r;
                    TextureRegion[] rs;
                    final Color[] colors;

                    {
                        this.colors = new Color[]{UnityPal.scarColor, UnityPal.endColor, Color.white, Color.black};
                        this.mirror = false;
                        this.x = 0.0F;
                        this.y = 41.25F;
                        this.reload = 900.0F;
                        this.continuous = true;
                        this.shootSound = UnitySounds.thalassophobiaLaser;
                        this.bullet = new ContinuousSingularityLaserBulletType(3500.0F) {
                            {
                                this.lifetime = 300.0F;
                                this.width = 40.0F;
                                this.widthReduction = 6.0F;
                                this.collisionWidth = 17.0F;
                                this.accel = 15.0F;
                                this.laserSpeed = 45.0F;
                                this.pierceAmount = 20.0F;
                                this.gravityStrength = 1600.0F;
                                this.baseTriangleSize = 210.0F;
                                this.oscScl = 5.0F;
                                this.buildingDamageMultiplier = 0.7F;
                                this.overDamage = 600000.0F;
                                this.overDamagePower = 3.0F;
                                this.overDamageScl = 7000.0F;
                                this.ratioDamage = 0.005F;
                                this.ratioStart = 20000.0F;
                                this.bleedDuration = 600.0F;
                                this.hitEffect = HitFx.endHitRedBig;
                                this.modules = new AntiCheatBulletModule[]{new ArmorDamageModule(0.01F, 3.0F, 40.0F, 5.0F), new AbilityDamageModule(50.0F, 300.0F, 20.0F, 0.002F, 3.0F)};
                            }
                        };
                        this.drawCharge = (unit, mount, charge) -> {
                            float len = 32.25F;
                            float rr = unit.rotation;
                            float wx = Angles.trnsx(rr, this.y) + unit.x;
                            float wy = Angles.trnsy(rr, this.y) + unit.y;
                            float tx = Angles.trnsx(rr, len);
                            float ty = Angles.trnsy(rr, len);

                            for(int i = 0; i < this.colors.length; ++i) {
                                float w = 1.0F - 6.0F * (float)i / 50.0F;
                                float rx = Mathf.range(charge);
                                float ry = Mathf.range(charge);
                                float s = Mathf.absin(40.0F, 5.0F);
                                Draw.color(this.colors[i]);
                                Drawf.tri(wx + rx, wy + ry, (35.0F + s) * w * charge * 2.0F * 1.22F, charge * charge * w * (160.0F + Mathf.absin(Time.time + (float)i * 8.0F, 15.0F, 12.0F)), unit.rotation);
                                UnityDrawf.shiningCircle(unit.id, Time.time, wx + rx, wy + ry, (35.0F + s) * w * charge, 5, 90.0F, 0.5F, 25.0F, 15.0F * charge, 45.0F);
                            }

                            Draw.color(UnityPal.scarColor);
                            Draw.blend(Blending.additive);
                            Draw.alpha(charge);
                            Draw.rect(this.r, wx + tx + Mathf.range(charge * 3.0F), wy + ty + Mathf.range(charge * 3.0F), rr - 90.0F);

                            for(int i = 0; i < this.rs.length; ++i) {
                                float f = (float)i / (float)this.rs.length;
                                float t = ((float)i + 1.0F) / (float)this.rs.length;
                                float a = Mathf.curve(charge, f, t);
                                float inv = Interp.pow2In.apply(1.0F - a) * 3.0F;
                                Draw.alpha(Mathf.clamp(a * 2.0F));
                                Draw.rect(this.rs[i], wx + tx * (1.0F + inv) + Mathf.range(a * 2.0F), wy + ty * (1.0F + inv) + Mathf.range(a * 2.0F), rr - 90.0F);
                            }

                            Draw.blend();
                            Draw.reset();
                        };
                    }

                    public void load() {
                        super.load();
                        this.r = Core.atlas.find("unity-thalassophobia-cannon");
                        this.rs = new TextureRegion[4];

                        for(int i = 0; i < 4; ++i) {
                            this.rs[i] = Core.atlas.find("unity-thalassophobia-cannon-" + i);
                        }

                    }
                }});
            }

            public void load() {
                super.load();
                this.softShadowRegion = Core.atlas.find(this.name + "-soft-shadow");
            }

            public void drawSoftShadow(Unit unit) {
                Draw.color(0.0F, 0.0F, 0.0F, 1.0F);
                float rad = 1.6F;
                float size = (float)Math.max(this.region.width, this.region.height) * Draw.scl;
                Draw.rect(this.softShadowRegion, unit, size * rad * Draw.xscl, size * rad * Draw.yscl, unit.rotation - 90.0F);
                Draw.color();
            }
        };
        charShadowcape = new UnityUnitType("shadowcape") {
            {
                this.canBoost = true;
                this.decorations.add(new CapeDecorationType(this.name + "-cape-1") {
                    {
                        this.x = 8.25F;
                        this.y = 0.5F;
                        this.swayAmount = -30.0F;
                        this.swaySpeed = 0.5F;
                        this.alphaTo = 0.6F;
                        this.alphaScl = 3.0F;
                        this.shakeAmount = 1.5F;
                        this.shakeScl = 2.0F;
                    }
                }, new CapeDecorationType(this.name + "-cape-2") {
                    {
                        this.x = 3.0F;
                        this.y = -7.0F;
                        this.swayAmount = -15.0F;
                        this.shakeScl = 3.0F;
                    }
                });
            }
        };
    }
}
