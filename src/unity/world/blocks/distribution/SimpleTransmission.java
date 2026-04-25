package unity.world.blocks.distribution;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import unity.graphics.UnityDrawf;
import unity.world.blocks.GraphBlock;
import unity.world.modules.GraphTorqueModule;

public class SimpleTransmission extends GraphBlock {
    final TextureRegion[] topRegions = new TextureRegion[2];
    final TextureRegion[] overlayRegions = new TextureRegion[2];
    final TextureRegion[] movingRegions = new TextureRegion[3];
    TextureRegion bottomRegion;
    TextureRegion mbaseRegion;

    public SimpleTransmission(String name) {
        super(name);
        this.rotate = this.solid = true;
    }

    public void load() {
        super.load();

        for(int i = 0; i < 2; ++i) {
            this.topRegions[i] = Core.atlas.find(this.name + "-top" + (i + 1));
            this.overlayRegions[i] = Core.atlas.find(this.name + "-overlay" + (i + 1));
        }

        for(int i = 0; i < 3; ++i) {
            this.movingRegions[i] = Core.atlas.find(this.name + "-moving" + (i + 1));
        }

        this.bottomRegion = Core.atlas.find(this.name + "-bottom");
        this.mbaseRegion = Core.atlas.find(this.name + "-mbase");
    }

    public class SimpleTransmissionBuild extends GraphBlock.GraphBuild {
        public SimpleTransmissionBuild() {
            super(SimpleTransmission.this);
        }

        public void draw() {
            GraphTorqueModule<?> torqueGraph = this.torque();
            float graphRot0 = torqueGraph.getRotationOf(0);
            float graphRot1 = torqueGraph.getRotationOf(1);
            float fixedRot = (this.rotdeg() + 90.0F) % 180.0F - 90.0F;
            int variant = (this.rotation + 1) % 4 >= 2 ? 1 : 0;
            Draw.rect(SimpleTransmission.this.bottomRegion, this.x, this.y);
            Draw.rect(SimpleTransmission.this.mbaseRegion, this.x, this.y, this.rotdeg());
            Point2 offset = Geometry.d4(this.rotation + 1);
            float ox = (float)offset.x * 4.0F;
            float oy = (float)offset.y * 4.0F;
            UnityDrawf.drawRotRect(SimpleTransmission.this.movingRegions[0], this.x + ox, this.y + oy, 16.0F, 4.5F, 4.5F, fixedRot, graphRot0, graphRot0 + 180.0F);
            UnityDrawf.drawRotRect(SimpleTransmission.this.movingRegions[0], this.x + ox, this.y + oy, 16.0F, 4.5F, 4.5F, fixedRot, graphRot0 + 180.0F, graphRot0 + 360.0F);
            UnityDrawf.drawRotRect(SimpleTransmission.this.movingRegions[1], this.x + ox * -0.125F, this.y + oy * -0.125F, 16.0F, 4.5F, 4.5F, fixedRot, 360.0F - graphRot0, 180.0F - graphRot0);
            UnityDrawf.drawRotRect(SimpleTransmission.this.movingRegions[1], this.x + ox * -0.125F, this.y + oy * -0.125F, 16.0F, 4.5F, 4.5F, fixedRot, 540.0F - graphRot0, 360.0F - graphRot0);
            UnityDrawf.drawRotRect(SimpleTransmission.this.movingRegions[2], this.x - ox, this.y - oy, 16.0F, 2.5F, 2.5F, fixedRot, graphRot1, graphRot1 + 180.0F);
            UnityDrawf.drawRotRect(SimpleTransmission.this.movingRegions[2], this.x - ox, this.y - oy, 16.0F, 2.5F, 2.5F, fixedRot, graphRot1 + 180.0F, graphRot1 + 360.0F);
            Draw.rect(SimpleTransmission.this.overlayRegions[variant], this.x, this.y, this.rotdeg());
            Draw.rect(SimpleTransmission.this.topRegions[this.rotation % 2], this.x, this.y);
            this.drawTeamTop();
        }
    }
}
