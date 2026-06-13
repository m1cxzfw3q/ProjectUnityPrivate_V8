package unity.v8;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import mindustry.gen.Building;
import mindustry.world.*;
import mindustry.world.blocks.production.GenericCrafter.*;
import mindustry.world.draw.DrawBlock;

public class DrawGlow extends DrawBlock {
    public float glowAmount = 0.9f, glowScale = 3f;
    public TextureRegion top;

    @Override
    public void draw(Building build){
        if (!(build instanceof GenericCrafterBuild b)) return;
        Draw.rect(b.block.region, b.x, b.y);
        Draw.alpha(Mathf.absin(b.totalProgress, glowScale, glowAmount) * b.warmup);
        Draw.rect(top, b.x, b.y);
        Draw.reset();
    }

    @Override
    public void load(Block block){
        top = Core.atlas.find(block.name + "-top");
    }
}
