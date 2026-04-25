package unity.world.blocks.units;

import arc.math.geom.Position;
import arc.struct.Seq;
import arc.util.Nullable;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.gen.BlockUnitc;
import mindustry.gen.Player;
import mindustry.gen.Unit;
import mindustry.type.UnitType;

public class ConversionPad extends MechPad {
    public Seq<UnitType[]> upgrades = new Seq();

    public ConversionPad(String name) {
        super(name);
    }

    public class ConversionPadBuild extends MechPad.MechPadBuild {
        UnitType resultUnit;
        boolean coreSpawn;

        public ConversionPadBuild() {
            super(ConversionPad.this);
        }

        public boolean inRange(Player player) {
            boolean isValid = false;

            for(UnitType[] unitTypes : ConversionPad.this.upgrades) {
                if (player.unit().type == unitTypes[0]) {
                    isValid = true;
                }
            }

            return super.inRange(player) && isValid;
        }

        public void configured(@Nullable Unit unit, @Nullable Object value) {
            if (unit != null && unit.isPlayer() && !(unit instanceof BlockUnitc)) {
                this.time = 0.0F;

                for(UnitType[] unitTypes : ConversionPad.this.upgrades) {
                    if (unit.type == unitTypes[0]) {
                        this.resultUnit = unitTypes[1];
                    }
                }

                this.coreSpawn = unit.spawnedByCore;
                unit.spawnedByCore = true;
                if (!Vars.net.client()) {
                    unit.getPlayer().unit(this.unit());
                }
            }

        }

        public UnitType getResultUnit() {
            return this.resultUnit;
        }

        public void finishUnit() {
            Player thisP = this.thisU.getPlayer();
            if (thisP != null) {
                Fx.spawn.at((Position)this.self());
                if (!Vars.net.client()) {
                    Unit unit = this.getResultUnit().create(this.team);
                    unit.set((Position)this.self());
                    unit.rotation = ConversionPad.this.spawnRot;
                    unit.impulse(0.0F, ConversionPad.this.spawnForce);
                    unit.set(this.getResultUnit(), thisP);
                    unit.spawnedByCore = this.coreSpawn;
                    unit.add();
                }

                if (Vars.state.isCampaign() && thisP == Vars.player) {
                    this.getResultUnit().unlock();
                }

                this.consume();
                this.time = 0.0F;
            }
        }
    }
}
