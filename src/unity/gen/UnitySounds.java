package unity.gen;

import arc.Core;
import arc.assets.AssetDescriptor;
import arc.assets.loaders.SoundLoader;
import arc.audio.Sound;
import arc.util.Log;
import mindustry.Vars;

public final class UnitySounds {
    public static Sound cubeBlast = new Sound();
    public static Sound eclipseBeam = new Sound();
    public static Sound xenoBeam = new Sound();
    public static Sound chainyShot = new Sound();
    public static Sound clang = new Sound();
    public static Sound continuousLaserA = new Sound();
    public static Sound continuousLaserB = new Sound();
    public static Sound beamIntenseHighpitchTone = new Sound();
    public static Sound extinctionShoot = new Sound();
    public static Sound continueTime = new Sound();
    public static Sound devourerMainLaser = new Sound();
    public static Sound endBasicLarge = new Sound();
    public static Sound endBasicSmall = new Sound();
    public static Sound endBasic = new Sound();
    public static Sound endMissile = new Sound();
    public static Sound endgameActive = new Sound();
    public static Sound endgameShoot = new Sound();
    public static Sound endgameSmallShoot = new Sound();
    public static Sound fractureShoot = new Sound();
    public static Sound oppressionLightning = new Sound();
    public static Sound ravagerNightmareShoot = new Sound();
    public static Sound spaceFracture = new Sound();
    public static Sound stopTime = new Sound();
    public static Sound tenmeikiriCharge = new Sound();
    public static Sound tenmeikiriShoot = new Sound();
    public static Sound thalassophobiaLaser = new Sound();
    public static Sound energyBlast = new Sound();
    public static Sound energyBolt = new Sound();
    public static Sound energyCharge = new Sound();
    public static Sound heatRay = new Sound();
    public static Sound shieldBreak = new Sound();
    public static Sound shielderShoot = new Sound();
    public static Sound kamiLaser = new Sound();
    public static Sound kamiMasterspark = new Sound();
    public static Sound kamiSansLaser = new Sound();
    public static Sound kamiSegapower = new Sound();
    public static Sound kamiShootChime = new Sound();
    public static Sound kamiShootSimpleB = new Sound();
    public static Sound kamiShootSimple = new Sound();
    public static Sound laserFreeze = new Sound();
    public static Sound ephemeronShoot = new Sound();
    public static Sound gluonShoot = new Sound();
    public static Sound higgsBosonShoot = new Sound();
    public static Sound muonShoot = new Sound();
    public static Sound singularityShoot = new Sound();
    public static Sound wbosonShoot = new Sound();
    public static Sound zbosonShoot = new Sound();
    public static Sound supernovaActive = new Sound();
    public static Sound supernovaCharge = new Sound();
    public static Sound supernovaShoot = new Sound();

    private UnitySounds() {
        throw new AssertionError();
    }

    protected static Sound load(String name) {
        String n = "sounds/" + name;
        String path = Vars.tree.get(n + ".ogg").exists() ? n + ".ogg" : n + ".mp3";
        Sound sound = new Sound();
        AssetDescriptor<Sound> desc = Core.assets.load(path, Sound.class, new SoundLoader.SoundParameter(sound));
        desc.errored = (e) -> Log.err(e);
        return sound;
    }

    public static void load() {
        if (!Vars.headless) {
            cubeBlast = load("advance/cube-blast");
            eclipseBeam = load("advance/eclipse-beam");
            xenoBeam = load("advance/xeno-beam");
            chainyShot = load("chainy-shot");
            clang = load("clang");
            continuousLaserA = load("continuous-laser-a");
            continuousLaserB = load("continuous-laser-b");
            beamIntenseHighpitchTone = load("dark/beam-intense-highpitch-tone");
            extinctionShoot = load("dark/extinction-shoot");
            continueTime = load("end/continue-time");
            devourerMainLaser = load("end/devourer-main-laser");
            endBasicLarge = load("end/end-basic-large");
            endBasicSmall = load("end/end-basic-small");
            endBasic = load("end/end-basic");
            endMissile = load("end/end-missile");
            endgameActive = load("end/endgame-active");
            endgameShoot = load("end/endgame-shoot");
            endgameSmallShoot = load("end/endgame-small-shoot");
            fractureShoot = load("end/fracture-shoot");
            oppressionLightning = load("end/oppression-lightning");
            ravagerNightmareShoot = load("end/ravager-nightmare-shoot");
            spaceFracture = load("end/space-fracture");
            stopTime = load("end/stop-time");
            tenmeikiriCharge = load("end/tenmeikiri-charge");
            tenmeikiriShoot = load("end/tenmeikiri-shoot");
            thalassophobiaLaser = load("end/thalassophobia-laser");
            energyBlast = load("energy-blast");
            energyBolt = load("energy-bolt");
            energyCharge = load("energy-charge");
            heatRay = load("heat-ray");
            shieldBreak = load("imber/shield-break");
            shielderShoot = load("imber/shielder-shoot");
            kamiLaser = load("kami/kami-laser");
            kamiMasterspark = load("kami/kami-masterspark");
            kamiSansLaser = load("kami/kami-sans-laser");
            kamiSegapower = load("kami/kami-segapower");
            kamiShootChime = load("kami/kami-shoot-chime");
            kamiShootSimpleB = load("kami/kami-shoot-simple-b");
            kamiShootSimple = load("kami/kami-shoot-simple");
            laserFreeze = load("koruh/laser-freeze");
            ephemeronShoot = load("light/ephemeron-shoot");
            gluonShoot = load("light/gluon-shoot");
            higgsBosonShoot = load("light/higgs-boson-shoot");
            muonShoot = load("light/muon-shoot");
            singularityShoot = load("light/singularity-shoot");
            wbosonShoot = load("light/wboson-shoot");
            zbosonShoot = load("light/zboson-shoot");
            supernovaActive = load("monolith/supernova-active");
            supernovaCharge = load("monolith/supernova-charge");
            supernovaShoot = load("monolith/supernova-shoot");
        }
    }
}
