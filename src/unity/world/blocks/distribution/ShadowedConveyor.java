package unity.world.blocks.distribution;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import mindustry.gen.Building;
import mindustry.world.Tile;
import mindustry.world.blocks.distribution.Conveyor;

public class ShadowedConveyor extends Conveyor {
    TextureRegion shadowRegion;

    public ShadowedConveyor(String name) {
        super(name);
    }

    public void load() {
        super.load();
        this.shadowRegion = Core.atlas.find(this.name + "-shadow");
    }

    public class ShadowedConveyorBuild extends Conveyor.ConveyorBuild {
        boolean looking;

        public ShadowedConveyorBuild() {
            super(ShadowedConveyor.this);
        }

        public void draw() {
            super.draw();
            Draw.z(30.0F);
            if (this.nextc == null || this.block != this.nextc.block) {
                Draw.rect(ShadowedConveyor.this.shadowRegion, this.x, this.y, this.rotdeg());
            }

            if (!this.looking) {
                Draw.rect(ShadowedConveyor.this.shadowRegion, this.x, this.y, this.rotdeg() + 180.0F);
            }

        }

        public void onProximityUpdate() {
            super.onProximityUpdate();
            Building backBuilding = this.back();
            Building leftBuilding = this.left();
            Building rightBuilding = this.right();
            Tile back = backBuilding != null ? backBuilding.tile : this.tile;
            Tile left = leftBuilding != null ? leftBuilding.tile : this.tile;
            Tile right = rightBuilding != null ? rightBuilding.tile : this.tile;
            this.looking = back.relativeTo(this.tile) - back.build.rotation == 0 && back.build.block == this.block || left.relativeTo(this.tile) - left.build.rotation == 0 && left.build.block == this.block || right.relativeTo(this.tile) - right.build.rotation == 0 && right.build.block == this.block;
        }
    }
}
