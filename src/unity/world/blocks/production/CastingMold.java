package unity.world.blocks.production;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.struct.OrderedSet;
import arc.struct.Seq;
import arc.util.Strings;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.Building;
import mindustry.type.Item;
import unity.graphics.UnityDrawf;
import unity.world.blocks.GraphBlock;
import unity.world.blocks.GraphBlockBase;
import unity.world.graph.CrucibleGraph;
import unity.world.meta.CrucibleData;
import unity.world.meta.GraphData;
import unity.world.meta.MeltInfo;
import unity.world.modules.GraphCrucibleModule;

public class CastingMold extends GraphBlock {
    final TextureRegion[] baseRegions = new TextureRegion[4];
    final TextureRegion[] topRegions = new TextureRegion[4];

    public CastingMold(String name) {
        super(name);
        this.rotate = this.solid = this.hasItems = true;
        this.itemCapacity = 1;
    }

    public void load() {
        super.load();

        for(int i = 0; i < 4; ++i) {
            this.baseRegions[i] = Core.atlas.find(this.name + "-base" + (i + 1));
            this.topRegions[i] = Core.atlas.find(this.name + "-top" + (i + 1));
        }

    }

    public class CastingMoldBuild extends GraphBlock.GraphBuild {
        static final String tooHot = "Too hot to cast!";
        final OrderedSet<Building> outputBuildings = new OrderedSet(8);
        MeltInfo castingMelt;
        float pourProgress;
        float castProgress;
        float castSpeed;

        public CastingMoldBuild() {
            super(CastingMold.this);
        }

        public void proxUpdate() {
            this.updateOutput();
        }

        public void onRotationChanged() {
            this.updateOutput();
        }

        public void displayExt(Table table) {
            table.row();
            table.table((sub) -> {
                sub.clearChildren();
                sub.left();
                if (this.castingMelt != null) {
                    sub.image(this.castingMelt.item.uiIcon).size(32.0F);
                    sub.label(() -> this.pourProgress == 1.0F && this.castSpeed == 0.0F ? "Too hot to cast!" : Strings.fixed((this.pourProgress + this.castProgress) * 50.0F, 2) + "%").color(Color.lightGray);
                } else {
                    sub.labelWrap("Nothing being casted").color(Color.lightGray);
                }

            }).left();
        }

        void updateOutput() {
            this.outputBuildings.clear();

            for(int i = 0; i < 8; ++i) {
                GraphData pos = this.gms.getConnectSidePos(i);
                Building b = this.nearby(pos.toPos.x, pos.toPos.y);
                if (b != null) {
                    if (b instanceof GraphBlockBase.GraphBuildBase) {
                        GraphBlockBase.GraphBuildBase g = (GraphBlockBase.GraphBuildBase)b;
                        if (g.crucible() != null) {
                            continue;
                        }
                    }

                    this.outputBuildings.add(b);
                }
            }

        }

        public void updatePost() {
            if (this.items.total() > 0) {
                this.pourProgress = 0.0F;
                this.castProgress = 0.0F;
                if (this.timer(CastingMold.this.timerDump, 5.0F)) {
                    Item itemPass = this.items.first();
                    OrderedSet.OrderedSetIterator var10 = this.outputBuildings.iterator();

                    while(var10.hasNext()) {
                        Building i = (Building)var10.next();
                        if (i.team == this.team && i.acceptItem(this, itemPass)) {
                            i.handleItem(this, itemPass);
                            this.items.remove(itemPass, 1);
                            return;
                        }
                    }
                }

            } else {
                GraphCrucibleModule dex = this.crucible();
                if (this.castingMelt == null) {
                    this.pourProgress = 0.0F;
                    this.castProgress = 0.0F;
                    Seq<CrucibleData> cc = dex.getContained();
                    MeltInfo[] melts = MeltInfo.all;
                    if (cc.isEmpty()) {
                        return;
                    }

                    CrucibleData hpMelt = null;
                    MeltInfo hpMeltType = null;

                    for(CrucibleData i : cc) {
                        MeltInfo meltType = melts[i.id];
                        if (i.meltedRatio * i.volume > 1.0F && (hpMelt == null || meltType.priority > hpMeltType.priority) && meltType.item != null) {
                            hpMelt = i;
                            hpMeltType = meltType;
                        }
                    }

                    if (hpMelt != null) {
                        ((CrucibleGraph)dex.getNetwork()).addLiquidToSlot(hpMelt, -1.0F);
                        this.castingMelt = hpMeltType;
                    }
                } else if (this.pourProgress < 1.0F) {
                    this.pourProgress += this.edelta() * 0.05F;
                    if (this.pourProgress > 1.0F) {
                        this.pourProgress = 1.0F;
                    }
                } else if (this.castProgress < 1.0F) {
                    this.castSpeed = Math.max(0.0F, (1.0F - (this.heat().getTemp() - 75.0F) / this.castingMelt.meltPoint) * this.castingMelt.meltSpeed * 1.5F);
                    this.castProgress += this.castSpeed;
                    if (this.castProgress > 1.0F) {
                        this.castProgress = 1.0F;
                    }
                } else {
                    this.items.add(this.castingMelt.item, 1);
                    this.castingMelt = null;
                }

            }
        }

        public void draw() {
            Draw.rect(CastingMold.this.baseRegions[this.rotation], this.x, this.y);
            if (this.castingMelt != null) {
                if (this.pourProgress > 0.0F) {
                    Draw.color(this.castingMelt.item.color, 1.0F - Math.abs(this.pourProgress - 0.5F) * 2.0F);
                    Draw.rect(CastingMold.this.liquidRegion, this.x, this.y, this.rotdeg());
                    Draw.color();
                    Draw.rect(this.castingMelt.item.fullIcon, this.x, this.y, this.pourProgress * 8.0F, this.pourProgress * 8.0F);
                }

                if (this.castProgress < 1.0F && this.pourProgress > 0.0F) {
                    UnityDrawf.drawHeat(this.castingMelt.item.fullIcon, this.x, this.y, 0.0F, Mathf.map(this.castProgress, 0.0F, 1.0F, this.castingMelt.meltPoint, 275.0F));
                }
            }

            Draw.rect(CastingMold.this.topRegions[this.rotation], this.x, this.y);
            this.drawTeamTop();
        }

        public void writeExt(Writes write) {
            write.i(this.castingMelt != null ? this.castingMelt.id : -1);
            write.f(this.pourProgress);
            write.f(this.castProgress);
        }

        public void readExt(Reads read, byte revision) {
            this.castingMelt = MeltInfo.all[read.i()];
            this.pourProgress = read.f();
            this.castProgress = read.f();
        }
    }
}
