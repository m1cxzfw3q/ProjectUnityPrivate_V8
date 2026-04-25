//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package unity.world.blocks.units;

import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.util.Time;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.BlockUnitc;
import mindustry.gen.Building;
import mindustry.gen.Player;
import mindustry.gen.Sounds;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;
import mindustry.world.Block;
import mindustry.world.blocks.payloads.Payload;
import unity.content.UnityFx;
import unity.content.UnityStatusEffects;
import unity.graphics.UnityPal;

public class TeleUnit extends Block {
    protected static final TeleUnitBuild[] heads;
    protected static final int[] listSizes;
    protected TextureRegion lightRegion;
    protected TextureRegion topRegion;
    protected TextureRegion arrowRegion;

    public TeleUnit(String name) {
        super(name);
        this.update = this.configurable = this.outputsPayload = true;
        Events.on(EventType.WorldLoadEvent.class, (e) -> {
            for(int i = 0; i < heads.length; ++i) {
                heads[i] = null;
                listSizes[i] = 0;
            }

        });
    }

    public void load() {
        super.load();
        this.lightRegion = Core.atlas.find(this.name + "-lights");
        this.topRegion = Core.atlas.find(this.name + "-top");
        this.arrowRegion = Core.atlas.find("transfer-arrow");
    }

    static {
        heads = new TeleUnitBuild[Team.baseTeams.length];
        listSizes = new int[Team.baseTeams.length];
    }

    public class TeleUnitBuild extends Building {
        protected float warmup;
        protected float warmup2;
        protected TeleUnitBuild next;
        protected TeleUnitBuild prev;
        protected Team previousTeam;

        protected boolean isTeamChanged() {
            return this.previousTeam != this.team;
        }

        public void updateTile() {
            this.warmup = Mathf.lerpDelta(this.warmup, this.consValid() ? 1.0F : 0.0F, 0.05F);
            this.warmup2 = Mathf.lerpDelta(this.warmup2, this.consValid() && this.enabled ? 1.0F : 0.0F, 0.05F);
            if (this.isTeamChanged()) {
                this.onRemoved();
                this.created();
            }

        }

        public void draw() {
            super.draw();
            Draw.color(Color.white);
            Draw.alpha(0.45F + Mathf.absin(7.0F, 0.26F));
            Draw.rect(TeleUnit.this.topRegion, this.x, this.y);
            if (this.warmup >= 0.001F) {
                Draw.z(100.0F);
                Draw.color(UnityPal.dirium, this.team.color, Mathf.absin(19.0F, 1.0F));
                Lines.stroke((Mathf.absin(62.0F, 0.5F) + 0.5F) * this.warmup);
                Lines.square(this.x, this.y, 10.5F, 45.0F);
                if (this.warmup2 >= 0.001F) {
                    Lines.stroke((Mathf.absin(62.0F, 1.0F) + 1.0F) * this.warmup2);
                    Lines.square(this.x, this.y, 8.5F, Time.time / 2.0F);
                    Lines.square(this.x, this.y, 8.5F, -1.0F * Time.time / 2.0F);
                }
            }

            Draw.reset();
        }

        public void drawSelect() {
            Draw.color(this.consValid() ? (this.inRange(Vars.player) ? UnityPal.dirium : Pal.accent) : Pal.darkMetal);
            float length = (float)(8 * TeleUnit.this.size) / 2.0F + 3.0F + Mathf.absin(5.0F, 2.0F);
            Draw.rect(TeleUnit.this.arrowRegion, this.x + length, this.y, 180.0F);
            Draw.rect(TeleUnit.this.arrowRegion, this.x, this.y + length, 270.0F);
            Draw.rect(TeleUnit.this.arrowRegion, this.x - length, this.y, 0.0F);
            Draw.rect(TeleUnit.this.arrowRegion, this.x, this.y - length, 90.0F);
            Draw.color();
        }

        public boolean shouldAmbientSound() {
            return this.consValid();
        }

        public void created() {
            this.previousTeam = this.team;
            TeleUnitBuild temp = TeleUnit.heads[this.team.id];
            int var10002 = TeleUnit.listSizes[this.team.id]++;
            if (temp == null) {
                TeleUnit.heads[this.team.id] = this.next = this.prev = this;
            } else {
                int i = 0;

                for(int len = TeleUnit.listSizes[this.team.id] - 1; i < len; ++i) {
                    if (temp.pos() > this.pos()) {
                        if (i == 0) {
                            TeleUnit.heads[this.team.id] = this;
                        }

                        this.next = temp;
                        this.prev = temp.prev;
                        this.prev.next = temp.prev = this;
                        return;
                    }

                    temp = temp.next;
                }

                this.next = temp;
                this.prev = temp.prev;
                this.prev.next = temp.prev = this;
            }
        }

        public void onRemoved() {
            int a = this.isTeamChanged() ? this.previousTeam.id : this.team.id;
            if (TeleUnit.heads[a] != null) {
                if (this == TeleUnit.heads[a]) {
                    if (TeleUnit.listSizes[a] > 1) {
                        TeleUnit.heads[a] = this.next;
                    } else {
                        TeleUnit.heads[a] = null;
                    }
                }

                this.prev.next = this.next;
                this.next.prev = this.prev;
                this.next = this.prev = null;
                int var10002 = TeleUnit.listSizes[a]--;
            }

        }

