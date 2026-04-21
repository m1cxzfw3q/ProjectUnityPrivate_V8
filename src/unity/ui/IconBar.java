package unity.ui;

import arc.func.Prov;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Font;
import arc.graphics.g2d.GlyphLayout;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.scene.Element;
import arc.util.Strings;
import arc.util.pooling.Pools;
import mindustry.ui.Fonts;

public class IconBar extends Element {
    private final Prov<IconBarStat> barStats;
    private final float prefHeight;

    public IconBar(float prefHeight, Prov<IconBarStat> barStats) {
        this.prefHeight = prefHeight;
        this.barStats = barStats;
    }

    public void draw() {
        Font font = Fonts.outline;
        GlyphLayout lay = (GlyphLayout)Pools.obtain(GlyphLayout.class, GlyphLayout::new);
        float xStart = 20.0F;
        IconBarStat data = (IconBarStat)this.barStats.get();
        float maxVal = Math.max(data.defaultMax, data.value);
        float minVal = Math.min(data.defaultMin, data.value);

        for(float i : data.values) {
            maxVal = Math.max(maxVal, i);
            minVal = Math.min(minVal, i);
        }

        float dPos = Mathf.map(data.value, minVal, maxVal, xStart, this.width - xStart);
        Lines.stroke(3.0F);
        Draw.color(Color.gray);
        float realX = this.x + xStart;
        float realY = this.y + this.height * 0.5F;
        Lines.rect(realX, realY - 8.0F, this.width - xStart, 16.0F);
        Draw.color(data.color);
        Fill.rect(realX, realY, 26.0F, 26.0F, 45.0F);
        Fill.rect(realX + dPos * 0.5F, realY, dPos, 16.0F);
        float d = maxVal - minVal;
        float stepSize = Mathf.pow(10.0F, (float)Mathf.floor(Mathf.log(10.0F, 2.0F * d)));
        if (d <= stepSize) {
            stepSize *= 0.5F;
        }

        if (d <= stepSize) {
            stepSize *= 0.4F;
        }

        if (d <= stepSize) {
            stepSize *= 0.5F;
        }

        Draw.color(Color.white);
        int i = 0;

        for(int len = data.values.length; i < len; ++i) {
            dPos = Mathf.map(data.values[i], minVal, maxVal, realX, this.x + this.width);
            Lines.line(dPos, realY, dPos, realY + 12.0F);
            Draw.rect(data.icons[i], dPos, this.y + this.height * 0.75F);
        }

        Draw.color(Color.lightGray);
        float stsrt = ((float)Mathf.floor(minVal / stepSize) + 1.0F) * stepSize;

        for(float i = stsrt; i < maxVal; i += stepSize) {
            dPos = Mathf.map(i, minVal, maxVal, realX, this.x + this.width);
            Lines.line(dPos, realY, dPos, realY - 12.0F);
            String text;
            if (i >= 1000.0F) {
                text = Strings.fixed(i / 1000.0F, 1) + "K'C";
            } else {
                text = Strings.fixed(i, 0) + "'C";
            }

            lay.setText(font, text);
            font.setColor(Color.white);
            font.draw(text, dPos - lay.width / 2.0F, this.y + this.height * 0.25F + lay.height / 2.0F + 1.0F);
        }

        Pools.free(lay);
    }

    public float getPrefHeight() {
        return this.prefHeight;
    }

    public float getPrefWidth() {
        return 180.0F;
    }

    public static class IconBarStat {
        public float defaultMax;
        public float defaultMin;
        final TextureRegion[] icons;
        final float[] values;
        final Color color;
        final float value;
        private int i;

        public IconBarStat(float value, float defaultMax, float defaultMin, Color color, int size) {
            this.value = value;
            this.defaultMax = defaultMax;
            this.defaultMin = defaultMin;
            this.color = color;
            this.icons = new TextureRegion[size];
            this.values = new float[size];
        }

        public void push(float value, TextureRegion icon) {
            this.values[this.i] = value;
            this.icons[this.i++] = icon;
        }
    }
}
