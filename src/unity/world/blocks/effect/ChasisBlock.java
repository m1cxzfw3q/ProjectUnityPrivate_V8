package unity.world.blocks.effect;

import mindustry.gen.Building;
import unity.world.blocks.ConnectedBlock;
import unity.world.blocks.GraphBlock;

public class ChasisBlock extends GraphBlock {
    public ChasisBlock(String name) {
        super(name);
    }

    public class ChasisBlockBuild extends GraphBlock.GraphBuild implements ConnectedBlock {
        public ChasisBlockBuild() {
            super(ChasisBlock.this);
        }

        public void updatePre() {
            this.torque().setMotorForceMult(this.generateTorque());
        }

        protected float generateTorque() {
            return 1.0F;
        }

        public boolean isConnected(Building b) {
            return false;
        }
    }
}
