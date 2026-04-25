package unity.entities.abilities;

import arc.Core;
import arc.audio.Sound;
import arc.func.Func;
import arc.math.Mathf;
import arc.math.geom.Position;
import arc.math.geom.Vec2;
import arc.util.Nullable;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.gen.Hitboxc;
import mindustry.gen.Sounds;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;
import mindustry.gen.Velc;
import unity.content.UnityFx;

public class TeleportAbility<T extends Teamc & Hitboxc> extends BaseAbility {
    private static final Vec2 vec = new Vec2();
    public float teleportDistance;
    public Sound teleportSound;
    public Effect teleportEffect;
    public Effect teleportPosEffect;
    protected Func<Unit, T> teleportAble;
    @Nullable
    protected T toAvoid;

    public TeleportAbility(Func<Unit, T> teleportAble, float teleportDistance) {
        super((unit) -> teleportAble.get(unit) != null, true, true);
        this.teleportSound = Sounds.lasershoot;
        this.teleportEffect = Fx.none;
        this.teleportPosEffect = UnityFx.teleportPos;
        this.teleportAble = teleportAble;
        this.teleportDistance = teleportDistance;
    }

    public boolean able(Unit unit) {
        return super.able(unit) && (this.toAvoid = (T)(this.teleportAble.get(unit))) != null;
    }

    public void use(Unit unit, float x, float y) {
        super.use(unit, x, y);
        Vec2 pos;
        if (!Float.isNaN(x) && !Float.isNaN(y)) {
            pos = vec.set(x, y).sub(unit).limit(this.teleportDistance);
        } else if (this.toAvoid != null) {
            Teamc var6 = this.toAvoid;
            if (var6 instanceof Velc) {
                Velc vel = (Velc)var6;
                int i = Mathf.randomSeed((long)(Time.time + (float)unit.id)) > 0.5F ? 1 : -1;
                pos = vec.trns(vel.vel().angle() - 90.0F, (float)i * Math.min(unit.dst(vel) * 2.0F, this.teleportDistance));
            } else {
                pos = vec.set(unit).sub(this.toAvoid).rotate(Mathf.randomSeed((long)(Time.time + (float)unit.id), 360.0F)).setLength(Math.min(vec.len() * 2.0F, this.teleportDistance));
            }
        } else {
            pos = vec.trns(Mathf.randomSeed((long)(Time.time + (float)unit.id), 360.0F), Mathf.randomSeed((long)(Time.time + 1.0F + (float)unit.id), unit.hitSize() * 2.0F, this.teleportDistance));
        }

        this.teleportEffect.at(unit.x, unit.y, 0.0F, new Position[]{new Vec2(unit.x, unit.y), (new Vec2(pos)).add(unit)});
        this.teleportPosEffect.at(unit.x, unit.y, unit.rotation, unit.type);
        this.teleportSound.at(unit);
        unit.trns(pos);
        this.teleportPosEffect.at(unit.x, unit.y, unit.rotation, unit.type);
        this.teleportSound.at(unit);
        unit.snapSync();
        if (unit.isPlayer()) {
            unit.getPlayer().snapSync();
        }

        if (Vars.mobile && !Vars.headless && unit.isLocal()) {
            Core.camera.position.set(pos.x + unit.x, pos.y + unit.y);
        }

        unit.vel.trns(pos.angle(), 4.0F);
    }
}
