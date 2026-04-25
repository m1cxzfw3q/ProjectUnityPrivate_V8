package unity.world.consumers;

import arc.scene.ui.layout.Table;
import arc.struct.Bits;
import mindustry.gen.Building;
import mindustry.type.LiquidStack;
import mindustry.ui.ReqImage;
import mindustry.world.consumers.Consume;
import mindustry.world.consumers.ConsumeType;
import mindustry.world.meta.Stat;
import mindustry.world.meta.Stats;

public class ConsumeLiquids extends Consume {
    public final LiquidStack[] liquids;

    public ConsumeLiquids(LiquidStack[] liquids) {
        this.liquids = liquids;
    }

    public void applyLiquidFilter(Bits filter) {
        for(LiquidStack stack : this.liquids) {
            filter.set(stack.liquid.id);
        }

    }

    public ConsumeType type() {
        return ConsumeType.liquid;
    }

    public void build(Building build, Table table) {
        for(LiquidStack stack : this.liquids) {
            table.add(new ReqImage(stack.liquid.uiIcon, () -> build.liquids != null && build.liquids.get(stack.liquid) >= stack.amount)).padRight(8.0F);
        }

    }

    public String getIcon() {
        return "icon-liquid-consume";
    }

    public void update(Building build) {
        for(LiquidStack stack : this.liquids) {
            build.liquids.remove(stack.liquid, Math.min(this.use(build, stack.amount), build.liquids.get(stack.liquid)));
        }

    }

    public boolean valid(Building build) {
        if (build != null && build.liquids != null) {
            for(LiquidStack stack : this.liquids) {
                if (build.liquids.get(stack.liquid) < this.use(build, stack.amount)) {
                    return false;
                }
            }

            return true;
        } else {
            return false;
        }
    }

    public void display(Stats stats) {
        for(LiquidStack stack : this.liquids) {
            stats.add(this.booster ? Stat.booster : Stat.input, stack.liquid, stack.amount * 60.0F, false);
        }

    }

    private float use(Building build, float amount) {
        return Math.min(amount * build.edelta(), build.block.liquidCapacity);
    }
}
