package unity.world.blocks.environment;

import mindustry.type.Item;
import mindustry.world.blocks.environment.OreBlock;

public class UnityOreBlock extends OreBlock {
    public UnityOreBlock(Item ore) {
        super(ore.name.replaceFirst("unity-", ""));
        this.useColor = true;
        this.setup(ore);
    }
}
