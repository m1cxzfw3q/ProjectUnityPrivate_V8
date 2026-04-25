package unity.world.blocks.distribution;

import mindustry.world.blocks.distribution.Conveyor;

public class KoruhConveyor extends Conveyor {
    protected float realSpeed;
    protected float drawMultiplier;

    public KoruhConveyor(String name) {
        super(name);
        this.absorbLasers = true;
    }

    public void load() {
        super.load();
        this.realSpeed = this.speed;
    }

    public class KoruhConveyorBuild extends Conveyor.ConveyorBuild {
        public KoruhConveyorBuild() {
            super(KoruhConveyor.this);
        }

        public void draw() {
            KoruhConveyor.this.speed = KoruhConveyor.this.realSpeed * KoruhConveyor.this.drawMultiplier;
            super.draw();
            KoruhConveyor.this.speed = KoruhConveyor.this.realSpeed;
        }
    }
}
