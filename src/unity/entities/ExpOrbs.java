package unity.entities;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.bullet.BulletType;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.graphics.Drawf;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.distribution.Conveyor;
import mindustry.world.blocks.production.Incinerator;
import unity.content.UnityFx;
import unity.world.blocks.exp.ExpHolder;

public class ExpOrbs {
    public static final int expAmount = 10;
    private static final Color expColor = Color.valueOf("84ff00");
    private static final int[] d4x = new int[]{1, 0, -1, 0};
    private static final int[] d4y = new int[]{0, 1, 0, -1};
    private static final ExpOrb expOrb = new ExpOrb();

    public static void spreadExp(float x, float y, int amount) {
        spreadExp(x, y, amount, 4.0F);
    }

    public static void spreadExp(float x, float y, int amount, float v) {
        if (Vars.net.server() || !Vars.net.active()) {
            v *= 1000.0F;
            int n = amount / 10;

            for(int i = 0; i < n; ++i) {
                expOrb.createNet(Team.derelict, x, y, Mathf.random() * 360.0F, 0.0F, v, 1.0F);
            }
        }

    }

    public static void spreadExp(float x, float y, float amount, float v) {
        spreadExp(x, y, Mathf.ceilPositive(amount), v);
    }

    public static void dropExp(float x, float y, float rotation) {
        dropExp(x, y, rotation, 4.0F, 10);
    }

    public static void dropExp(float x, float y, float rotation, float v, int amount) {
        if (Vars.net.server() || !Vars.net.active()) {
            v *= 1000.0F;
            int n = amount / 10;

            for(int i = 0; i < n; ++i) {
                expOrb.createNet(Team.derelict, x, y, rotation, 0.0F, v, 1.0F);
            }
        }

    }

    public static int orbs(int exp) {
        return exp / 10;
    }

    public static int convertedExp(int exp) {
        return exp / 10 * 10;
    }

    public static int oneOrb(int exp) {
        return exp < 10 ? 0 : 10;
    }

    public static final class ExpOrb extends BulletType {
        private ExpOrb() {
            this.absorbable = false;
            this.damage = 8.0F;
            this.drag = 0.05F;
            this.lifetime = 180.0F;
            this.speed = 1.0E-4F;
            this.keepVelocity = false;
            this.pierce = true;
            this.hitSize = 2.0F;
            this.hittable = false;
            this.collides = false;
            this.collidesTiles = false;
            this.collidesAir = false;
            this.collidesGround = false;
            this.lightColor = ExpOrbs.expColor;
            this.hitEffect = Fx.none;
            this.shootEffect = Fx.none;
            this.despawnEffect = UnityFx.orbDespawn;
            this.layer = 99.99F;
        }

        public void draw(Bullet b) {
            if (!(b.fin() > 0.5F) || !(Time.time % 14.0F < 7.0F)) {
                Draw.color(ExpOrbs.expColor, Color.white, 0.1F + 0.1F * Mathf.sin(Time.time * 0.03F + (float)b.id * 2.0F));
                Fill.circle(b.x, b.y, 1.5F);
                Lines.stroke(0.5F);

                for(int i = 0; i < 4; ++i) {
                    Drawf.tri(b.x, b.y, 4.0F, 4.0F + 1.5F * Mathf.sin(Time.time * 0.12F + (float)b.id * 3.0F), (float)(i * 90) + Mathf.sin(Time.time * 0.04F + (float)b.id * 5.0F) * 28.0F);
                }

                Draw.color();
            }
        }

        public void update(Bullet b) {
            if (b.moving()) {
                b.time(0.0F);
            }

            Tile tile = Vars.world.tileWorld(b.x, b.y);
            if (tile != null && tile.build != null) {
                Building var5 = tile.build;
                if (var5 instanceof ExpHolder) {
                    ExpHolder exp = (ExpHolder)var5;
                    if (exp.acceptOrb() && exp.handleOrb(10)) {
                        b.remove();
                        return;
                    }
                }

                Block var6 = tile.block();
                if (var6 instanceof Conveyor) {
                    Conveyor conv = (Conveyor)var6;
                    if (conv.absorbLasers) {
                        this.expConveyor(b, conv, (Conveyor.ConveyorBuild)tile.build);
                    } else {
                        this.conveyor(b, conv, (Conveyor.ConveyorBuild)tile.build);
                    }
                } else if (tile.block() instanceof Incinerator && ((Incinerator.IncineratorBuild)tile.build).heat > 0.5F) {
                    b.remove();
                } else if (tile.solid()) {
                    b.trns(-1.1F * b.vel.x, -1.1F * b.vel.y);
                    b.vel.scl(0.0F);
                }

            }
        }

        private void conveyor(Bullet b, Conveyor block, Conveyor.ConveyorBuild build) {
            if (!(build.clogHeat > 0.5F) && build.enabled) {
                float speed = block.speed / 3.0F;
                b.vel.add((float)ExpOrbs.d4x[build.rotation] * speed * build.delta(), (float)ExpOrbs.d4y[build.rotation] * speed * build.delta());
            }
        }

        private void expConveyor(Bullet b, Conveyor block, Conveyor.ConveyorBuild build) {
            if (!(build.clogHeat > 0.5F) && build.enabled) {
                float speed = block.speed * 2.0F;
                b.vel.scl(0.7F);
                b.vel.add((float)ExpOrbs.d4x[build.rotation] * speed * build.delta(), (float)ExpOrbs.d4y[build.rotation] * speed * build.delta());
            }
        }
    }
}
