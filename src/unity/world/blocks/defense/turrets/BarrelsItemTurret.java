package unity.world.blocks.defense.turrets;

import arc.Core;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import mindustry.entities.bullet.BulletType;
import mindustry.world.blocks.defense.turrets.ItemTurret;

public class BarrelsItemTurret extends ItemTurret {
    protected final Seq<Barrel> barrels = new Seq(1);
    protected boolean focus;
    protected Vec2 tr3 = new Vec2();

    public BarrelsItemTurret(String name) {
        super(name);
    }

    protected void addBarrel(float x, float y, float reloadTime) {
        this.barrels.add(new Barrel(x, y, reloadTime));
    }

    public void load() {
        super.load();
        this.baseRegion = Core.atlas.find("unity-block-" + this.size);
    }

    protected class Barrel {
        public final float x;
        public final float y;
        public final float reloadTime;

        public Barrel(float x, float y, float reloadTime) {
            this.x = x;
            this.y = y;
            this.reloadTime = reloadTime;
        }
    }

    public class BarrelsItemTurretBuild extends ItemTurret.ItemTurretBuild {
        protected float[] barrelReloads;
        protected int[] barrelShotCounters;

        public BarrelsItemTurretBuild() {
            super(BarrelsItemTurret.this);
            this.barrelReloads = new float[BarrelsItemTurret.this.barrels.size];
            this.barrelShotCounters = new int[BarrelsItemTurret.this.barrels.size];
        }

        protected void shoot(BulletType type) {
            if (BarrelsItemTurret.this.focus) {
                this.recoil = BarrelsItemTurret.this.recoilAmount;
                this.heat = 1.0F;
                float i = (float)(this.shotCounter % 2) - 0.5F;
                BarrelsItemTurret.this.tr.trns(this.rotation - 90.0F, BarrelsItemTurret.this.spread * i + Mathf.range(BarrelsItemTurret.this.xRand), (float)(BarrelsItemTurret.this.size * 8) / 2.0F);
                BarrelsItemTurret.this.tr3.trns(this.rotation, Math.max(Mathf.dst(this.x, this.y, this.targetPos.x, this.targetPos.y), (float)(BarrelsItemTurret.this.size * 8)));
                float rot = Angles.angle(BarrelsItemTurret.this.tr.x, BarrelsItemTurret.this.tr.y, BarrelsItemTurret.this.tr3.x, BarrelsItemTurret.this.tr3.y);
                this.bullet(type, rot + Mathf.range(BarrelsItemTurret.this.inaccuracy));
                ++this.shotCounter;
                this.effects();
                this.useAmmo();
            } else {
                super.shoot(type);
            }

        }

        protected void shootBarrel(BulletType type, int index) {
            this.recoil = Mathf.clamp(this.recoil + BarrelsItemTurret.this.recoilAmount / 2.0F, 0.0F, BarrelsItemTurret.this.recoilAmount);
            float i = (float)(this.barrelShotCounters[index] % 2) - 0.5F;
            BarrelsItemTurret.this.tr.trns(this.rotation - 90.0F, ((Barrel)BarrelsItemTurret.this.barrels.get(index)).x * i + Mathf.range(BarrelsItemTurret.this.xRand), ((Barrel)BarrelsItemTurret.this.barrels.get(index)).y);
            float rot = this.rotation;
            if (BarrelsItemTurret.this.focus) {
                BarrelsItemTurret.this.tr3.trns(this.rotation, Math.max(Mathf.dst(this.x, this.y, this.targetPos.x, this.targetPos.y), (float)(BarrelsItemTurret.this.size * 8)));
                rot = Angles.angle(BarrelsItemTurret.this.tr.x, BarrelsItemTurret.this.tr.y, BarrelsItemTurret.this.tr3.x, BarrelsItemTurret.this.tr3.y);
            }

            this.bullet(type, rot + Mathf.range(BarrelsItemTurret.this.inaccuracy));
            int var10002 = this.barrelShotCounters[index]++;
            this.effects();
            this.useAmmo();
        }

        protected void updateShooting() {
            super.updateShooting();
            int i = 0;

            for(int len = BarrelsItemTurret.this.barrels.size; i < len; ++i) {
                if (this.hasAmmo()) {
                    if (this.barrelReloads[i] >= ((Barrel)BarrelsItemTurret.this.barrels.get(i)).reloadTime) {
                        this.shootBarrel(this.peekAmmo(), i);
                        this.barrelReloads[i] = 0.0F;
                    } else {
                        float[] var10000 = this.barrelReloads;
                        var10000[i] += this.delta() * this.peekAmmo().reloadMultiplier * this.baseReloadSpeed();
                    }
                }
            }

        }
    }
}
