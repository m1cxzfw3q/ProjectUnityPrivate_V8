package unity.content.units;

import arc.Core;
import arc.func.Prov;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureAtlas;
import arc.math.Interp;
import arc.math.Mathf;
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
import mindustry.entities.pattern.*;
import unity.v8.UnitDecal;
import mindustry.gen.Bullet;
import mindustry.gen.Healthc;
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
    public static UnitType
            monolithSoul, stele, pedestal, pilaster, pylon, monument, colossus, bastion, adsect, comitate, stray, tendence,
            liminality, calenture, hallucination, escapism, fantasy;

    public static void load() {
        monolithSoul = new UnityUnitType("monolith-soul"){
            {
                aiController = MonolithSoulAI::new;
                health = 300.0f;
                speed = 2.4f;
                rotateSpeed = 10.0f;
                accel = 0.2f;
                drag = 0.08f;
                flying = true;
                lowAltitude = true;
                fallSpeed = 1.0f;
                mineRange = 96.0f;
                maxRange = 96.0f;
                range = 96.0f;
                hitSize = 12.0f;
                omniMovement = false;
                engineColor = UnityPal.monolithLight;
                trailLength = 24;
                deathExplosionEffect = DeathFx.monolithSoulDeath;
                forceWreckRegion = true;
                trailType = unit -> new MultiTrail(MultiTrail.rot(unit), new MultiTrail.TrailHold(Trails.soul(MultiTrail.rot(unit), 50), engineColor), new MultiTrail.TrailHold(Trails.soul(MultiTrail.rot(unit), 64), -4.8f, 6.0f, 0.56f, engineColor), new MultiTrail.TrailHold(Trails.soul(MultiTrail.rot(unit), 64), 4.8f, 6.0f, 0.56f, engineColor));
            }

            public void update(Unit unit) {
                if (unit instanceof MonolithSoul soul) {
                    MultiTrail copy;
                    Trail trail = soul.trail;
                    if (!(trail instanceof MultiTrail trail2)) {
                        return;
                    }
                    float width = (engineSize + Mathf.absin(Time.time, 2.0f, engineSize / 4.0f) * soul.elevation) * trailScl;
                    if (trail2.trails.length == 3 && soul.corporeal()) {
                        copy = trail2.copy();
                        copy.rotation = MultiTrail::calcRot;
                        TrailFx.trailFadeLow.at(soul.x, soul.y, width, engineColor, copy);
                        soul.trail = new MultiTrail(new MultiTrail.TrailHold(Trails.soul(MultiTrail.rot(unit), trailLength), engineColor));
                    } else if (trail2.trails.length == 1 && !soul.corporeal()) {
                        copy = trail2.copy();
                        copy.rotation = MultiTrail::calcRot;
                        TrailFx.trailFadeLow.at(soul.x, soul.y, width, engineColor, copy);
                        soul.trail = trailType.get(soul);
                    }
                    if (!soul.corporeal()) {
                        if (Mathf.chance(Time.delta)) {
                            ParticleFx.monolithSoul.at(soul.x, soul.y, Time.time, new Vec2(soul.vel).scl(-0.3f));
                        }
                        if (soul.forming()) {
                            for (Tile form : soul.forms()) {
                                if (Mathf.chanceDelta(0.17f)) {
                                    ParticleFx.monolithSpark.at(form.drawx(), form.drawy(), 4.0f);
                                }
                                if (!Mathf.chanceDelta(0.67f)) continue;
                                LineFx.monolithSoulAbsorb.at(form.drawx(), form.drawy(), 0.0f, soul);
                            }
                        } else if (soul.joining() && Mathf.chanceDelta(0.33f)) {
                            LineFx.monolithSoulAbsorb.at(soul.x + Mathf.range(6f), soul.y + Mathf.range(6f), 0.0f, soul.joinTarget());
                        }
                    }
                }
                super.update(unit);
            }

            @Override
            public void draw(Unit unit) {
                if (!(unit instanceof MonolithSoul soul)) {
                    return;
                }
                if (!soul.corporeal()) {
                    int i;
                    float z = Draw.z();
                    Draw.z(90.0f);
                    float trailSize = (engineSize + Mathf.absin(Time.time, 2.0f, engineSize / 4.0f) * soul.elevation) * trailScl;
                    soul.trail.drawCap(engineColor, trailSize);
                    soul.trail.draw(engineColor, trailSize);
                    Draw.z(109.99f);
                    Draw.blend(Blending.additive);
                    Draw.color(UnityPal.monolith);
                    Fill.circle(soul.x, soul.y, 6.0f);
                    Draw.color(UnityPal.monolithDark);
                    Draw.rect(softShadowRegion, soul.x, soul.y, 10.0f, 10.0f);
                    Draw.blend();
                    Lines.stroke(1.0f, UnityPal.monolithDark);
                    float rotation = Time.time * 3.0f * Mathf.sign((unit.id % 2 == 0 ? 1 : 0) != 0);
                    for (i = 0; i < 5; ++i) {
                        float r = rotation + 72.0f * i;
                        UnityDrawf.arcLine(soul.x, soul.y, 10.0f, 60.0f, r);
                        Tmp.v1.trns(r, 10.0f).add(soul);
                        Drawf.tri(Tmp.v1.x, Tmp.v1.y, 2.5f, 6.0f, r);
                    }
                    Draw.z(115.0f);
                    Draw.reset();
                    for (i = 0; i < wreckRegions.length; ++i) {
                        float off = 360.0f / wreckRegions.length * i;
                        float fin = soul.formProgress();
                        float fout = 1.0f - fin;
                        Tmp.v1.trns(soul.rotation + off, fout * 24.0f).add(Tmp.v2.trns((Time.time + off) * 4.0f, fout * 3.0f)).add(soul);
                        Draw.alpha(fin);
                        Draw.rect(wreckRegions[i], Tmp.v1.x, Tmp.v1.y, soul.rotation - 90.0f);
                    }
                    Lines.stroke(1.5f, UnityPal.monolith);
                    TextureAtlas.AtlasRegion reg = Core.atlas.find("unity-monolith-chain");
                    Quat rot = Utils.q1.set(Vec3.Z, soul.ringRotation() + 90.0f).mul(Utils.q2.set(Vec3.X, 75.0f));
                    float t = Interp.pow3Out.apply(soul.joinTime());
                    float w = reg.width * Draw.scl * 0.5f * t;
                    float h = reg.height * Draw.scl * 0.5f * t;
                    float rad = t * 25.0f;
                    float a = Mathf.curve(t, 0.33f);
                    Draw.alpha(a);
                    UnityDrawf.panningCircle(reg, soul.x, soul.y, w, h, rad, 360.0f, Time.time * 6.0f * Mathf.sign((soul.id % 2 == 0 ? 1 : 0) != 0) + soul.id * 30.0f, rot, 89.99f, 115.0f);
                    Draw.color(Color.black, UnityPal.monolithDark, 0.67f);
                    Draw.alpha(a);
                    Draw.blend(Blending.additive);
                    UnityDrawf.panningCircle(Core.atlas.find("unity-line-shade"), soul.x, soul.y, w + 6.0f, h + 6.0f, rad, 360.0f, 0.0f, rot, true, 89.99f, 115.0f);
                    Draw.blend();
                    Draw.z(z);
                } else {
                    super.draw(soul);
                }
            }
        };
        stele = new UnityUnitType("stele"){
            {
                health = 300.0f;
                speed = 0.6f;
                hitSize = 8.0f;
                armor = 5.0f;
                canBoost = true;
                boostMultiplier = 2.5f;
                outlineColor = UnityPal.darkOutline;
                weapons.add(new Weapon(name + "-shotgun"){
                    {
                        layerOffset = -0.01f;
                        top = false;
                        x = 5.25f;
                        y = -0.25f;
                        shootY = 5.0f;
                        reload = 60.0f;
                        recoil = 2.5f;
                        inaccuracy = 0.5f;
                        shootSound = Sounds.shootSpectre;
                        bullet = new JoiningBulletType(3.5f, 36.0f){
                            {
                                lifetime = 48.0f;
                                radius = 10.0f;
                                weaveScale = 5.0f;
                                weaveMag = 2.0f;
                                homingPower = 0.07f;
                                homingRange = range * 2.0f;
                                sensitivity = 0.5f;
                                trailInterval = 3.0f;
                                trailColor = UnityPal.monolithGreen;
                                hitEffect = despawnEffect = HitFx.soulConcentrateHit;
                                shootEffect = ShootFx.soulConcentrateShoot;
                                smokeEffect = Fx.lightningShoot;
                            }
                        };
                    }
                });
            }
        };
        pedestal = new UnityUnitType("pedestal"){
            {
                health = 1200.0f;
                speed = 0.5f;
                rotateSpeed = 2.6f;
                hitSize = 11.0f;
                armor = 10.0f;
                singleTarget = true;
                maxSouls = 4;
                canBoost = true;
                boostMultiplier = 2.5f;
                engineSize = 3.5f;
                engineOffset = 6.0f;
                outlineColor = UnityPal.darkOutline;
                weapons.add(new Weapon(name + "-gun"){
                    {
                        top = false;
                        x = 10.75f;
                        y = 2.25f;
                        reload = 40.0f;
                        recoil = 3.2f;
                        shootSound = UnitySounds.energyBolt;
                        LightningBulletType subBullet = new LightningBulletType();
                        subBullet.damage = 24.0f;
                        bullet = new RicochetBulletType(3.0f, 72.0f, "shell"){
                            {
                                width = 16.0f;
                                height = 20.0f;
                                lifetime = 36.0f;
                                frontColor = UnityPal.monolithLight;
                                backColor = UnityPal.monolith.cpy().mul(0.75f);
                                trailColor = UnityPal.monolithDark.cpy().mul(0.5f);
                                trailChance = 0.25f;
                                trailEffect = UnityFx.ricochetTrailBig;
                                shootEffect = Fx.hitLaserBlast;
                                smokeEffect = Fx.lightningShoot;
                                hitEffect = despawnEffect = HitFx.monolithHitBig;
                            }

                            @Override
                            public void init(Bullet b) {
                                super.init(b);
                                for (int i = 0; i < 3; ++i) {
                                    subBullet.create(b, b.x, b.y, b.rotation());
                                    Sounds.shootArc.at(b.x, b.y, Mathf.random(0.6f, 0.8f));
                                }
                            }
                        };
                    }
                }, new ChargeShotgunWeapon(""){
                    {
                        mirror = false;
                        rotate = true;
                        rotateSpeed = 8.0f;
                        x = 0.0f;
                        y = 4.0f;
                        shootX = 0.0f;
                        shootY = 24.0f;
                        shoot.shots = 5;
                        shoot.shotDelay = 3.0f;
                        reload = 72.0f;
                        addSequenceTime = 25.0f;
                        shootCone = 90.0f;
                        addEffect = ShootFx.pedestalShootAdd;
                        addedEffect = HitFx.monolithHitBig;
                        shootSound = UnitySounds.chainyShot;
                        bullet = new BasicBulletType(6.0f, 32.0f, "unity-twisting-shell"){
                            {
                                width = 12.0f;
                                height = 16.0f;
                                shrinkY = 0.0f;
                                lifetime = 36.0f;
                                homingPower = 0.07f;
                                homingRange = range * 2.0f;
                                frontColor = UnityPal.monolith;
                                backColor = UnityPal.monolithDark;
                                trailChance = 0.25f;
                                trailEffect = UnityFx.ricochetTrailBig;
                                shootEffect = Fx.hitLaserBlast;
                                smokeEffect = Fx.lightningShoot;
                                hitEffect = despawnEffect = HitFx.monolithHitBig;
                            }
                        };
                    }

                    @Override
                    public void drawCharge(float x, float y, float rotation, float shootAngle, Unit unit, ChargeShotgunWeapon.ChargeShotgunMount mount) {
                        BulletType bulletType = bullet;
                        if (bulletType instanceof BasicBulletType b) {
                            float z = Draw.z();
                            Draw.z(100.0f);
                            Draw.color(b.backColor);
                            Draw.rect(b.backRegion, x, y, b.width, b.height, shootAngle - 90.0f);
                            Draw.color(b.frontColor);
                            Draw.rect(b.frontRegion, x, y, b.width, b.height, shootAngle - 90.0f);
                            Draw.z(z);
                        }
                    }
                });
            }
        };
        pilaster = new UnityUnitType("pilaster"){
            {
                health = 2000.0f;
                speed = 0.4f;
                rotateSpeed = 2.2f;
                hitSize = 26.5f;
                armor = 15.0f;
                mechFrontSway = 0.55f;
                maxSouls = 5;
                canBoost = true;
                boostMultiplier = 2.5f;
                engineSize = 5.0f;
                engineOffset = 10.0f;
                ammoType = new PowerAmmoType(1000.0f);
                outlineColor = UnityPal.darkOutline;
                weapons.add(new Weapon("unity-monolith-medium-weapon-mount"){
                    {
                        top = false;
                        x = 4.0f;
                        y = 7.5f;
                        shootY = 6.0f;
                        rotate = true;
                        recoil = 3.0f;
                        reload = 40.0f;
                        shootSound = Sounds.shootLancer;
                        bullet = new LaserBulletType(160.0f){
                            {
                                lifetime = 27.0f;
                                width = 20.0f;
                                sideAngle = 60.0f;
                                smokeEffect = ShootFx.phantasmalLaserShoot;
                            }
                        };
                    }
                }, new Weapon("unity-monolith-large-weapon-mount"){
                    {
                        top = false;
                        x = 13.0f;
                        y = 2.0f;
                        shootY = 10.5f;
                        rotate = true;
                        rotateSpeed = 10.0f;
                        recoil = 2.5f;
                        reload = 120.0f;
                        shootSound = UnitySounds.chainyShot;
                        bullet = new BasicBulletType(2.7f, 32.0f, "unity-twisting-shell"){
                            {
                                width = 16.0f;
                                height = 20.0f;
                                shrinkY = 0.0f;
                                lifetime = 54.0f;
                                scaleLife = true;
                                frontColor = UnityPal.monolith;
                                backColor = UnityPal.monolithDark;
                                trailColor = UnityPal.monolithLight;
                                trailLength = 32;
                                trailWidth = 1.0f;
                                trailChance = 0.33f;
                                shootEffect = Fx.hitLaserBlast;
                                smokeEffect = ShootFx.tendenceShoot;
                                hitEffect = despawnEffect = HitFx.monolithHitBig;
                                fragBullets = 1;
                                fragVelocityMax = 0.0f;
                                fragVelocityMin = 0.0f;
                                fragBullet = new BulletType(0.0f, 16.0f){
                                    private final Seq<Healthc> all;
                                    {
                                        all = new Seq<>();
                                        lifetime = 96.0f;
                                        collides = false;
                                        hittable = false;
                                        absorbable = false;
                                        keepVelocity = false;
                                        hitSound = Sounds.shootArc;
                                        hitEffect = despawnEffect = Fx.none;
                                    }

                                    float frac(Bullet b) {
                                        return Interp.pow5Out.apply(Mathf.curve(b.fin(), 0.0f, 0.1f)) * Interp.pow3Out.apply(1.0f - Mathf.curve(b.fin(), 0.8f, 1.0f));
                                    }

                                    float radius(Bullet b) {
                                        float s = frac(b);
                                        return 26.0f * s + Mathf.absin(6.0f, 3.0f) * s;
                                    }

                                    public void update(Bullet b) {
                                        updateTrail(b);
                                        float r = radius(b);
                                        if (Mathf.chanceDelta(0.17f)) {
                                            Tmp.v1.trns(Mathf.random(360.0f), Mathf.random(r)).add(b);
                                            ParticleFx.monolithSpark.at(Tmp.v1.x, Tmp.v1.y, 0.0f);
                                        }
                                        if (Mathf.chanceDelta(0.33f)) {
                                            ParticleFx.lightningPivot.at(b.x, b.y, UnityPal.monolith);
                                        }
                                        if (b.timer(0, 60.0f)) {
                                            UnityFx.monolithRingEffect.at(b.x, b.y, 0.0f, 1.0f);
                                        }
                                        if (b.timer(1, 16.0f)) {
                                            all.clear();
                                            Units.nearbyEnemies(b.team, b.x, b.y, 96.0f, all::add);
                                            Units.nearbyBuildings(b.x, b.y, 96.0f, e -> {
                                                if (e.isValid() && e.team != b.team) {
                                                    all.add(e);
                                                }
                                            });
                                            all.sort(arg_0 -> b.dst2(arg_0));
                                            int len = Math.min(all.size, 3);
                                            for (int i = 0; i < len; ++i) {
                                                Healthc target = all.get(i);
                                                target.damage(damage);
                                                Fx.chainLightning.at(b.x, b.y, 0.0f, UnityPal.monolithLight, target);
                                                Fx.hitLancer.at(target);
                                            }
                                            if (len > 0) {
                                                hitSound.at(b);
                                            }
                                        }
                                    }

                                    public void draw(Bullet b) {
                                        float r = radius(b);
                                        Fill.light(b.x, b.y, Lines.circleVertices(r), r, Tmp.c1.set(UnityPal.monolithDark).a(0.0f), Tmp.c2.set(UnityPal.monolith).a(0.8f));
                                        Lines.stroke(2.0f, UnityPal.monolithLight);
                                        Lines.circle(b.x, b.y, r);
                                        float ir = r / 4.0f;
                                        Draw.color(Tmp.c1.set(Pal.lancerLaser).a(0.4f));
                                        UnityDrawf.shiningCircle(b.id, Time.time * 0.67f, b.x, b.y, ir, 4, 16.0f, 30.0f, ir * 2.0f, 90.0f);
                                        Draw.color(Tmp.c1.set(Pal.lancerLaser));
                                        UnityDrawf.shiningCircle(b.id, Time.time * 0.67f, b.x, b.y, ir *= 0.5f, 4, 16.0f, 30.0f, ir * 2.0f, 90.0f);
                                        Draw.color();
                                        UnityDrawf.shiningCircle(b.id, Time.time * 0.67f, b.x, b.y, ir *= 0.5f, 4, 16.0f, 30.0f, ir * 2.0f, 90.0f);
                                        Draw.reset();
                                    }
                                };
                            }

                            public void updateTrail(final Bullet b) {
                                if (!Vars.headless && trailLength > 0 && b.trail == null) {
                                    b.trail = new MultiTrail(new MultiTrail.TrailHold[]{new MultiTrail.TrailHold(Trails.phantasmal(trailLength)), new MultiTrail.TrailHold(Trails.phantasmal(trailLength)), new MultiTrail.TrailHold(Trails.phantasmal(trailLength))}){
                                        boolean dead;
                                        float time;
                                        {
                                            time = Time.time;
                                            trailChance = 0.25f;
                                            trailColor = UnityPal.monolithLight;
                                        }

                                        public void update(float x, float y, float width) {
                                            if (!dead) {
                                                time += Time.delta * 10.0f * (Mathf.randomSeed(b.id, 0, 1) * 2 - 1);
                                                if (!b.isAdded()) {
                                                    dead = true;
                                                }
                                            }
                                            for (int i = 0; i < trails.length; ++i) {
                                                MultiTrail.TrailHold trail = trails[i];
                                                Tmp.v1.trns(b.id * 56.0f + Time.time * 4.0f + 360.0f / trails.length * i, 8.0f).add(x, y);
                                                trail.trail.update(Tmp.v1.x, Tmp.v1.y, width * trail.width);
                                                if (!(trailChance > 0.0f) || !Mathf.chanceDelta(trailChance)) continue;
                                                trailEffect.at(Tmp.v1.x, Tmp.v1.y, trail.width * trailWidth, trailColor);
                                            }
                                            lastX = x;
                                            lastY = y;
                                        }

                                        public void drawCap(Color color, float width) {
                                        }

                                        public void draw(Color color, float width) {
                                            for (MultiTrail.TrailHold trail : trails) {
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
                health = 14400.0f;
                speed = 0.43f;
                rotateSpeed = 1.48f;
                hitSize = 36.0f;
                armor = 23.0f;
                maxSouls = 7;
                hovering = true;
                allowLegStep = true;
                legCount = 4;
                legExtension = 8.0f;
                legSpeed = 0.08f;
                legLength = 16.0f;
                legMoveSpace = 1.2f;
                legBaseOffset = 11.0f;
                ammoType = new PowerAmmoType(2000.0f);
                groundLayer = 75.0f;
                outlineColor = UnityPal.darkOutline;
                weapons.add(new Weapon(name + "-laser"){
                    {
                        soundPitchMin = 1.0f;
                        top = false;
                        mirror = false;
                        shake = 15.0f;
                        shootY = 11.0f;
                        y = 0.0f;
                        x = 0.0f;
                        reload = 280.0f;
                        recoil = 0.0f;
                        cooldownTime = 280.0f;
                        shootStatusDuration = 108.0f;
                        shootStatus = StatusEffects.unmoving;
                        shootSound = Sounds.shootCorvus;
                        chargeSound = Sounds.chargeCorvus;
                        shoot.firstShotDelay = UnityFx.pylonLaserCharge.lifetime / 2.0f;
                        bullet = UnityBullets.pylonLaser;
                    }
                }, new Weapon("unity-monolith-large2-weapon-mount"){
                    {
                        x = 14.0f;
                        y = 5.0f;
                        shootY = 14.0f;
                        rotate = true;
                        rotateSpeed = 3.5f;
                        shootSound = Sounds.shootLancer;
                        shake = 5.0f;
                        reload = 20.0f;
                        recoil = 4.0f;
                        bullet = UnityBullets.pylonLaserSmall;
                    }
                });
            }
        };
        monument = new UnityUnitType("monument"){
            {
                health = 32000.0f;
                speed = 0.42f;
                rotateSpeed = 1.4f;
                hitSize = 48.0f;
                armor = 32.0f;
                maxSouls = 9;
                hovering = true;
                allowLegStep = true;
                legCount = 6;
                legLength = 30.0f;
                legExtension = 8.0f;
                legSpeed = 0.1f;
                legBaseOffset = 15.0f;
                legMoveSpace = 1.2f;
                legPairOffset = 3.0f;
                legSplashDamage = 64.0f;
                legSplashRange = 48.0f;
                ammoType = new PowerAmmoType(2000.0f);
                groundLayer = 75.0f;
                outlineColor = UnityPal.darkOutline;
                LaserBulletType laser = new LaserBulletType(640.0f);
                weapons.add(new Weapon("unity-monolith-large2-weapon-mount"){
                    {
                        top = false;
                        x = 14.0f;
                        y = 12.0f;
                        shootY = 14.0f;
                        rotate = true;
                        rotateSpeed = 3.5f;
                        reload = 36.0f;
                        shake = 5.0f;
                        recoil = 5.0f;
                        shootSound = Sounds.shootLancer;
                        bullet = laser;
                    }
                }, new Weapon("unity-monolith-large2-weapon-mount"){
                    {
                        top = false;
                        x = 20.0f;
                        y = 3.0f;
                        shootY = 14.0f;
                        rotate = true;
                        rotateSpeed = 3.5f;
                        reload = 48.0f;
                        shake = 5.0f;
                        recoil = 5.0f;
                        shootSound = Sounds.shootLancer;
                        bullet = laser;
                    }
                }, new Weapon("unity-monolith-railgun-big"){
                    {
                        mirror = false;
                        x = 0.0f;
                        y = -12.0f;
                        shootY = 35.0f;
                        shadow = 30.0f;
                        reload = 200.0f;
                        shake = 8.0f;
                        recoil = 8.0f;
                        shootCone = 2.0f;
                        cooldownTime = 210.0f;
                        shootSound = Sounds.shootForeshadow;
                        bullet = UnityBullets.monumentRailBullet;
                    }
                });
            }
        };
        colossus = new UnityUnitType("colossus"){
            {
                health = 60000.0f;
                speed = 0.4f;
                rotateSpeed = 1.2f;
                hitSize = 64.0f;
                armor = 45.0f;
                maxSouls = 12;
                hovering = true;
                allowLegStep = true;
                legCount = 6;
                legLength = 48.0f;
                legExtension = 12.0f;
                legSpeed = 0.1f;
                legBaseOffset = 15.0f;
                legMoveSpace = 0.82f;
                legPairOffset = 3.0f;
                legSplashDamage = 84.0f;
                legSplashRange = 48.0f;
                ammoType = new PowerAmmoType(2000.0f);
                groundLayer = 75.0f;
                outlineColor = UnityPal.darkOutline;
                abilities.add(new LightningSpawnAbility(8, 32.0f, 2.0f, 0.05f, 180.0f, 56.0f, 200.0f));
                weapons.add(new Weapon(name + "-weapon"){
                    {
                        top = false;
                        x = 30.0f;
                        y = 7.75f;
                        shootY = 20.0f;
                        reload = 144.0f;
                        recoil = 8.0f;
                        shoot = new ShootSpread(5, 1f) {{
                            shotDelay = 3f;
                        }};
                        inaccuracy = 6.0f;
                        shootSound = Sounds.shootCorvus;
                        bullet = new LaserBulletType(1920.0f){
                            {
                                width = 45.0f;
                                length = 400.0f;
                                lifetime = 32.0f;
                                lightningSpacing = 35.0f;
                                lightningLength = 4;
                                lightningDelay = 1.5f;
                                lightningLengthRand = 6;
                                lightningDamage = 48.0f;
                                lightningAngleRand = 30.0f;
                                lightningColor = Pal.lancerLaser;
                            }
                        };
                    }
                });
            }
        };
        bastion = new UnityUnitType("bastion"){
            {
                health = 120000.0f;
                speed = 0.4f;
                rotateSpeed = 1.2f;
                hitSize = 67.0f;
                armor = 100.0f;
                maxSouls = 15;
                hovering = true;
                allowLegStep = true;
                legCount = 6;
                legLength = 72.0f;
                legExtension = 16.0f;
                legSpeed = 0.12f;
                legBaseOffset = 18.0f;
                legMoveSpace = 0.6f;
                legPairOffset = 3.0f;
                legSplashDamage = 140.0f;
                legSplashRange = 56.0f;
                ammoType = new PowerAmmoType(2000.0f);
                groundLayer = 75.0f;
                outlineColor = UnityPal.darkOutline;
                abilities.add(new LightningSpawnAbility(12, 16.0f, 3.0f, 0.05f, 300.0f, 96.0f, 640.0f));
                RicochetBulletType energy = new RicochetBulletType(6.0f, 50.0f, "shell"){
                    {
                        width = 9.0f;
                        height = 11.0f;
                        shrinkY = 0.3f;
                        lifetime = 45.0f;
                        weaveMag = 3.0f;
                        weaveScale = 3.0f;
                        trailChance = 0.3f;
                        frontColor = UnityPal.monolithLight;
                        backColor = UnityPal.monolith;
                        trailColor = UnityPal.monolithDark;
                        shootEffect = Fx.lancerLaserShoot;
                        smokeEffect = Fx.hitLancer;
                        hitEffect = despawnEffect = HitFx.monolithHitSmall;
                        splashDamage = 60.0f;
                        splashDamageRadius = 10.0f;
                        lightning = 3;
                        lightningDamage = 12.0f;
                        lightningColor = Pal.lancerLaser;
                        lightningLength = 6;
                    }
                };
                weapons.add(new Weapon(name + "-mount"){
                    {
                        x = 9.0f;
                        y = -11.5f;
                        shootY = 10.0f;
                        rotate = true;
                        rotateSpeed = 8.0f;
                        reload = 24.0f;
                        recoil = 6.0f;
                        shoot = new ShootSpread(8, 5f);
                        velocityRnd = 0.3f;
                        shootSound = UnitySounds.energyBolt;
                        bullet = energy;
                    }
                }, new Weapon(name + "-mount"){
                    {
                        x = 23.5f;
                        y = 5.5f;
                        shootY = 10.0f;
                        rotate = true;
                        rotateSpeed = 8.0f;
                        reload = 15.0f;
                        recoil = 6.0f;
                        shoot = new ShootSpread(5, 6f);
                        velocityRnd = 0.3f;
                        shootSound = UnitySounds.energyBolt;
                        bullet = energy;
                    }
                }, new Weapon(name + "-gun"){
                    {
                        x = 12.5f;
                        y = 12.0f;
                        shootY = 13.5f;
                        rotate = true;
                        rotateSpeed = 6.0f;
                        shoot.shots = 8;
                        shoot.shotDelay = 3.0f;
                        reload = 30.0f;
                        recoil = 8.0f;
                        shootSound = Sounds.shootSpectre;
                        bullet = new RicochetBulletType(12.5f, 640.0f, "shell"){
                            {
                                width = 20.0f;
                                height = 25.0f;
                                shrinkY = 0.2f;
                                lifetime = 30.0f;
                                trailLength = 3;
                                pierceCap = 6;
                                frontColor = Color.white;
                                backColor = UnityPal.monolithLight;
                                trailColor = UnityPal.monolith;
                                shootEffect = Fx.lancerLaserShoot;
                                smokeEffect = Fx.hitLancer;
                                hitEffect = despawnEffect = HitFx.monolithHitBig;
                                lightning = 3;
                                lightningDamage = 12.0f;
                                lightningColor = Pal.lancerLaser;
                                lightningLength = 15;
                            }

                            @Override
                            public void update(Bullet b) {
                                super.update(b);
                                if (Mathf.chanceDelta(0.3f)) {
                                    Lightning.create(b, lightningColor, lightningDamage, b.x, b.y, b.rotation(), lightningLength / 2);
                                }
                            }
                        };
                    }
                });
            }
        };
        adsect = new UnityUnitType("adsect"){
            {
                aiController = AssistantAI.create(AssistantAI.Assistance.mendCore, AssistantAI.Assistance.mine, AssistantAI.Assistance.build);
                health = 180.0f;
                speed = 4.0f;
                accel = 0.4f;
                drag = 0.2f;
                rotateSpeed = 15.0f;
                flying = true;
                mineTier = 2;
                mineSpeed = 3.0f;
                buildSpeed = 0.8f;
                circleTarget = false;
                ammoType = new PowerAmmoType(500.0f);
                engineColor = UnityPal.monolith;
                outlineColor = UnityPal.darkOutline;
                weapons.add(new Weapon(){
                    {
                        mirror = false;
                        rotate = false;
                        x = 0.0f;
                        y = 4.0f;
                        reload = 6.0f;
                        shootCone = 40.0f;
                        shootSound = Sounds.shootLaser;
                        bullet = new LaserBoltBulletType(4.0f, 23.0f){
                            {
                                healPercent = 1.5f;
                                lifetime = 40.0f;
                                collidesTeam = true;
                                frontColor = UnityPal.monolithLight;
                                backColor = UnityPal.monolith;
                                hitEffect = despawnEffect = HitFx.hitMonolithLaser;
                                smokeEffect = despawnEffect;
                            }
                        };
                    }
                });
            }
        };
        comitate = new UnityUnitType("comitate"){
            {
                aiController = AssistantAI.create(AssistantAI.Assistance.mendCore, AssistantAI.Assistance.mine, AssistantAI.Assistance.build, AssistantAI.Assistance.heal);
                health = 420.0f;
                speed = 4.5f;
                accel = 0.5f;
                drag = 0.15f;
                rotateSpeed = 15.0f;
                flying = true;
                mineTier = 3;
                mineSpeed = 5.0f;
                buildSpeed = 1.3f;
                circleTarget = false;
                ammoType = new PowerAmmoType(500.0f);
                engineColor = UnityPal.monolith;
                outlineColor = UnityPal.darkOutline;
                weapons.add(new Weapon(){
                    {
                        mirror = false;
                        rotate = false;
                        x = 0.0f;
                        y = 6.0f;
                        reload = 12.0f;
                        shootCone = 40.0f;
                        shootSound = UnitySounds.energyBolt;
                        bullet = new LaserBoltBulletType(6.5f, 60.0f){
                            {
                                width = 4.0f;
                                height = 12.0f;
                                keepVelocity = false;
                                healPercent = 3.5f;
                                lifetime = 35.0f;
                                collidesTeam = true;
                                frontColor = UnityPal.monolithLight;
                                backColor = UnityPal.monolith;
                                hitEffect = despawnEffect = HitFx.hitMonolithLaser;
                                smokeEffect = despawnEffect;
                            }
                        };
                    }
                }, new Weapon("unity-monolith-small-weapon-mount"){
                    {
                        top = false;
                        alternate = true;
                        mirror = true;
                        x = 3.0f;
                        y = 3.0f;
                        reload = 40.0f;
                        shoot.shots = 2;
                        shoot.shotDelay = 5.0f;
                        shootCone = 20.0f;
                        shootSound = Sounds.shootLaser;
                        bullet = new LaserBoltBulletType(4.0f, 30.0f){
                            {
                                healPercent = 1.5f;
                                lifetime = 40.0f;
                                collidesTeam = true;
                                frontColor = UnityPal.monolithLight;
                                backColor = UnityPal.monolith;
                                hitEffect = despawnEffect = HitFx.hitMonolithLaser;
                                smokeEffect = despawnEffect;
                            }
                        };
                    }
                });
            }
        };
        stray = new UnityUnitType("stray"){{
            health = 300.0f;
            speed = 5.0f;
            accel = 0.08f;
            drag = 0.045f;
            rotateSpeed = 8.0f;
            flying = true;
            hitSize = 12.0f;
            lowAltitude = true;
            faceTarget = false;
            outlineColor = UnityPal.darkOutline;

            interface EngineType {
                Engine get(float var1, float var2);
            }

            EngineType etype = (s, offsetY) -> new Engine(){{
                color = UnityPal.monolithLight;
                offset = 11.0f - offsetY;
                size = s;
            }};

            engine = new Engine.MultiEngine(
                    new Engine.MultiEngine.EngineHold(etype.get(2.5f, 0.0f), 0.0f),
                    new Engine.MultiEngine.EngineHold(etype.get(1.5f, 2.5f), -4.5f),
                    new Engine.MultiEngine.EngineHold(etype.get(1.5f, 2.5f), 4.5f)
            ){{
                color = UnityPal.monolithLight;
                size = 2.5f;
                offset = 11.0f;
            }}.apply(this);

            trailType = unit -> new MultiTrail(
                    MultiTrail.rot(unit),
                    new MultiTrail.TrailHold(
                            Trails.phantasmal(MultiTrail.rot(unit), 16, 3.6f, 6.0f, 2.0f),
                            engineColor
                    ),
                    new MultiTrail.TrailHold(
                            Utils.with(Trails.singlePhantasmal(24), t -> {
                                t.trailChance = 0.0f;
                                t.fadeInterp = e -> (1.0f - Interp.pow10In.apply(e)) * Interp.pow2In.apply(e);
                                t.sideFadeInterp = e -> (1.0f - Interp.pow5In.apply(e)) * Interp.pow3In.apply(e);
                            }), -4.5f, 2.5f, 0.44f, UnityPal.monolithLight
                    ),
                    new MultiTrail.TrailHold(Utils.with(Trails.singlePhantasmal(24), t -> {
                        t.trailChance = 0.0f;
                        t.fadeInterp = e -> (1.0f - Interp.pow10In.apply(e)) * Interp.pow2In.apply(e);
                        t.sideFadeInterp = e -> (1.0f - Interp.pow5In.apply(e)) * Interp.pow3In.apply(e);
                    }), 4.5f, 2.5f, 0.44f, UnityPal.monolithLight
                    )
            );

            trailLength = 24;
            weapons.add(new EnergyRingWeapon(){{
                rings.add(
                        new EnergyRingWeapon.Ring(){{
                            radius = 5.5f;
                            thickness = 1.0f;
                            spikes = 4;
                            spikeOffset = 1.5f;
                            spikeWidth = 2.0f;
                            spikeLength = 4.0f;
                            color = UnityPal.monolithDark.cpy().lerp(UnityPal.monolith, 0.5f);
                        }}, new EnergyRingWeapon.Ring(){{
                            radius = 2.5f;
                            shootY = 2.5f;
                            rotate = false;
                            thickness = 1.0f;
                            divisions = 2;
                            divisionSeparation = 30.0f;
                            angleOffset = 90.0f;
                            color = UnityPal.monolith;
                        }}
                );
                y = 0.0f;
                x = 0.0f;
                mirror = false;
                rotate = true;
                reload = 60.0f;
                shoot.shots = 6;
                shoot.shotDelay = 1.0f;
                inaccuracy = 30.0f;
                layerOffset = 10.0f;
                eyeRadius = 1.8f;
                shootSound = UnitySounds.energyBolt;
                bullet = new BasicBulletType(1.0f, 6.0f, "shell"){{
                    drag = -0.08f;
                    lifetime = 35.0f;
                    width = 8.0f;
                    height = 13.0f;
                    homingDelay = 6.0f;
                    homingPower = 0.09f;
                    homingRange = 160.0f;
                    weaveMag = 6.0f;
                    keepVelocity = false;
                    frontColor = trailColor = UnityPal.monolith;
                    backColor = UnityPal.monolithDark;
                    trailChance = 0.3f;
                    trailParam = 1.5f;
                    trailWidth = 2.0f;
                    trailLength = 12;
                    shootEffect = Fx.lightningShoot;
                    hitEffect = despawnEffect = Fx.hitLancer;
                }

                public void updateTrail(Bullet b) {
                    if (!Vars.headless && trailLength > 0 && b.trail == null) {
                        b.trail = Trails.singlePhantasmal(trailLength);
                    }
                    super.updateTrail(b);
                }

                public void removed(Bullet b) {
                    super.removed(b);
                    b.trail = null;
                }
                };
            }}
            );
        }};
        tendence = new UnityUnitType("tendence"){{
            health = 1200.0f;
            speed = 4.2f;
            accel = 0.08f;
            drag = 0.045f;
            rotateSpeed = 7.2f;
            flying = true;
            hitSize = 16.0f;
            lowAltitude = true;
            faceTarget = false;
            maxSouls = 4;
            outlineColor = UnityPal.darkOutline;

            Prov<Engine> etype = () -> new Engine(){{
                offset = 10.0f;
                size = 2.5f;
                color = UnityPal.monolith;
            }};

            engine = new Engine.MultiEngine(
                    new Engine.MultiEngine.EngineHold(etype.get(), -5.0f),
                    new Engine.MultiEngine.EngineHold(etype.get(), 5.0f)
            ){{
                offset = 10.0f;
                size = 2.5f;
                color = UnityPal.monolith;
            }}.apply(this);

            trailType = unit -> new MultiTrail(
                    MultiTrail.rot(unit),
                    new MultiTrail.TrailHold(
                            Trails.soul(MultiTrail.rot(unit), 24),
                            -5.0f, 0.0f, 1.0f, UnityPal.monolithLight
                    ),
                    new MultiTrail.TrailHold(
                            Trails.soul(MultiTrail.rot(unit), 24),
                            5.0f, 0.0f, 1.0f, UnityPal.monolithLight
                    )
            );

            trailLength = 24;
            decals.add(
                    new UnitDecal(name + "-top", 0.0f, 0.0f, 0.0f, 109.98f, Color.white)
            );

            weapons.add(new EnergyRingWeapon(){{
                rings.add(
                        new EnergyRingWeapon.Ring(){{
                            radius = 6.5f;
                            thickness = 1.0f;
                            spikes = 8;
                            spikeOffset = 1.5f;
                            spikeWidth = 2.0f;
                            spikeLength = 5.0f;
                            color = UnityPal.monolithDark.cpy().lerp(UnityPal.monolith, 0.5f);
                        }},
                        new EnergyRingWeapon.Ring(){{
                            radius = 3.0f;
                            shootY = 3.0f;
                            rotate = false;
                            thickness = 1.0f;
                            divisions = 2;
                            divisionSeparation = 30.0f;
                            angleOffset = 90.0f;
                            color = UnityPal.monolith;
                        }}
                );

                x = 0.0f;
                y = 1.0f;
                mirror = false;
                rotate = true;
                reload = 72.0f;
                shoot.firstShotDelay = 35.0f;
                inaccuracy = 15.0f;
                layerOffset = 10.0f;
                eyeRadius = 1.8f;
                parentizeEffects = true;
                chargeSound = UnitySounds.energyCharge;
                shootSound = UnitySounds.energyBlast;
                bullet = new BasicBulletType(4.8f, 72.0f, "shell"){{
                    lifetime = 48.0f;
                    width = 16.0f;
                    height = 25.0f;
                    keepVelocity = false;
                    homingPower = 0.03f;
                    homingRange = range * 2.0f;
                    lightning = 3;
                    lightningColor = UnityPal.monolithLight;
                    lightningDamage = 12.0f;
                    lightningLength = 12;
                    frontColor = trailColor = UnityPal.monolith;
                    backColor = UnityPal.monolithDark;
                    trailEffect = ParticleFx.monolithSpark;
                    trailChance = 0.4f;
                    trailParam = 6.0f;
                    trailWidth = 5.0f;
                    trailLength = 32;
                    hitEffect = despawnEffect = HitFx.tendenceHit;
                    shootEffect = ShootFx.tendenceShoot;
                    chargeEffect = ChargeFx.tendenceCharge;
                }
                public void draw(Bullet b) {
                    super.draw(b);
                    long seed = Mathf.rand.getState(0);
                    TextureAtlas.AtlasRegion reg = Core.atlas.white();
                    TextureAtlas.AtlasRegion light = Core.atlas.find("unity-line-shade");
                    Lines.stroke(2.0f);
                    for (int i = 0; i < 2; ++i) {
                        Mathf.rand.setSeed(b.id);
                        Tmp.v31.set(1.0f, 0.0f, 0.0f).setToRandomDirection();
                        float r = b.id * 20.0f + Time.time * 6.0f * Mathf.sign((b.id % 2 == 0 ? 1 : 0) != 0);
                        Utils.q1.set(i == 0 ? Vec3.X : Vec3.Y, r).mul(Utils.q2.set(Tmp.v31, r * Mathf.signs[i]));
                        Draw.color(i == 0 ? UnityPal.monolith : UnityPal.monolithDark);
                        UnityDrawf.panningCircle(reg, b.x, b.y, 1.0f, 1.0f, 10.0f + i * 4.0f, 360.0f, 0.0f, Utils.q1, true, 89.99f, 115.0f);
                        Draw.color(Color.black, UnityPal.monolithDark, i == 0 ? 0.5f : 0.25f);
                        Draw.blend(Blending.additive);
                        UnityDrawf.panningCircle(light, b.x, b.y, 5.0f, 5.0f, 10.0f + i * 4.0f, 360.0f, 0.0f, Utils.q1, true, 89.99f, 115.0f);
                        Draw.blend();
                    }
                    Draw.reset();
                    Mathf.rand.setSeed(seed);
                }

                public void updateTrail(Bullet b) {
                    if (!Vars.headless && trailLength > 0 && b.trail == null) {
                        b.trail = Trails.soul(trailLength, 6.0f, trailWidth - 0.3f);
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
            }});
        }};
        liminality = new UnityUnitType("liminality"){{
            health = 2000.0f;
            faceTarget = false;
            lowAltitude = true;
            flying = true;
            strafePenalty = 0.1f;
            hitSize = 36.0f;
            speed = 3.5f;
            rotateSpeed = 3.6f;
            drag = 0.06f;
            accel = 0.08f;
            outlineColor = UnityPal.darkOutline;
            ammoType = new PowerAmmoType(2000.0f);

            Prov<Engine> etype = () -> new Engine(){{
                offset = 16.25f;
                size = 3.0f;
                color = UnityPal.monolith;
            }};

            engine = new Engine.MultiEngine(
                    new Engine.MultiEngine.EngineHold(
                            new Engine(){{
                                offset = 22.25f;
                                size = 4.0f;
                                color = UnityPal.monolithLight;
                            }}, 0.0f
                    ),
                    new Engine.MultiEngine.EngineHold(etype.get(), -17.75f),
                    new Engine.MultiEngine.EngineHold(etype.get(), 17.75f)
            ){{
                offset = 21.25f;
                size = 4.0f;
                color = UnityPal.monolithLight;
            }}.apply(this);

            trailType = unit -> new MultiTrail(
                    MultiTrail.rot(unit),
                    new MultiTrail.TrailHold(
                            Trails.phantasmal(MultiTrail.rot(unit), 32, 5.6f, 8.0f, 0.0f),
                            engineColor
                    ),
                    new MultiTrail.TrailHold(
                            Trails.soul(MultiTrail.rot(unit), 48, 6.0f, 3.2f),
                            -17.75f, 6.0f, 0.75f, engineColor
                    ),
                    new MultiTrail.TrailHold(
                            Trails.soul(MultiTrail.rot(unit), 48, 6.0f, 3.2f),
                            17.75f, 6.0f, 0.75f, engineColor
                    )
            );

            trailLength = 48;
            decals.add(
                    new UnitDecal(name + "-top", 0.0f, 0.0f, 0.0f, 109.98f, Color.white)
            );

            weapons.add(
                    new EnergyRingWeapon(){{
                        rings.add(
                                new EnergyRingWeapon.Ring(){{
                                    radius = 9.0f;
                                    thickness = 1.0f;
                                    spikes = 6;
                                    spikeOffset = 1.5f;
                                    spikeWidth = 2.0f;
                                    spikeLength = 4.0f;
                                    color = UnityPal.monolithDark.cpy().lerp(UnityPal.monolith, 0.5f);
                                }},
                                new EnergyRingWeapon.Ring(){{
                                    radius = 5.6f;
                                    shootY = 5.6f;
                                    rotate = false;
                                    thickness = 1.0f;
                                    divisions = 2;
                                    divisionSeparation = 30.0f;
                                    angleOffset = 90.0f;
                                    color = UnityPal.monolith;
                                }},
                                new EnergyRingWeapon.Ring(){{
                                    radius = 1.5f;
                                    thickness = 1.0f;
                                    spikes = 4;
                                    spikeOffset = 1.5f;
                                    spikeWidth = 2.0f;
                                    spikeLength = 2.0f;
                                    flip = true;
                                    color = UnityPal.monolithDark;
                                }}
                        );
                        x = 0.0f;
                        y = 5.0f;
                        mirror = false;
                        rotate = true;
                        reload = 72.0f;
                        layerOffset = 10.0f;
                        eyeRadius = 2.0f;
                        shootSound = Sounds.shootLancer;
                        bullet = new HelixLaserBulletType(240.0f){{
                            sideWidth = 1.4f;
                            sideAngle = 30.0f;
                        }};
                    }}
            );
        }};
        calenture = new UnityUnitType("calenture");
        hallucination = new UnityUnitType("hallucination");
        escapism = new UnityUnitType("escapism");
        fantasy = new UnityUnitType("fantasy");
    }
}