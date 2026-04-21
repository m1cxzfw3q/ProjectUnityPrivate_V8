package unity.ui.dialogs.canvas;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.input.KeyCode;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.scene.Element;
import arc.scene.event.ClickListener;
import arc.scene.event.InputEvent;
import arc.scene.event.InputListener;
import arc.scene.event.Touchable;
import arc.scene.style.Drawable;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.ImageButton;
import arc.scene.ui.TextField;
import arc.scene.ui.layout.Cell;
import arc.scene.ui.layout.Table;
import arc.scene.ui.layout.WidgetGroup;
import arc.util.Tmp;
import java.util.Iterator;
import mindustry.Vars;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.graphics.Pal;
import mindustry.ui.Fonts;
import mindustry.ui.Styles;
import unity.Unity;
import unity.map.cinematic.StoryNode;
import unity.ui.MoveDragListener;

public class CinematicCanvas extends WidgetGroup {
    private static final Color base;
    private static final Color connection;
    private static TextureRegionDrawable acceptor;
    private static TextureRegionDrawable distributor;
    private ImageButton selected;
    private ImageButton entered;

    public CinematicCanvas() {
        if (acceptor == null) {
            acceptor = new TextureRegionDrawable(Core.atlas.find("unity-cinematic-node-acceptor"));
        }

        if (distributor == null) {
            distributor = new TextureRegionDrawable(Core.atlas.find("unity-cinematic-node-distributor"));
        }

    }

    public void draw() {
        super.draw();
        Draw.alpha(this.parentAlpha);
        this.getChildren().each((e) -> e instanceof NodeElem, (rec$) -> ((NodeElem)rec$).drawConnection());
        if (this.selected != null) {
            Vec2 dest = this.selected.localToStageCoordinates(Tmp.v1.set(this.selected.getWidth() / 2.0F, this.selected.getHeight() / 2.0F));
            Vec2 mouse = Core.input.mouse();
            this.drawCurve(dest.x, dest.y, mouse.x, mouse.y);
        }

    }

    private void drawCurve(float x, float y, float x2, float y2) {
        Lines.stroke(4.0F, Tmp.c1.set(connection).a(connection.a * this.parentAlpha));
        float dist = Math.abs(x - x2) / 2.0F;
        Lines.curve(x, y, x + dist, y, x2 - dist, y2, x2, y2, Math.max(3, (int)(Mathf.dst(x, y, x2, y2) / 5.0F)));
        Draw.reset();
    }

    public void add(StoryNode node) {
        NodeElem elem = new NodeElem(node);
        Vec2 pos = this.localToStageCoordinates(node.position);
        elem.setPosition(pos.x, pos.y, 1);
        this.addChild(elem);
        elem.pack();
    }

    static {
        base = Pal.accent;
        connection = Pal.place;
    }

    public class NodeElem extends Table {
        public final StoryNode node;
        private ImageButton acceptCont;
        private ImageButton distCont;

