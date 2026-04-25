package unity.world.blocks.exp;

import arc.Core;
import arc.audio.Sound;
import arc.func.Floatc;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.scene.Element;
import arc.scene.ui.Image;
import arc.scene.ui.Label;
import arc.scene.ui.layout.Collapser;
import arc.scene.ui.layout.Table;
import arc.struct.OrderedMap;
import arc.struct.Seq;
import arc.util.Nullable;
import arc.util.Strings;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import mindustry.gen.Sounds;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.defense.turrets.Turret;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatCat;
import mindustry.world.meta.StatValue;
import unity.content.UnityFx;
import unity.entities.ExpOrbs;
import unity.graphics.UnityPal;
import unity.ui.Graph;
import unity.world.draw.DrawLevel;

public class ExpTurret extends Turret {
    public int maxLevel = 10;
    public int maxExp;
    public EField<?>[] expFields;
    public boolean passive = false;
    public boolean updateExpFields = true;
    @Nullable
    public ExpTurret pregrade = null;
    public int pregradeLevel = -1;
    public float orbScale = 0.8F;
    public int expScale = 1;
    public Effect upgradeEffect;
    public Effect upgradeBlockEffect;
    public Sound upgradeSound;
    public Color fromColor;
    public Color toColor;
    public Color[] effectColors;
    @Nullable
    protected EField<Float> rangeField;
    protected float rangeStart;
    protected float rangeEnd;
    private final Seq<Building> seqs;
    public EField<Float> damageReduction;
    @Nullable
    public DrawLevel draw;

    public ExpTurret(String name) {
        super(name);
        this.upgradeEffect = UnityFx.expPoof;
        this.upgradeBlockEffect = UnityFx.expShineRegion;
        this.upgradeSound = Sounds.message;
        this.fromColor = Pal.lancerLaser;
        this.toColor = UnityPal.exp;
        this.rangeField = null;
        this.seqs = new Seq();
        this.draw = null;
    }

    public void init() {
        super.init();
        if (this.expFields == null) {
            this.expFields = new EField[0];
        }

        this.maxExp = this.requiredExp(this.maxLevel);
        if (this.expLevel(this.maxExp) < this.maxLevel) {
            ++this.maxExp;
        }

        for(EField<?> f : this.expFields) {
            if (f.stat == Stat.shootRange || f.stat == Stat.range) {
                this.rangeField = f;
                break;
            }
        }

        if (this.rangeField == null) {
            this.rangeStart = this.rangeEnd = this.getRange();
        } else {
            this.rangeEnd = (Float)this.rangeField.fromLevel(this.maxLevel);
            this.rangeStart = (Float)this.rangeField.fromLevel(0);
        }

        this.setEFields(0);
        if (this.pregrade != null && this.pregradeLevel < 0) {
            this.pregradeLevel = this.pregrade.maxLevel;
        }

        if (this.damageReduction == null) {
            this.damageReduction = new EField.EExpoZero((fx) -> {
            }, 0.1F, Mathf.pow(4.0F + (float)this.size, 1.0F / (float)this.maxLevel), true, (Stat)null, (v) -> Strings.autoFixed((float)Mathf.roundPositive(v * 10000.0F) / 100.0F, 2) + "%");
        }

    }

    public void load() {
        super.load();
        if (this.draw != null) {
            this.draw.load(this);
        }

    }

    public float getRange() {
        return this.range;
    }

    public void checkStats() {
        if (!this.stats.intialized) {
            this.setStats();
            this.addExpStats();
            this.stats.intialized = true;
        }

    }

    public void addExpStats() {
        OrderedMap<StatCat, OrderedMap<Stat, Seq<StatValue>>> map = this.stats.toMap();
        boolean removeAbil = false;

        for(EField<?> f : this.expFields) {
            if (f.stat != null) {
                if (map.containsKey(f.stat.category) && ((OrderedMap)map.get(f.stat.category)).containsKey(f.stat)) {
                    if (f.stat == Stat.abilities) {
                        if (!removeAbil) {
                            this.stats.remove(f.stat);
                            removeAbil = true;
                        }
                    } else {
                        this.stats.remove(f.stat);
                    }
                }

                if (f.hasTable) {
                    this.stats.add(f.stat, (t) -> this.buildGraphTable(t, f));
                } else {
                    this.stats.add(f.stat, f.toString(), new Object[0]);
                }
            }
        }

        if (this.pregrade != null) {
            this.stats.add(Stat.buildCost, "[#84ff00]\ue804" + Core.bundle.format("exp.upgradefrom", new Object[]{this.pregradeLevel, this.pregrade.localizedName}) + "[]", new Object[0]);
            this.stats.add(Stat.buildCost, (t) -> t.button(Icon.infoCircleSmall, Styles.clearTransi, 20.0F, () -> Vars.ui.content.show(this.pregrade)).size(26.0F).color(UnityPal.exp));
        }

        this.stats.add(Stat.itemCapacity, "@", new Object[]{Core.bundle.format("exp.expAmount", new Object[]{this.maxExp})});
        this.stats.add(Stat.itemCapacity, (t) -> t.add(Core.bundle.format(this.passive ? "exp.lvlAmountP" : "exp.lvlAmount", new Object[]{this.maxLevel})).tooltip(Core.bundle.get("exp.tooltip")));
        this.stats.add(Stat.armor, (t) -> this.buildGraphTable(t, this.damageReduction));
    }

