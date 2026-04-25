package unity.map;

import arc.Core;
import arc.Events;
import arc.struct.Seq;
import arc.struct.StringMap;
import arc.util.Log;
import arc.util.Time;
import mindustry.Vars;
import mindustry.core.GameState.State;
import mindustry.game.EventType;
import mindustry.io.JsonIO;
import mindustry.type.Planet;
import mindustry.type.SectorPreset;
import unity.map.cinematic.Cinematics;
import unity.map.cinematic.StoryNode;

public class ScriptedSector extends SectorPreset {
    public final Cinematics cinematic = new Cinematics(this::valid);

    public ScriptedSector(String name, Planet planet, int sector) {
        super(name, planet, sector);
        Events.on(EventType.SaveWriteEvent.class, (e) -> {
            if (this.cinematic.bound() && this.valid()) {
                StringMap map = new StringMap();
                this.cinematic.save(map);
                Vars.state.rules.tags.put(name + "-cinematic", JsonIO.json.toJson(map, StringMap.class, String.class));
            }

        });
        Events.on(EventType.SaveLoadEvent.class, (e) -> this.cinematic.load((StringMap)JsonIO.json.fromJson(StringMap.class, String.class, (String)Vars.state.rules.tags.get(name + "-cinematic", JsonIO.json.toJson(StringMap.of(new Object[]{"object-tags", this.generator.map.tags.get("object-tags", "[]")}), StringMap.class, String.class)))));
        Events.on(EventType.StateChangeEvent.class, (e) -> {
            if (!this.cinematic.bound() && e.to == State.playing && this.valid()) {
                this.cinematic.bind();
            }

        });
    }

    public boolean valid() {
        return Vars.state.hasSector() ? Vars.state.getSector().id == this.sector.id : Vars.state.map != null && Vars.state.map.mod != null && Vars.state.map.mod.name.equals("unity") && (Vars.state.map.name().equals(this.generator.map.name()) || Vars.state.map.name().equals(this.name));
    }

    public void init() {
        super.init();
        Core.app.post(() -> {
            try {
                this.initNodes();
            } catch (Throwable t) {
                if (Vars.headless) {
                    Log.err(t);
                } else {
                    Events.on(EventType.ClientLoadEvent.class, (e) -> Time.runTask(6.0F, () -> Vars.ui.showException("Failed to load cinematic metadata of '" + this.localizedName + "'", t)));
                }
            }

        });
    }

    public void initNodes() {
        this.cinematic.setNodes((Seq)JsonIO.json.fromJson(Seq.class, StoryNode.class, (String)this.generator.map.tags.get("nodes", "[]")));
    }

    public void initTags() {
        this.cinematic.clearTags();
        this.cinematic.loadTags((Seq)JsonIO.json.fromJson(Seq.class, String.class, (String)this.generator.map.tags.get("object-tags", "[]")));
    }
}
