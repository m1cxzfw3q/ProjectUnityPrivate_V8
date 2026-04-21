package unity.ui;

import arc.func.Floatf;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.input.KeyCode;
import arc.math.Mathf;
import arc.scene.Element;
import arc.scene.event.InputEvent;
import arc.scene.event.InputListener;
import mindustry.graphics.Pal;

public class Graph extends Element {
    private final float drawPad;
    public Color background;
    public Color backlines;
    public Color graphline;
    public Color selection;
    public Color selectline;
    public int steps;
    public boolean dots;
    public Floatf<Integer> fun;
    public int lastMouseStep;
    public boolean lastMouseOver;
    private float minf;
    private float maxf;

    public Graph(Floatf<Integer> fun, int steps, Color graphline) {
        this.drawPad = 0.02F;
        this.background = Color.black;
        this.backlines = Pal.gray;
        this.selection = Pal.accent;
        this.selectline = Pal.accentBack;
        this.dots = true;
        this.lastMouseStep = 0;
        this.lastMouseOver = false;
        this.fun = fun;
        this.steps = steps;
        this.graphline = graphline;
        this.lastMouseOver = false;
        this.dots = steps <= 20;
        this.init();
        this.setSize(this.getPrefWidth(), this.getPrefHeight());
        this.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, KeyCode button) {
                Graph.this.select(x, y);
                return true;
            }

            public boolean mouseMoved(InputEvent event, float x, float y) {
                Graph.this.select(x, y);
                return false;
            }
        });
    }

    public Graph(Floatf<Integer> fun, int steps) {
        this(fun, steps, Color.white);
    }

    public void init() {
        this.minf = this.fun.get(0);
        this.maxf = this.fun.get(this.steps);
        if (this.minf > this.maxf) {
            float c = this.minf;
            this.minf = this.maxf;
            this.maxf = c;
        }

    }

    public void select(float cx, float cy) {
        float w = this.getWidth();
        float x = this.x + 0.02F * w;
        w -= w * 0.02F * 2.0F;
        if (cx < x) {
            cx = x;
        } else if (cx > x + w) {
            cx = x + w;
        }

        this.lastMouseOver = true;
        this.lastMouseStep = Mathf.clamp(Mathf.roundPositive((cx - x) * (float)this.steps / w), 0, this.steps);
    }

    public float mouseValue() {
        return this.fun.get(this.lastMouseStep);
    }

    public void draw() {
        this.validate();
        float width = this.getWidth();
        float height = this.getHeight();
        float x = this.x + width * 0.02F;
        width -= width * 0.02F * 2.0F;
        Draw.color(this.background, this.parentAlpha);
        Fill.rect(x, this.y, width, height);
        float stepw = width / (float)this.steps;
        float sy = this.y + 0.06F * height;
        float ey = this.y + height * 0.94F;
        float selx = x + stepw * (float)this.lastMouseStep;
        float sely = Mathf.map(this.fun.get(this.lastMouseStep), this.minf, this.maxf, sy, ey);
        float stroke = 3.5F;
        Draw.color(this.backlines, this.parentAlpha);

        for(int i = 0; i < this.steps; i += 2) {
            Fill.rect(x + stepw * (float)i + stepw / 2.0F, this.y + height / 2.0F, stepw, height);
        }

        Lines.stroke(stroke);
        if (this.lastMouseOver) {
            Draw.color(this.selectline, this.parentAlpha * 0.7F);
            Lines.line(selx, this.y, selx, sely);
            Lines.line(x, sely, selx, sely);
        }

        Draw.color(this.graphline, this.parentAlpha);
        Lines.beginLine();

        for(int i = 0; i <= this.steps; ++i) {
            float yy = Mathf.map(this.fun.get(i), this.minf, this.maxf, sy, ey);
            Lines.linePoint(x + stepw * (float)i, yy);
            if (this.dots) {
                Fill.square(x + stepw * (float)i, yy, 1.4F * stroke, 45.0F);
            }
        }

        Lines.endLine(false);
        if (this.lastMouseOver) {
            Draw.color(this.selection, this.parentAlpha);
            Fill.square(selx, sely, 1.5F * stroke, 45.0F);
        }

    }
}
