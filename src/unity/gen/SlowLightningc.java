package unity.gen;

import arc.func.Floatp;
import arc.math.geom.Position;
import arc.struct.Seq;
import mindustry.game.Team;
import mindustry.gen.Bullet;
import mindustry.gen.Childc;
import mindustry.gen.Drawc;
import mindustry.gen.Entityc;
import mindustry.gen.Posc;
import mindustry.gen.Rotc;
import unity.entities.effects.SlowLightningType;

public interface SlowLightningc extends Entityc, Drawc, Posc, Rotc, Childc {
    void end(SlowLightningType.SlowLightningNode var1);

    void updateLastPosition();

    boolean nextBoolean(float var1);

    float nextRange(float var1);

    float nextRand();

    Team team();

    void team(Team var1);

    Position target();

    void target(Position var1);

    Bullet bullet();

    void bullet(Bullet var1);

    Floatp liveDamage();

    void liveDamage(Floatp var1);

    SlowLightningType type();

    void type(SlowLightningType var1);

    Seq<SlowLightningType.SlowLightningNode> nodes();

    void nodes(Seq<SlowLightningType.SlowLightningNode> var1);

    int layer();

    void layer(int var1);

    int seed();

    void seed(int var1);

    int bulletId();

    void bulletId(int var1);

    float time();

    void time(float var1);

    float distance();

    void distance(float var1);

    float timer();

    void timer(float var1);

    float lastX();

    void lastX(float var1);

    float lastY();

    void lastY(float var1);

    boolean ended();

    void ended(boolean var1);

    boolean passed();

    void passed(boolean var1);
}
