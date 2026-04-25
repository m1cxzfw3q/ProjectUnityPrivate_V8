package unity.world.graph;

import arc.Core;
import arc.math.geom.Point2;
import arc.struct.ObjectSet;
import arc.struct.OrderedSet;
import arc.struct.PQueue;
import arc.struct.Seq;
import java.util.Comparator;
import mindustry.gen.Building;
import mindustry.world.Tile;
import unity.world.blocks.GraphBlockBase;
import unity.world.graphs.Graph;
import unity.world.meta.GraphData;
import unity.world.modules.GraphModule;

public abstract class BaseGraph<M extends GraphModule<? extends Graph, M, G>, G extends BaseGraph<M, G>> {
    public final OrderedSet<M> connected = new OrderedSet();
    public final int id;
    private static int lastId;
    long lastFrameUpdated;
    M target;

    BaseGraph() {
        this.id = lastId++;
    }

    public void init(M module) {
        this.connected.add(module);
        this.updateOnGraphChanged();
        this.addMergeStats(module);
    }

    public abstract G create();

    G copyGraph(M module) {
        G copygraph = this.create();
        ((BaseGraph)copygraph).init(module);
        ((BaseGraph)copygraph).copyGraphStatsFrom(this);
        return copygraph;
    }

    abstract void copyGraphStatsFrom(G var1);

    public void update() {
        long frameId = Core.graphics.getFrameId();
        if (frameId != this.lastFrameUpdated) {
            this.lastFrameUpdated = frameId;
            this.updateDirect();
            this.updateGraph();
        }
    }

    abstract void updateOnGraphChanged();

    abstract void updateGraph();

    abstract void updateDirect();

    boolean canConnect(M b1, M b2) {
        return true;
    }

    void addBuilding(M module, int connectIndex) {
        this.connected.add(module);
        this.updateOnGraphChanged();
        module.setNetworkOfPort(connectIndex, this);
        this.addMergeStats(module);
    }

    abstract void addMergeStats(M var1);

    void mergeGraph(G graph) {
        if (graph != null) {
            if (graph.connected.size > this.connected.size) {
                ((BaseGraph)graph).mergeGraph(this);
            } else {
                this.updateDirect();
                graph.updateDirect();
                this.mergeStats(graph);
                OrderedSet.OrderedSetIterator var2 = graph.connected.iterator();

                while(var2.hasNext()) {
                    M module = (M)(var2.next());
                    if (!this.connected.contains(module) && module.replaceNetwork(graph, this)) {
                        this.connected.add(module);
                    }
                }

                this.updateOnGraphChanged();
            }
        }
    }

    abstract void mergeStats(G var1);

    void killGraph() {
        this.connected.clear();
    }

    boolean isAtriculationPoint(M module) {
        Seq<M> neighs = new Seq(4);
        module.eachNeighbour((n) -> {
            if (module.getNetworkOfPort((Integer)n.value) == this) {
                neighs.add((GraphModule)n.key);
            }

        });
        int neighbourIndex = 1;
        if (neighbourIndex >= neighs.size) {
            this.target = null;
        } else {
            this.target = (M)(neighs.get(neighbourIndex));
        }

        PQueue<M> front = new PQueue();
        front.comparator = (a, b) -> this.target == null ? 99999999 : a.hueristic(this.target.parent.build) - b.hueristic(this.target.parent.build);
        front.add(((GraphModule)neighs.get(0)).d(0));
        int giveUp = this.connected.size;
        ObjectSet<M> visited = ObjectSet.with(new GraphModule[]{module});

        while(!front.empty()) {
            M current = (M)(front.poll());
            if (current == this.target) {
                ++neighbourIndex;
                if (neighbourIndex == neighs.size) {
                    return false;
                }

                for(this.target = (M)(neighs.get(neighbourIndex)); this.target == null || visited.contains(this.target); this.target = (M)(neighs.get(neighbourIndex))) {
                    ++neighbourIndex;
                    if (neighbourIndex == neighs.size) {
                        return false;
                    }
                }

                front.comparator = Comparator.comparingInt((a) -> a.hueristic(this.target.parent.build));
            }

            visited.add(current);
            current.eachNeighbour((n) -> {
                if (!visited.contains((GraphModule)n.key)) {
                    if (current.getNetworkOfPort((Integer)n.value) == this) {
                        front.add(((GraphModule)n.key).d(current.d + 4));
                    }

                }
            });
            --giveUp;
            if (giveUp < 0) {
                return true;
            }
        }

        return true;
    }

