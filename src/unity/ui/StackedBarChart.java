package unity.ui;

import arc.func.Prov;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Font;
import arc.graphics.g2d.GlyphLayout;
import arc.scene.Element;
import arc.util.pooling.Pools;
import mindustry.ui.Fonts;

public class StackedBarChart extends Element {
    private final Prov<BarStat[]> barStats;
    private final float prefHeight;

    public StackedBarChart(float prefHeight, Prov<BarStat[]> barStats) {
        this.prefHeight = prefHeight;
        this.barStats = barStats;
    }

    public void draw() {
        Font font = Fonts.outline;
        GlyphLayout lay = (GlyphLayout)Pools.obtain(GlyphLayout.class, GlyphLayout::new);
        float totalWeight = 0.0F;
        BarStat[] data = (BarStat[])this.barStats.get();

        for(BarStat i : data) {
            totalWeight += i.weight;
        }

        float yPos = this.y;

        for(BarStat i : data) {
            float ah = this.height * i.weight / totalWeight;
            float aw = this.width * i.filled;
            String text = i.name;
            Draw.color(i.dark);
            Fill.rect(this.x + this.width * 0.5F, yPos + ah * 0.5F, this.width, ah);
            Draw.color(i.color);
            Fill.rect(this.x + aw * 0.5F, yPos + ah * 0.5F, aw, ah);
            lay.setText(font, text);
            font.setColor(Color.white);
            font.draw(text, this.x + this.width / 2.0F - lay.width / 2.0F, yPos + ah / 2.0F + lay.height / 2.0F + 1.0F);
            yPos += ah;
        }

        Pools.free(lay);
    }

    public float getPrefHeight() {
        return this.prefHeight;
    }

    public float getPrefWidth() {
        return 180.0F;
    }

    public static class BarStat {
        final Color color;
        final Color dark;
        final String name;
        final float weight;
        final float filled;

        public BarStat(String name, float weight, float filled, Color color) {
            this.name = name;
            this.weight = weight;
            this.filled = filled;
            this.color = color;
            this.dark = color.cpy().mul(0.5F);
        }
    }
}
