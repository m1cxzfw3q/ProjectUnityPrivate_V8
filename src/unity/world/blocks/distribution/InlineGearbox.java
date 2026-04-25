package unity.world.blocks.distribution;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import unity.graphics.UnityDrawf;
import unity.world.blocks.GraphBlock;

public class InlineGearbox extends GraphBlock {
    TextureRegion topRegion;
    TextureRegion overlayRegion;
    TextureRegion movingRegion;
    TextureRegion baseRegion;
    TextureRegion mbaseRegion;
    TextureRegion gearRegion;

    public InlineGearbox(String name) {
        super(name);
        this.rotate = this.solid = true;
    }

    public void load() {
        super.load();
        this.topRegion = Core.atlas.find(this.name + "-top");
        this.overlayRegion = Core.atlas.find(this.name + "-overlay");
        this.movingRegion = Core.atlas.find(this.name + "-moving");
        this.baseRegion = Core.atlas.find(this.name + "-base");
        this.mbaseRegion = Core.atlas.find(this.name + "-mbase");
        this.gearRegion = Core.atlas.find(this.name + "-gear");
    }

    public class InlineGearboxBuild extends GraphBlock.GraphBuild {
        public InlineGearboxBuild() {
            super(InlineGearbox.this);
        }

        public void draw() {
            float shaftRot = this.torque().getRotation();
            float fixedRot = (this.rotdeg() + 90.0F) % 180.0F - 90.0F;
            Draw.rect(InlineGearbox.this.baseRegion, this.x, this.y);
            Draw.rect(InlineGearbox.this.mbaseRegion, this.x, this.y, fixedRot);
            Point2 offset = Geometry.d4(this.rotation + 1);
            float ox = (float)offset.x * 4.0F;
            float oy = (float)offset.y * 4.0F;
            UnityDrawf.drawRotRect(InlineGearbox.this.movingRegion, this.x + ox, this.y + oy, 16.0F, 3.5F, 8.0F, fixedRot, shaftRot, shaftRot + 90.0F);
            UnityDrawf.drawRotRect(InlineGearbox.this.movingRegion, this.x - ox, this.y - oy, 16.0F, 3.5F, 8.0F, fixedRot, shaftRot + 90.0F, shaftRot + 180.0F);
            Draw.rect(InlineGearbox.this.gearRegion, this.x + 2.0F, this.y + 2.0F, shaftRot);
            Draw.rect(InlineGearbox.this.gearRegion, this.x - 2.0F, this.y + 2.0F, -shaftRot);
            Draw.rect(InlineGearbox.this.gearRegion, this.x + 2.0F, this.y - 2.0F, -shaftRot);
            Draw.rect(InlineGearbox.this.gearRegion, this.x - 2.0F, this.y - 2.0F, shaftRot);
            Draw.rect(InlineGearbox.this.overlayRegion, this.x, this.y, fixedRot);
            Draw.rect(InlineGearbox.this.topRegion, this.x, this.y, fixedRot);
            this.drawTeamTop();
        }
    }
}
