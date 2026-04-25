package unity.world.blocks.sandbox;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import unity.graphics.UnityDrawf;
import unity.world.blocks.power.HeatGenerator;

public class HeatSource extends HeatGenerator {
    protected boolean isVoid;
    TextureRegion baseRegion;

    public HeatSource(String name) {
        super(name);
    }

    public void load() {
        super.load();
        this.baseRegion = Core.atlas.find(this.name + "-base");
    }

    public class HeatSourceBuild extends HeatGenerator.HeatGeneratorBuild {
        public HeatSourceBuild() {
            super(HeatSource.this);
        }

        public void updatePost() {
            if (HeatSource.this.isVoid) {
                this.heat().heat = 0.0F;
            } else {
                this.generateHeat(1.0F);
            }

        }

        public void draw() {
            float temp = this.heat().getTemp();
            Draw.rect(HeatSource.this.baseRegion, this.x, this.y);
            UnityDrawf.drawHeat(HeatSource.this.heatRegion, this.x, this.y, this.rotdeg(), temp);
            this.drawTeamTop();
        }
    }
}
