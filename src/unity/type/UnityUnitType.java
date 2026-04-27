package unity.type;

import arc.Core;
import arc.audio.Sound;
import arc.func.Boolf;
import arc.func.Func;
import arc.graphics.Color;
import arc.graphics.g2d.Batch;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mat;
import arc.math.Mathf;
import arc.math.geom.Rect;
import arc.math.geom.Vec2;
import arc.struct.FloatSeq;
import arc.struct.Seq;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.core.World;
import mindustry.ctype.ContentType;
import mindustry.entities.Leg;
import mindustry.entities.abilities.Ability;
import mindustry.entities.units.WeaponMount;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Legsc;
import mindustry.gen.Mechc;
import mindustry.gen.Payloadc;
import mindustry.gen.Sounds;
import mindustry.gen.Unit;
import mindustry.gen.WaterMovec;
import mindustry.graphics.Drawf;
import mindustry.graphics.Trail;
import mindustry.type.UnitType;
import mindustry.type.Weapon;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.blocks.environment.ShallowLiquid;
import unity.entities.AbilityTextures;
import unity.entities.Rotor;
import unity.entities.RotorMount;
import unity.entities.TriJointLeg;
import unity.entities.legs.CLegGroup;
import unity.entities.legs.CLegType;
import unity.entities.units.WormDefaultUnit;
import unity.gen.CLegc;
import unity.gen.Copterc;
import unity.gen.Decorationc;
import unity.gen.Monolithc;
import unity.gen.Tentaclec;
import unity.gen.TriJointLegsc;
import unity.gen.Worldc;
import unity.gen.Wormc;
import unity.graphics.UnityDrawf;
import unity.type.decal.UnitDecorationType;
import unity.v8.UnitDecal;

import static arc.graphics.g2d.Draw.z;

public class UnityUnitType extends UnitType {
    public final Seq<Weapon> segWeapSeq = new Seq<>();
    public TextureRegion segmentRegion;
    public TextureRegion tailRegion;
    public TextureRegion segmentCellRegion;
    public TextureRegion segmentOutline;
    public TextureRegion tailOutline;
    public TextureRegion legBackRegion;
    public TextureRegion legBaseBackRegion;
    public TextureRegion footBackRegion;
    public TextureRegion legMiddleRegion;
    public TextureRegion legShadowRegion;
    public TextureRegion legShadowBaseRegion;
    public TextureRegion payloadCellRegion;
    public TextureRegion[] abilityRegions = new TextureRegion[AbilityTextures.values().length];
    public Seq<Weapon> bottomWeapons = new Seq<>();
    public Engine engine;
    public float bulletWidth = 2.0F;
    public WormDecal wormDecal;
    public int segmentLength = 9;
    public int maxSegments = -1;
    public int segmentCast = 4;
    public float segmentOffset = 23.0F;
    public float headOffset = 0.0F;
    public float angleLimit = 30.0F;
    public float regenTime = -1.0F;
    public float healthDistribution = 0.1F;
    public float segmentDamageScl = 6.0F;
    public float anglePhysicsSmooth = 0.0F;
    public float jointStrength = 1.0F;
    public float barrageRange = 150.0F;
    public boolean counterDrag = false;
    public boolean preventDrifting = false;
    public boolean splittable = false;
    public boolean chainable = false;
    public Sound splitSound;
    public Sound chainSound;
    public Seq<Weapon>[] segmentWeapons;
    public Func<Unit, Trail> trailType;
    public Func<Unit, UnitType> toTrans;
    public Boolf<Unit> transPred;
    public float transformTime;
    public Seq<UnitDecorationType> decorations;
    public Seq<TentacleType> tentacles;
    public final Seq<Rotor> rotors;
    public float rotorDeathSlowdown;
    public float fallRotateSpeed;
    public FloatSeq weaponXs;
    protected static Vec2 legOffsetB = new Vec2();
    protected static float[][] jointOffsets = new float[2][2];
    public Seq<CLegType.ClegGroupType> legGroup;
    public boolean customBackLegs;
    public boolean legShadows;
    private static final Rect viewport = new Rect();
    private static final Rect viewport2 = new Rect();
    private static final int chunks = 4;
    public UnityUnitType linkType;
    public int linkCount;
    public float rotationSpeed;
    public int maxSouls;
    public AntiCheatVariables antiCheatType;
    protected boolean immuneAll;
    boolean wormCreating;
    public float laserRange;
    public int maxConnections;
    public int worldWidth;
    public int worldHeight;
    public boolean forceWreckRegion;

