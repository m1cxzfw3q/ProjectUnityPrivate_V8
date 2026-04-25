package unity.map.cinematic;

import arc.audio.Sound;
import arc.flabel.FLabel;
import arc.flabel.FListener;
import arc.func.Cons;
import arc.func.Prov;
import arc.graphics.Color;
import arc.graphics.g2d.TextureRegion;
import arc.math.Interp;
import arc.scene.Action;
import arc.scene.actions.Actions;
import arc.scene.style.Drawable;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.ImageButton;
import arc.scene.ui.Label;
import arc.scene.ui.layout.Stack;
import arc.scene.ui.layout.Table;
import java.util.Objects;
import mindustry.Vars;
import mindustry.gen.Icon;
import mindustry.gen.Sounds;
import mindustry.gen.Tex;
import mindustry.graphics.Pal;
import mindustry.ui.Styles;
import unity.ui.UnityStyles;

public class Speeches {
    public static Speeches root;
    private static boolean shown;
    private static boolean initialized;
    private static FLabel label;
    public Prov<CharSequence> title = () -> "";
    public CharSequence content = "";
    public Sound sound;
    public FListener listener;
    public Prov<Drawable> image;
    public Prov<Color> color;
    public float endDelay;
    protected boolean ended;
    protected boolean finished;
    protected Speeches child;

    public Speeches() {
        this.sound = Sounds.click;
        this.image = () -> Tex.clear;
        this.color = () -> Pal.accent;
        this.endDelay = 2.0F;
        this.ended = false;
        this.finished = false;
    }

