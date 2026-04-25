package unity.world.blocks.effect;

import unity.gen.SoulBlock;

public class SoulContainer extends SoulBlock {
    public SoulContainer(String name) {
        super(name);
    }

    public class SoulContainerBuild extends SoulBlock.SoulBuild {
        public SoulContainerBuild() {
            super(SoulContainer.this);
        }
    }
}
