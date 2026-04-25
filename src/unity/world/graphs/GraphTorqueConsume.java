package unity.world.graphs;

import arc.Core;
import arc.scene.ui.layout.Table;
import unity.world.modules.GraphTorqueConsumeModule;

public class GraphTorqueConsume extends GraphTorque {
    public final float nominalSpeed;
    public final float oversupplyFalloff;
    public final float idleFriction;
    public final float workingFriction;

    public GraphTorqueConsume(float inertia, float nominalS, float falloff, float idleF, float workingF) {
        super(idleF, inertia);
        this.nominalSpeed = nominalS;
        this.oversupplyFalloff = falloff;
        this.idleFriction = idleF;
        this.workingFriction = workingF;
    }

    public GraphTorqueConsume(float inertia, float nominalS, float idleF, float workingF) {
        this(inertia, nominalS, 0.7F, idleF, workingF);
    }

    public GraphTorqueConsume() {
        this.nominalSpeed = 10.0F;
        this.oversupplyFalloff = 0.7F;
        this.idleFriction = 0.01F;
        this.workingFriction = 0.1F;
    }

    public void setStatsExt(Table table) {
        table.row().left();
        table.add("[lightgray]" + Core.bundle.get("stat.unity.nominalspeed") + ":[] ").left();
        table.add(this.nominalSpeed * 0.1F + "rps");
        table.row().left();
        table.add("[lightgray]" + Core.bundle.get("stat.unity.idlefriction") + ":[] ").left();
        table.add(this.idleFriction * 1000.0F + "Nmv^-2");
        table.row().left();
        table.add("[lightgray]" + Core.bundle.get("stat.unity.workfriction") + ":[] ").left();
        table.add(this.workingFriction * 1000.0F + "Nmv^-2");
    }

    public GraphTorqueConsumeModule module() {
        return (new GraphTorqueConsumeModule()).graph(this);
    }

    boolean canBeMulti() {
        return false;
    }
}
