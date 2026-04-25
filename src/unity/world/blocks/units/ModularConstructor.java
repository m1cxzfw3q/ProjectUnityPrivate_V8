package unity.world.blocks.units;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.ButtonGroup;
import arc.scene.ui.ImageButton;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Structs;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import java.util.Arrays;
import mindustry.Vars;
import mindustry.entities.Units;
import mindustry.gen.Building;
import mindustry.gen.Tex;
import mindustry.gen.Unit;
import mindustry.graphics.Shaders;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.UnitType;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.consumers.ConsumeItemDynamic;
import mindustry.world.consumers.ConsumeType;
import unity.graphics.UnityPal;
import unity.world.modules.ModularConstructorModule;

public class ModularConstructor extends Block {
    public TextureRegion[] topRegions;
    public float minSize = 17.5F;
    public Seq<ModularConstructorPlan> plans = new Seq(4);
    public float efficiencyPerTier = 80.0F;
    public float maxEfficiency = 300.0F;
    public Color buildColor;
    public Vec2[] moduleNodes;
    public boolean mirrorNodes;
    public int moduleSize;
    public int moduleConnections;
    public Block moduleBlock;
    protected int maxTier;
    protected int[] capacities;
    protected Seq<ModularConstructorPlan> sortedPlans;

    public ModularConstructor(String name) {
        super(name);
        this.buildColor = UnityPal.advance;
        this.moduleNodes = new Vec2[]{new Vec2(3.5F, 9.5F)};
        this.mirrorNodes = true;
        this.moduleSize = 6;
        this.moduleConnections = 8;
        this.maxTier = 0;
        this.update = true;
        this.sync = true;
        this.solid = false;
        this.hasPower = true;
        this.hasItems = true;
        this.configurable = true;
        this.config(Integer.class, (tile, i) -> {
            tile.currentPlan = i >= 0 && i < this.plans.size ? i : -1;
            tile.progress = 0.0F;
        });
        this.config(UnitType.class, (tile, val) -> {
            tile.currentPlan = this.plans.indexOf((p) -> p.unit == val);
            tile.progress = 0.0F;
        });
        this.consumes.add(new ConsumeItemDynamic((e) -> e.currentPlan != -1 ? ((ModularConstructorPlan)this.plans.get(e.currentPlan)).requirements : ItemStack.empty));
    }

    public void init() {
        if (this.mirrorNodes && this.moduleNodes != null && this.moduleNodes.length > 0) {
            Vec2 point = this.moduleNodes[0];
            int amount = Math.abs(point.x) > 0.0F ? 8 : 4;
            this.moduleNodes = (Vec2[])Arrays.copyOf(this.moduleNodes, amount);
            int i = 0;

            for(int j = 0; j < 4; ++j) {
                this.moduleNodes[i++] = point.cpy().rotate((float)j * 90.0F);
                if (Math.abs(point.x) > 0.0F) {
                    Vec2 p = point.cpy();
                    this.moduleNodes[i++] = p.set(-p.x, p.y).rotate((float)j * 90.0F);
                }
            }
        }

        this.capacities = new int[Vars.content.items().size];
        this.sortedPlans = new Seq(this.plans);
        int i = 0;

        for(ModularConstructorPlan plan : this.plans) {
            plan.index = i++;
            this.maxTier = Math.max(this.maxTier, plan.tier);

            for(ItemStack stack : plan.requirements) {
                this.capacities[stack.item.id] = Math.max(this.capacities[stack.item.id], stack.amount * 2);
                this.itemCapacity = Math.max(this.itemCapacity, stack.amount * 2);
            }
        }

        this.sortedPlans.sort((px) -> (float)px.tier);
        super.init();
    }

    public void load() {
        super.load();
        this.topRegions = new TextureRegion[2];

        for(int i = 0; i < 2; ++i) {
            this.topRegions[i] = Core.atlas.find(this.name + "-top-" + i);
        }

    }

    protected TextureRegion[] icons() {
        return new TextureRegion[]{this.region, Core.atlas.find(this.name + "-top")};
    }

    public boolean outputsItems() {
        return false;
    }

    public static class ModularConstructorPlan {
        public UnitType unit;
        public ItemStack[] requirements;
        public int tier;
        public float time;
        int index;

