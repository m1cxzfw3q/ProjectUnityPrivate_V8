package unity.world.blocks.exp;

import arc.Core;
import arc.func.Prov;
import java.util.Objects;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.ui.Bar;
import mindustry.world.blocks.storage.StorageBlock;
import mindustry.world.meta.Stat;
import unity.graphics.UnityPal;

public class KoruhVault extends StorageBlock {
    public int expCap = 500;

    public KoruhVault(String name) {
        super(name);
        this.update = this.sync = true;
    }

    public void setStats() {
        super.setStats();
        this.stats.add(Stat.itemCapacity, "@", new Object[]{Core.bundle.format("exp.expAmount", new Object[]{this.expCap})});
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

    public class KoruhVaultBuild extends StorageBlock.StorageBuild implements ExpHolder {
        public int exp;

        public KoruhVaultBuild() {
            super(KoruhVault.this);
        }

        public int getExp() {
            return this.exp;
        }

        public int handleExp(int amount) {
            if (amount > 0) {
                int e = Math.min(KoruhVault.this.expCap - this.exp, amount);
                this.exp += e;
                return e;
            } else {
                int e = Math.min(-amount, this.exp);
                this.exp -= e;
                return -e;
            }
        }

        public boolean acceptItem(Building source, Item item) {
            return this.items.total() < KoruhVault.this.itemCapacity;
        }
    }
}
