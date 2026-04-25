package unity.entities.units;

import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Time;
import unity.entities.Tentacle;
import unity.gen.UnityEntityMapping;

public class ApocalypseUnit extends EndInvisibleUnit implements TentaclesBase {
    Seq<Tentacle> tentacles;
    private float immunity = 1.0F;
    private final float[] invFrames = new float[5];

    public Seq<Tentacle> tentacles() {
        return this.tentacles;
    }

    public void tentacles(Seq<Tentacle> t) {
        this.tentacles = t;
    }

    public void overrideAntiCheatDamage(float v) {
        this.overrideAntiCheatDamage(v, 0);
    }

    public void overrideAntiCheatDamage(float v, int priority) {
        if (!(this.invFrames[Mathf.clamp(priority, 0, this.invFrames.length - 1)] < 30.0F)) {
            this.hitTime = 1.0F;
            this.invFrames[Mathf.clamp(priority, 0, this.invFrames.length - 1)] = 0.0F;
            this.lastHealth -= v;
            this.health -= v;
        }
    }

    public void damage(float amount) {
        if (!(this.invFrame < 30.0F)) {
            this.invFrame = 0.0F;
            float max = Math.max(220.0F, this.type.health / 700.0F);
            float trueAmount = Mathf.clamp(amount / this.immunity, 0.0F, max);
            max *= 1.5F;
            this.immunity = (float)((double)this.immunity + Math.pow((double)(Math.max(amount - max, 0.0F) / max), (double)2.0F) * (double)2.0F);
            this.lastHealth -= trueAmount;
            this.superDamage(trueAmount);
        }
    }

    public void update() {
        super.update();

        for(int i = 0; i < this.invFrames.length; ++i) {
            float[] var10000 = this.invFrames;
            var10000[i] += Time.delta;
        }

        this.immunity = Math.max(1.0F, this.immunity - Time.delta / 4.0F);
        this.updateTentacles();
    }

    public void add() {
        if (!this.added) {
            super.add();
            this.addTentacles();
        }
    }

    public int classId() {
        return UnityEntityMapping.classId(ApocalypseUnit.class);
    }
}
