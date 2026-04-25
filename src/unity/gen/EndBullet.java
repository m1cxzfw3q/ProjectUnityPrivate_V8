package unity.gen;

import arc.func.Cons;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.math.geom.Position;
import arc.math.geom.QuadTree;
import arc.math.geom.Rect;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import arc.util.pooling.Pool;
import arc.util.pooling.Pools;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.core.World;
import mindustry.entities.EntityCollisions;
import mindustry.entities.EntityGroup;
import mindustry.entities.bullet.BulletType;
import mindustry.game.Teams;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.gen.Bulletc;
import mindustry.gen.Damagec;
import mindustry.gen.Drawc;
import mindustry.gen.Entityc;
import mindustry.gen.Flyingc;
import mindustry.gen.Groups;
import mindustry.gen.Healthc;
import mindustry.gen.Hitboxc;
import mindustry.gen.Ownerc;
import mindustry.gen.Posc;
import mindustry.gen.Shielderc;
import mindustry.gen.Teamc;
import mindustry.gen.Timedc;
import mindustry.gen.Timerc;
import mindustry.gen.Unit;
import mindustry.gen.Unitc;
import mindustry.gen.Velc;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.blocks.environment.StaticWall;
import mindustry.world.blocks.storage.CoreBlock;

public class EndBullet extends Bullet implements Pool.Poolable, EndBulletc, Drawc, Timedc, Ownerc, Posc, Hitboxc, Damagec, Bulletc, Teamc, Entityc, Timerc, Shielderc, Velc {
    private Teamc trueOwner;
    private float rotation;
    private transient boolean added;

    protected EndBullet() {
    }

    public String toString() {
        return "EndBullet#" + this.id;
    }

    public void velAddNet(float vx, float vy) {
        this.vel.add(vx, vy);
        if (this.isRemote()) {
            this.x += vx;
            this.y += vy;
        }

    }

    public float getY() {
        return this.y;
    }

    public void remove() {
        if (this.added) {
            Groups.all.remove(this);
            Groups.draw.remove(this);
            Groups.bullet.remove(this);
            if (!this.hit) {
                this.type.despawned(this);
            }

            this.type.removed(this);
            this.collided.clear();
            this.added = false;
            Groups.queueFree(this);
        }
    }

    public <T extends Entityc> T self() {
        return (T)this;
    }

    public void updateLastPosition() {
        this.deltaX = this.x - this.lastX;
        this.deltaY = this.y - this.lastY;
        this.lastX = this.x;
        this.lastY = this.y;
    }

    public boolean hasCollided(int id) {
        return this.collided.size != 0 && this.collided.contains(id);
    }

