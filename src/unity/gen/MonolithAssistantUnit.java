package unity.gen;

import arc.Core;
import arc.Events;
import arc.func.Boolf;
import arc.func.Cons;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Font;
import arc.graphics.g2d.GlyphLayout;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Position;
import arc.math.geom.QuadTree;
import arc.math.geom.Rect;
import arc.math.geom.Vec2;
import arc.math.geom.Vec3;
import arc.scene.ui.layout.Scl;
import arc.scene.ui.layout.Table;
import arc.struct.Bits;
import arc.struct.Seq;
import arc.util.Structs;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import arc.util.pooling.Pools;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.Iterator;
import mindustry.Vars;
import mindustry.ai.formations.DistanceAssignmentStrategy;
import mindustry.ai.formations.Formation;
import mindustry.ai.formations.FormationMember;
import mindustry.ai.formations.FormationPattern;
import mindustry.ai.types.FormationAI;
import mindustry.ai.types.LogicAI;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.core.World;
import mindustry.ctype.Content;
import mindustry.ctype.ContentType;
import mindustry.entities.Damage;
import mindustry.entities.Effect;
import mindustry.entities.EntityCollisions;
import mindustry.entities.Units;
import mindustry.entities.abilities.Ability;
import mindustry.entities.units.AIController;
import mindustry.entities.units.BuildPlan;
import mindustry.entities.units.StatusEntry;
import mindustry.entities.units.UnitController;
import mindustry.entities.units.WeaponMount;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.game.EventType.Trigger;
import mindustry.gen.Boundedc;
import mindustry.gen.Builderc;
import mindustry.gen.Building;
import mindustry.gen.Call;
import mindustry.gen.Commanderc;
import mindustry.gen.Drawc;
import mindustry.gen.Entityc;
import mindustry.gen.Flyingc;
import mindustry.gen.Groups;
import mindustry.gen.Healthc;
import mindustry.gen.Hitboxc;
import mindustry.gen.Itemsc;
import mindustry.gen.Minerc;
import mindustry.gen.Payloadc;
import mindustry.gen.Physicsc;
import mindustry.gen.Player;
import mindustry.gen.Posc;
import mindustry.gen.Rotc;
import mindustry.gen.Shieldc;
import mindustry.gen.Sounds;
import mindustry.gen.Statusc;
import mindustry.gen.Syncc;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;
import mindustry.gen.Unitc;
import mindustry.gen.Velc;
import mindustry.gen.Weaponsc;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.input.InputHandler;
import mindustry.io.TypeIO;
import mindustry.logic.LAccess;
import mindustry.type.Item;
import mindustry.type.StatusEffect;
import mindustry.type.UnitType;
import mindustry.type.Weapon;
import mindustry.ui.Fonts;
import mindustry.world.Block;
import mindustry.world.Build;
import mindustry.world.Tile;
import mindustry.world.blocks.ConstructBlock;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.blocks.payloads.BuildPayload;
import mindustry.world.blocks.payloads.UnitPayload;
import mindustry.world.blocks.storage.CoreBlock;
import unity.content.UnityStatusEffects;
import unity.mod.Faction;
import unity.type.UnityUnitType;

public class MonolithAssistantUnit extends Unit implements Drawc, Hitboxc, Itemsc, Rotc, Commanderc, Assistantc, Weaponsc, Healthc, Builderc, Velc, Statusc, Entityc, Physicsc, Flyingc, Posc, Monolithc, Shieldc, Teamc, Unitc, Factionc, Boundedc, Minerc, Syncc {
    private static final Seq<FormationMember> members = new Seq();
    private static final Seq<Unit> units = new Seq();
    public static final float hitDuration = 9.0F;
    public static final Vec2[] vecs = new Vec2[]{new Vec2(), new Vec2(), new Vec2(), new Vec2()};
    private static final Vec2 tmp1 = new Vec2();
    private static final Vec2 tmp2 = new Vec2();
    public static final float warpDst = 30.0F;
    private transient float rotation_TARGET_;
    private transient float rotation_LAST_;
    protected String lastText;
    protected float textFadeTime;
    private transient float textFadeTime_TARGET_;
    private transient float textFadeTime_LAST_;
    protected transient boolean isRotate;
    private transient BuildPlan lastActive;
    private transient int lastSize;
    private transient float buildAlpha = 0.0F;
    private Seq<StatusEntry> statuses = new Seq();
    private transient Bits applied;
    private transient boolean added;
    private transient boolean wasFlying;
    private transient float x_TARGET_;
    private transient float x_LAST_;
    private transient float y_TARGET_;
    private transient float y_LAST_;
    private int souls;
    private transient int maxSouls;
    private UnitController controller;
    private transient float resupplyTime;
    private transient boolean wasPlayer;
    private transient boolean wasHealed;

    protected MonolithAssistantUnit() {
        this.applied = new Bits(Vars.content.getBy(ContentType.status).size);
        this.resupplyTime = Mathf.random(10.0F);
    }

    public String toString() {
        return "MonolithAssistantUnit#" + this.id;
    }

    public boolean canMine() {
        return this.type.mineSpeed > 0.0F && this.type.mineTier >= 0;
    }

    public void drawBuildPlans() {
        Boolf<BuildPlan> skip = (planx) -> planx.progress > 0.01F || this.buildPlan() == planx && planx.initialized && (this.within((float)(planx.x * 8), (float)(planx.y * 8), 220.0F) || Vars.state.isEditor());

        for(int i = 0; i < 2; ++i) {
            for(BuildPlan plan : this.plans) {
                if (!skip.get(plan)) {
                    if (i == 0) {
                        this.drawPlan(plan, 1.0F);
                    } else {
                        this.drawPlanTop(plan, 1.0F);
                    }
                }
            }
        }

        Draw.reset();
    }

    public int maxAccepted(Item item) {
        return this.stack.item != item && this.stack.amount > 0 ? 0 : this.itemCapacity() - this.stack.amount;
    }

    public void lookAt(float angle) {
        this.rotation = Angles.moveToward(this.rotation, angle, this.type.rotateSpeed * Time.delta * this.speedMultiplier());
    }

    public float getY() {
        return this.y;
    }

    public void wobble() {
        this.x += Mathf.sin(Time.time + (float)(this.id() % 10 * 12), 25.0F, 0.05F) * Time.delta * this.elevation;
        this.y += Mathf.cos(Time.time + (float)(this.id() % 10 * 12), 25.0F, 0.05F) * Time.delta * this.elevation;
    }

    public void setupWeapons(UnitType def) {
        this.mounts = new WeaponMount[def.weapons.size];

        for(int i = 0; i < this.mounts.length; ++i) {
            this.mounts[i] = (WeaponMount)((Weapon)def.weapons.get(i)).mountType.get((Weapon)def.weapons.get(i));
        }

    }

    public void command(Formation formation, Seq<Unit> units) {
        this.clearCommand();
        units.shuffle();
        float spacing = this.hitSize * 0.9F;
        this.minFormationSpeed = this.type.speed;
        this.controlling.addAll(units);

        for(Unit unit : units) {
            FormationAI ai;
            unit.controller(ai = new FormationAI(this, formation));
            spacing = Math.max(spacing, ai.formationSize());
            this.minFormationSpeed = Math.min(this.minFormationSpeed, unit.type.speed);
        }

        this.formation = formation;
        formation.pattern.spacing = spacing;
        members.clear();

        for(Unitc u : units) {
            members.add((FormationAI)u.controller());
        }

        formation.addMembers(members);
    }

    public float realSpeed() {
        return this.speed();
    }

    public float healthf() {
        return this.health / this.maxHealth;
    }

    public boolean shouldSkip(BuildPlan request, Building core) {
        if (!Vars.state.rules.infiniteResources && !this.team.rules().infiniteResources && !request.breaking && core != null && !request.isRotation(this.team) && (!this.isBuilding() || this.within((Position)this.plans.last(), 220.0F))) {
            return request.stuck && !core.items.has(request.block.requirements) || Structs.contains(request.block.requirements, (i) -> !core.items.has(i.item, Math.min(i.amount, 15)) && Mathf.round((float)i.amount * Vars.state.rules.buildCostMultiplier) > 0) && !request.initialized;
        } else {
            return false;
        }
    }

    public boolean onSolid() {
        Tile tile = this.tileOn();
        return tile == null || tile.solid();
    }

    public boolean isBuilding() {
        return this.plans.size != 0;
    }

    public void moveAt(Vec2 vector, float acceleration) {
        Vec2 t = tmp1.set(vector);
        tmp2.set(t).sub(this.vel).limit(acceleration * vector.len() * Time.delta);
        this.vel.add(tmp2);
    }

    public void clearBuilding() {
        this.plans.clear();
    }

    public boolean isRemote() {
        boolean var10000;
        if (this instanceof Unitc) {
            Unitc u = this;
            if (u.isPlayer() && !this.isLocal()) {
                var10000 = true;
                return var10000;
            }
        }

        var10000 = false;
        return var10000;
    }

    public void set(Position pos) {
        this.set(pos.getX(), pos.getY());
    }

    public void afterRead() {
        this.updateLastPosition();
        this.afterSync();
        this.controller(this.type.createController());
    }

