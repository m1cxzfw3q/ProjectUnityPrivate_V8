package unity.editor;

import arc.Events;
import mindustry.Vars;
import mindustry.core.GameState.State;
import mindustry.ctype.ContentType;
import mindustry.ctype.MappableContent;
import mindustry.game.EventType;
import mindustry.game.EventType.Trigger;
import unity.map.ScriptedSector;
import unity.mod.Triggers;

public abstract class EditorListener {
    private ScriptedSector sector;
    private boolean attached;

    public EditorListener() {
        Events.on(EventType.ClientLoadEvent.class, (c) -> this.registerEvents());
    }

    protected void registerEvents() {
        Triggers.listen(Trigger.update, () -> this.valid(this::update));
        Triggers.listen(Trigger.drawOver, () -> this.valid(this::draw));
        Events.on(EventType.StateChangeEvent.class, (e) -> {
            if (e.from == State.menu && e.to == State.playing && Vars.state.isEditor()) {
                MappableContent c = Vars.content.getByName(ContentType.sector, "unity-" + (String)Vars.editor.tags.get("name"));
                if (c instanceof ScriptedSector) {
                    ScriptedSector sect = (ScriptedSector)c;
                    this.attached = true;
                    this.sector = sect;
                    this.begin();
                }
            } else if (this.attached && e.to == State.menu) {
                this.end();
                this.attached = false;
                this.sector = null;
            }

        });
    }

    public void begin() {
    }

    public void end() {
    }

    public void update() {
    }

    public void draw() {
    }

    public void valid(Runnable run) {
        if (this.attached && Vars.state.isEditor()) {
            run.run();
        }

    }

    public ScriptedSector sector() {
        return this.sector;
    }
}
