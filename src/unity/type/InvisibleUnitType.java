package unity.type;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Trailc;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.graphics.Trail;
import mindustry.type.Weapon;
import unity.gen.Invisiblec;

public class InvisibleUnitType extends UnityUnitType {
    public Color tint;

    public InvisibleUnitType(String name) {
        super(name);
        this.tint = Color.red;
    }

    protected float fade(Invisiblec unit) {
        float minimum = Vars.player.team() == unit.team() ? 0.1F : 0.01F;
        return Mathf.clamp(1.0F - unit.alphaLerp(), minimum, 1.0F);
    }

    public void drawOutline(Unit unit) {
        if (unit instanceof Invisiblec) {
            Invisiblec e = (Invisiblec)unit;
            Tmp.c1.set(Color.white).lerp(this.tint, Mathf.lerp(0.0F, 0.5F, e.alphaLerp()));
            Draw.color(Tmp.c1);
            Draw.alpha(1.0F - e.alphaLerp());
            if (Core.atlas.isFound(this.outlineRegion)) {
                Draw.rect(this.outlineRegion, unit.x, unit.y, unit.rotation - 90.0F);
            }

        } else {
            super.drawOutline(unit);
        }
    }

    public Color cellColor(Unit unit) {
        if (unit instanceof Invisiblec) {
            Invisiblec e = (Invisiblec)unit;
            return super.cellColor(unit).a(this.fade(e));
        } else {
            return super.cellColor(unit);
        }
    }

    public void drawEngine(Unit unit) {
        if (unit.isFlying()) {
            float scale = unit.elevation;
            float offset = this.engineOffset / 2.0F + this.engineOffset / 2.0F * scale;
            if (unit instanceof Trailc) {
                Trail trail = ((Trailc)unit).trail();
                trail.draw(unit.team.color, (this.engineSize + Mathf.absin(Time.time, 2.0F, this.engineSize / 4.0F) * scale) * this.trailScl);
            }

            Draw.color(unit.team.color);
            if (unit instanceof Invisiblec) {
                Invisiblec e = (Invisiblec)unit;
                Draw.alpha(this.fade(e));
            }

            Fill.circle(unit.x + Angles.trnsx(unit.rotation + 180.0F, offset), unit.y + Angles.trnsy(unit.rotation + 180.0F, offset), (this.engineSize + Mathf.absin(Time.time, 2.0F, this.engineSize / 4.0F)) * scale);
            Draw.color(Color.white);
            if (unit instanceof Invisiblec) {
                Invisiblec e = (Invisiblec)unit;
                Draw.alpha(this.fade(e));
            }

            Fill.circle(unit.x + Angles.trnsx(unit.rotation + 180.0F, offset - 1.0F), unit.y + Angles.trnsy(unit.rotation + 180.0F, offset - 1.0F), (this.engineSize + Mathf.absin(Time.time, 2.0F, this.engineSize / 4.0F)) / 2.0F * scale);
            Draw.color();
        }
    }

    public void drawSoftShadow(Unit unit) {
        if (unit instanceof Invisiblec) {
            Invisiblec e = (Invisiblec)unit;
            Draw.color(0.0F, 0.0F, 0.0F, 0.4F * this.fade(e));
            float rad = 1.6F;
            float size = (float)Math.max(this.region.width, this.region.height) * Draw.scl;
            Draw.rect(this.softShadowRegion, unit, size * rad, size * rad);
            Draw.color();
        } else {
            super.drawSoftShadow(unit);
        }
    }

    public void drawShadow(Unit unit) {
        if (unit instanceof Invisiblec) {
            Invisiblec e = (Invisiblec)unit;
            Draw.color(Pal.shadow);
            Draw.alpha(Pal.shadow.a * this.fade(e));
            float el = Math.max(unit.elevation, this.visualElevation);
            Draw.rect(this.shadowRegion, unit.x + -12.0F * el, unit.y + -13.0F * el, unit.rotation - 90.0F);
            Draw.color();
        } else {
            super.drawShadow(unit);
        }
    }