    public Seq<UnitDecal> decals = new Seq<>();

    public UnityUnitType(String name) {
        super(name);
        this.splitSound = Sounds.door;
        this.chainSound = Sounds.door;
        this.trailType = (unit) -> new Trail(this.trailLength);
        this.transPred = (unit) -> {
            Floor floor = unit.floorOn();
            return floor.isLiquid && !(floor instanceof ShallowLiquid) ^ unit instanceof WaterMovec;
        };
        this.decorations = new Seq<>();
        this.tentacles = new Seq<>();
        this.rotors = new Seq<>(4);
        this.rotorDeathSlowdown = 0.01F;
        this.fallRotateSpeed = 2.5F;
        this.weaponXs = new FloatSeq();
        this.legGroup = new Seq<>();
        this.customBackLegs = false;
        this.legShadows = false;
        this.linkCount = 1;
        this.rotationSpeed = 20.0F;
        this.maxSouls = 3;
        this.immuneAll = false;
        this.wormCreating = false;
        this.laserRange = -1.0F;
        this.maxConnections = -1;
        this.outlines = false;
    }

    public Unit create(Team team) {
        Unit unit = super.create(team);
        if (unit instanceof Monolithc e) {
            e.join();
        }

        if (!this.wormCreating && unit instanceof Wormc) {
            this.wormCreating = true;
            Unit cur = unit;
            int cid = unit.id;

            for(int i = 0; i < this.segmentLength; ++i) {
                Unit t = this.create(team);
                t.elevation = unit.elevation;
                ((Wormc)t).layer(1.0F + (float)i);
                ((Wormc)t).head(unit);
                ((Wormc)t).parent(cur);
                ((Wormc)cur).child(t);
                ((Wormc)cur).childId(cid);
                ((Wormc)cur).headId(unit.id);
                int idx = i >= this.segmentLength - 1 ? this.segmentWeapons.length - 1 : i % Math.max(1, this.segmentWeapons.length - 1);
                ((Wormc)t).weaponIdx((byte)idx);
                t.setupWeapons(this);
                cid = t.id;
                cur = t;
            }

            this.wormCreating = false;
        }

        return unit;
    }

    public void load() {
        super.load();
        this.rotors.each(Rotor::load);
        if (this.wormDecal != null) {
            this.wormDecal.load();
        }

        this.segmentRegion = Core.atlas.find(this.name + "-segment");
        this.segmentCellRegion = Core.atlas.find(this.name + "-segment-cell", this.cellRegion);
        this.tailRegion = Core.atlas.find(this.name + "-tail");
        this.segmentOutline = Core.atlas.find(this.name + "-segment-outline");
        this.tailOutline = Core.atlas.find(this.name + "-tail-outline");
        this.legBackRegion = Core.atlas.find(this.name + "-leg-back");
        this.legBaseBackRegion = Core.atlas.find(this.name + "-leg-base-back");
        this.footBackRegion = Core.atlas.find(this.name + "-foot-back");
        this.legMiddleRegion = Core.atlas.find(this.name + "-leg-middle", this.legRegion);
        this.legShadowRegion = Core.atlas.find(this.name + "-leg-shadow", this.legRegion);
        this.legShadowBaseRegion = Core.atlas.find(this.name + "-leg-base-shadow", this.legBaseRegion);
        this.payloadCellRegion = Core.atlas.find(this.name + "-cell-payload", this.cellRegion);

        for(AbilityTextures type : AbilityTextures.values()) {
            this.abilityRegions[type.ordinal()] = Core.atlas.find(this.name + "-" + type.name());
        }

        this.decorations.each(UnitDecorationType::load);
        if (this.segmentWeapons == null) {
            this.segWeapSeq.each(Weapon::load);
        } else {
            for(Seq<Weapon> seq : this.segmentWeapons) {
                seq.each(Weapon::load);
            }
        }

        this.tentacles.each(TentacleType::load);
        this.legGroup.each(CLegType.ClegGroupType::load);
    }

