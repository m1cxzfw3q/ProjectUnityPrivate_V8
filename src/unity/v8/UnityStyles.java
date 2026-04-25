package unity.v8;

import arc.graphics.Color;
import arc.scene.ui.ImageButton;
import mindustry.ui.Styles;

public class UnityStyles {
    public static ImageButton.ImageButtonStyle clearTransi, clearToggleTransi;

    public static void load() {
        clearTransi = new ImageButton.ImageButtonStyle() {{
            down = Styles.flatDown;
            up = Styles.black6;
            over = Styles.flatOver;
            disabled = Styles.black8;
            imageDisabledColor = Color.lightGray;
            imageUpColor = Color.white;
        }};

        clearToggleTransi = new ImageButton.ImageButtonStyle() {{
            down = Styles.flatDown;
            checked = Styles.flatDown;
            up = Styles.black6;
            over = Styles.flatOver;
        }};
    }
}