    protected void buildGraphTable(Table t, EField<?> f) {
        Label l = (Label)t.add(f.toString()).get();
        Collapser c = new Collapser((tc) -> f.buildTable(tc, this.maxLevel), true);
        Runnable toggle = () -> c.toggle(false);
        l.clicked(toggle);
        t.button(Icon.downOpenSmall, Styles.clearToggleTransi, 20.0F, toggle).size(26.0F).color(UnityPal.exp).padLeft(8.0F);
        t.row();
        t.add(c).colspan(2).left();
    }

    public void setBars() {
        super.setBars();
        this.bars.remove("health");
    }

    public void drawPlace(int x, int y, int rotation, boolean valid) {
        this.drawPotentialLinks(x, y);
        if (this.rangeStart != this.rangeEnd) {
            Drawf.dashCircle((float)(x * 8) + this.offset, (float)(y * 8) + this.offset, this.rangeEnd, UnityPal.exp);
        }

        Drawf.dashCircle((float)(x * 8) + this.offset, (float)(y * 8) + this.offset, this.rangeStart, Pal.placing);
        if (!valid && this.pregrade != null) {
            this.drawPlaceText(Core.bundle.format("exp.pregrade", new Object[]{this.pregradeLevel, this.pregrade.localizedName}), x, y, false);
        }

    }

    public boolean canReplace(Block other) {
        return super.canReplace(other) || this.pregrade != null && other == this.pregrade;
    }

    public boolean canPlaceOn(Tile tile, Team team, int rotation) {
        if (tile == null) {
            return false;
        } else if (this.pregrade == null) {
            return super.canPlaceOn(tile, team, rotation);
        } else {
            CoreBlock.CoreBuild core = team.core();
            if (core != null && (Vars.state.rules.infiniteResources || core.items.has(this.requirements, Vars.state.rules.buildCostMultiplier))) {
                this.seqs.clear();
                tile.getLinkedTilesAs(this, (inside) -> {
                    if (inside.build != null && !this.seqs.contains(inside.build) && this.seqs.size <= 1) {
                        if (inside.block() == this.pregrade && ((ExpTurretBuild)inside.build).level() >= this.pregradeLevel) {
                            this.seqs.add(inside.build);
                        }

                    }
                });
                return this.seqs.size == 1;
            } else {
                return false;
            }
        }
    }

    public void placeBegan(Tile tile, Block previous) {
        if (this.pregrade != null && previous == this.pregrade) {
            tile.setBlock(this, tile.team());
            UnityFx.placeShine.at(tile.drawx(), tile.drawy(), (float)(tile.block().size * 8), UnityPal.exp);
            Fx.upgradeCore.at(tile, (float)tile.block().size);
        } else {
            super.placeBegan(tile, previous);
        }

    }

    public int expLevel(int e) {
        return Math.min(this.maxLevel, (int)Mathf.sqrt((float)e / (5.0F * (float)this.expScale)));
    }

    public float expCap(int l) {
        if (l < 0) {
            return 0.0F;
        } else {
            if (l > this.maxLevel) {
                l = this.maxLevel;
            }

            return (float)this.requiredExp(l + 1);
        }
    }

    public int requiredExp(int l) {
        return l * l * 5 * this.expScale;
    }

    public void setEFields(int l) {
        for(EField<?> f : this.expFields) {
            f.setLevel(l);
        }

    }

    public class ExpTurretBuild extends Turret.TurretBuild implements ExpHolder, LevelHolder {
        public int exp;
        @Nullable
        public ExpHub.ExpHubBuild hub = null;

        public ExpTurretBuild() {
            super(ExpTurret.this);
        }

