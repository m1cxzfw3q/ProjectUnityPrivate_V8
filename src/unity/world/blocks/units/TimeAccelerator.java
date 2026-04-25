package unity.world.blocks.units;

import arc.scene.ui.layout.Table;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.entities.Units;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.logic.Ranged;
import mindustry.ui.Styles;
import mindustry.world.Block;

public class TimeAccelerator extends Block {
    public float accelTime = 300.0F;
    public float range = 100.0F;
    public float reload = 360.0F;
    public float boost = 3.5F;

    public TimeAccelerator(String name) {
        super(name);
        this.configurable = this.update = this.sync = this.solid = this.hasPower = this.hasLiquids = true;
        this.hasItems = this.hasLiquids = this.noUpdateDisabled = this.rotate = this.logicConfigurable = false;
        this.size = 3;
        this.timers = 2;
        this.config(Integer.class, (entity, value) -> entity.setTarget());
    }

    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);
        Drawf.dashCircle((float)(x * 8), (float)(y * 8), this.range, Pal.accent);
    }

    public class TimeAcceleratorBuild extends Building implements Ranged {
        public Teamc boostTarget;
        public int first = 1;
        public boolean isBoosted = false;

        public float range() {
            return TimeAccelerator.this.range;
        }

        public void created() {
            super.created();
            this.boostTarget = this.setTarget();
        }

        public void updateTile() {
            super.updateTile();
            if (this.boostTarget == null || !this.boostTarget.within(this, TimeAccelerator.this.range) || this.targetDead(this.boostTarget)) {
                this.setTarget();
            }

            if (this.boostTarget != null) {
                if (!this.timer(0, TimeAccelerator.this.reload) && !this.isBoosted) {
                    this.timer.reset(1, 0.0F);
                } else {
                    this.isBoosted = true;
                    this.timer.reset(0, 0.0F);
                }

                if (this.isBoosted) {
                    if (this.timer(1, TimeAccelerator.this.accelTime)) {
                        this.isBoosted = false;
                        this.resetBoost(this.boostTarget);
                    } else if (this.boostTarget instanceof Unit) {
                        ((Unit)this.boostTarget).speedMultiplier = TimeAccelerator.this.boost;
                    } else if (this.boostTarget instanceof Building) {
                        ((Building)this.boostTarget).applyBoost(TimeAccelerator.this.boost, 2.0F);
                    }
                }
            } else {
                this.timer.reset(0, 0.0F);
                this.timer.reset(1, 0.0F);
            }

        }

        public void buildConfiguration(Table table) {
            table.button(Icon.refresh, Styles.cleari, 40.0F, () -> this.configure(0)).size(60.0F).disabled((b) -> this.isBoosted);
        }

        public void write(Writes write) {
            super.write(write);
            write.i(this.first);
        }

        public void read(Reads read, byte revision) {
            super.read(read, revision);
            this.first = read.i();
        }

        public void resetBoost(Teamc e) {
            if (e instanceof Unit) {
                ((Unit)e).speedMultiplier = 1.0F;
            }

        }

        public boolean targetDead(Teamc e) {
            if (e instanceof Unit) {
                return ((Unit)e).dead();
            } else {
                return e instanceof Building ? ((Building)e).dead() : false;
            }
        }

        public Teamc setTarget() {
            return Units.bestTarget((Team)null, this.x, this.y, TimeAccelerator.this.range, (u) -> !u.dead() && u.speedMultiplier < 3.5F && !u.isPlayer(), (b) -> !b.proximity.contains(this) && b != this && !b.dead() && b.block.canOverdrive, (e, x, y) -> e.dst(this));
        }
    }
}