        public ModularConstructorPlan(UnitType unit, float time, int tier, ItemStack[] requirements) {
            this.unit = unit;
            this.time = time;
            this.tier = tier;
            this.requirements = requirements;
        }
    }

    public class ModularConstructorBuild extends Building implements ModularConstructorModule.ModularConstructorModuleInterface {
        public int currentPlan = -1;
        public int tier = 0;
        public float progress;
        public float topOffset;
        public Seq<ModularConstructorPart.ModularConstructorPartBuild> parts = new Seq();
        ModularConstructorModule module = new ModularConstructorModule(this);
        Building[] occupied;

        public ModularConstructorBuild() {
            this.occupied = new Building[ModularConstructor.this.moduleNodes.length];
        }

        public int moduleConnections() {
            return ModularConstructor.this.moduleConnections;
        }

        public ModularConstructorModule consModule() {
            return this.module;
        }

        public boolean consConnected(Building other) {
            int ang = Mathf.mod(Mathf.round(other.angleTo(this) / 90.0F), 4);
            if (ModularConstructor.this.moduleBlock == null || ModularConstructor.this.moduleBlock == other.block) {
                int i = 0;

                for(Vec2 node : ModularConstructor.this.moduleNodes) {
                    Tmp.r1.setCentered(node.x * 8.0F + this.x, node.y * 8.0F + this.y, (float)(ModularConstructor.this.moduleSize * 8));
                    Tmp.r2.setCentered(other.x, other.y, (float)(other.block.size * 8) - 1.0F);
                    if (ang == other.rotation() && other.block.size == ModularConstructor.this.moduleSize && Tmp.r1.contains(Tmp.r2)) {
                        this.occupied[i] = other;
                        return true;
                    }

                    ++i;
                }
            }

            return false;
        }

        public void buildConfiguration(Table table) {
            ButtonGroup<ImageButton> group = new ButtonGroup();
            int lastTier = -1;
            table.setBackground(Styles.black3);
            Table cont = null;
            table.add(Core.bundle.format("stat.unity.currentTier", new Object[]{this.tier + 1}));
            table.row();

            for(ModularConstructorPlan plan : ModularConstructor.this.sortedPlans) {
                if (plan.unit.unlockedNow() && plan.tier <= this.tier) {
                    if (plan.tier != lastTier) {
                        if (lastTier != -1) {
                            table.row();
                        }

                        lastTier = plan.tier;
                        table.add("[lightgray]T" + (plan.tier + 1) + ":");
                        table.row();
                        cont = new Table();
                        cont.defaults().size(40.0F);
                        table.add(cont);
                    }

                    if (cont != null) {
                        ImageButton button = (ImageButton)cont.button(Tex.whiteui, Styles.clearToggleTransi, 24.0F, () -> Vars.control.input.frag.config.hideConfig()).group(group).get();
                        button.changed(() -> this.currentPlan = button.isChecked() ? plan.index : -1);
                        button.getStyle().imageUp = new TextureRegionDrawable(plan.unit.uiIcon);
                        button.update(() -> button.setChecked(this.currentPlan == plan.index));
                    }
                }
            }

        }

