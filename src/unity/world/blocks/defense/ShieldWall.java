package unity.world.blocks.defense;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.entities.Effect;
import mindustry.gen.Bullet;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.meta.Stat;

public class ShieldWall extends LevelLimitWall {
    private final int timerHeal;
    protected float shieldHealth;
    protected float repair;
    public TextureRegion topRegion;
    public Effect shieldGen;
    public Effect shieldBreak;
    public Effect shieldShrink;

    public ShieldWall(String name) {
        super(name);
        this.timerHeal = this.timers++;
        this.repair = 50.0F;
        this.shieldGen = (new Effect(20.0F, (e) -> {
            Draw.color(e.color, e.fin());
            if (Core.settings.getBool("animatedshields")) {
                Fill.rect(e.x, e.y, e.fin() * (float)this.size * 8.0F, e.fin() * (float)this.size * 8.0F);
            } else {
                Lines.stroke(1.5F);
                Draw.alpha(0.09F);
                Fill.rect(e.x - e.fin() * (float)this.size * 4.0F, e.y - e.fin() * (float)this.size * 4.0F, e.fin() * (float)this.size * 8.0F, e.fin() * (float)this.size * 8.0F);
                Draw.alpha(1.0F);
                Lines.rect(e.x, e.y, e.fin() * (float)this.size * 8.0F, e.fin() * (float)this.size * 8.0F);
            }

        })).layer(125.0F);
        this.shieldBreak = (new Effect(40.0F, (e) -> {
            Draw.color(e.color);
            Lines.stroke(3.0F * e.fout());
            Lines.rect(e.x, e.y, e.fin() * (float)this.size * 8.0F, e.fin() * (float)this.size * 8.0F);
        })).followParent(true);
        this.shieldShrink = (new Effect(20.0F, (e) -> {
            Draw.color(e.color, e.fout());
            if (Core.settings.getBool("animatedshields")) {
                Fill.rect(e.x, e.y, e.fout() * (float)this.size * 8.0F, e.fout() * (float)this.size * 8.0F);
            } else {
                Lines.stroke(1.5F);
                Draw.alpha(0.2F);
                Fill.rect(e.x, e.y, e.fout() * (float)this.size * 8.0F, e.fout() * (float)this.size * 8.0F);
                Draw.alpha(1.0F);
                Lines.rect(e.x, e.y, e.fout() * (float)this.size * 8.0F, e.fout() * (float)this.size * 8.0F);
            }

        })).layer(125.0F);
        this.update = true;
        this.flashHit = false;
    }

    public void load() {
        super.load();
        this.topRegion = Core.atlas.find(this.name + "-top");
    }

    public void setStats() {
        super.setStats();
        this.stats.add(Stat.shieldHealth, this.shieldHealth);
    }

    public void setBars() {
        super.setBars();
        this.bars.add("shield", (e) -> new Bar("stat.shieldhealth", Pal.accent, () -> e.shieldBroke ? 0.0F : 1.0F - e.gotDamage / this.shieldHealth));
    }

    public class ShieldWallBuild extends LevelLimitWall.LevelLimitWallBuild {
        public boolean shieldBroke = true;
        public float gotDamage;
        public float warmup;
        public float scl = 0.0F;

        public ShieldWallBuild() {
            super(ShieldWall.this);
        }

        public void created() {
            super.created();
            ShieldWall.this.shieldGen.at(this.x, this.y);
        }

        public void updateTile() {
            super.updateTile();
            this.warmup = Mathf.lerpDelta(this.warmup, 1.0F, 0.05F);
            this.scl = Mathf.lerpDelta(this.scl, this.shieldBroke ? 0.0F : 1.0F, 0.05F);
            if (this.timer(ShieldWall.this.timerHeal, 60.0F) && this.shieldBroke && this.gotDamage > 0.0F) {
                this.gotDamage -= ShieldWall.this.repair * this.delta();
            }

            if (this.gotDamage >= ShieldWall.this.shieldHealth && !this.shieldBroke) {
                this.shieldBroke = true;
                this.gotDamage = ShieldWall.this.shieldHealth;
                ShieldWall.this.shieldBreak.at(this.x, this.y);
            }

            if (this.shieldBroke && this.gotDamage <= 0.0F) {
                this.shieldBroke = false;
                this.gotDamage = 0.0F;
            }

            if (this.gotDamage < 0.0F) {
                this.gotDamage = 0.0F;
            }

            if (this.hit > 0.0F) {
                this.hit -= 0.2F * Time.delta;
            }

        }

        public void draw() {
            super.draw();
            if (this.gotDamage > 0.0F) {
                Draw.alpha(this.gotDamage / ShieldWall.this.shieldHealth * 0.75F);
                Draw.blend(Blending.additive);
                Draw.rect(ShieldWall.this.topRegion, this.x, this.y);
                Draw.blend();
                Draw.reset();
            }

            this.drawShield();
        }

        public boolean collide(Bullet b) {
            if (b.team != this.team && b.type.speed > 0.001F && b.type.absorbable) {
                b.hit = true;
                b.type.despawnEffect.at(this.x, this.y, b.rotation(), b.type.hitColor);
                if (this.shieldBroke) {
                    this.damage(b.damage);
                } else {
                    this.handleExp((int)(b.damage * ShieldWall.this.damageExp));
                    ShieldWall.this.setEFields(this.level());
                    this.gotDamage += b.damage;
                }

                this.hit = 1.0F;
                b.remove();
                return false;
            } else {
                return super.collide(b);
            }
        }

        public void onRemoved() {
            super.onRemoved();
            if (!this.shieldBroke) {
                ShieldWall.this.shieldShrink.at(this.x, this.y);
            }

        }

        public void drawShield() {
            if (!this.shieldBroke) {
                Draw.z(125.0F);
                Draw.color(this.team.color, Color.white, Mathf.clamp(this.hit));
                float radius = (float)(this.block.size * 8) * this.warmup * this.scl;
                if (Core.settings.getBool("animatedshields")) {
                    Fill.rect(this.x, this.y, radius, radius);
                } else {
                    Lines.stroke(1.5F);
                    Draw.alpha(0.09F + Mathf.clamp(0.08F * this.hit));
                    Fill.rect(this.x, this.y, radius, radius);
                    Draw.alpha(1.0F);
                    Lines.rect(this.x - radius / 2.0F, this.y - radius - 2.0F, radius, radius);
                    Draw.reset();
                }
            }

            Draw.reset();
        }

        public void write(Writes write) {
            super.write(write);
            write.bool(this.shieldBroke);
            write.f(this.gotDamage);
            write.f(this.warmup);
        }

        public void read(Reads read, byte revision) {
            super.read(read, revision);
            this.shieldBroke = read.bool();
            this.gotDamage = read.f();
            this.warmup = read.f();
        }
    }
}
