package unity.world.blocks.distribution;

import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.scene.ui.Button;
import arc.scene.ui.ButtonGroup;
import arc.scene.ui.ImageButton;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectSet;
import arc.struct.Seq;
import arc.util.Eachable;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.entities.units.BuildPlan;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Tex;
import mindustry.type.Item;
import mindustry.ui.Styles;
import mindustry.world.Block;

public class Teleporter extends Block {
    protected static final Color[] selection;
    protected static final ObjectSet<TeleporterBuild>[][] teleporters;
    protected float powerUse = 2.5F;
    protected TextureRegion blankRegion;
    protected TextureRegion topRegion;

    public Teleporter(String name) {
        super(name);
        this.update = true;
        this.solid = true;
        this.configurable = true;
        this.saveConfig = true;
        this.unloadable = false;
        this.hasItems = true;
        Events.on(EventType.WorldLoadEvent.class, (e) -> {
            for(int i = 0; i < teleporters.length; ++i) {
                for(int j = 0; j < teleporters[i].length; ++j) {
                    teleporters[i][j].clear();
                }
            }

        });
        this.config(Integer.class, (build, value) -> {
            if (build.toggle != -1) {
                teleporters[build.team.id][build.toggle].remove(build);
            }

            if (value != -1) {
                teleporters[build.team.id][value].add(build);
            }

            build.toggle = value;
        });
        this.configClear((build) -> build.toggle = -1);
    }

    public boolean outputsItems() {
        return true;
    }

    public void init() {
        this.consumes.powerCond(this.powerUse, TeleporterBuild::isConsuming);
        super.init();
    }

    public void load() {
        super.load();
        this.blankRegion = Core.atlas.find(this.name + "-blank");
        this.topRegion = Core.atlas.find(this.name + "-top");
    }

    public void drawRequestConfig(BuildPlan req, Eachable<BuildPlan> list) {
        this.drawRequestConfigCenter(req, req.config, "nothing");
    }

    public void drawRequestConfigCenter(BuildPlan req, Object content, String region) {
        if (content instanceof Integer) {
            Integer temp = (Integer)content;
            Draw.color(selection[temp]);
            Draw.rect(this.blankRegion, req.drawx(), req.drawy());
        }
    }

    static {
        selection = new Color[]{Color.royal, Color.orange, Color.scarlet, Color.forest, Color.purple, Color.gold, Color.pink, Color.black};
        teleporters = new ObjectSet[Team.baseTeams.length][selection.length];

        for(int i = 0; i < Team.baseTeams.length; ++i) {
            if (teleporters[i] == null) {
                teleporters[i] = new ObjectSet[selection.length];
            }

            for(int j = 0; j < selection.length; ++j) {
                teleporters[i][j] = new ObjectSet();
            }
        }

    }

    public class TeleporterBuild extends Building {
        protected int toggle = -1;
        protected int entry;
        protected float duration;
        protected TeleporterBuild target;
        protected Team previousTeam;

        protected void onDuration() {
            if (this.duration < 0.0F) {
                this.duration = 0.0F;
            } else {
                this.duration -= Time.delta;
            }

        }

        protected boolean isConsuming() {
            return this.duration > 0.0F;
        }

        protected boolean isTeamChanged() {
            return this.previousTeam != this.team;
        }

        public void draw() {
            super.draw();
            if (this.toggle != -1) {
                Draw.color(Teleporter.selection[this.toggle]);
                Draw.rect(Teleporter.this.blankRegion, this.x, this.y);
            }

            Draw.color(Color.white);
            Draw.alpha(0.45F + Mathf.absin(7.0F, 0.26F));
            Draw.rect(Teleporter.this.topRegion, this.x, this.y);
            Draw.reset();
        }

        public void updateTile() {
            this.onDuration();
            if (this.items.any()) {
                this.dump();
            }

            if (this.isTeamChanged() && this.toggle != -1) {
                Teleporter.teleporters[this.team.id][this.toggle].add(this);
                Teleporter.teleporters[this.previousTeam.id][this.toggle].remove(this);
                this.previousTeam = this.team;
            }

        }

        public void buildConfiguration(Table table) {
            ButtonGroup<Button> group = new ButtonGroup();
            group.setMinCheckCount(0);

            for(int i = 0; i < Teleporter.selection.length; ++i) {
                ImageButton button = (ImageButton)table.button(Tex.whiteui, Styles.clearToggleTransi, 24.0F, () -> {
                }).size(34.0F).group(group).get();
                button.changed(() -> this.configure(button.isChecked() ? i : -1));
                button.getStyle().imageUpColor = Teleporter.selection[i];
                button.update(() -> button.setChecked(this.toggle == i));
                if (i % 4 == 3) {
                    table.row();
                }
            }

        }

        protected TeleporterBuild findLink(int value) {
            ObjectSet<TeleporterBuild> teles = Teleporter.teleporters[this.team.id][value];
            Seq<TeleporterBuild> entries = teles.toSeq();
            if (this.entry >= entries.size) {
                this.entry = 0;
            }

            if (this.entry == entries.size - 1) {
                TeleporterBuild other = (TeleporterBuild)teles.get((TeleporterBuild)entries.get(this.entry));
                if (other == this) {
                    this.entry = 0;
                }
            }

            int i = this.entry;

            for(int len = entries.size; i < len; ++i) {
                TeleporterBuild other = (TeleporterBuild)teles.get((TeleporterBuild)entries.get(i));
                if (other != this) {
                    this.entry = i + 1;
                    return other;
                }
            }

            return null;
        }

        public boolean acceptItem(Building source, Item item) {
            if (this.toggle == -1) {
                return false;
            } else {
                this.target = this.findLink(this.toggle);
                if (this.target == null) {
                    return false;
                } else {
                    return source != this && this.consValid() && Mathf.zero(1.0F - this.efficiency()) && this.target.items.total() < this.target.getMaximumAccepted(item);
                }
            }
        }

        public void handleItem(Building source, Item item) {
            this.target.items.add(item, 1);
            this.duration = 0.0F;
        }

        public void created() {
            if (this.toggle != -1) {
                Teleporter.teleporters[this.team.id][this.toggle].add(this);
            }

            this.previousTeam = this.team;
        }

        public void onRemoved() {
            if (this.toggle != -1) {
                if (this.isTeamChanged()) {
                    Teleporter.teleporters[this.previousTeam.id][this.toggle].remove(this);
                } else {
                    Teleporter.teleporters[this.team.id][this.toggle].remove(this);
                }
            }

        }

        public Integer config() {
            return this.toggle;
        }

        public void write(Writes write) {
            super.write(write);
            write.b(this.toggle);
        }

        public void read(Reads read, byte revision) {
            super.read(read, revision);
            this.toggle = read.b();
        }
    }
}
