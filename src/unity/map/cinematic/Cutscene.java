package unity.map.cinematic;

import arc.Core;
import arc.func.Cons;
import arc.func.Floatf;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.scene.Action;
import arc.scene.Element;
import arc.scene.actions.Actions;
import arc.scene.ui.layout.Scl;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;

public class Cutscene {
    public static Cutscene root;
    public static float stripe = Scl.scl(56.0F);
    private float progress;
    private float zoomProgress;
    private final boolean isRoot;
    private boolean acting;
    protected Floatf<Cutscene> zoom;
    protected Cutscene parent;
    protected Cutscene child;
    private Cons<Cutscene> start;
    private Cons<Cutscene> end;
    private Vec2 endPos;
    private float endTime;
    private float firstZoom;
    private float lastZoom;
    private float startTime;

    public static void init() {
        new Cutscene(true);
        Core.scene.add(new Element() {
            public void act(float delta) {
                this.setZIndex(Vars.ui.hudGroup.getZIndex() + 1);
                Cutscene.root.updateRoot();
            }

            public void draw() {
                Cutscene.root.drawStripes();
            }
        });
    }

    private Cutscene(boolean isRoot) {
        this.zoom = (c) -> Scl.scl(4.0F);
        this.endTime = -1.0F;
        this.firstZoom = -1.0F;
        this.lastZoom = -1.0F;
        this.startTime = -1.0F;
        if (!isRoot) {
            this.isRoot = false;
        } else {
            if (root != null) {
                throw new IllegalArgumentException("Root cutscene already defined!");
            }

            root = this;
            this.isRoot = true;
            Vars.control.input.addLock(this::acting);
        }

    }

    public Cutscene() {
        this(false);
    }

    public <T extends Cutscene> T then(T next) {
        Cutscene target;
        for(target = this; target.child != null; target = target.child) {
        }

        next.parent = target;

        for(Cutscene cur = target; cur != null && cur != root; cur = cur.parent) {
            if (cur == next) {
                throw new IllegalArgumentException("Can't invoke then() to a parent cutscene!");
            }
        }

        target.child = next;
        return next;
    }

    public <T extends Cutscene> T start(Cons<T> start) {
        this.start = start;
        return (T)this;
    }

    public <T extends Cutscene> T end(Cons<T> end) {
        this.end = end;
        return (T)this;
    }

    public <T extends Cutscene> T zoom(Floatf<T> zoom) {
        this.zoom = zoom;
        return (T)this;
    }

    private void drawStripes() {
        Draw.color(Color.black);
        Vec2 pos = Core.scene.screenToStageCoordinates(Tmp.v1.set(0.0F, (float)Core.graphics.getHeight()));
        float x = pos.x;
        float y = pos.y;
        float w = (float)Core.graphics.getWidth();
        float h = (float)Core.graphics.getHeight();
        float thick = stripe * this.progress;
        Fill.quad(x, y, x + w, y, x + w, y - thick, x, y - thick);
        Fill.quad(x, y - h, x + w, y - h, x + w, y - h + thick, x, y - h + thick);
    }

    private void updateRoot() {
        if (!this.isRoot) {
            throw new IllegalStateException();
        } else {
            if (this.child != null) {
                if (Vars.control.input.locked() && !this.acting) {
                    return;
                }

                if (!this.acting) {
                    Vars.ui.hudGroup.actions(new Action[]{Actions.alpha(0.0F, 0.17F)});
                }

                if (this.firstZoom < 0.0F) {
                    this.firstZoom = Vars.renderer.getScale();
                }

                if (this.lastZoom < 0.0F) {
                    this.lastZoom = Vars.renderer.getScale();
                }

                this.acting = true;
                this.endPos = null;
                this.endTime = -1.0F;
                this.progress = Mathf.approachDelta(this.progress, 1.0F, 0.016666668F);
                this.zoomProgress = Mathf.approachDelta(this.zoomProgress, 1.0F, 0.020833334F);
                float targetZoom = this.child.zoom();
                Vars.renderer.setScale(Mathf.lerp(this.lastZoom, targetZoom, this.zoomProgress));
                if (this.child.startTime < 0.0F) {
                    this.child.startTime = Time.time;
                }

                if (this.child.start != null) {
                    this.child.start.get(this.child);
                    this.child.start = null;
                }

                if (this.child.update()) {
                    if (this.child.end != null) {
                        this.child.end.get(this.child);
                        this.child.end = null;
                    }

                    this.lastZoom = targetZoom;
                    this.zoomProgress = 0.0F;
                    this.child = this.child.child;
                }
            } else {
                this.progress = Mathf.approachDelta(this.progress, 0.0F, 0.016666668F);
                this.zoomProgress = Mathf.approachDelta(this.zoomProgress, 0.0F, 0.016666668F);
                if (this.firstZoom > 0.0F) {
                    Vars.renderer.setScale(Mathf.lerp(this.firstZoom, Vars.renderer.getScale(), this.zoomProgress));
                    if (Mathf.equal(this.firstZoom, Vars.renderer.getScale())) {
                        this.firstZoom = -1.0F;
                        this.lastZoom = -1.0F;
                        this.zoomProgress = 0.0F;
                    }
                }

                if (this.acting) {
                    if (this.endPos == null) {
                        this.endPos = new Vec2(Core.camera.position);
                        this.endTime = Time.time;
                    }

                    float endProgress = Time.time - this.endTime;
                    Core.camera.position.set(this.endPos).lerp(Vars.player, Interp.smoother.apply(Mathf.clamp(endProgress / 52.0F)));
                    if (endProgress >= 52.0F) {
                        Vars.ui.hudGroup.actions(new Action[]{Actions.alpha(1.0F, 0.17F)});
                        this.zoomProgress = 0.0F;
                        this.firstZoom = -1.0F;
                        this.lastZoom = -1.0F;
                        this.acting = false;
                    }
                }
            }

        }
    }

    public boolean update() {
        return true;
    }

    public float startTime() {
        return this.startTime;
    }

    public boolean acting() {
        return root.acting;
    }

    public float zoom() {
        return this.zoom.get(this);
    }

    public interface Pos {
        long get();
    }
}
