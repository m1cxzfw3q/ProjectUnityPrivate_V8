package younggamExperimental;

public class Segment {
    public int damage;
    public int end;
    public final int start;

    public Segment(int start, int end, int damage) {
        this.start = start;
        this.end = end;
        this.damage = damage;
    }
}
