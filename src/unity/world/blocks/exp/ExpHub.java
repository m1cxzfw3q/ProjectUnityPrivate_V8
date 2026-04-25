package unity.world.blocks.exp;

import arc.Core;
import arc.func.Cons;
import arc.func.Cons2;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.struct.IntSeq;
import arc.struct.Seq;
import arc.util.Eachable;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.core.Renderer;
import mindustry.entities.Effect;
import mindustry.entities.units.BuildPlan;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.Tile;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import unity.content.UnityFx;
import unity.entities.ExpOrbs;
import unity.graphics.UnityPal;

public class ExpHub extends ExpTank {
    public float ratio = 0.3F;
    public float reloadTime = 30.0F;
    public float range = 40.0F;
    public int maxLinks = 4;
    public Effect transferEffect;
    public TextureRegion laser;
    public TextureRegion laserEnd;
    private final Seq<Building> tmpe;

    public ExpHub(String name) {
        super(name);
        this.transferEffect = UnityFx.expLaser;
        this.tmpe = new Seq();
        this.rotate = true;
        this.configurable = true;
        this.solid = false;
        this.schematicPriority = -16;
        this.config(Integer.class, (entity, value) -> {
            Building other = Vars.world.build(value);
            boolean contains = entity.links.contains(value);
            if (contains) {
                int i = entity.links.indexOf(value);
                entity.links.removeIndex(i);
            } else {
                if (!this.linkValid(entity, other, true) || entity.links.size >= this.maxLinks) {
                    return;
                }

                if (!entity.links.contains(other.pos())) {
                    entity.links.add(other.pos());
                }
            }

            entity.sanitize();
        });
        this.configClear((entity) -> entity.links.clear());
        this.config(Point2[].class, (tile, value) -> {
            IntSeq old = new IntSeq(tile.links);

            for(int i = 0; i < old.size; ++i) {
                int cur = old.get(i);
                ((Cons2)this.configurations.get(Integer.class)).get(tile, cur);
            }

            for(Point2 p : value) {
                int newPos = Point2.pack(p.x + tile.tileX(), p.y + tile.tileY());
                ((Cons2)this.configurations.get(Integer.class)).get(tile, newPos);
            }

        });
    }

    public void init() {
        super.init();
        this.clipSize = Math.max(this.clipSize, this.range * 2.0F + 8.0F);
    }

    public void load() {
        super.load();
        this.laser = Core.atlas.find("unity-exp-laser");
        this.laserEnd = Core.atlas.find("unity-exp-laser-end");
    }

    public void setBars() {
        super.setBars();
        this.bars.add("links", (entity) -> new Bar(() -> Core.bundle.format("bar.iconlinks", new Object[]{entity.links.size, this.maxLinks, '\ue871'}), () -> Pal.accent, () -> (float)entity.links.size / (float)this.maxLinks));
    }

    public void setStats() {
        super.setStats();
        this.stats.add(Stat.powerRange, (float)((int)(this.range / 8.0F) + 1), StatUnit.blocks);
        this.stats.add(Stat.powerConnections, (float)this.maxLinks, StatUnit.none);
    }

    public boolean linkValid(Building tile, Building link, boolean checkHub) {
        if (tile != link && link != null && tile.team == link.team && !link.dead) {
            boolean var10000;
            if (tile.dst2(link) <= this.range * this.range && link instanceof ExpHolder) {
                ExpHolder e = (ExpHolder)link;
                if (e.hubbable() && (!checkHub || e.canHub(tile))) {
                    var10000 = true;
                    return var10000;
                }
            }

            var10000 = false;
            return var10000;
        } else {
            return false;
        }
    }

    protected TextureRegion[] icons() {
        return new TextureRegion[]{this.region};
    }

