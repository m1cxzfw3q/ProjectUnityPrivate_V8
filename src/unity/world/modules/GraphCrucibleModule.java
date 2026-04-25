package unity.world.modules;

import arc.Core;
import arc.graphics.Color;
import arc.scene.ui.layout.Table;
import arc.struct.IntMap;
import arc.struct.Seq;
import arc.util.Strings;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.ui.Bar;
import unity.graphics.UnityPal;
import unity.ui.IconBar;
import unity.ui.StackedBarChart;
import unity.util.Utils;
import unity.world.graph.CrucibleGraph;
import unity.world.graphs.GraphCrucible;
import unity.world.meta.CrucibleData;
import unity.world.meta.GraphType;
import unity.world.meta.MeltInfo;

public class GraphCrucibleModule extends GraphModule<GraphCrucible, GraphCrucibleModule, CrucibleGraph> {
    public final IntMap<Seq<CrucibleData>> propsList = new IntMap(4);
    public float liquidCap;
    public int tilingIndex;
    final Seq<CrucibleData> contains = new Seq();
    int containedAmCache;
    boolean melter = true;
    boolean containChanged = true;

    public boolean addItem(Item item) {
        return ((CrucibleGraph)this.networks.get(0)).addItem(item);
    }

    public Seq<CrucibleData> getContained() {
        CrucibleGraph net = (CrucibleGraph)this.networks.get(0);
        return net != null ? net.contains() : this.contains;
    }

    public float getVolumeContained() {
        CrucibleGraph net = (CrucibleGraph)this.networks.get(0);
        return net != null ? net.getVolumeContained() : 0.0F;
    }

    public boolean canContainMore(float amount) {
        CrucibleGraph net = (CrucibleGraph)this.networks.get(0);
        return net != null ? net.canContainMore(amount) : false;
    }

    public float getTotalLiquidCapacity() {
        CrucibleGraph net = (CrucibleGraph)this.networks.get(0);
        return net != null ? net.totalCapacity() : 0.0F;
    }

    public StackedBarChart getStackedBars() {
        return new StackedBarChart(200.0F, () -> {
            Seq<CrucibleData> cc = this.getContained();
            StackedBarChart.BarStat[] data;
            if (cc.isEmpty()) {
                data = new StackedBarChart.BarStat[]{new StackedBarChart.BarStat(Core.bundle.get("stat.unity.crucible.empty"), 1.0F, 1.0F, UnityPal.youngchaGray)};
            } else {
                float tv = this.getVolumeContained();
                int len = cc.size;
                float min = Math.min(1.0F / (float)len, 0.15F);
                float remain = 1.0F - (float)len * min;
                MeltInfo[] melts = MeltInfo.all;
                data = new StackedBarChart.BarStat[len];

                for(int i = 0; i < len; ++i) {
                    CrucibleData ccl = (CrucibleData)cc.get(i);
                    MeltInfo m = melts[ccl.id];
                    Item item = m.item;
                    if (item != null) {
                        data[i] = new StackedBarChart.BarStat(Core.bundle.format("stat.unity.crucible.iteminfo", new Object[]{item.toString(), Strings.fixed(ccl.volume, 2)}), min + remain * ccl.volume / tv, ccl.meltedRatio, item.color);
                    } else {
                        data[i] = new StackedBarChart.BarStat(Core.bundle.format("stat.unity.crucible.iteminfo", new Object[]{m.name, Strings.fixed(ccl.volume, 2)}), min + remain * ccl.volume / tv, ccl.meltedRatio, UnityPal.youngchaGray);
                    }
                }
            }

            return data;
        });
    }

    public IconBar getIconBar() {
        return new IconBar(96.0F, () -> {
            float temp = 0.0F;
            Seq<CrucibleData> cc = this.getContained();
            CrucibleGraph net = (CrucibleGraph)this.networks.get(0);
            if (net != null) {
                temp = net.getAverageTemp();
            }

            Color tempCol = Utils.tempColor(temp);
            tempCol.mul(tempCol.a);
            tempCol.add(Color.gray);
            tempCol.a = 1.0F;
            int len = 0;
            if (cc != null) {
                len = cc.size;
            }

            IconBar.IconBarStat data = new IconBar.IconBarStat(temp - 273.0F, 500.0F, 0.0F, tempCol, len);
            if (temp < 270.0F) {
                data.defaultMin = Math.max(-273.15F, 5.0F * (temp - 273.0F));
                data.defaultMax = Math.max(20.0F, 500.0F + 5.0F * (temp - 273.0F));
            }

            if (len > 0) {
                MeltInfo[] melts = MeltInfo.all;

                for(CrucibleData i : cc) {
                    MeltInfo m = melts[i.id];
                    Item item = m.item;
                    if (item != null) {
                        data.push(m.meltPoint - 273.0F, item.fullIcon);
                    }
                }
            }

            return data;
        });
    }

    void applySaveState(CrucibleGraph graph, int index) {
        CrucibleData[] cache = (CrucibleData[])this.saveCache.get(index);
        int len = cache.length;
        Seq<CrucibleData> cc = graph.contains();
        if (cc.size != len) {
            cc.clear();

            for(CrucibleData i : cache) {
                cc.add(i);
            }

        }
    }

    void updateExtension() {
    }

    void updateProps(CrucibleGraph graph, int index) {
    }

    void proximityUpdateCustom() {
    }

    void display(Table table) {
    }

    void initStats() {
        this.tilingIndex = this.containedAmCache = 0;
        this.liquidCap = 0.0F;
        this.contains.clear();
        this.melter = true;
        this.propsList.clear();
    }

    void displayBars(Table table) {
        CrucibleGraph net = (CrucibleGraph)this.networks.get(0);
        if (net != null) {
            table.add(new Bar(() -> Core.bundle.get("stat.unity.liquidtotal") + ": " + Strings.fixed(net.getVolumeContained(), 1) + "/" + Strings.fixed(net.totalCapacity(), 1), () -> Pal.darkishGray, () -> net.getVolumeContained() / net.totalCapacity())).growX().row();
        }
    }

    CrucibleGraph newNetwork() {
        return new CrucibleGraph();
    }

    void writeGlobal(Writes write) {
    }

    void readGlobal(Reads read, byte revision) {
    }

    void writeLocal(Writes write, CrucibleGraph graph) {
        Seq<CrucibleData> cc = graph.contains();
        write.i(cc.size);

        for(CrucibleData i : cc) {
            write.i(i.id);
            write.f(i.meltedRatio);
            write.f(i.volume);
        }

    }

    CrucibleData[] readLocal(Reads read, byte revision) {
        int len = read.i();
        CrucibleData[] save = new CrucibleData[len];
        MeltInfo[] melts = MeltInfo.all;

        for(int i = 0; i < len; ++i) {
            int id = read.i();
            float mratio = read.f();
            float vol = read.f();
            save[i] = new CrucibleData(id, vol, mratio, melts[id].item);
        }

        return save;
    }

    public GraphType type() {
        return GraphType.crucible;
    }
}
