package unity.content.effects;

import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Position;
import arc.util.Tmp;
import mindustry.entities.Effect;
import mindustry.graphics.Drawf;
import unity.graphics.UnityPal;

public class LineFx {
    public static final Effect endPointDefence = new Effect(17.0F, 600.0F, (e) -> {
        Object data$temp = e.data;
        if (data$temp instanceof Position data) {

            for(int i = 0; i < 2; ++i) {
                float width = (float)(2 - i) * 2.2F * e.fout();
                Draw.color(i == 0 ? UnityPal.scarColor : Color.white);
                Lines.stroke(width);
                Lines.line(e.x, e.y, data.getX(), data.getY(), false);
                Fill.circle(e.x, e.y, width);
                Fill.circle(data.getX(), data.getY(), width);
            }

        }
    });
    public static final Effect monolithSoulAbsorb = (new Effect(32.0F, (e) -> {
        Object data$temp = e.data;
        if (data$temp instanceof Position data) {
            Tmp.v1.trns(Angles.angle(e.x, e.y, data.getX(), data.getY()) - 90.0F, Mathf.randomSeedRange((long)e.id, 3.0F)).scl(Interp.pow3Out.apply(e.fslope()));
            Tmp.v2.trns(Mathf.randomSeed((long)(e.id + 1), 360.0F), e.fin(Interp.pow4Out));
            Tmp.v3.set(data).sub(e.x, e.y).scl(e.fin(Interp.pow4In)).add(Tmp.v2).add(Tmp.v1).add(e.x, e.y);
            float fin = 0.3F + e.fin() * 1.4F;
            Draw.blend(Blending.additive);
            Draw.color(Color.black, UnityPal.monolithDark, e.fin());
            Draw.alpha(1.0F);
            Fill.circle(Tmp.v3.x, Tmp.v3.y, fin);
            Draw.alpha(0.67F);
            Draw.rect("circle-shadow", Tmp.v3.x, Tmp.v3.y, fin + 6.0F, fin + 6.0F);
            Draw.blend();
        }
    })).layer(90.0F);
    public static final Effect monolithSoulTransfer = new Effect(64.0F, (e) -> {
        Object data$temp = e.data;
        if (data$temp instanceof Position data) {
            Tmp.v1.set(data).sub(e.x, e.y).scl(e.fin(Interp.pow2In)).add(e.x, e.y);
            Draw.color(UnityPal.monolithDark, UnityPal.monolith, e.fslope());
            Angles.randLenVectors((long)e.id, 5, Interp.pow3Out.apply(e.fslope()) * 8.0F, 360.0F, 0.0F, 8.0F, (x, y) -> Fill.circle(Tmp.v1.x + x, Tmp.v1.y + y, 0.5F + e.fslope() * 2.7F));
            float size = e.fin(Interp.pow10Out) * e.foutpowdown();
            Draw.color(UnityPal.monolith);
            Fill.circle(Tmp.v1.x, Tmp.v1.y, size * 4.8F);
            Draw.color(UnityPal.monolithLight);

            for(int i = 0; i < 4; ++i) {
                Drawf.tri(Tmp.v1.x, Tmp.v1.y, size * 6.4F, size * 27.0F, e.rotation + 90.0F * (float)i + e.finpow() * 45.0F * (float)Mathf.sign(e.id % 2 == 0));
            }

        }
    });
}
