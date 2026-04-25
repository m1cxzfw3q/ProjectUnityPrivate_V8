package unity.world.blocks.exp;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.entities.Effect;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import unity.content.UnityFx;
import unity.graphics.UnityPal;

public class ExpNode extends ExpTank {
    public int range = 5;
    public float reloadTime = 600.0F;
    public float warmupTime = 35.0F;
    public int minExp = 10;
    public Effect shootEffect;
    public Color lightClearColor;
    private static final Seq<ExpHolder> tmps = new Seq();
    private static int tmpm = 0;
    private final Color tmpc;

    public ExpNode(String name) {
        super(name);
        this.shootEffect = UnityFx.expPoof;
        this.tmpc = new Color();
        this.lightColor = UnityPal.exp;
    }

    public void init() {
        super.init();
        this.lightRadius = (float)(this.range * 8);
        this.clipSize = Math.max(this.clipSize, ((float)this.range * 2.0F + 2.0F) * 8.0F);
        this.lightClearColor = this.lightColor.cpy().a(0.0F);
    }

    public void setStats() {
        super.setStats();
        this.stats.add(Stat.output, 60.0F / this.reloadTime, StatUnit.perSecond);
        this.stats.add(Stat.powerRange, (float)this.range, StatUnit.blocks);
    }

    public void setBars() {
        super.setBars();
        this.bars.add("links", (entity) -> new Bar(() -> Core.bundle.format("bar.reloading", new Object[]{(int)(100.0F * Mathf.clamp(entity.reload / this.reloadTime))}), () -> Pal.accent, () -> Mathf.clamp(entity.reload / this.reloadTime)));
    }

    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);
        Drawf.dashCircle((float)(x * 8) + this.offset, (float)(y * 8) + this.offset, (float)(this.range * 8), UnityPal.exp);
    }

    protected TextureRegion[] icons() {
        return new TextureRegion[]{this.region};
    }

    public class ExpNodeBuild extends ExpTank.ExpTankBuild {
        public float reload = 0.0F;
        public float warmup = 0.0F;
        public boolean shooting = false;

        public ExpNodeBuild() {
            super(ExpNode.this);
        }

        public void updateTile() {
            if (this.shooting) {
                this.warmup += Time.delta;
                if (this.warmup >= ExpNode.this.warmupTime) {
                    this.shoot();
                    this.shooting = false;
                    this.reload = 0.0F;
                }
            } else {
                this.reload += this.edelta();
            }

            if (this.reload >= ExpNode.this.reloadTime && !this.shooting && this.exp >= ExpNode.this.minExp) {
                this.reload = this.warmup = 0.0F;
                this.shooting = true;
                ExpNode.this.shootEffect.at(this);
            }

        }

        public void shoot() {
            ExpNode.tmps.clear();
            ExpNode.tmpm = 0;
            Geometry.circle(this.tile.x, this.tile.y, ExpNode.this.range, (x, y) -> {
                Building other = Vars.world.build(x, y);
                if (other != null && other != this && other.team == this.team && other instanceof ExpHolder) {
                    ExpHolder exp = (ExpHolder)other;
                    if (!exp.hubbable() && other instanceof LevelHolder) {
                        ExpNode.tmps.add(exp);
                        if (exp.getExp() + 1 > ExpNode.tmpm) {
                            ExpNode.tmpm = exp.getExp() + 1;
                        }
                    }
                }

            });
            if (!ExpNode.tmps.isEmpty()) {
                float scoresum = 0.0F;

                for(ExpHolder e : ExpNode.tmps) {
                    float score = 1.0F - (float)(e.getExp() + 1) / (float)ExpNode.tmpm;
                    if (score == 0.0F) {
                        score = 0.1F;
                    }

                    scoresum += score;
                }

                int expm = this.exp;

                for(ExpHolder e : ExpNode.tmps) {
                    float score = 1.0F - (float)(e.getExp() + 1) / (float)(ExpNode.tmpm + 1);
                    if (score == 0.0F) {
                        score = 0.1F;
                    }

                    int amount = Mathf.ceilPositive(score / scoresum * (float)expm);
                    if (this.exp >= amount) {
                        int a = e.handleExp(amount);
                        this.exp -= a;
                    }
                }

            }
        }

        public void draw() {
            Draw.rect(ExpNode.this.region, this.x, this.y);
            Draw.color(UnityPal.exp, Color.white, Mathf.absin(20.0F, 0.6F));
            Draw.alpha(this.expf());
            Draw.rect(ExpNode.this.expRegion, this.x, this.y);
            Draw.color();
            if (this.shooting) {
                Draw.blend(Blending.additive);
                Draw.color(ExpNode.this.lightColor, 1.0F - this.fin());
                Draw.rect(ExpNode.this.topRegion, this.x, this.y);
                Draw.blend();
                Draw.color();
                Draw.z(71.0F);
                float r = (float)(ExpNode.this.range * 8) * this.fin();
                Fill.light(this.x, this.y, Lines.circleVertices(r), r, ExpNode.this.lightClearColor, ExpNode.this.tmpc.set(ExpNode.this.lightColor).a(Mathf.clamp(2.0F * (1.0F - this.fin()))));
                Draw.z(110.0011F);
                Lines.stroke((0.5F + Mathf.absin(Time.globalTime, 3.0F, 1.5F)) * (1.0F - this.fin()), ExpNode.this.lightColor);
                Lines.circle(this.x, this.y, r);
            }

        }

        public void drawLight() {
            super.drawLight();
            if (this.shooting) {
                float r = (float)(ExpNode.this.range * 8) * this.fin();
                Drawf.light(this.x, this.y, r, ExpNode.this.lightColor, Mathf.clamp(2.0F * (1.0F - this.fin())));
            }

        }

        public void drawSelect() {
            Drawf.dashCircle(this.x, this.y, (float)(ExpNode.this.range * 8), UnityPal.exp);
        }

        public float fin() {
            return Mathf.clamp(this.warmup / ExpNode.this.warmupTime);
        }

        public void write(Writes write) {
            super.write(write);
            write.f(this.reload);
            write.f(this.warmup);
            write.bool(this.shooting);
        }

        public void read(Reads read, byte revision) {
            super.read(read, revision);
            this.reload = read.f();
            this.warmup = read.f();
            this.shooting = read.bool();
        }
    }
}
