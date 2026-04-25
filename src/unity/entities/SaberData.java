package unity.entities;

import arc.math.WindowedMean;
import unity.graphics.FixedTrail;

public class SaberData {
    public float f;
    public float rot;
    public FixedTrail fT;
    public WindowedMean mean;

    public SaberData(float f, int ft, float rot, int mean) {
        this.f = f;
        this.fT = new FixedTrail(ft);
        this.rot = rot;
        this.mean = new WindowedMean(mean);
        this.mean.fill(0.0F);
    }
}
