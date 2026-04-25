package unity.entities.bullet.anticheat;

import arc.func.Floatp;
import arc.graphics.Color;
import arc.math.geom.Position;
import mindustry.content.Fx;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.gen.Posc;
import mindustry.gen.Unit;
import unity.entities.effects.SlowLightningType;
import unity.gen.SlowLightning;

public class SlowLightningBulletType extends AntiCheatBulletTypeBase {
    protected float range = 870.0F;
    protected float nodeLength = 80.0F;
    protected float nodeTime = 7.0F;
    protected float splitChance = 0.06F;
    protected SlowLightningType type;

    public SlowLightningBulletType(float damage) {
        super(0.0F, damage);
        this.lifetime = 160.0F;
        this.collides = false;
        this.hittable = this.absorbable = this.reflectable = false;
        this.keepVelocity = false;
        this.despawnEffect = this.hitEffect = Fx.none;
    }

    public float range() {
        return this.range * 0.8F;
    }

    public void init() {
        super.init();
        this.type = new SlowLightningType() {
            {
                this.damage = SlowLightningBulletType.this.damage;
                this.lifetime = SlowLightningBulletType.this.lifetime;
                this.range = SlowLightningBulletType.this.range;
                this.nodeLength = SlowLightningBulletType.this.nodeLength;
                this.nodeTime = SlowLightningBulletType.this.nodeTime;
                this.colorFrom = Color.red;
                this.colorTo = Color.black;
                this.splitChance = SlowLightningBulletType.this.splitChance;
                this.continuous = true;
                this.lineWidth = 3.0F;
            }

            public void damageUnit(SlowLightningType.SlowLightningNode s, Unit unit) {
                if (s.main.bullet != null && s.main.bullet.type == SlowLightningBulletType.this) {
                    SlowLightningBulletType.this.hitUnitAntiCheat(s.main.bullet, unit);
                }

            }

            public void damageBuilding(SlowLightningType.SlowLightningNode s, Building building) {
                if (s.main.bullet != null && s.main.bullet.type == SlowLightningBulletType.this) {
                    SlowLightningBulletType.this.hitBuildingAntiCheat(s.main.bullet, building);
                }

            }

            public void hit(SlowLightningType.SlowLightningNode s, float x, float y) {
                super.hit(s, x, y);
                if (s.main.bullet != null && s.main.bullet.type == SlowLightningBulletType.this) {
                    SlowLightningBulletType.this.hit(s.main.bullet, x, y);
                }

            }
        };
    }

    public void init(Bullet b) {
        b.data = this.type.create(b.team, b, b.x, b.y, b.rotation(), (Floatp)null, b.owner instanceof Posc ? (Posc)b.owner : null, (Position)null);
    }

    public void update(Bullet b) {
        if (b.data instanceof SlowLightning) {
            SlowLightning data = (SlowLightning)b.data;
            b.x = data.x;
            b.y = data.y;
        }

    }

    public void drawLight(Bullet b) {
    }

    public void draw(Bullet b) {
    }
}
