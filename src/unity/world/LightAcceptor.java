package unity.world;

import arc.struct.Seq;
import mindustry.core.World;
import unity.gen.Light;
import unity.gen.LightHoldc;
import unity.world.meta.StemData;

public class LightAcceptor {
    public final LightAcceptorType type;
    public final LightHoldc.LightHoldBuildc hold;
    public final StemData data = new StemData();
    public Seq<Light> sources = new Seq(2);

    public LightAcceptor(LightAcceptorType type, LightHoldc.LightHoldBuildc hold) {
        this.type = type;
        this.hold = hold;
    }

    public float status() {
        return this.type.required <= 0.0F ? 1.0F : this.sources.sumf(Light::endStrength) / this.type.required;
    }

    public boolean fulfilled() {
        return !this.requires() || this.sources.sumf(Light::endStrength) >= this.type.required;
    }

    public boolean requires() {
        return this.type.required > 0.0F;
    }

    public boolean accepts(Light light, int x, int y) {
        int dx = World.toTile((float)(x * 8) - (this.hold.x() - (float)(this.hold.block().size * 8) / 2.0F + 4.0F));
        int dy = -World.toTile((float)(y * 8) - (this.hold.y() + (float)(this.hold.block().size * 8) / 2.0F - 4.0F));
        return dx >= this.type.x && dx < this.type.x + this.type.width && dy >= this.type.y && dy < this.type.y + this.type.height;
    }

    public void add(Light light) {
        this.sources.add(light);
    }

    public void remove(Light light) {
        this.sources.remove(light);
    }

    public void draw() {
        this.type.draw.get(this.hold, this);
    }

    public void update() {
        this.type.update.get(this.hold, this);
    }
}
