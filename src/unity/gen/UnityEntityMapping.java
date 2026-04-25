package unity.gen;

import arc.func.Prov;
import arc.struct.ObjectIntMap;
import arc.util.Strings;
import mindustry.gen.EntityMapping;
import mindustry.gen.Entityc;
import mindustry.gen.LegsUnit;
import mindustry.gen.MechUnit;
import mindustry.gen.PayloadUnit;
import mindustry.gen.Unit;
import mindustry.gen.UnitEntity;
import mindustry.gen.UnitWaterMove;
import mindustry.type.UnitType;
import unity.content.UnityUnitTypes;
import unity.content.units.MonolithUnitTypes;
import unity.entities.units.ApocalypseUnit;
import unity.entities.units.EndInvisibleUnit;
import unity.entities.units.EndLegsUnit;
import unity.entities.units.EndWormUnit;
import unity.entities.units.WormDefaultUnit;
import unity.entities.units.WormSegmentUnit;

public class UnityEntityMapping {
    private static final ObjectIntMap<Class<? extends Entityc>> ids = new ObjectIntMap();
    private static volatile int last = 0;

    public static <T extends Entityc> void register(Class<T> type, Prov<T> prov) {
        synchronized(UnityEntityMapping.class) {
            if (!ids.containsKey(type) && !EntityMapping.nameMap.containsKey(type.getSimpleName())) {
                while(last < EntityMapping.idMap.length) {
                    if (EntityMapping.idMap[last] == null) {
                        EntityMapping.idMap[last] = prov;
                        ids.put(type, last);
                        EntityMapping.nameMap.put(type.getSimpleName(), prov);
                        EntityMapping.nameMap.put(Strings.camelToKebab(type.getSimpleName()), prov);
                        break;
                    }

                    ++last;
                }

            }
        }
    }

    public static <T extends Entityc> void register(String name, Class<T> type, Prov<T> prov) {
        register(type, prov);
        EntityMapping.nameMap.put(name, prov);
        int id = classId(type);
        if (id != -1) {
            EntityMapping.customIdMap.put(classId(type), name);
        }

    }

    public static <T extends Unit> void register(UnitType unit, Class<T> type, Prov<T> prov) {
        register(unit.name, type, prov);
        unit.constructor = prov;
    }

    public static <T extends Entityc> int classId(Class<T> type) {
        return ids.get(type, -1);
    }

