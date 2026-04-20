package unity.async;

import arc.Core;
import arc.func.Boolf;
import arc.math.Mathf;
import arc.struct.EnumSet;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.struct.ShortSeq;
import arc.util.Log;
import arc.util.Time;
import mindustry.Vars;
import mindustry.async.AsyncProcess;
import mindustry.core.ContentLoader;
import mindustry.ctype.Content;
import mindustry.ctype.ContentType;
import mindustry.entities.bullet.BulletType;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.Liquid;
import mindustry.type.UnitType;
import mindustry.world.Block;
import mindustry.world.blocks.ConstructBlock;
import mindustry.world.blocks.defense.MendProjector;
import mindustry.world.blocks.defense.OverdriveProjector;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.defense.turrets.PowerTurret;
import mindustry.world.blocks.defense.turrets.Turret;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.blocks.power.ItemLiquidGenerator;
import mindustry.world.blocks.power.PowerGenerator;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.blocks.units.Reconstructor;
import mindustry.world.blocks.units.UnitBlock;
import mindustry.world.blocks.units.UnitFactory;
import mindustry.world.consumers.Consume;
import mindustry.world.consumers.ConsumeItemFilter;
import mindustry.world.consumers.ConsumeItems;
import mindustry.world.consumers.ConsumeLiquid;
import mindustry.world.consumers.ConsumeLiquidFilter;
import mindustry.world.consumers.ConsumePower;
import mindustry.world.consumers.ConsumeType;
import unity.Unity;

public class ContentScoreProcess implements AsyncProcess {
    static final float stackConstant = 2.5F;
    static final int maxDepth = 128;
    final Seq<ContentScore> unloaded = new Seq();
    final Seq<ContentScore> allScores = new Seq();
    final Seq<Floor> ores = new Seq();
    volatile boolean processing = false;
    volatile boolean finished = false;
    EnumSet<ContentType> blackListed;
    ContentScore[][] scores;
    int depth;
    boolean depthLoaded;

    public ContentScoreProcess() {
        this.blackListed = EnumSet.of(new ContentType[]{ContentType.mech_UNUSED, ContentType.weather, ContentType.effect_UNUSED, ContentType.sector, ContentType.loadout_UNUSED, ContentType.typeid_UNUSED, ContentType.error, ContentType.planet, ContentType.ammo_UNUSED});
    }

    public void process() {
        if (!this.finished) {
            this.processing = true;
            long lt = System.nanoTime();
            ContentLoader l = Vars.content;
            Core.app.post(() -> Unity.print(new Object[]{"Content Scoring Begin"}));
            this.scores = new ContentScore[ContentType.all.length][0];

            for(int i = 0; i < ContentType.all.length; ++i) {
                if (!this.blackListed.contains(ContentType.all[i])) {
                    Seq<Content> c = l.getContentMap()[i];
                    this.scores[i] = new ContentScore[c.size];

                    for(Content cs : c) {
                        if (ContentType.all[i] == ContentType.block && (!((Block)cs).synthetic() || cs instanceof ConstructBlock)) {
                            if (cs instanceof Floor && (((Floor)cs).itemDrop != null || ((Floor)cs).liquidDrop != null)) {
                                this.ores.add((Floor)cs);
                            }
                        } else {
                            this.scores[i][cs.id] = new ContentScore(cs);
                        }
                    }
                }
            }

            for(Floor ore : this.ores) {
                if (ore.itemDrop != null) {
                    ContentScore cs = this.get(ore.itemDrop);
                    cs.artificial = false;
                }

                if (ore.liquidDrop != null) {
                    ContentScore cs = this.get(ore.liquidDrop);
                    cs.artificial = false;
                }
            }

            Core.app.post(() -> Unity.print(new Object[]{"Content Score Processing Begin"}));

            for(ContentScore score : this.allScores) {
                if (score.artificial) {
                    this.processContent(score);
                }
            }

            this.unloaded.removeAll((csx) -> {
                if (!csx.loaded) {
                    this.resetDepth();
                    csx.loadScore();
                }

                return csx.loaded;
            });
            this.clear();
            float time = (float)Time.nanosToMillis(System.nanoTime() - lt);
            StringBuilder builder = new StringBuilder(64);

            for(ContentType type : ContentType.all) {
                if (!this.blackListed.contains(type)) {
                    builder.append(type.toString()).append(":\n\n");

                    for(Content c : Vars.content.getContentMap()[type.ordinal()]) {
                        ContentScore cs = this.get(c);
                        if (cs != null) {
                            builder.append("  ").append(cs.toString()).append("\n");
                        }
                    }
                }
            }

            builder.append("Content Processed in: ").append(time);
            String out = builder.toString();
            Core.app.post(() -> Unity.print(new Object[]{out}));
            this.processing = false;
            this.finished = true;
        }

    }

