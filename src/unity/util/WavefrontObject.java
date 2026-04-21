package unity.util;

import arc.Core;
import arc.files.Fi;
import arc.func.Cons;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureAtlas;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.math.geom.Vec3;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Nullable;
import arc.util.Strings;
import arc.util.Tmp;
import java.io.BufferedReader;
import java.util.Arrays;
import java.util.Objects;
import mindustry.Vars;
import unity.Unity;

public class WavefrontObject {
    protected static final float zScale = 0.01F;
    protected static final float defaultScl = 4.0F;
    protected static final float perspectiveDistance = 350.0F;
    public Seq<Vec3> vertices = new Seq();
    public Seq<Vec2> uvs = new Seq();
    public Seq<Vec3> normals = new Seq();
    public Seq<Face> faces = new Seq();
    public String textureName = "";
    public ObjectMap<String, Material> materials;
    private final Seq<Vertex> drawnVertices = new Seq();
    private final Seq<Vec3> drawnNormals = new Seq();
    private TextureAtlas.AtlasRegion texture = null;
    private boolean hasMaterial = false;
    private boolean hasNormal = false;
    private boolean hasTexture = false;
    private boolean hasMaterialTex = false;
    private boolean odd = false;
    public ShadingType shadingType;
    public Color lightColor;
    public Color shadeColor;
    public float size;
    public float shadingSmoothness;
    public float drawLayer;
    protected int indexerA;
    protected float indexerZ;

    public WavefrontObject() {
        this.shadingType = WavefrontObject.ShadingType.normalAngle;
        this.lightColor = Color.white;
        this.shadeColor = Color.black;
        this.size = 1.0F;
        this.shadingSmoothness = 2.8F;
        this.drawLayer = 40.0F;
    }

