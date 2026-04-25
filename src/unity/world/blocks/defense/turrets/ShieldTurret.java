package unity.world.blocks.defense.turrets;

import arc.math.Mathf;
import mindustry.entities.Units;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Building;
import mindustry.gen.Groups;
import mindustry.world.blocks.defense.turrets.PowerTurret;
import unity.entities.bullet.energy.ShieldBulletType;

public class ShieldTurret extends PowerTurret {
    public ShieldTurret(String name) {
        super(name);
    }

    public class ShieldTurretBuild extends PowerTurret.PowerTurretBuild {
        public boolean shield;

        public ShieldTurretBuild() {
            super(ShieldTurret.this);
        }

        public void bullet(BulletType type, float angle) {
            float spdScl = Mathf.clamp(Mathf.dst(this.x + ShieldTurret.this.tr.x, this.y + ShieldTurret.this.tr.y, this.targetPos.x, this.targetPos.y) / ShieldTurret.this.range, 0.0F, 1.0F);
            type.create(this, this.team, this.x + ShieldTurret.this.tr.x, this.y + ShieldTurret.this.tr.y, angle, spdScl, 1.0F);
        }

        public void findTarget() {
            this.target = Units.findAllyTile(this.team, this.x, this.y, ShieldTurret.this.range, (e) -> this.targetShield(e, this, 10.0F) && e != this);
        }

        public boolean validateTarget() {
            return this.target != null || this.isControlled() || this.logicControlled();
        }

        public boolean targetShield(Building t, ShieldTurretBuild b, float radius) {
            Groups.bullet.intersect(t.x - radius, t.y - radius, radius * 2.0F, radius * 2.0F, (e) -> {
                if (e != null && e.team == b.team && e.type instanceof ShieldBulletType) {
                    this.shield = true;
                }

            });
            this.shield = !this.shield;
            return t.damaged() && this.shield;
        }
    }
}
