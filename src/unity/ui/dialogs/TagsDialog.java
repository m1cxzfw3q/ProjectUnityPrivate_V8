package unity.ui.dialogs;

import arc.scene.ui.layout.Cell;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectMap;
import arc.struct.ObjectSet;
import arc.struct.Seq;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;

public class TagsDialog extends BaseDialog {
    private Table content;
    private final Seq<TagElem> elems = new Seq();
    public ObjectMap<Object, ObjectSet<String>> tags = new ObjectMap();
    public Object bound;

    public TagsDialog() {
        super("@root.cinematic.tag");
        this.cont.pane((t) -> this.content = t).growY().width(500.0F);
        this.addCloseButton();
        this.buttons.button("@add", Icon.add, () -> {
            this.add(new TagElem(this.lastName()));
            this.refresh();
        }).size(210.0F, 64.0F);
        this.buttons.button("Remove", Icon.trash, () -> {
            this.tags.remove(this.bound);
            this.tags = null;
            this.bound = null;
            this.hide();
        }).size(210.0F, 64.0F);
        this.shown(() -> {
            ObjectSet<String> set = (ObjectSet)this.tags.get(this.bound, ObjectSet::new);
            ObjectSet.ObjectSetIterator var2 = set.iterator();

            while(var2.hasNext()) {
                String tag = (String)var2.next();
                this.add(new TagElem(tag));
            }

            this.refresh();
        });
        this.hidden(() -> {
            this.refresh();
            this.tags = null;
            this.bound = null;
        });
    }

    private String lastName() {
        int i = 0;
        ObjectMap.Values var2 = this.tags.values().iterator();

        while(var2.hasNext()) {
            ObjectSet<String> set = (ObjectSet)var2.next();
            ObjectSet.ObjectSetIterator var4 = set.iterator();

            while(var4.hasNext()) {
                String tag = (String)var4.next();
                if (tag.startsWith("tag") && Character.isDigit(tag.codePointAt("tag".length()))) {
                    int index = Character.digit(tag.charAt("tag".length()), 10);
                    if (index > i) {
                        i = index;
                    }
                }
            }
        }

        return "tag" + (i + 1);
    }

    private void add(TagElem elem) {
        this.elems.add(elem);
        this.content.add(elem).growX().fillY().pad(4.0F).row();
    }

    private void refresh() {
        if (this.bound != null) {
            ObjectSet<String> set = (ObjectSet)this.tags.get(this.bound, ObjectSet::new);
            set.clear();
            this.elems.each((e) -> set.add(e.tag()));
        }
    }

    public TagsDialog show() {
        if (this.bound != null) {
            super.show();
            return this;
        } else {
            throw new IllegalArgumentException("Use #show(ObjectMap, Object)");
        }
    }

    public TagsDialog show(ObjectMap<Object, ObjectSet<String>> tags, Object bound) {
        this.tags = null;
        this.bound = null;
        this.elems.clear();
        this.content.clear();
        this.tags = tags;
        this.bound = bound;
        return this.show();
    }

    private class TagElem extends Table {
        private String tag;

        private TagElem(String init) {
            this.tag = init;
            this.background(Tex.button);
            this.add("Tag: ").padLeft(4.0F);
            this.field(this.tag, (str) -> {
                this.tag = str;
                TagsDialog.this.refresh();
            }).valid((str) -> {
                if (TagsDialog.this.elems.contains((e) -> e.tag().equals(str))) {
                    return false;
                } else {
                    ObjectMap.Values var2 = TagsDialog.this.tags.values().iterator();

                    while(var2.hasNext()) {
                        ObjectSet<String> set = (ObjectSet)var2.next();
                        if (set.contains(str)) {
                            return false;
                        }
                    }

                    return true;
                }
            }).growX().padRight(8.0F);
            this.button(Icon.trash, Styles.emptyi, this::remove).padRight(8.0F);
        }

        private String tag() {
            return this.tag == null ? "" : this.tag;
        }

        public boolean remove() {
            Cell cell = TagsDialog.this.content.getCell(this);
            boolean succeed = super.remove();
            if (succeed && cell != null) {
                TagsDialog.this.elems.remove(this);
                TagsDialog.this.content.getCells().remove(cell);
                TagsDialog.this.content.invalidate();
            }

            TagsDialog.this.refresh();
            return succeed;
        }
    }
}
