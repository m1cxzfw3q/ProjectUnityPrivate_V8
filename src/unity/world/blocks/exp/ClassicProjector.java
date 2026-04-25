package unity.world.blocks.exp;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.gen.Bullet;
import mindustry.gen.Groups;
import mindustry.graphics.Drawf;
import mindustry.world.consumers.ConsumeLiquidFilter;
import mindustry.world.consumers.ConsumeType;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import unity.content.UnityFx;
import unity.gen.ExpForceProjector;
import unity.graphics.UnityPal;

public class ClassicProjector extends ExpForceProjector {
    public float deflectChance = 0.0F;
    public Effect deflectEffect;
    public Effect absorbEffect;
    public Effect shieldBreakEffect;
    public float expChance;
    public int expGain;
    public TextureRegion altRegion;

    public ClassicProjector(String name) {
        super(name);
        this.deflectEffect = UnityFx.deflect;
        this.absorbEffect = UnityFx.absorb;
        this.shieldBreakEffect = UnityFx.shieldBreak;
        this.expChance = 1.0F;
        this.expGain = 1;
    }

    public float getRange() {
        return this.radius;
    }

    public void init() {
        super.init();
        if (this.effectColors == null) {
            this.effectColors = new Color[]{this.fromColor};
        }

    }

    public void load() {
        super.load();
        this.altRegion = Core.atlas.find(this.name + "-1");
    }

    public void setStats() {
        super.setStats();
        if (this.deflectChance > 0.0F) {
            this.stats.add(Stat.baseDeflectChance, this.deflectChance, StatUnit.none);
        }

    }

    public void drawPlace(int x, int y, int rotation, boolean valid) {
        this.drawPotentialLinks(x, y);
        if (this.rangeStart != this.rangeEnd) {
            Drawf.circles((float)(x * 8) + this.offset, (float)(y * 8) + this.offset, this.rangeEnd, UnityPal.exp);
        }

        Drawf.circles((float)(x * 8) + this.offset, (float)(y * 8) + this.offset, this.rangeStart, this.fromColor);
        if (!valid && this.pregrade != null) {
            this.drawPlaceText(Core.bundle.format("exp.pregrade", new Object[]{this.pregradeLevel, this.pregrade.localizedName}), x, y, false);
        }

    }

    public class ClassicProjectorBuild extends ExpForceProjector.ExpForceProjectorBuild {
        public ClassicProjectorBuild() {
            super(ClassicProjector.this);
        }

        public void hitBullet(Bullet b, float r) {
            if (b.type.absorbable && b.team != this.team && !(this.dst2(b) > r * r)) {
                if (b.type.reflectable && ClassicProjector.this.deflectChance > 0.0F && Mathf.chance((double)(ClassicProjector.this.deflectChance / b.damage()))) {
                    float a = b.angleTo(this);
                    float rb = b.rotation();
                    if (Angles.near(a, rb, 90.0F)) {
                        b.trns(-b.vel.x, -b.vel.y);
                        b.rotation(rb + 2.0F * (a - rb) + 180.0F);
                    }

                    b.owner = this;
                    b.team = this.team;
                    ++b.time;
                    ClassicProjector.this.deflectEffect.at(b.x, b.y, this.angleTo(b), this.effectColor());
                } else {
                    b.absorb();
                    ClassicProjector.this.absorbEffect.at(b.x, b.y, 0.0F, this.effectColor());
                }

                this.hit = 1.0F;
                this.buildup += b.damage();
                if (Mathf.chance((double)ClassicProjector.this.expChance)) {
                    this.handleExp(ClassicProjector.this.expGain);
                }

            }
        }

        public float realRadius() {
            return ((ClassicProjector.this.rangeField == null ? ClassicProjector.this.radius : (Float)ClassicProjector.this.rangeField.fromLevel(this.level())) + this.phaseHeat * ClassicProjector.this.phaseRadiusBoost) * this.radscl;
        }

        public void onRemoved() {
            float radius = this.realRadius();
            if (!this.broken && radius > 1.0F) {
                UnityFx.forceShrink.at(this.x, this.y, radius, this.effectColor());
            }

        }

