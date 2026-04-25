package unity.v8;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.util.Nullable;
import mindustry.graphics.Drawf;
import mindustry.type.Liquid;
import mindustry.world.Block;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.consumers.Consume;
import mindustry.world.consumers.ConsumeLiquid;
import mindustry.world.draw.DrawBlock;

public class DrawLiquid extends DrawBlock {
    public TextureRegion inLiquid;
    public TextureRegion liquid;
    public TextureRegion top;
    public boolean useOutputSprite = false;
    private static @Nullable ConsumeLiquid consume;

    public DrawLiquid() {}

    public DrawLiquid(boolean useOutputSprite) {
        this.useOutputSprite = useOutputSprite;
    }

    public void draw(GenericCrafter.GenericCrafterBuild build) {
        Draw.rect(build.block.region, build.x, build.y);
        GenericCrafter type = (GenericCrafter)build.block;

        for (Consume cons : type.consumers) {
            if (cons instanceof ConsumeLiquid c) {
                consume = c;
                break;
            }
        }

        if ((inLiquid.found() || useOutputSprite) && consume != null) {
            Liquid input = consume.liquid;
            Drawf.liquid(useOutputSprite ? liquid : inLiquid, build.x, build.y, build.liquids.get(input) / type.liquidCapacity, input.color);
        }

        if (type.outputLiquid != null && build.liquids.get(type.outputLiquid.liquid) > 0.0F) {
            Drawf.liquid(liquid, build.x, build.y, build.liquids.get(type.outputLiquid.liquid) / type.liquidCapacity, type.outputLiquid.liquid.color);
        }

        if (top.found()) {
            Draw.rect(top, build.x, build.y);
        }
    }

    public void load(Block block) {
        top = Core.atlas.find(block.name + "-top");
        liquid = Core.atlas.find(block.name + "-liquid");
        inLiquid = Core.atlas.find(block.name + "-input-liquid");
    }

    public TextureRegion[] icons(Block block) {
        return top.found() ? new TextureRegion[]{block.region, top} : new TextureRegion[]{block.region};
    }
}
