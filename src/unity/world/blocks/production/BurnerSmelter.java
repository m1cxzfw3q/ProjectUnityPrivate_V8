package unity.world.blocks.production;

import arc.Core;
import arc.math.Mathf;
import mindustry.gen.Building;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.ui.Bar;
import mindustry.world.consumers.ConsumeItemFilter;
import mindustry.world.consumers.ConsumeType;
import mindustry.world.meta.Stat;
import unity.content.UnityItems;
import unity.gen.StemGenericCrafter;

public class BurnerSmelter extends StemGenericCrafter {
    public Item input;
    public float minEfficiency = 0.6F;
    public float boostScale = 1.25F;
    public float boostConstant = -0.75F;

    public BurnerSmelter(String name) {
        super(name);
    }

    public void init() {
        if (!this.consumes.has(ConsumeType.item)) {
            ((ConsumeItemFilter)this.consumes.add(new ConsumeItemFilter((item) -> this.getItemEfficiency(item) > this.minEfficiency))).update(false).optional(true, false);
        }

        if (this.input == null) {
            this.input = UnityItems.stone;
        }

        super.init();
    }

    public void setBars() {
        super.setBars();
        this.bars.add("efficiency", (build) -> new Bar(() -> Core.bundle.format("bar.efficiency", new Object[]{(int)(100.0F * build.productionEfficiency)}), () -> Pal.lighterOrange, () -> build.productionEfficiency));
    }

    public void setStats() {
        this.stats.add(Stat.input, this.input);
        super.setStats();
    }

    protected float getItemEfficiency(Item item) {
        return item.flammability;
    }

    public class BurnerSmelterBuild extends StemGenericCrafter.StemGenericCrafterBuild {
        public float itemDuration;
        public float productionEfficiency;

        public BurnerSmelterBuild() {
            super(BurnerSmelter.this);
        }

        public void updateTile() {
            if (this.items.has(BurnerSmelter.this.input) && this.itemDuration > 0.0F) {
                this.progress += this.getProgressIncrease(BurnerSmelter.this.craftTime) * this.productionEfficiency;
                this.itemDuration -= this.delta();
                this.totalProgress += this.delta();
                this.warmup = Mathf.lerpDelta(this.warmup, 1.0F, 0.02F);
                if (Mathf.chanceDelta((double)BurnerSmelter.this.updateEffectChance)) {
                    BurnerSmelter.this.updateEffect.at(this.x + Mathf.range((float)BurnerSmelter.this.size * 4.0F), this.y + Mathf.range((float)BurnerSmelter.this.size * 4.0F));
                }
            } else {
                if (this.itemDuration <= 0.0F) {
                    this.productionEfficiency = 0.0F;
                    if (this.items.has(BurnerSmelter.this.input) && this.consValid()) {
                        int temp = this.items.nextIndex(-1);
                        if (temp == BurnerSmelter.this.input.id) {
                            temp = this.items.nextIndex(temp);
                        }

                        if (temp != BurnerSmelter.this.input.id) {
                            Item item = this.items.takeIndex(temp);
                            this.productionEfficiency = BurnerSmelter.this.getItemEfficiency(item) * BurnerSmelter.this.boostScale + BurnerSmelter.this.boostConstant;
                            this.items.remove(item, 1);
                            this.itemDuration = BurnerSmelter.this.craftTime;
                        }
                    }
                } else {
                    this.itemDuration -= this.delta();
                }

                this.warmup = Mathf.lerp(this.warmup, 0.0F, 0.02F);
            }

            if (this.progress >= 1.0F) {
                this.items.remove(BurnerSmelter.this.input, 1);
                if (BurnerSmelter.this.outputLiquid != null) {
                    this.handleLiquid(this, BurnerSmelter.this.outputLiquid.liquid, BurnerSmelter.this.outputLiquid.amount);
                }

                BurnerSmelter.this.craftEffect.at(this.x, this.y);
                this.progress = 0.0F;
            }

            if (BurnerSmelter.this.outputLiquid != null) {
                this.dumpLiquid(BurnerSmelter.this.outputLiquid.liquid);
            }

            super.updateTile();
        }

        public boolean acceptItem(Building source, Item item) {
            return (this.block.consumes.itemFilters.get(item.id) || item == BurnerSmelter.this.input) && this.items.get(item) < this.getMaximumAccepted(item);
        }
    }
}
