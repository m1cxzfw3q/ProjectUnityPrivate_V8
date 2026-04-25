package unity.world.blocks.power;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.content.Fx;
import mindustry.type.Item;
import mindustry.world.consumers.ConsumeItemFilter;
import unity.graphics.UnityDrawf;

public class CombustionHeater extends HeatGenerator {
    public final TextureRegion[] baseRegions = new TextureRegion[4];

    public CombustionHeater(String name) {
        super(name);
        this.rotate = true;
    }

    public void load() {
        super.load();

        for(int i = 0; i < 4; ++i) {
            this.baseRegions[i] = Core.atlas.find(this.name + "-base" + (i + 1));
        }

    }

    public void init() {
        ((ConsumeItemFilter)this.consumes.add(new ConsumeItemFilter((item) -> item.flammability >= 0.1F))).update(false).optional(true, false);
        super.init();
    }

    public class CombustionHeaterBuild extends HeatGenerator.HeatGeneratorBuild {
        float generateTime;
        float productionEfficiency;

        public CombustionHeaterBuild() {
            super(CombustionHeater.this);
        }

        public boolean productionValid() {
            return this.generateTime > 0.0F;
        }

        public void updatePost() {
            if (!this.consValid()) {
                this.productionEfficiency = 0.0F;
            } else {
                if (this.generateTime <= 0.0F && (float)this.items.total() > 0.0F) {
                    Fx.generatespark.at(this.x + Mathf.range(3.0F), this.y + Mathf.range(3.0F));
                    Item item = this.items.take();
                    this.productionEfficiency = item.flammability;
                    this.generateTime = 1.0F;
                }

                if (this.generateTime > 0.0F) {
                    this.generateTime -= Math.min(0.01F * this.delta(), this.generateTime);
                } else {
                    this.productionEfficiency = 0.0F;
                }

                this.generateHeat(this.productionEfficiency);
            }
        }

        public void draw() {
            Draw.rect(CombustionHeater.this.baseRegions[this.rotation], this.x, this.y);
            UnityDrawf.drawHeat(CombustionHeater.this.heatRegion, this.x, this.y, this.rotdeg(), this.heat().getTemp());
            this.drawTeamTop();
        }

        public void writeExt(Writes write) {
            write.f(this.productionEfficiency);
        }

        public void readExt(Reads read, byte revision) {
            this.productionEfficiency = read.f();
        }
    }
}
