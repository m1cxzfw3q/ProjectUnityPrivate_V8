package unity.world.graph;

import arc.graphics.Color;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.struct.OrderedSet;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.world.Tile;
import unity.graphics.UnityPal;
import unity.world.blocks.GraphBlockBase;
import unity.world.graphs.GraphCrucible;
import unity.world.meta.CrucibleData;
import unity.world.meta.CrucibleRecipe;
import unity.world.meta.MeltInfo;
import unity.world.modules.GraphCrucibleModule;

public class CrucibleGraph extends BaseGraph<GraphCrucibleModule, CrucibleGraph> {
    static final float[] capacityMul = new float[]{0.0F, 0.1F, 0.2F, 0.5F, 1.0F};
    public final Color color;
    final Seq<CrucibleData> contains;
    float totalVolume;
    float totalCapacity;
    float containedAmCache;
    boolean containChanged;
    boolean crafts;

    public CrucibleGraph() {
        this.color = Color.clear.cpy();
        this.contains = new Seq();
        this.containChanged = true;
        this.crafts = true;
    }

    public CrucibleGraph create() {
        return new CrucibleGraph();
    }

    public float getVolumeContained() {
        if (this.containChanged) {
            this.containedAmCache = 0.0F;
            int i = 0;

            for(int len = this.contains.size; i < len; ++i) {
                this.containedAmCache += ((CrucibleData)this.contains.get(i)).volume;
            }
        }

        return this.containedAmCache;
    }

    public boolean addItem(Item item) {
        MeltInfo meltProd = (MeltInfo)MeltInfo.map.get(item);
        if (meltProd == null) {
            return false;
        } else {
            return meltProd.additive ? this.addMeltItem(meltProd.additiveID, meltProd.additiveWeight, false) : this.addMeltItem(meltProd, 1.0F, false);
        }
    }

    public CrucibleData getMeltFromID(int id) {
        return (CrucibleData)this.contains.find((i) -> i.id == id);
    }

    public boolean addMeltItem(MeltInfo meltProd, float am, boolean liquid) {
        CrucibleData avalslot = null;
        int totalContained = 0;

        for(CrucibleData i : this.contains) {
            if (i.id == meltProd.id) {
                avalslot = i;
            }

            totalContained = (int)((float)totalContained + i.volume);
        }

        if ((float)totalContained + am > this.totalCapacity) {
            return false;
        } else {
            if (avalslot != null) {
                if (liquid) {
                    this.addLiquidToSlot(avalslot, am);
                } else {
                    this.addSolidToSlot(avalslot, am);
                }
            } else {
                this.contains.add(new CrucibleData(meltProd.id, am, liquid ? 1.0F : 0.0F, meltProd.item));
            }

            this.containChanged = true;
            return true;
        }
    }

    public boolean canContainMore(float amount) {
        return this.getVolumeContained() + amount <= this.totalCapacity;
    }

    public float getRemainingSpace() {
        return Math.max(0.0F, this.totalCapacity - this.getVolumeContained());
    }

    void addSolidToSlot(CrucibleData slot, float am) {
        float melted = slot.meltedRatio * slot.volume;
        slot.volume += am;
        slot.meltedRatio = melted / slot.volume;
        if (slot.volume <= 0.0F || slot.meltedRatio <= 0.0F) {
            slot.meltedRatio = 0.0F;
        }

        this.containChanged = true;
    }

    public void addLiquidToSlot(CrucibleData slot, float am) {
        float melted = slot.meltedRatio * slot.volume + am;
        slot.volume += am;
        slot.meltedRatio = melted / slot.volume;
        if (slot.volume <= 0.0F || slot.meltedRatio <= 0.0F) {
            slot.meltedRatio = 0.0F;
        }

        this.containChanged = true;
    }

    void copyGraphStatsFrom(CrucibleGraph graph) {
    }

