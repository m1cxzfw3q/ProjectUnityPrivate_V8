package unity.gen;

import arc.func.Cons;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.math.geom.Ellipse;
import arc.math.geom.Position;
import arc.math.geom.QuadTree;
import arc.math.geom.Rect;
import arc.math.geom.Vec2;
import arc.struct.FloatSeq;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
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
import unity.ai.kami.KamiBulletDatas;

public class KamiBullet extends Bullet implements Pool.Poolable, Entityc, Drawc, Timedc, Ownerc, Posc, Hitboxc, Damagec, Bulletc, KamiBulletc, Teamc, Timerc, Shielderc, Velc {
    private static final Ellipse e = new Ellipse();
    private static final Vec2 vec = new Vec2();
    private static float lastDelta;
    private transient boolean added;
    private float rotation;
    public float turn;
    public float width;
    public float length;
    public float resetTime;
    public float lastTime;
    public float fdata2;
    public int telegraph;
    public FloatSeq lastPositions;
    public KamiBulletDatas.KamiBulletData bdata;

    protected KamiBullet() {
    }

    public String toString() {
        return "KamiBullet#" + this.id;
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
            this.added = false;
            if (!this.hit) {
                this.type.despawned(this);
            }

            this.type.removed(this);
            this.collided.clear();
            if (this.bdata != null) {
                this.bdata.removed(this);
            }

            Groups.queueFree(this);
        }
    }

    public void getCollisions(Cons<QuadTree> consumer) {
        Seq<Teams.TeamData> data = Vars.state.teams.present;

        for(int i = 0; i < data.size; ++i) {
            if (((Teams.TeamData[])data.items)[i].team != this.team) {
                consumer.get(((Teams.TeamData[])data.items)[i].tree());
            }
        }

    }

    public void trns(Position pos) {
        this.trns(pos.getX(), pos.getY());
    }

    public void update() {
        this.updatePre();
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

        if (this.lastPositions != null) {
            this.lastPositions.add(this.x, this.y);
            if (this.lastPositions.size > this.telegraph * 4) {
                this.lastPositions.removeRange(0, 1);
            }
        }

        if (this.telegraph > 0 && this.lastPositions == null) {
            KamiBullet b = create();
            b.x = this.x;
            b.y = this.y;
            b.type = this.type;
            b.team = this.team;
            b.owner = this.owner;
            b.vel.set(this.vel());
            b.lifetime = this.lifetime();
            b.time = this.time();
            b.drag = this.drag();
            b.fdata = this.fdata();
            b.fdata2 = this.fdata2;
            b.bdata = this.bdata;
            b.width = this.width;
            b.length = this.length;
            b.hitSize = this.hitSize();
            b.lastPositions = new FloatSeq();
            b.telegraph = this.telegraph;
            b.add();
            this.telegraph = -1;
        }

        if (this.resetTime >= 60.0F) {
            this.collided.clear();
            this.resetTime = 0.0F;
        }

        this.resetTime += Time.delta;
        if (this.bdata != null) {
            this.bdata.update(this);
        }

        if (this.turn != 0.0F) {
            this.rotation(this.rotation() + this.turn * Time.delta);
        }

        this.updateLastTime();
        this.time = Math.min(this.time + Time.delta, this.lifetime);
        if (this.time >= this.lifetime) {
            this.remove();
        }

        this.updatePost();
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

    public CoreBlock.CoreBuild closestCore() {
        return Vars.state.teams.closestCore(this.x, this.y, this.team);
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

    public boolean timer(int index, float time) {
        return Float.isInfinite(time) ? false : this.timer.get(index, time);
    }

    private void updatePre() {
        if (this.isTelegraph()) {
            lastDelta = Time.delta;
            Time.delta = 3.0F * lastDelta;
        }

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

    public boolean hasCollided(int id) {
        return this.collided.size != 0 && this.collided.contains(id);
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
        if (this.width == this.length) {
            float size = this.width * 2.0F;
            rect.setCentered(this.x, this.y, size, size);
        } else {
            Vec2 v = Tmp.v1.trns(this.rotation(), this.length * 2.0F - this.width * 2.0F);
            rect.setCentered(v.x + this.x, v.y + this.y, this.width * 2.0F);
            rect.merge(Tmp.r1.setCentered(-v.x + this.x, -v.y + this.y, this.width * 2.0F));
        }

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
            this.added = true;
            this.updateLastPosition();
            this.type.init(this);
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

    public void updateLastPosition() {
        this.deltaX = this.x - this.lastX;
        this.deltaY = this.y - this.lastY;
        this.lastX = this.x;
        this.lastY = this.y;
    }

    private void updatePost() {
        if (this.isTelegraph()) {
            Time.delta = lastDelta;
        }

    }

    public boolean isLocal() {
        return true;
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
        boolean base = this.lastPositions == null && this.type.collides && other instanceof Teamc && ((Teamc)other).team() != this.team && (!(other instanceof Flyingc) || ((Flyingc)other).checkTarget(this.type.collidesAir, this.type.collidesGround)) && (!this.type.pierce || !this.hasCollided(other.id()));
        boolean result;
        if (this.width == this.length) {
            result = this.within(other, other.hitSize() / 2.0F + this.width);
        } else {
            float h = other.hitSize() / 2.0F;
            vec.set(other).sub(this.x, this.y).rotate(-this.rotation());
            e.set(0.0F, 0.0F, h + this.length * 2.0F, h + this.width * 2.0F);
            result = e.contains(vec);
        }

        return base && result;
    }

    public void write(Writes write) {
    }

    public <T> T as() {
        return (T)this;
    }

    public boolean isTelegraph() {
        return this.lastPositions != null;
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

    public int tileX() {
        return World.toTile(this.x);
    }

    private void updateLastTime() {
        this.lastTime = this.time();
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

    public <T extends Entityc> T self() {
        return (T)this;
    }

    public boolean isAdded() {
        return this.added;
    }

    public void initVel(float angle, float amount) {
        this.vel.trns(angle, amount);
        this.rotation = angle;
    }

    public void afterRead() {
        this.updateLastPosition();
    }

    public void hitboxTile(Rect rect) {
        float size = Math.min(this.hitSize * 0.66F, 7.9F);
        rect.setCentered(this.x, this.y, size, size);
    }

    public float deltaLen() {
        return Mathf.len(this.deltaX, this.deltaY);
    }

    public float rotation() {
        return this.vel.isZero(0.001F) ? this.rotation : this.vel.angle();
    }

    public boolean serialize() {
        return false;
    }

    public void reset() {
        this.added = false;
        this.id = EntityGroup.nextId();
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
        this.turn = 0.0F;
        this.width = 0.0F;
        this.length = 0.0F;
        this.resetTime = 0.0F;
        this.lastTime = 0.0F;
        this.fdata2 = 0.0F;
        this.telegraph = 0;
        this.lastPositions = null;
        this.bdata = null;
        this.drag = 0.0F;
    }

    public static KamiBullet create() {
        return (KamiBullet)Pools.obtain(KamiBullet.class, KamiBullet::new);
    }

    public int classId() {
        return UnityEntityMapping.classId(KamiBullet.class);
    }

    public float turn() {
        return this.turn;
    }

    public void turn(float turn) {
        this.turn = turn;
    }

    public float width() {
        return this.width;
    }

    public void width(float width) {
        this.width = width;
    }

    public float length() {
        return this.length;
    }

    public void length(float length) {
        this.length = length;
    }

    public float resetTime() {
        return this.resetTime;
    }

    public void resetTime(float resetTime) {
        this.resetTime = resetTime;
    }

    public float lastTime() {
        return this.lastTime;
    }

    public void lastTime(float lastTime) {
        this.lastTime = lastTime;
    }

    public float fdata2() {
        return this.fdata2;
    }

    public void fdata2(float fdata2) {
        this.fdata2 = fdata2;
    }

    public int telegraph() {
        return this.telegraph;
    }

    public void telegraph(int telegraph) {
        this.telegraph = telegraph;
    }

    public FloatSeq lastPositions() {
        return this.lastPositions;
    }

    public void lastPositions(FloatSeq lastPositions) {
        this.lastPositions = lastPositions;
    }

    public KamiBulletDatas.KamiBulletData bdata() {
        return this.bdata;
    }

    public void bdata(KamiBulletDatas.KamiBulletData bdata) {
        this.bdata = bdata;
    }
}
