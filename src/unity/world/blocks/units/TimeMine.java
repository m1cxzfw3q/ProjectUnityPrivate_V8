package unity.world.blocks.units;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.util.Nullable;
import arc.util.Tmp;
import mindustry.content.StatusEffects;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.gen.Building;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.logic.Ranged;
import mindustry.world.Block;

public class TimeMine extends Block {
    public float maxRange = 16.0F;
    public float rangeScale = 2.0F;
    public float pullTime = 300.0F;
    public float shake = 1.0F;
    public float force = 15.0F;
    public float forceScale = 25.0F;
    public int maxUnits = 5;

    public TimeMine(String name) {
        super(name);
        this.update = true;
        this.solid = this.targetable = this.hasItems = this.hasPower = this.hasLiquids = false;
    }

    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);
        Drawf.dashCircle((float)(x * 8), (float)(y * 8), this.maxRange, Pal.accent);
        Drawf.dashCircle((float)(x * 8), (float)(y * 8), this.maxRange * this.rangeScale, Pal.lancerLaser);
        Draw.reset();
    }

    public class TimeMineBuild extends Building implements Ranged {
        @Nullable
        Unit[] pulledUnits;
        float heat;
        float range;

        public TimeMineBuild() {
            this.pulledUnits = new Unit[TimeMine.this.maxUnits];
            this.heat = 0.0F;
            this.range = TimeMine.this.maxRange;
        }

        public float range() {
            return this.range;
        }

        public void drawSelect() {
            super.drawSelect();
            Drawf.dashCircle(this.x, this.y, this.range, this.team.color);
        }

        public int getPullCount() {
            int count = 0;

            for(Unit unit : this.pulledUnits) {
                if (unit != null && !(unit.dst(this) > this.range)) {
                    ++count;
                }
            }

            return count;
        }

        public boolean isPulling() {
            return this.getPullCount() > 0 && !this.dead();
        }

        public void updateTile() {
            super.updateTile();
            int[] i = new int[]{0};
            Units.nearbyEnemies(this.team, this.x, this.y, this.range, (unitx) -> {
                if (i[0] < TimeMine.this.maxUnits) {
                    int var10004 = i[0];
                    int var10001 = i[0];
                    i[0] = var10004 + 1;
                    this.pulledUnits[var10001] = unitx;
                }

            });
            if (this.isPulling()) {
                if (this.heat < TimeMine.this.pullTime) {
                    this.heat += this.delta();
                } else {
                    this.kill();
                }

                if (this.range < TimeMine.this.maxRange * TimeMine.this.rangeScale) {
                    this.range = Mathf.lerpDelta(this.range, TimeMine.this.maxRange * TimeMine.this.rangeScale, 2.0F / TimeMine.this.pullTime);
                }

                for(Unit unit : this.pulledUnits) {
                    if (unit != null && !(unit.dst(this) > this.range)) {
                        unit.apply(StatusEffects.muddy, TimeMine.this.pullTime - this.heat);
                        unit.impulseNet(Tmp.v1.set(this).sub(unit).limit((TimeMine.this.force + (1.0F - unit.dst(this) / this.range) * TimeMine.this.forceScale) * this.delta()));
                    }
                }

                this.damage(0.1F);
                Effect.shake((float)this.getPullCount() / ((float)TimeMine.this.maxUnits * 1.0F) * TimeMine.this.shake, 1.0F, this);
            } else {
                if (this.heat > 0.0F) {
                    this.heat -= this.delta();
                }

                if (this.range > TimeMine.this.maxRange) {
                    this.range = Mathf.lerpDelta(TimeMine.this.maxRange, this.range, 2.0F / TimeMine.this.pullTime);
                }
            }

        }

        public void draw() {
            super.draw();
            Draw.color(Color.black, Pal.darkerMetal, 0.4F);
            Draw.alpha(0.7F);
            Fill.circle(this.x, this.y, this.heat / TimeMine.this.pullTime * this.range);

            for(Unit unit : this.pulledUnits) {
                if (unit != null && !(unit.dst(this) > this.range)) {
                    float fin = 0.75F * unit.dst(this) / this.range;
                    float lope = (0.5F - Math.abs(fin - 0.5F)) * 1.25F;
                    Draw.color(Pal.lancerLaser, Color.black, fin);
                    Lines.stroke(0.25F + lope * (1.0F - fin));
                    Lines.square(unit.x, unit.y, unit.hitSize * 1.25F, 45.0F);
                }
            }

        }
    }
}