    void updateOnGraphChanged() {
        this.totalCapacity = 0.0F;
        this.crafts = false;

        GraphCrucibleModule module;
        for(OrderedSet.OrderedSetIterator var1 = this.connected.iterator(); var1.hasNext(); this.crafts |= ((GraphCrucible)module.graph).doesCrafting) {
            module = (GraphCrucibleModule)var1.next();
            int bitmask = 0;
            if (!module.initialized()) {
                module.tilingIndex = 0;
                return;
            }

            int directNeighbour = 0;

            for(int i = 0; i < 8; ++i) {
                Tile tile = module.parent.build.tile().nearby(Geometry.d8(i));
                if (tile != null) {
                    Building var8 = tile.build;
                    if (var8 instanceof GraphBlockBase.GraphBuildBase) {
                        GraphBlockBase.GraphBuildBase build = (GraphBlockBase.GraphBuildBase)var8;
                        GraphCrucibleModule conModule = build.crucible();
                        if (conModule != null && !conModule.dead() && this.canConnect(module, conModule)) {
                            if (i % 2 == 0) {
                                ++directNeighbour;
                            }

                            bitmask += 1 << i;
                        }
                    }
                }
            }

            module.tilingIndex = bitmask;
            module.liquidCap = (module.parent.build.block().size == 1 ? capacityMul[directNeighbour] : 1.0F) * ((GraphCrucible)module.graph).baseLiquidCapcity;
            this.totalCapacity += module.liquidCap;
        }

        if (this.getVolumeContained() > this.totalCapacity) {
            float decRatio = this.totalCapacity / this.getVolumeContained();
            int i = 0;

            for(int len = this.contains.size; i < len; ++i) {
                CrucibleData var10000 = (CrucibleData)this.contains.get(i);
                var10000.volume *= decRatio;
            }

            this.containChanged = true;
        }

    }

    public float getAverageTemp() {
        float speed = 0.0F;
        int count = 0;
        OrderedSet.OrderedSetIterator var3 = this.connected.iterator();

        while(var3.hasNext()) {
            GraphCrucibleModule module = (GraphCrucibleModule)var3.next();
            if (((GraphCrucible)module.graph).doesCrafting) {
                speed += module.parent.build.heat().getTemp();
                ++count;
            }
        }

        if (count == 0) {
            return 0.0F;
        } else {
            return speed / (float)count;
        }
    }

    float getAverageTempDecay(float meltPoint, float meltSpeed, float tmpDep, float coolDep) {
        float speed = 0.0F;
        int count = 0;
        OrderedSet.OrderedSetIterator var7 = this.connected.iterator();

        while(var7.hasNext()) {
            GraphCrucibleModule module = (GraphCrucibleModule)var7.next();
            if (((GraphCrucible)module.graph).doesCrafting) {
                float temp = module.parent.build.heat().getTemp();
                if (temp > meltPoint) {
                    speed += (1.0F + temp / meltPoint * tmpDep) * meltSpeed;
                } else {
                    speed -= (1.0F - temp / meltPoint) * coolDep * meltSpeed;
                }

                ++count;
            }
        }

        if (count == 0) {
            return 0.0F;
        } else {
            return speed / (float)count;
        }
    }

    float getAverageMeltSpeed(MeltInfo m, float tmpDep, float coolDep) {
        return this.getAverageTempDecay(m.meltPoint, m.meltSpeed, tmpDep, coolDep);
    }

    float getAverageMeltSpeedIndex(int index, float tmpDep, float coolDep) {
        return this.getAverageMeltSpeed(MeltInfo.all[index], tmpDep, coolDep);
    }

    public void updateColor() {
        this.color.set(0.0F, 0.0F, 0.0F);
        float tLiquid = 0.0F;

        for(CrucibleData i : this.contains) {
            if (i.meltedRatio > 0.0F) {
                float liquidVol = i.meltedRatio * i.volume;
                tLiquid += liquidVol;
                Color itemCol = UnityPal.youngchaGray;
                if (i.item != null) {
                    itemCol = i.item.color;
                }

                Color var10000 = this.color;
                var10000.r += itemCol.r * liquidVol;
                var10000 = this.color;
                var10000.g += itemCol.g * liquidVol;
                var10000 = this.color;
                var10000.b += itemCol.b * liquidVol;
            }
        }

        float invt = 1.0F / tLiquid;
        this.color.mul(invt).a(Mathf.clamp(2.0F * tLiquid / this.totalCapacity));
    }

