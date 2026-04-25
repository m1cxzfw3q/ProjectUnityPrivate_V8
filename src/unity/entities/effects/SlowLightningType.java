package unity.entities.effects;

import arc.func.Floatf;
import arc.func.Floatp;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.geom.Position;
import arc.math.geom.Vec2;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.pooling.Pool;
import mindustry.entities.Effect;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.gen.Posc;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;
import unity.content.effects.HitFx;
import unity.gen.SlowLightning;
import unity.util.BasicPool;
import unity.util.Utils;

public class SlowLightningType {
    private static int seed = 1;
    public static final int maxNodes = 60;
    public static BasicPool<SlowLightningNode> nodes = new BasicPool(8, 300, SlowLightningNode::new);
    public Color colorFrom;
    public Color colorTo;
    public float damage;
    public float colorTime;
    public float fadeTime;
    public float splitChance;
    public float nodeLength;
    public float nodeTime;
    public float range;
    public float randSpacing;
    public float splitRandSpacing;
    public float lineWidth;
    public float lifetime;
    public float maxRotationSpeed;
    public float minRotationSpeed;
    public float rotationDistance;
    public boolean continuous;
    public Effect hitEffect;

    public SlowLightningType() {
        this.colorFrom = Color.white;
        this.colorTo = Pal.lancerLaser;
        this.damage = 12.0F;
        this.colorTime = 32.0F;
        this.fadeTime = 20.0F;
        this.splitChance = 0.035F;
        this.nodeLength = 50.0F;
        this.nodeTime = 3.0F;
        this.range = 150.0F;
        this.randSpacing = 20.0F;
        this.splitRandSpacing = 60.0F;
        this.lineWidth = 2.0F;
        this.lifetime = 120.0F;
        this.maxRotationSpeed = 22.0F;
        this.minRotationSpeed = 1.5F;
        this.rotationDistance = 600.0F;
        this.continuous = false;
        this.hitEffect = HitFx.coloredHitSmall;
    }

    public SlowLightning create(Team team, float x, float y, float rotation, Floatp liveDamage, Posc parent, Position target) {
        return this.create(team, (Bullet)null, x, y, rotation, liveDamage, parent, target);
    }

    public SlowLightning create(Team team, Bullet b, float x, float y, float rotation, Floatp liveDamage, Posc parent, Position target) {
        SlowLightning s = SlowLightning.create();
        s.seed = seed;
        s.type = this;
        s.team = team;
        s.bullet = b;
        s.set(x, y);
        s.rotation = rotation;
        s.liveDamage = liveDamage;
        s.parent = parent;
        s.target = target;
        s.add();
        ++seed;
        return s;
    }

    public void damageUnit(SlowLightningNode s, Unit unit) {
        Floatp l = s.main.liveDamage;
        unit.damage(l != null ? l.get() : this.damage);
    }

    public void damageBuilding(SlowLightningNode s, Building building) {
        Floatp l = s.main.liveDamage;
        building.damage(l != null ? l.get() : this.damage);
    }

    public void hit(SlowLightningNode s, float x, float y) {
        this.hitEffect.at(x, y, s.rotation, this.colorFrom);
    }

    public static class SlowLightningNode implements Position, Pool.Poolable {
        public float x;
        public float y;
        public float colorProgress;
        public float time;
        public float rotation;
        public float rotRand;
        public float dist;
        public int layer = 0;
        public SlowLightning main;
        public SlowLightningNode parent;
        public boolean ended = false;

        public void move(int originLayer, float mx, float my) {
            float scl = 1.0F - (float)this.layer / (float)originLayer;
            this.x += mx * scl;
            this.y += my * scl;
        }

        public void update() {
            SlowLightningType type = this.main.type;
            if (this.colorProgress < 1.0F) {
                this.colorProgress = Math.min(1.0F, this.colorProgress + Time.delta / type.colorTime);
            }

            if (this.time < 1.0F) {
                this.time = Math.min(1.0F, this.time + Time.delta / type.nodeTime);
                if (this.time >= 1.0F) {
                    this.end();
                }
            }

        }

        public void draw() {
            SlowLightningType type = this.main.type;
            Draw.color(type.colorFrom, type.colorTo, this.colorProgress);
            Position p = this.getLast();
            if (this.time >= 1.0F) {
                Lines.line(p.getX(), p.getY(), this.x, this.y);
            } else {
                Vec2 v = Tmp.v1.set(this).sub(p).scl(this.time).add(p);
                Lines.line(p.getX(), p.getY(), v.x, v.y);
            }

        }

        void line(float x, float y, float x2, float y2) {
            SlowLightningType type = this.main.type;
            Utils.collideLineRawEnemy(this.main.team, x, y, x2, y2, type.lineWidth / 3.0F, (building, direct) -> {
                if (direct) {
                    type.damageBuilding(this, building);
                }

                return building.block.absorbLasers;
            }, (unit) -> {
                type.damageUnit(this, unit);
                return false;
            }, (Floatf)null, (ex, ey) -> type.hit(this, ex, ey), false);
        }

        void end() {
            SlowLightningType type = this.main.type;
            if (!type.continuous) {
                Position p = this.getLast();
                this.line(p.getX(), p.getY(), this.x, this.y);
            }

            if (!this.ended && this.main.distance < type.range && this.main.nodes.size < 60) {
                this.main.end(this);
            }

        }

        public void collide() {
            Position p = this.getLast();
            if (this.time >= 1.0F) {
                this.line(p.getX(), p.getY(), this.x, this.y);
            } else {
                Vec2 v = Tmp.v1.set(this).sub(p).scl(this.time).add(p);
                this.line(p.getX(), p.getY(), v.x, v.y);
            }

        }

        Position getLast() {
            return (Position)(this.parent != null ? this.parent : this.main);
        }

        public float getX() {
            return this.x;
        }

        public float getY() {
            return this.y;
        }

        public void reset() {
            this.x = this.y = this.colorProgress = this.time = this.rotation = this.rotRand = 0.0F;
            this.layer = 0;
            this.main = null;
            this.parent = null;
            this.ended = false;
        }
    }
}
