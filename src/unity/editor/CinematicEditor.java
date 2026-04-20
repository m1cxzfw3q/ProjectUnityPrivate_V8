package unity.editor;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.input.KeyCode;
import arc.math.geom.Vec2;
import arc.math.geom.Vec3;
import arc.scene.ui.Dialog;
import arc.struct.ObjectMap;
import arc.struct.ObjectSet;
import arc.struct.Seq;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.graphics.Pal;
import mindustry.io.JsonIO;
import mindustry.world.Tile;
import unity.Unity;
import unity.map.cinematic.Cinematics;
import unity.map.cinematic.StoryNode;

public class CinematicEditor extends EditorListener {
    public final Seq<StoryNode> nodes = new Seq();
    public final ObjectMap<Object, ObjectSet<String>> tags = new ObjectMap();

    public void update() {
        if (!Unity.cinematicDialog.isShown() && Core.input.keyTap(KeyCode.f4)) {
            Unity.cinematicDialog.show();
        }

        if (Core.scene.getScrollFocus() == null && Core.input.alt() && Core.input.keyTap(KeyCode.mouseRight)) {
            Vec2 pos = Core.input.mouseWorld();
            final Tile tile = Vars.world.tileWorld(pos.x, pos.y);
            if (tile != null) {
                if (tile.build != null) {
                    (new Dialog("") {
                        {
                            this.clear();
                            this.addCloseButton();
                            this.buttons.row().button("@building", () -> {
                                CinematicEditor.this.showTag(tile.build);
                                this.hide();
                            }).size(210.0F, 64.0F);
                            this.buttons.row().button("@tile", () -> {
                                CinematicEditor.this.showTag(tile);
                                this.hide();
                            }).size(210.0F, 64.0F);
                            this.add(this.buttons).grow();
                        }
                    }).show();
                } else {
                    this.showTag(tile);
                }
            }
        }

    }

    public void draw() {
        Draw.draw(35.0F, () -> {
            ObjectMap.Keys var1 = this.tags.keys().iterator();

            while(var1.hasNext()) {
                Object obj = var1.next();
                Vec3 data = Tmp.v31;
                if (obj instanceof Building) {
                    Building b = (Building)obj;
                    data.set(b.getX(), b.getY(), (float)(b.block.size * 8) / 2.0F);
                } else {
                    if (!(obj instanceof Tile)) {
                        continue;
                    }

                    Tile tile = (Tile)obj;
                    data.set(tile.worldx(), tile.worldy(), (float)(tile.floor().size * 8) / 2.0F);
                }

                Lines.stroke(1.0F, Pal.place);
                Lines.square(data.x, data.y, data.z);
            }

        });
    }

    protected void showTag(Object target) {
        if (!this.tags.containsKey(target)) {
            this.tags.put(target, new ObjectSet());
        }

        Unity.tagsDialog.show(this.tags, target);
    }

    public void begin() {
        this.nodes.set(this.sector().cinematic.nodes);
        this.sector().cinematic.nodes.clear();
        this.sector().initTags();
        this.tags.clear();
        this.tags.putAll(this.sector().cinematic.objectToTag);
        this.sector().cinematic.clearTags();
        Unity.cinematicDialog.begin();
    }

    public void end() {
        try {
            this.apply();
        } catch (Exception e) {
            Vars.ui.showException("Failed to parse story nodes", e);
        } finally {
            Unity.cinematicDialog.end();
            this.nodes.each((node) -> node.elem = null);
            this.nodes.clear();
        }

    }

    public void apply() throws Exception {
        Cinematics core = this.sector().cinematic;
        core.nodes.clear();
        core.clearTags();
        core.setNodes(this.nodes);
        core.setTags(this.tags);
        Vars.editor.tags.put("nodes", JsonIO.json.toJson(core.nodes, Seq.class, StoryNode.class));
        Vars.editor.tags.put("object-tags", JsonIO.json.toJson(core.saveTags(), Seq.class, String.class));
    }
}
