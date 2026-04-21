package unity.ui.dialogs;

import arc.Core;
import mindustry.graphics.Pal;
import mindustry.ui.dialogs.BaseDialog;
import unity.world.blocks.production.Crucible;

public class CrucibleDialog extends BaseDialog {
    private Crucible.CrucibleBuild build;

    public CrucibleDialog(Crucible.CrucibleBuild build) {
        super("@info.title");
        this.build = build;
        this.shown(() -> Core.app.post(this::setup));
        this.shown(this::setup);
        this.onResize(this::setup);
    }

    void setup() {
        this.cont.clear();
        this.buttons.clear();
        float w = Core.graphics.isPortrait() ? 320.0F : 640.0F;
        this.cont.table((t) -> {
            Runnable set = () -> {
                t.clearChildren();
                t.left();
                t.label(() -> Core.bundle.get("stat.unity.crucible.temp")).color(Pal.accent).growX().row();
                t.add(this.build.crucible().getIconBar()).padTop(4.0F).growX().row();
                t.label(() -> Core.bundle.get("stat.unity.crucible.contents")).color(Pal.accent).growX().row();
                t.add(this.build.crucible().getStackedBars()).padTop(4.0F).growX();
            };
            set.run();
            t.update(() -> set.run());
        }).width(w);
        this.addCloseButton();
    }
}
