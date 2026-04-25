package unity.world.blocks.defense;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.struct.ObjectFloatMap;
import arc.util.Strings;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.Bullet;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.meta.BlockGroup;

public class PowerWall extends Wall {
    public ObjectFloatMap<Class<?>> energyMultiplier = new ObjectFloatMap();
    public float powerProduction = 2.0F;
    public float damageThreshold = 150.0F;
    public float overloadDamage = 0.8F;
    public TextureRegion heatRegion;
    public float heatThreshold = 0.35F;
    public Color heatColor;

    public PowerWall(String name) {
        super(name);
        this.heatColor = Color.red;
        this.update = true;
        this.sync = true;
        this.flashHit = true;
        this.solid = true;
        this.consumesPower = false;
        this.outputsPower = true;
        this.hasPower = true;
        this.group = BlockGroup.walls;
    }

    public void load() {
        super.load();
        this.heatRegion = Core.atlas.find(this.name + "-heat");
    }

    public void setBars() {
        super.setBars();
        if (this.hasPower && this.outputsPower && !this.consumes.hasPower()) {
            this.bars.add("power", (entity) -> new Bar(() -> Core.bundle.format("bar.poweroutput", new Object[]{Strings.fixed(entity.getPowerProduction() * 60.0F * entity.timeScale(), 1)}), () -> Pal.powerBar, () -> entity.productionEfficiency));
        }

    }

    public class PowerWallBuild extends Wall.WallBuild {
        public float productionEfficiency = 0.0F;
        protected boolean overloaded;

        public PowerWallBuild() {
            super(PowerWall.this);
        }

        public void draw() {
            super.draw();
            if (this.productionEfficiency > PowerWall.this.heatThreshold) {
                float heat = 1.0F + (this.productionEfficiency - PowerWall.this.heatThreshold) / (1.0F - PowerWall.this.heatThreshold) * 5.4F;
                heat += heat * Time.delta;
                Draw.color(PowerWall.this.heatColor, Mathf.absin(heat, 9.0F, 1.0F) * Mathf.curve(this.productionEfficiency, PowerWall.this.heatThreshold, 1.0F));
                Draw.blend(Blending.additive);
                Draw.rect(PowerWall.this.heatRegion, this.x, this.y);
                Draw.blend();
            }

        }

        public void updateTile() {
            super.updateTile();
            this.productionEfficiency = Mathf.lerpDelta(this.productionEfficiency, 0.0F, 0.05F);
            this.overloaded = this.productionEfficiency > 1.0F;
            if (this.overloaded) {
                this.health -= PowerWall.this.overloadDamage * Time.delta;
            }

        }

        public boolean collision(Bullet bullet) {
            this.productionEfficiency += bullet.damage() / PowerWall.this.damageThreshold * (bullet.vel().len() / bullet.type.speed) * PowerWall.this.energyMultiplier.get(bullet.type.getClass().isAnonymousClass() ? bullet.type.getClass().getSuperclass() : bullet.type.getClass(), 1.0F);
            if (!bullet.team.isEnemy(this.team) && bullet.type.healPercent > 0.0F) {
                this.productionEfficiency = 0.0F;
            }

            return super.collision(bullet);
        }

        public float getPowerProduction() {
            return PowerWall.this.powerProduction * this.productionEfficiency;
        }

        public void heal() {
            super.heal();
            this.productionEfficiency = 0.0F;
        }

        public void heal(float amount) {
            super.heal(amount);
            this.productionEfficiency = 0.0F;
        }

        public void health(float health) {
            if (this.health < health) {
                this.productionEfficiency = 0.0F;
            }

            super.health(health);
        }

        public void write(Writes write) {
            super.write(write);
            write.f(this.productionEfficiency);
        }

        public void read(Reads read, byte revision) {
            super.read(read, revision);
            this.productionEfficiency = read.f();
        }
    }
}
