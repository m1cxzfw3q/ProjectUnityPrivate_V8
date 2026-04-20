package unity.sync.packets;

import arc.util.io.Reads;
import mindustry.net.Packet;

public abstract class BasePacket extends Packet {
    private byte[] data;

    public BasePacket() {
        this.data = NODATA;
    }

    public void read(Reads read, int length) {
        this.data = read.b(length);
    }

    public void handled() {
        BAIS.setBytes(this.data);
        this.readFields();
    }

    public void readFields() {
    }
}
