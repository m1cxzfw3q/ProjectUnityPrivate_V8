package younggamExperimental;

import arc.struct.Seq;

public class StatContainer {
    public final Seq<Segment> segments = new Seq<>();
    public int inertia;
    public int hpinc;
    public int rangeInc;

    public void clear() {
        this.segments.clear();
        this.inertia = this.hpinc = this.rangeInc = 0;
    }
}
