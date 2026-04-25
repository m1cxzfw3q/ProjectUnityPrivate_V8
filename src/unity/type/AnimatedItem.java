package unity.type;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.TextureRegion;
import arc.util.Time;
import mindustry.game.EventType.Trigger;
import mindustry.type.Item;
import unity.mod.Triggers;
import unity.util.GraphicUtils;

public class AnimatedItem extends Item {
    public int frames = 5;
    public int transitionFrames = 0;
    public float frameTime = 5.0F;
    protected TextureRegion[] animRegions;

    public AnimatedItem(String name, Color color) {
        super(name, color);
    }

    public void load() {
        int n = this.frames * (1 + this.transitionFrames);
        TextureRegion[] spriteArr = new TextureRegion[this.frames];

        for(int i = 1; i <= this.frames; ++i) {
            spriteArr[i - 1] = Core.atlas.find(this.name + i + "-full", Core.atlas.find(this.name + i, Core.atlas.find(this.name + "1")));
        }

        this.animRegions = new TextureRegion[n];

        for(int i = 0; i < this.frames; ++i) {
            if (this.transitionFrames <= 0) {
                this.animRegions[i] = spriteArr[i];
            } else {
                this.animRegions[i * (this.transitionFrames + 1)] = spriteArr[i];

                for(int j = 1; j <= this.transitionFrames; ++j) {
                    float f = (float)j / (float)(this.transitionFrames + 1);
                    this.animRegions[i * (this.transitionFrames + 1) + j] = GraphicUtils.blendSprites(spriteArr[i], spriteArr[(i + 1) % this.frames], f, this.name + i);
                }
            }
        }

        Triggers.listen(Trigger.update, this::update);
    }

    public void update() {
        int i = (int)(Time.globalTime / this.frameTime) % this.animRegions.length;
        this.fullIcon.set(this.animRegions[i]);
        this.uiIcon.set(this.animRegions[i]);
    }
}
