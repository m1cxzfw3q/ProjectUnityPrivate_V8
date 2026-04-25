package unity.world.draw;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import mindustry.world.Block;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.consumers.ConsumeType;
import mindustry.world.draw.DrawBlock;
import unity.gen.LightHoldc;

public class DrawLightBlock extends DrawBlock {
    public TextureRegion baseRegion;
    public TextureRegion liquidRegion;

    public void draw(GenericCrafter.GenericCrafterBuild build) {
        Draw.rect(this.baseRegion, build.x, build.y);
        if (build.block.consumes.has(ConsumeType.liquid)) {
            Draw.color(build.liquids.current().color);
            Draw.alpha(build.liquids.currentAmount() / build.block.liquidCapacity);
            Draw.rect(this.liquidRegion, build.x, build.y);
            Draw.color();
        }

        TextureRegion var10000 = build.block.region;
        float var10001 = build.x;
        float var10002 = build.y;
        Block var3 = build.block;
        float var10003;
        if (var3 instanceof LightHoldc) {
            LightHoldc hold = (LightHoldc)var3;
            var10003 = hold.getRotation(build) - 90.0F;
        } else {
            var10003 = 0.0F;
        }

        Draw.rect(var10000, var10001, var10002, var10003);
    }

    public void load(Block block) {
        this.baseRegion = Core.atlas.find(block.name + "-base");
        this.liquidRegion = Core.atlas.find(block.name + "-liquid");
    }

    public TextureRegion[] icons(Block block) {
        return new TextureRegion[]{this.baseRegion, block.region};
    }
}
