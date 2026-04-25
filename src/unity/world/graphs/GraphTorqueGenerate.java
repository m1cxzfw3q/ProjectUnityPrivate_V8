package unity.world.graphs;

import arc.Core;
import arc.scene.ui.layout.Table;
import unity.world.modules.GraphTorqueGenerateModule;

public class GraphTorqueGenerate extends GraphTorque {
    public final float maxSpeed;
    public final float torqueCoeff;
    public final float maxTorque;
    public final float startTorque;

    public GraphTorqueGenerate(float friction, float inertia, float maxSpeed, float torqueCoeff, float maxTorque, float startTorque) {
        super(friction, inertia);
        this.maxSpeed = maxSpeed;
        this.torqueCoeff = torqueCoeff;
        this.maxTorque = maxTorque;
        this.startTorque = startTorque;
    }

    public GraphTorqueGenerate(float friction, float inertia, float maxSpeed, float maxTorque) {
        this(friction, inertia, maxSpeed, 1.0F, maxTorque, 5.0F);
    }

    public GraphTorqueGenerate() {
        this.maxSpeed = 10.0F;
        this.torqueCoeff = 1.0F;
        this.maxTorque = 5.0F;
        this.startTorque = 5.0F;
    }

    public void setStatsExt(Table table) {
        table.row().left();
        table.add("[lightgray]" + Core.bundle.get("stat.unity.maxspeed") + ":[] ").left();
        table.add(this.maxSpeed * 0.1F + "rps");
        table.row().left();
        table.add("[lightgray]" + Core.bundle.get("stat.unity.maxtorque") + ":[] ").left();
        table.add(this.maxTorque + "KNm");
    }

    public GraphTorqueGenerateModule module() {
        return (new GraphTorqueGenerateModule()).graph(this);
    }

    boolean canBeMulti() {
        return false;
    }
}
