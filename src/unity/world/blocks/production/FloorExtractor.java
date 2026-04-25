package unity.world.blocks.production;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.scene.Element;
import arc.scene.ui.Image;
import arc.scene.ui.layout.Table;
import arc.struct.IntFloatMap;
import arc.struct.Seq;
import arc.util.Scaling;
import mindustry.Vars;
import mindustry.ctype.ContentType;
import mindustry.game.Team;
import mindustry.type.Item;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.meta.Stat;

public class FloorExtractor extends GenericCrafter {
    private static final Seq<Tile> source = new Seq();
    /** @deprecated */
    public IntFloatMap sources = new IntFloatMap();

    public FloorExtractor(String name) {
        super(name);
    }

    public void setup(Object... arr) {
        if (arr.length % 2 > 0) {
            throw new IllegalArgumentException("map must be [Block, float, Block, float, ...]");
        } else {
            for(int i = 0; i < arr.length; i += 2) {
                Block block = (Block)arr[i];
                float val = (Float)arr[i + 1];
                this.sources.put(block.id, val);
            }

        }
    }

    public void setStats() {
        super.setStats();
        Seq<Block> blocks = new Seq();

        for(int id : this.sources.keys().toArray().items) {
            blocks.add((Block)Vars.content.getByID(ContentType.block, id));
        }

        blocks.sort((block) -> this.sources.get(block.id, 0.0F));

        for(Block block : blocks) {
            this.stats.add(Stat.tiles, (table) -> table.stack(new Element[]{new Image(block.uiIcon) {
                {
                    this.setSize(32.0F);
                    this.setScaling(Scaling.fit);
                }
            }, new Table((cont) -> cont.top().right().add("[accent]" + (int)(this.sources.get(block.id) * 100.0F) + "%").style(Styles.outlineLabel))}));
        }

    }

    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);
        Tile tile = Vars.world.tile(x, y);
        Item item = this.outputItem != null ? this.outputItem.item : null;
        float width = this.drawPlaceText(Core.bundle.formatFloat("bar.extractspeed", 60.0F / this.craftTime * (this.count(tile) / (float)this.size), 2), x, y, valid);
        float dx = (float)(x * 8) + this.offset - width / 2.0F - 4.0F;
        float dy = (float)(y * 8) + this.offset + (float)(this.size * 8) / 2.0F + 5.0F;
        if (item != null) {
            float s = 6.0F;
            Draw.mixcol(Color.darkGray, 1.0F);
            Draw.rect(item.uiIcon, dx, dy - 1.0F, s, s);
            Draw.reset();
            Draw.rect(item.uiIcon, dx, dy, s, s);
        }

    }

    public boolean canPlaceOn(Tile tile, Team team) {
        return super.canPlaceOn(tile, team) && this.count(tile) > 0.0F;
    }

    public float count(Tile tile) {
        return tile == null ? 0.0F : tile.getLinkedTilesAs(this, source).sumf((t) -> this.sources.get(t.floorID(), this.sources.get(t.overlayID(), 0.0F)));
    }

    public class FloorExtractorBuild extends GenericCrafter.GenericCrafterBuild {
        public FloorExtractorBuild() {
            super(FloorExtractor.this);
        }

        public float getProgressIncrease(float baseTime) {
            float incr = super.getProgressIncrease(baseTime);
            return incr * (FloorExtractor.this.count(this.tile) / (float)FloorExtractor.this.size);
        }
    }
}
