package unity.world.draw;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import mindustry.world.Block;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.draw.DrawBlock;
import unity.graphics.UnityPal;
import unity.world.blocks.exp.KoruhCrafter;

public class DrawExp extends DrawBlock {
    public TextureRegion exp;
    public TextureRegion top;
    public float glowAmount = 0.9F;
    public float glowScale = 8.0F;
    public Color flame;

    public DrawExp() {
        this.flame = Color.yellow;
    }

    public void draw(GenericCrafter.GenericCrafterBuild build) {
        Draw.rect(build.block.region, build.x, build.y);
        if (this.exp.found() && build instanceof KoruhCrafter.KoruhCrafterBuild) {
            KoruhCrafter.KoruhCrafterBuild kr = (KoruhCrafter.KoruhCrafterBuild)build;
            Draw.color(UnityPal.exp, Color.white, Mathf.absin(20.0F, 0.6F));
            Draw.alpha(kr.expf());
            Draw.rect(this.exp, build.x, build.y);
        }

        if (this.top.found()) {
            Draw.color(this.flame);
            Draw.alpha(Mathf.absin(build.totalProgress, this.glowScale, this.glowAmount) * build.warmup);
            Draw.rect(this.top, build.x, build.y);
        }

        Draw.reset();
    }

    public void load(Block block) {
        this.exp = Core.atlas.find(block.name + "-exp");
        this.top = Core.atlas.find(block.name + "-top");
    }
}
