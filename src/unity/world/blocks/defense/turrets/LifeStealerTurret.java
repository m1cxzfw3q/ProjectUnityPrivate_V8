package unity.world.blocks.defense.turrets;

import arc.Core;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.gen.Healthc;
import mindustry.gen.Teamc;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import unity.content.UnityFx;
import unity.gen.SVec2;
import unity.graphics.UnityPal;

public class LifeStealerTurret extends GenericTractorBeamTurret<Teamc> {
    public boolean targetAir = true;
    public boolean targetGround = true;
    public float damage = 60.0F;
    public float maxContain = 100.0F;
    public float healPercent = 0.05F;
    public Color healColor;
    public Effect healEffect;
    public Effect healTrnsEffect;

    public LifeStealerTurret(String name) {
        super(name);
        this.healColor = UnityPal.monolithLight;
        this.healEffect = Fx.healBlockFull;
        this.healTrnsEffect = UnityFx.supernovaPullEffect;
    }

    public void setStats() {
        super.setStats();
        this.stats.add(Stat.damage, this.damage / 60.0F, StatUnit.perSecond);
        this.stats.add(Stat.targetsAir, this.targetAir);
        this.stats.add(Stat.targetsGround, this.targetGround);
        this.stats.add(Stat.abilities, (cont) -> {
            cont.row();
            cont.table((bt) -> {
                bt.left().defaults().padRight(3.0F).left();
                bt.row();
                bt.add(Core.bundle.format("stat.unity.maxcontain", new Object[]{this.maxContain}));
                bt.row();
                bt.add(Core.bundle.format("stat.unity.healpercent", new Object[]{this.healPercent}));
            });
        });
    }

    public class LifeStealerTurretBuild extends GenericTractorBeamTurret<Teamc>.GenericTractorBeamTurretBuild {
        public float contained;

        public LifeStealerTurretBuild() {
            super(LifeStealerTurret.this);
        }

        protected void findTarget() {
            this.target = Units.closestTarget(this.team, this.x, this.y, LifeStealerTurret.this.range, (unit) -> unit.team != this.team && unit.isValid() && unit.checkTarget(LifeStealerTurret.this.targetAir, LifeStealerTurret.this.targetGround), (tile) -> LifeStealerTurret.this.targetGround && tile.isValid());
        }

        protected void findTarget(Vec2 pos) {
            this.target = Units.closestTarget(this.team, pos.x, pos.y, LifeStealerTurret.this.laserWidth, (unit) -> unit.team != this.team && unit.isValid() && unit.checkTarget(LifeStealerTurret.this.targetAir, LifeStealerTurret.this.targetGround), (tile) -> LifeStealerTurret.this.targetGround && tile.isValid());
        }

        protected void apply() {
            Teamc var2 = this.target;
            if (var2 instanceof Healthc) {
                Healthc h = (Healthc)var2;
                float health = LifeStealerTurret.this.damage / 60.0F * this.efficiency();
                h.damageContinuous(health);
                this.contained += health * Time.delta;
            }

            if (this.contained >= LifeStealerTurret.this.maxContain) {
                this.tryHeal();
            }

        }

        protected void tryHeal() {
            boolean any = Vars.indexer.eachBlock(this, LifeStealerTurret.this.range, (b) -> b.health() < b.maxHealth(), (b) -> {
                LifeStealerTurret.this.healTrnsEffect.at(this.x, this.y, 2.5F + Mathf.range(0.3F), SVec2.construct(b.x, b.y));
                Time.run(LifeStealerTurret.this.healEffect.lifetime, () -> {
                    if (b.isValid()) {
                        LifeStealerTurret.this.healEffect.at(b.x, b.y, (float)b.block.size, LifeStealerTurret.this.healColor);
                        b.healFract(LifeStealerTurret.this.healPercent);
                    }

                });
            });
            if (any) {
                this.contained %= LifeStealerTurret.this.maxContain;
            }

        }
    }
}
