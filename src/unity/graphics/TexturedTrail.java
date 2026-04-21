package unity.graphics;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureAtlas;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.struct.FloatSeq;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.graphics.Pal;
import mindustry.graphics.Trail;

public class TexturedTrail extends Trail {
    public TextureRegion region;
    public TextureRegion capRegion;
    public float shrink;
    public float fadeAlpha;
    public float mixAlpha;
    public float baseWidth;
    public Color fadeColor;
    public Interp gradientInterp;
    public Interp fadeInterp;
    public Interp sideFadeInterp;
    public Interp mixInterp;
    public Blending blend;
    public boolean forceCap;
    public Effect trailEffect;
    public float trailChance;
    public float trailWidth;
    public Color trailColor;
    public float trailThreshold;
    private static final float[] vertices = new float[24];
    private static final Color tmp = new Color();
    protected final FloatSeq points;
    protected float lastX;
    protected float lastY;
    protected float lastAngle;
    protected float lastW;
    protected float counter;

    public TexturedTrail(TextureRegion region, TextureRegion capRegion, int length) {
        this(length);
        this.region = region;
        this.capRegion = capRegion;
    }

    public TexturedTrail(TextureRegion region, int length) {
        this(length);
        this.region = region;
        if (!Vars.headless && region instanceof TextureAtlas.AtlasRegion) {
            TextureAtlas.AtlasRegion reg = (TextureAtlas.AtlasRegion)region;
            this.capRegion = Core.atlas.find(reg.name + "-cap", "unity-hcircle");
        }

    }

    public TexturedTrail(int length) {
        super(0);
        this.shrink = 1.0F;
        this.fadeAlpha = 0.0F;
        this.mixAlpha = 0.5F;
        this.baseWidth = 1.0F;
        this.fadeColor = Color.white;
        this.gradientInterp = Interp.linear;
        this.fadeInterp = Interp.pow2In;
        this.sideFadeInterp = Interp.pow3In;
        this.mixInterp = Interp.pow5In;
        this.blend = Blending.normal;
        this.trailEffect = Fx.missileTrail;
        this.trailChance = 0.0F;
        this.trailWidth = 1.0F;
        this.trailColor = Pal.engine;
        this.trailThreshold = 3.0F;
        this.lastX = -1.0F;
        this.lastY = -1.0F;
        this.lastAngle = -1.0F;
        this.lastW = 0.0F;
        this.counter = 0.0F;
        this.length = length;
        this.points = new FloatSeq(length * 4);
    }

    public TexturedTrail copy() {
        TexturedTrail out = new TexturedTrail(this.region, this.capRegion, this.length);
        out.shrink = this.shrink;
        out.fadeAlpha = this.fadeAlpha;
        out.mixAlpha = this.mixAlpha;
        out.baseWidth = this.baseWidth;
        out.fadeColor = this.fadeColor;
        out.gradientInterp = this.gradientInterp;
        out.fadeInterp = this.fadeInterp;
        out.sideFadeInterp = this.sideFadeInterp;
        out.mixInterp = this.mixInterp;
        out.blend = this.blend;
        out.forceCap = this.forceCap;
        out.trailEffect = this.trailEffect;
        out.trailChance = this.trailChance;
        out.trailWidth = this.trailWidth;
        out.trailColor = this.trailColor;
        out.trailThreshold = this.trailThreshold;
        out.points.addAll(this.points);
        out.lastX = this.lastX;
        out.lastY = this.lastY;
        out.lastAngle = this.lastAngle;
        out.lastW = this.lastW;
        out.counter = this.counter;
        return out;
    }

    public void clear() {
        this.points.clear();
    }

    public int size() {
        return this.points.size / 4;
    }

