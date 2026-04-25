package unity.gen;

import arc.func.Cons;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Structs;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.Building;
import mindustry.world.blocks.defense.Wall;
import unity.world.LightAcceptor;
import unity.world.LightAcceptorType;
import unity.world.meta.StemData;

public class LightHoldWall extends Wall implements LightHoldc, Stemc {
    public Seq<LightAcceptorType> acceptors = new Seq();
    protected Cons<Stemc.StemBuildc> drawStem = (e) -> {
    };
    protected Cons<Stemc.StemBuildc> updateStem = (e) -> {
    };

    public LightHoldWall(String name) {
        super(name);
        this.update = true;
        this.sync = true;
        this.destructible = true;
    }

    public float getRotation(Building build) {
        return 0.0F;
    }

    public <T extends Stemc.StemBuildc> void draw(Cons<T> draw) {
        this.drawStem = draw;
    }

    public <T extends Stemc.StemBuildc> void update(Cons<T> update) {
        this.updateStem = update;
    }

    public Seq<LightAcceptorType> acceptors() {
        return this.acceptors;
    }

    public void acceptors(Seq<LightAcceptorType> acceptors) {
        this.acceptors = acceptors;
    }

    public Cons<Stemc.StemBuildc> drawStem() {
        return this.drawStem;
    }

    public Cons<Stemc.StemBuildc> updateStem() {
        return this.updateStem;
    }

    public class LightHoldWallBuild extends Wall.WallBuild implements LightHoldc.LightHoldBuildc, Stemc.StemBuildc {
        public transient LightAcceptor[] slots;
        protected transient boolean needsReinteract;
        protected transient StemData data = new StemData();

        public LightHoldWallBuild() {
            super(LightHoldWall.this);
        }

        public String toString() {
            return "LightHoldWallBuild#" + this.id;
        }

        public void draw() {
            super.draw();

            for(LightAcceptor slot : this.slots) {
                slot.draw();
            }

            LightHoldWall.this.drawStem.get(this);
        }

        public boolean requiresLight() {
            return !Structs.contains(this.slots, (e) -> !e.requires());
        }

        public float lightStatus() {
            if (this.slots.length <= 0) {
                return 1.0F;
            } else {
                float val = 0.0F;

                for(LightAcceptor slot : this.slots) {
                    val += Mathf.clamp(slot.status());
                }

                return Mathf.clamp(val / (float)this.slots.length);
            }
        }

        public void add(Light light, int x, int y) {
            for(LightAcceptor slot : this.slots) {
                if (slot.accepts(light, x, y)) {
                    slot.add(light);
                }
            }

        }

        public void remove(Light light) {
            for(LightAcceptor slot : this.slots) {
                slot.remove(light);
            }

        }

        public void updateTile() {
            super.updateTile();

            for(LightAcceptor slot : this.slots) {
                slot.update();
            }

            LightHoldWall.this.updateStem.get(this);
        }

        public void interact(Light light) {
            this.needsReinteract = false;
        }

        public boolean acceptLight(Light light, int x, int y) {
            return Structs.contains(this.slots, (e) -> e.accepts(light, x, y));
        }

        public void read(Reads read, byte revision) {
            super.read(read, revision);
            this.data.read(read);
        }

        public void created() {
            super.created();
            int len = LightHoldWall.this.acceptors.size;
            this.slots = new LightAcceptor[len];

            for(int i = 0; i < len; ++i) {
                this.slots[i] = ((LightAcceptorType)LightHoldWall.this.acceptors.get(i)).create(this);
            }

        }

        public float efficiency() {
            return super.efficiency() * (this.requiresLight() ? Math.min(this.lightStatus(), 1.0F) : 1.0F);
        }

        public void write(Writes write) {
            super.write(write);
            this.data.write(write);
        }

        public boolean consValid() {
            return super.consValid() && (!this.requiresLight() || !Structs.contains(this.slots, (e) -> !e.fulfilled()));
        }

        public LightAcceptor[] slots() {
            return this.slots;
        }

        public void slots(LightAcceptor[] slots) {
            this.slots = slots;
        }

        public boolean needsReinteract() {
            return this.needsReinteract;
        }

        public StemData data() {
            return this.data;
        }
    }
}
