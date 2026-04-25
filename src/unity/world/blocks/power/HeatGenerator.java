package unity.world.blocks.power;

import unity.world.blocks.GraphBlock;
import unity.world.modules.GraphHeatModule;

public class HeatGenerator extends GraphBlock {
    protected float maxTemp = 9999.0F;
    protected float mulCoeff = 0.5F;

    public HeatGenerator(String name) {
        super(name);
    }

    public class HeatGeneratorBuild extends GraphBlock.GraphBuild {
        public HeatGeneratorBuild() {
            super(HeatGenerator.this);
        }

        protected void generateHeat(float mul) {
            GraphHeatModule hgraph = this.heat();
            hgraph.heat += Math.max(0.0F, HeatGenerator.this.maxTemp - hgraph.getTemp()) * HeatGenerator.this.mulCoeff * mul;
        }

        protected void generateHeat(float limit, float mul) {
            GraphHeatModule hgraph = this.heat();
            hgraph.heat += Math.min(limit, Math.max(0.0F, HeatGenerator.this.maxTemp - hgraph.getTemp()) * HeatGenerator.this.mulCoeff * mul);
        }
    }
}
