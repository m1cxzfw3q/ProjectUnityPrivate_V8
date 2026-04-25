package unity.content.effects;

import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.Rand;
import arc.math.geom.Position;
import arc.math.geom.Vec2;
import arc.struct.FloatSeq;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.entities.Effect;
import mindustry.gen.EffectState;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import unity.entities.bullet.anticheat.VoidFractureBulletType;
import unity.entities.bullet.kami.KamiBulletType;
import unity.entities.effects.CustomStateEffect;
import unity.entities.effects.FragmentationShaderEffect;
import unity.entities.effects.VapourizeShaderEffect;
import unity.gen.KamiBullet;
import unity.graphics.UnityBlending;
import unity.graphics.UnityPal;
import unity.mod.TimeStop;

public class SpecialFx {
    private static final Rand rand = new Rand();
    public static Effect kamiBulletSpawn = new Effect(30.0F, 300.0F, (e) -> {
        Object kb$temp = e.data;
        if (kb$temp instanceof KamiBullet kb) {
            KamiBulletType type = (KamiBulletType)kb.type;
            TextureRegion r = KamiBulletType.region;
            e.lifetime = type.delay;
            float time = Time.time / 2.0F + (e.time - type.delay) * 2.0F;
            float scl = 1.0F + e.fout() * 5.0F;
            float st = Mathf.clamp(Math.max(kb.width, kb.length) / 10.0F + 1.2F, 1.5F, 4.0F) * (1.0F + Mathf.absin(time, 10.0F, 0.33F));
            Tmp.c1.set(Color.red).shiftHue(time).a(e.fin());
            Draw.color(Tmp.c1);
            Draw.rect(r, kb.x, kb.y, (kb.width * 2.0F + st) * scl, (kb.length * 2.0F + st) * scl, kb.rotation());
            Draw.color(Color.white);
            Draw.rect(r, kb.x, kb.y, kb.width * 2.0F * e.fin(), kb.length * 2.0F * e.fin(), kb.rotation());
        }
    });
    public static Effect endDeny = new Effect(80.0F, 1200.0F, (e) -> {
        Object u$temp = e.data;
        if (u$temp instanceof Unit u) {
            Draw.blend(Blending.additive);
            float a = e.color.a / 2.0F + 0.5F;
            e.scaled(40.0F, (s) -> {
                Draw.color(UnityPal.scarColor);
                Interp in = Interp.pow3Out;
                float f1 = in.apply(Mathf.curve(s.fin(), 0.0F, 0.8F));
                float f2 = in.apply(Mathf.curve(s.fin(), 0.2F, 1.0F));
                float hs = u.hitSize / 2.0F;
                rand.setSeed((long)e.id * 99999L);

                for(int i = 0; i < (int)(7.0F * a); ++i) {
                    float len = hs * rand.random(0.75F, 1.0F);
                    float r = rand.range(360.0F);
                    float scl = rand.random(0.75F, 1.5F);
                    Vec2 v = Tmp.v1.trns(r, len + hs * f1 * scl).add(e.x, e.y);
                    Vec2 v2 = Tmp.v2.trns(r, len + hs * f2 * scl).add(e.x, e.y);
                    Lines.stroke(1.5F);
                    Lines.line(v.x, v.y, v2.x, v2.y);
                }

            });
            Draw.alpha(e.fout());
            Draw.mixcol(UnityPal.scarColor, a);
            Draw.rect(u.icon(), u.x + Mathf.range(e.fin() * 2.0F), u.y + Mathf.range(e.fin() * 2.0F), u.rotation - 90.0F);
            Draw.blend();
            Draw.reset();
        }
    });
    public static Effect fragmentation = new FragmentationShaderEffect(210.0F);
    public static Effect fragmentationFast = new FragmentationShaderEffect(90.0F) {
        {
            this.fragOffset = 0.0F;
            this.heatOffset = 0.0F;
        }
    };
    public static Effect endgameVapourize = (new VapourizeShaderEffect(180.0F, 900.0F)).updateVel(false);
    public static Effect chainLightningActive = (new Effect(20.0F, 300.0F, (e) -> {
        Object p$temp = e.data;
        if (p$temp instanceof Position p) {
            float tx = p.getX();
            float ty = p.getY();
            float dst = Mathf.dst(e.x, e.y, tx, ty);
            Tmp.v1.set(p).sub(e.x, e.y).nor();
            float normx = Tmp.v1.x;
            float normy = Tmp.v1.y;
            float range = 6.0F;
            int links = Mathf.ceil(dst / range);
            float spacing = dst / (float)links;
            Lines.stroke(2.5F * e.fout());
            Draw.color(Color.white, e.color, e.fin());
            Lines.beginLine();
            Lines.linePoint(e.x, e.y);
            rand.setSeed((long)e.id + (long)(Time.time / e.rotation));

            for(int i = 0; i < links; ++i) {
                float nx;
                float ny;
                if (i == links - 1) {
                    nx = tx;
                    ny = ty;
                } else {
                    float len = (float)(i + 1) * spacing;
                    Tmp.v1.setToRandomDirection(rand).scl(range / 2.0F);
                    nx = e.x + normx * len + Tmp.v1.x;
                    ny = e.y + normy * len + Tmp.v1.y;
                }

                Lines.linePoint(nx, ny);
            }

            Lines.endLine();
        }
    })).followParent(false);
    public static Effect chargeTransfer = new Effect(20.0F, (e) -> {
        if (e.data instanceof Position) {
            Position to = (Position)e.data();
            Tmp.v1.set(e.x, e.y).interpolate(Tmp.v2.set(to), e.fin(), Interp.pow3).add(Tmp.v2.sub(e.x, e.y).nor().rotate90(1).scl(Mathf.randomSeedRange((long)e.id, 1.0F) * e.fslope() * 10.0F));
            float x = Tmp.v1.x;
            float y = Tmp.v1.y;
            float s = e.fslope() * 4.0F;
            Draw.color(e.color);
            Fill.square(x, y, s, 45.0F);
        }
    });
    public static Effect timeStop = new CustomStateEffect(() -> {
        EffectState s = EffectState.create();
        if (TimeStop.inTimeStop()) {
            TimeStop.addEntity(s, 270.0F);
        }

        return s;
    }, 210.0F, 1000.0F, (e) -> {
        float s = Interp.pow2.apply(e.fslope()) * 500.0F;
        Draw.blend(UnityBlending.invert);
        Fill.poly(e.x, e.y, (int)(s / 5.0F) + 24, s);
        Draw.blend(UnityBlending.multiply);
        Draw.color(Color.red);
        Fill.poly(e.x, e.y, (int)(s / 5.0F) + 24, s);
        Draw.blend();
    });
    public static Effect voidFractureEffect = (new Effect(30.0F, 700.0F, (e) -> {
        if (e.data instanceof VoidFractureData data) {
            float rot = Angles.angle(data.x, data.y, data.x2, data.y2);
            Draw.color(Color.black);

            for(int i = 0; i < 3; ++i) {
                float f = Mathf.lerp(data.b.width, data.b.widthTo, (float)i / 2.0F);
                float a = Mathf.lerp(0.25F, 1.0F, (float)i / 2.0F * ((float)i / 2.0F));
                Draw.alpha(a);
                Lines.stroke(f * e.fout());
                Lines.line(data.x, data.y, data.x2, data.y2, false);
                Drawf.tri(data.x2, data.y2, f * 1.22F * e.fout(), f * 2.0F, rot);
                Drawf.tri(data.x, data.y, f * 1.22F * e.fout(), f * 2.0F, rot + 180.0F);
            }

            FloatSeq s = data.spikes;
            if (!s.isEmpty()) {
                for(int i = 0; i < data.spikes.size; i += 4) {
                    float x1 = s.get(i);
                    float y1 = s.get(i + 1);
                    float x2 = s.get(i + 2);
                    float y2 = s.get(i + 3);
                    Drawf.tri(x1, y1, (data.b.widthTo + 1.0F) * e.fout(), Mathf.dst(x1, y1, x2, y2) * 2.0F * Mathf.curve(e.fin(), 0.0F, 0.2F), Angles.angle(x1, y1, x2, y2));
                    Fill.circle(x1, y1, (data.b.widthTo + 1.0F) / 1.22F * e.fout());
                }
            }

        }
    })).layer(110.03F);
    public static Effect pointBlastLaserEffect = new Effect(23.0F, 600.0F, (e) -> {
        Object data$temp = e.data;
        if (data$temp instanceof PointBlastInterface data) {

            for(int i = 0; i < data.colors().length; ++i) {
                Draw.color(data.colors()[i]);
                Fill.circle(e.x, e.y, (e.rotation - data.widthReduction() * (float)i) * e.fout());
            }

            Drawf.light(e.x, e.y, e.rotation * e.fout() * 3.0F, data.colors()[0], 0.66F);
        }
    });

    public static class VoidFractureData {
        public float x;
        public float y;
        public float x2;
        public float y2;
        public VoidFractureBulletType b;
        public FloatSeq spikes = new FloatSeq();
    }

    public interface PointBlastInterface {
        Color[] colors();

        float widthReduction();
    }
}
