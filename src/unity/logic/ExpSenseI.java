package unity.logic;

import mindustry.logic.LExecutor;
import mindustry.logic.LVar;
import unity.gen.Expc;

public class ExpSenseI implements LExecutor.LInstruction {
    public LVar res;
    public LVar type;
    public ExpContentList cont;

    public ExpSenseI(LVar res, ExpContentList cont, LVar type) {
        this.res = res;
        this.cont = cont;
        this.type = type;
    }

    public void run(LExecutor exec) {
        Object b = type.obj();
        if (b instanceof Expc.ExpBuildc build) {
            switch (this.cont) {
                case totalExp:
                    res.setnum((double)build.exp());
                    break;
                case totalLevel:
                    res.setnum((double)build.level());
                    break;
                case expCapacity:
                    res.setnum((double)((Expc)build.block()).maxExp());
                    break;
                case maxLevel:
                    res.setnum((double)build.maxLevel());
            }
        } else {
            res.setnum(0.0F);
        }

    }
}