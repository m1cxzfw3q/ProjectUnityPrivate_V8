package unity.map.objectives.types;

import arc.Core;
import arc.func.Cons;
import arc.graphics.Color;
import arc.math.Interp;
import arc.scene.Action;
import arc.scene.Element;
import arc.scene.actions.Actions;
import arc.scene.ui.ScrollPane;
import arc.scene.ui.layout.Cell;
import arc.scene.ui.layout.Table;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.game.Team;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.ui.Styles;
import mindustry.world.blocks.storage.CoreBlock;
import rhino.Function;
import unity.map.cinematic.StoryNode;
import unity.map.objectives.Objective;
import unity.map.objectives.ObjectiveModel;
import unity.util.JSBridge;

public class ResourceAmountObj extends Objective {
    protected transient Table container;
    public ItemStack[] items;
    public Team team;
    public Color from;
    public Color to;

    public ResourceAmountObj(StoryNode node, String name, Cons<ResourceAmountObj> executor) {
        super(node, name, executor);
    }

    public static void setup() {
        ObjectiveModel.setup(ResourceAmountObj.class, Pal.accent, () -> Icon.crafting, (node, f) -> {
            String exec = (String)f.get("executor", "function(objective){}");
            Function func = JSBridge.compileFunc(JSBridge.unityScope, f.name() + "-executor.js", exec, 1);
            Object[] args = new Object[]{null};
            ResourceAmountObj obj = new ResourceAmountObj(node, f.name(), (e) -> {
                args[0] = e;
                func.call(JSBridge.context, JSBridge.unityScope, JSBridge.unityScope, args);
            });
            obj.ext(f);
            return obj;
        });
    }

    public void ext(ObjectiveModel.FieldTranslator f) {
        super.ext(f);
        this.items = (ItemStack[])f.get("items", new ItemStack[0]);
        this.team = (Team)f.get("team", Vars.state.rules.defaultTeam);
        this.from = (Color)f.get("from", Color.white);
        this.to = (Color)f.get("to", Color.lime);
    }

    public void init() {
        super.init();
        if (!Vars.headless && !this.completed) {
            Vars.ui.hudGroup.fill((table) -> {
                table.name = this.name;
                table.actions(new Action[]{Actions.scaleTo(0.0F, 1.0F), Actions.visible(true), Actions.scaleTo(1.0F, 1.0F, 0.07F, Interp.pow3Out)});
                table.center().left();
                Cell<Table> cell = table.table(Tex.pane, (t) -> {
                    this.container = t;
                    ScrollPane pane = (ScrollPane)t.pane(Styles.defaultPane, (cont) -> {
                        cont.defaults().pad(4.0F);

                        for(int i = 0; i < this.items.length; ++i) {
                            if (i > 0) {
                                cont.row();
                            }

                            ItemStack item = this.items[i];
                            cont.table((hold) -> {
                                hold.defaults().pad(4.0F);
                                hold.image(() -> item.item.uiIcon).size(32.0F);
                                hold.add().growX();
                                hold.label(() -> {
                                    float amount = (float)Math.min(this.count(item.item), item.amount);
                                    return "[#" + Tmp.c1.set(this.from).lerp(this.to, amount / (float)item.amount).toString() + "]" + amount + " []/ [accent]" + item.amount + "[]";
                                });
                            }).height(40.0F).growX().left();
                        }

                    }).update((p) -> {
                        if (p.hasScroll()) {
                            Element result = Core.scene.hit((float)Core.input.mouseX(), (float)Core.input.mouseY(), true);
                            if (result == null || !result.isDescendantOf(p)) {
                                Core.scene.setScrollFocus((Element)null);
                            }
                        }

                    }).grow().pad(4.0F, 0.0F, 4.0F, 4.0F).get();
                    pane.setScrollingDisabled(true, false);
                    pane.setOverscroll(false, false);
                }).minSize(300.0F, 48.0F).maxSize(300.0F, 156.0F);
                cell.visible(() -> Vars.ui.hudfrag.shown && this.node.sector.valid() && this.container == cell.get());
            });
        }
    }

    public void update() {
        super.update();
        this.completed = true;

        for(ItemStack item : this.items) {
            this.completed = this.count(item.item) >= item.amount;
            if (!this.completed) {
                break;
            }
        }

    }

    protected int count(Item item) {
        CoreBlock.CoreBuild core = (CoreBlock.CoreBuild)Vars.state.teams.cores(this.team).firstOpt();
        return core == null ? 0 : core.items.get(item);
    }

    public void stop() {
        super.stop();
        if (this.container != null) {
            this.container.actions(new Action[]{Actions.moveBy(-this.container.getWidth(), 0.0F, 2.0F, Interp.pow3In), Actions.visible(false), Actions.run(() -> {
                this.container.parent.removeChild(this.container);
                this.container = null;
            })});
        }

    }
}
