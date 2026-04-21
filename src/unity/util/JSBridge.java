package unity.util;

import arc.Core;
import arc.func.Func;
import mindustry.Vars;
import rhino.Context;
import rhino.Function;
import rhino.ImporterTopLevel;
import rhino.NativeJavaClass;
import rhino.NativeJavaPackage;
import rhino.Scriptable;
import rhino.Wrapper;
import unity.Unity;

public final class JSBridge {
    public static Context context;
    public static ImporterTopLevel defaultScope;
    public static ImporterTopLevel unityScope;

    private JSBridge() {
    }

    public static void init() {
        if (!Unity.tools) {
            context = Vars.mods.getScripts().context;
            defaultScope = (ImporterTopLevel)Vars.mods.getScripts().scope;
            unityScope = new ImporterTopLevel(context);
            context.evaluateString(unityScope, Core.files.internal("scripts/global.js").readString(), "global.js", 1);
            context.evaluateString(unityScope, "function apply(map, object){\n    for(let key in object){\n        map.put(key, object[key]);\n    }\n}\n", "apply.js", 1);
        }
    }

    public static void importDefaults(ImporterTopLevel scope) {
        if (!Unity.tools) {
            for(String pack : Unity.packages) {
                importPackage(scope, pack);
            }

        }
    }

    public static void importPackage(ImporterTopLevel scope, String packageName) {
        if (!Unity.tools) {
            NativeJavaPackage p = new NativeJavaPackage(packageName, Vars.mods.mainLoader());
            p.setParentScope(scope);
            scope.importPackage(p);
        }
    }

    public static void importPackage(ImporterTopLevel scope, Package pack) {
        if (!Unity.tools) {
            importPackage(scope, pack.getName());
        }
    }

    public static void importClass(ImporterTopLevel scope, String canonical) {
        if (!Unity.tools) {
            importClass(scope, ReflectUtils.findClass(canonical));
        }
    }

    public static void importClass(ImporterTopLevel scope, Class<?> type) {
        if (!Unity.tools) {
            NativeJavaClass nat = new NativeJavaClass(scope, type);
            nat.setParentScope(scope);
            scope.importClass(nat);
        }
    }

    public static Function compileFunc(Scriptable scope, String sourceName, String source) {
        if (Unity.tools) {
            throw new IllegalStateException();
        } else {
            return compileFunc(scope, sourceName, source, 1);
        }
    }

    public static Function compileFunc(Scriptable scope, String sourceName, String source, int lineNum) {
        if (Unity.tools) {
            throw new IllegalStateException();
        } else {
            return context.compileFunction(scope, source, sourceName, lineNum);
        }
    }

    public static <T> Func<Object[], T> requireType(Function func, Context context, Scriptable scope, Class<T> returnType) {
        Class<?> type = ReflectUtils.box(returnType);
        return (args) -> {
            Object res = func.call(context, scope, scope, args);
            if (type != Void.TYPE && type != Void.class) {
                if (res instanceof Wrapper) {
                    Wrapper w = (Wrapper)res;
                    res = w.unwrap();
                }

                if (!type.isAssignableFrom(res.getClass())) {
                    throw new IllegalStateException("Incompatible return type: Expected '" + returnType + "', but got '" + res.getClass() + "'!");
                } else {
                    return type.cast(res);
                }
            } else {
                return null;
            }
        };
    }
}
