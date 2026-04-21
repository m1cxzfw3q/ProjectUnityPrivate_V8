package unity.mod;

import arc.Core;
import arc.Events;
import arc.audio.Sound;
import arc.func.Floatp;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Position;
import arc.math.geom.Vec2;
import arc.struct.IntMap;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.game.EventType.Trigger;
import mindustry.gen.Entityc;
import mindustry.gen.Mechc;
import mindustry.gen.Unit;
import mindustry.input.Binding;
import mindustry.type.UnitType;
import unity.gen.UnitySounds;
import unity.util.BasicPool;

public class TimeStop {
    private static final float lerpTime = 20.0F;
    private static final float error = 4.0E-5F;
    private static final float slowDownTime = 30.0F;
    private static final float continueTimeDuration = 89.0F;
    private static final Seq<TimeStopEntity> entities = new Seq();
    private static final IntMap<TimeStopEntity> map = new IntMap(102);
    private static final BasicPool<TimeStopEntity> pool = new BasicPool(8, 200, TimeStopEntity::new);
    private static final Vec2 movement = new Vec2();
    private static float time = 0.0F;
    private static float lastTime = 0.0F;
    private static boolean set = false;
    private static boolean reseting = false;
    private static Sound continueTimeSound;
    private static final Floatp defaultDelta = () -> Math.min(Core.graphics.getDeltaTime() * 60.0F, 3.0F);
    private static final Floatp timeStopDelta = () -> Math.min(Core.graphics.getDeltaTime() * 60.0F * Mathf.sqrt(time / 20.0F), 3.0F);
    private static final Floatp stoppedTimeDelta = () -> Math.min(Core.graphics.getDeltaTime() * 60.0F * Mathf.sqrt(1.0F - time / 20.0F), 3.0F);

    public static void init() {
        Events.run(Trigger.update, TimeStop::update);
        Events.on(EventType.ResetEvent.class, (event) -> reset());
    }

    public static boolean inTimeStop() {
        return set || !entities.isEmpty();
    }

    public static boolean contains(Entityc entity) {
        return map.containsKey(entity.id());
    }

    public static float getTime(Entityc entity) {
        TimeStopEntity e = (TimeStopEntity)map.get(entity.id());
        return e == null ? 0.0F : e.time;
    }

    public static void addEntity(Entityc entity, float time) {
        if (!map.containsKey(entity.id())) {
            TimeStopEntity te = (TimeStopEntity)pool.obtain();
            te.entity = entity;
            te.time = time;
            te.id = entity.id();
            te.fakeTime = Time.time;
            map.put(entity.id(), te);
            entities.add(te);
        } else {
            TimeStopEntity te = (TimeStopEntity)map.get(entity.id());
            te.time = Math.max(te.time, time);
        }

    }

    static void draw() {
        if (time > 1.0E-4F && (Vars.player.unit() == null || !map.containsKey(Vars.player.unit().id))) {
            float z = Draw.z();
            Draw.z(161.0F);
            Draw.color(Color.black);
            Draw.rect();
            Draw.color();
            Draw.z(z);
        }

    }

    static void reset() {
        entities.clear();
        map.clear();
        lastTime = 0.0F;
        time = 0.0F;
        reseting = false;
        set = false;
    }

    static void update() {
        if (continueTimeSound == null) {
            continueTimeSound = UnitySounds.continueTime;
        }

        if (Vars.state.isGame() && !Vars.state.isPaused()) {
            float tDelta = defaultDelta.get();
            time = Mathf.approach(time, reseting ? 19.99996F : 0.0F, tDelta);
            if (!set && !entities.isEmpty()) {
                set = true;
                reseting = true;
                lastTime = Time.time;
                Time.setDeltaProvider(stoppedTimeDelta);
            }

            if (set && !entities.isEmpty()) {
                float lastDelta = Time.delta;
                lastTime += Time.delta;
                float delta = timeStopDelta.get();
                reseting = false;
                entities.removeAll((te) -> {
                    float lastT = te.time;
                    te.time -= tDelta;
                    boolean valid = te.entity.isAdded() && te.entity.id() == te.id;
                    if (valid) {
                        float d = delta * Mathf.clamp(te.time / 30.0F);
                        boolean isPlayer = te.entity instanceof Unit && ((Unit)te.entity).controller() == Vars.player;
                        te.fakeTime += d;
                        Time.delta = d;
                        Time.time = te.fakeTime;
                        if (isPlayer) {
                            Unit u = (Unit)te.entity;
                            if (Vars.mobile) {
                                updateMovementMobile(u);
                            } else {
                                updateMovementDesktop(u);
                            }
                        }

                        te.entity.update();
                        if (isPlayer) {
                            Position p = (Position)te.entity;
                            Core.camera.position.set(p);
                            if (te.time < 89.0F && lastT >= 89.0F) {
                                continueTimeSound.at(p);
                            }
                        }
                    }

                    if (te.time > 30.0F && valid) {
                        reseting = true;
                    }

                    if (te.time <= 0.0F || !valid) {
                        pool.free(te);
                        map.remove(te.id);
                    }

                    return te.time <= 0.0F || !valid;
                });
                Time.delta = lastDelta;
                Time.time = lastTime;
            }

            if (entities.isEmpty() && set) {
                set = false;
                Time.time = lastTime;
                Time.setDeltaProvider(defaultDelta);
            }

        }
    }

    static void updateMovementMobile(Unit unit) {
        UnitType type = unit.type;
        if (type != null) {
            Tmp.v1.set(Core.camera.position);
            float attractDst = 15.0F;
            float speed = unit.speed();
            movement.set(Tmp.v1).sub(Vars.player).limit(speed);
            movement.setAngle(Mathf.slerp(movement.angle(), unit.vel.angle(), 0.05F));
            if (Vars.player.within(Tmp.v1, attractDst)) {
                movement.setZero();
                unit.vel.approachDelta(Vec2.ZERO, unit.speed() * type.accel / 2.0F);
            }

            unit.movePref(movement);
        }
    }

    static void updateMovementDesktop(Unit unit) {
        boolean omni = unit.type.omniMovement;
        float speed = unit.speed();
        float xa = Core.input.axis(Binding.move_x);
        float ya = Core.input.axis(Binding.move_y);
        boolean boosted = unit instanceof Mechc && unit.isFlying();
        movement.set(xa, ya).nor().scl(speed);
        if (Core.input.keyDown(Binding.mouse_move)) {
            movement.add(Core.input.mouseWorld().sub(Vars.player).scl(0.04F * speed)).limit(speed);
        }

        float mouseAngle = Angles.mouseAngle(unit.x, unit.y);
        boolean aimCursor = omni && Vars.player.shooting && unit.type.hasWeapons() && unit.type.faceTarget && !boosted && unit.type.rotateShooting;
        if (aimCursor) {
            unit.lookAt(mouseAngle);
        } else {
            unit.lookAt(unit.prefRotation());
        }

        unit.movePref(movement);
    }

    static class TimeStopEntity {
        Entityc entity;
        float time;
        float fakeTime;
        int id;
    }
}