        protected TeleUnitBuild getDest() {
            TeleUnitBuild temp = this;
            int i = 0;

            for(int len = TeleUnit.listSizes[this.previousTeam.id]; i < len; ++i) {
                temp = temp.next;
                if (temp != null && temp.enabled && temp.power.graph == this.power.graph) {
                    return temp;
                }
            }

            return temp;
        }

        protected boolean inRange(Player player) {
            return player.unit() != null && player.unit().isValid() && Math.abs(player.unit().x - this.x) <= 20.0F && Math.abs(player.unit().y - this.y) <= 20.0F;
        }

        public boolean shouldShowConfigure(Player player) {
            return this.consValid() && this.inRange(player);
        }

        public boolean configTapped() {
            if (this.consValid() && this.inRange(Vars.player)) {
                this.configure((Object)null);
                Sounds.click.at(this);
                return false;
            } else {
                return false;
            }
        }

        public void configured(Unit unit, Object value) {
            if (unit != null && unit.isPlayer() && !(unit instanceof BlockUnitc)) {
                this.tpPlayer(unit.getPlayer());
            }

        }

        protected void tpPlayer(Player player1) {
            this.tpUnit(player1.unit(), player1 == Vars.player);
            if (Vars.player != null && player1 == Vars.player) {
                Core.camera.position.set(player1);
            }

        }

        protected void tpUnit(Unit unit, boolean isPlayer) {
            TeleUnitBuild dest = this.getDest();
            if (dest != null) {
                if (!Vars.headless) {
                    UnityFx.tpIn.at(unit.x, unit.y, unit.rotation - 90.0F, Color.white, unit.type);
                }

                unit.set(dest.x, dest.y);
                unit.snapInterpolation();
                unit.set(dest.x, dest.y);
                if (!Vars.headless) {
                    this.effects(dest, unit.hitSize * 1.7F, isPlayer, unit);
                }

            }
        }

        protected void effects(TeleUnitBuild dest, float hitSize, boolean isPlayer, Unit unit) {
            if (isPlayer) {
                Sounds.plasmadrop.at(dest, Mathf.random() * 0.2F + 1.0F);
                Sounds.lasercharge2.at(this, Mathf.random() * 0.2F + 0.7F);
            } else {
                Sounds.plasmadrop.at(this, Mathf.random() * 0.2F + 1.0F);
                Sounds.lasercharge2.at(dest, Mathf.random() * 0.2F + 0.7F);
            }

            UnityFx.tpOut.at(dest, hitSize);
            UnityFx.tpFlash.at(dest.x, dest.y, 0.0F, Color.white, unit);
        }

        public void unitOn(Unit unit) {
            if (this.consValid()) {
                if (!unit.hasEffect(UnityStatusEffects.tpCoolDown) && !unit.isPlayer()) {
                    this.tpUnit(unit, false);
                    unit.apply(UnityStatusEffects.tpCoolDown, 120.0F);
                }
            }
        }

        public boolean consValid() {
            return this.power.status > 0.98F;
        }

        public boolean acceptPayload(Building source, Payload payload) {
            TeleUnitBuild dest = this.getDest();
            if (this.consValid() && dest.enabled) {
                Building nextBuild;
                boolean var10000;
                label45: {
                    label44: {
                        int ntrns = 1 + TeleUnit.this.size / 2;
                        nextBuild = dest.nearby(Geometry.d4x(source.rotation) * ntrns, Geometry.d4y(source.rotation) * ntrns);
                        if (nextBuild != null) {
                            if (nextBuild.block.size == TeleUnit.this.size && dest.tileX() + Geometry.d4(source.rotation).x * TeleUnit.this.size == nextBuild.tileX() && dest.tileY() + Geometry.d4(source.rotation).y * TeleUnit.this.size == nextBuild.tileY()) {
                                break label44;
                            }

                            if (nextBuild.block.size > TeleUnit.this.size) {
                                if (source.rotation % 2 == 0) {
                                    if (Math.abs(nextBuild.y - dest.y) <= (float)(nextBuild.block.size * 8 - TeleUnit.this.size * 8) / 2.0F) {
                                        break label44;
                                    }
                                } else if (Math.abs(nextBuild.x - dest.x) <= (float)(nextBuild.block.size * 8 - TeleUnit.this.size * 8) / 2.0F) {
                                    break label44;
                                }
                            }
                        }

                        var10000 = false;
                        break label45;
                    }

                    var10000 = true;
                }

                boolean result = var10000;
                if (result && nextBuild.block.outputsPayload && !nextBuild.tile.solid() && (nextBuild.rotation + 2) % 4 != source.rotation) {
                    result = nextBuild.acceptPayload(source, payload);
                    if (result) {
                        nextBuild.handlePayload(source, payload);
                    }

                    return result;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }
}
