package unity.gen;

import arc.func.Cons;
import mindustry.gen.Boundedc;
import mindustry.gen.Builderc;
import mindustry.gen.Commanderc;
import mindustry.gen.Drawc;
import mindustry.gen.Entityc;
import mindustry.gen.Flyingc;
import mindustry.gen.Healthc;
import mindustry.gen.Hitboxc;
import mindustry.gen.Itemsc;
import mindustry.gen.Minerc;
import mindustry.gen.Physicsc;
import mindustry.gen.Posc;
import mindustry.gen.Rotc;
import mindustry.gen.Shieldc;
import mindustry.gen.Statusc;
import mindustry.gen.Syncc;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;
import mindustry.gen.Unitc;
import mindustry.gen.Velc;
import mindustry.gen.Weaponsc;

public interface Wormc extends Statusc, Entityc, Drawc, Hitboxc, Physicsc, Itemsc, Flyingc, Shieldc, Posc, Commanderc, Syncc, Weaponsc, Teamc, Unitc, Healthc, Rotc, Boundedc, Builderc, Minerc, Velc {
    boolean isHead();

    boolean isTail();

    int countFoward();

    int countBackward();

    <T extends Unit & Wormc> void distributeActionBack(Cons<T> var1);

    <T extends Unit & Wormc> void distributeActionForward(Cons<T> var1);

    boolean updateBounded();

    Unit addTail();

    Unit head();

    void head(Unit var1);

    Unit parent();

    void parent(Unit var1);

    Unit child();

    void child(Unit var1);

    float layer();

    void layer(float var1);

    float scanTime();

    void scanTime(float var1);

    byte weaponIdx();

    void weaponIdx(byte var1);

    boolean removing();

    void removing(boolean var1);

    boolean saveAdd();

    void saveAdd(boolean var1);

    float splitHealthDiv();

    void splitHealthDiv(float var1);

    float regenTime();

    void regenTime(float var1);

    float waitTime();

    void waitTime(float var1);

    int childId();

    void childId(int var1);

    int headId();

    void headId(int var1);
}
