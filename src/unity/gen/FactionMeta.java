package unity.gen;

import arc.audio.Music;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.ctype.Content;
import mindustry.ctype.ContentType;
import mindustry.ctype.UnlockableContent;
import unity.content.UnityBlocks;
import unity.content.UnityPlanets;
import unity.content.UnitySectorPresets;
import unity.content.UnityUnitTypes;
import unity.content.units.MonolithUnitTypes;
import unity.mod.Faction;

public class FactionMeta {
    private static ObjectMap<Object, Faction> map = new ObjectMap();
    private static ObjectMap<Music, String> music = new ObjectMap();

    public static Faction map(Object content) {
        return (Faction)map.get(content);
    }

    public static void put(Object content, Faction faction) {
        map.put(content, faction);
        if (content instanceof UnlockableContent) {
            UnlockableContent unlockable = (UnlockableContent)content;
            unlockable.description = unlockable.description + "\n[gray]Faction:[] " + faction.localizedName;
        }

    }

    public static <T> Seq<T> getByFaction(Faction faction, Class<T> type) {
        Seq<T> contents = new Seq();
        map.keys().toSeq().select((o) -> ((Faction)map.get(o)).equals(faction) && type.isAssignableFrom(o.getClass())).each((o) -> contents.add(o));
        return contents;
    }

    public static <T extends Content> Seq<T> getByCtype(ContentType ctype) {
        Seq<T> contents = new Seq();

        for(Object o : map.keys().toSeq()) {
            if (o instanceof Content) {
                Content c = (Content)o;
                if (c.getContentType().equals(ctype)) {
                    contents.add(c);
                }
            }
        }

        return contents;
    }

    public static Seq<Music> getMusicCategory(Music mus) {
        String category = (String)music.get(mus);
        if (category == null) {
            return Vars.control.sound.ambientMusic;
        } else {
            Seq var10000;
            switch (category) {
                case "ambient":
                    var10000 = Vars.control.sound.ambientMusic;
                    break;
                case "dark":
                    var10000 = Vars.control.sound.darkMusic;
                    break;
                case "boss":
                    var10000 = Vars.control.sound.bossMusic;
                    break;
                default:
                    throw new IllegalArgumentException("Unknown category: " + category);
            }

            return var10000;
        }
    }

