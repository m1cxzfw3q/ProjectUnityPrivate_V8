package unity.world.blocks.effect;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.gen.Building;
import mindustry.world.Block;
import unity.util.BlockMovement;

public class UnityThruster extends Block {
    public final int timerUse;
    public int maxBlocks;
    public float maxSpeed;
    public float acceleration;
    public float engineSize;
    public float itemDuration;

    public UnityThruster(String name) {
        super(name);
        this.timerUse = this.timers++;
        this.maxBlocks = 10;
        this.maxSpeed = 1.0F;
        this.acceleration = 1.0F;
        this.engineSize = 8.0F;
        this.itemDuration = 150.0F;
        this.update = true;
        this.hasItems = true;
        this.sync = true;
    }

    public class UnityThrusterBuild extends Building {
        public float speed = 0.0F;

        public void updateTile() {
            super.updateTile();
            if (this.consValid()) {
                if (this.timer(UnityThruster.this.timerUse, UnityThruster.this.itemDuration / this.timeScale())) {
                    this.consume();
                }

                this.speed += UnityThruster.this.acceleration * this.edelta();
                this.speed = Mathf.clamp(this.speed, 0.0F, UnityThruster.this.maxSpeed);
            } else {
                this.speed = (float)((double)this.speed * 0.9);
            }

            if ((double)this.speed > 0.05) {
                BlockMovement.pushBlock(this, this.rotation, UnityThruster.this.maxBlocks, this.speed, (building) -> true);
            }

        }

        public void draw() {
            super.draw();
            float scale = this.speed / UnityThruster.this.maxSpeed;
            Draw.color(this.team.color);
            float blockRotation = (float)(this.rotation * 90);
            Fill.circle(this.x + Angles.trnsx(blockRotation + 180.0F, UnityThruster.this.offset), this.y + Angles.trnsy(blockRotation + 180.0F, UnityThruster.this.offset), (UnityThruster.this.engineSize + Mathf.absin(Time.time, 2.0F, UnityThruster.this.engineSize / 4.0F)) * scale);
            Draw.color(Color.white);
            Fill.circle(this.x + Angles.trnsx(blockRotation + 180.0F, UnityThruster.this.offset - 1.0F), this.y + Angles.trnsy(blockRotation + 180.0F, UnityThruster.this.offset - 1.0F), (UnityThruster.this.engineSize + Mathf.absin(Time.time, 2.0F, UnityThruster.this.engineSize / 4.0F)) / 2.0F * scale);
            Draw.color();
        }
    }
}