    public void velAddNet(Vec2 v) {
        this.vel.add(v);
        if (this.isRemote()) {
            this.x += v.x;
            this.y += v.y;
        }

    }

    public void read(Reads read) {
        this.ammo = read.f();
        this.controller = TypeIO.readController(read, this.controller);
        this.elevation = read.f();
        this.flag = read.d();
        this.health = read.f();
        this.isShooting = read.bool();
        this.lastText = TypeIO.readString(read);
        this.mineTile = TypeIO.readTile(read);
        this.mounts = TypeIO.readMounts(read, this.mounts);
        int plans_LENGTH = read.i();
        this.plans.clear();

        for(int INDEX = 0; INDEX < plans_LENGTH; ++INDEX) {
            BuildPlan plans_ITEM = TypeIO.readRequest(read);
            if (plans_ITEM != null) {
                this.plans.add(plans_ITEM);
            }
        }

        this.rotation = read.f();
        this.shield = read.f();
        this.souls = read.i();
        this.spawnedByCore = read.bool();
        this.stack = TypeIO.readItems(read, this.stack);
        int statuses_LENGTH = read.i();
        this.statuses.clear();

        for(int INDEX = 0; INDEX < statuses_LENGTH; ++INDEX) {
            StatusEntry statuses_ITEM = TypeIO.readStatus(read);
            if (statuses_ITEM != null) {
                this.statuses.add(statuses_ITEM);
            }
        }

        this.team = TypeIO.readTeam(read);
        this.textFadeTime = read.f();
        this.type = (UnitType)Vars.content.getByID(ContentType.unit, read.s());
        this.updateBuilding = read.bool();
        this.vel = TypeIO.readVec2(read, this.vel);
        this.x = read.f();
        this.y = read.f();
        this.afterRead();
    }

    private void rawDamage(float amount) {
        boolean hadShields = this.shield > 1.0E-4F;
        if (hadShields) {
            this.shieldAlpha = 1.0F;
        }

        float shieldDamage = Math.min(Math.max(this.shield, 0.0F), amount);
        this.shield -= shieldDamage;
        this.hitTime = 1.0F;
        amount -= shieldDamage;
        if (amount > 0.0F) {
            this.health -= amount;
            if (this.health <= 0.0F && !this.dead) {
                this.kill();
            }

            if (hadShields && this.shield <= 1.0E-4F) {
                Fx.unitShieldBreak.at(this.x, this.y, 0.0F, this.team.color, this);
            }
        }

    }

    public void addBuild(BuildPlan place) {
        this.addBuild(place, true);
    }

    public Bits statusBits() {
        return this.applied;
    }

    public boolean isFlying() {
        return this.elevation >= 0.09F;
    }

    public void approach(Vec2 vector) {
        this.vel.approachDelta(vector, this.type.accel * this.realSpeed());
    }

    public boolean canLand() {
        return !this.onSolid() && Units.count(this.x, this.y, this.physicSize(), (f) -> f != this && f.isGrounded()) == 0;
    }

    public Faction faction() {
        return FactionMeta.map(this.type);
    }

    public boolean isLocal() {
        boolean var10000;
        if (this != Vars.player) {
            label26: {
                if (this instanceof Unitc) {
                    Unitc u = this;
                    if (u.controller() == Vars.player) {
                        break label26;
                    }
                }

                var10000 = false;
                return var10000;
            }
        }

        var10000 = true;
        return var10000;
    }

    public void set(UnitType def, UnitController controller) {
        if (this.type != def) {
            this.setType(def);
        }

        this.controller(controller);
    }

    public CoreBlock.CoreBuild closestEnemyCore() {
        return Vars.state.teams.closestEnemyCore(this.x, this.y, this.team);
    }

    public void damage(float amount) {
        amount = Math.max(amount - this.armor, 0.1F * amount);
        amount /= this.healthMultiplier;
        this.rawDamage(amount);
    }

    public void rotateMove(Vec2 vec) {
        this.moveAt(Tmp.v2.trns(this.rotation, vec.len()));
        if (!vec.isZero()) {
            this.rotation = Angles.moveToward(this.rotation, vec.angle(), this.type.rotateSpeed * Math.max(Time.delta, 1.0F));
        }

    }

    public boolean activelyBuilding() {
        if (this.isBuilding() && !Vars.state.isEditor() && !this.within(this.buildPlan(), Vars.state.rules.infiniteResources ? Float.MAX_VALUE : 220.0F)) {
            return false;
        } else {
            return this.isBuilding() && this.updateBuilding;
        }
    }

    public boolean apply(MonolithSoul soul, int index, boolean transferred) {
        if (this.isPlayer() && !transferred && (Mathf.chance((double)(1.0F / (float)this.souls)) || index == this.souls - 1)) {
            soul.controller(this.getPlayer());
            transferred = true;
        }

        return transferred;
    }

    public void damage(float amount, boolean withEffect) {
        float pre = this.hitTime;
        this.damage(amount);
        if (!withEffect) {
            this.hitTime = pre;
        }

    }

    public boolean canShoot() {
        return !this.disarmed && (!this.type.canBoost || !this.isFlying());
    }

    public void velAddNet(float vx, float vy) {
        this.vel.add(vx, vy);
        if (this.isRemote()) {
            this.x += vx;
            this.y += vy;
        }

    }

    public boolean damaged() {
        return this.health < this.maxHealth - 0.001F;
    }

    public float clipSize() {
        if (this.isBuilding()) {
            return Vars.state.rules.infiniteResources ? Float.MAX_VALUE : Math.max(this.type.clipSize, (float)this.type.region.width) + 220.0F + 32.0F;
        } else {
            return this.mining() ? this.type.clipSize + this.type.miningRange : this.type.clipSize;
        }
    }

    public void snapInterpolation() {
        this.updateSpacing = 16L;
        this.lastUpdated = Time.millis();
        this.rotation_LAST_ = this.rotation;
        this.rotation_TARGET_ = this.rotation;
        this.textFadeTime_LAST_ = this.textFadeTime;
        this.textFadeTime_TARGET_ = this.textFadeTime;
        this.x_LAST_ = this.x;
        this.x_TARGET_ = this.x;
        this.y_LAST_ = this.y;
        this.y_TARGET_ = this.y;
    }

    public void aimLook(Position pos) {
        this.aim(pos);
        this.lookAt(pos);
    }

    public float getX() {
        return this.x;
    }

    public double sense(LAccess sensor) {
        double var10000;
        switch (sensor) {
            case totalItems:
                var10000 = (double)this.stack().amount;
                break;
            case itemCapacity:
                var10000 = (double)this.type.itemCapacity;
                break;
            case rotation:
                var10000 = (double)this.rotation;
                break;
            case health:
                var10000 = (double)this.health;
                break;
            case maxHealth:
                var10000 = (double)this.maxHealth;
                break;
            case ammo:
                var10000 = !Vars.state.rules.unitAmmo ? (double)this.type.ammoCapacity : (double)this.ammo;
                break;
            case ammoCapacity:
                var10000 = (double)this.type.ammoCapacity;
                break;
            case x:
                var10000 = (double)World.conv(this.x);
                break;
            case y:
                var10000 = (double)World.conv(this.y);
                break;
            case dead:
                var10000 = !this.dead && this.isAdded() ? (double)0.0F : (double)1.0F;
                break;
            case team:
                var10000 = (double)this.team.id;
                break;
            case shooting:
                var10000 = this.isShooting() ? (double)1.0F : (double)0.0F;
                break;
            case boosting:
                var10000 = this.type.canBoost && this.isFlying() ? (double)1.0F : (double)0.0F;
                break;
            case range:
                var10000 = (double)(this.range() / 8.0F);
                break;
            case shootX:
                var10000 = (double)World.conv(this.aimX());
                break;
            case shootY:
                var10000 = (double)World.conv(this.aimY());
                break;
            case mining:
                var10000 = this.mining() ? (double)1.0F : (double)0.0F;
                break;
            case mineX:
                var10000 = this.mining() ? (double)this.mineTile.x : (double)-1.0F;
                break;
            case mineY:
                var10000 = this.mining() ? (double)this.mineTile.y : (double)-1.0F;
                break;
            case flag:
                var10000 = this.flag;
                break;
            case controlled:
                var10000 = !this.isValid() ? (double)0.0F : (this.controller instanceof LogicAI ? (double)1.0F : (this.controller instanceof Player ? (double)2.0F : (this.controller instanceof FormationAI ? (double)3.0F : (double)0.0F)));
                break;
            case commanded:
                var10000 = this.controller instanceof FormationAI && this.isValid() ? (double)1.0F : (double)0.0F;
                break;
            case payloadCount:
                int var4;
                if (this instanceof Payloadc) {
                    Payloadc pay = (Payloadc)this;
                    var4 = pay.payloads().size;
                } else {
                    var4 = 0;
                }

                var10000 = (double)var4;
                break;
            case size:
                var10000 = (double)(this.hitSize / 8.0F);
                break;
            default:
                var10000 = Double.NaN;
        }

        return var10000;
    }

    public void clampHealth() {
        this.health = Math.min(this.health, this.maxHealth);
    }

