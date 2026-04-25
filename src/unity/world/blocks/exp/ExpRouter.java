package unity.world.blocks.exp;

import arc.math.Angles;
import arc.math.geom.Geometry;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.Building;
import mindustry.world.Tile;
import mindustry.world.blocks.distribution.Conveyor;
import mindustry.world.blocks.distribution.Junction;
import mindustry.world.blocks.production.Incinerator;
import mindustry.world.meta.Stat;
import unity.entities.ExpOrbs;

public class ExpRouter extends Junction {
    public float reloadTime = 15.0F;

    public ExpRouter(String name) {
        super(name);
        this.noUpdateDisabled = false;
    }

    public void setStats() {
        super.setStats();
        this.stats.add(Stat.speed, "stat.unity.exppersec", new Object[]{60.0F / this.reloadTime});
    }

    public class ExpRouterBuild extends Junction.JunctionBuild implements ExpHolder {
        public float reload;

        public ExpRouterBuild() {
            super(ExpRouter.this);
            this.reload = ExpRouter.this.reloadTime;
        }

        public void updateTile() {
            super.updateTile();
            this.reload += this.edelta();
        }

        public int getExp() {
            return 0;
        }

        public int handleExp(int amount) {
            return 0;
        }

        public boolean acceptOrb() {
            return this.enabled && this.reload >= ExpRouter.this.reloadTime;
        }

        public boolean handleOrb(int orbExp) {
            this.reload = 0.0F;

            for(int i = 0; i < 4; ++i) {
                int dir = (this.rotation + i) % 4;
                if (this.tryOutput(dir, orbExp)) {
                    this.rotation = (dir + 1) % 4;
                    return true;
                }
            }

            return false;
        }

        public int handleTower(int amount, float angle) {
            if (this.enabled && ExpOrbs.orbs(amount) > 0) {
                int a = ExpOrbs.oneOrb(amount);
                if (Angles.near(angle % 90.0F, 45.0F, 10.0F)) {
                    int dir = (int)(angle / 90.0F);
                    boolean yes = this.tryOutput((dir + this.rotation % 2) % 4, a);
                    this.rotation = (this.rotation + 1) % 4;
                    if (yes) {
                        return a;
                    }

                    if (this.tryOutput((dir + this.rotation % 2) % 4, a)) {
                        return a;
                    }
                } else {
                    int dir = ((int)angle + 45) / 90 % 4;
                    if (this.tryOutput(dir, a)) {
                        return a;
                    }
                }

                return 0;
            } else {
                return 0;
            }
        }

        public boolean tryOutput(int dir, int orbExp) {
            Tile t = this.tile.nearby(dir);
            if (t == null) {
                return false;
            } else if (t.solid()) {
                Building var7 = t.build;
                if (var7 instanceof ExpHolder) {
                    ExpHolder exp = (ExpHolder)var7;
                    if (exp.acceptOrb() && exp.handleOrb(orbExp)) {
                        return true;
                    }
                }

                return t.block() instanceof Incinerator;
            } else {
                Building var5 = t.build;
                if (var5 instanceof Conveyor.ConveyorBuild) {
                    Conveyor.ConveyorBuild conv = (Conveyor.ConveyorBuild)var5;
                    if (conv.nearby(conv.rotation) == this) {
                        return false;
                    }
                }

                ExpOrbs.dropExp(this.x + (float)Geometry.d4x(dir) * 7.0F, this.y + (float)Geometry.d4y(dir) * 7.0F, (float)dir * 90.0F, 4.0F, orbExp);
                return true;
            }
        }

        public void read(Reads read, byte revision) {
            super.read(read, revision);
            this.reload = read.f();
        }

        public void write(Writes write) {
            super.write(write);
            write.f(this.reload);
        }
    }
}
