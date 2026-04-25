package unity.gen;

import arc.util.Log;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.entities.EntityGroup;
import mindustry.gen.Entityc;
import mindustry.gen.Groups;
import mindustry.gen.Unitc;

public class Test4 implements Entityc, Test3c, Test4c {
    private transient boolean added;
    public transient int id = EntityGroup.nextId();
    public transient int thing;

    protected Test4() {
    }

    public String toString() {
        return "Test4#" + this.id;
    }

    public void insertUpdateIfInheritsFromTest2() {
    }

    public void remove() {
        if (this.added) {
            Groups.all.remove(this);
            this.added = false;
        }
    }

    public void update() {
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

    public void add() {
        if (!this.added) {
            Groups.all.add(this);
            this.added = true;
        }
    }

    public <T extends Entityc> T self() {
        return (T)this;
    }

    public boolean isAdded() {
        return this.added;
    }

    public void yourThing() {
        Log.info("5, and no, it's a local variable so you can't change it.");
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

    public static Test4 create() {
        return new Test4();
    }

    public int classId() {
        return UnityEntityMapping.classId(Test4.class);
    }

    public int id() {
        return this.id;
    }

    public void id(int arg0) {
        this.id = this.id;
    }

    public int thing() {
        return this.thing;
    }

    public void thing(int thing) {
        this.thing = thing;
    }
}
