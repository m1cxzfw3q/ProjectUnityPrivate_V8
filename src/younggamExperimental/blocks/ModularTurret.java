package younggamExperimental.blocks;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.Texture;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.graphics.gl.FrameBuffer;
import arc.math.Mathf;
import arc.scene.ui.ImageButton;
import arc.scene.ui.layout.Table;
import arc.struct.IntSeq;
import arc.struct.ObjectMap;
import arc.struct.OrderedMap;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.content.Bullets;
import mindustry.entities.Units;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.world.blocks.defense.turrets.Turret;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.meta.Stat;
import mindustry.world.meta.Stats;
import mindustry.world.modules.ItemModule;
import unity.content.UnityBullets;
import unity.graphics.UnityDrawf;
import unity.util.Utils;
import unity.v8.UnityStyles;
import unity.world.blocks.GraphBlockBase;
import unity.world.graphs.Graphs;
import unity.world.modules.GraphModules;
import younggamExperimental.IntPacker;
import younggamExperimental.ModularConstructorUI;
import younggamExperimental.PartInfo;
import younggamExperimental.PartStat;
import younggamExperimental.PartStatType;
import younggamExperimental.PartType;
import younggamExperimental.StatContainer;
import younggamExperimental.UnityParts;

public class ModularTurret extends Turret implements GraphBlockBase {
    static final int mask = 65535;
    protected final Graphs graphs = new Graphs();
    PartInfo[] partInfo;
    final TextureRegion[] regions = new TextureRegion[4];
    TextureRegion partsRegion;
    TextureRegion rootRegion;
    TextureRegion rootOutlineRegion;
    protected float yShift;
    protected float yScale = 1.0F;
    protected float partCostAccum = 0.2F;
    float autoBuildDelay = 10.0F;
    protected int spriteGridSize = 32;
    protected int spriteGridPadding;
    int gridW = 1;
    int gridH = 1;
    int tx;
    int ty;

    public ModularTurret(String name) {
        super(name);
        this.rotate = this.configurable = this.hasItems = true;
        this.config(String.class, (ModularTurretBuild build, String value) -> build.changed = build.setBluePrintFromString(value));
        this.config(IntSeq.class, (ModularTurretBuild build, IntSeq value) -> build.changed = build.setBluePrint(Utils.unpackInts(value)));
        this.configClear((ModularTurretBuild build) -> build.setBluePrint(null));
    }

    public void load() {
        super.load();

        for(int i = 0; i < 4; ++i) {
            this.regions[i] = Core.atlas.find(this.name + (i + 1));
        }

        this.partsRegion = Core.atlas.find("unity-partsicons");
        this.rootRegion = Core.atlas.find(this.name + "-root");
        this.rootOutlineRegion = Core.atlas.find(this.name + "-root-outline");
        this.tx = this.spriteGridPadding * 2 + this.gridW * this.spriteGridSize;
        this.ty = this.spriteGridPadding * 2 + this.gridH * this.spriteGridSize;
    }

    public void init() {
        super.init();
        this.partInfo = UnityParts.getPartList();
    }

    public void setStats() {
        super.setStats();
        this.graphs.setStats(this.stats);
        this.setStatsExt(this.stats);
    }

    public void drawPlace(int x, int y, int rotation, boolean valid) {
        this.graphs.drawPlace(x, y, this.size, rotation, valid);
        super.drawPlace(x, y, rotation, valid);
    }

    public Graphs graphs() {
        return this.graphs;
    }

    protected void setGridW(int s) {
        this.gridW = Math.min(16, s);
    }

    protected void setGridH(int s) {
        this.gridH = Math.min(16, s);
    }

    PartType[] getPartsCategories() {
        return null;
    }

    public void setStatsExt(Stats stats) {
        stats.add(Stat.ammo, (table) -> {
        });
    }

    public class ModularTurretBuild extends Turret.TurretBuild implements GraphBlockBase.GraphBuildBase {
        protected GraphModules gms;
        final OrderedMap<Item, Integer> blueprintRemainingCost = new OrderedMap<>(12);
        final IntSeq bluePrint = new IntSeq();
        final FrameBuffer buffer;
        final StatContainer currentStats;
        float turretRange;
        float originalMaxHp;
        float aniProg;
        float aniSpeed;
        float aniTime;
        int totalItemCountCost;
        int totalItemCountPaid;
        int itemCap;
        boolean changed;
        boolean validTurret;

        public ModularTurretBuild() {
            this.buffer = new FrameBuffer(ModularTurret.this.tx, ModularTurret.this.ty);
            this.currentStats = new StatContainer();
            this.turretRange = 80.0F;
        }

        float getPaidRatio() {
            return this.totalItemCountCost == 0 ? 0.0F : (float)this.totalItemCountPaid / (float)this.totalItemCountCost;
        }

