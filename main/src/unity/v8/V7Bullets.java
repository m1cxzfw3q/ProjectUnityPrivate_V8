package unity.v8;

import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.bullet.ArtilleryBulletType;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.effect.MultiEffect;
import mindustry.graphics.Pal;

public class V7Bullets {
    public static BulletType
            standardIncendiary = new BasicBulletType(3.2f, 16, "bullet"){{
                width = 10f;
                height = 12f;
                frontColor = Pal.lightishOrange;
                backColor = Pal.lightOrange;
                status = StatusEffects.burning;
                hitEffect = new MultiEffect(Fx.hitBulletSmall, Fx.fireHit);

                ammoMultiplier = 5;

                splashDamage = 10f;
                splashDamageRadius = 22f;

                makeFire = true;
                lifetime = 60f;
    }}, standardThorium = new BasicBulletType(4f, 29, "bullet"){{
        width = 10f;
        height = 13f;
        shootEffect = Fx.shootBig;
        smokeEffect = Fx.shootBigSmoke;
        ammoMultiplier = 4;
        lifetime = 60f;
    }}, standardDense = new BasicBulletType(3.5f, 18f) {{
        width = 9.0F;
        height = 12.0F;
        reloadMultiplier = 0.6F;
        ammoMultiplier = 4.0F;
        lifetime = 60.0F;
    }}, artilleryDense = new ArtilleryBulletType(3f, 20f) {{
        hitEffect = Fx.flakExplosion;
        knockback = 0.8F;
        lifetime = 80.0F;
        width = height = 11.0F;
        collidesTiles = false;
        splashDamageRadius = 18.75F;
        splashDamage = 33.0F;
    }}, standardThoriumBig = new BasicBulletType(8f, 80f) {{
        hitSize = 5.0F;
        width = 16.0F;
        height = 23.0F;
        shootEffect = Fx.shootBig;
        pierceCap = 2;
        pierceBuilding = true;
        knockback = 0.7F;
    }},standardIncendiaryBig = new BasicBulletType(7f, 70, "bullet"){{
        hitSize = 5;
        width = 16f;
        height = 21f;
        frontColor = Pal.lightishOrange;
        backColor = Pal.lightOrange;
        status = StatusEffects.burning;
        hitEffect = new MultiEffect(Fx.hitBulletSmall, Fx.fireHit);
        shootEffect = Fx.shootBig;
        makeFire = true;
        pierceCap = 2;
        pierceBuilding = true;
        knockback = 0.6f;
        ammoMultiplier = 3;
        splashDamage = 15f;
        splashDamageRadius = 24f;
    }},standardDenseBig = new BasicBulletType(7.5f, 50, "bullet"){{
        hitSize = 4.8f;
        width = 15f;
        height = 21f;
        shootEffect = Fx.shootBig;
        ammoMultiplier = 4;
        reloadMultiplier = 1.7f;
        knockback = 0.3f;
    }},artilleryExplosive = new ArtilleryBulletType(2f, 20, "shell"){{
        hitEffect = Fx.blastExplosion;
        knockback = 0.8f;
        lifetime = 80f;
        width = height = 14f;
        collidesTiles = false;
        ammoMultiplier = 4f;
        splashDamageRadius = 45f * 0.75f;
        splashDamage = 55f;
        backColor = Pal.missileYellowBack;
        frontColor = Pal.missileYellow;

        status = StatusEffects.blasted;
    }};
}
