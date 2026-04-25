package unity.world.blocks.distribution;

import arc.Core;
import arc.graphics.g2d.TextureRegion;
import arc.scene.ui.ScrollPane;
import arc.scene.ui.layout.Table;
import arc.util.Time;
import mindustry.gen.Building;
import mindustry.gen.Player;
import mindustry.gen.Tex;
import mindustry.graphics.Pal;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.world.Block;
import unity.content.UnderworldBlocks;
import unity.type.UnderworldBlock;
import unity.ui.UnderworldMap;

public class UnderPiper extends Block {
    private TextureRegion pencil;
    private TextureRegion eraser;
    private TextureRegion move;

    public UnderPiper(String name, int itemCapacity) {
        super(name);
        this.configurable = true;
        this.hasItems = true;
        this.acceptsItems = true;
        this.solid = true;
        this.update = true;
        this.itemCapacity = itemCapacity;
    }

    public void load() {
        super.load();
        this.pencil = Core.atlas.find("unity-pencil");
        this.eraser = Core.atlas.find("unity-eraser");
        this.move = Core.atlas.find("unity-move");
        Time.runTask(30.0F, UnderworldBlocks::load);
    }

    public class UnderPiperBuild extends Building {
        public BaseDialog piping;

        public boolean shouldHideConfigure(Player player) {
            return true;
        }

        public void drawConfigure() {
        }

        public void buildConfiguration(Table table) {
            if (this.piping == null) {
                this.piping = new BaseDialog(this.block.localizedName);
                this.piping.addCloseListener();
            }

            this.piping.cont.clear();
            this.piping.cont.center().top();
            this.piping.cont.table((t) -> {
                t.center().bottom();
                ScrollPane pane = (ScrollPane)t.pane(Styles.nonePane, (p) -> {
                    UnderworldMap map = new UnderworldMap();
                    p.add(map).grow();
                }).fill().padRight(5.0F).get();
                pane.setScrollX((float)(this.tile.x + 2) * 32.0F - 16.0F);
                pane.setScrollY((float)(this.tile.y + 2) * 32.0F - 16.0F);
                t.table((tl) -> {
                    tl.center().top();
                    tl.table(Tex.pane, (tt) -> {
                        tt.center().top();
                        tt.labelWrap(Core.bundle.get("block.unity-underpiper.info")).labelAlign(1).marginBottom(5.0F).growX();
                        tt.row();
                        tt.image().color(Pal.accent).height(4.0F).growX().marginLeft(5.0F).marginRight(5.0F).growX();
                        tt.row();
                        tt.labelWrap("Planet: ???").growX().labelAlign(1);
                    }).padBottom(5.0F).grow();
                    tl.row();
                    tl.table(Tex.pane, (tt) -> {
                        tt.center().top();
                        tt.labelWrap(Core.bundle.get("block.unity-underpiper.blocks")).labelAlign(1).marginBottom(5.0F).growX();
                        tt.row();
                        tt.image().color(Pal.accent).height(4.0F).growX().marginLeft(5.0F).marginRight(5.0F).growX();
                        tt.row();
                        tt.pane((p) -> {
                            p.center().top();

                            for(int i = 0; i < UnderworldBlocks.blocks.size; ++i) {
                                UnderworldBlock bloc = (UnderworldBlock)UnderworldBlocks.blocks.get(i);
                                p.button((b) -> b.image(bloc.region).size(32.0F), () -> {
                                }).size(34.0F).pad(2.0F).style(Styles.clearTransi).tooltip(bloc.localizedName);
                                if ((i + 1) % 4 == 0) {
                                    p.row();
                                }
                            }

                        }).grow();
                        tt.row();
                        tt.image().color(Pal.accent).height(4.0F).growX().marginLeft(5.0F).marginRight(5.0F).padBottom(2.0F).growX();
                        tt.row();
                        tt.table((ttt) -> {
                            ttt.left();
                            ttt.button((b) -> b.image(UnderPiper.this.pencil), Styles.cleari, () -> {
                            }).size(32.0F).padRight(5.0F);
                            ttt.button((b) -> b.image(UnderPiper.this.eraser), Styles.cleari, () -> {
                            }).size(32.0F).padRight(5.0F);
                            ttt.button((b) -> b.image(UnderPiper.this.move), Styles.cleari, () -> {
                            }).size(32.0F);
                        }).growX();
                    }).height(240.0F).growX();
                }).growY().width(205.0F);
            }).grow();
            this.piping.show();
        }
    }
}
