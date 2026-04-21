package unity.util;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.Mesh;
import arc.graphics.Pixmap;
import arc.graphics.Pixmaps;
import arc.graphics.Texture;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.PixmapRegion;
import arc.graphics.g2d.TextureAtlas;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.struct.IntIntMap;
import arc.util.Structs;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Mechc;
import mindustry.gen.Unit;
import mindustry.graphics.MultiPacker;
import mindustry.graphics.MultiPacker.PageType;
import mindustry.type.UnitType;
import mindustry.type.Weapon;
import unity.gen.SColor;

public final class GraphicUtils {
    private static final IntIntMap matches = new IntIntMap();

    public static TextureRegion getRegionRect(TextureRegion region, float x, float y, int rw, int rh, int w, int h) {
        TextureRegion reg = new TextureRegion(region);
        float tileW = (reg.u2 - reg.u) / (float)w;
        float tileH = (region.v2 - region.v) / (float)h;
        float tileX = x / (float)w;
        float tileY = y / (float)h;
        reg.u = Mathf.map(tileX, 0.0F, 1.0F, reg.u, reg.u2) + tileW * 0.02F;
        reg.v = Mathf.map(tileY, 0.0F, 1.0F, reg.v, reg.v2) + tileH * 0.02F;
        reg.u2 = reg.u + tileW * ((float)rw - 0.02F);
        reg.v2 = reg.v + tileH * ((float)rh - 0.02F);
        reg.width = 32 * rw;
        reg.height = 32 * rh;
        return reg;
    }

    public static TextureRegion[] getRegions(TextureRegion region, int w, int h) {
        int size = w * h;
        TextureRegion[] regions = new TextureRegion[size];
        float tileW = (region.u2 - region.u) / (float)w;
        float tileH = (region.v2 - region.v) / (float)h;

        for(int i = 0; i < size; ++i) {
            float tileX = (float)(i % w) / (float)w;
            float tileY = (float)(i / w) / (float)h;
            TextureRegion reg = new TextureRegion(region);
            reg.u = Mathf.map(tileX, 0.0F, 1.0F, reg.u, reg.u2) + tileW * 0.02F;
            reg.v = Mathf.map(tileY, 0.0F, 1.0F, reg.v, reg.v2) + tileH * 0.02F;
            reg.u2 = reg.u + tileW * 0.96F;
            reg.v2 = reg.v + tileH * 0.96F;
            reg.width = reg.height = 32;
            regions[i] = reg;
        }

        return regions;
    }

    public static void simpleUnitDrawer(Unit unit, boolean drawLegs) {
        UnitType type = unit.type;
        if (drawLegs && unit instanceof Mechc) {
        }

        Draw.rect(type.region, unit.x, unit.y, unit.rotation - 90.0F);
        float rotation = unit.rotation - 90.0F;

        for(WeaponMount mount : unit.mounts) {
            Weapon weapon = mount.weapon;
            float weaponRotation = rotation + (weapon.rotate ? mount.rotation : 0.0F);
            float recoil = -(mount.reload / weapon.reload * weapon.recoil);
            float wx = unit.x + Angles.trnsx(rotation, weapon.x, weapon.y) + Angles.trnsx(weaponRotation, 0.0F, recoil);
            float wy = unit.y + Angles.trnsy(rotation, weapon.x, weapon.y) + Angles.trnsy(weaponRotation, 0.0F, recoil);
            Draw.rect(weapon.region, wx, wy, (float)weapon.region.width * Draw.scl * (float)(-Mathf.sign(weapon.flipSprite)), (float)weapon.region.height * Draw.scl, weaponRotation);
        }

    }

    public static TextureRegion blendSprites(TextureRegion a, TextureRegion b, float f, String name) {
        PixmapRegion r1 = Core.atlas.getPixmap(a);
        PixmapRegion r2 = Core.atlas.getPixmap(b);
        Pixmap out = new Pixmap(r1.width, r1.height);

        for(int x = 0; x < r1.width; ++x) {
            for(int y = 0; y < r1.height; ++y) {
                int c1 = r1.get(x, y);
                int c2 = r2.get(x, y);
                out.setRaw(x, y, SColor.lerp(c1, SColor.r(c2), SColor.g(c2), SColor.b(c2), SColor.a(c2), f));
            }
        }

        Texture tex = new Texture(out);
        return Core.atlas.addRegion(name + "-blended-" + (int)(f * 100.0F), tex, 0, 0, tex.width, tex.height);
    }

    public static Pixmap outline(TextureRegion region, Color color, int width) {
        Pixmap out = Pixmaps.outline(Core.atlas.getPixmap(region), color, width);
        if (Core.settings.getBool("linear")) {
            Pixmaps.bleed(out);
        }

        return out;
    }

    public static Pixmap outline(Pixmap pixmap, Color color, int width) {
        Pixmap out = Pixmaps.outline(new PixmapRegion(pixmap), color, width);
        if (Core.settings.getBool("linear")) {
            Pixmaps.bleed(out);
        }

        return out;
    }

    public static void outline(MultiPacker packer, TextureRegion region, Color color, int width) {
        if (region instanceof TextureAtlas.AtlasRegion) {
            TextureAtlas.AtlasRegion at = (TextureAtlas.AtlasRegion)region;
            if (at.found()) {
                outline(packer, region, color, width, at.name + "-outline", false);
            }
        }

    }

    public static void outline(MultiPacker packer, TextureRegion region, Color color, int width, String name, boolean override) {
        if (region instanceof TextureAtlas.AtlasRegion) {
            TextureAtlas.AtlasRegion at = (TextureAtlas.AtlasRegion)region;
            if (at.found() && (override || !packer.has(name))) {
                packer.add(PageType.main, name, outline(region, color, width));
            }
        }

    }

