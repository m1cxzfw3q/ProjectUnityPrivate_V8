package unity.content;

import arc.graphics.Color;
import arc.math.Mathf;
import mindustry.Vars;
import mindustry.game.Team;
import mindustry.gen.Entityc;
import mindustry.gen.Sounds;
import mindustry.gen.WeatherState;
import mindustry.graphics.Pal;
import mindustry.type.Weather;
import mindustry.type.weather.ParticleWeather;
import unity.type.DebrisWeather;

public class UnityWeathers {
    public static Weather timeStorm;
    public static Weather debrisStorm;

    public static void load() {
        timeStorm = new ParticleWeather("time-anomoly") {
            public final float spawnChance = 0.02F;
            public final float minDistSize = 16.0F;
            public final float maxDistSize = 48.0F;

            {
                this.duration = 5400.0F;
                this.noiseLayerSclM = 0.8F;
                this.noiseLayerAlphaM = 0.7F;
                this.noiseLayerSpeedM = 2.0F;
                this.noiseLayerSclM = 0.6F;
                this.baseSpeed = 0.05F;
                this.color = this.noiseColor = Pal.lancerLaser;
                this.noiseScale = 1100.0F;
                this.noisePath = "fog";
                this.drawParticles = false;
                this.drawNoise = true;
                this.useWindVector = false;
                this.xspeed = 2.0F;
                this.yspeed = -0.5F;
                this.opacityMultiplier = 0.47F;
            }

            public void update(WeatherState state) {
                if (Mathf.chanceDelta((double)(state.intensity * 0.02F))) {
                    UnityBullets.distField.create((Entityc)null, Team.derelict, (float)Mathf.random(Vars.world.unitWidth()), (float)Mathf.random(Vars.world.unitHeight()), 0.0F, 1.0F, 1.0F, 1.0F, new Float[]{Mathf.random(16.0F, 48.0F), 2.0F});
                }

                super.update(state);
            }
        };
        debrisStorm = new DebrisWeather("debris-storm") {
            {
                this.sizeMax = 32.0F;
                this.sizeMin = 10.0F;
                this.density = 100000.0F;
                this.xspeed = 18.0F;
                this.yspeed = -12.0F;
                this.color = Color.darkGray;
                this.opacityMultiplier = 2.0F;
                this.sound = Sounds.windhowl;
                this.soundVol = 0.0F;
                this.soundVolOscMag = 1.5F;
                this.soundVolOscScl = 1100.0F;
                this.soundVolMin = 0.02F;
            }
        };
    }
}
