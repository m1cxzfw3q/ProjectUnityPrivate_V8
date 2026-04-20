package unity.ai;

import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Interval;
import mindustry.core.World;
import mindustry.entities.Units;
import mindustry.entities.units.UnitController;
import mindustry.gen.Building;
import mindustry.gen.Healthc;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;
import mindustry.world.Tile;
import unity.Unity;
import unity.entities.Soul;
import unity.gen.MonolithSoul;
import unity.util.MathU;
import unity.world.MonolithWorld;

public class MonolithSoulAI implements UnitController {
    private static final Vec2 vec = new Vec2();
    protected MonolithSoul unit;
    protected Teamc joinTarget;
    protected MonolithWorld.Chunk formTarget;
    protected Interval timer = new Interval(2);

    public void unit(Unit unit) {
        this.unit = (MonolithSoul)unit;
    }

    public Unit unit() {
        return this.unit;
    }

    public void updateUnit() {
        if (this.timer.get(0, 12.0F)) {
            this.contemplate();
        }

        if (this.joinTarget != null) {
            MathU.addLength(vec.set(this.joinTarget).add(Mathf.randomSeedRange((long)this.unit.id, 24.0F), Mathf.randomSeedRange((long)(this.unit.id + 1), 24.0F)).sub(this.unit), -this.unit.type.range * 0.8F).limit(this.unit.type.speed);
            this.unit.moveAt(vec);
            this.unit.lookAt(this.unit.prefRotation());
            this.unit.join(this.joinTarget);
        } else if (this.formTarget != null) {
            MathU.addLength(vec.set(this.formTarget.centerX, this.formTarget.centerY).add(Mathf.randomSeedRange((long)this.unit.id, 24.0F), Mathf.randomSeedRange((long)(this.unit.id + 1), 24.0F)).sub(this.unit), -this.unit.type.range * 0.8F).limit(this.unit.type.speed);
            this.unit.moveAt(vec);
            this.unit.lookAt(this.unit.prefRotation());
            if (this.timer.get(1, 5.0F)) {
                MonolithWorld.Chunk in = this.formTarget.within(this.unit) ? this.formTarget : Unity.monolithWorld.getChunk(World.toTile(this.unit.x), World.toTile(this.unit.y));
                if (in != null) {
                    for(int i = 0; i < 3; ++i) {
                        Tile tile = (Tile)in.monolithTiles.random();
                        if (tile != null && !this.unit.forms().contains(tile) && this.unit.forms().size < 5) {
                            this.unit.form(tile);
                            break;
                        }
                    }
                }
            }
        }

    }

    public void contemplate() {
        if (!this.unit.corporeal()) {
            float delta = this.unit.lifeDelta();
            if (!this.unit.joining() && (!this.unit.forming() || !(delta > 0.0F))) {
                float range = this.unit.type.speed * (this.unit.health / -delta) / 2.0F;
                Unit vesselUnit = Units.closest(this.unit.team, this.unit.x, this.unit.y, range, this::accept);
                Building vesselBuild = Units.findAllyTile(this.unit.team, this.unit.x, this.unit.y, range, this::accept);
                this.joinTarget = (Teamc)(vesselUnit == null && vesselBuild == null ? null : (vesselUnit == null ? vesselBuild : (vesselBuild == null ? vesselUnit : (Math.max(this.unit.dst(vesselUnit) - vesselUnit.hitSize / 2.0F, 0.0F) <= Math.max(this.unit.dst(vesselBuild) - vesselBuild.hitSize() / 2.0F, 0.0F) ? vesselUnit : vesselBuild))));
                if (this.joinTarget == null) {
                    float r = range * range;
                    this.formTarget = Unity.monolithWorld.nearest(this.unit.x, this.unit.y, range, (c) -> (float)Math.min(c.monolithTiles.size, 5) * (r / this.unit.dst2(c.centerX, c.centerY)));
                } else {
                    this.formTarget = null;
                }
            }

        }
    }

    public <T extends Teamc & Healthc> boolean accept(T other) {
        Soul soul = Soul.toSoul(other);
        return soul != null && ((Healthc)other).isValid() && soul.acceptSoul(1) >= 1;
    }
}