    public void setWeaponRotation(float rotation) {
        for(WeaponMount mount : this.mounts) {
            mount.rotation = rotation;
        }

    }

    public TextureRegion icon() {
        return this.type.fullIcon;
    }

    public float deltaAngle() {
        return Mathf.angle(this.deltaX, this.deltaY);
    }

    public float hitSize() {
        return this.hitSize;
    }

    public void killed() {
        this.clearCommand();
        if (Vars.net.server() || !Vars.net.active()) {
            this.spreadSouls();
        }

        this.wasPlayer = this.isLocal();
        this.health = Math.min(this.health, 0.0F);
        this.dead = true;
        if (!this.type.flying) {
            this.destroy();
        }

    }

    public void moveAt(Vec2 vector) {
        this.moveAt(vector, this.type.accel);
    }

    public void join() {
        if (this.canJoin()) {
            ++this.souls;
        }

    }

    public boolean inRange(Position other) {
        return this.within(other, this.type.range);
    }

    public boolean isCommanding() {
        return this.formation != null;
    }

    public float deltaLen() {
        return Mathf.len(this.deltaX, this.deltaY);
    }

    public void draw() {
        Draw.z(150.0F);
        float z = Drawf.text();
        Font font = Fonts.def;
        GlyphLayout layout = (GlyphLayout)Pools.obtain(GlyphLayout.class, GlyphLayout::new);
        float textHeight = 15.0F;
        boolean ints = font.usesIntegerPositions();
        font.setUseIntegerPositions(false);
        font.getData().setScale(0.25F / Scl.scl(1.0F));
        String text = this.lastText != null ? Core.bundle.getOrNull(this.lastText) : null;
        if (Core.settings.getBool("playerchat") && this.textFadeTime > 0.0F && text != null) {
            float width = 100.0F;
            float visualFadeTime = 1.0F - Mathf.curve(1.0F - this.textFadeTime, 0.9F);
            font.setColor(1.0F, 1.0F, 1.0F, this.textFadeTime <= 0.0F ? 1.0F : visualFadeTime);
            layout.setText(font, text, Color.lightGray, width, 4, true);
            Draw.color(0.0F, 0.0F, 0.0F, 0.3F * (this.textFadeTime <= 0.0F ? 1.0F : visualFadeTime));
            Fill.rect(this.x, this.y + textHeight + layout.height - layout.height / 2.0F, layout.width + 2.0F, layout.height + 3.0F);
            font.draw(text, this.x - width / 2.0F, this.y + textHeight + layout.height, width, 1, true);
        }

        Draw.reset();
        Pools.free(layout);
        font.getData().setScale(1.0F);
        font.setColor(Color.white);
        font.setUseIntegerPositions(ints);
        Draw.z(z);
        boolean active = this.activelyBuilding();
        if (active || this.lastActive != null) {
            Draw.z(115.0F);
            BuildPlan plan = active ? this.buildPlan() : this.lastActive;
            Tile tile = Vars.world.tile(plan.x, plan.y);
            CoreBlock.CoreBuild core = this.team.core();
            if (tile != null && this.within(plan, Vars.state.rules.infiniteResources ? Float.MAX_VALUE : 220.0F)) {
                if (core != null && active && !this.isLocal() && !(tile.block() instanceof ConstructBlock)) {
                    Draw.z(84.0F);
                    this.drawPlan(plan, 0.5F);
                    this.drawPlanTop(plan, 0.5F);
                    Draw.z(115.0F);
                }

                ints = (boolean)(plan.breaking ? (active ? tile.block().size : this.lastSize) : plan.block.size);
                float tx = plan.drawx();
                float ty = plan.drawy();
                Lines.stroke(1.0F, plan.breaking ? Pal.remove : Pal.accent);
                float focusLen = this.type.buildBeamOffset + Mathf.absin(Time.time, 3.0F, 0.6F);
                float px = this.x + Angles.trnsx(this.rotation, focusLen);
                float py = this.y + Angles.trnsy(this.rotation, focusLen);
                float sz = (float)(8 * ints) / 2.0F;
                float ang = this.angleTo(tx, ty);
                vecs[0].set(tx - sz, ty - sz);
                vecs[1].set(tx + sz, ty - sz);
                vecs[2].set(tx - sz, ty + sz);
                vecs[3].set(tx + sz, ty + sz);
                Arrays.sort(vecs, Structs.comparingFloat((vec) -> -Angles.angleDist(this.angleTo(vec), ang)));
                Vec2 close = (Vec2)Geometry.findClosest(this.x, this.y, vecs);
                float x1 = vecs[0].x;
                float y1 = vecs[0].y;
                float x2 = close.x;
                float y2 = close.y;
                float x3 = vecs[1].x;
                float y3 = vecs[1].y;
                Draw.z(122.0F);
                Draw.alpha(this.buildAlpha);
                if (!active && !(tile.build instanceof ConstructBlock.ConstructBuild)) {
                    Fill.square(plan.drawx(), plan.drawy(), (float)(ints * 8) / 2.0F);
                }

                if (Vars.renderer.animateShields) {
                    if (close != vecs[0] && close != vecs[1]) {
                        Fill.tri(px, py, x1, y1, x2, y2);
                        Fill.tri(px, py, x3, y3, x2, y2);
                    } else {
                        Fill.tri(px, py, x1, y1, x3, y3);
                    }
                } else {
                    Lines.line(px, py, x1, y1);
                    Lines.line(px, py, x3, y3);
                }

                Fill.square(px, py, 1.8F + Mathf.absin(Time.time, 2.2F, 1.1F), this.rotation + 45.0F);
                Draw.reset();
                Draw.z(115.0F);
            }
        }

        for(StatusEntry e : this.statuses) {
            e.effect.draw(this, e.time);
        }

        this.type.draw(this);
        if (this.mining()) {
            float focusLen = this.hitSize / 2.0F + Mathf.absin(Time.time, 1.1F, 0.5F);
            float swingScl = 12.0F;
            float swingMag = 1.0F;
            textHeight = 0.3F;
            float px = this.x + Angles.trnsx(this.rotation, focusLen);
            float py = this.y + Angles.trnsy(this.rotation, focusLen);
            float ex = this.mineTile.worldx() + Mathf.sin(Time.time + 48.0F, swingScl, swingMag);
            float ey = this.mineTile.worldy() + Mathf.sin(Time.time + 48.0F, swingScl + 2.0F, swingMag);
            Draw.z(115.1F);
            Draw.color(Color.lightGray, Color.white, 1.0F - textHeight + Mathf.absin(Time.time, 0.5F, textHeight));
            Drawf.laser(this.team(), Core.atlas.find("minelaser"), Core.atlas.find("minelaser-end"), px, py, ex, ey, 0.75F);
            if (this.isLocal()) {
                Lines.stroke(1.0F, Pal.accent);
                Lines.poly(this.mineTile.worldx(), this.mineTile.worldy(), 4, 4.0F * Mathf.sqrt2, Time.time);
            }

            Draw.color();
        }

    }

    public void snapSync() {
        this.updateSpacing = 16L;
        this.lastUpdated = Time.millis();
        this.rotation_LAST_ = this.rotation_TARGET_;
        this.rotation = this.rotation_TARGET_;
        this.textFadeTime_LAST_ = this.textFadeTime_TARGET_;
        this.textFadeTime = this.textFadeTime_TARGET_;
        this.x_LAST_ = this.x_TARGET_;
        this.x = this.x_TARGET_;
        this.y_LAST_ = this.y_TARGET_;
        this.y = this.y_TARGET_;
    }

    public void destroy() {
        if (this.isAdded()) {
            float explosiveness = 2.0F + this.item().explosiveness * (float)this.stack().amount * 1.53F;
            float flammability = this.item().flammability * (float)this.stack().amount / 1.9F;
            float power = this.item().charge * Mathf.pow((float)this.stack().amount, 1.11F) * 160.0F;
            if (!this.spawnedByCore) {
                Damage.dynamicExplosion(this.x, this.y, flammability, explosiveness, power, this.bounds() / 2.0F, Vars.state.rules.damageExplosions, this.item().flammability > 1.0F, this.team, this.type.deathExplosionEffect);
            } else {
                this.type.deathExplosionEffect.at(this.x, this.y, this.bounds() / 2.0F / 8.0F);
            }

            float shake = this.hitSize / 3.0F;
            Effect.scorch(this.x, this.y, (int)(this.hitSize / 5.0F));
            Effect.shake(shake, shake, this);
            this.type.deathSound.at(this);
            Events.fire(new EventType.UnitDestroyEvent(this));
            if (explosiveness > 7.0F && (this.isLocal() || this.wasPlayer)) {
                Events.fire(Trigger.suicideBomb);
            }

            for(WeaponMount mount : this.mounts) {
                if (mount.weapon.shootOnDeath && (!mount.weapon.bullet.killShooter || !mount.shoot)) {
                    mount.reload = 0.0F;
                    mount.shoot = true;
                    mount.weapon.update(this, mount);
                }
            }

            if (this.type.flying && !this.spawnedByCore) {
                Damage.damage(this.team, this.x, this.y, Mathf.pow(this.hitSize, 0.94F) * 1.25F, Mathf.pow(this.hitSize, 0.75F) * this.type.crashDamageMultiplier * 5.0F, true, false, true);
            }

            if (!Vars.headless) {
                for(int i = 0; i < this.type.wreckRegions.length; ++i) {
                    if (this.type.wreckRegions[i].found()) {
                        float range = this.type.hitSize / 4.0F;
                        Tmp.v1.rnd(range);
                        Effect.decal(this.type.wreckRegions[i], this.x + Tmp.v1.x, this.y + Tmp.v1.y, this.rotation - 90.0F);
                    }
                }
            }

            if (this.abilities.size > 0) {
                for(Ability a : this.abilities) {
                    a.death(this);
                }
            }

            this.remove();
        }
    }

