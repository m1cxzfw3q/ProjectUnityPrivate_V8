package unity.content;

import arc.func.Cons;
import arc.func.Func;
import mindustry.content.Blocks;
import mindustry.content.Items;
import mindustry.ctype.UnlockableContent;
import mindustry.gen.LogicIO;
import mindustry.logic.LAssembler;
import mindustry.type.ItemStack;
import mindustry.type.UnitType;
import mindustry.world.Block;
import mindustry.world.blocks.units.Reconstructor;
import mindustry.world.blocks.units.UnitFactory;
import unity.content.units.MonolithUnitTypes;
import unity.logic.ExpSensorStatement;

public class Overwriter {
    public static <T extends UnlockableContent> void overwrite(UnlockableContent target, Cons<T> setter) {
        setter.get((T) target);
    }

    public static void load() {
        overwrite(Blocks.basalt, (Block t) -> {
            t.itemDrop = UnityItems.stone;
            t.playerUnmineable = true;
        });
        overwrite(Blocks.craters, (Block t) -> {
            t.itemDrop = UnityItems.stone;
            t.playerUnmineable = true;
        });
        overwrite(Blocks.dacite, (Block t) -> {
            t.itemDrop = UnityItems.stone;
            t.playerUnmineable = true;
        });
        overwrite(Blocks.stone, (Block t) -> {
            t.itemDrop = UnityItems.stone;
            t.playerUnmineable = true;
        });
        overwrite(Blocks.airFactory, (UnitFactory f) -> f.plans.add(new UnitFactory.UnitPlan(
                UnityUnitTypes.caelifera,
                1500.0F,
                ItemStack.with(Items.silicon, 15, Items.titanium, 25)
        )));
        overwrite(Blocks.navalFactory, (UnitFactory f) -> f.plans.add(new UnitFactory.UnitPlan(
                UnityUnitTypes.amphibiNaval,
                1500.0F,
                ItemStack.with(Items.silicon, 15, Items.titanium, 25)
        )));
        overwrite(Blocks.additiveReconstructor, (Reconstructor r) -> r.upgrades.add(
                new UnitType[]{UnityUnitTypes.caelifera, UnityUnitTypes.schistocerca},
                new UnitType[]{UnityUnitTypes.amphibiNaval, UnityUnitTypes.craberNaval},
                new UnitType[]{MonolithUnitTypes.stele, MonolithUnitTypes.pedestal}
        ));
        overwrite(Blocks.multiplicativeReconstructor, (Reconstructor r) -> r.upgrades.add(
                new UnitType[]{UnityUnitTypes.schistocerca, UnityUnitTypes.anthophila},
                new UnitType[]{MonolithUnitTypes.pedestal, MonolithUnitTypes.pilaster}
        ));
        overwrite(Blocks.exponentialReconstructor, (Reconstructor r) -> r.upgrades.add(
                new UnitType[]{UnityUnitTypes.anthophila, UnityUnitTypes.vespula},
                new UnitType[]{MonolithUnitTypes.pilaster, MonolithUnitTypes.pylon}
        ));
        overwrite(Blocks.tetrativeReconstructor, (Reconstructor r) -> r.upgrades.add(
                new UnitType[]{UnityUnitTypes.vespula, UnityUnitTypes.lepidoptera},
                new UnitType[]{MonolithUnitTypes.pylon, MonolithUnitTypes.monument}
        ));
        LAssembler.customParsers.put("expsensor", (args) -> new ExpSensorStatement());
        LogicIO.allStatements.add(ExpSensorStatement::new);
    }
}
