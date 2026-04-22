package unity.content.units;

import arc.Core;
import arc.func.Prov;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureAtlas;
import arc.graphics.g2d.TextureRegion;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Position;
import arc.math.geom.Quat;
import arc.math.geom.Vec2;
import arc.math.geom.Vec3;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.Lightning;
import mindustry.entities.Units;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.bullet.LaserBoltBulletType;
import mindustry.entities.bullet.LaserBulletType;
import mindustry.entities.bullet.LightningBulletType;
import mindustry.entities.units.UnitDecal;
import mindustry.game.Team;
import mindustry.gen.Bullet;
import mindustry.gen.Healthc;
import mindustry.gen.Rotc;
import mindustry.gen.Sounds;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.graphics.Trail;
import mindustry.type.UnitType;
import mindustry.type.Weapon;
import mindustry.type.ammo.PowerAmmoType;
import mindustry.world.Tile;
import unity.ai.AssistantAI;
import unity.ai.MonolithSoulAI;
import unity.content.Trails;
import unity.content.UnityBullets;
import unity.content.UnityFx;
import unity.content.effects.ChargeFx;
import unity.content.effects.DeathFx;
import unity.content.effects.HitFx;
import unity.content.effects.LineFx;
import unity.content.effects.ParticleFx;
import unity.content.effects.ShootFx;
import unity.content.effects.TrailFx;
import unity.entities.abilities.LightningSpawnAbility;
import unity.entities.bullet.monolith.energy.JoiningBulletType;
import unity.entities.bullet.monolith.energy.RicochetBulletType;
import unity.entities.bullet.monolith.laser.HelixLaserBulletType;
import unity.gen.MonolithSoul;
import unity.gen.UnitySounds;
import unity.graphics.MultiTrail;
import unity.graphics.UnityDrawf;
import unity.graphics.UnityPal;
import unity.type.Engine;
import unity.type.UnityUnitType;
import unity.type.weapons.monolith.ChargeShotgunWeapon;
import unity.type.weapons.monolith.EnergyRingWeapon;
import unity.util.Utils;

public final class MonolithUnitTypes {
    public static UnitType monolithSoul;
    public static UnitType stele;
    public static UnitType pedestal;
    public static UnitType pilaster;
    public static UnitType pylon;
    public static UnitType monument;
    public static UnitType colossus;
    public static UnitType bastion;
    public static UnitType adsect;
    public static UnitType comitate;
    public static UnitType stray;
    public static UnitType tendence;
    public static UnitType liminality;
    public static UnitType calenture;
    public static UnitType hallucination;
    public static UnitType escapism;
    public static UnitType fantasy;

    private MonolithUnitTypes() {
        throw new AssertionError();
    }

