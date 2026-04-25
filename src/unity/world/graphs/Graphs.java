package unity.world.graphs;

import arc.struct.ObjectSet;
import mindustry.world.meta.Stat;
import mindustry.world.meta.Stats;
import unity.world.meta.GraphType;
import unity.world.modules.GraphModules;

public class Graphs {
    private final Graph[] graphBlocks = new Graph[GraphType.values().length];
    private final ObjectSet<GraphType> results = new ObjectSet(4);
    boolean useOriginalUpdate = true;

    public <T extends Graph> T getGraphConnectorBlock(GraphType type) {
        if (this.graphBlocks[type.ordinal()] == null) {
            throw new IllegalArgumentException();
        } else {
            return (T)this.graphBlocks[type.ordinal()];
        }
    }

    public boolean hasGraph(GraphType type) {
        return this.results.contains(type);
    }

    public void setGraphConnectorTypes(Graph graph) {
        int i = graph.type().ordinal();
        this.graphBlocks[i] = graph;
        this.results.add(graph.type());
    }

    public void injectGraphConnector(GraphModules gms) {
        ObjectSet.ObjectSetIterator var2 = this.results.iterator();

        while(var2.hasNext()) {
            GraphType type = (GraphType)var2.next();
            int i = type.ordinal();
            gms.setGraphConnector(this.graphBlocks[i].module());
        }

    }

    public void setStats(Stats stats) {
        stats.add(Stat.abilities, (table) -> {
            ObjectSet.ObjectSetIterator var2 = this.results.iterator();

            while(var2.hasNext()) {
                GraphType type = (GraphType)var2.next();
                this.graphBlocks[type.ordinal()].setStats(table);
            }

        });
    }

    public void drawPlace(int x, int y, int size, int rotation, boolean valid) {
        ObjectSet.ObjectSetIterator var6 = this.results.iterator();

        while(var6.hasNext()) {
            GraphType type = (GraphType)var6.next();
            this.graphBlocks[type.ordinal()].drawPlace(x, y, size, rotation, valid);
        }

    }

    public boolean useOriginalUpdate() {
        return this.useOriginalUpdate;
    }

    public void disableOgUpdate() {
        this.useOriginalUpdate = false;
    }
}
