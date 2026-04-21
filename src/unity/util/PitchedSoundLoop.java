package unity.util;

import arc.Core;
import arc.audio.Sound;

public class PitchedSoundLoop {
    private final Sound sound;
    private int id = -1;
    private float baseVolume;

    public PitchedSoundLoop(Sound sound, float baseVolume) {
        this.sound = sound;
        this.baseVolume = baseVolume;
    }

    public void update(float x, float y, float volume, float pitch) {
        if (!(this.baseVolume <= 0.0F)) {
            if (this.id < 0) {
                this.id = this.sound.loop(this.sound.calcVolume(x, y) * volume * this.baseVolume, 1.0F, this.sound.calcPan(x, y));
            } else {
                if (volume <= 0.001F) {
                    Core.audio.stop(this.id);
                    this.id = -1;
                    return;
                }

                Core.audio.set(this.id, this.sound.calcPan(x, y), this.sound.calcVolume(x, y) * volume * this.baseVolume);
                Core.audio.setPitch(this.id, pitch);
            }

        }
    }

    public void stop() {
        if (this.id != -1) {
            Core.audio.stop(this.id);
            this.id = -1;
            this.baseVolume = -1.0F;
        }

    }
}
