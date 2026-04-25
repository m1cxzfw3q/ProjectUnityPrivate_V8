package unity.world.blocks.defense.turrets;

import arc.Core;
import arc.func.Boolf;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Strings;
import arc.util.Time;
import java.util.Objects;
import mindustry.Vars;
import mindustry.ai.BlockIndexer;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.gen.Groups;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;
import mindustry.type.StatusEffect;
import mindustry.ui.Bar;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

public class AbsorberTurret extends GenericTractorBeamTurret<Teamc> {
    public float powerProduction = 2.5F;
    public float resistance = 0.4F;
    public float damageScale = 18.0F;
    public float damage = 0.0F;
    public float speedScale = 3.5F;
    public StatusEffect status;
    public boolean targetBullets;
    public boolean targetUnits;
    public boolean targetBuildings = false;
    private Seq<Building> buildings = new Seq();

    public AbsorberTurret(String name) {
        super(name);
        this.outputsPower = true;
    }

    public void setStats() {
        super.setStats();
        this.stats.add(Stat.basePowerGeneration, this.powerProduction * 60.0F, StatUnit.powerSecond);
    }

    public void setBars() {
        super.setBars();
        this.bars.add("power", (entity) -> new Bar(() -> Core.bundle.format("bar.poweroutput", new Object[]{Strings.fixed(entity.getPowerProduction() * 60.0F * entity.timeScale(), 1)}), () -> Pal.powerBar, () -> entity.getPowerProduction() / this.powerProduction));
    }

    public class AbsorberTurretBuild extends GenericTractorBeamTurret<Teamc>.GenericTractorBeamTurretBuild {
        public AbsorberTurretBuild() {
            super(AbsorberTurret.this);
        }

        protected void findTarget() {
            this.findTarget(this.x, this.y, AbsorberTurret.this.range);
        }

        protected void findTarget(Vec2 pos) {
            this.findTarget(pos.x, pos.y, AbsorberTurret.this.laserWidth / 2.0F);
        }

        protected void findTarget(float x, float y, float r) {
            Teamc tempTarget = null;
            this.target = null;
            float distance = Float.MAX_VALUE;
            if (AbsorberTurret.this.targetBullets) {
                tempTarget = (Teamc)Groups.bullet.intersect(x - r, y - r, r * 2.0F, r * 2.0F).min((b) -> b.team != this.team && b.type().hittable, (b) -> b.dst2(x, y));
                if (tempTarget != null) {
                    this.target = tempTarget;
                    distance = Mathf.dst(x, y, tempTarget.x(), tempTarget.y());
                }
            }

            if (AbsorberTurret.this.targetUnits) {
                tempTarget = (Teamc)Groups.unit.intersect(x - r, y - r, r * 2.0F, r * 2.0F).min((b) -> b.team != this.team && !b.dead, (b) -> b.dst2(x, y));
                if (tempTarget != null) {
                    float d = Mathf.dst(x, y, tempTarget.x(), tempTarget.y());
                    if (d < distance) {
                        distance = d;
                        this.target = tempTarget;
                    }
                }
            }

            if (AbsorberTurret.this.targetBuildings) {
                AbsorberTurret.this.buildings.clear();
                BlockIndexer var10000 = Vars.indexer;
                Boolf var10005 = (b) -> b.team != this.team && !b.dead;
                Seq var10006 = AbsorberTurret.this.buildings;
                Objects.requireNonNull(var10006);
                var10000.eachBlock((Team)null, x, y, r, var10005, var10006::add);
                tempTarget = (Teamc)AbsorberTurret.this.buildings.min((b) -> b.dst2(x, y));
                if (tempTarget != null) {
                    float d = Mathf.dst(x, y, tempTarget.x(), tempTarget.y());
                    if (d < distance) {
                        this.target = tempTarget;
                    }
                }
            }

        }

        protected void apply() {
            Teamc var2 = this.target;
            if (var2 instanceof Bullet) {
                Bullet bullet = (Bullet)var2;
                bullet.vel.setLength(Math.max(bullet.vel.len() - AbsorberTurret.this.resistance * this.strength, 0.0F));
                bullet.damage = Math.max(bullet.damage - AbsorberTurret.this.resistance / 2.0F * this.strength * Time.delta, 0.0F);
                if (bullet.vel.isZero(0.01F) || bullet.damage <= 0.0F) {
                    bullet.remove();
                }
            }

            var2 = this.target;
            if (var2 instanceof Unit) {
                Unit unit = (Unit)var2;
                if (AbsorberTurret.this.damage > 0.0F) {
                    unit.apply(AbsorberTurret.this.status);
                    unit.damage(AbsorberTurret.this.damage);
                }
            }

            var2 = this.target;
            if (var2 instanceof Building) {
                Building building = (Building)var2;
                if (AbsorberTurret.this.damage > 0.0F) {
                    building.damage(AbsorberTurret.this.damage);
                }
            }

        }

        public float getPowerProduction() {
            if (this.target == null) {
                return 0.0F;
            } else {
                Teamc var2 = this.target;
                if (var2 instanceof Bullet) {
                    Bullet bullet = (Bullet)var2;
                    return bullet.type == null ? 0.0F : bullet.type.damage / AbsorberTurret.this.damageScale * (bullet.vel.len() / AbsorberTurret.this.speedScale) * AbsorberTurret.this.powerProduction;
                } else {
                    var2 = this.target;
                    if (var2 instanceof Unit) {
                        Unit unit = (Unit)var2;
                        return unit.type == null ? 0.0F : unit.type.dpsEstimate / AbsorberTurret.this.damageScale * (unit.vel.len() / AbsorberTurret.this.speedScale) * AbsorberTurret.this.powerProduction;
                    } else {
                        return 0.0F;
                    }
                }
            }
        }
    }
}
