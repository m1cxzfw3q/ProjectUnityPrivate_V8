package unity.world.modules;

import arc.func.Cons;
import arc.math.geom.Point2;
import arc.math.geom.Position;
import arc.scene.ui.layout.Table;
import arc.struct.IntMap;
import arc.struct.ObjectMap;
import arc.struct.OrderedMap;
import arc.struct.Seq;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import unity.world.blocks.GraphBlockBase;
import unity.world.graph.BaseGraph;
import unity.world.graphs.Graph;
import unity.world.meta.GraphData;
import unity.world.meta.GraphType;

public abstract class GraphModule<T extends Graph, M extends GraphModule<T, M, G>, G extends BaseGraph<M, G>> {
    public final Seq<GraphData> acceptPorts = new Seq();
    public GraphModules parent;
    public T graph;
    public int d;
    protected final IntMap<G> networks = new IntMap(4);
    final OrderedMap<M, Integer> neighbours = new OrderedMap();
    final Seq saveCache = new Seq(4);
    int lastRecalc;
    boolean dead;
    boolean needsNetworkUpdate = true;
    boolean networkSaveState;
    boolean multi;
    private boolean initialized;

    GraphData getConnectSidePos(int index) {
        return this.parent.getConnectSidePos(index);
    }

    public int canConnect(Point2 pos) {
        GraphData temp = (GraphData)this.acceptPorts.find((d) -> pos.equals(d.toPos.x + this.parent.build.tileX(), d.toPos.y + this.parent.build.tileY()));
        return temp != null ? temp.index : -1;
    }

    void onCreate(GraphBlockBase.GraphBuildBase build) {
        this.acceptPorts.setSize(this.graph.accept.length);
        this.neighbours.shrink(this.graph.accept.length);
        this.initAllNets();
        this.needsNetworkUpdate = true;
        this.lastRecalc = -1;
        this.initStats();
        this.initialized = true;
    }

    public void recalcPorts() {
        if (this.lastRecalc != this.parent.build.rotation()) {
            this.acceptPorts.clear();
            int i = 0;

            for(int len = this.graph.accept.length; i < len; ++i) {
                if (this.graph.accept[i] != 0) {
                    this.acceptPorts.add(this.getConnectSidePos(i));
                }
            }

            this.lastRecalc = this.parent.build.rotation();
        }
    }

    public void onRemoved() {
        this.deleteSelfFromNetwork();
        this.deleteFromNeighbours();
    }

    void deleteFromNeighbours() {
        ObjectMap.Keys var1 = this.neighbours.keys().iterator();

        while(var1.hasNext()) {
            M n = (M)(var1.next());
            ((GraphModule)n).removeNeighbour(this);
        }

    }

    void deleteSelfFromNetwork() {
        this.dead = true;
        if (this.multi) {
            this.deleteSelfFromNetworkMulti();
        } else if (this.networks.get(0) != null) {
            ((BaseGraph)this.networks.get(0)).remove(this);
        }

    }

    void deleteSelfFromNetworkMulti() {
        if (!this.networks.isEmpty()) {
            for(G i : this.networks.values()) {
                i.remove(this);
            }

        }
    }

    void onUpdate() {
        if (!this.dead) {
            this.updateNetworks();
            this.updateExtension();
        }
    }

    void onRotationChanged(int prevRot, int newRot) {
        if (prevRot != -1) {
            this.deleteSelfFromNetwork();
            this.deleteFromNeighbours();
            this.dead = false;
            this.initAllNets();
            this.neighbours.clear();
        }

        this.recalcPorts();
        this.needsNetworkUpdate = true;
    }

    void updateNetworks() {
        if (this.multi) {
            this.updateNetworksMulti();
        } else {
            if (this.networks.get(0) != null) {
                if (this.needsNetworkUpdate) {
                    this.needsNetworkUpdate = false;
                    ((BaseGraph)this.networks.get(0)).rebuildGraph(this);
                    if (this.networkSaveState) {
                        this.applySaveState((BaseGraph)this.networks.get(0), 0);
                        this.networkSaveState = false;
                    }

                    this.parent.build.onGraphUpdate();
                }

                ((BaseGraph)this.networks.get(0)).update();
                this.updateProps((BaseGraph)this.networks.get(0), 0);
            }

        }
    }