    public static void load() {
        monolithSoul = new UnityUnitType("monolith-soul"){
            {
                this.defaultController = MonolithSoulAI::new;
                this.health = 300.0f;
                this.speed = 2.4f;
                this.rotateSpeed = 10.0f;
                this.accel = 0.2f;
                this.drag = 0.08f;
                this.flying = true;
                this.lowAltitude = true;
                this.fallSpeed = 1.0f;
                this.miningRange = 96.0f;
                this.maxRange = 96.0f;
                this.range = 96.0f;
                this.hitSize = 12.0f;
                this.omniMovement = false;
                this.engineColor = UnityPal.monolithLight;
                this.trailLength = 24;
                this.deathExplosionEffect = DeathFx.monolithSoulDeath;
                this.forceWreckRegion = true;
                this.trailType = unit -> new MultiTrail(MultiTrail.rot((Rotc)unit), new MultiTrail.TrailHold[]{new MultiTrail.TrailHold((Trail)Trails.soul((MultiTrail.RotationHandler)MultiTrail.rot((Rotc)unit), (int)50), this.engineColor), new MultiTrail.TrailHold((Trail)Trails.soul((MultiTrail.RotationHandler)MultiTrail.rot((Rotc)unit), (int)64), -4.8f, 6.0f, 0.56f, this.engineColor), new MultiTrail.TrailHold((Trail)Trails.soul((MultiTrail.RotationHandler)MultiTrail.rot((Rotc)unit), (int)64), 4.8f, 6.0f, 0.56f, this.engineColor)});
            }

            public void update(Unit unit) {
                Unit unit2 = unit;
                if (unit2 instanceof MonolithSoul) {
                    MultiTrail copy;
                    MonolithSoul soul = (MonolithSoul)unit2;
                    Trail trail = soul.trail;
                    if (!(trail instanceof MultiTrail)) {
                        return;
                    }
                    MultiTrail trail2 = (MultiTrail)trail;
                    float width = (this.engineSize + Mathf.absin((float)Time.time, (float)2.0f, (float)(this.engineSize / 4.0f)) * soul.elevation) * this.trailScl;
                    if (trail2.trails.length == 3 && soul.corporeal()) {
                        copy = trail2.copy();
                        copy.rotation = MultiTrail::calcRot;
                        TrailFx.trailFadeLow.at(soul.x, soul.y, width, this.engineColor, (Object)copy);
                        soul.trail = new MultiTrail(new MultiTrail.TrailHold[]{new MultiTrail.TrailHold((Trail)Trails.soul((MultiTrail.RotationHandler)MultiTrail.rot((Rotc)unit), (int)this.trailLength), this.engineColor)});
                    } else if (trail2.trails.length == 1 && !soul.corporeal()) {
                        copy = trail2.copy();
                        copy.rotation = MultiTrail::calcRot;
                        TrailFx.trailFadeLow.at(soul.x, soul.y, width, this.engineColor, (Object)copy);
                        soul.trail = (Trail)this.trailType.get((Object)soul);
                    }
                    if (!soul.corporeal()) {
                        if (Mathf.chance((double)Time.delta)) {
                            ParticleFx.monolithSoul.at(soul.x, soul.y, Time.time, (Object)new Vec2(soul.vel).scl(-0.3f));
                        }
                        if (soul.forming()) {
                            for (Tile form : soul.forms()) {
                                if (Mathf.chanceDelta((double)0.17f)) {
                                    ParticleFx.monolithSpark.at(form.drawx(), form.drawy(), 4.0f);
                                }
                                if (!Mathf.chanceDelta((double)0.67f)) continue;
                                LineFx.monolithSoulAbsorb.at(form.drawx(), form.drawy(), 0.0f, (Object)soul);
                            }
                        } else if (soul.joining() && Mathf.chanceDelta((double)0.33f)) {
                            LineFx.monolithSoulAbsorb.at(soul.x + Mathf.range((float)6.0f), soul.y + Mathf.range((float)6.0f), 0.0f, (Object)soul.joinTarget());
                        }
                    }
                }
                super.update(unit);
            }

            @Override
            public void draw(Unit unit) {
                Unit unit2 = unit;
                if (!(unit2 instanceof MonolithSoul)) {
                    return;
                }
                MonolithSoul soul = (MonolithSoul)unit2;
                if (!soul.corporeal()) {
                    int i;
                    float z = Draw.z();
                    Draw.z((float)90.0f);
                    float trailSize = (this.engineSize + Mathf.absin((float)Time.time, (float)2.0f, (float)(this.engineSize / 4.0f)) * soul.elevation) * this.trailScl;
                    soul.trail.drawCap(this.engineColor, trailSize);
                    soul.trail.draw(this.engineColor, trailSize);
                    Draw.z((float)109.99f);
                    Draw.blend((Blending)Blending.additive);
                    Draw.color((Color)UnityPal.monolith);
                    Fill.circle((float)soul.x, (float)soul.y, (float)6.0f);
                    Draw.color((Color)UnityPal.monolithDark);
                    Draw.rect((TextureRegion)this.softShadowRegion, (float)soul.x, (float)soul.y, (float)10.0f, (float)10.0f);
                    Draw.blend();
                    Lines.stroke((float)1.0f, (Color)UnityPal.monolithDark);
                    float rotation = Time.time * 3.0f * (float)Mathf.sign((unit.id % 2 == 0 ? 1 : 0) != 0);
                    for (i = 0; i < 5; ++i) {
                        float r = rotation + 72.0f * (float)i;
                        UnityDrawf.arcLine((float)soul.x, (float)soul.y, (float)10.0f, (float)60.0f, (float)r);
                        Tmp.v1.trns(r, 10.0f).add((Position)soul);
                        Drawf.tri((float)Tmp.v1.x, (float)Tmp.v1.y, (float)2.5f, (float)6.0f, (float)r);
                    }
                    Draw.z((float)115.0f);
                    Draw.reset();
                    for (i = 0; i < this.wreckRegions.length; ++i) {
                        float off = 360.0f / (float)this.wreckRegions.length * (float)i;
                        float fin = soul.formProgress();
                        float fout = 1.0f - fin;
                        Tmp.v1.trns(soul.rotation + off, fout * 24.0f).add(Tmp.v2.trns((Time.time + off) * 4.0f, fout * 3.0f)).add((Position)soul);
                        Draw.alpha((float)fin);
                        Draw.rect((TextureRegion)this.wreckRegions[i], (float)Tmp.v1.x, (float)Tmp.v1.y, (float)(soul.rotation - 90.0f));
                    }
                    Lines.stroke((float)1.5f, (Color)UnityPal.monolith);
                    TextureAtlas.AtlasRegion reg = Core.atlas.find("unity-monolith-chain");
                    Quat rot = Utils.q1.set(Vec3.Z, soul.ringRotation() + 90.0f).mul(Utils.q2.set(Vec3.X, 75.0f));
                    float t = Interp.pow3Out.apply(soul.joinTime());
                    float w = (float)reg.width * Draw.scl * 0.5f * t;
                    float h = (float)reg.height * Draw.scl * 0.5f * t;
                    float rad = t * 25.0f;
                    float a = Mathf.curve((float)t, (float)0.33f);
                    Draw.alpha((float)a);
                    UnityDrawf.panningCircle((TextureRegion)reg, (float)soul.x, (float)soul.y, (float)w, (float)h, (float)rad, (float)360.0f, (float)(Time.time * 6.0f * (float)Mathf.sign((soul.id % 2 == 0 ? 1 : 0) != 0) + (float)soul.id * 30.0f), (Quat)rot, (float)89.99f, (float)115.0f);
                    Draw.color((Color)Color.black, (Color)UnityPal.monolithDark, (float)0.67f);
                    Draw.alpha((float)a);
                    Draw.blend((Blending)Blending.additive);
                    UnityDrawf.panningCircle((TextureRegion)Core.atlas.find("unity-line-shade"), (float)soul.x, (float)soul.y, (float)(w + 6.0f), (float)(h + 6.0f), (float)rad, (float)360.0f, (float)0.0f, (Quat)rot, (boolean)true, (float)89.99f, (float)115.0f);
                    Draw.blend();
                    Draw.z((float)z);
                } else {
                    super.draw(soul);
                }
            }
        };
        stele = new UnityUnitType("stele"){
            {
                this.health = 300.0f;
                this.speed = 0.6f;
                this.hitSize = 8.0f;
                this.armor = 5.0f;
                this.canBoost = true;
                this.boostMultiplier = 2.5f;
                this.outlineColor = UnityPal.darkOutline;
                this.weapons.add((Object)new Weapon(this.name + "-shotgun"){
                    {
                        super(name);
                        this.layerOffset = -0.01f;
                        this.top = false;
                        this.x = 5.25f;
                        this.y = -0.25f;
                        this.shootY = 5.0f;
                        this.reload = 60.0f;
                        this.recoil = 2.5f;
                        this.inaccuracy = 0.5f;
                        this.shootSound = Sounds.shootBig;
                        this.bullet = new JoiningBulletType(3.5f, 36.0f){
                            {
                                super(speed, damage);
                                this.lifetime = 48.0f;
                                this.radius = 10.0f;
                                this.weaveScale = 5.0f;
                                this.weaveMag = 2.0f;
                                this.homingPower = 0.07f;
                                this.homingRange = this.range() * 2.0f;
                                this.sensitivity = 0.5f;
                                this.trailInterval = 3.0f;
                                this.trailColor = UnityPal.monolithGreen;
                                this.hitEffect = this.despawnEffect = HitFx.soulConcentrateHit;
                                this.shootEffect = ShootFx.soulConcentrateShoot;
                                this.smokeEffect = Fx.lightningShoot;
                            }
                        };
                    }
                });
            }
        };
        pedestal = new UnityUnitType("pedestal"){
            {
                this.health = 1200.0f;
                this.speed = 0.5f;
                this.rotateSpeed = 2.6f;
                this.hitSize = 11.0f;
                this.armor = 10.0f;
                this.singleTarget = true;
                this.maxSouls = 4;
                this.canBoost = true;
                this.boostMultiplier = 2.5f;
                this.engineSize = 3.5f;
                this.engineOffset = 6.0f;
                this.outlineColor = UnityPal.darkOutline;
                this.weapons.add((Object)new Weapon(this.name + "-gun"){
                    {
                        super(name);
                        this.top = false;
                        this.x = 10.75f;
                        this.y = 2.25f;
                        this.reload = 40.0f;
                        this.recoil = 3.2f;
                        this.shootSound = UnitySounds.energyBolt;
                        LightningBulletType subBullet = new LightningBulletType();
                        subBullet.damage = 24.0f;
                        this.bullet = new RicochetBulletType(3.0f, 72.0f, "shell", (BulletType)subBullet){
                            final /* synthetic */ BulletType val$subBullet;
                            {
                                this.val$subBullet = bulletType;
                                super(speed, damage, spriteName);
                                this.width = 16.0f;
                                this.height = 20.0f;
                                this.lifetime = 36.0f;
                                this.frontColor = UnityPal.monolithLight;
                                this.backColor = UnityPal.monolith.cpy().mul(0.75f);
                                this.trailColor = UnityPal.monolithDark.cpy().mul(0.5f);
                                this.trailChance = 0.25f;
                                this.trailEffect = UnityFx.ricochetTrailBig;
                                this.shootEffect = Fx.hitLaserBlast;
                                this.smokeEffect = Fx.lightningShoot;
                                this.hitEffect = this.despawnEffect = HitFx.monolithHitBig;
                            }

                            @Override
                            public void init(Bullet b) {
                                super.init(b);
                                for (int i = 0; i < 3; ++i) {
                                    this.val$subBullet.create(b, b.x, b.y, b.rotation());
                                    Sounds.spark.at(b.x, b.y, Mathf.random((float)0.6f, (float)0.8f));
                                }
                            }
                        };
                    }
                }, (Object)new ChargeShotgunWeapon(""){
                    {
                        super(name);
                        this.mirror = false;
                        this.rotate = true;
                        this.rotateSpeed = 8.0f;
                        this.x = 0.0f;
                        this.y = 4.0f;
                        this.shootX = 0.0f;
                        this.shootY = 24.0f;
                        this.shots = 5;
                        this.shotDelay = 3.0f;
                        this.reload = 72.0f;
                        this.addSequenceTime = 25.0f;
                        this.shootCone = 90.0f;
                        this.addEffect = ShootFx.pedestalShootAdd;
                        this.addedEffect = HitFx.monolithHitBig;
                        this.shootSound = UnitySounds.chainyShot;
                        this.bullet = new BasicBulletType(6.0f, 32.0f, "unity-twisting-shell"){
                            {
                                super(speed, damage, bulletSprite);
                                this.width = 12.0f;
                                this.height = 16.0f;
                                this.shrinkY = 0.0f;
                                this.lifetime = 36.0f;
                                this.homingPower = 0.07f;
                                this.homingRange = this.range() * 2.0f;
                                this.frontColor = UnityPal.monolith;
                                this.backColor = UnityPal.monolithDark;
                                this.trailChance = 0.25f;
                                this.trailEffect = UnityFx.ricochetTrailBig;
                                this.shootEffect = Fx.hitLaserBlast;
                                this.smokeEffect = Fx.lightningShoot;
                                this.hitEffect = this.despawnEffect = HitFx.monolithHitBig;
                            }
                        };
                    }

                    @Override
                    public void drawCharge(float x, float y, float rotation, float shootAngle, Unit unit, ChargeShotgunWeapon.ChargeShotgunMount mount) {
                        BulletType bulletType = this.bullet;
                        if (bulletType instanceof BasicBulletType) {
                            BasicBulletType b = (BasicBulletType)bulletType;
                            float z = Draw.z();
                            Draw.z((float)100.0f);
                            Draw.color((Color)b.backColor);
                            Draw.rect((TextureRegion)b.backRegion, (float)x, (float)y, (float)b.width, (float)b.height, (float)(shootAngle - 90.0f));
                            Draw.color((Color)b.frontColor);
                            Draw.rect((TextureRegion)b.frontRegion, (float)x, (float)y, (float)b.width, (float)b.height, (float)(shootAngle - 90.0f));
                            Draw.z((float)z);
                        }
                    }
                });
            }
        };
        pilaster = new UnityUnitType("pilaster"){
            {
                this.health = 2000.0f;
                this.speed = 0.4f;
                this.rotateSpeed = 2.2f;
                this.hitSize = 26.5f;
                this.armor = 15.0f;
                this.mechFrontSway = 0.55f;
                this.maxSouls = 5;
                this.canBoost = true;
                this.boostMultiplier = 2.5f;
                this.engineSize = 5.0f;
                this.engineOffset = 10.0f;
                this.ammoType = new PowerAmmoType(1000.0f);
                this.outlineColor = UnityPal.darkOutline;
                this.weapons.add((Object)new Weapon("unity-monolith-medium-weapon-mount"){
                    {
                        super(name);
                        this.top = false;
                        this.x = 4.0f;
                        this.y = 7.5f;
                        this.shootY = 6.0f;
                        this.rotate = true;
                        this.recoil = 3.0f;
                        this.reload = 40.0f;
                        this.shootSound = Sounds.laser;
                        this.bullet = new LaserBulletType(160.0f){
                            {
                                super(damage);
                                this.lifetime = 27.0f;
                                this.width = 20.0f;
                                this.sideAngle = 60.0f;
                                this.smokeEffect = ShootFx.phantasmalLaserShoot;
                            }
                        };
                    }
                }, (Object)new Weapon("unity-monolith-large-weapon-mount"){
                    {
                        super(name);
                        this.top = false;
                        this.x = 13.0f;
                        this.y = 2.0f;
                        this.shootY = 10.5f;
                        this.rotate = true;
                        this.rotateSpeed = 10.0f;
                        this.recoil = 2.5f;
                        this.reload = 120.0f;
                        this.shootSound = UnitySounds.chainyShot;
                        this.bullet = new BasicBulletType(2.7f, 32.0f, "unity-twisting-shell"){
                            {
                                super(speed, damage, bulletSprite);
                                this.width = 16.0f;
                                this.height = 20.0f;
                                this.shrinkY = 0.0f;
                                this.lifetime = 54.0f;
                                this.scaleVelocity = true;
                                this.frontColor = UnityPal.monolith;
                                this.backColor = UnityPal.monolithDark;
                                this.trailColor = UnityPal.monolithLight;
                                this.trailLength = 32;
                                this.trailWidth = 1.0f;
                                this.trailChance = 0.33f;
                                this.shootEffect = Fx.hitLaserBlast;
                                this.smokeEffect = ShootFx.tendenceShoot;
                                this.hitEffect = this.despawnEffect = HitFx.monolithHitBig;
                                this.fragBullets = 1;
                                this.fragVelocityMax = 0.0f;
                                this.fragVelocityMin = 0.0f;
                                this.fragBullet = new BulletType(0.0f, 16.0f){
                                    private final Seq<Healthc> all;
                                    {
                                        super(speed, damage);
                                        this.all = new Seq();
                                        this.lifetime = 96.0f;
                                        this.collides = false;
                                        this.hittable = false;
                                        this.absorbable = false;
                                        this.keepVelocity = false;
                                        this.hitSound = Sounds.spark;
                                        this.hitEffect = this.despawnEffect = Fx.none;
                                    }

                                    float frac(Bullet b) {
                                        return Interp.pow5Out.apply(Mathf.curve((float)b.fin(), (float)0.0f, (float)0.1f)) * Interp.pow3Out.apply(1.0f - Mathf.curve((float)b.fin(), (float)0.8f, (float)1.0f));
                                    }

                                    float radius(Bullet b) {
                                        float s = this.frac(b);
                                        return 26.0f * s + Mathf.absin((float)6.0f, (float)3.0f) * s;
                                    }

                                    public void update(Bullet b) {
                                        this.updateTrail(b);
                                        float r = this.radius(b);
                                        if (Mathf.chanceDelta((double)0.17f)) {
                                            Tmp.v1.trns(Mathf.random((float)360.0f), Mathf.random((float)r)).add((Position)b);
                                            ParticleFx.monolithSpark.at(Tmp.v1.x, Tmp.v1.y, 0.0f);
                                        }
                                        if (Mathf.chanceDelta((double)0.33f)) {
                                            ParticleFx.lightningPivot.at(b.x, b.y, UnityPal.monolith);
                                        }
                                        if (b.timer(0, 60.0f)) {
                                            UnityFx.monolithRingEffect.at(b.x, b.y, 0.0f, (Object)Float.valueOf(1.0f));
                                        }
                                        if (b.timer(1, 16.0f)) {
                                            this.all.clear();
                                            Units.nearbyEnemies((Team)b.team, (float)b.x, (float)b.y, (float)96.0f, arg_0 -> this.all.add(arg_0));
                                            Units.nearbyBuildings((float)b.x, (float)b.y, (float)96.0f, e -> {
                                                if (e.isValid() && e.team != b.team) {
                                                    this.all.add(e);
                                                }
                                            });
                                            this.all.sort(arg_0 -> ((Bullet)b).dst2(arg_0));
                                            int len = Math.min(this.all.size, 3);
                                            for (int i = 0; i < len; ++i) {
                                                Healthc target = (Healthc)this.all.get(i);
                                                target.damage(this.damage);
                                                Fx.chainLightning.at(b.x, b.y, 0.0f, UnityPal.monolithLight, (Object)target);
                                                Fx.hitLancer.at((Position)target);
                                            }
                                            if (len > 0) {
                                                this.hitSound.at((Position)b);
                                            }
                                        }
                                    }

                                    public void draw(Bullet b) {
                                        float r = this.radius(b);
                                        Fill.light((float)b.x, (float)b.y, (int)Lines.circleVertices((float)r), (float)r, (Color)Tmp.c1.set(UnityPal.monolithDark).a(0.0f), (Color)Tmp.c2.set(UnityPal.monolith).a(0.8f));
                                        Lines.stroke((float)2.0f, (Color)UnityPal.monolithLight);
                                        Lines.circle((float)b.x, (float)b.y, (float)r);
                                        float ir = r / 4.0f;
                                        Draw.color((Color)Tmp.c1.set(Pal.lancerLaser).a(0.4f));
                                        UnityDrawf.shiningCircle((int)b.id, (float)(Time.time * 0.67f), (float)b.x, (float)b.y, (float)ir, (int)4, (float)16.0f, (float)30.0f, (float)(ir * 2.0f), (float)90.0f);
                                        Draw.color((Color)Tmp.c1.set(Pal.lancerLaser));
                                        UnityDrawf.shiningCircle((int)b.id, (float)(Time.time * 0.67f), (float)b.x, (float)b.y, (float)(ir *= 0.5f), (int)4, (float)16.0f, (float)30.0f, (float)(ir * 2.0f), (float)90.0f);
                                        Draw.color();
                                        UnityDrawf.shiningCircle((int)b.id, (float)(Time.time * 0.67f), (float)b.x, (float)b.y, (float)(ir *= 0.5f), (int)4, (float)16.0f, (float)30.0f, (float)(ir * 2.0f), (float)90.0f);
                                        Draw.reset();
                                    }
                                };
                            }

                            public void updateTrail(final Bullet b) {
                                if (!Vars.headless && this.trailLength > 0 && b.trail == null) {
                                    b.trail = new MultiTrail(new MultiTrail.TrailHold[]{new MultiTrail.TrailHold((Trail)Trails.phantasmal((int)this.trailLength)), new MultiTrail.TrailHold((Trail)Trails.phantasmal((int)this.trailLength)), new MultiTrail.TrailHold((Trail)Trails.phantasmal((int)this.trailLength))}){
                                        boolean dead;
                                        float time;
                                        {
                                            super(trails);
                                            this.time = Time.time;
                                            trailChance = 0.25f;
                                            trailColor = UnityPal.monolithLight;
                                        }

                                        public void update(float x, float y, float width) {
                                            if (!this.dead) {
                                                this.time += Time.delta * 10.0f * (float)(Mathf.randomSeed((long)b.id, (int)0, (int)1) * 2 - 1);
                                                if (!b.isAdded()) {
                                                    this.dead = true;
                                                }
                                            }
                                            for (int i = 0; i < this.trails.length; ++i) {
                                                MultiTrail.TrailHold trail = this.trails[i];
                                                Tmp.v1.trns((float)b.id * 56.0f + Time.time * 4.0f + 360.0f / (float)this.trails.length * (float)i, 8.0f).add(x, y);
                                                trail.trail.update(Tmp.v1.x, Tmp.v1.y, width * trail.width);
                                                if (!(trailChance > 0.0f) || !Mathf.chanceDelta((double)trailChance)) continue;
                                                trailEffect.at(Tmp.v1.x, Tmp.v1.y, trail.width * trailWidth, trailColor);
                                            }
                                            this.lastX = x;
                                            this.lastY = y;
                                        }

                                        public void drawCap(Color color, float width) {
                                        }

                                        public void draw(Color color, float width) {
                                            for (MultiTrail.TrailHold trail : this.trails) {
                                                Trail t = trail.trail;
                                                Color col = trail.color == null ? color : trail.color;
                                                float w = width * trail.width;
                                                t.drawCap(col, w);
                                                t.draw(col, w);
                                            }
                                        }
                                    };
                                }
                                super.updateTrail(b);
                            }

                            public void removed(Bullet b) {
                                super.removed(b);
                                b.trail = null;
                            }
                        };
                    }
                });
            }
        };
        pylon = new UnityUnitType("pylon"){
            {
                this.health = 14400.0f;
                this.speed = 0.43f;
                this.rotateSpeed = 1.48f;
                this.hitSize = 36.0f;
                this.armor = 23.0f;
                this.commandLimit = 8;
                this.maxSouls = 7;
                this.hovering = true;
                this.allowLegStep = true;
                this.visualElevation = 0.2f;
                this.legCount = 4;
                this.legExtension = 8.0f;
                this.legSpeed = 0.08f;
                this.legLength = 16.0f;
                this.legMoveSpace = 1.2f;
                this.legTrns = 0.5f;
                this.legBaseOffset = 11.0f;
                this.ammoType = new PowerAmmoType(2000.0f);
                this.groundLayer = 75.0f;
                this.outlineColor = UnityPal.darkOutline;
                this.weapons.add((Object)new Weapon(this.name + "-laser"){
                    {
                        super(name);
                        this.soundPitchMin = 1.0f;
                        this.top = false;
                        this.mirror = false;
                        this.shake = 15.0f;
                        this.shootY = 11.0f;
                        this.y = 0.0f;
                        this.x = 0.0f;
                        this.reload = 280.0f;
                        this.recoil = 0.0f;
                        this.cooldownTime = 280.0f;
                        this.shootStatusDuration = 108.0f;
                        this.shootStatus = StatusEffects.unmoving;
                        this.shootSound = Sounds.laserblast;
                        this.chargeSound = Sounds.lasercharge;
                        this.firstShotDelay = UnityFx.pylonLaserCharge.lifetime / 2.0f;
                        this.bullet = UnityBullets.pylonLaser;
                    }
                }, (Object)new Weapon("unity-monolith-large2-weapon-mount"){
                    {
                        super(name);
                        this.x = 14.0f;
                        this.y = 5.0f;
                        this.shootY = 14.0f;
                        this.rotate = true;
                        this.rotateSpeed = 3.5f;
                        this.shootSound = Sounds.laser;
                        this.shake = 5.0f;
                        this.reload = 20.0f;
                        this.recoil = 4.0f;
                        this.bullet = UnityBullets.pylonLaserSmall;
                    }
                });
            }
        };
        monument = new UnityUnitType("monument"){
            {
                this.health = 32000.0f;
                this.speed = 0.42f;
                this.rotateSpeed = 1.4f;
                this.hitSize = 48.0f;
                this.armor = 32.0f;
                this.commandLimit = 8;
                this.maxSouls = 9;
                this.visualElevation = 0.3f;
                this.hovering = true;
                this.allowLegStep = true;
                this.legCount = 6;
                this.legLength = 30.0f;
                this.legExtension = 8.0f;
                this.legSpeed = 0.1f;
                this.legTrns = 0.5f;
                this.legBaseOffset = 15.0f;
                this.legMoveSpace = 1.2f;
                this.legPairOffset = 3.0f;
                this.legSplashDamage = 64.0f;
                this.legSplashRange = 48.0f;
                this.ammoType = new PowerAmmoType(2000.0f);
                this.groundLayer = 75.0f;
                this.outlineColor = UnityPal.darkOutline;
                LaserBulletType laser = new LaserBulletType(640.0f);
                this.weapons.add((Object)new Weapon("unity-monolith-large2-weapon-mount", (BulletType)laser){
                    final /* synthetic */ BulletType val$laser;
                    {
                        this.val$laser = bulletType;
                        super(name);
                        this.top = false;
                        this.x = 14.0f;
                        this.y = 12.0f;
                        this.shootY = 14.0f;
                        this.rotate = true;
                        this.rotateSpeed = 3.5f;
                        this.reload = 36.0f;
                        this.shake = 5.0f;
                        this.recoil = 5.0f;
                        this.shootSound = Sounds.laser;
                        this.bullet = this.val$laser;
                    }
                }, (Object)new Weapon("unity-monolith-large2-weapon-mount", (BulletType)laser){
                    final /* synthetic */ BulletType val$laser;
                    {
                        this.val$laser = bulletType;
                        super(name);
                        this.top = false;
                        this.x = 20.0f;
                        this.y = 3.0f;
                        this.shootY = 14.0f;
                        this.rotate = true;
                        this.rotateSpeed = 3.5f;
                        this.reload = 48.0f;
                        this.shake = 5.0f;
                        this.recoil = 5.0f;
                        this.shootSound = Sounds.laser;
                        this.bullet = this.val$laser;
                    }
                }, (Object)new Weapon("unity-monolith-railgun-big"){
                    {
                        super(name);
                        this.mirror = false;
                        this.x = 0.0f;
                        this.y = -12.0f;
                        this.shootY = 35.0f;
                        this.shadow = 30.0f;
                        this.reload = 200.0f;
                        this.shake = 8.0f;
                        this.recoil = 8.0f;
                        this.shootCone = 2.0f;
                        this.cooldownTime = 210.0f;
                        this.shootSound = Sounds.railgun;
                        this.bullet = UnityBullets.monumentRailBullet;
                    }
                });
            }
        };
        colossus = new UnityUnitType("colossus"){
            {
                this.health = 60000.0f;
                this.speed = 0.4f;
                this.rotateSpeed = 1.2f;
                this.hitSize = 64.0f;
                this.armor = 45.0f;
                this.commandLimit = 8;
                this.maxSouls = 12;
                this.visualElevation = 0.5f;
                this.hovering = true;
                this.allowLegStep = true;
                this.legCount = 6;
                this.legLength = 48.0f;
                this.legExtension = 12.0f;
                this.legSpeed = 0.1f;
                this.legTrns = 0.5f;
                this.legBaseOffset = 15.0f;
                this.legMoveSpace = 0.82f;
                this.legPairOffset = 3.0f;
                this.legSplashDamage = 84.0f;
                this.legSplashRange = 48.0f;
                this.ammoType = new PowerAmmoType(2000.0f);
                this.groundLayer = 75.0f;
                this.outlineColor = UnityPal.darkOutline;
                this.abilities.add((Object)new LightningSpawnAbility(8, 32.0f, 2.0f, 0.05f, 180.0f, 56.0f, 200.0f));
                this.weapons.add((Object)new Weapon(this.name + "-weapon"){
                    {
                        super(name);
                        this.top = false;
                        this.x = 30.0f;
                        this.y = 7.75f;
                        this.shootY = 20.0f;
                        this.reload = 144.0f;
                        this.recoil = 8.0f;
                        this.spacing = 1.0f;
                        this.inaccuracy = 6.0f;
                        this.shots = 5;
                        this.shotDelay = 3.0f;
                        this.shootSound = Sounds.laserblast;
                        this.bullet = new LaserBulletType(1920.0f){
                            {
                                super(damage);
                                this.width = 45.0f;
                                this.length = 400.0f;
                                this.lifetime = 32.0f;
                                this.lightningSpacing = 35.0f;
                                this.lightningLength = 4;
                                this.lightningDelay = 1.5f;
                                this.lightningLengthRand = 6;
                                this.lightningDamage = 48.0f;
                                this.lightningAngleRand = 30.0f;
                                this.lightningColor = Pal.lancerLaser;
                            }
                        };
                    }
                });
            }
        };
        bastion = new UnityUnitType("bastion"){
            {
                this.health = 120000.0f;
                this.speed = 0.4f;
                this.rotateSpeed = 1.2f;
                this.hitSize = 67.0f;
                this.armor = 100.0f;
                this.commandLimit = 8;
                this.maxSouls = 15;
                this.visualElevation = 0.7f;
                this.hovering = true;
                this.allowLegStep = true;
                this.legCount = 6;
                this.legLength = 72.0f;
                this.legExtension = 16.0f;
                this.legSpeed = 0.12f;
                this.legTrns = 0.6f;
                this.legBaseOffset = 18.0f;
                this.legMoveSpace = 0.6f;
                this.legPairOffset = 3.0f;
                this.legSplashDamage = 140.0f;
                this.legSplashRange = 56.0f;
                this.ammoType = new PowerAmmoType(2000.0f);
                this.groundLayer = 75.0f;
                this.outlineColor = UnityPal.darkOutline;
                this.abilities.add((Object)new LightningSpawnAbility(12, 16.0f, 3.0f, 0.05f, 300.0f, 96.0f, 640.0f));
                RicochetBulletType energy = new RicochetBulletType(6.0f, 50.0f, "shell"){
                    {
                        super(speed, damage, spriteName);
                        this.width = 9.0f;
                        this.height = 11.0f;
                        this.shrinkY = 0.3f;
                        this.lifetime = 45.0f;
                        this.weaveMag = 3.0f;
                        this.weaveScale = 3.0f;
                        this.trailChance = 0.3f;
                        this.frontColor = UnityPal.monolithLight;
                        this.backColor = UnityPal.monolith;
                        this.trailColor = UnityPal.monolithDark;
                        this.shootEffect = Fx.lancerLaserShoot;
                        this.smokeEffect = Fx.hitLancer;
                        this.hitEffect = this.despawnEffect = HitFx.monolithHitSmall;
                        this.splashDamage = 60.0f;
                        this.splashDamageRadius = 10.0f;
                        this.lightning = 3;
                        this.lightningDamage = 12.0f;
                        this.lightningColor = Pal.lancerLaser;
                        this.lightningLength = 6;
                    }
                };
                this.weapons.add((Object)new Weapon(this.name + "-mount", (BulletType)energy){
                    final /* synthetic */ BulletType val$energy;
                    {
                        this.val$energy = bulletType;
                        super(name);
                        this.x = 9.0f;
                        this.y = -11.5f;
                        this.shootY = 10.0f;
                        this.rotate = true;
                        this.rotateSpeed = 8.0f;
                        this.reload = 24.0f;
                        this.recoil = 6.0f;
                        this.shots = 8;
                        this.velocityRnd = 0.3f;
                        this.spacing = 5.0f;
                        this.shootSound = UnitySounds.energyBolt;
                        this.bullet = this.val$energy;
                    }
                }, (Object)new Weapon(this.name + "-mount", (BulletType)energy){
                    final /* synthetic */ BulletType val$energy;
                    {
                        this.val$energy = bulletType;
                        super(name);
                        this.x = 23.5f;
                        this.y = 5.5f;
                        this.shootY = 10.0f;
                        this.rotate = true;
                        this.rotateSpeed = 8.0f;
                        this.reload = 15.0f;
                        this.recoil = 6.0f;
                        this.shots = 5;
                        this.velocityRnd = 0.3f;
                        this.spacing = 6.0f;
                        this.shootSound = UnitySounds.energyBolt;
                        this.bullet = this.val$energy;
                    }
                }, (Object)new Weapon(this.name + "-gun"){
                    {
                        super(name);
                        this.x = 12.5f;
                        this.y = 12.0f;
                        this.shootY = 13.5f;
                        this.rotate = true;
                        this.rotateSpeed = 6.0f;
                        this.shots = 8;
                        this.shotDelay = 3.0f;
                        this.reload = 30.0f;
                        this.recoil = 8.0f;
                        this.shootSound = Sounds.shootBig;
                        this.bullet = new RicochetBulletType(12.5f, 640.0f, "shell"){
                            {
                                super(speed, damage, spriteName);
                                this.width = 20.0f;
                                this.height = 25.0f;
                                this.shrinkY = 0.2f;
                                this.lifetime = 30.0f;
                                this.trailLength = 3;
                                this.pierceCap = 6;
                                this.frontColor = Color.white;
                                this.backColor = UnityPal.monolithLight;
                                this.trailColor = UnityPal.monolith;
                                this.shootEffect = Fx.lancerLaserShoot;
                                this.smokeEffect = Fx.hitLancer;
                                this.hitEffect = this.despawnEffect = HitFx.monolithHitBig;
                                this.lightning = 3;
                                this.lightningDamage = 12.0f;
                                this.lightningColor = Pal.lancerLaser;
                                this.lightningLength = 15;
                            }

                            @Override
                            public void update(Bullet b) {
                                super.update(b);
                                if (Mathf.chanceDelta((double)0.3f)) {
                                    Lightning.create((Bullet)b, (Color)this.lightningColor, (float)this.lightningDamage, (float)b.x, (float)b.y, (float)b.rotation(), (int)(this.lightningLength / 2));
                                }
                            }
                        };
                    }
                });
            }
        };
        adsect = new UnityUnitType("adsect"){
            {
                this.defaultController = AssistantAI.create((AssistantAI.Assistance[])new AssistantAI.Assistance[]{AssistantAI.Assistance.mendCore, AssistantAI.Assistance.mine, AssistantAI.Assistance.build});
                this.health = 180.0f;
                this.speed = 4.0f;
                this.accel = 0.4f;
                this.drag = 0.2f;
                this.rotateSpeed = 15.0f;
                this.flying = true;
                this.mineTier = 2;
                this.mineSpeed = 3.0f;
                this.buildSpeed = 0.8f;
                this.circleTarget = false;
                this.ammoType = new PowerAmmoType(500.0f);
                this.engineColor = UnityPal.monolith;
                this.outlineColor = UnityPal.darkOutline;
                this.weapons.add((Object)new Weapon(){
                    {
                        this.mirror = false;
                        this.rotate = false;
                        this.x = 0.0f;
                        this.y = 4.0f;
                        this.reload = 6.0f;
                        this.shootCone = 40.0f;
                        this.shootSound = Sounds.lasershoot;
                        this.bullet = new LaserBoltBulletType(4.0f, 23.0f){
                            {
                                super(speed, damage);
                                this.healPercent = 1.5f;
                                this.lifetime = 40.0f;
                                this.collidesTeam = true;
                                this.frontColor = UnityPal.monolithLight;
                                this.backColor = UnityPal.monolith;
                                this.hitEffect = this.despawnEffect = HitFx.hitMonolithLaser;
                                this.smokeEffect = this.despawnEffect;
                            }
                        };
                    }
                });
            }
        };
        comitate = new UnityUnitType("comitate"){
            {
                this.defaultController = AssistantAI.create((AssistantAI.Assistance[])new AssistantAI.Assistance[]{AssistantAI.Assistance.mendCore, AssistantAI.Assistance.mine, AssistantAI.Assistance.build, AssistantAI.Assistance.heal});
                this.health = 420.0f;
                this.speed = 4.5f;
                this.accel = 0.5f;
                this.drag = 0.15f;
                this.rotateSpeed = 15.0f;
                this.flying = true;
                this.mineTier = 3;
                this.mineSpeed = 5.0f;
                this.buildSpeed = 1.3f;
                this.circleTarget = false;
                this.ammoType = new PowerAmmoType(500.0f);
                this.engineColor = UnityPal.monolith;
                this.outlineColor = UnityPal.darkOutline;
                this.weapons.add((Object)new Weapon(){
                    {
                        this.mirror = false;
                        this.rotate = false;
                        this.x = 0.0f;
                        this.y = 6.0f;
                        this.reload = 12.0f;
                        this.shootCone = 40.0f;
                        this.shootSound = UnitySounds.energyBolt;
                        this.bullet = new LaserBoltBulletType(6.5f, 60.0f){
                            {
                                super(speed, damage);
                                this.width = 4.0f;
                                this.height = 12.0f;
                                this.keepVelocity = false;
                                this.healPercent = 3.5f;
                                this.lifetime = 35.0f;
                                this.collidesTeam = true;
                                this.frontColor = UnityPal.monolithLight;
                                this.backColor = UnityPal.monolith;
                                this.hitEffect = this.despawnEffect = HitFx.hitMonolithLaser;
                                this.smokeEffect = this.despawnEffect;
                            }
                        };
                    }
                }, (Object)new Weapon("unity-monolith-small-weapon-mount"){
                    {
                        super(name);
                        this.top = false;
                        this.alternate = true;
                        this.mirror = true;
                        this.x = 3.0f;
                        this.y = 3.0f;
                        this.reload = 40.0f;
                        this.shots = 2;
                        this.shotDelay = 5.0f;
                        this.shootCone = 20.0f;
                        this.shootSound = Sounds.lasershoot;
                        this.bullet = new LaserBoltBulletType(4.0f, 30.0f){
                            {
                                super(speed, damage);
                                this.healPercent = 1.5f;
                                this.lifetime = 40.0f;
                                this.collidesTeam = true;
                                this.frontColor = UnityPal.monolithLight;
                                this.backColor = UnityPal.monolith;
                                this.hitEffect = this.despawnEffect = HitFx.hitMonolithLaser;
                                this.smokeEffect = this.despawnEffect;
                            }
                        };
                    }
                });
            }
        };
        stray = new UnityUnitType("stray"){
            {
                this.health = 300.0f;
                this.speed = 5.0f;
                this.accel = 0.08f;
                this.drag = 0.045f;
                this.rotateSpeed = 8.0f;
                this.flying = true;
                this.hitSize = 12.0f;
                this.lowAltitude = true;
                this.rotateShooting = false;
                this.outlineColor = UnityPal.darkOutline;
                static interface 1EngineType {
                public Engine get(float var1, float var2);
            }
                1EngineType etype = (s, offsetY) -> new Engine(){
                {
                    this.color = UnityPal.monolithLight;
                    this.offset = 11.0f - offsetY;
                    this.size = s;
                }
            };
                this.engine = new Engine.MultiEngine(new Engine.MultiEngine.EngineHold[]{new Engine.MultiEngine.EngineHold(etype.get(2.5f, 0.0f), 0.0f), new Engine.MultiEngine.EngineHold(etype.get(1.5f, 2.5f), -4.5f), new Engine.MultiEngine.EngineHold(etype.get(1.5f, 2.5f), 4.5f)}){
                    {
                        super(engines);
                        this.color = UnityPal.monolithLight;
                        this.size = 2.5f;
                        this.offset = 11.0f;
                    }
                }.apply(this);
                this.trailType = unit -> new MultiTrail(MultiTrail.rot((Rotc)unit), new MultiTrail.TrailHold[]{new MultiTrail.TrailHold((Trail)Trails.phantasmal((MultiTrail.RotationHandler)MultiTrail.rot((Rotc)unit), (int)16, (float)3.6f, (float)6.0f, (float)2.0f), this.engineColor), new MultiTrail.TrailHold((Trail)Utils.with((Object)Trails.singlePhantasmal((int)24), t -> {
                    t.trailChance = 0.0f;
                    t.fadeInterp = e -> (1.0f - Interp.pow10In.apply(e)) * Interp.pow2In.apply(e);
                    t.sideFadeInterp = e -> (1.0f - Interp.pow5In.apply(e)) * Interp.pow3In.apply(e);
                }), -4.5f, 2.5f, 0.44f, UnityPal.monolithLight), new MultiTrail.TrailHold((Trail)Utils.with((Object)Trails.singlePhantasmal((int)24), t -> {
                    t.trailChance = 0.0f;
                    t.fadeInterp = e -> (1.0f - Interp.pow10In.apply(e)) * Interp.pow2In.apply(e);
                    t.sideFadeInterp = e -> (1.0f - Interp.pow5In.apply(e)) * Interp.pow3In.apply(e);
                }), 4.5f, 2.5f, 0.44f, UnityPal.monolithLight)});
                this.trailLength = 24;
                this.weapons.add((Object)new EnergyRingWeapon(){
                    {
                        this.rings.add((Object)new EnergyRingWeapon.Ring(){
                            {
                                this.radius = 5.5f;
                                this.thickness = 1.0f;
                                this.spikes = 4;
                                this.spikeOffset = 1.5f;
                                this.spikeWidth = 2.0f;
                                this.spikeLength = 4.0f;
                                this.color = UnityPal.monolithDark.cpy().lerp(UnityPal.monolith, 0.5f);
                            }
                        }, (Object)new EnergyRingWeapon.Ring(){
                            {
                                this.radius = 2.5f;
                                shootY = 2.5f;
                                this.rotate = false;
                                this.thickness = 1.0f;
                                this.divisions = 2;
                                this.divisionSeparation = 30.0f;
                                this.angleOffset = 90.0f;
                                this.color = UnityPal.monolith;
                            }
                        });
                        this.y = 0.0f;
                        this.x = 0.0f;
                        this.mirror = false;
                        this.rotate = true;
                        this.reload = 60.0f;
                        this.shots = 6;
                        this.shotDelay = 1.0f;
                        this.inaccuracy = 30.0f;
                        this.layerOffset = 10.0f;
                        this.eyeRadius = 1.8f;
                        this.shootSound = UnitySounds.energyBolt;
                        this.bullet = new BasicBulletType(1.0f, 6.0f, "shell"){
                            {
                                super(speed, damage, bulletSprite);
                                this.drag = -0.08f;
                                this.lifetime = 35.0f;
                                this.width = 8.0f;
                                this.height = 13.0f;
                                this.homingDelay = 6.0f;
                                this.homingPower = 0.09f;
                                this.homingRange = 160.0f;
                                this.weaveMag = 6.0f;
                                this.keepVelocity = false;
                                this.frontColor = this.trailColor = UnityPal.monolith;
                                this.backColor = UnityPal.monolithDark;
                                this.trailChance = 0.3f;
                                this.trailParam = 1.5f;
                                this.trailWidth = 2.0f;
                                this.trailLength = 12;
                                this.shootEffect = Fx.lightningShoot;
                                this.hitEffect = this.despawnEffect = Fx.hitLancer;
                            }

                            public void updateTrail(Bullet b) {
                                if (!Vars.headless && this.trailLength > 0 && b.trail == null) {
                                    b.trail = Trails.singlePhantasmal((int)this.trailLength);
                                }
                                super.updateTrail(b);
                            }

                            public void removed(Bullet b) {
                                super.removed(b);
                                b.trail = null;
                            }
                        };
                    }
                });
            }
        };
        tendence = new UnityUnitType("tendence"){
            {
                this.health = 1200.0f;
                this.speed = 4.2f;
                this.accel = 0.08f;
                this.drag = 0.045f;
                this.rotateSpeed = 7.2f;
                this.flying = true;
                this.hitSize = 16.0f;
                this.lowAltitude = true;
                this.rotateShooting = false;
                this.maxSouls = 4;
                this.outlineColor = UnityPal.darkOutline;
                Prov etype = () -> new Engine(){
                    {
                        this.offset = 10.0f;
                        this.size = 2.5f;
                        this.color = UnityPal.monolith;
                    }
                };
                this.engine = new Engine.MultiEngine(new Engine.MultiEngine.EngineHold[]{new Engine.MultiEngine.EngineHold((Engine)etype.get(), -5.0f), new Engine.MultiEngine.EngineHold((Engine)etype.get(), 5.0f)}){
                    {
                        super(engines);
                        this.offset = 10.0f;
                        this.size = 2.5f;
                        this.color = UnityPal.monolith;
                    }
                }.apply(this);
                this.trailType = unit -> new MultiTrail(MultiTrail.rot((Rotc)unit), new MultiTrail.TrailHold[]{new MultiTrail.TrailHold((Trail)Trails.soul((MultiTrail.RotationHandler)MultiTrail.rot((Rotc)unit), (int)24), -5.0f, 0.0f, 1.0f, UnityPal.monolithLight), new MultiTrail.TrailHold((Trail)Trails.soul((MultiTrail.RotationHandler)MultiTrail.rot((Rotc)unit), (int)24), 5.0f, 0.0f, 1.0f, UnityPal.monolithLight)});
                this.trailLength = 24;
                this.decals.add((Object)new UnitDecal(this.name + "-top", 0.0f, 0.0f, 0.0f, 109.98f, Color.white));
                this.weapons.add((Object)new EnergyRingWeapon(){
                    {
                        this.rings.add((Object)new EnergyRingWeapon.Ring(){
                            {
                                this.radius = 6.5f;
                                this.thickness = 1.0f;
                                this.spikes = 8;
                                this.spikeOffset = 1.5f;
                                this.spikeWidth = 2.0f;
                                this.spikeLength = 5.0f;
                                this.color = UnityPal.monolithDark.cpy().lerp(UnityPal.monolith, 0.5f);
                            }
                        }, (Object)new EnergyRingWeapon.Ring(){
                            {
                                this.radius = 3.0f;
                                shootY = 3.0f;
                                this.rotate = false;
                                this.thickness = 1.0f;
                                this.divisions = 2;
                                this.divisionSeparation = 30.0f;
                                this.angleOffset = 90.0f;
                                this.color = UnityPal.monolith;
                            }
                        });
                        this.x = 0.0f;
                        this.y = 1.0f;
                        this.mirror = false;
                        this.rotate = true;
                        this.reload = 72.0f;
                        this.firstShotDelay = 35.0f;
                        this.inaccuracy = 15.0f;
                        this.layerOffset = 10.0f;
                        this.eyeRadius = 1.8f;
                        this.parentizeEffects = true;
                        this.chargeSound = UnitySounds.energyCharge;
                        this.shootSound = UnitySounds.energyBlast;
                        this.bullet = new BasicBulletType(4.8f, 72.0f, "shell"){
                            {
                                super(speed, damage, bulletSprite);
                                this.lifetime = 48.0f;
                                this.width = 16.0f;
                                this.height = 25.0f;
                                this.keepVelocity = false;
                                this.homingPower = 0.03f;
                                this.homingRange = this.range() * 2.0f;
                                this.lightning = 3;
                                this.lightningColor = UnityPal.monolithLight;
                                this.lightningDamage = 12.0f;
                                this.lightningLength = 12;
                                this.frontColor = this.trailColor = UnityPal.monolith;
                                this.backColor = UnityPal.monolithDark;
                                this.trailEffect = ParticleFx.monolithSpark;
                                this.trailChance = 0.4f;
                                this.trailParam = 6.0f;
                                this.trailWidth = 5.0f;
                                this.trailLength = 32;
                                this.hitEffect = this.despawnEffect = HitFx.tendenceHit;
                                this.chargeShootEffect = ShootFx.tendenceShoot;
                                this.shootEffect = ChargeFx.tendenceCharge;
                            }

                            public void draw(Bullet b) {
                                super.draw(b);
                                long seed = Mathf.rand.getState(0);
                                TextureAtlas.AtlasRegion reg = Core.atlas.white();
                                TextureAtlas.AtlasRegion light = Core.atlas.find("unity-line-shade");
                                Lines.stroke((float)2.0f);
                                for (int i = 0; i < 2; ++i) {
                                    Mathf.rand.setSeed((long)b.id);
                                    Tmp.v31.set(1.0f, 0.0f, 0.0f).setToRandomDirection();
                                    float r = (float)b.id * 20.0f + Time.time * 6.0f * (float)Mathf.sign((b.id % 2 == 0 ? 1 : 0) != 0);
                                    Utils.q1.set(i == 0 ? Vec3.X : Vec3.Y, r).mul(Utils.q2.set(Tmp.v31, r * (float)Mathf.signs[i]));
                                    Draw.color((Color)(i == 0 ? UnityPal.monolith : UnityPal.monolithDark));
                                    UnityDrawf.panningCircle((TextureRegion)reg, (float)b.x, (float)b.y, (float)1.0f, (float)1.0f, (float)(10.0f + (float)i * 4.0f), (float)360.0f, (float)0.0f, (Quat)Utils.q1, (boolean)true, (float)89.99f, (float)115.0f);
                                    Draw.color((Color)Color.black, (Color)UnityPal.monolithDark, (float)(i == 0 ? 0.5f : 0.25f));
                                    Draw.blend((Blending)Blending.additive);
                                    UnityDrawf.panningCircle((TextureRegion)light, (float)b.x, (float)b.y, (float)5.0f, (float)5.0f, (float)(10.0f + (float)i * 4.0f), (float)360.0f, (float)0.0f, (Quat)Utils.q1, (boolean)true, (float)89.99f, (float)115.0f);
                                    Draw.blend();
                                }
                                Draw.reset();
                                Mathf.rand.setSeed(seed);
                            }

                            public void updateTrail(Bullet b) {
                                if (!Vars.headless && this.trailLength > 0 && b.trail == null) {
                                    b.trail = Trails.soul((int)this.trailLength, (float)6.0f, (float)(this.trailWidth - 0.3f));
                                    for (int i = 0; i < b.trail.length; ++i) {
                                        b.trail.update(b.x, b.y, 0.0f);
                                    }
                                }
                                super.updateTrail(b);
                            }

                            public void removed(Bullet b) {
                                super.removed(b);
                                b.trail = null;
                            }
                        };
                    }
                });
            }
        };
        liminality = new UnityUnitType("liminality"){
            {
                this.health = 2000.0f;
                this.rotateShooting = false;
                this.lowAltitude = true;
                this.flying = true;
                this.strafePenalty = 0.1f;
                this.hitSize = 36.0f;
                this.speed = 3.5f;
                this.rotateSpeed = 3.6f;
                this.drag = 0.06f;
                this.accel = 0.08f;
                this.outlineColor = UnityPal.darkOutline;
                this.ammoType = new PowerAmmoType(2000.0f);
                Prov etype = () -> new Engine(){
                    {
                        this.offset = 16.25f;
                        this.size = 3.0f;
                        this.color = UnityPal.monolith;
                    }
                };
                this.engine = new Engine.MultiEngine(new Engine.MultiEngine.EngineHold[]{new Engine.MultiEngine.EngineHold(new Engine(){
                    {
                        this.offset = 22.25f;
                        this.size = 4.0f;
                        this.color = UnityPal.monolithLight;
                    }
                }, 0.0f), new Engine.MultiEngine.EngineHold((Engine)etype.get(), -17.75f), new Engine.MultiEngine.EngineHold((Engine)etype.get(), 17.75f)}){
                    {
                        super(engines);
                        this.offset = 21.25f;
                        this.size = 4.0f;
                        this.color = UnityPal.monolithLight;
                    }
                }.apply(this);
                this.trailType = unit -> new MultiTrail(MultiTrail.rot((Rotc)unit), new MultiTrail.TrailHold[]{new MultiTrail.TrailHold((Trail)Trails.phantasmal((MultiTrail.RotationHandler)MultiTrail.rot((Rotc)unit), (int)32, (float)5.6f, (float)8.0f, (float)0.0f), this.engineColor), new MultiTrail.TrailHold((Trail)Trails.soul((MultiTrail.RotationHandler)MultiTrail.rot((Rotc)unit), (int)48, (float)6.0f, (float)3.2f), -17.75f, 6.0f, 0.75f, this.engineColor), new MultiTrail.TrailHold((Trail)Trails.soul((MultiTrail.RotationHandler)MultiTrail.rot((Rotc)unit), (int)48, (float)6.0f, (float)3.2f), 17.75f, 6.0f, 0.75f, this.engineColor)});
                this.trailLength = 48;
                this.decals.add((Object)new UnitDecal(this.name + "-top", 0.0f, 0.0f, 0.0f, 109.98f, Color.white));
                this.weapons.add((Object)new EnergyRingWeapon(){
                    {
                        this.rings.add((Object)new EnergyRingWeapon.Ring(){
                            {
                                this.radius = 9.0f;
                                this.thickness = 1.0f;
                                this.spikes = 6;
                                this.spikeOffset = 1.5f;
                                this.spikeWidth = 2.0f;
                                this.spikeLength = 4.0f;
                                this.color = UnityPal.monolithDark.cpy().lerp(UnityPal.monolith, 0.5f);
                            }
                        }, (Object)new EnergyRingWeapon.Ring(){
                            {
                                this.radius = 5.6f;
                                shootY = 5.6f;
                                this.rotate = false;
                                this.thickness = 1.0f;
                                this.divisions = 2;
                                this.divisionSeparation = 30.0f;
                                this.angleOffset = 90.0f;
                                this.color = UnityPal.monolith;
                            }
                        }, (Object)new EnergyRingWeapon.Ring(){
                            {
                                this.radius = 1.5f;
                                this.thickness = 1.0f;
                                this.spikes = 4;
                                this.spikeOffset = 1.5f;
                                this.spikeWidth = 2.0f;
                                this.spikeLength = 2.0f;
                                this.flip = true;
                                this.color = UnityPal.monolithDark;
                            }
                        });
                        this.x = 0.0f;
                        this.y = 5.0f;
                        this.mirror = false;
                        this.rotate = true;
                        this.reload = 72.0f;
                        this.layerOffset = 10.0f;
                        this.eyeRadius = 2.0f;
                        this.shootSound = Sounds.laser;
                        this.bullet = new HelixLaserBulletType(240.0f){
                            {
                                super(damage);
                                this.sideWidth = 1.4f;
                                this.sideAngle = 30.0f;
                            }
                        };
                    }
                });
            }
        };
        calenture = new UnityUnitType("calenture"){};
        hallucination = new UnityUnitType("hallucination"){};
        escapism = new UnityUnitType("escapism"){};
        fantasy = new UnityUnitType("fantasy"){};
    }
}