    public void load(Fi file, @Nullable Fi material) {
        if (material != null) {
            BufferedReader matR = material.reader(64);
            Material current = null;

            while(true) {
                try {
                    String line = matR.readLine();
                    if (line == null) {
                        break;
                    }

                    if (line.contains("newmtl ")) {
                        current = new Material();
                        current.name = line.replaceFirst("newmtl ", "");
                        if (this.materials == null) {
                            this.materials = new ObjectMap();
                        }

                        this.materials.put(current.name, current);
                        this.hasMaterial = true;
                    }

                    if (line.startsWith("Ka ") && current != null) {
                        String[] val = line.replaceFirst("Ka ", "").split("\\s+");
                        float[] col = new float[3];
                        if (val.length != 3) {
                            throw new IllegalStateException("'Ka' must be followed with 3 arguments. Required: [r, g, b], found: " + Arrays.toString(val));
                        }

                        for(int i = 0; i < 3; ++i) {
                            col[i] = Strings.parseFloat(val[i], 0.0F);
                        }

                        Tmp.c1.set(col[0], col[1], col[2]).a(1.0F);
                        current.ambientCol = Tmp.c1.rgba8888();
                        if (!Tmp.c1.equals(Color.white)) {
                            current.hasColor = true;
                        }
                    }

                    if (line.startsWith("Kd ") && current != null) {
                        String[] val = line.replaceFirst("Kd ", "").split("\\s+");
                        float[] col = new float[3];
                        if (val.length != 3) {
                            throw new IllegalStateException("'Kd' must be followed with 3 arguments. Required: [r, g, b], found: " + Arrays.toString(val));
                        }

                        for(int i = 0; i < 3; ++i) {
                            col[i] = Strings.parseFloat(val[i], 0.0F);
                        }

                        Tmp.c1.set(col[0], col[1], col[2]).a(1.0F);
                        current.diffuseCol = Tmp.c1.rgba8888();
                        if (!Tmp.c1.equals(Color.white)) {
                            current.hasColor = true;
                        }
                    }

                    if (line.startsWith("Ke ") && current != null) {
                        String[] val = line.replaceFirst("Ke ", "").split("\\s+");
                        float[] col = new float[3];
                        if (val.length != 3) {
                            throw new IllegalStateException("'Ke' must be followed with 3 arguments. Required: [r, g, b], found: " + Arrays.toString(val));
                        }

                        for(int i = 0; i < 3; ++i) {
                            col[i] = Strings.parseFloat(val[i], 0.0F);
                        }

                        Tmp.c1.set(col[0], col[1], col[2]).a(1.0F);
                        current.emitCol = Tmp.c1.rgba8888();
                        if (!Tmp.c1.equals(Color.black)) {
                            current.hasColor = true;
                        }
                    }

                    if (line.contains("map_Kd ") && current != null) {
                        this.hasTexture = true;
                        this.hasMaterialTex = true;
                        if (this.canLoadTex()) {
                            String n = line.replaceFirst("map_Kd ", "");
                            current.diffTex = Core.atlas.find("unity-" + n);
                        }
                    }

                    if (line.contains("map_Ke ") && current != null && this.canLoadTex()) {
                        String n = line.replaceFirst("map_Ke ", "");
                        current.emitTex = Core.atlas.find("unity-" + n);
                    }
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            }
        }

        BufferedReader reader = file.reader(64);
        Material current = null;

        while(true) {
            try {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }

                if (line.contains("v ")) {
                    String[] pos = line.replaceFirst("v ", "").split("\\s+");
                    if (pos.length != 3) {
                        throw new IllegalStateException("'v' must define all 3 vector points");
                    }

                    float[] vec = new float[3];

                    for(int i = 0; i < 3; ++i) {
                        vec[i] = Strings.parseFloat(pos[i], 0.0F);
                    }

                    this.drawnVertices.add(new Vertex(vec[0], vec[1], vec[2]));
                    this.vertices.add(new Vec3(vec[0], vec[1], vec[2]));
                }

                if (line.contains("vt ")) {
                    if (!this.hasTexture) {
                        this.hasTexture = true;
                    }

                    String[] pos = line.replaceFirst("vt ", "").split("\\s+");
                    Vec2 uv = new Vec2();
                    uv.x = Strings.parseFloat(pos[0], 0.0F);
                    uv.y = Strings.parseFloat(pos[1], 0.0F);
                    this.uvs.add(uv);
                }

                if (line.contains("vn ")) {
                    if (!this.hasNormal) {
                        this.hasNormal = true;
                    }

                    String[] pos = line.replaceFirst("vn ", "").split("\\s+");
                    if (pos.length != 3) {
                        throw new IllegalStateException("'v' must define all 3 vector points");
                    }

                    float[] vec = new float[3];

                    for(int i = 0; i < 3; ++i) {
                        vec[i] = Strings.parseFloat(pos[i], 0.0F);
                    }

                    this.drawnNormals.add(new Vec3(vec[0], vec[1], vec[2]));
                    this.normals.add(new Vec3(vec[0], vec[1], vec[2]));
                }

                if (this.hasMaterial && line.contains("usemtl ")) {
                    String key = line.replace("usemtl ", "");
                    current = (Material)this.materials.get(key);
                }

                if (line.contains("f ")) {
                    String[] segments = line.replace("f ", "").split("\\s+");
                    Face face = new Face();
                    face.verts = new Vertex[segments.length];
                    if (this.hasNormal) {
                        face.normal = new Vec3[segments.length];
                    }

                    if (this.hasTexture) {
                        face.vertexTexture = new Vec2[segments.length];
                    }

                    if (this.hasMaterial && current != null) {
                        face.mat = current;
                    }

                    if (segments.length != 4) {
                        this.odd = true;
                    }

                    int[] i = new int[]{0};

                    for(String segment : segments) {
                        String[] faceIndex = segment.split("/");
                        Vertex vert = (Vertex)this.drawnVertices.get(getFaceVal(faceIndex[0]));
                        face.verts[i[0]] = vert;
                        if (this.hasNormal) {
                            face.normal[i[0]] = (Vec3)this.drawnNormals.get(getFaceVal(faceIndex[2]));
                        }

                        if (this.hasTexture) {
                            face.vertexTexture[i[0]] = (Vec2)this.uvs.get(getFaceVal(faceIndex[1]));
                        }

                        for(int sign : Mathf.signs) {
                            Vertex v = (Vertex)this.drawnVertices.get(faceVertIndex(segments[Mathf.mod(sign + i[0], segments.length)]));
                            if (!face.verts[i[0]].neighbors.contains(v)) {
                                face.verts[i[0]].neighbors.add(v);
                            }
                        }

                        face.size += 6;
                        int var10002 = i[0]++;
                    }

                    face.data = new float[face.size];
                    i[0] = 0;

                    for(Vertex vt : face.verts) {
                        vt.neighbors.each((vs) -> {
                            for(Vertex vc : face.verts) {
                                if (vs == vc) {
                                    return true;
                                }
                            }

                            return false;
                        }, (vs) -> {
                            face.shadingValue += vt.source.dst(vs.source);
                            int var10002 = i[0]++;
                        });
                    }

                    face.shadingValue /= (float)i[0];
                    this.faces.add(face);
                }
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        if (this.canLoadTex()) {
            this.texture = Core.atlas.find("unity-" + this.textureName + "-tex");
        }

        Unity.print(new Object[]{this.drawnVertices.size + " : " + this.faces.size});
    }

    private boolean canLoadTex() {
        return !Vars.headless && Core.atlas != null && this.hasTexture;
    }

    public void draw(float x, float y, float rX, float rY, float rZ) {
        this.draw(x, y, rX, rY, rZ, (Cons)null);
    }

    public void draw(float x, float y, float rX, float rY, float rZ, Cons<Vec3> cons) {
        float oz = Draw.z();

        for(int i = 0; i < this.drawnVertices.size; ++i) {
            Vec3 v = ((Vertex)this.drawnVertices.get(i)).source;
            v.set((Vec3)this.vertices.get(i));
            if (cons != null) {
                cons.get(v);
            }

            v.scl(4.0F * this.size).rotate(Vec3.X, rX).rotate(Vec3.Y, rY).rotate(Vec3.Z, rZ);
            float depth = Math.max(0.0F, (350.0F + v.z) / 350.0F);
            v.scl(depth);
            v.add(x, y, 0.0F);
            if (i <= this.drawnNormals.size - 1) {
                ((Vec3)this.drawnNormals.get(i)).set((Vec3)this.normals.get(i)).rotate(Vec3.X, rX).rotate(Vec3.Y, rY).rotate(Vec3.Z, rZ);
            }
        }

        for(Face face : this.faces) {
            this.indexerA = 0;
            this.indexerZ = 0.0F;

            for(Vertex vert : face.verts) {
                this.indexerZ += vert.source.z;
                ++this.indexerA;
            }

            this.indexerZ /= (float)this.indexerA;
            float z = this.indexerZ * 0.01F + this.drawLayer;
            Draw.z(z);
            if (!this.hasNormal || !(Math.abs(face.normal[0].angle(Vec3.Z)) >= 90.0F)) {
                switch (this.shadingType) {
                    case zMedian:
                        this.zMedianDraw(face);
                        break;
                    case zDistance:
                        this.zDistanceDraw(face);
                        break;
                    case normalAngle:
                        this.normalAngleDraw(face);
                        break;
                    default:
                        Draw.color(this.lightColor);
                }

                float color = Draw.getColor().toFloatBits();
                float mColor = Draw.getMixColor().toFloatBits();
                this.updateFace(face, color, mColor);
                if (this.odd && face.verts.length != 4) {
                    Objects.requireNonNull(face);
                    Draw.draw(z, face::draw);
                } else {
                    face.draw();
                }
            }
        }

        Draw.reset();
        Draw.z(oz);
    }

    protected void normalAngleDraw(Face face) {
        if (!this.hasNormal) {
            Draw.color(this.lightColor);
        } else {
            Vec3 tmp = Tmp.v31.setZero();
            this.indexerA = 0;

            for(Vec3 n : face.normal) {
                tmp.add(n);
                ++this.indexerA;
            }

            tmp.scl(1.0F / (float)this.indexerA);
            boolean matB = face.mat != null && face.mat.hasColor;
            if (matB) {
                Tmp.c2.rgba8888(face.mat.ambientCol).mul(this.shadeColor);
                Tmp.c3.rgba8888(face.mat.diffuseCol).mul(this.lightColor);
                Tmp.c4.rgba8888(face.mat.emitCol);
                Tmp.c2.r = Mathf.lerp(Tmp.c2.r, Tmp.c3.r, Tmp.c4.r);
                Tmp.c2.g = Mathf.lerp(Tmp.c2.g, Tmp.c3.g, Tmp.c4.g);
                Tmp.c2.b = Mathf.lerp(Tmp.c2.b, Tmp.c3.b, Tmp.c4.b);
            }

            float angle = Math.abs(tmp.angleRad(Vec3.Z)) / ((float)Math.PI / 4F) / this.shadingSmoothness;
            Tmp.c1.set(matB ? Tmp.c3 : this.lightColor).lerp(matB ? Tmp.c2 : this.shadeColor, Mathf.clamp(angle));
            Draw.color(Tmp.c1);
        }
    }

    protected void zMedianDraw(Face face) {
        this.indexerA = 0;
        this.indexerZ = 0.0F;

        for(Vertex vert : face.verts) {
            this.indexerZ += -vert.source.z;
            ++this.indexerA;
        }

        this.indexerZ /= (float)this.indexerA;
        Tmp.c1.set(this.lightColor).lerp(this.shadeColor, Mathf.clamp(this.indexerZ / face.shadingValue / (this.shadingSmoothness * 4.0F)));
        Draw.color(Tmp.c1);
    }

    protected void zDistanceDraw(Face face) {
        this.indexerA = 0;
        this.indexerZ = 0.0F;

        for(Vertex vert : face.verts) {
            vert.neighbors.each((vertex) -> {
                for(Vertex v : face.verts) {
                    if (v == vertex) {
                        return true;
                    }
                }

                return false;
            }, (vertex) -> {
                this.indexerZ += Math.abs(vertex.source.z - vert.source.z) / face.shadingValue / (this.shadingSmoothness * 4.0F);
                ++this.indexerA;
            });
        }

        this.indexerZ /= (float)this.indexerA;
        Tmp.c1.set(this.lightColor).lerp(this.shadeColor, Mathf.clamp(this.indexerZ));
        Draw.color(Tmp.c1);
    }

    protected void updateFace(Face face, float color, float mColor) {
        float[] dface = face.data;
        TextureAtlas.AtlasRegion textureB = this.texture;
        TextureAtlas.AtlasRegion region = Core.atlas.white();
        if (face.mat != null && face.mat.diffTex != null) {
            textureB = face.mat.diffTex;
        }

        for(int i = 0; i < face.verts.length; ++i) {
            int s = i * 6;
            dface[s] = face.verts[i].source.x;
            dface[s + 1] = face.verts[i].source.y;
            dface[s + 2] = color;
            if (this.hasTexture && textureB != null) {
                float u = textureB.u;
                float v = textureB.v;
                float u2 = textureB.u2;
                float v2 = textureB.v2;
                dface[s + 3] = Mathf.lerp(u, u2, face.vertexTexture[i].x);
                dface[s + 4] = Mathf.lerp(v2, v, face.vertexTexture[i].y);
            } else {
                dface[s + 3] = region.u;
                dface[s + 4] = region.v;
            }

            dface[s + 5] = mColor;
        }

    }

    protected static int faceVertIndex(String node) {
        return getFaceVal(node.split("/")[0]);
    }

    protected static int getFaceVal(String value) {
        return Strings.parseInt(value, 1) - 1;
    }

    public String toString() {
        return "WavefrontObject{vertices=" + this.vertices.size + ", faces=" + this.faces.size + ", shadingType=" + this.shadingType + '}';
    }

    public class Face {
        public Material mat;
        public Vertex[] verts;
        public Vec3[] normal;
        public Vec2[] vertexTexture;
        public float shadingValue = 0.0F;
        public int size = 0;
        public float[] data;

        protected void draw() {
            TextureAtlas.AtlasRegion textureB = WavefrontObject.this.texture;
            TextureAtlas.AtlasRegion region = Core.atlas.white();

            for(int f = 0; f < (this.mat != null && this.mat.emitTex != null ? 2 : 1); ++f) {
                boolean emit = f > 0;
                if (this.mat != null) {
                    textureB = f <= 0 ? this.mat.diffTex : this.mat.emitTex;
                }

                for(int i = 0; i < this.verts.length; ++i) {
                    int s = i * 6;
                    if (emit) {
                        this.data[s + 2] = Color.whiteFloatBits;
                    }
                }

                Draw.vert(textureB != null && WavefrontObject.this.hasTexture ? textureB.texture : region.texture, this.data, 0, this.data.length);
            }

        }
    }

    public static class Vertex {
        public Vec3 source;
        public Seq<Vertex> neighbors = new Seq();

        public Vertex(float x, float y, float z) {
            this.source = new Vec3(x, y, z);
        }
    }

    public static class Material {
        public String name;
        public int ambientCol = -1;
        public int diffuseCol = -1;
        public int emitCol = 0;
        public boolean hasColor = false;
        public TextureAtlas.AtlasRegion diffTex;
        public TextureAtlas.AtlasRegion emitTex;
    }

    public static enum ShadingType {
        zMedian,
        zDistance,
        normalAngle,
        noShading;
    }
}
