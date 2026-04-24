package unity.gen;

import arc.math.geom.Vec2;
import arc.struct.Seq;
import mindustry.gen.Drawc;
import mindustry.gen.Entityc;
import mindustry.gen.Hitboxc;
import mindustry.gen.Posc;
import mindustry.gen.Rotc;
import mindustry.gen.Timedc;
import unity.entities.effects.CutEffects;

public interface CutEffectc extends Rotc, Drawc, Hitboxc, Posc, Timedc, Entityc {
    float z();

    void despawn();

    Drawc other();

    void other(Drawc var1);

    Seq<CutEffects> stencils();

    void stencils(Seq<CutEffects> var1);

    Vec2 velocity();

    void velocity(Vec2 var1);

    float originX();

    void originX(float var1);

    float originY();

    void originY(float var1);

    float angularVel();

    void angularVel(float var1);

    float drag();

    void drag(float var1);

    boolean removed();

    void removed(boolean var1);
}
