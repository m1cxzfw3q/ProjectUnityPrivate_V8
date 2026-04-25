package unity.world.blocks.production;

import arc.Core;
import arc.func.Prov;
import java.util.Objects;
import mindustry.type.Liquid;
import mindustry.type.LiquidStack;
import mindustry.ui.Bar;
import mindustry.world.consumers.ConsumeType;
import unity.gen.StemGenericCrafter;
import unity.world.consumers.ConsumeLiquids;

public class LiquidsSmelter extends StemGenericCrafter {
    protected Liquid[] liquids;

    public LiquidsSmelter(String name) {
        super(name);
    }

    public void init() {
        if (this.consumes.has(ConsumeType.liquid) && this.consumes.get(ConsumeType.liquid) instanceof ConsumeLiquids) {
            LiquidStack[] stacks = ((ConsumeLiquids)this.consumes.get(ConsumeType.liquid)).liquids;
            this.liquids = new Liquid[stacks.length];

            for(int i = 0; i < this.liquids.length; ++i) {
                this.liquids[i] = stacks[i].liquid;
            }

            super.init();
        } else {
            throw new RuntimeException("LiquidSmelter must have a ConsumeLiquids. Note that filters are not supported.");
        }
    }

    public void setBars() {
        super.setBars();
        this.bars.remove("liquid");

        for(Liquid liquid : this.liquids) {
            this.bars.add(liquid.name, (build) -> {
                Prov var10002 = () -> build.liquids.get(liquid) <= 0.001F ? Core.bundle.get("bar.liquid") : liquid.localizedName;
                Objects.requireNonNull(liquid);
                return new Bar(var10002, liquid::barColor, () -> build.liquids.get(liquid) / this.liquidCapacity);
            });
        }

    }
}
