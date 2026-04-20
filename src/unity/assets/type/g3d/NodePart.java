package unity.assets.type.g3d;

import unity.assets.type.g3d.attribute.Material;
import unity.graphics.MeshPart;

public class NodePart {
    public boolean enabled = true;
    public MeshPart meshPart;
    public Material material;

    public NodePart() {
    }

    public NodePart(MeshPart meshPart, Material material) {
        this.meshPart = meshPart;
        this.material = material;
    }

    public Renderable setRenderable(Renderable out) {
        out.material = this.material;
        out.meshPart.set(this.meshPart);
        return out;
    }

    public NodePart copy() {
        return (new NodePart()).set(this);
    }

    protected NodePart set(NodePart other) {
        this.meshPart = new MeshPart(other.meshPart);
        this.material = other.material;
        this.enabled = other.enabled;
        return this;
    }
}