    public void init() {
        super.init();
        Seq<Rotor> mapped = new Seq<>();
        this.rotors.each((rotor) -> {
            mapped.add(rotor);
            if (rotor.mirror) {
                Rotor copy = rotor.copy();
                copy.x *= -1.0F;
                copy.speed *= -1.0F;
                copy.shadeSpeed *= -1.0F;
                copy.rotOffset += 360.0F / (float)(copy.bladeCount * 2);
                mapped.add(copy);
            }

        });
        TentacleType.set(this.tentacles);
        this.weapons.each((w) -> this.weaponXs.add(w.x));
        this.rotors.set(mapped);
        if (this.segmentWeapons == null) {
            this.sortSegWeapons(this.segWeapSeq);
            this.segmentWeapons = new Seq[]{this.segWeapSeq};
        } else {
            for(Seq<Weapon> seq : this.segmentWeapons) {
                this.sortSegWeapons(seq);
            }
        }

        Seq<Weapon> addBottoms = new Seq<>();

        for(Weapon w : this.weapons) {
            if (this.bottomWeapons.contains(w) && w.otherSide != -1) {
                addBottoms.add(this.weapons.get(w.otherSide));
            }
        }

        this.bottomWeapons.addAll(addBottoms.distinct());
        if (this.immuneAll) {
            this.immunities.addAll(Vars.content.getBy(ContentType.status));
        }

    }

    public void sortSegWeapons(Seq<Weapon> weaponSeq) {
        Seq<Weapon> mapped = new Seq<>();
        int i = 0;

        for(int len = weaponSeq.size; i < len; ++i) {
            Weapon w = weaponSeq.get(i);
            if (w.recoilTime < 0.0F) {
                w.recoilTime = w.reload;
            }

            mapped.add(w);
            if (w.mirror) {
                Weapon copy = w.copy();
                copy.x *= -1.0F;
                copy.shootX *= -1.0F;
                copy.flipSprite = !copy.flipSprite;
                mapped.add(copy);
                w.reload *= 2.0F;
                copy.reload *= 2.0F;
                w.recoilTime *= 2.0F;
                copy.recoilTime *= 2.0F;
                w.otherSide = mapped.size - 1;
                copy.otherSide = mapped.size - 2;
            }
        }

        weaponSeq.set(mapped);
    }

