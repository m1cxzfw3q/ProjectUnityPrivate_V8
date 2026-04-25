package unity.world.blocks.power;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.scene.ui.ImageButton;
import arc.scene.ui.layout.Table;
import arc.util.Time;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.ui.Styles;
import unity.world.blocks.GraphBlock;
import unity.world.graph.TorqueGraph;
import unity.world.modules.GraphTorqueModule;

public class HandCrank extends GraphBlock {
    public final TextureRegion[] shaftRegions = new TextureRegion[2];
    public TextureRegion handleRegion;
    public TextureRegion baseRegion;

    public HandCrank(String name) {
        super(name);
        this.rotate = this.configurable = true;
        this.config(Integer.class, (build, value) -> {
            build.force = 40.0F;
            build.cooldown = 0.0F;
        });
    }

    public void load() {
        super.load();
        this.handleRegion = Core.atlas.find(this.name + "-handle");
        this.baseRegion = Core.atlas.find(this.name + "-bottom");

        for(int i = 0; i < 2; ++i) {
            this.shaftRegions[i] = Core.atlas.find(this.name + "-base" + (i + 1));
        }

    }

    public class HandCrankBuild extends GraphBlock.GraphBuild {
        float cooldown;
        float force;

        public HandCrankBuild() {
            super(HandCrank.this);
        }

        public void buildConfiguration(Table table) {
            ((ImageButton)table.button(Tex.whiteui, Styles.clearTransi, 50.0F, () -> this.configure(0)).size(50.0F).disabled((b) -> this.cooldown < 30.0F).get()).getStyle().imageUp = Icon.redo;
        }

        public void updatePre() {
            GraphTorqueModule<?> tGraph = this.torque();
            float ratio = (20.0F - ((TorqueGraph)tGraph.getNetwork()).lastVelocity) / 20.0F;
            tGraph.force = ratio * this.force;
            this.cooldown += Time.delta;
            this.force *= 0.8F;
        }

        public void draw() {
            int variant = this.rotation != 2 && this.rotation != 1 ? 0 : 1;
            Draw.rect(HandCrank.this.baseRegion, this.x, this.y);
            Draw.rect(HandCrank.this.shaftRegions[variant], this.x, this.y, this.rotdeg());
            Draw.rect(HandCrank.this.handleRegion, this.x, this.y, this.torque().getRotation());
            this.drawTeamTop();
        }
    }
}
