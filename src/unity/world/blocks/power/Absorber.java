package unity.world.blocks.power;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.content.StatusEffects;
import mindustry.entities.Units;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.type.StatusEffect;
import mindustry.world.blocks.power.PowerGenerator;

public class Absorber extends PowerGenerator {
    public float range = 50.0F;
    public float powerProduction = 1.2F;
    public StatusEffect status;
    public TextureRegion laserRegion;
    public TextureRegion laserEndRegion;
    public TextureRegion baseRegion;
    public float rotateSpeed;
    public float cone;
    public float damagePerTick;

    public Absorber(String name) {
        super(name);
        this.status = StatusEffects.slow;
        this.rotateSpeed = 2.0F;
        this.cone = 2.0F;
        this.damagePerTick = 1.0F;
        this.update = true;
        this.outlineIcon = true;
    }

    public void load() {
        super.load();
        this.laserRegion = Core.atlas.find("laser");
        this.laserEndRegion = Core.atlas.find("laser-end");
        this.baseRegion = Core.atlas.find("block-" + this.size);
    }

    protected TextureRegion[] icons() {
        return new TextureRegion[]{this.baseRegion, this.region};
    }

    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);
        Drawf.dashCircle((float)(x * 8) + this.offset, (float)(y * 8) + this.offset, this.range, Pal.placing);
    }

    public class AbsorberBuilding extends PowerGenerator.GeneratorBuild {
        public Unit unit;
        public float time = 0.0F;
        public float rotation = 90.0F;
        public float targetRot = 90.0F;
        public boolean canAbsorb = false;

        public AbsorberBuilding() {
            super(Absorber.this);
        }

        public void update() {
            super.update();
            this.canAbsorb = Angles.angleDist(this.rotation, this.targetRot) <= Absorber.this.cone;
            this.unit = Units.closestEnemy(this.team, this.x, this.y, Absorber.this.range, (e) -> !e.dead() && !e.isFlying());
            if (this.unit != null) {
                this.targetRot = Angles.angle(this.x, this.y, this.unit.x, this.unit.y);
                this.turnToTarget(this.targetRot);
                if (this.canAbsorb) {
                    this.unit.apply(Absorber.this.status);
                    this.unit.damage(Absorber.this.damagePerTick);
                }
            }

            this.turnToTarget(this.targetRot);
            this.productionEfficiency = this.unit == null ? 0.0F : 1.0F;
            this.time += 0.016666668F * Time.delta;
        }

        public float getPowerProduction() {
            return Absorber.this.powerProduction * this.productionEfficiency;
        }

        public void drawSelect() {
            Drawf.dashCircle(this.x, this.y, Absorber.this.range, this.team.color);
        }

        public void draw() {
            super.draw();
            Draw.z(30.0F);
            Draw.rect(Absorber.this.baseRegion, this.x, this.y);
            Draw.rect(Absorber.this.region, this.x, this.y, this.rotation - 90.0F);
            if (this.unit != null && this.canAbsorb) {
                Tmp.v1.set(0.0F, 0.0F).trns(this.rotation, (float)(Absorber.this.size * 8) / 2.0F);
                Draw.z(114.0F);
                Draw.color(this.unit.team.color);
                Lines.circle(this.unit.x, this.unit.y, this.unit.hitSize + Mathf.sin(this.time * 2.0F));
                Draw.color(Color.white);
                Drawf.laser(this.team, Absorber.this.laserRegion, Absorber.this.laserEndRegion, this.x + Tmp.v1.x + Mathf.sin(this.time * 2.0F), this.y + Tmp.v1.y + Mathf.cos(this.time * 2.0F), this.unit.x, this.unit.y, 0.6F);
                Draw.reset();
            }
        }

        public void turnToTarget(float targetRot) {
            this.rotation = Angles.moveToward(this.rotation, targetRot, Absorber.this.rotateSpeed * Time.delta);
        }
    }
}
