package unity.assets.type.g3d;

import arc.graphics.Mesh;
import arc.graphics.Texture;
import arc.graphics.VertexAttribute;
import arc.math.geom.Quat;
import arc.math.geom.Vec3;
import arc.struct.Seq;
import arc.util.Buffers;
import arc.util.Disposable;
import mindustry.Vars;
import unity.assets.type.g3d.attribute.Material;
import unity.assets.type.g3d.attribute.type.BlendingAttribute;
import unity.assets.type.g3d.attribute.type.ColorAttribute;
import unity.assets.type.g3d.attribute.type.FloatAttribute;
import unity.assets.type.g3d.attribute.type.TextureAttribute;
import unity.assets.type.g3d.model.ModelAnimation;
import unity.assets.type.g3d.model.ModelData;
import unity.assets.type.g3d.model.ModelMaterial;
import unity.assets.type.g3d.model.ModelMesh;
import unity.assets.type.g3d.model.ModelMeshPart;
import unity.assets.type.g3d.model.ModelNode;
import unity.assets.type.g3d.model.ModelNodeAnimation;
import unity.assets.type.g3d.model.ModelNodeKeyframe;
import unity.assets.type.g3d.model.ModelNodePart;
import unity.assets.type.g3d.model.ModelTexture;
import unity.graphics.MeshPart;

public class Model implements Disposable {
    public final Seq<Material> materials = new Seq();
    public final Seq<Node> nodes = new Seq();
    public final Seq<Animation> animations = new Seq();
    public final Seq<Mesh> meshes = new Seq();
    public final Seq<MeshPart> meshParts = new Seq();

    public Model() {
    }

    public Model(ModelData modelData) {
        this.load(modelData);
    }

    public void load(ModelData data) {
        this.loadMeshes(data.meshes);
        this.loadMaterials(data.materials);
        this.loadNodes(data.nodes);
        this.loadAnimations(data.animations);
        this.calculateTransforms();
    }

    protected void loadAnimations(Iterable<ModelAnimation> modelAnimations) {
        for(ModelAnimation anim : modelAnimations) {
            Animation animation = new Animation();
            animation.id = anim.id;

            for(ModelNodeAnimation nanim : anim.nodeAnimations) {
                Node node = this.getNode(nanim.nodeId);
                if (node != null) {
                    NodeAnimation nodeAnim = new NodeAnimation();
                    nodeAnim.node = node;
                    if (nanim.translation != null) {
                        nodeAnim.translation = new Seq();
                        nodeAnim.translation.ensureCapacity(nanim.translation.size);

                        for(ModelNodeKeyframe<Vec3> kf : nanim.translation) {
                            if (kf.keytime > animation.duration) {
                                animation.duration = kf.keytime;
                            }

                            nodeAnim.translation.add(new NodeKeyframe(kf.keytime, new Vec3(kf.value == null ? node.translation : (Vec3)kf.value)));
                        }
                    }

                    if (nanim.rotation != null) {
                        nodeAnim.rotation = new Seq();
                        nodeAnim.rotation.ensureCapacity(nanim.rotation.size);

                        for(ModelNodeKeyframe<Quat> kf : nanim.rotation) {
                            if (kf.keytime > animation.duration) {
                                animation.duration = kf.keytime;
                            }

                            nodeAnim.rotation.add(new NodeKeyframe(kf.keytime, new Quat(kf.value == null ? node.rotation : (Quat)kf.value)));
                        }
                    }

                    if (nanim.scaling != null) {
                        nodeAnim.scaling = new Seq();
                        nodeAnim.scaling.ensureCapacity(nanim.scaling.size);

                        for(ModelNodeKeyframe<Vec3> kf : nanim.scaling) {
                            if (kf.keytime > animation.duration) {
                                animation.duration = kf.keytime;
                            }

                            nodeAnim.scaling.add(new NodeKeyframe(kf.keytime, new Vec3(kf.value == null ? node.scale : (Vec3)kf.value)));
                        }
                    }

                    if (nodeAnim.translation != null && nodeAnim.translation.any() || nodeAnim.rotation != null && nodeAnim.rotation.any() || nodeAnim.scaling != null && nodeAnim.scaling.any()) {
                        animation.nodeAnimations.add(nodeAnim);
                    }
                }
            }

            if (animation.nodeAnimations.size > 0) {
                this.animations.add(animation);
            }
        }

    }

