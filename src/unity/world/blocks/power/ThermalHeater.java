package unity.world.blocks.power;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import mindustry.game.Team;
import mindustry.world.Tile;
import mindustry.world.meta.Attribute;
import unity.graphics.UnityDrawf;

public class ThermalHeater extends HeatGenerator {
    public final TextureRegion[] regions = new TextureRegion[4];
    public final Attribute attri;

    public ThermalHeater(String name) {
        super(name);
        this.attri = Attribute.heat;
        this.rotate = true;
    }

    public void load() {
        super.load();

        for(int i = 0; i < 4; ++i) {
            this.regions[i] = Core.atlas.find(this.name + (i + 1));
        }

    }

    public boolean canPlaceOn(Tile tile, Team team) {
        return tile.getLinkedTilesAs(this, tempTiles).sumf((other) -> other.floor().attributes.get(this.attri)) > 0.01F;
    }

    public class ThermalHeaterBuild extends HeatGenerator.HeatGeneratorBuild {
        public float sum;

        public ThermalHeaterBuild() {
            super(ThermalHeater.this);
        }

        public void updatePost() {
            this.generateHeat(this.sum + ThermalHeater.this.attri.env());
        }

        public void draw() {
            Draw.rect(ThermalHeater.this.regions[this.rotation], this.x, this.y);
            UnityDrawf.drawHeat(ThermalHeater.this.heatRegion, this.x, this.y, this.rotdeg(), this.heat().getTemp());
            this.drawTeamTop();
        }

        public void onProximityAdded() {
            super.onProximityAdded();
            this.sum = ThermalHeater.this.sumAttribute(ThermalHeater.this.attri, this.tileX(), this.tileY());
        }
    }
}