    public <T extends Unit & Wormc> void drawWorm(T unit) {
        Mechc mech = unit instanceof Mechc ? (Mechc)unit : null;
        float z = (unit.elevation > 0.5F ? (this.lowAltitude ? 90.0F : 115.0F) : this.groundLayer + Mathf.clamp(this.hitSize / 4000.0F, 0.0F, 0.01F)) - unit.layer() * 1.0E-5F;
        if (unit.isFlying()) {
            TextureRegion tmpShadow = this.shadowRegion;
            if (!unit.isHead() || unit.isTail()) {
                this.shadowRegion = unit.isTail() ? this.tailRegion : this.segmentRegion;
            }

            z(Math.min(80.0F, z - 1.0F));
            this.drawShadow(unit);
            this.shadowRegion = tmpShadow;
        }

        z(z - 0.02F);
        if (mech != null) {
            this.drawMech(mech);
            legOffsetB.trns(mech.baseRotation(), 0.0F, Mathf.lerp(Mathf.sin(mech.walkExtend(true), 0.63661975F, 1.0F) * this.mechSideSway, 0.0F, unit.elevation));
            legOffsetB.add(Tmp.v1.trns(mech.baseRotation() + 90.0F, 0.0F, Mathf.lerp(Mathf.sin(mech.walkExtend(true), 0.31830987F, 1.0F) * this.mechFrontSway, 0.0F, unit.elevation)));
            unit.trns(legOffsetB.x, legOffsetB.y);
        }

        if (unit instanceof Legsc) {
            this.drawLegs((Unit & Legsc) unit);
        }

        z(Math.min(z - 0.01F, 99.0F));
        if (unit instanceof Payloadc) {
            this.drawPayload((Unit & Payloadc) unit);
        }

        this.drawSoftShadow(unit);
        z(z);
        TextureRegion tmp = this.region;
        TextureRegion tmpOutline = this.outlineRegion;
        TextureRegion tmpCell = this.cellRegion;
        if (!unit.isHead() || unit.isTail()) {
            this.region = unit.isTail() ? this.tailRegion : this.segmentRegion;
            this.outlineRegion = unit.isTail() ? this.tailOutline : this.segmentOutline;
        }

        if (!unit.isHead()) {
            this.cellRegion = this.segmentCellRegion;
        }

        this.drawOutline(unit);
        this.drawWeaponOutlines(unit);
        if (unit.isTail() && unit.layer() < this.maxSegments) {
            Draw.draw(z, () -> {
                Tmp.v1.trns(unit.rotation + 180.0F, this.segmentOffset).add(unit);
                Drawf.construct(Tmp.v1.x, Tmp.v1.y, this.tailRegion, unit.rotation - 90.0F, unit.regenTime() / this.regenTime, 1.0F, unit.regenTime());
            });
        }

        this.drawBody(unit);
        if (this.drawCell && !unit.isTail()) {
            this.drawCell(unit);
        }

        if (this.wormDecal != null) {
            this.wormDecal.draw(unit, unit.parent());
        }

        this.cellRegion = tmpCell;
        this.region = tmp;
        this.outlineRegion = tmpOutline;
        this.drawWeapons(unit);
        if (unit.shieldAlpha > 0.0F && this.drawShields) {
            this.drawShield(unit);
        }

        if (mech != null) {
            unit.trns(-legOffsetB.x, -legOffsetB.y);
        }

        if (unit.abilities.length > 0) {
            for(Ability a : unit.abilities) {
                Draw.reset();
                a.draw(unit);
            }

            Draw.reset();
        }
    }

    public void draw(Unit unit) {
        if (unit instanceof Wormc w) {
            if (!w.isHead()) {
                this.drawWorm((Unit & Wormc) w);
            }
        }
        super.draw(unit);
        float z = z();

        if (this.decals.size > 0) {
            float base = unit.rotation - 90.0F;

            for(UnitDecal d : this.decals) {
                z(d.layer);
                Draw.scl(d.xScale, d.yScale);
                Draw.color(d.color);
                Draw.rect(d.region, unit.x + Angles.trnsx(base, d.x, d.y), unit.y + Angles.trnsy(base, d.x, d.y), base + d.rotation);
            }

            Draw.reset();
            Draw.z(z);
        }

        if (unit instanceof Copterc c) {
            this.drawRotors((Unit & Copterc) c);
        }
    }

    @Override
    public void drawEngines(Unit unit) {
        if (unit.isFlying()) {
            if (engine != null) {
                engine.draw(unit);
            } else {
                super.drawEngines(unit);
            }
        }
    }

    public Color cellColor(Unit unit) {
        if (unit instanceof Monolithc e) {
            if (e.disabled()) {
                return Tmp.c1.set(Color.black).lerp(unit.team.color, 0.1F);
            }
        }

        return super.cellColor(unit);
    }

    public void drawCell(Unit unit) {
        if (unit.isAdded()) {
            super.drawCell(unit);
        } else {
            this.applyColor(unit);
            Draw.color(this.cellColor(unit));
            Draw.rect(this.payloadCellRegion, unit.x, unit.y, unit.rotation - 90.0F);
            Draw.reset();
        }

    }

