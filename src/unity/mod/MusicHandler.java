package unity.mod;

import arc.audio.Music;
import arc.func.Boolp;
import arc.util.Disposable;

public interface MusicHandler extends Disposable {
    default void setup() {
    }

    default void registerLoop(String name, Music loop) {
        this.registerLoop(name, loop, loop);
    }

    default void registerLoop(String name, Music intro, Music loop) {
    }

    default void play(String name) {
        this.play(name, (Boolp)null);
    }

    default void play(String name, Boolp predicate) {
    }

    default void dispose() {
    }
}
