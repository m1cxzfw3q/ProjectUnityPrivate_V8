package unity.world.blocks.exp;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.entities.Effect;
import mindustry.entities.Fires;
import mindustry.entities.Puddles;
import mindustry.graphics.Pal;
import mindustry.type.Liquid;
import mindustry.ui.Bar;
import mindustry.world.Tile;
import unity.content.UnityFx;
import unity.content.UnityLiquids;

public class MeltingCrafter extends KoruhCrafter {
    public float meltAmount = 0.01F;
    public float cooldown = 0.01F;
    public Liquid lava;
    public Color lavaColor1;
    public Color lavaColor2;
    public Effect meltEffect;
    public Effect smokeEffect;

    public MeltingCrafter(String name) {
        super(name);
        this.lava = UnityLiquids.lava;
        this.lavaColor1 = Color.coral;
        this.lavaColor2 = Color.orange;
        this.meltEffect = UnityFx.blockMelt;
        this.smokeEffect = UnityFx.longSmoke;
        this.ignoreExp = true;
    }

    public void setBars() {
        super.setBars();
        this.bars.add("heat", (entity) -> new Bar(() -> Core.bundle.get("bar.heat"), () -> Pal.ammo, () -> Mathf.clamp(entity.melt)));
    }

    public class MeltingCrafterBuild extends KoruhCrafter.KoruhCrafterBuild {
        public float melt = 0.0F;

        public MeltingCrafterBuild() {
            super(MeltingCrafter.this);
        }

        public void lackingExp(int missing) {
            this.melt += MeltingCrafter.this.meltAmount * (float)missing;
        }

        public void updateTile() {
            super.updateTile();
            if (this.exp > 0 && this.melt > 0.0F) {
                this.melt -= this.delta() * MeltingCrafter.this.cooldown;
                if (this.melt < 0.0F) {
                    this.melt = 0.0F;
                }
            }

            if (Mathf.chance((double)(Mathf.clamp(this.melt) * 0.1F))) {
                MeltingCrafter.this.smokeEffect.at(this.x + Mathf.range((float)MeltingCrafter.this.size * 2.0F), this.y + Mathf.range((float)MeltingCrafter.this.size * 2.0F));
            }

            if (this.melt >= 1.0F && (this.liquids == null || this.liquids.get(MeltingCrafter.this.lava) > 0.1F * MeltingCrafter.this.liquidCapacity)) {
                this.kill();
            }

        }

        public void draw() {
            super.draw();
            if (!(this.melt < 0.1F)) {
                if (this.melt > 1.0F) {
                    this.melt = 1.0F;
                }

                Draw.z(99.99F);
                Draw.color(MeltingCrafter.this.lavaColor1, MeltingCrafter.this.lavaColor2, Mathf.absin(3.0F, 1.0F));
                TextureRegion region = Vars.renderer.blocks.cracks[this.block.size - 1][Mathf.clamp((int)(this.melt * 8.0F), 0, 7)];
                Draw.rect(region, this.x, this.y, (float)(this.id % 4 * 90));
                Draw.color();
            }
        }

        public void onDestroyed() {
            super.onDestroyed();
            if (this.liquids == null || this.liquids.currentAmount() > 0.1F * MeltingCrafter.this.liquidCapacity) {
                MeltingCrafter.this.meltEffect.at(this.x, this.y, 0.0F, MeltingCrafter.this.lavaColor2);
                Puddles.deposit(this.tile, MeltingCrafter.this.lava, this.liquids.get(MeltingCrafter.this.lava) * 10.0F);

                for(int i = 0; i < 4; ++i) {
                    Tile tg = this.tile.nearby(i);
                    if (tg != null && tg.solid()) {
                        Fires.create(tg);
                    }
                }

                float fx = this.x;
                float fy = this.y;
                int fsize = MeltingCrafter.this.size;

                for(int i = 0; i < 5; ++i) {
                    Time.run(Mathf.random(60.0F), () -> MeltingCrafter.this.smokeEffect.at(fx + Mathf.range((float)fsize * 2.0F), fy + Mathf.range((float)fsize * 2.0F)));
                }
            }

        }

        public void write(Writes write) {
            super.write(write);
            write.f(this.melt);
        }

        public void read(Reads read, byte revision) {
            super.read(read, revision);
            this.melt = read.f();
        }
    }
}
