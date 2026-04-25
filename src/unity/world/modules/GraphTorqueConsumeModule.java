package unity.world.modules;

import arc.math.Mathf;
import unity.world.graph.TorqueGraph;
import unity.world.graphs.GraphTorqueConsume;

public class GraphTorqueConsumeModule extends GraphTorqueModule<GraphTorqueConsume> {
    void updateExtension() {
        if (!this.parent.build.enabled()) {
            this.friction = ((GraphTorqueConsume)this.graph).idleFriction;
        } else {
            this.friction = ((GraphTorqueConsume)this.graph).workingFriction;
        }

    }

    float efficiency() {
        float ratio = ((TorqueGraph)this.networks.get(0)).lastVelocity / ((GraphTorqueConsume)this.graph).nominalSpeed;
        if (ratio > 1.0F) {
            ratio = Mathf.log2(ratio);
            ratio = 1.0F + ratio * ((GraphTorqueConsume)this.graph).oversupplyFalloff;
        }

        return ratio;
    }

    public GraphTorqueConsumeModule graph(GraphTorqueConsume graph) {
        this.graph = graph;
        if (graph.isMultiConnector) {
            this.multi = true;
        }

        return this;
    }
}