    public boolean canDrown() {
        return this.isGrounded() && !this.hovering && this.type.canDrown;
    }

    public void landed() {
        if (this.type.landShake > 0.0F) {
            Effect.shake(this.type.landShake, this.type.landShake, this);
        }

        this.type.landed(this);
    }

    public Floor floorOn() {
        Tile tile = this.tileOn();
        return tile != null && tile.block() == Blocks.air ? tile.floor() : (Floor)Blocks.air;
    }

    public Block blockOn() {
        Tile tile = this.tileOn();
        return tile == null ? Blocks.air : tile.block();
    }

    public void clearStatuses() {
        this.statuses.clear();
    }

    public int count() {
        return this.team.data().countType(this.type);
    }

    public void impulse(float x, float y) {
        float mass = this.mass();
        this.vel.add(x / mass, y / mass);
    }

    public Color statusColor() {
        if (this.statuses.size == 0) {
            return Tmp.c1.set(Color.white);
        } else {
            float r = 1.0F;
            float g = 1.0F;
            float b = 1.0F;
            float total = 0.0F;

            for(StatusEntry entry : this.statuses) {
                float intensity = entry.time < 10.0F ? entry.time / 10.0F : 1.0F;
                r += entry.effect.color.r * intensity;
                g += entry.effect.color.g * intensity;
                b += entry.effect.color.b * intensity;
                total += intensity;
            }

            float count = (float)this.statuses.size + total;
            return Tmp.c1.set(r / count, g / count, b / count, 1.0F);
        }
    }

    public boolean isAI() {
        return this.controller instanceof AIController;
    }

    public Item item() {
        return this.stack.item;
    }

    public boolean collides(Hitboxc other) {
        return true;
    }

    public <T> T as() {
        return (T)this;
    }

    public Object senseObject(LAccess sensor) {
        Object var10000;
        switch (sensor) {
            case type:
                var10000 = this.type;
                break;
            case name:
                UnitController var9 = this.controller;
                if (var9 instanceof Player) {
                    Player p = (Player)var9;
                    var10000 = p.name;
                } else {
                    var10000 = null;
                }
                break;
            case firstItem:
                var10000 = this.stack().amount == 0 ? null : this.item();
                break;
            case controller:
                if (!this.isValid()) {
                    var10000 = null;
                } else {
                    UnitController var10 = this.controller;
                    if (var10 instanceof LogicAI) {
                        LogicAI log = (LogicAI)var10;
                        var10000 = log.controller;
                    } else {
                        var10 = this.controller;
                        if (var10 instanceof FormationAI) {
                            FormationAI form = (FormationAI)var10;
                            var10000 = form.leader;
                        } else {
                            var10000 = this;
                        }
                    }
                }
                break;
            case payloadType:
                if (this instanceof Payloadc) {
                    Payloadc pay = (Payloadc)this;
                    if (pay.payloads().isEmpty()) {
                        var10000 = null;
                    } else {
                        Object var5 = pay.payloads().peek();
                        if (var5 instanceof UnitPayload) {
                            UnitPayload p1 = (UnitPayload)var5;
                            var10000 = p1.unit.type;
                        } else {
                            var5 = pay.payloads().peek();
                            if (var5 instanceof BuildPayload) {
                                BuildPayload p2 = (BuildPayload)var5;
                                var10000 = p2.block();
                            } else {
                                var10000 = null;
                            }
                        }
                    }
                } else {
                    var10000 = null;
                }
                break;
            default:
                var10000 = noSensed;
        }

        return var10000;
    }

    public void readSyncManual(FloatBuffer buffer) {
        if (this.lastUpdated != 0L) {
            this.updateSpacing = Time.timeSinceMillis(this.lastUpdated);
        }

        this.lastUpdated = Time.millis();
        this.rotation_LAST_ = this.rotation;
        this.rotation_TARGET_ = buffer.get();
        this.textFadeTime_LAST_ = this.textFadeTime;
        this.textFadeTime_TARGET_ = buffer.get();
        this.x_LAST_ = this.x;
        this.x_TARGET_ = buffer.get();
        this.y_LAST_ = this.y;
        this.y_TARGET_ = buffer.get();
    }

    public void apply(StatusEffect effect, float duration) {
        if (effect != StatusEffects.none && effect != null && !this.isImmune(effect)) {
            if (Vars.state.isCampaign()) {
                effect.unlock();
            }

            if (this.statuses.size > 0) {
                for(int i = 0; i < this.statuses.size; ++i) {
                    StatusEntry entry = (StatusEntry)this.statuses.get(i);
                    if (entry.effect == effect) {
                        entry.time = Math.max(entry.time, duration);
                        return;
                    }

                    if (entry.effect.applyTransition(this, effect, entry, duration)) {
                        return;
                    }
                }
            }

            if (!effect.reactive) {
                StatusEntry entry = (StatusEntry)Pools.obtain(StatusEntry.class, StatusEntry::new);
                entry.set(effect, duration);
                this.statuses.add(entry);
            }

        }
    }

    public void add() {
        if (!this.added) {
            Groups.all.add(this);
            Groups.draw.add(this);
            Groups.unit.add(this);
            Groups.sync.add(this);
            this.updateLastPosition();
            this.added = true;
            if (this.spawnedByCore) {
                this.maxSouls = 0;
                this.souls = 0;
            }

            this.team.data().updateCount(this.type, 1);
            if (this.count() > this.cap() && !this.spawnedByCore && !this.dead && !Vars.state.rules.editor) {
                Call.unitCapDeath(this);
                this.team.data().updateCount(this.type, -1);
            }

        }
    }

    public void interpolate() {
        if (this.lastUpdated != 0L && this.updateSpacing != 0L) {
            float timeSinceUpdate = (float)Time.timeSinceMillis(this.lastUpdated);
            float alpha = Math.min(timeSinceUpdate / (float)this.updateSpacing, 2.0F);
            this.rotation = Mathf.slerp(this.rotation_LAST_, this.rotation_TARGET_, alpha);
            this.textFadeTime = Mathf.lerp(this.textFadeTime_LAST_, this.textFadeTime_TARGET_, alpha);
            this.x = Mathf.lerp(this.x_LAST_, this.x_TARGET_, alpha);
            this.y = Mathf.lerp(this.y_LAST_, this.y_TARGET_, alpha);
        } else if (this.lastUpdated != 0L) {
            this.rotation = this.rotation_TARGET_;
            this.textFadeTime = this.textFadeTime_TARGET_;
            this.x = this.x_TARGET_;
            this.y = this.y_TARGET_;
        }

    }

    public EntityCollisions.SolidPred solidity() {
        return null;
    }

    public boolean isCounted() {
        return this.type.isCounted;
    }

    public String getControllerName() {
        if (this.isPlayer()) {
            return this.getPlayer().name;
        } else {
            UnitController var2 = this.controller;
            if (var2 instanceof LogicAI) {
                LogicAI ai = (LogicAI)var2;
                if (ai.controller != null) {
                    return ai.controller.lastAccessed;
                }
            }

            var2 = this.controller;
            if (var2 instanceof FormationAI) {
                FormationAI ai = (FormationAI)var2;
                if (ai.leader != null && ai.leader.isPlayer()) {
                    return ai.leader.getPlayer().name;
                }
            }

            return null;
        }
    }

    public void writeSyncManual(FloatBuffer buffer) {
        buffer.put(this.rotation);
        buffer.put(this.textFadeTime);
        buffer.put(this.x);
        buffer.put(this.y);
    }

    public boolean isSameFaction(Entityc other) {
        if (other instanceof Building) {
            Building build = (Building)other;
            return this.faction() == FactionMeta.map(build.block);
        } else {
            boolean var10000;
            if (other instanceof Factionc) {
                Factionc fac = (Factionc)other;
                if (this.faction() == fac.faction()) {
                    var10000 = true;
                    return var10000;
                }
            }

            var10000 = false;
            return var10000;
        }
    }

    public double sense(Content content) {
        return content == this.stack().item ? (double)this.stack().amount : Double.NaN;
    }

    public void move(float cx, float cy) {
        EntityCollisions.SolidPred check = this.solidity();
        if (check != null) {
            Vars.collisions.move(this, cx, cy, check);
        } else {
            this.x += cx;
            this.y += cy;
        }

    }

    public boolean cheating() {
        return this.team.rules().cheat;
    }

    public float getDuration(StatusEffect effect) {
        StatusEntry entry = (StatusEntry)this.statuses.find((e) -> e.effect == effect);
        return entry == null ? 0.0F : entry.time;
    }

