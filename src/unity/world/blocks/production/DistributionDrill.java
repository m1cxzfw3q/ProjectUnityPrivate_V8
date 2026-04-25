package unity.world.blocks.production;

import arc.struct.Seq;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.world.blocks.production.Drill;

public class DistributionDrill extends Drill {
    protected int timerDumpAlt;

    public DistributionDrill(String name) {
        super(name);
        this.timerDumpAlt = this.timers++;
    }

    public class DistributionDrillBuild extends Drill.DrillBuild {
        protected Seq<Building> invalidBuildings = new Seq();
        protected boolean canDistribute = true;

        public DistributionDrillBuild() {
            super(DistributionDrill.this);
        }

        public boolean acceptItem(Building source, Item item) {
            return this.items.get(item) < this.getMaximumAccepted(item);
        }

        public boolean canDump(Building to, Item item) {
            if (!(to instanceof DistributionDrillBuild)) {
                return super.canDump(to, item);
            } else {
                DistributionDrillBuild b = (DistributionDrillBuild)to;
                return !b.invalidBuildings.contains(to) && this.canDistribute;
            }
        }

        public void handleItem(Building source, Item item) {
            if (source instanceof DistributionDrillBuild) {
                this.invalidBuildings.add(source);
            }

            super.handleItem(source, item);
        }

        protected void canDistribute() {
            for(int i = 0; i < this.proximity.size; ++i) {
                Building other = (Building)this.proximity.get((i + this.cdump) % this.proximity.size);
                if (!(other instanceof DistributionDrillBuild) && other.acceptItem(this, this.dominantItem)) {
                    this.canDistribute = false;
                    return;
                }
            }

        }

        public void updateTile() {
            if (this.dominantItem != null) {
                this.canDistribute();
            }

            if (this.timer.get(DistributionDrill.this.timerDumpAlt, 5.0F)) {
                this.dump();
            }

            super.updateTile();
            this.invalidBuildings.clear();
            this.canDistribute = true;
        }
    }
}
