package younggamExperimental;

import arc.struct.IntSeq;

public class IntPacker {
    public final IntSeq packed = new IntSeq();
    public final IntSeq raw = new IntSeq();
    int prev = -1;
    int count;
    int packIndex = -1;
    boolean highi;

    public void add(int bytef) {
        if (bytef != this.prev) {
            if (this.prev != -1) {
                if (!this.highi) {
                    this.packed.add(0);
                    ++this.packIndex;
                }

                this.raw.add(this.count);
                this.raw.add(this.prev);
                int comb = this.prev + this.count * 256;
                this.packed.incr(this.packIndex, comb << (this.highi ? 16 : 0));
                this.highi = !this.highi;
            }

            this.count = 1;
            this.prev = bytef;
        } else {
            ++this.count;
        }

    }

    public IntSeq end() {
        if (prev != -1) {
            if (!highi) {
                packed.add(0);
                ++packIndex;
            }

            raw.add(count);
            raw.add(prev);
            int comb = prev + count * 256;
            packed.incr(packIndex, comb << (highi ? 16 : 0));
            highi = !highi;
        }

        return packed;
    }

    public String toStringPack() {
        StringBuilder str = new StringBuilder();
        int i = 0;

        for(int len = this.raw.size; i < len; ++i) {
            str.append(this.raw.get(i));
        }

        return str.toString();
    }

    public static IntPacker packArray(IntSeq a) {
        IntPacker packer = new IntPacker();
        int i = 0;

        for(int len = a.size; i < len; ++i) {
            packer.add(a.get(i));
        }

        packer.end();
        return packer;
    }
}
