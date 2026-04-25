package unity.gen;

import arc.Core;
import arc.audio.Sound;
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
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatCat;
import mindustry.world.meta.StatValue;
import unity.content.UnityFx;
import unity.entities.ExpOrbs;
import unity.graphics.UnityPal;
import unity.world.blocks.distribution.KoruhConveyor;
import unity.world.blocks.exp.EField;
import unity.world.blocks.exp.ExpHolder;
import unity.world.blocks.exp.ExpHub;
import unity.world.blocks.exp.LevelHolder;
import unity.world.draw.DrawLevel;

public class ExpKoruhConveyor extends KoruhConveyor {
    public int maxLevel = 10;
    public int maxExp;
    public EField<?>[] expFields;
    public boolean passive = false;
    public boolean updateExpFields = true;
    @Nullable
    public ExpKoruhConveyor pregrade = null;
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

    public ExpKoruhConveyor(String name) {
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

    public void setBars() {
        super.setBars();
        this.bars.remove("health");
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

    public void setEFields(int l) {
        for(EField<?> f : this.expFields) {
            f.setLevel(l);
        }

    }

    public void buildGraphTable(Table t, EField<?> f) {
        Label l = (Label)t.add(f.toString()).get();
        Collapser c = new Collapser((tc) -> f.buildTable(tc, this.maxLevel), true);
        Runnable toggle = () -> c.toggle(false);
        l.clicked(toggle);
        t.button(Icon.downOpenSmall, Styles.clearToggleTransi, 20.0F, toggle).size(26.0F).color(UnityPal.exp).padLeft(8.0F);
        t.row();
        t.add(c).colspan(2).left();
    }

    public float getRange() {
        return 0.0F;
    }

    public int requiredExp(int l) {
        return l * l * 5 * this.expScale;
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

    public void placeBegan(Tile tile, Block previous) {
        if (this.pregrade != null && previous == this.pregrade) {
            tile.setBlock(this, tile.team());
            UnityFx.placeShine.at(tile.drawx(), tile.drawy(), (float)(tile.block().size * 8), UnityPal.exp);
            Fx.upgradeCore.at(tile, (float)tile.block().size);
        } else {
            super.placeBegan(tile, previous);
        }

    }

    public boolean canReplace(Block other) {
        return super.canReplace(other) || this.pregrade != null && other == this.pregrade;
    }

    public void load() {
        super.load();
        if (this.draw != null) {
            this.draw.load(this);
        }

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

    public void checkStats() {
        if (!this.stats.intialized) {
            this.setStats();
            this.addExpStats();
            this.stats.intialized = true;
        }

    }

    public int expLevel(int e) {
        return Math.min(this.maxLevel, (int)Mathf.sqrt((float)e / (5.0F * (float)this.expScale)));
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
                        if (inside.block() == this.pregrade && ((ExpKoruhConveyorBuild)inside.build).level() >= this.pregradeLevel) {
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

    public class ExpKoruhConveyorBuild extends KoruhConveyor.KoruhConveyorBuild implements ExpHolder, LevelHolder {
        public int exp;
        @Nullable
        public ExpHub.ExpHubBuild hub = null;

        public ExpKoruhConveyorBuild() {
            super(ExpKoruhConveyor.this);
        }

        public String toString() {
            return "ExpKoruhConveyorBuild#" + this.id;
        }

        public int maxLevel() {
            return ExpKoruhConveyor.this.maxLevel;
        }

        public void read(Reads read, byte revision) {
            super.read(read, revision);
            this.exp = read.i();
            if (this.exp > ExpKoruhConveyor.this.maxExp) {
                this.exp = ExpKoruhConveyor.this.maxExp;
            }

        }

        public int level() {
            return ExpKoruhConveyor.this.expLevel(this.exp);
        }

        public int incExp(int amount, boolean hub) {
            int ehub = hub && this.hubValid() ? this.hub.takeAmount(amount, this) : 0;
            int e = Math.min(amount - ehub, ExpKoruhConveyor.this.maxExp - this.exp);
            if (e == 0) {
                return 0;
            } else {
                int before = this.level();
                this.exp += e;
                int after = this.level();
                if (this.exp > ExpKoruhConveyor.this.maxExp) {
                    this.exp = ExpKoruhConveyor.this.maxExp;
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

        public boolean hubValid() {
            boolean val = this.hub != null && this.hub.isValid() && !this.hub.dead && this.hub.links.contains(this.pos());
            if (!val) {
                this.hub = null;
            }

            return val;
        }

        public Color shootColor(Color tmp) {
            return tmp.set(ExpKoruhConveyor.this.fromColor).lerp(ExpKoruhConveyor.this.toColor, (float)this.exp / (float)ExpKoruhConveyor.this.maxExp);
        }

        public void update() {
            if (ExpKoruhConveyor.this.updateExpFields) {
                ExpKoruhConveyor.this.setEFields(this.level());
            }

            super.update();
        }

        public boolean handleOrb(int orbExp) {
            int a = (int)(ExpKoruhConveyor.this.orbScale * (float)orbExp);
            if (a < 1) {
                return false;
            } else {
                this.incExp(a, false);
                return true;
            }
        }

        public int handleExp(int amount) {
            return this.incExp(amount, true);
        }

        public void buildHBar(Table t) {
            t.clearChildren();
            t.defaults().height(18.0F).pad(4.0F);
            int l = this.level();
            if ((Float)ExpKoruhConveyor.this.damageReduction.fromLevel(this.level()) >= 0.01F) {
                Image ii = new Image(Icon.defense, Pal.health);
                ii.setSize(14.0F);
                Label ll = new Label(() -> Mathf.roundPositive((Float)ExpKoruhConveyor.this.damageReduction.fromLevel(this.level()) * 100.0F) + "");
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

        public Color effectColor() {
            return ExpKoruhConveyor.this.effectColors == null ? Color.white : ExpKoruhConveyor.this.effectColors[Math.min((int)(this.levelf() * (float)ExpKoruhConveyor.this.effectColors.length), ExpKoruhConveyor.this.effectColors.length - 1)];
        }

        public void write(Writes write) {
            super.write(write);
            write.i(this.exp);
        }

        public boolean canHub(Building build) {
            return !this.hubValid() || build != null && build == this.hub;
        }

        public void setHub(ExpHub.ExpHubBuild hub) {
            this.hub = hub;
        }

        public float handleDamage(float amount) {
            return super.handleDamage(amount) * Mathf.clamp(1.0F - (Float)ExpKoruhConveyor.this.damageReduction.fromLevel(this.level()));
        }

        public void levelup() {
            ExpKoruhConveyor.this.upgradeSound.at(this);
            ExpKoruhConveyor.this.upgradeEffect.at(this);
            if (ExpKoruhConveyor.this.upgradeBlockEffect != Fx.none) {
                ExpKoruhConveyor.this.upgradeBlockEffect.at(this.x, this.y, (float)(this.rotation - 90), Color.white, ExpKoruhConveyor.this.region);
            }

        }

        public float levelf() {
            return (float)this.level() / (float)ExpKoruhConveyor.this.maxLevel;
        }

        public int unloadExp(int amount) {
            if (ExpKoruhConveyor.this.passive) {
                return 0;
            } else {
                int e = Math.min(amount, this.exp);
                this.exp -= e;
                return e;
            }
        }

        public int getExp() {
            return this.exp;
        }

        public void displayBars(Table table) {
            table.table(this::buildHBar).pad(0.0F).growX().padTop(8.0F).padBottom(4.0F);
            table.row();
            super.displayBars(table);
            table.table((t) -> {
                t.defaults().height(18.0F).pad(4.0F);
                t.label(() -> "Lv " + this.level()).color(ExpKoruhConveyor.this.passive ? UnityPal.passive : Pal.accent).width(65.0F);
                t.add(new Bar(() -> this.level() >= ExpKoruhConveyor.this.maxLevel ? "MAX" : Core.bundle.format("bar.expp", new Object[]{(int)(this.expf() * 100.0F)}), () -> UnityPal.exp, this::expf)).growX();
            }).pad(0.0F).growX().padTop(4.0F).padBottom(4.0F);
            table.row();
        }

        public float expf() {
            int lv = this.level();
            if (lv >= ExpKoruhConveyor.this.maxLevel) {
                return 1.0F;
            } else {
                float lb = ExpKoruhConveyor.this.expCap(lv - 1);
                float lc = ExpKoruhConveyor.this.expCap(lv);
                return ((float)this.exp - lb) / (lc - lb);
            }
        }

        public int handleTower(int amount, float angle) {
            return ExpKoruhConveyor.this.passive ? 0 : this.incExp(amount, false);
        }

        public void drawLight() {
            if (ExpKoruhConveyor.this.draw != null) {
                ExpKoruhConveyor.this.draw.drawLight(this);
            }

            super.drawLight();
        }

        public void draw() {
            if (ExpKoruhConveyor.this.draw != null) {
                ExpKoruhConveyor.this.draw.draw(this);
            }

            super.draw();
        }

        public boolean acceptOrb() {
            return !ExpKoruhConveyor.this.passive && this.exp < ExpKoruhConveyor.this.maxExp;
        }

        public boolean hubbable() {
            return !ExpKoruhConveyor.this.passive;
        }

        public void killed() {
            ExpOrbs.spreadExp(this.x, this.y, (float)this.exp * 0.3F, 3.0F * (float)ExpKoruhConveyor.this.size);
        }
    }
}
