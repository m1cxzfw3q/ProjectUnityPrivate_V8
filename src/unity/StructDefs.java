package unity;

import arc.graphics.Color;
import arc.math.geom.Vec2;

final class StructDefs {
    Color colorStruct = new Color();
    Vec2 vec2Struct = new Vec2();

    class Float2Struct {
        float x;
        float y;
    }

    class Bool3Struct {
        boolean x;
        boolean y;
        boolean z;
    }
}