        boolean setBluePrintFromString(String s) {
            return this.setBluePrint(Utils.unpackIntsFromString(s));
        }

        boolean setBluePrint(IntSeq s) {
            if (!this.bluePrint.equals(s)) {
                this.bluePrint.clear();
                if (s != null) {
                    this.bluePrint.addAll(s);
                }

                return true;
            } else {
                return false;
            }
        }

        TextureRegion getBufferRegion() {
            TextureRegion tex = Draw.wrap((Texture)this.buffer.getTexture());
            tex.v = tex.v2;
            tex.v2 = tex.u;
            return tex;
        }

        public void displayExt(Table table) {
            table.row();
            table.table().left().update((sub) -> {
                sub.clearChildren();
                if (this.totalItemCountPaid != this.totalItemCountCost) {
                    sub.left();
                    if (this.blueprintRemainingCost.isEmpty()) {
                        sub.labelWrap("No blueprint").color(Color.lightGray);
                    } else {
                        for (ObjectMap.Entry<Item, Integer> i : this.blueprintRemainingCost) {
                            sub.image(i.key.uiIcon).size(32.0F);
                            sub.add((i.value >>> 16) + "/" + (i.value & '\uffff'));
                            sub.row();
                        }
                    }

                }
            });
        }

        void updateAutoBuild() {
            if (this.totalItemCountPaid < this.totalItemCountCost) {
                if (!Vars.state.rules.infiniteResources && !this.team.rules().infiniteResources && !this.team.rules().cheat) {
                    if (this.timer(ModularTurret.this.timerDump, ModularTurret.this.autoBuildDelay)) {
                        CoreBlock.CoreBuild core = this.team.core();
                        if (core == null) {
                            return;
                        }

                        ItemModule cItems = core.items;

                        for (ObjectMap.Entry<Item, Integer> i : this.blueprintRemainingCost) {
                            if (i.value >>> 16 < (i.value & '\uffff') && cItems.get(i.key) > 0) {
                                cItems.remove(i.key, 1);
                                ++totalItemCountPaid;
                                blueprintRemainingCost.put(i.key, i.value + 65536);
                                if (totalItemCountPaid == totalItemCountCost) {
                                    applyStats();
                                }
                                return;
                            }
                        }
                    }

                } else {
                    ObjectMap.Entry<Item, Integer> i;
                    int temp;
                    for(ObjectMap.Entries<Item, Integer> var1 = blueprintRemainingCost.iterator(); var1.hasNext(); i.value = i.value + temp) {
                        i = var1.next();
                        temp = i.value & '\uffff';
                        i.value = i.value << 16;
                    }

                    totalItemCountPaid = totalItemCountCost;
                    applyStats();
                }
            }
        }

        public boolean acceptItem(Building source, Item item) {
            int value = blueprintRemainingCost.get(item, 0);
            boolean hasSpace = value >>> 16 < (value & '\uffff');
            return super.acceptItem(source, item) || hasSpace && acceptItemExt(source, item);
        }

        public void handleItem(Building source, Item item) {
            if (totalItemCountPaid == totalItemCountCost) {
                handleItemExt(source, item);
            } else {
                ++this.totalItemCountPaid;
                int value = blueprintRemainingCost.get(item, 0);
                blueprintRemainingCost.put(item, value + 65536);
                if (totalItemCountPaid == totalItemCountCost) {
                    applyStats();
                }
            }
        }

        boolean acceptItemExt(Building source, Item item) {
            return false;
        }

        void handleItemExt(Building source, Item item) {
            super.handleItem(source, item);
        }

        PartInfo[] getPartsConfig() {
            return ModularTurret.this.partInfo;
        }

        public boolean hasAmmo() {
            return false;
        }

        void attemptRefillingMag() {
        }

        boolean haveAmmoType() {
            return false;
        }

        public BulletType useAmmo() {
            return UnityBullets.standardCopper;
        }

        public BulletType peekAmmo() {
            return UnityBullets.standardCopper;
        }

        void applyStats() {
            this.originalMaxHp = this.maxHealth;
            this.maxHealth = this.originalMaxHp + this.currentStats.hpinc;
            this.turretRange = 80.0F;
            this.heal(this.currentStats.hpinc * this.health / this.originalMaxHp);
        }

        public void displayBarsExt(Table table) {
            GraphBuildBase.super.displayBarsExt(table);
        }

        void accumStats(PartInfo part, int x, int y, int[][] grid) {
            PartStat hp = part.stats.get(PartStatType.hp);
            if (hp != null) {
                this.currentStats.hpinc += hp.asInt();
            }

            PartStat range = part.stats.get(PartStatType.rangeinc);
            if (range != null) {
                this.currentStats.rangeInc += range.asInt();
            }
        }

