package unity.world.blocks.defense.turrets;

import arc.math.geom.Vec2;
import mindustry.content.StatusEffects;
import mindustry.entities.Units;
import mindustry.gen.Healthc;
import mindustry.gen.Teamc;
import mindustry.gen.Unitc;
import mindustry.type.StatusEffect;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

public class HeatRayTurret extends GenericTractorBeamTurret<Teamc> {
    public boolean targetAir = true;
    public boolean targetGround = true;
    public float damage = 60.0F;
    public StatusEffect status;
    public float statusDuration;

    public HeatRayTurret(String name) {
        super(name);
        this.status = StatusEffects.melting;
        this.statusDuration = 60.0F;
    }

    public void setStats() {
        super.setStats();
        this.stats.add(Stat.damage, this.damage / 60.0F, StatUnit.perSecond);
        this.stats.add(Stat.targetsAir, this.targetAir);
        this.stats.add(Stat.targetsGround, this.targetGround);
    }

    public class HeatRayTurretBuild extends GenericTractorBeamTurret<Teamc>.GenericTractorBeamTurretBuild {
        public HeatRayTurretBuild() {
            super(HeatRayTurret.this);
        }

        protected void findTarget() {
            this.target = Units.closestTarget(this.team, this.x, this.y, HeatRayTurret.this.range, (unit) -> unit.team != this.team && unit.isValid() && unit.checkTarget(HeatRayTurret.this.targetAir, HeatRayTurret.this.targetGround), (tile) -> HeatRayTurret.this.targetGround && tile.isValid());
        }

        protected void findTarget(Vec2 pos) {
            this.target = Units.closestTarget(this.team, pos.x, pos.y, HeatRayTurret.this.laserWidth, (unit) -> unit.team != this.team && unit.isValid() && unit.checkTarget(HeatRayTurret.this.targetAir, HeatRayTurret.this.targetGround), (tile) -> HeatRayTurret.this.targetGround && tile.isValid());
        }

        protected void apply() {
            Teamc var2 = this.target;
            if (var2 instanceof Healthc) {
                Healthc h = (Healthc)var2;
                h.damageContinuous(HeatRayTurret.this.damage / 60.0F * this.efficiency());
            }

            var2 = this.target;
            if (var2 instanceof Unitc) {
                Unitc unit = (Unitc)var2;
                unit.apply(HeatRayTurret.this.status, HeatRayTurret.this.statusDuration);
            }

        }
    }
}