    public <T extends Unit & TriJointLegsc> void drawTriLegs(T unit) {
        this.applyColor(unit);
        TriJointLeg[] legs = unit.legs();
        float ssize = (float)this.footRegion.width * Draw.scl * 1.5F;
        float rotation = unit.baseRotation();

        for(TriJointLeg leg : legs) {
            Drawf.shadow(leg.joints[2].x, leg.joints[2].y, ssize);
        }

        for(int j = legs.length - 1; j >= 0; --j) {
            int i = j % 2 == 0 ? j / 2 : legs.length - 1 - j / 2;
            TriJointLeg leg = legs[i];
            float angle = unit.legAngle(rotation, i);
            boolean flip = (float)i >= (float)legs.length / 2.0F;
            int flips = Mathf.sign(flip);
            Vec2 position = legOffsetB.trns(angle, this.legBaseOffset).add(unit);

            for(int k = 0; k < 2; ++k) {
                Tmp.v1.set(leg.joints[1 + k]).sub(leg.joints[k]).inv().setLength(this.legExtension);
                jointOffsets[k][0] = Tmp.v1.x;
                jointOffsets[k][1] = Tmp.v1.y;
            }

            Draw.rect(this.footRegion, leg.joints[2].x, leg.joints[2].y, position.angleTo(leg.joints[2]));
            Lines.stroke((float)this.legRegion.height * Draw.scl * (float)flips);
            Lines.line(this.legRegion, position.x, position.y, leg.joints[0].x, leg.joints[0].y, false);

            for(int k = 0; k < 2; ++k) {
                TextureRegion region = k == 0 ? this.legMiddleRegion : this.legBaseRegion;
                Lines.stroke((float)region.height * Draw.scl * (float)flips);
                Lines.line(region, leg.joints[k].x + jointOffsets[k][0], leg.joints[k].y + jointOffsets[k][1], leg.joints[k + 1].x, leg.joints[k + 1].y, false);
            }

            if (this.baseJointRegion.found() || this.jointRegion.found()) {
                for(int k = -1; k < 2; ++k) {
                    Vec2 pos = k == -1 ? position : leg.joints[k];
                    TextureRegion region = k == -1 ? this.baseJointRegion : this.jointRegion;
                    if (region.found()) {
                        Draw.rect(region, pos.x, pos.y, k == -1 ? rotation : 0.0F);
                    }
                }
            }
        }

        if (this.baseRegion.found()) {
            Draw.rect(this.baseRegion, unit.x, unit.y, rotation);
        }

        Draw.reset();
    }

    public <T extends Unit & Legsc> void drawLegs(T unit) {
        if (!this.customBackLegs) {
            super.drawLegs(unit);
        } else {
            this.applyColor(unit);
            Leg[] legs = unit.legs();
            float ssize = (float)this.footRegion.width * Draw.scl * 1.5F;
            float rotation = unit.baseRotation();

            for(Leg leg : legs) {
                Drawf.shadow(leg.base.x, leg.base.y, ssize);
            }

            for(int j = legs.length - 1; j >= 0; --j) {
                int i = j % 2 == 0 ? j / 2 : legs.length - 1 - j / 2;
                Leg leg = legs[i];
                float angle = unit.legAngle(i);
                boolean flip = (float)i >= (float)legs.length / 2.0F;
                boolean back = j < legs.length - 2;
                int flips = Mathf.sign(flip);
                TextureRegion fr = back ? this.footRegion : this.footBackRegion;
                TextureRegion lr = back ? this.legRegion : this.legBackRegion;
                TextureRegion lbr = back ? this.legBaseRegion : this.legBaseBackRegion;
                Vec2 position = legOffsetB.trns(angle, this.legBaseOffset).add(unit);
                Tmp.v1.set(leg.base).sub(leg.joint).inv().setLength(this.legExtension);

                Draw.rect(fr, leg.base.x, leg.base.y, position.angleTo(leg.base));
                Lines.stroke((float)lr.height * Draw.scl * (float)flips);
                Lines.line(lr, position.x, position.y, leg.joint.x, leg.joint.y, false);
                Lines.stroke((float)lbr.height * Draw.scl * (float)flips);
                Lines.line(lbr, leg.joint.x + Tmp.v1.x, leg.joint.y + Tmp.v1.y, leg.base.x, leg.base.y, false);
                if (this.jointRegion.found()) {
                    Draw.rect(this.jointRegion, leg.joint.x, leg.joint.y);
                }

                if (this.baseJointRegion.found()) {
                    Draw.rect(this.baseJointRegion, position.x, position.y, rotation);
                }
            }

            if (this.baseRegion.found()) {
                Draw.rect(this.baseRegion, unit.x, unit.y, rotation - 90.0F);
            }

            Draw.reset();
        }

    }

