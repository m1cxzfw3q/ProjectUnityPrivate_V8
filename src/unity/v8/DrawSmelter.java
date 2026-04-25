package unity.v8;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.graphics.Drawf;
import mindustry.world.Block;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.draw.DrawBlock;

public class DrawSmelter extends DrawBlock {
    public Color flameColor = Color.valueOf("ffc999");
    public TextureRegion top;
    public float lightRadius = 60.0F;
    public float lightAlpha = 0.65F;
    public float lightSinScl = 10.0F;
    public float lightSinMag = 5.0F;
    public float flameRadius = 3.0F;
    public float flameRadiusIn = 1.9F;
    public float flameRadiusScl = 5.0F;
    public float flameRadiusMag = 2.0F;
    public float flameRadiusInMag = 1.0F;

    public DrawSmelter() {
    }

    public DrawSmelter(Color flameColor) {
        this.flameColor = flameColor;
    }

    public void load(Block block) {
        top = Core.atlas.find(block.name + "-top");
        block.clipSize = Math.max(block.clipSize, (this.lightRadius + this.lightSinMag) * 2.0F * (float)block.size);
    }

    public void draw(GenericCrafter.GenericCrafterBuild build) {
        Draw.rect(build.block.region, build.x, build.y, build.block.rotate ? build.rotdeg() : 0.0F);
        if (build.warmup > 0.0F && this.flameColor.a > 0.001F) {
            float g = 0.3F;
            float r = 0.06F;
            float cr = Mathf.random(0.1F);
            Draw.z(30.01F);
            Draw.alpha(build.warmup);
            Draw.rect(this.top, build.x, build.y);
            Draw.alpha((1.0F - g + Mathf.absin(Time.time, 8.0F, g) + Mathf.random(r) - r) * build.warmup);
            Draw.tint(this.flameColor);
            Fill.circle(build.x, build.y, this.flameRadius + Mathf.absin(Time.time, this.flameRadiusScl, this.flameRadiusMag) + cr);
            Draw.color(1.0F, 1.0F, 1.0F, build.warmup);
            Fill.circle(build.x, build.y, this.flameRadiusIn + Mathf.absin(Time.time, this.flameRadiusScl, this.flameRadiusInMag) + cr);
            Draw.color();
        }

    }

    public void drawLight(GenericCrafter.GenericCrafterBuild build) {
        Drawf.light(build.x, build.y, (this.lightRadius + Mathf.absin(this.lightSinScl, this.lightSinMag)) * build.warmup * (float)build.block.size, this.flameColor, this.lightAlpha);
    }
}
