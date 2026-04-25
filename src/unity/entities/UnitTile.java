package unity.entities;

import arc.func.Prov;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;

public class UnitTile extends Tile {
    public UnitTile(int x, int y) {
        super(x, y);
    }

    public void setFloor(Floor type) {
        this.floor = type;
    }

    public void setBlockQuiet(Block block) {
        this.block = block;
    }

    protected void changeBuild(Team team, Prov<Building> entityprov, int rotation) {
        if (this.block.hasBuilding()) {
            this.build = (Building)entityprov.get();
            this.build.rotation = rotation;
            this.build.tile = this;
        }

    }

    protected void fireChanged() {
    }

    public void recache() {
    }

    public void recacheWall() {
    }
}
