package unity.world.blocks.light;

import unity.gen.Bool3;
import unity.gen.LightHoldBlock;

public class LightRouter extends LightHoldBlock {
    public LightRouter(String name) {
        super(name);
        this.rotate = true;
    }

    public class LightRouterBuild extends LightHoldBlock.LightHoldBuild {
        public byte config = Bool3.construct(true, true, true);

        public LightRouterBuild() {
            super(LightRouter.this);
        }

        public Byte config() {
            return this.config;
        }
    }
}
