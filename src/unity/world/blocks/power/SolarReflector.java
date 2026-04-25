package unity.world.blocks.power;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.world.Block;

public class SolarReflector extends Block {
    public TextureRegion mirrorRegion;
    public TextureRegion baseRegion;

    public SolarReflector(String name) {
        super(name);
        this.solid = this.update = this.configurable = true;
        this.config(Point2.class, (build, point) -> build.setLink(Point2.pack(point.x + build.tileX(), point.y + build.tileY())));
        this.config(Integer.class, (build, point) -> build.setLink(point));
    }

    public void load() {
        super.load();
        this.mirrorRegion = Core.atlas.find(this.name + "-mirror");
        this.baseRegion = Core.atlas.find(this.name + "-base");
    }

    public class SolarReflectorBuild extends Building {
        float mirrorRot;
        int link = -1;
        boolean hasChanged;

        public void setLink(int s) {
            if (s != this.link) {
                if (this.link != -1) {
                    Building build = Vars.world.build(this.link);
                    if (build instanceof SolarCollector.SolarCollectorBuild) {
                        SolarCollector.SolarCollectorBuild b = (SolarCollector.SolarCollectorBuild)build;
                        b.removeReflector(this);
                    }
                }

                if (s != -1) {
                    this.hasChanged = true;
                }

                this.link = s;
            }
        }

        public void updateTile() {
            this.mirrorRot += 0.4F;
            Building build = Vars.world.build(this.link);
            if (this.linkValid()) {
                this.setLink(build.pos());
                this.mirrorRot = Mathf.slerpDelta(this.mirrorRot, this.tile.angleTo(build.tile), 0.05F);
                if (this.hasChanged) {
                    ((SolarCollector.SolarCollectorBuild)build).appendSolarReflector(this);
                    this.hasChanged = false;
                }
            }

        }

        public void draw() {
            Draw.rect(SolarReflector.this.baseRegion, this.x, this.y);
            Drawf.shadow(SolarReflector.this.mirrorRegion, this.x - (float)SolarReflector.this.size / 2.0F, this.y - (float)SolarReflector.this.size / 2.0F, this.mirrorRot);
            Draw.rect(SolarReflector.this.mirrorRegion, this.x, this.y, this.mirrorRot);
        }

        public void drawConfigure() {
            float sin = Mathf.absin(6.0F, 1.0F);
            if (this.linkValid()) {
                Building target = Vars.world.build(this.link);
                Drawf.circles(target.x, target.y, ((float)target.block.size / 2.0F + 1.0F) * 8.0F + sin - 2.0F, Pal.place);
                Drawf.arrow(this.x, this.y, target.x, target.y, (float)(SolarReflector.this.size * 8) + sin, 4.0F + sin);
            }

            Drawf.dashCircle(this.x, this.y, 100.0F, Pal.accent);
        }

        public boolean onConfigureTileTapped(Building other) {
            if (this == other) {
                this.configure(-1);
                return false;
            } else if (this.link == other.pos()) {
                this.configure(-1);
                return false;
            } else if (other instanceof SolarCollector.SolarCollectorBuild && other.dst(this.tile) <= 100.0F && other.team == this.team) {
                this.configure(other.pos());
                return false;
            } else {
                return true;
            }
        }

        public Point2 config() {
            return Point2.unpack(this.link).sub(this.tileX(), this.tileY());
        }

        boolean linkValid() {
            if (this.link == -1) {
                return false;
            } else {
                Building build = Vars.world.build(this.link);
                if (!(build instanceof SolarCollector.SolarCollectorBuild)) {
                    return false;
                } else {
                    return build.team == this.team && this.within(build, 100.0F);
                }
            }
        }

        public void write(Writes write) {
            super.write(write);
            write.i(this.link);
        }

        public void read(Reads read, byte revision) {
            super.read(read, revision);
            this.setLink(read.i());
        }

        public void onRemoved() {
            Building build = Vars.world.build(this.link);
            if (build instanceof SolarCollector.SolarCollectorBuild) {
                SolarCollector.SolarCollectorBuild b = (SolarCollector.SolarCollectorBuild)build;
                b.removeReflector(this);
            }

        }
    }
}
