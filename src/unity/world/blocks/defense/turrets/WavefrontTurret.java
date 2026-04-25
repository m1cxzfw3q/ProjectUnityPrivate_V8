package unity.world.blocks.defense.turrets;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec3;
import arc.util.Time;
import arc.util.Tmp;
import java.util.Objects;
import mindustry.entities.bullet.BulletType;
import mindustry.world.blocks.defense.turrets.PowerTurret;
import unity.assets.type.g3d.AnimControl;
import unity.assets.type.g3d.Model;
import unity.assets.type.g3d.ModelInstance;
import unity.util.Utils;

public class WavefrontTurret extends PowerTurret {
    public Model model;
    public float scale = 2.4F;
    public float objectRotationSpeed = 7.0F;

    public WavefrontTurret(String name) {
        super(name);
        this.recoilAmount = 6.0F;
    }

    public void load() {
        super.load();
        this.baseRegion = Core.atlas.find(this.name + "-base");
    }

    public class WavefrontTurretBuild extends PowerTurret.PowerTurretBuild {
        public ModelInstance inst;
        public AnimControl cont;
        float gap = 0.0F;
        float offset = 0.0F;
        float angle = 0.0F;
        float waitTime = 0.0F;
        float animTime = 0.0F;

        public WavefrontTurretBuild() {
            super(WavefrontTurret.this);
        }

        public void created() {
            super.created();
            this.inst = new ModelInstance(WavefrontTurret.this.model);
            this.cont = new AnimControl(this.inst);
        }

        public void updateTile() {
            super.updateTile();
            if (this.isShooting() && this.consValid()) {
                this.gap = Math.min(0.5F, this.gap + 0.005F * Time.delta);
                this.angle += this.reload / WavefrontTurret.this.reloadTime * WavefrontTurret.this.objectRotationSpeed;
                this.offset = this.reload / WavefrontTurret.this.reloadTime * 0.25F;
                this.animTime = Mathf.approach(this.animTime, 40.0F, Time.delta);
            } else {
                this.angle = Mathf.slerp(this.angle, (float)Mathf.round(this.angle / 90.0F) * 90.0F, 0.1F);
                if (this.resetAvailable()) {
                    this.gap = Math.max(0.0F, this.gap - 0.005F * Time.delta);
                }

                this.animTime = Mathf.approach(this.animTime, 0.0F, Time.delta);
            }

            if (this.waitTime > 0.0F) {
                this.waitTime -= Time.delta;
            }

            WavefrontTurret.this.tr2.trns(this.rotation, -this.recoil);
            this.inst.transform.set(Tmp.v31.set(this.x + WavefrontTurret.this.tr2.x, this.y + WavefrontTurret.this.tr2.y, this.gap), Utils.q1.set(Vec3.Z, this.rotation - 90.0F), Tmp.v33.set(WavefrontTurret.this.scale, WavefrontTurret.this.scale, WavefrontTurret.this.scale));
        }

        public boolean shouldTurn() {
            return super.shouldTurn() && this.waitTime <= 0.0F;
        }

        protected void shoot(BulletType type) {
            super.shoot(type);
            this.waitTime = 60.0F;
        }

        private boolean resetAvailable() {
            return Angles.within(this.angle, (float)Mathf.round(this.angle / 90.0F) * 90.0F, 3.0F);
        }

        public void draw() {
            Draw.rect(WavefrontTurret.this.baseRegion, this.x, this.y);
            Draw.color();
            float var10000 = Draw.z();
            ModelInstance var10001 = this.inst;
            Objects.requireNonNull(var10001);
            Draw.draw(var10000, var10001::render);
        }
    }
}