    void updateNetworksMulti() {
        if (!this.networks.isEmpty()) {
            if (this.needsNetworkUpdate) {
                boolean[] covered = new boolean[4];
                int[] portArray = this.graph.accept;
                int i = 0;

                for(int len = portArray.length; i < len; ++i) {
                    int j = portArray[i] - 1;
                    if (portArray[i] != 0 && !covered[j]) {
                        this.getNetworkOfPort(j).rebuildGraphIndex(this, i);
                        covered[j] = true;
                    }
                }

                if (this.networkSaveState) {
                    for(IntMap.Entry<G> i : this.networks) {
                        this.applySaveState((BaseGraph)i.value, i.key);
                    }
                }

                this.networkSaveState = false;
            }

            for(IntMap.Entry<G> i : this.networks) {
                ((BaseGraph)i.value).update();
                this.updateProps((BaseGraph)i.value, i.key);
            }

            this.needsNetworkUpdate = false;
        }
    }

    abstract void applySaveState(G var1, int var2);

    abstract void updateExtension();

    abstract void updateProps(G var1, int var2);

    abstract void proximityUpdateCustom();

    abstract void display(Table var1);

    abstract void initStats();

    abstract void displayBars(Table var1);

    void drawSelect() {
        G net = (G)(this.networks.get(0));
        if (net != null) {
            net.connected.each((module) -> Drawf.selected((Building)module.parent.build.self(), Pal.accent));
        }

    }

    abstract G newNetwork();

    void initAllNets() {
        this.recalcPorts();
        if (this.multi) {
            this.initAllNetsMulti();
        } else {
            G net = this.newNetwork();
            this.networks.put(0, net);
            net.init(this);
        }

    }

    void initAllNetsMulti() {
        int[] portArray = this.graph.accept;
        this.networks.clear();
        int i = 0;

        for(int len = portArray.length; i < len; ++i) {
            if (portArray[i] != 0 && !this.networks.containsKey(portArray[i] - 1)) {
                G net = this.newNetwork();
                this.networks.put(portArray[i] - 1, net);
                net.init(this);
            }
        }

    }

    public boolean dead() {
        return this.dead;
    }

    public float lastRecalc() {
        return (float)this.lastRecalc;
    }

    public M getNeighbour(M module) {
        return (M)(this.neighbours.containsKey(module) ? module : null);
    }

    public void eachNeighbour(Cons<ObjectMap.Entry<M, Integer>> func) {
        ObjectMap.Entries var2 = this.neighbours.iterator();

        while(var2.hasNext()) {
            ObjectMap.Entry<M, Integer> n = (ObjectMap.Entry)var2.next();
            func.get(n);
        }

    }

    public void eachNeighbourKey(Cons<M> func) {
        ObjectMap.Keys var2 = this.neighbours.keys().iterator();

        while(var2.hasNext()) {
            M n = (M)(var2.next());
            func.get(n);
        }

    }

    public void eachNeighbourValue(Cons<Integer> func) {
        ObjectMap.Values var2 = this.neighbours.values().iterator();

        while(var2.hasNext()) {
            Integer n = (Integer)var2.next();
            func.get(n);
        }

    }

    float efficiency() {
        return 1.0F;
    }

    public int countNeighbours() {
        return this.neighbours.size;
    }

    public void removeNeighbour(M module) {
        if (module != null && this.neighbours.remove(module) != null) {
            this.parent.build.onNeighboursChanged();
        }

    }

    public void addNeighbour(M n, int portIndex) {
        if (n != null) {
            Integer v = (Integer)this.neighbours.put(n, portIndex);
            if (v == null || v == portIndex) {
                this.parent.build.onNeighboursChanged();
            }
        }

    }

    public Seq<GraphData> getConnectedNeighbours(int index) {
        return this.multi ? this.getConnectedNeighboursMulti(index) : this.acceptPorts;
    }

    Seq<GraphData> getConnectedNeighboursMulti(int index) {
        int[] portArray = this.graph.accept;
        int targetPort = portArray[index];
        Seq<GraphData> output = new Seq();

        for(GraphData i : this.acceptPorts) {
            if (portArray[i.index] == targetPort) {
                output.add(i);
            }
        }

        return output;
    }

