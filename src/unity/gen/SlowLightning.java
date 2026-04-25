package unity.gen;

import arc.func.Floatp;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Position;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Nullable;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.core.World;
import mindustry.entities.EntityGroup;
import mindustry.game.Team;
import mindustry.gen.Bullet;
import mindustry.gen.Childc;
import mindustry.gen.Drawc;
import mindustry.gen.Entityc;
import mindustry.gen.Groups;
import mindustry.gen.Posc;
import mindustry.gen.Rotc;
import mindustry.gen.Unitc;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;
import unity.entities.effects.SlowLightningType;
import unity.util.Utils;

public class SlowLightning implements Entityc, Drawc, Posc, SlowLightningc, Rotc, Childc {
    public static Vec2 tv = new Vec2();
    public static boolean collided = false;
    private transient boolean added;
    public transient int id = EntityGroup.nextId();
    public float x;
    public float y;
    public Team team;
    public Position target;
    public Bullet bullet;
    public Floatp liveDamage;
    public SlowLightningType type;
    public Seq<SlowLightningType.SlowLightningNode> nodes;
    public int layer;
    public int seed;
    public int bulletId;
    public float time;
    public float distance;
    public float timer;
    public float lastX;
    public float lastY;
    public boolean ended;
    public boolean passed;
    public float rotation;
    @Nullable
    public Posc parent;
    public boolean rotWithParent;
    public float offsetX;
    public float offsetY;
    public float offsetPos;
    public float offsetRot;

    protected SlowLightning() {
        this.team = Team.derelict;
        this.nodes = new Seq(SlowLightningType.SlowLightningNode.class);
        this.layer = 0;
        this.seed = 1;
        this.bulletId = -1;
        this.ended = false;
        this.passed = false;
    }

    public String toString() {
        return "SlowLightning#" + this.id;
    }

