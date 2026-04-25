package unity.world.blocks.exp.turrets;

import arc.Core;
import arc.func.Boolf;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.struct.ObjectMap;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.core.World;
import mindustry.entities.Effect;
import mindustry.entities.Fires;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Building;
import mindustry.gen.Fire;
import mindustry.gen.Sounds;
import mindustry.graphics.Drawf;
import mindustry.type.Item;
import mindustry.type.Liquid;
import mindustry.world.Tile;
import mindustry.world.consumers.ConsumeLiquidFilter;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatValues;
import mindustry.world.meta.Stats;
import unity.world.blocks.exp.ExpTurret;

public class ExpLiquidTurret extends ExpTurret {
    public ObjectMap<Liquid, BulletType> ammoTypes = new ObjectMap();
    public TextureRegion liquidRegion;
    public TextureRegion topRegion;
    public boolean extinguish = true;

    public ExpLiquidTurret(String name) {
        super(name);
        this.acceptCoolant = false;
        this.hasLiquids = true;
        this.loopSound = Sounds.spray;
        this.shootSound = Sounds.none;
        this.smokeEffect = Fx.none;
        this.shootEffect = Fx.none;
        this.outlinedIcon = 1;
    }

    public void ammo(Object... objects) {
        this.ammoTypes = ObjectMap.of(objects);
    }

    public void setStats() {
        super.setStats();
        this.stats.add(Stat.ammo, StatValues.ammo(this.ammoTypes));
    }

    public void init() {
        this.consumes.add(new ConsumeLiquidFilter((i) -> this.ammoTypes.containsKey(i), 1.0F) {
            public boolean valid(Building entity) {
                return entity.liquids.total() > 0.001F;
            }

            public void update(Building entity) {
            }

            public void display(Stats stats) {
            }
        });
        super.init();
    }

    public void load() {
        super.load();
        this.liquidRegion = Core.atlas.find(this.name + "-liquid");
        this.topRegion = Core.atlas.find(this.name + "-top");
    }

    public TextureRegion[] icons() {
        return this.topRegion.found() ? new TextureRegion[]{this.baseRegion, this.region, this.topRegion} : super.icons();
    }

    public class ExpLiquidTurretBuild extends ExpTurret.ExpTurretBuild {
        public ExpLiquidTurretBuild() {
            super(ExpLiquidTurret.this);
        }

        public void draw() {
            super.draw();
            if (ExpLiquidTurret.this.liquidRegion.found()) {
                Drawf.liquid(ExpLiquidTurret.this.liquidRegion, this.x + ExpLiquidTurret.this.tr2.x, this.y + ExpLiquidTurret.this.tr2.y, this.liquids.total() / ExpLiquidTurret.this.liquidCapacity, this.liquids.current().color, this.rotation - 90.0F);
            }

            if (ExpLiquidTurret.this.topRegion.found()) {
                Draw.rect(ExpLiquidTurret.this.topRegion, this.x + ExpLiquidTurret.this.tr2.x, this.y + ExpLiquidTurret.this.tr2.y, this.rotation - 90.0F);
            }

        }

        public boolean shouldActiveSound() {
            return this.wasShooting && this.enabled;
        }

        public void updateTile() {
            this.unit.ammo((float)this.unit.type().ammoCapacity * this.liquids.currentAmount() / ExpLiquidTurret.this.liquidCapacity);
            super.updateTile();
        }

        protected void findTarget() {
            if (ExpLiquidTurret.this.extinguish && this.liquids.current().canExtinguish()) {
                int tx = World.toTile(this.x);
                int ty = World.toTile(this.y);
                Fire result = null;
                float mindst = 0.0F;
                int tr = (int)(ExpLiquidTurret.this.range / 8.0F);

                for(int x = -tr; x <= tr; ++x) {
                    for(int y = -tr; y <= tr; ++y) {
                        Tile other = Vars.world.tile(x + tx, y + ty);
                        Fire fire = Fires.get(x + tx, y + ty);
                        float dst = fire == null ? 0.0F : this.dst2(fire);
                        if (other != null && fire != null && Fires.has(other.x, other.y) && dst <= ExpLiquidTurret.this.range * ExpLiquidTurret.this.range && (result == null || dst < mindst) && (other.build == null || other.team() == this.team)) {
                            result = fire;
                            mindst = dst;
                        }
                    }
                }

                if (result != null) {
                    this.target = result;
                    return;
                }
            }

            super.findTarget();
        }

        protected void effects() {
            BulletType type = this.peekAmmo();
            Effect fshootEffect = ExpLiquidTurret.this.shootEffect == Fx.none ? type.shootEffect : ExpLiquidTurret.this.shootEffect;
            Effect fsmokeEffect = ExpLiquidTurret.this.smokeEffect == Fx.none ? type.smokeEffect : ExpLiquidTurret.this.smokeEffect;
            fshootEffect.at(this.x + ExpLiquidTurret.this.tr.x, this.y + ExpLiquidTurret.this.tr.y, this.rotation, this.liquids.current().color);
            fsmokeEffect.at(this.x + ExpLiquidTurret.this.tr.x, this.y + ExpLiquidTurret.this.tr.y, this.rotation, this.liquids.current().color);
            ExpLiquidTurret.this.shootSound.at(this.tile);
            if (ExpLiquidTurret.this.shootShake > 0.0F) {
                Effect.shake(ExpLiquidTurret.this.shootShake, ExpLiquidTurret.this.shootShake, this.tile.build);
            }

            this.recoil = ExpLiquidTurret.this.recoilAmount;
        }

        public BulletType useAmmo() {
            if (this.cheating()) {
                return (BulletType)ExpLiquidTurret.this.ammoTypes.get(this.liquids.current());
            } else {
                BulletType type = (BulletType)ExpLiquidTurret.this.ammoTypes.get(this.liquids.current());
                this.liquids.remove(this.liquids.current(), 1.0F / type.ammoMultiplier);
                return type;
            }
        }

        public BulletType peekAmmo() {
            return (BulletType)ExpLiquidTurret.this.ammoTypes.get(this.liquids.current());
        }

        public boolean hasAmmo() {
            return ExpLiquidTurret.this.ammoTypes.get(this.liquids.current()) != null && this.liquids.total() >= 1.0F / ((BulletType)ExpLiquidTurret.this.ammoTypes.get(this.liquids.current())).ammoMultiplier;
        }

        public boolean acceptItem(Building source, Item item) {
            return false;
        }

        public boolean acceptLiquid(Building source, Liquid liquid) {
            return ExpLiquidTurret.this.ammoTypes.get(liquid) != null && (this.liquids.current() == liquid || ExpLiquidTurret.this.ammoTypes.containsKey(liquid) && (!ExpLiquidTurret.this.ammoTypes.containsKey(this.liquids.current()) || this.liquids.get(this.liquids.current()) <= 1.0F / ((BulletType)ExpLiquidTurret.this.ammoTypes.get(this.liquids.current())).ammoMultiplier + 0.001F));
        }
    }
}