        public void updateTile() {
            boolean phaseValid = ClassicProjector.this.hasItems && ClassicProjector.this.consumes.get(ConsumeType.item).valid(this);
            this.phaseHeat = Mathf.lerpDelta(this.phaseHeat, (float)Mathf.num(phaseValid), 0.1F);
            if (phaseValid && !this.broken && this.timer(ClassicProjector.this.timerUse, ClassicProjector.this.phaseUseTime) && this.efficiency() > 0.0F) {
                this.consume();
            }

            this.radscl = Mathf.lerpDelta(this.radscl, this.broken ? 0.0F : this.warmup, 0.05F);
            if (Mathf.chanceDelta((double)(this.buildup / ClassicProjector.this.shieldHealth * 0.1F))) {
                Fx.reactorsmoke.at(this.x + Mathf.range(4.0F), this.y + Mathf.range(4.0F));
            }

            this.warmup = Mathf.lerpDelta(this.warmup, this.efficiency(), 0.1F);
            if (this.buildup > 0.0F) {
                float scale = !this.broken ? ClassicProjector.this.cooldownNormal : ClassicProjector.this.cooldownBrokenBase;
                if (ClassicProjector.this.hasLiquids) {
                    ConsumeLiquidFilter cons = (ConsumeLiquidFilter)ClassicProjector.this.consumes.get(ConsumeType.liquid);
                    if (cons.valid(this)) {
                        cons.update(this);
                        scale *= ClassicProjector.this.cooldownLiquid * (1.0F + (this.liquids.current().heatCapacity - 0.4F) * 0.9F);
                    }
                }

                this.buildup -= this.delta() * scale;
            }

            if (this.broken && this.buildup <= 0.0F) {
                this.broken = false;
            }

            if (this.buildup >= ClassicProjector.this.shieldHealth + ClassicProjector.this.phaseShieldBoost * this.phaseHeat && !this.broken) {
                this.broken = true;
                this.buildup = ClassicProjector.this.shieldHealth;
                ClassicProjector.this.shieldBreakEffect.at(this.x, this.y, this.realRadius(), this.effectColor());
            }

            if (this.hit > 0.0F) {
                this.hit -= 0.2F * Time.delta;
            }

            float realRadius = this.realRadius();
            if (realRadius > 0.0F && !this.broken) {
                Groups.bullet.intersect(this.x - realRadius, this.y - realRadius, realRadius * 2.0F, realRadius * 2.0F, (b) -> this.hitBullet(b, realRadius));
            }

        }

        public void draw() {
            Draw.rect(ClassicProjector.this.region, this.x, this.y);
            if (ClassicProjector.this.altRegion.found()) {
                Draw.alpha(this.levelf());
                Draw.rect(ClassicProjector.this.altRegion, this.x, this.y);
                Draw.color();
            }

            if (this.buildup > 0.0F) {
                Draw.alpha(this.buildup / ClassicProjector.this.shieldHealth * 0.75F);
                Draw.mixcol(this.shootColor(Tmp.c1), 1.0F);
                Draw.blend(Blending.additive);
                Draw.rect(ClassicProjector.this.topRegion, this.x, this.y);
                Draw.blend();
                Draw.reset();
            }

            this.drawShield();
        }

        public void drawShield() {
            if (!this.broken) {
                float radius = this.realRadius();
                Draw.z(125.0F);
                Draw.color(this.shootColor(Tmp.c2), Color.white, Mathf.clamp(this.hit));
                if (Core.settings.getBool("animatedshields")) {
                    Fill.poly(this.x, this.y, Lines.circleVertices(radius), radius);
                } else {
                    Lines.stroke(1.5F);
                    Draw.alpha(0.09F + Mathf.clamp(0.08F * this.hit));
                    Fill.circle(this.x, this.y, radius);
                    Draw.alpha(1.0F);
                    Lines.circle(this.x, this.y, radius);
                    Draw.reset();
                }
            }

            Draw.reset();
        }
    }
}
