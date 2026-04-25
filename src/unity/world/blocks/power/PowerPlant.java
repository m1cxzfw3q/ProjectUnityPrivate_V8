package unity.world.blocks.power;

import arc.struct.Seq;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.type.ItemStack;
import mindustry.world.Tile;
import mindustry.world.blocks.power.PowerGenerator;

public class PowerPlant extends PowerGenerator {
    public int steps = 60;

    public PowerPlant(String name) {
        super(name);
        this.hasItems = true;
        this.acceptsItems = true;
        this.itemCapacity = 200;
    }

    public class PowerPlantBuilding extends Building {
        public Seq<ItemStack> itemSeq = new Seq();

        public Building init(Tile tile, Team team, boolean shouldAdd, int rotation) {
            return super.init(tile, team, shouldAdd, rotation);
        }
    }
}