        public int incExp(int amount, boolean hub) {
            int ehub = hub && this.hubValid() ? this.hub.takeAmount(amount, this) : 0;
            int e = Math.min(amount - ehub, ExpTurret.this.maxExp - this.exp);
            if (e == 0) {
                return 0;
            } else {
                int before = this.level();
                this.exp += e;
                int after = this.level();
                if (this.exp > ExpTurret.this.maxExp) {
                    this.exp = ExpTurret.this.maxExp;
                }

                if (this.exp < 0) {
                    this.exp = 0;
                }

                if (after > before) {
                    this.levelup();
                }

                return e;
            }
        }

        public int getExp() {
            return this.exp;
        }

        public int handleExp(int amount) {
            return this.incExp(amount, true);
        }

        public int unloadExp(int amount) {
            if (ExpTurret.this.passive) {
                return 0;
            } else {
                int e = Math.min(amount, this.exp);
                this.exp -= e;
                return e;
            }
        }

        public boolean acceptOrb() {
            return !ExpTurret.this.passive && this.exp < ExpTurret.this.maxExp;
        }

        public boolean handleOrb(int orbExp) {
            int a = (int)(ExpTurret.this.orbScale * (float)orbExp);
            if (a < 1) {
                return false;
            } else {
                this.incExp(a, false);
                return true;
            }
        }

        public int handleTower(int amount, float angle) {
            return ExpTurret.this.passive ? 0 : this.incExp(amount, false);
        }

        public int level() {
            return ExpTurret.this.expLevel(this.exp);
        }

        public int maxLevel() {
            return ExpTurret.this.maxLevel;
        }

        public float expf() {
            int lv = this.level();
            if (lv >= ExpTurret.this.maxLevel) {
                return 1.0F;
            } else {
                float lb = ExpTurret.this.expCap(lv - 1);
                float lc = ExpTurret.this.expCap(lv);
                return ((float)this.exp - lb) / (lc - lb);
            }
        }

        public float levelf() {
            return (float)this.level() / (float)ExpTurret.this.maxLevel;
        }

        public void levelup() {
            ExpTurret.this.upgradeSound.at(this);
            ExpTurret.this.upgradeEffect.at(this);
            if (ExpTurret.this.upgradeBlockEffect != Fx.none) {
                ExpTurret.this.upgradeBlockEffect.at(this.x, this.y, this.rotation - 90.0F, Color.white, ExpTurret.this.region);
            }

        }

        public Color shootColor(Color tmp) {
            return tmp.set(ExpTurret.this.fromColor).lerp(ExpTurret.this.toColor, (float)this.exp / (float)ExpTurret.this.maxExp);
        }

        public Color effectColor() {
            return ExpTurret.this.effectColors == null ? Color.white : ExpTurret.this.effectColors[Math.min((int)(this.levelf() * (float)ExpTurret.this.effectColors.length), ExpTurret.this.effectColors.length - 1)];
        }

        public void update() {
            if (ExpTurret.this.updateExpFields) {
                ExpTurret.this.setEFields(this.level());
            }

            super.update();
        }

        public void draw() {
            if (ExpTurret.this.draw != null) {
                ExpTurret.this.draw.draw(this);
            }

            super.draw();
        }

        public void drawLight() {
            if (ExpTurret.this.draw != null) {
                ExpTurret.this.draw.drawLight(this);
            }

            super.drawLight();
        }

        protected void effects() {
            Effect fshootEffect = ExpTurret.this.shootEffect == Fx.none ? this.peekAmmo().shootEffect : ExpTurret.this.shootEffect;
            Effect fsmokeEffect = ExpTurret.this.smokeEffect == Fx.none ? this.peekAmmo().smokeEffect : ExpTurret.this.smokeEffect;
            Color effectc = this.effectColor();
            fshootEffect.at(this.x + ExpTurret.this.tr.x, this.y + ExpTurret.this.tr.y, this.rotation, effectc);
            fsmokeEffect.at(this.x + ExpTurret.this.tr.x, this.y + ExpTurret.this.tr.y, this.rotation, effectc);
            ExpTurret.this.shootSound.at(this.x + ExpTurret.this.tr.x, this.y + ExpTurret.this.tr.y, Mathf.random(0.9F, 1.1F));
            if (ExpTurret.this.shootShake > 0.0F) {
                Effect.shake(ExpTurret.this.shootShake, ExpTurret.this.shootShake, this);
            }

            this.recoil = ExpTurret.this.recoilAmount;
        }

