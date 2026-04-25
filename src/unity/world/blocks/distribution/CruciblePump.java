package unity.world.blocks.distribution;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.util.Strings;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.type.Item;
import mindustry.ui.Styles;
import mindustry.world.blocks.ItemSelection;
import mindustry.world.meta.StatUnit;
import unity.Unity;
import unity.graphics.UnityDrawf;
import unity.world.blocks.GraphBlock;
import unity.world.graph.CrucibleGraph;
import unity.world.meta.CrucibleData;
import unity.world.meta.MeltInfo;
import unity.world.modules.GraphCrucibleModule;

public class CruciblePump extends GraphBlock {
    public static final float[] fillAm = new float[]{1.0F, 0.5F, 0.25F};
    public final TextureRegion[] topRegions = new TextureRegion[4];
    public TextureRegion bottomRegion;

    public CruciblePump(String name) {
        super(name);
        this.rotate = this.solid = this.configurable = true;
        this.config(Item.class, (build, item) -> build.filterItem = item);
        this.config(Integer.class, (build, value) -> {
            Unity.print(new Object[]{value, value & 3, value >>> 2});
            build.pumpMode = value & 3;
            if (value > 2) {
                build.filterItem = Vars.content.item(value >>> 2);
            }

        });
        this.configClear((build) -> build.filterItem = null);
    }

    public void load() {
        super.load();

        for(int i = 0; i < 4; ++i) {
            this.topRegions[i] = Core.atlas.find(this.name + "-top" + (i + 1));
        }

        this.bottomRegion = Core.atlas.find(this.name + "-bottom");
    }

    public class CruciblePumpBuild extends GraphBlock.GraphBuild {
        Item filterItem;
        float flowRate;
        float flowAnimation;
        int pumpMode;

        public CruciblePumpBuild() {
            super(CruciblePump.this);
        }

        public void buildConfiguration(Table table) {
            table.labelWrap("Fill until:").growX().pad(5.0F).center().row();
            table.table((bt) -> {
                bt.button("Full", Styles.clearPartialt, () -> this.configure(0)).left().size(50.0F).disabled((b) -> this.pumpMode == 0);
                bt.button("50%", Styles.clearPartialt, () -> this.configure(1)).left().size(50.0F).disabled((b) -> this.pumpMode == 1);
                bt.button("25%", Styles.clearPartialt, () -> this.configure(2)).left().size(50.0F).disabled((b) -> this.pumpMode == 2);
            }).row();
            table.labelWrap("Pump:").growX().pad(5.0F).center().row();
            ItemSelection.buildTable(table, Vars.content.items(), () -> this.filterItem, this::configure);
            table.setBackground(Styles.black5);
        }

        public void displayExt(Table table) {
            String ps = " " + StatUnit.perSecond.localized();
            table.row();
            table.table((sub) -> {
                sub.clearChildren();
                sub.left();
                if (this.filterItem != null) {
                    sub.image(this.filterItem.uiIcon).size(32.0F);
                    sub.label(() -> Strings.fixed(this.flowRate * 10.0F, 2) + "units" + ps).color(Color.lightGray);
                } else {
                    sub.labelWrap("No filter selected").color(Color.lightGray);
                }

            }).left();
        }

        public void updatePost() {
            float rate = 0.08F;
            GraphCrucibleModule dex = this.crucible();
            this.flowRate /= 2.0F;
            if (this.filterItem != null) {
                CrucibleGraph fromNet = (CrucibleGraph)dex.getNetworkFromSet(1);
                CrucibleGraph toNet = (CrucibleGraph)dex.getNetworkFromSet(0);
                if (fromNet != null && toNet != null) {
                    for(CrucibleData fnc : fromNet.contains()) {
                        if (fnc.item == this.filterItem) {
                            float transfer = Math.min(toNet.getRemainingSpace(), Math.min(rate * this.edelta(), fnc.volume * fnc.meltedRatio));
                            CrucibleData toG = toNet.getMeltFromID(fnc.id);
                            if (toG != null) {
                                transfer = Math.min(toNet.totalCapacity() * CruciblePump.fillAm[this.pumpMode] - toG.volume, transfer);
                            }

                            if (!(transfer <= 0.0F)) {
                                fromNet.addLiquidToSlot(fnc, -transfer);
                                toNet.addMeltItem(MeltInfo.all[fnc.id], transfer, true);
                                this.flowRate = transfer;
                            }
                            break;
                        }
                    }
                }
            }

            this.flowAnimation += this.flowRate * 0.4F;
        }

        public void draw() {
            Draw.rect(CruciblePump.this.bottomRegion, this.x, this.y);
            if (this.filterItem != null) {
                Draw.color(this.filterItem.color, Mathf.clamp(this.flowRate * 60.0F));
                UnityDrawf.drawSlideRect(CruciblePump.this.liquidRegion, this.x, this.y, 16.0F, 16.0F, 32.0F, 16.0F, this.rotdeg() + 180.0F, 16, this.flowAnimation);
                Draw.color();
            }

            UnityDrawf.drawHeat(CruciblePump.this.heatRegion, this.x, this.y, this.rotdeg(), this.heat().getTemp());
            Draw.rect(CruciblePump.this.topRegions[this.rotation], this.x, this.y);
            this.drawTeamTop();
        }

        public void writeExt(Writes write) {
            write.s(this.filterItem == null ? -1 : this.filterItem.id);
            write.b(this.pumpMode);
        }

        public void readExt(Reads read, byte revision) {
            this.filterItem = Vars.content.item(read.s());
            this.pumpMode = read.b();
        }

        public Integer config() {
            return this.pumpMode + (this.filterItem != null ? this.filterItem.id + 1 << 2 : 0);
        }
    }
}
