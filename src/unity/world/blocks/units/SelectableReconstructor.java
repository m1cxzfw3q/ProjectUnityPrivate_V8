package unity.world.blocks.units;

import arc.Core;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Scaling;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.type.UnitType;
import mindustry.ui.Styles;
import mindustry.world.blocks.units.Reconstructor;
import mindustry.world.meta.Stat;

public class SelectableReconstructor extends Reconstructor {
    public Seq<UnitType[]> otherUpgrades = new Seq();
    protected int minTier;

    public SelectableReconstructor(String name) {
        super(name);
    }

    public void load() {
        super.load();
        this.outRegion = Core.atlas.find("unity-factory-out-" + this.size);
        this.inRegion = Core.atlas.find("unity-factory-in-" + this.size);
    }

    public void setStats() {
        this.stats.add(Stat.output, (table) -> {
            table.row();
            table.add("[accent]T" + this.minTier);
        });
        super.setStats();
        this.stats.add(Stat.output, (table) -> {
            float size = 24.0F;
            table.row();
            table.add("[accent]T" + (this.minTier + 1)).row();
            this.otherUpgrades.each((upgrade) -> {
                if (upgrade[0].unlockedNow() && upgrade[1].unlockedNow()) {
                    table.image(upgrade[0].uiIcon).size(size).padRight(4.0F).padLeft(10.0F).scaling(Scaling.fit).right();
                    table.add(upgrade[0].localizedName).left();
                    table.add("[lightgray] -> ");
                    table.image(upgrade[1].uiIcon).size(size).padRight(4.0F).scaling(Scaling.fit);
                    table.add(upgrade[1].localizedName).left();
                    table.row();
                }

            });
        });
    }

    public class SelectableReconstructorBuild extends Reconstructor.ReconstructorBuild {
        protected int tier;

        public SelectableReconstructorBuild() {
            super(SelectableReconstructor.this);
            this.tier = SelectableReconstructor.this.minTier;
        }

        public void buildConfiguration(Table table) {
            table.button("T" + SelectableReconstructor.this.minTier, Styles.togglet, () -> this.tier = SelectableReconstructor.this.minTier).width(50.0F).height(50.0F).update((b) -> b.setChecked(this.tier == SelectableReconstructor.this.minTier));
            table.button("T" + (SelectableReconstructor.this.minTier + 1), Styles.togglet, () -> this.tier = SelectableReconstructor.this.minTier + 1).width(50.0F).height(50.0F).update((b) -> b.setChecked(this.tier == SelectableReconstructor.this.minTier + 1));
        }

        public UnitType upgrade(UnitType type) {
            UnitType[] ret = null;
            if (this.tier == SelectableReconstructor.this.minTier) {
                ret = (UnitType[])SelectableReconstructor.this.upgrades.find((u) -> u[0] == type);
            } else if (this.tier == SelectableReconstructor.this.minTier + 1) {
                ret = (UnitType[])SelectableReconstructor.this.otherUpgrades.find((u) -> u[0] == type);
            }

            return ret == null ? null : ret[1];
        }

        public void write(Writes write) {
            super.write(write);
            write.b(this.tier);
        }

        public void read(Reads read, byte revision) {
            super.read(read, revision);
            this.tier = read.b();
        }
    }
}
