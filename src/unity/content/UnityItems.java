package unity.content;

import arc.graphics.Color;
import mindustry.content.Items;
import mindustry.type.Item;
import unity.graphics.UnityPal;
import unity.type.AnimatedItem;
import unity.world.meta.CrucibleRecipe;
import unity.world.meta.MeltInfo;

public class UnityItems {
    public static Item advanceAlloy;
    public static Item cupronickel;
    public static Item darkAlloy;
    public static Item dirium;
    public static Item lightAlloy;
    public static Item monolithAlloy;
    public static Item archDebris;
    public static Item plagueAlloy;
    public static Item sparkAlloy;
    public static Item superAlloy;
    public static Item terminaAlloy;
    public static Item terminationFragment;
    public static Item terminum;
    public static Item contagium;
    public static Item denseAlloy;
    public static Item imberium;
    public static Item irradiantSurge;
    public static Item luminum;
    public static Item monolite;
    public static Item nickel;
    public static Item steel;
    public static Item stone;
    public static Item umbrium;
    public static Item xenium;
    public static Item uranium;

    public static void load() {
        advanceAlloy = new Item("advance-alloy", Color.valueOf("748096")) {
            {
                this.cost = 1.4F;
                this.radioactivity = 0.1F;
            }
        };
        cupronickel = new Item("cupronickel", Color.valueOf("a19975")) {
            {
                this.cost = 2.0F;
            }
        };
        darkAlloy = new Item("dark-alloy", Color.valueOf("716264")) {
            {
                this.cost = 1.4F;
                this.radioactivity = 0.11F;
            }
        };
        dirium = new Item("dirium", Color.valueOf("96f7c3")) {
            {
                this.cost = 0.3F;
                this.hardness = 9;
            }
        };
        lightAlloy = new Item("light-alloy", Color.valueOf("e0ecee")) {
            {
                this.cost = 1.4F;
                this.radioactivity = 0.08F;
            }
        };
        monolithAlloy = new AnimatedItem("monolith-alloy", UnityPal.monolithLight) {
            {
                this.cost = 1.4F;
                this.flammability = 0.1F;
                this.radioactivity = 0.12F;
                this.frames = 14;
                this.frameTime = 1.0F;
                this.transitionFrames = 3;
            }
        };
        archDebris = new AnimatedItem("archaic-debris", UnityPal.monolith) {
            {
                this.cost = 1.3F;
                this.radioactivity = 0.1F;
                this.frames = 7;
                this.frameTime = 3.0F;
                this.transitionFrames = 1;
            }
        };
        plagueAlloy = new Item("plague-alloy", Color.valueOf("6a766a")) {
            {
                this.cost = 1.4F;
                this.radioactivity = 0.16F;
            }
        };
        sparkAlloy = new Item("spark-alloy", Color.valueOf("f4ff61")) {
            {
                this.cost = 1.3F;
                this.radioactivity = 0.01F;
                this.explosiveness = 0.1F;
            }
        };
        superAlloy = new Item("super-alloy", Color.valueOf("67a8a0")) {
            {
                this.cost = 2.5F;
            }
        };
        terminaAlloy = new Item("termina-alloy", Color.valueOf("9e6d74")) {
            {
                this.cost = 4.2F;
                this.radioactivity = 1.74F;
            }
        };
        terminationFragment = new Item("termination-fragment", Color.valueOf("f9504f")) {
            {
                this.cost = 1.2F;
                this.radioactivity = 3.64F;
            }
        };
        terminum = new Item("terminum", Color.valueOf("f53036")) {
            {
                this.cost = 3.2F;
                this.radioactivity = 1.32F;
            }
        };
        contagium = new Item("contagium", Color.valueOf("68985e")) {
            {
                this.cost = 1.5F;
                this.hardness = 3;
                this.radioactivity = 0.7F;
            }
        };
        denseAlloy = new Item("dense-alloy", Color.valueOf("a68a84")) {
            {
                this.hardness = 2;
                this.cost = 2.0F;
            }
        };
        imberium = new Item("imberium", Color.valueOf("f6ff7d")) {
            {
                this.cost = 1.4F;
                this.hardness = 3;
                this.radioactivity = 0.6F;
            }
        };
        irradiantSurge = new AnimatedItem("irradiant-surge", Color.valueOf("3d423e")) {
            {
                this.cost = 2.0F;
                this.frames = 2;
                this.frameTime = 3.0F;
                this.transitionFrames = 30;
            }
        };
        luminum = new Item("luminum", Color.valueOf("e9eaf1")) {
            {
                this.cost = 1.2F;
                this.hardness = 3;
                this.radioactivity = 0.1F;
            }
        };
        monolite = new Item("monolite", UnityPal.monolithDark) {
            {
                this.cost = 1.5F;
                this.hardness = 3;
                this.radioactivity = 0.2F;
                this.flammability = 0.2F;
            }
        };
        nickel = new Item("nickel", Color.valueOf("6e9675")) {
            {
                this.hardness = 3;
                this.cost = 2.5F;
            }
        };
        steel = new Item("steel", Color.valueOf("e1e3ed")) {
            {
                this.hardness = 4;
                this.cost = 2.5F;
            }
        };
        stone = new Item("stone", Color.valueOf("8a8a8a")) {
            {
                this.hardness = 1;
                this.cost = 0.4F;
                this.lowPriority = true;
            }
        };
        umbrium = new Item("umbrium", Color.valueOf("8c3d3b")) {
            {
                this.cost = 1.2F;
                this.hardness = 3;
                this.radioactivity = 0.2F;
            }
        };
        xenium = new Item("xenium", Color.valueOf("9dddff")) {
            {
                this.cost = 1.2F;
                this.hardness = 3;
                this.radioactivity = 0.6F;
            }
        };
        uranium = new Item("uranium", Color.valueOf("ace284")) {
            {
                this.cost = 2.0F;
                this.hardness = 3;
                this.radioactivity = 1.0F;
            }
        };
        MeltInfo meltCopper = new MeltInfo(Items.copper, 750.0F, 0.1F, 0.02F, 2100.0F, 1);
        MeltInfo meltLead = new MeltInfo(Items.lead, 570.0F, 0.2F, 0.02F, 1900.0F, 1);
        MeltInfo meltTitanium = new MeltInfo(Items.titanium, 1600.0F, 0.07F, 1);
        MeltInfo meltSand = new MeltInfo(Items.sand, 1000.0F, 0.25F, 1);
        MeltInfo carbon = new MeltInfo("carbon", 4000.0F, 0.01F, 0.01F, 600.0F, 0);
        new MeltInfo(Items.coal, carbon, 0.5F, 0, true);
        new MeltInfo(Items.graphite, carbon, 1.0F, 0, true);
        MeltInfo meltNickel = new MeltInfo(nickel, 1100.0F, 0.15F, 1);
        MeltInfo meltCuproNickel = new MeltInfo(cupronickel, 850.0F, 0.05F, 2);
        MeltInfo meltMetaglass = new MeltInfo(Items.metaglass, 950.0F, 0.05F, 2);
        MeltInfo meltSilicon = new MeltInfo(Items.silicon, 900.0F, 0.2F, 2);
        MeltInfo meltSurgeAlloy = new MeltInfo(Items.surgeAlloy, 1500.0F, 0.05F, 3);
        MeltInfo meltThorium = new MeltInfo(Items.thorium, 1650.0F, 0.03F, 1);
        MeltInfo meltSuperAlloy = new MeltInfo(superAlloy, 1800.0F, 0.02F, 4);
        new CrucibleRecipe(meltCuproNickel, 0.6F, new CrucibleRecipe.InputRecipe[]{new CrucibleRecipe.InputRecipe(meltNickel, 0.8F, false), new CrucibleRecipe.InputRecipe(meltCopper, 2.0F)});
        new CrucibleRecipe(meltSilicon, 0.25F, new CrucibleRecipe.InputRecipe[]{new CrucibleRecipe.InputRecipe(meltSand, 1.25F), new CrucibleRecipe.InputRecipe(carbon, 0.25F, false)});
        new CrucibleRecipe(meltMetaglass, 0.5F, new CrucibleRecipe.InputRecipe[]{new CrucibleRecipe.InputRecipe(meltSand, 0.33333334F), new CrucibleRecipe.InputRecipe(meltLead, 0.33333334F)});
        new CrucibleRecipe(meltSurgeAlloy, 0.25F, new CrucibleRecipe.InputRecipe[]{new CrucibleRecipe.InputRecipe(meltSilicon, 1.0F), new CrucibleRecipe.InputRecipe(meltLead, 2.0F), new CrucibleRecipe.InputRecipe(meltCopper, 1.0F), new CrucibleRecipe.InputRecipe(meltTitanium, 1.5F)});
        new CrucibleRecipe(meltSuperAlloy, 0.2F, new CrucibleRecipe.InputRecipe[]{new CrucibleRecipe.InputRecipe(meltCuproNickel, 1.0F), new CrucibleRecipe.InputRecipe(meltSilicon, 1.0F), new CrucibleRecipe.InputRecipe(meltThorium, 1.0F), new CrucibleRecipe.InputRecipe(meltTitanium, 1.0F)});
    }
}