    protected void loadNodes(Iterable<ModelNode> modelNodes) {
        for(ModelNode node : modelNodes) {
            this.nodes.add(this.loadNode(node));
        }

    }

    protected Node loadNode(ModelNode modelNode) {
        Node node = new Node();
        node.id = modelNode.id;
        if (modelNode.translation != null) {
            node.translation.set(modelNode.translation);
        }

        if (modelNode.rotation != null) {
            node.rotation.set(modelNode.rotation);
        }

        if (modelNode.scale != null) {
            node.scale.set(modelNode.scale);
        }

        if (modelNode.parts != null) {
            for(ModelNodePart modelNodePart : modelNode.parts) {
                MeshPart meshPart = null;
                Material meshMaterial = null;
                if (modelNodePart.meshPartId != null) {
                    for(MeshPart part : this.meshParts) {
                        if (modelNodePart.meshPartId.equals(part.id)) {
                            meshPart = part;
                            break;
                        }
                    }
                }

                if (modelNodePart.materialId != null) {
                    for(Material material : this.materials) {
                        if (modelNodePart.materialId.equals(material.id)) {
                            meshMaterial = material;
                            break;
                        }
                    }
                }

                if (meshPart == null || meshMaterial == null) {
                    throw new IllegalArgumentException("Invalid node: " + node.id);
                }

                NodePart nodePart = new NodePart();
                nodePart.meshPart = meshPart;
                nodePart.material = meshMaterial;
                node.parts.add(nodePart);
            }
        }

        if (modelNode.children != null) {
            for(ModelNode child : modelNode.children) {
                node.addChild(this.loadNode(child));
            }
        }

        return node;
    }

    protected void loadMeshes(Seq<ModelMesh> meshes) {
        for(ModelMesh mesh : meshes) {
            this.convertMesh(mesh);
        }

    }

    protected void convertMesh(ModelMesh modelMesh) {
        int numIndices = 0;

        for(ModelMeshPart part : modelMesh.parts) {
            numIndices += part.indices.length;
        }

        boolean hasIndices = numIndices > 0;
        int vertSize = 0;

        for(VertexAttribute vert : modelMesh.attributes) {
            vertSize += vert.size;
        }

        int numVertices = modelMesh.vertices.length / (vertSize / 4);
        Mesh mesh = new Mesh(true, numVertices, numIndices, modelMesh.attributes);
        this.meshes.add(mesh);
        Buffers.copy(modelMesh.vertices, mesh.getVerticesBuffer(), modelMesh.vertices.length, 0);
        int offset = 0;
        mesh.getIndicesBuffer().clear();

        for(ModelMeshPart part : modelMesh.parts) {
            MeshPart meshPart = new MeshPart();
            meshPart.id = part.id;
            meshPart.primitiveType = part.primitiveType;
            meshPart.offset = offset;
            meshPart.size = hasIndices ? part.indices.length : numVertices;
            meshPart.mesh = mesh;
            if (hasIndices) {
                mesh.getIndicesBuffer().put(part.indices);
            }

            offset += meshPart.size;
            this.meshParts.add(meshPart);
        }

        mesh.getIndicesBuffer().position(0);

        for(MeshPart part : this.meshParts) {
            part.calculateCenter();
        }

    }

    protected void loadMaterials(Seq<ModelMaterial> modelMaterials) {
        for(ModelMaterial mtl : modelMaterials) {
            this.materials.add(this.convertMaterial(mtl));
        }

    }

