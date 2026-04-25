package unity.world.blocks.production;

import arc.Core;
import arc.audio.Sound;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.content.Items;
import mindustry.entities.Effect;
import mindustry.game.Team;
import mindustry.world.blocks.production.GenericCrafter;
import unity.content.UnityFx;
import unity.gen.UnitySounds;

public class Press extends GenericCrafter {
    public float movementSize = 10.0F;
    public float fxYVariation = 1.875F;
    public Sound clangSound;
    public Effect sparkEffect;
    public TextureRegion leftRegion;
    public TextureRegion rightRegion;
    public TextureRegion baseRegion;

    public Press(String name) {
        super(name);
        this.clangSound = UnitySounds.clang;
        this.sparkEffect = UnityFx.sparkBoi;
        this.update = true;
        this.updateEffectChance = 0.0F;
    }

    public void load() {
        super.load();
        this.leftRegion = Core.atlas.find(this.name + "-left");
        this.rightRegion = Core.atlas.find(this.name + "-right");
        this.baseRegion = Core.atlas.find(this.name + "-base");
    }

    public TextureRegion[] icons() {
        return new TextureRegion[]{this.region, this.leftRegion, this.rightRegion};
    }

    public class PressBuild extends GenericCrafter.GenericCrafterBuild {
        public float realMovementSize;
        public float alphaValueMax;
        public float alphaValue;

        public PressBuild() {
            super(Press.this);
            this.realMovementSize = Press.this.movementSize / 8.0F;
            this.alphaValueMax = 0.4F;
            this.alphaValue = 0.0F;
        }

        public void draw() {
            Draw.rect(Press.this.baseRegion, this.x, this.y);
            Draw.color(Team.crux.color);
            if (this.alphaValue > 0.0F) {
                Draw.alpha(this.alphaValue);

                for(int i = 0; i < 10; ++i) {
                    Fill.circle(this.x, this.y, (float)i * 0.6F + Mathf.sin((this.totalProgress + Time.time) / 16.0F) / 3.0F);
                }
            }

            Draw.color();
            Draw.rect(Press.this.leftRegion, this.x - Math.abs(Mathf.sin(Mathf.clamp(this.progress * 1.2F - 0.2F, 0.0F, 1.0F) / 2.0F * 360.0F * ((float)Math.PI / 180F))) * this.realMovementSize, this.y);
            Draw.rect(Press.this.rightRegion, this.x + Math.abs(Mathf.sin(Mathf.clamp(this.progress * 1.2F - 0.2F, 0.0F, 1.0F) / 2.0F * 360.0F * ((float)Math.PI / 180F))) * this.realMovementSize, this.y);
            Draw.rect(Press.this.region, this.x, this.y);
        }

        public void updateTile() {
            super.updateTile();
            if (this.efficiency() > 0.001F) {
                this.alphaValue += 0.01F;
            } else {
                this.alphaValue -= 0.01F;
            }

            this.alphaValue = Mathf.clamp(this.alphaValue, 0.0F, this.alphaValueMax);
        }

        public void consume() {
            super.consume();
            Press.this.clangSound.at(this.x, this.y, Mathf.random(0.6F, 0.8F));

            for(int i = 0; i < 8; ++i) {
                Press.this.sparkEffect.at(this.x, this.y + Mathf.range(Press.this.fxYVariation), Mathf.random() * 360.0F, Items.surgeAlloy.color, Mathf.random() + 0.5F);
            }

        }
    }
}
