package unity.assets.loaders;

import arc.assets.AssetDescriptor;
import arc.assets.AssetLoaderParameters;
import arc.assets.AssetManager;
import arc.assets.loaders.FileHandleResolver;
import arc.assets.loaders.SynchronousAssetLoader;
import arc.files.Fi;
import arc.graphics.Color;
import arc.graphics.VertexAttribute;
import arc.math.geom.Quat;
import arc.math.geom.Vec2;
import arc.math.geom.Vec3;
import arc.struct.Seq;
import arc.util.Nullable;
import arc.util.serialization.BaseJsonReader;
import arc.util.serialization.JsonValue;
import unity.assets.type.g3d.Model;
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

public class ModelLoader extends SynchronousAssetLoader<Model, ModelParameter> {
    protected final BaseJsonReader reader;

    public ModelLoader(FileHandleResolver tree, BaseJsonReader reader) {
        super(tree);
        this.reader = reader;
    }

    public Model load(AssetManager manager, String fileName, Fi file, ModelParameter parameter) {
        Model model;
        if (parameter != null && parameter.model != null) {
            model = parameter.model;
        } else {
            model = new Model();
        }

        model.load(this.parseModel(file));
        return model;
    }

    public ModelData parseModel(Fi handle) {
        JsonValue json = this.reader.parse(handle);
        ModelData model = new ModelData();
        model.id = json.getString("id", "");
        this.parseMeshes(model, json);
        this.parseMaterials(model, json, handle.parent().path());
        this.parseNodes(model, json);
        this.parseAnimations(model, json);
        return model;
    }

    protected void parseMeshes(ModelData model, JsonValue json) {
        JsonValue meshes = json.get("meshes");
        if (meshes != null) {
            model.meshes.ensureCapacity(meshes.size);

            for(JsonValue mesh = meshes.child; mesh != null; mesh = mesh.next) {
                ModelMesh data = new ModelMesh();
                data.id = mesh.getString("id", "");
                JsonValue attributes = mesh.require("attributes");
                data.attributes = this.parseAttributes(attributes);
                data.vertices = mesh.require("vertices").asFloatSeq();
                JsonValue meshParts = mesh.require("parts");
                Seq<ModelMeshPart> parts = new Seq(ModelMeshPart.class);

                for(JsonValue meshPart = meshParts.child; meshPart != null; meshPart = meshPart.next) {
                    ModelMeshPart part = new ModelMeshPart();
                    String partId = meshPart.require("id").asString();
                    if (parts.contains((other) -> other.id.equals(partId))) {
                        throw new IllegalArgumentException("Mesh part with id '" + partId + "' already in defined");
                    }

                    part.id = partId;
                    part.primitiveType = this.parseType(meshPart.require("type").asString());
                    part.indices = meshPart.require("indices").asShortArray();
                    parts.add(part);
                }

                data.parts = (ModelMeshPart[])parts.toArray();
                model.meshes.add(data);
            }
        }

    }

    protected int parseType(String type) {
        byte var10000;
        switch (type) {
            case "POINTS":
                var10000 = 0;
                break;
            case "LINES":
                var10000 = 1;
                break;
            case "LINE_LOOP":
                var10000 = 2;
                break;
            case "LINE_STRIP":
                var10000 = 3;
                break;
            case "TRIANGLES":
                var10000 = 4;
                break;
            case "TRIANGLE_FAN":
                var10000 = 6;
                break;
            case "TRIANGLE_STRIP":
                var10000 = 5;
                break;
            default:
                throw new IllegalArgumentException("Unknown primitive type '" + type + "'");
        }

        return var10000;
    }

    protected VertexAttribute[] parseAttributes(JsonValue attributes) {
        Seq<VertexAttribute> vertexAttributes = new Seq(VertexAttribute.class);
        int texUnit = 0;
        int blendUnit = 0;

        for(JsonValue value = attributes.child; value != null; value = value.next) {
            String attr = value.asString();
            if (attr.equals("POSITION")) {
                vertexAttributes.add(VertexAttribute.position3);
            } else if (attr.equals("NORMAL")) {
                vertexAttributes.add(VertexAttribute.normal);
            } else if (attr.equals("COLORPACKED")) {
                vertexAttributes.add(VertexAttribute.color);
            } else if (attr.startsWith("TEXCOORD")) {
                vertexAttributes.add(new VertexAttribute(2, "a_texCoord" + texUnit++));
            } else {
                if (!attr.startsWith("BLENDWEIGHT")) {
                    throw new IllegalArgumentException("Unknown vertex attribute '" + attr + "'");
                }

                vertexAttributes.add(new VertexAttribute(2, "a_blendWeight" + blendUnit++));
            }
        }

        return (VertexAttribute[])vertexAttributes.toArray();
    }