    public boolean emitWalkSound() {
        return true;
    }

    public boolean acceptsItem(Item item) {
        return !this.hasItem() || item == this.stack.item && this.stack.amount + 1 <= this.itemCapacity();
    }

    public float bounds() {
        return this.hitSize * 2.0F;
    }

    public void healFract(float amount) {
        this.heal(amount * this.maxHealth);
    }

    public void addItem(Item item) {
        this.addItem(item, 1);
    }

    public void hitboxTile(Rect rect) {
        float size = Math.min(this.hitSize * 0.66F, 7.9F);
        rect.setCentered(this.x, this.y, size, size);
    }

    public int cap() {
        return Units.getCap(this.team);
    }

    public boolean disabled() {
        return !this.spawnedByCore && !this.hasSouls();
    }

    public void display(String text) {
        this.lastText = text;
        this.textFadeTime = 1.0F;
    }

    public void trns(float x, float y) {
        this.set(this.x + x, this.y + y);
    }

    public float mass() {
        return this.hitSize * this.hitSize * (float)Math.PI;
    }

    public float prefRotation() {
        if (this.activelyBuilding()) {
            return this.angleTo(this.buildPlan());
        } else if (this.mineTile != null) {
            return this.angleTo(this.mineTile);
        } else {
            return this.moving() && this.type.omniMovement ? this.vel().angle() : this.rotation;
        }
    }

    public void write(Writes write) {
        write.f(this.ammo);
        TypeIO.writeController(write, this.controller);
        write.f(this.elevation);
        write.d(this.flag);
        write.f(this.health);
        write.bool(this.isShooting);
        TypeIO.writeString(write, this.lastText);
        TypeIO.writeTile(write, this.mineTile);
        TypeIO.writeMounts(write, this.mounts);
        write.i(this.plans.size);

        for(int INDEX = 0; INDEX < this.plans.size; ++INDEX) {
            TypeIO.writeRequest(write, (BuildPlan)this.plans.get(INDEX));
        }

        write.f(this.rotation);
        write.f(this.shield);
        write.i(this.souls);
        write.bool(this.spawnedByCore);
        TypeIO.writeItems(write, this.stack);
        write.i(this.statuses.size);

        for(int INDEX = 0; INDEX < this.statuses.size; ++INDEX) {
            TypeIO.writeStatus(write, (StatusEntry)this.statuses.get(INDEX));
        }

        TypeIO.writeTeam(write, this.team);
        write.f(this.textFadeTime);
        write.s(this.type.id);
        write.bool(this.updateBuilding);
        TypeIO.writeVec2(write, this.vel);
        write.f(this.x);
        write.f(this.y);
    }

    public void unjoin() {
        if (this.souls > 0) {
            --this.souls;
        }

    }

    public void remove() {
        if (this.added) {
            Groups.all.remove(this);
            Groups.draw.remove(this);
            Groups.unit.remove(this);
            Groups.sync.remove(this);
            this.clearCommand();

            for(WeaponMount mount : this.mounts) {
                if (mount.bullet != null && mount.bullet.owner == this) {
                    mount.bullet.time = mount.bullet.lifetime - 10.0F;
                    mount.bullet = null;
                }

                if (mount.sound != null) {
                    mount.sound.stop();
                }
            }

            this.added = false;
            this.team.data().updateCount(this.type, -1);
            this.controller.removed(this);
            if (Vars.net.client()) {
                Vars.netClient.addRemovedEntity(this.id());
            }

        }
    }

    public void getCollisions(Cons<QuadTree> consumer) {
    }

    public void movePref(Vec2 movement) {
        if (this.type.omniMovement) {
            this.moveAt(movement);
        } else {
            this.rotateMove(movement);
        }

    }

