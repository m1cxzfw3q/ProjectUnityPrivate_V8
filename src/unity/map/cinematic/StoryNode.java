package unity.map.cinematic;

import arc.func.Func3;
import arc.math.geom.Vec2;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.struct.StringMap;
import arc.util.serialization.Json;
import arc.util.serialization.JsonValue;
import mindustry.Vars;
import mindustry.ctype.ContentType;
import mindustry.io.JsonIO;
import rhino.Function;
import unity.gen.Float2;
import unity.map.ScriptedSector;
import unity.map.objectives.Objective;
import unity.map.objectives.ObjectiveModel;
import unity.ui.dialogs.canvas.CinematicCanvas;
import unity.util.JSBridge;

public class StoryNode implements Json.JsonSerializable {
    public final Vec2 position = new Vec2();
    public CinematicCanvas.NodeElem elem;
    public StoryNode parent;
    public final Seq<StoryNode> children = new Seq(2);
    public ScriptedSector sector;
    public String name;
    public String dataScript;
    public Object data;
    public Func3<StoryNode, Object, Json, String> dataSerializer = (node, data, json) -> json.toJson(data);
    public Func3<StoryNode, String, Json, Object> dataDeserializer = (node, data, json) -> json.fromJson(Object.class, data);
    public StringMap scripts = new StringMap();
    public Seq<ObjectiveModel> objectiveModels = new Seq();
    public Seq<Objective> objectives = new Seq();
    protected boolean initialized = false;
    protected boolean completed = false;

    public void write(Json json) {
        json.writeValue("sector", this.sector.name);
        json.writeValue("name", this.name);
        json.writeValue("dataScript", this.dataScript);
        json.writeValue("position", Float2.construct(this.position.x, this.position.y));
        json.writeValue("scripts", this.scripts, StringMap.class, String.class);
        json.writeValue("objectiveModels", this.objectiveModels, Seq.class, ObjectiveModel.class);
        json.writeValue("children", this.children, Seq.class, StoryNode.class);
    }

    public void read(Json json, JsonValue data) {
        this.sector = (ScriptedSector)Vars.content.getByName(ContentType.sector, data.getString("sector"));
        this.name = data.getString("name");
        long pos = data.getLong("position");
        this.position.set(Float2.x(pos), Float2.y(pos));
        this.dataScript = data.getString("dataScript", (String)null);
        this.scripts.clear();
        this.scripts.putAll((ObjectMap)json.readValue(StringMap.class, String.class, data.require("scripts"), String.class));
        this.children.set((Seq)json.readValue(Seq.class, StoryNode.class, data.require("children")));
        this.children.each((c) -> c.parent = this);
        this.objectiveModels.set((Seq)json.readValue(Seq.class, ObjectiveModel.class, data.require("objectiveModels")));
    }

    public void createObjectives() {
        this.objectives.clear();
        this.objectiveModels.each((m) -> this.objectives.add(m.create(this)));
    }

    public void init() {
        if (!this.initialized) {
            this.initialized = true;
            if (this.dataScript != null) {
                Function func = JSBridge.compileFunc(JSBridge.unityScope, this.dataScript, this.name + "-metadata.js");
                this.data = func.call(JSBridge.context, JSBridge.unityScope, JSBridge.unityScope, new Object[]{this});
            }

            this.objectives.each(Objective::init);
        }
    }

    public void reset() {
        this.initialized = false;
        this.completed = false;
        this.objectives.each(Objective::reset);
        this.children.each(StoryNode::reset);
    }

    public boolean completed() {
        if (!this.completed && !(this.completed = this.objectives.isEmpty())) {
            this.completed = true;

            for(Objective o : this.objectives) {
                if (!o.executed()) {
                    return this.completed = false;
                }
            }

            return true;
        } else {
            return true;
        }
    }

    public void update() {
        if (!this.initialized) {
            this.init();
        }

        if (this.completed()) {
            this.children.each(StoryNode::update);
        } else {
            for(Objective o : this.objectives) {
                if (!o.executed()) {
                    if (o.shouldUpdate()) {
                        o.update();
                    }

                    if (o.qualified()) {
                        o.execute();
                    }
                }
            }

        }
    }

    public void draw() {
        if (this.completed()) {
            this.children.each(StoryNode::draw);
        } else {
            for(Objective o : this.objectives) {
                if (o.shouldDraw()) {
                    o.draw();
                }
            }

        }
    }

    public void child(StoryNode other) {
        if (!this.children.contains(other)) {
            if (other.parent != null) {
                other.parent.children.remove(other);
            }

            other.parent = this;
            this.children.add(other);
        }
    }

    public void save(StringMap map) {
        map.put("data", (String)this.dataSerializer.get(this, this.data, JsonIO.json));
        map.put("completed", String.valueOf(this.completed));
        StringMap objMap = new StringMap();

        for(Objective obj : this.objectives) {
            StringMap child = new StringMap();
            obj.save(child);
            objMap.put(obj.name, JsonIO.json.toJson(child, StringMap.class, String.class));
        }

        map.put("objectives", JsonIO.json.toJson(objMap, StringMap.class, String.class));
        StringMap childMap = new StringMap();

        for(StoryNode child : this.children) {
            StringMap m = new StringMap();
            child.save(m);
            childMap.put(child.name, JsonIO.json.toJson(m, StringMap.class, String.class));
        }

        map.put("children", JsonIO.json.toJson(childMap, StringMap.class, String.class));
    }

    public void load(StringMap map) {
        this.data = this.dataDeserializer.get(this, (String)map.get("data", ""), JsonIO.json);
        this.completed = map.getBool("completed");
        StringMap objMap = (StringMap)JsonIO.json.fromJson(StringMap.class, String.class, (String)map.get("objectives", "{}"));
        ObjectMap.Entries var3 = objMap.entries().iterator();

        while(var3.hasNext()) {
            ObjectMap.Entry<String, String> e = (ObjectMap.Entry)var3.next();
            Objective obj = (Objective)this.objectives.find((o) -> o.name.equals(e.key));
            if (obj == null) {
                throw new IllegalStateException("Objective '" + (String)e.key + "' not found!");
            }

            obj.load((StringMap)JsonIO.json.fromJson(StringMap.class, String.class, (String)e.value));
        }

        StringMap nodes = (StringMap)JsonIO.json.fromJson(StringMap.class, String.class, (String)map.get("children", "[]"));
        ObjectMap.Entries var8 = nodes.entries().iterator();

        while(var8.hasNext()) {
            ObjectMap.Entry<String, String> e = (ObjectMap.Entry)var8.next();
            StoryNode node = (StoryNode)this.children.find((n) -> n.name.equals(e.key));
            if (node == null) {
                throw new IllegalStateException("Node '" + (String)e.key + "' not found!");
            }

            node.load((StringMap)JsonIO.json.fromJson(StringMap.class, String.class, (String)e.value));
        }

    }
}
