package unity.content;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.math.Interp;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.graphics.Trail;
import unity.graphics.MultiTrail;
import unity.graphics.TexturedTrail;
import unity.graphics.UnityPal;
import unity.util.Utils;

public final class Trails {
    public static TexturedTrail singlePhantasmal(int length) {
        return new TexturedTrail(Core.atlas.find("unity-phantasmal-trail"), length) {
            {
                this.blend = Blending.additive;
                this.fadeInterp = Interp.pow2In;
                this.sideFadeInterp = Interp.pow3In;
                this.mixInterp = Interp.pow10In;
                this.gradientInterp = Interp.pow10Out;
                this.fadeColor = new Color(0.3F, 0.5F, 1.0F);
                this.shrink = 0.0F;
                this.fadeAlpha = 1.0F;
                this.mixAlpha = 1.0F;
                this.trailChance = 0.4F;
                this.trailWidth = 1.6F;
                this.trailColor = UnityPal.monolithLight;
            }
        };
    }

    public static TexturedTrail phantasmalExhaust(int length) {
        return (TexturedTrail)Utils.with(singlePhantasmal(length), (t) -> {
            t.fadeInterp = Interp.pow3In;
            t.sideFadeInterp = Interp.pow5In;
            t.mixAlpha = 0.0F;
            t.trailChance = 0.0F;
            t.shrink = -3.6F;
        });
    }

    public static MultiTrail phantasmal(int length) {
        return phantasmal(length, 3.6F, 3.5F, 0.0F);
    }

    public static MultiTrail phantasmal(MultiTrail.RotationHandler rot, int length) {
        return phantasmal(rot, length, 3.6F, 3.5F, 0.0F);
    }

    public static MultiTrail phantasmal(int length, float scale, float magnitude, float offsetY) {
        return phantasmal(MultiTrail::calcRot, length, scale, magnitude, offsetY);
    }

    public static MultiTrail phantasmal(MultiTrail.RotationHandler rot, int length, final float scale, final float magnitude, final float offsetY) {
        final int strandsAmount = 2;
        MultiTrail.TrailHold[] trails = new MultiTrail.TrailHold[strandsAmount + 2];

        for(int i = 0; i < strandsAmount; ++i) {
            trails[i] = new MultiTrail.TrailHold((Trail)Utils.with(singlePhantasmal(Mathf.round((float)length * 1.5F)), (t) -> t.trailWidth = 4.8F), 0.0F, 0.0F, 0.16F);
        }

        trails[strandsAmount] = new MultiTrail.TrailHold(singlePhantasmal(length));
        trails[strandsAmount + 1] = new MultiTrail.TrailHold(phantasmalExhaust(Mathf.round((float)length * 0.5F)), 0.0F, 1.6F, 1.0F);
        final float offset = Mathf.random(((float)Math.PI * 2F) * scale);
        return new MultiTrail(rot, trails) {
            public void update(float x, float y, float width) {
                float angle = this.rotation.get(this, x, y) - 90.0F;

                for(int i = 0; i < strandsAmount; ++i) {
                    Tmp.v1.trns(angle, Mathf.sin(Time.time + offset + ((float)Math.PI * 2F) * scale * ((float)i / (float)strandsAmount), scale, magnitude * width), offsetY);
                    MultiTrail.TrailHold trail = this.trails[i];
                    trail.trail.update(x + Tmp.v1.x, y + Tmp.v1.y, width * trail.width);
                }

                for(int i = strandsAmount; i < this.trails.length; ++i) {
                    MultiTrail.TrailHold trail = this.trails[i];
                    Tmp.v1.trns(angle, trail.x, trail.y);
                    trail.trail.update(x + Tmp.v1.x, y + Tmp.v1.y, width * trail.width);
                }

                this.lastX = x;
                this.lastY = y;
            }
        };
    }

    public static TexturedTrail singleSoul(int length) {
        return new TexturedTrail(Core.atlas.find("unity-soul-trail"), length) {
            {
                this.blend = Blending.additive;
                this.fadeInterp = Interp.pow5In;
                this.sideFadeInterp = Interp.pow10In;
                this.mixInterp = Interp.pow5In;
                this.gradientInterp = Interp.pow5Out;
                this.fadeColor = new Color(0.1F, 0.2F, 1.0F);
                this.shrink = 1.0F;
                this.mixAlpha = 0.8F;
                this.fadeAlpha = 0.5F;
                this.trailChance = 0.0F;
            }
        };
    }

    public static MultiTrail soul(int length) {
        return soul(length, 6.0F, 2.2F);
    }

    public static MultiTrail soul(MultiTrail.RotationHandler rot, int length) {
        return soul(rot, length, 6.0F, 2.2F);
    }

    public static MultiTrail soul(int length, float scale, float magnitude) {
        return soul(MultiTrail::calcRot, length, scale, magnitude);
    }

    public static MultiTrail soul(MultiTrail.RotationHandler rot, int length, final float scale, final float magnitude) {
        final int strandsAmount = 3;
        MultiTrail.TrailHold[] trails = new MultiTrail.TrailHold[strandsAmount + 1];

        for(int i = 0; i < strandsAmount; ++i) {
            trails[i] = new MultiTrail.TrailHold((Trail)Utils.with(singleSoul(Mathf.round((float)length * 1.5F)), (t) -> t.mixAlpha = 0.0F), 0.0F, 0.0F, 0.56F);
        }

        trails[strandsAmount] = new MultiTrail.TrailHold(singlePhantasmal(length), UnityPal.monolith);
        final float dir = (float)Mathf.sign(Mathf.chance((double)0.5F));
        final float offset = Mathf.random(((float)Math.PI * 2F) * scale);
        return new MultiTrail(rot, trails) {
            public void update(float x, float y, float width) {
                float angle = this.rotation.get(this, x, y) - 90.0F;

                for(int i = 0; i < strandsAmount; ++i) {
                    float rad = (Time.time + offset + ((float)Math.PI * 2F) * scale * ((float)i / (float)strandsAmount)) * dir;
                    float scl = Mathf.map(Mathf.sin(rad, scale, 1.0F), -1.0F, 1.0F, 0.2F, 1.0F);
                    Tmp.v1.trns(angle, Mathf.cos(rad, scale, magnitude * width));
                    MultiTrail.TrailHold trail = this.trails[i];
                    trail.trail.update(x + Tmp.v1.x, y + Tmp.v1.y, width * trail.width * scl);
                }

                MultiTrail.TrailHold main = this.trails[this.trails.length - 1];
                Tmp.v1.trns(angle, main.x, main.y);
                main.trail.update(x + Tmp.v1.x, y + Tmp.v1.y, width * main.width);
                this.lastX = x;
                this.lastY = y;
            }
        };
    }

    private Trails() {
        throw new AssertionError();
    }
}
