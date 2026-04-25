package unity.world.blocks.power;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Vec2;
import arc.util.Time;
import mindustry.gen.Groups;
import unity.entities.ExpOrbs;
import unity.world.blocks.GraphBlock;

public class Magnet extends GraphBlock {
    public final TextureRegion[] regions = new TextureRegion[4];

    public Magnet(String name) {
        super(name);
        this.rotate = this.solid = true;
    }

    public void load() {
        super.load();

        for(int i = 0; i < 4; ++i) {
            this.regions[i] = Core.atlas.find(this.name + (i + 1));
        }

    }

    public class MagnetBuild extends GraphBlock.GraphBuild {
        public MagnetBuild() {
            super(Magnet.this);
        }

        public void updatePre() {
            if (Magnet.this.hasPower) {
                this.flux().mulFlux(this.power.graph.getSatisfaction());
            }

        }

        public void draw() {
            Draw.rect(Magnet.this.regions[this.rotation], this.x, this.y);
            this.drawTeamTop();
        }

        public void updatePost() {
            float f = this.flux().flux();
            Groups.bullet.intersect(this.x - f * 2.0F, this.y - f * 2.0F, f * 4.0F, f * 4.0F, (bullet) -> {
                if (bullet.type != null) {
                    boolean isOrb = bullet.type instanceof ExpOrbs.ExpOrb;
                    if (bullet.type.hittable || isOrb) {
                        float dx = bullet.x - this.x;
                        float dy = bullet.y - this.y;
                        float dis = Mathf.sqrt(dx * dx + dy * dy);
                        if (dis < f * 2.0F) {
                            float mul = 1.0F / Math.max(1.0F, bullet.type.estimateDPS() / 10.0F) * (isOrb ? 5.0F : 1.0F) * Time.delta * 0.1F * f / (8.0F + dis);
                            Vec2 var10000 = bullet.vel;
                            var10000.x += mul * (float)Geometry.d4x(this.rotation);
                            var10000 = bullet.vel;
                            var10000.y += mul * (float)Geometry.d4y(this.rotation);
                        }
                    }

                }
            });
        }
    }
}
