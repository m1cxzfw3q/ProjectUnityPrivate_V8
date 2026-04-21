package unity.ui.dialogs;

import arc.scene.style.Drawable;
import arc.scene.ui.Label;
import arc.scene.ui.TextField;
import arc.scene.ui.layout.Cell;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Tmp;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import mindustry.Vars;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.graphics.Pal;
import mindustry.ui.Fonts;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;
import unity.Unity;
import unity.map.cinematic.StoryNode;
import unity.map.objectives.Objective;
import unity.map.objectives.ObjectiveModel;

public class ObjectivesDialog extends BaseDialog {
    private StoryNode node;
    private final Seq<ObjectiveModel> models = new Seq();
    private Table content;
    private final ObjectMap<Class<? extends Objective>, Seq<Field>> fields = new ObjectMap();

    public ObjectivesDialog() {
        super("@root.cinematic.objectives");
        this.addCloseButton();
        this.cont.pane(Styles.nonePane, (t) -> this.content = t).growY().width(750.0F);
        this.buttons.button("@add", Icon.add, () -> {
            ObjectiveModel model = new ObjectiveModel();
            model.name = this.lastName();
            this.models.add(model);
            this.add(model);
        }).size(210.0F, 64.0F);
        this.shown(() -> {
            this.content.clearChildren();
            this.models.set(this.node.objectiveModels);
            this.models.each(this::add);
        });
        this.hidden(() -> {
            this.node.objectiveModels.set(this.models);
            this.node = null;
        });
    }

    private String lastName() {
        int i = 0;

        for(ObjectiveModel m : this.models) {
            if (m.name.startsWith("objective") && Character.isDigit(m.name.codePointAt("objective".length()))) {
                int index = Character.digit(m.name.charAt("objective".length()), 10);
                if (index > i) {
                    i = index;
                }
            }
        }

        return "objective" + (i + 1);
    }

    private Seq<Field> getFields(Class<? extends Objective> type) {
        return (Seq)this.fields.get(type, () -> {
            Seq<Field> all = new Seq();
            Class<?> current = type;

            while(true) {
                for(Field f : current.getDeclaredFields()) {
                    int mod = f.getModifiers();
                    if (!Modifier.isStatic(mod) && !Modifier.isTransient(mod)) {
                        all.add(f);
                    }
                }

                if (current == Objective.class) {
                    return all;
                }

                current = current.getSuperclass();
            }
        });
    }

    public ObjectivesDialog show() {
        if (this.node != null) {
            super.show();
            return this;
        } else {
            throw new IllegalArgumentException("Use #show(StoryNode)");
        }
    }

    public ObjectivesDialog show(StoryNode node) {
        this.node = node;
        return this.show();
    }

    private void add(ObjectiveModel model) {
        this.content.add(new ObjectiveElem(model)).growX().fillY().padTop(8.0F).padBottom(8.0F).row();
    }

    private class ObjectiveElem extends Table {
        private final ObjectiveModel model;
        private Table fields;

        private ObjectiveElem(ObjectiveModel model) {
            this.model = model;
            this.background(Tex.whiteui);
            this.update(() -> this.setColor(model.type == null ? Pal.darkerMetal : ObjectiveModel.data(model.type).color));
            this.margin(0.0F);
            this.table((t) -> {
                t.add("Name: ").style(Styles.outlineLabel).padLeft(8.0F);
                TextField field = (TextField)t.field(model.name, Styles.defaultField, (str) -> model.name = str).get();
                field.setValidator((str) -> !ObjectivesDialog.this.models.contains((m) -> m.name.equals(str)));
                field.getStyle().font = Fonts.outline;
                t.add().growX();
                t.button(Icon.trash, Styles.logici, () -> Vars.ui.showConfirm("@root.cinematic.objectives.delete.title", "@root.cinematic.objectives.delete.content", this::remove)).padRight(8.0F);
            }).growX().fillY().pad(4.0F);
            this.row().table(Styles.black5, (t) -> {
                t.defaults().pad(4.0F);
                t.table((ts) -> {
                    Label typeSelect = (Label)ts.label(() -> "Type: " + (model.type == null ? "..." : model.type.getSimpleName())).fillX().padLeft(8.0F).get();
                    typeSelect.setAlignment(8);
                    ts.add().growX();
                    ts.button(Icon.downOpen, Styles.logici, () -> {
                        BaseDialog dialog = new BaseDialog("@root.cinematic.objectives.select");
                        dialog.addCloseButton();
                        dialog.cont.pane(Styles.nonePane, (s) -> {
                            ObjectMap.Keys var3 = ObjectiveModel.datas.keys().iterator();

                            while(var3.hasNext()) {
                                Class<? extends Objective> type = (Class)var3.next();
                                ObjectiveModel.ObjectiveData data = ObjectiveModel.data(type);
                                s.button(type.getSimpleName(), (Drawable)data.icon.get(), Styles.defaultt, () -> {
                                    model.set(model.type == type ? null : type);
                                    this.rebuild();
                                }).size(350.0F, 64.0F).color(data.color).pad(4.0F).row();
                            }

                        }).width(500.0F).growY();
                        dialog.show();
                    }).padRight(8.0F);
                    ts.button(Icon.pencil, Styles.logici, () -> {
                        Unity.jsEditDialog.listener = (str) -> model.init = str;
                        Unity.jsEditDialog.area.setText(model.init);
                        if (Unity.jsEditDialog.area.getText().isEmpty()) {
                            Unity.jsEditDialog.area.setText("function(fields){\n\n}\n");
                        }

                        Unity.jsEditDialog.show();
                    });
                }).growX().fillY();
                t.row().table((c) -> {
                    this.fields = c;
                    this.rebuild();
                }).growX().fillY().padTop(8.0F);
            }).grow().pad(8.0F).padTop(16.0F);
        }

        private void rebuild() {
            this.fields.clearChildren();
            this.fields.defaults().pad(4.0F);
            if (this.model.type == null) {
                this.fields.add("...", Styles.outlineLabel).grow();
            } else {
                this.fields.table((t) -> {
                    ((Label)t.add("Fields", Styles.outlineLabel).get()).setAlignment(8);
                    t.row().image(Tex.whiteui).growX().height(3.0F).color(Tmp.c1.set(this.color).mul(0.5F)).padTop(8.0F);
                }).growX().fillY().padLeft(8.0F).padRight(8.0F).padBottom(8.0F);
                this.fields.row().table((t) -> {
                    for(Field f : ObjectivesDialog.this.getFields(this.model.type)) {
                        t.add(f.getName(), Styles.outlineLabel).growX();
                        t.add(" | ", Styles.outlineLabel);
                        t.add(f.getGenericType().getTypeName(), Styles.outlineLabel).growX();
                        t.row();
                    }

                }).grow().padLeft(8.0F).padRight(8.0F).padTop(8.0F);
            }

        }

        public boolean remove() {
            Cell<ObjectiveElem> cell = ObjectivesDialog.this.content.getCell(this);
            boolean succeed = super.remove();
            if (succeed && cell != null) {
                ObjectivesDialog.this.models.remove(this.model);
                ObjectivesDialog.this.content.getCells().remove(cell);
                ObjectivesDialog.this.content.invalidate();
            }

            return succeed;
        }
    }
}
