package unity.util;

import arc.math.Mathf;
import java.util.Arrays;

public class BoolGrid {
    boolean[] array;
    int width;
    int height;

    public void updateSize(int newWidth, int newHeight) {
        if (newWidth != this.width || newHeight != this.height) {
            this.array = new boolean[newWidth * newHeight];
        }

        this.width = newWidth;
        this.height = newHeight;
    }

    public void clear() {
        Arrays.fill(this.array, false);
    }

    public boolean within(int x, int y) {
        return x >= 0 && y >= 0 && x < this.width && y < this.height;
    }

    public boolean get(int x, int y) {
        return this.array[x + y * this.width];
    }

    public int clampX(int x) {
        return Mathf.clamp(x, 0, this.width - 1);
    }

    public int clampY(int y) {
        return Mathf.clamp(y, 0, this.height - 1);
    }

    public void set(int x, int y, boolean b) {
        this.array[x + y * this.width] = b;
    }
}
