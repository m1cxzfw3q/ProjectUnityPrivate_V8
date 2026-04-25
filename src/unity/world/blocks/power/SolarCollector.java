package unity.world.blocks.power;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.struct.OrderedSet;
import arc.struct.Seq;
import unity.graphics.UnityDrawf;

public class SolarCollector extends HeatGenerator {
    public final TextureRegion[] regions = new TextureRegion[4];
    public TextureRegion lightRegion;

    public SolarCollector(String name) {
        super(name);
        this.rotate = this.solid = true;
    }

    public void load() {
        super.load();
        this.lightRegion = Core.atlas.find(this.name + "-light");

        for(int i = 0; i < 4; ++i) {
            this.regions[i] = Core.atlas.find(this.name + (i + 1));
        }

    }

    public class SolarCollectorBuild extends HeatGenerator.HeatGeneratorBuild {
        final OrderedSet<SolarReflector.SolarReflectorBuild> linkedReflect = new OrderedSet(8);
        float thermalPwr;

        public SolarCollectorBuild() {
            super(SolarCollector.this);
        }

        float getThermalPowerCoeff(SolarReflector.SolarReflectorBuild ref) {
            float dst = Mathf.dst(ref.x, ref.y, this.x, this.y);
            Point2 dir = Geometry.d4(this.rotation);
            return Mathf.clamp(((float)dir.x * (ref.x - this.x) / dst + (float)dir.y * (ref.y - this.y) / dst) * 1.5F);
        }

        void recalcThermalPwr() {
            this.thermalPwr = 0.0F;
            if (!this.linkedReflect.isEmpty()) {
                SolarReflector.SolarReflectorBuild i;
                for(OrderedSet.OrderedSetIterator var1 = this.linkedReflect.iterator(); var1.hasNext(); this.thermalPwr += this.getThermalPowerCoeff(i)) {
                    i = (SolarReflector.SolarReflectorBuild)var1.next();
                }

            }
        }

        public void appendSolarReflector(SolarReflector.SolarReflectorBuild ref) {
            this.linkedReflect.add(ref);
            this.recalcThermalPwr();
        }

        public void removeReflector(SolarReflector.SolarReflectorBuild ref) {
            if (this.linkedReflect.remove(ref)) {
                this.recalcThermalPwr();
            }

        }

        public void onDelete() {
            Seq<SolarReflector.SolarReflectorBuild> items = this.linkedReflect.orderedItems();

            while(!items.isEmpty()) {
                ((SolarReflector.SolarReflectorBuild)items.first()).setLink(-1);
            }

        }

        public void updatePost() {
            this.generateHeat(this.thermalPwr, this.thermalPwr);
        }

        public void draw() {
            Draw.rect(SolarCollector.this.regions[this.rotation], this.x, this.y);
            UnityDrawf.drawHeat(SolarCollector.this.heatRegion, this.x, this.y, this.rotdeg(), this.heat().getTemp());
            if (this.thermalPwr > 0.0F) {
                Draw.z(110.0F);
                Draw.color(this.thermalPwr, this.thermalPwr, this.thermalPwr);
                Draw.rect(SolarCollector.this.lightRegion, this.x, this.y, this.rotdeg());
                Draw.z();
            }

            this.drawTeamTop();
        }
    }
}
