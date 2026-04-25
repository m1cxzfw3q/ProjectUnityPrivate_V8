package unity.v8;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import mindustry.world.Block;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.draw.DrawBlock;

public class DrawGlow extends DrawBlock {
    public float glowAmount = 0.9F;
    public float glowScale = 3.0F;
    public TextureRegion top;

    public void draw(GenericCrafter.GenericCrafterBuild build) {
        Draw.rect(build.block.region, build.x, build.y);
        Draw.alpha(Mathf.absin(build.totalProgress, glowScale, glowAmount) * build.warmup);
        Draw.rect(top, build.x, build.y);
        Draw.reset();
    }

    public void load(Block block) {
        top = Core.atlas.find(block.name + "-top");
    }
}
