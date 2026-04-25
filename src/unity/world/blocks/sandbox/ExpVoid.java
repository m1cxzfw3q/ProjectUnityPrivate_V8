package unity.world.blocks.sandbox;

import arc.Core;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.meta.Stat;
import unity.world.blocks.exp.ExpHolder;

public class ExpVoid extends Block {
    public int produceTimer;
    public float reload;

    public ExpVoid(String name) {
        super(name);
        this.produceTimer = this.timers++;
        this.reload = 30.0F;
        this.update = this.solid = true;
    }

    public void setStats() {
        super.setStats();
        this.stats.add(Stat.itemCapacity, "@", new Object[]{Core.bundle.format("exp.expAmount", new Object[]{"Infinity"})});
    }

    public class ExpVoidBuild extends Building implements ExpHolder {
        public void updateTile() {
            if (this.enabled && this.timer.get(ExpVoid.this.produceTimer, ExpVoid.this.reload)) {
                for(Building b : this.proximity) {
                    if (b instanceof ExpHolder) {
                        ExpHolder exp = (ExpHolder)b;
                        exp.unloadExp(99999999);
                    }
                }
            }

        }

        public int getExp() {
            return 0;
        }

        public int handleExp(int amount) {
            return amount;
        }

        public boolean acceptOrb() {
            return true;
        }

        public boolean handleOrb(int orbExp) {
            return true;
        }
    }
}
