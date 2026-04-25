package unity.world.blocks.defense;

import unity.gen.Float2;
import unity.gen.Light;
import unity.gen.LightHoldWall;
import unity.world.LightAcceptorType;

public class LightWall extends LightHoldWall {
    public float suppression = 0.8F;

    public LightWall(String name) {
        super(name);
    }

    public void init() {
        super.init();
        this.acceptors.add(new LightAcceptorType() {
            {
                this.x = 0;
                this.y = 0;
                this.width = LightWall.this.size;
                this.height = LightWall.this.size;
                this.required = -1.0F;
            }
        });
    }

    public class LightWallBuild extends LightHoldWall.LightHoldWallBuild {
        public LightWallBuild() {
            super(LightWall.this);
        }

        public void interact(Light light) {
            light.child((l) -> Float2.construct(l.rotation(), LightWall.this.suppression));
        }
    }
}
