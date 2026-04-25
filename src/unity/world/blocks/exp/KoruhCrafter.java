package unity.world.blocks.exp;

import arc.Core;
import arc.func.Prov;
import arc.math.Mathf;
import arc.util.io.Reads;
import arc.util.io.Writes;
import java.util.Objects;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.ui.Bar;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import unity.entities.ExpOrbs;
import unity.graphics.UnityPal;

public class KoruhCrafter extends GenericCrafter {
    public int expUse = 2;
    public int expCapacity = 24;
    public boolean ignoreExp = true;
    public float craftDamage = 3.5F;
    public Effect craftDamageEffect;

    public KoruhCrafter(String name) {
        super(name);
        this.craftDamageEffect = Fx.explosion;
        this.sync = true;
    }

    public void setStats() {
        super.setStats();
        this.stats.add(Stat.itemCapacity, "@", new Object[]{Core.bundle.format("exp.expAmount", new Object[]{this.expCapacity})});
        if (this.expUse > 0) {
            this.stats.add(Stat.input, "@ [lightgray]@[]", new Object[]{Core.bundle.format("explib.expAmount", new Object[]{(float)this.expUse / this.craftTime * 60.0F}), StatUnit.perSecond.localized()});
        }

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

    public class KoruhCrafterBuild extends GenericCrafter.GenericCrafterBuild implements ExpHolder {
        public int exp;

        public KoruhCrafterBuild() {
            super(KoruhCrafter.this);
        }

        public void lackingExp(int missing) {
            Core.app.post(() -> this.damage(KoruhCrafter.this.craftDamage * (float)missing * Mathf.random(0.5F, 1.0F)));
        }

        public boolean consValid() {
            return super.consValid() && (KoruhCrafter.this.ignoreExp || this.exp >= KoruhCrafter.this.expUse);
        }

        public void consume() {
            super.consume();
            int a = Math.min(KoruhCrafter.this.expUse, this.exp);
            this.exp -= a;
            if (a < KoruhCrafter.this.expUse) {
                this.lackingExp(KoruhCrafter.this.expUse - a);
                KoruhCrafter.this.craftDamageEffect.at(this);
            }

        }

        public int getExp() {
            return this.exp;
        }

        public int handleExp(int amount) {
            if (amount > 0) {
                int e = Math.min(KoruhCrafter.this.expCapacity - this.exp, amount);
                this.exp += e;
                return e;
            } else {
                int e = Math.min(-amount, this.exp);
                this.exp -= e;
                return -e;
            }
        }

        public float expf() {
            return (float)this.exp / (float)KoruhCrafter.this.expCapacity;
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

        public void drawSelect() {
            super.drawSelect();
            KoruhCrafter.this.drawPlaceText(this.exp + "/" + KoruhCrafter.this.expCapacity, this.tile.x, this.tile.y, this.exp >= KoruhCrafter.this.expUse);
        }

        public void onDestroyed() {
            ExpOrbs.spreadExp(this.x, this.y, (float)this.exp * 0.3F, (float)(3 * KoruhCrafter.this.size));
            super.onDestroyed();
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