    void updateGraph() {
        if (!this.contains.isEmpty()) {
            if (!this.crafts) {
                this.removeEmptyMelts();
                this.updateColor();
            } else {
                float capcityMul = Mathf.sqrt(this.totalCapacity / 15.0F);

                for(CrucibleData i : this.contains) {
                    float meltMul = Time.delta / i.volume;
                    if (i.id < MeltInfo.all.length) {
                        MeltInfo m = MeltInfo.all[i.id];
                        i.meltedRatio += meltMul * this.getAverageMeltSpeed(m, 0.002F, 0.5F) * 0.4F * capcityMul;
                        i.meltedRatio = Mathf.clamp(i.meltedRatio);
                        if (m.evaporationTemp >= 0.0F) {
                            float evap = this.getAverageTempDecay(m.evaporationTemp, m.evaporation, 0.0F, 1.0F);
                            if (evap > 0.0F) {
                                i.volume -= evap;
                                this.containChanged = true;
                            }
                        }
                    }
                }

                for(CrucibleRecipe z : CrucibleRecipe.all) {
                    boolean valid = true;
                    float maxCraftable = 9999999.0F;
                    int len = z.input.length;
                    int[] inputSlots = new int[len];

                    for(int r = 0; r < len; ++r) {
                        boolean found = false;

                        for(CrucibleData ingre : this.contains) {
                            CrucibleRecipe.InputRecipe alyInput = z.input[r];
                            if (MeltInfo.all[ingre.id] == alyInput.material && (!alyInput.needsLiquid || ingre.meltedRatio > 0.0F)) {
                                found = true;
                                inputSlots[r] = ingre.id;
                                maxCraftable = Math.min(maxCraftable, (alyInput.needsLiquid ? ingre.meltedRatio : 1.0F) * ingre.volume / alyInput.amount);
                                break;
                            }
                        }

                        if (!found) {
                            valid = false;
                            break;
                        }
                    }

                    if (valid && maxCraftable > 0.0F) {
                        float craftAm = Math.min(maxCraftable, z.alloySpeed * Time.delta * 0.2F * capcityMul);
                        if (craftAm <= 0.0F) {
                            return;
                        }

                        for(int r = 0; r < len; ++r) {
                            CrucibleRecipe.InputRecipe alyInput = z.input[r];
                            if (alyInput.needsLiquid) {
                                this.addLiquidToSlot((CrucibleData)this.contains.get(inputSlots[r]), -alyInput.amount * craftAm);
                            } else {
                                CrucibleData var10000 = (CrucibleData)this.contains.get(inputSlots[r]);
                                var10000.volume -= alyInput.amount * craftAm;
                                this.containChanged = true;
                            }
                        }

                        this.addMeltItem(z.melt, craftAm, true);
                    }
                }

                this.removeEmptyMelts();
                this.updateColor();
            }
        }
    }

    void removeEmptyMelts() {
        this.contains.removeAll((i) -> i.volume <= 0.0F);
    }

    void killGraph() {
        OrderedSet.OrderedSetIterator var1 = this.connected.iterator();

        while(var1.hasNext()) {
            GraphCrucibleModule module = (GraphCrucibleModule)var1.next();
            Seq<CrucibleData> nc = new Seq();
            float ratio = module.liquidCap / this.totalCapacity;

            for(CrucibleData i : this.contains) {
                nc.add(new CrucibleData(i.id, i.volume * ratio, i.meltedRatio, i.item));
            }

            module.propsList.put(module.getPortOfNetwork(this), nc);
        }

        this.connected.clear();
    }

    void updateDirect() {
    }

    void addMergeStats(GraphCrucibleModule module) {
        int port = module.getPortOfNetwork(this);
        this.totalCapacity += module.liquidCap;
        Seq<CrucibleData> cc = (Seq)module.propsList.get(port);
        if (cc != null && !cc.isEmpty()) {
            MeltInfo[] melts = MeltInfo.all;

            for(CrucibleData i : cc) {
                this.addMeltItem(melts[i.id], i.volume * (1.0F - i.meltedRatio), false);
                this.addMeltItem(melts[i.id], i.volume * i.meltedRatio, true);
            }

        }
    }

    void mergeStats(CrucibleGraph graph) {
        MeltInfo[] melts = MeltInfo.all;
        this.totalCapacity += graph.totalCapacity;

        for(CrucibleData i : graph.contains) {
            this.addMeltItem(melts[i.id], i.volume * (1.0F - i.meltedRatio), false);
            this.addMeltItem(melts[i.id], i.volume * i.meltedRatio, true);
        }

    }

    public Seq<CrucibleData> contains() {
        return this.contains;
    }

    public float totalCapacity() {
        return this.totalCapacity;
    }
}
