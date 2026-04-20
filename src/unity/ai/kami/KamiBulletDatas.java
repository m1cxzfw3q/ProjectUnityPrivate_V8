package unity.ai.kami;

import arc.math.Mathf;
import arc.math.geom.Position;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.gen.Bullet;
import unity.gen.KamiBullet;
import unity.gen.KamiLaser;

public class KamiBulletDatas {
    public static Seq<KamiBulletDataBase<?>> all = new Seq();
    public static KamiBulletData turnDelay1 = new KamiBulletData() {
        public void update(KamiBullet b) {
            if (b.time >= 108.0F) {
                b.turn = b.fdata;
            }

        }
    };
    public static KamiBulletData suddenTurnDelay1 = new KamiBulletData() {
        public void update(KamiBullet b) {
            if (b.time >= 108.0F && b.fdata != 0.0F) {
                b.rotation(b.rotation() + b.fdata);
                b.fdata = 0.0F;
            }

        }
    };
    public static KamiBulletData stopChangeDirection = new KamiBulletData() {
        public void update(KamiBullet b) {
            if (b.time >= 108.0F && b.fdata > 0.0F) {
                b.vel.scl(1.0F - 0.15F * Time.delta);
                if (b.time >= b.fdata) {
                    b.rotation(b.fdata2);
                    b.vel.trns(b.rotation(), 4.0F);
                    b.fdata = 0.0F;
                }
            }

        }
    };
    public static KamiBulletData positionLock = new KamiBulletData() {
        public void update(KamiBullet b) {
            if (b.owner instanceof Position) {
                b.set((Position)b.owner);
            }

        }
    };
    public static KamiLaserData hyperSpeedLaser1 = new KamiLaserData() {
        public void update(KamiLaser b) {
            if (b.data instanceof Position) {
                Position p = (Position)b.data;
                if (b.time <= 7.0F) {
                    b.x = p.getX();
                    b.y = p.getY();
                    b.fdata = Mathf.dst(b.x, b.y, b.x2, b.y2);
                } else {
                    Tmp.v1.set(b.x2, b.y2).approachDelta(Tmp.v2.set(b.x, b.y), b.fdata / 9.0F);
                    b.x2 = Tmp.v1.x;
                    b.y2 = Tmp.v1.y;
                    if (b.within(b.x2, b.y2, 2.0F)) {
                        b.remove();
                    }
                }
            }

        }
    };

    public static class KamiBulletData extends KamiBulletDataBase<KamiBullet> {
    }

    public static class KamiLaserData extends KamiBulletDataBase<KamiLaser> {
    }

    private abstract static class KamiBulletDataBase<T extends Bullet> {
        public int id;

        KamiBulletDataBase() {
            this.id = KamiBulletDatas.all.size;
            KamiBulletDatas.all.add(this);
        }

        public void update(T b) {
        }

        public void removed(T b) {
        }
    }
}
