package unity.world.modules;

import unity.world.graph.TorqueGraph;
import unity.world.graphs.GraphTorqueTrans;

public class GraphTorqueTransModule extends GraphTorqueModule<GraphTorqueTrans> {
    void updateExtension() {
        if (!this.networks.isEmpty() && !this.dead) {
            float[] ratios = ((GraphTorqueTrans)this.graph).ratio;
            float totalMRatio = 0.0F;
            float totalM = 0.0F;
            boolean allPositive = true;

            for(int i = 0; i < ratios.length; ++i) {
                TorqueGraph net = (TorqueGraph)this.networks.get(i);
                if (net == null) {
                    return;
                }

                totalMRatio += net.lastInertia * ratios[i];
                totalM += net.lastInertia * net.lastVelocity;
                allPositive &= net.lastInertia > 0.0F;
            }

            if (totalMRatio != 0.0F && totalM != 0.0F && allPositive) {
                for(int i = 0; i < ratios.length; ++i) {
                    TorqueGraph net = (TorqueGraph)this.networks.get(i);
                    float cratio = net.lastInertia * ratios[i] / totalMRatio;
                    net.lastVelocity = totalM * cratio / net.lastInertia;
                }
            }

        }
    }

    public GraphTorqueTransModule graph(GraphTorqueTrans graph) {
        this.graph = graph;
        if (graph.isMultiConnector) {
            this.multi = true;
        }

        return this;
    }
}
