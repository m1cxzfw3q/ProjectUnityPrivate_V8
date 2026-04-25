package unity.entities.bullet.laser;

import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Position;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.content.StatusEffects;
import mindustry.entities.Damage;
import mindustry.entities.Fires;
import mindustry.entities.Lightning;
import mindustry.entities.bullet.ContinuousLaserBulletType;
import mindustry.gen.Bullet;
import mindustry.gen.Unit;
import mindustry.gen.Unitc;
import unity.content.UnityFx;
import unity.content.UnityStatusEffects;
import unity.entities.ExtraEffect;
import unity.entities.UnitVecData;
import unity.graphics.UnityPal;
import unity.util.Utils;

public class SparkingContinuousLaserBulletType extends ContinuousLaserBulletType {
    protected float fromBlockChance;
    protected float fromBlockDamage;
    protected float fromLaserChance;
    protected float fromLaserDamage;
    protected float incendStart;
    protected float coneRange;
    protected int fromLaserLen;
    protected int fromLaserLenRand;
    protected int fromLaserAmount;
    protected int fromBlockLen;
    protected int fromBlockLenRand;
    protected int fromBlockAmount;
    protected boolean extinction;
    protected final Seq<Unit> tempSeq;

    public SparkingContinuousLaserBulletType(float damage) {
        super(damage);
        this.fromBlockChance = 0.4F;
        this.fromBlockDamage = 23.0F;
        this.fromLaserChance = 0.9F;
        this.fromLaserDamage = 23.0F;
        this.incendStart = 2.9F;
        this.coneRange = 1.1F;
        this.fromLaserLen = 4;
        this.fromLaserLenRand = 5;
        this.fromLaserAmount = 1;
        this.fromBlockLen = 2;
        this.fromBlockLenRand = 5;
        this.fromBlockAmount = 1;
        this.tempSeq = new Seq();
        this.lightningColor = UnityPal.laserOrange;
    }

    public SparkingContinuousLaserBulletType() {
        this(0.0F);
    }

    public void update(Bullet b) {
        super.update(b);
        float realLength = Damage.findLaserLength(b, this.length);

        for(int i = 0; i < this.fromBlockAmount; ++i) {
            if (Mathf.chanceDelta((double)this.fromBlockChance)) {
                Lightning.create(b.team, this.lightningColor, this.fromBlockDamage, b.x, b.y, b.rotation(), Mathf.round(this.length / 8.0F) + this.fromBlockLen + Mathf.random(this.fromBlockLenRand));
            }
        }

        for(int i = 0; i < this.fromLaserAmount; ++i) {
            if (Mathf.chanceDelta((double)this.fromLaserChance)) {
                int lLength = this.fromLaserLen + Mathf.random(this.fromLaserLenRand);
                Tmp.v1.trns(b.rotation(), Mathf.random(0.0F, Math.max(realLength - (float)lLength * 8.0F, 4.0F)));
                Lightning.create(b.team, this.lightColor, this.fromLaserDamage, b.x + Tmp.v1.x, b.y + Tmp.v1.y, b.rotation(), lLength);
            }
        }

        if (Mathf.chance((double)this.incendChance)) {
            Tmp.v1.trns(b.rotation(), Mathf.random(this.incendStart, realLength));
            Damage.createIncend(b.x + Tmp.v1.x, b.y + Tmp.v1.y, this.incendSpread, this.incendAmount);
        }

        if (this.extinction) {
            if (b.timer(2, 15.0F)) {
                b.data = Utils.castConeTile(b.x, b.y, this.length * this.coneRange, b.rotation(), 70.0F, 45, (build, tile) -> {
                    float angD = Mathf.clamp(1.0F - Utils.angleDist(Angles.angle(tile.worldx() - b.x, tile.worldy() - b.y), b.rotation()) / 70.0F);
                    float dst = Mathf.clamp(1.0F - Mathf.dst(tile.worldx() - b.x, tile.worldy() - b.y) / (this.length * this.coneRange));
                    if (Mathf.chance((double)(Interp.smooth.apply(angD) * 0.32F * Mathf.clamp(dst * 1.7F)))) {
                        Fires.create(tile);
                    }

                    if (build != null && build.team != b.team) {
                        build.damage(Interp.smooth.apply(angD) * 23.3F * Mathf.clamp(dst * 1.7F));
                        ExtraEffect.addMoltenBlock(build);
                    }

                }, (tile) -> tile.block().absorbLasers || tile.block().insulated);
            }

            Object var7 = b.data;
            if (var7 instanceof float[]) {
                float[] data = (float[])var7;
                Utils.castCone(b.x, b.y, this.length * this.coneRange, b.rotation(), 70.0F, (e, dst, angD) -> {
                    float clamped = Mathf.clamp(dst * 1.7F);
                    int idx = Mathf.clamp(Mathf.round((Utils.angleDistSigned(b.angleTo(e), b.rotation()) + 70.0F) / 140.0F * (float)(data.length - 1)), 0, data.length - 1);
                    boolean h = b.dst2(e) + e.hitSize / 2.0F < data[idx] || e.isFlying();
                    if (h) {
                        if (!e.dead) {
                            float damageMulti = e.team != b.team ? 0.25F : 1.0F;
                            e.damage(28.0F * angD * dst * damageMulti * Time.delta);
                            Tmp.v1.trns(Angles.angle(b.x, b.y, e.x, e.y), angD * clamped * 160.0F * (e.hitSize / 20.0F + 0.95F));
                            e.impulse(Tmp.v1);
                            if (Mathf.chanceDelta((double)(Mathf.clamp(angD * dst * 12.0F) * 0.9F))) {
                                ExtraEffect.createEvaporation(e.x, e.y, angD * dst / 70.0F, e, b.owner);
                            }

                            e.apply(UnityStatusEffects.radiation, angD * damageMulti * 3800.3F * clamped);
                            e.apply(StatusEffects.melting, angD * damageMulti * 240.3F * clamped);
                        } else {
                            this.tempSeq.add(e);
                            Tmp.v1.trns(Angles.angle(b.x, b.y, e.x, e.y), angD * clamped * 130.0F / Math.max(e.mass() / 120.0F + 0.9916667F, 1.0F));
                            Tmp.v1.scl(12.0F);
                            UnityFx.evaporateDeath.at(e.x, e.y, 0.0F, new UnitVecData(e, Tmp.v1.cpy()));

                            for(int i = 0; i < 12; ++i) {
                                Tmp.v1.trns(Angles.angle(b.x, b.y, e.x(), e.y()), 65.0F + Mathf.range(0.3F)).add(e);
                                Tmp.v2.trns(Mathf.random(360.0F), Mathf.random(e.hitSize / 1.25F));
                                UnityFx.vaporation.at(e.x, e.y, 0.0F, new Position[]{e, Tmp.v1.cpy(), Tmp.v2.cpy()});
                            }
                        }
                    }

                });
                this.tempSeq.each(Unitc::remove);
                this.tempSeq.clear();
            }
        }

    }
}
