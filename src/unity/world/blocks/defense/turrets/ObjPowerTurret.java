package unity.world.blocks.defense.turrets;

import arc.func.Cons;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.math.geom.Vec3;
import arc.util.Time;
import mindustry.world.blocks.defense.turrets.PowerTurret;
import unity.util.WavefrontObject;

public class ObjPowerTurret extends PowerTurret {
    public WavefrontObject object;

    public ObjPowerTurret(String name) {
        super(name);
    }

    public void load() {
        super.load();
        this.baseRegion = this.region;
    }

    public class ObjPowerTurretBuild extends PowerTurret.PowerTurretBuild {
        float time = 0.0F;
        float distortionTime = 0.0F;

        public ObjPowerTurretBuild() {
            super(ObjPowerTurret.this);
        }

        public void updateTile() {
            super.updateTile();
            if (Float.isNaN(this.time)) {
                this.time = 0.0F;
            }

            this.time += this.efficiency() * (1.0F + this.reload * 2.5F / ObjPowerTurret.this.reloadTime) * Time.delta;
            this.distortionTime = Math.max(0.0F, this.distortionTime - Time.delta * 0.2F);
        }

        public void damage(float damage) {
            this.distortionTime = Mathf.clamp(Mathf.sqrt(Math.max(0.0F, damage / 20.0F)), 0.0F, 3.0F);
            super.damage(damage);
        }

        protected float getDistortion() {
            return (Mathf.clamp(1.0F - this.healthf() * 2.0F) * 2.0F + this.distortionTime) / 16.0F;
        }

        public void draw() {
            Draw.rect(ObjPowerTurret.this.baseRegion, this.x, this.y);
            Draw.color();
            Cons<Vec3> distort = (v) -> {
                if (this.getDistortion() >= 0.001F) {
                    v.add(Mathf.range(this.getDistortion()), Mathf.range(this.getDistortion()), Mathf.range(this.getDistortion()));
                }

            };
            ObjPowerTurret.this.object.draw(this.x, this.y, Mathf.cos(this.time, 76.0F, 120.0F), Mathf.sin(this.time, 76.0F, 120.0F), -this.rotation, distort);
        }
    }
}
