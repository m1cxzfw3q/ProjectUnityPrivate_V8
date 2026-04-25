package unity.world;

import arc.func.Cons2;
import unity.gen.LightHoldc;

public class LightAcceptorType {
    public int x;
    public int y;
    public int width;
    public int height;
    public float required;
    public Cons2<LightHoldc.LightHoldBuildc, LightAcceptor> update;
    public Cons2<LightHoldc.LightHoldBuildc, LightAcceptor> draw;

    public LightAcceptorType() {
        this(0, 0, 1.0F);
    }

    public LightAcceptorType(int x, int y, float required) {
        this(x, y, 1, 1, required);
    }

    public LightAcceptorType(int x, int y, int width, int height, float required) {
        this.update = (e, s) -> {
        };
        this.draw = (e, s) -> {
        };
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.required = required;
    }

    public <T extends LightHoldc.LightHoldBuildc, V extends LightAcceptor> LightAcceptorType update(Cons2<T, V> update) {
        this.update = update;
        return this;
    }

    public <T extends LightHoldc.LightHoldBuildc, V extends LightAcceptor> LightAcceptorType draw(Cons2<T, V> draw) {
        this.draw = draw;
        return this;
    }

    public LightAcceptor create(LightHoldc.LightHoldBuildc hold) {
        return new LightAcceptor(this, hold);
    }
}
