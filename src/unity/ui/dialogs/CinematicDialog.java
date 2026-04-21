package unity.ui.dialogs;

import arc.Core;
import arc.struct.Seq;
import arc.util.Tmp;
import java.util.Objects;
import mindustry.Vars;
import mindustry.gen.Icon;
import mindustry.ui.dialogs.BaseDialog;
import unity.Unity;
import unity.map.cinematic.StoryNode;
import unity.ui.dialogs.canvas.CinematicCanvas;

public class CinematicDialog extends BaseDialog {
    private final CinematicCanvas canvas;

    public CinematicDialog() {
        super("@root.cinematic");
        this.clearChildren();
        this.add(this.titleTable).growX().fillY().row();
        this.add(this.canvas = new CinematicCanvas()).grow().row();
        this.addCloseButton();
        this.buttons.button("@add", Icon.add, () -> {
            StoryNode node = new StoryNode();
            node.sector = Unity.cinematicEditor.sector();
            node.name = this.lastName();
            node.position.set(this.canvas.stageToLocalCoordinates(Tmp.v1.set((float)Core.graphics.getWidth() / 2.0F, (float)Core.graphics.getHeight() / 2.0F)));
            Unity.cinematicEditor.nodes.add(node);
            this.canvas.add(node);
        }).size(210.0F, 64.0F);
        this.buttons.button("@root.cinematic.test", Icon.downOpen, () -> {
            Exception thrown = null;

            try {
                Unity.cinematicEditor.apply();
            } catch (Exception e) {
                thrown = e;
            }

            if (thrown != null) {
                Vars.ui.showException("@root.cinematic.misbehave", thrown);
            } else {
                Vars.ui.showInfo("@root.cinematic.proceed");
            }

        }).size(210.0F, 64.0F);
        this.add(this.buttons).fillX();
    }

    public void hide() {
        if (this.isShown()) {
            try {
                Unity.cinematicEditor.apply();
                super.hide();
            } catch (Exception e) {
                Vars.ui.showException("@root.cinematic.misbehave", e);
            }

        }
    }

    private String lastName() {
        int i = 0;

        for(StoryNode node : Unity.cinematicEditor.nodes) {
            if (node.name.startsWith("node") && Character.isDigit(node.name.codePointAt("node".length()))) {
                int index = Character.digit(node.name.charAt("node".length()), 10);
                if (index > i) {
                    i = index;
                }
            }
        }

        return "node" + (i + 1);
    }

    public void begin() {
        this.canvas.clearChildren();
        Seq var10000 = Unity.cinematicEditor.nodes;
        CinematicCanvas var10001 = this.canvas;
        Objects.requireNonNull(var10001);
        var10000.each(var10001::add);
    }

    public void end() {
        this.canvas.clearChildren();
    }
}
