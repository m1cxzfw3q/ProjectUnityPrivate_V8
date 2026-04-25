package unity.world.draw;

import mindustry.gen.Building;
import mindustry.world.Block;
import unity.world.blocks.exp.LevelHolder;

public class DrawLevel {
    public <T extends Building & LevelHolder> void draw(T build) {
    }

    public <T extends Building & LevelHolder> void drawLight(T build) {
    }

    public void load(Block block) {
    }
}
