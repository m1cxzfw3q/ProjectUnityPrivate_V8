package unity.v8;

import arc.graphics.*;
import arc.graphics.g2d.TextureRegion;

class OldDrawRequest implements Comparable<OldDrawRequest>{
    TextureRegion region = new TextureRegion();
    float x, y, z, originX, originY, width, height, rotation, color, mixColor;
    float[] vertices = new float[24];
    Texture texture;
    Blending blending;
    Runnable run;

    @Override
    public int compareTo(OldDrawRequest o){
        return Float.compare(z, o.z);
    }
}