    protected void parseMaterials(ModelData model, JsonValue json, String dir) {
        JsonValue materials = json.get("materials");
        if (materials != null) {
            model.materials.ensureCapacity(materials.size);

            for(JsonValue material = materials.child; material != null; material = material.next) {
                ModelMaterial data = new ModelMaterial();
                data.id = material.require("id").asString();
                JsonValue diffuse = material.get("diffuse");
                if (diffuse != null) {
                    data.diffuse = this.parseColor(diffuse);
                }

                JsonValue ambient = material.get("ambient");
                if (ambient != null) {
                    data.ambient = this.parseColor(ambient);
                }

                JsonValue emissive = material.get("emissive");
                if (emissive != null) {
                    data.emissive = this.parseColor(emissive);
                }

                JsonValue specular = material.get("specular");
                if (specular != null) {
                    data.specular = this.parseColor(specular);
                }

                JsonValue reflection = material.get("reflection");
                if (reflection != null) {
                    data.reflection = this.parseColor(reflection);
                }

                data.shininess = material.getFloat("shininess", 0.0F);
                data.opacity = material.getFloat("opacity", 1.0F);
                JsonValue textures = material.get("textures");
                if (textures != null) {
                    for(JsonValue texture = textures.child; texture != null; texture = texture.next) {
                        ModelTexture tex = new ModelTexture();
                        tex.id = texture.require("id").asString();
                        String fileName = texture.require("filename").asString();
                        tex.fileName = dir + (dir.length() != 0 && !dir.endsWith("/") ? "/" : "") + fileName;
                        tex.uvTranslation = this.readVec2(texture.get("uvTranslation"), 0.0F, 0.0F);
                        tex.uvScaling = this.readVec2(texture.get("uvScaling"), 1.0F, 1.0F);
                        tex.usage = this.parseTextureUsage(texture.require("type").asString());
                        if (data.textures == null) {
                            data.textures = new Seq(textures.size);
                        }

                        data.textures.add(tex);
                    }
                }

                model.materials.add(data);
            }
        }

    }

    protected int parseTextureUsage(String value) {
        byte var10000;
        switch (value.toUpperCase()) {
            case "AMBIENT":
                var10000 = 4;
                break;
            case "BUMP":
                var10000 = 8;
                break;
            case "DIFFUSE":
                var10000 = 2;
                break;
            case "EMISSIVE":
                var10000 = 3;
                break;
            case "NONE":
                var10000 = 1;
                break;
            case "NORMAL":
                var10000 = 7;
                break;
            case "REFLECTION":
                var10000 = 10;
                break;
            case "SHININESS":
                var10000 = 6;
                break;
            case "SPECULAR":
                var10000 = 5;
                break;
            case "TRANSPARENCY":
                var10000 = 9;
                break;
            default:
                var10000 = 0;
        }

        return var10000;
    }

    protected Color parseColor(JsonValue col) {
        if (col.size >= 3) {
            return new Color(col.getFloat(0), col.getFloat(1), col.getFloat(2), 1.0F);
        } else {
            throw new IllegalArgumentException("Expected Color values < 3");
        }
    }

    protected Vec2 readVec2(JsonValue vectorArray, float x, float y) {
        if (vectorArray == null) {
            return new Vec2(x, y);
        } else if (vectorArray.size == 2) {
            return new Vec2(vectorArray.getFloat(0), vectorArray.getFloat(1));
        } else {
            throw new IllegalArgumentException("Expected Vector2 values < 2");
        }
    }

    protected void parseNodes(ModelData model, JsonValue json) {
        JsonValue nodes = json.get("nodes");
        if (nodes != null) {
            model.nodes.ensureCapacity(nodes.size);

            for(JsonValue node = nodes.child; node != null; node = node.next) {
                model.nodes.add(this.parseNodesRecurse(node));
            }
        }

    }