    public void drawCap(Color color, float widthMultiplier) {
        if (!this.forceCap) {
            float width = this.baseWidth * widthMultiplier;
            if (this.capRegion == null) {
                this.capRegion = Core.atlas.find("unity-hcircle");
            }

            int psize = this.points.size;
            if (psize > 0) {
                float rv = Mathf.clamp(this.points.items[psize - 1]);
                float alpha = rv * this.fadeAlpha + (1.0F - this.fadeAlpha);
                float w = this.lastW * width / ((float)psize / 4.0F) * (((float)psize - 4.0F) / 4.0F) * 2.0F;
                float h = (float)this.capRegion.height / (float)this.capRegion.width * w;
                float angle = -57.295776F * this.lastAngle - 90.0F;
                float u = this.capRegion.u;
                float v = this.capRegion.v2;
                float u2 = this.capRegion.u2;
                float v2 = this.capRegion.v;
                float uh = Mathf.lerp(u, u2, 0.5F);
                float cx = Mathf.cosDeg(angle) * w / 2.0F;
                float cy = Mathf.sinDeg(angle) * w / 2.0F;
                float x1 = this.lastX;
                float y1 = this.lastY;
                float x2 = this.lastX + Mathf.cosDeg(angle + 90.0F) * h;
                float y2 = this.lastY + Mathf.sinDeg(angle + 90.0F) * h;
                float col1 = tmp.set(Draw.getColor()).lerp(this.fadeColor, this.gradientInterp.apply(1.0F - rv)).a(this.fadeInterp.apply(alpha)).toFloatBits();
                float col1h = tmp.set(Draw.getColor()).lerp(this.fadeColor, this.gradientInterp.apply(1.0F - rv)).a(this.sideFadeInterp.apply(alpha)).toFloatBits();
                float col2 = tmp.set(Draw.getColor()).lerp(this.fadeColor, this.gradientInterp.apply(1.0F - rv)).a(this.fadeInterp.apply(alpha)).toFloatBits();
                float col2h = tmp.set(Draw.getColor()).lerp(this.fadeColor, this.gradientInterp.apply(1.0F - rv)).a(this.sideFadeInterp.apply(alpha)).toFloatBits();
                float mix1 = tmp.set(color).a(this.mixInterp.apply(rv * this.mixAlpha)).toFloatBits();
                float mix2 = tmp.set(color).a(this.mixInterp.apply(rv * this.mixAlpha)).toFloatBits();
                Draw.blend(this.blend);
                vertices[0] = x1 - cx;
                vertices[1] = y1 - cy;
                vertices[2] = col1h;
                vertices[3] = u;
                vertices[4] = v;
                vertices[5] = mix1;
                vertices[6] = x1;
                vertices[7] = y1;
                vertices[8] = col1;
                vertices[9] = uh;
                vertices[10] = v;
                vertices[11] = mix1;
                vertices[12] = x2;
                vertices[13] = y2;
                vertices[14] = col2;
                vertices[15] = uh;
                vertices[16] = v2;
                vertices[17] = mix2;
                vertices[18] = x2 - cx;
                vertices[19] = y2 - cy;
                vertices[20] = col2h;
                vertices[21] = u;
                vertices[22] = v2;
                vertices[23] = mix2;
                Draw.vert(this.region.texture, vertices, 0, 24);
                vertices[6] = x1 + cx;
                vertices[7] = y1 + cy;
                vertices[8] = col1h;
                vertices[9] = u2;
                vertices[10] = v;
                vertices[11] = mix1;
                vertices[0] = x1;
                vertices[1] = y1;
                vertices[2] = col1;
                vertices[3] = uh;
                vertices[4] = v;
                vertices[5] = mix1;
                vertices[18] = x2;
                vertices[19] = y2;
                vertices[20] = col2;
                vertices[21] = uh;
                vertices[22] = v2;
                vertices[23] = mix2;
                vertices[12] = x2 + cx;
                vertices[13] = y2 + cy;
                vertices[14] = col2h;
                vertices[15] = u2;
                vertices[16] = v2;
                vertices[17] = mix2;
                Draw.vert(this.region.texture, vertices, 0, 24);
                Draw.blend();
            }

        }
    }

