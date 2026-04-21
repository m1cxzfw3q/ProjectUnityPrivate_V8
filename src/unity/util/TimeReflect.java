package unity.util;

import arc.struct.Seq;
import arc.util.Log;
import arc.util.Time;
import arc.util.pooling.Pools;
import java.lang.reflect.Field;
import unity.Unity;

public class TimeReflect {
    static Field runs;
    static Field delay;
    static Field finish;
    static Seq<Time.DelayRun> trueRuns;
    static Seq<Time.DelayRun> removes = new Seq();

    public static void init() {
        runs = ReflectUtils.findField(Time.class, "runs", true);
        trueRuns = (Seq)ReflectUtils.getField((Object)null, runs);
        delay = ReflectUtils.findField(Time.DelayRun.class, "delay", true);
        finish = ReflectUtils.findField(Time.DelayRun.class, "finish", true);
    }

    public static void swapRuns(Seq<Time.DelayRun> newRuns) {
        try {
            runs.set((Object)null, newRuns);
        } catch (Exception e) {
            Unity.print(new Object[]{e});
        }

    }

    public static void resetRuns() {
        try {
            runs.set((Object)null, trueRuns);
        } catch (Exception e) {
            Unity.print(new Object[]{e});
        }

    }

    public static void updateDelays(Seq<Time.DelayRun> runSeq) {
        removes.clear();

        for(Time.DelayRun r : runSeq) {
            updateDelay(r);
        }

        runSeq.removeAll(removes);
    }

    static void updateDelay(Time.DelayRun run) {
        try {
            float time = delay.getFloat(run);
            time -= Time.delta;
            if (time <= 0.0F) {
                Runnable r = (Runnable)ReflectUtils.getField(run, finish);
                r.run();
                removes.add(run);
                Pools.free(run);
            } else {
                delay.setFloat(run, time);
            }
        } catch (Exception e) {
            Log.err(e);
        }

    }
}