        void resetStats() {
            if (this.originalMaxHp > 0.0F) {
                this.maxHealth = this.originalMaxHp;
            }

            this.validTurret = false;
            this.turretRange = 80.0F;
            this.itemCap = 10;
        }

        void drawPartBuffer(PartInfo part, int x, int y, int[][] grid) {
        }

        void preDrawBuffer() {
        }

        public void buildConfiguration(Table table) {
            ((ImageButton)table.button(Tex.whiteui, UnityStyles.clearTransi, 50.0F, () -> {
                BaseDialog dialog = new BaseDialog("Edit Blueprint");
                dialog.setFillParent(false);
                ModularConstructorUI mtd = ModularConstructorUI.applyModularConstructorUI(dialog.cont, ModularTurret.this.partsRegion, Math.round((float)ModularTurret.this.partsRegion.width / 32.0F), Math.round((float)ModularTurret.this.partsRegion.height / 32.0F), ModularTurret.this.partInfo, ModularTurret.this.gridW, ModularTurret.this.gridH, this.bluePrint, ModularTurret.this.getPartsCategories(), ModularTurret.this.partCostAccum);
                dialog.buttons.button("@ok", () -> {
                    this.configure(mtd.getPackedSave());
                    dialog.hide();
                }).size(130.0F, 60.0F);
                dialog.update(() -> {
                    if (!(this.tile.build instanceof Chopper.ChopperBuild)) {
                        dialog.hide();
                    }

                });
                dialog.show();
            }).size(50.0F).get()).getStyle().imageUp = Icon.pencil;
        }

        public void configured(Unit builder, Object value) {
            this.changed = false;
            super.configured(builder, value);
            if (this.changed) {
                this.resetStats();
                float cstMult = 1.0F;
                int len = this.bluePrint.size;

                for(int p = 0; p < len; ++p) {
                    int temp = this.bluePrint.get(p);
                    if (temp != 0) {
                        PartInfo partL = ModularTurret.this.partInfo[temp - 1];
                        cstMult += ModularTurret.this.partCostAccum * (float)partL.tw * (float)partL.th;
                    }
                }

                cstMult -= ModularTurret.this.partCostAccum;
                this.totalItemCountCost = this.totalItemCountPaid = 0;
                int[][] gridPrint = new int[len / ModularTurret.this.gridH][ModularTurret.this.gridH];
                this.blueprintRemainingCost.clear();

                for(int p = 0; p < len; ++p) {
                    int temp = this.bluePrint.get(p);
                    if (temp != 0) {
                        PartInfo partL = ModularTurret.this.partInfo[temp - 1];
                        ItemStack[] prtTmp = partL.cost;

                        for(ItemStack cstItem : prtTmp) {
                            int cur = (Integer)this.blueprintRemainingCost.get(cstItem.item, 0);
                            int increment = Mathf.floor((float)cstItem.amount * cstMult);
                            this.blueprintRemainingCost.put(cstItem.item, cur + increment);
                            this.totalItemCountCost += increment;
                        }
                    }

                    gridPrint[p / ModularTurret.this.gridH][p % ModularTurret.this.gridH] = temp;
                }

                this.currentStats.clear();

                for(int p = 0; p < len; ++p) {
                    int temp = this.bluePrint.get(p);
                    if (temp != 0) {
                        this.accumStats(ModularTurret.this.partInfo[temp - 1], p / ModularTurret.this.gridH, p % ModularTurret.this.gridH, gridPrint);
                    }
                }

                if (!Vars.headless) {
                    Draw.draw(Draw.z(), () -> {
                        Tmp.m1.set(Draw.proj());
                        Draw.proj(0.0F, 0.0F, (float)ModularTurret.this.tx, (float)ModularTurret.this.ty);
                        this.buffer.begin(Color.clear);
                        Draw.color(Color.white);

                        for(int p = 0; p < len; ++p) {
                            int temp = this.bluePrint.get(p);
                            if (temp != 0) {
                                this.drawPartBuffer(ModularTurret.this.partInfo[temp - 1], p / ModularTurret.this.gridH, p % ModularTurret.this.gridH, gridPrint);
                            }
                        }

                        this.buffer.end();
                        Draw.proj(Tmp.m1);
                        Draw.reset();
                    });
                }

            }
        }

        public String config() {
            return this.bluePrint.isEmpty() ? "" : IntPacker.packArray(this.bluePrint).toStringPack();
        }

        public void writeExt(Writes write) {
            if (this.bluePrint.isEmpty()) {
                write.i(0);
            } else {
                IntSeq tmp = IntPacker.packArray(this.bluePrint).packed;
                int len = tmp.size;
                write.s(len);

                for(int i = 0; i < len; ++i) {
                    write.i(tmp.get(i));
                }

                if (this.blueprintRemainingCost.isEmpty()) {
                    write.s(0);
                } else {
                    write.s(this.blueprintRemainingCost.size);

                    for (ObjectMap.Entry<Item, Integer> i : this.blueprintRemainingCost) {
                        write.s(i.key.id);
                        write.s(i.value >>> 16);
                    }
                }

            }
        }

