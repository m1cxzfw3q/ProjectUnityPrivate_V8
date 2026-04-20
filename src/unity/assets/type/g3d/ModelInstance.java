package unity.assets.type.g3d;

import arc.func.Prov;
import arc.math.geom.Mat3D;
import arc.math.geom.Quat;
import arc.math.geom.Vec3;
import arc.struct.Seq;
import unity.assets.type.g3d.attribute.Material;

public class ModelInstance implements RenderableProvider {
    public final Seq<Material> materials;
    public final Seq<Node> nodes;
    public final Seq<Animation> animations;
    public final Model model;
    public Mat3D transform;
    public Object userData;

    public ModelInstance(Model model) {
        this(model, (String[])null);
    }

    public ModelInstance(Model model, String... rootNodeIds) {
        this(model, (Mat3D)null, rootNodeIds);
    }

    public ModelInstance(Model model, Mat3D transform, String... rootNodeIds) {
        this.materials = new Seq();
        this.nodes = new Seq();
        this.animations = new Seq();
        this.model = model;
        this.transform = transform == null ? new Mat3D() : transform;
        if (rootNodeIds == null) {
            this.copyNodes(model.nodes);
        } else {
            this.copyNodes(model.nodes, rootNodeIds);
        }

        this.copyAnimations(model.animations);
        this.calculateTransforms();
    }

    public ModelInstance(ModelInstance inst, Mat3D transform) {
        this.materials = new Seq();
        this.nodes = new Seq();
        this.animations = new Seq();
        this.model = inst.model;
        this.transform = transform == null ? new Mat3D() : transform;
        this.copyNodes(inst.nodes);
        this.copyAnimations(inst.animations);
        this.calculateTransforms();
    }

    public ModelInstance(ModelInstance copy) {
        this(copy, copy.transform.cpy());
    }

    public ModelInstance copy() {
        return new ModelInstance(this);
    }

    private void copyNodes(Seq<Node> nodes) {
        int i = 0;

        for(int n = nodes.size; i < n; ++i) {
            Node node = (Node)nodes.get(i);
            this.nodes.add(node.copy());
        }

        this.invalidate();
    }

    private void copyNodes(Seq<Node> nodes, String... nodeIds) {
        int i = 0;

        for(int n = nodes.size; i < n; ++i) {
            Node node = (Node)nodes.get(i);

            for(String nodeId : nodeIds) {
                if (nodeId.equals(node.id)) {
                    this.nodes.add(node.copy());
                    break;
                }
            }
        }

        this.invalidate();
    }

    public void copyAnimations(Iterable<Animation> source) {
        for(Animation anim : source) {
            this.copyAnimation(anim, true);
        }

    }

    public void copyAnimations(Iterable<Animation> source, boolean shareKeyframes) {
        for(Animation anim : source) {
            this.copyAnimation(anim, shareKeyframes);
        }

    }

    public void copyAnimation(Animation sourceAnim) {
        this.copyAnimation(sourceAnim, true);
    }

    public void copyAnimation(Animation sourceAnim, boolean shareKeyframes) {
        Animation animation = new Animation();
        animation.id = sourceAnim.id;
        animation.duration = sourceAnim.duration;

        for(NodeAnimation nanim : sourceAnim.nodeAnimations) {
            Node node = this.getNode(nanim.node.id);
            if (node != null) {
                NodeAnimation nodeAnim = new NodeAnimation();
                nodeAnim.node = node;
                if (shareKeyframes) {
                    nodeAnim.translation = nanim.translation;
                    nodeAnim.rotation = nanim.rotation;
                    nodeAnim.scaling = nanim.scaling;
                } else {
                    if (nanim.translation != null) {
                        nodeAnim.translation = new Seq();

                        for(NodeKeyframe<Vec3> kf : nanim.translation) {
                            nodeAnim.translation.add(new NodeKeyframe(kf.keytime, (Vec3)kf.value));
                        }
                    }

                    if (nanim.rotation != null) {
                        nodeAnim.rotation = new Seq();

                        for(NodeKeyframe<Quat> kf : nanim.rotation) {
                            nodeAnim.rotation.add(new NodeKeyframe(kf.keytime, (Quat)kf.value));
                        }
                    }

                    if (nanim.scaling != null) {
                        nodeAnim.scaling = new Seq();

                        for(NodeKeyframe<Vec3> kf : nanim.scaling) {
                            nodeAnim.scaling.add(new NodeKeyframe(kf.keytime, (Vec3)kf.value));
                        }
                    }
                }

                if (nodeAnim.translation != null || nodeAnim.rotation != null || nodeAnim.scaling != null) {
                    animation.nodeAnimations.add(nodeAnim);
                }
            }
        }

        if (animation.nodeAnimations.size > 0) {
            this.animations.add(animation);
        }

    }

    private void invalidate(Node node) {
        int i = 0;

        for(int n = node.parts.size; i < n; ++i) {
            NodePart part = (NodePart)node.parts.get(i);
            if (!this.materials.contains(part.material, true)) {
                int midx = this.materials.indexOf(part.material, false);
                if (midx < 0) {
                    this.materials.add(part.material = part.material.copy());
                } else {
                    part.material = (Material)this.materials.get(midx);
                }
            }
        }

        i = 0;

        for(int n = node.getChildCount(); i < n; ++i) {
            this.invalidate(node.getChild(i));
        }

    }

    private void invalidate() {
        int i = 0;

        for(int n = this.nodes.size; i < n; ++i) {
            this.invalidate((Node)this.nodes.get(i));
        }

    }

    public void getRenderables(Prov<Renderable> renders) {
        for(Node node : this.nodes) {
            this.getRenderables(node, renders);
        }

    }

    public void getRenderable(Renderable out, NodePart nodePart) {
        nodePart.setRenderable(out);
        if (this.transform != null) {
            out.worldTransform.set(this.transform);
        } else {
            out.worldTransform.idt();
        }

        out.userData = this.userData;
    }

    protected void getRenderables(Node node, Prov<Renderable> renders) {
        if (node.parts.size > 0) {
            for(NodePart nodePart : node.parts) {
                if (nodePart.enabled) {
                    this.getRenderable((Renderable)renders.get(), nodePart);
                }
            }
        }

        for(Node child : node.getChildren()) {
            this.getRenderables(child, renders);
        }

    }

    public void calculateTransforms() {
        int n = this.nodes.size;

        for(int i = 0; i < n; ++i) {
            ((Node)this.nodes.get(i)).calculateTransforms(true);
        }

    }

    public Material getMaterial() {
        return (Material)this.materials.firstOpt();
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

    public Animation getAnimation(String id) {
        return this.getAnimation(id, false);
    }

    public Animation getAnimation(String id, boolean ignoreCase) {
        int n = this.animations.size;
        if (ignoreCase) {
            for(int i = 0; i < n; ++i) {
                Animation animation;
                if ((animation = (Animation)this.animations.get(i)).id.equalsIgnoreCase(id)) {
                    return animation;
                }
            }
        } else {
            for(int i = 0; i < n; ++i) {
                Animation animation;
                if ((animation = (Animation)this.animations.get(i)).id.equals(id)) {
                    return animation;
                }
            }
        }

        return null;
    }
}
