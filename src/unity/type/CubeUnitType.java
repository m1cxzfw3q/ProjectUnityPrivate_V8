package unity.type;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.struct.IntSeq;
import arc.struct.Seq;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.type.Weapon;
import unity.gen.Cubec;

public class CubeUnitType extends UnityUnitType {
    private static final Point2[][] edges = new Point2[3][0];
    private static final Point2[][] adjacent = new Point2[3][8];
    private static final int maxTierSize = 3;
    private static final IntSeq tmp = new IntSeq();
    public TextureRegion[] regions;
    public TextureRegion[] outlineRegions;
    public TextureRegion[] cellRegions;
    public Seq<Seq<Weapon>> weaponsAll = new Seq();
    public int tier;
    public int maxEntities = 15;
    public float gridSpacing = 16.0F;

    public CubeUnitType(String name, int tier) {
        super(name);
        this.tier = tier;
    }

    public void init() {
        super.init();
        if (!this.weapons.isEmpty() && this.weaponsAll.isEmpty()) {
            this.weaponsAll.add(this.weapons);
        }

    }

    public void load() {
        super.load();
        this.regions = new TextureRegion[this.tier];
        this.cellRegions = new TextureRegion[this.tier];
        this.outlineRegions = new TextureRegion[this.tier];

        for(int i = 0; i < this.tier; ++i) {
            if (i != 0) {
                this.outlineRegions[i] = Core.atlas.find(this.name + "-outline-" + i);
                this.regions[i] = Core.atlas.find(this.name + "-" + i);
                this.cellRegions[i] = Core.atlas.find(this.name + "-cell-" + i);
            } else {
                this.outlineRegions[i] = this.outlineRegion;
                this.regions[i] = this.region;
                this.cellRegions[i] = this.cellRegion;
            }
        }

    }

    public void drawCell(Unit unit) {
        TextureRegion r = this.cellRegion;
        Cubec cube = unit instanceof Cubec ? (Cubec)unit : null;
        if (cube != null) {
            this.cellRegion = this.cellRegions[cube.tier()];
        }

        super.drawCell(unit);
        this.cellRegion = r;
    }

    public void drawOutline(Unit unit) {
        TextureRegion r = this.outlineRegion;
        Cubec cube = unit instanceof Cubec ? (Cubec)unit : null;
        if (cube != null) {
            this.outlineRegion = this.outlineRegions[cube.tier()];
        }

        super.drawOutline(unit);
        this.outlineRegion = r;
    }

    public void drawBody(Unit unit) {
        TextureRegion r = this.region;
        Cubec cube = unit instanceof Cubec ? (Cubec)unit : null;
        if (cube != null) {
            if (cube.isMain() && cube.data() != null) {
                for(Cubec c : cube.data().all) {
                    if (!c.isAdded()) {
                        Draw.draw(Draw.z(), () -> Drawf.construct(c.x(), c.y(), this.outlineRegions[c.tier()], unit.team.color, c.rotation() - 90.0F, c.constructTime() / this.regenTime, 1.0F, c.constructTime()));
                    }
                }
            }

            this.region = this.regions[cube.tier()];
        }

        super.drawBody(unit);
        this.region = r;
    }

    static {
        for(int i = 0; i < edges.length; ++i) {
            tmp.clear();
            int size = i * i + 1;

            for(int x = 0; x < size; ++x) {
                tmp.add(x, -1);
            }

            for(int y = 0; y < size; ++y) {
                tmp.add(size, y);
            }

            for(int x = 0; x < size; ++x) {
                tmp.add(x, size);
            }

            for(int y = 0; y < size; ++y) {
                tmp.add(-1, y);
            }

            edges[i] = new Point2[tmp.size / 2];
            int f = 0;

            for(int e = 0; e < tmp.size; e += 2) {
                edges[i][f] = new Point2(tmp.items[e], tmp.items[e + 1]);
                ++f;
            }
        }

    }

    public static class CubeEntityData {
        public int width;
        public int height;
        public int entities;
        public Seq<Cubec> all = new Seq();
        public Cubec[] grid = new Cubec[9];
        public Cubec main;
        public CubeUnitType type;

