package unity.gen;

import arc.func.Boolf;
import arc.func.Cons;
import arc.func.Longf;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Position;
import arc.math.geom.Rect;
import arc.struct.ObjectFloatMap;
import arc.struct.ObjectMap;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import arc.util.pooling.Pool;
import arc.util.pooling.Pools;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.core.World;
import mindustry.entities.EntityGroup;
import mindustry.gen.Building;
import mindustry.gen.Drawc;
import mindustry.gen.Entityc;
import mindustry.gen.Groups;
import mindustry.gen.Posc;
import mindustry.gen.Unitc;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;
import unity.Unity;
import unity.util.AtomicPair;

public class Light implements Pool.Poolable, Entityc, Drawc, Posc, Lightc {
    public static final float yield = 400.0F;
    public static final float width = 1.5F;
    public static final float rotationInc = 22.5F;
    private static final Color tmpCol = new Color();
    private transient boolean added;
    public transient int id = EntityGroup.nextId();
    public float x;
    public float y;
    protected transient volatile float endX;
    protected transient volatile float endY;
    protected transient volatile float strength = 0.0F;
    public transient volatile float queueStrength = 0.0F;
    protected transient volatile float rotation = 0.0F;
    public transient volatile float queueRotation = 0.0F;
    public transient volatile long queuePosition = 0L;
    protected transient volatile LightHoldc.LightHoldBuildc source = null;
    public transient volatile LightHoldc.LightHoldBuildc queueSource = null;
    protected transient volatile int color;
    public transient volatile int queueColor;
    protected transient volatile boolean casted;
    protected transient volatile boolean valid;
    public transient volatile LightHoldc.LightHoldBuildc pointed;
    public transient volatile boolean rotationChanged;
    private transient ObjectFloatMap<Light> parents;
    private transient ThreadLocal<ObjectFloatMap.Entries<Light>> parentEntries;
    private transient ObjectMap<Longf<Light>, AtomicPair<Light, Light>> children;
    private transient ThreadLocal<ObjectMap.Entries<Longf<Light>, AtomicPair<Light, Light>>> childEntries;

    protected Light() {
        this.color = Color.whiteRgba;
        this.queueColor = SColor.a(Color.whiteRgba, 0.0F);
        this.casted = false;
        this.valid = false;
        this.rotationChanged = false;
        this.parents = new ObjectFloatMap(2);
        this.parentEntries = new ThreadLocal<ObjectFloatMap.Entries<Light>>() {
            protected ObjectFloatMap.Entries<Light> initialValue() {
                return new ObjectFloatMap.Entries(Light.this.parents);
            }
        };
        this.children = new ObjectMap(2);
        this.childEntries = new ThreadLocal<ObjectMap.Entries<Longf<Light>, AtomicPair<Light, Light>>>() {
            protected ObjectMap.Entries<Longf<Light>, AtomicPair<Light, Light>> initialValue() {
                return new ObjectMap.Entries(Light.this.children);
            }
        };
    }

    public String toString() {
        return "Light#" + this.id;
    }

    public void trns(float x, float y) {
        this.set(this.x + x, this.y + y);
    }

    public void parent(Light light, float mult) {
        this.parents((parents) -> parents.put(light, mult));
    }

    public void clearParents() {
        this.parents((parents) -> {
            ObjectFloatMap.Entries var2 = this.parentEntries().iterator();

            while(var2.hasNext()) {
                ObjectFloatMap.Entry<Light> l = (ObjectFloatMap.Entry)var2.next();
                ((Light)l.key).detachChild(this);
            }

            parents.clear();
        });
    }

    public float getY() {
        return this.y;
    }

    public void remove() {
        if (this.added) {
            Groups.all.remove(this);
            Groups.draw.remove(this);
            this.added = false;
            Unity.lights.quad((quad) -> quad.remove(this));
            Groups.queueFree(this);
        }
    }

    public ObjectFloatMap.Entries<Light> parentEntries() {
        ObjectFloatMap.Entries<Light> e = (ObjectFloatMap.Entries)this.parentEntries.get();
        e.reset();
        return e;
    }

    public boolean onSolid() {
        Tile tile = this.tileOn();
        return tile == null || tile.solid();
    }

