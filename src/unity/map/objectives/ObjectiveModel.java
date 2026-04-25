package unity.map.objectives;

import arc.func.Prov;
import arc.graphics.Color;
import arc.scene.style.Drawable;
import arc.struct.ObjectMap;
import arc.struct.OrderedMap;
import arc.util.Strings;
import arc.util.Log.LogLevel;
import arc.util.serialization.Json;
import arc.util.serialization.JsonValue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import mindustry.Vars;
import mindustry.graphics.Pal;
import rhino.Function;
import unity.Unity;
import unity.map.cinematic.StoryNode;
import unity.map.objectives.types.CustomObj;
import unity.map.objectives.types.ResourceAmountObj;
import unity.map.objectives.types.UnitPosObj;
import unity.util.JSBridge;

public class ObjectiveModel implements Json.JsonSerializable {
    public static OrderedMap<Class<? extends Objective>, ObjectiveData> datas = new OrderedMap();
    public Class<? extends Objective> type;
    public String name;
    public String init;
    public ObjectMap<String, Object> fields = new ObjectMap();
    private static final String env = "\\$\\{{2}.*\\}{2}";
    private static final Pattern replacer = Pattern.compile("\\$\\{{2}.*\\}{2}");
    private static final FieldTranslator translator = new FieldTranslator();

    public void set(Class<? extends Objective> type) {
        this.type = type;
        this.fields.clear();
    }

    public void write(Json json) {
        json.writeValue("type", this.type == null ? "null" : this.type.getName());
        json.writeValue("name", this.name);
        json.writeValue("init", this.init);
        json.writeValue("fields", this.fields, ObjectMap.class);
    }

    public void read(Json json, JsonValue data) {
        try {
            String typeName = data.getString("type");
            if (!typeName.equals("null")) {
                this.set(Class.forName(typeName, true, Vars.mods.mainLoader()));
            }

            this.name = data.getString("name");
            this.init = data.getString("init", "");
            this.fields.clear();
            this.fields.putAll((ObjectMap)json.readValue(ObjectMap.class, data.get("fields")));
        } catch (Exception e) {
            Unity.print(LogLevel.err, "", new Object[]{Strings.getStackTrace(Strings.getFinalCause(e))});
        }

    }

    public <T extends Objective> T create(StoryNode node) {
        ObjectiveData data = data(this.type);
        this.fields.clear();
        if (this.init != null) {
            String source = this.init;

            String script;
            for(Matcher matcher = replacer.matcher(this.init); matcher.find(); source = source.replaceFirst("\\$\\{{2}.*\\}{2}", script.replace("\r", "\n").replace("\n", "").replace("\\", "\\\\").replace("\"", "\\\\\""))) {
                String occur = this.init.substring(matcher.start(), matcher.end());
                occur = occur.substring(3, occur.length() - 2).trim();
                script = (String)node.scripts.getThrow(occur, () -> new IllegalArgumentException("No such script: '" + occur + "'"));
            }

            Function func = JSBridge.compileFunc(JSBridge.unityScope, this.name + "-init.js", source);
            func.call(JSBridge.context, JSBridge.unityScope, JSBridge.unityScope, new Object[]{this.fields});
        }

        translator.fields.clear();
        translator.fields.putAll(this.fields);
        translator.name = this.name;
        T obj = data.constructor.get(node, translator);
        translator.fields.clear();
        return obj;
    }

    public static void setup(Class<? extends Objective> type, Color color, Prov<Drawable> icon, ObjConstructor constructor) {
        datas.put(type, new ObjectiveData(color, icon, constructor));
    }

    public static ObjectiveData data(Class<? extends Objective> type) {
        if (type == null) {
            throw new IllegalArgumentException("type can't be null");
        } else {
            return (ObjectiveData)datas.getThrow(type, () -> new IllegalArgumentException("No data registered for " + type.getSimpleName()));
        }
    }

    static {
        ResourceAmountObj.setup();
        UnitPosObj.setup();
        CustomObj.setup();
    }

    public static class FieldTranslator {
        private String name;
        private final ObjectMap<String, Object> fields = new ObjectMap();

        public String name() {
            return this.name;
        }

        public <T> T get(String name) {
            return (T)this.fields.getThrow(name, () -> new IllegalArgumentException("'" + name + "' not found"));
        }

        public <T> T get(String name, T def) {
            return (T)this.fields.get(name, def);
        }

        public boolean has(String name) {
            return this.fields.containsKey(name);
        }
    }

    public static class ObjectiveData {
        public final Color color;
        public final ObjConstructor constructor;
        public final Prov<Drawable> icon;

        public ObjectiveData(Color color, Prov<Drawable> icon, ObjConstructor constructor) {
            this.color = Pal.accent.cpy();
            this.color.set(color);
            this.constructor = constructor;
            this.icon = icon;
        }
    }

    public interface ObjConstructor {
        Objective get(StoryNode var1, FieldTranslator var2);
    }
}