    public void drawPlace(int x, int y, int rotation, boolean valid) {
        Tile tile = Vars.world.tile(x, y);
        if (tile != null) {
            Lines.stroke(1.0F);
            Drawf.circles((float)(x * 8) + this.offset, (float)(y * 8) + this.offset, this.range, UnityPal.exp);
            if (valid) {
                this.tmpe.clear();
                int linkRange = (int)(this.range / 8.0F) + 1;

                for(int xx = x - linkRange; xx <= x + linkRange; ++xx) {
                    for(int yy = y - linkRange; yy <= y + linkRange; ++yy) {
                        if (xx != x || yy != y) {
                            Building link = Vars.world.build(xx, yy);
                            if (link != null && link.team == Vars.player.team() && !this.tmpe.contains(link) && link.dst2(Tmp.v1.set((float)(x * 8) + this.offset, (float)(y * 8) + this.offset)) <= this.range * this.range && link instanceof ExpHolder) {
                                ExpHolder e = (ExpHolder)link;
                                if (e.hubbable() && e.canHub((Building)null)) {
                                    this.tmpe.add(link);
                                    Drawf.square(link.x, link.y, (float)(link.block.size * 8) / 2.0F + 2.0F, Pal.place);
                                }
                            }
                        }
                    }
                }
            }

            Draw.reset();
        }
    }

    public void drawRequestRegion(BuildPlan req, Eachable<BuildPlan> list) {
        Draw.rect(this.region, req.drawx(), req.drawy());
        Draw.rect(this.topRegion, req.drawx(), req.drawy(), (float)(req.rotation * 90));
    }

    protected void getPotentialLinks(Tile tile, Team team, Cons<Building> others, boolean checkHub) {
        if (tile != null && tile.build != null) {
            this.tmpe.clear();
            int linkRange = (int)(this.range / 8.0F) + 1;

            for(int x = tile.x - linkRange; x <= tile.x + linkRange; ++x) {
                for(int y = tile.y - linkRange; y <= tile.y + linkRange; ++y) {
                    Building link = Vars.world.build(x, y);
                    if (link != null && link.team == team && link != tile.build && !this.tmpe.contains(link) && this.linkValid(tile.build, link, checkHub)) {
                        this.tmpe.add(link);
                        others.get(link);
                    }
                }
            }

        }
    }

    public class ExpHubBuild extends ExpTank.ExpTankBuild {
        public float reload;
        public IntSeq links;

        public ExpHubBuild() {
            super(ExpHub.this);
            this.reload = ExpHub.this.reloadTime;
            this.links = new IntSeq();
        }

        public int takeAmount(int e, Building source) {
            if (e <= 0) {
                return 0;
            } else {
                int prefa = Mathf.ceilPositive(ExpHub.this.ratio * (float)e);
                int r = this.handleExp(prefa);
                if (r > 0) {
                    ExpHub.this.transferEffect.at(this.x, this.y, 0.0F, Color.white, source);
                }

                return r;
            }
        }

        public void sanitize() {
            for(int i = 0; i < this.links.size; ++i) {
                Building b = Vars.world.build(this.links.get(i));
                if (ExpHub.this.linkValid(this, b, true) && this.links.get(i) == b.pos()) {
                    if (b instanceof ExpHolder) {
                        ExpHolder e = (ExpHolder)b;
                        if (e.hubbable() && e.canHub(this)) {
                            e.setHub(this);
                        }
                    }
                } else {
                    this.links.removeIndex(i);
                    --i;
                }
            }

        }

        public void placed() {
            if (!Vars.net.client()) {
                ExpHub.this.getPotentialLinks(this.tile, this.team, (other) -> {
                    if (!this.links.contains(other.pos())) {
                        this.configureAny(other.pos());
                    }

                }, true);
                super.placed();
            }
        }

        public void dropped() {
            this.links.clear();
        }

        public void onProximityUpdate() {
            super.onProximityUpdate();
            this.sanitize();
        }

        public void updateTile() {
            this.reload += this.edelta();
            if (this.reload >= ExpHub.this.reloadTime && ExpOrbs.orbs(this.exp) > 0) {
                int a = this.handleExp(-ExpOrbs.oneOrb(this.exp));
                if (a < 0) {
                    ExpOrbs.dropExp(this.x, this.y, (float)this.rotation * 90.0F, 4.0F, -a);
                }

                this.reload = 0.0F;
            }

        }

