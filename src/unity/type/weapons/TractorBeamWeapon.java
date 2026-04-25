//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package unity.type.weapons;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.scene.ui.layout.Table;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.audio.SoundLoop;
import mindustry.content.StatusEffects;
import mindustry.entities.Units;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Building;
import mindustry.gen.Healthc;
import mindustry.gen.Sounds;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;
import mindustry.type.Weapon;

public class TractorBeamWeapon extends Weapon {
    public float pullStrength = 10.0F;
    public float scaledForce = 1.0F;
    public float beamWidth = 0.75F;
    public boolean includeDead = false;
    public TextureRegion laser;
    public TextureRegion laserEnd;
    public TextureRegion laserTop;
    public TextureRegion laserTopEnd;
    public Color laserColor;
    public Color laserTopColor;

    public TractorBeamWeapon(String name) {
        super(name);
        this.laserColor = Pal.lancerLaser;
        this.laserTopColor = Color.white;
        this.reload = 1.0F;
        this.predictTarget = false;
        this.autoTarget = true;
        this.controllable = false;
        this.rotate = true;
        this.useAmmo = false;
        this.recoil = -3.0F;
        this.shootSound = Sounds.tractorbeam;
        this.alternate = false;
        this.mountType = TractorBeamMount::new;
    }

    public void load() {
        super.load();
        this.laser = Core.atlas.find("laser-white");
        this.laserEnd = Core.atlas.find("laser-white-end");
        this.laserTop = Core.atlas.find("laser-top");
        this.laserTopEnd = Core.atlas.find("laser-top-end");
    }

    public void addStats(UnitType u, Table t) {
        t.row();
        String n = this.scaledForce != 0.0F ? this.pullStrength + "-" + (this.pullStrength + this.scaledForce) : String.valueOf(this.pullStrength);
        t.add("[lightgray]" + Core.bundle.get("stat.unity.pullstrength") + "[white]" + n);
        if (this.bullet.damage > 0.0F) {
            t.row();
            t.add(Core.bundle.format("bullet.damage", new Object[]{this.bullet.damage}));
        }

        if (this.bullet.status != null && this.bullet.status != StatusEffects.none) {
            t.row();
            t.add((this.bullet.minfo.mod == null ? this.bullet.status.emoji() : "") + "[stat]" + this.bullet.status.localizedName);
        }

    }

    public void update(Unit unit, WeaponMount mount) {
        super.update(unit, mount);
        TractorBeamMount tm = (TractorBeamMount)mount;
        float weaponRotation = unit.rotation - 90.0F;
        float wx = unit.x + Angles.trnsx(weaponRotation, this.x, this.y);
        float wy = unit.y + Angles.trnsy(weaponRotation, this.x, this.y);
        if (mount.target != null && Angles.within(unit.rotation + mount.rotation, mount.target.angleTo(wx, wy) + 180.0F, this.shootCone)) {
            tm.targetP.set(mount.target);
            Teamc var8 = mount.target;
            if (var8 instanceof Unit) {
                Unit u = (Unit)var8;
                tm.scl = Mathf.lerpDelta(tm.scl, 1.0F, 0.07F);
                float scl = tm.scl * (this.pullStrength + Mathf.clamp(1.0F - Mathf.dst(wx, wy, tm.targetP.x, tm.targetP.y) / this.bullet.range()) * this.scaledForce);
                float ang = mount.target.angleTo(wx, wy);
                u.impulseNet(Tmp.v1.trns(ang, scl));
                unit.impulseNet(Tmp.v1.scl(-1.0F));
                if ((tm.timer += Time.delta) >= 5.0F) {
                    u.damage(this.bullet.damage);
                    tm.timer = 0.0F;
                }

                u.apply(this.bullet.status, this.bullet.statusDuration);
            }
        } else {
            tm.scl = Mathf.lerpDelta(tm.scl, 0.0F, 0.07F);
        }

        if (tm.scl > 0.01F && !Vars.headless) {
            if (mount.sound == null) {
                mount.sound = new SoundLoop(this.shootSound, 1.0F);
            }

            mount.sound.update(wx, wy, true);
        } else if (mount.sound != null) {
            mount.sound.update(wx, wy, false);
        }

        mount.reload = tm.scl * this.reload;
    }

    protected Teamc findTarget(Unit unit, float x, float y, float range, boolean air, boolean ground) {
        return Units.closestTarget(unit.team, x, y, range + Math.abs(this.shootY), (u) -> u.checkTarget(air, ground), (t) -> false);
    }

    protected boolean checkTarget(Unit unit, Teamc target, float x, float y, float range) {
        boolean var10000;
        label23: {
            if (super.checkTarget(unit, target, x, y, range)) {
                if (!(target instanceof Healthc)) {
                    break label23;
                }

                Healthc h = (Healthc)target;
                if (!target.isAdded() || h.dead() && !this.includeDead) {
                    break label23;
                }
            }

            if (!(target instanceof Building)) {
                var10000 = false;
                return var10000;
            }
        }

        var10000 = true;
        return var10000;
    }

    protected void shoot(Unit unit, WeaponMount mount, float shootX, float shootY, float aimX, float aimY, float mountX, float mountY, float rotation, int side) {
    }

    public void draw(Unit unit, WeaponMount mount) {
        super.draw(unit, mount);
        TractorBeamMount tm = (TractorBeamMount)mount;
        if (tm.scl > 0.01F) {
            float z = Draw.z();
            float weaponRotation = unit.rotation - 90.0F;
            float wx = unit.x + Angles.trnsx(weaponRotation, this.x, this.y);
            float wy = unit.y + Angles.trnsy(weaponRotation, this.x, this.y);
            float ox = wx + Angles.trnsx(unit.rotation + mount.rotation, this.shootY);
            float oy = wy + Angles.trnsy(unit.rotation + mount.rotation, this.shootY);
            Draw.z(116.0F);
            Draw.color(this.laserColor);
            Drawf.laser(unit.team, this.laser, this.laserEnd, ox, oy, tm.targetP.x, tm.targetP.y, tm.scl * this.beamWidth);
            Draw.z(116.1F);
            Draw.color(this.laserTopColor);
            Drawf.laser(unit.team, this.laserTop, this.laserTopEnd, ox, oy, tm.targetP.x, tm.targetP.y, tm.scl * this.beamWidth);
            Draw.z(z);
        }

    }

    public static class TractorBeamMount extends WeaponMount {
        Vec2 targetP = new Vec2();
        float scl;
        float timer;

        TractorBeamMount(Weapon weapon) {
            super(weapon);
        }
    }
}
