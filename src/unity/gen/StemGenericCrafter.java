package unity.gen;

import arc.func.Cons;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.world.blocks.production.GenericCrafter;
import unity.world.meta.StemData;

public class StemGenericCrafter extends GenericCrafter implements Stemc {
    protected Cons<Stemc.StemBuildc> drawStem = (e) -> {
    };
    protected Cons<Stemc.StemBuildc> updateStem = (e) -> {
    };

    public StemGenericCrafter(String name) {
        super(name);
    }

    public <T extends Stemc.StemBuildc> void draw(Cons<T> draw) {
        this.drawStem = (Cons<StemBuildc>) draw;
    }

    public <T extends Stemc.StemBuildc> void update(Cons<T> update) {
        this.updateStem = (Cons<StemBuildc>) update;
    }

    public Cons<Stemc.StemBuildc> drawStem() {
        return this.drawStem;
    }

    public Cons<Stemc.StemBuildc> updateStem() {
        return this.updateStem;
    }

    public class StemGenericCrafterBuild extends GenericCrafter.GenericCrafterBuild implements Stemc.StemBuildc {
        protected transient StemData data = new StemData();

        public String toString() {
            return "StemGenericCrafterBuild#" + this.id;
        }

        public void draw() {
            super.draw();
            StemGenericCrafter.this.drawStem.get(this);
        }

        public void write(Writes write) {
            super.write(write);
            this.data.write(write);
        }

        public void updateTile() {
            super.updateTile();
            StemGenericCrafter.this.updateStem.get(this);
        }

        public void read(Reads read, byte revision) {
            super.read(read, revision);
            this.data.read(read);
        }

        public StemData data() {
            return this.data;
        }
    }
}