    public G getNetwork() {
        return (G)(this.networks.get(0));
    }

    public boolean hasNetwork(G net) {
        if (this.multi) {
            return this.hasNetworkMulti(net);
        } else {
            return ((BaseGraph)this.networks.get(0)).id == net.id;
        }
    }

    boolean hasNetworkMulti(G net) {
        return this.getPortOfNetworkMulti(net) != -1;
    }

    public int getPortOfNetwork(G net) {
        if (this.multi) {
            return this.getPortOfNetworkMulti(net);
        } else {
            return ((BaseGraph)this.networks.get(0)).id == net.id ? 0 : -1;
        }
    }

    int getPortOfNetworkMulti(G net) {
        return net == null ? -1 : this.networks.findKey(net, true, -1);
    }

    public boolean replaceNetwork(G old, G set) {
        if (this.multi) {
            return this.replaceNetworkMulti(old, set);
        } else {
            this.networks.put(0, set);
            return true;
        }
    }

    boolean replaceNetworkMulti(G old, G set) {
        int index = this.networks.findKey(old, true, -1);
        if (index == -1) {
            return false;
        } else {
            this.networks.put(index, set);
            return true;
        }
    }

    public G getNetworkOfPort(int index) {
        return (G)(this.multi ? this.getNetworkOfPortMulti(index) : (BaseGraph)this.networks.get(0));
    }

    G getNetworkOfPortMulti(int index) {
        int l = this.graph.accept[index];
        return (G)(l == 0 ? null : (BaseGraph)this.networks.get(l - 1));
    }

    public void setNetworkOfPort(int index, G net) {
        if (this.multi) {
            this.setNetworkOfPortMulti(index, net);
        } else {
            this.networks.put(0, net);
        }

    }

    void setNetworkOfPortMulti(int index, G net) {
        int l = this.graph.accept[index];
        if (l != 0) {
            this.networks.put(l - 1, net);
        }
    }

    abstract void writeGlobal(Writes var1);

    abstract void readGlobal(Reads var1, byte var2);

    abstract void writeLocal(Writes var1, G var2);

    abstract Object[] readLocal(Reads var1, byte var2);

    void write(Writes write) {
        this.writeGlobal(write);
        if (this.multi) {
            this.writeMulti(write);
        } else {
            this.writeLocal(write, (BaseGraph)this.networks.get(0));
        }

    }

    void writeMulti(Writes write) {
        write.b(this.networks.size);

        for(G i : this.networks.values()) {
            this.writeLocal(write, i);
        }

    }

    void read(Reads read, byte revision) {
        this.readGlobal(read, revision);
        this.saveCache.clear();
        if (this.multi) {
            this.readMulti(read, revision);
        } else {
            this.saveCache.add(this.readLocal(read, revision));
            this.networkSaveState = true;
        }

    }

    void readMulti(Reads read, byte revision) {
        int netAm = read.b();

        for(int i = 0; i < netAm; ++i) {
            this.saveCache.add(this.readLocal(read, revision));
        }

        this.networkSaveState = true;
    }

    public G getNetworkFromSet(int index) {
        return (G)(this.networks.get(index));
    }

    boolean setNetworkFromSet(int index, G net) {
        if (this.networks.containsKey(index) && ((BaseGraph)this.networks.get(index)).id == net.id) {
            return false;
        } else {
            this.networks.put(index, net);
            return true;
        }
    }

    public abstract GraphType type();

    public int hueristic(Position target) {
        return this.d + Math.abs(Math.round(this.parent.build.x() - target.getX())) + Math.abs(Math.round(this.parent.build.y() - target.getY()));
    }

    public M d(int a) {
        this.d = a;
        return (M)this;
    }

    public int portIndex(M module) {
        return (Integer)this.neighbours.get(module, -1);
    }

    public float getTemp() {
        return 0.0F;
    }

    void setTemp(float t) {
    }

    public M graph(T graph) {
        this.graph = graph;
        if (graph.isMultiConnector) {
            this.multi = true;
        }

        return (M)this;
    }

    public boolean initialized() {
        return this.initialized;
    }
}
