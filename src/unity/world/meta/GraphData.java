package unity.world.meta;

import arc.math.geom.Geometry;
import arc.math.geom.Point2;

public class GraphData {
    public final Point2 fromPos;
    public final Point2 toPos;
    public final int dir;
    public final int index;

    private GraphData(int fx, int fy, int tx, int ty, int dir, int index) {
        this.fromPos = new Point2(fx, fy);
        this.toPos = new Point2(tx, ty);
        this.dir = dir;
        this.index = index;
    }

    public static GraphData getConnectSidePos(int index, int size, int rotation) {
        int side = index / size;
        side = (side + rotation) % 4;
        Point2 normal = Geometry.d4((side + 3) % 4);
        Point2 tangent = Geometry.d4((side + 1) % 4);
        int originX = 0;
        int originY = 0;
        if (size > 1) {
            originX += size / 2;
            originY += size / 2;
            originY -= size - 1;
            if (side > 0) {
                for(int i = 1; i <= side; ++i) {
                    originX += Geometry.d4x(i) * (size - 1);
                    originY += Geometry.d4y(i) * (size - 1);
                }
            }

            originX += tangent.x * (index % size);
            originY += tangent.y * (index % size);
        }

        return new GraphData(originX, originY, originX + Geometry.d4x(side), originY + Geometry.d4y(side), side, index);
    }
}
