package unity.gen;

import arc.math.Angles;
import arc.math.geom.Position;
import arc.util.Nullable;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.core.World;
import mindustry.entities.EntityGroup;
import mindustry.gen.Entityc;
import mindustry.gen.Groups;
import mindustry.gen.Posc;
import mindustry.gen.Rotc;
import mindustry.gen.Unitc;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;

public class Trns implements Rotc, Posc, Entityc, Trnsc {
    public float rotation;
    public float x;
    public float y;
    private transient boolean added;
    public transient int id = EntityGroup.nextId();
    @Nullable
    public transient Posc parent;
    public transient float offsetX;
    public transient float offsetY;
    public transient float offsetRot;

    protected Trns() {
    }

    public String toString() {
        return "Trns#" + this.id;
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
            this.added = false;
        }
    }

    public void update() {
        if (this.parent != null) {
            float px = this.parent.getX();
            float py = this.parent.getY();
            Posc var4 = this.parent;
            if (var4 instanceof Rotc) {
                Rotc rot = (Rotc)var4;
                float r = rot.rotation();
                this.x = px + Angles.trnsx(r - 90.0F, this.offsetX, this.offsetY);
                this.y = py + Angles.trnsy(r - 90.0F, this.offsetX, this.offsetY);
                this.rotation = r + this.offsetRot;
            } else {
                this.x = px + this.offsetX;
                this.y = py + this.offsetY;
            }
        }

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

    public void set(Position pos) {
        this.set(pos.getX(), pos.getY());
    }

    public void afterRead() {
    }

    public void write(Writes write) {
        write.f(this.rotation);
        write.f(this.x);
        write.f(this.y);
    }

    public <T extends Entityc> T self() {
        return (T)this;
    }

    public void read(Reads read) {
        this.rotation = read.f();
        this.x = read.f();
        this.y = read.f();
        this.afterRead();
    }

    public <T> T as() {
        return (T)this;
    }

    public void trns(float x, float y) {
        this.set(this.x + x, this.y + y);
    }

    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void add() {
        if (!this.added) {
            Groups.all.add(this);
            this.added = true;
        }
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

    public void trns(Position pos) {
        this.trns(pos.getX(), pos.getY());
    }

    public boolean isAdded() {
        return this.added;
    }

    public boolean onSolid() {
        Tile tile = this.tileOn();
        return tile == null || tile.solid();
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
        return true;
    }

    public static Trns create() {
        return new Trns();
    }

    public int classId() {
        return UnityEntityMapping.classId(Trns.class);
    }

    public float rotation() {
        return this.rotation;
    }

    public void rotation(float arg0) {
        this.rotation = this.rotation;
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

    public int id() {
        return this.id;
    }

    public void id(int arg0) {
        this.id = this.id;
    }

    public Posc parent() {
        return this.parent;
    }

    public void parent(Posc parent) {
        this.parent = parent;
    }

    public float offsetX() {
        return this.offsetX;
    }

    public void offsetX(float offsetX) {
        this.offsetX = offsetX;
    }

    public float offsetY() {
        return this.offsetY;
    }

    public void offsetY(float offsetY) {
        this.offsetY = offsetY;
    }

    public float offsetRot() {
        return this.offsetRot;
    }

    public void offsetRot(float offsetRot) {
        this.offsetRot = offsetRot;
    }
}
