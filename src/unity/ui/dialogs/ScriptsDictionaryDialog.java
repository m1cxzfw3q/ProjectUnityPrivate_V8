package unity.ui.dialogs;

import arc.scene.ui.TextField;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.struct.StringMap;
import mindustry.Vars;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;
import unity.Unity;

public class ScriptsDictionaryDialog extends BaseDialog {
    private StringMap dictionary;
    private final Seq<ScriptElem> elems = new Seq();
    private Table content;

    public ScriptsDictionaryDialog() {
        super("@root.cinematic.scripts");
        this.cont.pane(Styles.nonePane, (t) -> {
            this.content = t;
            this.content.defaults().pad(3.0F);
        }).grow();
        this.addCloseButton();
        this.buttons.button("@add", Icon.add, () -> {
            ScriptElem e = new ScriptElem(this.lastName(), "");
            this.elems.add(e);
            this.put(e);
        }).size(210.0F, 64.0F);
        this.shown(() -> {
            this.content.clearChildren();
            this.elems.clear();
            ObjectMap.Entries var1 = this.dictionary.entries().iterator();

            while(var1.hasNext()) {
                ObjectMap.Entry<String, String> e = (ObjectMap.Entry)var1.next();
                ScriptElem elem = new ScriptElem((String)e.key, (String)e.value);
                this.elems.add(elem);
            }

            this.rebuild();
        });
        this.hidden(() -> {
            this.dictionary.clear();

            for(ScriptElem e : this.elems) {
                this.dictionary.put(e.name, e.script);
            }

            this.elems.clear();
            this.dictionary = null;
        });
    }

    private String lastName() {
        int i = 0;

        for(ScriptElem e : this.elems) {
            if (e.name.startsWith("script") && Character.isDigit(e.name.codePointAt("script".length()))) {
                int index = Character.digit(e.name.charAt("script".length()), 10);
                if (index > i) {
                    i = index;
                }
            }
        }

        return "script" + (i + 1);
    }

    public ScriptsDictionaryDialog show() {
        if (this.dictionary != null) {
            super.show();
            return this;
        } else {
            throw new IllegalArgumentException("Use #show(StringMap)");
        }
    }

    public ScriptsDictionaryDialog show(StringMap dictionary) {
        this.dictionary = dictionary;
        return this.show();
    }

    private void rebuild() {
        this.content.clearChildren();

        for(ScriptElem e : this.elems) {
            this.put(e);
        }

    }

    private void put(ScriptElem elem) {
        this.content.add(elem).size(300.0F, 64.0F);
        if (this.content.getChildren().size % 3 == 0) {
            this.content.row();
        }

    }

    private class ScriptElem extends Table {
        private String name;
        private String script;

        public ScriptElem(String name, String script) {
            this.name = name;
            this.script = script;
            this.background(Tex.button);
            this.margin(0.0F);
            this.add("Name: ").padLeft(8.0F);
            TextField field = (TextField)this.field(name, (str) -> this.name = str).growX().get();
            field.setValidator((str) -> !ScriptsDictionaryDialog.this.elems.contains((e) -> e.name.equals(str)));
            this.add().growX();
            this.button(Icon.pencil, Styles.emptyi, () -> {
                Unity.jsEditDialog.listener = (str) -> this.script = str;
                Unity.jsEditDialog.area.setText(this.script);
                Unity.jsEditDialog.show();
            });
            this.button(Icon.trash, Styles.emptyi, () -> Vars.ui.showConfirm("@root.scripts-dictionary.delete.title", "@root.scripts-dictionary.delete.content", () -> {
                ScriptsDictionaryDialog.this.elems.remove(this);
                ScriptsDictionaryDialog.this.rebuild();
            })).padRight(8.0F);
        }
    }
}
