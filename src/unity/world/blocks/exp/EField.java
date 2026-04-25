package unity.world.blocks.exp;

import arc.Core;
import arc.func.Boolc;
import arc.func.Cons;
import arc.func.Floatc;
import arc.func.Func;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.util.Nullable;
import arc.util.Strings;
import mindustry.world.meta.Stat;
import unity.graphics.UnityPal;
import unity.ui.Graph;

public abstract class EField<T> {
    public static final float graphWidth = 330.0F;
    public static final float graphHeight = 160.0F;
    @Nullable
    public Stat stat;
    public boolean hasTable = true;
    public boolean formatAll = true;

    public EField(Stat stat) {
        this.stat = stat;
    }

    public abstract T fromLevel(int var1);

    public abstract void setLevel(int var1);

    public void buildTable(Table table, int end) {
    }

    public EField<T> formatAll(boolean f) {
        this.formatAll = f;
        return this;
    }

    public String toString() {
        return "[#84ff00]NULL[]";
    }

    public static class ELinear extends EField<Float> {
        public Floatc set;
        public float start;
        public float scale;
        public Func<Float, String> format;

        public ELinear(Floatc set, float start, float scale, Stat stat, Func<Float, String> format) {
            super(stat);
            this.start = start;
            this.scale = scale;
            this.set = set;
            this.format = format;
        }

        public ELinear(Floatc set, float start, float scale, Stat stat) {
            this(set, start, scale, stat, (f) -> Strings.autoFixed(f, 1));
        }

        public Float fromLevel(int l) {
            return this.start + (float)l * this.scale;
        }

        public void setLevel(int l) {
            this.set.get(this.fromLevel(l));
        }

        public String toString() {
            return Core.bundle.format("field.linear", new Object[]{this.format.get(this.start), this.formatAll ? this.format.get(this.scale) : Strings.autoFixed(this.scale, 2)});
        }

        public void buildTable(Table table, int end) {
            table.left();
            Graph g = new Graph(this::fromLevel, end, UnityPal.exp);
            table.add(g).size(330.0F, 160.0F).left();
            table.row();
            table.label(() -> g.lastMouseOver ? Core.bundle.format("ui.graph.label", new Object[]{g.lastMouseStep, this.formatAll ? this.format.get(g.mouseValue()) : Strings.autoFixed(g.mouseValue(), 2)}) : Core.bundle.get("ui.graph.hover"));
        }
    }

    public static class ELinearCap extends ELinear {
        public int cap;

        public ELinearCap(Floatc set, float start, float scale, int cap, Stat stat, Func<Float, String> format) {
            super(set, start, scale, stat, format);
            this.cap = cap;
        }

        public ELinearCap(Floatc set, float start, float scale, int cap, Stat stat) {
            this(set, start, scale, cap, stat, (f) -> Strings.autoFixed(f, 1));
        }

        public Float fromLevel(int l) {
            return this.start + (float)Math.min(l, this.cap) * this.scale;
        }

        public void setLevel(int l) {
            this.set.get(this.fromLevel(l));
        }

        public String toString() {
            return Core.bundle.format("field.linearcap", new Object[]{this.format.get(this.start), this.formatAll ? this.format.get(this.scale) : Strings.autoFixed(this.scale, 2), this.cap});
        }
    }

    public static class EExpo extends EField<Float> {
        public Floatc set;
        public float start;
        public float scale;
        public Func<Float, String> format;

        public EExpo(Floatc set, float start, float scale, Stat stat, Func<Float, String> format) {
            super(stat);
            this.start = start;
            this.scale = scale;
            this.set = set;
            this.format = format;
        }

        public EExpo(Floatc set, float start, float scale, Stat stat) {
            this(set, start, scale, stat, (f) -> Strings.autoFixed(f, 1));
        }

        public Float fromLevel(int l) {
            return this.start * Mathf.pow(this.scale, (float)l);
        }

        public void setLevel(int l) {
            this.set.get(this.fromLevel(l));
        }

        public String toString() {
            return Core.bundle.format("field.exponent", new Object[]{this.format.get(this.start), this.scale});
        }