    public static void init() {
        if (!initialized) {
            initialized = true;
            ((Table)((Table)((Stack)((Table)Vars.ui.hudGroup.find("overlaymarker")).find("waves/editor")).find("waves")).find("statustable")).row().table(Tex.buttonRight, (t) -> {
                root = new Speeches();
                label = new FLabel("") {
                    boolean gamePaused;
                    boolean lastPaused;

                    {
                        this.setWrap(true);
                        this.setAlignment(10);
                        this.setStyle(UnityStyles.speecht);
                        this.setTypingListener(new FListener() {
                            public void event(String event) {
                                Speeches current = Speeches.root.child;
                                if (current != null && current.listener != null) {
                                    current.listener.event(event);
                                }

                            }

                            public String replaceVariable(String variable) {
                                Speeches current = Speeches.root.child;
                                return current != null && current.listener != null ? current.listener.replaceVariable(variable) : super.replaceVariable(variable);
                            }

                            public void onChar(char ch) {
                                Speeches current = Speeches.root.child;
                                if (current != null) {
                                    if (!Character.isWhitespace(ch)) {
                                        current.sound.play();
                                    }

                                    if (current.listener != null) {
                                        current.listener.onChar(ch);
                                    }
                                }

                            }

                            public void end() {
                                Speeches current = Speeches.root.child;
                                if (current != null) {
                                    if (!current.ended) {
                                        current.ended = true;
                                        actions(new Action[]{Actions.delay(current.endDelay), Actions.run(() -> current.finished = true)});
                                    }

                                    if (current.listener != null) {
                                        current.listener.end();
                                    }
                                }

                            }
                        });
                    }

                    public void act(float delta) {
                        if (Vars.state.isPaused()) {
                            if (!this.gamePaused) {
                                this.gamePaused = true;
                                this.lastPaused = this.isPaused();
                                this.pause();
                            }
                        } else if (this.gamePaused) {
                            this.gamePaused = false;
                            if (!this.lastPaused) {
                                this.resume();
                            }
                        }

                        super.act(delta);
                    }

                    public void resume() {
                        if (this.gamePaused) {
                            this.lastPaused = false;
                        } else {
                            super.resume();
                        }

                    }
                };
                t.margin(5.0F);
                t.setClip(true);
                t.table(Styles.black3, (header) -> {
                    header.image().update((i) -> {
                        if (root.child != null) {
                            Drawable icon = (Drawable)root.child.image.get();
                            i.setDrawable(icon == Tex.clear ? Styles.black5 : icon);
                        } else {
                            i.setDrawable(Styles.black5);
                        }

                    }).pad(5.0F).size(48.0F);
                    header.table((title) -> {
                        title.table((up) -> {
                            ((Label)up.label(() -> (CharSequence)(root.child != null ? (CharSequence)root.child.title.get() : "")).style(UnityStyles.speechtitlet).grow().padRight(5.0F).get()).setAlignment(8);
                            TextureRegionDrawable var10001 = Icon.play;
                            ImageButton.ImageButtonStyle var10002 = Styles.emptyi;
                            FLabel var10003 = label;
                            Objects.requireNonNull(var10003);
                            ImageButton var10000 = (ImageButton)up.button(var10001, var10002, var10003::skipToTheEnd).fill().align(18).get();
                            FLabel var1 = label;
                            Objects.requireNonNull(var1);
                            var10000.setDisabled(var1::hasEnded);
                        }).pad(5.0F).grow();
                        title.row().image(Tex.whiteui).update((i) -> {
                            if (root.child != null) {
                                i.setColor((Color)root.child.color.get());
                            }

                        }).growX().height(3.0F).pad(5.0F);
                    }).pad(5.0F).grow();
                }).pad(5.0F).growX().fillY();
                t.row().table(Styles.black3, (cont) -> cont.add(label).pad(5.0F).grow()).pad(5.0F).grow();
            }).width(320.0F).minHeight(200.0F).fillY().align(10).update((t) -> {
                t.setOrigin(10);
                if (!Vars.state.isPlaying() && !Vars.state.isPaused()) {
                    root.child = null;
                    label.restart("");
                    label.pause();
                    shown = false;
                } else {
                    Speeches current = root.child;
                    if (current != null && !current.finished && !shown) {
                        shown = true;
                        Action[] var10001 = new Action[]{Actions.run(() -> {
                            label.restart(current.content);
                            label.pause();
                        }), Actions.scaleTo(1.0F, 1.0F, 0.2F, Interp.pow3Out), null};
                        FLabel var10004 = label;
                        Objects.requireNonNull(var10004);
                        var10001[2] = Actions.run(var10004::resume);
                        t.actions(var10001);
                    }

                    if (current != null && current.finished) {
                        if (current.child != null) {
                            Speeches child = current.child;
                            current.child = null;
                            root.child = child;
                            label.restart(child.content);
                            label.resume();
                        } else if (shown) {
                            shown = false;
                            t.actions(new Action[]{Actions.scaleTo(1.0F, 0.0F, 0.2F, Interp.pow3In)});
                        }
                    }

                    if (root.child == null) {
                        t.actions(new Action[]{Actions.scaleTo(1.0F, 0.0F)});
                    }
                }

            });
        }
    }

    public Speeches show(Cons<Speeches> cons) {
        Speeches child = new Speeches();
        cons.get(child);
        return this.show(child);
    }

    public Speeches show(Speeches child) {
        return this.last().child = child;
    }

    public Speeches last() {
        Speeches current;
        for(current = this; current.child != null; current = current.child) {
        }

        return current;
    }

    public Speeches setTitle(CharSequence title) {
        return this.setTitle((Prov)(() -> title));
    }

    public Speeches setTitle(Prov<CharSequence> title) {
        this.title = title;
        return this;
    }

    public Speeches setContent(CharSequence content) {
        this.content = content;
        return this;
    }

    public Speeches setSound(Sound sound) {
        this.sound = sound;
        return this;
    }

    public Speeches setListener(FListener listener) {
        this.listener = listener;
        return this;
    }

    public Speeches setImage(TextureRegion region) {
        TextureRegionDrawable image = new TextureRegionDrawable(region);
        return this.setImage((Prov)(() -> image));
    }

    public Speeches setImage(Drawable image) {
        return this.setImage((Prov)(() -> image));
    }

    public Speeches setImage(Prov<Drawable> image) {
        this.image = image;
        return this;
    }

    public Speeches setColor(Color color) {
        return this.setColor((Prov)(() -> color));
    }

    public Speeches setColor(Prov<Color> color) {
        this.color = color;
        return this;
    }

    public Speeches setDelay(float endDelay) {
        this.endDelay = endDelay;
        return this;
    }
}
