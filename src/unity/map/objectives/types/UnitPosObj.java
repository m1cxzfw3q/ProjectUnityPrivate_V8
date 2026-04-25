package unity.map.objectives.types;

import arc.func.Boolf2;
import arc.func.Cons;
import arc.func.Cons2;
import arc.func.Func;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.math.geom.Intersector;
import arc.math.geom.Vec2;
import arc.struct.IntSet;
import arc.struct.Seq;
import arc.struct.StringMap;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.game.Team;
import mindustry.gen.Groups;
import mindustry.gen.Icon;
import mindustry.gen.Unit;
import mindustry.io.JsonIO;
import rhino.Context;
import rhino.Function;
import rhino.ImporterTopLevel;
import unity.gen.Float2;
import unity.map.cinematic.StoryNode;
import unity.map.objectives.Objective;
import unity.map.objectives.ObjectiveModel;
import unity.util.JSBridge;

public class UnitPosObj extends Objective {
    public int count;
    public boolean continuous;
    public Cons2<UnitPosObj, Unit> spotted = (objective, unit) -> {
    };
    public Cons2<UnitPosObj, Unit> released = (objective, unit) -> {
    };
    public Boolf2<UnitPosObj, Unit> valid = (objective, unit) -> true;
    public Team team;
    public Vec2 pos;
    public float radius;
    public final transient IntSet within;
    private static final IntSet tmp = new IntSet();

    public UnitPosObj(StoryNode node, String name, Cons<UnitPosObj> executor) {
        super(node, name, executor);
        this.team = Vars.state.rules.defaultTeam;
        this.pos = new Vec2();
        this.radius = 32.0F;
        this.within = new IntSet();
    }

    public static void setup() {
        ObjectiveModel.setup(UnitPosObj.class, Color.sky, () -> Icon.units, (node, f) -> {
            String exec = (String)f.get("executor", "function(objective){}");
            Function func = JSBridge.compileFunc(JSBridge.unityScope, f.name() + "-executor.js", exec, 1);
            Object[] args = new Object[]{null};
            UnitPosObj obj = new UnitPosObj(node, f.name(), (e) -> {
                args[0] = e;
                func.call(JSBridge.context, JSBridge.unityScope, JSBridge.unityScope, args);
            });
            obj.ext(f);
            return obj;
        });
    }

    public void ext(ObjectiveModel.FieldTranslator f) {
        super.ext(f);
        this.count = (int)num(f.get("count"));
        this.continuous = (Boolean)f.get("continuous");
        this.team = (Team)f.get("team", Vars.state.rules.defaultTeam);
        this.pos = (Vec2)f.get("pos", this.pos);
        this.radius = num(f.get("radius", this.radius));
        Context c = JSBridge.context;
        ImporterTopLevel s = JSBridge.unityScope;
        Object[] args = new Object[]{null, null};
        if (f.has("spotted")) {
            Function spottedFunc = JSBridge.compileFunc(s, this.name + "-spotted.js", (String)f.get("spotted"));
            this.spotted = (obj, u) -> {
                args[0] = obj;
                args[1] = u;
                spottedFunc.call(c, s, s, args);
            };
        }

        if (f.has("released")) {
            Function releasedFunc = JSBridge.compileFunc(s, this.name + "-released.js", (String)f.get("released"));
            this.released = (obj, u) -> {
                args[0] = obj;
                args[1] = u;
                releasedFunc.call(c, s, s, args);
            };
        }

        if (f.has("valid")) {
            Func<Object[], Boolean> validFunc = JSBridge.requireType(JSBridge.compileFunc(s, this.name + "-valid.js", (String)f.get("valid")), c, s, Boolean.TYPE);
            this.valid = (obj, u) -> {
                args[0] = obj;
                args[1] = u;
                return (Boolean)validFunc.get(args);
            };
        }

    }

    public void update() {
        super.update();
        float sqrRad = this.radius * this.radius;
        tmp.clear();
        Groups.unit.each(this::valid, (ux) -> {
            if (this.within.contains(ux.id)) {
                if (this.continuous) {
                    tmp.add(ux.id);
                }

            } else {
                ux.hitbox(Tmp.r1);
                if (Intersector.overlaps(Tmp.cr1.set(this.pos, this.radius), Tmp.r1) || Intersector.intersectSegmentCircle(Tmp.v1.set(ux.lastX, ux.lastY), Tmp.v2.set(ux.x, ux.y), this.pos, sqrRad)) {
                    this.spotted.get(this, ux);
                    this.within.add(ux.id);
                }

            }
        });
        if (this.continuous) {
            IntSet.IntSetIterator it = tmp.iterator();

            while(it.hasNext) {
                int id = it.next();
                Unit u = (Unit)Groups.unit.getByID(id);
                if (u == null) {
                    this.within.remove(id);
                } else {
                    u.hitbox(Tmp.r1);
                    if (!this.valid(u) || !Intersector.overlaps(Tmp.cr1.set(this.pos, this.radius), Tmp.r1)) {
                        this.within.remove(id);
                        this.released.get(this, u);
                    }
                }
            }
        }

        this.completed = this.within.size >= this.count;
    }

    public void reset() {
        super.reset();
        this.within.clear();
    }

    public void save(StringMap map) {
        super.save(map);
        map.put("pos", String.valueOf(Float2.construct(this.pos.x, this.pos.y)));
        map.put("radius", String.valueOf(this.radius));
        map.put("team", String.valueOf(this.team.id));
        map.put("count", String.valueOf(this.count));
        map.put("continuous", String.valueOf(this.continuous));
        Seq<Unit> units = new Seq(this.within.size);
        IntSet.IntSetIterator it = this.within.iterator();

        while(it.hasNext) {
            Unit unit = (Unit)Groups.unit.getByID(it.next());
            if (unit != null && this.valid(unit)) {
                units.add(unit);
            }
        }

        map.put("within", JsonIO.json.toJson(units.map((u) -> String.valueOf(Float2.construct(u.x, u.y))), Seq.class, String.class));
    }

    public void load(StringMap map) {
        super.load(map);
        if (map.containsKey("pos")) {
            long saved = map.getLong("pos");
            this.pos.set(Float2.x(saved), Float2.y(saved));
        }

        this.radius = map.getFloat("radius", this.radius);
        this.team = Team.get(map.getInt("team", this.team.id));
        this.count = map.getInt("count", this.count);
        this.continuous = !map.containsKey("continuous") ? this.continuous : map.getBool("continuous");
        this.within.clear();

        for(String str : (Seq)JsonIO.json.fromJson(Seq.class, String.class, (String)map.get("within", "[]"))) {
            long pos = Long.parseLong(str);
            Unit unit = (Unit)Groups.unit.find((u) -> u != null && Mathf.equal(u.x, Float2.x(pos)) && Mathf.equal(u.y, Float2.y(pos)));
            if (unit != null) {
                this.within.add(unit.id);
            }
        }

    }

    public boolean valid(Unit unit) {
        return unit.team == this.team && unit.isValid() && this.valid.get(this, unit);
    }
}
