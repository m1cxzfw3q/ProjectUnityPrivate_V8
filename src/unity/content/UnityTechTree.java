package unity.content;

import arc.struct.Seq;
import mindustry.content.Blocks;
import mindustry.content.Items;
import mindustry.content.TechTree;
import mindustry.content.UnitTypes;
import mindustry.ctype.UnlockableContent;
import mindustry.game.Objectives;
import mindustry.type.ItemStack;
import unity.content.units.MonolithUnitTypes;

public class UnityTechTree {
    private static TechTree.TechNode context = null;

    public static void load() {
        attach(Blocks.surgeSmelter, () -> {
            node(UnityBlocks.darkAlloyForge);
            node(UnityBlocks.monolithAlloyForge);
            node(UnityBlocks.sparkAlloyForge, (Seq)Seq.with(new Objectives.Objective[]{new Objectives.Research(UnityItems.sparkAlloy)}), (Runnable)(() -> node(UnityBlocks.orb, (Runnable)(() -> {
                node(UnityBlocks.shielder);
                node(UnityBlocks.shockwire, (Runnable)(() -> node(UnityBlocks.current, (Runnable)(() -> node(UnityBlocks.plasma, (Runnable)(() -> node(UnityBlocks.electrobomb)))))));
            }))));
        });
        attach(Blocks.powerNode, () -> node(UnityBlocks.lightLamp, (Runnable)(() -> {
            node(UnityBlocks.lightReflector, (Runnable)(() -> node(UnityBlocks.lightDivisor)));
            node(UnityBlocks.oilLamp);
        })));
        attach(Blocks.arc, () -> {
            node(UnityBlocks.diviner, (Seq)Seq.with(new Objectives.Objective[]{new Objectives.Research(UnityItems.monolite)}), (Runnable)(() -> {
                node(UnityBlocks.mage, (Runnable)(() -> {
                    node(UnityBlocks.heatRay, (Runnable)(() -> node(UnityBlocks.incandescence)));
                    node(UnityBlocks.oracle, (Seq)Seq.with(new Objectives.Objective[]{new Objectives.Research(UnityItems.monolithAlloy)}));
                }));
                node(UnityBlocks.recluse, (Runnable)(() -> node(UnityBlocks.blackout)));
            }));
            node(UnityBlocks.ricochet, (Seq)Seq.with(new Objectives.Objective[]{new Objectives.Research(UnityItems.monolite)}), (Runnable)(() -> {
                node(UnityBlocks.shellshock, (Seq)Seq.with(new Objectives.Objective[]{new Objectives.Research(UnityItems.monolithAlloy)}), (Runnable)(() -> node(UnityBlocks.purge)));
                node(UnityBlocks.lifeStealer, (Runnable)(() -> node(UnityBlocks.absorberAura)));
            }));
        });
        attach(Blocks.titaniumWall, () -> {
            node(UnityBlocks.metaglassWall, (Runnable)(() -> node(UnityBlocks.metaglassWallLarge)));
            node(UnityBlocks.electrophobicWall, (Seq)Seq.with(new Objectives.Objective[]{new Objectives.Research(UnityItems.monolite)}), (Runnable)(() -> node(UnityBlocks.electrophobicWallLarge)));
        });
        attach(Blocks.siliconCrucible, () -> node(UnityBlocks.irradiator, (Seq)Seq.with(new Objectives.Objective[]{new Objectives.Research(Items.thorium), new Objectives.Research(Items.titanium), new Objectives.Research(Items.surgeAlloy)})));
        attach(Blocks.overdriveProjector, () -> node(UnityBlocks.superCharger, (Seq)Seq.with(new Objectives.Objective[]{new Objectives.Research(UnityBlocks.irradiator)})));
        attach(Blocks.surgeTower, () -> node(UnityBlocks.absorber, (Seq)Seq.with(new Objectives.Objective[]{new Objectives.Research(UnityBlocks.sparkAlloyForge)})));
        attach(UnitTypes.fortress, () -> node(MonolithUnitTypes.stele, (Runnable)(() -> {
            node(MonolithUnitTypes.pedestal, (Runnable)(() -> node(MonolithUnitTypes.pilaster, (Runnable)(() -> node(MonolithUnitTypes.pylon, (Runnable)(() -> node(MonolithUnitTypes.monument, (Runnable)(() -> node(MonolithUnitTypes.colossus, (Runnable)(() -> node(MonolithUnitTypes.bastion)))))))))));
            node(MonolithUnitTypes.adsect, (Runnable)(() -> node(MonolithUnitTypes.comitate)));
        })));
        attach(Items.lead, () -> nodeProduce(UnityItems.nickel));
        attach(Items.graphite, () -> {
            nodeProduce(UnityItems.monolite);
            nodeProduce(UnityItems.stone, () -> nodeProduce(UnityItems.denseAlloy, () -> nodeProduce(UnityItems.steel, () -> nodeProduce(UnityLiquids.lava, () -> nodeProduce(UnityItems.dirium)))));
        });
        attach(Items.thorium, () -> nodeProduce(UnityItems.archDebris, Seq.with(new Objectives.Objective[]{new Objectives.Research(UnityItems.monolite)}), () -> nodeProduce(UnityItems.monolithAlloy)));
        attach(Items.surgeAlloy, () -> nodeProduce(UnityItems.imberium, () -> {
            nodeProduce(UnityItems.sparkAlloy);
            nodeProduce(UnityItems.irradiantSurge);
        }));
    }

    private static void attach(UnlockableContent parent, Runnable children) {
        context = TechTree.get(parent);
        children.run();
    }

    private static void node(UnlockableContent content, ItemStack[] requirements, Seq<Objectives.Objective> objectives, Runnable children) {
        TechTree.TechNode node = new TechTree.TechNode(context, content, requirements);
        if (objectives != null) {
            node.objectives = objectives;
        }

        TechTree.TechNode prev = context;
        context = node;
        children.run();
        context = prev;
    }

    private static void node(UnlockableContent content, ItemStack[] requirements, Runnable children) {
        node(content, requirements, (Seq)null, children);
    }

    private static void node(UnlockableContent content, Seq<Objectives.Objective> objectives, Runnable children) {
        node(content, content.researchRequirements(), objectives, children);
    }

    private static void node(UnlockableContent content, Runnable children) {
        node(content, content.researchRequirements(), children);
    }

    private static void node(UnlockableContent content, Seq<Objectives.Objective> objectives) {
        node(content, content.researchRequirements(), objectives, () -> {
        });
    }

    private static void node(UnlockableContent content, ItemStack[] requirements) {
        node(content, requirements, (Seq)null, () -> {
        });
    }

    private static void node(UnlockableContent content, ItemStack[] requirements, Seq<Objectives.Objective> objectives) {
        node(content, requirements, objectives, () -> {
        });
    }

    private static void node(UnlockableContent content) {
        node(content, (Runnable)(() -> {
        }));
    }

    private static void nodeProduce(UnlockableContent content, Seq<Objectives.Objective> objectives, Runnable children) {
        node(content, content.researchRequirements(), objectives.and(new Objectives.Produce(content)), children);
    }

    private static void nodeProduce(UnlockableContent content, Runnable children) {
        nodeProduce(content, Seq.with(new Objectives.Objective[0]), children);
    }

    private static void nodeProduce(UnlockableContent content) {
        nodeProduce(content, Seq.with(new Objectives.Objective[0]), () -> {
        });
    }
}
