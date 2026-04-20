package unity;

import arc.Core;
import arc.Events;
import arc.assets.AssetDescriptor;
import arc.assets.AssetManager;
import arc.files.Fi;
import arc.freetype.FreeTypeFontGenerator;
import arc.freetype.FreeTypeFontGeneratorLoader;
import arc.freetype.FreetypeFontLoader;
import arc.func.Func;
import arc.graphics.Camera;
import arc.graphics.g2d.Font;
import arc.graphics.g3d.Camera3D;
import arc.scene.Group;
import arc.scene.ui.TextButton;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Strings;
import arc.util.Time;
import arc.util.Log.LogLevel;
import arc.util.serialization.JsonReader;
import arc.util.serialization.UBJsonReader;
import java.util.Objects;
import mindustry.Vars;
import mindustry.ctype.Content;
import mindustry.ctype.UnlockableContent;
import mindustry.game.EventType;
import mindustry.game.EventType.Trigger;
import mindustry.mod.Mod;
import mindustry.mod.Mods;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.blocks.environment.Prop;
import unity.ai.kami.KamiRegions;
import unity.assets.list.UnityFonts;
import unity.assets.list.UnityShaders;
import unity.assets.loaders.ModelLoader;
import unity.assets.type.g3d.Model;
import unity.assets.type.g3d.Models;
import unity.async.ContentScoreProcess;
import unity.async.LightProcess;
import unity.content.Overwriter;
import unity.content.UnityBlocks;
import unity.content.UnityBullets;
import unity.content.UnityItems;
import unity.content.UnityLiquids;
import unity.content.UnityPlanets;
import unity.content.UnitySectorPresets;
import unity.content.UnitySettings;
import unity.content.UnityStatusEffects;
import unity.content.UnityTechTree;
import unity.content.UnityUnitTypes;
import unity.content.UnityWeaponTemplates;
import unity.content.UnityWeathers;
import unity.editor.CinematicEditor;
import unity.gen.FactionMeta;
import unity.gen.Regions;
import unity.gen.UnityEntityMapping;
import unity.gen.UnityModels;
import unity.gen.UnityObjs;
import unity.gen.UnitySounds;
import unity.map.cinematic.Cinematics;
import unity.map.cinematic.Cutscene;
import unity.map.cinematic.Speeches;
import unity.mod.AntiCheat;
import unity.mod.DevBuild;
import unity.mod.Faction;
import unity.mod.MusicHandler;
import unity.mod.TapHandler;
import unity.mod.TimeStop;
import unity.mod.Triggers;
import unity.sync.UnityCall;
import unity.ui.UnityStyles;
import unity.ui.dialogs.CinematicDialog;
import unity.ui.dialogs.CreditsDialog;
import unity.ui.dialogs.ObjectivesDialog;
import unity.ui.dialogs.ScriptsDictionaryDialog;
import unity.ui.dialogs.ScriptsEditorDialog;
import unity.ui.dialogs.TagsDialog;
import unity.util.BlockMovement;
import unity.util.JSBridge;
import unity.util.TimeReflect;
import unity.util.Utils;
import unity.util.WavefrontObject;
import unity.util.WavefrontObjectLoader;
import unity.world.MonolithWorld;
import younggamExperimental.UnityParts;

