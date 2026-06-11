package unity.entities.comp;

import arc.math.geom.*;
import arc.util.*;
import mindustry.*;
import mindustry.gen.*;
import unity.annotations.Annotations.*;
import unity.gen.*;
import unity.mod.*;

@SuppressWarnings("unused")
@EntityDef(value = {Bulletc.class, TimeStopBulletc.class}, serialize = false, pooled = true)
@EntityComponent
abstract class TimeStopBulletComp implements Bulletc{
    @Import float x, y;
    @Import float drag;
    @Import Vec2 vel;

    @Wrap(value = "update()", block = Velc.class)
    boolean updateVel(){
        return !TimeStop.inTimeStop();
    }

    @MethodPriority(-1)
    @Override
    public void update(){
        if(!updateVel() && (!Vars.net.client() || isLocal())){
            x += vel.x * Time.delta;
            y += vel.y * Time.delta;

            vel.scl(Math.max(1f - drag * Time.delta, 0.0F));
        }
    }
}
