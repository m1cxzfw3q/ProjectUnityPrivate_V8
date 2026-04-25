package unity.world.blocks.production;

import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Damage;
import mindustry.entities.Effect;
import mindustry.game.EventType.Trigger;
import mindustry.gen.Sounds;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.logic.LAccess;
import mindustry.type.Item;
import mindustry.type.Liquid;
import mindustry.ui.Bar;
import mindustry.world.blocks.production.Separator;
import mindustry.world.consumers.ConsumeLiquid;
import mindustry.world.consumers.ConsumeType;

public class ExplosiveSeparator extends Separator {
    public Color lightColor = Color.valueOf("7f19ea");
    public Color coolColor = new Color(1.0F, 1.0F, 1.0F, 0.0F);
    public Color hotColor = Color.valueOf("ff9575a3");
    public Item fuelItem;
    public float heating = 0.01F;
    public float smokeThreshold = 0.3F;
    public float flashThreshold = 0.46F;
    public float explosionRadius = 19.0F;
    public float explosionDamage = 1350.0F;
    public float coolantPower = 0.5F;
    public TextureRegion lightsRegion;
    public TextureRegion topRegion;
    protected Vec2 tr = new Vec2();

    public ExplosiveSeparator(String name) {
        super(name);
    }

    public void load() {
        super.load();
        this.topRegion = Core.atlas.find(this.name + "-top");
        this.lightsRegion = Core.atlas.find(this.name + "-lights");
    }

    protected TextureRegion[] icons() {
        return new TextureRegion[]{this.region, this.topRegion};
    }

    public void setBars() {
        super.setBars();
        this.bars.add("heat", (entity) -> new Bar("bar.heat", Pal.lightOrange, () -> ((ExplosiveSeparatorBuild)entity).heat));
    }

    public class ExplosiveSeparatorBuild extends Separator.SeparatorBuild {
        protected float heat;
        protected float productionEfficiency;

        public ExplosiveSeparatorBuild() {
            super(ExplosiveSeparator.this);
        }

        public void updateTile() {
            super.updateTile();
            ConsumeLiquid cliquid = (ConsumeLiquid)ExplosiveSeparator.this.consumes.get(ConsumeType.liquid);
            int fuel = this.items.get(ExplosiveSeparator.this.fuelItem);
            float fullness = (float)fuel / (float)ExplosiveSeparator.this.itemCapacity;
            this.productionEfficiency = fullness;
            if (fuel > 0 && this.enabled && this.power.status > 0.0F) {
                this.heat += fullness * ExplosiveSeparator.this.heating * Math.min(this.delta(), 4.0F);
            } else {
                this.productionEfficiency = 0.0F;
            }

            Liquid liquid = cliquid.liquid;
            if (this.heat > 0.0F && this.enabled) {
                float maxUsed = Math.min(this.liquids.get(liquid), this.heat / ExplosiveSeparator.this.coolantPower);
                this.heat -= maxUsed * ExplosiveSeparator.this.coolantPower;
                this.liquids.remove(liquid, maxUsed);
            }

            if ((double)this.heat > 0.3) {
                float smoke = 1.0F + (this.heat - ExplosiveSeparator.this.smokeThreshold) / (1.0F - ExplosiveSeparator.this.smokeThreshold);
                if (Mathf.chance((double)(smoke / 20.0F * this.delta()))) {
                    Fx.reactorsmoke.at(this.x + Mathf.range((float)(ExplosiveSeparator.this.size * 8) / 2.0F), this.y + Mathf.random((float)(ExplosiveSeparator.this.size * 8) / 2.0F));
                }
            }

            this.heat = Mathf.clamp(this.heat);
            if (this.heat >= 0.999F) {
                Events.fire(Trigger.thoriumReactorOverheat);
                this.kill();
            }

        }

        public void onDestroyed() {
            super.onDestroyed();
            Sounds.explosionbig.at(this.tile);
            int fuel = this.items.get(ExplosiveSeparator.this.fuelItem);
            if ((fuel >= 5 || !(this.heat < 0.5F)) && Vars.state.rules.reactorExplosions) {
                Effect.shake(6.0F, 16.0F, this.x, this.y);
                Fx.nuclearShockwave.at(this.x, this.y);

                for(int i = 0; i < 6; ++i) {
                    Time.run(Mathf.random(40.0F), () -> Fx.nuclearcloud.at(this.x, this.y));
                }

                Damage.damage(this.x, this.y, ExplosiveSeparator.this.explosionRadius * 8.0F, ExplosiveSeparator.this.explosionDamage * 4.0F);

                for(int i = 0; i < 20; ++i) {
                    Time.run(Mathf.random(50.0F), () -> {
                        ExplosiveSeparator.this.tr.rnd(Mathf.random(40.0F));
                        Fx.explosion.at(ExplosiveSeparator.this.tr.x + this.x, ExplosiveSeparator.this.tr.y + this.y);
                    });
                }

                for(int i = 0; i < 70; ++i) {
                    Time.run(Mathf.random(80.0F), () -> {
                        ExplosiveSeparator.this.tr.rnd(Mathf.random(120.0F));
                        Fx.nuclearsmoke.at(ExplosiveSeparator.this.tr.x + this.x, ExplosiveSeparator.this.tr.y + this.y);
                    });
                }

            }
        }

        public void drawLight() {
            float fract = this.productionEfficiency;
            Drawf.light(this.team, this.x, this.y, (90.0F + Mathf.absin(5.0F, 5.0F)) * fract, Tmp.c1.set(ExplosiveSeparator.this.lightColor).lerp(Color.scarlet, this.heat), 0.6F * fract);
        }

        public void draw() {
            super.draw();
            Draw.rect(ExplosiveSeparator.this.topRegion, this.x, this.y);
            Draw.color(ExplosiveSeparator.this.coolColor, ExplosiveSeparator.this.hotColor, this.heat);
            Fill.rect(this.x, this.y, (float)(ExplosiveSeparator.this.size * 8), (float)(ExplosiveSeparator.this.size * 8));
            if (this.heat > ExplosiveSeparator.this.flashThreshold) {
                float flash = 1.0F + (this.heat - ExplosiveSeparator.this.flashThreshold) / 0.53999996F * 5.4F;
                flash += flash * this.delta();
                Draw.color(Color.red, Color.yellow, Mathf.absin(flash, 9.0F, 1.0F));
                Draw.alpha(0.6F);
                Draw.rect(ExplosiveSeparator.this.lightsRegion, this.x, this.y);
            }

            Draw.reset();
        }

        public double sense(LAccess sensor) {
            return sensor == LAccess.heat ? (double)this.heat : super.sense(sensor);
        }

        public void write(Writes write) {
            super.write(write);
            write.f(this.heat);
        }

        public void read(Reads read, byte revision) {
            super.read(read, revision);
            this.heat = read.f();
        }
    }
}