        public void draw() {
            super.draw();

            for(int i = 0; i < this.occupied.length; ++i) {
                if (this.occupied[i] == null) {
                    Vec2 node = ModularConstructor.this.moduleNodes[i];
                    Tmp.r1.setCentered(node.x * 8.0F + this.x, node.y * 8.0F + this.y, (float)(ModularConstructor.this.moduleSize * 8));
                    Draw.color(Tmp.c1.set(ModularConstructor.this.buildColor).a(0.3F));
                    Fill.crect(Tmp.r1.x, Tmp.r1.y, Tmp.r1.width, Tmp.r1.height);
                    Draw.reset();
                }
            }

            ModularConstructorPlan plan = this.currentPlan != -1 ? (ModularConstructorPlan)ModularConstructor.this.plans.get(this.currentPlan) : null;
            if (plan != null && plan.tier <= this.tier) {
                float time = this.progressTime(plan);
                float prog = Mathf.clamp(this.progress / time);
                Draw.mixcol(ModularConstructor.this.buildColor, 1.0F);
                Draw.alpha(0.35F);
                Draw.rect(plan.unit.fullIcon, this, 0.0F);
                Draw.alpha(1.0F);
                Draw.mixcol();
                if (!(this.progress < time) && !Units.canCreate(this.team, plan.unit)) {
                    Draw.color(0.7F, 0.7F, 0.7F);
                    Draw.rect(plan.unit.fullIcon, this, 0.0F);
                } else if (this.progress > 0.001F) {
                    Draw.draw(Draw.z(), () -> {
                        Draw.shader(Shaders.blockbuild);
                        Draw.color(ModularConstructor.this.buildColor);
                        Shaders.blockbuild.region = plan.unit.fullIcon;
                        Shaders.blockbuild.progress = prog;
                        Draw.rect(plan.unit.fullIcon, this, 0.0F);
                        Draw.flush();
                        Draw.color();
                        Draw.shader();
                    });
                }

                Draw.reset();
            }

            for(int i = 0; i < 4; ++i) {
                TextureRegion tex = ModularConstructor.this.topRegions[Mathf.clamp(i / 2, 0, 1)];
                float ang = (float)i * 90.0F;
                Tmp.v1.trns(ang, this.topOffset).add(this);
                Draw.rect(tex, Tmp.v1, ang);
            }

        }

        public void placed() {
            super.placed();
            this.module.graph.added(this);
        }

        float progressTime(ModularConstructorPlan plan) {
            return Math.max(plan.time - Math.max(this.module.graph.tier - (float)plan.tier, 0.0F) * ModularConstructor.this.efficiencyPerTier, ModularConstructor.this.maxEfficiency);
        }

        public void updateTile() {
            this.module.update();

            for(int i = 0; i < this.occupied.length; ++i) {
                if (this.occupied[i] != null && !this.occupied[i].added) {
                    this.occupied[i] = null;
                }
            }

            ModularConstructorPlan plan = this.currentPlan != -1 ? (ModularConstructorPlan)ModularConstructor.this.plans.get(this.currentPlan) : null;
            if (plan != null && plan.tier <= this.tier) {
                float time = this.progressTime(plan);
                if (this.progress >= time) {
                    if (Units.canCreate(this.team, plan.unit) && this.consValid()) {
                        Unit unit = plan.unit.spawn(this.team, this.x, this.y);
                        unit.rotation = 90.0F;
                        this.cons.trigger();
                        this.progress = 0.0F;
                    }
                } else if (this.consValid()) {
                    this.progress += Time.delta;
                }

                this.topOffset = Mathf.lerpDelta(this.topOffset, Math.max(0.0F, plan.unit.hitSize / 2.0F - ModularConstructor.this.minSize), 0.1F);
            } else {
                this.topOffset = Mathf.lerpDelta(this.topOffset, 0.0F, 0.1F);
            }

        }

        public boolean consValid() {
            boolean valid = true;

            for(ModularConstructorPart.ModularConstructorPartBuild build : this.module.graph.all) {
                valid &= build.cons.canConsume();
            }

            return super.consValid() && valid;
        }

        public boolean shouldConsume() {
            if (this.currentPlan == -1) {
                return false;
            } else {
                return this.enabled && (!ModularConstructor.this.consumes.has(ConsumeType.item) || ModularConstructor.this.consumes.get(ConsumeType.item).valid(this));
            }
        }

        public boolean acceptItem(Building source, Item item) {
            return this.currentPlan != -1 && this.items.get(item) < this.getMaximumAccepted(item) && Structs.contains(((ModularConstructorPlan)ModularConstructor.this.plans.get(this.currentPlan)).requirements, (stack) -> stack.item == item);
        }

        public int getMaximumAccepted(Item item) {
            return ModularConstructor.this.capacities[item.id];
        }

        public void write(Writes write) {
            super.write(write);
            write.s(this.currentPlan);
            write.s(this.tier);
            write.f(this.progress);
            this.module.write(write);
        }

        public void read(Reads read) {
            super.read(read);
            this.currentPlan = read.s();
            this.tier = read.s();
            this.progress = read.f();
            this.module.read(read);
        }
    }
}
