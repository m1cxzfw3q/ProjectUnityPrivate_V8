package unity.content;

import arc.func.Cons;
import arc.graphics.Color;
import mindustry.content.Fx;
import mindustry.entities.bullet.MissileBulletType;
import mindustry.graphics.Pal;
import mindustry.type.Weapon;
import unity.entities.bullet.anticheat.EndBasicBulletType;
import unity.entities.bullet.energy.EmpBasicBulletType;
import unity.gen.UnitySounds;
import unity.graphics.UnityPal;
import unity.type.CloneableSetWeapon;

public class UnityWeaponTemplates {
    public static CloneableSetWeapon apocalypseSmall;
    public static CloneableSetWeapon apocalypseLauncher;
    public static CloneableSetWeapon apocalypseLaser;
    public static CloneableSetWeapon waveformSmallMount;
    public static CloneableSetWeapon ultravioletMount;
    public static CloneableSetWeapon plagueSmallMount;

    public static void load() {
        apocalypseSmall = new CloneableSetWeapon("unity-ravager-small-turret") {
            {
                this.reload = 120.0F;
                this.shootY = 6.5F;
                this.shots = 3;
                this.spacing = 15.0F;
                this.shootCone = 10.0F;
                this.shadow = 15.0F;
                this.mirror = true;
                this.alternate = true;
                this.rotate = true;
                this.bullet = new EndBasicBulletType(6.0F, 140.0F) {
                    {
                        this.lifetime = 70.0F;
                        this.width = 15.0F;
                        this.height = 19.0F;
                        this.shrinkY = 0.0F;
                        this.backColor = this.hitColor = this.lightColor = UnityPal.scarColor;
                        this.frontColor = UnityPal.endColor;
                        this.ratioStart = 90000.0F;
                        this.ratioDamage = 0.005F;
                        this.overDamage = 900000.0F;
                    }
                };
            }
        };
        apocalypseLauncher = new CloneableSetWeapon("unity-doeg-launcher") {
            {
                this.reload = 210.0F;
                this.rotateSpeed = 6.0F;
                this.shootY = 6.5F;
                this.shots = 12;
                this.shotDelay = 6.0F;
                this.inaccuracy = 20.0F;
                this.shootCone = 10.0F;
                this.shadow = 24.0F;
                this.mirror = true;
                this.alternate = true;
                this.rotate = true;
                this.bullet = new EndBasicBulletType(6.0F, 220.0F, "missile") {
                    {
                        this.lifetime = 80.0F;
                        this.width = 15.0F;
                        this.height = 17.0F;
                        this.shrinkY = 0.0F;
                        this.drag = -0.013F;
                        this.splashDamageRadius = 40.0F;
                        this.splashDamage = 210.0F;
                        this.backColor = this.trailColor = this.hitColor = this.lightColor = UnityPal.scarColor;
                        this.frontColor = UnityPal.endColor;
                        this.trailChance = 0.2F;
                        this.homingPower = 0.08F;
                        this.weaveScale = 6.0F;
                        this.weaveMag = 1.2F;
                        this.hitEffect = Fx.blastExplosion;
                        this.despawnEffect = Fx.blastExplosion;
                        this.ratioStart = 56000.0F;
                        this.ratioDamage = 0.006666667F;
                        this.overDamage = 850000.0F;
                    }
                };
            }
        };
        apocalypseLaser = new CloneableSetWeapon("unity-ravager-artillery") {
            {
                this.reload = 300.0F;
                this.rotateSpeed = 2.0F;
                this.shootY = 6.5F;
                this.shootCone = 10.0F;
                this.shadow = 24.0F;
                this.shootSound = UnitySounds.continuousLaserB;
                this.continuous = true;
                this.rotate = true;
                this.mirror = true;
                this.alternate = false;
                this.bullet = UnityBullets.endLaserSmall;
            }
        };
        waveformSmallMount = new CloneableSetWeapon("unity-emp-small-mount") {
            {
                this.reload = 6.0F;
                this.mirror = false;
                this.alternate = true;
                this.rotate = true;
                this.shootSound = UnitySounds.zbosonShoot;
                this.bullet = new EmpBasicBulletType(5.5F, 9.0F) {
                    {
                        this.lifetime = 38.0F;
                        this.splashDamageRadius = 15.0F;
                        this.splashDamage = 1.5F;
                        this.shrinkY = 0.0F;
                        this.height = 12.0F;
                        this.width = 9.0F;
                        this.trailWidth = this.width / 2.0F / 2.0F;
                        this.powerGridIteration = 1;
                        this.empDuration = 0.0F;
                        this.empBatteryDamage = 4000.0F;
                        this.empRange = 90.0F;
                        this.hitEffect = Fx.hitLancer;
                        this.trailColor = this.backColor = this.lightColor = this.hitColor = Pal.lancerLaser;
                        this.frontColor = Color.white;
                    }
                };
            }
        };
        ultravioletMount = new CloneableSetWeapon("unity-emp-small-launcher") {
            {
                this.shootY = 6.75F;
                this.reload = 20.0F;
                this.mirror = false;
                this.alternate = true;
                this.rotate = true;
                this.shootSound = UnitySounds.zbosonShoot;
                this.bullet = new EmpBasicBulletType(5.7F, 25.0F) {
                    {
                        this.lifetime = 40.0F;
                        this.splashDamageRadius = 15.0F;
                        this.splashDamage = 5.0F;
                        this.shrinkY = 0.0F;
                        this.height = 14.0F;
                        this.width = 10.0F;
                        this.trailWidth = this.width / 2.0F / 2.0F;
                        this.powerGridIteration = 7;
                        this.empDuration = 20.0F;
                        this.empBatteryDamage = 8000.0F;
                        this.empRange = 120.0F;
                        this.hitEffect = Fx.hitLancer;
                        this.trailColor = this.backColor = this.lightColor = this.hitColor = Pal.lancerLaser;
                        this.frontColor = Color.white;
                    }
                };
            }
        };
        plagueSmallMount = new CloneableSetWeapon("unity-small-plague-launcher") {
            {
                this.shootY = 4.75F;
                this.reload = 90.0F;
                this.shots = 4;
                this.inaccuracy = 15.0F;
                this.mirror = false;
                this.alternate = true;
                this.rotate = true;
                this.bullet = new MissileBulletType(3.8F, 9.0F) {
                    {
                        this.width = this.height = 8.0F;
                        this.lifetime = 45.0F;
                        this.backColor = this.hitColor = this.lightColor = this.trailColor = UnityPal.plagueDark;
                        this.frontColor = UnityPal.plague;
                        this.shrinkY = 0.0F;
                        this.drag = -0.01F;
                        this.splashDamage = 17.0F;
                        this.splashDamageRadius = 30.0F;
                        this.weaveScale = 8.0F;
                        this.weaveMag = 2.0F;
                        this.hitEffect = Fx.blastExplosion;
                        this.despawnEffect = Fx.blastExplosion;
                    }
                };
            }
        };
    }

    public static Weapon clnW(Weapon w, Cons<Weapon> cons) {
        Weapon c = w.copy();
        cons.get(c);
        return c;
    }
}
