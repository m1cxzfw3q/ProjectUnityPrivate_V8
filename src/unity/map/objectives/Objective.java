package unity.map.objectives;

import arc.func.Cons;
import arc.struct.StringMap;
import rhino.Context;
import rhino.Function;
import rhino.ImporterTopLevel;
import unity.map.cinematic.StoryNode;
import unity.util.JSBridge;

public abstract class Objective {
    public final transient String name;
    public final Cons<Objective> executor;
    public final transient StoryNode node;
    protected transient boolean executed = false;
    protected transient boolean completed = false;
    public Cons<Objective> init = (objective) -> {
    };
    public Cons<Objective> update = (objective) -> {
    };
    public Cons<Objective> draw = (objective) -> {
    };

    public <T extends Objective> Objective(StoryNode node, String name, Cons<T> executor) {
        this.name = name;
        this.node = node;
        this.executor = executor;
    }

    public static float num(Object object) {
        if (object instanceof Number) {
            Number num = (Number)object;
            return num.floatValue();
        } else {
            throw new IllegalArgumentException("Must be a number");
        }
    }

    public void ext(ObjectiveModel.FieldTranslator f) {
        Context c = JSBridge.context;
        ImporterTopLevel s = JSBridge.unityScope;
        Object[] args = new Object[]{null};
        if (f.has("init")) {
            Function initFunc = JSBridge.compileFunc(s, this.name + "-init.js", (String)f.get("init"));
            this.init = (obj) -> {
                args[0] = obj;
                initFunc.call(c, s, s, args);
            };
        }

        if (f.has("update")) {
            Function updateFunc = JSBridge.compileFunc(s, this.name + "-update.js", (String)f.get("update"));
            this.update = (obj) -> {
                args[0] = obj;
                updateFunc.call(c, s, s, args);
            };
        }

        if (f.has("draw")) {
            Function drawFunc = JSBridge.compileFunc(s, this.name + "-draw.js", (String)f.get("draw"));
            this.draw = (obj) -> {
                args[0] = obj;
                drawFunc.call(c, s, s, args);
            };
        }

    }

    public void init() {
        this.init.get(this);
    }

    public void update() {
        this.update.get(this);
    }

    public void draw() {
        this.draw.get(this);
    }

    public boolean shouldUpdate() {
        return !this.executed && !this.completed;
    }

    public boolean qualified() {
        return !this.executed && this.completed;
    }

    public boolean shouldDraw() {
        return this.shouldUpdate();
    }

    public <T extends Objective> T init(Cons<T> init) {
        this.init = init;
        return (T)this;
    }

    public <T extends Objective> T update(Cons<T> update) {
        this.update = update;
        return (T)this;
    }

    public <T extends Objective> T draw(Cons<T> draw) {
        this.draw = draw;
        return (T)this;
    }

    public void save(StringMap map) {
        map.put("executed", String.valueOf(this.executed));
        map.put("completed", String.valueOf(this.completed));
    }

    public void load(StringMap map) {
        this.executed = map.getBool("executed");
        this.completed = map.getBool("completed");
    }

    public void reset() {
        this.completed = false;
        this.executed = false;
    }

    public void execute() {
        this.executor.get(this);
        this.stop();
    }

    public boolean completed() {
        return this.completed;
    }

    public boolean executed() {
        return this.executed;
    }

    public void stop() {
        this.completed = true;
        this.executed = true;
    }
}
