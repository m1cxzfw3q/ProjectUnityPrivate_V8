package unity.gen;

import arc.func.Cons;
import arc.math.Mathf;
import arc.math.geom.Position;
import arc.math.geom.QuadTree;
import arc.math.geom.Rect;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.core.World;
import mindustry.entities.EntityGroup;
import mindustry.gen.Drawc;
import mindustry.gen.Entityc;
import mindustry.gen.Groups;
import mindustry.gen.Hitboxc;
import mindustry.gen.Posc;
import mindustry.gen.Rotc;
import mindustry.gen.Timedc;
import mindustry.gen.Unit;
import mindustry.gen.Unitc;
import mindustry.type.UnitType;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;
import unity.entities.effects.CutEffects;

public class CutEffect implements Rotc, Drawc, Hitboxc, Posc, Timedc, Entityc, CutEffectc {
    public float rotation;
    public transient float lastX;
    public transient float lastY;
    public transient float deltaX;
    public transient float deltaY;
    public transient float hitSize;
    public float x;
    public float y;
    public float time;
    public float lifetime;
    private transient boolean added;
    public transient int id = EntityGroup.nextId();
    public Drawc other;
    public Seq<CutEffects> stencils;
    public Vec2 velocity = new Vec2();
    public float originX;
    public float originY;
    public float angularVel;
    public float drag = 0.01F;
    public boolean removed = false;

    protected CutEffect() {
    }

