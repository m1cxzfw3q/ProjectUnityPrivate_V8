package unity.world.blocks.distribution;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Eachable;
import mindustry.content.StatusEffects;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Unit;
import unity.util.GraphicUtils;
import unity.util.Utils;
import unity.world.blocks.GraphBlock;

public class HeatPipe extends GraphBlock {
    static final Color baseColor = Color.valueOf("6e7080");
    static final int[] shift = new int[]{0, 3, 2, 1};
    TextureRegion[] heatRegions;
    TextureRegion[] regions;

    public HeatPipe(String name) {
        super(name);
    }

    public void load() {
        super.load();
        this.heatRegions = GraphicUtils.getRegions(this.heatRegion, 8, 2);
        this.regions = GraphicUtils.getRegions(Core.atlas.find(this.name + "-tiles"), 8, 2);
    }

    public void drawRequestRegion(BuildPlan req, Eachable<BuildPlan> list) {
        float scl = 8.0F * req.animScale;
        Draw.rect(this.region, req.drawx(), req.drawy(), scl, scl, (float)req.rotation * 90.0F);
    }

    public class HeatPipeBuild extends GraphBlock.GraphBuild {
        int spriteIndex;

        public HeatPipeBuild() {
            super(HeatPipe.this);
        }

        public void created() {
            this.rotation = 0;
            super.created();
        }

        public void onNeighboursChanged() {
            this.spriteIndex = 0;
            this.heat().eachNeighbourValue((n) -> this.spriteIndex += 1 << HeatPipe.shift[n]);
        }

        public void unitOn(Unit unit) {
            if (this.timer(5, 20.0F)) {
                float intensity = Mathf.clamp(Mathf.map(this.heat().getTemp(), 400.0F, 1000.0F, 0.0F, 1.0F));
                unit.apply(StatusEffects.burning, intensity * 20.0F + 5.0F);
                unit.damage(intensity * 10.0F);
            }

        }

        public void draw() {
            float temp = this.heat().getTemp();
            Draw.rect(HeatPipe.this.regions[this.spriteIndex], this.x, this.y);
            if (temp < 273.0F || temp > 498.0F) {
                Draw.color(Utils.tempColor(temp).add(HeatPipe.baseColor));
                Draw.rect(HeatPipe.this.heatRegions[this.spriteIndex], this.x, this.y);
                Draw.color();
            }

            this.drawTeamTop();
        }
    }
}
