package unity.ui;

import arc.input.KeyCode;
import arc.math.geom.Vec2;
import arc.scene.Element;
import arc.scene.event.InputEvent;
import arc.scene.event.InputListener;
import arc.util.Tmp;

public class MoveDragListener extends InputListener {
    private final Element element;
    private float lastx;
    private float lasty;

    public MoveDragListener(Element element) {
        this.element = element;
    }

    public boolean touchDown(InputEvent event, float x, float y, int pointer, KeyCode button) {
        Vec2 v = this.element.localToStageCoordinates(Tmp.v1.set(x, y));
        this.lastx = v.x;
        this.lasty = v.y;
        this.element.toFront();
        return true;
    }

    public void touchDragged(InputEvent event, float x, float y, int pointer) {
        Vec2 v = this.element.localToStageCoordinates(Tmp.v1.set(x, y));
        this.element.moveBy(v.x - this.lastx, v.y - this.lasty);
        this.lastx = v.x;
        this.lasty = v.y;
    }
}
