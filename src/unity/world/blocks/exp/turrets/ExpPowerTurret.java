package unity.world.blocks.exp.turrets;

import arc.struct.ObjectMap;
import mindustry.entities.bullet.BulletType;
import mindustry.logic.LAccess;
import mindustry.world.blocks.defense.turrets.Turret;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatValues;
import unity.world.blocks.exp.ExpTurret;

public class ExpPowerTurret extends ExpTurret {
    public BulletType shootType;
    public float powerUse = 1.0F;

    public ExpPowerTurret(String name) {
        super(name);
        this.hasPower = true;
        this.envEnabled |= 2;
    }

    public void setStats() {
        super.setStats();
        this.stats.add(Stat.ammo, StatValues.ammo(ObjectMap.of(new Object[]{this, this.shootType})));
    }

    public void init() {
        this.consumes.powerCond(this.powerUse, Turret.TurretBuild::isActive);
        super.init();
    }

    public class ExpPowerTurretBuild extends ExpTurret.ExpTurretBuild {
        public ExpPowerTurretBuild() {
            super(ExpPowerTurret.this);
        }

        public void updateTile() {
            this.unit.ammo(this.power.status * (float)this.unit.type().ammoCapacity);
            super.updateTile();
        }

        public double sense(LAccess sensor) {
            double var10000;
            switch (sensor) {
                case ammo:
                    var10000 = (double)this.power.status;
                    break;
                case ammoCapacity:
                    var10000 = (double)1.0F;
                    break;
                default:
                    var10000 = super.sense(sensor);
            }

            return var10000;
        }

        public BulletType useAmmo() {
            return ExpPowerTurret.this.shootType;
        }

        public boolean hasAmmo() {
            return true;
        }

        public BulletType peekAmmo() {
            return ExpPowerTurret.this.shootType;
        }
    }
}
