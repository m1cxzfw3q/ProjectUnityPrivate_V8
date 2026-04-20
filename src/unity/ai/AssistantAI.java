package unity.ai;

import arc.func.Boolf;
import arc.func.Boolf2;
import arc.func.Prov;
import arc.math.Angles;
import arc.math.geom.Vec2;
import arc.struct.IntMap;
import arc.struct.ObjectSet;
import arc.struct.Seq;
import arc.util.Interval;
import mindustry.Vars;
import mindustry.ai.types.BuilderAI;
import mindustry.ai.types.FlyingAI;
import mindustry.ai.types.MinerAI;
import mindustry.entities.Predict;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Builderc;
import mindustry.gen.Groups;
import mindustry.gen.Healthc;
import mindustry.gen.Minerc;
import mindustry.gen.Player;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;
import mindustry.type.Weapon;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.meta.BlockFlag;
import unity.gen.Assistantc;

public class AssistantAI extends FlyingAI {
    protected static IntMap<ObjectSet<Unit>> hooks = new IntMap();
    protected Teamc user;
    protected final Seq<Assistance> services;
    protected final Interval timer = new Interval(1);
    protected Assistance current;

    public AssistantAI(Assistance... services) {
        this.services = Seq.with(services).sort((service) -> service.priority);
    }

    public static Prov<AssistantAI> create(Assistance... services) {
        return () -> new AssistantAI(services);
    }

    public void updateUnit() {
        this.updateAssistance();
        this.updateTargeting();
        if (this.fallback != null && this.target == null) {
            if (this.fallback.unit() != this.unit) {
                this.fallback.unit(this.unit);
            }

            if (this.current != null) {
                this.fallback.updateTargeting();
                if (this.current.preserveVisuals) {
                    this.fallback.updateVisuals();
                }

                if (this.current.preserveMovement) {
                    this.fallback.updateMovement();
                }
            } else {
                this.fallback.updateUnit();
            }
        } else {
            this.updateVisuals();
            this.updateMovement();
        }

    }

    public void updateAssistance() {
        if (this.current != null && !this.current.predicate.get(this)) {
            this.current.dispose(this);
            this.current.initialized = false;
            this.current = null;
        }

        for(Assistance service : this.services) {
            if (this.current != null && !this.current.predicate.get(this)) {
                this.current.dispose(this);
                this.current = null;
            }

            if (this.current != service && (this.current == null || service.priority < this.current.priority) && service.predicate.get(this)) {
                if (this.current != null) {
                    this.current.dispose(this);
                }

                this.current = service;
                break;
            }
        }

        if (this.current != null && !this.current.initialized) {
            this.current.init(this);
            this.current.initialized = true;
        }

        label47: {
            if (this.userValid()) {
                Teamc var4 = this.user;
                if (var4 instanceof Unit) {
                    Unit unit = (Unit)var4;
                    if (unit.isPlayer()) {
                        break label47;
                    }
                }
            }

            if (this.timer.get(5.0F)) {
                this.updateUser();
            }
        }

        if (this.current != null) {
            this.current.update(this);
        }

    }

    public void updateVisuals() {
        if (this.current != null && this.current.updateVisuals.get(this)) {
            if (this.current.preserveVisuals) {
                this.unit.lookAt(this.unit.prefRotation());
            }

            this.current.updateVisuals(this);
        } else {
            this.unit.lookAt(this.unit.prefRotation());
        }

    }

    public void updateTargeting() {
        super.updateTargeting();
        if (this.current != null && this.current.updateTargeting.get(this)) {
            this.current.updateTargetting(this);
        }

    }

    public void updateMovement() {
        if (this.target != null) {
            if (!this.unit.type.circleTarget) {
                this.moveTo(this.target, this.unit.type.range * 0.8F);
                this.unit.lookAt(this.target);
            } else {
                this.attack(120.0F);
            }
        } else if (this.current != null && this.current.updateMovement.get(this)) {
            if (this.current.preserveMovement) {
                if (this.target != null) {
                    if (!this.unit.type.circleTarget) {
                        this.moveTo(this.target, this.unit.type.range * 0.8F);
                        this.unit.lookAt(this.target);
                    } else {
                        this.attack(120.0F);
                    }
                } else if (this.userValid()) {
                    this.moveTo(this.user, this.unit.type.range * 0.8F);
                }
            }

            this.current.updateMovement(this);
        } else if (this.userValid()) {
            this.moveTo(this.user, this.unit.type.range * 0.8F);
        }

    }

    public void updateUser() {
        if (this.userValid()) {
            ((ObjectSet)hooks.get(this.user.id(), ObjectSet::new)).remove(this.unit);
        }

        Teamc next = null;
        int prev = Integer.MAX_VALUE;

        for(Player player : Groups.player) {
            ObjectSet<Unit> assists = (ObjectSet)hooks.get(player.id, ObjectSet::new);
            if (assists.size < prev) {
                next = player.unit();
                prev = assists.size;
            }
        }

        if (next == null) {
            next = this.targetFlag(this.unit.x, this.unit.y, BlockFlag.core, false);
        }

        if (next != null) {
            ((ObjectSet)hooks.get(next.id(), ObjectSet::new)).add(this.unit);
        }

        this.user = next;
    }

