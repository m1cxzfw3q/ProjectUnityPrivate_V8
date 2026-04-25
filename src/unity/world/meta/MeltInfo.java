package unity.world.meta;

import arc.struct.ObjectMap;
import mindustry.type.Item;

public class MeltInfo {
    public static final MeltInfo[] all = new MeltInfo[14];
    public static final ObjectMap<Item, MeltInfo> map = new ObjectMap(14);
    public final Item item;
    public final MeltInfo additiveID;
    public final String name;
    public final float meltPoint;
    public final float meltSpeed;
    public final float evaporation;
    public final float evaporationTemp;
    public final float additiveWeight;
    public final int priority;
    public final byte id;
    public final boolean additive;
    private static byte total;

    public MeltInfo(Item item, MeltInfo additiveID, String name, float meltPoint, float meltSpeed, float evaporation, float evaporationTemp, float additiveWeight, int priority, boolean additive) {
        this.item = item;
        this.additiveID = additiveID;
        this.name = name;
        this.meltPoint = meltPoint;
        this.meltSpeed = meltSpeed;
        this.evaporation = evaporation;
        this.evaporationTemp = evaporationTemp;
        this.additiveWeight = additiveWeight;
        this.priority = priority;
        this.additive = additive;
        all[total] = this;
        if (item != null) {
            map.put(item, this);
        }

        byte var10001 = total;
        total = (byte)(var10001 + 1);
        this.id = var10001;
    }

    public MeltInfo(Item item, float meltPoint, float meltSpeed, float evaporation, float evaporationTemp, int priority) {
        this(item, (MeltInfo)null, item.name, meltPoint, meltSpeed, evaporation, evaporationTemp, -1.0F, priority, false);
    }

    public MeltInfo(String name, float meltPoint, float meltSpeed, float evaporation, float evaporationTemp, int priority) {
        this((Item)null, (MeltInfo)null, name, meltPoint, meltSpeed, evaporation, evaporationTemp, -1.0F, priority, false);
    }

    public MeltInfo(Item item, MeltInfo additiveID, float additiveWeight, int priority, boolean additive) {
        this(item, additiveID, item.name, -1.0F, -1.0F, -1.0F, -1.0F, additiveWeight, priority, additive);
    }

    public MeltInfo(Item item, float meltPoint, float meltSpeed, int priority) {
        this(item, meltPoint, meltSpeed, -1.0F, -1.0F, priority);
    }
}