    void clear() {
        for(ContentScore sc : this.allScores) {
            sc.consumesScore = null;
            sc.crafterRequirements = null;
            sc.outputs = null;
        }

    }

    void resetDepth() {
        this.depth = 0;
        this.depthLoaded = true;
    }

    ContentScore get(Content content) {
        if (content.id < this.scores[content.getContentType().ordinal()].length) {
            return this.scores[content.getContentType().ordinal()][content.id];
        } else {
            Log.warn("[scarlet]Array out of bounds for " + content.toString() + "![]", new Object[0]);
            Log.warn("[scarlet]Bad mod: " + (content.minfo != null && content.minfo.mod != null ? content.minfo.mod.name : "VANILLA") + "[]", new Object[0]);
            return this.scores[content.getContentType().ordinal()][0];
        }
    }

    ContentScore get(ContentType type, short id) {
        return this.scores[type.ordinal()][id];
    }

    <T extends Content> T getc(int type, short id) {
        return (T)(Vars.content.getContentMap()[type].get(id));
    }

    float getItemScore(Item item) {
        float energyScore = Mathf.sqr(item.charge + item.explosiveness + item.flammability + item.radioactivity);
        return (float)Math.pow((double)Math.max(item.hardness + 1, 1), (double)1.5F) * Math.max(item.cost + energyScore, 0.1F) * 1.5F;
    }

    float getLiquidScore(Liquid liquid) {
        return Mathf.sqr(liquid.flammability + liquid.explosiveness + Math.abs((liquid.temperature - 0.5F) * 2.0F)) + liquid.heatCapacity;
    }

    float getItemStackScore(ItemStack stack) {
        return this.get(stack.item).loadScore() * Mathf.pow((float)stack.amount, 0.4F);
    }

    float getItemStackScore(ItemStack stack, int size) {
        return this.get(stack.item).loadScore() * Mathf.pow((float)stack.amount, 1.0F / (2.5F / (float)size));
    }

    float getItemStackScore(short id, short amount) {
        return this.get(Vars.content.getByID(ContentType.item, id)).loadScore() * Mathf.pow((float)amount, 0.4F);
    }

    Item getMaxFilter(ShortSeq seq) {
        Item tmp = null;
        float last = 0.0F;

        for(int i = 0; i < seq.size; ++i) {
            ContentScore cs = this.get(ContentType.item, seq.get(i));
            if (cs.loadScore() > last || tmp == null) {
                tmp = (Item)cs.as();
                last = cs.score;
            }
        }

        return tmp;
    }

    Liquid getMaxFilterLiquid(ShortSeq seq) {
        Liquid tmp = null;
        float last = 0.0F;

        for(int i = 0; i < seq.size; ++i) {
            ContentScore cs = this.get(ContentType.liquid, seq.get(i));
            if (cs.loadScore() > last || tmp == null) {
                tmp = (Liquid)cs.as();
                last = cs.score;
            }
        }

        return tmp;
    }

