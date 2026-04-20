package unity.assets.type.g3d;

import arc.math.geom.BoundingBox;
import arc.math.geom.Mat3D;
import arc.math.geom.Quat;
import arc.math.geom.Vec3;
import arc.struct.Seq;
import unity.graphics.MeshPart;

public class Node {
    public String id;
    public boolean inheritTransform = true;
    public boolean isAnimated = false;
    public final Vec3 translation = new Vec3();
    public final Quat rotation = new Quat(0.0F, 0.0F, 0.0F, 1.0F);
    public final Vec3 scale = new Vec3(1.0F, 1.0F, 1.0F);
    public final Mat3D localTransform = new Mat3D();
    public final Mat3D globalTransform = new Mat3D();
    public final Seq<NodePart> parts = new Seq(2);
    protected Node parent;
    private final Seq<Node> children = new Seq(2);

    public Mat3D calculateLocalTransform() {
        if (!this.isAnimated) {
            this.localTransform.set(this.translation, this.rotation, this.scale);
        }

        return this.localTransform;
    }

    public Mat3D calculateWorldTransform() {
        if (this.inheritTransform && this.parent != null) {
            this.globalTransform.set(this.parent.globalTransform).mul(this.localTransform);
        } else {
            this.globalTransform.set(this.localTransform);
        }

        return this.globalTransform;
    }

    public void calculateTransforms(boolean recursive) {
        this.calculateLocalTransform();
        this.calculateWorldTransform();
        if (recursive) {
            for(Node child : this.children) {
                child.calculateTransforms(true);
            }
        }

    }

    public BoundingBox extendBoundingBox(BoundingBox out) {
        return this.extendBoundingBox(out, true);
    }

    public BoundingBox extendBoundingBox(BoundingBox out, boolean transform) {
        int partCount = this.parts.size;

        for(int i = 0; i < partCount; ++i) {
            NodePart part = (NodePart)this.parts.get(i);
            if (part.enabled) {
                MeshPart meshPart = part.meshPart;
                meshPart.extendBoundingBox(transform ? this.globalTransform : null);
            }
        }

        int childCount = this.children.size;

        for(int i = 0; i < childCount; ++i) {
            ((Node)this.children.get(i)).extendBoundingBox(out);
        }

        return out;
    }

    public <T extends Node> void attachTo(T parent) {
        ((Node)parent).addChild(this);
    }

    public void detach() {
        if (this.parent != null) {
            this.parent.removeChild(this);
            this.parent = null;
        }

    }

    public boolean hasChildren() {
        return this.children.size > 0;
    }

    public int getChildCount() {
        return this.children.size;
    }

    public Node getChild(int index) {
        return (Node)this.children.get(index);
    }

    public Node getChild(String id, boolean recursive, boolean ignoreCase) {
        return getNode(this.children, id, recursive, ignoreCase);
    }

    public <T extends Node> int addChild(T child) {
        return this.insertChild(-1, child);
    }

    public <T extends Node> int addChildren(Iterable<T> nodes) {
        return this.insertChildren(-1, nodes);
    }

    public <T extends Node> int insertChild(int index, T child) {
        for(Node p = this; p != null; p = p.getParent()) {
            if (p == child) {
                throw new IllegalArgumentException("Cannot add a parent as a child");
            }
        }

        Node p = child.getParent();
        if (p != null && !p.removeChild(child)) {
            throw new IllegalArgumentException("Could not remove child from its current parent");
        } else {
            if (index >= 0 && index < this.children.size) {
                this.children.insert(index, child);
            } else {
                index = this.children.size;
                this.children.add(child);
            }

            child.parent = this;
            return index;
        }
    }

    public <T extends Node> int insertChildren(int index, Iterable<T> nodes) {
        if (index < 0 || index > this.children.size) {
            index = this.children.size;
        }

        int i = index;

        for(T child : nodes) {
            this.insertChild(i++, child);
        }

        return index;
    }

    public <T extends Node> boolean removeChild(T child) {
        if (!this.children.remove(child, true)) {
            return false;
        } else {
            child.parent = null;
            return true;
        }
    }

    public Iterable<Node> getChildren() {
        return this.children;
    }

    public Node getParent() {
        return this.parent;
    }

    public boolean hasParent() {
        return this.parent != null;
    }

    public Node copy() {
        return (new Node()).set(this);
    }

    protected Node set(Node other) {
        this.detach();
        this.id = other.id;
        this.inheritTransform = other.inheritTransform;
        this.translation.set(other.translation);
        this.rotation.set(other.rotation);
        this.scale.set(other.scale);
        this.localTransform.set(other.localTransform);
        this.globalTransform.set(other.globalTransform);
        this.parts.clear();

        for(NodePart nodePart : other.parts) {
            this.parts.add(nodePart.copy());
        }

        this.children.clear();

        for(Node child : other.getChildren()) {
            this.addChild(child.copy());
        }

        return this;
    }

    public static Node getNode(Seq<Node> nodes, String id, boolean recursive, boolean ignoreCase) {
        int n = nodes.size;
        if (ignoreCase) {
            for(int i = 0; i < n; ++i) {
                Node node;
                if ((node = (Node)nodes.get(i)).id.equalsIgnoreCase(id)) {
                    return node;
                }
            }
        } else {
            for(int i = 0; i < n; ++i) {
                Node node;
                if ((node = (Node)nodes.get(i)).id.equals(id)) {
                    return node;
                }
            }
        }

        if (recursive) {
            for(int i = 0; i < n; ++i) {
                Node node;
                if ((node = getNode(((Node)nodes.get(i)).children, id, true, ignoreCase)) != null) {
                    return node;
                }
            }
        }

        return null;
    }
}
