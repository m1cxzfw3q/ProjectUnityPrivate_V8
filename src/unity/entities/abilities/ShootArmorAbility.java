package unity.entities.abilities;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Mechc;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.type.UnitType;
import mindustry.type.Weapon;
import unity.type.UnityUnitType;

public class ShootArmorAbility extends Ability {
    public float armorInc = 25.0F;
    public float warmup = 0.06F;
    public float speedReduction = 0.5F;
    public float spread = 2.0F;
    public String armorRegion = "error";
    protected float shootHeat;
    protected Vec2 offset = new Vec2();

    public ShootArmorAbility() {
    }

    public ShootArmorAbility(float armorInc, float warmup, float spread, float speedReduction, String armorRegion) {
        this.armorInc = armorInc;
        this.warmup = warmup;
        this.spread = spread;
        this.speedReduction = speedReduction;
        this.armorRegion = armorRegion;
    }

    public void update(Unit unit) {
        this.shootHeat = Mathf.lerpDelta(this.shootHeat, unit.isShooting() ? 1.0F : 0.0F, this.warmup);
        unit.armor = unit.type.armor + this.armorInc * this.shootHeat;
        float scl = 1.0F - this.shootHeat * this.speedReduction * Time.delta;
        unit.vel.scl(scl);

        for(int i = 0; i < unit.mounts.length; ++i) {
            Weapon w = unit.mounts[i].weapon;
            if (w.mirror) {
                try {
                    UnitType var6 = unit.type;
                    if (var6 instanceof UnityUnitType) {
                        UnityUnitType type = (UnityUnitType)var6;
                        float x = type.weaponXs.items[i];
                        if (x > 0.0F) {
                            w.x = x + this.spread * this.shootHeat;
                        } else if (x < 0.0F) {
                            w.x = x - this.spread * this.shootHeat;
                        }
                    }
                } catch (Throwable var7) {
                }
            }
        }

    }

    public void draw(Unit unit) {
        TextureRegion region = Core.atlas.find(this.armorRegion);
        if (this.shootHeat >= 0.01F && Core.atlas.isFound(region)) {
            Draw.draw(Draw.z(), () -> {
                Mechc mech = unit instanceof Mechc ? (Mechc)unit : null;
                if (mech != null) {
                    this.offset.trns(mech.baseRotation(), 0.0F, Mathf.lerp(Mathf.sin(mech.walkExtend(true), 0.63661975F, 1.0F) * unit.type.mechSideSway, 0.0F, unit.elevation));
                    this.offset.add(Tmp.v1.trns(mech.baseRotation() + 90.0F, 0.0F, Mathf.lerp(Mathf.sin(mech.walkExtend(true), 0.31830987F, 1.0F) * unit.type.mechFrontSway, 0.0F, unit.elevation)));
                } else {
                    this.offset.set(0.0F, 0.0F);
                }

                Drawf.construct(unit.x + this.offset.x, unit.y + this.offset.y, region, unit.team.color, unit.rotation - 90.0F, this.shootHeat, this.shootHeat, Time.time * 2.0F + (float)unit.id());
            });
        }

    }
}
