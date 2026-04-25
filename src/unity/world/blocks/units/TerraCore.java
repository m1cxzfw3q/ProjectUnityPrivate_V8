package unity.world.blocks.units;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.scene.ui.layout.Table;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import mindustry.gen.Unit;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.ui.Styles;
import mindustry.world.Block;
import unity.content.UnityUnitTypes;
import unity.gen.Worldc;
import unity.type.UnityUnitType;
import unity.v8.UnityStyles;

public class TerraCore extends Block {
    UnityUnitType type;

    public TerraCore(String name) {
        super(name);
        this.type = (UnityUnitType)UnityUnitTypes.terra;
        this.update = true;
        this.configurable = true;
        this.hasItems = true;
        this.itemCapacity = 150;
        this.separateItemCapacity = true;
        this.highUnloadPriority = true;
    }

    public class TerraCoreBuild extends Building {
        Worldc unit;

        public void buildConfiguration(Table table) {
            table.button(Icon.units, UnityStyles.clearTransi, () -> {
                Unit u = TerraCore.this.type.create(this.team);
                if (u instanceof Worldc) {
                    u.x = this.x;
                    u.y = this.y;
                    u.rotation = 90.0F;
                    this.unit = (Worldc)u;
                    u.add();
                    ((Worldc)u).setup();
                }

            }).size(50.0F);
        }

        public void draw() {
            if (this.unit == null) {
                float z = Draw.z();
                Draw.z(20.0F);
                Draw.color(Color.white, 0.2F);
                Draw.rect(TerraCore.this.type.fullIcon, this.x, this.y, 0.0F);
                Draw.z(z);
                Draw.reset();
            }

            super.draw();
        }

        public void updateTile() {
            if (this.unit != null) {
                Item item = this.unit.item();
                if (this.items.get(item) < TerraCore.this.itemCapacity) {
                    int amount = this.acceptStack(this.unit.item(), this.unit.stack().amount, this.unit);
                    if (amount > 0) {
                        this.handleStack(item, amount, this.unit);
                        ItemStack var10000 = this.unit.stack();
                        var10000.amount -= amount;
                    }
                }
            }

        }
    }
}