    public void draw(Color color, float widthMultiplier) {
        if (this.forceCap) {
            this.drawCap(color, widthMultiplier);
        }

        float width = this.baseWidth * widthMultiplier;
        if (this.region == null) {
            this.region = Core.atlas.find("white");
        }

        if (!this.points.isEmpty()) {
            float[] items = this.points.items;
            int psize = this.points.size;
            float endAngle = this.lastAngle;
            float lastAngle = endAngle;
            float u = this.region.u2;
            float v = this.region.v2;
            float u2 = this.region.u;
            float v2 = this.region.v;
            float uh = Mathf.lerp(u, u2, 0.5F);
            Draw.blend(this.blend);

            for(int i = 0; i < psize; i += 4) {
                float x1 = items[i];
                float y1 = items[i + 1];
                float w1 = items[i + 2];
                float rv1 = Mathf.clamp(items[i + 3]);
                float x2;
                float y2;
                float w2;
                float rv2;
                if (i < psize - 4) {
                    x2 = items[i + 4];
                    y2 = items[i + 5];
                    w2 = items[i + 6];
                    rv2 = Mathf.clamp(items[i + 7]);
                } else {
                    x2 = this.lastX;
                    y2 = this.lastY;
                    w2 = this.lastW;
                    rv2 = this.points.items[psize - 1];
                }

                float z2 = i == psize - 4 ? endAngle : -Angles.angleRad(x1, y1, x2, y2);
                float z1 = i == 0 ? z2 : lastAngle;
                float fs1 = Mathf.map((float)i, 0.0F, (float)psize, 1.0F - this.shrink, 1.0F) * width * w1;
                float fs2 = Mathf.map(Math.min((float)i + 4.0F, (float)psize - 4.0F), 0.0F, (float)psize, 1.0F - this.shrink, 1.0F) * width * w2;
                float cx = Mathf.sin(z1) * fs1;
                float cy = Mathf.cos(z1) * fs1;
                float nx = Mathf.sin(z2) * fs2;
                float ny = Mathf.cos(z2) * fs2;
                float mv1 = Mathf.lerp(v, v2, rv1);
                float mv2 = Mathf.lerp(v, v2, rv2);
                float cv1 = rv1 * this.fadeAlpha + (1.0F - this.fadeAlpha);
                float cv2 = rv2 * this.fadeAlpha + (1.0F - this.fadeAlpha);
                float col1 = tmp.set(Draw.getColor()).lerp(this.fadeColor, this.gradientInterp.apply(1.0F - rv1)).a(this.fadeInterp.apply(cv1)).toFloatBits();
                float col1h = tmp.set(Draw.getColor()).lerp(this.fadeColor, this.gradientInterp.apply(1.0F - rv1)).a(this.sideFadeInterp.apply(cv1)).toFloatBits();
                float col2 = tmp.set(Draw.getColor()).lerp(this.fadeColor, this.gradientInterp.apply(1.0F - rv2)).a(this.fadeInterp.apply(cv2)).toFloatBits();
                float col2h = tmp.set(Draw.getColor()).lerp(this.fadeColor, this.gradientInterp.apply(1.0F - rv2)).a(this.sideFadeInterp.apply(cv2)).toFloatBits();
                float mix1 = tmp.set(color).a(this.mixInterp.apply(rv1 * this.mixAlpha)).toFloatBits();
                float mix2 = tmp.set(color).a(this.mixInterp.apply(rv2 * this.mixAlpha)).toFloatBits();
                vertices[0] = x1 - cx;
                vertices[1] = y1 - cy;
                vertices[2] = col1h;
                vertices[3] = u;
                vertices[4] = mv1;
                vertices[5] = mix1;
                vertices[6] = x1;
                vertices[7] = y1;
                vertices[8] = col1;
                vertices[9] = uh;
                vertices[10] = mv1;
                vertices[11] = mix1;
                vertices[12] = x2;
                vertices[13] = y2;
                vertices[14] = col2;
                vertices[15] = uh;
                vertices[16] = mv2;
                vertices[17] = mix2;
                vertices[18] = x2 - nx;
                vertices[19] = y2 - ny;
                vertices[20] = col2h;
                vertices[21] = u;
                vertices[22] = mv2;
                vertices[23] = mix2;
                Draw.vert(this.region.texture, vertices, 0, 24);
                vertices[6] = x1 + cx;
                vertices[7] = y1 + cy;
                vertices[8] = col1h;
                vertices[9] = u2;
                vertices[10] = mv1;
                vertices[11] = mix1;
                vertices[0] = x1;
                vertices[1] = y1;
                vertices[2] = col1;
                vertices[3] = uh;
                vertices[4] = mv1;
                vertices[5] = mix1;
                vertices[18] = x2;
                vertices[19] = y2;
                vertices[20] = col2;
                vertices[21] = uh;
                vertices[22] = mv2;
                vertices[23] = mix2;
                vertices[12] = x2 + nx;
                vertices[13] = y2 + ny;
                vertices[14] = col2h;
                vertices[15] = u2;
                vertices[16] = mv2;
                vertices[17] = mix2;
                Draw.vert(this.region.texture, vertices, 0, 24);
                lastAngle = z2;
            }

            Draw.blend();
        }
    }

    public void shorten() {
        if ((this.counter += Time.delta) >= 0.96F) {
            if (this.points.size >= 4) {
                this.points.removeRange(0, 3);
            }

            this.counter = 0.0F;
        }

        this.calcProgress();
    }

    public void update(float x, float y, float widthMultiplier) {
        float dst = Mathf.dst(this.lastX, this.lastY, x, y);
        float speed = dst / Time.delta;
        float width = this.baseWidth * widthMultiplier;
        if ((this.counter += Time.delta) >= 0.96F) {
            if (this.points.size > this.length * 4) {
                this.points.removeRange(0, 3);
            }

            this.counter = 0.0F;
            this.points.add(x, y, width, 0.0F);
        }

        this.lastAngle = Mathf.zero(dst, 0.4F) ? this.lastAngle : -Angles.angleRad(x, y, this.lastX, this.lastY) + (float)Math.PI;
        this.lastX = x;
        this.lastY = y;
        this.lastW = width;
        this.calcProgress();
        if (this.points.size > 0 && this.trailChance > 0.0F && Mathf.chanceDelta((double)(this.trailChance * Mathf.clamp(speed / this.trailThreshold)))) {
            this.trailEffect.at(x, y, width * this.trailWidth, tmp.set(this.trailColor).a(this.fadeInterp.apply(Mathf.clamp(this.points.items[this.points.size - 1]) * this.fadeAlpha + (1.0F - this.fadeAlpha))));
        }

    }

    public void calcProgress() {
        int psize = this.points.size;
        if (psize > 0) {
            float[] items = this.points.items;
            float maxDst = 0.0F;

            for(int i = 0; i < psize; i += 4) {
                float x1 = items[i];
                float y1 = items[i + 1];
                float dst = i < psize - 4 ? Mathf.dst(x1, y1, items[i + 4], items[i + 5]) : Mathf.dst(x1, y1, this.lastX, this.lastY);
                maxDst += dst;
                items[i + 3] = dst;
            }

            float frac = (float)this.points.size / 4.0F / (float)this.length;
            float first = items[3];
            float last = 0.0F;

            for(int i = 0; i < psize; i += 4) {
                float v = items[i + 3];
                items[i + 3] = Mathf.clamp((v + last - first) / maxDst * frac);
                last += v;
            }
        }

    }
}