public class Unity extends Mod {
    public static boolean tools = false;
    public static Cinematics cinematic;
    public static MusicHandler music;
    public static TapHandler tap;
    public static AntiCheat antiCheat;
    public static MonolithWorld monolithWorld;
    public static DevBuild dev;
    public static CinematicEditor cinematicEditor;
    public static CreditsDialog creditsDialog;
    public static ScriptsEditorDialog jsEditDialog;
    public static ScriptsDictionaryDialog jsDictDialog;
    public static CinematicDialog cinematicDialog;
    public static TagsDialog tagsDialog;
    public static ObjectivesDialog objectivesDialog;
    public static LightProcess lights;
    public static ContentScoreProcess scoring;
    public static final Seq<String> classes = Seq.with(new String[]{"unity.ai.AssistantAI", "unity.ai.AssistantAI$Assistance", "unity.ai.CopterAI", "unity.ai.DistanceGroundAI", "unity.ai.EmptyAI", "unity.ai.HealerAI", "unity.ai.HealingDefenderAI", "unity.ai.kami.KamiAI", "unity.ai.kami.KamiAI$KamiDelay", "unity.ai.kami.KamiBulletDatas", "unity.ai.kami.KamiBulletDatas$KamiBulletData", "unity.ai.kami.KamiBulletDatas$KamiLaserData", "unity.ai.kami.KamiBulletDatas$KamiBulletDataBase", "unity.ai.kami.KamiBulletPresets", "unity.ai.kami.KamiPattern", "unity.ai.kami.KamiPattern$PatternType", "unity.ai.kami.KamiPattern$PatternData", "unity.ai.kami.KamiPattern$StagePattern", "unity.ai.kami.KamiPattern$StagePattern$Stage", "unity.ai.kami.KamiPattern$StagePattern$StageData", "unity.ai.kami.KamiPatterns", "unity.ai.kami.KamiPatterns$HyperSpeedData", "unity.ai.kami.KamiRegions", "unity.ai.LinkedAI", "unity.ai.LinkerAI", "unity.ai.MonolithSoulAI", "unity.ai.NewHealerAI", "unity.ai.SmartGroundAI", "unity.ai.WormAI", "unity.assets.list.UnityFonts", "unity.assets.list.UnityShaders", "unity.assets.list.UnityShaders$CondShader", "unity.assets.list.UnityShaders$FragmentationShader", "unity.assets.list.UnityShaders$VapourizeShader", "unity.assets.list.UnityShaders$StencilShader", "unity.assets.list.UnityShaders$PlanetObjectShader", "unity.assets.list.UnityShaders$MegalithRingShader", "unity.assets.list.UnityShaders$Graphics3DShaderProvider", "unity.assets.list.UnityShaders$Graphics3DShader", "unity.assets.loaders.ModelLoader", "unity.assets.loaders.ModelLoader$ModelParameter", "unity.assets.type.g3d.Animation", "unity.assets.type.g3d.AnimControl", "unity.assets.type.g3d.attribute.Attribute", "unity.assets.type.g3d.attribute.Attributes", "unity.assets.type.g3d.attribute.Environment", "unity.assets.type.g3d.attribute.light.BaseLight", "unity.assets.type.g3d.attribute.light.DirectionalLight", "unity.assets.type.g3d.attribute.light.PointLight", "unity.assets.type.g3d.attribute.light.ShadowMap", "unity.assets.type.g3d.attribute.light.SpotLight", "unity.assets.type.g3d.attribute.Material", "unity.assets.type.g3d.attribute.type.BlendingAttribute", "unity.assets.type.g3d.attribute.type.ColorAttribute", "unity.assets.type.g3d.attribute.type.FloatAttribute", "unity.assets.type.g3d.attribute.type.light.DirectionalLightsAttribute", "unity.assets.type.g3d.attribute.type.light.PointLightsAttribute", "unity.assets.type.g3d.attribute.type.light.SpotLightsAttribute", "unity.assets.type.g3d.attribute.type.TextureAttribute", "unity.assets.type.g3d.model.ModelAnimation", "unity.assets.type.g3d.model.ModelData", "unity.assets.type.g3d.model.ModelMaterial", "unity.assets.type.g3d.model.ModelMesh", "unity.assets.type.g3d.model.ModelMeshPart", "unity.assets.type.g3d.model.ModelNode", "unity.assets.type.g3d.model.ModelNodeAnimation", "unity.assets.type.g3d.model.ModelNodeKeyframe", "unity.assets.type.g3d.model.ModelNodePart", "unity.assets.type.g3d.model.ModelTexture", "unity.assets.type.g3d.Model", "unity.assets.type.g3d.ModelInstance", "unity.assets.type.g3d.Models", "unity.assets.type.g3d.Models$RenderPool", "unity.assets.type.g3d.Node", "unity.assets.type.g3d.NodeAnimation", "unity.assets.type.g3d.NodeKeyframe", "unity.assets.type.g3d.NodePart", "unity.assets.type.g3d.Renderable", "unity.assets.type.g3d.RenderableProvider", "unity.assets.type.g3d.RenderableSorter", "unity.async.ContentScoreProcess", "unity.async.ContentScoreProcess$ContentScore", "unity.async.ContentScoreProcess$CrafterRequirements", "unity.async.ContentScoreProcess$UnitRequirements", "unity.async.ContentScoreProcess$CrafterScore", "unity.async.ContentScoreProcess$BlockOutput", "unity.async.ContentScoreProcess$OutputHandler", "unity.async.ContentScoreProcess$ConsumesScore", "unity.async.LightProcess", "unity.content.effects.ChargeFx", "unity.content.effects.DeathFx", "unity.content.effects.HitFx", "unity.content.effects.LineFx", "unity.content.effects.ParticleFx", "unity.content.effects.ShootFx", "unity.content.effects.SpecialFx", "unity.content.effects.SpecialFx$PointBlastInterface", "unity.content.effects.SpecialFx$VoidFractureData", "unity.content.effects.TrailFx", "unity.content.Overwriter", "unity.content.Trails", "unity.content.UnderworldBlocks", "unity.content.units.MonolithUnitTypes", "unity.content.UnityBlocks", "unity.content.UnityBullets", "unity.content.UnityFx", "unity.content.UnityItems", "unity.content.UnityLiquids", "unity.content.UnityPlanets", "unity.content.UnitySectorPresets", "unity.content.UnitySettings", "unity.content.UnityStatusEffects", "unity.content.UnityTechTree", "unity.content.UnityUnitTypes", "unity.content.UnityWeaponTemplates", "unity.content.UnityWeathers", "unity.editor.CinematicEditor", "unity.editor.EditorListener", "unity.entities.abilities.BaseAbility", "unity.entities.abilities.BaseAbility$WaitEffectData", "unity.entities.abilities.BulletReflectPulseAbility", "unity.entities.abilities.DirectionShieldAbility", "unity.entities.abilities.DirectionShieldAbility$ShieldNode", "unity.entities.abilities.LightningBurstAbility", "unity.entities.abilities.LightningSpawnAbility", "unity.entities.abilities.ShootArmorAbility", "unity.entities.abilities.SlashAbility", "unity.entities.abilities.TeleportAbility", "unity.entities.abilities.TimeStopAbility", "unity.entities.AbilityTextures", "unity.entities.bullet.anticheat.AntiCheatBulletTypeBase", "unity.entities.bullet.anticheat.ContinuousSingularityLaserBulletType", "unity.entities.bullet.anticheat.ContinuousSingularityLaserBulletType$VoidLaserData", "unity.entities.bullet.anticheat.DesolationBulletType", "unity.entities.bullet.anticheat.DesolationBulletType$DesolationBulletData", "unity.entities.bullet.anticheat.EndBasicBulletType", "unity.entities.bullet.anticheat.EndContinuousLaserBulletType", "unity.entities.bullet.anticheat.EndCutterLaserBulletType", "unity.entities.bullet.anticheat.EndPointBlastLaserBulletType", "unity.entities.bullet.anticheat.EndRailBulletType", "unity.entities.bullet.anticheat.EndSweepLaser", "unity.entities.bullet.anticheat.modules.AbilityDamageModule", "unity.entities.bullet.anticheat.modules.AntiCheatBulletModule", "unity.entities.bullet.anticheat.modules.ArmorDamageModule", "unity.entities.bullet.anticheat.modules.ForceFieldDamageModule", "unity.entities.bullet.anticheat.OppressionLaserBulletType", "unity.entities.bullet.anticheat.SlowLightningBulletType", "unity.entities.bullet.anticheat.TimeStopBulletType", "unity.entities.bullet.anticheat.VoidAreaBulletType", "unity.entities.bullet.anticheat.VoidFractureBulletType", "unity.entities.bullet.anticheat.VoidFractureBulletType$FractureData", "unity.entities.bullet.anticheat.VoidPelletBulletType", "unity.entities.bullet.anticheat.VoidPortalBulletType", "unity.entities.bullet.anticheat.VoidPortalBulletType$VoidPortalData", "unity.entities.bullet.anticheat.VoidPortalBulletType$VoidTentacle", "unity.entities.bullet.energy.ArcBulletType", "unity.entities.bullet.energy.ArrowBulletType", "unity.entities.bullet.energy.BeamBulletType", "unity.entities.bullet.energy.CygnusBulletType", "unity.entities.bullet.energy.DecayBasicBulletType", "unity.entities.bullet.energy.EmpBasicBulletType", "unity.entities.bullet.energy.EphemeronBulletType", "unity.entities.bullet.energy.EphemeronBulletType$EphemeronEffectData", "unity.entities.bullet.energy.EphemeronPairBulletType", "unity.entities.bullet.energy.FlameBulletType", "unity.entities.bullet.energy.GluonOrbBulletType", "unity.entities.bullet.energy.GluonOrbBulletType$GluonOrbData", "unity.entities.bullet.energy.GluonWhirlBulletType", "unity.entities.bullet.energy.HealingConeBulletType", "unity.entities.bullet.energy.HealingNukeBulletType", "unity.entities.bullet.energy.HealingShockWaveBulletType", "unity.entities.bullet.energy.HealingShockWaveBulletType$HealingShockWaveData", "unity.entities.bullet.energy.HealingShockWaveBulletType$ShockWavePositionData", "unity.entities.bullet.energy.LightningTurretBulletType", "unity.entities.bullet.energy.PointDrainLaserBulletType", "unity.entities.bullet.energy.PointDrainLaserBulletType$DrainLaserData", "unity.entities.bullet.energy.ShieldBulletType", "unity.entities.bullet.energy.SingularityBulletType", "unity.entities.bullet.energy.SingularityBulletType$SingularityAbsorbEffectData", "unity.entities.bullet.energy.SmokeBulletType", "unity.entities.bullet.energy.TrailingEmpBulletType", "unity.entities.bullet.energy.TriangleBulletType", "unity.entities.bullet.energy.VelocityLaserBoltBulletType", "unity.entities.bullet.exp.DistFieldBulletType", "unity.entities.bullet.exp.ExpBasicBulletType", "unity.entities.bullet.exp.ExpBulletType", "unity.entities.bullet.exp.ExpLaserBlastBulletType", "unity.entities.bullet.exp.ExpLaserBulletType", "unity.entities.bullet.exp.ExpLaserFieldBulletType", "unity.entities.bullet.exp.GeyserBulletType", "unity.entities.bullet.exp.GeyserLaserBulletType", "unity.entities.bullet.kami.CircleBulletType", "unity.entities.bullet.kami.KamiAltLaserBulletType", "unity.entities.bullet.kami.KamiAltLaserBulletType$KamiLaserData", "unity.entities.bullet.kami.KamiBulletType", "unity.entities.bullet.kami.KamiLaserBulletType", "unity.entities.bullet.kami.NewKamiLaserBulletType", "unity.entities.bullet.laser.AcceleratingLaserBulletType", "unity.entities.bullet.laser.AcceleratingLaserBulletType$LaserData", "unity.entities.bullet.laser.AnomalyLaserBulletType", "unity.entities.bullet.laser.ChangeTeamLaserBulletType", "unity.entities.bullet.laser.GravitonLaserBulletType", "unity.entities.bullet.laser.PointBlastLaserBulletType", "unity.entities.bullet.laser.ReflectingLaserBulletType", "unity.entities.bullet.laser.ReflectingLaserBulletType$ReflectLaserData", "unity.entities.bullet.laser.RoundLaserBulletType", "unity.entities.bullet.laser.SaberContinuousLaserBulletType", "unity.entities.bullet.laser.SagittariusLaserBulletType", "unity.entities.bullet.laser.SagittariusLaserBulletType$SagittariusLaserData", "unity.entities.bullet.laser.SparkingContinuousLaserBulletType", "unity.entities.bullet.laser.WavefrontLaser", "unity.entities.bullet.misc.BlockStatusEffectBulletType", "unity.entities.bullet.misc.MultiBulletType", "unity.entities.bullet.misc.MultiBulletType$MultiBulletData", "unity.entities.bullet.misc.ShootingBulletType", "unity.entities.bullet.misc.TentacleBulletType", "unity.entities.bullet.misc.TentacleBulletType$TentacleNode", "unity.entities.bullet.misc.TentacleBulletType$TentacleBulletData", "unity.entities.bullet.monolith.energy.JoiningBulletType", "unity.entities.bullet.monolith.energy.JoiningBulletType$JoinData", "unity.entities.bullet.monolith.energy.RicochetBulletType", "unity.entities.bullet.monolith.energy.RicochetBulletType$RicochetBulletData", "unity.entities.bullet.monolith.laser.HelixLaserBulletType", "unity.entities.bullet.physical.AntiBulletFlakBulletType", "unity.entities.bullet.physical.GuidedMissileBulletType", "unity.entities.bullet.physical.MortarBulletType", "unity.entities.bullet.physical.SlowRailBulletType", "unity.entities.bullet.physical.SlowRailBulletType$RailData", "unity.entities.CircleCollisionBullet", "unity.entities.effects.CustomStateEffect", "unity.entities.effects.CutEffects", "unity.entities.effects.FragmentationShaderEffect", "unity.entities.effects.FragmentationShaderEffect$FragEffectState", "unity.entities.effects.ParentEffect", "unity.entities.effects.ParentEffect$ParentEffectState", "unity.entities.effects.SlowLightningType", "unity.entities.effects.SlowLightningType$SlowLightningNode", "unity.entities.effects.UnitCutEffect", "unity.entities.effects.VapourizeEffectState", "unity.entities.effects.VapourizeShaderEffect", "unity.entities.effects.VapourizeShaderEffect$VapourizeShaderEffectState", "unity.entities.Emp", "unity.entities.ExpOrbs", "unity.entities.ExpOrbs$ExpOrb", "unity.entities.ExtraEffect", "unity.entities.ExtraEffect$BuildQueue", "unity.entities.legs.BasicLeg", "unity.entities.legs.BasicLeg$BasicLegType", "unity.entities.legs.CLeg", "unity.entities.legs.CLegGroup", "unity.entities.legs.CLegType", "unity.entities.legs.CLegType$ClegGroupType", "unity.entities.NewTentacle", "unity.entities.NewTentacle$NewTentacleSegment", "unity.entities.Rotor", "unity.entities.RotorMount", "unity.entities.SaberData", "unity.entities.Soul", "unity.entities.Tentacle", "unity.entities.Tentacle$TentacleSegment", "unity.entities.TriJointLeg", "unity.entities.units.AntiCheatBase", "unity.entities.units.ApocalypseUnit", "unity.entities.units.EndInvisibleUnit", "unity.entities.units.EndLegsUnit", "unity.entities.units.EndWormUnit", "unity.entities.units.EndWormUnit$EndWormSegmentUnit", "unity.entities.units.TentaclesBase", "unity.entities.units.WormDefaultUnit", "unity.entities.units.WormSegmentUnit", "unity.entities.units.WormSegmentUnit$SegmentData", "unity.entities.UnitTile", "unity.entities.UnitVecData", "unity.graphics.ColorMesh", "unity.graphics.CompositeMesh", "unity.graphics.CompositeMesh$MeshComp", "unity.graphics.CompositeMesh$ShaderRef", "unity.graphics.FixedTrail", "unity.graphics.MeshPart", "unity.graphics.MultiTrail", "unity.graphics.MultiTrail$TrailHold", "unity.graphics.MultiTrail$RotationHandler", "unity.graphics.TexturedTrail", "unity.graphics.UnityBlending", "unity.graphics.UnityDrawf", "unity.graphics.UnityDrawf$Skewer", "unity.graphics.UnityPal", "unity.logic.ExpContentList", "unity.logic.ExpSenseI", "unity.logic.ExpSensorStatement", "unity.map.cinematic.Cinematics", "unity.map.cinematic.Cutscene", "unity.map.cinematic.Cutscene$Pos", "unity.map.cinematic.cutscenes.CallbackCutscene", "unity.map.cinematic.cutscenes.PanCutscene", "unity.map.cinematic.Speeches", "unity.map.cinematic.StoryNode", "unity.map.GlobalObjective", "unity.map.objectives.Objective", "unity.map.objectives.ObjectiveModel", "unity.map.objectives.ObjectiveModel$ObjConstructor", "unity.map.objectives.ObjectiveModel$FieldTranslator", "unity.map.objectives.ObjectiveModel$ObjectiveData", "unity.map.objectives.types.CustomObj", "unity.map.objectives.types.ResourceAmountObj", "unity.map.objectives.types.UnitPosObj", "unity.map.planets.ElectrodePlanetGenerator", "unity.map.planets.MegalithPlanetGenerator", "unity.map.ScriptedSector", "unity.map.UnityWaves", "unity.map.UnityWaves$WaveBuilder", "unity.mod.AntiCheat", "unity.mod.AntiCheat$EntitySampler", "unity.mod.AntiCheat$UnitQueue", "unity.mod.AntiCheat$BuildingQueue", "unity.mod.AntiCheat$DisableRegenStatus", "unity.mod.ContributorList", "unity.mod.ContributorList$ContributionType", "unity.mod.DevBuild", "unity.mod.DevBuildImpl", "unity.mod.Faction", "unity.mod.MusicHandler", "unity.mod.TapHandler", "unity.mod.TapHandler$TapListener", "unity.mod.TimeStop", "unity.mod.TimeStop$TimeStopEntity", "unity.mod.Triggers", "unity.StructDefs", "unity.StructDefs$Float2Struct", "unity.StructDefs$Bool3Struct", "unity.sync.packets.BasePacket", "unity.sync.UnityCall", "unity.type.AnimatedItem", "unity.type.AntiCheatVariables", "unity.type.CloneableSetWeapon", "unity.type.CubeUnitType", "unity.type.CubeUnitType$CubeEntityData", "unity.type.DebrisWeather", "unity.type.decal.CapeDecorationType", "unity.type.decal.CapeDecorationType$CapeDecoration", "unity.type.decal.CapeDecorationType$CapeEffectData", "unity.type.decal.FlagellaDecorationType", "unity.type.decal.FlagellaDecorationType$FlagellaDecoration", "unity.type.decal.FlagellaDecorationType$FlagellaSegment", "unity.type.decal.UnitDecorationType", "unity.type.decal.UnitDecorationType$UnitDecoration", "unity.type.decal.WingDecorationType", "unity.type.decal.WingDecorationType$Wing", "unity.type.decal.WingDecorationType$WingDecoration", "unity.type.Engine", "unity.type.Engine$MultiEngine", "unity.type.Engine$MultiEngine$EngineHold", "unity.type.ExpUpgrade", "unity.type.InvisibleUnitType", "unity.type.RainbowUnitType", "unity.type.TentacleType", "unity.type.UnderworldBlock", "unity.type.UnityUnitType", "unity.type.weapons.AcceleratingWeapon", "unity.type.weapons.AcceleratingWeapon$AcceleratingMount", "unity.type.weapons.EnergyChargeWeapon", "unity.type.weapons.EnergyChargeWeapon$ChargeMount", "unity.type.weapons.LimitedAngleWeapon", "unity.type.weapons.monolith.ChargeShotgunWeapon", "unity.type.weapons.monolith.ChargeShotgunWeapon$ChargeShotgunMount", "unity.type.weapons.monolith.EnergyRingWeapon", "unity.type.weapons.monolith.EnergyRingWeapon$Ring", "unity.type.weapons.monolith.EnergyRingWeapon$EnergyRingMount", "unity.type.weapons.MortarWeapon", "unity.type.weapons.MortarWeapon$MortarMount", "unity.type.weapons.MultiBarrelWeapon", "unity.type.weapons.MultiBarrelWeapon$MultiBarrelMount", "unity.type.weapons.MultiTargetPointDefenceWeapon", "unity.type.weapons.MultiTargetPointDefenceWeapon$MultiTargetPointDefenceMount", "unity.type.weapons.PointDefenceMultiBarrelWeapon", "unity.type.weapons.SweepWeapon", "unity.type.weapons.SweepWeapon$SweepWeaponMount", "unity.type.weapons.TractorBeamWeapon", "unity.type.weapons.TractorBeamWeapon$TractorBeamMount", "unity.type.WormDecal", "unity.ui.dialogs.canvas.CinematicCanvas", "unity.ui.dialogs.canvas.CinematicCanvas$NodeElem", "unity.ui.dialogs.CinematicDialog", "unity.ui.dialogs.CreditsDialog", "unity.ui.dialogs.CreditsDialog$ModLink", "unity.ui.dialogs.CrucibleDialog", "unity.ui.dialogs.ObjectivesDialog", "unity.ui.dialogs.ObjectivesDialog$ObjectiveElem", "unity.ui.dialogs.ScriptsDictionaryDialog", "unity.ui.dialogs.ScriptsDictionaryDialog$ScriptElem", "unity.ui.dialogs.ScriptsEditorDialog", "unity.ui.dialogs.TagsDialog", "unity.ui.dialogs.TagsDialog$TagElem", "unity.ui.Graph", "unity.ui.IconBar", "unity.ui.IconBar$IconBarStat", "unity.ui.MoveDragListener", "unity.ui.StackedBarChart", "unity.ui.StackedBarChart$BarStat", "unity.ui.UnderworldMap", "unity.ui.UnityStyles", "unity.Unity", "unity.util.AtomicPair", "unity.util.BasicPool", "unity.util.BlockMovement", "unity.util.BlockMovement$BlockMovementUpdater", "unity.util.BoolGrid", "unity.util.CyclicCoordinateDescent", "unity.util.CyclicCoordinateDescent$DefaultBone", "unity.util.CyclicCoordinateDescent$WorldBone", "unity.util.GraphicUtils", "unity.util.JSBridge", "unity.util.MathU", "unity.util.MathU$UParticleConsumer", "unity.util.PitchedSoundLoop", "unity.util.ReflectUtils", "unity.util.TimeReflect", "unity.util.TriJointInverseKinematics", "unity.util.Utils", "unity.util.Utils$Hit", "unity.util.Utils$HitHandler", "unity.util.WavefrontObject", "unity.util.WavefrontObject$Face", "unity.util.WavefrontObject$Vertex", "unity.util.WavefrontObject$Material", "unity.util.WavefrontObject$ShadingType", "unity.util.WavefrontObjectLoader", "unity.util.WavefrontObjectLoader$WavefrontObjectParameters", "unity.util.Wrappers", "unity.util.Wrappers$NumberWrapper", "unity.util.Wrappers$ObjectWrapper", "unity.world.blocks.ConnectedBlock", "unity.world.blocks.defense.HeatWall", "unity.world.blocks.defense.HeatWall$HeatWallBuild", "unity.world.blocks.defense.LevelLimitWall", "unity.world.blocks.defense.LevelLimitWall$LevelLimitWallBuild", "unity.world.blocks.defense.LightWall", "unity.world.blocks.defense.LightWall$LightWallBuild", "unity.world.blocks.defense.LimitWall", "unity.world.blocks.defense.LimitWall$LimitWallBuild", "unity.world.blocks.defense.PowerWall", "unity.world.blocks.defense.PowerWall$PowerWallBuild", "unity.world.blocks.defense.ShieldWall", "unity.world.blocks.defense.ShieldWall$ShieldWallBuild", "unity.world.blocks.defense.turrets.AbsorberTurret", "unity.world.blocks.defense.turrets.AbsorberTurret$AbsorberTurretBuild", "unity.world.blocks.defense.turrets.BarrelsItemTurret", "unity.world.blocks.defense.turrets.BarrelsItemTurret$Barrel", "unity.world.blocks.defense.turrets.BarrelsItemTurret$BarrelsItemTurretBuild", "unity.world.blocks.defense.turrets.BigLaserTurret", "unity.world.blocks.defense.turrets.BlockOverdriveTurret", "unity.world.blocks.defense.turrets.BlockOverdriveTurret$BlockOverdriveTurretBuild", "unity.world.blocks.defense.turrets.BurstPowerTurret", "unity.world.blocks.defense.turrets.BurstPowerTurret$BurstPowerTurretBuild", "unity.world.blocks.defense.turrets.EndGameTurret", "unity.world.blocks.defense.turrets.EndGameTurret$EndGameTurretBuilding", "unity.world.blocks.defense.turrets.EndLaserTurret", "unity.world.blocks.defense.turrets.EndLaserTurret$EndLaserTurretBuild", "unity.world.blocks.defense.turrets.GenericTractorBeamTurret", "unity.world.blocks.defense.turrets.GenericTractorBeamTurret$GenericTractorBeamTurretBuild", "unity.world.blocks.defense.turrets.HeatRayTurret", "unity.world.blocks.defense.turrets.HeatRayTurret$HeatRayTurretBuild", "unity.world.blocks.defense.turrets.LifeStealerTurret", "unity.world.blocks.defense.turrets.LifeStealerTurret$LifeStealerTurretBuild", "unity.world.blocks.defense.turrets.ObjPowerTurret", "unity.world.blocks.defense.turrets.ObjPowerTurret$ObjPowerTurretBuild", "unity.world.blocks.defense.turrets.OrbTurret", "unity.world.blocks.defense.turrets.OrbTurret$OrbTurretBuild", "unity.world.blocks.defense.turrets.PrismTurret", "unity.world.blocks.defense.turrets.PrismTurret$PrismTurretBuild", "unity.world.blocks.defense.turrets.RampupPowerTurret", "unity.world.blocks.defense.turrets.RampupPowerTurret$RampupPowerTurretBuild", "unity.world.blocks.defense.turrets.ShieldTurret", "unity.world.blocks.defense.turrets.ShieldTurret$ShieldTurretBuild", "unity.world.blocks.defense.turrets.SupernovaTurret", "unity.world.blocks.defense.turrets.SupernovaTurret$SupernovaTurretBuild", "unity.world.blocks.defense.turrets.WavefrontTurret", "unity.world.blocks.defense.turrets.WavefrontTurret$WavefrontTurretBuild", "unity.world.blocks.distribution.CruciblePump", "unity.world.blocks.distribution.CruciblePump$CruciblePumpBuild", "unity.world.blocks.distribution.DriveShaft", "unity.world.blocks.distribution.DriveShaft$DriveShaftBuild", "unity.world.blocks.distribution.HeatPipe", "unity.world.blocks.distribution.HeatPipe$HeatPipeBuild", "unity.world.blocks.distribution.InlineGearbox", "unity.world.blocks.distribution.InlineGearbox$InlineGearboxBuild", "unity.world.blocks.distribution.KoruhConveyor", "unity.world.blocks.distribution.KoruhConveyor$KoruhConveyorBuild", "unity.world.blocks.distribution.ShadowedConveyor", "unity.world.blocks.distribution.ShadowedConveyor$ShadowedConveyorBuild", "unity.world.blocks.distribution.SimpleTransmission", "unity.world.blocks.distribution.SimpleTransmission$SimpleTransmissionBuild", "unity.world.blocks.distribution.Teleporter", "unity.world.blocks.distribution.Teleporter$TeleporterBuild", "unity.world.blocks.distribution.UnderPiper", "unity.world.blocks.distribution.UnderPiper$UnderPiperBuild", "unity.world.blocks.effect.ChasisBlock", "unity.world.blocks.effect.ChasisBlock$ChasisBlockBuild", "unity.world.blocks.effect.Reinforcer", "unity.world.blocks.effect.Reinforcer$ReinforcerBuilding", "unity.world.blocks.effect.SoulContainer", "unity.world.blocks.effect.SoulContainer$SoulContainerBuild", "unity.world.blocks.effect.UnityThruster", "unity.world.blocks.effect.UnityThruster$UnityThrusterBuild", "unity.world.blocks.environment.UnityOreBlock", "unity.world.blocks.exp.ClassicProjector", "unity.world.blocks.exp.ClassicProjector$ClassicProjectorBuild", "unity.world.blocks.exp.DiagonalTower", "unity.world.blocks.exp.DiagonalTower$DiagonalTowerBuild", "unity.world.blocks.exp.EField", "unity.world.blocks.exp.EField$ELinear", "unity.world.blocks.exp.EField$ELinearCap", "unity.world.blocks.exp.EField$EExpo", "unity.world.blocks.exp.EField$EExpoZero", "unity.world.blocks.exp.EField$ERational", "unity.world.blocks.exp.EField$EBool", "unity.world.blocks.exp.EField$EList", "unity.world.blocks.exp.ExpBase", "unity.world.blocks.exp.ExpBase$ExpBaseBuild", "unity.world.blocks.exp.ExpHolder", "unity.world.blocks.exp.ExpHub", "unity.world.blocks.exp.ExpHub$ExpHubBuild", "unity.world.blocks.exp.ExpNode", "unity.world.blocks.exp.ExpNode$ExpNodeBuild", "unity.world.blocks.exp.ExpRouter", "unity.world.blocks.exp.ExpRouter$ExpRouterBuild", "unity.world.blocks.exp.ExpTank", "unity.world.blocks.exp.ExpTank$ExpTankBuild", "unity.world.blocks.exp.ExpTower", "unity.world.blocks.exp.ExpTower$ExpTowerBuild", "unity.world.blocks.exp.ExpTurret", "unity.world.blocks.exp.ExpTurret$ExpTurretBuild", "unity.world.blocks.exp.ExpTurret$LinearReloadTime", "unity.world.blocks.exp.KoruhCrafter", "unity.world.blocks.exp.KoruhCrafter$KoruhCrafterBuild", "unity.world.blocks.exp.KoruhReactor", "unity.world.blocks.exp.KoruhReactor$KoruhReactorBuild", "unity.world.blocks.exp.KoruhVault", "unity.world.blocks.exp.KoruhVault$KoruhVaultBuild", "unity.world.blocks.exp.LevelHolder", "unity.world.blocks.exp.MeltingCrafter", "unity.world.blocks.exp.MeltingCrafter$MeltingCrafterBuild", "unity.world.blocks.exp.turrets.BurstChargePowerTurret", "unity.world.blocks.exp.turrets.BurstChargePowerTurret$BurstChargeTurretBuild", "unity.world.blocks.exp.turrets.ExpItemTurret", "unity.world.blocks.exp.turrets.ExpItemTurret$ExpItemTurretBuild", "unity.world.blocks.exp.turrets.ExpItemTurret$ItemEntry", "unity.world.blocks.exp.turrets.ExpLiquidTurret", "unity.world.blocks.exp.turrets.ExpLiquidTurret$ExpLiquidTurretBuild", "unity.world.blocks.exp.turrets.ExpPowerTurret", "unity.world.blocks.exp.turrets.ExpPowerTurret$ExpPowerTurretBuild", "unity.world.blocks.exp.turrets.OmniLiquidTurret", "unity.world.blocks.exp.turrets.OmniLiquidTurret$OmniLiquidTurretBuild", "unity.world.blocks.GraphBlock", "unity.world.blocks.GraphBlock$GraphBuild", "unity.world.blocks.GraphBlockBase", "unity.world.blocks.GraphBlockBase$GraphBuildBase", "unity.world.blocks.light.LightDiffractor", "unity.world.blocks.light.LightDiffractor$LightDiffractorBuild", "unity.world.blocks.light.LightReflector", "unity.world.blocks.light.LightReflector$LightReflectorBuild", "unity.world.blocks.light.LightRouter", "unity.world.blocks.light.LightRouter$LightRouterBuild", "unity.world.blocks.light.LightSource", "unity.world.blocks.light.LightSource$LightSourceBuild", "unity.world.blocks.LoreMessageBlock", "unity.world.blocks.LoreMessageBlock$LoreMessageBuild", "unity.world.blocks.power.Absorber", "unity.world.blocks.power.Absorber$AbsorberBuilding", "unity.world.blocks.power.CombustionHeater", "unity.world.blocks.power.CombustionHeater$CombustionHeaterBuild", "unity.world.blocks.power.ElectricMotor", "unity.world.blocks.power.ElectricMotor$ElectricMotorBuild", "unity.world.blocks.power.HandCrank", "unity.world.blocks.power.HandCrank$HandCrankBuild", "unity.world.blocks.power.HeatGenerator", "unity.world.blocks.power.HeatGenerator$HeatGeneratorBuild", "unity.world.blocks.power.Magnet", "unity.world.blocks.power.Magnet$MagnetBuild", "unity.world.blocks.power.PowerPlant", "unity.world.blocks.power.PowerPlant$PowerPlantBuilding", "unity.world.blocks.power.RotorBlock", "unity.world.blocks.power.RotorBlock$RotorBuild", "unity.world.blocks.power.SolarCollector", "unity.world.blocks.power.SolarCollector$SolarCollectorBuild", "unity.world.blocks.power.SolarReflector", "unity.world.blocks.power.SolarReflector$SolarReflectorBuild", "unity.world.blocks.power.ThermalHeater", "unity.world.blocks.power.ThermalHeater$ThermalHeaterBuild", "unity.world.blocks.power.TorqueGenerator", "unity.world.blocks.power.TorqueGenerator$TorqueGeneratorBuild", "unity.world.blocks.power.WaterTurbine", "unity.world.blocks.power.WaterTurbine$WaterTurbineBuild", "unity.world.blocks.power.WindTurbine", "unity.world.blocks.power.WindTurbine$WindTurbineBuild", "unity.world.blocks.production.AugerDrill", "unity.world.blocks.production.AugerDrill$ArguerDrillBuild", "unity.world.blocks.production.BurnerSmelter", "unity.world.blocks.production.BurnerSmelter$BurnerSmelterBuild", "unity.world.blocks.production.CastingMold", "unity.world.blocks.production.CastingMold$CastingMoldBuild", "unity.world.blocks.production.Crucible", "unity.world.blocks.production.Crucible$CrucibleBuild", "unity.world.blocks.production.DistributionDrill", "unity.world.blocks.production.DistributionDrill$DistributionDrillBuild", "unity.world.blocks.production.ExplosiveSeparator", "unity.world.blocks.production.ExplosiveSeparator$ExplosiveSeparatorBuild", "unity.world.blocks.production.FloorExtractor", "unity.world.blocks.production.FloorExtractor$FloorExtractorBuild", "unity.world.blocks.production.HoldingCrucible", "unity.world.blocks.production.HoldingCrucible$HoldingCrucibleBuild", "unity.world.blocks.production.LiquidsSmelter", "unity.world.blocks.production.MechanicalExtractor", "unity.world.blocks.production.MechanicalExtractor$MechanicalExtractorBuild", "unity.world.blocks.production.Press", "unity.world.blocks.production.Press$PressBuild", "unity.world.blocks.production.SoulInfuser", "unity.world.blocks.production.SoulInfuser$SoulInfuserBuild", "unity.world.blocks.production.SporeFarm", "unity.world.blocks.production.SporeFarm$SporeFarmBuild", "unity.world.blocks.production.SporePyrolyser", "unity.world.blocks.production.SporePyrolyser$SporPyrolyserBuild", "unity.world.blocks.sandbox.ExpSource", "unity.world.blocks.sandbox.ExpSource$ExpSourceBuild", "unity.world.blocks.sandbox.ExpVoid", "unity.world.blocks.sandbox.ExpVoid$ExpVoidBuild", "unity.world.blocks.sandbox.HeatSource", "unity.world.blocks.sandbox.HeatSource$HeatSourceBuild", "unity.world.blocks.units.ConversionPad", "unity.world.blocks.units.ConversionPad$ConversionPadBuild", "unity.world.blocks.units.MechPad", "unity.world.blocks.units.MechPad$MechPadBuild", "unity.world.blocks.units.ModularConstructor", "unity.world.blocks.units.ModularConstructor$ModularConstructorPlan", "unity.world.blocks.units.ModularConstructor$ModularConstructorBuild", "unity.world.blocks.units.ModularConstructorPart", "unity.world.blocks.units.ModularConstructorPart$ModularConstructorPartBuild", "unity.world.blocks.units.SelectableReconstructor", "unity.world.blocks.units.SelectableReconstructor$SelectableReconstructorBuild", "unity.world.blocks.units.TeleUnit", "unity.world.blocks.units.TeleUnit$TeleUnitBuild", "unity.world.blocks.units.TerraCore", "unity.world.blocks.units.TerraCore$TerraCoreBuild", "unity.world.blocks.units.TimeAccelerator", "unity.world.blocks.units.TimeAccelerator$TimeAcceleratorBuild", "unity.world.blocks.units.TimeMine", "unity.world.blocks.units.TimeMine$TimeMineBuild", "unity.world.blocks.units.TimeMineTp", "unity.world.blocks.units.TimeMineTp$TimeMineTpBuild", "unity.world.consumers.ConsumeLiquids", "unity.world.draw.DrawExp", "unity.world.draw.DrawLevel", "unity.world.draw.DrawLightBlock", "unity.world.draw.DrawOver", "unity.world.graph.BaseGraph", "unity.world.graph.BaseGraph$GraphTree", "unity.world.graph.CrucibleGraph", "unity.world.graph.FluxGraph", "unity.world.graph.HeatGraph", "unity.world.graph.TorqueGraph", "unity.world.graphs.Graph", "unity.world.graphs.GraphCrucible", "unity.world.graphs.GraphFlux", "unity.world.graphs.GraphHeat", "unity.world.graphs.Graphs", "unity.world.graphs.GraphTorque", "unity.world.graphs.GraphTorqueConsume", "unity.world.graphs.GraphTorqueGenerate", "unity.world.graphs.GraphTorqueTrans", "unity.world.LightAcceptor", "unity.world.LightAcceptorType", "unity.world.meta.CrucibleData", "unity.world.meta.CrucibleRecipe", "unity.world.meta.CrucibleRecipe$InputRecipe", "unity.world.meta.DynamicProgression", "unity.world.meta.GraphData", "unity.world.meta.GraphType", "unity.world.meta.LightData", "unity.world.meta.MeltInfo", "unity.world.meta.StemData", "unity.world.modules.GraphCrucibleModule", "unity.world.modules.GraphFluxModule", "unity.world.modules.GraphHeatModule", "unity.world.modules.GraphModule", "unity.world.modules.GraphModules", "unity.world.modules.GraphTorqueConsumeModule", "unity.world.modules.GraphTorqueGenerateModule", "unity.world.modules.GraphTorqueModule", "unity.world.modules.GraphTorqueTransModule", "unity.world.modules.ModularConstructorModule", "unity.world.modules.ModularConstructorModule$ModularConstructorModuleInterface", "unity.world.modules.ModularConstructorModule$ModularConstructorGraph", "unity.world.MonolithWorld", "unity.world.MonolithWorld$Chunk", "younggamExperimental.blocks.Chopper", "younggamExperimental.blocks.Chopper$ChopperBuild", "younggamExperimental.blocks.ModularTurret", "younggamExperimental.blocks.ModularTurret$ModularTurretBuild", "younggamExperimental.ConnectData", "younggamExperimental.IntPacker", "younggamExperimental.ModularConstructorUI", "younggamExperimental.ModularConstructorUI$PartPlaceObj", "younggamExperimental.PartInfo", "younggamExperimental.PartStat", "younggamExperimental.PartStatType", "younggamExperimental.PartType", "younggamExperimental.Segment", "younggamExperimental.StatContainer", "younggamExperimental.TurretBaseUpdater", "younggamExperimental.UnityParts", "younggamExperimental.UnityParts$PartSize", "unity.gen.FactionMeta", "unity.gen.Regions", "unity.gen.Regions$Outline", "unity.gen.Assistantc", "unity.gen.Boostc", "unity.gen.Bossc", "unity.gen.CLegc", "unity.gen.Copterc", "unity.gen.CTrailc", "unity.gen.Cubec", "unity.gen.CutEffectc", "unity.gen.Decorationc", "unity.gen.EndBulletc", "unity.gen.Endc", "unity.gen.Factionc", "unity.gen.Imberc", "unity.gen.Invisiblec", "unity.gen.KamiBulletc", "unity.gen.Kamic", "unity.gen.KamiLaserc", "unity.gen.Lightc", "unity.gen.Monolithc", "unity.gen.MonolithSoulc", "unity.gen.Oppressionc", "unity.gen.SlowLightningc", "unity.gen.Tallc", "unity.gen.Tentaclec", "unity.gen.Test2c", "unity.gen.Test3c", "unity.gen.Test4c", "unity.gen.Testc", "unity.gen.TimeStopBulletc", "unity.gen.TimeStopVelc", "unity.gen.Transc", "unity.gen.TriJointLegsc", "unity.gen.Trnsc", "unity.gen.Unintersectablec", "unity.gen.Worldc", "unity.gen.Wormc", "unity.gen.Expc", "unity.gen.Expc$ExpBuildc", "unity.gen.LightHoldc", "unity.gen.LightHoldc$LightHoldBuildc", "unity.gen.Soulc", "unity.gen.Soulc$SoulBuildc", "unity.gen.Stemc", "unity.gen.Stemc$StemBuildc", "unity.gen.Turretc", "unity.gen.Turretc$TurretBuildc", "unity.gen.UnitySounds", "unity.gen.UnityObjs", "unity.gen.UnityModels", "unity.gen.SColor", "unity.gen.Bool3", "unity.gen.Float2", "unity.gen.SVec2", "unity.gen.ExpKoruhConveyor", "unity.gen.ExpKoruhConveyor$ExpKoruhConveyorBuild", "unity.gen.ExpLimitWall", "unity.gen.ExpLimitWall$ExpLimitWallBuild", "unity.gen.ExpForceProjector", "unity.gen.ExpForceProjector$ExpForceProjectorBuild", "unity.gen.ExpLBase", "unity.gen.ExpLBase$ExpLBaseBuild", "unity.gen.UnityEntityMapping", "unity.gen.MonolithMechUnit", "unity.gen.MonolithLegsUnit", "unity.gen.MonolithAssistantUnit", "unity.gen.MonolithCTrailUnit", "unity.gen.CopterUnit", "unity.gen.TransWaterMoveUnit", "unity.gen.TransLegsUnit", "unity.gen.WorldUnit", "unity.gen.ImberUnit", "unity.gen.TriJointLegsUnit", "unity.gen.CLegUnit", "unity.gen.WormUnit", "unity.gen.DecorationUnit", "unity.gen.BoostEndUnit", "unity.gen.EndUnit", "unity.gen.TimeStopVelEndUnit", "unity.gen.InvisibleEndUnit", "unity.gen.WormEndUnit", "unity.gen.TentacleInvisibleEndUnit", "unity.gen.LegsEndUnit", "unity.gen.TallTentacleLegsEndUnit", "unity.gen.DecorationWaterMoveEndUnit", "unity.gen.MonolithDecorationMechUnit", "unity.gen.CutEffect", "unity.gen.EndBullet", "unity.gen.KamiBullet", "unity.gen.Kami", "unity.gen.KamiLaser", "unity.gen.Light", "unity.gen.MonolithSoul", "unity.gen.Oppression", "unity.gen.SlowLightning", "unity.gen.Test4", "unity.gen.Test", "unity.gen.TimeStopBullet", "unity.gen.Trns", "unity.gen.StemGenericCrafter", "unity.gen.StemGenericCrafter$StemGenericCrafterBuild", "unity.gen.LightHoldGenericCrafter", "unity.gen.LightHoldGenericCrafter$LightHoldGenericCrafterBuild", "unity.gen.SoulFloorExtractor", "unity.gen.SoulFloorExtractor$SoulFloorExtractorBuild", "unity.gen.SoulGenericCrafter", "unity.gen.SoulGenericCrafter$SoulGenericCrafterBuild", "unity.gen.SoulLifeStealerTurret", "unity.gen.SoulLifeStealerTurret$SoulLifeStealerTurretBuild", "unity.gen.SoulAbsorberTurret", "unity.gen.SoulAbsorberTurret$SoulAbsorberTurretBuild", "unity.gen.SoulHeatRayTurret", "unity.gen.SoulHeatRayTurret$SoulHeatRayTurretBuild", "unity.gen.SoulTurretPowerTurret", "unity.gen.SoulTurretPowerTurret$SoulTurretPowerTurretBuild", "unity.gen.SoulTurretItemTurret", "unity.gen.SoulTurretItemTurret$SoulTurretItemTurretBuild", "unity.gen.SoulTurretBurstPowerTurret", "unity.gen.SoulTurretBurstPowerTurret$SoulTurretBurstPowerTurretBuild", "unity.gen.LightHoldWall", "unity.gen.LightHoldWall$LightHoldWallBuild", "unity.gen.SoulPowerTurret", "unity.gen.SoulPowerTurret$SoulPowerTurretBuild", "unity.gen.SoulLaserTurret", "unity.gen.SoulLaserTurret$SoulLaserTurretBuild", "unity.gen.SoulBlock", "unity.gen.SoulBlock$SoulBuild", "unity.gen.LightHoldBlock", "unity.gen.LightHoldBlock$LightHoldBuild"});
    public static final Seq<String> packages = Seq.with(new String[]{"java.lang", "java.util", "java.io", "rhino", "unity.ai", "unity.ai.kami", "unity.assets.list", "unity.assets.loaders", "unity.assets.type.g3d", "unity.assets.type.g3d.attribute", "unity.assets.type.g3d.attribute.light", "unity.assets.type.g3d.attribute.type", "unity.assets.type.g3d.attribute.type.light", "unity.assets.type.g3d.model", "unity.async", "unity.content.effects", "unity.content", "unity.content.units", "unity.editor", "unity.entities.abilities", "unity.entities", "unity.entities.bullet.anticheat", "unity.entities.bullet.anticheat.modules", "unity.entities.bullet.energy", "unity.entities.bullet.exp", "unity.entities.bullet.kami", "unity.entities.bullet.laser", "unity.entities.bullet.misc", "unity.entities.bullet.monolith.energy", "unity.entities.bullet.monolith.laser", "unity.entities.bullet.physical", "unity.entities.effects", "unity.entities.legs", "unity.entities.units", "unity.graphics", "unity.logic", "unity.map.cinematic", "unity.map.cinematic.cutscenes", "unity.map", "unity.map.objectives", "unity.map.objectives.types", "unity.map.planets", "unity.mod", "unity", "unity.sync.packets", "unity.sync", "unity.type", "unity.type.decal", "unity.type.weapons", "unity.type.weapons.monolith", "unity.ui.dialogs.canvas", "unity.ui.dialogs", "unity.ui", "unity.util", "unity.world.blocks", "unity.world.blocks.defense", "unity.world.blocks.defense.turrets", "unity.world.blocks.distribution", "unity.world.blocks.effect", "unity.world.blocks.environment", "unity.world.blocks.exp", "unity.world.blocks.exp.turrets", "unity.world.blocks.light", "unity.world.blocks.power", "unity.world.blocks.production", "unity.world.blocks.sandbox", "unity.world.blocks.units", "unity.world.consumers", "unity.world.draw", "unity.world.graph", "unity.world.graphs", "unity.world", "unity.world.meta", "unity.world.modules", "younggamExperimental.blocks", "younggamExperimental", "unity.gen"});