        public void draw() {
            Draw.rect(ExpHub.this.region, this.x, this.y);
            Draw.color(UnityPal.exp, Color.white, Mathf.absin(20.0F, 0.6F));
            Draw.alpha(this.expf() * 0.6F);
            Draw.rect(ExpHub.this.expRegion, this.x, this.y);
            Draw.color();
            Draw.rect(ExpHub.this.topRegion, this.x, this.y, this.rotdeg());
            this.drawLinks();
        }

        protected void drawLinks() {
            if (!Mathf.zero(Renderer.laserOpacity) && this.links.size != 0) {
                Draw.z(71.0F);
                Draw.alpha(Renderer.laserOpacity * (Mathf.absin(5.0F, 0.3F) + 0.1F));

                for(int i = 0; i < this.links.size; ++i) {
                    Building b = Vars.world.build(this.links.get(i));
                    if (ExpHub.this.linkValid(this, b, true) && this.links.get(i) == b.pos()) {
                        Tmp.v2.set(b);
                        Tmp.v1.set(Tmp.v2).sub(this).nor().scl((float)(ExpHub.this.size * 8) / 2.0F);
                        Tmp.v2.sub(Tmp.v1);
                        Tmp.v1.add(this);
                        Drawf.laser(this.team, ExpHub.this.laser, ExpHub.this.laserEnd, Tmp.v1.x, Tmp.v1.y, Tmp.v2.x, Tmp.v2.y, 0.3F);
                    }
                }

                Draw.reset();
            }
        }

        public void drawLight() {
            super.drawLight();
            Drawf.light(this.team, this.x, this.y, ExpHub.this.lightRadius * this.expf(), UnityPal.exp, 0.5F);
        }

        public void drawConfigure() {
            Lines.stroke(1.0F);
            Drawf.circles(this.x, this.y, (float)(this.tile.block().size * 8) / 2.0F + 1.0F + Mathf.absin(Time.time, 4.0F, 1.0F), UnityPal.exp);
            Drawf.circles(this.x, this.y, ExpHub.this.range, UnityPal.exp);
            int linkRange = (int)(ExpHub.this.range / 8.0F) + 1;

            for(int x = this.tile.x - linkRange; x <= this.tile.x + linkRange; ++x) {
                for(int y = this.tile.y - linkRange; y <= this.tile.y + linkRange; ++y) {
                    Building link = Vars.world.build(x, y);
                    if (link != null && link != this && ExpHub.this.linkValid(this, link, true)) {
                        boolean linked = this.links.indexOf(link.pos()) >= 0;
                        if (linked) {
                            Drawf.square(link.x, link.y, (float)(link.block.size * 8) / 2.0F + 1.0F, Pal.accent);
                        }
                    }
                }
            }

            Draw.reset();
        }

        public boolean onConfigureTileTapped(Building other) {
            if (ExpHub.this.linkValid(this, other, false)) {
                this.configure(other.pos());
                return false;
            } else if (this == other) {
                if (this.links.size == 0) {
                    ExpHub.this.getPotentialLinks(this.tile, this.team, (b) -> this.configure(b.pos()), true);
                } else {
                    this.configure((Object)null);
                }

                this.deselect();
                return false;
            } else {
                return true;
            }
        }

        public void write(Writes write) {
            super.write(write);
            write.s(this.links.size);

            for(int i = 0; i < this.links.size; ++i) {
                write.i(this.links.get(i));
            }

            write.f(this.reload);
        }

        public void read(Reads read, byte revision) {
            super.read(read, revision);
            this.links.clear();
            short amount = read.s();

            for(int i = 0; i < amount; ++i) {
                this.links.add(read.i());
            }

            this.reload = read.f();
        }

        public Point2[] config() {
            Point2[] out = new Point2[this.links.size];

            for(int i = 0; i < out.length; ++i) {
                out[i] = Point2.unpack(this.links.get(i)).sub(this.tile.x, this.tile.y);
            }

            return out;
        }

        public boolean acceptOrb() {
            return false;
        }

        public int handleTower(int amount, float angle) {
            return 0;
        }
    }
}
