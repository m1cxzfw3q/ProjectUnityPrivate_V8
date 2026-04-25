package unity.world.blocks.production;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.scene.style.Drawable;
import arc.scene.ui.ImageButton;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.gen.Unit;
import mindustry.type.Item;
import mindustry.ui.Styles;
import unity.gen.SVec2;
import unity.graphics.UnityDrawf;
import unity.graphics.UnityPal;
import unity.ui.dialogs.CrucibleDialog;
import unity.util.GraphicUtils;
import unity.world.blocks.GraphBlock;
import unity.world.graph.CrucibleGraph;
import unity.world.meta.CrucibleData;
import unity.world.meta.MeltInfo;
import unity.world.modules.GraphCrucibleModule;

public class Crucible extends GraphBlock {
    CrucibleGraph viewPos;
    private static final long[] randomPos = new long[]{SVec2.construct(0.0F, 0.0F), SVec2.construct(-1.6F, 1.6F), SVec2.construct(-1.6F, -1.6F), SVec2.construct(1.6F, -1.6F), SVec2.construct(-1.6F, -1.6F), SVec2.construct(0.0F, 0.0F)};
    public TextureRegion[] liquidRegions;
    public TextureRegion[] baseRegions;
    public TextureRegion[] roofRegions;
    public TextureRegion[] solidItemStrips;
    public TextureRegion[] heatRegions;
    public TextureRegion floorRegion;
    public TextureRegion solidItem;

    public Crucible(String name) {
        super(name);
        this.configurable = this.solid = true;
    }

    public void load() {
        super.load();
        this.liquidRegions = GraphicUtils.getRegions(this.liquidRegion, 12, 4);
        this.baseRegions = GraphicUtils.getRegions(Core.atlas.find(this.name + "-base"), 12, 4);
        this.floorRegion = Core.atlas.find(this.name + "-floor");
        this.roofRegions = GraphicUtils.getRegions(Core.atlas.find(this.name + "-roof"), 12, 4);
        this.solidItem = Core.atlas.find(this.name + "-solid");
        this.solidItemStrips = GraphicUtils.getRegions(Core.atlas.find(this.name + "-solidstrip"), 6, 1);
        this.heatRegions = GraphicUtils.getRegions(this.heatRegion, 12, 4);
    }

    public class CrucibleBuild extends GraphBlock.GraphBuild {
        final Color color;

        public CrucibleBuild() {
            super(Crucible.this);
            this.color = Color.clear.cpy();
        }

        public void buildConfiguration(Table table) {
            Drawable var10001 = Tex.whiteui;
            ImageButton.ImageButtonStyle var10002 = Styles.clearTransi;
            CrucibleDialog var10004 = new CrucibleDialog(this);
            ((ImageButton)table.button(var10001, var10002, 50.0F, var10004::show).size(50.0F).get()).getStyle().imageUp = Icon.chartBar;
            ((ImageButton)table.button(Tex.whiteui, Styles.clearTransi, 50.0F, () -> this.configure(0)).size(50.0F).get()).getStyle().imageUp = Icon.eye;
        }

        public void configured(Unit builder, Object value) {
            CrucibleGraph thisG = (CrucibleGraph)this.crucible().getNetwork();
            Crucible.this.viewPos = Crucible.this.viewPos == thisG ? null : thisG;
        }

        public void drawConfigure() {
        }

        public void draw() {
            GraphCrucibleModule dex = this.crucible();
            byte tileIndex = UnityDrawf.tileMap[dex.tilingIndex];
            if (Crucible.this.viewPos == dex.getNetwork()) {
                Draw.rect(Crucible.this.floorRegion, this.x, this.y, 8.0F, 8.0F);
                this.drawContents(dex, tileIndex);
                Draw.rect(Crucible.this.baseRegions[tileIndex], this.x, this.y, 8.0F, 8.0F, 4.0F, 4.0F, 0.0F);
                UnityDrawf.drawHeat(Crucible.this.heatRegions[tileIndex], this.x, this.y, 0.0F, this.heat().getTemp());
            } else {
                Draw.rect(Crucible.this.roofRegions[tileIndex], this.x, this.y, 8.0F, 8.0F, 4.0F, 4.0F, 0.0F);
            }

            this.drawTeamTop();
        }

        public boolean acceptItem(Building source, Item item) {
            return this.crucible().canContainMore(1.0F) && MeltInfo.map.containsKey(item);
        }

        public void handleItem(Building source, Item item) {
            this.crucible().addItem(item);
        }

        protected void drawContents(GraphCrucibleModule crucGraph, int tIndex) {
            this.color.set(0.0F, 0.0F, 0.0F);
            Seq<CrucibleData> cc = crucGraph.getContained();
            if (!cc.isEmpty()) {
                float tLiquid = 0.0F;
                float fraction = crucGraph.liquidCap / crucGraph.getTotalLiquidCapacity();

                for(CrucibleData i : cc) {
                    if (i.meltedRatio > 0.0F) {
                        float liquidVol = i.meltedRatio * i.volume;
                        tLiquid += liquidVol;
                        Color itemCol = UnityPal.youngchaGray;
                        if (i.item != null) {
                            itemCol = i.item.color;
                        }

                        Color var10000 = this.color;
                        var10000.r += itemCol.r * liquidVol;
                        var10000 = this.color;
                        var10000.g += itemCol.g * liquidVol;
                        var10000 = this.color;
                        var10000.b += itemCol.b * liquidVol;
                    }
                }

                if (tLiquid > 0.0F) {
                    float invt = 1.0F / tLiquid;
                    Draw.color(this.color.mul(invt), Mathf.clamp(tLiquid * fraction * 2.0F));
                    Draw.rect(Crucible.this.liquidRegions[tIndex], this.x, this.y, 8.0F, 8.0F);
                }

                for(CrucibleData i : cc) {
                    if (i.meltedRatio < 1.0F && i.volume * fraction > 0.1F) {
                        Color itemCol = UnityPal.youngchaGray;
                        if (i.item != null) {
                            itemCol = i.item.color;
                        }

                        float ddd = (1.0F - i.meltedRatio) * i.volume * fraction;
                        if (ddd > 0.1F) {
                            Draw.color(itemCol);
                            if (ddd > 1.0F) {
                                Draw.rect(Crucible.this.solidItemStrips[Mathf.floor(ddd) - 1], this.x, this.y);
                            }

                            float siz = 8.0F * (ddd % 1.0F);
                            long pos = Crucible.randomPos[Math.max(Mathf.floor(ddd), 5)];
                            Draw.rect(Crucible.this.solidItem, SVec2.x(pos) + this.x, SVec2.y(pos) + this.y, siz, siz);
                        }
                    }
                }

                Draw.color();
            }
        }
    }
}