    public Unity() {
        this(false);
    }

    public Unity(boolean tools) {
        Unity.tools = tools;
        if (!Vars.headless) {
            Core.assets.setLoader(Model.class, ".g3dj", new ModelLoader(Vars.tree, new JsonReader()));
            Core.assets.setLoader(Model.class, ".g3db", new ModelLoader(Vars.tree, new UBJsonReader()));
            Core.assets.setLoader(WavefrontObject.class, new WavefrontObjectLoader(Vars.tree));
            final String fontSuff = ".gen_pu";
            Core.assets.setLoader(FreeTypeFontGenerator.class, fontSuff, new FreeTypeFontGeneratorLoader(Vars.tree) {
                public FreeTypeFontGenerator load(AssetManager assetManager, String fileName, Fi file, FreeTypeFontGeneratorLoader.FreeTypeFontGeneratorParameters parameter) {
                    return new FreeTypeFontGenerator(Vars.tree.get(file.pathWithoutExtension()));
                }
            });
            Core.assets.setLoader(Font.class, "-pu", new FreetypeFontLoader(Vars.tree) {
                public Font loadSync(AssetManager manager, String fileName, Fi file, FreetypeFontLoader.FreeTypeFontLoaderParameter parameter) {
                    if (parameter == null) {
                        throw new IllegalArgumentException("parameter is null");
                    } else {
                        FreeTypeFontGenerator generator = (FreeTypeFontGenerator)manager.get(parameter.fontFileName + fontSuff, FreeTypeFontGenerator.class);
                        return generator.generateFont(parameter.fontParameters);
                    }
                }

                public Seq<AssetDescriptor> getDependencies(String fileName, Fi file, FreetypeFontLoader.FreeTypeFontLoaderParameter parameter) {
                    return Seq.with(new AssetDescriptor[]{new AssetDescriptor(parameter.fontFileName + fontSuff, FreeTypeFontGenerator.class)});
                }
            });
        }

        Events.on(EventType.ContentInitEvent.class, (e) -> {
            if (!Vars.headless) {
                Regions.load();
                KamiRegions.load();
            }

            UnityFonts.load();
            UnityStyles.load();
        });
        Events.on(EventType.FileTreeInitEvent.class, (e) -> Core.app.post(() -> {
            UnityShaders.load();
            UnityObjs.load();
            UnityModels.load();
            UnitySounds.load();
        }));
        Events.on(EventType.ClientLoadEvent.class, (e) -> {
            creditsDialog = new CreditsDialog();
            jsEditDialog = new ScriptsEditorDialog();
            jsDictDialog = new ScriptsDictionaryDialog();
            cinematicDialog = new CinematicDialog();
            tagsDialog = new TagsDialog();
            objectivesDialog = new ObjectivesDialog();
            this.addCredits();
            UnitySettings.init();
            Speeches.init();
            Cutscene.init();
            Triggers.listen(Trigger.preDraw, () -> {
                Camera cam = Core.camera;
                Camera3D cam3D = Models.camera;
                cam3D.position.set(cam.position.x, cam.position.y, 50.0F);
                cam3D.resize(cam.width, cam.height);
                cam3D.update();
            });
            Mods.LoadedMod mod = Vars.mods.getMod(Unity.class);
            Func<String, String> stringf = (value) -> Core.bundle.get("mod." + mod.name + "." + value);
            mod.meta.displayName = (String)stringf.get("name");
            mod.meta.description = (String)stringf.get("description");
            Core.settings.getBoolOnce("unity-install", () -> Time.runTask(5.0F, CreditsDialog::showList));
        });
        Utils.init();
        TimeStop.init();
        TimeReflect.init();

        try {
            Class<? extends DevBuild> impl = Class.forName("unity.mod.DevBuildImpl");
            dev = (DevBuild)impl.getDeclaredConstructor().newInstance();
            print("Dev build class implementation found and instantiated.");
        } catch (Throwable var3) {
            print("Dev build class implementation not found; defaulting to regular user implementation.");
            dev = new DevBuild() {
            };
        }

        if (dev.isDev()) {
            Log.level = LogLevel.debug;
        }

        music = new MusicHandler() {
        };
        tap = new TapHandler();
        antiCheat = new AntiCheat();
        monolithWorld = new MonolithWorld();
        cinematicEditor = new CinematicEditor();
        Vars.asyncCore.processes.add(lights = new LightProcess(), scoring = new ContentScoreProcess());
        Core.app.post(() -> {
            JSBridge.init();
            JSBridge.importDefaults(JSBridge.unityScope);
        });
    }