    public static void init() {
        register(MonolithUnitTypes.stele, MonolithMechUnit.class, MonolithMechUnit::create);
        register(MonolithUnitTypes.pedestal, MonolithMechUnit.class, MonolithMechUnit::create);
        register(MonolithUnitTypes.pilaster, MonolithMechUnit.class, MonolithMechUnit::create);
        register(MonolithUnitTypes.pylon, MonolithLegsUnit.class, MonolithLegsUnit::create);
        register(MonolithUnitTypes.monument, MonolithLegsUnit.class, MonolithLegsUnit::create);
        register(MonolithUnitTypes.colossus, MonolithLegsUnit.class, MonolithLegsUnit::create);
        register(MonolithUnitTypes.bastion, MonolithLegsUnit.class, MonolithLegsUnit::create);
        register(MonolithUnitTypes.adsect, MonolithAssistantUnit.class, MonolithAssistantUnit::create);
        register(MonolithUnitTypes.comitate, MonolithAssistantUnit.class, MonolithAssistantUnit::create);
        register(MonolithUnitTypes.stray, MonolithCTrailUnit.class, MonolithCTrailUnit::create);
        register(MonolithUnitTypes.tendence, MonolithCTrailUnit.class, MonolithCTrailUnit::create);
        register(MonolithUnitTypes.liminality, MonolithCTrailUnit.class, MonolithCTrailUnit::create);
        register(MonolithUnitTypes.calenture, MonolithCTrailUnit.class, MonolithCTrailUnit::create);
        register(MonolithUnitTypes.hallucination, MonolithCTrailUnit.class, MonolithCTrailUnit::create);
        register(MonolithUnitTypes.escapism, MonolithCTrailUnit.class, MonolithCTrailUnit::create);
        register(MonolithUnitTypes.fantasy, MonolithCTrailUnit.class, MonolithCTrailUnit::create);
        register(UnityUnitTypes.caelifera, CopterUnit.class, CopterUnit::create);
        register(UnityUnitTypes.schistocerca, CopterUnit.class, CopterUnit::create);
        register(UnityUnitTypes.anthophila, CopterUnit.class, CopterUnit::create);
        register(UnityUnitTypes.vespula, CopterUnit.class, CopterUnit::create);
        register(UnityUnitTypes.lepidoptera, CopterUnit.class, CopterUnit::create);
        register(UnityUnitTypes.mantodea, CopterUnit.class, CopterUnit::create);
        register(UnityUnitTypes.amphibiNaval, TransWaterMoveUnit.class, TransWaterMoveUnit::create);
        register(UnityUnitTypes.amphibi, TransWaterMoveUnit.class, TransWaterMoveUnit::create);
        register(UnityUnitTypes.craberNaval, TransLegsUnit.class, TransLegsUnit::create);
        register(UnityUnitTypes.craber, TransLegsUnit.class, TransLegsUnit::create);
        register(UnityUnitTypes.terra, WorldUnit.class, WorldUnit::create);
        register((UnitType)UnityUnitTypes.rayTest, ImberUnit.class, ImberUnit::create);
        register(UnityUnitTypes.exowalker, TriJointLegsUnit.class, TriJointLegsUnit::create);
        register(UnityUnitTypes.toxoswarmer, CLegUnit.class, CLegUnit::create);
        register(UnityUnitTypes.toxobyte, WormUnit.class, WormUnit::create);
        register(UnityUnitTypes.catenapede, WormUnit.class, WormUnit::create);
        register(UnityUnitTypes.deviation, DecorationUnit.class, DecorationUnit::create);
        register(UnityUnitTypes.anomaly, DecorationUnit.class, DecorationUnit::create);
        register(UnityUnitTypes.enigma, BoostEndUnit.class, BoostEndUnit::create);
        register(UnityUnitTypes.voidVessel, EndUnit.class, EndUnit::create);
        register(UnityUnitTypes.chronos, TimeStopVelEndUnit.class, TimeStopVelEndUnit::create);
        register(UnityUnitTypes.opticaecus, InvisibleEndUnit.class, InvisibleEndUnit::create);
        register(UnityUnitTypes.devourer, WormEndUnit.class, WormEndUnit::create);
        register(UnityUnitTypes.apocalypse, TentacleInvisibleEndUnit.class, TentacleInvisibleEndUnit::create);
        register(UnityUnitTypes.ravager, LegsEndUnit.class, LegsEndUnit::create);
        register(UnityUnitTypes.desolation, TallTentacleLegsEndUnit.class, TallTentacleLegsEndUnit::create);
        register(UnityUnitTypes.thalassophobia, DecorationWaterMoveEndUnit.class, DecorationWaterMoveEndUnit::create);
        register(UnityUnitTypes.charShadowcape, MonolithDecorationMechUnit.class, MonolithDecorationMechUnit::create);
        register(CutEffect.class, CutEffect::create);
        register(EndBullet.class, EndBullet::create);
        register(KamiBullet.class, KamiBullet::create);
        register(Kami.class, Kami::create);
        register(KamiLaser.class, KamiLaser::create);
        register(Light.class, Light::create);
        register(MonolithSoul.class, MonolithSoul::create);
        register(Oppression.class, Oppression::create);
        register(SlowLightning.class, SlowLightning::create);
        register(Test4.class, Test4::create);
        register(Test.class, Test::create);
        register(TimeStopBullet.class, TimeStopBullet::create);
        register(Trns.class, Trns::create);
        register(MonolithUnitTypes.monolithSoul, MonolithSoul.class, MonolithSoul::new);
        register(UnityUnitTypes.cherub, UnitEntity.class, UnitEntity::create);
        register(UnityUnitTypes.malakhim, UnitEntity.class, UnitEntity::create);
        register(UnityUnitTypes.seraphim, UnitEntity.class, UnitEntity::create);
        register(UnityUnitTypes.discharge, UnitEntity.class, UnitEntity::create);
        register(UnityUnitTypes.pulse, UnitEntity.class, UnitEntity::create);
        register(UnityUnitTypes.emission, UnitEntity.class, UnitEntity::create);
        register(UnityUnitTypes.waveform, UnitEntity.class, UnitEntity::create);
        register(UnityUnitTypes.ultraviolet, UnitEntity.class, UnitEntity::create);
        register(UnityUnitTypes.citadel, MechUnit.class, MechUnit::create);
        register(UnityUnitTypes.empire, MechUnit.class, MechUnit::create);
        register(UnityUnitTypes.cygnus, LegsUnit.class, LegsUnit::create);
        register(UnityUnitTypes.sagittarius, LegsUnit.class, LegsUnit::create);
        register(UnityUnitTypes.araneidae, LegsUnit.class, LegsUnit::create);
        register(UnityUnitTypes.theraphosidae, LegsUnit.class, LegsUnit::create);
        register(UnityUnitTypes.mantle, UnitEntity.class, UnitEntity::create);
        register(UnityUnitTypes.aphelion, UnitEntity.class, UnitEntity::create);
        register(UnityUnitTypes.sedec, PayloadUnit.class, PayloadUnit::create);
        register(UnityUnitTypes.trigintaduo, PayloadUnit.class, PayloadUnit::create);
        register(UnityUnitTypes.fin, UnitWaterMove.class, UnitWaterMove::create);
        register(UnityUnitTypes.blue, UnitWaterMove.class, UnitWaterMove::create);
        register(UnityUnitTypes.philinopsis, UnitWaterMove.class, UnitWaterMove::create);
        register(UnityUnitTypes.chelidonura, UnitWaterMove.class, UnitWaterMove::create);
        register(UnityUnitTypes.hovos, LegsUnit.class, LegsUnit::create);
        register(UnityUnitTypes.ryzer, LegsUnit.class, LegsUnit::create);
        register(UnityUnitTypes.zena, LegsUnit.class, LegsUnit::create);
        register(UnityUnitTypes.sundown, LegsUnit.class, LegsUnit::create);
        register(UnityUnitTypes.rex, LegsUnit.class, LegsUnit::create);
        register(UnityUnitTypes.excelsus, LegsUnit.class, LegsUnit::create);
        register(UnityUnitTypes.whirlwind, UnitEntity.class, UnitEntity::create);
        register(UnityUnitTypes.jetstream, UnitEntity.class, UnitEntity::create);
        register(UnityUnitTypes.vortex, UnitEntity.class, UnitEntity::create);
        register(UnityUnitTypes.arcnelidia, WormDefaultUnit.class, WormDefaultUnit::new);
        register((UnitType)UnityUnitTypes.testLink, UnitEntity.class, UnitEntity::create);
        register((UnitType)UnityUnitTypes.test, UnitEntity.class, UnitEntity::create);
        register(UnityUnitTypes.buffer, MechUnit.class, MechUnit::create);
        register(UnityUnitTypes.omega, MechUnit.class, MechUnit::create);
        register(UnityUnitTypes.cache, UnitEntity.class, UnitEntity::create);
        register(UnityUnitTypes.dijkstra, UnitEntity.class, UnitEntity::create);
        register(UnityUnitTypes.phantasm, UnitEntity.class, UnitEntity::create);
        register(UnityUnitTypes.kami, Kami.class, Kami::new);
        register(UnityUnitTypes.oppression, Oppression.class, Oppression::new);
        register(ApocalypseUnit.class, ApocalypseUnit::new);
        register(EndInvisibleUnit.class, EndInvisibleUnit::new);
        register(EndLegsUnit.class, EndLegsUnit::new);
        register(EndWormUnit.class, EndWormUnit::new);
        register(EndWormUnit.EndWormSegmentUnit.class, EndWormUnit.EndWormSegmentUnit::new);
        register(WormSegmentUnit.class, WormSegmentUnit::new);
    }
}