    public void update() {
        if (this.disabled()) {
            if (!this.hasEffect(UnityStatusEffects.disabled)) {
                this.apply(UnityStatusEffects.disabled, Float.MAX_VALUE);
            }
        } else {
            this.unapply(UnityStatusEffects.disabled);
        }

        if (!Vars.net.client() || this.isLocal()) {
            float px = this.x;
            float py = this.y;
            this.move(this.vel.x * Time.delta, this.vel.y * Time.delta);
            if (Mathf.equal(px, this.x)) {
                this.vel.x = 0.0F;
            }

            if (Mathf.equal(py, this.y)) {
                this.vel.y = 0.0F;
            }

            this.vel.scl(Math.max(1.0F - this.drag * Time.delta, 0.0F));
        }

        this.stack.amount = Mathf.clamp(this.stack.amount, 0, this.itemCapacity());
        this.itemTime = Mathf.lerpDelta(this.itemTime, (float)Mathf.num(this.hasItem()), 0.05F);
        if (this.controlling.isEmpty() && !Vars.net.client()) {
            this.formation = null;
        }

        if (this.formation != null) {
            this.formation.anchor.set(this.x, this.y, 0.0F);
            this.formation.updateSlots();
            this.controlling.removeAll((u) -> {
                boolean var10000;
                if (!u.dead) {
                    UnitController ai$temp = u.controller();
                    if (ai$temp instanceof FormationAI) {
                        FormationAI ai = (FormationAI)ai$temp;
                        if (ai.leader == this) {
                            var10000 = false;
                            return var10000;
                        }
                    }
                }

                var10000 = true;
                return var10000;
            });
        }

        this.textFadeTime -= Time.delta / 300.0F;

        for(WeaponMount mount : this.mounts) {
            mount.weapon.update(this, mount);
        }

        this.hitTime -= Time.delta / 9.0F;
        if (!Vars.headless) {
            if (this.lastActive != null && this.buildAlpha <= 0.01F) {
                this.lastActive = null;
            }

            this.buildAlpha = Mathf.lerpDelta(this.buildAlpha, this.activelyBuilding() ? 1.0F : 0.0F, 0.15F);
        }

        if (this.updateBuilding && this.canBuild()) {
            float finalPlaceDst = Vars.state.rules.infiniteResources ? Float.MAX_VALUE : 220.0F;
            boolean infinite = Vars.state.rules.infiniteResources || this.team().rules().infiniteResources;
            Iterator<BuildPlan> it = this.plans.iterator();

            while(it.hasNext()) {
                BuildPlan req = (BuildPlan)it.next();
                Tile tile = Vars.world.tile(req.x, req.y);
                if (tile == null || req.breaking && tile.block() == Blocks.air || !req.breaking && (tile.build != null && tile.build.rotation == req.rotation || !req.block.rotate) && tile.block() == req.block) {
                    it.remove();
                }
            }

            CoreBlock.CoreBuild core = this.core();
            if (this.buildPlan() != null) {
                BuildPlan req;
                if (this.plans.size > 1) {
                    for(int total = 0; (!this.within((req = this.buildPlan()).tile(), finalPlaceDst) || this.shouldSkip(req, core)) && total < this.plans.size; ++total) {
                        this.plans.removeFirst();
                        this.plans.addLast(req);
                    }
                }

                BuildPlan current = this.buildPlan();
                Tile tile = current.tile();
                this.lastActive = current;
                this.buildAlpha = 1.0F;
                if (current.breaking) {
                    this.lastSize = tile.block().size;
                }

                if (this.within(tile, finalPlaceDst)) {
                    label583: {
                        if (!Vars.headless) {
                            Vars.control.sound.loop(Sounds.build, tile, 0.51F);
                        }

                        Building hasAll = tile.build;
                        if (hasAll instanceof ConstructBlock.ConstructBuild) {
                            ConstructBlock.ConstructBuild cb = (ConstructBlock.ConstructBuild)hasAll;
                            if (tile.team() != this.team && tile.team() != Team.derelict || !current.breaking && (cb.current != current.block || cb.tile != current.tile())) {
                                this.plans.removeFirst();
                                break label583;
                            }
                        } else if (!current.initialized && !current.breaking && Build.validPlace(current.block, this.team, current.x, current.y, current.rotation)) {
                            boolean hasAll = infinite || current.isRotation(this.team) || !Structs.contains(current.block.requirements, (i) -> core != null && !core.items.has(i.item, Math.min(Mathf.round((float)i.amount * Vars.state.rules.buildCostMultiplier), 1)));
                            if (hasAll) {
                                Call.beginPlace(this, current.block, this.team, current.x, current.y, current.rotation);
                            } else {
                                current.stuck = true;
                            }
                        } else {
                            if (current.initialized || !current.breaking || !Build.validBreak(this.team, current.x, current.y)) {
                                this.plans.removeFirst();
                                break label583;
                            }

                            Call.beginBreak(this, this.team, current.x, current.y);
                        }

                        if (tile.build instanceof ConstructBlock.ConstructBuild && !current.initialized) {
                            Core.app.post(() -> Events.fire(new EventType.BuildSelectEvent(tile, this.team, this, current.breaking)));
                            current.initialized = true;
                        }

                        if (core != null || infinite) {
                            hasAll = tile.build;
                            if (hasAll instanceof ConstructBlock.ConstructBuild) {
                                ConstructBlock.ConstructBuild entity = (ConstructBlock.ConstructBuild)hasAll;
                                float bs = 1.0F / entity.buildCost * Time.delta * this.type.buildSpeed * this.buildSpeedMultiplier * Vars.state.rules.buildSpeed(this.team);
                                if (current.breaking) {
                                    entity.deconstruct(this, core, bs);
                                } else {
                                    entity.construct(this, core, bs, current.config);
                                }

                                current.stuck = Mathf.equal(current.progress, entity.progress);
                                current.progress = entity.progress;
                            }
                        }
                    }
                }
            }
        }

        Floor floor = this.floorOn();
        if (this.isGrounded() && !this.type.hovering) {
            this.apply(floor.status, floor.statusDuration);
        }

        this.applied.clear();
        this.speedMultiplier = this.damageMultiplier = this.healthMultiplier = this.reloadMultiplier = this.buildSpeedMultiplier = this.dragMultiplier = 1.0F;
        this.disarmed = false;
        if (!this.statuses.isEmpty()) {
            int index = 0;

            while(index < this.statuses.size) {
                StatusEntry entry = (StatusEntry)this.statuses.get(index++);
                entry.time = Math.max(entry.time - Time.delta, 0.0F);
                if (entry.effect != null && (!(entry.time <= 0.0F) || entry.effect.permanent)) {
                    this.applied.set(entry.effect.id);
                    this.speedMultiplier *= entry.effect.speedMultiplier;
                    this.healthMultiplier *= entry.effect.healthMultiplier;
                    this.damageMultiplier *= entry.effect.damageMultiplier;
                    this.reloadMultiplier *= entry.effect.reloadMultiplier;
                    this.buildSpeedMultiplier *= entry.effect.buildSpeedMultiplier;
                    this.dragMultiplier *= entry.effect.dragMultiplier;
                    this.disarmed |= entry.effect.disarm;
                    entry.effect.update(this, entry.time);
                } else {
                    Pools.free(entry);
                    --index;
                    this.statuses.remove(index);
                }
            }
        }

        floor = this.floorOn();
        if (this.isFlying() != this.wasFlying) {
            if (this.wasFlying && this.tileOn() != null) {
                Fx.unitLand.at(this.x, this.y, this.floorOn().isLiquid ? 1.0F : 0.5F, this.tileOn().floor().mapColor);
            }

            this.wasFlying = this.isFlying();
        }

        if (!this.hovering && this.isGrounded() && (this.splashTimer += Mathf.dst(this.deltaX(), this.deltaY())) >= 7.0F + this.hitSize() / 8.0F) {
            floor.walkEffect.at(this.x, this.y, this.hitSize() / 8.0F, floor.mapColor);
            this.splashTimer = 0.0F;
            if (this.emitWalkSound()) {
                floor.walkSound.at(this.x, this.y, Mathf.random(floor.walkSoundPitchMin, floor.walkSoundPitchMax), floor.walkSoundVolume);
            }
        }

        this.updateDrowning();
        this.shieldAlpha -= Time.delta / 15.0F;
        if (this.shieldAlpha < 0.0F) {
            this.shieldAlpha = 0.0F;
        }

        this.type.update(this);
        if (this.wasHealed && this.healTime <= -1.0F) {
            this.healTime = 1.0F;
        }

        this.healTime -= Time.delta / 20.0F;
        this.wasHealed = false;
        if (!this.type.supportsEnv(Vars.state.rules.environment) && !this.dead) {
            Call.unitCapDeath(this);
            this.team.data().updateCount(this.type, -1);
        }

        if (Vars.state.rules.unitAmmo && this.ammo < (float)this.type.ammoCapacity - 1.0E-4F) {
            this.resupplyTime += Time.delta;
            if (this.resupplyTime > 10.0F) {
                this.type.ammoType.resupply(this);
                this.resupplyTime = 0.0F;
            }
        }

        if (this.abilities.size > 0) {
            for(Ability a : this.abilities) {
                a.update(this);
            }
        }

        this.drag = this.type.drag * (this.isGrounded() ? this.floorOn().dragMultiplier : 1.0F) * this.dragMultiplier;
        if (this.team != Vars.state.rules.waveTeam && Vars.state.hasSpawns() && (!Vars.net.client() || this.isLocal())) {
            float relativeSize = Vars.state.rules.dropZoneRadius + this.hitSize / 2.0F + 1.0F;

            for(Tile spawn : Vars.spawner.getSpawns()) {
                if (this.within(spawn.worldx(), spawn.worldy(), relativeSize)) {
                    this.velAddNet(Tmp.v1.set(this).sub(spawn.worldx(), spawn.worldy()).setLength(1.1F - this.dst(spawn) / relativeSize).scl(0.45F * Time.delta));
                }
            }
        }

        if (this.dead || this.health <= 0.0F) {
            this.drag = 0.01F;
            if (Mathf.chanceDelta(0.1)) {
                Tmp.v1.rnd(Mathf.range(this.hitSize));
                this.type.fallEffect.at(this.x + Tmp.v1.x, this.y + Tmp.v1.y);
            }

            if (Mathf.chanceDelta(0.2)) {
                float offset = this.type.engineOffset / 2.0F + this.type.engineOffset / 2.0F * this.elevation;
                float range = Mathf.range(this.type.engineSize);
                this.type.fallThrusterEffect.at(this.x + Angles.trnsx(this.rotation + 180.0F, offset) + Mathf.range(range), this.y + Angles.trnsy(this.rotation + 180.0F, offset) + Mathf.range(range), Mathf.random());
            }

            this.elevation -= this.type.fallSpeed * Time.delta;
            if (this.isGrounded() || this.health <= -this.maxHealth) {
                Call.unitDestroy(this.id);
            }
        }

        Tile tile = this.tileOn();
        Floor floor = this.floorOn();
        if (tile != null && this.isGrounded() && !this.type.hovering) {
            if (tile.build != null) {
                tile.build.unitOn(this);
            }

            if (floor.damageTaken > 0.0F) {
                this.damageContinuous(floor.damageTaken);
            }
        }

        if (tile != null && !this.canPassOn()) {
            if (this.type.canBoost) {
                this.elevation = 1.0F;
            } else if (!Vars.net.client()) {
                this.kill();
            }
        }

        if (!Vars.net.client() && !this.dead) {
            this.controller.updateUnit();
        }

        if (!this.controller.isValidController()) {
            this.resetController();
        }

        if (this.spawnedByCore && !this.isPlayer() && !this.dead) {
            Call.unitDespawn(this);
        }

        if (!Vars.net.client() || this.isLocal()) {
            float dx = 0.0F;
            float dy = 0.0F;
            if (this.x < 0.0F) {
                dx += -this.x / 30.0F;
            }

            if (this.y < 0.0F) {
                dy += -this.y / 30.0F;
            }

            if (this.x > (float)Vars.world.unitWidth()) {
                dx -= (this.x - (float)Vars.world.unitWidth()) / 30.0F;
            }

            if (this.y > (float)Vars.world.unitHeight()) {
                dy -= (this.y - (float)Vars.world.unitHeight()) / 30.0F;
            }

            this.velAddNet(dx * Time.delta, dy * Time.delta);
        }

        if (this.isGrounded()) {
            this.x = Mathf.clamp(this.x, 0.0F, (float)(Vars.world.width() * 8 - 8));
            this.y = Mathf.clamp(this.y, 0.0F, (float)(Vars.world.height() * 8 - 8));
        }

        if (this.x < -250.0F || this.y < -250.0F || this.x >= (float)(Vars.world.width() * 8) + 250.0F || this.y >= (float)(Vars.world.height() * 8) + 250.0F) {
            this.kill();
        }

        Building core = this.closestCore();
        if (core != null && this.mineTile != null && this.mineTile.drop() != null && !this.acceptsItem(this.mineTile.drop()) && this.within(core, 220.0F) && !this.offloadImmediately()) {
            int accepted = core.acceptStack(this.item(), this.stack().amount, this);
            if (accepted > 0) {
                Call.transferItemTo(this, this.item(), accepted, this.mineTile.worldx() + Mathf.range(4.0F), this.mineTile.worldy() + Mathf.range(4.0F), core);
                this.clearItem();
            }
        }

        if ((!Vars.net.client() || this.isLocal()) && !this.validMine(this.mineTile)) {
            this.mineTile = null;
            this.mineTimer = 0.0F;
        } else if (this.mining()) {
            Item item = this.mineTile.drop();
            this.mineTimer += Time.delta * this.type.mineSpeed;
            if (Mathf.chance(0.06 * (double)Time.delta)) {
                Fx.pulverizeSmall.at(this.mineTile.worldx() + Mathf.range(4.0F), this.mineTile.worldy() + Mathf.range(4.0F), 0.0F, item.color);
            }

            if (this.mineTimer >= 50.0F + (float)item.hardness * 15.0F) {
                this.mineTimer = 0.0F;
                if (Vars.state.rules.sector != null && this.team() == Vars.state.rules.defaultTeam) {
                    Vars.state.rules.sector.info.handleProduction(item, 1);
                }

                if (core != null && this.within(core, 220.0F) && core.acceptStack(item, 1, this) == 1 && this.offloadImmediately()) {
                    if (this.item() == item && !Vars.net.client()) {
                        this.addItem(item);
                    }

                    Call.transferItemTo(this, item, 1, this.mineTile.worldx() + Mathf.range(4.0F), this.mineTile.worldy() + Mathf.range(4.0F), core);
                } else if (this.acceptsItem(item)) {
                    InputHandler.transferItemToUnit(item, this.mineTile.worldx() + Mathf.range(4.0F), this.mineTile.worldy() + Mathf.range(4.0F), this);
                } else {
                    this.mineTile = null;
                    this.mineTimer = 0.0F;
                }
            }

            if (!Vars.headless) {
                Vars.control.sound.loop(this.type.mineSound, this, this.type.mineSoundVolume);
            }
        }

        if (Vars.net.client() && !this.isLocal() || this.isRemote()) {
            this.interpolate();
        }

    }

