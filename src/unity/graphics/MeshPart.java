package unity.graphics;

import arc.graphics.Mesh;
import arc.graphics.VertexAttribute;
import arc.graphics.gl.Shader;
import arc.math.geom.BoundingBox;
import arc.math.geom.Mat3D;
import arc.math.geom.Vec3;
import arc.util.Structs;
import arc.util.Tmp;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class MeshPart {
    public String id;
    public int primitiveType;
    public int offset;
    public int size;
    public Mesh mesh;
    public final Vec3 center = new Vec3();
    private static final BoundingBox bounds = new BoundingBox();

    public MeshPart() {
    }

    public MeshPart(String id, Mesh mesh, int offset, int size, int type) {
        this.set(id, mesh, offset, size, type);
    }

    public MeshPart(MeshPart copyFrom) {
        this.set(copyFrom);
    }

    public MeshPart set(MeshPart other) {
        this.id = other.id;
        this.mesh = other.mesh;
        this.offset = other.offset;
        this.size = other.size;
        this.primitiveType = other.primitiveType;
        this.center.set(other.center);
        return this;
    }

    public MeshPart set(String id, Mesh mesh, int offset, int size, int type) {
        this.id = id;
        this.mesh = mesh;
        this.offset = offset;
        this.size = size;
        this.primitiveType = type;
        this.center.set(0.0F, 0.0F, 0.0F);
        return this;
    }

    public void calculateCenter() {
        this.extendBoundingBox((Mat3D)null);
        bounds.getCenter(this.center);
    }

    public void extendBoundingBox(Mat3D transform) {
        int numIndices = this.mesh.getNumIndices();
        int numVertices = this.mesh.getNumVertices();
        int max = numIndices == 0 ? numVertices : numIndices;
        if (this.offset >= 0 && this.size >= 1 && this.offset + this.size <= max) {
            FloatBuffer verts = this.mesh.vertices.buffer();
            ShortBuffer index = this.mesh.indices.buffer();
            int posIndex = Structs.indexOf(this.mesh.attributes, VertexAttribute.position3);
            if (posIndex < 0) {
                throw new IllegalStateException("Mesh has no position3 attribute");
            } else {
                int offset = 0;

                for(int i = 0; i < posIndex; ++i) {
                    offset += this.mesh.attributes[i].size;
                }

                int posoff = offset / 4;
                int vertSize = this.mesh.vertexSize / 4;
                int end = offset + this.size;
                if (numIndices > 0) {
                    for(int i = offset; i < end; ++i) {
                        int idx = (index.get(i) & '\uffff') * vertSize + posoff;
                        Tmp.v31.set(verts.get(idx), verts.get(idx + 1), verts.get(idx + 2));
                        if (transform != null) {
                            Mat3D.prj(Tmp.v31, transform);
                        }

                        bounds.ext(Tmp.v31);
                    }
                } else {
                    for(int i = offset; i < end; ++i) {
                        int idx = i * vertSize + posoff;
                        Tmp.v31.set(verts.get(idx), verts.get(idx + 1), verts.get(idx + 2));
                        bounds.ext(Tmp.v31);
                    }
                }

            }
        } else {
            throw new IllegalStateException("Invalid part specified ( offset=" + this.offset + ", count=" + this.size + ", max=" + max + " )");
        }
    }

    public boolean equals(MeshPart other) {
        return other == this || other != null && other.mesh == this.mesh && other.primitiveType == this.primitiveType && other.offset == this.offset && other.size == this.size;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        } else if (other == this) {
            return true;
        } else if (other instanceof MeshPart) {
            MeshPart mesh = (MeshPart)other;
            return this.equals(mesh);
        } else {
            return false;
        }
    }

    public void render(Shader shader) {
        this.mesh.render(shader, this.primitiveType, this.offset, this.size, true);
    }
}
