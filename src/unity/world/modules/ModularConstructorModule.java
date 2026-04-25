package unity.world.modules;

import arc.struct.IntSet;
import arc.struct.Seq;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.Building;
import mindustry.gen.Buildingc;
import mindustry.world.modules.BlockModule;
import unity.world.blocks.units.ModularConstructor;
import unity.world.blocks.units.ModularConstructorPart;

public class ModularConstructorModule extends BlockModule {
    public ModularConstructorGraph graph;
    boolean main = false;

    public ModularConstructorModule() {
    }

    public ModularConstructorModule(ModularConstructor.ModularConstructorBuild build) {
        this.graph = new ModularConstructorGraph();
        this.graph.main = build;
        this.main = true;
    }

    public void update() {
        if (this.graph != null) {
            this.graph.update();
        }

    }

    public void write(Writes write) {
    }

    public void read(Reads read) {
        if (this.main && this.graph != null && this.graph.main != null) {
            this.graph.queueAdded = true;
        }

    }

    public static class ModularConstructorGraph {
        public Seq<ModularConstructorPart.ModularConstructorPartBuild> all = new Seq();
        public Seq<ModularConstructorPart.ModularConstructorPartBuild> toRemove = new Seq();
        public IntSet toRemoveSet = new IntSet();
        public IntSet tmp = new IntSet();
        public float tier = 0.0F;
        public ModularConstructor.ModularConstructorBuild main;
        public boolean queueAdded = false;

        public void added(ModularConstructor.ModularConstructorBuild b) {
            this.all.clear();
            b.updateProximity();

            for(Building other : b.proximity) {
                if (other instanceof ModularConstructorPart.ModularConstructorPartBuild) {
                    ModularConstructorPart.ModularConstructorPartBuild mod = (ModularConstructorPart.ModularConstructorPartBuild)other;
                    if (b.consConnected(other) && this.tmp.add(other.pos())) {
                        mod.module.graph = this;
                        this.all.add(mod);
                        mod.updateBack();
                    }
                }
            }

            this.tmp.clear();
        }

        public void remove(ModularConstructorPart.ModularConstructorPartBuild build) {
            if (this.toRemoveSet.add(build.pos())) {
                this.toRemove.add(build);
                build.module.graph = null;
            }

        }

        void update() {
            if (this.queueAdded && this.main != null) {
                this.added(this.main);
                this.queueAdded = false;
            }

            for(ModularConstructorPart.ModularConstructorPartBuild build : this.all) {
                if (!build.added) {
                    build.removePart();
                }
            }

            this.all.removeAll(this.toRemove);
            this.toRemove.clear();
            this.toRemoveSet.clear();
            this.tier = (float)this.all.size / (float)this.main.moduleConnections();
            this.main.tier = this.all.size / this.main.moduleConnections();
        }
    }

    public interface ModularConstructorModuleInterface extends Buildingc {
        ModularConstructorModule consModule();

        boolean consConnected(Building var1);
    }
}