    public boolean userValid() {
        boolean var10000;
        label25: {
            if (this.user != null && this.user.isAdded()) {
                Teamc var2 = this.user;
                if (!(var2 instanceof Healthc)) {
                    break label25;
                }

                Healthc health = (Healthc)var2;
                if (health.isValid()) {
                    break label25;
                }
            }

            var10000 = false;
            return var10000;
        }

        var10000 = true;
        return var10000;
    }

    protected void displayMessage(String message) {
        Unit var3 = this.unit;
        if (var3 instanceof Assistantc) {
            Assistantc assist = (Assistantc)var3;
            assist.display(message);
        }

    }

    public void init() {
        this.updateUser();
        if (!this.unit.dead()) {
            this.displayMessage("service.init");
        }

    }

    protected boolean hasAmmo() {
        return !Vars.state.rules.unitAmmo || this.unit.ammof() > 0.0F;
    }

    public static enum Assistance {
        mendCore(-100.0F) {
            final IntMap<CoreBlock.CoreBuild> tiles = new IntMap();

            {
                this.predicate = (ai) -> this.canMend(ai) && Vars.state.teams.cores(ai.unit.team).contains((b) -> b.health() < b.maxHealth());
                this.updateVisuals = this.updateMovement = this.predicate;
                this.updateTargeting = (ai) -> this.canMend(ai) && this.tile(ai) != null && ai.unit.dst(this.tile(ai)) <= ai.unit.type.range;
            }

            boolean canMend(AssistantAI ai) {
                return ai.hasAmmo() && ai.unit.type.weapons.contains((w) -> w.bullet.healPercent > 0.0F && w.bullet.collidesTeam);
            }

            CoreBlock.CoreBuild tile(AssistantAI ai) {
                return (CoreBlock.CoreBuild)this.tiles.get(ai.unit.id);
            }

            protected void init(AssistantAI ai) {
                ai.displayMessage("service.core");
            }

            protected void dispose(AssistantAI ai) {
                if (!Vars.state.teams.cores(ai.unit.team).contains((b) -> b.health() < b.maxHealth()) && !ai.hasAmmo()) {
                    ai.displayMessage("service.coredone");
                } else {
                    ai.displayMessage("service.outofammo");
                }

            }

            protected void update(AssistantAI ai) {
                this.tiles.put(ai.unit.id, (CoreBlock.CoreBuild)Vars.state.teams.cores(ai.unit.team).min((b) -> b.health() < b.maxHealth(), (b) -> ai.unit.dst2(b)));
            }

            protected void updateVisuals(AssistantAI ai) {
                CoreBlock.CoreBuild tile = this.tile(ai);
                if (tile != null) {
                    ai.unit.lookAt(tile);
                }

            }

            protected void updateMovement(AssistantAI ai) {
                CoreBlock.CoreBuild tile = this.tile(ai);
                if (tile != null) {
                    ai.moveTo(tile, ai.unit.type.range * 0.9F);
                }

            }

            protected void updateTargetting(AssistantAI ai) {
                CoreBlock.CoreBuild tile = this.tile(ai);

                for(WeaponMount mount : ai.unit.mounts) {
                    Weapon weapon = mount.weapon;
                    if (!(weapon.bullet.healPercent <= 0.0F)) {
                        float rotation = ai.unit.rotation - 90.0F;
                        float mountX = ai.unit.x + Angles.trnsx(rotation, weapon.x, weapon.y);
                        float mountY = ai.unit.y + Angles.trnsy(rotation, weapon.x, weapon.y);
                        boolean shoot = tile.within(mountX, mountY, weapon.bullet.range()) && ai.shouldShoot();
                        Vec2 to = Predict.intercept(ai.unit, tile, weapon.bullet.speed);
                        mount.aimX = to.x;
                        mount.aimY = to.y;
                        mount.shoot = shoot;
                        mount.rotate = shoot;
                        Unit var10000 = ai.unit;
                        var10000.isShooting |= shoot;
                        if (shoot) {
                            ai.unit.aimX = mount.aimX;
                            ai.unit.aimY = mount.aimY;
                        }
                    }
                }

            }
        },
        build(0.0F) {
            {
                this.predicate = (ai) -> {
                    boolean var10000;
                    if (ai.unit.type.buildSpeed > 0.0F) {
                        Teamc builder$temp = ai.user;
                        if (builder$temp instanceof Builderc) {
                            Builderc builder = (Builderc)builder$temp;
                            if (builder.activelyBuilding()) {
                                var10000 = true;
                                return var10000;
                            }
                        }
                    }

                    var10000 = false;
                    return var10000;
                };
                this.preserveVisuals = this.preserveMovement = true;
            }

            protected void init(AssistantAI ai) {
                ai.displayMessage("service.build");
            }

            protected void update(AssistantAI ai) {
                if (!(ai.fallback instanceof BuilderAI)) {
                    ai.fallback = new BuilderAI();
                }

                BuilderAI buildAI = (BuilderAI)ai.fallback;
                Teamc var4 = ai.user;
                if (var4 instanceof Builderc) {
                    Builderc builder = (Builderc)var4;
                    buildAI.following = (Unit)builder;
                }

            }

            protected void dispose(AssistantAI ai) {
                if (ai.fallback instanceof BuilderAI) {
                    ai.fallback = null;
                    ai.unit.clearBuilding();
                }

            }
        },
        mine(10.0F) {
            {
                this.predicate = (ai) -> {
                    boolean var10000;
                    if (!ai.unit.mining() || ai.unit.closestCore().acceptStack(ai.unit.stack.item, ai.unit.stack.amount, ai.unit) <= 0) {
                        label36: {
                            if (ai.unit.type.mineTier > 0 && ai.unit.type.itemCapacity > 0) {
                                Teamc miner$temp = ai.user;
                                if (miner$temp instanceof Minerc) {
                                    Minerc miner = (Minerc)miner$temp;
                                    if (miner.mining() && ai.unit.validMine(miner.mineTile(), false) && ai.unit.closestCore().acceptStack(miner.mineTile().drop(), 1, ai.unit) > 0) {
                                        break label36;
                                    }
                                }
                            }

                            var10000 = false;
                            return var10000;
                        }
                    }

                    var10000 = true;
                    return var10000;
                };
                this.preserveVisuals = this.preserveMovement = true;
            }

            protected void init(AssistantAI ai) {
                ai.displayMessage("service.mine");
            }

            protected void update(AssistantAI ai) {
                if (!(ai.fallback instanceof MinerAI)) {
                    ai.fallback = new MinerAI();
                }

                MinerAI minAI = (MinerAI)ai.fallback;
                Teamc var4 = ai.user;
                if (var4 instanceof Minerc) {
                    Minerc miner = (Minerc)var4;
                    minAI.targetItem = ai.unit.stack.amount > 0 ? ai.unit.stack.item : (miner.mineTile() != null ? miner.mineTile().drop() : null);
                }

            }

            protected void dispose(AssistantAI ai) {
                if (ai.fallback instanceof MinerAI) {
                    ai.fallback = null;
                    ai.unit.clearItem();
                }

            }
        },
        heal(20.0F) {
            final float rad = 240.0F;
            final Boolf2<Healthc, AssistantAI> pred = (target, ai) -> target.within(ai.unit, 240.0F) && target.health() < target.maxHealth();

            {
                this.predicate = (ai) -> ai.hasAmmo() && this.hasTarget(ai);
                this.preserveVisuals = this.preserveMovement = true;
            }

            boolean hasTarget(AssistantAI ai) {
                return Groups.unit.contains((unit) -> this.pred.get(unit, ai)) || Vars.indexer.findTile(ai.unit.team, ai.unit.x, ai.unit.y, 240.0F, (tile) -> this.pred.get(tile, ai)) != null;
            }

            protected void init(AssistantAI ai) {
                ai.displayMessage("service.mend");
            }

            protected void dispose(AssistantAI ai) {
                if (ai.fallback instanceof NewHealerAI) {
                    ai.fallback = null;
                }

                if (this.hasTarget(ai) || !ai.hasAmmo()) {
                    ai.displayMessage("service.outofammo");
                }

            }

            protected void update(AssistantAI ai) {
                if (!(ai.fallback instanceof NewHealerAI)) {
                    ai.fallback = new NewHealerAI();
                }

            }
        };

        protected final float priority;
        protected Boolf<AssistantAI> predicate;
        protected Boolf<AssistantAI> updateVisuals;
        protected boolean preserveVisuals;
        protected Boolf<AssistantAI> updateTargeting;
        protected Boolf<AssistantAI> updateMovement;
        protected boolean preserveMovement;
        protected boolean initialized;

        protected void init(AssistantAI ai) {
        }

        protected void update(AssistantAI ai) {
        }

        protected void dispose(AssistantAI ai) {
        }

        protected void updateVisuals(AssistantAI ai) {
        }

        protected void updateTargetting(AssistantAI ai) {
        }

        protected void updateMovement(AssistantAI ai) {
        }

        private Assistance(float priority) {
            this.predicate = (ai) -> false;
            this.updateVisuals = (ai) -> false;
            this.preserveVisuals = false;
            this.updateTargeting = (ai) -> false;
            this.updateMovement = (ai) -> false;
            this.preserveMovement = false;
            this.initialized = false;
            this.priority = priority;
        }
    }
}