    void processContent(ContentScore c) {
        if (c.content instanceof Block) {
            Block b = (Block)c.content;
            c.outputs.add(new BlockOutput(b));
            if (b.consumes.has(ConsumeType.item)) {
                Consume con = b.consumes.get(ConsumeType.item);
                if (con instanceof ConsumeItemFilter) {
                    ConsumeItemFilter cons = (ConsumeItemFilter)con;
                    c.itemFilter(cons.filter);
                } else if (con instanceof ConsumeItems) {
                    ConsumeItems cons = (ConsumeItems)con;

                    for(ItemStack stack : cons.items) {
                        c.addItemConsumes(stack.item, stack.amount);
                    }
                }

                if (c.consumesScore != null) {
                    ConsumesScore var10000 = c.consumesScore;
                    var10000.optional = (byte)(var10000.optional | (con.optional ? 1 : 0));
                }
            }

            if (b.consumes.has(ConsumeType.liquid)) {
                Consume con = b.consumes.get(ConsumeType.liquid);
                if (con instanceof ConsumeLiquidFilter) {
                    c.liquidFilter(((ConsumeLiquidFilter)con).filter, ((ConsumeLiquidFilter)con).amount);
                } else if (con instanceof ConsumeLiquid) {
                    ConsumeLiquid cons = (ConsumeLiquid)con;
                    c.addLiquidConsume(cons.liquid, cons.amount);
                }

                if (c.consumesScore != null) {
                    ConsumesScore var41 = c.consumesScore;
                    var41.optional = (byte)(var41.optional | (con.optional ? 2 : 0));
                }
            }

            if (b.consumes.has(ConsumeType.power)) {
                Consume con = b.consumes.get(ConsumeType.power);
                if (con instanceof ConsumePower) {
                    c.setPower(((ConsumePower)con).usage);
                }

                if (c.consumesScore != null) {
                    ConsumesScore var42 = c.consumesScore;
                    var42.optional = (byte)(var42.optional | (con.optional ? 4 : 0));
                }
            }

            if (c.content instanceof GenericCrafter) {
                GenericCrafter g = (GenericCrafter)c.content;
                float output = 0.0F;
                if (g.outputItems != null) {
                    for(ItemStack stack : g.outputItems) {
                        output += (float)stack.amount;
                    }
                }

                if (g.outputLiquid != null) {
                    output += g.outputLiquid.amount;
                }

                if (g.outputItems != null) {
                    for(ItemStack stack : g.outputItems) {
                        ContentScore cs = this.get(stack.item);
                        CrafterRequirements req = new CrafterRequirements(g);
                        req.outputAmount = output;
                        req.setConsumes(c.consumesScore);
                        cs.crafterRequirements.add(req);
                    }
                }

                if (g.outputLiquid != null) {
                    ContentScore cs = this.get(g.outputLiquid.liquid);
                    CrafterRequirements req = new CrafterRequirements(g);
                    req.outputAmount = output;
                    req.setConsumes(c.consumesScore);
                    cs.crafterRequirements.add(req);
                }
            } else if (c.content instanceof UnitFactory) {
                UnitFactory f = (UnitFactory)c.as();

                for(int i = 0; i < f.plans.size; ++i) {
                    UnitFactory.UnitPlan p = (UnitFactory.UnitPlan)f.plans.get(i);
                    ContentScore cs = this.get(p.unit);
                    UnitRequirements ur = new UnitRequirements(f);
                    ur.time = p.time;
                    ur.setConsumes(c.consumesScore);

                    for(ItemStack stack : p.requirements) {
                        ur.addItems(stack.item, (short)stack.amount);
                    }

                    cs.crafterRequirements.add(ur);
                }
            } else if (c.content instanceof Reconstructor) {
                Reconstructor r = (Reconstructor)c.as();

                for(UnitType[] upgrade : r.upgrades) {
                    ContentScore cs = this.get(upgrade[1]);
                    UnitRequirements ur = new UnitRequirements(r);
                    ur.prev = upgrade[0];
                    ur.time = r.constructTime;
                    ur.setConsumes(c.consumesScore);
                    cs.crafterRequirements.add(ur);
                }
            }
        }

    }

    public boolean shouldProcess() {
        return !this.processing && !this.finished;
    }

    private class ContentScore {
        Content content;
        boolean loaded = false;
        boolean outputLoaded = false;
        boolean artificial = true;
        boolean processing = false;
        float score;
        float outputScore;
        Seq<CrafterScore> crafterRequirements = new Seq();
        Seq<OutputHandler> outputs = new Seq();
        ConsumesScore consumesScore;

        ContentScore(Content content) {
            this.content = content;
            ContentScoreProcess.this.unloaded.add(this);
            ContentScoreProcess.this.allScores.add(this);
        }

        <T extends Content> T as() {
            return (T)this.content;
        }

        void liquidFilter(Boolf<Liquid> filter, float amount) {
            if (this.consumesScore == null || this.consumesScore.liquidFilter == null) {
                if (this.consumesScore == null) {
                    this.consumesScore = ContentScoreProcess.this.new ConsumesScore();
                }

                this.consumesScore.liquidFilter = new ShortSeq();
                this.consumesScore.liquidAmount = amount;
                ShortSeq liquidFilter = this.consumesScore.liquidFilter;

                for(Liquid liquid : Vars.content.liquids()) {
                    if (filter.get(liquid)) {
                        liquidFilter.add(liquid.id);
                    }
                }

            }
        }

