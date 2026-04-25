package unity.world.blocks.exp.turrets;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.util.Log;
import arc.util.Strings;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.core.World;
import mindustry.entities.Effect;
import mindustry.entities.Fires;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Building;
import mindustry.gen.Fire;
import mindustry.gen.Sounds;
import mindustry.gen.Tex;
import mindustry.graphics.Drawf;
import mindustry.type.Item;
import mindustry.type.Liquid;
import mindustry.world.Tile;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatValue;
import unity.entities.bullet.exp.GeyserBulletType;
import unity.entities.bullet.exp.GeyserLaserBulletType;
import unity.world.blocks.exp.ExpTurret;

public class OmniLiquidTurret extends ExpTurret {
    public TextureRegion liquidRegion;
    public TextureRegion topRegion;
    public boolean extinguish = true;
    public BulletType shootType;
    public float shootAmount = 0.5F;

    public OmniLiquidTurret(String name) {
        super(name);
        this.acceptCoolant = false;
        this.hasLiquids = true;
        this.loopSound = Sounds.spray;
        this.shootSound = Sounds.none;
        this.smokeEffect = Fx.none;
        this.shootEffect = Fx.none;
        this.outlinedIcon = 1;
    }

    public void setStats() {
        super.setStats();
        this.stats.add(Stat.ammo, this.ammo(0));
    }

    public void load() {
        super.load();
        this.liquidRegion = Core.atlas.find(this.name + "-liquid");
        this.topRegion = Core.atlas.find(this.name + "-top");
    }

    public TextureRegion[] icons() {
        return this.topRegion.found() ? new TextureRegion[]{this.baseRegion, this.region, this.topRegion} : super.icons();
    }

    public static boolean friendly(Liquid l) {
        return l.effect != StatusEffects.none && l.effect.damage <= 0.1F && (l.effect.damage < -0.01F || l.effect.healthMultiplier > 1.01F || l.effect.damageMultiplier > 1.01F);
    }

    public StatValue ammo(int indent) {
        BulletType var3 = this.shootType;
        if (var3 instanceof GeyserLaserBulletType) {
            GeyserLaserBulletType g = (GeyserLaserBulletType)var3;
            GeyserBulletType var4 = (GeyserBulletType)g.geyser;
            return (table) -> {
                table.row();

                for(Liquid t : Vars.content.liquids()) {
                    boolean compact = indent > 0;
                    table.image(t.uiIcon).size(24.0F).padRight(4.0F).right().top();
                    table.add(t.localizedName).padRight(10.0F).left().top();
                    ((Table)table.table((bt) -> {
                        bt.left().defaults().padRight(3.0F).left();
                        float damage = var4.damage * GeyserBulletType.damageScale(t) * 60.0F;
                        if (damage > 0.0F) {
                            sep(bt, Core.bundle.format("bullet.splashdamage", new Object[]{Strings.autoFixed(damage, 1), Strings.fixed(var4.radius / 8.0F, 1)}));
                        } else {
                            sep(bt, Core.bundle.format("bullet.splashheal", new Object[]{Strings.autoFixed(-damage, 1), Strings.fixed(var4.radius / 8.0F, 1)}));
                        }

                        float kn = GeyserBulletType.knockbackScale(t) * var4.knockback;
                        if (kn > 0.0F) {
                            sep(bt, Core.bundle.format("bullet.knockback", new Object[]{Strings.autoFixed(kn, 2)}));
                        }

                        if (t.temperature > 0.8F) {
                            sep(bt, "@bullet.incendiary");
                        }

                        if (GeyserBulletType.hasLightning(t)) {
                            sep(bt, Core.bundle.format("bullet.lightning", new Object[]{(int)(1.0F + t.heatCapacity * 5.0F), damage * 0.5F}));
                        }

                        if (t.effect != StatusEffects.none) {
                            sep(bt, (t.effect.minfo.mod == null ? t.effect.emoji() : "") + "[stat]" + t.effect.localizedName);
                        }

                    }).padTop(compact ? 0.0F : -9.0F).padLeft((float)(indent * 8)).left().get()).background(compact ? null : Tex.underline);
                    table.row();
                }

            };
        } else {
            return (table) -> {
            };
        }
    }

    private static void sep(Table table, String text) {
        table.row();
        table.add(text);
    }

    public class OmniLiquidTurretBuild extends ExpTurret.ExpTurretBuild {
        public OmniLiquidTurretBuild() {
            super(OmniLiquidTurret.this);
        }

