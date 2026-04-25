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
    public Seq<UnitType[]> otherUpgrades = new Seq<>();
    protected int minTier;

    public SelectableReconstructor(String name) {
        super(name);
    }

    public void load() {
        super.load();
        outRegion = Core.atlas.find("unity-factory-out-" + size);
        inRegion = Core.atlas.find("unity-factory-in-" + size);
    }

    public void setStats() {
        stats.add(Stat.output, (table) -> {
            table.row();
            table.add("[accent]T" + minTier);
        });
        super.setStats();
        stats.add(Stat.output, (table) -> {
            float size = 24.0F;
            table.row();
            table.add("[accent]T" + (minTier + 1)).row();
            otherUpgrades.each((upgrade) -> {
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
        protected int tier = minTier;

        public void buildConfiguration(Table table) {
            table.button("T" + minTier, Styles.togglet, () -> tier = minTier).size(50f).update((b) -> b.setChecked(tier == minTier));
            table.button("T" + (minTier + 1), Styles.togglet, () -> tier = minTier + 1).size(50f).update((b) -> b.setChecked(tier == minTier + 1));
        }

        public UnitType upgrade(UnitType type) {
            UnitType[] ret = null;
            if (this.tier == SelectableReconstructor.this.minTier) {
                ret = SelectableReconstructor.this.upgrades.find((u) -> u[0] == type);
            } else if (this.tier == SelectableReconstructor.this.minTier + 1) {
                ret = SelectableReconstructor.this.otherUpgrades.find((u) -> u[0] == type);
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
