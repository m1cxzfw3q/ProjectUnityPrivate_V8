package unity.world.blocks.light;

import unity.gen.LightHoldBlock;

public class LightDiffractor extends LightHoldBlock {
    public int diffractionCount = 3;
    public float minAngle = 22.5F;
    public float maxAngle = 90.0F;

    public LightDiffractor(String name) {
        super(name);
        this.solid = true;
        this.configurable = true;
    }

    public class LightDiffractorBuild extends LightHoldBlock.LightHoldBuild {
        public LightDiffractorBuild() {
            super(LightDiffractor.this);
        }
    }
}