    public boolean onSolid() {
        Tile tile = this.tileOn();
        return tile == null || tile.solid();
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

    public boolean isNull() {
        return false;
    }

    public void afterRead() {
        this.updateLastPosition();
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

    public void collision(Hitboxc other, float x, float y) {
        this.type.hit(this, x, y);
        if (!this.type.pierce) {
            this.hit = true;
            this.remove();
        } else {
            this.collided.add(other.id());
        }

        BulletType var10000 = this.type;
        float var10003;
        if (other instanceof Healthc) {
            Healthc h = (Healthc)other;
            var10003 = h.health();
        } else {
            var10003 = 0.0F;
        }

        var10000.hitEntity(this, other, var10003);
    }

    public void read(Reads read) {
        this.afterRead();
    }

    public void set(Position pos) {
        this.set(pos.getX(), pos.getY());
    }

    public CoreBlock.CoreBuild closestCore() {
        return Vars.state.teams.closestCore(this.x, this.y, this.team);
    }

    public Floor floorOn() {
        Tile tile = this.tileOn();
        return tile != null && tile.block() == Blocks.air ? tile.floor() : (Floor)Blocks.air;
    }

    public void tileRaycast(int x0f, int y0f, int x1, int y1) {
        int x = x0f;
        int dx = Math.abs(x1 - x0f);
        int sx = x0f < x1 ? 1 : -1;
        int y = y0f;
        int dy = Math.abs(y1 - y0f);
        int sy = y0f < y1 ? 1 : -1;
        int err = dx - dy;
        int ww = Vars.world.width();
        int wh = Vars.world.height();

        while(x >= 0 && y >= 0 && x < ww && y < wh) {
            Building build = Vars.world.build(x, y);
            if (this.type.collideFloor || this.type.collideTerrain) {
                Tile tile = Vars.world.tile(x, y);
                if (this.type.collideFloor && (tile == null || tile.floor().hasSurface() || tile.block() != Blocks.air) || this.type.collideTerrain && tile != null && tile.block() instanceof StaticWall) {
                    this.type.despawned(this);
                    this.remove();
                    this.hit = true;
                    return;
                }
            }

            if (build != null && this.isAdded() && build.collide(this) && this.type.testCollision(this, build) && !build.dead() && (this.type.collidesTeam || build.team != this.team) && (!this.type.pierceBuilding || !this.hasCollided(build.id))) {
                boolean remove = false;
                float health = build.health;
                if (build.team != this.team) {
                    remove = build.collision(this);
                }

                if (remove || this.type.collidesTeam) {
                    if (!this.type.pierceBuilding) {
                        this.hit = true;
                        this.remove();
                    } else {
                        this.collided.add(build.id);
                    }
                }

                this.type.hitTile(this, build, health, true);
                if (this.type.pierceBuilding) {
                    return;
                }
            }

            if (x == x1 && y == y1) {
                break;
            }

            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x += sx;
            }

            if (e2 < dx) {
                err += dx;
                y += sy;
            }
        }

    }

    public boolean isLocal() {
        return true;
    }

    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void move(Vec2 v) {
        this.move(v.x, v.y);
    }

    public int tileY() {
        return World.toTile(this.y);
    }

    public void hitbox(Rect rect) {
        rect.setCentered(this.x, this.y, this.hitSize, this.hitSize);
    }

    public float deltaAngle() {
        return Mathf.angle(this.deltaX, this.deltaY);
    }

    public float clipSize() {
        return this.type.drawSize;
    }

    public float hitSize() {
        return this.hitSize;
    }

    public void rotation(float angle) {
        this.vel.setAngle(this.rotation = angle);
    }

    public void add() {
        if (!this.added) {
            Groups.all.add(this);
            Groups.draw.add(this);
            Groups.bullet.add(this);
            this.updateLastPosition();
            this.type.init(this);
            this.added = true;
        }
    }

    public boolean canPass(int tileX, int tileY) {
        EntityCollisions.SolidPred s = this.solidity();
        return s == null || !s.solid(tileX, tileY);
    }

    public boolean canPassOn() {
        return this.canPass(this.tileX(), this.tileY());
    }

    public void velAddNet(Vec2 v) {
        this.vel.add(v);
        if (this.isRemote()) {
            this.x += v.x;
            this.y += v.y;
        }

    }

    public float getX() {
        return this.x;
    }

    public Tile tileOn() {
        return Vars.world.tileWorld(this.x, this.y);
    }

    public void draw() {
        Draw.z(this.type.layer);
        this.type.draw(this);
        this.type.drawLight(this);
        Draw.reset();
    }

    public void setTrueOwner(Teamc owner) {
        this.trueOwner = owner;
    }

    public float fin() {
        return this.time / this.lifetime;
    }

    public Block blockOn() {
        Tile tile = this.tileOn();
        return tile == null ? Blocks.air : tile.block();
    }

    public boolean moving() {
        return !this.vel.isZero(0.01F);
    }

    public boolean collides(Hitboxc other) {
        boolean var10000;
        if (this.type.collides && other instanceof Teamc) {
            Teamc t = (Teamc)other;
            if (t.team() != this.team) {
                label35: {
                    if (other instanceof Flyingc) {
                        Flyingc f = (Flyingc)other;
                        if (!f.checkTarget(this.type.collidesAir, this.type.collidesGround)) {
                            break label35;
                        }
                    }

                    if (!this.type.pierce || !this.hasCollided(other.id())) {
                        var10000 = true;
                        return var10000;
                    }
                }
            }
        }

        var10000 = false;
        return var10000;
    }

    public void write(Writes write) {
    }

    public <T> T as() {
        return (T)this;
    }

    public float rotation() {
        return this.vel.isZero(0.001F) ? this.rotation : this.vel.angle();
    }

    public CoreBlock.CoreBuild core() {
        return this.team.core();
    }

    public void trns(float x, float y) {
        this.set(this.x + x, this.y + y);
    }

    public float damageMultiplier() {
        Entityc var2 = this.owner;
        if (var2 instanceof Unit) {
            Unit u = (Unit)var2;
            return u.damageMultiplier() * Vars.state.rules.unitDamage(this.team);
        } else {
            return this.owner instanceof Building ? Vars.state.rules.blockDamage(this.team) : 1.0F;
        }
    }

    public EntityCollisions.SolidPred solidity() {
        return null;
    }

    public void update() {
        if (this.trueOwner != null && (this.owner != this.trueOwner || this.team != this.trueOwner.team())) {
            this.team = this.trueOwner.team();
            this.owner = this.trueOwner;
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

        this.type.update(this);
        if (this.type.collidesTiles && this.type.collides && this.type.collidesGround) {
            this.tileRaycast(World.toTile(this.lastX()), World.toTile(this.lastY()), this.tileX(), this.tileY());
        }

        if (this.type.pierceCap != -1 && this.collided.size >= this.type.pierceCap) {
            this.hit = true;
            this.remove();
        }

        this.time = Math.min(this.time + Time.delta, this.lifetime);
        if (this.time >= this.lifetime) {
            this.remove();
        }

    }

    public int tileX() {
        return World.toTile(this.x);
    }

    public boolean timer(int index, float time) {
        return Float.isInfinite(time) ? false : this.timer.get(index, time);
    }

    public void absorb() {
        this.absorbed = true;
        this.remove();
    }

    public CoreBlock.CoreBuild closestEnemyCore() {
        return Vars.state.teams.closestEnemyCore(this.x, this.y, this.team);
    }

    public boolean cheating() {
        return this.team.rules().cheat;
    }

    public void trns(Position pos) {
        this.trns(pos.getX(), pos.getY());
    }

    public boolean isAdded() {
        return this.added;
    }

    public void initVel(float angle, float amount) {
        this.vel.trns(angle, amount);
        this.rotation = angle;
    }

    public void hitboxTile(Rect rect) {
        float size = Math.min(this.hitSize * 0.66F, 7.9F);
        rect.setCentered(this.x, this.y, size, size);
    }

    public float deltaLen() {
        return Mathf.len(this.deltaX, this.deltaY);
    }

    public void getCollisions(Cons<QuadTree> consumer) {
        Seq<Teams.TeamData> data = Vars.state.teams.present;

        for(int i = 0; i < data.size; ++i) {
            if (((Teams.TeamData[])data.items)[i].team != this.team) {
                consumer.get(((Teams.TeamData[])data.items)[i].tree());
            }
        }

    }

    public boolean serialize() {
        return false;
    }

    public void reset() {
        this.trueOwner = null;
        this.time = 0.0F;
        this.lifetime = 0.0F;
        this.owner = null;
        this.x = 0.0F;
        this.y = 0.0F;
        this.lastX = 0.0F;
        this.lastY = 0.0F;
        this.deltaX = 0.0F;
        this.deltaY = 0.0F;
        this.hitSize = 0.0F;
        this.damage = 0.0F;
        this.data = null;
        this.type = null;
        this.fdata = 0.0F;
        this.rotation = 0.0F;
        this.absorbed = false;
        this.hit = false;
        this.trail = null;
        this.added = false;
        this.id = EntityGroup.nextId();
        this.drag = 0.0F;
    }

    public static EndBullet create() {
        return (EndBullet)Pools.obtain(EndBullet.class, EndBullet::new);
    }

    public int classId() {
        return UnityEntityMapping.classId(EndBullet.class);
    }
}
