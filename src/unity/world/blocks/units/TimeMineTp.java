package unity.world.blocks.units;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.world.Block;

public class TimeMineTp extends Block {
    public float range = 16.0F;
    public float reload = 30.0F;
    public float teleportRange = 500.0F;
    public Effect tpEffect;
    public float lifeTime = 180.0F;

    public TimeMineTp(String name) {
        super(name);
        this.update = this.sync = this.configurable = true;
        this.logicConfigurable = this.solid = this.rotate = this.noUpdateDisabled = false;
        this.size = 1;
        this.hasPower = this.hasItems = this.hasLiquids = false;
        this.timers = 2;
        this.config(Integer.class, (entity, value) -> {
            Building other = Vars.world.build(value);
            TimeMineTpBuild otherB = (TimeMineTpBuild)other;
            if (entity.teleporter == value) {
                entity.teleporter = -1;
                otherB.fromPos = -1;
            } else if (entity.tpValid(entity, other)) {
                entity.teleporter = other.pos();
                otherB.fromPos = entity.pos();
            }

            if (entity.teleporter == value && otherB.teleporter == entity.pos()) {
                otherB.teleporter = -1;
                entity.fromPos = -1;
                entity.teleporter = other.pos();
                otherB.fromPos = entity.pos();
            }

        });
    }

    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);
        Drawf.dashCircle((float)(x * 8), (float)(y * 8), this.range, Pal.accent);
        Drawf.dashCircle((float)(x * 8), (float)(y * 8), this.teleportRange, Pal.accent);
        Draw.reset();
    }

    public class TimeMineTpBuild extends Building {
        public Building dest;
        public Building from;
        public Seq<Unit> teleportUnit = new Seq();
        public int teleporter = -1;
        public int fromPos = -1;

        public void updateTile() {
            if (this.teleporter != -1) {
                this.dest = Vars.world.build(this.teleporter);
            }

            if (this.fromPos != -1) {
                this.from = Vars.world.build(this.fromPos);
            }

            if (this.connected() && this.unitCount(this.team) >= 3) {
                Units.nearbyEnemies(this.team, this.x, this.y, TimeMineTp.this.range, (e) -> {
                    e.impulseNet(Tmp.v1.trns(e.angleTo(this), e.dst(this) - e.vel.len()).scl(Time.delta * (float)Mathf.floor(Mathf.pow(e.mass(), Mathf.lerpDelta(0.2F, 0.5F, 0.3F / TimeMineTp.this.lifeTime)))));
                    e.disarmed = true;
                    if (e.dst(this) <= 4.0F) {
                        e.set(this);
                        e.vel.limit(0.01F);
                        if (!this.teleportUnit.contains(e) && this.teleportUnit.size < 5) {
                            this.teleportUnit.add(e);
                        }
                    }

                });
                if (this.teleportUnit.size > 0) {
                    if (this.timer(0, TimeMineTp.this.reload)) {
                        for(Unit toTeleport : this.teleportUnit) {
                            this.teleport(toTeleport);
                            this.teleportUnit.remove(toTeleport);
                        }

                        if (TimeMineTp.this.tpEffect != null) {
                            TimeMineTp.this.tpEffect.at(this.x, this.y);
                        }

                        this.timer.reset(0, 0.0F);
                    }
                } else {
                    this.timer.reset(0, 0.0F);
                }
            } else {
                this.timer.reset(1, 0.0F);
            }

            if (this.timer.getTime(1) >= 180.0F) {
                this.kill();
            }

        }

        public boolean onConfigureTileTapped(Building other) {
            if (this.tpValid(this, other)) {
                this.configure(other.pos());
                return false;
            } else {
                return true;
            }
        }

        public void drawConfigure() {
            if (this.dest != null && this.teleporter != -1) {
                Drawf.circles(this.dest.x, this.dest.y, 16.0F, Pal.accent);
                Drawf.arrow(this.x, this.y, this.dest.x, this.dest.y, 12.0F, 6.0F);
            }

            Drawf.dashCircle(this.x, this.y, TimeMineTp.this.teleportRange, Pal.accent);
        }

        public boolean connected() {
            return this.teleporter != -1 && this.dest == Vars.world.build(this.teleporter);
        }

        public boolean tpValid(Building tile, Building link) {
            return tile != link && tile.dst(link) <= TimeMineTp.this.teleportRange && link != null && tile.team == link.team && !link.dead() && link instanceof TimeMineTpBuild;
        }

        public int unitCount(Team t) {
            return Units.count(this.x, this.y, 16.0F, 16.0F, (e) -> e.team != t);
        }

        public void teleport(Unit unit) {
            unit.set(this.dest.x, this.dest.y);
            if (unit.isPlayer() && unit.getPlayer() == Vars.player && !Vars.headless) {
                Core.camera.position.set(this.dest.x, this.dest.y);
            }

        }

        public void write(Writes write) {
            super.write(write);
            write.i(this.teleporter);
            write.i(this.fromPos);
        }

        public void read(Reads read, byte revision) {
            super.read(read, revision);
            this.teleporter = read.i();
            this.fromPos = read.i();
        }
    }
}
