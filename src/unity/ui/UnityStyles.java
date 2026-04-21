package unity.ui;

import arc.Core;
import arc.graphics.Color;
import arc.scene.ui.Label;
import arc.scene.ui.TextButton;
import arc.scene.ui.TextField;
import mindustry.Vars;
import mindustry.gen.Tex;
import mindustry.ui.Fonts;

public class UnityStyles {
    public static TextButton.TextButtonStyle creditst;
    public static Label.LabelStyle speecht;
    public static Label.LabelStyle speechtitlet;
    public static Label.LabelStyle codeLabel;
    public static TextField.TextFieldStyle codeArea;

    public static void load() {
        if (!Vars.headless) {
            creditst = new TextButton.TextButtonStyle() {
                {
                    this.font = Fonts.def;
                    this.fontColor = Color.white;
                    this.up = Core.atlas.drawable("unity-credits-banner-up");
                    this.down = Core.atlas.drawable("unity-credits-banner-down");
                    this.over = Core.atlas.drawable("unity-credits-banner-over");
                }
            };
            speecht = new Label.LabelStyle() {
                {
                    this.fontColor = Color.white;
                }
            };
            speechtitlet = new Label.LabelStyle() {
                {
                    this.fontColor = Color.white;
                }
            };
            codeLabel = new Label.LabelStyle() {
                {
                    this.fontColor = Color.white;
                }
            };
            codeArea = new TextField.TextFieldStyle() {
                {
                    this.fontColor = Color.white;
                    this.disabledFontColor = Color.gray;
                    this.disabledBackground = Tex.underlineDisabled;
                    this.selection = Tex.selection;
                    this.cursor = Tex.cursor;
                    this.messageFont = Fonts.def;
                    this.messageFontColor = Color.gray;
                }
            };
        }
    }
}