        public void drawSelect() {
            Drawf.dashCircle(this.x, this.y, ExpTurret.this.rangeField == null ? ExpTurret.this.range : (Float)ExpTurret.this.rangeField.fromLevel(this.level()), this.team.color);
        }

        public void displayBars(Table table) {
            table.table(this::buildHBar).pad(0.0F).growX().padTop(8.0F).padBottom(4.0F);
            table.row();
            super.displayBars(table);
            table.table((t) -> {
                t.defaults().height(18.0F).pad(4.0F);
                t.label(() -> "Lv " + this.level()).color(ExpTurret.this.passive ? UnityPal.passive : Pal.accent).width(65.0F);
                t.add(new Bar(() -> this.level() >= ExpTurret.this.maxLevel ? "MAX" : Core.bundle.format("bar.expp", new Object[]{(int)(this.expf() * 100.0F)}), () -> UnityPal.exp, this::expf)).growX();
            }).pad(0.0F).growX().padTop(4.0F).padBottom(4.0F);
            table.row();
        }

        protected void buildHBar(Table t) {
            t.clearChildren();
            t.defaults().height(18.0F).pad(4.0F);
            int l = this.level();
            if ((Float)ExpTurret.this.damageReduction.fromLevel(this.level()) >= 0.01F) {
                Image ii = new Image(Icon.defense, Pal.health);
                ii.setSize(14.0F);
                Label ll = new Label(() -> Mathf.roundPositive((Float)ExpTurret.this.damageReduction.fromLevel(this.level()) * 100.0F) + "");
                ll.setStyle(new Label.LabelStyle(Styles.outlineLabel));
                ll.setSize(26.0F, 18.0F);
                ll.setAlignment(1);
                t.stack(new Element[]{ii, ll}).size(26.0F, 18.0F).pad(4.0F).padRight(8.0F).center();
            } else {
                t.update(() -> {
                    if (this.level() != l) {
                        this.buildHBar(t);
                    }

                });
            }

            t.add((new Bar("stat.health", Pal.health, this::healthf)).blink(Color.white)).growX();
        }

        public void killed() {
            ExpOrbs.spreadExp(this.x, this.y, (float)this.exp * 0.3F, 3.0F * (float)ExpTurret.this.size);
        }

        public float handleDamage(float amount) {
            return super.handleDamage(amount) * Mathf.clamp(1.0F - (Float)ExpTurret.this.damageReduction.fromLevel(this.level()));
        }

        public void write(Writes write) {
            super.write(write);
            write.i(this.exp);
        }

        public void read(Reads read, byte revision) {
            super.read(read, revision);
            this.exp = read.i();
            if (this.exp > ExpTurret.this.maxExp) {
                this.exp = ExpTurret.this.maxExp;
            }

        }

        public boolean hubbable() {
            return !ExpTurret.this.passive;
        }

        public boolean canHub(Building build) {
            return !this.hubValid() || build != null && build == this.hub;
        }

        public void setHub(ExpHub.ExpHubBuild hub) {
            this.hub = hub;
        }

        public boolean hubValid() {
            boolean val = this.hub != null && this.hub.isValid() && !this.hub.dead && this.hub.links.contains(this.pos());
            if (!val) {
                this.hub = null;
            }

            return val;
        }
    }

    public class LinearReloadTime extends EField<Float> {
        public Floatc set;
        public float start;
        public float scale;

        public LinearReloadTime(Floatc set, float start, float scale) {
            super(Stat.reload);
            this.start = start;
            this.scale = scale;
            this.set = set;
        }

        public Float fromLevel(int l) {
            return this.start + (float)l * this.scale;
        }

        public void setLevel(int l) {
            this.set.get(this.fromLevel(l));
        }

        public String toString() {
            return Core.bundle.format("field.linearreload", new Object[]{Strings.autoFixed((float)ExpTurret.this.shots * 60.0F / this.start, 2), Strings.autoFixed((float)ExpTurret.this.shots * 60.0F / (this.start + this.scale * (float)ExpTurret.this.maxLevel), 2)});
        }

        public void buildTable(Table table, int end) {
            table.left();
            Graph g = new Graph((i) -> (float)ExpTurret.this.shots * 60.0F / this.fromLevel(i), end, UnityPal.exp);
            table.add(g).size(330.0F, 160.0F).left();
            table.row();
            table.label(() -> g.lastMouseOver ? Core.bundle.format("ui.graph.label", new Object[]{g.lastMouseStep, Strings.autoFixed(g.mouseValue(), 2) + "/s"}) : Core.bundle.get("ui.graph.hover"));
        }
    }
}
