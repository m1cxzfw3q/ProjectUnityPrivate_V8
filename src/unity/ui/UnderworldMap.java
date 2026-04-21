package unity.ui;

import arc.graphics.Color;
import arc.graphics.Pixmap;
import arc.graphics.Texture;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.TextureRegion;
import arc.input.KeyCode;
import arc.scene.Element;
import arc.scene.event.InputEvent;
import arc.scene.event.InputListener;
import arc.util.Log;
import mindustry.Vars;
import mindustry.graphics.Pal;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.StaticWall;
import unity.type.UnderworldBlock;

public class UnderworldMap extends Element {
    private static Pixmap pixmap;
    private static Texture texture;
    private static TextureRegion region;
    private static Pixmap wallPixmap;
    private static Texture wallTexture;
    private static TextureRegion wallRegion;
    private static Pixmap shadowPixmap;
    private static Texture shadowTexture;
    private static TextureRegion shadowRegion;
    private static Pixmap darknessPixmap;
    private static Texture darknessTexture;
    private static TextureRegion darknessRegion;
    private static int mouseX = -1;
    private static int mouseY = -1;
    private static final Color darknessColor;
    private static final Color realDarknessColor;
    public UnderworldBlock placing;

    public float getMinWidth() {
        return (float)Vars.world.width() * 32.0F;
    }

    public float getMinHeight() {
        return (float)Vars.world.height() * 32.0F;
    }

    public UnderworldMap() {
        this.addListener(new InputListener() {
            public void enter(InputEvent event, float x, float y, int pointer, Element fromActor) {
                UnderworldMap.mouseX = (int)(x / 32.0F);
                UnderworldMap.mouseY = (int)(y / 32.0F);
                super.enter(event, x, y, pointer, fromActor);
            }

            public boolean mouseMoved(InputEvent event, float x, float y) {
                UnderworldMap.mouseX = (int)(x / 32.0F);
                UnderworldMap.mouseY = (int)(y / 32.0F);
                return super.mouseMoved(event, x, y);
            }

            public void exit(InputEvent event, float x, float y, int pointer, Element toActor) {
                UnderworldMap.mouseX = -1;
                UnderworldMap.mouseY = -1;
                super.exit(event, x, y, pointer, toActor);
            }

            public boolean touchDown(InputEvent event, float x, float y, int pointer, KeyCode button) {
                Log.info("Clicked at x:" + UnderworldMap.mouseX + " y:" + UnderworldMap.mouseY);
                return super.touchDown(event, x, y, pointer, button);
            }
        });
    }

    public static void reset() {
        if (pixmap != null) {
            pixmap.dispose();
            texture.dispose();
        }

        if (wallPixmap != null) {
            wallPixmap.dispose();
            wallTexture.dispose();
        }

        if (shadowPixmap != null) {
            shadowPixmap.dispose();
            shadowTexture.dispose();
        }

        if (darknessPixmap != null) {
            darknessPixmap.dispose();
            darknessTexture.dispose();
        }

        pixmap = wallPixmap = shadowPixmap = darknessPixmap = new Pixmap(1, 1);
        texture = new Texture(pixmap);
        region = new TextureRegion(texture);
        wallTexture = new Texture(wallPixmap);
        wallRegion = new TextureRegion(wallTexture);
        shadowTexture = new Texture(shadowPixmap);
        shadowRegion = new TextureRegion(shadowTexture);
        darknessTexture = new Texture(darknessPixmap);
        darknessRegion = new TextureRegion(darknessTexture);
    }

    public static boolean isFree(Tile tile, int x, int y) {
        Tile nearby = tile.nearby(x, y);
        if (nearby == null) {
            return false;
        } else {
            return nearby.block() == null || !(nearby.block() instanceof StaticWall);
        }
    }

    public static boolean checkSquare(Tile tile, int radius) {
        boolean has = false;

        for(int i = -radius; i <= radius; ++i) {
            for(int j = -radius; j <= radius; ++j) {
                if ((i != 0 || j != 0) && isFree(tile, i, j)) {
                    has = true;
                    break;
                }
            }

            if (has) {
                break;
            }
        }

        return has;
    }

    public static void updateAll() {
    }

    public void rectCorner(TextureRegion tr, float w, float h) {
        Draw.rect(tr, this.x + w * 0.5F, this.y + h * 0.5F + 32.0F, w, h);
    }

    public void draw() {
        super.draw();
        Draw.color(Color.white);
        if (region != null && wallRegion != null && shadowRegion != null && darknessRegion != null) {
            this.rectCorner(region, (float)region.width, (float)region.height);
            this.rectCorner(shadowRegion, (float)shadowRegion.width * 32.0F, (float)shadowRegion.height * 32.0F);
            this.rectCorner(wallRegion, (float)wallRegion.width, (float)wallRegion.height);
            this.rectCorner(darknessRegion, (float)darknessRegion.width * 32.0F, (float)darknessRegion.height * 32.0F);
        } else {
            reset();
            updateAll();
        }

        if (mouseX != -1 && mouseY != -1 && !(Vars.world.tile(mouseX, mouseY).block() instanceof StaticWall)) {
            Draw.color(Pal.accent);
            Draw.alpha(0.2F);
            Fill.rect(this.x + (float)mouseX * 32.0F + 16.0F, this.y + (float)mouseY * 32.0F + 16.0F, 32.0F, 32.0F);
            Draw.reset();
        }

    }

    static {
        darknessColor = Color.white.cpy().lerp(Color.black, 0.71F);
        realDarknessColor = new Color(0.0F, 0.0F, 0.0F, darknessColor.a);
    }
}