    public void drawShadow(Unit unit) {
        super.drawShadow(unit);
        if (unit instanceof WormDefaultUnit wormUnit) {
            wormUnit.drawShadow();
        }
    }

    public void drawSoftShadow(Unit unit) {
        if (unit instanceof TriJointLegsc) {
            float oz = z();
            z(oz - 0.01F);
            this.drawTriLegs((Unit & TriJointLegsc)unit);
            z(oz);
        }

        if (unit instanceof CLegc) {
            for(CLegGroup group : ((CLegc)unit).legGroups()) {
                group.draw(unit);
            }
        }

        super.drawSoftShadow(unit);
        if (unit instanceof WormDefaultUnit wormUnit) {
            float var8 = z();

            for(int var9 = 0; var9 < wormUnit.segmentUnits.length; ++var9) {
                z(var8 - ((float)var9 + 1.1F) / 10000.0F);
                wormUnit.type.drawSoftShadow(wormUnit.segmentUnits[var9]);
            }

            z(var8);
        }
    }

    public void drawOutline(Unit unit) {
        if (unit instanceof Decorationc d) {

            for(UnitDecorationType.UnitDecoration decor : d.decors()) {
                if (!decor.type.top) {
                    decor.type.draw(unit, decor);
                }
            }
        }

        super.drawOutline(unit);
    }

    public void drawBody(Unit unit) {
        float z = z();
        if (unit instanceof Tentaclec t) {
            z(Math.min(z - 0.01F, 99.0F));
            t.drawTentacles();
            z(z);
        }

        super.drawBody(unit);
        if (unit instanceof Decorationc d) {

            for(UnitDecorationType.UnitDecoration decor : d.decors()) {
                if (decor.type.top) {
                    decor.type.draw(unit, decor);
                }
            }
        }

        if (unit instanceof WormDefaultUnit wormUnit) {
            Core.camera.bounds(viewport);
            int index = -4;

            for(int i = 0; i < wormUnit.segmentUnits.length; ++i) {
                if (i >= index + 4) {
                    index = i;
                    Unit seg = wormUnit.segmentUnits[i];
                    Unit segN = wormUnit.segmentUnits[Math.min(i + 4, wormUnit.segmentUnits.length - 1)];
                    float grow = wormUnit.regenAvailable() && i + 4 >= wormUnit.segmentUnits.length - 1 ? seg.clipSize() : 0.0F;
                    Tmp.r3.setCentered(segN.x, segN.y, segN.clipSize());
                    viewport2.setCentered(seg.x, seg.y, seg.clipSize()).merge(Tmp.r3).grow(grow + seg.clipSize() / 2.0F);
                }

                if (viewport.overlaps(viewport2)) {
                    z(z - ((float)i + 1.0F) / 10000.0F);
                    if (wormUnit.regenAvailable() && i == wormUnit.segmentUnits.length - 1) {
                        int finalI = i;
                        Draw.draw(z - ((float)i + 2.0F) / 10000.0F, () -> {
                            Tmp.v1.trns(wormUnit.segmentUnits[finalI].rotation + 180.0F, this.segmentOffset).add(wormUnit.segmentUnits[finalI]);
                            Drawf.construct(Tmp.v1.x, Tmp.v1.y, this.tailRegion, wormUnit.segmentUnits[finalI].rotation - 90.0F, wormUnit.repairTime / this.regenTime, 1.0F, wormUnit.repairTime);
                        });
                    }

                    wormUnit.segmentUnits[i].drawBody();
                    this.drawWeapons(wormUnit.segmentUnits[i]);
                }
            }
        }

        if (unit instanceof Worldc) {
            Draw.draw(z + 1.0E-4F, () -> {
                Seq<Building> build = ((Worldc)unit).buildings();
                World world = ((Worldc)unit).unitWorld();
                float cx = (float)(world.width() * 8) / 2.0F;
                float cy = (float)(world.height() * 8) / 2.0F;
                float r = unit.rotation() - 90.0F;
                Mat proj = Tmp.m1.set(Draw.proj());
                Vec2 cam = Core.camera.position;
                float camX = cam.x;
                float camY = cam.y;
                float cw = Core.camera.width / 2.0F;
                float ch = Core.camera.height / 2.0F;
                Tmp.v2.set(-cx, -cy).rotate(r);
                Tmp.v1.set(unit).sub(camX, camY).add(cw, ch).add(Tmp.v2);
                cam.set(cw - Tmp.v1.x, ch - Tmp.v1.y);
                Core.camera.update();
                Draw.flush();
                Batch old = Core.batch;
                Core.batch = UnityDrawf.altBatch;
                Draw.proj(Core.camera);
                Draw.proj().rotate(r);
                Draw.sort(true);

                for(int i = 0; i < build.size; ++i) {
                    Building b = build.get(i);
                    z(30.0F);
                    b.draw();
                }

                z(9999.0F);
                Draw.color(Color.clear);
                Fill.rect(0.0F, 0.0F, 0.0F, 0.0F);
                Draw.reset();
                Draw.flush();
                Draw.sort(false);
                cam.set(camX, camY);
                Core.camera.update();
                Draw.proj(proj);
                Core.batch = old;
            });
        }

        z(z);
    }