    public void remove(M module) {
        if (this.connected.contains(module)) {
            int c = module.countNeighbours();
            if (c != 0) {
                if (c != 1 && this.isAtriculationPoint(module)) {
                    this.killGraph();
                    ObjectSet<G> networksAdded = new ObjectSet(c);
                    module.eachNeighbour((n) -> {
                        G copyNet = module.getNetworkOfPort((Integer)n.value);
                        M temp = (M)(n.key);
                        if (copyNet == this) {
                            M selfref = temp.getNeighbour(module);
                            if (selfref == null) {
                                return;
                            }

                            temp.setNetworkOfPort(temp.portIndex(selfref), ((BaseGraph)copyNet).copyGraph(temp));
                        }

                    });
                    module.eachNeighbour((n) -> {
                        if (module.getNetworkOfPort((Integer)n.value) == this) {
                            M temp = (M)(n.key);
                            M selfref = temp.getNeighbour(module);
                            if (selfref == null) {
                                return;
                            }

                            G neiNet = temp.getNetworkOfPort(temp.portIndex(selfref));
                            if (!networksAdded.contains(neiNet)) {
                                networksAdded.add(neiNet);
                                ((BaseGraph)neiNet).rebuildGraphIndex(temp, temp.portIndex(selfref));
                            }
                        }

                    });
                    module.replaceNetwork(this, (BaseGraph)null);
                } else {
                    this.connected.remove(module);
                    module.eachNeighbourKey((n) -> n.removeNeighbour(module));
                    this.updateOnGraphChanged();
                }
            }
        }
    }

    public void rebuildGraph(M module) {
        this.rebuildGrpahWithSet(module, ObjectSet.with(new GraphModule[]{module}), -1);
    }

    public void rebuildGraphIndex(M module, int index) {
        this.rebuildGrpahWithSet(module, ObjectSet.with(new GraphModule[]{module}), index);
    }

    void rebuildGrpahWithSet(M root, ObjectSet<M> searched, int rootIndex) {
        BaseGraph<M, G>.GraphTree tree = new GraphTree(root, rootIndex);
        BaseGraph<M, G>.GraphTree current = tree;
        int total = 0;

        label87:
        while(current != null) {
            ++total;
            M buildConnector = current.module;
            GraphBlockBase.GraphBuildBase build = buildConnector.parent.build;
            int index = current.parentConnectPort;
            if (buildConnector.graph.accept == null) {
                return;
            }

            Seq<GraphData> acceptPorts = buildConnector.acceptPorts;
            if (index != -1) {
                acceptPorts = buildConnector.getConnectedNeighbours(index);
            }

            M prevModule = null;
            searched.add(buildConnector);
            int port = 0;

            for(int len = acceptPorts.size; port < len; ++port) {
                GraphData portInfo = (GraphData)acceptPorts.get(port);
                int portIndex = portInfo.index;
                if (buildConnector.getNetworkOfPort(portIndex) != null) {
                    if (!buildConnector.initialized()) {
                        return;
                    }

                    Tile tile = build.tile().nearby(portInfo.toPos);
                    if (tile == null) {
                        return;
                    }

                    Building var18 = tile.build;
                    if (var18 instanceof GraphBlockBase.GraphBuildBase) {
                        GraphBlockBase.GraphBuildBase other = (GraphBlockBase.GraphBuildBase)var18;
                        M conModule = other.getGraphConnector(root.type());
                        if (conModule != null && conModule != prevModule && !conModule.dead() && this.canConnect(current.module, conModule)) {
                            G thisGraph = buildConnector.getNetworkOfPort(portIndex);
                            if ((float)conModule.parent.build.rotation() != conModule.lastRecalc()) {
                                conModule.recalcPorts();
                            }

                            Point2 fPos = portInfo.fromPos.cpy();
                            fPos.x += build.tileX();
                            fPos.y += build.tileY();
                            int connectIndex = conModule.canConnect(fPos);
                            if (connectIndex != -1) {
                                buildConnector.addNeighbour(conModule, portIndex);
                                conModule.addNeighbour(buildConnector, connectIndex);
                                G conNet = conModule.getNetworkOfPort(connectIndex);
                                if (!thisGraph.connected.contains(conModule) && conNet != null && !buildConnector.hasNetwork(conNet)) {
                                    if (conNet.connected.contains(conModule)) {
                                        ((BaseGraph)thisGraph).mergeGraph(conNet);
                                    } else {
                                        ((BaseGraph)thisGraph).addBuilding(conModule, connectIndex);
                                    }

                                    if (!searched.contains(conModule)) {
                                        current.children.add(new GraphTree(current, conModule, connectIndex));
                                    }
                                }

                                prevModule = conModule;
                            }
                        }
                    }
                }
            }

            Seq<BaseGraph<M, G>.GraphTree> children = current.children;
            int i = 0;

            for(int childLen = children.size; i < childLen; ++i) {
                if (!((GraphTree)children.get(i)).complete) {
                    current = (GraphTree)children.get(i);
                    continue label87;
                }
            }

            current.complete = true;
            current = current.parent;
        }

    }

    String connectedToString() {
        StringBuilder s = new StringBuilder("Network:" + this.id + ":");
        OrderedSet.OrderedSetIterator var2 = this.connected.iterator();

        while(var2.hasNext()) {
            M build = (M)(var2.next());
            s.append(build.parent.build.block().localizedName).append(", ");
        }

        return s.toString();
    }

    class GraphTree {
        boolean complete;
        final BaseGraph<M, G>.GraphTree parent;
        final Seq<BaseGraph<M, G>.GraphTree> children;
        final M module;
        final int parentConnectPort;

        GraphTree(BaseGraph<M, G>.GraphTree parent, M root, int rootIndex) {
            this.children = new Seq(4);
            this.parent = parent;
            this.module = root;
            this.parentConnectPort = rootIndex;
        }

        GraphTree(M root, int rootIndex) {
            this((GraphTree)null, root, rootIndex);
        }
    }
}
