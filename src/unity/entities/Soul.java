package unity.entities;

import arc.math.Mathf;
import arc.util.Tmp;
import mindustry.entities.Sized;
import mindustry.entities.units.UnitController;
import mindustry.gen.BlockUnitc;
import mindustry.gen.Entityc;
import mindustry.gen.Healthc;
import mindustry.gen.Teamc;
import unity.gen.MonolithSoul;

public interface Soul extends Teamc, Healthc, Sized {
    int souls();

    int maxSouls();

    default boolean canJoin() {
        return this.souls() < this.maxSouls();
    }

    default boolean hasSouls() {
        return this.souls() > 0;
    }

    default int acceptSoul(Entityc other) {
        Soul soul = toSoul(other);
        return soul != null ? this.acceptSoul(soul) : 0;
    }

    default int acceptSoul(Soul other) {
        return this.acceptSoul(other.souls());
    }

    default int acceptSoul(int amount) {
        return Math.min(this.maxSouls() - this.souls(), amount);
    }

    void join();

    void unjoin();

    default void joined() {
    }

    default float soulf() {
        return (float)this.souls() / (float)this.maxSouls();
    }

    default void spreadSouls() {
        boolean transferred = false;
        float start = Mathf.random(360.0F);

        for(int i = 0; i < this.souls(); ++i) {
            MonolithSoul soul = MonolithSoul.create(this.team());
            Tmp.v1.trns(Mathf.random(360.0F), Mathf.random(this.hitSize()));
            soul.set(this.x() + Tmp.v1.x, this.y() + Tmp.v1.y);
            Tmp.v1.trns(start + 360.0F / (float)this.souls() * (float)i, Mathf.random(6.0F, 12.0F));
            soul.rotation = Tmp.v1.angle();
            soul.vel.set(Tmp.v1.x, Tmp.v1.y);
            transferred = this.apply(soul, i, transferred);
            soul.add();
        }

    }

    boolean apply(MonolithSoul var1, int var2, boolean var3);

    static boolean isSoul(Object e) {
        return toSoul(e) != null;
    }

    static Soul toSoul(Object e) {
        if (e instanceof UnitController) {
            UnitController cont = (UnitController)e;
            e = cont.unit();
        }

        if (e instanceof BlockUnitc) {
            BlockUnitc unit = (BlockUnitc)e;
            e = unit.tile();
        }

        if (e instanceof Soul) {
            Soul soul = (Soul)e;
            return soul;
        } else {
            return null;
        }
    }
}
