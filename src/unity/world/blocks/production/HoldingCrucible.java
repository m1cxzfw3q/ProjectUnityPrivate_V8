package unity.world.blocks.production;

import arc.graphics.g2d.Draw;
import unity.graphics.UnityDrawf;
import unity.world.blocks.GraphBlock;
import unity.world.graph.CrucibleGraph;
import unity.world.modules.GraphCrucibleModule;

public class HoldingCrucible extends GraphBlock {
    public HoldingCrucible(String name) {
        super(name);
        this.solid = true;
    }

    public class HoldingCrucibleBuild extends GraphBlock.GraphBuild {
        public HoldingCrucibleBuild() {
            super(HoldingCrucible.this);
        }

        public void draw() {
            Draw.rect(HoldingCrucible.this.region, this.x, this.y);
            this.drawContents();
            UnityDrawf.drawHeat(HoldingCrucible.this.heatRegion, this.x, this.y, 0.0F, this.heat().getTemp());
            this.drawTeamTop();
        }

        void drawContents() {
            GraphCrucibleModule crucGraph = this.crucible();
            if (crucGraph.getVolumeContained() > 0.0F) {
                Draw.color(((CrucibleGraph)crucGraph.getNetwork()).color);
                Draw.rect(HoldingCrucible.this.liquidRegion, this.x, this.y);
            }

            Draw.color();
        }
    }
}