    protected ModelNode parseNodesRecurse(JsonValue json) {
        ModelNode data = new ModelNode();
        data.id = json.require("id").asString();
        JsonValue trns = json.get("translation");
        data.translation = trns == null ? null : new Vec3(trns.getFloat(0), trns.getFloat(1), trns.getFloat(2));
        JsonValue rot = json.get("rotation");
        data.rotation = rot == null ? null : new Quat(rot.getFloat(0), rot.getFloat(1), rot.getFloat(2), rot.getFloat(3));
        JsonValue scl = json.get("scale");
        data.scale = scl == null ? null : new Vec3(scl.getFloat(0), scl.getFloat(1), scl.getFloat(2));
        String meshId = json.getString("mesh", (String)null);
        if (meshId != null) {
            data.meshId = meshId;
        }

        JsonValue parts = json.get("parts");
        if (parts != null) {
            data.parts = new ModelNodePart[parts.size];
            int i = 0;

            for(JsonValue material = parts.child; material != null; ++i) {
                ModelNodePart part = new ModelNodePart();
                part.materialId = material.require("materialid").asString();
                part.meshPartId = material.require("meshpartid").asString();
                data.parts[i] = part;
                material = material.next;
            }
        }

        JsonValue children = json.get("children");
        if (children != null) {
            data.children = new ModelNode[children.size];
            int i = 0;

            for(JsonValue child = children.child; child != null; ++i) {
                data.children[i] = this.parseNodesRecurse(child);
                child = child.next;
            }
        }

        return data;
    }

    protected void parseAnimations(ModelData model, JsonValue json) {
        JsonValue animations = json.get("animations");
        if (animations != null) {
            model.animations.ensureCapacity(animations.size);

            for(JsonValue anim = animations.child; anim != null; anim = anim.next) {
                JsonValue nodes = anim.get("bones");
                if (nodes != null) {
                    ModelAnimation animation = new ModelAnimation();
                    model.animations.add(animation);
                    animation.nodeAnimations.ensureCapacity(nodes.size);
                    animation.id = anim.getString("id");

                    for(JsonValue node = nodes.child; node != null; node = node.next) {
                        ModelNodeAnimation nodeAnim = new ModelNodeAnimation();
                        animation.nodeAnimations.add(nodeAnim);
                        nodeAnim.nodeId = node.getString("boneId");
                        JsonValue keyframes = node.get("keyframes");
                        if (keyframes != null && keyframes.isArray()) {
                            for(JsonValue keyframe = keyframes.child; keyframe != null; keyframe = keyframe.next) {
                                float keytime = keyframe.getFloat("keytime", 0.0F) / 33.333332F;
                                JsonValue translation = keyframe.get("translation");
                                if (translation != null && translation.size == 3) {
                                    if (nodeAnim.translation == null) {
                                        nodeAnim.translation = new Seq();
                                    }

                                    ModelNodeKeyframe<Vec3> tkf = new ModelNodeKeyframe();
                                    tkf.keytime = keytime;
                                    tkf.value = new Vec3(translation.getFloat(0), translation.getFloat(1), translation.getFloat(2));
                                    nodeAnim.translation.add(tkf);
                                }

                                JsonValue rotation = keyframe.get("rotation");
                                if (rotation != null && rotation.size == 4) {
                                    if (nodeAnim.rotation == null) {
                                        nodeAnim.rotation = new Seq();
                                    }

                                    ModelNodeKeyframe<Quat> rkf = new ModelNodeKeyframe();
                                    rkf.keytime = keytime;
                                    rkf.value = new Quat(rotation.getFloat(0), rotation.getFloat(1), rotation.getFloat(2), rotation.getFloat(3));
                                    nodeAnim.rotation.add(rkf);
                                }

                                JsonValue scale = keyframe.get("scale");
                                if (scale != null && scale.size == 3) {
                                    if (nodeAnim.scaling == null) {
                                        nodeAnim.scaling = new Seq();
                                    }

                                    ModelNodeKeyframe<Vec3> skf = new ModelNodeKeyframe();
                                    skf.keytime = keytime;
                                    skf.value = new Vec3(scale.getFloat(0), scale.getFloat(1), scale.getFloat(2));
                                    nodeAnim.scaling.add(skf);
                                }
                            }
                        }
                    }
                }
            }

        }
    }

    public Seq<AssetDescriptor> getDependencies(String fileName, Fi file, ModelParameter parameter) {
        return null;
    }

    public static class ModelParameter extends AssetLoaderParameters<Model> {
        @Nullable
        protected Model model;

        public ModelParameter(Model model) {
            this.model = model;
        }
    }
}
