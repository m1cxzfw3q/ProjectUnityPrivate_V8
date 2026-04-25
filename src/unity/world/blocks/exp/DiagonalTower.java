package unity.world.blocks.exp;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.geom.Geometry;
import arc.scene.ui.layout.Table;
import arc.util.Eachable;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.ui.Styles;
import mindustry.world.Tile;
import unity.graphics.UnityPal;

public class DiagonalTower extends ExpTower {
    public DiagonalTower(String name) {
        super(name);
        this.configurable = this.saveConfig = true;
        this.lastConfig = false;
        this.config(Boolean.class, (build, value) -> build.diagonal = value);
        this.config(Integer.class, (build, value) -> {
            if (value >= 0) {
                value = value % 8;
                build.diagonal = value % 2 == 1;
                build.rotation = value / 2;
            }
        });
    }

    public void drawRequestRegion(BuildPlan req, Eachable<BuildPlan> list) {
        Draw.rect(this.topRegion, req.drawx(), req.drawy());
        this.drawRequestConfig(req, list);
    }

    public void drawPlace(int x, int y, int rotation, boolean valid) {
    }

    public void drawRequestConfig(BuildPlan req, Eachable<BuildPlan> list) {
        if (req.worldContext) {
            TextureRegion var10000;
            float var10001;
            float var10002;
            int var10003;
            byte var10004;
            label22: {
                var10000 = this.region;
                var10001 = req.drawx();
                var10002 = req.drawy();
                var10003 = req.rotation * 90 - 90;
                Object var4 = req.config;
                if (var4 instanceof Boolean) {
                    Boolean b = (Boolean)var4;
                    if (b) {
                        var10004 = 45;
                        break label22;
                    }
                }

                var10004 = 0;
            }

            Draw.rect(var10000, var10001, var10002, (float)(var10003 + var10004));
            Draw.mixcol();
            Object var7 = req.config;
            if (var7 instanceof Boolean) {
                Boolean b = (Boolean)var7;
                if (b) {
                    int dx = Geometry.d8edge(req.rotation).x;
                    int dy = Geometry.d8edge(req.rotation).y;
                    Drawf.dashLine(UnityPal.exp, (float)(req.x * 8) + (float)dx * 6.0F, (float)(req.y * 8) + (float)dy * 6.0F, (float)(req.x * 8 + dx * this.range * 8), (float)(req.y * 8 + dy * this.range * 8));
                } else {
                    this.drawPlaceDash(req.x, req.y, req.rotation);
                }
            }

        }
    }

    public class DiagonalTowerBuild extends ExpTower.ExpTowerBuild {
        public boolean diagonal = false;

        public DiagonalTowerBuild() {
            super(DiagonalTower.this);
        }

        public void drawSelectDash() {
            if (!this.diagonal) {
                super.drawSelectDash();
            } else {
                int dx = Geometry.d8edge(this.rotation).x;
                int dy = Geometry.d8edge(this.rotation).y;
                Drawf.dashLine(UnityPal.exp, this.x + (float)dx * 6.0F, this.y + (float)dy * 6.0F, this.x + (float)(dx * DiagonalTower.this.range * 8), this.y + (float)(dy * DiagonalTower.this.range * 8));
            }
        }

        public float laserRotation() {
            return this.diagonal ? this.rotdeg() + 45.0F : this.rotdeg();
        }

        public int shootExp(int amount) {
            if (!this.diagonal) {
                return super.shootExp(amount);
            } else {
                for(int i = 1; i <= DiagonalTower.this.range; ++i) {
                    Tile t = Vars.world.tile(this.tile.x + Geometry.d8edge(this.rotation).x * i, this.tile.y + Geometry.d8edge(this.rotation).y * i);
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
        }

        public int rotint() {
            return this.rotation * 2 + (this.diagonal ? 1 : 0);
        }

        public void buildConfiguration(Table table) {
            table.button(Icon.undo, Styles.clearTransi, () -> this.configure(this.rotint() + 1)).size(40.0F);
            table.image().color(Pal.gray).size(4.0F, 40.0F).pad(0.0F);
            table.button(Icon.redo, Styles.clearTransi, () -> this.configure(this.rotint() + 7)).size(40.0F);
        }

        public Object config() {
            return this.diagonal;
        }

        public void read(Reads read, byte revision) {
            super.read(read, revision);
            this.diagonal = read.bool();
        }

        public void write(Writes write) {
            super.write(write);
            write.bool(this.diagonal);
        }
    }
}