    public void init() {
        music.setup();
        antiCheat.setup();
        dev.setup();
        UnityCall.init();
        BlockMovement.init();
        dev.init();
    }

    public void loadContent() {
        Faction.init();
        UnityItems.load();
        UnityStatusEffects.load();
        UnityWeathers.load();
        UnityLiquids.load();
        UnityBullets.load();
        UnityWeaponTemplates.load();
        UnityUnitTypes.load();
        UnityBlocks.load();
        UnityPlanets.load();
        UnitySectorPresets.load();
        UnityTechTree.load();
        UnityParts.load();
        Overwriter.load();
        FactionMeta.init();
        UnityEntityMapping.init();
        this.logContent();
    }

    public void logContent() {
        for(Faction faction : Faction.all) {
            Seq<Object> array = FactionMeta.getByFaction(faction, Object.class);
            print(LogLevel.debug, "", Strings.format("Faction @ has @ contents.", new Object[]{faction, array.size}));
        }

        Seq<Class<?>> ignored = Seq.with(new Class[]{Floor.class, Prop.class});

        for(Seq<Content> content : Vars.content.getContentMap()) {
            content.each((c) -> {
                if (c.minfo.mod != null && c.minfo.mod.main == this && c instanceof UnlockableContent) {
                    UnlockableContent cont = (UnlockableContent)c;
                    if (Core.bundle.getOrNull(cont.getContentType() + "." + cont.name + ".name") == null) {
                        print(LogLevel.debug, "", Strings.format("@ has no bundle entry for name", new Object[]{cont}));
                    }

                    if (!ignored.contains((t) -> t.isAssignableFrom(cont.getClass())) && Core.bundle.getOrNull(cont.getContentType() + "." + cont.name + ".description") == null) {
                        print(LogLevel.debug, "", Strings.format("@ has no bundle entry for description", new Object[]{cont}));
                    }

                }
            });
        }

    }