    protected Material convertMaterial(ModelMaterial mtl) {
        Material result = new Material();
        result.id = mtl.id;
        if (mtl.ambient != null) {
            result.set(new ColorAttribute(ColorAttribute.ambient, mtl.ambient));
        }

        if (mtl.diffuse != null) {
            result.set(new ColorAttribute(ColorAttribute.diffuse, mtl.diffuse));
        }

        if (mtl.specular != null) {
            result.set(new ColorAttribute(ColorAttribute.specular, mtl.specular));
        }

        if (mtl.emissive != null) {
            result.set(new ColorAttribute(ColorAttribute.emissive, mtl.emissive));
        }

        if (mtl.reflection != null) {
            result.set(new ColorAttribute(ColorAttribute.reflection, mtl.reflection));
        }

        if (mtl.shininess > 0.0F) {
            result.set(new FloatAttribute(FloatAttribute.shininess, mtl.shininess));
        }

        if (mtl.opacity != 1.0F) {
            result.set(new BlendingAttribute(770, 771, mtl.opacity));
        }

        if (mtl.textures != null) {
            for(ModelTexture tex : mtl.textures) {
                Texture texture = new Texture(Vars.tree.get(tex.fileName));
                float offsetU = tex.uvTranslation == null ? 0.0F : tex.uvTranslation.x;
                float offsetV = tex.uvTranslation == null ? 0.0F : tex.uvTranslation.y;
                float scaleU = tex.uvScaling == null ? 1.0F : tex.uvScaling.x;
                float scaleV = tex.uvScaling == null ? 1.0F : tex.uvScaling.y;
                TextureAttribute var10001 = new TextureAttribute;
                long var10003;
                switch (tex.usage) {
                    case 2:
                        var10003 = TextureAttribute.diffuse;
                        break;
                    case 3:
                        var10003 = TextureAttribute.emissive;
                        break;
                    case 4:
                        var10003 = TextureAttribute.ambient;
                        break;
                    case 5:
                        var10003 = TextureAttribute.specular;
                        break;
                    case 6:
                    case 9:
                    default:
                        throw new IllegalArgumentException("Unknown usage: " + tex.usage);
                    case 7:
                        var10003 = TextureAttribute.normal;
                        break;
                    case 8:
                        var10003 = TextureAttribute.bump;
                        break;
                    case 10:
                        var10003 = TextureAttribute.reflection;
                }

                var10001.<init>(var10003, texture, offsetU, offsetV, scaleU, scaleV);
                result.set(var10001);
            }
        }

        return result;
    }

    public void dispose() {
        this.materials.clear();
        this.nodes.clear();
        this.meshes.clear();
        this.meshParts.clear();
    }

    public void calculateTransforms() {
        int n = this.nodes.size;

        for(int i = 0; i < n; ++i) {
            ((Node)this.nodes.get(i)).calculateTransforms(true);
        }

    }

    public Material getMaterial(String id) {
        return this.getMaterial(id, true);
    }

    public Material getMaterial(String id, boolean ignoreCase) {
        int n = this.materials.size;
        if (ignoreCase) {
            for(int i = 0; i < n; ++i) {
                Material material;
                if ((material = (Material)this.materials.get(i)).id.equalsIgnoreCase(id)) {
                    return material;
                }
            }
        } else {
            for(int i = 0; i < n; ++i) {
                Material material;
                if ((material = (Material)this.materials.get(i)).id.equals(id)) {
                    return material;
                }
            }
        }

        return null;
    }

    public Node getNode(String id) {
        return this.getNode(id, true);
    }

    public Node getNode(String id, boolean recursive) {
        return this.getNode(id, recursive, false);
    }

    public Node getNode(String id, boolean recursive, boolean ignoreCase) {
        return Node.getNode(this.nodes, id, recursive, ignoreCase);
    }
}
