package unity.world.blocks.exp;

import arc.Core;
import arc.audio.Sound;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.geom.Geometry;
import arc.util.Eachable;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.entities.units.BuildPlan;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Sounds;
import mindustry.graphics.Drawf;
import mindustry.world.Tile;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import unity.graphics.UnityPal;

public class ExpTower extends ExpTank {
    public int range = 5;
    public float reloadTime = 10.0F;
    public float manualReload = 20.0F;
    public boolean buffer = false;
    public int bufferExp = 20;
    public float laserWidth = 0.5F;
    public TextureRegion laser;
    public TextureRegion laserEnd;
    public float elevation = -1.0F;
    public Sound shootSound;
    public float shootSoundVolume;

    public ExpTower(String name) {
        super(name);
        this.shootSound = Sounds.plasmadrop;
        this.shootSoundVolume = 0.05F;
        this.rotate = true;
        this.outlineIcon = true;
        this.drawArrow = false;
        this.noUpdateDisabled = false;
    }

    public void init() {
        super.init();
        if (this.elevation < 0.0F) {
            this.elevation = (float)this.size / 2.0F;
        }

    }

    public void load() {
        super.load();
        this.laser = Core.atlas.find(this.name + "laser", "unity-exp-laser");
        this.laserEnd = Core.atlas.find(this.name + "laser-end", "unity-exp-laser-end");
        this.topRegion = Core.atlas.find(this.name + "-base", "block-" + this.size);
    }

    public void setStats() {
        super.setStats();
        if (this.buffer) {
            this.stats.add(Stat.output, "@ [lightgray]@[]", new Object[]{Core.bundle.format("explib.expAmount", new Object[]{(float)this.bufferExp / this.manualReload * 60.0F}), StatUnit.perSecond.localized()});
        }

    }

    public void drawPlaceDash(int x, int y, int rotation) {
        int dx = Geometry.d4x(rotation);
        int dy = Geometry.d4y(rotation);
        Drawf.dashLine(UnityPal.exp, (float)(x * 8) + (float)dx * 6.0F, (float)(y * 8) + (float)dy * 6.0F, (float)(x * 8 + dx * this.range * 8), (float)(y * 8 + dy * this.range * 8));
    }

    public void drawRequestRegion(BuildPlan req, Eachable<BuildPlan> list) {
        Draw.rect(this.topRegion, req.drawx(), req.drawy());
        Draw.rect(this.region, req.drawx(), req.drawy(), (float)(req.rotation * 90 - 90));
    }

    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);
        this.drawPlaceDash(x, y, rotation);
    }

    protected TextureRegion[] icons() {
        return new TextureRegion[]{this.topRegion, this.region};
    }

    public class ExpTowerBuild extends ExpTank.ExpTankBuild {
        public float reload = 0.0F;
        protected Tile lastTarget = null;
        private float heat = 0.0F;
        private int lastSent = 0;

        public ExpTowerBuild() {
            super(ExpTower.this);
        }

        public int unloadExp(int amount) {
            return 0;
        }

        public int handleTower(int amount, float angle) {
            if (ExpTower.this.buffer && this.exp > 0) {
                return 0;
            } else {
                int a = this.handleExp(amount);
                if (a > 0 && this.reload >= ExpTower.this.reloadTime && !Angles.near(angle + 180.0F, this.laserRotation(), 1.0F)) {
                    this.shoot();
                }

                return a;
            }
        }

        public boolean handleOrb(int orbExp) {
            int a = this.handleExp(orbExp);
            if (a <= 0) {
                return false;
            } else {
                if (this.reload >= ExpTower.this.manualReload) {
                    this.shoot();
                }

                return true;
            }
        }

        public void updateTile() {
            super.updateTile();
            if (ExpTower.this.buffer && this.exp <= 0) {
                this.reload = 0.0F;
            } else {
                this.reload += this.delta();
            }

            if (this.heat > 0.0F) {
                this.heat -= this.delta();
            }

            if (this.reload >= ExpTower.this.manualReload && this.exp > 0) {
                this.shoot();
            }

        }

        public void draw() {
            Draw.rect(ExpTower.this.topRegion, this.x, this.y);
            Draw.color();
            Tmp.v1.trns(this.laserRotation(), this.heat / ExpTower.this.manualReload);
            Draw.z(50.0F);
            Drawf.shadow(ExpTower.this.region, this.x - ExpTower.this.elevation, this.y - ExpTower.this.elevation, this.laserRotation() - 90.0F);
            Draw.rect(ExpTower.this.region, this.x - Tmp.v1.x, this.y - Tmp.v1.y, this.laserRotation() - 90.0F);
            this.drawLaser();
        }

        public void drawSelect() {
            this.drawSelectDash();
            super.drawSelect();
        }

        public void drawSelectDash() {
            int dx = Geometry.d4x(this.rotation);
            int dy = Geometry.d4y(this.rotation);
            Drawf.dashLine(UnityPal.exp, this.x + (float)dx * 6.0F, this.y + (float)dy * 6.0F, this.x + (float)(dx * ExpTower.this.range * 8), this.y + (float)(dy * ExpTower.this.range * 8));
        }

        public float laserRotation() {
            return this.rotdeg();
        }

        public void drawLaser() {
            if (!(this.heat <= 0.0F) && this.lastSent != 0 && this.lastTarget != null) {
                Draw.z(101.0F);
                float fout = this.heat / ExpTower.this.manualReload;
                float f = (float)this.lastSent / (float)ExpTower.this.expCapacity;
                f = (0.7F + 0.3F * f) * ExpTower.this.laserWidth;
                Tmp.v2.set(this.lastTarget.worldx(), this.lastTarget.worldy());
                Tmp.v1.set(Tmp.v2).sub(this).nor().scl((float)(ExpTower.this.size * 8) / 2.0F);
                Tmp.v2.sub(Tmp.v1);
                Tmp.v1.add(this);
                Lines.stroke(f * fout);
                Drawf.laser((Team)null, ExpTower.this.laser, ExpTower.this.laserEnd, Tmp.v1.x, Tmp.v1.y, Tmp.v2.x, Tmp.v2.y, f * fout);
                Draw.reset();
            }
        }

        public void shoot() {
            if (this.enabled) {
                this.reload = 0.0F;
                if (this.exp > 0) {
                    int a = this.shootExp(ExpTower.this.buffer ? ExpTower.this.bufferExp : this.exp);
                    if (a > 0) {
                        this.exp -= a;
                        this.heat = ExpTower.this.manualReload;
                        this.lastSent = a;
                        ExpTower.this.shootSound.at(this.x, this.y, 1.0F, ExpTower.this.shootSoundVolume);
                    }

                }
            }
        }

        public int shootExp(int amount) {
            for(int i = 1; i <= ExpTower.this.range; ++i) {
                Tile t = Vars.world.tile(this.tile.x + Geometry.d4x(this.rotation) * i, this.tile.y + Geometry.d4y(this.rotation) * i);
                if (t != null) {
                    Building var5 = t.build;
                    if (var5 instanceof ExpHolder) {
                        ExpHolder exp = (ExpHolder)var5;
                        int a = exp.handleTower(amount, this.laserRotation());
                        if (a > 0) {
                            this.lastTarget = t;
                            return a;
                        }
                    }
                }
            }

            return 0;
        }

        public void read(Reads read, byte revision) {
            super.read(read, revision);
            this.reload = read.f();
        }

        public void write(Writes write) {
            super.write(write);
            write.f(this.reload);
        }
    }
}
