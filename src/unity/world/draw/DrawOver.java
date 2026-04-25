package unity.world.draw;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import mindustry.gen.Building;
import mindustry.world.Block;
import unity.world.blocks.exp.LevelHolder;

public class DrawOver extends DrawLevel {
    public TextureRegion[] levelRegions;
    public float layer = 35.0F;

    public void load(Block block) {
        int n;
        for(n = 1; n <= 100; ++n) {
            TextureRegion t = Core.atlas.find(block.name + n);
            if (!t.found()) {
                break;
            }
        }

        if (n > 1) {
            this.levelRegions = new TextureRegion[n];
            this.levelRegions[0] = block.region;

            for(int i = 1; i < n; ++i) {
                this.levelRegions[i] = Core.atlas.find(block.name + i);
            }
        }

    }

    public <T extends Building & LevelHolder> void draw(T build) {
        TextureRegion r = this.levelRegion(build);
        if (r != build.block.region) {
            Draw.z(this.layer);
            Draw.rect(r, build.x, build.y, build.block.rotate ? build.rotdeg() : 0.0F);
        }

    }

    public <T extends Building & LevelHolder> TextureRegion levelRegion(T build) {
        return this.levelRegions == null ? build.block.region : this.levelRegions[Math.min((int)(((LevelHolder)build).levelf() * (float)this.levelRegions.length), this.levelRegions.length - 1)];
    }
}
