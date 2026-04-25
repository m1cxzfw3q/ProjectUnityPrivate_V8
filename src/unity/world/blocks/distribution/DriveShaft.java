package unity.world.blocks.distribution;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import unity.graphics.UnityDrawf;
import unity.world.blocks.GraphBlock;

public class DriveShaft extends GraphBlock {
    final TextureRegion[] baseRegions = new TextureRegion[4];
    TextureRegion topRegion;
    TextureRegion overlayRegion;
    TextureRegion movingRegion;

    public DriveShaft(String name) {
        super(name);
        this.rotate = true;
    }

    public void load() {
        super.load();
        this.topRegion = Core.atlas.find(this.name + "-top");
        this.overlayRegion = Core.atlas.find(this.name + "-overlay");
        this.movingRegion = Core.atlas.find(this.name + "-moving");

        for(int i = 0; i < 4; ++i) {
            this.baseRegions[i] = Core.atlas.find(this.name + "-base" + (i + 1));
        }

    }

    public class DriveShaftBuild extends GraphBlock.GraphBuild {
        int baseSpriteIndex;

        public DriveShaftBuild() {
            super(DriveShaft.this);
        }

        public void onNeighboursChanged() {
            this.baseSpriteIndex = 0;
            this.torque().eachNeighbourValue((n) -> {
                if (this.rotation != 1 && this.rotation != 2) {
                    this.baseSpriteIndex += n.equals(0) ? 1 : 2;
                } else {
                    this.baseSpriteIndex += n.equals(0) ? 2 : 1;
                }

            });
        }

        public void draw() {
            float graphRot = this.torque().getRotation();
            float fixedRot = (this.rotdeg() + 90.0F) % 180.0F - 90.0F;
            Draw.rect(DriveShaft.this.baseRegions[this.baseSpriteIndex], this.x, this.y, fixedRot);
            UnityDrawf.drawRotRect(DriveShaft.this.movingRegion, this.x, this.y, 8.0F, 3.5F, 6.0F, fixedRot, graphRot, graphRot + 90.0F);
            Draw.rect(DriveShaft.this.overlayRegion, this.x, this.y, fixedRot);
            Draw.rect(DriveShaft.this.topRegion, this.x, this.y, fixedRot);
        }
    }
}
