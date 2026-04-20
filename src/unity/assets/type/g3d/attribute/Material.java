package unity.assets.type.g3d.attribute;

import arc.struct.Seq;

public class Material extends Attributes {
    private static int counter = 0;
    public String id;

    public Material() {
        this("mtl" + ++counter);
    }

    public Material(String id) {
        this.id = id;
    }

    public Material(Attribute... attributes) {
        this.set(attributes);
    }

    public Material(String id, Attribute... attributes) {
        this(id);
        this.set(attributes);
    }

    public Material(Seq<Attribute> attributes) {
        this.set(attributes);
    }

    public Material(String id, Seq<Attribute> attributes) {
        this(id);
        this.set(attributes);
    }

    public Material(Material ref) {
        this(ref.id, ref);
    }

    public Material(String id, Material ref) {
        this(id);

        for(Attribute attr : ref) {
            this.set(attr.copy());
        }

    }

    public Material copy() {
        return new Material(this);
    }

    public int hashCode() {
        return super.hashCode() + 3 * this.id.hashCode();
    }

    public boolean equals(Object other) {
        boolean var10000;
        if (other instanceof Material) {
            Material mat = (Material)other;
            if (other == this || mat.id.equals(this.id) && super.equals(other)) {
                var10000 = true;
                return var10000;
            }
        }

        var10000 = false;
        return var10000;
    }
}