        public void readExt(Reads read, byte revision) {
            short packedSize = read.s();
            IntSeq pack = new IntSeq();

            for(int i = 0; i < packedSize; ++i) {
                pack.add(read.i());
            }

            this.configureAny(pack);
            short costSize = read.s();
            if (costSize > 0) {
                for(int i = 0; i < costSize; ++i) {
                    Item item = Vars.content.item(read.s());
                    short paid = read.s();
                    int cur = (Integer)this.blueprintRemainingCost.get(item, -1);
                    if (cur != -1) {
                        this.blueprintRemainingCost.put(item, (paid << 16) + cur);
                        this.totalItemCountPaid += paid;
                    }
                }
            }

            if (this.totalItemCountPaid == this.totalItemCountCost) {
                this.applyStats();
            }

        }

        protected void updateShooting() {
        }

        protected void findTarget() {
            this.target = Units.bestTarget(this.team, this.x, this.y, this.turretRange, (e) -> !e.dead, (b) -> true, unitSort);
        }

        public void updatePre() {
            this.updateAutoBuild();
        }

        void drawExt() {
            Draw.rect(regions[(int) rotation / 90], this.x, this.y);
            this.drawTeamTop();
        }

        public void draw() {
            this.aniTime += Time.delta;
            float prog = this.getPaidRatio();
            if (this.aniProg < prog) {
                this.aniSpeed = (prog - this.aniProg) * 0.1F;
                this.aniProg += this.aniSpeed;
            } else {
                this.aniProg = prog;
                this.aniSpeed = 0.0F;
            }

            this.drawExt();
            TextureRegion turretSprite = this.getBufferRegion();
            if (turretSprite != null) {
                Draw.z(50.0F);
                if (this.getPaidRatio() < 1.0F) {
                    float ou = turretSprite.u;
                    float ou2 = turretSprite.u2;
                    float ov = turretSprite.v;
                    float ov2 = turretSprite.v2;
                    turretSprite.setU2(Mathf.map(this.aniProg, 0.0F, 1.0F, ou + 0.5F * (ou2 - ou), ou2));
                    turretSprite.setU(Mathf.map(this.aniProg, 0.0F, 1.0F, ou + 0.5F * (ou2 - ou), ou));
                    turretSprite.setV2(Mathf.map(this.aniProg, 0.0F, 1.0F, ov + 0.5F * (ov2 - ov), ov2));
                    turretSprite.setV(Mathf.map(this.aniProg, 0.0F, 1.0F, ov + 0.5F * (ov2 - ov), ov));
                    UnityDrawf.drawConstruct(turretSprite, this.aniProg, Pal.accent, 1.0F, this.aniTime * 0.5F, 50.0F, (tex) -> Draw.rect(tex, this.x, this.y, this.rotation + 90.0F));
                } else {
                    Draw.rect(turretSprite, this.x, this.y, this.rotation - 90.0F);
                }
            }

        }

        public void created() {
            this.gms = new GraphModules(this);
            ModularTurret.this.graphs.injectGraphConnector(this.gms);
            this.gms.created();
        }

        public float efficiency() {
            return super.efficiency * this.gms.efficiency();
        }

        public void onRemoved() {
            this.gms.updateGraphRemovals();
            this.onDelete();
            super.onRemoved();
            this.onDeletePost();
        }

        public void updateTile() {
            if (ModularTurret.this.graphs.useOriginalUpdate()) {
                super.updateTile();
            }

            this.updatePre();
            this.gms.updateTile();
            this.updatePost();
            this.gms.prevTileRotation((int) this.rotation / 90);
        }

        public void onProximityUpdate() {
            super.onProximityUpdate();
            this.gms.onProximityUpdate();
            this.proxUpdate();
        }

        public void display(Table table) {
            super.display(table);
            this.gms.display(table);
            this.displayExt(table);
        }

        public void displayBars(Table table) {
            super.displayBars(table);
            this.gms.displayBars(table);
            this.displayBarsExt(table);
        }

        public void write(Writes write) {
            super.write(write);
            this.gms.write(write);
            this.writeExt(write);
        }

        public void read(Reads read, byte revision) {
            super.read(read, revision);
            this.gms.read(read, revision);
            this.readExt(read, revision);
        }

        public GraphModules gms() {
            return this.gms;
        }

        public void drawSelect() {
            Drawf.dashCircle(this.x, this.y, this.turretRange, this.team.color);
            this.gms.drawSelect();
        }
    }
}
