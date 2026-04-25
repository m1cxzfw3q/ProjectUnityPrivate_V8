package younggamExperimental.blocks;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.graphics.gl.FrameBuffer;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.math.geom.Rect;
import arc.scene.ui.layout.Table;
import arc.struct.IntSeq;
import arc.struct.IntSet;
import arc.struct.ObjectMap;
import arc.struct.OrderedMap;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.entities.Units;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.modules.ItemModule;
import unity.graphics.UnityDrawf;
import unity.util.Utils;
import unity.v8.UnityStyles;
import unity.world.blocks.GraphBlock;
import unity.world.modules.GraphTorqueModule;
import younggamExperimental.IntPacker;
import younggamExperimental.ModularConstructorUI;
import younggamExperimental.PartInfo;
import younggamExperimental.PartStat;
import younggamExperimental.PartStatType;
import younggamExperimental.PartType;
import younggamExperimental.Segment;
import younggamExperimental.StatContainer;

public class Chopper extends GraphBlock {
    static final IntSet collidedBlocks = new IntSet();
    static final PartInfo[] partInfo = new PartInfo[4];
    static final float knockbackMult = 10.0F;
    static final int mask = 65535;
    final PartType[] categories;
    final TextureRegion[] baseRegions;
    TextureRegion topRegion;
    TextureRegion partsRegion;
    float partCostAccum;
    float autoBuildDelay;
    int gridW;
    int gridH;
    int spriteGridSize;
    int spriteGridPadding;
    int tx;
    int ty;
    private int index;

    public Chopper(String name) {
        super(name);
        this.categories = new PartType[]{PartType.blade, PartType.saw};
        this.baseRegions = new TextureRegion[4];
        this.partCostAccum = 0.2F;
        this.autoBuildDelay = 10.0F;
        this.gridW = 1;
        this.gridH = 1;
        this.spriteGridSize = 32;
        this.rotate = this.solid = this.configurable = this.acceptsItems = true;
        this.config(String.class, (ChopperBuild build, String value) -> build.changed = build.setBluePrintFromString(value));
        this.config(IntSeq.class, (ChopperBuild build, IntSeq value) -> build.changed = build.setBluePrint(Utils.unpackInts(value)));
        this.configClear((ChopperBuild build) -> build.setBluePrint(null));
    }

    protected void setGridW(int s) {
        this.gridW = Math.min(16, s);
    }

    protected void setGridH(int s) {
        this.gridH = Math.min(16, s);
    }

    PartType[] getPartsCategories() {
        return this.categories;
    }

    public void load() {
        super.load();
        this.topRegion = Core.atlas.find(this.name + "-top");

        for(int i = 0; i < 4; ++i) {
            this.baseRegions[i] = Core.atlas.find(this.name + "-base" + (i + 1));
        }

        this.partsRegion = Core.atlas.find(this.name + "-parts");
        partInfo[0].sprite = partInfo[3].sprite = Core.atlas.find(this.name + "-rod");
        partInfo[1].sprite = Core.atlas.find(this.name + "-blade1");
        partInfo[1].sprite2 = Core.atlas.find(this.name + "-blade2");
        partInfo[2].sprite = Core.atlas.find(this.name + "-sblade");

        for(int i = 0; i < 2; ++i) {
            this.categories[i].region = Core.atlas.find(this.name + "-category" + (i + 1));
        }

        this.tx = this.spriteGridPadding * 2 + this.gridW * this.spriteGridSize;
        this.ty = this.spriteGridPadding * 2 + this.gridH * this.spriteGridSize;
    }

    protected void addPart(String name, String desc, PartType category, int tx, int ty, int tw, int th, boolean cannotPlace, boolean isRoot, Point2 prePlace, ItemStack[] cost, byte[] connectOut, byte[] connectIn, PartStat... stats) {
        partInfo[this.index++] = new PartInfo(name, desc, category, tx, ty, tw, th, cannotPlace, isRoot, prePlace, cost, connectOut, connectIn, stats);
    }