    public static void drawCenter(Pixmap pix, Pixmap other) {
        pix.draw(other, pix.width / 2 - other.width / 2, pix.height / 2 - other.height / 2, true);
    }

    public static void drawCenter(Pixmap pix, PixmapRegion other) {
        Pixmap copy = other.crop();
        drawCenter(pix, copy);
        copy.dispose();
    }

    public static PixmapRegion get(MultiPacker packer, TextureRegion region) {
        if (region instanceof TextureAtlas.AtlasRegion) {
            TextureAtlas.AtlasRegion at = (TextureAtlas.AtlasRegion)region;
            PixmapRegion reg = packer.get(at.name);
            return reg != null ? reg : Core.atlas.getPixmap(at.name);
        } else {
            return null;
        }
    }

    public static PixmapRegion get(MultiPacker packer, String name) {
        PixmapRegion reg = packer.get(name);
        return reg != null ? reg : Core.atlas.getPixmap(name);
    }

    public static int colorLerp(int a, int b, float frac) {
        return SColor.construct(pythagoreanLerp(SColor.r(a), SColor.r(b), frac), pythagoreanLerp(SColor.g(a), SColor.g(b), frac), pythagoreanLerp(SColor.b(a), SColor.b(b), frac), pythagoreanLerp(SColor.a(a), SColor.a(b), frac));
    }

    public static int averageColor(int a, int b) {
        return SColor.construct(pythagoreanAverage(SColor.r(a), SColor.r(b)), pythagoreanAverage(SColor.g(a), SColor.g(b)), pythagoreanAverage(SColor.b(a), SColor.b(b)), pythagoreanAverage(SColor.a(a), SColor.a(b)));
    }

    public static float pythagoreanLerp(float a, float b, float frac) {
        if (a != b && !(frac <= 0.0F)) {
            if (frac >= 1.0F) {
                return b;
            } else {
                a *= a * (1.0F - frac);
                b *= b * frac;
                return Mathf.sqrt(a + b);
            }
        } else {
            return a;
        }
    }

    public static float pythagoreanAverage(float a, float b) {
        return Mathf.sqrt(a * a + b * b) * Utils.sqrtHalf;
    }

    public static int getColor(PixmapRegion pix, float x, float y) {
        int xInt = (int)x;
        int yInt = (int)y;
        if (!Structs.inBounds(xInt, yInt, pix.width, pix.height)) {
            return 0;
        } else {
            boolean isXInt = x == (float)xInt;
            boolean isYInt = y == (float)yInt;
            boolean xOverflow = x + 1.0F > (float)pix.width;
            boolean yOverflow = y + 1.0F > (float)pix.height;
            if ((!isXInt || !isYInt) && (!xOverflow || !yOverflow)) {
                if (!isXInt && !xOverflow) {
                    return !isYInt && !yOverflow ? colorLerp(colorLerp(getAlphaMedianColor(pix, xInt, yInt), getAlphaMedianColor(pix, xInt + 1, yInt), x % 1.0F), colorLerp(getAlphaMedianColor(pix, xInt, yInt + 1), getAlphaMedianColor(pix, xInt + 1, yInt + 1), x % 1.0F), y % 1.0F) : colorLerp(getAlphaMedianColor(pix, xInt, yInt), getAlphaMedianColor(pix, xInt + 1, yInt), x % 1.0F);
                } else {
                    return colorLerp(getAlphaMedianColor(pix, xInt, yInt), getAlphaMedianColor(pix, xInt, yInt + 1), y % 1.0F);
                }
            } else {
                return pix.get(xInt, yInt);
            }
        }
    }

    public static int getAlphaMedianColor(PixmapRegion pix, int x, int y) {
        int color = pix.get(x, y);
        float alpha = SColor.a(color);
        return alpha >= 0.1F ? color : SColor.a(alphaMedian(color, pix.get(x + 1, y), pix.get(x, y + 1), pix.get(x - 1, y), pix.get(x, y - 1)), alpha);
    }

    public static int alphaMedian(int main, int... colors) {
        int c1 = main;
        int c2 = main;
        synchronized(matches) {
            matches.clear();
            int primaryCount = -1;
            int secondaryCount = -1;

            for(int color : colors) {
                if (!(SColor.a(color) < 0.1F)) {
                    int count = matches.increment(color) + 1;
                    if (count > primaryCount) {
                        secondaryCount = primaryCount;
                        c2 = c1;
                        primaryCount = count;
                        c1 = color;
                    } else if (count > secondaryCount) {
                        secondaryCount = count;
                        c2 = color;
                    }
                }
            }

            if (primaryCount > secondaryCount) {
                return c1;
            } else if (primaryCount == -1) {
                return main;
            } else {
                return averageColor(c1, c2);
            }
        }
    }

    public static Mesh copy(Mesh mesh) {
        FloatBuffer originf = mesh.getVerticesBuffer();
        originf.clear();
        ShortBuffer origini = mesh.getIndicesBuffer();
        origini.clear();
        Mesh out = new Mesh(true, mesh.getNumVertices(), mesh.getNumIndices(), mesh.attributes);
        FloatBuffer dstf = out.getVerticesBuffer();
        dstf.clear();
        dstf.put(originf);
        originf.clear();
        dstf.clear();
        ShortBuffer dsti = out.getIndicesBuffer();
        dsti.clear();
        dsti.put(origini);
        origini.clear();
        dsti.clear();
        return out;
    }
}
