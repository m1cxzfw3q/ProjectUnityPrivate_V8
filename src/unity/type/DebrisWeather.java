package unity.type;

import arc.math.Mathf;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Damage;
import mindustry.gen.Groups;
import mindustry.gen.WeatherState;
import mindustry.type.weather.ParticleWeather;

public class DebrisWeather extends ParticleWeather {
    public float spawnChance = 0.5F;
    public float minSplashRadius = 2.0F;
    public float maxSplashRadius = 10.0F;
    public float minDamage = 50.0F;
    public float maxDamage = 300.0F;
    public float knockbackChance = 0.005F;
    public float minKnockback = 5.0F;
    public float maxKnockback = 15.0F;
    public float knockbackDamageMin = 100.0F;
    public float knockbackDamageMax = 500.0F;

    public DebrisWeather(String name) {
        super(name);
        this.particleRegion = "unity-debris";
    }

    public void update(WeatherState state) {
        if (Mathf.chanceDelta((double)(state.intensity * this.spawnChance))) {
            float x = (float)Mathf.random(Vars.world.unitWidth());
            float y = (float)Mathf.random(Vars.world.unitHeight());
            Fx.blockExplosionSmoke.at(x, y);
            Fx.blockExplosion.at(x, y);
            Damage.damage(x, y, Mathf.random(this.minSplashRadius, this.maxSplashRadius), Mathf.random(this.minDamage, this.maxDamage));
        }

        Groups.unit.each((u) -> {
            if (Mathf.chanceDelta((double)(state.intensity * this.knockbackChance))) {
                u.impulse(Tmp.v1.trns(Mathf.angle(this.xspeed, this.yspeed), Mathf.random(this.minKnockback, this.maxKnockback) * 80.0F));
                u.damage(Mathf.random(this.knockbackDamageMin, this.knockbackDamageMax));
            }

        });
        super.update(state);
    }
}