    public void snap() {
        this.strength = this.queueStrength + this.recStrength();
        this.source = this.queueSource;
        this.color = this.combinedCol(this.queueColor);
        float rot = fixRot(this.queueRotation);
        if (!Mathf.equal(this.rotation, rot)) {
            this.rotationChanged = true;
        }

        this.rotation = rot;
        this.x = SVec2.x(this.queuePosition);
        this.y = SVec2.y(this.queuePosition);
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

    public void hitbox(Rect out) {
        out.set(this.x, this.y, 0.0F, 0.0F);
    }

    public void afterRead() {
    }

    public void detachParent(Light light) {
        this.parents((parents) -> parents.remove(light, 0.0F));
    }

    public float getX() {
        return this.x;
    }

    public void read(Reads read) {
        this.afterRead();
    }

    public void queueAdd() {
        Unity.lights.queueAdd(this);
    }

    public <T> T as() {
        return (T)this;
    }

    public boolean isParent(Light light) {
        return this.parentsAny((parents) -> parents.containsKey(light));
    }

    public boolean parentsAny(Boolf<ObjectFloatMap<Light>> cons) {
        synchronized(this.parents) {
            return cons.get(this.parents);
        }
    }

    public void clearInvalid() {
        this.parents((parents) -> {
            ObjectFloatMap.Entries<Light> it = this.parentEntries();

            while(it.hasNext) {
                Light l = (Light)it.next().key;
                if (l != null && (l.casted() && !l.valid() || !Mathf.equal(this.x, l.endX()) || !Mathf.equal(this.y, l.endY()))) {
                    l.detachChild(this);
                    it.remove();
                }
            }

        });
        this.children((children) -> {
            ObjectMap.Entries var2 = this.childEntries().iterator();

            while(var2.hasNext()) {
                ObjectMap.Entry<Longf<Light>, AtomicPair<Light, Light>> e = (ObjectMap.Entry)var2.next();
                AtomicPair<Light, Light> pair = (AtomicPair)e.value;
                Light direct = (Light)pair.key;
                Light indirect = (Light)pair.value;
                if (direct != null && direct.casted() && !direct.valid()) {
                    direct.detachParent(this);
                    pair.key = null;
                }

                if (indirect != null && indirect.casted() && !indirect.valid()) {
                    indirect.detachParent(this);
                    pair.value = null;
                }
            }

        });
    }

    public void cast() {
        this.clearInvalid();
        if ((this.source == null || !this.source.isValid()) && this.parentsAny((parents) -> parents.size <= 0)) {
            this.queueRemove();
        } else {
            float targetX = this.x + Angles.trnsx(this.rotation, this.strength * 400.0F);
            float targetY = this.y + Angles.trnsy(this.rotation, this.strength * 400.0F);
            boolean hit = Vars.world.raycast(World.toTile(this.x), World.toTile(this.y), World.toTile(targetX), World.toTile(targetY), (tx, ty) -> {
                Tile tile = Vars.world.tile(tx, ty);
                if (tile == null) {
                    Unity.lights.queuePoint(this, (LightHoldc.LightHoldBuildc)null);
                    this.endX = (float)(tx * 8);
                    this.endY = (float)(ty * 8);
                    return true;
                } else {
                    Building build = tile.build;
                    if (build instanceof LightHoldc.LightHoldBuildc) {
                        LightHoldc.LightHoldBuildc hold = (LightHoldc.LightHoldBuildc)build;
                        if (hold == this.source || this.parentsAny((parents) -> {
                            ObjectFloatMap.Entries var3 = this.parentEntries().iterator();

                            while(var3.hasNext()) {
                                ObjectFloatMap.Entry<Light> e = (ObjectFloatMap.Entry)var3.next();
                                if (hold == ((Light)e.key).pointed) {
                                    return true;
                                }
                            }

                            return false;
                        })) {
                            return false;
                        }

                        if (this.parentsAny((parents) -> {
                            ObjectFloatMap.Entries var3 = this.parentEntries().iterator();

                            while(var3.hasNext()) {
                                ObjectFloatMap.Entry<Light> e = (ObjectFloatMap.Entry)var3.next();
                                Light l = (Light)e.key;
                                if (l.parentsAny((p) -> {
                                    ObjectFloatMap.Entries var3 = l.parentEntries().iterator();

                                    while(var3.hasNext()) {
                                        ObjectFloatMap.Entry<Light> f = (ObjectFloatMap.Entry)var3.next();
                                        if (hold == ((Light)f.key).pointed) {
                                            return true;
                                        }
                                    }

                                    return false;
                                })) {
                                    return true;
                                }
                            }

                            return false;
                        })) {
                            Unity.lights.queuePoint(this, (LightHoldc.LightHoldBuildc)null);
                            this.endX = (float)(tx * 8);
                            this.endY = (float)(ty * 8);
                            return true;
                        }

                        if (hold.acceptLight(this, tx, ty)) {
                            Unity.lights.queuePoint(this, hold);
                            this.endX = tile.worldx();
                            this.endY = tile.worldy();
                            return true;
                        }

                        if (tile.solid()) {
                            Unity.lights.queuePoint(this, (LightHoldc.LightHoldBuildc)null);
                            this.endX = tile.worldx();
                            this.endY = tile.worldy();
                            return true;
                        }
                    } else if (tile.solid()) {
                        Unity.lights.queuePoint(this, (LightHoldc.LightHoldBuildc)null);
                        this.endX = tile.worldx();
                        this.endY = tile.worldy();
                        return true;
                    }

                    return false;
                }
            });
            if (!hit) {
                this.endX = (float)(Mathf.round(targetX / 8.0F) * 8);
                this.endY = (float)(Mathf.round(targetY / 8.0F) * 8);
            }

            Tile tile = Vars.world.tileWorld(this.endX, this.endY);
            if (tile != null) {
                this.children((children) -> {
                    ObjectMap.Entries var3 = this.childEntries().iterator();

                    while(var3.hasNext()) {
                        ObjectMap.Entry<Longf<Light>, AtomicPair<Light, Light>> e = (ObjectMap.Entry)var3.next();
                        Longf<Light> key = (Longf)e.key;
                        AtomicPair<Light, Light> pair = (AtomicPair)e.value;
                        long res = key.get(this);
                        float rot = Float2.x(res);
                        float str = Float2.y(res);
                        Unity.lights.quad((quad) -> quad.intersect(tile.worldx() - 4.0F, tile.worldy() - 4.0F, 8.0F, 8.0F, (l) -> {
                            if (l.valid() && pair.key != l && pair.value != l && !this.isParent(l) && Angles.near(rot, l.rotation(), 1.0F)) {
                                if (pair.key != null) {
                                    ((Light)pair.key).queueRemove();
                                    pair.key = null;
                                }

                                if (pair.value != null) {
                                    ((Light)pair.value).detachParent(this);
                                }

                                pair.value = l;
                                ((Light)pair.value).parent(this, str);
                            }

                        }));
                        if (pair.key == null && (pair.value == null || !Angles.near(rot, ((Light)pair.value).rotation(), 1.0F))) {
                            if (pair.value != null) {
                                ((Light)pair.value).detachParent(this);
                                pair.value = null;
                            }

                            Light l = create();
                            l.set(this.endX, this.endY);
                            l.parent(this, str);
                            l.queueAdd();
                            pair.key = l;
                        }
                    }

                });
            }

            this.children((children) -> {
                ObjectMap.Entries var2 = this.childEntries().iterator();

                while(var2.hasNext()) {
                    ObjectMap.Entry<Longf<Light>, AtomicPair<Light, Light>> e = (ObjectMap.Entry)var2.next();
                    Light l = (Light)((AtomicPair)e.value).key;
                    if (l != null) {
                        l.queuePosition = SVec2.construct(this.endX, this.endY);
                        long res = ((Longf)e.key).get(this);
                        float rot = Float2.x(res);
                        float str = Float2.y(res);
                        l.queueRotation = rot;
                        l.parent(this, str);
                    }
                }

            });
            this.casted = true;
            this.valid = true;
        }
    }

    public int tileY() {
        return World.toTile(this.y);
    }

    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void children(Cons<ObjectMap<Longf<Light>, AtomicPair<Light, Light>>> cons) {
        synchronized(this.children) {
            cons.get(this.children);
        }
    }

    public void detachChild(Light light) {
        this.children((children) -> {
            ObjectMap.Entries var3 = this.childEntries().iterator();

            while(var3.hasNext()) {
                ObjectMap.Entry<Longf<Light>, AtomicPair<Light, Light>> e = (ObjectMap.Entry)var3.next();
                AtomicPair<Light, Light> pair = (AtomicPair)e.value;
                if (pair.key == light) {
                    pair.key = null;
                }

                if (pair.value == light) {
                    pair.value = null;
                }
            }

        });
    }

    public int combinedCol(int baseCol) {
        synchronized(tmpCol) {
            tmpCol.set(1.0F, 1.0F, 1.0F, 1.0F);
            this.parents((parents) -> {
                int col;
                Color var8;
                for(ObjectFloatMap.Entries var3 = this.parentEntries().iterator(); var3.hasNext(); var8.b += SColor.b(col)) {
                    ObjectFloatMap.Entry<Light> e = (ObjectFloatMap.Entry)var3.next();
                    col = ((Light)e.key).color();
                    var8 = tmpCol;
                    var8.r += SColor.r(col);
                    var8 = tmpCol;
                    var8.g += SColor.g(col);
                    var8 = tmpCol;
                }

                int size = parents.size;
                if (size > 0) {
                    var8 = tmpCol;
                    var8.r /= (float)size;
                    var8 = tmpCol;
                    var8.g /= (float)size;
                    var8 = tmpCol;
                    var8.b /= (float)size;
                }

                tmpCol.lerp(SColor.r(baseCol), SColor.g(baseCol), SColor.b(baseCol), 1.0F, SColor.a(baseCol) / Math.min((float)size + 1.0F, 2.0F));
            });
            return tmpCol.rgba();
        }
    }

    public int tileX() {
        return World.toTile(this.x);
    }

    public static float fixRot(float rotation) {
        return Mathf.mod((float)Mathf.round(rotation / 22.5F) * 22.5F, 360.0F);
    }

    public void queueRemove() {
        this.valid = false;
        this.clearParents();
        this.clearChildren();
        Unity.lights.queueRemove(this);
    }

    public Tile tileOn() {
        return Vars.world.tileWorld(this.x, this.y);
    }

    public void parents(Cons<ObjectFloatMap<Light>> cons) {
        synchronized(this.parents) {
            cons.get(this.parents);
        }
    }

    public ObjectMap.Entries<Longf<Light>, AtomicPair<Light, Light>> childEntries() {
        ObjectMap.Entries<Longf<Light>, AtomicPair<Light, Light>> e = (ObjectMap.Entries)this.childEntries.get();
        e.reset();
        return e;
    }

    public void clearChildren() {
        this.children((children) -> {
            ObjectMap.Entries var2 = this.childEntries().iterator();

            while(var2.hasNext()) {
                ObjectMap.Entry<Longf<Light>, AtomicPair<Light, Light>> e = (ObjectMap.Entry)var2.next();
                AtomicPair<Light, Light> pair = (AtomicPair)e.value;
                Light direct = (Light)pair.key;
                Light indirect = (Light)pair.value;
                if (direct != null) {
                    direct.queueRemove();
                    pair.key = null;
                }

                if (indirect != null) {
                    indirect.detachParent(this);
                    pair.value = null;
                }
            }

            children.clear();
        });
    }

    public float clipSize() {
        return Mathf.dst(this.x, this.y, this.endX, this.endY) * 3.0F;
    }

    public <T extends Entityc> T self() {
        return (T)this;
    }

    public void draw() {
        if (this.valid) {
            float z = Draw.z();
            Draw.z(35.0F);
            Draw.blend(Blending.additive);
            float stroke = 0.75F;
            float rot = this.visualRot();
            float op = this.strength - 1.0F;
            float dst2 = this.dst2(this.endX, this.endY);
            float startc = Tmp.c1.set(this.color).a(Mathf.clamp(this.strength)).toFloatBits();
            float endc = Tmp.c1.set(this.color).a(Mathf.clamp(this.endStrength())).toFloatBits();
            if (op > 0.0F) {
                Tmp.v1.trns(rot, op * 400.0F).limit2(dst2).add(this);
                float x2 = Tmp.v1.x;
                float y2 = Tmp.v1.y;
                float len = Mathf.len(x2 - this.x, y2 - this.y);
                float diffx = (x2 - this.x) / len * stroke;
                float diffy = (y2 - this.y) / len * stroke;
                Fill.quad(this.x - diffx - diffy, this.y - diffy + diffx, startc, this.x - diffx + diffy, this.y - diffy - diffx, startc, x2 + diffx + diffy, y2 + diffy - diffx, startc, x2 + diffx - diffy, y2 + diffy + diffx, startc);
            }

            Tmp.v1.trns(rot, Math.max(op, 0.0F) * 400.0F).limit2(dst2).add(this);
            if (!Mathf.zero(Tmp.v1.len2())) {
                float x2 = Tmp.v1.x;
                float y2 = Tmp.v1.y;
                float len = Mathf.len(this.endX - x2, this.endY - y2);
                float diffx = (this.endX - x2) / len * stroke;
                float diffy = (this.endY - y2) / len * stroke;
                Fill.quad(x2 - diffx - diffy, y2 - diffy + diffx, startc, x2 - diffx + diffy, y2 - diffy - diffx, startc, this.endX + diffx + diffy, this.endY + diffy - diffx, endc, this.endX + diffx - diffy, this.endY + diffy + diffx, endc);
            }

            Draw.blend();
            Draw.z(z);
        }

    }

    public boolean isAdded() {
        return this.added;
    }

    public float visualRot() {
        return Angles.angle(this.x, this.y, this.endX, this.endY);
    }

    public void write(Writes write) {
    }

    public float recStrength() {
        float str = 0.0F;
        synchronized(this.parents) {
            ObjectFloatMap.Entry<Light> p;
            for(ObjectFloatMap.Entries var3 = this.parentEntries().iterator(); var3.hasNext(); str += ((Light)p.key).endStrength() * p.value) {
                p = (ObjectFloatMap.Entry)var3.next();
            }

            return str;
        }
    }

    public void update() {
    }

    public float endStrength() {
        return Math.max(this.strength - Mathf.dst(this.x, this.y, this.endX, this.endY) / 400.0F, 0.0F);
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

    public void child(Longf<Light> child) {
        this.children((children) -> ((AtomicPair)children.get(child, AtomicPair::new)).reset());
    }

    public boolean isNull() {
        return false;
    }

    public void add() {
        if (!this.added) {
            Groups.all.add(this);
            Groups.draw.add(this);
            this.added = true;
            Unity.lights.quad((quad) -> quad.insert(this));
        }
    }

    public boolean serialize() {
        return false;
    }

    public void reset() {
        this.added = false;
        this.id = EntityGroup.nextId();
        this.x = 0.0F;
        this.y = 0.0F;
        this.endX = 0.0F;
        this.endY = 0.0F;
        this.strength = 0.0F;
        this.queueStrength = 0.0F;
        this.rotation = 0.0F;
        this.queueRotation = 0.0F;
        this.queuePosition = 0L;
        this.source = null;
        this.queueSource = null;
        this.color = Color.whiteRgba;
        this.queueColor = SColor.a(Color.whiteRgba, 0.0F);
        this.casted = false;
        this.valid = false;
        this.pointed = null;
        this.rotationChanged = false;
    }

    public static Light create() {
        return (Light)Pools.obtain(Light.class, Light::new);
    }

    public int classId() {
        return UnityEntityMapping.classId(Light.class);
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

    public float endX() {
        return this.endX;
    }

    public float endY() {
        return this.endY;
    }

    public float strength() {
        return this.strength;
    }

    public float queueStrength() {
        return this.queueStrength;
    }

    public void queueStrength(float queueStrength) {
        this.queueStrength = queueStrength;
    }

    public float rotation() {
        return this.rotation;
    }

    public float queueRotation() {
        return this.queueRotation;
    }

    public void queueRotation(float queueRotation) {
        this.queueRotation = queueRotation;
    }

    public long queuePosition() {
        return this.queuePosition;
    }

    public void queuePosition(long queuePosition) {
        this.queuePosition = queuePosition;
    }

    public LightHoldc.LightHoldBuildc source() {
        return this.source;
    }

    public LightHoldc.LightHoldBuildc queueSource() {
        return this.queueSource;
    }

    public void queueSource(LightHoldc.LightHoldBuildc queueSource) {
        this.queueSource = queueSource;
    }

    public int color() {
        return this.color;
    }

    public int queueColor() {
        return this.queueColor;
    }

    public void queueColor(int queueColor) {
        this.queueColor = queueColor;
    }

    public boolean casted() {
        return this.casted;
    }

    public boolean valid() {
        return this.valid;
    }

    public LightHoldc.LightHoldBuildc pointed() {
        return this.pointed;
    }

    public void pointed(LightHoldc.LightHoldBuildc pointed) {
        this.pointed = pointed;
    }

    public boolean rotationChanged() {
        return this.rotationChanged;
    }

    public void rotationChanged(boolean rotationChanged) {
        this.rotationChanged = rotationChanged;
    }
}
