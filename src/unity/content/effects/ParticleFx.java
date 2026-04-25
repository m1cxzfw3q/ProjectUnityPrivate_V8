package unity.content.effects;

import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Time;
import mindustry.entities.Effect;
import unity.graphics.UnityPal;

public class ParticleFx {
    public static Effect endRegenDisable = new Effect(30.0F, (e) -> {
        Draw.color(UnityPal.scarColor);
        Fill.square(e.x, e.y, 2.5F * Interp.pow2In.apply(e.fslope()), 45.0F);
    });
    public static Effect monolithSpark = new Effect(60.0F, (e) -> Angles.randLenVectors((long)e.id, 2, e.rotation, (x, y) -> {
        Draw.color(UnityPal.monolith, UnityPal.monolithDark, e.fin());
        float w = 1.0F + e.fout() * 4.0F;
        Fill.rect(e.x + x, e.y + y, w, w, 45.0F);
    }));
    public static Effect monolithSoul = (new Effect(48.0F, (e) -> {
        Object data$temp = e.data;
        if (data$temp instanceof Vec2 data) {
            Draw.blend(Blending.additive);
            Draw.color(UnityPal.monolith, UnityPal.monolithDark, Color.black, e.finpow());
            float time = Time.time - e.rotation;
            float vx = data.x * time;
            float vy = data.y * time;
            Angles.randLenVectors((long)e.id, 1, 5.0F + e.finpowdown() * 8.0F, (x, y) -> {
                float fin = 1.0F - e.fin(Interp.pow2In);
                Draw.alpha(1.0F);
                Fill.circle(e.x + x + vx, e.y + y + vy, fin * 2.0F);
                Draw.alpha(0.67F);
                Draw.rect("circle-shadow", e.x + x + vx, e.y + y + vy, fin * 8.0F, fin * 8.0F);
            });
            Draw.blend();
        }
    })).layer(114.99F);
    public static Effect lightningPivot = new Effect(36.0F, (e) -> {
        Lines.stroke(2.0F, e.color);
        Angles.randLenVectors((long)e.id, 3, e.foutpowdown() * 32.0F, (x, y) -> Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fin() * 6.0F));
    });
}
