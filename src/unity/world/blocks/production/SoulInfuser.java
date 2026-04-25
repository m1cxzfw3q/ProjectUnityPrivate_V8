package unity.world.blocks.production;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.geom.Intersector;
import arc.struct.IntSeq;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import unity.gen.SoulFloorExtractor;
import unity.world.blocks.effect.SoulContainer;

public class SoulInfuser extends SoulFloorExtractor {
    public int amount = 1;
    public int maxContainers = 3;
    public float range = 15.0F;

    public SoulInfuser(String name) {
        super(name);
        this.configurable = true;
        this.outputItem = null;
        this.outputLiquid = null;
        this.config(Integer.class, (build, value) -> {
            if (build.containers.contains(value)) {
                build.containers.removeValue(value);
            } else if (build.containers.size < this.maxContainers) {
                build.containers.add(value);
            }

        });
    }

    public boolean outputsItems() {
        return false;
    }

    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);
        Drawf.dashCircle((float)(x * 8) + this.offset, (float)(y * 8) + this.offset, this.range * 8.0F, Pal.accent);
    }

    public class SoulInfuserBuild extends SoulFloorExtractor.SoulFloorExtractorBuild {
        public IntSeq containers = new IntSeq();

        public SoulInfuserBuild() {
            super(SoulInfuser.this);
        }

        public void placed() {
            if (!Vars.net.client()) {
                super.placed();
            }
        }

        public boolean onConfigureTileTapped(Building other) {
            if (other instanceof SoulContainer.SoulContainerBuild && Intersector.overlaps(Tmp.cr1.set(this.x, this.y, SoulInfuser.this.range * 8.0F), other.tile().getHitbox(Tmp.r1))) {
                this.configure(other.pos());
                return false;
            } else {
                return true;
            }
        }

        public void drawSelect() {
            super.drawSelect();
            Lines.stroke(1.0F);
            Draw.color(Pal.accent);
            Drawf.circles(this.x, this.y, SoulInfuser.this.range * 8.0F);
            Draw.reset();
        }

        public void drawConfigure() {
            Drawf.circles(this.x, this.y, (float)(this.tile.block().size * 8) / 2.0F + 1.0F + Mathf.absin(Time.time, 4.0F, 1.0F));
            Drawf.circles(this.x, this.y, SoulInfuser.this.range * 8.0F);

            for(int i = 0; i < this.containers.size; ++i) {
                Building build = Vars.world.build(this.containers.get(i));
                if (build != null && build.isValid()) {
                    Drawf.square(build.x, build.y, (float)(build.block.size * 8) / 2.0F + 1.0F, Pal.place);
                }
            }

            Draw.reset();
        }

        public boolean shouldConsume() {
            for(int i = 0; i < this.containers.size; ++i) {
                Building build = Vars.world.build(this.containers.items[i]);
                if (build instanceof SoulContainer.SoulContainerBuild) {
                    SoulContainer.SoulContainerBuild cont = (SoulContainer.SoulContainerBuild)build;
                    if (cont.acceptSoul(1) > 0) {
                        return true;
                    }
                }
            }

            return this.acceptSoul(SoulInfuser.this.amount) >= SoulInfuser.this.amount;
        }

        public void consume() {
            super.consume();
            int sent = 0;

            for(int i = 0; i < this.containers.size && sent < SoulInfuser.this.amount; ++i) {
                Building build = Vars.world.build(this.containers.items[i]);
                if (build instanceof SoulContainer.SoulContainerBuild) {
                    SoulContainer.SoulContainerBuild cont = (SoulContainer.SoulContainerBuild)build;
                    if (cont.acceptSoul(SoulInfuser.this.amount) >= SoulInfuser.this.amount) {
                        cont.join();
                        ++sent;
                    }
                }
            }

            if (sent < SoulInfuser.this.amount) {
                sent = SoulInfuser.this.amount - sent;
                int accept = this.acceptSoul(sent);

                for(int i = 0; i < accept; ++i) {
                    this.join();
                }
            }

        }
    }
}
