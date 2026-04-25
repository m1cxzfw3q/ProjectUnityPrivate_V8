package unity.world.blocks;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.scene.Element;
import arc.scene.ui.ScrollPane;
import arc.scene.ui.layout.Table;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.Building;
import mindustry.gen.Tex;
import mindustry.graphics.Drawf;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.meta.BlockGroup;
import unity.mod.Faction;

public class LoreMessageBlock extends Block {
    public final Faction faction;
    public final Color color;
    public final Color lightColor;
    public TextureRegion topRegion;

    public LoreMessageBlock(String name, Faction faction) {
        super(name);
        this.faction = faction;
        this.color = faction.color;
        this.lightColor = this.color.cpy().mul(1.2F);
        this.size = 1;
        this.health = Integer.MAX_VALUE;
        this.configurable = true;
        this.solid = false;
        this.destructible = true;
        this.group = BlockGroup.logic;
        this.drawDisabled = false;
    }

    public void load() {
        super.load();
        this.region = Core.atlas.find("unity-lore-message");
        this.topRegion = Core.atlas.find("unity-lore-message-top");
    }

    protected TextureRegion[] icons() {
        return new TextureRegion[]{this.region, this.topRegion};
    }

    public void loadIcon() {
        this.fullIcon = Core.atlas.find("unity-lore-message");
        this.uiIcon = Core.atlas.find("unity-lore-message-ui", this.fullIcon);
    }

    public class LoreMessageBuild extends Building {
        private String message;
        private boolean messageSet;

        public void draw() {
            Draw.rect(LoreMessageBlock.this.region, this.x, this.y);
            Draw.color(LoreMessageBlock.this.color, LoreMessageBlock.this.lightColor, Mathf.absin(4.0F, 1.0F));
            Draw.rect(LoreMessageBlock.this.topRegion, this.x, this.y);
            Draw.color();
        }

        public void setMessage(String message) {
            if (!this.messageSet) {
                this.message = message;
                this.messageSet = true;
            } else {
                throw new IllegalArgumentException("Lore message already set!");
            }
        }

        public void drawLight() {
            Drawf.light(this.team, this, 32.0F, LoreMessageBlock.this.color, 0.5F);
        }

        public void buildConfiguration(Table table) {
            table.table(Styles.black6, (cont) -> {
                cont.add("@lore.unity.message", LoreMessageBlock.this.lightColor).align(1);
                cont.row();
                cont.image(Tex.whiteui, LoreMessageBlock.this.color).growX().height(3.0F).pad(6.0F);
                cont.row();
                ScrollPane scrl = (ScrollPane)cont.pane(Styles.defaultPane, (pane) -> {
                    pane.setBackground(Tex.scroll);
                    pane.labelWrap(() -> Core.bundle.get(this.message, "...")).grow().pad(6.0F);
                }).update((p) -> {
                    if (p.hasScroll()) {
                        Element result = Core.scene.hit((float)Core.input.mouseX(), (float)Core.input.mouseY(), true);
                        if (result == null || !result.isDescendantOf(p)) {
                            Core.scene.setScrollFocus((Element)null);
                        }
                    }

                }).grow().pad(6.0F).get();
                scrl.setScrollingDisabled(true, false);
                scrl.setOverscroll(false, false);
            }).size(300.0F, 200.0F);
        }

        public void write(Writes write) {
            super.write(write);
            write.str(this.message);
            write.bool(this.messageSet);
        }

        public void read(Reads read, byte revision) {
            super.read(read, revision);
            this.message = read.str();
            this.messageSet = read.bool();
        }
    }
}
