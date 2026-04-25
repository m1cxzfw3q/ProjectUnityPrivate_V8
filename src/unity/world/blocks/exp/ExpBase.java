package unity.world.blocks.exp;

import arc.Core;
import arc.audio.Sound;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.struct.OrderedMap;
import arc.struct.Seq;
import arc.util.Nullable;
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

/** @deprecated */
@Deprecated
public class ExpBase extends Block {
    public int maxLevel = 10;
    public int maxExp;
    public EField<?>[] expFields;
    @Nullable
    public ExpBase pregrade = null;
    public int pregradeLevel = -1;
    public float orbScale = 0.8F;
    public int expScale = 1;
    public Effect upgradeEffect;
    public Sound upgradeSound;

    public ExpBase(String name) {
        super(name);
        this.upgradeEffect = UnityFx.upgradeBlockFx;
        this.upgradeSound = Sounds.message;
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

        this.setEFields(0);
        if (this.pregrade != null && this.pregradeLevel < 0) {
            this.pregradeLevel = this.pregrade.maxLevel;
        }

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

        for(EField<?> f : this.expFields) {
            if (f.stat != null) {
                if (map.containsKey(f.stat.category) && ((OrderedMap)map.get(f.stat.category)).containsKey(f.stat)) {
                    this.stats.remove(f.stat);
                }

                this.stats.add(f.stat, f.toString(), new Object[0]);
            }
        }

        if (this.pregrade != null) {
            this.stats.add(Stat.buildCost, "[#84ff00]\ue804" + Core.bundle.format("exp.upgradefrom", new Object[]{this.pregradeLevel, this.pregrade.localizedName}) + "[]", new Object[0]);
            this.stats.add(Stat.buildCost, (t) -> t.button(Icon.infoSmall, Styles.cleari, 20.0F, () -> Vars.ui.content.show(this.pregrade)).size(26.0F));
        }

        this.stats.add(Stat.itemCapacity, "@", new Object[]{Core.bundle.format("exp.lvlAmount", new Object[]{this.maxLevel})});
        this.stats.add(Stat.itemCapacity, "@", new Object[]{Core.bundle.format("exp.expAmount", new Object[]{this.maxExp})});
    }

    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);
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
                return tile.block() == this.pregrade && ((ExpTurret.ExpTurretBuild)tile.build).level() >= this.pregradeLevel;
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
        return Math.min(this.maxLevel, (int)Mathf.sqrt((float)e / (25.0F * (float)this.expScale)));
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
        return l * l * 25 * this.expScale;
    }

    public void setEFields(int l) {
        for(EField<?> f : this.expFields) {
            f.setLevel(l);
        }

    }

    public class ExpBaseBuild extends Building implements ExpHolder {
        public int exp;
        @Nullable
        public ExpHub.ExpHubBuild hub = null;

        public int getExp() {
            return this.exp;
        }

        public int handleExp(int amount) {
            int e = Math.min(amount, ExpBase.this.maxExp - this.exp);
            int before = this.level();
            this.exp += e;
            int after = this.level();
            if (this.exp > ExpBase.this.maxExp) {
                this.exp = ExpBase.this.maxExp;
            }

            if (this.exp < 0) {
                this.exp = 0;
            }

            if (after > before) {
                this.levelup();
            }

            return e;
        }

        public boolean acceptOrb() {
            return this.exp < ExpBase.this.maxExp;
        }

        public boolean handleOrb(int orbExp) {
            int a = (int)(ExpBase.this.orbScale * (float)orbExp);
            if (a < 1) {
                return false;
            } else {
                this.handleExp(a);
                return true;
            }
        }

        public int level() {
            return ExpBase.this.expLevel(this.exp);
        }

        public int maxLevel() {
            return ExpBase.this.maxLevel;
        }

        public float expf() {
            int lv = this.level();
            if (lv >= ExpBase.this.maxLevel) {
                return 1.0F;
            } else {
                float lb = ExpBase.this.expCap(lv - 1);
                float lc = ExpBase.this.expCap(lv);
                return ((float)this.exp - lb) / (lc - lb);
            }
        }

        public float levelf() {
            return (float)this.level() / (float)ExpBase.this.maxLevel;
        }

        public void levelup() {
            ExpBase.this.upgradeSound.at(this);
            ExpBase.this.upgradeEffect.at(this, (float)ExpBase.this.size);
        }

        public void update() {
            ExpBase.this.setEFields(this.level());
            super.update();
        }

        public void displayBars(Table table) {
            super.displayBars(table);
            table.table((t) -> {
                t.defaults().height(18.0F).pad(4.0F);
                t.label(() -> "Lv " + this.level()).color(Pal.accent).width(65.0F);
                t.add(new Bar(() -> this.level() >= ExpBase.this.maxLevel ? "MAX" : Core.bundle.format("bar.expp", new Object[]{(int)(this.expf() * 100.0F)}), () -> UnityPal.exp, this::expf)).growX();
            }).pad(0.0F).growX().padTop(4.0F).padBottom(4.0F);
            table.row();
        }

        public void killed() {
            ExpOrbs.spreadExp(this.x, this.y, (float)this.exp * 0.3F, 3.0F * (float)ExpBase.this.size);
        }

        public void write(Writes write) {
            super.write(write);
            write.i(this.exp);
        }

        public void read(Reads read, byte revision) {
            super.read(read, revision);
            this.exp = read.i();
        }

        public boolean hubValid() {
            return this.hub != null && !this.hub.dead && this.hub.links.contains(this.pos());
        }
    }
}
