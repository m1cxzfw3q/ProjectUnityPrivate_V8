package unity.entities.bullet.monolith.laser;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Interp;
import arc.math.Mathf;
import arc.util.Tmp;
import mindustry.entities.bullet.LaserBulletType;
import mindustry.gen.Bullet;
import unity.graphics.UnityDrawf;
import unity.graphics.UnityPal;

public class HelixLaserBulletType extends LaserBulletType {
    public int swirlAmount = 3;
    public Color swirlColor;
    public Color swirlColorDark;
    public Color dashColor;
    public Color dashColorDark;
    public float swirlScale;
    public float swirlMagnitude;
    public float swirlThickness;
    public float swirlIn;
    public float swirlStay;
    public float swirlOut;
    public float swirlFrom;
    public float swirlTo;
    public float laserExtTime;
    public float laserShrinkTime;
    public float laserTo;
    public float dashWidth;
    public float dashFrom;
    public float dashTo;
    public float dashThickness;
    public Interp laserGrowInterp;
    public Interp laserShrinkInterp;
    public Interp laserThickInterp;
    public Interp swirlInInterp;
    public Interp swirlOutInterp;
    public Interp swirlFadeInterp;
    public Interp dashInterp1;
    public Interp dashInterp2;
    public Interp dashColorInterp;

    public HelixLaserBulletType(float damage) {
        super(damage);
        this.swirlColor = UnityPal.monolith;
        this.swirlColorDark = UnityPal.monolithDark;
        this.dashColor = UnityPal.monolithLight;
        this.dashColorDark = UnityPal.monolith;
        this.swirlScale = 12.0F;
        this.swirlMagnitude = 6.0F;
        this.swirlThickness = 1.0F;
        this.swirlIn = 0.1F;
        this.swirlStay = 0.2F;
        this.swirlOut = 0.5F;
        this.swirlFrom = 0.1F;
        this.swirlTo = 0.96F;
        this.laserExtTime = 0.2F;
        this.laserShrinkTime = 0.3F;
        this.laserTo = 1.0F;
        this.dashWidth = 8.0F;
        this.dashFrom = 0.2F;
        this.dashTo = 0.92F;
        this.dashThickness = 1.5F;
        this.laserGrowInterp = Interp.pow2Out;
        this.laserShrinkInterp = Interp.pow2Out;
        this.laserThickInterp = Interp.pow4Out;
        this.swirlInInterp = Interp.pow2In;
        this.swirlOutInterp = Interp.pow3In;
        this.swirlFadeInterp = Interp.pow10Out;
        this.dashInterp1 = Interp.pow2In;
        this.dashInterp2 = Interp.pow2Out;
        this.dashColorInterp = Interp.pow5In;
        this.lifetime = 32.0F;
    }

    public void draw(Bullet b) {
        float z = Draw.z();
        float realLength = b.fdata;
        float scl = realLength / this.length;
        float fin = b.fin();
        float rot = b.rotation();
        float lfin = Mathf.curve(fin, 0.0F, this.laserTo);
        float lfout = 1.0F - lfin;
        float laserLenf = Mathf.curve(lfin, 0.0F, this.laserExtTime * scl);
        float laserLen = this.laserGrowInterp.apply(laserLenf) * realLength;
        float laserShrinkf = Mathf.curve(lfin, 1.0F - this.laserShrinkTime * scl, 1.0F);
        float laserShrink = this.laserShrinkInterp.apply(laserShrinkf) * realLength;
        float cwidth = this.width;
        float compound = 1.0F;
        float sfin = Mathf.curve(fin, this.swirlFrom, this.swirlTo);
        float slife = this.swirlIn + this.swirlStay + this.swirlOut;
        float soffset = 1.0F - slife;
        float dfin = Mathf.curve(fin, this.dashFrom * scl, this.dashTo);

        for(Color color : this.colors) {
            Tmp.v1.trns(rot, laserShrink);
            Draw.color(color);
            Lines.stroke((cwidth *= this.lengthFalloff) * this.laserThickInterp.apply(lfout));
            Lines.lineAngle(b.x + Tmp.v1.x, b.y + Tmp.v1.y, rot, laserLen - laserShrink, false);
            UnityDrawf.tri(b.x + Tmp.v1.x, b.y + Tmp.v1.y, Lines.getStroke(), Lines.getStroke() / 2.0F, rot + 180.0F);
            Tmp.v1.trns(rot, laserLen);
            UnityDrawf.tri(b.x + Tmp.v1.x, b.y + Tmp.v1.y, Lines.getStroke(), cwidth * 2.0F + this.width / 2.0F, rot);
            Fill.circle(b.x, b.y, 1.0F * cwidth * lfout);

            for(int i : Mathf.signs) {
                UnityDrawf.tri(b.x, b.y, this.sideWidth * lfout * cwidth, this.sideLength * compound, rot + this.sideAngle * (float)i);
            }

            compound *= this.lengthFalloff;
        }

        Lines.stroke(this.dashThickness, Tmp.c1.set(this.dashColor).lerp(this.dashColorDark, this.dashColorInterp.apply(dfin)));

        for(int sign : Mathf.signs) {
            float x = this.dashWidth * (float)sign * 0.5F;
            float cy = Lines.getStroke() * 2.5F;
            Tmp.v1.trns(rot - 90.0F, x, this.dashInterp1.apply(dfin) * realLength + cy).add(b);
            Tmp.v2.trns(rot - 90.0F, x, this.dashInterp2.apply(dfin) * realLength + cy).add(b);
            Lines.line(Tmp.v1.x, Tmp.v1.y, Tmp.v2.x, Tmp.v2.y, false);
            UnityDrawf.tri(Tmp.v1.x, Tmp.v1.y, Lines.getStroke(), cy, rot + 180.0F);
            UnityDrawf.tri(Tmp.v2.x, Tmp.v2.y, Lines.getStroke(), cy, rot);
        }

        Lines.stroke(this.swirlThickness);
        int iterations = Math.max(Mathf.round(realLength), 2);
        float seg = realLength / (float)iterations;
        float rand = Mathf.randomSeed((long)b.id, ((float)Math.PI * 2F) * this.swirlScale) + ((float)Mathf.randomSeed((long)(b.id + 1), 0, 1) * 2.0F - 1.0F);

        for(int i = 0; i < this.swirlAmount; ++i) {
            UnityDrawf.beginLine();
            float angleOffset = rand + ((float)Math.PI * 2F) * this.swirlScale * ((float)i / (float)this.swirlAmount);

            for(int it = 0; it < iterations; ++it) {
                float in = (float)it / ((float)iterations - 1.0F);
                float off = soffset * in;
                float prog = (this.swirlInInterp.apply(Mathf.curve(sfin, off, this.swirlIn + off)) - this.swirlOutInterp.apply(Mathf.curve(sfin, this.swirlStay + off, this.swirlOut + off))) * this.swirlFadeInterp.apply(1.0F - in);
                float rad = (float)it * seg + angleOffset;
                float x = Mathf.cos(rad, this.swirlScale, this.swirlMagnitude);
                float tz = Mathf.sin(rad, this.swirlScale, 1.0F) >= 0.0F ? z : z - 0.01F;
                Tmp.v1.trns(rot - 90.0F, x, (float)it * seg).add(b);
                UnityDrawf.linePoint(Tmp.v1.x, Tmp.v1.y, Tmp.c1.set(this.swirlColor).lerp(this.swirlColorDark, 1.0F - prog).a(prog).toFloatBits(), tz);
            }

            UnityDrawf.endLine();
        }

        Draw.z(z);
        Draw.reset();
    }
}