    protected void addCredits() {
        try {
            Group group = (Group)Vars.ui.menuGroup.getChildren().first();
            if (!Vars.mobile) {
                group.fill((c) -> {
                    Table var10000 = c.bottom().left();
                    TextButton.TextButtonStyle var10002 = UnityStyles.creditst;
                    CreditsDialog var10003 = creditsDialog;
                    Objects.requireNonNull(var10003);
                    var10000.button("", var10002, var10003::show).size(84.0F, 45.0F).name("unity credits");
                });
            }
        } catch (Throwable t) {
            print(LogLevel.err, "Couldn't create Unity's credits button", Strings.getFinalCause(t));
        }

    }

    public static void print(Object... args) {
        print(LogLevel.info, " ", args);
    }

    public static void print(Log.LogLevel level, Object... args) {
        print(level, " ", args);
    }

    public static void print(Log.LogLevel level, String separator, Object... args) {
        StringBuilder builder = new StringBuilder();
        if (args == null) {
            builder.append("null");
        } else {
            for(int i = 0; i < args.length; ++i) {
                builder.append(args[i]);
                if (i < args.length - 1) {
                    builder.append(separator);
                }
            }
        }

        Log.log(level, "&lm&fb[unity]&fr @", new Object[]{builder.toString()});
    }
}
