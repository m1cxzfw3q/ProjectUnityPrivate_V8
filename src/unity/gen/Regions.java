package unity.gen;

import arc.Core;
import arc.graphics.g2d.TextureRegion;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class Regions {
    public static TextureRegion lightForgeTop1Region;
    public static TextureRegion lightForgeTop2Region;
    public static TextureRegion lightForgeTop3Region;
    public static TextureRegion lightForgeTop4Region;
    public static TextureRegion btLaserTurretTopRegion;
    public static TextureRegion debrisExtractorHeat1Region;
    public static TextureRegion debrisExtractorHeat2Region;
    public static TextureRegion endForgeLightsRegion;
    public static TextureRegion endForgeTopRegion;
    public static TextureRegion endForgeTopSmallRegion;
    public static TextureRegion terminalCrucibleLightsRegion;
    public static TextureRegion terminalCrucibleTopRegion;
    public static TextureRegion tenmeikiriBaseRegion;
    @Regions.Outline(
            color = "464649",
            radius = 4
    )
    public static TextureRegion tenmeikiriBaseOutlineRegion;
    public static TextureRegion supernovaHeadRegion;
    @Regions.Outline(
            color = "464649",
            radius = 4
    )
    public static TextureRegion supernovaHeadOutlineRegion;
    public static TextureRegion supernovaCoreRegion;
    @Regions.Outline(
            color = "464649",
            radius = 4
    )
    public static TextureRegion supernovaCoreOutlineRegion;
    public static TextureRegion supernovaWingLeftRegion;
    @Regions.Outline(
            color = "464649",
            radius = 4
    )
    public static TextureRegion supernovaWingLeftOutlineRegion;
    public static TextureRegion supernovaWingRightRegion;
    @Regions.Outline(
            color = "464649",
            radius = 4
    )
    public static TextureRegion supernovaWingRightOutlineRegion;
    public static TextureRegion supernovaWingLeftBottomRegion;
    @Regions.Outline(
            color = "464649",
            radius = 4
    )
    public static TextureRegion supernovaWingLeftBottomOutlineRegion;
    public static TextureRegion supernovaWingRightBottomRegion;
    @Regions.Outline(
            color = "464649",
            radius = 4
    )
    public static TextureRegion supernovaWingRightBottomOutlineRegion;
    public static TextureRegion supernovaBottomRegion;
    @Regions.Outline(
            color = "464649",
            radius = 4
    )
    public static TextureRegion supernovaBottomOutlineRegion;

    public static void load() {
        lightForgeTop1Region = Core.atlas.find("unity-light-forge-top1");
        lightForgeTop2Region = Core.atlas.find("unity-light-forge-top2");
        lightForgeTop3Region = Core.atlas.find("unity-light-forge-top3");
        lightForgeTop4Region = Core.atlas.find("unity-light-forge-top4");
        btLaserTurretTopRegion = Core.atlas.find("unity-bt-laser-turret-top");
        debrisExtractorHeat1Region = Core.atlas.find("unity-debris-extractor-heat1");
        debrisExtractorHeat2Region = Core.atlas.find("unity-debris-extractor-heat2");
        endForgeLightsRegion = Core.atlas.find("unity-end-forge-lights");
        endForgeTopRegion = Core.atlas.find("unity-end-forge-top");
        endForgeTopSmallRegion = Core.atlas.find("unity-end-forge-top-small");
        terminalCrucibleLightsRegion = Core.atlas.find("unity-terminal-crucible-lights");
        terminalCrucibleTopRegion = Core.atlas.find("unity-terminal-crucible-top");
        tenmeikiriBaseRegion = Core.atlas.find("unity-tenmeikiri-base");
        tenmeikiriBaseOutlineRegion = Core.atlas.find("unity-tenmeikiri-base-outline");
        supernovaHeadRegion = Core.atlas.find("unity-supernova-head");
        supernovaHeadOutlineRegion = Core.atlas.find("unity-supernova-head-outline");
        supernovaCoreRegion = Core.atlas.find("unity-supernova-core");
        supernovaCoreOutlineRegion = Core.atlas.find("unity-supernova-core-outline");
        supernovaWingLeftRegion = Core.atlas.find("unity-supernova-wing-left");
        supernovaWingLeftOutlineRegion = Core.atlas.find("unity-supernova-wing-left-outline");
        supernovaWingRightRegion = Core.atlas.find("unity-supernova-wing-right");
        supernovaWingRightOutlineRegion = Core.atlas.find("unity-supernova-wing-right-outline");
        supernovaWingLeftBottomRegion = Core.atlas.find("unity-supernova-wing-left-bottom");
        supernovaWingLeftBottomOutlineRegion = Core.atlas.find("unity-supernova-wing-left-bottom-outline");
        supernovaWingRightBottomRegion = Core.atlas.find("unity-supernova-wing-right-bottom");
        supernovaWingRightBottomOutlineRegion = Core.atlas.find("unity-supernova-wing-right-bottom-outline");
        supernovaBottomRegion = Core.atlas.find("unity-supernova-bottom");
        supernovaBottomOutlineRegion = Core.atlas.find("unity-supernova-bottom-outline");
    }

    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Outline {
        String color();

        int radius();
    }
}
