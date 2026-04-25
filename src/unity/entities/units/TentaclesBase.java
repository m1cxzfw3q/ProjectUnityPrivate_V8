package unity.entities.units;

import arc.struct.Seq;
import mindustry.gen.Unit;
import mindustry.gen.Unitc;
import mindustry.type.UnitType;
import unity.entities.Tentacle;
import unity.type.TentacleType;
import unity.type.UnityUnitType;

public interface TentaclesBase extends Unitc {
    Seq<Tentacle> tentacles();

    void tentacles(Seq<Tentacle> var1);

    default void updateTentacles() {
        this.tentacles().each(Tentacle::update);
    }

    default void drawTentacles() {
        this.tentacles().each(Tentacle::draw);
    }

    default void addTentacles() {
        UnitType var2 = this.type();
        if (var2 instanceof UnityUnitType) {
            UnityUnitType e = (UnityUnitType)var2;
            Seq<Tentacle> t = new Seq();

            for(TentacleType tentacle : e.tentacles) {
                t.add((new Tentacle()).add(tentacle, (Unit)this.self()));
            }

            this.tentacles(t);
        }

    }
}