        void itemFilter(Boolf<Item> filter) {
            if (this.consumesScore == null || this.consumesScore.itemFilter == null) {
                if (this.consumesScore == null) {
                    this.consumesScore = ContentScoreProcess.this.new ConsumesScore();
                }

                this.consumesScore.itemFilter = new ShortSeq();
                ShortSeq itemFilter = this.consumesScore.itemFilter;

                for(Item item : Vars.content.items()) {
                    if (filter.get(item)) {
                        itemFilter.add(item.id);
                    }
                }

            }
        }

        void addItemConsumes(Item item, int amount) {
            if (this.consumesScore == null) {
                this.consumesScore = ContentScoreProcess.this.new ConsumesScore();
            }

            if (this.consumesScore.itemConsumes == null) {
                this.consumesScore.itemConsumes = new ShortSeq();
            }

            this.consumesScore.itemConsumes.add(item.id, (short)amount);
        }

        void addLiquidConsume(Liquid liquid, float amount) {
            if (this.consumesScore == null) {
                this.consumesScore = ContentScoreProcess.this.new ConsumesScore();
            }

            this.consumesScore.liquid = liquid.id;
            this.consumesScore.liquidAmount = amount;
        }

        void setPower(float amount) {
            if (this.consumesScore == null) {
                this.consumesScore = ContentScoreProcess.this.new ConsumesScore();
            }

            this.consumesScore.power = amount;
        }

        float loadOutputScore() {
            if (this.outputLoaded) {
                return this.outputScore;
            } else {
                if (!this.outputs.isEmpty()) {
                    for(OutputHandler output : this.outputs) {
                        this.outputScore = Math.max(this.outputScore, output.calculateOutput());
                    }
                }

                this.outputLoaded = ContentScoreProcess.this.depthLoaded;
                return this.outputScore;
            }
        }

        float loadScore() {
            if (this.processing) {
                return 0.0F;
            } else if (this.loaded) {
                return this.score;
            } else {
                ++ContentScoreProcess.this.depth;
                if (ContentScoreProcess.this.depth <= 128 && ContentScoreProcess.this.depthLoaded) {
                    this.processing = true;
                    if (this.artificial) {
                        float ns = 0.0F;
                        if (!(this.content instanceof Block)) {
                            if (!this.crafterRequirements.isEmpty()) {
                                for(CrafterScore s : this.crafterRequirements) {
                                    ns = Math.max(ns, s.calculateScore());
                                }
                            }
                        } else {
                            Block block = (Block)this.content;

                            for(ItemStack stack : block.requirements) {
                                ns += ContentScoreProcess.this.getItemStackScore(stack, block.size);
                            }

                            ns *= block.buildCostMultiplier;
                        }

                        this.score = ns;
                    } else if (this.content instanceof Item) {
                        this.score = this.outputScore = ContentScoreProcess.this.getItemScore((Item)this.content);
                    } else if (this.content instanceof Liquid) {
                        this.score = this.outputScore = ContentScoreProcess.this.getLiquidScore((Liquid)this.content);
                    }

                    this.loadOutputScore();
                    this.loaded = ContentScoreProcess.this.depthLoaded;
                    this.processing = false;
                    return this.score;
                } else {
                    ContentScoreProcess.this.depthLoaded = false;
                    this.loaded = false;
                    return 0.0F;
                }
            }
        }

        public String toString() {
            return this.content.toString() + ": Score: " + this.score + ", Output Score: " + this.outputScore;
        }
    }

    private class CrafterRequirements extends CrafterScore {
        GenericCrafter crafter;
        float outputAmount = 1.0F;

        CrafterRequirements(GenericCrafter crafter) {
            this.crafter = crafter;
        }