        public void draw() {
            super.draw();
            if (OmniLiquidTurret.this.liquidRegion.found()) {
                Drawf.liquid(OmniLiquidTurret.this.liquidRegion, this.x + OmniLiquidTurret.this.tr2.x, this.y + OmniLiquidTurret.this.tr2.y, this.liquids.total() / OmniLiquidTurret.this.liquidCapacity, this.liquids.current().color, this.rotation - 90.0F);
            }

            if (OmniLiquidTurret.this.topRegion.found()) {
                Draw.rect(OmniLiquidTurret.this.topRegion, this.x + OmniLiquidTurret.this.tr2.x, this.y + OmniLiquidTurret.this.tr2.y, this.rotation - 90.0F);
            }

        }

        public boolean shouldActiveSound() {
            return this.wasShooting && this.enabled;
        }

        public void updateTile() {
            this.unit.ammo((float)this.unit.type().ammoCapacity * this.liquids.currentAmount() / OmniLiquidTurret.this.liquidCapacity);
            super.updateTile();
        }

        protected void findTarget() {
            if (OmniLiquidTurret.this.extinguish && this.liquids.current().canExtinguish()) {
                int tx = World.toTile(this.x);
                int ty = World.toTile(this.y);
                Fire result = null;
                float mindst = 0.0F;
                int tr = (int)(OmniLiquidTurret.this.range / 8.0F);

                for(int x = -tr; x <= tr; ++x) {
                    for(int y = -tr; y <= tr; ++y) {
                        Tile other = Vars.world.tile(x + tx, y + ty);
                        Fire fire = Fires.get(x + tx, y + ty);
                        float dst = fire == null ? 0.0F : this.dst2(fire);
                        if (other != null && fire != null && Fires.has(other.x, other.y) && dst <= OmniLiquidTurret.this.range * OmniLiquidTurret.this.range && (result == null || dst < mindst) && (other.build == null || other.team() == this.team)) {
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

        protected boolean canHeal() {
            return this.liquids.current() != null && OmniLiquidTurret.friendly(this.liquids.current());
        }

        protected void effects() {
            BulletType type = this.peekAmmo();
            Effect fshootEffect = OmniLiquidTurret.this.shootEffect == Fx.none ? type.shootEffect : OmniLiquidTurret.this.shootEffect;
            Effect fsmokeEffect = OmniLiquidTurret.this.smokeEffect == Fx.none ? type.smokeEffect : OmniLiquidTurret.this.smokeEffect;
            fshootEffect.at(this.x + OmniLiquidTurret.this.tr.x, this.y + OmniLiquidTurret.this.tr.y, this.rotation, this.liquids.current().color);
            fsmokeEffect.at(this.x + OmniLiquidTurret.this.tr.x, this.y + OmniLiquidTurret.this.tr.y, this.rotation, this.liquids.current().color);
            OmniLiquidTurret.this.shootSound.at(this.tile);
            if (OmniLiquidTurret.this.shootShake > 0.0F) {
                Effect.shake(OmniLiquidTurret.this.shootShake, OmniLiquidTurret.this.shootShake, this.tile.build);
            }

            this.recoil = OmniLiquidTurret.this.recoilAmount;
        }

        public BulletType useAmmo() {
            if (this.cheating()) {
                return OmniLiquidTurret.this.shootType;
            } else {
                this.liquids.remove(this.liquids.current(), OmniLiquidTurret.this.shootAmount);
                return OmniLiquidTurret.this.shootType;
            }
        }

        public BulletType peekAmmo() {
            return OmniLiquidTurret.this.shootType;
        }

        public boolean hasAmmo() {
            return this.liquids.total() >= OmniLiquidTurret.this.shootAmount;
        }

        public boolean acceptItem(Building source, Item item) {
            return false;
        }

        public boolean acceptLiquid(Building source, Liquid liquid) {
            if (!OmniLiquidTurret.this.hasLiquids) {
                return false;
            } else {
                return this.liquids.current() == liquid || this.liquids.currentAmount() < 0.2F;
            }
        }

        protected void bullet(BulletType type, float angle) {
            Log.info("Shoot with " + this.liquids.current().name);
            float lifeScl = type.scaleVelocity ? Mathf.clamp(Mathf.dst(this.x + OmniLiquidTurret.this.tr.x, this.y + OmniLiquidTurret.this.tr.y, this.targetPos.x, this.targetPos.y) / type.range(), OmniLiquidTurret.this.minRange / type.range(), OmniLiquidTurret.this.range / type.range()) : 1.0F;
            type.create(this, this.team, this.x + OmniLiquidTurret.this.tr.x, this.y + OmniLiquidTurret.this.tr.y, angle, -1.0F, 1.0F + Mathf.range(OmniLiquidTurret.this.velocityInaccuracy), lifeScl, this.liquids.current());
        }
    }
}