    public void clearItem() {
        this.stack.amount = 0;
    }

    public boolean validMine(Tile tile) {
        return this.validMine(tile, true);
    }

    public void heal(float amount) {
        this.health += amount;
        this.clampHealth();
        if (this.health < this.maxHealth && amount > 0.0F) {
            this.wasHealed = true;
        }

    }

    public float physicSize() {
        return this.hitSize * 0.7F;
    }

    public void apply(StatusEffect effect) {
        this.apply(effect, 1.0F);
    }

    public boolean isNull() {
        return false;
    }

    public boolean checkTarget(boolean targetAir, boolean targetGround) {
        return this.isGrounded() && targetGround || this.isFlying() && targetAir;
    }

    public void collision(Hitboxc other, float x, float y) {
    }

    public void addItem(Item item, int amount) {
        this.stack.amount = this.stack.item == item ? this.stack.amount + amount : amount;
        this.stack.item = item;
        this.stack.amount = Mathf.clamp(this.stack.amount, 0, this.itemCapacity());
    }

    public void eachGroup(Cons<Unit> cons) {
        cons.get(this);
        this.controlling().each(cons);
    }

    public boolean isImmune(StatusEffect effect) {
        return this.type.immunities.contains(effect);
    }

    public CoreBlock.CoreBuild closestCore() {
        return Vars.state.teams.closestCore(this.x, this.y, this.team);
    }

    public boolean canMine(Item item) {
        return this.type.mineTier >= item.hardness;
    }

    public boolean isAdded() {
        return this.added;
    }

    public UnitController controller() {
        return this.controller;
    }

    public boolean hasWeapons() {
        return this.type.hasWeapons();
    }

    public void aim(float x, float y) {
        Tmp.v1.set(x, y).sub(this.x, this.y);
        if (Tmp.v1.len() < this.type.aimDst) {
            Tmp.v1.setLength(this.type.aimDst);
        }

        x = Tmp.v1.x + this.x;
        y = Tmp.v1.y + this.y;

        for(WeaponMount mount : this.mounts) {
            if (mount.weapon.controllable) {
                mount.aimX = x;
                mount.aimY = y;
            }
        }

        this.aimX = x;
        this.aimY = y;
    }

    public <T extends Entityc> T self() {
        return (T)this;
    }

    public void commandNearby(FormationPattern pattern) {
        this.commandNearby(pattern, (u) -> true);
    }

    public void addBuild(BuildPlan place, boolean tail) {
        if (this.canBuild()) {
            BuildPlan replace = null;

            for(BuildPlan request : this.plans) {
                if (request.x == place.x && request.y == place.y) {
                    replace = request;
                    break;
                }
            }

            if (replace != null) {
                this.plans.remove(replace);
            }

            Tile tile = Vars.world.tile(place.x, place.y);
            if (tile != null) {
                Building var6 = tile.build;
                if (var6 instanceof ConstructBlock.ConstructBuild) {
                    ConstructBlock.ConstructBuild cons = (ConstructBlock.ConstructBuild)var6;
                    place.progress = cons.progress;
                }
            }

            if (tail) {
                this.plans.addLast(place);
            } else {
                this.plans.addFirst(place);
            }

        }
    }

    public void lookAt(Position pos) {
        this.lookAt(this.angleTo(pos));
    }

    public boolean validMine(Tile tile, boolean checkDst) {
        return tile != null && tile.block() == Blocks.air && (this.within(tile.worldx(), tile.worldy(), this.type.miningRange) || !checkDst) && tile.drop() != null && this.canMine(tile.drop());
    }

    public float speed() {
        float strafePenalty = !this.isGrounded() && this.isPlayer() ? Mathf.lerp(1.0F, this.type.strafePenalty, Angles.angleDist(this.vel().angle(), this.rotation) / 180.0F) : 1.0F;
        float boost = Mathf.lerp(1.0F, this.type.canBoost ? this.type.boostMultiplier : 1.0F, this.elevation);
        return (this.isCommanding() ? this.minFormationSpeed * 0.98F : this.type.speed) * strafePenalty * boost * this.floorSpeedMultiplier();
    }

    public boolean isValid() {
        return !this.dead && this.isAdded();
    }

    public void move(Vec2 v) {
        this.move(v.x, v.y);
    }

    public void hitbox(Rect rect) {
        rect.setCentered(this.x, this.y, this.hitSize, this.hitSize);
    }

    public int itemCapacity() {
        return this.type.itemCapacity;
    }

    public void controlWeapons(boolean rotateShoot) {
        this.controlWeapons(rotateShoot, rotateShoot);
    }

    public boolean isGrounded() {
        return this.elevation < 0.001F;
    }

    public void damagePierce(float amount, boolean withEffect) {
        float pre = this.hitTime;
        this.rawDamage(amount);
        if (!withEffect) {
            this.hitTime = pre;
        }

    }

    public void controlWeapons(boolean rotate, boolean shoot) {
        for(WeaponMount mount : this.mounts) {
            if (mount.weapon.controllable) {
                mount.rotate = rotate;
                mount.shoot = shoot;
            }
        }

        this.isRotate = rotate;
        this.isShooting = shoot;
    }

    public Floor drownFloor() {
        return this.canDrown() ? this.floorOn() : null;
    }

    public boolean mining() {
        return this.mineTile != null && !this.activelyBuilding();
    }

    public void commandNearby(FormationPattern pattern, Boolf<Unit> include) {
        Formation formation = new Formation(new Vec3(this.x, this.y, this.rotation), pattern);
        formation.slotAssignmentStrategy = new DistanceAssignmentStrategy(pattern);
        units.clear();
        Units.nearby(this.team, this.x, this.y, this.type.commandRadius, (u) -> {
            if (u.isAI() && include.get(u) && u != this && u.type.flying == this.type.flying && u.hitSize <= this.hitSize * 1.1F) {
                units.add(u);
            }

        });
        if (!units.isEmpty()) {
            units.sort(Structs.comps(Structs.comparingFloat((u) -> -u.hitSize), Structs.comparingFloat((u) -> u.dst2(this))));
            units.truncate(this.type.commandLimit);
            this.command(formation, units);
        }
    }

    public boolean hasItem() {
        return this.stack.amount > 0;
    }

    public boolean canPass(int tileX, int tileY) {
        EntityCollisions.SolidPred s = this.solidity();
        return s == null || !s.solid(tileX, tileY);
    }

    public float floorSpeedMultiplier() {
        Floor on = !this.isFlying() && !this.hovering ? this.floorOn() : Blocks.air.asFloor();
        return on.speedMultiplier * this.speedMultiplier;
    }

    public void clearCommand() {
        for(Unit unit : this.controlling) {
            if (unit.controller().isBeingControlled(this)) {
                unit.controller(unit.type.createController());
            }
        }

        this.controlling.clear();
        this.formation = null;
    }

    public boolean canPassOn() {
        return this.canPass(this.tileX(), this.tileY());
    }

    public void trns(Position pos) {
        this.trns(pos.getX(), pos.getY());
    }

    public void impulseNet(Vec2 v) {
        this.impulse(v.x, v.y);
        if (this.isRemote()) {
            float mass = this.mass();
            this.move(v.x / mass, v.y / mass);
        }

    }

    public void drawPlanTop(BuildPlan request, float alpha) {
        if (!request.breaking) {
            Draw.reset();
            Draw.mixcol(Color.white, 0.24F + Mathf.absin(Time.globalTime, 6.0F, 0.28F));
            Draw.alpha(alpha);
            request.block.drawRequestConfigTop(request, this.plans);
        }

    }

    public void resetController() {
        this.controller(this.type.createController());
    }

    public BuildPlan buildPlan() {
        return this.plans.size == 0 ? null : (BuildPlan)this.plans.first();
    }

    public void unloaded() {
    }

    public float ammof() {
        return this.ammo / (float)this.type.ammoCapacity;
    }

