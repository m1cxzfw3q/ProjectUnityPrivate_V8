package unity.gen;

import arc.Core;
import arc.func.Cons;
import arc.math.Mathf;
import arc.util.Nullable;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.content.UnitTypes;
import mindustry.gen.BlockUnitc;
import mindustry.gen.Building;
import mindustry.gen.Entityc;
import mindustry.gen.Unit;
import mindustry.world.Block;
import mindustry.world.blocks.defense.turrets.Turret;
import mindustry.world.meta.Stat;
import unity.world.blocks.defense.turrets.GenericTractorBeamTurret;
import unity.world.meta.DynamicProgression;
import unity.world.meta.StemData;

public class SoulBlock extends Block implements Soulc, Stemc {
    public int maxSouls = 3;
    public float efficiencyFrom = 0.3F;
    public float efficiencyTo = 1.0F;
    public boolean requireSoul = true;
    public DynamicProgression progression = new DynamicProgression();
    protected Cons<Stemc.StemBuildc> drawStem = (e) -> {
    };
    protected Cons<Stemc.StemBuildc> updateStem = (e) -> {
    };

    public SoulBlock(String name) {
        super(name);
        this.update = true;
        this.destructible = true;
        this.sync = true;
    }

    public void setStats() {
        super.setStats();
        this.stats.add(Stat.abilities, (cont) -> {
            cont.row();
            cont.table((bt) -> {
                bt.left().defaults().padRight(3.0F).left();
                bt.row();
                bt.add(this.requireSoul ? "@soul.require" : "@soul.optional");
                if (this.maxSouls > 0) {
                    bt.row();
                    bt.add(Core.bundle.format("soul.max", new Object[]{this.maxSouls}));
                }

            });
        });
    }

    public <T extends Stemc.StemBuildc> void draw(Cons<T> draw) {
        this.drawStem = draw;
    }

    public <T extends Stemc.StemBuildc> void update(Cons<T> update) {
        this.updateStem = update;
    }

    public int maxSouls() {
        return this.maxSouls;
    }

    public void maxSouls(int maxSouls) {
        this.maxSouls = maxSouls;
    }

    public float efficiencyFrom() {
        return this.efficiencyFrom;
    }

    public void efficiencyFrom(float efficiencyFrom) {
        this.efficiencyFrom = efficiencyFrom;
    }

    public float efficiencyTo() {
        return this.efficiencyTo;
    }

    public void efficiencyTo(float efficiencyTo) {
        this.efficiencyTo = efficiencyTo;
    }

    public boolean requireSoul() {
        return this.requireSoul;
    }

    public void requireSoul(boolean requireSoul) {
        this.requireSoul = requireSoul;
    }

    public DynamicProgression progression() {
        return this.progression;
    }

    public void progression(DynamicProgression progression) {
        this.progression = progression;
    }

    public Cons<Stemc.StemBuildc> drawStem() {
        return this.drawStem;
    }

    public Cons<Stemc.StemBuildc> updateStem() {
        return this.updateStem;
    }

    public class SoulBuild extends Building implements Soulc.SoulBuildc, Stemc.StemBuildc {
        @Nullable
        public transient BlockUnitc unit;
        private int souls;
        protected transient StemData data = new StemData();

        public String toString() {
            return "SoulBuild#" + this.id;
        }

        public void onRemoved() {
            super.onRemoved();
            if (Vars.net.server() || !Vars.net.active()) {
                this.spreadSouls();
            }

        }

        public void unjoin() {
            if (this.souls > 0) {
                --this.souls;
            }

        }

        public Unit unit() {
            return (Unit)this.unit.as();
        }

        public void update() {
            SoulBlock.this.progression.apply((float)this.souls);
            super.update();
        }

        public void write(Writes write) {
            super.write(write);
            write.i(this.souls);
            this.data.write(write);
        }

        public boolean apply(MonolithSoul soul, int index, boolean transferred) {
            if (this.isControlled() && !transferred && (Mathf.chance((double)(1.0F / (float)this.souls)) || index == this.souls - 1)) {
                soul.controller(this.unit.getPlayer());
                transferred = true;
            }

            return transferred;
        }

        public void updateTile() {
            super.updateTile();
            SoulBlock.this.updateStem.get(this);
        }

        public int maxSouls() {
            return SoulBlock.this.maxSouls;
        }

        public int souls() {
            return this.souls;
        }

        public void read(Reads read, byte revision) {
            super.read(read, revision);
            this.souls = read.i();
            this.data.read(read);
        }

        public void created() {
            super.created();
            Entityc var3 = this.self();
            if (var3 instanceof Turret.TurretBuild) {
                Turret.TurretBuild build = (Turret.TurretBuild)var3;
                this.unit = build.unit;
            } else {
                var3 = this.self();
                if (var3 instanceof GenericTractorBeamTurret.GenericTractorBeamTurretBuild) {
                    GenericTractorBeamTurret<?>.GenericTractorBeamTurretBuild build = (GenericTractorBeamTurret.GenericTractorBeamTurretBuild)var3;
                    this.unit = build.unit;
                } else {
                    this.unit = (BlockUnitc)UnitTypes.block.create(this.team).as();
                    this.unit.tile(this);
                }
            }

        }

        public float efficiency() {
            float result = 1.0F;
            return SoulBlock.this.requireSoul && this.disabled() ? 0.0F : super.efficiency() * result * ((float)this.souls / (float)SoulBlock.this.maxSouls * (SoulBlock.this.efficiencyTo - SoulBlock.this.efficiencyFrom) + SoulBlock.this.efficiencyFrom);
        }

        public void join() {
            if (this.canJoin()) {
                ++this.souls;
            }

        }

        public void draw() {
            super.draw();
            SoulBlock.this.drawStem.get(this);
        }

        public boolean disabled() {
            return !this.hasSouls();
        }

        public void unit(BlockUnitc unit) {
            this.unit = unit;
        }

        public StemData data() {
            return this.data;
        }
    }
}
