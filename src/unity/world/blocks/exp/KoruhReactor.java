package unity.world.blocks.exp;

import arc.Core;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.ui.Bar;
import mindustry.world.blocks.power.ImpactReactor;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import unity.content.UnityFx;
import unity.entities.ExpOrbs;
import unity.graphics.UnityPal;

public class KoruhReactor extends ImpactReactor {
    public int expUse = 2;
    public int expCapacity = 24;

    public KoruhReactor(String name) {
        super(name);
    }

    public void setStats() {
        super.setStats();
        this.stats.add(Stat.itemCapacity, "@", new Object[]{Core.bundle.format("exp.expAmount", new Object[]{this.expCapacity})});
        this.stats.add(Stat.input, "@ [lightgray]@[]", new Object[]{Core.bundle.format("explib.expAmount", new Object[]{(float)this.expUse / this.itemDuration * 60.0F}), StatUnit.perSecond.localized()});
    }

    public void setBars() {
        super.setBars();
        this.bars.add("exp", (entity) -> new Bar(() -> Core.bundle.get("bar.exp"), () -> UnityPal.exp, () -> 1.0F * (float)entity.exp / (float)this.expCapacity));
    }

    public class KoruhReactorBuild extends ImpactReactor.ImpactReactorBuild implements ExpHolder {
        public int exp;

        public KoruhReactorBuild() {
            super(KoruhReactor.this);
        }

        public int getExp() {
            return this.exp;
        }

        public int handleExp(int amount) {
            if (amount > 0) {
                int e = Math.min(KoruhReactor.this.expCapacity - this.exp, amount);
                this.exp += e;
                return e;
            } else {
                int e = Math.min(-amount, this.exp);
                this.exp -= e;
                return -e;
            }
        }

        public int unloadExp(int amount) {
            int e = Math.min(amount, this.exp);
            this.exp -= e;
            return e;
        }

        public boolean acceptOrb() {
            return true;
        }

        public boolean handleOrb(int orbExp) {
            return this.handleExp(orbExp) > 0;
        }

        public void updateTile() {
            super.updateTile();
            if (this.consValid()) {
                if (this.exp >= KoruhReactor.this.expUse) {
                    if (this.productionEfficiency >= 0.8F && Mathf.randomBoolean(0.001F)) {
                        float dir = Mathf.random(360.0F);
                        Vec2 vec = new Vec2();
                        vec.trns(dir, ((float)KoruhReactor.this.size + Mathf.random(0.5F, 1.5F)) * 8.0F).add(this.x, this.y);
                        UnityFx.expDump.at(this.x, this.y, 0.0F, vec);
                        Time.run(UnityFx.expDump.lifetime, () -> ExpOrbs.spreadExp(vec.x, vec.y, 10, 0.0F));
                    }
                } else {
                    this.damage(1.0F);
                    if (this.health <= 0.0F) {
                        int i = 0;

                        for(int m = Mathf.ceilPositive((float)this.exp * 1.5F); i < m; ++i) {
                            Time.run((float)(i * 10), () -> {
                                float dir = Mathf.random(360.0F);
                                Vec2 vec = new Vec2();
                                vec.trns(dir, ((float)KoruhReactor.this.size + Mathf.random(0.5F, 1.5F)) * 8.0F).add(this.x, this.y);
                                UnityFx.expDump.at(this.x, this.y, 0.0F, vec);
                                Time.run(UnityFx.expDump.lifetime, () -> ExpOrbs.spreadExp(vec.x, vec.y, 10, 0.0F));
                            });
                        }
                    }
                }
            }

        }

        public void consume() {
            super.consume();
            if (this.exp >= KoruhReactor.this.expUse) {
                this.handleExp(-KoruhReactor.this.expUse);
            }

        }

        public void onDestroyed() {
            super.onDestroyed();
        }

        public void write(Writes write) {
            super.write(write);
            write.i(this.exp);
        }

        public void read(Reads read, byte revision) {
            super.read(read, revision);
            this.exp = read.i();
        }
    }
}
