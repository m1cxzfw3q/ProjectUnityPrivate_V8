package unity.world.blocks.power;

import unity.world.blocks.GraphBlock;

public class TorqueGenerator extends GraphBlock {
    public TorqueGenerator(String name) {
        super(name);
        this.rotate = true;
    }

    public class TorqueGeneratorBuild extends GraphBlock.GraphBuild {
        public TorqueGeneratorBuild() {
            super(TorqueGenerator.this);
        }

        public void updatePre() {
            this.torque().setMotorForceMult(this.generateTorque());
        }

        protected float generateTorque() {
            return 1.0F;
        }
    }
}
