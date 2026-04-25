package unity.world.blocks.exp;

import arc.Core;
import arc.func.Prov;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.io.Reads;
import arc.util.io.Writes;
import java.util.Objects;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.logic.LAccess;
import mindustry.ui.Bar;
import mindustry.world.Block;
import mindustry.world.meta.Stat;
import unity.entities.ExpOrbs;
import unity.graphics.UnityPal;

public class ExpTank extends Block {
    public int expCapacity = 600;
    public TextureRegion topRegion;
    public TextureRegion expRegion;

    public ExpTank(String name) {
        super(name);
        this.update = this.solid = this.sync = true;
    }

    public void setStats() {
        super.setStats();
        this.stats.add(Stat.itemCapacity, "@", new Object[]{Core.bundle.format("exp.expAmount", new Object[]{this.expCapacity})});
    }

    public void setBars() {
        super.setBars();
        this.bars.add("exp", (entity) -> {
            Prov var10002 = () -> Core.bundle.get("bar.exp");
            Prov var10003 = () -> UnityPal.exp;
            Objects.requireNonNull(entity);
            return new Bar(var10002, var10003, entity::expf);
        });
    }

    public void load() {
        super.load();
        this.topRegion = Core.atlas.find(this.name + "-top");
        this.expRegion = Core.atlas.find(this.name + "-exp");
    }

    protected TextureRegion[] icons() {
        return new TextureRegion[]{this.region, this.topRegion};
    }

    public class ExpTankBuild extends Building implements ExpHolder {
        public int exp = 0;

        public int getExp() {
            return this.exp;
        }

        public int handleExp(int amount) {
            if (amount > 0) {
                int e = Math.min(ExpTank.this.expCapacity - this.exp, amount);
                this.exp += e;
                return e;
            } else {
                int e = Math.min(-amount, this.exp);
                this.exp -= e;
                return -e;
            }
        }

        public float expf() {
            return (float)this.exp / (float)ExpTank.this.expCapacity;
        }

        public int unloadExp(int amount) {
            int e = Math.min(amount, this.exp);
            this.exp -= e;
            return e;
        }

        public boolean acceptOrb() {
            return true;
        }

        public boolean handleOrb(int orbExp) {
            return this.handleExp(orbExp) > 0;
        }

        public void draw() {
            Draw.rect(ExpTank.this.region, this.x, this.y);
            Draw.color(UnityPal.exp, Color.white, Mathf.absin(20.0F, 0.6F));
            Draw.alpha(this.expf());
            Draw.rect(ExpTank.this.expRegion, this.x, this.y);
            Draw.color();
            Draw.rect(ExpTank.this.topRegion, this.x, this.y);
        }

        public void drawSelect() {
            super.drawSelect();
            ExpTank.this.drawPlaceText(this.exp + "/" + ExpTank.this.expCapacity, this.tile.x, this.tile.y, this.exp > 0);
        }

        public void drawLight() {
            Drawf.light(this.team, this.x, this.y, 25.0F + 25.0F * this.expf(), UnityPal.exp, 0.5F * this.expf());
        }

        public void onDestroyed() {
            ExpOrbs.spreadExp(this.x, this.y, (float)this.exp * 0.8F, (float)(3 * ExpTank.this.size));
            super.onDestroyed();
        }

        public double sense(LAccess sensor) {
            double var10000;
            switch (sensor) {
                case itemCapacity:
                    var10000 = (double)ExpTank.this.expCapacity;
                    break;
                case totalItems:
                    var10000 = (double)this.exp;
                    break;
                default:
                    var10000 = super.sense(sensor);
            }

            return var10000;
        }

        public void write(Writes write) {
            super.write(write);
            write.i(this.exp);
        }

        public void read(Reads read, byte revision) {
            super.read(read, revision);
            this.exp = read.i();
        }
    }
}