    public void drawLight(Unit unit) {
        if (unit instanceof Invisiblec) {
            Invisiblec e = (Invisiblec)unit;
            if (this.lightRadius > 0.0F) {
                Drawf.light(unit.team, unit.x, unit.y, this.lightRadius, this.lightColor, this.lightOpacity * (1.0F - e.alphaLerp()));
            }

        } else {
            super.drawLight(unit);
        }
    }

    public void drawWeapons(Unit unit) {
        float z = Draw.z();

        for(WeaponMount mount : unit.mounts) {
            Weapon weapon = mount.weapon;
            boolean found = this.bottomWeapons.contains(weapon);
            float rotation = unit.rotation - 90.0F;
            float weaponRotation = rotation + (weapon.rotate ? mount.rotation : 0.0F);
            float recoil = -(mount.reload / weapon.reload * weapon.recoil);
            float wx = unit.x + Angles.trnsx(rotation, weapon.x, weapon.y) + Angles.trnsx(weaponRotation, 0.0F, recoil);
            float wy = unit.y + Angles.trnsy(rotation, weapon.x, weapon.y) + Angles.trnsy(weaponRotation, 0.0F, recoil);
            float zC = Draw.z();
            if (found) {
                Draw.z(zC - 0.005F);
            }

            if (weapon.shadow > 0.0F) {
                float fade = 1.0F;
                if (unit instanceof Invisiblec) {
                    Invisiblec e = (Invisiblec)unit;
                    fade = this.fade(e);
                }

                Drawf.shadow(wx, wy, weapon.shadow, fade);
            }

            boolean outlineFound = weapon.outlineRegion.found();
            this.applyColor(unit);
            if (outlineFound) {
                float zB = Draw.z();
                if (!weapon.top || found) {
                    Draw.z(zB);
                }

                Draw.rect(weapon.outlineRegion, wx, wy, (float)weapon.outlineRegion.width * Draw.scl * (float)(-Mathf.sign(weapon.flipSprite)), (float)weapon.region.height * Draw.scl, weaponRotation);
                Draw.z(zB);
            }

            if (unit instanceof Invisiblec) {
                Invisiblec e = (Invisiblec)unit;
                if (outlineFound) {
                    Draw.alpha(1.0F - e.alphaLerp());
                }
            }

            Draw.rect(weapon.region, wx, wy, (float)weapon.region.width * Draw.scl * (float)(-Mathf.sign(weapon.flipSprite)), (float)weapon.region.height * Draw.scl, weaponRotation);
            if (weapon.heatRegion.found() && mount.heat > 0.0F) {
                Draw.color(weapon.heatColor, mount.heat);
                Draw.blend(Blending.additive);
                Draw.rect(weapon.heatRegion, wx, wy, (float)weapon.heatRegion.width * Draw.scl * (float)(-Mathf.sign(weapon.flipSprite)), (float)weapon.heatRegion.height * Draw.scl, weaponRotation);
                Draw.blend();
                Draw.color();
            }

            Draw.z(zC);
        }

        Draw.reset();
        Draw.z(z);
    }

    public void applyColor(Unit unit) {
        if (unit instanceof Invisiblec) {
            Invisiblec e = (Invisiblec)unit;
            float lerp = this.fade(e);
            Tmp.c1.set(Color.white).lerp(this.tint, Mathf.lerp(0.0F, 0.5F, e.alphaLerp()));
            Draw.color(Tmp.c1);
            Draw.alpha(lerp);
            Draw.mixcol(Color.white, unit.hitTime);
            if (unit.drownTime > 0.0F && unit.floorOn().isDeep()) {
                Draw.mixcol(unit.floorOn().mapColor, unit.drownTime * 0.8F);
            }

        } else {
            super.applyColor(unit);
        }
    }
}
