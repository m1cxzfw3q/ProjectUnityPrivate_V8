package unity.type;

import arc.Core;
import arc.graphics.g2d.TextureRegion;
import mindustry.type.ItemStack;

public class UnderworldBlock {
    public String name;
    public String localizedName;
    public ItemStack[] cost;
    public int buildTime;
    public TextureRegion region;

    public UnderworldBlock(String name, ItemStack[] cost, int buildTime) {
        this.name = name;
        this.cost = cost;
        this.buildTime = buildTime;
        this.region = Core.atlas.find("unity-" + name);
        this.localizedName = Core.bundle.get("block." + name + ".name");
    }
}
