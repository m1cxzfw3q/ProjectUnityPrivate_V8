package unity.world.blocks.defense;

import arc.Core;
import arc.util.Time;
import mindustry.entities.Effect;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.meta.Stat;
import unity.content.UnityFx;

public class LimitWall extends Wall {
    protected Effect maxDamageFx;
    protected Effect withstandFx;
    protected Effect blinkFx;
    protected float maxDamage;
    protected float over9000;
    protected float blinkFrame;

    public LimitWall(String name) {
        super(name);
        this.maxDamageFx = UnityFx.maxDamageFx;
        this.withstandFx = UnityFx.withstandFx;
        this.blinkFx = UnityFx.blinkFx;
        this.maxDamage = 30.0F;
        this.over9000 = 9.0E7F;
        this.blinkFrame = -1.0F;
    }

    public void setStats() {
        super.setStats();
        if (this.maxDamage > 0.0F) {
            this.stats.add(Stat.abilities, "@", new Object[]{Core.bundle.format("stat.unity.maxdamage", new Object[]{this.maxDamage})});
        }

        if (this.blinkFrame > 0.0F) {
            this.stats.add(Stat.abilities, "@", new Object[]{Core.bundle.format("stat.unity.blinkframe", new Object[]{this.blinkFrame})});
        }

    }

    public class LimitWallBuild extends Wall.WallBuild {
        protected float blink;

        public LimitWallBuild() {
            super(LimitWall.this);
        }

        public float handleDamage(float amount) {
            if (LimitWall.this.blinkFrame > 0.0F) {
                if (!(Time.time - this.blink >= LimitWall.this.blinkFrame)) {
                    return 0.0F;
                }

                this.blink = Time.time;
                LimitWall.this.blinkFx.at(this.x, this.y, (float)LimitWall.this.size);
            }

            if (LimitWall.this.maxDamage > 0.0F && amount > LimitWall.this.maxDamage && amount < LimitWall.this.over9000) {
                LimitWall.this.withstandFx.at(this.x, this.y, (float)LimitWall.this.size);
                return super.handleDamage(Math.min(amount, LimitWall.this.maxDamage));
            } else {
                return super.handleDamage(amount);
            }
        }
    }
}
