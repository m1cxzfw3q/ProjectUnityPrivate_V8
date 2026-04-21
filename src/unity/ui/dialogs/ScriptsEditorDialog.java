package unity.ui.dialogs;

import arc.func.Cons;
import arc.scene.ui.Label;
import arc.scene.ui.TextArea;
import mindustry.ui.dialogs.BaseDialog;
import unity.ui.UnityStyles;

public class ScriptsEditorDialog extends BaseDialog {
    public Cons<String> listener = (str) -> {
    };
    public TextArea area;

    public ScriptsEditorDialog() {
        super("@root.editscript");
        this.addCloseButton();
        ((Label)this.cont.label(() -> this.linesStr(this.area.getFirstLineShowing(), this.area.getLinesShowing(), this.area.getCursorLine())).growY().padRight(2.0F).style(UnityStyles.codeLabel).get()).setAlignment(16);
        this.area = (TextArea)this.cont.area("", UnityStyles.codeArea, (str) -> this.listener.get(str.replace("\r", "\n"))).grow().get();
        this.area.setFocusTraversal(false);
        this.hidden(() -> this.listener.get(this.area.getText()));
    }

    private String linesStr(int first, int len, int now) {
        StringBuilder str = new StringBuilder("[lightgray]");

        for(int i = 0; i < len; ++i) {
            if (i > 0) {
                str.append("\n");
            }

            if (i + first == now) {
                str.append("[accent]");
            }

            str.append(i + first + 1);
            if (i + first == now) {
                str.append("[]");
            }
        }

        return str + "[]";
    }
}
