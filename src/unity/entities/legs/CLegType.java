package unity.entities.legs;

import arc.Core;
import arc.func.Cons;
import arc.func.Prov;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;

public class CLegType<T extends CLeg> {
    public float targetX;
    public float targetY;
    public float x;
    public float y;
    public float legSplashDamage = 0.0F;
    public float legSplashRange = 5.0F;
    public float legTrns = 1.0F;
    public boolean flipped = false;
    public Prov<T> prov;
    public Runnable loadPost;
    public String name;
    public TextureRegion footRegion;

    public CLegType(Prov<T> prov, String name) {
        this.prov = prov;
        this.name = name;
    }

    public T create() {
        T t = (T)(this.prov.get());
        t.type = this;
        return t;
    }

    public float length() {
        return Mathf.dst(this.x, this.y, this.targetX, this.targetY);
    }

    public void load() {
        this.footRegion = Core.atlas.find(this.name + "-foot");
    }

    @SafeVarargs
    public static ClegGroupType createGroup(String name, Cons<ClegGroupType> cons, CLegType<? extends CLeg>... legs) {
        ClegGroupType g = new ClegGroupType();
        g.name = name;
        g.legs = legs;
        cons.get(g);
        return g;
    }

    public static class ClegGroupType {
        public float moveSpacing = 1.0F;
        public float legSpeed = 0.1F;
        public float maxStretch = 1.7F;
        public float baseRotateSpeed = 5.0F;
        public int legGroupSize = 2;
        public CLegType<? extends CLeg>[] legs;
        public TextureRegion baseRegion;
        public String name;

        public CLegGroup create() {
            CLegGroup g = new CLegGroup();
            g.init(this);
            return g;
        }

        public void load() {
            this.baseRegion = Core.atlas.find(this.name + "-base");

            for(CLegType<? extends CLeg> leg : this.legs) {
                leg.load();
                if (leg.loadPost != null) {
                    leg.loadPost.run();
                }
            }

        }
    }
}
