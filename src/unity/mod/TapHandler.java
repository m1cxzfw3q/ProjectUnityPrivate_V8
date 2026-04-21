package unity.mod;

import arc.Core;
import arc.Events;
import arc.input.GestureDetector;
import arc.input.KeyCode;
import arc.scene.ui.TextField;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.game.EventType.Trigger;
import mindustry.gen.Player;
import mindustry.input.Binding;
import unity.sync.UnityCall;

public class TapHandler {
    private final Seq<TapListener> listeners = new Seq();
    private boolean press = false;

    public TapHandler() {
        if (!Vars.headless) {
            if (Vars.mobile) {
                Core.input.addProcessor(new GestureDetector(new GestureDetector.GestureListener() {
                    public boolean tap(float x, float y, int count, KeyCode button) {
                        if (count == 2) {
                            if (Vars.state.isMenu() || Core.scene.hasMouse(x, y) || Vars.control.input.isPlacing() || Vars.control.input.isBreaking() || Vars.control.input.selectedUnit() != null) {
                                return false;
                            }

                            UnityCall.tap(Vars.player, x, y);
                        }

                        return false;
                    }
                }));
            } else {
                Events.run(Trigger.update, () -> {
                    if (!Vars.state.isMenu()) {
                        if (Core.input.keyDown(Binding.boost) && !(Core.scene.getKeyboardFocus() instanceof TextField)) {
                            if (!this.press) {
                                this.press = true;
                                UnityCall.tap(Vars.player, Core.input.mouseWorldX(), Core.input.mouseWorldY());
                            }
                        } else {
                            this.press = false;
                        }
                    } else {
                        this.press = false;
                    }

                });
            }

        }
    }

    public void tap(Player player, float x, float y) {
        for(TapListener listener : this.listeners) {
            listener.tap(player, x, y);
        }

    }

    public void addListener(TapListener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(TapListener listener) {
        this.listeners.remove(listener);
    }

    public interface TapListener {
        void tap(Player var1, float var2, float var3);
    }
}