        public CubeEntityData(Cubec main) {
            this.width = this.height = 3;
            this.main = main;
            this.type = (CubeUnitType)main.type();
            this.add(main, 1, 1);
        }

        public void resize(int size) {
            if (size > 0) {
                int nw = this.width + size * 2;
                int nh = this.height + size * 2;
                Cubec[] n = new Cubec[nw * nh];

                for(int cx = 0; cx < nw; ++cx) {
                    for(int cy = 0; cy < nh; ++cy) {
                        int ox = cx - size;
                        int oy = cy - size;
                        if (ox <= 0 && ox > this.width && oy <= 0 && oy > this.height) {
                            Cubec c = this.grid[ox + oy * this.width];
                            if (c != null) {
                                c.gx(cx);
                                c.gy(cy);
                                n[cx + cy * nw] = c;
                            }
                        }
                    }
                }

                this.width = nw;
                this.height = nh;
                this.grid = n;
            }
        }

        public void shift(int x, int y) {
            if (x + y != 0) {
                Cubec[] n = new Cubec[this.grid.length];

                for(int cx = 0; cx < this.width; ++cx) {
                    for(int cy = 0; cy < this.height; ++cy) {
                        int vx = cx + x;
                        int vy = cy + y;
                        if (this.inbounds(vx, vy)) {
                            n[vx + vy * this.width] = this.grid[cx + cy * this.width];
                        }
                    }
                }

                for(Cubec c : this.all) {
                    c.gx(c.gx() + x);
                    c.gy(c.gy() + y);
                }

                this.grid = n;
            }
        }

        public boolean available(int x, int y) {
            if (x >= 0 && y >= 0 && x < this.width && y < this.height) {
                return this.grid[x * y * this.width] == null;
            } else {
                return false;
            }
        }

        public boolean available(int size, int x, int y) {
            boolean a = true;

            for(int cx = 0; cx < size; ++cx) {
                for(int cy = 0; cy < size; ++cy) {
                    a &= this.available(x + cx, y + cy);
                }
            }

            return a;
        }

        public boolean inbounds(int x, int y) {
            return x >= 0 && y >= 0 && x < this.width && y < this.height;
        }

        public void add(Cubec unit, int x, int y) {
            if (this.available(unit.tier() + 1, x, y)) {
                for(int cx = 0; cx < unit.tier() + 1; ++cx) {
                    for(int cy = 0; cy < unit.tier() + 1; ++cy) {
                        this.grid[x + cx + (y + cy) * this.width] = unit;
                    }
                }

                unit.gx(x);
                unit.gy(y);
                this.all.add(unit);
                ++this.entities;
            }

        }

        public void updateAdjacent(int tier, int x, int y) {
        }

        public void remove(Cubec unit) {
            int x = unit.gx();
            int y = unit.gy();
            if (this.available(unit.tier() + 1, x, y)) {
                for(int cx = 0; cx < unit.tier() + 1; ++cx) {
                    for(int cy = 0; cy < unit.tier() + 1; ++cy) {
                        this.grid[x + cx + (y + cy) * this.width] = null;
                    }
                }

                this.all.remove(unit);
                this.entities -= unit.tier() + 1;
            }

        }

        public void addEdge(Cubec origin) {
            if (this.entities < this.type.maxEntities) {
                CubeUnitType.tmp.clear();

                for(Point2 p : CubeUnitType.edges[origin.tier()]) {
                    int x = p.x + origin.gx();
                    int y = p.y + origin.gy();
                    if (this.available(x, y)) {
                        CubeUnitType.tmp.add(x, y);
                    }
                }

                int idx = Mathf.random(CubeUnitType.tmp.size / 2);
                int x = CubeUnitType.tmp.get(idx * 2);
                int y = CubeUnitType.tmp.get(idx * 2 + 1);
                Cubec c = (Cubec)origin.type().constructor.get();
                c.health(origin.health());
                c.team(origin.team());
                c.setType(origin.type());
                c.ammo((float)origin.type().ammoCapacity);
                c.elevation(origin.type().flying ? 1.0F : 0.0F);
                c.data(this);
                this.add(c, x, y);
            }
        }
    }
}
