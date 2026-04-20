package unity.assets.list;

import arc.Core;
import arc.freetype.FreeTypeFontGenerator;
import arc.freetype.FreetypeFontLoader;
import arc.graphics.Color;
import arc.graphics.g2d.Font;
import mindustry.Vars;
import unity.ui.UnityStyles;

public class UnityFonts {
    public static Font speech;
    public static Font speechtitle;
    public static Font code;

    public static void load() {
        if (!Vars.headless) {
            Core.assets.load("unity-speech", Font.class, new FreetypeFontLoader.FreeTypeFontLoaderParameter("fonts/font.woff", new FreeTypeFontGenerator.FreeTypeFontParameter() {
                {
                    this.size = 14;
                    this.incremental = true;
                }
            })).loaded = (f) -> speech = UnityStyles.speecht.font = f;
            Core.assets.load("unity-speechtitle", Font.class, new FreetypeFontLoader.FreeTypeFontLoaderParameter("fonts/font.woff", new FreeTypeFontGenerator.FreeTypeFontParameter() {
                {
                    this.size = 21;
                    this.incremental = true;
                    this.shadowColor = Color.darkGray;
                    this.shadowOffsetX = -1;
                    this.shadowOffsetY = 3;
                }
            })).loaded = (f) -> speechtitle = UnityStyles.speechtitlet.font = f;
            Core.assets.load("unity-code-pu", Font.class, new FreetypeFontLoader.FreeTypeFontLoaderParameter("fonts/code.ttf", new FreeTypeFontGenerator.FreeTypeFontParameter() {
                {
                    this.size = 18;
                    this.incremental = true;
                }
            })).loaded = (f) -> {
                code = UnityStyles.codeArea.font = UnityStyles.codeLabel.font = f;
                code.getData().markupEnabled = true;
            };
        }
    }
}