    public void drawWeapons(Unit unit) {
        float z = z();
        this.applyColor(unit);

        for(WeaponMount mount : unit.mounts) {
            Weapon weapon = mount.weapon;
            if (this.bottomWeapons.contains(weapon)) {
                z(z - 1.0E-4F);
            }

            weapon.draw(unit, mount);
            z(z);
        }

        Draw.reset();
    }

    public <T extends Unit & Copterc> void drawRotors(T unit) {
        this.applyColor(unit);
        RotorMount[] rotors = unit.rotors();

        for(RotorMount mount : rotors) {
            Rotor rotor = mount.rotor;
            float x = unit.x + Angles.trnsx(unit.rotation - 90.0F, rotor.x, rotor.y);
            float y = unit.y + Angles.trnsy(unit.rotation - 90.0F, rotor.x, rotor.y);
            float alpha = Mathf.curve(unit.rotorSpeedScl(), 0.2F, 1.0F);
            Draw.color(0.0F, 0.0F, 0.0F, rotor.shadowAlpha);
            float rad = 1.2F;
            float size = (float)Math.max(rotor.bladeRegion.width, rotor.bladeRegion.height) * Draw.scl;
            Draw.rect(this.softShadowRegion, x, y, size * rad * Draw.xscl, size * rad * Draw.yscl);
            Draw.color();
            Draw.alpha(alpha * rotor.ghostAlpha);
            Draw.rect(rotor.bladeGhostRegion, x, y, mount.rotorRot);
            Draw.rect(rotor.bladeShadeRegion, x, y, mount.rotorShadeRot);
            Draw.alpha(1.0F - alpha * rotor.bladeFade);

            for(int j = 0; j < rotor.bladeCount; ++j) {
                Draw.rect(rotor.bladeOutlineRegion, x, y, (unit.rotation + (float)unit.id * 24.0F + mount.rotorRot + 360.0F / (float)rotor.bladeCount * (float)j) % 360.0F);
            }
        }

        for(RotorMount mount : rotors) {
            Rotor rotor = mount.rotor;
            float x = unit.x + Angles.trnsx(unit.rotation - 90.0F, rotor.x, rotor.y);
            float y = unit.y + Angles.trnsy(unit.rotation - 90.0F, rotor.x, rotor.y);
            Draw.alpha(1.0F - Mathf.curve(unit.rotorSpeedScl(), 0.2F, 1.0F) * rotor.bladeFade);

            for(int j = 0; j < rotor.bladeCount; ++j) {
                Draw.rect(rotor.bladeRegion, x, y, (unit.rotation + (float)unit.id * 24.0F + mount.rotorRot + 360.0F / (float)rotor.bladeCount * (float)j) % 360.0F);
            }

            Draw.alpha(1.0F);
            Draw.rect(rotor.topRegion, x, y, unit.rotation - 90.0F);
        }

        Draw.reset();
    }
}