    protected void addPart(String name, String desc, PartType category, int tx, int ty, int tw, int th, ItemStack[] cost, byte[] connectOut, byte[] connectIn, PartStat... stats) {
        partInfo[this.index++] = new PartInfo(name, desc, category, tx, ty, tw, th, cost, connectOut, connectIn, stats);
    }

    public class ChopperBuild extends GraphBlock.GraphBuild {
        final OrderedMap<Item, Integer> blueprintRemainingCost = new OrderedMap<>(12);
        final IntSeq bluePrint = new IntSeq();
        final Seq<Segment> hitSegments = new Seq<>();
        final FrameBuffer buffer;
        final StatContainer currentStats;
        final Rect detectRect;
        float originalMaxHp;
        float speedDmgMul;
        float aniProg;
        float aniSpeed;
        float aniTime;
        int totalItemCountCost;
        int totalItemCountPaid;
        int knockbackTorque;
        int inertia;
        int bladeRadius;
        boolean changed;

        public ChopperBuild() {
            this.buffer = new FrameBuffer(Chopper.this.tx, Chopper.this.ty);
            this.currentStats = new StatContainer();
            this.detectRect = new Rect();
            this.inertia = 5;
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
            TextureRegion tex = Draw.wrap(this.buffer.getTexture());
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
            if (totalItemCountPaid < totalItemCountCost) {
                if (!Vars.state.rules.infiniteResources && !team.rules().infiniteResources && !team.rules().cheat) {
                    if (timer(timerDump, autoBuildDelay)) {
                        CoreBlock.CoreBuild core = team.core();
                        if (core == null) {
                            return;
                        }

                        ItemModule cItems = core.items;

                        for (ObjectMap.Entry<Item, Integer> i : blueprintRemainingCost) {
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
            return super.acceptItem(source, item) || hasSpace;
        }

        public void handleItem(Building source, Item item) {
            if (totalItemCountPaid != totalItemCountCost) {
                ++totalItemCountPaid;
                int value = blueprintRemainingCost.get(item, 0);
                blueprintRemainingCost.put(item, value + 65536);
                if (totalItemCountPaid == totalItemCountCost) {
                    applyStats();
                }

            }
        }

        void resetStats() {
            this.inertia = 5;
            this.hitSegments.clear();
            if (this.originalMaxHp > 0.0F) {
                this.maxHealth = this.originalMaxHp;
            }

        }

        void applyStats() {
            this.inertia = 5 + this.currentStats.inertia;
            this.originalMaxHp = this.maxHealth;
            this.maxHealth = this.originalMaxHp + (float)this.currentStats.hpinc;
            this.heal((float)this.currentStats.hpinc * this.health / this.originalMaxHp);
            hitSegments.set(this.currentStats.segments);
            int r = 0;
            int i = 0;

            for(int len = this.hitSegments.size; i < len; ++i) {
                r = Math.max(r, (hitSegments.get(i)).end * 8);
            }

            this.detectRect.setCentered(this.x, this.y, (float)r * 2.0F);
            this.bladeRadius = r;
        }

        float getHitDamage(float rx, float ry, float rot) {
            float dist = Mathf.dst(rx, ry);
            float drx = Mathf.cosDeg(rot);
            float dry = Mathf.sinDeg(rot);
            if (!(rx * drx / dist + ry * dry / dist < Mathf.cosDeg(Mathf.clamp(this.speedDmgMul * 10.0F, 0.0F, 180.0F)))) {
                for (Segment seg : this.hitSegments) {
                    if ((float) (seg.start * 8 + 4) < dist && (float) (seg.end * 8 + 4) > dist) {
                        return (float) seg.damage * Mathf.clamp(dist * 0.1F);
                    }
                }
            }
            return 0.0F;
        }

        void onIntCollider(int cx, int cy, float rot) {
            Building build = Vars.world.build(cx, cy);
            boolean collide = build != null && Chopper.collidedBlocks.add(this.tile.pos());
            if (collide && build.team != this.team) {
                float k = this.getHitDamage((float)((cx - this.tileX()) * 8), (float)((cy - this.tileY()) * 8), rot);
                build.damage(k);
                this.knockbackTorque = this.knockbackTorque + (int) k * 10;
            }

        }

        void damageChk(float rot) {
            float drx = Mathf.cosDeg(rot);
            float dry = Mathf.sinDeg(rot);
            Chopper.collidedBlocks.clear();
            Vars.world.raycastEachWorld(this.x, this.y, this.x + drx * (float)this.bladeRadius, this.y + dry * (float)this.bladeRadius, (cx, cy) -> {
                onIntCollider(cx, cy, rot);
                return false;
            });
            Units.nearbyEnemies(this.team, this.detectRect, (unit) -> {
                if (unit.checkTarget(false, true)) {
                    float k = this.getHitDamage(unit.x - this.x, unit.y - this.y, rot);
                    if (k > 0.0F) {
                        unit.damage(k);
                        unit.impulse(-dry * k * 10.0F, drx * k * 10.0F);
                        this.knockbackTorque = (int)((float)this.knockbackTorque + k * 10.0F);
                    }
                }
            });
        }

        void accumStats(PartInfo part, int x, int y, int[][] grid) {
            PartStat iner = part.stats.get(PartStatType.mass);
            if (iner != null) {
                this.currentStats.inertia += iner.asInt() * x;
            }

            PartStat hp = part.stats.get(PartStatType.hp);
            if (hp != null) {
                this.currentStats.hpinc += hp.asInt();
            }

            PartStat collides = part.stats.get(PartStatType.collides);
            if (collides != null && collides.asBool()) {
                PartStat damage = part.stats.get(PartStatType.damage);
                int dmg = damage != null ? damage.asInt() : 0;
                if (this.currentStats.segments.isEmpty()) {
                    this.currentStats.segments.add(new Segment(x, x + part.tw, dmg));
                } else {
                    boolean appended = false;

                    for(Segment i : this.currentStats.segments) {
                        if (i.damage == dmg && i.end == x) {
                            i.end += part.tw;
                            appended = true;
                            break;
                        }
                    }

                    if (!appended) {
                        this.currentStats.segments.add(new Segment(x, x + part.tw, dmg));
                    }
                }
            }

        }

        void drawPartBuffer(PartInfo part, int x, int y, int[][] grid) {
            Draw.rect(part.sprite, ((float)x + (float)part.tw * 0.5F) * 32.0F, ((float)y + (float)part.th * 0.5F) * 32.0F, (float)part.tw * 32.0F, (float)part.th * 32.0F);
            if (part.sprite2 != null && x + 1 < grid.length && grid[x + 1][y] == 0) {
                Draw.rect(part.sprite2, ((float)x + (float)part.tw * 0.5F + 1.0F) * 32.0F, ((float)y + (float)part.th * 0.5F) * 32.0F, (float)part.tw * 32.0F, (float)part.th * 32.0F);
            }

        }

        public void buildConfiguration(Table table) {
            table.button(Tex.whiteui, UnityStyles.clearTransi, 50f, () -> {
                BaseDialog dialog = new BaseDialog("Edit Blueprint");
                dialog.setFillParent(false);
                ModularConstructorUI mtd = ModularConstructorUI.applyModularConstructorUI(dialog.cont, partsRegion, Math.round(partsRegion.width / 32f), Math.round(partsRegion.height / 32f), partInfo, gridW, gridH, bluePrint, getPartsCategories(), partCostAccum);
                dialog.buttons.button("@ok", () -> {
                    configure(mtd.getPackedSave());
                    dialog.hide();
                }).size(130f, 60f);
                dialog.update(() -> {
                    if (!(tile.build instanceof ChopperBuild)) {
                        dialog.hide();
                    }
                });
                dialog.show();
            }).size(50f).get().getStyle().imageUp = Icon.pencil;
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
                        PartInfo partL = Chopper.partInfo[temp - 1];
                        cstMult += Chopper.this.partCostAccum * (float)partL.tw * (float)partL.th;
                    }
                }

                cstMult -= Chopper.this.partCostAccum;
                this.totalItemCountCost = this.totalItemCountPaid = 0;
                int[][] gridPrint = new int[len / Chopper.this.gridH][Chopper.this.gridH];
                this.blueprintRemainingCost.clear();

                for(int p = 0; p < len; ++p) {
                    int temp = this.bluePrint.get(p);
                    if (temp != 0) {
                        PartInfo partL = Chopper.partInfo[temp - 1];
                        ItemStack[] prtTmp = partL.cost;

                        for(ItemStack cstItem : prtTmp) {
                            int cur = (Integer)this.blueprintRemainingCost.get(cstItem.item, 0);
                            int increment = Mathf.floor((float)cstItem.amount * cstMult);
                            this.blueprintRemainingCost.put(cstItem.item, cur + increment);
                            this.totalItemCountCost += increment;
                        }
                    }

                    gridPrint[p / Chopper.this.gridH][p % Chopper.this.gridH] = temp;
                }

                this.currentStats.clear();

                for(int p = 0; p < len; ++p) {
                    int temp = this.bluePrint.get(p);
                    if (temp != 0) {
                        this.accumStats(Chopper.partInfo[temp - 1], p / Chopper.this.gridH, p % Chopper.this.gridH, gridPrint);
                    }
                }

                if (!Vars.headless) {
                    Draw.draw(Draw.z(), () -> {
                        Tmp.m1.set(Draw.proj());
                        Draw.proj(0.0F, 0.0F, (float)Chopper.this.tx, (float)Chopper.this.ty);
                        this.buffer.begin(Color.clear);
                        Draw.color(Color.white);

                        for(int p = 0; p < len; ++p) {
                            int temp = this.bluePrint.get(p);
                            if (temp != 0) {
                                this.drawPartBuffer(Chopper.partInfo[temp - 1], p / Chopper.this.gridH, p % Chopper.this.gridH, gridPrint);
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
                        write.s((i.key).id);
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

        public void updatePre() {
            GraphTorqueModule<?> tGraph = this.torque();
            tGraph.setInertia((float)this.inertia);
            tGraph.force = (float)(-this.knockbackTorque);
            this.knockbackTorque = 0;
            this.aniTime += Time.delta;
            float prog = this.getPaidRatio();
            if (this.aniProg < prog) {
                this.aniSpeed = (prog - this.aniProg) * 0.1F;
                this.aniProg += this.aniSpeed;
            } else {
                this.aniProg = prog;
                this.aniSpeed = 0.0F;
            }

            this.speedDmgMul = tGraph.getNetwork().lastVelocity;
            this.updateAutoBuild();
        }

        public void updatePost() {
            if (this.getPaidRatio() >= 1.0F && this.speedDmgMul > 0.8F) {
                this.damageChk(this.torque().getRotation());
            }

        }

        public void draw() {
            float rot = this.torque().getRotation();
            Draw.rect(Chopper.this.baseRegions[this.rotation], this.x, this.y);
            TextureRegion blades = this.getBufferRegion();
            if (blades != null) {
                Draw.z(50.0F);
                if (this.getPaidRatio() < 1.0F) {
                    blades.setU2(Mathf.map(this.aniProg, 0.0F, 1.0F, blades.u, blades.u2));
                    UnityDrawf.drawConstruct(blades, this.aniProg, Pal.accent, 1.0F, this.aniTime * 0.5F, 50.0F, (tex) -> Draw.rect(tex, this.x + (float)tex.width * 0.125F, this.y, (float)tex.width * 0.25F, (float)tex.height * 0.25F, 0.0F, (float)tex.height * 0.25F * 0.5F, rot));
                } else {
                    Draw.rect(blades, this.x + (float)blades.width * 0.125F, this.y, (float)blades.width * 0.25F, (float)blades.height * 0.25F, 0.0F, (float)blades.height * 0.25F * 0.5F, rot);
                }

                Draw.rect(Chopper.this.topRegion, this.x, this.y);
            }

            this.drawTeamTop();
        }
    }
}
