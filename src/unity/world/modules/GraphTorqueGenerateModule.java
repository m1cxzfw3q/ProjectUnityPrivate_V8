package unity.world.modules;

import arc.Core;
import arc.math.WindowedMean;
import arc.scene.ui.layout.Table;
import arc.util.Strings;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import unity.util.Utils;
import unity.world.graph.TorqueGraph;
import unity.world.graphs.GraphTorqueGenerate;

public class GraphTorqueGenerateModule extends GraphTorqueModule<GraphTorqueGenerate> {
    final WindowedMean smoothedForce = new WindowedMean(40);
    float motorForceMult = 1.0F;
    float maxMotorForceMult = 1.0F;

    void updateExtension() {
        this.force = Utils.linear(((TorqueGraph)this.networks.get(0)).lastVelocity, ((GraphTorqueGenerate)this.graph).maxSpeed, ((GraphTorqueGenerate)this.graph).maxTorque, ((GraphTorqueGenerate)this.graph).torqueCoeff) * this.parent.build.edelta() * this.motorForceMult * this.maxMotorForceMult;
        this.smoothedForce.add(this.force);
    }

    void displayBars(Table table) {
        table.add(new Bar(() -> Core.bundle.get("stat.unity.torque") + ": " + Strings.fixed(this.smoothedForce.mean(), 1) + "/" + Strings.fixed(((GraphTorqueGenerate)this.graph).maxTorque * this.maxMotorForceMult, 1), () -> Pal.darkishGray, () -> this.smoothedForce.mean() / ((GraphTorqueGenerate)this.graph).maxTorque / this.maxMotorForceMult)).growX().row();
    }

    public GraphTorqueGenerateModule graph(GraphTorqueGenerate graph) {
        this.graph = graph;
        if (graph.isMultiConnector) {
            this.multi = true;
        }

        return this;
    }

    public void setMotorForceMult(float a) {
        this.motorForceMult = a;
    }
}
