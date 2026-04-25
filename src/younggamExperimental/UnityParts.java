package younggamExperimental;

import arc.struct.ObjectMap;
import mindustry.content.Items;
import mindustry.type.ItemStack;
import unity.content.UnityItems;

public class UnityParts {
    public static final ObjectMap<String, PartInfo> parts = new ObjectMap<>();
    public static PartInfo gunBase;
    public static PartInfo rotaryGunBase;
    public static PartInfo gunBreach;
    public static PartInfo grenadeBreach;

    public static void load() {
        gunBase = new PartInfo(
                "Gun base",
                "A basic gun base, very extendable",
                PartType.base,
                0, 2, 1, 1,
                ItemStack.with(UnityItems.nickel, 10, Items.titanium, 5),
                new byte[]{5, 2, 5, 0},
                new byte[]{0, 0, 0, 1},
                new PartStat(PartStatType.hp, 10),
                new PartStat(PartStatType.support, "[accent]1x [white]small turret"),
                new PartStat(PartStatType.reload, 10),
                new PartStat(PartStatType.heatAccumMult, 1.0F)
        );

        rotaryGunBase = new PartInfo(
                "Rotary Gun base",
                "A spinning gun base that can fire shots at intense speed, given enough ammo and torque. Its size makes it difficult to modify.",
                PartType.base,
                1, 2, 3, 1,
                ItemStack.with(UnityItems.nickel, 8, Items.titanium, 10, Items.graphite, 5),
                new byte[]{0, 2, 2, 2, 0, 0, 0, 0},
                new byte[]{0, 0, 0, 0, 0, 0, 1, 0},
                new PartStat(PartStatType.hp, 30),
                new PartStat(PartStatType.support, "[accent]3x [white]small turret"),
                new PartStat(PartStatType.reload, 1.5F),
                new PartStat(PartStatType.mass, 20),
                new PartStat(PartStatType.useTorque, true),
                new PartStat(PartStatType.shaftSpd, 25),
                new PartStat(PartStatType.heatAccumMult, 0.4F)
        );

        gunBreach = new PartInfo(
                "Gun breach",
                "Run of the mill breach, Accepts and fires simple shots",
                PartType.breach,
                0, 1, 1, 1,
                ItemStack.with(UnityItems.nickel, 4),
                new byte[]{0, 3, 0, 0},
                new byte[]{0, 0, 0, 2},
                new PartStat(PartStatType.hp, 10),
                new PartStat(PartStatType.bulletType, "normal"),
                new PartStat(PartStatType.baseDmg, 15),
                new PartStat(PartStatType.baseSpeed, 4),
                new PartStat(PartStatType.ammoType, "normal"),
                new PartStat(PartStatType.payload, 1),
                new PartStat(PartStatType.magazine, 3),
                new PartStat(PartStatType.shots, 1),
                new PartStat(PartStatType.reloadMultiplier, 1),
                new PartStat(PartStatType.spread, 10),
                new PartStat(PartStatType.lifetime, 35),
                new PartStat(PartStatType.heat, 200)
        );

        grenadeBreach = new PartInfo(
                "Grenade breach",
                "Accepts and fires bouncing grenades",
                PartType.breach,
                1, 1, 1, 1,
                ItemStack.with(UnityItems.nickel, 3, Items.graphite, 5),
                new byte[]{0, 3, 0, 0},
                new byte[]{0, 0, 0, 2},
                new PartStat(PartStatType.hp, 10),
                new PartStat(PartStatType.bulletType, "grenade"),
                new PartStat(PartStatType.baseDmg, 5),
                new PartStat(PartStatType.baseSpeed, 3),
                new PartStat(PartStatType.ammoType, "explosive"),
                new PartStat(PartStatType.payload, 1),
                new PartStat(PartStatType.magazine, 2),
                new PartStat(PartStatType.shots, 1),
                new PartStat(PartStatType.reloadMultiplier, 3.5F),
                new PartStat(PartStatType.spread, 20),
                new PartStat(PartStatType.lifetime, 150),
                new PartStat(PartStatType.mod, "Explosive", (config) -> {})
        );
    }

    public static PartInfo[] getPartList() {
        return new PartInfo[0];
    }

    public enum PartSize {
        SMALL,
        MEDIUM,
        LARGE,
        XLARGE,
        EXPERIMENTAL
    }
}
