package unity.type;

import mindustry.world.Block;

public class ExpUpgrade {
    public final Block type;
    public int index;
    public int min = 1;
    public int max;
    public boolean hide = false;

    public ExpUpgrade(Block type) {
        this.type = type;
    }
}