        float calculateScore() {
            if (this.score == -1.0F) {
                this.score = 0.0F;
                if (this.itemStacks != null) {
                    for(int i = 0; i < this.itemStacks.size; i += 2) {
                        this.score += ContentScoreProcess.this.getItemStackScore(this.itemStacks.get(i), this.itemStacks.get(i + 1));
                    }
                }

                if (this.liquid != null) {
                    this.score += ContentScoreProcess.this.get(this.liquid).loadScore() * this.liquidAmount;
                }

                this.score += this.power;
                this.score /= this.outputAmount;
                this.score += ContentScoreProcess.this.get(this.crafter).loadScore() / 110.0F;
                this.score *= Mathf.sqrt(Math.max(this.crafter.craftTime, 0.1F) / 60.0F);
            }

            return this.score;
        }
    }

    private class UnitRequirements extends CrafterScore {
        UnitBlock block;
        UnitType prev;
        float time = 0.0F;

        UnitRequirements(UnitBlock block) {
            this.block = block;
        }

        float calculateScore() {
            if (this.score == -1.0F) {
                this.score = 0.0F;
                if (this.itemStacks != null) {
                    for(int i = 0; i < this.itemStacks.size; i += 2) {
                        this.score += ContentScoreProcess.this.getItemStackScore(this.itemStacks.get(i), this.itemStacks.get(i + 1));
                    }
                }

                if (this.liquid != null) {
                    this.score += ContentScoreProcess.this.get(this.liquid).loadScore() * this.liquidAmount;
                }

                this.score += this.power;
                if (this.prev != null) {
                    this.score += ContentScoreProcess.this.get(this.prev).loadScore();
                }

                this.score *= Mathf.sqrt(Math.max(this.time, 0.1F) / 60.0F);
                this.score += ContentScoreProcess.this.get(this.block).loadScore() / 110.0F;
            }

            return this.score;
        }
    }

    abstract class CrafterScore {
        float score = -1.0F;
        ShortSeq itemStacks;
        Liquid liquid;
        float liquidAmount;
        float power;

        abstract float calculateScore();

        void addItems(Item item, short amount) {
            if (this.itemStacks == null) {
                this.itemStacks = new ShortSeq();
            }

            this.itemStacks.add(item.id, amount);
        }

        void setConsumes(ConsumesScore cons) {
            if (cons != null) {
                if (cons.itemFilter != null && !cons.itemFilter.isEmpty()) {
                    Item i = ContentScoreProcess.this.getMaxFilter(cons.itemFilter);
                    if (i != null) {
                        this.itemStacks = new ShortSeq();
                        this.itemStacks.add(i.id, (short)1);
                    }
                } else {
                    this.itemStacks = cons.itemConsumes;
                }

                if (cons.liquidFilter != null && !cons.liquidFilter.isEmpty()) {
                    this.liquid = ContentScoreProcess.this.getMaxFilterLiquid(cons.liquidFilter);
                } else if (cons.liquid != -1) {
                    this.liquid = (Liquid)ContentScoreProcess.this.getc(ContentType.liquid.ordinal(), cons.liquid);
                }

                this.liquidAmount = cons.liquidAmount;
                this.power = cons.power;
            }
        }
    }

    class BlockOutput implements OutputHandler {
        Block block;
        float score;
        boolean loaded;

        public BlockOutput(Block b) {
            this.block = b;
        }

