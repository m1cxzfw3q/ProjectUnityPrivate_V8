package unity.async;

import arc.func.Cons;
import arc.math.geom.QuadTree;
import arc.math.geom.Rect;
import arc.struct.Seq;
import arc.util.Nullable;
import arc.util.TaskQueue;
import java.util.Objects;
import mindustry.Vars;
import mindustry.async.AsyncProcess;
import mindustry.core.World;
import mindustry.gen.Groups;
import unity.gen.Light;
import unity.gen.LightHoldc;

public class LightProcess implements AsyncProcess {
    protected TaskQueue queue = new TaskQueue();
    public final Seq<Light> all = new Seq(Light.class);
    public final QuadTree<Light> quad = new QuadTree(new Rect());
    protected volatile boolean processing = false;
    protected volatile boolean end = false;
    protected volatile boolean ready = false;

    public void begin() {
        if (this.end) {
            this.queue.run();
            this.end = false;
        }

        this.all.size = 0;
        Groups.draw.each((e) -> e instanceof Light, (e) -> {
            Light l = (Light)e;
            l.snap();
            this.all.add(l);
        });
    }

    public void init() {
        this.queue.clear();
        this.quad.clear();
        this.quad.botLeft = null;
        this.quad.botRight = null;
        this.quad.topLeft = null;
        this.quad.topRight = null;
        this.quad.leaf = true;
        this.quad.bounds.set(-250.0F, -250.0F, (float)Vars.world.unitWidth() + 500.0F, (float)Vars.world.unitHeight() + 500.0F);
        this.ready = true;
    }

    public void reset() {
        this.queue.clear();
        this.quad.clear();
        this.ready = false;
    }

    public void process() {
        this.processing = true;
        int size = this.all.size;

        for(int i = 0; i < size; ++i) {
            ((Light[])this.all.items)[i].cast();
        }

        this.end = true;
        this.processing = false;
    }

    public boolean shouldProcess() {
        return !this.processing && !Vars.state.isPaused();
    }

    public void quad(Cons<QuadTree<Light>> cons) {
        synchronized(this.quad) {
            cons.get(this.quad);
        }
    }

    public void queuePoint(Light light, @Nullable LightHoldc.LightHoldBuildc hold) {
        if (hold == null) {
            this.queue.post(() -> {
                light.clearChildren();
                LightHoldc.LightHoldBuildc pointed = light.pointed;
                if (pointed != null) {
                    pointed.remove(light);
                    light.pointed = null;
                }

            });
        } else {
            this.queue.post(() -> {
                LightHoldc.LightHoldBuildc pointed = light.pointed;
                if (light.rotationChanged || pointed != hold || hold.needsReinteract()) {
                    light.clearChildren();
                    if (pointed != null) {
                        pointed.remove(light);
                    }

                    light.pointed = hold;
                    hold.add(light, World.toTile(light.endX()), World.toTile(light.endY()));
                    hold.interact(light);
                    light.rotationChanged = false;
                }

            });
        }

    }

    public void queueAdd(Light light) {
        if (this.ready) {
            TaskQueue var10000 = this.queue;
            Objects.requireNonNull(light);
            var10000.post(light::add);
        } else {
            light.add();
        }

    }

    public void queueRemove(Light light) {
        if (this.ready) {
            this.queue.post(() -> {
                if (light.pointed != null) {
                    light.pointed.remove(light);
                }

                light.remove();
            });
        } else {
            light.remove();
        }

    }
}