    public static void init() {
        put(MonolithUnitTypes.monolithSoul, Faction.monolith);
        put(MonolithUnitTypes.stele, Faction.monolith);
        put(MonolithUnitTypes.pedestal, Faction.monolith);
        put(MonolithUnitTypes.pilaster, Faction.monolith);
        put(MonolithUnitTypes.pylon, Faction.monolith);
        put(MonolithUnitTypes.monument, Faction.monolith);
        put(MonolithUnitTypes.colossus, Faction.monolith);
        put(MonolithUnitTypes.bastion, Faction.monolith);
        put(MonolithUnitTypes.adsect, Faction.monolith);
        put(MonolithUnitTypes.comitate, Faction.monolith);
        put(MonolithUnitTypes.stray, Faction.monolith);
        put(MonolithUnitTypes.tendence, Faction.monolith);
        put(MonolithUnitTypes.liminality, Faction.monolith);
        put(MonolithUnitTypes.calenture, Faction.monolith);
        put(MonolithUnitTypes.hallucination, Faction.monolith);
        put(MonolithUnitTypes.escapism, Faction.monolith);
        put(MonolithUnitTypes.fantasy, Faction.monolith);
        put(UnityBlocks.oreUmbrium, Faction.dark);
        put(UnityBlocks.apparition, Faction.dark);
        put(UnityBlocks.ghost, Faction.dark);
        put(UnityBlocks.banshee, Faction.dark);
        put(UnityBlocks.fallout, Faction.dark);
        put(UnityBlocks.catastrophe, Faction.dark);
        put(UnityBlocks.calamity, Faction.dark);
        put(UnityBlocks.extinction, Faction.dark);
        put(UnityBlocks.darkWall, Faction.dark);
        put(UnityBlocks.darkWallLarge, Faction.dark);
        put(UnityBlocks.darkAlloyForge, Faction.dark);
        put(UnityBlocks.oreLuminum, Faction.light);
        put(UnityBlocks.photon, Faction.light);
        put(UnityBlocks.electron, Faction.light);
        put(UnityBlocks.graviton, Faction.light);
        put(UnityBlocks.proton, Faction.light);
        put(UnityBlocks.neutron, Faction.light);
        put(UnityBlocks.gluon, Faction.light);
        put(UnityBlocks.wBoson, Faction.light);
        put(UnityBlocks.zBoson, Faction.light);
        put(UnityBlocks.higgsBoson, Faction.light);
        put(UnityBlocks.singularity, Faction.light);
        put(UnityBlocks.muon, Faction.light);
        put(UnityBlocks.ephemeron, Faction.light);
        put(UnityBlocks.lightLamp, Faction.light);
        put(UnityBlocks.oilLamp, Faction.light);
        put(UnityBlocks.lightLampInfi, Faction.light);
        put(UnityBlocks.lightReflector, Faction.light);
        put(UnityBlocks.lightDivisor, Faction.light);
        put(UnityBlocks.metaglassWall, Faction.light);
        put(UnityBlocks.metaglassWallLarge, Faction.light);
        put(UnityBlocks.lightForge, Faction.light);
        put(UnityBlocks.terraCore, Faction.scar);
        put(UnityBlocks.oreImberium, Faction.imber);
        put(UnityBlocks.electroTile, Faction.imber);
        put(UnityBlocks.orb, Faction.imber);
        put(UnityBlocks.shockwire, Faction.imber);
        put(UnityBlocks.current, Faction.imber);
        put(UnityBlocks.plasma, Faction.imber);
        put(UnityBlocks.electrobomb, Faction.imber);
        put(UnityBlocks.shielder, Faction.imber);
        put(UnityBlocks.orbTurret, Faction.imber);
        put(UnityBlocks.powerPlant, Faction.imber);
        put(UnityBlocks.absorber, Faction.imber);
        put(UnityBlocks.piper, Faction.imber);
        put(UnityBlocks.sparkAlloyForge, Faction.imber);
        put(UnityBlocks.denseSmelter, Faction.koruh);
        put(UnityBlocks.solidifier, Faction.koruh);
        put(UnityBlocks.steelSmelter, Faction.koruh);
        put(UnityBlocks.liquifier, Faction.koruh);
        put(UnityBlocks.titaniumExtractor, Faction.koruh);
        put(UnityBlocks.lavaSmelter, Faction.koruh);
        put(UnityBlocks.diriumCrucible, Faction.koruh);
        put(UnityBlocks.coalExtractor, Faction.koruh);
        put(UnityBlocks.stoneWall, Faction.koruh);
        put(UnityBlocks.denseWall, Faction.koruh);
        put(UnityBlocks.steelWall, Faction.koruh);
        put(UnityBlocks.steelWallLarge, Faction.koruh);
        put(UnityBlocks.diriumWall, Faction.koruh);
        put(UnityBlocks.diriumWallLarge, Faction.koruh);
        put(UnityBlocks.shieldProjector, Faction.koruh);
        put(UnityBlocks.diriumProjector, Faction.koruh);
        put(UnityBlocks.timeMine, Faction.koruh);
        put(UnityBlocks.shieldWall, Faction.koruh);
        put(UnityBlocks.shieldWallLarge, Faction.koruh);
        put(UnityBlocks.steelConveyor, Faction.koruh);
        put(UnityBlocks.teleporter, Faction.koruh);
        put(UnityBlocks.teleunit, Faction.koruh);
        put(UnityBlocks.diriumConveyor, Faction.koruh);
        put(UnityBlocks.bufferPad, Faction.koruh);
        put(UnityBlocks.omegaPad, Faction.koruh);
        put(UnityBlocks.cachePad, Faction.koruh);
        put(UnityBlocks.convertPad, Faction.koruh);
        put(UnityBlocks.uraniumReactor, Faction.koruh);
        put(UnityBlocks.expFountain, Faction.koruh);
        put(UnityBlocks.expVoid, Faction.koruh);
        put(UnityBlocks.expTank, Faction.koruh);
        put(UnityBlocks.expChest, Faction.koruh);
        put(UnityBlocks.expRouter, Faction.koruh);
        put(UnityBlocks.expTower, Faction.koruh);
        put(UnityBlocks.expTowerDiagonal, Faction.koruh);
        put(UnityBlocks.bufferTower, Faction.koruh);
        put(UnityBlocks.expHub, Faction.koruh);
        put(UnityBlocks.expNode, Faction.koruh);
        put(UnityBlocks.expNodeLarge, Faction.koruh);
        put(UnityBlocks.laser, Faction.koruh);
        put(UnityBlocks.laserCharge, Faction.koruh);
        put(UnityBlocks.laserBranch, Faction.koruh);
        put(UnityBlocks.laserFractal, Faction.koruh);
        put(UnityBlocks.laserBreakthrough, Faction.koruh);
        put(UnityBlocks.laserFrost, Faction.koruh);
        put(UnityBlocks.laserKelvin, Faction.koruh);
        put(UnityBlocks.inferno, Faction.koruh);
        put(UnityBlocks.buffTurret, Faction.koruh);
        put(UnityBlocks.upgradeTurret, Faction.koruh);
        put(UnityBlocks.oreMonolite, Faction.monolith);
        put(UnityBlocks.sharpslate, Faction.monolith);
        put(UnityBlocks.sharpslateWall, Faction.monolith);
        put(UnityBlocks.infusedSharpslate, Faction.monolith);
        put(UnityBlocks.infusedSharpslateWall, Faction.monolith);
        put(UnityBlocks.archSharpslate, Faction.monolith);
        put(UnityBlocks.archEnergy, Faction.monolith);
        put(UnityBlocks.loreMonolith, Faction.monolith);
        put(UnityBlocks.debrisExtractor, Faction.monolith);
        put(UnityBlocks.soulInfuser, Faction.monolith);
        put(UnityBlocks.monolithAlloyForge, Faction.monolith);
        put(UnityBlocks.electrophobicWall, Faction.monolith);
        put(UnityBlocks.electrophobicWallLarge, Faction.monolith);
        put(UnityBlocks.lifeStealer, Faction.monolith);
        put(UnityBlocks.absorberAura, Faction.monolith);
        put(UnityBlocks.heatRay, Faction.monolith);
        put(UnityBlocks.incandescence, Faction.monolith);
        put(UnityBlocks.ricochet, Faction.monolith);
        put(UnityBlocks.shellshock, Faction.monolith);
        put(UnityBlocks.purge, Faction.monolith);
        put(UnityBlocks.blackout, Faction.monolith);
        put(UnityBlocks.diviner, Faction.monolith);
        put(UnityBlocks.mage, Faction.monolith);
        put(UnityBlocks.recluse, Faction.monolith);
        put(UnityBlocks.oracle, Faction.monolith);
        put(UnityBlocks.prism, Faction.monolith);
        put(UnityBlocks.supernova, Faction.monolith);
        put(UnityBlocks.oreNickel, Faction.youngcha);
        put(UnityBlocks.concreteBlank, Faction.youngcha);
        put(UnityBlocks.concreteFill, Faction.youngcha);
        put(UnityBlocks.concreteNumber, Faction.youngcha);
        put(UnityBlocks.concreteStripe, Faction.youngcha);
        put(UnityBlocks.concrete, Faction.youngcha);
        put(UnityBlocks.stoneFullTiles, Faction.youngcha);
        put(UnityBlocks.stoneFull, Faction.youngcha);
        put(UnityBlocks.stoneHalf, Faction.youngcha);
        put(UnityBlocks.stoneTiles, Faction.youngcha);
        put(UnityBlocks.smallTurret, Faction.youngcha);
        put(UnityBlocks.medTurret, Faction.youngcha);
        put(UnityBlocks.chopper, Faction.youngcha);
        put(UnityBlocks.augerDrill, Faction.youngcha);
        put(UnityBlocks.mechanicalExtractor, Faction.youngcha);
        put(UnityBlocks.sporeFarm, Faction.youngcha);
        put(UnityBlocks.mechanicalConveyor, Faction.youngcha);
        put(UnityBlocks.heatPipe, Faction.youngcha);
        put(UnityBlocks.driveShaft, Faction.youngcha);
        put(UnityBlocks.inlineGearbox, Faction.youngcha);
        put(UnityBlocks.shaftRouter, Faction.youngcha);
        put(UnityBlocks.simpleTransmission, Faction.youngcha);
        put(UnityBlocks.crucible, Faction.youngcha);
        put(UnityBlocks.holdingCrucible, Faction.youngcha);
        put(UnityBlocks.cruciblePump, Faction.youngcha);
        put(UnityBlocks.castingMold, Faction.youngcha);
        put(UnityBlocks.sporePyrolyser, Faction.youngcha);
        put(UnityBlocks.smallRadiator, Faction.youngcha);
        put(UnityBlocks.thermalHeater, Faction.youngcha);
        put(UnityBlocks.combustionHeater, Faction.youngcha);
        put(UnityBlocks.solarCollector, Faction.youngcha);
        put(UnityBlocks.solarReflector, Faction.youngcha);
        put(UnityBlocks.nickelStator, Faction.youngcha);
        put(UnityBlocks.nickelStatorLarge, Faction.youngcha);
        put(UnityBlocks.nickelElectromagnet, Faction.youngcha);
        put(UnityBlocks.electricRotorSmall, Faction.youngcha);
        put(UnityBlocks.electricRotor, Faction.youngcha);
        put(UnityBlocks.handCrank, Faction.youngcha);
        put(UnityBlocks.windTurbine, Faction.youngcha);
        put(UnityBlocks.waterTurbine, Faction.youngcha);
        put(UnityBlocks.electricMotor, Faction.youngcha);
        put(UnityBlocks.cupronickelWall, Faction.youngcha);
        put(UnityBlocks.cupronickelWallLarge, Faction.youngcha);
        put(UnityBlocks.smallThruster, Faction.youngcha);
        put(UnityBlocks.infiHeater, Faction.youngcha);
        put(UnityBlocks.infiCooler, Faction.youngcha);
        put(UnityBlocks.infiTorque, Faction.youngcha);
        put(UnityBlocks.neodymiumStator, Faction.youngcha);
        put(UnityBlocks.advanceConstructorModule, Faction.advance);
        put(UnityBlocks.advanceConstructor, Faction.advance);
        put(UnityBlocks.celsius, Faction.advance);
        put(UnityBlocks.kelvin, Faction.advance);
        put(UnityBlocks.caster, Faction.advance);
        put(UnityBlocks.storm, Faction.advance);
        put(UnityBlocks.eclipse, Faction.advance);
        put(UnityBlocks.xenoCorruptor, Faction.advance);
        put(UnityBlocks.cube, Faction.advance);
        put(UnityBlocks.wavefront, Faction.advance);
        put(UnityBlocks.terminalCrucible, Faction.end);
        put(UnityBlocks.endForge, Faction.end);
        put(UnityBlocks.endGame, Faction.end);
        put(UnityBlocks.tenmeikiri, Faction.end);
        put(UnityPlanets.electrode, Faction.imber);
        put(UnityPlanets.inert, Faction.imber);
        put(UnityPlanets.megalith, Faction.monolith);
        put(UnitySectorPresets.accretion, Faction.monolith);
        put(UnitySectorPresets.salvagedLab, Faction.monolith);
        put(UnityUnitTypes.terra, Faction.scar);
        put(UnityUnitTypes.hovos, Faction.scar);
        put(UnityUnitTypes.ryzer, Faction.scar);
        put(UnityUnitTypes.zena, Faction.scar);
        put(UnityUnitTypes.sundown, Faction.scar);
        put(UnityUnitTypes.rex, Faction.scar);
        put(UnityUnitTypes.excelsus, Faction.scar);
        put(UnityUnitTypes.whirlwind, Faction.scar);
        put(UnityUnitTypes.jetstream, Faction.scar);
        put(UnityUnitTypes.vortex, Faction.scar);
        put(UnityUnitTypes.arcnelidia, Faction.imber);
        put(UnityUnitTypes.rayTest, Faction.imber);
        put(UnityUnitTypes.testLink, Faction.imber);
        put(UnityUnitTypes.test, Faction.imber);
        put(UnityUnitTypes.exowalker, Faction.plague);
        put(UnityUnitTypes.toxoswarmer, Faction.plague);
        put(UnityUnitTypes.toxobyte, Faction.plague);
        put(UnityUnitTypes.catenapede, Faction.plague);
        put(UnityUnitTypes.buffer, Faction.koruh);
        put(UnityUnitTypes.omega, Faction.koruh);
        put(UnityUnitTypes.cache, Faction.koruh);
        put(UnityUnitTypes.dijkstra, Faction.koruh);
        put(UnityUnitTypes.phantasm, Faction.koruh);
        put(UnityUnitTypes.kami, Faction.koruh);
        put(UnityUnitTypes.deviation, Faction.advance);
        put(UnityUnitTypes.anomaly, Faction.advance);
        put(UnityUnitTypes.enigma, Faction.end);
        put(UnityUnitTypes.voidVessel, Faction.end);
        put(UnityUnitTypes.chronos, Faction.end);
        put(UnityUnitTypes.opticaecus, Faction.end);
        put(UnityUnitTypes.devourer, Faction.end);
        put(UnityUnitTypes.oppression, Faction.end);
        put(UnityUnitTypes.apocalypse, Faction.end);
        put(UnityUnitTypes.ravager, Faction.end);
        put(UnityUnitTypes.desolation, Faction.end);
        put(UnityUnitTypes.thalassophobia, Faction.end);
        put(UnityUnitTypes.charShadowcape, Faction.monolith);
    }
}
