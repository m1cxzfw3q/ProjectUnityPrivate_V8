package unity.mod;

import arc.Events;
import arc.math.Mathf;
import arc.struct.IntMap;
import arc.struct.IntSet;
import arc.struct.Seq;
import arc.util.Interval;
import arc.util.Reflect;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.pooling.Pool;
import arc.util.pooling.Pools;
import java.util.Arrays;
import mindustry.Vars;
import mindustry.entities.units.WeaponMount;
import mindustry.game.EventType;
import mindustry.game.EventType.Trigger;
import mindustry.gen.Building;
import mindustry.gen.Drawc;
import mindustry.gen.Entityc;
import mindustry.gen.Groups;
import mindustry.gen.Healthc;
import mindustry.gen.Syncc;
import mindustry.gen.Unit;
import mindustry.world.blocks.ConstructBlock;
import unity.Unity;
import unity.content.effects.ParticleFx;
import unity.entities.units.WormDefaultUnit;
import unity.entities.units.WormSegmentUnit;
import unity.util.ReflectUtils;

public class AntiCheat {
    private final Interval timer = new Interval();
    private final Seq<UnitQueue> unitSeq = new Seq<>();
    private final Seq<BuildingQueue> buildingSeq = new Seq<>();
    private final Seq<EntitySampler> sampler = new Seq<>();
    private final Seq<EntitySampler> samplerTmp = new Seq<>();
    private final IntSet exclude = new IntSet(204);
    private final IntMap<EntitySampler> samplerMap = new IntMap<>(409);
    private final Seq<DisableRegenStatus> status = new Seq<>();
    private final IntMap<DisableRegenStatus> statusMap = new IntMap<>(204);
    private float lastTime = 0.0F;

    public void setup() {
        Triggers.listen(Trigger.update, this::update);
        Events.on(EventType.BlockBuildBeginEvent.class, (event) -> {
            if (event.breaking && event.tile.build != null && event.unit != null && event.unit.team == event.tile.build.team) {
                this.removeBuilding(event.tile.build);
            }

        });
        Events.on(EventType.ResetEvent.class, (event) -> {
            this.exclude.clear();
            this.unitSeq.clear();
            this.buildingSeq.clear();
            this.sampler.clear();
            this.samplerTmp.clear();
            this.samplerMap.clear();
        });
    }

    public static void annihilateEntity(Entityc entity, boolean override) {
        annihilateEntity(entity, override, false);
    }

    public static void annihilateEntity(Entityc entity, boolean override, boolean setNaN) {
        Groups.all.remove(entity);
        if (entity instanceof Drawc draw) {
            Groups.draw.remove(draw);
        }

        if (entity instanceof Syncc sync) {
            Groups.sync.remove(sync);
        }

        if (entity instanceof Unit unit) {
            if (Unity.antiCheat != null && override) {
                Unity.antiCheat.removeUnit(unit);
            }

            try {
                ReflectUtils.setField(unit, ReflectUtils.findField(unit.getClass(), "added", true), false);
            } catch (Exception e) {
                Unity.print(e);
            }

            if (unit instanceof WormDefaultUnit) {
                WormSegmentUnit nullUnit = new WormSegmentUnit();
                WormSegmentUnit[] tmpArray = Arrays.copyOf(((WormDefaultUnit)unit).segmentUnits, ((WormDefaultUnit)unit).segmentUnits.length);
                Arrays.fill(((WormDefaultUnit)unit).segmentUnits, nullUnit);

                for(WormSegmentUnit segmentUnit : tmpArray) {
                    if (segmentUnit != null) {
                        segmentUnit.remove();
                    }
                }
            }

            if (setNaN) {
                unit.x = unit.y = unit.rotation = Float.NaN;

                for(WeaponMount mount : unit.mounts) {
                    mount.reload = Float.NaN;
                }
            }

            unit.team.data().updateCount(unit.type, -1);
            unit.controller().removed(unit);
            Groups.unit.remove(unit);
            if (Vars.net.client()) {
                Vars.netClient.addRemovedEntity(unit.id);
            }

            for(WeaponMount mount : unit.mounts) {
                if (mount.bullet != null) {
                    mount.bullet.time = mount.bullet.lifetime;
                    mount.bullet = null;
                }

                if (mount.sound != null) {
                    mount.sound.stop();
                }
            }
        }

        if (entity instanceof Building building) {
            Groups.build.remove(building);
            building.tile.remove();
            if (Unity.antiCheat != null && override) {
                Unity.antiCheat.removeBuilding(building);
            }

            if (setNaN) {
                building.x = building.y = Float.NaN;
            }

            //if (building.sound != null) {
            //    building.sound.stop();
            //}

            //building.added = false;

            Reflect.set(building, "added", false);
        }

    }

