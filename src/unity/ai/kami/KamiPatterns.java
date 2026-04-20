package unity.ai.kami;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.FloatSeq;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.gen.Bullet;
import mindustry.gen.Unit;
import unity.ai.kami.KamiPattern.PatternType;
import unity.content.UnityBullets;
import unity.entities.bullet.kami.NewKamiLaserBulletType;
import unity.gen.KamiBullet;
import unity.gen.KamiLaser;

public class KamiPatterns {
    public static final int[] zero = new int[]{0};
    public static KamiPattern basicPattern1;
    public static KamiPattern basicPattern2;
    public static KamiPattern expandPattern;
    public static KamiPattern flowerPattern;
    public static KamiPattern flowerPattern2;
    public static KamiPattern hyperSpeedPattern;

    public static void load() {
        basicPattern1 = new KamiPattern(1200.0F) {
            {
                this.type = PatternType.permanent;
            }

            public void update(KamiAI ai) {
                Unit u = ai.unit;
                int diff = 6 + Mathf.clamp(ai.difficulty / 2, 0, 6);
                int diff2 = 16 + Mathf.clamp(ai.difficulty * 2, 0, 16);
                float turn = Mathf.sin(ai.patternTime, 90.0F, 0.75F);
                if (ai.shoot(0, 15.0F)) {
                    for(int i = 0; i < diff; ++i) {
                        float ang = (float)i * (360.0F / (float)diff) + ai.reloads[1];
                        KamiBullet b = (KamiBullet)UnityBullets.kamiBullet2.create(u, u.team, u.x, u.y, ang);
                        b.width = b.length = 4.0F;
                        b.turn = turn;
                        b.lifetime = 300.0F;
                        b.vel.scl(4.0F);
                    }

                    float[] var10000 = ai.reloads;
                    var10000[1] += 180.0F / (float)diff;
                }

                if (ai.shoot(2, 40.0F)) {
                    for(int i = 0; i < diff2; ++i) {
                        float ang = (float)i * (360.0F / (float)diff2) + ai.reloads[3];
                        KamiBullet b = (KamiBullet)UnityBullets.kamiBullet2.create(u, u.team, u.x, u.y, ang);
                        b.width = b.length = 10.0F;
                        b.lifetime = 300.0F;
                        b.vel.scl(5.0F);
                    }

                    float[] var12 = ai.reloads;
                    var12[3] += 180.0F / (float)diff2;
                }

            }
        };
        basicPattern2 = new KamiPattern(1200.0F) {
            public void init(KamiAI ai) {
                ai.reloads[0] = 1.0F;
                ai.reloads[4] = 1.0F;
            }

            public void update(KamiAI ai) {
                Unit u = ai.unit;
                int diff = 8 + ai.difficulty / 2;
                if (ai.reloads[3] < 120.0F && ai.shoot(1, 5.0F)) {
                    for(int i = 0; i < diff; ++i) {
                        float ang = (float)i * (360.0F / (float)diff) + ai.reloads[2];
                        KamiBullet b = (KamiBullet)UnityBullets.kamiBullet3.create(u, u.team, u.x, u.y, ang);
                        b.width = b.length = 6.0F;
                        b.turn = 0.25F * ai.reloads[0];
                        b.lifetime = 360.0F;
                        b.vel.scl(4.0F);
                    }

                    float[] var10000 = ai.reloads;
                    var10000[0] *= -1.0F;
                    var10000 = ai.reloads;
                    var10000[2] += 40.0F / (float)diff * ai.reloads[4];
                }

                float[] var8 = ai.reloads;
                var8[3] += Time.delta;
                if (ai.reloads[3] > 210.0F) {
                    ai.reloads[2] = 0.0F;
                    var8 = ai.reloads;
                    var8[3] -= 210.0F;
                    var8 = ai.reloads;
                    var8[4] *= -1.0F;
                }

            }
        };
        expandPattern = new KamiPattern.StagePattern(-4.0F, PatternType.bossBasic, new KamiPattern.StagePattern.Stage[]{new KamiPattern.StagePattern.Stage(240.0F, 2, (ai, data) -> {
            Unit u = ai.unit;
            if (data.time < 150.0F && ai.shoot(2, 8.0F)) {
                int amount = ai.difficulty * 2 + (int)ai.reloads[0] + 1;
                float spread = 18.0F / (1.0F + (float)ai.difficulty / 3.0F);
                Angles.shotgun(amount, spread, ai.reloads[1] + Mathf.sin(ai.reloads[3], 3.5F, spread / 1.5F) * ai.reloads[4], (f) -> {
                    Vec2 v = Tmp.v1.trns(f, 16.0F);
                    KamiBullet b = (KamiBullet)UnityBullets.kamiBullet3.create(u, u.team, u.x + v.x, u.y + v.y, f);
                    b.width = b.length = 14.0F;
                    b.lifetime = 480.0F;
                    b.vel.scl(2.75F + (float)ai.difficulty / 6.0F);
                });
                int var10002 = ai.reloads[0]++;
                var10002 = ai.reloads[3]++;
            }

        }, (ai, data) -> {
            ai.reloads[0] = ai.reloads[3] = 0.0F;
            ai.reloads[1] = ai.targetAngle();
            float[] var10000 = ai.reloads;
            var10000[4] *= -1.0F;
        }), new KamiPattern.StagePattern.Stage(150.0F, (ai, data) -> {
            Unit u = ai.unit;
            if (data.time < 120.0F && ai.shoot(2, 5.0F)) {
                int amount = ai.difficulty * 4 + (int)ai.reloads[0] + 8;

                for(int i = 0; i < amount; ++i) {
                    float ang = (float)i * (360.0F / (float)amount) + ai.reloads[1] + Mathf.sin(ai.reloads[3], 4.0F, 180.0F / (float)amount) * ai.reloads[5];
                    KamiBullet b = (KamiBullet)UnityBullets.kamiBullet3.create(u, u.team, u.x, u.y, ang);
                    b.width = b.length = 16.0F;
                    b.lifetime = 540.0F;
                    b.vel.scl(2.0F + (float)ai.difficulty / 8.0F);
                }

                float[] var10000 = ai.reloads;
                var10000[0] += 1.0F + (float)ai.difficulty * 0.5F;
                int var10002 = ai.reloads[3]++;
            }

        }, (ai, data) -> {
            float[] var10000 = ai.reloads;
            var10000[5] *= -1.0F;
        })}) {
            public void init(KamiAI ai) {
                super.init(ai);
                ai.reloads[4] = ai.reloads[5] = 1.0F;
            }
        };
        flowerPattern = new KamiPattern.StagePattern(-3.0F, new KamiPattern.StagePattern.Stage[]{new KamiPattern.StagePattern.Stage(210.0F, 2, (ai, d) -> {
            Unit u = ai.unit;
            if (d.time < 120.0F && ai.shoot(2, 7.0F)) {
                int petals = Mathf.clamp(3 + ai.difficulty, 0, 8);
                int amount = 4 + ai.difficulty;
                int len = ai.difficulty;
                float spacing = 180.0F / (float)petals / 17.142857F;

                for(int i = 0; i < petals; ++i) {
                    float ang = (float)i * 360.0F / (float)petals + ai.reloads[0];
                    int[] sign = ai.reloads[1] <= 0.0F ? zero : Mathf.signs;

                    for(int s : sign) {
                        KamiBulletPresets.shootLine(3.0F - (float)len / 16.0F, 5.0F + (float)len / 4.0F, amount, (v) -> {
                            KamiBullet b = (KamiBullet)UnityBullets.kamiBullet3.create(u, u.team, u.x, u.y, ang + ai.reloads[1] * (float)s);
                            b.width = b.length = 12.0F;
                            b.lifetime = 480.0F;
                            b.vel.scl(v);
                        });
                    }
                }

                float[] var10000 = ai.reloads;
                var10000[1] += spacing;
            }

        }, (ai, d) -> {
            ai.reloads[0] = ai.targetAngle();
            ai.reloads[1] = 0.0F;
        }), new KamiPattern.StagePattern.Stage(270.0F, (ai, d) -> {
            Unit u = ai.unit;
            if (d.time < 190.0F && ai.shoot(1, 15.0F)) {
                int diff = 3 + ai.difficulty / 3;
                int amount = 4 + ai.difficulty;

                for(int i = 0; i < diff; ++i) {
                    float ang = (float)i * 360.0F / (float)diff + ai.reloads[0];

                    for(int s : Mathf.signs) {
                        KamiBulletPresets.shootLine(3.0F, 5.5F, 4 + amount, (v, j) -> {
                            KamiBullet b = (KamiBullet)UnityBullets.kamiBullet2.create(u, u.team, u.x, u.y, ang + ai.reloads[2] * (float)s);
                            b.width = b.length = 12.0F;
                            b.lifetime = 420.0F;
                            b.vel.scl(v);
                            b.turn = (0.2F + (1.0F - j / (float)amount) * 0.3F) * (float)s * (1.0F - ai.reloads[3]);
                        });
                    }
                }

                float[] var10000 = ai.reloads;
                var10000[2] += 15.0F;
                var10000 = ai.reloads;
                var10000[3] += 0.05F;
            }

        }, (ai, d) -> {
            ai.reloads[0] = ai.targetAngle();
            ai.reloads[1] = ai.reloads[2] = ai.reloads[3] = 0.0F;
        })});
        flowerPattern2 = new KamiPattern(2100.0F) {
            {
                this.type = PatternType.bossBasic;
            }

            public void init(KamiAI ai) {
                ai.reloads[4] = 720.0F;
                ai.reloads[3] = 1.0F;
            }

            public void update(KamiAI ai) {
                Unit u = ai.unit;
                int petals = 4 + ai.difficulty / 3;
                if (ai.burst(0, 280.0F, 3, 35.0F, () -> {
                    float[] var10000 = ai.reloads;
                    var10000[3] *= -1.0F;
                    ai.reloads[2] = 0.0F;
                    ai.reloads[8] = 0.0F;
                })) {
                    float x = u.x;
                    float y = u.y;
                    float side = ai.reloads[3];
                    float d = ai.reloads[8];

                    for(int i = 0; i < petals; ++i) {
                        float ang = (float)i * 360.0F / (float)petals + ai.reloads[2];
                        KamiBulletPresets.petal(ai, 180.0F / (float)petals, 40.0F, 9 + ai.difficulty, (f) -> f * (2.0F - f), (angle, delay) -> {
                            KamiBullet b = (KamiBullet)UnityBullets.kamiBullet2.create(u, u.team, x, y, ang + angle);
                            b.width = b.length = 13.0F;
                            b.lifetime = 720.0F;
                            b.time = delay;
                            b.vel.scl(5.0F);
                            b.bdata = KamiBulletDatas.stopChangeDirection;
                            b.fdata = 240.0F - d;
                            b.fdata2 = ang + 160.0F * side;
                        });
                    }

                    float[] var10000 = ai.reloads;
                    var10000[2] += 120.0F / (float)petals * ai.reloads[3];
                    var10000 = ai.reloads;
                    var10000[8] += 35.0F;
                }

                int d = ai.difficulty;
                int bursts = 12 + d * 2;
                float spacing = Math.max(7.0F - (float)d, 3.0F);
                if (ai.burst(4, 115.0F, bursts, spacing, () -> {
                    ai.reloads[6] = 1.0F;
                    ai.reloads[7] = ai.targetAngle();
                })) {
                    for(int s : Mathf.signs) {
                        float ang = ai.reloads[7] + ai.reloads[6] * 90.0F * (float)s;
                        KamiBullet b = (KamiBullet)UnityBullets.kamiBullet2.create(u, u.team, u.x, u.y, ang);
                        b.width = b.length = 16.0F;
                        b.lifetime = 420.0F;
                        b.vel.scl(4.0F);
                    }

                    float[] var20 = ai.reloads;
                    var20[6] -= 1.0F / (float)bursts;
                }

            }
        };
        hyperSpeedPattern = new KamiPattern(7200.0F) {
            {
                this.type = PatternType.advance;
                this.data = () -> new HyperSpeedData();
            }

            public void draw(KamiAI ai) {
                super.draw(ai);
                HyperSpeedData d = (HyperSpeedData)ai.patternData;
                if (d != null && !d.nextPosition.isEmpty()) {
                    Color c = Tmp.c1.set(Draw.getColor());
                    FloatSeq f = d.nextPosition;
                    float time = 0.0F;
                    float size = ai.unit.hitSize / 1.7F * 2.0F;

                    for(int i = 0; i < f.size - 3; i += 3) {
                        float x = f.get(i);
                        float y = f.get(i + 1);
                        float x2 = f.get(i + 3);
                        float y2 = f.get(i + 4);
                        float ang = Angles.angle(x, y, x2, y2);
                        if (f.get(i + 2) > 0.0F) {
                            time += f.get(i + 2);
                        }

                        float fout = (time - 6.0F) / 60.0F;
                        float fout2 = time / 66.0F;
                        if (fout > 1.0E-4F && fout <= 1.0F) {
                            Draw.color(c);
                            float fin = 1.0F - fout;
                            Vec2 v = Tmp.v1.trns(ang + 90.0F, fout * 70.0F + size / 2.0F);
                            Lines.stroke(fin * 2.0F);

                            for(int s : Mathf.signs) {
                                Lines.line(x + v.x * (float)s, y + v.y * (float)s, x2 + v.x * (float)s, y2 + v.y * (float)s, false);
                            }
                        }

                        if (fout2 > 1.0E-4F) {
                            Tmp.c2.set(c).a(0.5F * Mathf.clamp(fout2));
                            Draw.color(Tmp.c2);
                            Lines.stroke(size);
                            Lines.line(x, y, x2, y2, false);
                        }
                    }
                }

            }

            public void update(KamiAI ai) {
                HyperSpeedData d = (HyperSpeedData)ai.patternData;
                FloatSeq pos = d.nextPosition;
                Unit u = ai.unit;
                Vec2 v = Tmp.v1;
                if (pos.isEmpty()) {
                    ai.updateFollowing();
                }

                if (ai.shoot(0, 420.0F)) {
                    d.index = 0;
                    pos.add(u.x, u.y, 66.0F);
                    v.trns(ai.targetAngle(), u.dst(ai.target) * 2.0F).add(u.x, u.y);
                    pos.add(v.x, v.y, 7.0F);
                    float lx = v.x;
                    float ly = v.y;

                    for(int i = 0; i < 12 + ai.difficulty * 2; ++i) {
                        float ang = Angles.angle(lx, ly, ai.x, ai.y) + Mathf.range(70.0F);
                        v.trns(ang, 840.0F).add(ai.x, ai.y);
                        pos.add(v.x, v.y, 7.0F);
                        lx = v.x;
                        ly = v.y;
                    }
                }

                if (!pos.isEmpty() && ai.reloads[2] <= 0.0F) {
                    float fin = 1.0F - pos.get(d.index + 2) / 6.0F;
                    float lastTime = pos.get(d.index + 2);
                    float[] var10000 = pos.items;
                    int var10001 = d.index + 2;
                    var10000[var10001] -= Time.delta;
                    boolean s = pos.get(d.index + 2) < 7.0F && lastTime >= 7.0F;
                    if (s) {
                        KamiLaser l = ((NewKamiLaserBulletType)UnityBullets.kamiLaser2).createL(u, u.team, u.x, u.y, u.x, u.y, u);
                        l.lifetime = 16.0F;
                        l.width = u.hitSize / 1.7F;
                        l.bdata = KamiBulletDatas.hyperSpeedLaser1;
                        l.intervalCollision = false;
                        l.ellipseCollision = false;
                        d.dashBullet = l;
                        KamiBullet b = (KamiBullet)UnityBullets.kamiBullet3.create(u, u.team, u.x, u.y, 0.0F);
                        b.bdata = KamiBulletDatas.positionLock;
                        b.width = b.length = u.hitSize / 1.5F;
                        b.lifetime = 6.0F;
                        b.vel.setZero();
                    }

                    if (fin > 0.0F) {
                        float x = pos.get(d.index);
                        float nx = pos.get(d.index + 3);
                        float y = pos.get(d.index + 1);
                        float ny = pos.get(d.index + 4);
                        v.set(x, y).lerp(nx, ny, Mathf.clamp(fin));
                        u.set(v);
                        u.rotation = Angles.angle(x, y, nx, ny);
                        if (fin >= 1.0F) {
                            d.index += 3;
                            if (d.dashBullet != null) {
                                d.dashBullet.set(u);
                            }

                            d.dashBullet = null;
                            if (d.index >= pos.size - 3) {
                                d.index = 0;
                                int var10002 = ai.reloads[1]++;
                                ai.reloads[2] = 40.0F;
                                Tmp.v1.set(nx, ny).sub(x, y).setLength(5.0F);
                                u.vel.add(Tmp.v1);
                            }
                        }
                    }
                }

                if (ai.reloads[2] > 0.0F) {
                    float[] var19 = ai.reloads;
                    var19[2] -= Time.delta;
                    if (ai.reloads[2] <= 0.0F) {
                        pos.clear();
                        if (ai.reloads[1] >= 5.0F) {
                            ai.patternTime = 0.0F;
                        }
                    }
                }

            }
        };
    }

    private static class HyperSpeedData extends KamiPattern.PatternData {
        FloatSeq nextPosition;
        int index;
        Bullet dashBullet;

        private HyperSpeedData() {
            this.nextPosition = new FloatSeq();
            this.index = 0;
        }
    }
}