        public float calculateOutput() {
            if (this.loaded) {
                return this.score;
            } else {
                ContentScore cs = ContentScoreProcess.this.get(this.block);
                float conScore = cs.consumesScore != null ? cs.consumesScore.score() : 0.0F;
                float consTime = 5.0F;
                float score = (float)this.block.health / (float)this.block.size;
                if (this.block.hasItems) {
                    score += (float)this.block.itemCapacity * (1.0F + this.block.baseExplosiveness);
                }

                if (this.block.hasLiquids) {
                    score += this.block.liquidCapacity * this.block.liquidPressure * (1.0F + this.block.baseExplosiveness);
                }

                if (this.block instanceof Wall) {
                    Wall w = (Wall)this.block;
                    float ls = score;
                    score /= 4.0F;
                    if (w.chanceDeflect > 0.0F) {
                        score += ls * w.chanceDeflect;
                    }

                    if (w.lightningChance > 0.0F) {
                        score += w.lightningChance * ((float)w.lightningLength / 4.0F) * w.lightningDamage;
                    }
                } else if (this.block instanceof Turret) {
                    Turret t = (Turret)this.block;
                    int shots = t.alternate ? 1 : t.shots;
                    float inaccuracy = 1.0F - t.inaccuracy / 180.0F / (float)(shots * shots);
                    if (this.block instanceof ItemTurret) {
                        ItemTurret it = (ItemTurret)this.block;
                        float bs = 0.0F;

                        ObjectMap.Entry<Item, BulletType> entry;
                        for(ObjectMap.Entries var10 = it.ammoTypes.iterator(); var10.hasNext(); bs = Math.max(bs, ContentScoreProcess.this.get((Content)entry.value).loadOutputScore())) {
                            entry = (ObjectMap.Entry)var10.next();
                        }

                        score += bs * (float)shots * inaccuracy / t.reloadTime;
                    } else if (this.block instanceof PowerTurret) {
                        PowerTurret pt = (PowerTurret)this.block;
                        if (pt.shootType != null) {
                            score += ContentScoreProcess.this.get(pt.shootType).loadOutputScore() * (float)shots * inaccuracy / t.reloadTime;
                        }
                    }
                } else if (this.block instanceof MendProjector) {
                    MendProjector mp = (MendProjector)this.block;
                    float rr = (mp.range + mp.phaseRangeBoost) / 8.0F;
                    score += (mp.healPercent + mp.phaseBoost) / 100.0F * score * rr * rr / mp.reload;
                } else if (this.block instanceof OverdriveProjector) {
                    OverdriveProjector op = (OverdriveProjector)this.block;
                    float rr = (op.range + op.phaseRangeBoost) / 8.0F;
                    score += (op.speedBoostPhase + op.speedBoostPhase) / 100.0F * score * rr * rr / op.reload;
                } else if (this.block instanceof PowerGenerator) {
                    float power = ((PowerGenerator)this.block).powerProduction;
                    if (this.block instanceof ItemLiquidGenerator) {
                        ItemLiquidGenerator ilg = (ItemLiquidGenerator)this.block;
                        consTime = ilg.itemDuration + ilg.maxLiquidGenerate;
                    }

                    score += power;
                } else if (this.block instanceof GenericCrafter) {
                    GenericCrafter gc = (GenericCrafter)this.block;
                    if (gc.outputItems != null) {
                        for(ItemStack item : gc.outputItems) {
                            score += ContentScoreProcess.this.get(item.item).loadScore();
                        }
                    }

                    if (gc.outputLiquid != null) {
                        score += ContentScoreProcess.this.get(gc.outputLiquid.liquid).loadScore();
                    }

                    consTime = gc.craftTime;
                }

                score /= conScore / consTime + 1.0F;
                this.loaded = ContentScoreProcess.this.depthLoaded;
                this.score = score;
                return score;
            }
        }
    }

    private class ConsumesScore {
        ShortSeq itemConsumes;
        ShortSeq itemFilter;
        ShortSeq liquidFilter;
        short liquid;
        float liquidAmount;
        float power;
        float score;
        byte optional;

        private ConsumesScore() {
            this.liquid = -1;
            this.liquidAmount = 0.0F;
            this.score = -1.0F;
            this.optional = 0;
        }

        float score() {
            if (this.score == -1.0F) {
                float is = 0.0F;
                float ls = 0.0F;
                if (this.itemConsumes != null) {
                    for(int i = 0; i < this.itemConsumes.size; i += 2) {
                        is += ContentScoreProcess.this.getItemStackScore(this.itemConsumes.get(i), this.itemConsumes.get(i + 1));
                    }
                } else if (this.itemFilter != null && this.itemFilter.size > 0) {
                    is += ContentScoreProcess.this.get(ContentScoreProcess.this.getMaxFilter(this.itemFilter)).loadScore();
                }

                if ((this.optional & 1) != 0) {
                    is /= 10.0F;
                }

                if (this.liquid != -1) {
                    ls += ContentScoreProcess.this.get(ContentType.liquid, this.liquid).loadScore() * this.liquidAmount;
                } else if (this.liquidFilter != null && this.liquidFilter.size > 0) {
                    ls += ContentScoreProcess.this.get(ContentScoreProcess.this.getMaxFilterLiquid(this.liquidFilter)).loadScore() * this.liquidAmount;
                }

                if ((this.optional & 2) != 0) {
                    ls /= 10.0F;
                }

                float p = (this.optional & 4) != 0 ? this.power / 10.0F : this.power;
                this.score = is + ls + p;
            }

            return this.score;
        }
    }

    interface OutputHandler {
        float calculateOutput();
    }
}
