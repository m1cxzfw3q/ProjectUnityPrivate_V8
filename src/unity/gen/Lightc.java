package unity.gen;

import arc.func.Boolf;
import arc.func.Cons;
import arc.func.Longf;
import arc.math.geom.QuadTree;
import arc.struct.ObjectFloatMap;
import arc.struct.ObjectMap;
import mindustry.gen.Drawc;
import mindustry.gen.Entityc;
import mindustry.gen.Posc;
import unity.util.AtomicPair;

public interface Lightc extends QuadTree.QuadTreeObject, Entityc, Drawc, Posc {
    void snap();

    void cast();

    float recStrength();

    int combinedCol(int var1);

    float endStrength();

    void queueAdd();

    void queueRemove();

    void children(Cons<ObjectMap<Longf<Light>, AtomicPair<Light, Light>>> var1);

    ObjectMap.Entries<Longf<Light>, AtomicPair<Light, Light>> childEntries();

    void parents(Cons<ObjectFloatMap<Light>> var1);

    ObjectFloatMap.Entries<Light> parentEntries();

    boolean parentsAny(Boolf<ObjectFloatMap<Light>> var1);

    void clearChildren();

    void clearParents();

    void clearInvalid();

    boolean isParent(Light var1);

    void parent(Light var1, float var2);

    void child(Longf<Light> var1);

    void detachChild(Light var1);

    void detachParent(Light var1);

    float visualRot();

    float endX();

    float endY();

    float strength();

    float queueStrength();

    void queueStrength(float var1);

    float rotation();

    float queueRotation();

    void queueRotation(float var1);

    long queuePosition();

    void queuePosition(long var1);

    LightHoldc.LightHoldBuildc source();

    LightHoldc.LightHoldBuildc queueSource();

    void queueSource(LightHoldc.LightHoldBuildc var1);

    int color();

    int queueColor();

    void queueColor(int var1);

    boolean casted();

    boolean valid();

    LightHoldc.LightHoldBuildc pointed();

    void pointed(LightHoldc.LightHoldBuildc var1);

    boolean rotationChanged();

    void rotationChanged(boolean var1);
}
