package unity.entities.effects;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Mathf;
import arc.math.geom.Intersector;
import arc.math.geom.Vec2;
import arc.math.geom.Vec3;
import arc.util.Time;
import arc.util.pooling.Pools;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.gen.EffectState;
import mindustry.gen.LegsUnit;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;
import unity.assets.list.UnityShaders;
import unity.content.UnityFx;

public class UnitCutEffect extends EffectState {
    static Vec2 tmpPoint = new Vec2();
    static Vec2 tmpPoint2 = new Vec2();
    static Color color = new Color();
    Unit unit;
    Vec3 cutDirection = new Vec3();
    float rotationVelocity = 0.0F;
    float rotationOffset = 0.0F;
    Vec2 vel = new Vec2();
    Vec2 offset = new Vec2();

    public static void createCut(Unit unit, float x, float y, float x2, float y2) {
        Intersector.nearestSegmentPoint(x, y, x2, y2, unit.x, unit.y, tmpPoint);
        tmpPoint.sub(unit);
        tmpPoint.limit(unit.hitSize / 4.0F);
        float rot = tmpPoint.angle();
        unit.hitTime = 0.0F;

        for(int i = 0; i < 2; ++i) {
            UnitCutEffect l = (UnitCutEffect)Pools.obtain(UnitCutEffect.class, UnitCutEffect::new);
            l.cutDirection.set(tmpPoint.x, tmpPoint.y, rot + (float)i * 180.0F);
            l.lifetime = 40.0F + unit.hitSize / 20.0F + Mathf.range(2.0F, 5.0F);
            l.x = unit.x;
            l.y = unit.y;
            l.unit = unit;
            l.rotationVelocity = -((float)Mathf.signs[i] * 1.2F) + Mathf.range(0.7F);
            l.offset.setZero();
            l.vel.trns(rot + 180.0F + (float)i * 180.0F, unit.hitSize / 60.0F);
            l.add();
        }

        UnityFx.tenmeikiriCut.at(unit.x + tmpPoint.x, unit.y + tmpPoint.y, rot + 90.0F, unit.hitSize * 1.5F);
    }

    public float clipSize() {
        return this.unit.clipSize() * 1.5F;
    }

    public void reset() {
        super.reset();
        this.unit = null;
        this.rotationVelocity = 0.0F;
        this.rotationOffset = 0.0F;
    }

    public void update() {
        if (this.time >= this.lifetime) {
            Effect.shake(this.unit.hitSize / 3.0F, this.unit.hitSize / 3.0F, this.x + this.offset.x, this.y + this.offset.x);
            Fx.dynamicExplosion.at(this.x + this.offset.x, this.y + this.offset.y, this.unit.bounds() / 2.0F / 8.0F);
            Effect.scorch(this.x + this.offset.x, this.y + this.offset.y, (int)(this.unit.hitSize / 5.0F));
            Fx.explosion.at(this.x + this.offset.x, this.y + this.offset.y);
            this.unit.type.deathSound.at(this.x + this.offset.x, this.y + this.offset.y);
            this.remove();
        } else {
            this.unit.hitTime = 0.0F;
            this.offset.add(this.vel.x * Time.delta, this.vel.y * Time.delta);
            this.rotationOffset += Time.delta * this.rotationVelocity;
            this.vel.scl(1.0F - Math.min(this.unit.drag, 0.07F));
            this.rotationVelocity *= 1.0F - Math.min(this.unit.drag, 0.07F);
            if (Mathf.chanceDelta((double)(0.4F * (this.unit.hitSize / 45.0F)))) {
                tmpPoint2.trns(this.cutDirection.z + this.rotationOffset, 0.0F, Mathf.range(this.unit.hitSize / 2.0F)).add(this.cutDirection.x + this.offset.x, this.cutDirection.y + this.offset.y).add(this.unit);
                Fx.fallSmoke.at(tmpPoint2.x, tmpPoint2.y);
            }

            this.time += Time.delta;
        }
    }

    public float size() {
        return this.unit instanceof LegsUnit ? this.unit.hitSize + this.unit.type.legLength * 2.0F : this.unit.hitSize;
    }

    public void draw() {
        float z = (double)this.unit.elevation > (double)0.5F ? (this.unit.type.lowAltitude ? 90.0F : 115.0F) : this.unit.type.groundLayer + Mathf.clamp(this.unit.type.hitSize / 4000.0F, 0.0F, 0.01F);
        Draw.draw(z, () -> {
            tmpPoint.set(Core.camera.position);
            Core.camera.position.set(tmpPoint).sub(this.offset);
            Core.camera.update();
            Draw.proj(Core.camera);
            color.set(Color.green);
            UnityShaders.stencilShader.stencilColor.set(color);
            UnityShaders.stencilShader.heatColor.set(Pal.lightFlame).lerp(Pal.darkFlame, this.fin());
            Vars.renderer.effectBuffer.begin(Color.clear);
            float lastRotation = this.unit.rotation;
            this.unit.rotation = lastRotation + this.rotationOffset;
            this.unit.draw();
            this.unit.rotation = lastRotation;
            Draw.reset();
            float[] verts = new float[8];
            int[] dx = new int[]{-1, -1, 1, 1};
            int[] dy = new int[]{0, 1, 1, 0};

            for(int i = 0; i < 4; ++i) {
                tmpPoint2.trns(this.cutDirection.z + this.rotationOffset, (float)dy[i] * this.size() * 1.5F, (float)dx[i] * this.size() * 1.5F).add(this.cutDirection.x, this.cutDirection.y).add(this.unit);

                for(int j = 0; j < 2; ++j) {
                    verts[i * 2 + j] = j == 0 ? tmpPoint2.x : tmpPoint2.y;
                }
            }

            Draw.color(color);
            Fill.quad(verts[0], verts[1], verts[2], verts[3], verts[4], verts[5], verts[6], verts[7]);
            Vars.renderer.effectBuffer.end();
            Draw.blit(Vars.renderer.effectBuffer, UnityShaders.stencilShader);
            Core.camera.position.set(tmpPoint);
            Core.camera.update();
            Draw.proj(Core.camera);
        });
    }
}