    void update() {
        if (!Vars.state.isPaused()) {
            if (this.timer.get(15.0F) && (!this.unitSeq.isEmpty() || !this.buildingSeq.isEmpty())) {
                for(Entityc e : Groups.all) {
                    if (e instanceof Unit) {
                        for(UnitQueue u : this.unitSeq) {
                            if (e == u.unit) {
                                u.allAdded = true;
                                --u.counter;
                            }

                            if (u.removed) {
                                this.unitSeq.remove(u);
                            }
                        }
                    } else if (e instanceof Building) {
                        for(BuildingQueue b : this.buildingSeq) {
                            if (e == b.build) {
                                b.allAdded = true;
                                --b.counter;
                            }

                            if (b.removed) {
                                this.buildingSeq.remove(b);
                            }
                        }
                    }
                }

                this.unitSeq.each((ux) -> {
                    if (!ux.allAdded && !ux.removed) {
                        ux.unit.add();
                    }

                    ux.allAdded = false;
                    ++ux.counter;
                });
                this.buildingSeq.each((bx) -> {
                    if (this.deconstructed(bx.build)) {
                        this.removeBuilding(bx.build);
                    } else {
                        if (!bx.allAdded && !bx.removed) {
                            bx.build.tile.setBlock(bx.build.block, bx.build.team, bx.build.rotation, () -> bx.build);
                        }

                        bx.allAdded = false;
                        ++bx.counter;
                    }
                });
                this.sampler.each((es) -> {
                    if (es.duration <= 0.0F && es.excludeDuration <= 0.0F) {
                        this.samplerTmp.add(es);
                        this.samplerMap.remove(es.entity.id());
                        Pools.free(es);
                    }

                    es.excludeDuration -= 15.0F;
                    es.duration -= 15.0F;
                });
                this.sampler.removeAll(this.samplerTmp);
                this.samplerTmp.clear();
            }

            if (Time.time > this.lastTime) {
                this.buildingSeq.each((bx) -> bx.counter > 10, (bx) -> bx.build.update());
                this.unitSeq.each((ex) -> ex.counter > 10, (ex) -> ex.unit.update());

                for(DisableRegenStatus s : this.status) {
                    s.update();
                    if (s.duration <= 0.0F || !s.unit.isValid()) {
                        this.status.remove(s);
                        this.statusMap.remove(s.unit.id);
                        Pools.free(s);
                    }
                }

                this.lastTime = Time.time;
            }

        }
    }

    public void notifyDamage(int unitId, float delta) {
        if (!(delta > 0.0F)) {
            DisableRegenStatus status = this.statusMap.get(unitId);
            if (status != null) {
                status.lastHealth += delta;
            }

        }
    }

    public void applyStatus(Unit unit, float duration) {
        if (!this.exclude.contains(unit.id)) {
            DisableRegenStatus status = this.statusMap.get(unit.id);
            if (status != null) {
                status.duration = Math.max(status.duration, duration);
            } else {
                DisableRegenStatus s = Pools.obtain(DisableRegenStatus.class, DisableRegenStatus::new);
                s.unit = unit;
                s.lastHealth = unit.health;
                s.duration = duration;
                this.status.add(s);
                this.statusMap.put(unit.id, s);
            }

        }
    }

    public void samplerAdd(Healthc entity) {
        this.samplerAdd(entity, false);
    }

