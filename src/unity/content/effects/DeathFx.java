package unity.content.effects;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureAtlas;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Quat;
import arc.math.geom.Vec3;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.entities.Effect;
import mindustry.graphics.Drawf;
import mindustry.type.UnitType;
import unity.content.units.MonolithUnitTypes;
import unity.gen.MonolithSoul;
import unity.graphics.UnityDrawf;
import unity.graphics.UnityPal;
import unity.util.Utils;

public final class DeathFx {
    public static final Effect monolithSoulDeath = new Effect(64.0F, (e) -> {
        Draw.color(UnityPal.monolith, UnityPal.monolithDark, e.fin());
        Angles.randLenVectors((long)e.id, 27, e.finpow() * 56.0F, (x, y) -> Fill.circle(e.x + x, e.y + y, 0.5F + e.fout() * 2.5F));
        e.scaled(48.0F, (i) -> {
            Lines.stroke(i.fout() * 2.5F, UnityPal.monolithLight);
            Lines.circle(e.x, e.y, i.fin(Interp.pow10Out) * 32.0F);
            float thick = i.foutpowdown() * 4.0F;
            Fill.circle(e.x, e.y, thick / 2.0F);

            for(int t = 0; t < 4; ++t) {
                Drawf.tri(e.x, e.y, thick, thick * 14.0F, Mathf.randomSeed((long)(e.id + 1), 360.0F) + 90.0F * (float)t + i.finpow() * 60.0F * (float)Mathf.sign(e.id % 2 == 0));
            }

        });
    });
    public static final Effect monolithSoulCrack = (new Effect(20.0F, (e) -> {
        UnitType type = MonolithUnitTypes.monolithSoul;

        for(int i = 0; i < type.wreckRegions.length; ++i) {
            float off = 360.0F / (float)type.wreckRegions.length * (float)i;
            Tmp.v1.trns(e.rotation + off, e.finpow() * 24.0F).add(e.x, e.y);
            Draw.alpha(e.foutpowdown());
            Draw.rect(type.wreckRegions[i], Tmp.v1.x, Tmp.v1.y, e.rotation - 90.0F);
        }

    })).layer(115.0F);
    public static final Effect monolithSoulJoin = (new Effect(72.0F, (e) -> {
        Object soul$temp = e.data;
        if (soul$temp instanceof MonolithSoul) {
            MonolithSoul soul = (MonolithSoul)soul$temp;
            Lines.stroke(1.5F, UnityPal.monolith);
            TextureAtlas.AtlasRegion reg = Core.atlas.find("unity-monolith-chain");
            Quat rot = Utils.q1.set(Vec3.Z, e.rotation + 90.0F).mul(Utils.q2.set(Vec3.X, 75.0F));
            float t = e.foutpowdown();
            float w = (float)reg.width * Draw.scl * 0.5F * t;
            float h = (float)reg.height * Draw.scl * 0.5F * t;
            float rad = t * 25.0F;
            float a = Mathf.curve(t, 0.25F);
            Draw.alpha(a);
            UnityDrawf.panningCircle(reg, e.x, e.y, w, h, rad, 360.0F, Time.time * 6.0F * (float)Mathf.sign(soul.id % 2 == 0) + (float)soul.id * 30.0F, rot, 89.99F, 115.0F);
            Draw.color(Color.black, UnityPal.monolithDark, 0.67F);
            Draw.alpha(a);
            Draw.blend(Blending.additive);
            UnityDrawf.panningCircle(Core.atlas.find("unity-line-shade"), e.x, e.y, w + 6.0F, h + 6.0F, rad, 360.0F, 0.0F, rot, true, 89.99F, 115.0F);
            Draw.blend();
        }
    })).layer(115.0F);
}
