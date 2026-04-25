package unity.entities.effects;

import arc.Core;
import arc.func.Cons;
import arc.graphics.Color;
import arc.math.geom.Rect;
import arc.util.Tmp;
import arc.util.pooling.Pools;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.gen.EffectState;
import mindustry.gen.Posc;
import mindustry.gen.Rotc;
import mindustry.world.blocks.defense.turrets.BaseTurret;

public class ParentEffect extends Effect {
    public ParentEffect(float life, Cons<Effect.EffectContainer> renderer) {
        super(life, renderer);
    }

    public ParentEffect(float life, float clipSize, Cons<Effect.EffectContainer> renderer) {
        super(life, clipSize, renderer);
    }

    public void at(float x, float y, float rotation, Object data) {
        this.at(x, y, rotation, Color.white, data);
    }

    public void at(float x, float y, float rotation, Color color, Object data) {
        create(this, x, y, rotation, color, data);
    }

    public static void create(Effect effect, float x, float y, float rotation, Color color, Object data) {
        if (!Vars.headless && effect != Fx.none) {
            if (Core.settings.getBool("effects")) {
                Rect view = Core.camera.bounds(Tmp.r1);
                Rect pos = Tmp.r2.setSize(effect.clip).setCenter(x, y);
                if (view.overlaps(pos)) {
                    ParentEffectState entity = createState();
                    entity.effect = effect;
                    entity.rotation = rotation;
                    entity.originalRotation = rotation;
                    entity.data = data;
                    entity.lifetime = effect.lifetime;
                    entity.set(x, y);
                    entity.color.set(color);
                    float rotationA = 0.0F;
                    if (data instanceof Rotc) {
                        rotationA = ((Rotc)data).rotation();
                    } else if (data instanceof BaseTurret.BaseTurretBuild) {
                        rotationA = ((BaseTurret.BaseTurretBuild)data).rotation;
                    }

                    if (data instanceof Posc) {
                        entity.parent = (Posc)data;
                        entity.positionRotation = ((Posc)data).angleTo(entity) - rotationA;
                    }

                    entity.add();
                }
            }

        }
    }

    public static ParentEffectState createState() {
        return (ParentEffectState)Pools.obtain(ParentEffectState.class, ParentEffectState::new);
    }

    public static class ParentEffectState extends EffectState {
        public float originalRotation = 0.0F;
        public float positionRotation = 0.0F;

        public void update() {
            super.update();
            if (this.parent != null) {
                float rotationA = 0.0F;
                if (this.parent instanceof Rotc) {
                    rotationA = ((Rotc)this.parent).rotation();
                } else if (this.parent instanceof BaseTurret.BaseTurretBuild) {
                    rotationA = ((BaseTurret.BaseTurretBuild)this.parent).rotation;
                }

                this.rotation = rotationA - this.originalRotation;
                float len = (float)Math.sqrt((double)(this.offsetX * this.offsetX + this.offsetY * this.offsetY));
                Tmp.v1.trns(rotationA - this.positionRotation, len).add(this.parent);
                this.x = Tmp.v1.x;
                this.y = Tmp.v1.y;
            }

        }
    }
}
