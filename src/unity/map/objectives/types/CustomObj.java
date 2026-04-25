package unity.map.objectives.types;

import arc.func.Boolf;
import arc.func.Cons;
import arc.func.Func;
import mindustry.gen.Icon;
import rhino.Context;
import rhino.Function;
import rhino.ImporterTopLevel;
import unity.graphics.UnityPal;
import unity.map.cinematic.StoryNode;
import unity.map.objectives.Objective;
import unity.map.objectives.ObjectiveModel;
import unity.util.JSBridge;

public class CustomObj extends Objective {
    public final Boolf<CustomObj> completer;

    public CustomObj(StoryNode node, String name, Boolf<CustomObj> completer, Cons<CustomObj> executor) {
        super(node, name, executor);
        this.completer = completer;
    }

    public static void setup() {
        ObjectiveModel.setup(CustomObj.class, UnityPal.scarColor, () -> Icon.pencil, (node, f) -> {
            Context c = JSBridge.context;
            ImporterTopLevel s = JSBridge.unityScope;
            String exec = (String)f.get("executor", "function(objective){}");
            Function func = JSBridge.compileFunc(s, f.name() + "-executor.js", exec, 1);
            String completerFunc = (String)f.get("completer");
            Func<Object[], Boolean> completer = JSBridge.requireType(JSBridge.compileFunc(s, f.name() + "-completer.js", completerFunc, 1), c, s, Boolean.TYPE);
            Object[] args = new Object[]{null};
            CustomObj obj = new CustomObj(node, f.name(), (e) -> {
                args[0] = e;
                return (Boolean)completer.get(args);
            }, (e) -> {
                args[0] = e;
                func.call(c, s, s, args);
            });
            obj.ext(f);
            return obj;
        });
    }

    public void update() {
        super.update();
        this.completed = this.completer.get(this);
    }
}
