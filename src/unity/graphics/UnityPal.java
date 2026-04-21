package unity.graphics;

import arc.graphics.Color;
import mindustry.graphics.Pal;

public class UnityPal {
    public static final Color plague = Color.valueOf("a3f080");
    public static final Color plagueDark = Color.valueOf("54de3b");
    public static final Color scarColor = Color.valueOf("f53036");
    public static final Color scarColorAlpha = Color.valueOf("f5303690");
    public static final Color monolithLight = Color.valueOf("c0ecff");
    public static final Color monolith = Color.valueOf("87ceeb");
    public static final Color monolithDark = Color.valueOf("6586b0");
    public static final Color monolithAtmosphere = Color.valueOf("001e6360");
    public static final Color monolithGreenLight = Color.valueOf("8cf7dd");
    public static final Color monolithGreen = Color.valueOf("4dcfc8");
    public static final Color monolithGreenDark = Color.valueOf("358697");
    public static final Color advance = Color.valueOf("a3e3ff");
    public static final Color advanceDark = Color.valueOf("59a7ff");
    public static final Color wavefrontDark = Color.valueOf("9e9f9f");
    public static final Color lightLight = Color.valueOf("a0ffff");
    public static final Color lightMid = Color.valueOf("50ecff");
    public static final Color lightDark = Color.valueOf("00d9ff");
    public static final Color lightHeat = Color.valueOf("ccffff");
    public static final Color lightEffect = Color.valueOf("4787ff");
    public static final Color purpleLightning = Color.valueOf("bf92f9");
    public static final Color endColor = Color.valueOf("ff786e");
    public static final Color imberColor = Color.valueOf("fff566");
    public static final Color navalReddish = Color.valueOf("d4816b");
    public static final Color navalYellowish = Color.valueOf("ffd37f");
    public static final Color laserOrange = Color.valueOf("ff9c5a");
    public static final Color expLaser = Color.valueOf("F9DBB1");
    public static final Color exp = Color.valueOf("84ff00");
    public static final Color expMax = Color.valueOf("90ff00");
    public static final Color expBack = Color.valueOf("4d8f07");
    public static final Color lava = Color.valueOf("ff2a00");
    public static final Color lava2 = Color.valueOf("ffcc00");
    public static final Color dense = Color.valueOf("ffbeb8");
    public static final Color dirium = Color.valueOf("96f7c3");
    public static final Color diriumLight = Color.valueOf("ccffe4");
    public static final Color coldColor = Color.valueOf("6bc7ff");
    public static final Color bgCol = Color.valueOf("323232");
    public static final Color deepRed = Color.valueOf("f25555");
    public static final Color deepBlue = Color.valueOf("554deb");
    public static final Color passive = Color.valueOf("61caff");
    public static final Color armor = Color.valueOf("e09e75");
    public static final Color lancerSap1;
    public static final Color lancerSap2;
    public static final Color lancerSap3;
    public static final Color lancerSap4;
    public static final Color lancerSap5;
    public static final Color lancerDir1;
    public static final Color lancerDir2;
    public static final Color lancerDir3;
    public static final Color youngchaGray;
    public static final Color blueprintCol;
    public static final Color outline;
    public static final Color darkOutline;
    public static final Color darkerOutline;

    static {
        lancerSap1 = Pal.lancerLaser.cpy().lerp(Pal.sapBullet, 0.167F);
        lancerSap2 = Pal.lancerLaser.cpy().lerp(Pal.sapBullet, 0.333F);
        lancerSap3 = Pal.lancerLaser.cpy().lerp(Pal.sapBullet, 0.5F);
        lancerSap4 = Pal.lancerLaser.cpy().lerp(Pal.sapBullet, 0.667F);
        lancerSap5 = Pal.lancerLaser.cpy().lerp(Pal.sapBullet, 0.833F);
        lancerDir1 = Pal.lancerLaser.cpy().lerp(diriumLight, 0.25F);
        lancerDir2 = Pal.lancerLaser.cpy().lerp(diriumLight, 0.5F);
        lancerDir3 = Pal.lancerLaser.cpy().lerp(diriumLight, 0.75F);
        youngchaGray = Color.valueOf("555555");
        blueprintCol = Color.valueOf("354654");
        outline = Pal.darkerMetal;
        darkOutline = Color.valueOf("38383d");
        darkerOutline = Color.valueOf("2e3142");
    }
}
