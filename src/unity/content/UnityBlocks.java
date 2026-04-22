package unity.content;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.struct.ObjectMap;
import arc.util.Strings;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.content.Bullets;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.content.StatusEffects;
import mindustry.content.UnitTypes;
import mindustry.entities.Effect;
import mindustry.entities.Fires;
import mindustry.entities.Lightning;
import mindustry.entities.Units;
import mindustry.entities.bullet.ArtilleryBulletType;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.bullet.ContinuousLaserBulletType;
import mindustry.entities.bullet.FlakBulletType;
import mindustry.entities.bullet.LaserBoltBulletType;
import mindustry.entities.bullet.LaserBulletType;
import mindustry.entities.bullet.LightningBulletType;
import mindustry.gen.Bullet;
import mindustry.gen.Hitboxc;
import mindustry.gen.Sounds;
import mindustry.graphics.MultiPacker;
import mindustry.graphics.Pal;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.type.Liquid;
import mindustry.type.LiquidStack;
import mindustry.type.UnitType;
import mindustry.world.Block;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.defense.turrets.LaserTurret;
import mindustry.world.blocks.defense.turrets.PowerTurret;
import mindustry.world.blocks.defense.turrets.Turret;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.blocks.environment.OverlayFloor;
import mindustry.world.blocks.environment.StaticWall;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.consumers.ConsumeLiquidFilter;
import mindustry.world.draw.DrawGlow;
import mindustry.world.draw.DrawLiquid;
import mindustry.world.draw.DrawSmelter;
import mindustry.world.meta.BuildVisibility;
import mindustry.world.meta.Stat;
import unity.content.effects.ChargeFx;
import unity.content.effects.HitFx;
import unity.content.effects.ShootFx;
import unity.content.units.MonolithUnitTypes;
import unity.entities.bullet.anticheat.EndCutterLaserBulletType;
import unity.entities.bullet.energy.ArcBulletType;
import unity.entities.bullet.energy.DecayBasicBulletType;
import unity.entities.bullet.energy.EphemeronBulletType;
import unity.entities.bullet.energy.EphemeronPairBulletType;
import unity.entities.bullet.energy.VelocityLaserBoltBulletType;
import unity.entities.bullet.exp.GeyserLaserBulletType;
import unity.entities.bullet.laser.AcceleratingLaserBulletType;
import unity.entities.bullet.laser.ChangeTeamLaserBulletType;
import unity.entities.bullet.laser.GravitonLaserBulletType;
import unity.entities.bullet.laser.PointBlastLaserBulletType;
import unity.entities.bullet.laser.RoundLaserBulletType;
import unity.entities.bullet.laser.WavefrontLaser;
import unity.gen.ExpKoruhConveyor;
import unity.gen.LightHoldGenericCrafter;
import unity.gen.Regions;
import unity.gen.SoulAbsorberTurret;
import unity.gen.SoulFloorExtractor;
import unity.gen.SoulGenericCrafter;
import unity.gen.SoulHeatRayTurret;
import unity.gen.SoulLifeStealerTurret;
import unity.gen.SoulTurretBurstPowerTurret;
import unity.gen.SoulTurretItemTurret;
import unity.gen.SoulTurretPowerTurret;
import unity.gen.StemGenericCrafter;
import unity.gen.UnityModels;
import unity.gen.UnityObjs;
import unity.gen.UnitySounds;
import unity.graphics.TexturedTrail;
import unity.graphics.UnityDrawf;
import unity.graphics.UnityPal;
import unity.mod.Faction;
import unity.world.LightAcceptorType;
import unity.world.blocks.GraphBlock;
import unity.world.blocks.LoreMessageBlock;
import unity.world.blocks.defense.HeatWall;
import unity.world.blocks.defense.LevelLimitWall;
import unity.world.blocks.defense.LightWall;
import unity.world.blocks.defense.LimitWall;
import unity.world.blocks.defense.PowerWall;
import unity.world.blocks.defense.ShieldWall;
import unity.world.blocks.defense.turrets.AbsorberTurret;
import unity.world.blocks.defense.turrets.BarrelsItemTurret;
import unity.world.blocks.defense.turrets.BigLaserTurret;
import unity.world.blocks.defense.turrets.BlockOverdriveTurret;
import unity.world.blocks.defense.turrets.EndGameTurret;
import unity.world.blocks.defense.turrets.EndLaserTurret;
import unity.world.blocks.defense.turrets.ObjPowerTurret;
import unity.world.blocks.defense.turrets.OrbTurret;
import unity.world.blocks.defense.turrets.PrismTurret;
import unity.world.blocks.defense.turrets.RampupPowerTurret;
import unity.world.blocks.defense.turrets.ShieldTurret;
import unity.world.blocks.defense.turrets.SupernovaTurret;
import unity.world.blocks.defense.turrets.WavefrontTurret;
import unity.world.blocks.distribution.CruciblePump;
import unity.world.blocks.distribution.DriveShaft;
import unity.world.blocks.distribution.HeatPipe;
import unity.world.blocks.distribution.InlineGearbox;
import unity.world.blocks.distribution.KoruhConveyor;
import unity.world.blocks.distribution.ShadowedConveyor;
import unity.world.blocks.distribution.SimpleTransmission;
import unity.world.blocks.distribution.Teleporter;
import unity.world.blocks.distribution.UnderPiper;
import unity.world.blocks.effect.Reinforcer;
import unity.world.blocks.effect.UnityThruster;
import unity.world.blocks.environment.UnityOreBlock;
import unity.world.blocks.exp.ClassicProjector;
import unity.world.blocks.exp.DiagonalTower;
import unity.world.blocks.exp.EField;
import unity.world.blocks.exp.ExpHub;
import unity.world.blocks.exp.ExpNode;
import unity.world.blocks.exp.ExpRouter;
import unity.world.blocks.exp.ExpTank;
import unity.world.blocks.exp.ExpTower;
import unity.world.blocks.exp.ExpTurret;
import unity.world.blocks.exp.KoruhCrafter;
import unity.world.blocks.exp.KoruhReactor;
import unity.world.blocks.exp.MeltingCrafter;
import unity.world.blocks.exp.turrets.BurstChargePowerTurret;
import unity.world.blocks.exp.turrets.ExpItemTurret;
import unity.world.blocks.exp.turrets.ExpLiquidTurret;
import unity.world.blocks.exp.turrets.ExpPowerTurret;
import unity.world.blocks.exp.turrets.OmniLiquidTurret;
import unity.world.blocks.light.LightReflector;
import unity.world.blocks.light.LightSource;
import unity.world.blocks.power.CombustionHeater;
import unity.world.blocks.power.ElectricMotor;
import unity.world.blocks.power.HandCrank;
import unity.world.blocks.power.Magnet;
import unity.world.blocks.power.PowerPlant;
import unity.world.blocks.power.RotorBlock;
import unity.world.blocks.power.SolarCollector;
import unity.world.blocks.power.SolarReflector;
import unity.world.blocks.power.ThermalHeater;
import unity.world.blocks.power.TorqueGenerator;
import unity.world.blocks.power.WaterTurbine;
import unity.world.blocks.power.WindTurbine;
import unity.world.blocks.production.AugerDrill;
import unity.world.blocks.production.BurnerSmelter;
import unity.world.blocks.production.CastingMold;
import unity.world.blocks.production.Crucible;
import unity.world.blocks.production.DistributionDrill;
import unity.world.blocks.production.HoldingCrucible;
import unity.world.blocks.production.LiquidsSmelter;
import unity.world.blocks.production.MechanicalExtractor;
import unity.world.blocks.production.Press;
import unity.world.blocks.production.SoulInfuser;
import unity.world.blocks.production.SporeFarm;
import unity.world.blocks.production.SporePyrolyser;
import unity.world.blocks.sandbox.ExpSource;
import unity.world.blocks.sandbox.ExpVoid;
import unity.world.blocks.sandbox.HeatSource;
import unity.world.blocks.units.ConversionPad;
import unity.world.blocks.units.MechPad;
import unity.world.blocks.units.ModularConstructor;
import unity.world.blocks.units.ModularConstructorPart;
import unity.world.blocks.units.SelectableReconstructor;
import unity.world.blocks.units.TeleUnit;
import unity.world.blocks.units.TerraCore;
import unity.world.blocks.units.TimeMine;
import unity.world.consumers.ConsumeLiquids;
import unity.world.draw.DrawExp;
import unity.world.draw.DrawLightBlock;
import unity.world.draw.DrawOver;
import unity.world.graphs.GraphCrucible;
import unity.world.graphs.GraphFlux;
import unity.world.graphs.GraphHeat;
import unity.world.graphs.GraphTorque;
import unity.world.graphs.GraphTorqueConsume;
import unity.world.graphs.GraphTorqueGenerate;
import unity.world.graphs.GraphTorqueTrans;
import unity.world.meta.StemData;
import younggamExperimental.PartStat;
import younggamExperimental.PartStatType;
import younggamExperimental.PartType;
import younggamExperimental.blocks.Chopper;
import younggamExperimental.blocks.ModularTurret;

public class UnityBlocks {
    public static Block distributionDrill;
    public static Block recursiveReconstructor;
    public static Block irradiator;
    public static Block superCharger;
    public static Block oreUmbrium;
    public static Block apparition;
    public static Block ghost;
    public static Block banshee;
    public static Block fallout;
    public static Block catastrophe;
    public static Block calamity;
    public static Block extinction;
    public static Block darkWall;
    public static Block darkWallLarge;
    public static Block darkAlloyForge;
    public static Block oreLuminum;
    public static Block photon;
    public static Block electron;
    public static Block graviton;
    public static Block proton;
    public static Block neutron;
    public static Block gluon;
    public static Block wBoson;
    public static Block zBoson;
    public static Block higgsBoson;
    public static Block singularity;
    public static Block muon;
    public static Block ephemeron;
    public static Block lightLamp;
    public static Block oilLamp;
    public static Block lightLampInfi;
    public static Block lightReflector;
    public static Block lightDivisor;
    public static Block metaglassWall;
    public static Block metaglassWallLarge;
    public static Block lightForge;
    public static Block terraCore;
    public static Block oreImberium;
    public static Block electroTile;
    public static Block orb;
    public static Block shockwire;
    public static Block current;
    public static Block plasma;
    public static Block electrobomb;
    public static Block shielder;
    public static Block orbTurret;
    public static Block powerPlant;
    public static Block absorber;
    public static Block piper;
    public static Block sparkAlloyForge;
    public static Block denseSmelter;
    public static Block solidifier;
    public static Block steelSmelter;
    public static Block liquifier;
    public static Block titaniumExtractor;
    public static Block lavaSmelter;
    public static Block diriumCrucible;
    public static Block coalExtractor;
    public static Block stoneWall;
    public static Block denseWall;
    public static Block steelWall;
    public static Block steelWallLarge;
    public static Block diriumWall;
    public static Block diriumWallLarge;
    public static Block shieldProjector;
    public static Block diriumProjector;
    public static Block timeMine;
    public static Block shieldWall;
    public static Block shieldWallLarge;
    public static Block steelConveyor;
    public static Block teleporter;
    public static Block teleunit;
    public static Block diriumConveyor;
    public static Block bufferPad;
    public static Block omegaPad;
    public static Block cachePad;
    public static Block convertPad;
    public static Block uraniumReactor;
    public static Block expFountain;
    public static Block expVoid;
    public static Block expTank;
    public static Block expChest;
    public static Block expRouter;
    public static Block expTower;
    public static Block expTowerDiagonal;
    public static Block bufferTower;
    public static Block expHub;
    public static Block expNode;
    public static Block expNodeLarge;
    public static Block laser;
    public static Block laserCharge;
    public static Block laserBranch;
    public static Block laserFractal;
    public static Block laserBreakthrough;
    public static Block laserFrost;
    public static Block laserKelvin;
    public static Block inferno;
    public static Block buffTurret;
    public static Block upgradeTurret;
    public static Block oreMonolite;
    public static Block sharpslate;
    public static Block sharpslateWall;
    public static Block infusedSharpslate;
    public static Block infusedSharpslateWall;
    public static Block archSharpslate;
    public static Block archEnergy;
    public static Block loreMonolith;
    public static Block debrisExtractor;
    public static Block soulInfuser;
    public static Block monolithAlloyForge;
    public static Block electrophobicWall;
    public static Block electrophobicWallLarge;
    public static Block lifeStealer;
    public static Block absorberAura;
    public static Block heatRay;
    public static Block incandescence;
    public static Block ricochet;
    public static Block shellshock;
    public static Block purge;
    public static Block blackout;
    public static Block diviner;
    public static Block mage;
    public static Block recluse;
    public static Block oracle;
    public static Block prism;
    public static Block supernova;
    public static Block oreNickel;
    public static Block concreteBlank;
    public static Block concreteFill;
    public static Block concreteNumber;
    public static Block concreteStripe;
    public static Block concrete;
    public static Block stoneFullTiles;
    public static Block stoneFull;
    public static Block stoneHalf;
    public static Block stoneTiles;
    public static Block smallTurret;
    public static Block medTurret;
    public static Block chopper;
    public static Block augerDrill;
    public static Block mechanicalExtractor;
    public static Block sporeFarm;
    public static Block mechanicalConveyor;
    public static Block heatPipe;
    public static Block driveShaft;
    public static Block inlineGearbox;
    public static Block shaftRouter;
    public static Block simpleTransmission;
    public static Block crucible;
    public static Block holdingCrucible;
    public static Block cruciblePump;
    public static Block castingMold;
    public static Block sporePyrolyser;
    public static Block smallRadiator;
    public static Block thermalHeater;
    public static Block combustionHeater;
    public static Block solarCollector;
    public static Block solarReflector;
    public static Block nickelStator;
    public static Block nickelStatorLarge;
    public static Block nickelElectromagnet;
    public static Block electricRotorSmall;
    public static Block electricRotor;
    public static Block handCrank;
    public static Block windTurbine;
    public static Block waterTurbine;
    public static Block electricMotor;
    public static Block cupronickelWall;
    public static Block cupronickelWallLarge;
    public static Block smallThruster;
    public static Block infiHeater;
    public static Block infiCooler;
    public static Block infiTorque;
    public static Block neodymiumStator;
    public static Block advanceConstructorModule;
    public static Block advanceConstructor;
    public static Block celsius;
    public static Block kelvin;
    public static Block caster;
    public static Block storm;
    public static Block eclipse;
    public static Block xenoCorruptor;
    public static Block cube;
    public static Block wavefront;
    public static Block terminalCrucible;
    public static Block endForge;
    public static Block endGame;
    public static Block tenmeikiri;

