package unity.v8;

import arc.graphics.Color;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import mindustry.entities.Effect;

import static arc.graphics.g2d.Draw.color;
import static arc.graphics.g2d.Lines.stroke;
import static arc.math.Angles.randLenVectors;

public class V7Fx {
    public static Effect nuclearShockwave = new Effect(10f, 200f, e -> {
        color(Color.white, Color.lightGray, e.fin());
        stroke(e.fout() * 3f + 0.2f);
        Lines.circle(e.x, e.y, e.fin() * 140f);
    }),nuclearcloud = new Effect(90, 200f, e -> {
        randLenVectors(e.id, 10, e.finpow() * 90f, (x, y) -> {
            float size = e.fout() * 14f;
            color(Color.lime, Color.gray, e.fin());
            Fill.circle(e.x + x, e.y + y, size/2f);
        });
    }),nuclearsmoke = new Effect(40, e -> {
        randLenVectors(e.id, 4, e.fin() * 13f, (x, y) -> {
            float size = e.fslope() * 4f;
            color(Color.lightGray, Color.gray, e.fin());
            Fill.circle(e.x + x, e.y + y, size/2f);
        });
    });
}
