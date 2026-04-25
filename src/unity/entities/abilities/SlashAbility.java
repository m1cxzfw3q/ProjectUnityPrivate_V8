package unity.entities.abilities;

import arc.Core;
import arc.audio.Sound;
import arc.func.Boolf;
import arc.math.geom.Vec2;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Damage;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Bullet;
import mindustry.gen.Sounds;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;
import mindustry.type.StatusEffect;
import unity.content.UnityBullets;
import unity.content.UnityFx;
import unity.content.UnityStatusEffects;

public class SlashAbility extends BaseAbility {
    public float slashDistance = 144.0F;
    public Effect teleportEffect;
    public Effect postTeleportEffect;
    public Sound teleportSound;
    public StatusEffect boostedEffect;
    public BulletType slashBullet;
    public Effect slashEffect;

    public SlashAbility(Boolf<Unit> able) {
        super(able, true, true);
        this.teleportEffect = Fx.lightningShoot;
        this.postTeleportEffect = Fx.lancerLaserShootSmoke;
        this.teleportSound = Sounds.spark;
        this.boostedEffect = UnityStatusEffects.boosted;
        this.slashBullet = UnityBullets.teleportLightning;
        this.slashEffect = UnityFx.slashEffect;
    }

    public void use(Unit unit, float x, float y) {
        super.use(unit, x, y);
        Teamc target = Units.closestEnemy(unit.team, unit.x, unit.y, 280.0F, (u) -> true);
        float dir = unit.rotation;
        if (target != null) {
            dir = Tmp.v1.set(target.getX() - unit.x, target.getY() - unit.y).angle();
        }

        this.teleportEffect.at(unit.x, unit.y, dir);
        Bullet b = this.slashBullet.create(unit, unit.team, unit.x, unit.y, dir, -1.0F, 1.0F, 1.0F, (Object)null);
        Damage.collideLine(b, unit.team, this.slashEffect, unit.x, unit.y, dir, 128.0F, true);
        unit.apply(this.boostedEffect, 30.0F);
        Vec2 pos = Tmp.v1.set(this.slashDistance, 0.0F).setAngle(dir);
        unit.set(pos.x + unit.x, pos.y + unit.y);
        unit.snapSync();
        if (unit.isPlayer()) {
            unit.getPlayer().snapSync();
        }

        if (Vars.mobile && !Vars.headless && unit.getPlayer() == Vars.player) {
            Core.camera.position.set(pos.x + unit.x, pos.y + unit.y);
        }

        this.teleportSound.at(pos.x + unit.x, pos.y + unit.y, 1.6F);
        this.postTeleportEffect.at(unit.x, unit.y, (dir + 180.0F) % 360.0F);
        unit.vel.trns(dir, 4.0F);
    }
}
