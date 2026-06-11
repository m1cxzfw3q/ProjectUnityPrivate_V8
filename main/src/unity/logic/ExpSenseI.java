package unity.logic;

import mindustry.logic.*;
import mindustry.logic.LExecutor.*;
import unity.gen.*;
import unity.gen.Expc.*;

public class ExpSenseI implements LInstruction{
    public LVar res, type;
    public ExpContentList cont;

    public ExpSenseI(LVar res, ExpContentList cont, LVar type){
        this.res = res;
        this.cont = cont;
        this.type = type;
    }

    @Override
    public void run(LExecutor exec){
        Object b = type.obj();

        if(b instanceof ExpBuildc build){
            switch(cont){
                case totalExp -> res.setnum(build.exp());
                case totalLevel -> res.setnum(build.level());
                case expCapacity -> res.setnum(((Expc)build.block()).maxExp());
                case maxLevel -> res.setnum(build.maxLevel());
            }
        }else{
            res.setnum(0d);
        }
    }
}
