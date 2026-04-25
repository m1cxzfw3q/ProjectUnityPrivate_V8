package unity.world.blocks.defense;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Strings;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.world.meta.Stat;
import unity.gen.ExpLimitWall;
import unity.graphics.UnityPal;
import unity.world.blocks.exp.EField;

public class LevelLimitWall extends ExpLimitWall {
    public TextureRegion[] levelRegions;
    public TextureRegion edgeRegion;
    public TextureRegion shieldRegion;
    public TextureRegion edgeMaxRegion;
    public float damageExp = 0.05F;
    public float shieldZ = 122.0F;
    public Effect updateEffect;
    public float updateChance;

    public LevelLimitWall(String name) {
        super(name);
        this.updateEffect = Fx.none;
        this.updateChance = 0.01F;
        this.maxLevel = 6;
        this.passive = true;
        this.updateExpFields = false;
        this.upgradeEffect = Fx.none;
    }

    public void init() {
        this.damageReduction = new EField.EExpoZero((f) -> {
        }, 0.1F, Mathf.pow(8.0F, 1.0F / (float)this.maxLevel), true, (Stat)null, (v) -> Strings.autoFixed((float)Mathf.roundPositive(v * 10000.0F) / 100.0F, 2) + "%");
        super.init();
    }

    public void load() {
        super.load();
        this.edgeRegion = Core.atlas.find(this.name + "-under");
        this.edgeMaxRegion = Core.atlas.find(this.name + "-under-max", this.name + "-under");
        this.shieldRegion = Core.atlas.find(this.name + "-shield");

        int n;
        for(n = 1; n <= 100; ++n) {
            TextureRegion t = Core.atlas.find(this.name + n);
            if (!t.found()) {
                break;
            }
        }

        if (n > 1) {
            this.levelRegions = new TextureRegion[n];
            this.levelRegions[0] = this.region;

            for(int i = 1; i < n; ++i) {
                this.levelRegions[i] = Core.atlas.find(this.name + i);
            }
        }

    }

    public class LevelLimitWallBuild extends ExpLimitWall.ExpLimitWallBuild {
        public LevelLimitWallBuild() {
            super(LevelLimitWall.this);
        }

        public TextureRegion levelRegion() {
            return LevelLimitWall.this.levelRegions == null ? LevelLimitWall.this.region : LevelLimitWall.this.levelRegions[Math.min((int)(this.levelf() * (float)LevelLimitWall.this.levelRegions.length), LevelLimitWall.this.levelRegions.length - 1)];
        }

        public void draw() {
            TextureRegion top = this.levelRegion();
            Draw.z(30.0F);
            Draw.rect(top, this.x, this.y);
            if (top != LevelLimitWall.this.region) {
                Draw.z(29.49F);
                if (LevelLimitWall.this.edgeRegion.found()) {
                    Draw.rect(top == LevelLimitWall.this.levelRegions[LevelLimitWall.this.levelRegions.length - 1] ? LevelLimitWall.this.edgeMaxRegion : LevelLimitWall.this.edgeRegion, this.x, this.y);
                }

                if (!Vars.state.isPaused() && LevelLimitWall.this.updateEffect != Fx.none && top == LevelLimitWall.this.levelRegions[LevelLimitWall.this.levelRegions.length - 1] && Mathf.chanceDelta((double)LevelLimitWall.this.updateChance)) {
                    LevelLimitWall.this.updateEffect.at(this.x + Mathf.range((float)LevelLimitWall.this.size * 4.0F), this.y + Mathf.range((float)LevelLimitWall.this.size * 4.0F), UnityPal.exp);
                }
            }

            if (LevelLimitWall.this.flashHit && this.hit > 1.0E-4F) {
                Draw.z(30.0F);
                Draw.color(LevelLimitWall.this.flashColor);
                Draw.alpha(this.hit * 0.5F);
                Draw.blend(Blending.additive);
                Fill.rect(this.x, this.y, (float)(8 * LevelLimitWall.this.size), (float)(8 * LevelLimitWall.this.size));
                if (top != LevelLimitWall.this.region) {
                    Draw.z(29.49F);
                    Draw.mixcol(Color.white, 1.0F);
                    Draw.rect(LevelLimitWall.this.edgeRegion, this.x, this.y);
                    Draw.mixcol();
                }

                Draw.blend();
                Draw.reset();
                if (!Vars.state.isPaused()) {
                    this.hit = Mathf.clamp(this.hit - Time.delta / 10.0F);
                }
            }

        }

        public float handleDamage(float amount) {
            float a = amount * LevelLimitWall.this.damageExp;
            if (a >= 1.0F) {
                this.handleExp((int)a);
            } else if (a > 0.0F && Mathf.chance((double)a)) {
                this.handleExp(1);
            }

            LevelLimitWall.this.setEFields(this.level());
            return super.handleDamage(amount);
        }

        public void levelup() {
            LevelLimitWall.this.upgradeSound.at(this);
            LevelLimitWall.this.upgradeEffect.at(this);
            if (LevelLimitWall.this.upgradeBlockEffect != Fx.none) {
                LevelLimitWall.this.upgradeBlockEffect.at(this.x, this.y, 0.0F, Color.white, this.levelRegion());
            }

        }
    }
}