    public static void load() {
        distributionDrill = new DistributionDrill("distribution-drill") {
            {
                this.requirements(Category.production, ItemStack.with(new Object[]{Items.copper, 20, Items.silicon, 15, Items.titanium, 20}));
                this.tier = 3;
                this.drillTime = 450.0F;
                this.size = 2;
                this.consumes.liquid(Liquids.water, 0.06F).boost();
            }
        };
        recursiveReconstructor = new SelectableReconstructor("recursive-reconstructor") {
            {
                this.requirements(Category.units, ItemStack.with(new Object[]{Items.graphite, 1600, Items.silicon, 2000, Items.metaglass, 900, Items.thorium, 600, Items.lead, 1200, Items.plastanium, 3600}));
                this.size = 11;
                this.liquidCapacity = 360.0F;
                this.configurable = true;
                this.constructTime = 20000.0F;
                this.minTier = 6;
                this.upgrades.addAll(new UnitType[][]{{UnitTypes.reign, UnityUnitTypes.citadel}, {UnitTypes.toxopid, UnityUnitTypes.araneidae}, {UnitTypes.corvus, UnityUnitTypes.cygnus}, {UnityUnitTypes.rex, UnityUnitTypes.excelsus}, {MonolithUnitTypes.monument, MonolithUnitTypes.colossus}});
                this.otherUpgrades.add(new UnitType[]{UnityUnitTypes.citadel, UnityUnitTypes.empire}, new UnitType[]{UnityUnitTypes.araneidae, UnityUnitTypes.theraphosidae}, new UnitType[]{MonolithUnitTypes.colossus, MonolithUnitTypes.bastion});
                this.consumes.power(5.0F);
                this.consumes.items(ItemStack.with(new Object[]{Items.silicon, 1200, Items.metaglass, 800, Items.thorium, 700, Items.surgeAlloy, 400, Items.plastanium, 600, Items.phaseFabric, 350}));
                this.consumes.liquid(Liquids.cryofluid, 7.0F);
            }
        };
        irradiator = new Press("irradiator") {
            {
                this.requirements(Category.crafting, ItemStack.with(new Object[]{Items.lead, 120, Items.silicon, 80, Items.titanium, 30}));
                this.outputItem = new ItemStack(UnityItems.irradiantSurge, 3);
                this.size = 3;
                this.movementSize = 29.0F;
                this.fxYVariation = 3.125F;
                this.craftTime = 50.0F;
                this.consumes.power(1.2F);
                this.consumes.items(ItemStack.with(new Object[]{Items.thorium, 5, Items.titanium, 5, Items.surgeAlloy, 1}));
            }
        };
        superCharger = new Reinforcer("supercharger") {
            {
                this.requirements(Category.effect, ItemStack.with(new Object[]{Items.titanium, 60, Items.lead, 20, Items.silicon, 30}));
                this.size = 2;
                this.itemCapacity = 15;
                this.laserColor = Items.surgeAlloy.color;
                this.consumes.power(0.4F);
                this.consumes.items(ItemStack.with(new Object[]{UnityItems.irradiantSurge, 10}));
            }
        };
        oreNickel = new UnityOreBlock(UnityItems.nickel) {
            {
                this.oreScale = 24.77F;
                this.oreThreshold = 0.913F;
                this.oreDefault = false;
            }
        };
        oreUmbrium = new UnityOreBlock(UnityItems.umbrium) {
            {
                this.oreScale = 23.77F;
                this.oreThreshold = 0.813F;
                this.oreDefault = false;
            }
        };
        oreLuminum = new UnityOreBlock(UnityItems.luminum) {
            {
                this.oreScale = 23.77F;
                this.oreThreshold = 0.81F;
                this.oreDefault = false;
            }
        };
        oreImberium = new UnityOreBlock(UnityItems.imberium) {
            {
                this.oreScale = 23.77F;
                this.oreThreshold = 0.807F;
                this.oreDefault = false;
            }
        };
        apparition = new ItemTurret("apparition") {
            {
                this.requirements(Category.turret, ItemStack.with(new Object[]{Items.copper, 350, Items.graphite, 380, Items.silicon, 360, Items.plastanium, 200, Items.thorium, 220, UnityItems.umbrium, 370, Items.surgeAlloy, 290}));
                this.size = 5;
                this.health = 3975;
                this.range = 235.0F;
                this.reloadTime = 6.0F;
                this.coolantMultiplier = 0.5F;
                this.restitution = 0.09F;
                this.inaccuracy = 3.0F;
                this.spread = 12.0F;
                this.shots = 2;
                this.shootSound = Sounds.shootBig;
                this.alternate = true;
                this.recoilAmount = 3.0F;
                this.rotateSpeed = 4.5F;
                this.ammo(new Object[]{Items.graphite, UnityBullets.standardDenseLarge, Items.silicon, UnityBullets.standardHomingLarge, Items.pyratite, UnityBullets.standardIncendiaryLarge, Items.thorium, UnityBullets.standardThoriumLarge});
            }

            public void load() {
                super.load();
                this.baseRegion = Core.atlas.find("unity-block-" + this.size);
            }
        };
        ghost = new BarrelsItemTurret("ghost") {
            {
                this.size = 8;
                this.health = 9750;
                this.range = 290.0F;
                this.reloadTime = 9.0F;
                this.coolantMultiplier = 0.5F;
                this.restitution = 0.08F;
                this.inaccuracy = 3.0F;
                this.shots = 2;
                this.shootSound = Sounds.shootBig;
                this.alternate = true;
                this.recoilAmount = 5.5F;
                this.rotateSpeed = 3.5F;
                this.spread = 21.0F;
                this.addBarrel(8.0F, 18.75F, 6.0F);
                this.ammo(new Object[]{Items.graphite, UnityBullets.standardDenseHeavy, Items.silicon, UnityBullets.standardHomingHeavy, Items.pyratite, UnityBullets.standardIncendiaryHeavy, Items.thorium, UnityBullets.standardThoriumHeavy});
                this.requirements(Category.turret, ItemStack.with(new Object[]{Items.copper, 1150, Items.graphite, 1420, Items.silicon, 960, Items.plastanium, 800, Items.thorium, 1230, UnityItems.darkAlloy, 380}));
            }
        };
        banshee = new BarrelsItemTurret("banshee") {
            {
                this.size = 12;
                this.health = 22000;
                this.range = 370.0F;
                this.reloadTime = 12.0F;
                this.coolantMultiplier = 0.5F;
                this.restitution = 0.08F;
                this.inaccuracy = 3.0F;
                this.shots = 2;
                this.shootSound = Sounds.shootBig;
                this.alternate = true;
                this.recoilAmount = 5.5F;
                this.rotateSpeed = 3.5F;
                this.spread = 37.0F;
                this.focus = true;
                this.addBarrel(23.5F, 36.5F, 9.0F);
                this.addBarrel(8.5F, 24.5F, 6.0F);
                this.ammo(new Object[]{Items.graphite, UnityBullets.standardDenseMassive, Items.silicon, UnityBullets.standardHomingMassive, Items.pyratite, UnityBullets.standardIncendiaryMassive, Items.thorium, UnityBullets.standardThoriumMassive});
                this.requirements(Category.turret, ItemStack.with(new Object[]{Items.copper, 2800, Items.graphite, 2980, Items.silicon, 2300, Items.titanium, 1900, Items.phaseFabric, 1760, Items.thorium, 1780, UnityItems.darkAlloy, 1280}));
            }
        };
        fallout = new LaserTurret("fallout") {
            {
                this.size = 5;
                this.health = 3975;
                this.range = 215.0F;
                this.reloadTime = 110.0F;
                this.coolantMultiplier = 0.8F;
                this.shootCone = 40.0F;
                this.shootDuration = 230.0F;
                this.powerUse = 19.0F;
                this.shootShake = 3.0F;
                this.firingMoveFract = 0.2F;
                this.shootEffect = Fx.shootBigSmoke2;
                this.recoilAmount = 4.0F;
                this.shootSound = Sounds.laserbig;
                this.heatColor = Color.valueOf("e04300");
                this.rotateSpeed = 3.5F;
                this.loopSound = Sounds.beam;
                this.loopSoundVolume = 2.1F;
                this.requirements(Category.turret, ItemStack.with(new Object[]{Items.copper, 450, Items.lead, 350, Items.graphite, 390, Items.silicon, 360, Items.titanium, 250, UnityItems.umbrium, 370, Items.surgeAlloy, 360}));
                this.shootType = UnityBullets.falloutLaser;
                ((ConsumeLiquidFilter)this.consumes.add(new ConsumeLiquidFilter((liquid) -> liquid.temperature <= 0.5F && liquid.flammability < 0.1F, 0.58F))).update(false);
            }

            public void load() {
                super.load();
                this.baseRegion = Core.atlas.find("unity-block-" + this.size);
            }
        };
        catastrophe = new BigLaserTurret("catastrophe") {
            {
                this.size = 8;
                this.health = 9750;
                this.range = 300.0F;
                this.reloadTime = 190.0F;
                this.coolantMultiplier = 0.6F;
                this.shootCone = 40.0F;
                this.shootDuration = 320.0F;
                this.powerUse = 39.0F;
                this.shootShake = 4.0F;
                this.firingMoveFract = 0.16F;
                this.shootEffect = Fx.shootBigSmoke2;
                this.recoilAmount = 7.0F;
                this.cooldown = 0.012F;
                this.heatColor = Color.white;
                this.rotateSpeed = 1.9F;
                this.shootSound = Sounds.laserbig;
                this.loopSound = Sounds.beam;
                this.loopSoundVolume = 2.2F;
                this.requirements(Category.turret, ItemStack.with(new Object[]{Items.copper, 1250, Items.lead, 1320, Items.graphite, 1100, Items.titanium, 1340, Items.surgeAlloy, 1240, Items.silicon, 1350, Items.thorium, 770, UnityItems.darkAlloy, 370}));
                this.shootType = UnityBullets.catastropheLaser;
                ((ConsumeLiquidFilter)this.consumes.add(new ConsumeLiquidFilter((liquid) -> liquid.temperature <= 0.4F && liquid.flammability < 0.1F, 1.3F))).update(false);
            }
        };
        calamity = new BigLaserTurret("calamity") {
            {
                this.size = 12;
                this.health = 22000;
                this.range = 420.0F;
                this.reloadTime = 320.0F;
                this.coolantMultiplier = 0.6F;
                this.shootCone = 23.0F;
                this.shootDuration = 360.0F;
                this.powerUse = 87.0F;
                this.shootShake = 4.0F;
                this.firingMoveFract = 0.09F;
                this.shootEffect = Fx.shootBigSmoke2;
                this.recoilAmount = 7.0F;
                this.cooldown = 0.009F;
                this.heatColor = Color.white;
                this.rotateSpeed = 0.97F;
                this.shootSound = Sounds.laserbig;
                this.loopSound = Sounds.beam;
                this.loopSoundVolume = 2.6F;
                this.requirements(Category.turret, ItemStack.with(new Object[]{Items.copper, 2800, Items.lead, 2970, Items.graphite, 2475, Items.titanium, 3100, Items.surgeAlloy, 2790, Items.silicon, 3025, Items.thorium, 1750, UnityItems.darkAlloy, 1250}));
                this.shootType = UnityBullets.calamityLaser;
                ((ConsumeLiquidFilter)this.consumes.add(new ConsumeLiquidFilter((liquid) -> liquid.temperature <= 0.3F && liquid.flammability < 0.1F, 2.1F))).update(false);
            }
        };
        extinction = new BigLaserTurret("extinction") {
            {
                this.requirements(Category.turret, ItemStack.with(new Object[]{Items.copper, 3800, Items.lead, 4100, Items.graphite, 3200, Items.titanium, 4200, Items.surgeAlloy, 3800, Items.silicon, 4300, Items.thorium, 2400, UnityItems.darkAlloy, 1700, UnityItems.terminum, 900, UnityItems.terminaAlloy, 500}));
                this.size = 14;
                this.health = 29500;
                this.range = 520.0F;
                this.reloadTime = 380.0F;
                this.coolantMultiplier = 0.4F;
                this.shootCone = 12.0F;
                this.shootDuration = 360.0F;
                this.powerUse = 175.0F;
                this.shootShake = 4.0F;
                this.firingMoveFract = 0.09F;
                this.shootEffect = Fx.shootBigSmoke2;
                this.recoilAmount = 7.0F;
                this.cooldown = 0.003F;
                this.heatColor = Color.white;
                this.rotateSpeed = 0.82F;
                this.shootSound = UnitySounds.extinctionShoot;
                this.loopSound = UnitySounds.beamIntenseHighpitchTone;
                this.loopSoundVolume = 2.0F;
                this.shootType = UnityBullets.extinctionLaser;
                ((ConsumeLiquidFilter)this.consumes.add(new ConsumeLiquidFilter((liquid) -> liquid.temperature <= 0.27F && liquid.flammability < 0.1F, 2.5F))).update(false);
            }
        };
        darkAlloyForge = new StemGenericCrafter("dark-alloy-forge") {
            {
                this.requirements(Category.crafting, ItemStack.with(new Object[]{Items.copper, 30, Items.lead, 25}));
                this.outputItem = new ItemStack(UnityItems.darkAlloy, 3);
                this.craftTime = 140.0F;
                this.size = 4;
                this.ambientSound = Sounds.respawning;
                this.ambientSoundVolume = 0.6F;
                this.drawer = new DrawSmelter();
                this.consumes.items(ItemStack.with(new Object[]{Items.lead, 2, Items.silicon, 3, Items.blastCompound, 1, Items.phaseFabric, 1, UnityItems.umbrium, 2}));
                this.consumes.power(3.2F);
                this.update((e) -> {
                    if (e.consValid() && Mathf.chanceDelta((double)0.76F)) {
                        UnityFx.craftingEffect.at(e.x, e.y, Mathf.random(360.0F));
                    }

                });
            }
        };
        darkWall = new Wall("dark-wall") {
            {
                this.requirements(Category.defense, ItemStack.with(new Object[]{UnityItems.umbrium, 6}));
                this.health = 480;
            }
        };
        darkWallLarge = new Wall("dark-wall-large") {
            {
                this.requirements(Category.defense, ItemStack.with(new Object[]{UnityItems.umbrium, 24}));
                this.health = 1920;
                this.size = 2;
            }
        };
        photon = new LaserTurret("photon") {
            {
                this.requirements(Category.turret, ItemStack.with(new Object[]{Items.lead, 50, Items.silicon, 35, UnityItems.luminum, 65, Items.titanium, 65}));
                this.size = 2;
                this.health = 1280;
                this.reloadTime = 100.0F;
                this.shootCone = 30.0F;
                this.range = 120.0F;
                this.powerUse = 4.5F;
                this.heatColor = UnityPal.lightHeat;
                this.loopSound = Sounds.respawning;
                this.shootType = new ContinuousLaserBulletType(16.0F) {
                    {
                        this.incendChance = -1.0F;
                        this.length = 130.0F;
                        this.width = 4.0F;
                        this.colors = new Color[]{Pal.lancerLaser.cpy().a(3.75F), Pal.lancerLaser, Color.white};
                        this.strokes = new float[]{0.92F, 0.6F, 0.28F};
                        this.lightColor = this.hitColor = Pal.lancerLaser;
                    }
                };
                ((ConsumeLiquidFilter)this.consumes.add(new ConsumeLiquidFilter((liquid) -> liquid.temperature <= 0.5F && liquid.flammability < 0.1F, 0.2F))).update(false);
            }
        };
        graviton = new LaserTurret("graviton") {
            {
                this.requirements(Category.turret, ItemStack.with(new Object[]{Items.lead, 110, Items.graphite, 90, Items.silicon, 70, UnityItems.luminum, 180, Items.titanium, 135}));
                this.size = 3;
                this.health = 2780;
                this.reloadTime = 150.0F;
                this.recoilAmount = 2.0F;
                this.shootCone = 30.0F;
                this.range = 230.0F;
                this.powerUse = 5.75F;
                this.heatColor = UnityPal.lightHeat;
                this.loopSound = UnitySounds.xenoBeam;
                this.shootType = new GravitonLaserBulletType(0.8F) {
                    {
                        this.length = 260.0F;
                        this.knockback = -5.0F;
                        this.incendChance = -1.0F;
                        this.colors = new Color[]{UnityPal.advanceDark.cpy().a(0.1F), Pal.lancerLaser.cpy().a(0.2F)};
                        this.strokes = new float[]{2.4F, 1.8F};
                    }
                };
                ((ConsumeLiquidFilter)this.consumes.add(new ConsumeLiquidFilter((liquid) -> liquid.temperature <= 0.5F && liquid.flammability < 0.1F, 0.25F))).update(false);
            }
        };
        electron = new PowerTurret("electron") {
            {
                this.requirements(Category.turret, ItemStack.with(new Object[]{Items.lead, 110, Items.silicon, 75, UnityItems.luminum, 165, Items.titanium, 125}));
                this.size = 3;
                this.health = 2540;
                this.reloadTime = 60.0F;
                this.coolantMultiplier = 2.0F;
                this.range = 170.0F;
                this.powerUse = 6.6F;
                this.heatColor = UnityPal.lightHeat;
                this.shootEffect = ShootFx.blueTriangleShoot;
                this.shootSound = Sounds.pew;
                this.shootType = new BasicBulletType(9.0F, 34.0F, "unity-electric-shell") {
                    {
                        this.lifetime = 22.0F;
                        this.width = 12.0F;
                        this.height = 19.0F;
                        this.shrinkX = this.shrinkY = 0.0F;
                        this.backColor = this.lightColor = this.hitColor = Pal.lancerLaser;
                        this.frontColor = Color.white;
                        this.hitEffect = HitFx.electronHit;
                    }

                    public void update(Bullet b) {
                        super.update(b);
                        if (b.timer(0, 2.0F + b.fslope() * 1.5F)) {
                            UnityFx.blueTriangleTrail.at(b.x, b.y, b.rotation());
                        }

                    }
                };
            }
        };
        proton = new PowerTurret("proton") {
            {
                this.requirements(Category.turret, ItemStack.with(new Object[]{Items.lead, 110, Items.silicon, 75, UnityItems.luminum, 165, Items.titanium, 135}));
                this.size = 4;
                this.health = 2540;
                this.reloadTime = 60.0F;
                this.range = 245.0F;
                this.shootCone = 20.0F;
                this.heatColor = UnityPal.lightHeat;
                this.rotateSpeed = 1.5F;
                this.recoilAmount = 4.0F;
                this.powerUse = 4.9F;
                this.targetAir = false;
                this.cooldown = 0.008F;
                this.shootEffect = ShootFx.blueTriangleShoot;
                this.shootType = new ArtilleryBulletType(8.0F, 44.0F, "unity-electric-shell") {
                    {
                        this.lifetime = 35.0F;
                        this.width = 18.0F;
                        this.splashDamage = 23.0F;
                        this.splashDamageRadius = 45.0F;
                        this.height = 27.0F;
                        this.shrinkX = this.shrinkY = 0.0F;
                        this.hitSize = 15.0F;
                        this.hitEffect = HitFx.protonHit;
                        this.hittable = this.collides = false;
                        this.backColor = this.lightColor = this.hitColor = this.lightningColor = Pal.lancerLaser;
                        this.frontColor = Color.white;
                        this.lightning = 3;
                        this.lightningDamage = 18.0F;
                        this.lightningLength = 10;
                        this.lightningLengthRand = 6;
                    }

                    public void update(Bullet b) {
                        super.update(b);
                        if (b.timer(0, 2.0F + b.fslope() * 1.5F)) {
                            UnityFx.blueTriangleTrail.at(b.x, b.y, b.rotation());
                        }

                    }
                };
            }
        };
        neutron = new PowerTurret("neutron") {
            {
                this.requirements(Category.turret, ItemStack.with(new Object[]{Items.lead, 110, Items.silicon, 75, UnityItems.luminum, 165, Items.titanium, 135}));
                this.size = 4;
                this.health = 2520;
                this.reloadTime = 10.0F;
                this.range = 235.0F;
                this.shootCone = 20.0F;
                this.heatColor = UnityPal.lightHeat;
                this.rotateSpeed = 3.9F;
                this.recoilAmount = 4.0F;
                this.powerUse = 4.9F;
                this.cooldown = 0.008F;
                this.inaccuracy = 3.4F;
                this.shootEffect = ShootFx.blueTriangleShoot;
                this.shootType = new FlakBulletType(8.7F, 7.0F) {
                    {
                        this.lifetime = 30.0F;
                        this.width = 8.0F;
                        this.height = 14.0F;
                        this.splashDamage = 28.0F;
                        this.splashDamageRadius = 34.0F;
                        this.shrinkX = this.shrinkY = 0.0F;
                        this.hitSize = 7.0F;
                        this.sprite = "unity-electric-shell";
                        this.hitEffect = HitFx.neutronHit;
                        this.collides = this.collidesGround = true;
                        this.hittable = false;
                        this.backColor = this.lightColor = this.hitColor = Pal.lancerLaser;
                        this.frontColor = Color.white;
                    }

                    public void update(Bullet b) {
                        super.update(b);
                        if (b.timer(0, 2.0F + b.fslope() * 1.5F)) {
                            UnityFx.blueTriangleTrail.at(b.x, b.y, b.rotation());
                        }

                    }
                };
            }
        };
        gluon = new PowerTurret("gluon") {
            {
                this.requirements(Category.turret, ItemStack.with(new Object[]{Items.silicon, 300, UnityItems.luminum, 430, Items.titanium, 190, Items.thorium, 110, UnityItems.lightAlloy, 15}));
                this.size = 4;
                this.health = 5000;
                this.reloadTime = 90.0F;
                this.coolantMultiplier = 3.0F;
                this.shootCone = 30.0F;
                this.range = 200.0F;
                this.heatColor = UnityPal.lightHeat;
                this.rotateSpeed = 4.3F;
                this.recoilAmount = 2.0F;
                this.powerUse = 1.9F;
                this.cooldown = 0.012F;
                this.shootSound = UnitySounds.gluonShoot;
                this.shootType = UnityBullets.gluonEnergyBall;
            }
        };
        wBoson = new PowerTurret("w-boson") {
            {
                this.requirements(Category.turret, ItemStack.with(new Object[]{Items.silicon, 300, UnityItems.luminum, 430, Items.titanium, 190, Items.thorium, 110, UnityItems.lightAlloy, 15}));
                this.health = 4000;
                this.size = 5;
                this.reloadTime = 90.0F;
                this.range = 250.0F;
                this.rotateSpeed = 2.5F;
                this.shootCone = 20.0F;
                this.heatColor = UnityPal.lightHeat;
                this.chargeBeginEffect = ChargeFx.wBosonChargeBeginEffect;
                this.chargeEffect = ChargeFx.wBosonChargeEffect;
                this.chargeTime = 38.0F;
                this.cooldown = 0.008F;
                this.powerUse = 8.6F;
                this.shootType = new DecayBasicBulletType(8.5F, 24.0F) {
                    {
                        this.drag = 0.026F;
                        this.lifetime = 48.0F;
                        this.hittable = this.absorbable = this.collides = false;
                        this.backColor = this.trailColor = this.hitColor = this.lightColor = Pal.lancerLaser;
                        this.shootEffect = this.smokeEffect = Fx.none;
                        this.hitEffect = Fx.hitLancer;
                        this.despawnEffect = HitFx.lightHitLarge;
                        this.frontColor = Color.white;
                        this.decayEffect = UnityFx.wBosonEffectLong;
                        this.height = 13.0F;
                        this.width = 12.0F;
                        this.decayBullet = new BasicBulletType(4.8F, 24.0F) {
                            {
                                this.drag = 0.04F;
                                this.lifetime = 18.0F;
                                this.pierce = true;
                                this.pierceCap = 3;
                                this.height = 9.0F;
                                this.width = 8.0F;
                                this.backColor = this.trailColor = this.hitColor = this.lightColor = Pal.lancerLaser;
                                this.hitEffect = Fx.hitLancer;
                                this.despawnEffect = HitFx.wBosonDecayHitEffect;
                                this.frontColor = Color.white;
                                this.hittable = false;
                            }

                            public void draw(Bullet b) {
                                Draw.color(this.backColor);
                                Fill.circle(b.x, b.y, 1.5F + b.fout() * 3.0F);
                                Draw.color(this.frontColor);
                                Fill.circle(b.x, b.y, 0.75F + b.fout() * 2.75F);
                            }

                            public void update(Bullet b) {
                                super.update(b);
                                if (Mathf.chance((double)0.8F)) {
                                    UnityFx.wBosonEffect.at(b, b.rotation() + 180.0F);
                                }

                            }
                        };
                        this.fragBullet = this.decayBullet;
                        this.fragBullets = 12;
                        this.fragVelocityMin = 0.75F;
                        this.fragVelocityMax = 1.25F;
                        this.fragLifeMin = 1.2F;
                        this.fragLifeMax = 1.3F;
                    }
                };
            }

            public void load() {
                super.load();
                this.baseRegion = Core.atlas.find("unity-block-" + this.size);
            }
        };
        zBoson = new RampupPowerTurret("z-boson") {
            {
                this.requirements(Category.turret, ItemStack.with(new Object[]{Items.silicon, 290, UnityItems.luminum, 430, Items.titanium, 190, Items.thorium, 120, UnityItems.lightAlloy, 15}));
                this.health = 4000;
                this.size = 5;
                this.reloadTime = 40.0F;
                this.range = 230.0F;
                this.shootCone = 20.0F;
                this.heatColor = UnityPal.lightHeat;
                this.coolantMultiplier = 1.9F;
                this.rotateSpeed = 2.7F;
                this.recoilAmount = 2.0F;
                this.restitution = 0.09F;
                this.cooldown = 0.008F;
                this.powerUse = 3.6F;
                this.targetAir = true;
                this.shootSound = UnitySounds.zbosonShoot;
                this.alternate = true;
                this.shots = 2;
                this.spread = 14.0F;
                this.inaccuracy = 2.3F;
                this.lightning = true;
                this.lightningThreshold = 12.0F;
                this.baseLightningLength = 16;
                this.lightningLengthDec = 1;
                this.baseLightningDamage = 18.0F;
                this.lightningDamageDec = 1.0F;
                this.barBaseY = -10.75F;
                this.barLength = 20.0F;
                this.shootType = new VelocityLaserBoltBulletType(9.5F, 56.0F) {
                    {
                        this.splashDamage = 8.0F;
                        this.splashDamageRadius = 16.0F;
                        this.drag = 0.005F;
                        this.lifetime = 27.0F;
                        this.hitSize = 8.0F;
                        this.shootEffect = this.smokeEffect = Fx.none;
                        this.hitEffect = Fx.hitLancer;
                        this.hittable = false;
                    }
                };
            }

            public void load() {
                super.load();
                this.baseRegion = Core.atlas.find("unity-block-" + this.size);
            }
        };
        higgsBoson = new PowerTurret("higgs-boson") {
            {
                this.requirements(Category.turret, ItemStack.with(new Object[]{Items.silicon, 290, UnityItems.luminum, 430, Items.titanium, 190, Items.thorium, 120, UnityItems.lightAlloy, 20}));
                this.size = 6;
                this.health = 6000;
                this.reloadTime = 13.0F;
                this.alternate = true;
                this.spread = 17.25F;
                this.shots = 2;
                this.range = 260.0F;
                this.shootCone = 20.0F;
                this.heatColor = UnityPal.lightHeat;
                this.coolantMultiplier = 3.4F;
                this.rotateSpeed = 2.2F;
                this.recoilAmount = 1.5F;
                this.restitution = 0.09F;
                this.powerUse = 10.4F;
                this.shootSound = UnitySounds.higgsBosonShoot;
                this.cooldown = 0.008F;
                this.shootType = new RoundLaserBulletType(85.0F) {
                    {
                        this.length = 270.0F;
                        this.width = 5.8F;
                        this.hitSize = 13.0F;
                        this.drawSize = 460.0F;
                        this.shootEffect = this.smokeEffect = Fx.none;
                    }
                };
            }

            public void load() {
                super.load();
                this.baseRegion = Core.atlas.find("unity-block-" + this.size);
            }
        };
        singularity = new PowerTurret("singularity") {
            {
                this.requirements(Category.turret, ItemStack.with(new Object[]{Items.silicon, 290, UnityItems.luminum, 430, Items.titanium, 190, Items.thorium, 120, UnityItems.lightAlloy, 20}));
                this.size = 7;
                this.health = 9800;
                this.reloadTime = 220.0F;
                this.coolantMultiplier = 1.1F;
                this.shootCone = 30.0F;
                this.range = 310.0F;
                this.heatColor = UnityPal.lightHeat;
                this.rotateSpeed = 3.3F;
                this.recoilAmount = 6.0F;
                this.powerUse = 39.3F;
                this.cooldown = 0.012F;
                this.shootSound = UnitySounds.singularityShoot;
                this.shootType = UnityBullets.singularityEnergyBall;
            }

            public void load() {
                super.load();
                this.baseRegion = Core.atlas.find("unity-block-" + this.size);
            }
        };
        muon = new PowerTurret("muon") {
            {
                this.requirements(Category.turret, ItemStack.with(new Object[]{Items.silicon, 290, UnityItems.luminum, 430, Items.titanium, 190, Items.thorium, 120, UnityItems.lightAlloy, 25}));
                this.size = 8;
                this.health = 9800;
                this.range = 310.0F;
                this.shots = 9;
                this.spread = 12.0F;
                this.reloadTime = 90.0F;
                this.coolantMultiplier = 1.9F;
                this.shootCone = 80.0F;
                this.powerUse = 18.0F;
                this.shootShake = 5.0F;
                this.recoilAmount = 8.0F;
                this.shootLength = (float)(this.size * 8) / 2.0F - 8.0F;
                this.shootSound = UnitySounds.muonShoot;
                this.rotateSpeed = 1.9F;
                this.heatColor = UnityPal.lightHeat;
                this.cooldown = 0.009F;
                this.shootType = new RoundLaserBulletType(200.0F) {
                    {
                        this.length = 330.0F;
                        this.width = 3.8F;
                        this.hitSize = 13.0F;
                        this.hitEffect = Fx.hitLancer;
                        this.despawnEffect = Fx.none;
                        this.drawSize = 460.0F;
                        this.shootEffect = Fx.lightningShoot;
                        this.smokeEffect = Fx.none;
                    }
                };
            }

            public void load() {
                super.load();
                this.baseRegion = Core.atlas.find("unity-block-" + this.size);
            }
        };
        ephemeron = new PowerTurret("ephemeron") {
            {
                this.requirements(Category.turret, ItemStack.with(new Object[]{Items.silicon, 290, UnityItems.luminum, 430, Items.titanium, 190, Items.thorium, 120, UnityItems.lightAlloy, 25}));
                this.size = 8;
                this.health = 9800;
                this.range = 320.0F;
                this.reloadTime = 70.0F;
                this.coolantMultiplier = 1.9F;
                this.powerUse = 26.0F;
                this.shootShake = 2.0F;
                this.recoilAmount = 4.0F;
                this.shootSound = UnitySounds.ephemeronShoot;
                this.rotateSpeed = 1.9F;
                this.heatColor = UnityPal.lightHeat;
                this.cooldown = 0.009F;
                this.chargeTime = 80.0F;
                this.chargeBeginEffect = ChargeFx.ephmeronCharge;
                this.shootType = new EphemeronBulletType(7.7F, 10.0F) {
                    {
                        this.lifetime = 70.0F;
                        this.hitSize = 12.0F;
                        this.pierce = true;
                        this.collidesTiles = false;
                        this.scaleVelocity = true;
                        this.shootEffect = Fx.lightningShoot;
                        this.hitEffect = Fx.hitLancer;
                        this.despawnEffect = this.smokeEffect = Fx.none;
                        this.positive = new EphemeronPairBulletType(4.0F) {
                            {
                                this.positive = true;
                                this.frontColor = Pal.lancerLaser;
                                this.backColor = Color.white;
                            }
                        };
                        this.negative = new EphemeronPairBulletType(4.0F) {
                            {
                                this.frontColor = Color.white;
                                this.backColor = Pal.lancerLaser;
                            }
                        };
                    }
                };
            }

            public void load() {
                super.load();
                this.baseRegion = Core.atlas.find("unity-block-" + this.size);
            }
        };
        lightLamp = new LightSource("light-lamp") {
            {
                this.requirements(Category.crafting, ItemStack.with(new Object[]{Items.lead, 5, Items.metaglass, 10}));
                this.lightProduction = 0.6F;
                this.consumes.power(1.0F);
                this.drawer = new DrawLightBlock();
            }
        };
        oilLamp = new LightSource("oil-lamp") {
            {
                this.requirements(Category.logic, ItemStack.with(new Object[]{Items.lead, 20, Items.metaglass, 20, Items.titanium, 15}));
                this.size = 3;
                this.health = 240;
                this.lightProduction = 2.0F;
                this.consumes.power(1.8F);
                this.consumes.liquid(Liquids.oil, 0.1F);
                this.drawer = new DrawLightBlock();
            }
        };
        lightLampInfi = new LightSource("light-lamp-infi") {
            {
                this.requirements(Category.logic, BuildVisibility.sandboxOnly, ItemStack.with(new Object[0]));
                this.lightProduction = 600000.0F;
                this.drawer = new DrawLightBlock();
            }
        };
        lightReflector = new LightReflector("light-reflector") {
            {
                this.requirements(Category.logic, ItemStack.with(new Object[]{Items.metaglass, 10, Items.silicon, 5}));
            }
        };
        lightDivisor = new LightReflector("light-divisor") {
            {
                this.requirements(Category.logic, ItemStack.with(new Object[]{Items.metaglass, 10, Items.titanium, 2}));
                this.health = 80;
                this.fallthrough = 0.5F;
            }
        };
        metaglassWall = new LightWall("metaglass-wall") {
            {
                this.requirements(Category.defense, ItemStack.with(new Object[]{Items.lead, 6, Items.metaglass, 6}));
                this.health = 350;
            }
        };
        metaglassWallLarge = new LightWall("metaglass-wall-large") {
            {
                this.requirements(Category.defense, ItemStack.with(new Object[]{Items.lead, 24, Items.metaglass, 24}));
                this.size = 2;
                this.health = 1400;
            }
        };
        lightForge = new LightHoldGenericCrafter("light-forge") {
            {
                this.requirements(Category.crafting, ItemStack.with(new Object[]{Items.copper, 1}));
                this.size = 4;
                this.outputItem = new ItemStack(UnityItems.lightAlloy, 3);
                this.consumes.items(ItemStack.with(new Object[]{Items.copper, 2, Items.silicon, 5, Items.plastanium, 2, UnityItems.luminum, 2}));
                this.consumes.power(3.5F);
                this.drawer = new DrawSmelter(UnityPal.lightDark) {
                    {
                        this.flameRadius = 7.0F;
                        this.flameRadiusIn = 3.5F;
                        this.flameRadiusMag = 3.0F;
                        this.flameRadiusInMag = 1.8F;
                    }
                };
                float req = 4.0F;
                this.acceptors.add((new LightAcceptorType(0, 0, req / 4.0F)).update((b, s) -> s.data.floatValue = Mathf.lerpDelta(s.data.floatValue, Mathf.clamp(s.status()), this.warmupSpeed)).draw((b, s) -> {
                    Draw.z(30.01F);
                    Draw.alpha(s.data.floatValue);
                    Draw.blend(Blending.additive);
                    Draw.rect(Regions.lightForgeTop1Region, b.x, b.y);
                    Draw.blend();
                }), (new LightAcceptorType(this.size - 1, 0, req / 4.0F)).update((b, s) -> s.data.floatValue = Mathf.lerpDelta(s.data.floatValue, Mathf.clamp(s.status()), this.warmupSpeed)).draw((b, s) -> {
                    Draw.z(30.01F);
                    Draw.alpha(s.data.floatValue);
                    Draw.blend(Blending.additive);
                    Draw.rect(Regions.lightForgeTop2Region, b.x, b.y);
                    Draw.blend();
                }), (new LightAcceptorType(this.size - 1, this.size - 1, req / 4.0F)).update((b, s) -> s.data.floatValue = Mathf.lerpDelta(s.data.floatValue, Mathf.clamp(s.status()), this.warmupSpeed)).draw((b, s) -> {
                    Draw.z(30.01F);
                    Draw.alpha(s.data.floatValue);
                    Draw.blend(Blending.additive);
                    Draw.rect(Regions.lightForgeTop3Region, b.x, b.y);
                    Draw.blend();
                }), (new LightAcceptorType(0, this.size - 1, req / 4.0F)).update((b, s) -> s.data.floatValue = Mathf.lerpDelta(s.data.floatValue, Mathf.clamp(s.status()), this.warmupSpeed)).draw((b, s) -> {
                    Draw.z(30.01F);
                    Draw.alpha(s.data.floatValue);
                    Draw.blend(Blending.additive);
                    Draw.rect(Regions.lightForgeTop4Region, b.x, b.y);
                    Draw.blend();
                }));
            }
        };
        terraCore = new TerraCore("terra-core") {
            {
                this.requirements(Category.units, ItemStack.with(new Object[]{Items.copper, 1}));
                this.size = 2;
            }
        };
        orb = new PowerTurret("orb") {
            {
                this.requirements(Category.turret, ItemStack.with(new Object[]{Items.copper, 55, Items.lead, 30, Items.graphite, 25, Items.silicon, 35, UnityItems.imberium, 20}));
                this.size = 2;
                this.health = 480;
                this.range = 145.0F;
                this.reloadTime = 130.0F;
                this.coolantMultiplier = 2.0F;
                this.shootCone = 0.1F;
                this.shots = 1;
                this.inaccuracy = 12.0F;
                this.chargeTime = 65.0F;
                this.chargeEffects = 5;
                this.chargeMaxDelay = 25.0F;
                this.powerUse = 4.2069F;
                this.targetAir = false;
                this.shootType = UnityBullets.orb;
                this.shootSound = Sounds.laser;
                this.heatColor = Pal.turretHeat;
                this.shootEffect = ShootFx.orbShoot;
                this.smokeEffect = Fx.none;
                this.chargeEffect = UnityFx.orbCharge;
                this.chargeBeginEffect = UnityFx.orbChargeBegin;
            }
        };
        shockwire = new LaserTurret("shockwire") {
            {
                this.requirements(Category.turret, ItemStack.with(new Object[]{Items.copper, 150, Items.lead, 145, Items.titanium, 160, Items.silicon, 130, UnityItems.imberium, 70}));
                this.size = 2;
                this.health = 860;
                this.range = 125.0F;
                this.reloadTime = 140.0F;
                this.coolantMultiplier = 2.0F;
                this.shootCone = 1.0F;
                this.inaccuracy = 0.0F;
                this.powerUse = 6.942F;
                this.targetAir = false;
                this.shootType = UnityBullets.shockBeam;
                this.shootSound = Sounds.thruster;
                ((ConsumeLiquidFilter)this.consumes.add(new ConsumeLiquidFilter((liquid) -> liquid.temperature <= 0.5F && liquid.flammability <= 0.1F, 0.4F))).update(false);
            }
        };
        current = new PowerTurret("current") {
            {
                this.requirements(Category.turret, ItemStack.with(new Object[]{Items.copper, 280, Items.lead, 295, Items.silicon, 260, UnityItems.sparkAlloy, 65}));
                this.size = 3;
                this.health = 2400;
                this.range = 220.0F;
                this.reloadTime = 120.0F;
                this.coolantMultiplier = 2.0F;
                this.shootCone = 0.01F;
                this.inaccuracy = 0.0F;
                this.chargeTime = 60.0F;
                this.chargeEffects = 4;
                this.chargeMaxDelay = 260.0F;
                this.powerUse = 6.8F;
                this.shootType = UnityBullets.currentStroke;
                this.shootSound = Sounds.laserbig;
                this.chargeEffect = UnityFx.currentCharge;
                this.chargeBeginEffect = UnityFx.currentChargeBegin;
                ((ConsumeLiquidFilter)this.consumes.add(new ConsumeLiquidFilter((liquid) -> liquid.temperature <= 0.5F && liquid.flammability <= 0.1F, 0.52F))).boost();
            }
        };
        plasma = new PowerTurret("plasma") {
            {
                this.requirements(Category.turret, ItemStack.with(new Object[]{Items.copper, 580, Items.lead, 520, Items.graphite, 410, Items.silicon, 390, Items.surgeAlloy, 180, UnityItems.sparkAlloy, 110}));
                this.size = 4;
                this.health = 2800;
                this.range = 200.0F;
                this.reloadTime = 360.0F;
                this.recoilAmount = 4.0F;
                this.coolantMultiplier = 1.2F;
                this.liquidCapacity = 20.0F;
                this.shootCone = 1.0F;
                this.inaccuracy = 0.0F;
                this.powerUse = 8.2F;
                this.shootType = UnityBullets.plasmaTriangle;
                this.shootSound = Sounds.shotgun;
                ((ConsumeLiquidFilter)this.consumes.add(new ConsumeLiquidFilter((liquid) -> liquid.temperature <= 0.5F && liquid.flammability <= 0.1F, 0.52F))).boost();
            }
        };
        electrobomb = new ItemTurret("electrobomb") {
            {
                this.requirements(Category.turret, ItemStack.with(new Object[]{Items.titanium, 360, Items.thorium, 630, Items.silicon, 240, UnityItems.sparkAlloy, 420}));
                this.health = 3650;
                this.size = 5;
                this.range = 400.0F;
                this.minRange = 60.0F;
                this.reloadTime = 320.0F;
                this.coolantMultiplier = 2.0F;
                this.shootCone = 20.0F;
                this.shots = 1;
                this.inaccuracy = 0.0F;
                this.targetAir = false;
                this.ammo(new Object[]{UnityItems.sparkAlloy, UnityBullets.surgeBomb});
                this.shootSound = Sounds.laser;
                this.shootEffect = Fx.none;
                this.smokeEffect = Fx.none;
                this.consumes.powerCond(10.0F, Turret.TurretBuild::isActive);
            }

            public void load() {
                super.load();
                this.baseRegion = Core.atlas.find("unity-block-" + this.size);
            }
        };
        shielder = new ShieldTurret("shielder") {
            {
                this.requirements(Category.turret, ItemStack.with(new Object[]{Items.copper, 300, Items.lead, 100, Items.titanium, 160, Items.silicon, 240, UnityItems.sparkAlloy, 90}));
                this.size = 3;
                this.health = 900;
                this.range = 260.0F;
                this.reloadTime = 800.0F;
                this.coolantMultiplier = 2.0F;
                this.shootCone = 60.0F;
                this.inaccuracy = 0.0F;
                this.powerUse = 6.4F;
                this.targetAir = false;
                this.shootType = UnityBullets.shielderBullet;
                this.shootSound = Sounds.pew;
                this.chargeEffect = new Effect(38.0F, (e) -> {
                    Draw.color(Pal.accent);
                    Angles.randLenVectors((long)e.id, 2, 1.0F + 20.0F * e.fout(), e.rotation, 120.0F, (x, y) -> Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fslope() * 3.0F + 1.0F));
                });
                this.chargeBeginEffect = Fx.none;
                ((ConsumeLiquidFilter)this.consumes.add(new ConsumeLiquidFilter((liquid) -> liquid.temperature <= 0.5F && liquid.flammability <= 0.1F, 0.4F))).update(false);
            }
        };
        orbTurret = new OrbTurret("orb-turret") {
            {
                this.requirements(Category.turret, BuildVisibility.shown, ItemStack.with(new Object[]{Items.copper, 1}));
                this.size = 3;
                this.powerUse = 0.3F;
                this.shootType = new BasicBulletType() {
                    {
                        this.damage = 20.0F;
                    }

                    public void draw(Bullet b) {
                        Draw.color(((Color[])b.data)[0]);
                        b.trail.draw(((Color[])b.data)[1], 1.0F);
                        ((TexturedTrail)b.trail).drawCap(((Color[])b.data)[1], 1.0F);
                    }

                    public void update(Bullet b) {
                        super.update(b);
                        ((TexturedTrail)b.trail).update(b.x, b.y);
                    }

                    public void removed(Bullet b) {
                        b.trail = null;
                        super.removed(b);
                    }
                };
            }
        };
        powerPlant = new PowerPlant("power-plant") {
            {
                this.requirements(Category.power, BuildVisibility.editorOnly, ItemStack.with(new Object[]{Items.copper, 1}));
                this.powerProduction = 8.6F;
            }
        };
        sparkAlloyForge = new StemGenericCrafter("spark-alloy-forge") {
            {
                this.requirements(Category.crafting, ItemStack.with(new Object[]{Items.lead, 160, Items.graphite, 340, UnityItems.imberium, 270, Items.silicon, 250, Items.thorium, 120, Items.surgeAlloy, 100}));
                this.outputItem = new ItemStack(UnityItems.sparkAlloy, 4);
                this.size = 4;
                this.craftTime = 160.0F;
                this.ambientSound = Sounds.machine;
                this.ambientSoundVolume = 0.6F;
                this.craftEffect = UnityFx.imberCircleSparkCraftingEffect;
                this.drawer = new DrawSmelter();
                this.consumes.power(2.6F);
                this.consumes.items(ItemStack.with(new Object[]{Items.surgeAlloy, 3, Items.titanium, 4, Items.silicon, 6, UnityItems.imberium, 3}));
                this.update((e) -> {
                    if (e.consValid()) {
                        if (Mathf.chanceDelta((double)0.3F)) {
                            UnityFx.imberSparkCraftingEffect.at(e.x, e.y, Mathf.random(360.0F));
                        } else if (Mathf.chanceDelta((double)0.02F)) {
                            Lightning.create(e.team, UnityPal.imberColor, 5.0F, e.x, e.y, Mathf.random(360.0F), 5);
                        }
                    }

                });
            }
        };
        absorber = new AbsorberTurret("absorber") {
            {
                this.requirements(Category.power, ItemStack.with(new Object[]{UnityItems.imberium, 20, Items.lead, 20}));
                this.consumesPower = false;
                this.powerProduction = 1.2F;
                this.range = 50.0F;
                this.targetUnits = true;
                this.status = StatusEffects.slow;
                this.rotateSpeed = 1.2F;
                this.shootCone = 2.0F;
                this.damage = 0.6F;
            }
        };
        electroTile = new Floor("electro-tile");
        piper = new UnderPiper("piper", 80) {
            {
                this.requirements(Category.distribution, ItemStack.with(new Object[]{Items.copper, 1}));
                this.size = 4;
            }
        };
        denseSmelter = new KoruhCrafter("dense-smelter") {
            {
                this.requirements(Category.crafting, ItemStack.with(new Object[]{Items.copper, 30, Items.lead, 20, UnityItems.stone, 35}));
                this.health = 70;
                this.hasItems = true;
                this.craftTime = 46.2F;
                this.craftEffect = UnityFx.denseCraft;
                this.itemCapacity = 10;
                this.outputItem = new ItemStack(UnityItems.denseAlloy, 1);
                this.consumes.items(ItemStack.with(new Object[]{Items.copper, 1, Items.lead, 2, Items.coal, 1}));
                this.expUse = 2;
                this.expCapacity = 24;
                this.drawer = new DrawExp() {
                    {
                        this.flame = Color.orange;
                        this.glowAmount = 1.0F;
                    }
                };
            }
        };
        solidifier = new LiquidsSmelter("solidifier") {
            {
                this.requirements(Category.crafting, ItemStack.with(new Object[]{Items.copper, 20, UnityItems.denseAlloy, 30}));
                this.health = 150;
                this.hasItems = true;
                this.liquidCapacity = 12.0F;
                this.updateEffect = Fx.fuelburn;
                this.craftEffect = UnityFx.rockFx;
                this.craftTime = 60.0F;
                this.outputItem = new ItemStack(UnityItems.stone, 1);
                this.consumes.add(new ConsumeLiquids(new LiquidStack[]{new LiquidStack(UnityLiquids.lava, 0.1F), new LiquidStack(Liquids.water, 0.1F)}));
                this.drawer = new DrawGlow() {
                    public void draw(GenericCrafter.GenericCrafterBuild build) {
                        Draw.rect(build.block.region, build.x, build.y);
                        Draw.color(liquids[0].color, build.liquids.get(liquids[0]) / liquidCapacity);
                        Draw.rect(this.top, build.x, build.y);
                        Draw.reset();
                    }
                };
            }
        };
        steelSmelter = new GenericCrafter("steel-smelter") {
            {
                this.requirements(Category.crafting, ItemStack.with(new Object[]{Items.lead, 45, Items.silicon, 20, UnityItems.denseAlloy, 30}));
                this.health = 140;
                this.itemCapacity = 10;
                this.craftEffect = UnityFx.craft;
                this.updateEffect = Fx.fuelburn;
                this.craftTime = 300.0F;
                this.outputItem = new ItemStack(UnityItems.steel, 1);
                this.consumes.power(2.0F);
                this.consumes.items(ItemStack.with(new Object[]{Items.coal, 2, Items.graphite, 2, UnityItems.denseAlloy, 3}));
                this.drawer = new DrawGlow() {
                    public void draw(GenericCrafter.GenericCrafterBuild build) {
                        Draw.rect(build.block.region, build.x, build.y);
                        Draw.color(1.0F, 1.0F, 1.0F, build.warmup * Mathf.absin(8.0F, 0.6F));
                        Draw.rect(this.top, build.x, build.y);
                        Draw.reset();
                    }
                };
            }
        };
        lavaSmelter = new MeltingCrafter("lava-smelter") {
            {
                this.requirements(Category.crafting, ItemStack.with(new Object[]{Items.silicon, 70, UnityItems.denseAlloy, 60, UnityItems.steel, 40}));
                this.health = 190;
                this.hasLiquids = true;
                this.hasItems = true;
                this.craftTime = 70.0F;
                this.updateEffect = Fx.fuelburn;
                this.craftEffect = UnityFx.craft;
                this.itemCapacity = 21;
                this.outputItem = new ItemStack(UnityItems.steel, 5);
                this.consumes.items(ItemStack.with(new Object[]{Items.graphite, 7, UnityItems.denseAlloy, 7}));
                this.consumes.power(2.0F);
                this.consumes.liquid(UnityLiquids.lava, 0.4F);
                this.expUse = 10;
                this.expCapacity = 60;
                this.drawer = new DrawLiquid();
            }
        };
        liquifier = new BurnerSmelter("liquifier") {
            {
                this.requirements(Category.crafting, ItemStack.with(new Object[]{Items.titanium, 30, Items.silicon, 15, UnityItems.steel, 10}));
                this.health = 100;
                this.hasLiquids = true;
                this.updateEffect = Fx.fuelburn;
                this.craftTime = 30.0F;
                this.outputLiquid = new LiquidStack(UnityLiquids.lava, 0.1F);
                this.configClear((b) -> Fires.create(b.tile));
                this.consumes.power(3.7F);
                this.update((e) -> {
                    if (e.progress == 0.0F && e.warmup > 0.001F && (Vars.net.server() || !Vars.net.active()) && Mathf.chanceDelta((double)0.2F)) {
                        e.configureAny((Object)null);
                    }

                });
                this.drawer = new DrawGlow() {
                    public void draw(GenericCrafter.GenericCrafterBuild build) {
                        Draw.rect(build.block.region, build.x, build.y);
                        Liquid liquid = outputLiquid.liquid;
                        Draw.color(liquid.color, build.liquids.get(liquid) / liquidCapacity);
                        Draw.rect(this.top, build.x, build.y);
                        Draw.color();
                        Draw.reset();
                    }
                };
            }
        };
        titaniumExtractor = new GenericCrafter("titanium-extractor") {
            {
                this.requirements(Category.crafting, ItemStack.with(new Object[]{Items.lead, 20, Items.metaglass, 10, UnityItems.denseAlloy, 30}));
                this.health = 160;
                this.hasLiquids = true;
                this.updateEffect = UnityFx.craftFx;
                this.itemCapacity = 10;
                this.craftTime = 360.0F;
                this.outputItem = new ItemStack(Items.titanium, 1);
                this.consumes.power(1.0F);
                this.consumes.items(ItemStack.with(new Object[]{UnityItems.denseAlloy, 3, UnityItems.steel, 2}));
                this.consumes.liquid(Liquids.water, 0.3F);
                this.drawer = new DrawGlow() {
                    public void draw(GenericCrafter.GenericCrafterBuild build) {
                        Draw.rect(build.block.region, build.x, build.y);
                        Draw.color(UnityItems.denseAlloy.color, Items.titanium.color, build.progress);
                        Draw.alpha(0.6F);
                        Draw.rect(this.top, build.x, build.y);
                        Draw.reset();
                    }
                };
            }
        };
        diriumCrucible = new KoruhCrafter("dirium-crucible") {
            {
                this.requirements(Category.crafting, ItemStack.with(new Object[]{Items.plastanium, 60, UnityItems.stone, 90, UnityItems.denseAlloy, 90, UnityItems.steel, 150}));
                this.health = 320;
                this.hasItems = true;
                this.craftTime = 250.0F;
                this.craftEffect = UnityFx.diriumCraft;
                this.itemCapacity = 40;
                this.ambientSound = Sounds.techloop;
                this.ambientSoundVolume = 0.02F;
                this.outputItem = new ItemStack(UnityItems.dirium, 1);
                this.consumes.items(ItemStack.with(new Object[]{Items.titanium, 6, Items.pyratite, 3, Items.surgeAlloy, 3, UnityItems.steel, 9}));
                this.consumes.power(8.28F);
                this.expUse = 40;
                this.expCapacity = 160;
                this.ignoreExp = false;
                this.craftDamage = 0.0F;
                this.drawer = new DrawExp();
            }
        };
        coalExtractor = new KoruhCrafter("coal-extractor") {
            {
                this.requirements(Category.crafting, ItemStack.with(new Object[]{Items.silicon, 80, UnityItems.stone, 100, UnityItems.steel, 150}));
                this.health = 250;
                this.hasItems = true;
                this.craftTime = 240.0F;
                this.craftEffect = UnityFx.craftFx;
                this.itemCapacity = 50;
                this.consumes.items(ItemStack.with(new Object[]{UnityItems.stone, 6, Items.scrap, 2}));
                this.consumes.liquid(Liquids.water, 0.5F);
                this.consumes.power(6.0F);
                this.outputItem = new ItemStack(Items.coal, 1);
                this.expUse = 30;
                this.expCapacity = 120;
                this.craftDamage = 0.0F;
                this.drawer = new DrawExp();
                this.ignoreExp = false;
                this.ambientSound = Sounds.techloop;
                this.ambientSoundVolume = 0.01F;
            }
        };
        stoneWall = new LimitWall("ustone-wall") {
            {
                this.requirements(Category.defense, ItemStack.with(new Object[]{UnityItems.stone, 6}));
                this.maxDamage = 40.0F;
                this.health = 200;
            }
        };
        denseWall = new LimitWall("dense-wall") {
            {
                this.requirements(Category.defense, ItemStack.with(new Object[]{UnityItems.denseAlloy, 6}));
                this.maxDamage = 32.0F;
                this.health = 560;
            }
        };
        steelWall = new LevelLimitWall("steel-wall") {
            {
                this.requirements(Category.defense, ItemStack.with(new Object[]{UnityItems.steel, 6}));
                this.maxDamage = 24.0F;
                this.health = 810;
                this.maxLevel = 6;
                this.expFields = new EField[]{(new EField.ERational((v) -> this.maxDamage = v, 48.0F, 24.0F, -3.0F, Stat.abilities, (v) -> Core.bundle.format("stat.unity.maxdamage", new Object[]{v}))).formatAll(false)};
            }
        };
        steelWallLarge = new LevelLimitWall("steel-wall-large") {
            {
                this.requirements(Category.defense, ItemStack.with(new Object[]{UnityItems.steel, 24}));
                this.maxDamage = 48.0F;
                this.health = 3240;
                this.size = 2;
                this.maxLevel = 12;
                this.expFields = new EField[]{(new EField.ERational((v) -> this.maxDamage = v, 72.0F, 24.0F, -3.0F, Stat.abilities, (v) -> Core.bundle.format("stat.unity.maxdamage", new Object[]{v}))).formatAll(false)};
            }
        };
        diriumWall = new LevelLimitWall("dirium-wall") {
            {
                this.requirements(Category.defense, ItemStack.with(new Object[]{UnityItems.dirium, 6}));
                this.maxDamage = 76.0F;
                this.blinkFrame = 30.0F;
                this.health = 760;
                this.updateEffect = UnityFx.sparkle;
                this.maxLevel = 6;
                this.expFields = new EField[]{(new EField.ERational((v) -> this.maxDamage = v, 152.0F, 50.0F, -3.0F, Stat.abilities, (v) -> Core.bundle.format("stat.unity.maxdamage", new Object[]{v}))).formatAll(false), (new EField.ELinearCap((v) -> this.blinkFrame = v, 10.0F, 10.0F, 2, Stat.abilities, (v) -> Core.bundle.format("stat.unity.blinkframe", new Object[]{v}))).formatAll(false)};
            }
        };
        diriumWallLarge = new LevelLimitWall("dirium-wall-large") {
            {
                this.requirements(Category.defense, ItemStack.with(new Object[]{UnityItems.dirium, 24}));
                this.maxDamage = 152.0F;
                this.blinkFrame = 30.0F;
                this.health = 3040;
                this.size = 2;
                this.updateEffect = UnityFx.sparkle;
                this.maxLevel = 12;
                this.expFields = new EField[]{(new EField.ERational((v) -> this.maxDamage = v, 304.0F, 50.0F, -2.0F, Stat.abilities, (v) -> Core.bundle.format("stat.unity.maxdamage", new Object[]{v}))).formatAll(false), (new EField.ELinearCap((v) -> this.blinkFrame = v, 10.0F, 5.0F, 4, Stat.abilities, (v) -> Core.bundle.format("stat.unity.blinkframe", new Object[]{v}))).formatAll(false)};
            }
        };
        shieldProjector = new ClassicProjector("shield-generator") {
            {
                this.requirements(Category.effect, ItemStack.with(new Object[]{Items.silicon, 50, Items.titanium, 35, UnityItems.steel, 15}));
                this.health = 200;
                this.cooldownNormal = 1.0F;
                this.cooldownBrokenBase = 0.3F;
                this.phaseRadiusBoost = 10.0F;
                this.phaseShieldBoost = 200.0F;
                this.hasItems = this.hasLiquids = false;
                this.consumes.power(1.5F);
                this.maxLevel = 15;
                this.expFields = new EField[]{new EField.ELinear((v) -> this.radius = v, 40.0F, 0.5F, Stat.range, (v) -> Strings.autoFixed(v / 8.0F, 2) + " blocks"), new EField.ELinear((v) -> this.shieldHealth = v, 500.0F, 25.0F, Stat.shieldHealth)};
                this.fromColor = this.toColor = Pal.lancerLaser;
            }
        };
        diriumProjector = new ClassicProjector("deflect-generator") {
            {
                this.requirements(Category.effect, ItemStack.with(new Object[]{Items.silicon, 50, Items.titanium, 30, UnityItems.steel, 30, UnityItems.dirium, 8}));
                this.health = 800;
                this.size = 2;
                this.cooldownNormal = 1.5F;
                this.cooldownLiquid = 1.2F;
                this.cooldownBrokenBase = 0.35F;
                this.phaseRadiusBoost = 40.0F;
                this.consumes.item(Items.phaseFabric).boost();
                this.consumes.power(5.0F);
                this.fromColor = Pal.lancerLaser;
                this.toColor = UnityPal.diriumLight;
                this.maxLevel = 30;
                this.expFields = new EField[]{new EField.ELinear((v) -> this.radius = v, 60.0F, 0.75F, Stat.range, (v) -> Strings.autoFixed(v / 8.0F, 2) + " blocks"), new EField.ELinear((v) -> this.shieldHealth = v, 820.0F, 35.0F, Stat.shieldHealth), new EField.ELinear((v) -> this.deflectChance = v, 0.0F, 0.1F, Stat.baseDeflectChance, (v) -> Strings.autoFixed(v * 100.0F, 1) + "%")};
                this.pregrade = (ClassicProjector)UnityBlocks.shieldProjector;
                this.pregradeLevel = 5;
                this.effectColors = new Color[]{Pal.lancerLaser, UnityPal.lancerDir1, UnityPal.lancerDir2, UnityPal.lancerDir3, UnityPal.diriumLight};
            }
        };
        timeMine = new TimeMine("time-mine") {
            {
                this.requirements(Category.effect, ItemStack.with(new Object[]{Items.lead, 25, Items.silicon, 12}));
                this.hasShadow = false;
                this.health = 45;
                this.pullTime = 360.0F;
            }
        };
        steelConveyor = new KoruhConveyor("steel-conveyor") {
            {
                this.requirements(Category.distribution, ItemStack.with(new Object[]{UnityItems.stone, 1, UnityItems.denseAlloy, 1, UnityItems.steel, 1}));
                this.health = 140;
                this.speed = 0.1F;
                this.displayedSpeed = 12.5F;
                this.drawMultiplier = 1.9F;
            }
        };
        diriumConveyor = new ExpKoruhConveyor("dirium-conveyor") {
            {
                this.requirements(Category.distribution, ItemStack.with(new Object[]{UnityItems.steel, 1, Items.phaseFabric, 1, UnityItems.dirium, 1}));
                this.health = 150;
                this.speed = 0.16F;
                this.displayedSpeed = 20.0F;
                this.drawMultiplier = 1.3F;
                this.draw = new DrawOver();
            }
        };
        bufferPad = new MechPad("buffer-pad") {
            {
                this.requirements(Category.units, ItemStack.with(new Object[]{UnityItems.stone, 120, Items.copper, 170, Items.lead, 150, Items.titanium, 150, Items.silicon, 180}));
                this.size = 2;
                this.craftTime = 100.0F;
                this.consumes.power(0.7F);
                this.unitType = UnityUnitTypes.buffer;
            }
        };
        omegaPad = new MechPad("omega-pad") {
            {
                this.requirements(Category.units, ItemStack.with(new Object[]{UnityItems.stone, 220, Items.lead, 200, Items.silicon, 230, Items.thorium, 260, Items.surgeAlloy, 100}));
                this.size = 3;
                this.craftTime = 300.0F;
                this.consumes.power(1.2F);
                this.unitType = UnityUnitTypes.omega;
            }
        };
        cachePad = new MechPad("cache-pad") {
            {
                this.requirements(Category.units, ItemStack.with(new Object[]{UnityItems.stone, 150, Items.lead, 160, Items.silicon, 100, Items.titanium, 60, Items.plastanium, 120, Items.phaseFabric, 60}));
                this.size = 2;
                this.craftTime = 130.0F;
                this.consumes.power(0.8F);
                this.unitType = UnityUnitTypes.cache;
            }
        };
        convertPad = new ConversionPad("conversion-pad") {
            {
                this.requirements(Category.units, BuildVisibility.sandboxOnly, ItemStack.empty);
                this.size = 2;
                this.craftTime = 60.0F;
                this.consumes.power(1.0F);
                this.upgrades.add(new UnitType[]{UnitTypes.dagger, UnitTypes.mace}, new UnitType[]{UnitTypes.flare, UnitTypes.horizon}, new UnitType[]{UnityUnitTypes.cache, UnityUnitTypes.dijkstra}, new UnitType[]{UnityUnitTypes.omega, UnitTypes.reign});
            }
        };
        uraniumReactor = new KoruhReactor("uranium-reactor") {
            {
                this.requirements(Category.power, ItemStack.with(new Object[]{Items.plastanium, 80, Items.surgeAlloy, 100, Items.lead, 150, UnityItems.steel, 200}));
                this.size = 3;
                this.itemDuration = 200.0F;
                this.consumes.item(UnityItems.uranium, 2);
                this.consumes.liquid(Liquids.water, 0.7F);
                this.consumes.power(20.0F);
                this.itemCapacity = 20;
                this.powerProduction = 150.0F;
                this.health = 1000;
                this.plasma1 = Color.valueOf("a5e1a2");
                this.plasma2 = Color.valueOf("869B84");
            }
        };
        teleporter = new Teleporter("teleporter") {
            {
                this.requirements(Category.distribution, ItemStack.with(new Object[]{Items.lead, 22, Items.silicon, 10, Items.phaseFabric, 32, UnityItems.dirium, 32}));
            }
        };
        teleunit = new TeleUnit("teleunit") {
            {
                this.requirements(Category.units, ItemStack.with(new Object[]{Items.lead, 180, Items.titanium, 80, Items.silicon, 90, Items.phaseFabric, 64, UnityItems.dirium, 48}));
                this.size = 3;
                this.ambientSound = Sounds.techloop;
                this.ambientSoundVolume = 0.02F;
                this.consumes.power(3.0F);
            }
        };
        laser = new ExpPowerTurret("laser-turret") {
            {
                this.requirements(Category.turret, ItemStack.with(new Object[]{Items.copper, 90, Items.silicon, 40, Items.titanium, 15}));
                this.size = 2;
                this.health = 600;
                this.reloadTime = 35.0F;
                this.coolantMultiplier = 2.0F;
                this.range = 140.0F;
                this.targetAir = false;
                this.shootSound = Sounds.laser;
                this.powerUse = 7.0F;
                this.shootType = UnityBullets.laser;
                this.maxLevel = 10;
                this.expFields = new EField[]{new ExpTurret.LinearReloadTime(this, (v) -> this.reloadTime = v, 45.0F, -2.0F), new EField.ELinear((v) -> this.range = v, 120.0F, 2.0F, Stat.shootRange, (v) -> Strings.autoFixed(v / 8.0F, 2) + " blocks"), new EField.EBool((v) -> this.targetAir = v, false, 5, Stat.targetsAir)};
            }
        };
        laserCharge = new ExpPowerTurret("charge-laser-turret") {
            {
                this.requirements(Category.turret, ItemStack.with(new Object[]{UnityItems.denseAlloy, 60, Items.graphite, 15}));
                this.size = 2;
                this.health = 1400;
                this.reloadTime = 60.0F;
                this.coolantMultiplier = 2.0F;
                this.range = 140.0F;
                this.chargeTime = 50.0F;
                this.chargeMaxDelay = 30.0F;
                this.chargeEffects = 4;
                this.recoilAmount = 2.0F;
                this.cooldown = 0.03F;
                this.targetAir = true;
                this.shootShake = 2.0F;
                this.powerUse = 7.0F;
                this.shootEffect = ShootFx.laserChargeShoot;
                this.smokeEffect = Fx.none;
                this.chargeEffect = UnityFx.laserCharge;
                this.chargeBeginEffect = UnityFx.laserChargeBegin;
                this.heatColor = Color.red;
                this.shootSound = Sounds.laser;
                this.shootType = UnityBullets.shardLaser;
                this.maxLevel = 30;
                this.expFields = new EField[]{new ExpTurret.LinearReloadTime(this, (v) -> this.reloadTime = v, 60.0F, -1.0F), new EField.ELinear((v) -> this.range = v, 140.0F, 1.3F, Stat.shootRange, (v) -> Strings.autoFixed(v / 8.0F, 2) + " blocks")};
                this.pregrade = (ExpTurret)UnityBlocks.laser;
                this.effectColors = new Color[]{Pal.lancerLaser, UnityPal.lancerSap1, UnityPal.lancerSap2, UnityPal.lancerSap3, UnityPal.lancerSap4, UnityPal.lancerSap5, Pal.sapBullet};
            }
        };
        laserFrost = new ExpLiquidTurret("frost-laser-turret") {
            {
                this.ammo(new Object[]{Liquids.cryofluid, UnityBullets.frostLaser});
                this.requirements(Category.turret, ItemStack.with(new Object[]{UnityItems.denseAlloy, 60, Items.metaglass, 15}));
                this.size = 2;
                this.health = 1000;
                this.range = 160.0F;
                this.reloadTime = 80.0F;
                this.targetAir = true;
                this.liquidCapacity = 10.0F;
                this.shootSound = Sounds.laser;
                this.extinguish = false;
                this.maxLevel = 30;
                this.consumes.powerCond(1.0F, Turret.TurretBuild::isActive);
                this.pregrade = (ExpTurret)UnityBlocks.laser;
            }
        };
        laserFractal = new ExpPowerTurret("fractal-laser-turret") {
            {
                this.requirements(Category.turret, ItemStack.with(new Object[]{UnityItems.steel, 50, Items.graphite, 90, Items.thorium, 95}));
                this.size = 3;
                this.health = 2000;
                this.reloadTime = UnityBullets.distField.lifetime / 3.0F;
                this.coolantMultiplier = 2.0F;
                this.range = 140.0F;
                this.chargeTime = 80.0F;
                this.chargeMaxDelay = 20.0F;
                this.chargeEffects = 8;
                this.recoilAmount = 4.0F;
                this.cooldown = 0.03F;
                this.targetAir = true;
                this.shootShake = 5.0F;
                this.powerUse = 13.0F;
                this.shootEffect = ShootFx.laserFractalShoot;
                this.smokeEffect = Fx.none;
                this.chargeEffect = UnityFx.laserFractalCharge;
                this.chargeBeginEffect = UnityFx.laserFractalChargeBegin;
                this.shootSound = Sounds.laser;
                this.heatColor = Color.red;
                this.fromColor = UnityPal.lancerSap3;
                this.toColor = Pal.place;
                this.shootType = UnityBullets.fractalLaser;
                this.maxLevel = 30;
                this.expFields = new EField[]{new ExpTurret.LinearReloadTime(this, (v) -> this.reloadTime = v, UnityBullets.distField.lifetime / 3.0F, -2.0F), new EField.ELinear((v) -> this.range = v, 140.0F, 2.0F, Stat.shootRange, (v) -> Strings.autoFixed(v / 8.0F, 2) + " blocks")};
                this.pregrade = (ExpTurret)UnityBlocks.laserCharge;
                this.pregradeLevel = 15;
                this.effectColors = new Color[]{this.fromColor, Pal.lancerLaser.cpy().lerp(Pal.sapBullet, 0.75F), Pal.sapBullet};
            }
        };
        laserBranch = new BurstChargePowerTurret("swarm-laser-turret") {
            {
                this.requirements(Category.turret, ItemStack.with(new Object[]{UnityItems.steel, 50, Items.silicon, 90, Items.thorium, 95}));
                this.size = 3;
                this.health = 2400;
                this.reloadTime = 90.0F;
                this.coolantMultiplier = 2.25F;
                this.powerUse = 15.0F;
                this.targetAir = true;
                this.range = 150.0F;
                this.chargeTime = 50.0F;
                this.chargeMaxDelay = 30.0F;
                this.chargeEffects = 4;
                this.recoilAmount = 2.0F;
                this.cooldown = 0.03F;
                this.shootShake = 2.0F;
                this.shootEffect = ShootFx.laserChargeShootShort;
                this.smokeEffect = Fx.none;
                this.chargeEffect = UnityFx.laserChargeShort;
                this.chargeBeginEffect = UnityFx.laserChargeBegin;
                this.heatColor = Color.red;
                this.fromColor = UnityPal.lancerSap3;
                this.shootSound = Sounds.plasmaboom;
                this.shootType = UnityBullets.branchLaser;
                this.shootLength = (float)(this.size * 8) / 2.7F;
                this.shots = 4;
                this.burstSpacing = 20.0F;
                this.inaccuracy = 1.0F;
                this.spread = 0.0F;
                this.xRand = 6.0F;
                this.maxLevel = 30;
                this.expFields = new EField[]{new EField.ELinearCap((v) -> this.shots = (int)v, 2.0F, 0.35F, 15, Stat.shots), new EField.ELinearCap((v) -> this.inaccuracy = v, 1.0F, 0.25F, 10, Stat.inaccuracy, (v) -> Strings.autoFixed(v, 1) + " degrees"), new EField.ELinear((v) -> this.burstSpacing = v, 20.0F, -0.5F, (Stat)null), new EField.ELinear((v) -> this.range = v, 150.0F, 2.0F, Stat.shootRange, (v) -> Strings.autoFixed(v / 8.0F, 2) + " blocks")};
                this.pregrade = (ExpTurret)UnityBlocks.laserCharge;
                this.pregradeLevel = 15;
                this.effectColors = new Color[]{UnityPal.lancerSap3, UnityPal.lancerSap4, UnityPal.lancerSap5, Pal.sapBullet};
            }
        };
        laserKelvin = new OmniLiquidTurret("kelvin-laser-turret") {
            {
                this.requirements(Category.turret, ItemStack.with(new Object[]{Items.phaseFabric, 50, Items.metaglass, 90, Items.thorium, 95}));
                this.size = 3;
                this.health = 2100;
                this.range = 180.0F;
                this.reloadTime = 120.0F;
                this.targetAir = true;
                this.liquidCapacity = 15.0F;
                this.shootAmount = 3.0F;
                this.shootSound = Sounds.laser;
                this.shootType = new GeyserLaserBulletType(185.0F, 30.0F) {
                    {
                        this.geyser = UnityBullets.laserGeyser;
                        this.damageInc = 5.0F;
                        this.maxRange = 185.0F;
                    }
                };
                this.consumes.powerCond(2.5F, Turret.TurretBuild::isActive);
                this.maxLevel = 30;
                this.pregrade = (ExpTurret)UnityBlocks.laserFrost;
                this.pregradeLevel = 15;
            }
        };
        laserBreakthrough = new ExpPowerTurret("bt-laser-turret") {
            {
                this.requirements(Category.turret, ItemStack.with(new Object[]{UnityItems.dirium, 190, Items.silicon, 230, Items.thorium, 450, UnityItems.steel, 230}));
                this.size = 4;
                this.health = 2800;
                this.range = 500.0F;
                this.coolantMultiplier = 1.5F;
                this.targetAir = true;
                this.reloadTime = 500.0F;
                this.chargeTime = 100.0F;
                this.chargeMaxDelay = 100.0F;
                this.chargeEffects = 0;
                this.recoilAmount = 5.0F;
                this.cooldown = 0.03F;
                this.powerUse = 17.0F;
                this.shootShake = 4.0F;
                this.shootEffect = ShootFx.laserBreakthroughShoot;
                this.smokeEffect = Fx.none;
                this.chargeEffect = Fx.none;
                this.chargeBeginEffect = UnityFx.laserBreakthroughChargeBegin;
                this.heatColor = this.fromColor = Pal.lancerLaser;
                this.toColor = UnityPal.exp;
                this.shootSound = Sounds.laserblast;
                this.chargeSound = Sounds.lasercharge;
                this.shootType = UnityBullets.breakthroughLaser;
                this.maxLevel = 1;
                this.expScale = 30;
                this.pregrade = (ExpTurret)UnityBlocks.laserCharge;
                this.pregradeLevel = 30;
                this.expFields = new EField[]{new EField.EList((v) -> this.chargeBeginEffect = v, new Effect[]{UnityFx.laserBreakthroughChargeBegin, UnityFx.laserBreakthroughChargeBegin2}, (Stat)null)};
                this.effectColors = new Color[]{Pal.lancerLaser, UnityPal.exp};
                this.drawer = (b) -> {
                    if (b instanceof ExpPowerTurret.ExpPowerTurretBuild) {
                        ExpPowerTurret.ExpPowerTurretBuild tile = (ExpPowerTurret.ExpPowerTurretBuild)b;
                        Draw.rect(this.region, tile.x + this.tr2.x, tile.y + this.tr2.y, tile.rotation - 90.0F);
                        if (tile.level() >= tile.maxLevel()) {
                            Draw.color(tile.shootColor(Tmp.c2));
                            Draw.alpha(Mathf.absin(Time.time, 20.0F, 0.6F));
                            Draw.rect(Regions.btLaserTurretTopRegion, tile.x + this.tr2.x, tile.y + this.tr2.y, tile.rotation - 90.0F);
                            Draw.color();
                        }

                    } else {
                        throw new IllegalStateException("building isn't an instance of ExpPowerTurretBuild");
                    }
                };
            }
        };
        inferno = new ExpItemTurret("inferno") {
            {
                this.requirements(Category.turret, ItemStack.with(new Object[]{UnityItems.stone, 150, UnityItems.denseAlloy, 65, Items.graphite, 60}));
                this.ammo(new Object[]{Items.scrap, Bullets.slagShot, Items.coal, UnityBullets.coalBlaze, Items.pyratite, UnityBullets.pyraBlaze});
                this.size = 3;
                this.range = 80.0F;
                this.reloadTime = 6.0F;
                this.coolantMultiplier = 2.0F;
                this.recoilAmount = 0.0F;
                this.shootCone = 5.0F;
                this.shootSound = Sounds.flame;
                this.maxLevel = 10;
                this.expFields = new EField[]{new EField.EList((v) -> this.shots = v, new Integer[]{1, 1, 2, 2, 2, 3, 3, 4, 4, 5, 5}, Stat.shots), new EField.EList((v) -> this.spread = v, new Float[]{0.0F, 0.0F, 5.0F, 10.0F, 15.0F, 7.0F, 14.0F, 8.0F, 10.0F, 6.0F, 9.0F}, (Stat)null)};
            }
        };
        expHub = new ExpHub("exp-output") {
            {
                this.requirements(Category.effect, ItemStack.with(new Object[]{UnityItems.stone, 30, Items.copper, 15}));
                this.expCapacity = 100;
            }
        };
        expRouter = new ExpRouter("exp-router") {
            {
                this.requirements(Category.effect, ItemStack.with(new Object[]{UnityItems.stone, 5}));
            }
        };
        expTower = new ExpTower("exp-tower") {
            {
                this.requirements(Category.effect, ItemStack.with(new Object[]{UnityItems.denseAlloy, 10, Items.silicon, 5}));
                this.expCapacity = 100;
            }
        };
        expTowerDiagonal = new DiagonalTower("diagonal-tower") {
            {
                this.requirements(Category.effect, ItemStack.with(new Object[]{UnityItems.steel, 10, Items.silicon, 5}));
                this.range = 7;
                this.expCapacity = 150;
            }
        };
        bufferTower = new ExpTower("buffer-tower") {
            {
                this.requirements(Category.effect, ItemStack.with(new Object[]{Items.thorium, 5, Items.graphite, 10}));
                this.manualReload = this.reloadTime = 20.0F;
                this.expCapacity = 180;
                this.buffer = true;
                this.health = 300;
            }
        };
        expNode = new ExpNode("exp-node") {
            {
                this.requirements(Category.effect, ItemStack.with(new Object[]{UnityItems.denseAlloy, 30, Items.silicon, 30, UnityItems.steel, 8}));
                this.expCapacity = 200;
                this.consumes.power(0.6F);
            }
        };
        expNodeLarge = new ExpNode("exp-node-large") {
            {
                this.requirements(Category.effect, ItemStack.with(new Object[]{UnityItems.denseAlloy, 120, Items.silicon, 120, UnityItems.steel, 24}));
                this.expCapacity = 600;
                this.range = 10;
                this.health = 200;
                this.size = 2;
                this.consumes.power(1.4F);
            }
        };
        expTank = new ExpTank("exp-tank") {
            {
                this.requirements(Category.effect, ItemStack.with(new Object[]{Items.copper, 100, UnityItems.denseAlloy, 100, Items.graphite, 30}));
                this.expCapacity = 800;
                this.health = 300;
                this.size = 2;
            }
        };
        expChest = new ExpTank("exp-chest") {
            {
                this.requirements(Category.effect, ItemStack.with(new Object[]{Items.copper, 400, UnityItems.steel, 250, Items.phaseFabric, 120}));
                this.expCapacity = 3600;
                this.health = 1200;
                this.size = 4;
            }
        };
        expFountain = new ExpSource("exp-fountain") {
            {
                this.requirements(Category.effect, BuildVisibility.sandboxOnly, ItemStack.with(new Object[0]));
            }
        };
        expVoid = new ExpVoid("exp-void") {
            {
                this.requirements(Category.effect, BuildVisibility.sandboxOnly, ItemStack.with(new Object[0]));
            }
        };
        buffTurret = new BlockOverdriveTurret("buff-turret") {
            {
                this.requirements(Category.effect, ItemStack.with(new Object[]{Items.thorium, 60, Items.plastanium, 90, UnityItems.stone, 100, UnityItems.denseAlloy, 70}));
                this.health = 200;
                this.size = 1;
                this.buffRange = 100.0F;
                this.consumes.item(UnityItems.steel).boost();
            }
        };
        upgradeTurret = new BlockOverdriveTurret("upgrade-turret") {
            {
                this.requirements(Category.effect, ItemStack.with(new Object[]{Items.surgeAlloy, 80, UnityItems.steel, 120, UnityItems.dirium, 70}));
                this.health = 300;
                this.size = 1;
                this.buffRange = 100.0F;
                this.consumes.item(UnityItems.dirium).boost();
            }
        };
        shieldWall = new ShieldWall("shield-wall") {
            {
                this.requirements(Category.defense, ItemStack.with(new Object[0]));
                this.health = 500;
                this.shieldHealth = 500.0F;
                this.maxDamage = 50.0F;
                this.size = 1;
                this.maxLevel = 10;
                this.expFields = new EField[]{(new EField.ERational((v) -> this.maxDamage = v, 100.0F, 25.0F, -3.0F, Stat.abilities, (v) -> Core.bundle.format("stat.unity.maxdamage", new Object[]{v}))).formatAll(false), (new EField.ELinear((v) -> this.repair = v, 50.0F, 10.0F, Stat.repairSpeed, (v) -> Core.bundle.format("stat.unity.repairspeed", new Object[]{v}))).formatAll(false), new EField.ELinear((v) -> this.shieldHealth = v, 500.0F, 25.0F, Stat.shieldHealth)};
            }
        };
        shieldWallLarge = new ShieldWall("shield-wall-large") {
            {
                this.requirements(Category.defense, ItemStack.with(new Object[0]));
                this.health = 2000;
                this.maxDamage = 100.0F;
                this.shieldHealth = 2000.0F;
                this.size = 2;
                this.maxLevel = 20;
                this.expFields = new EField[]{(new EField.ERational((v) -> this.maxDamage = v, 200.0F, 50.0F, -3.0F, Stat.abilities, (v) -> Core.bundle.format("stat.unity.maxdamage", new Object[]{v}))).formatAll(false), (new EField.ELinear((v) -> this.repair = v, 200.0F, 20.0F, Stat.repairSpeed, (v) -> Core.bundle.format("stat.unity.repairspeed", new Object[]{v}))).formatAll(false), new EField.ELinear((v) -> this.shieldHealth = v, 2000.0F, 50.0F, Stat.shieldHealth)};
            }
        };
        oreMonolite = new UnityOreBlock(UnityItems.monolite) {
            {
                this.oreScale = 23.77F;
                this.oreThreshold = 0.807F;
                this.oreDefault = false;
            }
        };
        sharpslate = new Floor("sharpslate") {
            {
                this.variants = 3;
            }
        };
        infusedSharpslate = new Floor("infused-sharpslate") {
            {
                this.variants = 3;
                this.emitLight = true;
                this.lightRadius = 24.0F;
                this.lightColor = UnityPal.monolith.cpy().a(0.1F);
            }

            public void createIcons(MultiPacker packer) {
                super.createIcons(packer);
                this.mapColor.lerp(UnityPal.monolith, 0.2F);
            }
        };
        archSharpslate = new Floor("archaic-sharpslate") {
            {
                this.variants = 3;
                this.emitLight = true;
                this.lightRadius = 24.0F;
                this.lightColor = UnityPal.monolithLight.cpy().a(0.12F);
            }

            public void createIcons(MultiPacker packer) {
                super.createIcons(packer);
                this.mapColor.lerp(UnityPal.monolith, 0.4F);
            }
        };
        sharpslateWall = new StaticWall("sharpslate-wall") {
            {
                this.variants = 2;
                UnityBlocks.sharpslate.asFloor().wall = this;
            }
        };
        infusedSharpslateWall = new StaticWall("infused-sharpslate-wall") {
            {
                this.variants = 2;
                UnityBlocks.infusedSharpslate.asFloor().wall = this;
                UnityBlocks.archSharpslate.asFloor().wall = this;
            }
        };
        archEnergy = new OverlayFloor("archaic-energy") {
            {
                this.variants = 3;
                this.emitLight = true;
                this.lightRadius = 24.0F;
                this.lightColor = UnityPal.monolithLight.cpy().a(0.24F);
            }
        };
        loreMonolith = new LoreMessageBlock("lore-monolith", Faction.monolith);
        debrisExtractor = new SoulFloorExtractor("debris-extractor") {
            final int effectTimer;

            {
                this.effectTimer = this.timers++;
                this.requirements(Category.crafting, ItemStack.with(new Object[]{UnityItems.monolite, 140, Items.surgeAlloy, 80, Items.thorium, 60}));
                this.setup(new Object[]{UnityBlocks.infusedSharpslate, 0.04F, UnityBlocks.archSharpslate, 0.08F, UnityBlocks.archEnergy, 1.0F});
                this.size = 2;
                this.outputItem = new ItemStack(UnityItems.archDebris, 1);
                this.craftTime = 84.0F;
                this.consumes.power(2.4F);
                this.consumes.liquid(Liquids.cryofluid, 0.08F);
                this.draw((e) -> {
                    Draw.color(UnityPal.monolith, UnityPal.monolithLight, Mathf.absin(Time.time, 6.0F, 1.0F) * e.warmup);
                    Draw.alpha(e.warmup);
                    Draw.rect(Regions.debrisExtractorHeat1Region, e.x, e.y);
                    Draw.color(UnityPal.monolith, UnityPal.monolithLight, Mathf.absin(Time.time + 4.0F, 6.0F, 1.0F) * e.warmup);
                    Draw.alpha(e.warmup);
                    Draw.rect(Regions.debrisExtractorHeat2Region, e.x, e.y);
                    Draw.color();
                    Draw.alpha(1.0F);
                });
                this.update((e) -> {
                    StemData data = e.data();
                    if (e.consValid()) {
                        data.floatValue = Mathf.lerpDelta(data.floatValue, e.efficiency(), 0.02F);
                    } else {
                        data.floatValue = Mathf.lerpDelta(data.floatValue, 0.0F, 0.02F);
                    }

                    if (!Mathf.zero(data.floatValue)) {
                        float f = e.soulf();
                        if (e.timer.get(this.effectTimer, 45.0F - f * 15.0F)) {
                            UnityFx.monolithRingEffect.at(e.x, e.y, (float)e.rotation, data.floatValue / 2.0F);
                        }
                    }

                });
            }
        };
        soulInfuser = new SoulInfuser("soul-infuser") {
            final float[] scales = new float[]{1.0F, 0.9F, 0.7F};
            final Color[] colors;
            final int effectTimer;

            {
                this.colors = new Color[]{UnityPal.monolithDark, UnityPal.monolith, UnityPal.monolithLight};
                this.effectTimer = this.timers++;
                this.requirements(Category.crafting, ItemStack.with(new Object[]{UnityItems.monolite, 200, Items.titanium, 250, Items.silicon, 420}));
                this.setup(new Object[]{UnityBlocks.infusedSharpslate, 0.6F, UnityBlocks.archSharpslate, 1.0F, UnityBlocks.archEnergy, 1.4F});
                this.size = 3;
                this.craftTime = 60.0F;
                this.updateEffect = Fx.smeltsmoke;
                this.craftEffect = Fx.producesmoke;
                this.requireSoul = false;
                this.consumes.power(3.2F);
                this.consumes.liquid(Liquids.cryofluid, 0.2F);
                this.drawer = new DrawGlow();
                this.draw((e) -> {
                    float z = Draw.z();
                    Draw.z(110.0F);

                    for(int i = 0; i < this.colors.length; ++i) {
                        Color color = this.colors[i];
                        float scl = e.warmup * 4.0F * this.scales[i];
                        Draw.color(color);
                        UnityDrawf.shiningCircle(e.id, Time.time + (float)i, e.x, e.y, scl, 3, 20.0F, scl * 2.0F, scl * 2.0F, 60.0F);
                    }

                    Draw.z(z);
                });
                this.update((e) -> {
                    StemData data = e.data();
                    if (e.consValid()) {
                        data.floatValue = Mathf.lerpDelta(data.floatValue, e.efficiency(), 0.02F);
                    } else {
                        data.floatValue = Mathf.lerpDelta(data.floatValue, 0.0F, 0.02F);
                    }

                    if (!Mathf.zero(data.floatValue)) {
                        float f = e.soulf();
                        if (e.timer.get(this.effectTimer, 45.0F - f * 15.0F)) {
                            UnityFx.monolithRingEffect.at(e.x, e.y, (float)e.rotation, data.floatValue * 3.0F / 4.0F);
                        }

                        if (Mathf.chanceDelta((double)(data.floatValue * 0.5F))) {
                            Lightning.create(e.team, Pal.lancerLaser, 1.0F, e.x, e.y, Mathf.randomSeed((long)((int)Time.time + e.id), 360.0F), (int)(data.floatValue * 3.0F) + Mathf.random(3));
                        }
                    }

                });
            }
        };
        monolithAlloyForge = new SoulGenericCrafter("monolith-alloy-forge") {
            final int effectTimer;

            {
                this.effectTimer = this.timers++;
                this.requirements(Category.crafting, ItemStack.with(new Object[]{Items.lead, 380, UnityItems.monolite, 240, Items.silicon, 400, Items.titanium, 240, Items.thorium, 90, Items.surgeAlloy, 160}));
                this.outputItem = new ItemStack(UnityItems.monolithAlloy, 3);
                this.size = 4;
                this.ambientSound = Sounds.machine;
                this.ambientSoundVolume = 0.6F;
                this.drawer = new DrawSmelter(UnityPal.monolithLight) {
                    {
                        this.flameRadius = 5.0F;
                        this.flameRadiusIn = 2.6F;
                    }
                };
                this.consumes.power(3.6F);
                this.consumes.items(ItemStack.with(new Object[]{Items.silicon, 3, UnityItems.archDebris, 1, UnityItems.monolite, 2}));
                this.consumes.liquid(Liquids.cryofluid, 0.1F);
                this.update((e) -> {
                    StemData data = e.data();
                    if (e.consValid()) {
                        data.floatValue = Mathf.lerpDelta(data.floatValue, e.efficiency(), 0.02F);
                    } else {
                        data.floatValue = Mathf.lerpDelta(data.floatValue, 0.0F, 0.02F);
                    }

                    if (!Mathf.zero(data.floatValue)) {
                        float f = e.soulf();
                        if (e.timer.get(this.effectTimer, 45.0F - f * 15.0F)) {
                            UnityFx.monolithRingEffect.at(e.x, e.y, (float)e.rotation, data.floatValue);
                        }

                        if (Mathf.chanceDelta((double)(data.floatValue * 0.5F))) {
                            Lightning.create(e.team, Pal.lancerLaser, 1.0F, e.x, e.y, Mathf.randomSeed((long)((int)Time.time + e.id), 360.0F), (int)(data.floatValue * 4.0F) + Mathf.random(3));
                        }
                    }

                });
            }
        };
        electrophobicWall = new PowerWall("electrophobic-wall") {
            {
                this.requirements(Category.defense, ItemStack.with(new Object[]{UnityItems.monolite, 4, Items.silicon, 2}));
                this.size = 1;
                this.health = 400;
                this.energyMultiplier.put(LightningBulletType.class, 15.0F);
                this.energyMultiplier.put(LaserBulletType.class, 9.0F);
                this.energyMultiplier.put(ContinuousLaserBulletType.class, 12.0F);
                this.energyMultiplier.put(LaserBoltBulletType.class, 9.0F);
            }
        };
        electrophobicWallLarge = new PowerWall("electrophobic-wall-large") {
            {
                this.requirements(Category.defense, ItemStack.with(new Object[]{UnityItems.monolite, 16, Items.silicon, 8}));
                this.size = 2;
                this.health = 1600;
                this.powerProduction = 4.0F;
                this.damageThreshold = 300.0F;
                this.energyMultiplier.put(LightningBulletType.class, 15.0F);
                this.energyMultiplier.put(LaserBulletType.class, 9.0F);
                this.energyMultiplier.put(ContinuousLaserBulletType.class, 12.0F);
                this.energyMultiplier.put(LaserBoltBulletType.class, 9.0F);
            }
        };
        ricochet = new SoulTurretPowerTurret("ricochet") {
            {
                this.requirements(Category.turret, ItemStack.with(new Object[]{UnityItems.monolite, 40}));
                this.size = 1;
                this.health = 200;
                this.powerUse = 1.0F;
                this.reloadTime = 60.0F;
                this.restitution = 0.03F;
                this.range = 180.0F;
                this.shootCone = 15.0F;
                this.ammoUseEffect = Fx.none;
                this.inaccuracy = 2.0F;
                this.rotateSpeed = 12.0F;
                this.shootSound = UnitySounds.energyBolt;
                this.shootType = UnityBullets.ricochetSmall.copy();
                this.requireSoul = false;
                this.efficiencyFrom = 0.8F;
                this.efficiencyTo = 1.5F;
                float base = this.shootType.damage;
                this.progression.linear(this.efficiencyFrom, (this.efficiencyTo - this.efficiencyFrom) / (float)this.maxSouls, (f) -> this.shootType.damage = base * f);
            }
        };
        diviner = new SoulTurretPowerTurret("diviner") {
            {
                this.requirements(Category.turret, ItemStack.with(new Object[]{Items.lead, 15, UnityItems.monolite, 30}));
                this.size = 1;
                this.health = 240;
                this.powerUse = 1.5F;
                this.reloadTime = 30.0F;
                this.range = 75.0F;
                this.targetGround = true;
                this.targetAir = false;
                this.shootSound = UnitySounds.energyBolt;
                this.shootType = new LaserBulletType(200.0F) {
                    {
                        this.length = 85.0F;
                    }
                };
                this.requireSoul = false;
                this.efficiencyFrom = 0.8F;
                this.efficiencyTo = 1.5F;
                float base = this.shootType.damage;
                this.progression.linear(this.efficiencyFrom, (this.efficiencyTo - this.efficiencyFrom) / (float)this.maxSouls, (f) -> this.shootType.damage = base * f);
            }
        };
        lifeStealer = new SoulLifeStealerTurret("life-stealer") {
            {
                this.requirements(Category.turret, ItemStack.with(new Object[]{Items.silicon, 50, UnityItems.monolite, 25}));
                this.size = 1;
                this.health = 320;
                this.powerUse = 1.0F;
                this.damage = 120.0F;
                this.requireSoul = false;
                this.efficiencyFrom = 0.8F;
                this.efficiencyTo = 1.5F;
                this.laserAlpha((b) -> b.power.status * (0.7F + b.soulf() * 0.3F));
            }
        };
        recluse = new SoulTurretItemTurret("recluse") {
            {
                this.requirements(Category.turret, ItemStack.with(new Object[]{Items.lead, 15, UnityItems.monolite, 20}));
                this.ammo(new Object[]{Items.lead, UnityBullets.stopLead.copy(), UnityItems.monolite, UnityBullets.stopMonolite.copy(), Items.silicon, UnityBullets.stopSilicon.copy()});
                this.size = 1;
                this.health = 200;
                this.spread = 4.0F;
                this.reloadTime = 20.0F;
                this.restitution = 0.03F;
                this.range = 110.0F;
                this.shootCone = 3.0F;
                this.ammoUseEffect = Fx.none;
                this.rotateSpeed = 12.0F;
                this.requireSoul = false;
                this.efficiencyFrom = 0.8F;
                this.efficiencyTo = 1.5F;
                ObjectMap.Values var2 = this.ammoTypes.values().iterator();

                while(var2.hasNext()) {
                    BulletType b = (BulletType)var2.next();
                    float base = b.damage;
                    this.progression.linear(this.efficiencyFrom, (this.efficiencyTo - this.efficiencyFrom) / (float)this.maxSouls, (f) -> b.damage = base * f);
                }

            }
        };
        absorberAura = new SoulAbsorberTurret("absorber-aura") {
            {
                this.requirements(Category.turret, ItemStack.with(new Object[]{Items.silicon, 75, UnityItems.monolite, 125}));
                this.size = 2;
                this.health = 720;
                this.range = 150.0F;
                this.powerUse = 1.0F;
                this.resistance = 0.8F;
                this.targetBullets = true;
                this.requireSoul = false;
                this.efficiencyFrom = 0.8F;
                this.efficiencyTo = 1.6F;
                this.laserAlpha((b) -> b.power.status * (0.7F + b.soulf() * 0.3F));
            }
        };
        mage = new SoulTurretPowerTurret("mage") {
            {
                this.requirements(Category.turret, ItemStack.with(new Object[]{Items.lead, 75, Items.silicon, 50, UnityItems.monolite, 25}));
                this.size = 2;
                this.health = 600;
                this.powerUse = 2.5F;
                this.range = 120.0F;
                this.reloadTime = 48.0F;
                this.shootCone = 15.0F;
                this.shots = 3;
                this.burstSpacing = 2.0F;
                this.shootSound = Sounds.spark;
                this.recoilAmount = 2.5F;
                this.rotateSpeed = 10.0F;
                this.shootType = new LightningBulletType() {
                    {
                        this.lightningLength = 20;
                        this.damage = 128.0F;
                    }
                };
                this.requireSoul = false;
                this.maxSouls = 5;
                this.efficiencyFrom = 0.8F;
                this.efficiencyTo = 1.6F;
                float base = this.shootType.damage;
                this.progression.linear(this.efficiencyFrom, (this.efficiencyTo - this.efficiencyFrom) / (float)this.maxSouls, (f) -> this.shootType.damage = base * f);
            }
        };
        blackout = new SoulTurretPowerTurret("blackout") {
            {
                this.requirements(Category.turret, ItemStack.with(new Object[]{Items.graphite, 85, Items.titanium, 25, UnityItems.monolite, 125}));
                this.size = 2;
                this.health = 720;
                this.powerUse = 3.0F;
                this.reloadTime = 140.0F;
                this.range = 200.0F;
                this.rotateSpeed = 10.0F;
                this.recoilAmount = 3.0F;
                this.shootSound = Sounds.shootBig;
                this.targetGround = true;
                this.targetAir = false;
                this.shootType = new BasicBulletType(6.0F, 180.0F, "shell") {
                    {
                        this.lifetime = 35.0F;
                        this.width = this.height = 20.0F;
                        this.frontColor = UnityPal.monolith;
                        this.backColor = UnityPal.monolithDark;
                        this.hitEffect = this.despawnEffect = Fx.blastExplosion;
                        this.splashDamage = 90.0F;
                        this.splashDamageRadius = 25.6F;
                    }

                    public void hitEntity(Bullet b, Hitboxc other, float initialHealth) {
                        super.hitEntity(b, other, initialHealth);
                        float r = this.splashDamageRadius;
                        Units.nearbyEnemies(b.team, b.x - r, b.y - r, r * 2.0F, r * 2.0F, (unit) -> {
                            if (unit.within(b, r)) {
                                unit.apply(StatusEffects.unmoving, 60.0F);
                                unit.apply(StatusEffects.disarmed, 60.0F);
                            }

                        });
                    }
                };
                this.requireSoul = false;
                this.maxSouls = 5;
                this.efficiencyFrom = 0.8F;
                this.efficiencyTo = 1.6F;
                float base = this.shootType.damage;
                this.progression.linear(this.efficiencyFrom, (this.efficiencyTo - this.efficiencyFrom) / (float)this.maxSouls, (f) -> this.shootType.damage = base * f);
            }
        };
        shellshock = new SoulTurretPowerTurret("shellshock") {
            {
                this.requirements(Category.turret, ItemStack.with(new Object[]{Items.lead, 90, Items.graphite, 100, UnityItems.monolite, 80}));
                this.size = 2;
                this.health = 720;
                this.powerUse = 2.0F;
                this.reloadTime = 75.0F;
                this.range = 260.0F;
                this.shootCone = 3.0F;
                this.ammoUseEffect = Fx.none;
                this.rotateSpeed = 10.0F;
                this.shootType = UnityBullets.ricochetMedium.copy();
                this.shootSound = UnitySounds.energyBolt;
                this.requireSoul = false;
                this.maxSouls = 5;
                this.efficiencyFrom = 0.8F;
                this.efficiencyTo = 1.6F;
                float base = this.shootType.damage;
                this.progression.linear(this.efficiencyFrom, (this.efficiencyTo - this.efficiencyFrom) / (float)this.maxSouls, (f) -> this.shootType.damage = base * f);
            }
        };
        heatRay = new SoulHeatRayTurret("heat-ray") {
            {
                this.requirements(Category.turret, ItemStack.with(new Object[]{Items.copper, 75, Items.lead, 50, Items.graphite, 25, Items.titanium, 45, UnityItems.monolite, 50}));
                this.size = 2;
                this.range = 120.0F;
                this.targetGround = true;
                this.targetAir = false;
                this.damage = 240.0F;
                this.powerUse = 2.0F;
                this.shootSound = UnitySounds.heatRay;
                this.requireSoul = false;
                this.maxSouls = 5;
                this.efficiencyFrom = 0.8F;
                this.efficiencyTo = 1.6F;
                this.laserAlpha((b) -> b.power.status * (0.7F + b.soulf() * 0.3F));
            }
        };
        oracle = new SoulTurretBurstPowerTurret("oracle") {
            {
                this.requirements(Category.turret, ItemStack.with(new Object[]{Items.silicon, 175, Items.titanium, 150, UnityItems.monolithAlloy, 75}));
                this.size = 3;
                this.health = 1440;
                this.powerUse = 3.0F;
                this.range = 180.0F;
                this.reloadTime = 72.0F;
                this.chargeTime = 30.0F;
                this.chargeMaxDelay = 4.0F;
                this.chargeEffects = 12;
                this.shootCone = 5.0F;
                this.shots = 8;
                this.burstSpacing = 2.0F;
                this.chargeEffect = UnityFx.oracleCharge;
                this.chargeBeginEffect = UnityFx.oracleChargeBegin;
                this.shootSound = Sounds.spark;
                this.shootShake = 3.0F;
                this.recoilAmount = 2.5F;
                this.rotateSpeed = 8.0F;
                this.shootType = new LightningBulletType() {
                    {
                        this.damage = 192.0F;
                        this.shootEffect = Fx.lightningShoot;
                    }
                };
                this.subShots = 3;
                this.subBurstSpacing = 1.0F;
                this.subShootEffect = Fx.hitLancer;
                this.subShootSound = Sounds.laser;
                this.subShootType = new LaserBulletType(288.0F) {
                    {
                        this.length = 180.0F;
                        this.sideAngle = 45.0F;
                        this.inaccuracy = 8.0F;
                    }
                };
                this.requireSoul = false;
                this.maxSouls = 7;
                this.efficiencyFrom = 0.7F;
                this.efficiencyTo = 1.67F;
                float base = this.shootType.damage;
                this.progression.linear(this.efficiencyFrom, (this.efficiencyTo - this.efficiencyFrom) / (float)this.maxSouls, (f) -> this.shootType.damage = base * f);
            }
        };
        purge = new SoulTurretPowerTurret("purge") {
            {
                this.requirements(Category.turret, ItemStack.with(new Object[]{Items.plastanium, 75, Items.lead, 350, UnityItems.monolite, 200, UnityItems.monolithAlloy, 75}));
                this.size = 3;
                this.health = 1680;
                this.powerUse = 3.0F;
                this.reloadTime = 90.0F;
                this.range = 360.0F;
                this.shootCone = 3.0F;
                this.ammoUseEffect = Fx.none;
                this.rotateSpeed = 8.0F;
                this.shootType = UnityBullets.ricochetBig.copy();
                this.shootSound = UnitySounds.energyBolt;
                this.requireSoul = false;
                this.maxSouls = 7;
                this.efficiencyFrom = 0.7F;
                this.efficiencyTo = 1.67F;
                float base = this.shootType.damage;
                this.progression.linear(this.efficiencyFrom, (this.efficiencyTo - this.efficiencyFrom) / (float)this.maxSouls, (f) -> this.shootType.damage = base * f);
            }
        };
        incandescence = new SoulHeatRayTurret("incandescence") {
            {
                this.requirements(Category.turret, ItemStack.with(new Object[]{UnityItems.monolite, 250, Items.phaseFabric, 45, UnityItems.monolithAlloy, 100}));
                this.size = 3;
                this.health = 1680;
                this.range = 180.0F;
                this.targetGround = true;
                this.targetAir = true;
                this.damage = 480.0F;
                this.powerUse = 4.0F;
                this.shootSound = UnitySounds.heatRay;
                this.laserWidth = 0.54F;
                this.shootLength = 6.0F;
                this.requireSoul = false;
                this.maxSouls = 7;
                this.efficiencyFrom = 0.7F;
                this.efficiencyTo = 1.67F;
                this.laserAlpha((b) -> b.power.status * (0.7F + b.soulf() * 0.3F));
            }
        };
        prism = new PrismTurret("prism") {
            {
                this.requirements(Category.turret, ItemStack.with(new Object[]{Items.copper, 1}));
                this.size = 4;
                this.health = 2800;
                this.range = 320.0F;
                this.reloadTime = 60.0F;
                this.rotateSpeed = 20.0F;
                this.recoilAmount = 6.0F;
                this.prismOffset = 6.0F;
                this.shootCone = 30.0F;
                this.targetGround = true;
                this.targetAir = true;
                this.shootSound = Sounds.shotgun;
                this.shootEffect = Fx.hitLaserBlast;
                this.model = UnityModels.prism;
                this.powerUse = 8.0F;
                this.requireSoul = false;
                this.maxSouls = 7;
                this.efficiencyFrom = 0.7F;
                this.efficiencyTo = 1.67F;
                this.shootType = new BulletType(1.0E-4F, 320.0F);
                float base = this.shootType.damage;
                this.progression.linear(this.efficiencyFrom, (this.efficiencyTo - this.efficiencyFrom) / (float)this.maxSouls, (f) -> this.shootType.damage = base * f);
            }
        };
        supernova = new SupernovaTurret("supernova") {
            {
                this.requirements(Category.turret, ItemStack.with(new Object[]{Items.surgeAlloy, 500, Items.silicon, 650, UnityItems.archDebris, 350, UnityItems.monolithAlloy, 325}));
                this.size = 7;
                this.health = 8100;
                this.powerUse = 24.0F;
                this.shootLength = (float)(this.size * 8) / 2.0F - 8.0F;
                this.rotateSpeed = 1.0F;
                this.recoilAmount = 4.0F;
                this.cooldown = 0.006F;
                this.shootCone = 15.0F;
                this.range = 250.0F;
                this.shootSound = UnitySounds.supernovaShoot;
                this.loopSound = UnitySounds.supernovaActive;
                this.loopSoundVolume = 1.0F;
                this.baseExplosiveness = 25.0F;
                this.shootDuration = 480.0F;
                this.shootType = UnityBullets.supernovaLaser.copy();
                this.chargeBeginEffect = UnityFx.supernovaChargeBegin;
                this.requireSoul = false;
                this.maxSouls = 12;
                this.efficiencyFrom = 0.7F;
                this.efficiencyTo = 1.8F;
                float base = this.shootType.damage;
                this.progression.linear(this.efficiencyFrom, (this.efficiencyTo - this.efficiencyFrom) / (float)this.maxSouls, (f) -> this.shootType.damage = base * f);
            }
        };
        concreteBlank = new Floor("concrete-blank");
        concreteFill = new Floor("concrete-fill") {
            {
                this.variants = 0;
            }
        };
        concreteNumber = new Floor("concrete-number") {
            {
                this.variants = 10;
            }
        };
        concreteStripe = new Floor("concrete-stripe");
        concrete = new Floor("concrete");
        stoneFullTiles = new Floor("stone-full-tiles");
        stoneFull = new Floor("stone-full");
        stoneHalf = new Floor("stone-half");
        stoneTiles = new Floor("stone-tiles");
        smallTurret = new ModularTurret("small-turret-base") {
            {
                this.requirements(Category.turret, ItemStack.with(new Object[]{Items.graphite, 20, UnityItems.nickel, 30, Items.copper, 30}));
                this.size = 2;
                this.health = 1200;
                this.setGridW(3);
                this.setGridH(3);
                this.spriteGridSize = 18;
                this.spriteGridPadding = 3;
                this.yScale = 0.8F;
                this.addGraph((new GraphTorque(0.03F, 50.0F)).setAccept(new int[]{1, 1, 0, 0, 0, 0, 0, 0, 0, 0}));
                this.addGraph((new GraphHeat(50.0F, 0.1F, 0.01F)).setAccept(new int[]{1, 1, 1, 1, 1, 1, 1, 1}));
            }
        };
        medTurret = new ModularTurret("med-turret-base") {
            {
                this.requirements(Category.turret, ItemStack.with(new Object[]{Items.graphite, 25, UnityItems.nickel, 30, Items.titanium, 50, Items.silicon, 40}));
                this.size = 3;
                this.health = 1200;
                this.acceptsItems = true;
                this.setGridW(5);
                this.setGridH(5);
                this.spriteGridSize = 16;
                this.spriteGridPadding = 4;
                this.yShift = 0.8F;
                this.yScale = 0.8F;
                this.partCostAccum = 0.12F;
                this.addGraph((new GraphTorque(0.05F, 150.0F)).setAccept(new int[]{0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}));
                this.addGraph((new GraphHeat(120.0F, 0.05F, 0.02F)).setAccept(new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}));
            }
        };
        chopper = new Chopper("chopper") {
            {
                this.requirements(Category.turret, ItemStack.with(new Object[]{UnityItems.nickel, 50, Items.titanium, 50, Items.lead, 30}));
                this.health = 650;
                this.setGridW(7);
                this.setGridH(1);
                this.addPart(Core.bundle.get("part.unity.pivot.name"), Core.bundle.get("part.unity.pivot.info"), PartType.blade, 4, 0, 1, 1, true, true, new Point2(0, 0), new ItemStack[0], new byte[]{1, 0, 0, 0}, new byte[]{0, 0, 0, 0}, new PartStat[]{new PartStat(PartStatType.mass, 1), new PartStat(PartStatType.collides, false), new PartStat(PartStatType.hp, 10)});
                this.addPart(Core.bundle.get("part.unity.blade.name"), Core.bundle.get("part.unity.blade.info"), PartType.blade, 0, 0, 1, 1, ItemStack.with(new Object[]{UnityItems.nickel, 3, Items.titanium, 5}), new byte[]{1, 0, 0, 0}, new byte[]{0, 0, 1, 0}, new PartStat[]{new PartStat(PartStatType.mass, 2), new PartStat(PartStatType.collides, true), new PartStat(PartStatType.hp, 80), new PartStat(PartStatType.damage, 5)});
                this.addPart(Core.bundle.get("part.unity.serrated-blade.name"), Core.bundle.get("part.unity.serrated-blade.info"), PartType.blade, 2, 0, 2, 1, ItemStack.with(new Object[]{UnityItems.nickel, 8, Items.lead, 5}), new byte[]{1, 0, 0, 0, 0, 0}, new byte[]{0, 0, 0, 1, 0, 0}, new PartStat[]{new PartStat(PartStatType.mass, 6), new PartStat(PartStatType.collides, true), new PartStat(PartStatType.hp, 120), new PartStat(PartStatType.damage, 12)});
                this.addPart(Core.bundle.get("part.unity.rod.name"), Core.bundle.get("part.unity.rod.info"), PartType.blade, 1, 0, 1, 1, ItemStack.with(new Object[]{Items.titanium, 3}), new byte[]{1, 0, 0, 0}, new byte[]{0, 0, 1, 0}, new PartStat[]{new PartStat(PartStatType.mass, 1), new PartStat(PartStatType.collides, false), new PartStat(PartStatType.hp, 40)});
                this.addGraph((new GraphTorque(0.03F, 5.0F)).setAccept(new int[]{1, 0, 0, 0}));
            }
        };
        augerDrill = new AugerDrill("auger-drill") {
            {
                this.requirements(Category.production, ItemStack.with(new Object[]{Items.lead, 100, Items.copper, 75}));
                this.size = 3;
                this.health = 1000;
                this.tier = 3;
                this.drillTime = 400.0F;
                this.addGraph((new GraphTorqueConsume(45.0F, 8.0F, 0.03F, 0.15F)).setAccept(new int[]{0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0}));
            }
        };
        mechanicalExtractor = new MechanicalExtractor("mechanical-extractor") {
            {
                this.requirements(Category.production, ItemStack.with(new Object[]{Items.lead, 100, Items.copper, 75}));
                this.hasPower = false;
                this.size = 3;
                this.health = 1000;
                this.pumpAmount = 0.4F;
                this.addGraph((new GraphTorqueConsume(45.0F, 8.0F, 0.06F, 0.3F)).setAccept(new int[]{0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0}));
            }
        };
        sporeFarm = new SporeFarm("spore-farm") {
            {
                this.requirements(Category.production, ItemStack.with(new Object[]{Items.lead, 5}));
                this.health = 50;
                this.rebuildable = false;
                this.hasItems = true;
                this.itemCapacity = 2;
                this.buildCostMultiplier = 0.01F;
                this.breakSound = Sounds.splash;
            }
        };
        mechanicalConveyor = new ShadowedConveyor("mechanical-conveyor") {
            {
                this.requirements(Category.distribution, ItemStack.with(new Object[]{Items.copper, 3, UnityItems.nickel, 2}));
                this.health = 250;
                this.speed = 0.1F;
            }
        };
        heatPipe = new HeatPipe("heat-pipe") {
            {
                this.requirements(Category.distribution, ItemStack.with(new Object[]{Items.copper, 15, UnityItems.cupronickel, 10, UnityItems.nickel, 5}));
                this.health = 140;
                this.addGraph((new GraphHeat(5.0F, 0.7F, 0.008F)).setAccept(new int[]{1, 1, 1, 1}));
            }
        };
        driveShaft = new DriveShaft("drive-shaft") {
            {
                this.requirements(Category.distribution, ItemStack.with(new Object[]{Items.copper, 10, Items.lead, 10}));
                this.health = 150;
                this.addGraph((new GraphTorque(0.01F, 3.0F)).setAccept(new int[]{1, 0, 1, 0}));
            }
        };
        inlineGearbox = new InlineGearbox("inline-gearbox") {
            {
                this.requirements(Category.distribution, ItemStack.with(new Object[]{Items.titanium, 20, Items.lead, 30, Items.copper, 30}));
                this.size = 2;
                this.health = 700;
                this.addGraph((new GraphTorque(0.02F, 20.0F)).setAccept(new int[]{1, 1, 0, 0, 1, 1, 0, 0}));
            }
        };
        shaftRouter = new GraphBlock("shaft-router") {
            {
                this.requirements(Category.distribution, ItemStack.with(new Object[]{Items.copper, 20, Items.lead, 20}));
                this.health = 100;
                this.preserveDraw = true;
                this.addGraph((new GraphTorque(0.05F, 5.0F)).setAccept(new int[]{1, 1, 1, 1}));
            }
        };
        simpleTransmission = new SimpleTransmission("simple-transmission") {
            {
                this.requirements(Category.distribution, ItemStack.with(new Object[]{Items.titanium, 50, Items.lead, 50, Items.copper, 50}));
                this.size = 2;
                this.health = 500;
                this.addGraph((new GraphTorqueTrans(0.05F, 25.0F)).setRatio(1.0F, 2.5F).setAccept(new int[]{2, 1, 0, 0, 1, 2, 0, 0}));
            }
        };
        crucible = new Crucible("crucible") {
            {
                this.requirements(Category.crafting, ItemStack.with(new Object[]{UnityItems.nickel, 10, Items.titanium, 15}));
                this.health = 400;
                this.addGraph((new GraphCrucible()).setAccept(new int[]{1, 1, 1, 1}));
                this.addGraph((new GraphHeat(75.0F, 0.2F, 0.006F)).setAccept(new int[]{1, 1, 1, 1}));
            }
        };
        holdingCrucible = new HoldingCrucible("holding-crucible") {
            {
                this.requirements(Category.crafting, ItemStack.with(new Object[]{UnityItems.nickel, 50, UnityItems.cupronickel, 150, Items.metaglass, 150, Items.titanium, 30}));
                this.size = 4;
                this.health = 2400;
                this.addGraph((new GraphCrucible(50.0F, false)).setAccept(new int[]{0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0}));
                this.addGraph((new GraphHeat(275.0F, 0.05F, 0.01F)).setAccept(new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}));
            }
        };
        cruciblePump = new CruciblePump("crucible-pump") {
            {
                this.requirements(Category.crafting, ItemStack.with(new Object[]{UnityItems.cupronickel, 50, UnityItems.nickel, 50, Items.metaglass, 15}));
                this.size = 2;
                this.health = 500;
                this.consumes.power(1.0F);
                this.addGraph((new GraphCrucible(10.0F, false)).setAccept(new int[]{1, 1, 0, 0, 2, 2, 0, 0}).multi());
                this.addGraph((new GraphHeat(50.0F, 0.1F, 0.003F)).setAccept(new int[]{1, 1, 1, 1, 1, 1, 1, 1}));
            }
        };
        castingMold = new CastingMold("casting-mold") {
            {
                this.requirements(Category.crafting, ItemStack.with(new Object[]{Items.titanium, 70, UnityItems.nickel, 30}));
                this.size = 2;
                this.health = 700;
                this.addGraph((new GraphCrucible(2.0F, false)).setAccept(new int[]{0, 0, 0, 0, 1, 1, 0, 0}));
                this.addGraph((new GraphHeat(55.0F, 0.2F, 0.0F)).setAccept(new int[]{1, 1, 1, 1, 1, 1, 1, 1}));
            }
        };
        sporePyrolyser = new SporePyrolyser("spore-pyrolyser") {
            {
                this.requirements(Category.crafting, ItemStack.with(new Object[]{UnityItems.nickel, 25, Items.titanium, 50, Items.copper, 50, Items.lead, 30}));
                this.size = 3;
                this.health = 1100;
                this.craftTime = 50.0F;
                this.outputItem = new ItemStack(Items.coal, 3);
                this.ambientSound = Sounds.machine;
                this.ambientSoundVolume = 0.6F;
                this.consumes.item(Items.sporePod, 1);
                this.addGraph((new GraphHeat(60.0F, 0.4F, 0.008F)).setAccept(new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}));
            }
        };
        smallRadiator = new GraphBlock("small-radiator") {
            {
                this.requirements(Category.power, ItemStack.with(new Object[]{Items.copper, 30, UnityItems.cupronickel, 20, UnityItems.nickel, 15}));
                this.health = 200;
                this.solid = true;
                this.addGraph((new GraphHeat(10.0F, 0.7F, 0.05F)).setAccept(new int[]{1, 1, 1, 1}));
            }
        };
        thermalHeater = new ThermalHeater("thermal-heater") {
            {
                this.requirements(Category.power, ItemStack.with(new Object[]{Items.copper, 150, UnityItems.nickel, 100, Items.titanium, 150}));
                this.size = 2;
                this.health = 500;
                this.maxTemp = 1100.0F;
                this.mulCoeff = 0.11F;
                this.addGraph((new GraphHeat(40.0F, 0.6F, 0.004F)).setAccept(new int[]{1, 1, 0, 0, 0, 0, 0, 0}));
            }
        };
        combustionHeater = new CombustionHeater("combustion-heater") {
            {
                this.requirements(Category.power, ItemStack.with(new Object[]{Items.copper, 100, UnityItems.nickel, 70, Items.graphite, 40, Items.titanium, 80}));
                this.size = 2;
                this.health = 550;
                this.itemCapacity = 5;
                this.maxTemp = 1200.0F;
                this.mulCoeff = 0.45F;
                this.addGraph((new GraphHeat(40.0F, 0.6F, 0.004F)).setAccept(new int[]{1, 1, 0, 0, 0, 0, 0, 0}));
            }
        };
        solarCollector = new SolarCollector("solar-collector") {
            {
                this.requirements(Category.power, ItemStack.with(new Object[]{UnityItems.nickel, 80, Items.titanium, 50, Items.lead, 30}));
                this.size = 3;
                this.health = 1500;
                this.maxTemp = 800.0F;
                this.mulCoeff = 0.03F;
                this.addGraph((new GraphHeat(60.0F, 1.0F, 0.02F)).setAccept(new int[]{0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0}));
            }
        };
        solarReflector = new SolarReflector("solar-reflector") {
            {
                this.requirements(Category.power, ItemStack.with(new Object[]{UnityItems.nickel, 25, Items.copper, 50}));
                this.size = 2;
                this.health = 800;
            }
        };
        nickelStator = new Magnet("nickel-stator") {
            {
                this.requirements(Category.power, ItemStack.with(new Object[]{UnityItems.nickel, 30, Items.titanium, 20}));
                this.health = 450;
                this.addGraph((new GraphFlux(2.0F)).setAccept(new int[]{1, 0, 0, 0}));
            }
        };
        nickelStatorLarge = new Magnet("nickel-stator-large") {
            {
                this.requirements(Category.power, ItemStack.with(new Object[]{UnityItems.nickel, 250, Items.titanium, 150}));
                this.size = 2;
                this.health = 1800;
                this.addGraph((new GraphFlux(10.0F)).setAccept(new int[]{1, 1, 0, 0, 0, 0, 0, 0}));
            }
        };
        nickelElectromagnet = new Magnet("nickel-electromagnet") {
            {
                this.requirements(Category.power, ItemStack.with(new Object[]{UnityItems.nickel, 250, Items.titanium, 200, Items.copper, 100, UnityItems.cupronickel, 50}));
                this.size = 2;
                this.health = 1000;
                this.consumes.power(1.6F);
                this.addGraph((new GraphFlux(25.0F)).setAccept(new int[]{1, 1, 0, 0, 0, 0, 0, 0}));
            }
        };
        electricRotorSmall = new RotorBlock("electric-rotor-small") {
            {
                this.requirements(Category.power, ItemStack.with(new Object[]{UnityItems.nickel, 30, Items.copper, 50, Items.titanium, 10}));
                this.health = 120;
                this.powerProduction = 2.0F;
                this.fluxEfficiency = 10.0F;
                this.rotPowerEfficiency = 0.8F;
                this.torqueEfficiency = 0.7F;
                this.baseTorque = 1.0F;
                this.baseTopSpeed = 3.0F;
                this.consumes.power(1.0F);
                this.addGraph((new GraphFlux(false)).setAccept(new int[]{0, 1, 0, 1}));
                this.addGraph((new GraphTorque(0.08F, 20.0F)).setAccept(new int[]{1, 0, 1, 0}));
            }
        };
        electricRotor = new RotorBlock("electric-rotor") {
            {
                this.requirements(Category.power, ItemStack.with(new Object[]{UnityItems.nickel, 200, Items.copper, 200, Items.titanium, 150, Items.graphite, 100}));
                this.size = 3;
                this.health = 1000;
                this.powerProduction = 32.0F;
                this.big = true;
                this.fluxEfficiency = 10.0F;
                this.rotPowerEfficiency = 0.8F;
                this.torqueEfficiency = 0.8F;
                this.baseTorque = 5.0F;
                this.baseTopSpeed = 15.0F;
                this.consumes.power(16.0F);
                this.addGraph((new GraphFlux(false)).setAccept(new int[]{0, 0, 0, 1, 1, 1, 0, 0, 0, 1, 1, 1}));
                this.addGraph((new GraphTorque(0.05F, 150.0F)).setAccept(new int[]{0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0}));
            }
        };
        handCrank = new HandCrank("hand-crank") {
            {
                this.requirements(Category.power, ItemStack.with(new Object[]{UnityItems.nickel, 5, Items.lead, 20}));
                this.health = 120;
                this.addGraph((new GraphTorque(0.01F, 3.0F)).setAccept(new int[]{1, 0, 0, 0}));
            }
        };
        windTurbine = new WindTurbine("wind-turbine") {
            {
                this.requirements(Category.power, ItemStack.with(new Object[]{Items.titanium, 20, Items.lead, 80, Items.copper, 70}));
                this.size = 3;
                this.health = 1200;
                this.addGraph((new GraphTorqueGenerate(0.03F, 20.0F, 5.0F, 5.0F)).setAccept(new int[]{0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}));
            }
        };
        waterTurbine = new WaterTurbine("water-turbine") {
            {
                this.requirements(Category.power, ItemStack.with(new Object[]{Items.metaglass, 50, UnityItems.nickel, 20, Items.lead, 150, Items.copper, 100}));
                this.size = 3;
                this.health = 1100;
                this.liquidCapacity = 250.0F;
                this.liquidPressure = 0.3F;
                this.disableOgUpdate();
                this.addGraph((new GraphTorqueGenerate(0.3F, 20.0F, 7.0F, 15.0F)).setAccept(new int[]{0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0}));
            }
        };
        electricMotor = new ElectricMotor("electric-motor") {
            {
                this.requirements(Category.power, ItemStack.with(new Object[]{Items.silicon, 100, Items.lead, 80, Items.copper, 150, Items.titanium, 150}));
                this.size = 3;
                this.health = 1300;
                this.consumes.power(4.5F);
                this.addGraph((new GraphTorqueGenerate(0.1F, 25.0F, 10.0F, 16.0F)).setAccept(new int[]{0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0}));
            }
        };
        cupronickelWall = new HeatWall("cupronickel-wall") {
            {
                this.requirements(Category.defense, ItemStack.with(new Object[]{UnityItems.cupronickel, 8, UnityItems.nickel, 5}));
                this.health = 500;
                this.addGraph((new GraphHeat(50.0F, 0.5F, 0.03F)).setAccept(new int[]{1, 1, 1, 1}));
            }
        };
        cupronickelWallLarge = new HeatWall("cupronickel-wall-large") {
            {
                this.requirements(Category.defense, ItemStack.with(new Object[]{UnityItems.cupronickel, 36, UnityItems.nickel, 20}));
                this.size = 2;
                this.health = 2000;
                this.minStatusRadius = 8.0F;
                this.statusRadiusMul = 40.0F;
                this.minStatusDuration = 5.0F;
                this.statusDurationMul = 120.0F;
                this.statusTime = 120.0F;
                this.maxDamage = 40.0F;
                this.addGraph((new GraphHeat(200.0F, 0.5F, 0.09F)).setAccept(new int[]{1, 1, 1, 1, 1, 1, 1, 1}));
            }
        };
        infiHeater = new HeatSource("infi-heater") {
            {
                this.requirements(Category.power, BuildVisibility.sandboxOnly, ItemStack.with(new Object[0]));
                this.health = 200;
                this.addGraph((new GraphHeat(1000.0F, 1.0F, 0.0F)).setAccept(new int[]{1, 1, 1, 1}));
            }
        };
        infiCooler = new HeatSource("infi-cooler") {
            {
                this.requirements(Category.power, BuildVisibility.sandboxOnly, ItemStack.with(new Object[0]));
                this.health = 200;
                this.isVoid = true;
                this.addGraph((new GraphHeat(1000.0F, 1.0F, 0.0F)).setAccept(new int[]{1, 1, 1, 1}));
            }
        };
        infiTorque = new TorqueGenerator("infi-torque") {
            {
                this.requirements(Category.power, BuildVisibility.sandboxOnly, ItemStack.with(new Object[0]));
                this.health = 200;
                this.preserveDraw = true;
                this.rotate = false;
                this.addGraph((new GraphTorqueGenerate(0.001F, 1.0F, 999999.0F, 9999.0F)).setAccept(new int[]{1, 1, 1, 1}));
            }
        };
        neodymiumStator = new Magnet("neodymium-stator") {
            {
                this.requirements(Category.power, BuildVisibility.sandboxOnly, ItemStack.with(new Object[0]));
                this.health = 400;
                this.addGraph((new GraphFlux(200.0F)).setAccept(new int[]{1, 0, 0, 0}));
            }
        };
        smallThruster = new UnityThruster("small-thruster") {
            {
                this.requirements(Category.effect, ItemStack.with(new Object[]{Items.silicon, 20, Items.graphite, 30, UnityItems.nickel, 25}));
                this.health = 400;
                this.acceleration = 0.2F;
                this.maxSpeed = 5.0F;
                this.maxBlocks = 5;
                this.itemDuration = 300.0F;
                this.consumes.item(Items.blastCompound);
            }
        };
        advanceConstructorModule = new ModularConstructorPart("advance-constructor-module") {
            {
                this.requirements(Category.units, ItemStack.with(new Object[]{UnityItems.xenium, 300, Items.silicon, 200, Items.graphite, 300, Items.thorium, 400, Items.phaseFabric, 50, Items.surgeAlloy, 100, UnityItems.advanceAlloy, 300}));
                this.size = 6;
                this.liquidCapacity = 20.0F;
                this.consumes.liquid(Liquids.cryofluid, 0.7F);
                this.consumes.power(3.0F);
                this.hasLiquids = true;
                this.hasPower = true;
            }
        };
        advanceConstructor = new ModularConstructor("advance-constructor") {
            {
                this.requirements(Category.units, ItemStack.with(new Object[]{UnityItems.xenium, 3000, Items.silicon, 5000, Items.graphite, 2000, Items.thorium, 3000, Items.phaseFabric, 800, Items.surgeAlloy, 700, UnityItems.advanceAlloy, 1500}));
                this.size = 13;
                this.efficiencyPerTier = 600.0F;
                this.plans.addAll(new ModularConstructor.ModularConstructorPlan[]{new ModularConstructor.ModularConstructorPlan(UnitTypes.antumbra, 1800.0F, 0, ItemStack.with(new Object[]{Items.silicon, 690, Items.graphite, 40, Items.titanium, 550, Items.metaglass, 40, Items.plastanium, 420})), new ModularConstructor.ModularConstructorPlan(UnitTypes.scepter, 1800.0F, 0, ItemStack.with(new Object[]{Items.silicon, 690, Items.lead, 60, Items.graphite, 30, Items.titanium, 550, Items.metaglass, 40, Items.plastanium, 420})), new ModularConstructor.ModularConstructorPlan(UnitTypes.eclipse, 2400.0F, 1, ItemStack.with(new Object[]{Items.silicon, 1350, Items.graphite, 120, Items.titanium, 550, Items.metaglass, 100, Items.plastanium, 830, Items.surgeAlloy, 330, Items.phaseFabric, 250})), new ModularConstructor.ModularConstructorPlan(UnitTypes.reign, 2400.0F, 1, ItemStack.with(new Object[]{Items.silicon, 1350, Items.lead, 160, Items.graphite, 90, Items.titanium, 550, Items.metaglass, 100, Items.plastanium, 830, Items.surgeAlloy, 330, Items.phaseFabric, 250})), new ModularConstructor.ModularConstructorPlan(UnityUnitTypes.mantle, 3000.0F, 2, ItemStack.with(new Object[]{Items.silicon, 2050, Items.graphite, 180, Items.titanium, 830, Items.metaglass, 150, Items.plastanium, 1250, Items.surgeAlloy, 500, Items.phaseFabric, 375}))});
                this.consumes.power(13.0F);
            }
        };
        celsius = new PowerTurret("celsius") {
            {
                this.requirements(Category.turret, ItemStack.with(new Object[]{Items.silicon, 20, UnityItems.xenium, 15, Items.titanium, 30, UnityItems.advanceAlloy, 25}));
                this.health = 780;
                this.size = 1;
                this.reloadTime = 3.0F;
                this.range = 47.0F;
                this.shootCone = 50.0F;
                this.heatColor = Color.valueOf("ccffff");
                this.ammoUseEffect = Fx.none;
                this.inaccuracy = 9.2F;
                this.rotateSpeed = 7.5F;
                this.shots = 2;
                this.recoilAmount = 1.0F;
                this.powerUse = 13.9F;
                this.hasPower = true;
                this.targetAir = true;
                this.shootSound = Sounds.flame;
                this.cooldown = 0.01F;
                this.shootType = UnityBullets.celsiusSmoke;
            }
        };
        kelvin = new PowerTurret("kelvin") {
            {
                this.requirements(Category.turret, ItemStack.with(new Object[]{Items.silicon, 80, UnityItems.xenium, 35, Items.titanium, 90, UnityItems.advanceAlloy, 50}));
                this.health = 2680;
                this.size = 2;
                this.reloadTime = 3.0F;
                this.range = 100.0F;
                this.shootCone = 50.0F;
                this.heatColor = Color.valueOf("ccffff");
                this.ammoUseEffect = Fx.none;
                this.inaccuracy = 9.2F;
                this.rotateSpeed = 6.5F;
                this.shots = 2;
                this.spread = 6.0F;
                this.recoilAmount = 1.0F;
                this.powerUse = 13.9F;
                this.hasPower = true;
                this.targetAir = true;
                this.shootSound = Sounds.flame;
                this.cooldown = 0.01F;
                this.shootType = UnityBullets.kelvinSmoke;
            }
        };
        caster = new PowerTurret("arc-caster") {
            {
                this.requirements(Category.turret, ItemStack.with(new Object[]{Items.silicon, 20, UnityItems.xenium, 15, Items.titanium, 30, UnityItems.advanceAlloy, 25}));
                this.size = 3;
                this.health = 4600;
                this.range = 190.0F;
                this.reloadTime = 120.0F;
                this.shootCone = 30.0F;
                this.inaccuracy = 9.2F;
                this.rotateSpeed = 5.5F;
                this.recoilAmount = 1.0F;
                this.powerUse = 9.4F;
                this.heatColor = UnityPal.lightHeat;
                this.cooldown = 0.01F;
                this.shootSound = Sounds.flame;
                this.shootEffect = Fx.none;
                this.chargeTime = 51.0F;
                this.chargeMaxDelay = 24.0F;
                this.chargeEffects = 5;
                this.chargeEffect = UnityFx.arcCharge;
                this.shootType = new ArcBulletType(4.6F, 8.0F) {
                    {
                        this.lifetime = 43.0F;
                        this.hitSize = 21.0F;
                        this.lightningChance1 = 0.5F;
                        this.lightningDamage1 = 29.0F;
                        this.lightningChance2 = 0.2F;
                        this.lightningDamage2 = 14.0F;
                        this.length1 = 11;
                        this.lengthRand1 = 7;
                    }
                };
            }
        };
        storm = new PowerTurret("arc-storm") {
            {
                this.requirements(Category.turret, ItemStack.with(new Object[]{Items.silicon, 80, UnityItems.xenium, 35, Items.titanium, 90, UnityItems.advanceAlloy, 50}));
                this.size = 4;
                this.health = 7600;
                this.range = 210.0F;
                this.reloadTime = 180.0F;
                this.shots = 5;
                this.shootCone = 30.0F;
                this.inaccuracy = 11.2F;
                this.rotateSpeed = 5.5F;
                this.recoilAmount = 2.0F;
                this.powerUse = 33.4F;
                this.heatColor = UnityPal.lightHeat;
                this.cooldown = 0.01F;
                this.shootSound = Sounds.flame;
                this.shootEffect = Fx.none;
                this.chargeTime = 51.0F;
                this.chargeMaxDelay = 24.0F;
                this.chargeEffects = 5;
                this.chargeEffect = UnityFx.arcCharge;
                this.shootType = new ArcBulletType(4.6F, 8.6F) {
                    {
                        this.lifetime = 53.0F;
                        this.hitSize = 28.0F;
                        this.radius = 13.0F;
                        this.lightningChance1 = 0.7F;
                        this.lightningDamage1 = 31.0F;
                        this.lightningChance2 = 0.3F;
                        this.lightningDamage2 = 17.0F;
                        this.length1 = 13;
                        this.lengthRand1 = 9;
                    }
                };
            }
        };
        eclipse = new LaserTurret("blue-eclipse") {
            {
                this.requirements(Category.turret, ItemStack.with(new Object[]{Items.lead, 620, Items.titanium, 520, Items.surgeAlloy, 720, Items.silicon, 760, Items.phaseFabric, 120, UnityItems.xenium, 620, UnityItems.advanceAlloy, 680}));
                this.size = 7;
                this.health = 9000;
                this.range = 340.0F;
                this.reloadTime = 280.0F;
                this.coolantMultiplier = 2.4F;
                this.shootCone = 40.0F;
                this.powerUse = 19.0F;
                this.shootShake = 3.0F;
                this.shootEffect = Fx.shootBigSmoke2;
                this.recoilAmount = 8.0F;
                this.shootSound = Sounds.laser;
                this.loopSound = UnitySounds.eclipseBeam;
                this.loopSoundVolume = 2.5F;
                this.heatColor = UnityPal.advanceDark;
                this.rotateSpeed = 1.9F;
                this.shootDuration = 320.0F;
                this.firingMoveFract = 0.12F;
                this.shootLength = (float)(this.size * 8) / 2.0F - this.recoilAmount;
                this.shootType = new AcceleratingLaserBulletType(390.0F) {
                    {
                        this.colors = new Color[]{Color.valueOf("59a7ff55"), Color.valueOf("59a7ffaa"), Color.valueOf("a3e3ff"), Color.white};
                        this.width = 29.2F;
                        this.collisionWidth = 12.0F;
                        this.knockback = 2.2F;
                        this.lifetime = 18.0F;
                        this.accel = 0.0F;
                        this.fadeInTime = 0.0F;
                        this.fadeTime = 18.0F;
                        this.maxLength = 490.0F;
                        this.shootEffect = Fx.none;
                        this.smokeEffect = Fx.none;
                        this.hitEffect = HitFx.eclipseHit;
                        this.buildingInsulator = (b, building) -> true;
                        this.unitInsulator = (b, u) -> true;
                    }
                };
                ((ConsumeLiquidFilter)this.consumes.add(new ConsumeLiquidFilter((liquid) -> liquid.temperature <= 0.4F && liquid.flammability < 0.1F, 2.1F))).update(false);
            }

            public void load() {
                super.load();
                this.baseRegion = Core.atlas.find("unity-block-" + this.size);
            }
        };
        xenoCorruptor = new LaserTurret("xeno-corruptor") {
            {
                this.requirements(Category.turret, ItemStack.with(new Object[]{Items.lead, 640, Items.graphite, 740, Items.titanium, 560, Items.surgeAlloy, 650, Items.silicon, 720, Items.thorium, 400, UnityItems.xenium, 340, UnityItems.advanceAlloy, 640}));
                this.health = 7900;
                this.size = 7;
                this.reloadTime = 230.0F;
                this.range = 290.0F;
                this.coolantMultiplier = 1.4F;
                this.shootCone = 40.0F;
                this.shootDuration = 310.0F;
                this.firingMoveFract = 0.16F;
                this.powerUse = 45.0F;
                this.shootShake = 3.0F;
                this.recoilAmount = 8.0F;
                this.shootSound = Sounds.laser;
                this.loopSound = UnitySounds.xenoBeam;
                this.loopSoundVolume = 2.0F;
                this.heatColor = UnityPal.advanceDark;
                this.shootType = new ChangeTeamLaserBulletType(60.0F) {
                    {
                        this.length = 300.0F;
                        this.lifetime = 18.0F;
                        this.shootEffect = Fx.none;
                        this.smokeEffect = Fx.none;
                        this.hitEffect = Fx.hitLancer;
                        this.incendChance = -1.0F;
                        this.lightColor = Color.valueOf("59a7ff");
                        this.conversionStatusEffect = UnityStatusEffects.teamConverted;
                        this.convertBlocks = false;
                        this.colors = new Color[]{Color.valueOf("59a7ff55"), Color.valueOf("59a7ffaa"), Color.valueOf("a3e3ff"), Color.white};
                    }
                };
                ((ConsumeLiquidFilter)this.consumes.add(new ConsumeLiquidFilter((liquid) -> liquid.temperature <= 0.4F && liquid.flammability < 0.1F, 2.1F))).update(false);
            }

            public void load() {
                super.load();
                this.baseRegion = Core.atlas.find("unity-block-" + this.size);
            }
        };
        cube = new ObjPowerTurret("the-cube") {
            {
                this.requirements(Category.turret, ItemStack.with(new Object[]{Items.copper, 3300, Items.lead, 2900, Items.graphite, 4400, Items.silicon, 3800, Items.titanium, 4600, UnityItems.xenium, 2300, Items.phaseFabric, 670, UnityItems.advanceAlloy, 1070}));
                this.health = 22500;
                this.object = UnityObjs.cube;
                this.size = 10;
                this.range = 320.0F;
                this.reloadTime = 240.0F;
                this.powerUse = 260.0F;
                this.coolantMultiplier = 1.1F;
                this.shootSound = UnitySounds.cubeBlast;
                this.shootType = new PointBlastLaserBulletType(580.0F) {
                    {
                        this.length = 320.0F;
                        this.lifetime = 17.0F;
                        this.pierce = true;
                        this.auraDamage = 8000.0F;
                        this.damageRadius = 120.0F;
                        this.laserColors = new Color[]{UnityPal.advance};
                    }
                };
            }
        };
        wavefront = new WavefrontTurret("wavefront") {
            {
                this.requirements(Category.turret, ItemStack.with(new Object[]{Items.copper, 4900, Items.graphite, 6000, Items.silicon, 5000, Items.titanium, 6500, UnityItems.xenium, 1500, UnityItems.advanceAlloy, 1500, UnityItems.terminum, 700, UnityItems.terminaAlloy, 500}));
                this.health = 50625;
                this.model = UnityModels.wavefront;
                this.size = 15;
                this.range = 420.0F;
                this.rotateSpeed = 3.0F;
                this.reloadTime = 240.0F;
                this.powerUse = 260.0F;
                this.coolantMultiplier = 0.9F;
                this.shootSound = UnitySounds.cubeBlast;
                this.shootType = new WavefrontLaser(2400.0F);
            }
        };
        terminalCrucible = new GenericCrafter("terminal-crucible") {
            {
                this.requirements(Category.crafting, ItemStack.with(new Object[]{Items.lead, 810, Items.graphite, 720, Items.silicon, 520, Items.phaseFabric, 430, Items.surgeAlloy, 320, UnityItems.plagueAlloy, 120, UnityItems.darkAlloy, 120, UnityItems.lightAlloy, 120, UnityItems.advanceAlloy, 120, UnityItems.monolithAlloy, 120, UnityItems.sparkAlloy, 120, UnityItems.superAlloy, 120}));
                this.size = 6;
                this.craftTime = 310.0F;
                this.ambientSound = Sounds.respawning;
                this.ambientSoundVolume = 0.6F;
                this.outputItem = new ItemStack(UnityItems.terminum, 1);
                this.consumes.power(45.2F);
                this.consumes.items(ItemStack.with(new Object[]{UnityItems.plagueAlloy, 3, UnityItems.darkAlloy, 3, UnityItems.lightAlloy, 3, UnityItems.advanceAlloy, 3, UnityItems.monolithAlloy, 3, UnityItems.sparkAlloy, 3, UnityItems.superAlloy, 3}));
                this.drawer = new DrawGlow() {
                    public void draw(GenericCrafter.GenericCrafterBuild build) {
                        Draw.rect(build.block.region, build.x, build.y);
                        Draw.blend(Blending.additive);
                        Draw.color(1.0F, Mathf.absin(5.0F, 0.5F) + 0.5F, Mathf.absin(Time.time + 5156.62F, 5.0F, 0.5F) + 0.5F, build.warmup);
                        Draw.rect(Regions.terminalCrucibleLightsRegion, build.x, build.y);
                        float b = (Mathf.absin(8.0F, 0.25F) + 0.75F) * build.warmup;
                        Draw.color(1.0F, b, b, b);
                        Draw.rect(this.top, build.x, build.y);
                        Draw.reset();
                        Draw.blend();
                    }
                };
            }
        };
        endForge = new StemGenericCrafter("end-forge") {
            final int effectTimer;

            {
                this.effectTimer = this.timers++;
                this.requirements(Category.crafting, ItemStack.with(new Object[]{Items.silicon, 2300, Items.phaseFabric, 650, Items.surgeAlloy, 1350, UnityItems.plagueAlloy, 510, UnityItems.darkAlloy, 510, UnityItems.lightAlloy, 510, UnityItems.advanceAlloy, 510, UnityItems.monolithAlloy, 510, UnityItems.sparkAlloy, 510, UnityItems.superAlloy, 510, UnityItems.terminationFragment, 230}));
                this.size = 8;
                this.craftTime = 410.0F;
                this.ambientSoundVolume = 0.6F;
                this.outputItem = new ItemStack(UnityItems.terminaAlloy, 2);
                this.consumes.power(86.7F);
                this.consumes.items(ItemStack.with(new Object[]{UnityItems.terminum, 3, UnityItems.darkAlloy, 5, UnityItems.lightAlloy, 5}));
                this.update((e) -> {
                    if (e.consValid()) {
                        if (e.timer.get(this.effectTimer, 120.0F)) {
                            UnityFx.forgeFlameEffect.at(e);
                            UnityFx.forgeAbsorbPulseEffect.at(e);
                        }

                        if (Mathf.chanceDelta((double)(0.7F * e.warmup))) {
                            UnityFx.forgeAbsorbEffect.at(e.x, e.y, Mathf.random(360.0F));
                        }
                    }

                });
                this.drawer = new DrawGlow() {
                    public void draw(GenericCrafter.GenericCrafterBuild build) {
                        Draw.rect(build.block.region, build.x, build.y);
                        Draw.blend(Blending.additive);
                        Draw.color(1.0F, Mathf.absin(5.0F, 0.5F) + 0.5F, Mathf.absin(Time.time + 5156.62F, 5.0F, 0.5F) + 0.5F, build.warmup);
                        Draw.rect(Regions.endForgeLightsRegion, build.x, build.y);
                        float b = (Mathf.absin(8.0F, 0.25F) + 0.75F) * build.warmup;
                        Draw.color(1.0F, b, b, b);
                        Draw.rect(this.top, build.x, build.y);

                        for(int i = 0; i < 4; ++i) {
                            float ang = (float)i * 90.0F;

                            for(int s = 0; s < 2; ++s) {
                                float offset = 45.0F * (float)(i * 2 + s);
                                TextureRegion reg = Regions.endForgeTopSmallRegion;
                                int sign = Mathf.signs[s];
                                float colA = (Mathf.absin(Time.time + offset * (180F / (float)Math.PI), 8.0F, 0.25F) + 0.75F) * build.warmup;
                                float colB = (Mathf.absin(Time.time + (90.0F + offset) * (180F / (float)Math.PI), 8.0F, 0.25F) + 0.75F) * build.warmup;
                                Draw.color(1.0F, colA, colB, build.warmup);
                                Draw.rect(reg, build.x, build.y, (float)(reg.width * sign) * Draw.scl, (float)reg.height * Draw.scl, -ang);
                            }
                        }

                        Draw.blend();
                        Draw.color();
                    }
                };
            }
        };
        tenmeikiri = new EndLaserTurret("tenmeikiri") {
            {
                this.requirements(Category.turret, ItemStack.with(new Object[]{Items.phaseFabric, 3000, Items.surgeAlloy, 4000, UnityItems.darkAlloy, 1800, UnityItems.terminum, 1200, UnityItems.terminaAlloy, 200}));
                this.health = 23000;
                this.range = 900.0F;
                this.size = 15;
                this.shootCone = 1.5F;
                this.reloadTime = 300.0F;
                this.coolantMultiplier = 0.5F;
                this.recoilAmount = 15.0F;
                this.powerUse = 350.0F;
                this.absorbLasers = true;
                this.shootLength = 8.0F;
                this.chargeTime = 158.0F;
                this.chargeEffects = 12;
                this.chargeMaxDelay = 80.0F;
                this.chargeEffect = ChargeFx.tenmeikiriChargeEffect;
                this.chargeBeginEffect = ChargeFx.tenmeikiriChargeBegin;
                this.chargeSound = UnitySounds.tenmeikiriCharge;
                this.shootSound = UnitySounds.tenmeikiriShoot;
                this.shootShake = 4.0F;
                this.shootType = new EndCutterLaserBulletType(7800.0F) {
                    {
                        this.maxLength = 1200.0F;
                        this.lifetime = 180.0F;
                        this.width = 30.0F;
                        this.laserSpeed = 80.0F;
                        this.status = StatusEffects.melting;
                        this.antiCheatScl = 5.0F;
                        this.statusDuration = 200.0F;
                        this.lightningColor = UnityPal.scarColor;
                        this.lightningDamage = 85.0F;
                        this.lightningLength = 15;
                        this.ratioDamage = 0.016666668F;
                        this.ratioStart = 30000.0F;
                        this.overDamage = 350000.0F;
                        this.bleedDuration = 300.0F;
                    }
                };
                ((ConsumeLiquidFilter)this.consumes.add(new ConsumeLiquidFilter((liquid) -> liquid.temperature <= 0.25F && liquid.flammability < 0.1F, 3.1F))).update(false);
            }
        };
        endGame = new EndGameTurret("endgame") {
            {
                this.requirements(Category.turret, ItemStack.with(new Object[]{Items.phaseFabric, 9500, Items.surgeAlloy, 10500, UnityItems.darkAlloy, 2300, UnityItems.lightAlloy, 2300, UnityItems.advanceAlloy, 2300, UnityItems.plagueAlloy, 2300, UnityItems.sparkAlloy, 2300, UnityItems.monolithAlloy, 2300, UnityItems.superAlloy, 2300, UnityItems.terminum, 1600, UnityItems.terminaAlloy, 800, UnityItems.terminationFragment, 100}));
                this.shootCone = 360.0F;
                this.reloadTime = 430.0F;
                this.range = 820.0F;
                this.size = 14;
                this.coolantMultiplier = 0.6F;
                this.hasItems = true;
                this.itemCapacity = 10;
                this.loopSoundVolume = 0.2F;
                this.shootType = new BulletType() {
                    {
                        this.damage = Float.POSITIVE_INFINITY;
                    }
                };
                this.consumes.item(UnityItems.terminum, 2);
            }
        };
    }
}
