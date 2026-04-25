package unity.world.blocks.light;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.scene.ui.layout.Table;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import unity.gen.Float2;
import unity.gen.Light;
import unity.gen.LightHoldBlock;
import unity.world.LightAcceptorType;

public class LightReflector extends LightHoldBlock {
    private static final Vec2 v1 = new Vec2();
    private static final Vec2 v2 = new Vec2();
    public float fallthrough = 0.0F;
    public TextureRegion baseRegion;

    public LightReflector(String name) {
        super(name);
        this.solid = true;
        this.configurable = true;
        this.outlineIcon = true;
        this.acceptors.add(new LightAcceptorType() {
            {
                this.x = 0;
                this.y = 0;
                this.width = 1;
                this.height = 1;
                this.required = -1.0F;
            }
        });
        this.config(Boolean.class, (tile, value) -> tile.lightRot = Mathf.mod(tile.lightRot + (value ? 22.5F : -22.5F) / 2.0F, 360.0F));
    }

    public void load() {
        super.load();
        this.baseRegion = Core.atlas.find(this.name + "-base");
    }

    public float getRotation(Building build) {
        float var10000;
        if (build instanceof LightReflectorBuild) {
            LightReflectorBuild b = (LightReflectorBuild)build;
            var10000 = b.lightRot;
        } else {
            var10000 = 0.0F;
        }

        return var10000;
    }

    protected TextureRegion[] icons() {
        return new TextureRegion[]{this.baseRegion, this.region};
    }

    public class LightReflectorBuild extends LightHoldBlock.LightHoldBuild {
        public float lightRot = 90.0F;

        public LightReflectorBuild() {
            super(LightReflector.this);
        }

        public Float config() {
            return this.lightRot;
        }

        public void interact(Light light) {
            super.interact(light);
            light.child((l) -> {
                synchronized(LightReflector.class) {
                    LightReflector.v1.trnsExact(this.lightRot, 1.0F);
                    return Float2.construct(Light.fixRot(LightReflector.v2.trnsExact(l.rotation(), 1.0F).sub(LightReflector.v1.scl(2.0F * LightReflector.v2.dot(LightReflector.v1))).angle()), 1.0F - LightReflector.this.fallthrough);
                }
            });
            if (!Mathf.zero(LightReflector.this.fallthrough)) {
                light.child((l) -> Float2.construct(l.rotation(), LightReflector.this.fallthrough));
            }

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

        public void draw() {
            Draw.rect(LightReflector.this.baseRegion, this.x, this.y);
            Draw.rect(LightReflector.this.region, this.x, this.y, this.lightRot - 90.0F);
        }
    }
}