    public void trns(float x, float y) {
        this.set(this.x + x, this.y + y);
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public void remove() {
        if (this.added) {
            Groups.all.remove(this);
            Groups.draw.remove(this);
            this.added = false;

            for(SlowLightningType.SlowLightningNode n : this.nodes) {
                SlowLightningType.nodes.free(n);
            }

            this.nodes.clear();
        }
    }

    public void updateLastPosition() {
        this.lastX = this.x;
        this.lastY = this.y;
    }

    public void update() {
        this.updateLastPosition();
        if (this.parent != null) {
            label58: {
                if (this.rotWithParent) {
                    Posc var2 = this.parent;
                    if (var2 instanceof Rotc) {
                        Rotc r = (Rotc)var2;
                        this.x = this.parent.getX() + Angles.trnsx(r.rotation() + this.offsetPos, this.offsetX, this.offsetY);
                        this.y = this.parent.getY() + Angles.trnsy(r.rotation() + this.offsetPos, this.offsetX, this.offsetY);
                        this.rotation = r.rotation() + this.offsetRot;
                        break label58;
                    }
                }

                this.x = this.parent.getX() + this.offsetX;
                this.y = this.parent.getY() + this.offsetY;
            }
        }

        if (this.parent() != null) {
            float dx = this.x - this.lastX;
            float dy = this.y - this.lastY;

            for(SlowLightningType.SlowLightningNode n : this.nodes) {
                n.move(this.layer, dx, dy);
            }
        }

        if (this.bullet != null && this.bullet.id != this.bulletId) {
            this.bullet = null;
        }

        if (this.type.continuous && (this.timer += Time.delta) >= 5.0F) {
            for(SlowLightningType.SlowLightningNode n : this.nodes) {
                n.collide();
            }

            this.timer = 0.0F;
        }

        for(int i = 0; i < this.nodes.size; ++i) {
            ((SlowLightningType.SlowLightningNode[])this.nodes.items)[i].update();
        }

        if (this.time >= this.type.lifetime) {
            this.remove();
        }

        this.time += Time.delta;
    }

    public Floor floorOn() {
        Tile tile = this.tileOn();
        return tile != null && tile.block() == Blocks.air ? tile.floor() : (Floor)Blocks.air;
    }

    public void trns(Position pos) {
        this.trns(pos.getX(), pos.getY());
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

    public boolean isNull() {
        return false;
    }

    public void afterRead() {
    }

    public void write(Writes write) {
    }

    public void read(Reads read) {
        this.afterRead();
    }

    public <T> T as() {
        return (T)this;
    }

    public float nextRange(float range) {
        float r = Mathf.randomSeed((long)this.seed, -range, range);
        this.seed = Mathf.randomSeed((long)this.seed, 63, Integer.MAX_VALUE);
        return r;
    }

    public boolean nextBoolean(float chance) {
        boolean b = Mathf.randomSeed((long)this.seed, 1.0F) < chance;
        this.seed = Mathf.randomSeed((long)this.seed, 63, Integer.MAX_VALUE);
        return b;
    }

    public void add() {
        if (!this.added) {
            Groups.all.add(this);
            Groups.draw.add(this);
            this.added = true;
            this.lastX = this.x;
            this.lastY = this.y;
            if (this.bullet != null) {
                this.bulletId = this.bullet.id;
            }

            this.end((SlowLightningType.SlowLightningNode)null);
            if (this.parent != null) {
                this.offsetX = this.x - this.parent.getX();
                this.offsetY = this.y - this.parent.getY();
                if (this.rotWithParent) {
                    Posc var2 = this.parent;
                    if (var2 instanceof Rotc) {
                        Rotc r = (Rotc)var2;
                        this.offsetPos = -r.rotation();
                        this.offsetRot = this.rotation - r.rotation();
                    }
                }
            }

        }
    }

    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public int tileX() {
        return World.toTile(this.x);
    }

    public int tileY() {
        return World.toTile(this.y);
    }

    public Tile tileOn() {
        return Vars.world.tileWorld(this.x, this.y);
    }

    public float clipSize() {
        return Float.MAX_VALUE;
    }

    public <T extends Entityc> T self() {
        return (T)this;
    }

    public void draw() {
        float fin = Math.min(this.type.lifetime - this.time, this.type.fadeTime) / this.type.fadeTime;
        float z = Draw.z();
        Draw.z(110.0F);
        Lines.stroke(this.type.lineWidth * fin);

        for(SlowLightningType.SlowLightningNode n : this.nodes) {
            n.draw();
        }

        Draw.reset();
        Draw.z(z);
    }

    public boolean isAdded() {
        return this.added;
    }

    public boolean onSolid() {
        Tile tile = this.tileOn();
        return tile == null || tile.solid();
    }

    public void end(SlowLightningType.SlowLightningNode node) {
        boolean split = this.nextBoolean(this.type.splitChance);

        for(int i = 0; i < (split ? 2 : 1); ++i) {
            float r = this.nextRange(split ? this.type.splitRandSpacing : this.type.randSpacing);
            float tr = node != null ? node.rotation + node.rotRand : this.rotation;
            if (this.target != null) {
                float scl = 1.0F - Mathf.clamp(this.dst(this.target) / this.type.rotationDistance);
                tr = Angles.moveToward(tr, this.angleTo(this.target), (this.type.maxRotationSpeed - this.type.minRotationSpeed) * scl + this.type.minRotationSpeed);
            }

            float rr = tr + r;
            collided = false;
            float nl = this.type.nodeLength;
            Vec2 v2 = Tmp.v2.set((Position)(node == null ? this : node));
            Vec2 v = Tmp.v1.trns(rr, Math.min(this.type.nodeLength, this.type.range - nl)).add(v2);
            float l = Utils.findLaserLength(v2.x, v2.y, v.x, v.y, (tile) -> collided |= tile.team() != this.team && tile.block() != null && tile.block().absorbLasers);
            if (l < this.type.nodeTime) {
                v.sub(v2).scl(l / this.type.nodeLength).add(v2);
            }

            SlowLightningType.SlowLightningNode n = (SlowLightningType.SlowLightningNode)SlowLightningType.nodes.obtain();
            n.main = this;
            n.parent = node;
            n.rotation = rr;
            n.x = v.x;
            n.y = v.y;
            if (node != null) {
                n.rotRand = -node.rotRand + (-r + node.rotRand) * this.nextRand();
                n.layer = node.layer + 1;
                n.dist = node.dist + l;
            } else {
                n.rotRand = -r;
                n.layer = this.layer + 1;
                n.dist = l;
            }

            n.ended = collided || n.dist >= this.type.range;
            this.distance = Math.max(this.distance, n.dist);
            this.layer = Math.max(this.layer, n.layer);
            this.nodes.add(n);
        }

    }

    public float nextRand() {
        float r = Mathf.randomSeed((long)this.seed, 1.0F);
        this.seed = Mathf.randomSeed((long)this.seed, 63, Integer.MAX_VALUE);
        return r;
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

    public boolean serialize() {
        return false;
    }

    public static SlowLightning create() {
        return new SlowLightning();
    }

    public int classId() {
        return UnityEntityMapping.classId(SlowLightning.class);
    }

    public int id() {
        return this.id;
    }

    public void id(int arg0) {
        this.id = this.id;
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

    public Team team() {
        return this.team;
    }

    public void team(Team team) {
        this.team = team;
    }

    public Position target() {
        return this.target;
    }

    public void target(Position target) {
        this.target = target;
    }

    public Bullet bullet() {
        return this.bullet;
    }

    public void bullet(Bullet bullet) {
        this.bullet = bullet;
    }

    public Floatp liveDamage() {
        return this.liveDamage;
    }

    public void liveDamage(Floatp liveDamage) {
        this.liveDamage = liveDamage;
    }

    public SlowLightningType type() {
        return this.type;
    }

    public void type(SlowLightningType type) {
        this.type = type;
    }

    public Seq<SlowLightningType.SlowLightningNode> nodes() {
        return this.nodes;
    }

    public void nodes(Seq<SlowLightningType.SlowLightningNode> nodes) {
        this.nodes = nodes;
    }

    public int layer() {
        return this.layer;
    }

    public void layer(int layer) {
        this.layer = layer;
    }

    public int seed() {
        return this.seed;
    }

    public void seed(int seed) {
        this.seed = seed;
    }

    public int bulletId() {
        return this.bulletId;
    }

    public void bulletId(int bulletId) {
        this.bulletId = bulletId;
    }

    public float time() {
        return this.time;
    }

    public void time(float time) {
        this.time = time;
    }

    public float distance() {
        return this.distance;
    }

    public void distance(float distance) {
        this.distance = distance;
    }

    public float timer() {
        return this.timer;
    }

    public void timer(float timer) {
        this.timer = timer;
    }

    public float lastX() {
        return this.lastX;
    }

    public void lastX(float lastX) {
        this.lastX = lastX;
    }

    public float lastY() {
        return this.lastY;
    }

    public void lastY(float lastY) {
        this.lastY = lastY;
    }

    public boolean ended() {
        return this.ended;
    }

    public void ended(boolean ended) {
        this.ended = ended;
    }

    public boolean passed() {
        return this.passed;
    }

    public void passed(boolean passed) {
        this.passed = passed;
    }

    public float rotation() {
        return this.rotation;
    }

    public void rotation(float arg0) {
        this.rotation = this.rotation;
    }

    public Posc parent() {
        return this.parent;
    }

    public void parent(Posc arg0) {
        this.parent = this.parent;
    }

    public boolean rotWithParent() {
        return this.rotWithParent;
    }

    public void rotWithParent(boolean arg0) {
        this.rotWithParent = this.rotWithParent;
    }

    public float offsetX() {
        return this.offsetX;
    }

    public void offsetX(float arg0) {
        this.offsetX = this.offsetX;
    }

    public float offsetY() {
        return this.offsetY;
    }

    public void offsetY(float arg0) {
        this.offsetY = this.offsetY;
    }

    public float offsetPos() {
        return this.offsetPos;
    }

    public void offsetPos(float arg0) {
        this.offsetPos = this.offsetPos;
    }

    public float offsetRot() {
        return this.offsetRot;
    }

    public void offsetRot(float arg0) {
        this.offsetRot = this.offsetRot;
    }
}
