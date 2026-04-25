package unity.world.blocks.defense.turrets;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Angles;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
import java.util.Objects;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Groups;
import mindustry.gen.Healthc;
import mindustry.gen.Posc;
import mindustry.gen.Teamc;
import unity.assets.type.g3d.Model;
import unity.assets.type.g3d.ModelInstance;
import unity.assets.type.g3d.attribute.type.ColorAttribute;
import unity.content.effects.SpecialFx;
import unity.gen.SoulPowerTurret;
import unity.graphics.UnityPal;
import unity.util.Utils;

public class PrismTurret extends SoulPowerTurret {
    public Model model;
    public float prismOffset = 10.0F;
    public float prismRotateSpeed = 20.0F;
    public float scale = 0.6F;
    public Color fromColor;
    public Color toColor;
    public Effect damageEffect;
    public float warmup;
    public int maxShots;
    public float shootRate;
    public float sortRange;

    public PrismTurret(String name) {
        super(name);
        this.fromColor = UnityPal.monolithDark;
        this.toColor = UnityPal.monolith;
        this.damageEffect = SpecialFx.chainLightningActive;
        this.warmup = 0.1F;
        this.maxShots = 5;
        this.shootRate = 2.0F;
        this.sortRange = 40.0F;
        this.unitSort = (unit, x, y) -> (float)Groups.unit.intersect(x - this.sortRange, y - this.sortRange, this.sortRange * 2.0F, this.sortRange * 2.0F).count((u) -> u.within(unit, this.sortRange) && u.checkTarget(this.targetAir, this.targetGround));
    }

    public class PrismTurretBuild extends SoulPowerTurret.SoulPowerTurretBuild {
        public ModelInstance inst;
        public float prismHeat = 0.0F;
        public float prismRotation = 0.0F;
        protected Seq<Posc> targets = new Seq(false);

        public PrismTurretBuild() {
            super(PrismTurret.this);
        }

        public void created() {
            super.created();
            this.inst = new ModelInstance(PrismTurret.this.model);
        }

        public void updateTile() {
            super.updateTile();
            boolean act = this.isActive();
            this.prismHeat = Mathf.lerpDelta(this.prismHeat, act ? this.efficiency() : 0.0F, act ? PrismTurret.this.warmup : PrismTurret.this.cooldown);
            this.prismRotation += this.prismHeat * PrismTurret.this.prismRotateSpeed * (float)Mathf.signs[this.id % 2];
            this.inst.transform.set(Tmp.v31.set(this.x + Angles.trnsx(this.rotation, PrismTurret.this.prismOffset - this.recoil), this.y + Angles.trnsy(this.rotation, PrismTurret.this.prismOffset - this.recoil), 0.0F), Utils.q1.setFromAxis(0.0F, 0.0F, 1.0F, this.rotation - 90.0F).mul(Utils.q2.setFromAxis(0.0F, 1.0F, 0.0F, this.prismRotation)), Tmp.v32.set(PrismTurret.this.scale, PrismTurret.this.scale, PrismTurret.this.scale));
            this.color().set(PrismTurret.this.fromColor).lerp(PrismTurret.this.toColor, this.prismHeat + Mathf.sin(4.0F, 0.1F) * this.prismHeat);
        }

        protected void findTarget() {
            super.findTarget();
            this.targets.clear().add(this.target);

            for(int i = 0; i < PrismTurret.this.maxShots; ++i) {
                Teamc t = Units.closestTarget(this.team, this.targetPos.x, this.targetPos.y, PrismTurret.this.sortRange, (u) -> u != this.target && u.checkTarget(PrismTurret.this.targetAir, PrismTurret.this.targetGround) && !this.targets.contains(u), (b) -> b != this.target && PrismTurret.this.targetGround && !this.targets.contains(b));
                if (t != null) {
                    this.targets.add(t);
                }
            }

        }

        public Color color() {
            return ((ColorAttribute)this.inst.getMaterial().get(ColorAttribute.diffuse)).color;
        }

        protected void shoot(BulletType type) {
            for(int i = 0; i < this.targets.size; ++i) {
                Posc u = (Posc)this.targets.get(i);
                Time.run((float)i / PrismTurret.this.shootRate, () -> {
                    if (this.isValid() && u != null) {
                        if (u instanceof Healthc) {
                            Healthc h = (Healthc)u;
                            if (!h.isValid()) {
                                return;
                            }
                        } else if (!u.isAdded()) {
                            return;
                        }

                        float angle = this.angleTo(u);
                        PrismTurret.this.shootType.create(this, u.x(), u.y(), angle);
                        this.heat = 1.0F;
                        PrismTurret.this.damageEffect.at(this.x + Angles.trnsx(this.rotation, PrismTurret.this.prismOffset - this.recoil), this.y + Angles.trnsy(this.rotation, PrismTurret.this.prismOffset - this.recoil), 2.0F, this.color(), u);
                        type.hitEffect.at(u.x(), u.y(), angle);
                        this.effects();
                    }
                });
            }

        }

        public void draw() {
            super.draw();
            float var10000 = Draw.z();
            ModelInstance var10001 = this.inst;
            Objects.requireNonNull(var10001);
            Draw.draw(var10000, var10001::render);
        }
    }
}
