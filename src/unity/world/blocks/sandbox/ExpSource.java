package unity.world.blocks.sandbox;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.meta.Stat;
import unity.entities.ExpOrbs;
import unity.world.blocks.exp.ExpHolder;

public class ExpSource extends Block {
    public int produceTimer;
    public float reload;
    public int amount;
    public TextureRegion topRegion;

    public ExpSource(String name) {
        super(name);
        this.produceTimer = this.timers++;
        this.reload = 60.0F;
        this.amount = 100;
        this.update = true;
        this.solid = this.rotate = false;
    }

    public void load() {
        super.load();
        this.topRegion = Core.atlas.find(this.name + "-top");
    }

    public void setStats() {
        super.setStats();
        this.stats.add(Stat.itemCapacity, "@", new Object[]{Core.bundle.format("exp.expAmount", new Object[]{"-Infinity"})});
    }

    public class ExpSourceBuild extends Building {
        public void updateTile() {
            if (this.enabled && this.timer.get(ExpSource.this.produceTimer, ExpSource.this.reload)) {
                ExpOrbs.spreadExp(this.x, this.y, ExpSource.this.amount, 6.0F);

                for(Building b : this.proximity) {
                    if (b instanceof ExpHolder) {
                        ExpHolder exp = (ExpHolder)b;
                        exp.handleExp(99999999);
                    }
                }
            }

        }

        public void draw() {
            super.draw();
            Draw.blend(Blending.additive);
            Draw.color(Color.white);
            Draw.alpha(Mathf.absin(Time.time, 20.0F, 0.4F));
            Draw.rect(ExpSource.this.topRegion, this.x, this.y);
            Draw.blend();
            Draw.reset();
        }

        public void onDestroyed() {
            ExpOrbs.spreadExp(this.x, this.y, ExpSource.this.amount * 5, 8.0F);
            super.onDestroyed();
        }
    }
}