    public void writeSync(Writes write) {
        write.f(this.ammo);
        TypeIO.writeController(write, this.controller);
        write.f(this.elevation);
        write.d(this.flag);
        write.f(this.health);
        write.bool(this.isShooting);
        TypeIO.writeString(write, this.lastText);
        TypeIO.writeTile(write, this.mineTile);
        TypeIO.writeMounts(write, this.mounts);
        write.i(this.plans.size);

        for(int INDEX = 0; INDEX < this.plans.size; ++INDEX) {
            TypeIO.writeRequest(write, (BuildPlan)this.plans.get(INDEX));
        }

        write.f(this.rotation);
        write.f(this.shield);
        write.i(this.souls);
        write.bool(this.spawnedByCore);
        TypeIO.writeItems(write, this.stack);
        write.i(this.statuses.size);

        for(int INDEX = 0; INDEX < this.statuses.size; ++INDEX) {
            TypeIO.writeStatus(write, (StatusEntry)this.statuses.get(INDEX));
        }

        TypeIO.writeTeam(write, this.team);
        write.f(this.textFadeTime);
        write.s(this.type.id);
        write.bool(this.updateBuilding);
        TypeIO.writeVec2(write, this.vel);
        write.f(this.x);
        write.f(this.y);
    }

    public boolean moving() {
        return !this.vel.isZero(0.01F);
    }

    public void impulse(Vec2 v) {
        this.impulse(v.x, v.y);
    }

    public void setType(UnitType type) {
        if (!this.spawnedByCore && type instanceof UnityUnitType) {
            UnityUnitType def = (UnityUnitType)type;
            this.maxSouls = def.maxSouls;
        } else {
            this.maxSouls = 0;
            this.souls = 0;
        }

        this.type = type;
        this.maxHealth = type.health;
        this.drag = type.drag;
        this.armor = type.armor;
        this.hitSize = type.hitSize;
        this.hovering = type.hovering;
        if (this.controller == null) {
            this.controller(type.createController());
        }

        if (this.mounts().length != type.weapons.size) {
            this.setupWeapons(type);
        }

        if (this.abilities.size != type.abilities.size) {
            this.abilities = type.abilities.map(Ability::copy);
        }

    }

    public void lookAt(float x, float y) {
        this.lookAt(this.angleTo(x, y));
    }

    public void updateDrowning() {
        Floor floor = this.drownFloor();
        if (floor != null && floor.isLiquid && floor.drownTime > 0.0F) {
            this.lastDrownFloor = floor;
            this.drownTime += Time.delta / floor.drownTime / this.type.drownTimeMultiplier;
            if (Mathf.chanceDelta((double)0.05F)) {
                floor.drownUpdateEffect.at(this.x, this.y, this.hitSize, floor.mapColor);
            }

            if (this.drownTime >= 0.999F && !Vars.net.client()) {
                this.kill();
                Events.fire(new EventType.UnitDrownEvent(this));
            }
        } else {
            this.drownTime -= Time.delta / 50.0F;
        }

        this.drownTime = Mathf.clamp(this.drownTime);
    }

    public void updateLastPosition() {
        this.deltaX = this.x - this.lastX;
        this.deltaY = this.y - this.lastY;
        this.lastX = this.x;
        this.lastY = this.y;
    }

    public void damageContinuous(float amount) {
        this.damage(amount * Time.delta, this.hitTime <= -1.0F);
    }

    public void afterSync() {
        this.setType(this.type);
        this.controller.unit(this);
    }

    public void removeBuild(int x, int y, boolean breaking) {
        int idx = this.plans.indexOf((req) -> req.breaking == breaking && req.x == x && req.y == y);
        if (idx != -1) {
            this.plans.removeIndex(idx);
        }

    }

    public Player getPlayer() {
        return this.isPlayer() ? (Player)this.controller : null;
    }

    public void display(Table table) {
        this.type.display(this, table);
    }

    public int maxSouls() {
        return this.maxSouls;
    }

    public boolean offloadImmediately() {
        return this.isPlayer();
    }

    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public CoreBlock.CoreBuild core() {
        return this.team.core();
    }

    public int souls() {
        return this.souls;
    }

    public boolean hasEffect(StatusEffect effect) {
        return this.applied.get(effect.id);
    }

    public int tileY() {
        return World.toTile(this.y);
    }

    public void damagePierce(float amount) {
        this.damagePierce(amount, true);
    }

    public void aimLook(float x, float y) {
        this.aim(x, y);
        this.lookAt(x, y);
    }

    public void unapply(StatusEffect effect) {
        this.statuses.remove((e) -> {
            if (e.effect == effect) {
                Pools.free(e);
                return true;
            } else {
                return false;
            }
        });
    }

    public void heal() {
        this.dead = false;
        this.health = this.maxHealth;
    }

    public void readSync(Reads read) {
        if (this.lastUpdated != 0L) {
            this.updateSpacing = Time.timeSinceMillis(this.lastUpdated);
        }

        this.lastUpdated = Time.millis();
        boolean islocal = this.isLocal();
        this.ammo = read.f();
        this.controller = TypeIO.readController(read, this.controller);
        if (!islocal) {
            this.elevation = read.f();
        } else {
            read.f();
        }

        this.flag = read.d();
        this.health = read.f();
        this.isShooting = read.bool();
        this.lastText = TypeIO.readString(read);
        if (!islocal) {
            this.mineTile = TypeIO.readTile(read);
        } else {
            TypeIO.readTile(read);
        }

        if (!islocal) {
            this.mounts = TypeIO.readMounts(read, this.mounts);
        } else {
            TypeIO.readMounts(read);
        }

        if (!islocal) {
            int plans_LENGTH = read.i();
            this.plans.clear();

            for(int INDEX = 0; INDEX < plans_LENGTH; ++INDEX) {
                BuildPlan plans_ITEM = TypeIO.readRequest(read);
                if (plans_ITEM != null) {
                    this.plans.add(plans_ITEM);
                }
            }
        } else {
            int _LENGTH = read.i();

            for(int INDEX = 0; INDEX < _LENGTH; ++INDEX) {
                TypeIO.readRequest(read);
            }
        }

        if (!islocal) {
            this.rotation_LAST_ = this.rotation;
            this.rotation_TARGET_ = read.f();
        } else {
            read.f();
            this.rotation_LAST_ = this.rotation;
            this.rotation_TARGET_ = this.rotation;
        }

        this.shield = read.f();
        this.souls = read.i();
        this.spawnedByCore = read.bool();
        this.stack = TypeIO.readItems(read, this.stack);
        int statuses_LENGTH = read.i();
        this.statuses.clear();

        for(int INDEX = 0; INDEX < statuses_LENGTH; ++INDEX) {
            StatusEntry statuses_ITEM = TypeIO.readStatus(read);
            if (statuses_ITEM != null) {
                this.statuses.add(statuses_ITEM);
            }
        }

        this.team = TypeIO.readTeam(read);
        this.textFadeTime_LAST_ = this.textFadeTime;
        this.textFadeTime_TARGET_ = read.f();
        this.type = (UnitType)Vars.content.getByID(ContentType.unit, read.s());
        if (!islocal) {
            this.updateBuilding = read.bool();
        } else {
            read.bool();
        }

        if (!islocal) {
            this.vel = TypeIO.readVec2(read, this.vel);
        } else {
            TypeIO.readVec2(read);
        }

        if (!islocal) {
            this.x_LAST_ = this.x;
            this.x_TARGET_ = read.f();
        } else {
            read.f();
            this.x_LAST_ = this.x;
            this.x_TARGET_ = this.x;
        }

        if (!islocal) {
            this.y_LAST_ = this.y;
            this.y_TARGET_ = read.f();
        } else {
            read.f();
            this.y_LAST_ = this.y;
            this.y_TARGET_ = this.y;
        }

        this.afterSync();
    }

    public int tileX() {
        return World.toTile(this.x);
    }

    public void controller(UnitController next) {
        this.clearCommand();
        this.controller = next;
        if (this.controller.unit() != this) {
            this.controller.unit(this);
        }

    }

    public Tile tileOn() {
        return Vars.world.tileWorld(this.x, this.y);
    }

    public void damageContinuousPierce(float amount) {
        this.damagePierce(amount * Time.delta, this.hitTime <= -11.0F);
    }

    public void drawPlan(BuildPlan request, float alpha) {
        request.animScale = 1.0F;
        if (request.breaking) {
            Vars.control.input.drawBreaking(request);
        } else {
            request.block.drawPlan(request, Vars.control.input.allRequests(), Build.validPlace(request.block, this.team, request.x, request.y, request.rotation) || Vars.control.input.requestMatches(request), alpha);
        }

    }

    public boolean isPlayer() {
        return this.controller instanceof Player;
    }

    public void aim(Position pos) {
        this.aim(pos.getX(), pos.getY());
    }

    public boolean isBoss() {
        return this.hasEffect(StatusEffects.boss);
    }

    public void kill() {
        if (!this.dead && !Vars.net.client()) {
            Call.unitDeath(this.id);
        }
    }

    public float range() {
        return this.type.maxRange;
    }

    public int pathType() {
        return 0;
    }

    public boolean canBuild() {
        return this.type.buildSpeed > 0.0F && this.buildSpeedMultiplier > 0.0F;
    }

    public boolean serialize() {
        return true;
    }

    public static MonolithAssistantUnit create() {
        return new MonolithAssistantUnit();
    }

    public int classId() {
        return UnityEntityMapping.classId(MonolithAssistantUnit.class);
    }

    public String lastText() {
        return this.lastText;
    }

    public float textFadeTime() {
        return this.textFadeTime;
    }

    public boolean isRotate() {
        return this.isRotate;
    }
}