    public String toString() {
        return "CutEffect#" + this.id;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public void draw() {
        CutEffects.draw(this);
    }

    public void getCollisions(Cons<QuadTree> consumer) {
    }

    public boolean isLocal() {
        boolean var10000;
        if (this != Vars.player) {
            label26: {
                if (this instanceof Unitc) {
                    Unitc u = (Unitc)this;
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

    public void updateLastPosition() {
        this.deltaX = this.x - this.lastX;
        this.deltaY = this.y - this.lastY;
        this.lastX = this.x;
        this.lastY = this.y;
    }

    public void update() {
        this.x += this.velocity.x * Time.delta;
        this.y += this.velocity.y * Time.delta;
        this.rotation += this.angularVel;
        this.velocity.scl(1.0F - this.drag * Time.delta);
        this.angularVel *= 1.0F - this.drag * Time.delta;
        this.time = Math.min(this.time + Time.delta, this.lifetime);
        if (this.time >= this.lifetime) {
            this.remove();
        }

        this.despawn();
    }

    public boolean isNull() {
        return false;
    }

    public Floor floorOn() {
        Tile tile = this.tileOn();
        return tile != null && tile.block() == Blocks.air ? tile.floor() : (Floor)Blocks.air;
    }

    public Block blockOn() {
        Tile tile = this.tileOn();
        return tile == null ? Blocks.air : tile.block();
    }

    public void set(Position pos) {
        this.set(pos.getX(), pos.getY());
    }

    public boolean isRemote() {
        boolean var10000;
        if (this instanceof Unitc) {
            Unitc u = (Unitc)this;
            if (u.isPlayer() && !this.isLocal()) {
                var10000 = true;
                return var10000;
            }
        }

        var10000 = false;
        return var10000;
    }

    public boolean collides(Hitboxc other) {
        return true;
    }

    public void afterRead() {
        this.updateLastPosition();
    }

    public void write(Writes write) {
    }

    public <T extends Entityc> T self() {
        return (T)this;
    }

    public void read(Reads read) {
        this.afterRead();
    }

    public void trns(float x, float y) {
        this.set(this.x + x, this.y + y);
    }

    public void despawn() {
        Drawc var2 = this.other;
        if (var2 instanceof Unit) {
            Unit unit = (Unit)var2;
            unit.type.deathExplosionEffect.at(this.x, this.y, this.hitSize() / 2.0F);
        } else {
            Fx.dynamicExplosion.at(this.x, this.y, this.hitSize() / 2.0F);
        }

    }

    public <T> T as() {
        return (T)this;
    }

    public void add() {
        if (!this.added) {
            Groups.all.add(this);
            Groups.draw.add(this);
            this.updateLastPosition();
            this.added = true;
            CutEffects.group.add(this);
            this.originX = this.x;
            this.originY = this.y;
        }
    }

    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float z() {
        Drawc var2 = this.other;
        if (var2 instanceof Unit) {
            Unit u = (Unit)var2;
            UnitType type = u.type;
            return u.elevation > 0.5F ? (type.lowAltitude ? 90.0F : 115.0F) : type.groundLayer + Mathf.clamp(u.hitSize / 4000.0F, 0.0F, 0.01F);
        } else {
            return 40.0F;
        }
    }

    public int tileX() {
        return World.toTile(this.x);
    }

    public int tileY() {
        return World.toTile(this.y);
    }

    public void hitbox(Rect rect) {
        rect.setCentered(this.x, this.y, this.hitSize, this.hitSize);
    }

    public Tile tileOn() {
        return Vars.world.tileWorld(this.x, this.y);
    }

    public void remove() {
        if (this.added) {
            Groups.all.remove(this);
            Groups.draw.remove(this);
            this.added = false;
            CutEffects.group.remove(this);
        }
    }

    public float clipSize() {
        return this.other.clipSize() * 1.5F;
    }

    public void trns(Position pos) {
        this.trns(pos.getX(), pos.getY());
    }

    public float hitSize() {
        return this.hitSize;
    }

    public boolean isAdded() {
        return this.added;
    }

    public boolean onSolid() {
        Tile tile = this.tileOn();
        return tile == null || tile.solid();
    }

    public void hitboxTile(Rect rect) {
        float size = Math.min(this.hitSize * 0.66F, 7.9F);
        rect.setCentered(this.x, this.y, size, size);
    }

    public float deltaAngle() {
        return Mathf.angle(this.deltaX, this.deltaY);
    }

    public float fin() {
        return this.time / this.lifetime;
    }

    public float deltaLen() {
        return Mathf.len(this.deltaX, this.deltaY);
    }

    public void collision(Hitboxc other, float x, float y) {
    }

    public boolean serialize() {
        return false;
    }

    public static CutEffect create() {
        return new CutEffect();
    }

    public int classId() {
        return UnityEntityMapping.classId(CutEffect.class);
    }

    public float rotation() {
        return this.rotation;
    }

    public void rotation(float arg0) {
        this.rotation = this.rotation;
    }

    public float lastX() {
        return this.lastX;
    }

    public void lastX(float arg0) {
        this.lastX = this.lastX;
    }

    public float lastY() {
        return this.lastY;
    }

    public void lastY(float arg0) {
        this.lastY = this.lastY;
    }

    public float deltaX() {
        return this.deltaX;
    }

    public void deltaX(float arg0) {
        this.deltaX = this.deltaX;
    }

    public float deltaY() {
        return this.deltaY;
    }

    public void deltaY(float arg0) {
        this.deltaY = this.deltaY;
    }

    public void hitSize(float arg0) {
        this.hitSize = this.hitSize;
    }

    public float x() {
        return this.x;
    }

    public void x(float arg0) {
        this.x = this.x;
    }

    public float y() {
        return this.y;
    }

    public void y(float arg0) {
        this.y = this.y;
    }

    public float time() {
        return this.time;
    }

    public void time(float arg0) {
        this.time = this.time;
    }

    public float lifetime() {
        return this.lifetime;
    }

    public void lifetime(float arg0) {
        this.lifetime = this.lifetime;
    }

    public int id() {
        return this.id;
    }

    public void id(int arg0) {
        this.id = this.id;
    }

    public Drawc other() {
        return this.other;
    }

    public void other(Drawc other) {
        this.other = other;
    }

    public Seq<CutEffects> stencils() {
        return this.stencils;
    }

    public void stencils(Seq<CutEffects> stencils) {
        this.stencils = stencils;
    }

    public Vec2 velocity() {
        return this.velocity;
    }

    public void velocity(Vec2 velocity) {
        this.velocity = velocity;
    }

    public float originX() {
        return this.originX;
    }

    public void originX(float originX) {
        this.originX = originX;
    }

    public float originY() {
        return this.originY;
    }

    public void originY(float originY) {
        this.originY = originY;
    }

    public float angularVel() {
        return this.angularVel;
    }

    public void angularVel(float angularVel) {
        this.angularVel = angularVel;
    }

    public float drag() {
        return this.drag;
    }

    public void drag(float drag) {
        this.drag = drag;
    }

    public boolean removed() {
        return this.removed;
    }

    public void removed(boolean removed) {
        this.removed = removed;
    }
}
