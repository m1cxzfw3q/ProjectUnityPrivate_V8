package unity.content.effects;

import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.entities.Effect;
import mindustry.graphics.Drawf;
import mindustry.graphics.Trail;
import unity.graphics.UnityPal;
import unity.type.decal.CapeDecorationType;

public class TrailFx {
    public static Effect trailFadeLow = (new Effect(400.0F, (e) -> {
        Object trail$temp = e.data;
        if (trail$temp instanceof Trail trail) {
            e.lifetime = (float)trail.length * 1.4F;
            if (!Vars.state.isPaused()) {
                trail.shorten();
            }

            trail.drawCap(e.color, e.rotation);
            trail.draw(e.color, e.rotation);
        }
    })).layer(89.999F);
    public static Effect coloredRailgunTrail = new Effect(30.0F, (e) -> {
        for(int i = 0; i < 2; ++i) {
            int sign = Mathf.signs[i];
            Draw.color(e.color);
            Drawf.tri(e.x, e.y, 10.0F * e.fout(), 24.0F, e.rotation + 90.0F + 90.0F * (float)sign);
        }

    });
    public static Effect coloredRailgunSmallTrail = new Effect(30.0F, (e) -> {
        for(int i = 0; i < 2; ++i) {
            int sign = Mathf.signs[i];
            Draw.color(e.color);
            Drawf.tri(e.x, e.y, 5.0F * e.fout(), 12.0F, e.rotation + 90.0F + 90.0F * (float)sign);
        }

    });
    public static Effect coloredArrowTrail = new Effect(40.0F, 80.0F, (e) -> {
        Tmp.v1.trns(e.rotation, 5.0F * e.fout());
        Draw.color(e.color);

        for(int s : Mathf.signs) {
            Tmp.v2.trns(e.rotation - 90.0F, 9.0F * (float)s * ((e.fout() + 2.0F) / 3.0F), -20.0F);
            Fill.tri(Tmp.v1.x + e.x, Tmp.v1.y + e.y, -Tmp.v1.x + e.x, -Tmp.v1.y + e.y, Tmp.v2.x + e.x, Tmp.v2.y + e.y);
        }

    });
    public static Effect spikedEnergyTrail = new Effect(16.0F, (e) -> {
        Draw.color(e.color);

        for(int s : Mathf.signs) {
            Drawf.tri(e.x, e.y, 4.0F, 30.0F * e.fslope(), e.rotation + 90.0F * (float)s);
        }

    });
    public static Effect endRailTrail = new Effect(50.0F, (e) -> {
        Draw.color(UnityPal.scarColor, UnityPal.endColor, e.fin());
        Drawf.tri(e.x, e.y, 13.0F * e.fout(), 29.0F, e.rotation);
        Drawf.tri(e.x, e.y, 13.0F * e.fout(), 29.0F, e.rotation + 180.0F);
    });
    public static Effect endTrail = new Effect(50.0F, (e) -> {
        Draw.color(Color.black, UnityPal.scarColor, Mathf.curve(e.fin(), 0.0F, 0.3F));
        Angles.randLenVectors(e.id, 2, e.finpow() * 7.0F, (x, y) -> Fill.circle(e.x + x, e.y + y, 3.0F * e.fout()));
    });
    public static Effect capeTrail = new Effect(30.0F, (e) -> {
        CapeDecorationType.CapeEffectData data = e.data();
        TextureRegion reg = data.type.region;
        Draw.alpha(data.alpha * e.fout());
        Draw.blend(Blending.additive);

        for(int sign : Mathf.signs) {
            Tmp.v1.trns(e.rotation - 90.0F, data.type.x * (float)sign, data.type.y);
            Draw.rect(reg, e.x + Tmp.v1.x, e.y + Tmp.v1.y, (float)reg.width * Draw.scl * (float)sign, (float)reg.height * Draw.scl, e.rotation + data.sway * (float)sign - 90.0F);
        }

        Draw.blend(Blending.normal);
    });
}
