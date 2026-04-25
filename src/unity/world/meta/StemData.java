package unity.world.meta;

import arc.util.io.Reads;
import arc.util.io.Writes;

public class StemData {
    public StemData genericValue;
    public boolean boolValue;
    public byte byteValue;
    public short shortValue;
    public int intValue;
    public long longValue;
    public float floatValue;
    public double doubleValue;
    public String stringValue;

    public void write(Writes write) {
        write.bool(this.genericValue != null);
        if (this.genericValue != null) {
            this.genericValue.write(write);
        }

        write.bool(this.boolValue);
        write.b(this.byteValue);
        write.s(this.shortValue);
        write.i(this.intValue);
        write.l(this.longValue);
        write.f(this.floatValue);
        write.d(this.doubleValue);
        write.bool(this.stringValue != null);
        if (this.stringValue != null) {
            write.str(this.stringValue);
        }

    }

    public void read(Reads read) {
        boolean hasGeneric = read.bool();
        if (hasGeneric) {
            this.genericValue = new StemData();
            this.genericValue.read(read);
        }

        this.boolValue = read.bool();
        this.byteValue = read.b();
        this.shortValue = read.s();
        this.intValue = read.i();
        this.longValue = read.l();
        this.floatValue = read.f();
        this.doubleValue = read.d();
        if (read.bool()) {
            this.stringValue = read.str();
        }

    }
}