        private NodeElem(StoryNode node) {
            this.node = node;
            node.elem = this;
            this.setClip(false);
            this.update(() -> node.position.set(this.parent.stageToLocalCoordinates(Tmp.v1.set(this.x, this.y))));
            this.background(Tex.whitePane);
            this.setColor(CinematicCanvas.connection);
            this.margin(0.0F);
            this.connection(true);
            this.table(Tex.whiteui, (t) -> {
                t.setColor(CinematicCanvas.base);
                t.margin(8.0F);
                t.touchable = Touchable.enabled;
                t.add("Name: ").style(Styles.outlineLabel);
                TextField field = (TextField)t.field(node.name, Styles.defaultField, (str) -> node.name = str).padRight(8.0F).get();
                field.setValidator((str) -> !Unity.cinematicEditor.nodes.contains((n) -> n.name.equals(str)));
                field.getStyle().font = Fonts.outline;
                t.add().growX();

                class DragButton extends ImageButton {
                    DragButton(Drawable drawable, ImageButton.ImageButtonStyle style, final Runnable listener) {
                        super(drawable, style);
                        this.resizeImage(drawable.imageSize());
                        this.addListener(new ClickListener() {
                            {
                                this.setButton(KeyCode.mouseLeft);
                            }

                            public void clicked(InputEvent event, float x, float y) {
                                if (listener != null && !DragButton.this.isDisabled()) {
                                    listener.run();
                                }

                            }

                            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                                if (pointer == this.pressedPointer && !this.cancelled) {
                                    this.cancel();
                                }

                            }
                        });
                    }
                }

                t.add(new DragButton(Icon.book, Styles.logici, () -> Unity.jsDictDialog.show(node.scripts))).padRight(4.0F);
                t.add(new DragButton(Icon.pencil, Styles.logici, () -> Unity.objectivesDialog.show(node))).padRight(4.0F);
                t.add(new DragButton(Icon.trash, Styles.logici, () -> Vars.ui.showConfirm("@root.cinematic.node-delete.title", "@root.cinematic.node-delete.content", () -> {
                    this.remove();
                    Iterator<StoryNode> it = Unity.cinematicEditor.nodes.iterator();

                    while(it.hasNext()) {
                        StoryNode e = (StoryNode)it.next();
                        e.children.remove(node);
                        if (e == node) {
                            it.remove();
                        }
                    }

                }))).padRight(4.0F);
                t.addListener(new MoveDragListener(this));
            }).growX().fillY();
            this.connection(false);
        }

        private void connection(boolean accept) {
            float size = 30.0F;
            float pad = size / 2.0F - 3.0F;
            Cell<ImageButton> c = this.button(accept ? CinematicCanvas.acceptor : CinematicCanvas.distributor, Styles.colori, () -> {
            }).size(size);
            final ImageButton button = (ImageButton)c.get();
            if (accept) {
                this.acceptCont = button;
                c.padLeft(-pad);
            } else {
                this.distCont = button;
                c.padRight(-pad);
            }

            button.userObject = this;
            button.getStyle().imageUpColor = CinematicCanvas.connection;
            if (!accept) {
                button.addListener(new InputListener() {
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, KeyCode code) {
                        if (CinematicCanvas.this.selected == null) {
                            CinematicCanvas.this.selected = button;
                        }

                        return true;
                    }

                    public void touchUp(InputEvent event, float x, float y, int pointer, KeyCode code) {
                        ImageButton e = CinematicCanvas.this.entered;
                        if (e != null) {
                            Object var8 = e.userObject;
                            if (var8 instanceof NodeElem) {
                                NodeElem elem = (NodeElem)var8;
                                if (elem != NodeElem.this && e == elem.acceptCont) {
                                    NodeElem.this.node.child(elem.node);
                                }
                            }
                        }

                        if (CinematicCanvas.this.selected == button) {
                            CinematicCanvas.this.selected = null;
                        }

                    }
                });
            } else {
                button.addListener(new InputListener() {
                    public void enter(InputEvent event, float x, float y, int pointer, Element from) {
                        CinematicCanvas.this.entered = button;
                    }

                    public void exit(InputEvent event, float x, float y, int pointer, Element to) {
                        if (CinematicCanvas.this.entered == button) {
                            CinematicCanvas.this.entered = null;
                        }

                    }
                });
            }

        }

        private void drawConnection() {
            for(StoryNode child : this.node.children) {
                NodeElem elem = child.elem;
                if (elem != null) {
                    Vec2 from = this.distCont.localToStageCoordinates(Tmp.v1.set(this.distCont.getWidth() / 2.0F, this.distCont.getHeight() / 2.0F));
                    Vec2 to = elem.acceptCont.localToStageCoordinates(Tmp.v2.set(elem.acceptCont.getWidth() / 2.0F, elem.acceptCont.getHeight() / 2.0F));
                    CinematicCanvas.this.drawCurve(from.x, from.y, to.x, to.y);
                }
            }

        }
    }
}