        public void buildTable(Table table, int end) {
            table.left();
            Graph g = new Graph(this::fromLevel, end, UnityPal.exp);
            table.add(g).size(330.0F, 160.0F).left();
            table.row();
            table.label(() -> g.lastMouseOver ? Core.bundle.format("ui.graph.label", new Object[]{g.lastMouseStep, this.formatAll ? this.format.get(g.mouseValue()) : Strings.autoFixed(g.mouseValue(), 2)}) : Core.bundle.get("ui.graph.hover"));
        }
    }

    public static class EExpoZero extends EExpo {
        public boolean clamp;

        public EExpoZero(Floatc set, float start, float scale, boolean clamp, Stat stat, Func<Float, String> format) {
            super(set, start, scale, stat, format);
            this.clamp = clamp;
        }

        public EExpoZero(Floatc set, float start, float scale, Stat stat) {
            this(set, start, scale, false, stat, (f) -> Strings.autoFixed(f, 1));
        }

        public Float fromLevel(int l) {
            return this.clamp ? Mathf.clamp(super.fromLevel(l) - this.start) : super.fromLevel(l) - this.start;
        }

        public String toString() {
            return Core.bundle.format("field.exponentzero", new Object[]{this.format.get(this.start), this.scale});
        }
    }

    public static class ERational extends EField<Float> {
        public Floatc set;
        public float start;
        public float end;
        public float axis;
        public float a;
        public Func<Float, String> format;

        public ERational(Floatc set, float start, float end, float axis, Stat stat, Func<Float, String> format) {
            super(stat);
            this.start = start;
            this.end = end;
            if (axis == 0.0F) {
                throw new ArithmeticException("Vertical asymptote cannot be x = 0");
            } else {
                this.axis = axis;
                this.a = (end - start) * axis;
                this.set = set;
                this.format = format;
            }
        }

        public ERational(Floatc set, float start, float end, Stat stat, Func<Float, String> format) {
            this(set, start, end, -1.0F, stat, format);
        }

        public ERational(Floatc set, float start, float end, Stat stat) {
            this(set, start, end, stat, (f) -> Strings.autoFixed(f, 1));
        }

        public Float fromLevel(int l) {
            return this.a / ((float)l - this.axis) + this.end;
        }

        public void setLevel(int l) {
            this.set.get(this.fromLevel(l));
        }

        public String toString() {
            return Core.bundle.format("field.rational", new Object[]{this.format.get(this.start), this.formatAll ? this.format.get(this.end) : Strings.autoFixed(this.end, 2)});
        }

        public void buildTable(Table table, int end) {
            table.left();
            Graph g = new Graph(this::fromLevel, end, UnityPal.exp);
            table.add(g).size(330.0F, 160.0F).left();
            table.row();
            table.label(() -> g.lastMouseOver ? Core.bundle.format("ui.graph.label", new Object[]{g.lastMouseStep, this.formatAll ? this.format.get(g.mouseValue()) : Strings.autoFixed(g.mouseValue(), 2)}) : Core.bundle.get("ui.graph.hover"));
        }
    }

    public static class EBool extends EField<Boolean> {
        public Boolc set;
        public boolean start;
        public int thresh;

        public EBool(Boolc set, boolean start, int thresh, Stat stat) {
            super(stat);
            this.start = start;
            this.thresh = thresh;
            this.set = set;
            this.hasTable = false;
        }

        public Boolean fromLevel(int l) {
            return l >= this.thresh != this.start;
        }

        public void setLevel(int l) {
            this.set.get(this.fromLevel(l));
        }

        public String toString() {
            return Core.bundle.format("field.bool", new Object[]{this.bs(this.start), this.bs(!this.start), this.thresh});
        }

        public String bs(boolean b) {
            return Core.bundle.get(b ? "yes" : "no");
        }
    }

    public static class EList<T> extends EField<T> {
        public Cons<T> set;
        public T[] list;
        public String unit;

        public EList(Cons<T> set, T[] list, Stat stat, String unit) {
            super(stat);
            this.set = set;
            this.list = list;
            this.unit = unit;
            this.hasTable = false;
        }

        public EList(Cons<T> set, T[] list, Stat stat) {
            this(set, list, stat, "");
        }

        public T fromLevel(int l) {
            return (T)this.list[Math.min(this.list.length - 1, l)];
        }

        public void setLevel(int l) {
            this.set.get(this.fromLevel(l));
        }

        public String toString() {
            return Core.bundle.format("field.list", new Object[]{this.list[0], this.list[this.list.length - 1], this.unit});
        }
    }
}