    public void samplerAdd(Healthc entity, boolean verified) {
        if (!verified) {
            if (this.exclude.contains(entity.id())) {
                return;
            }

            EntitySampler ent;
            if ((ent = this.samplerMap.get(entity.id())) != null) {
                if (entity.health() >= ent.lastHealth && ent.excludeDuration <= 0.0F) {
                    ent.duration = Math.max(30.0F, ent.duration);
                    if (ent.penalty++ >= 5) {
                        annihilateEntity(entity, false);
                        this.samplerMap.remove(entity.id());
                        this.sampler.remove(ent);
                    }
                }

                return;
            }

            EntitySampler s = Pools.obtain(EntitySampler.class, EntitySampler::new);
            s.entity = entity;
            s.duration = 120.0F;
            s.lastHealth = entity.health();
            this.sampler.add(s);
            this.samplerMap.put(entity.id(), s);
        } else {
            EntitySampler ent = this.samplerMap.get(entity.id());
            if (ent != null) {
                ent.excludeDuration = 120.0F;
            } else {
                EntitySampler s = Pools.obtain(EntitySampler.class, EntitySampler::new);
                s.entity = entity;
                s.excludeDuration = 120.0F;
                s.duration = 0.0F;
                this.sampler.add(s);
                this.samplerMap.put(entity.id(), s);
            }
        }

    }

    public void removeBuilding(Building building) {
        this.exclude.remove(building.id);
        this.buildingSeq.removeAll((bq) -> {
            boolean t = bq.build == building;
            if (t) {
                bq.removed = true;
            }

            return t;
        });
    }

    public void removeUnit(Unit unit) {
        this.exclude.remove(unit.id);
        this.unitSeq.removeAll((uq) -> {
            boolean t = uq.unit == unit;
            if (t) {
                uq.removed = true;
            }

            return t;
        });
    }

    public void addBuilding(Building build) {
        if (this.exclude.add(build.id)) {
            this.buildingSeq.add(new BuildingQueue(build));
        }

    }

    public void addUnit(Unit unit) {
        if (this.exclude.add(unit.id)) {
            this.unitSeq.add(new UnitQueue(unit));
        }

    }

    boolean deconstructed(Building building) {
        Building alt = building.tile.build;
        return alt instanceof ConstructBlock.ConstructBuild && alt.team == building.team;
    }

    static class EntitySampler implements Pool.Poolable {
        Healthc entity;
        float duration;
        float excludeDuration = 0.0F;
        float lastHealth;
        int penalty = 0;

        public void reset() {
            this.entity = null;
            this.duration = this.excludeDuration = this.lastHealth = 0.0F;
            this.penalty = 0;
        }
    }

    static class UnitQueue {
        Unit unit;
        boolean allAdded = true;
        int counter = 0;
        boolean removed = false;

        UnitQueue(Unit unit) {
            this.unit = unit;
        }
    }

    static class BuildingQueue {
        Building build;
        boolean allAdded = true;
        int counter = 0;
        boolean removed = false;

        BuildingQueue(Building build) {
            this.build = build;
        }
    }

    static class DisableRegenStatus implements Pool.Poolable {
        Unit unit;
        float lastHealth;
        float duration;

        void update() {
            if (this.unit.health == Float.POSITIVE_INFINITY || Float.isNaN(this.unit.health)) {
                this.unit.health = this.unit.maxHealth != Float.POSITIVE_INFINITY && !Float.isNaN(this.unit.maxHealth) ? this.unit.maxHealth : 800000.0F;
            }

            float delta = this.unit.health - this.lastHealth;
            if (delta > 0.0F) {
                Unit var10000 = this.unit;
                var10000.health -= delta;
            }

            if (this.unit.health <= 0.0F) {
                this.unit.damage(0.0F);
            }

            if (Mathf.chanceDelta(0.19F)) {
                Tmp.v1.rnd(Mathf.range(this.unit.type.hitSize / 2.0F));
                ParticleFx.endRegenDisable.at(this.unit.x + Tmp.v1.x, this.unit.y + Tmp.v1.y);
            }

            this.lastHealth = this.unit.health;
            this.duration -= Time.delta;
        }

        public void reset() {
            this.unit = null;
            this.lastHealth = 0.0F;
            this.duration = 0.0F;
        }
    }
}
