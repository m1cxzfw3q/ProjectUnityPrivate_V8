package unity.logic;

import arc.graphics.Color;
import arc.scene.ui.Button;
import arc.scene.ui.TextField;
import arc.scene.ui.layout.Table;
import mindustry.gen.Icon;
import mindustry.logic.LAssembler;
import mindustry.logic.LExecutor;
import mindustry.logic.LStatement;
import mindustry.ui.Styles;
import unity.graphics.UnityPal;

public class ExpSensorStatement extends LStatement {
    public String res = "result";
    public String type = "block1";
    public ExpContentList cont;

    public ExpSensorStatement() {
        cont = ExpContentList.totalExp;
    }

    public void build(Table table) {
        table.clearChildren();
        table.table((t) -> {
            t.left();
            t.setColor(table.color);
            field(t, res, (text) -> res = text);
            t.add(" = ");
        });
        row(table);
        table.table((t) -> {
            t.left();
            t.setColor(table.color);
            TextField tfield = field(t, cont.name(), (text) -> {
                try {
                    cont = ExpContentList.valueOf(text);
                } catch (Exception ignored) {}
            }).padRight(0.0F).get();
            Button b = new Button(Styles.logict);
            b.image(Icon.pencilSmall);
            b.clicked(() -> showSelect(b, ExpContentList.all, cont, (t2) -> {
                tfield.setText(t2.name());
                cont = t2;
                build(table);
            }, 1, (cell) -> cell.size(240.0F, 40.0F)));
            t.add(b).color(table.color).size(40.0F).padLeft(-1.0F);
            t.add(" in ");
            field(t, type, (text) -> type = text);
        }).left();
    }

    public void write(StringBuilder builder) {
        builder.append("expsensor ").append(res).append(" ").append(cont.name()).append(" ").append(type);
    }

    public Color color() {
        return UnityPal.exp;
    }

    public LExecutor.LInstruction build(LAssembler builder) {
        return new ExpSenseI(builder.var(res), cont, builder.var(type));
    }
}
