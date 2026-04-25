package unity.entities;

import arc.Events;
import arc.math.Mathf;
import arc.struct.IntMap;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.content.Liquids;
import mindustry.entities.Puddles;
import mindustry.game.EventType;
import mindustry.game.EventType.Trigger;
import mindustry.gen.Building;
import mindustry.gen.Entityc;
import mindustry.gen.Unit;
import unity.entities.effects.VapourizeEffectState;

public class ExtraEffect {
    private static final Seq<BuildQueue> vapourizeQueue = new Seq(512);
    private static final IntMap<BuildQueue> buildQMap = new IntMap();
    private static final IntMap<VapourizeEffectState> vapourizeMap = new IntMap();

    public static void addMoltenBlock(Building build) {
        BuildQueue tmp = (BuildQueue)buildQMap.get(build.id);
        if (tmp == null) {
            tmp = new BuildQueue(build);
            vapourizeQueue.add(tmp);
            buildQMap.put(build.id, tmp);
        } else {
            tmp.time = 14.99F;
        }

    }

    public static void createEvaporation(float x, float y, Unit host, Entityc influence) {
        createEvaporation(x, y, 0.001F, host, influence);
    }

    public static void createEvaporation(float x, float y, float strength, Unit host, Entityc influence) {
        if (host != null && influence != null) {
            VapourizeEffectState tmp = (VapourizeEffectState)vapourizeMap.get(host.id);
            if (tmp == null) {
                tmp = new VapourizeEffectState(x, y, host, influence);
                vapourizeMap.put(host.id, tmp);
                tmp.add();
            } else {
                tmp.extraAlpha = Mathf.clamp(strength * Time.delta + tmp.extraAlpha);
                tmp.time = Mathf.lerpDelta(tmp.time, tmp.lifetime / 2.0F, 0.3F);
            }

        }
    }

    public static void removeEvaporation(int id) {
        vapourizeMap.remove(id);
    }

    static {
        Events.on(EventType.WorldLoadEvent.class, (e) -> {
            vapourizeQueue.clear();
            buildQMap.clear();
            vapourizeMap.clear();
        });
        Events.run(Trigger.update, () -> {
            vapourizeQueue.each((buildq) -> {
                Building temp = buildq.build;
                if (!temp.isValid()) {
                    int size = temp.block.size;
                    Puddles.deposit(temp.tile, Liquids.slag, (float)(size * size * 2 + 6));
                }

                buildq.time -= Time.delta;
            });
            vapourizeQueue.removeAll((buildq) -> {
                boolean b = buildq.build.dead || buildq.time <= 0.0F;
                if (b) {
                    buildQMap.remove(buildq.build.id);
                }

                return b;
            });
        });
    }

    static class BuildQueue {
        Building build;
        float time;

        public BuildQueue(Building build, float time) {
            this.build = build;
            this.time = time;
        }

        public BuildQueue(Building build) {
            this(build, 14.99F);
        }
    }
}
