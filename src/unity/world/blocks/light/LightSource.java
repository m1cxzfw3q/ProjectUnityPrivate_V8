package unity.world.blocks.light;

import arc.scene.ui.layout.Table;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import unity.gen.Light;
import unity.gen.LightHoldGenericCrafter;
import unity.gen.SVec2;

public class LightSource extends LightHoldGenericCrafter {
    public float lightProduction = 1.0F;

    public LightSource(String name) {
        super(name);
        this.solid = true;
        this.configurable = true;
        this.outlineIcon = true;
        this.config(Boolean.class, (tile, value) -> tile.lightRot = Light.fixRot(tile.lightRot + (value ? 22.5F : -22.5F)));
    }

    public float getRotation(Building build) {
        float var10000;
        if (build instanceof LightSourceBuild) {
            LightSourceBuild b = (LightSourceBuild)build;
            var10000 = b.lightRot;
        } else {
            var10000 = 0.0F;
        }

        return var10000;
    }

    public class LightSourceBuild extends LightHoldGenericCrafter.LightHoldGenericCrafterBuild {
        public Light light;
        public float lightRot = 90.0F;

        public LightSourceBuild() {
            super(LightSource.this);
        }

        public Float config() {
            return this.lightRot;
        }

        public void created() {
            super.created();
            this.light = Light.create();
            this.light.queuePosition = SVec2.construct(this.x, this.y);
            this.light.queueRotation = this.lightRot;
            this.light.queueSource = this;
            this.light.queueAdd();
        }

        public void onRemoved() {
            this.light.queueRemove();
        }

        public void updateTile() {
            super.updateTile();
            this.light.queuePosition = SVec2.construct(this.x, this.y);
            this.light.queueRotation = this.lightRot;
            this.light.queueSource = this;
            this.light.queueStrength = this.efficiency() * LightSource.this.lightProduction;
        }

        public void buildConfiguration(Table table) {
            table.button(Icon.left, () -> this.configure(true)).size(40.0F);
            table.button(Icon.right, () -> this.configure(false)).size(40.0F);
        }

        public void write(Writes write) {
            super.write(write);
            write.f(this.lightRot);
        }

        public void read(Reads read, byte revision) {
            super.read(read, revision);
            this.lightRot = read.f();
        }
    }
}
