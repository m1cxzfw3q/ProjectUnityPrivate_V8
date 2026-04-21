package younggamExperimental;

import arc.Core;
import arc.func.Cons;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.input.KeyCode;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.scene.Element;
import arc.scene.event.InputEvent;
import arc.scene.event.InputListener;
import arc.scene.ui.Button;
import arc.scene.ui.ImageButton;
import arc.scene.ui.Label;
import arc.scene.ui.ScrollPane;
import arc.scene.ui.layout.Cell;
import arc.scene.ui.layout.Table;
import arc.struct.GridMap;
import arc.struct.IntSeq;
import arc.struct.ObjectMap;
import arc.struct.OrderedMap;
import arc.struct.OrderedSet;
import arc.struct.Seq;
import java.util.Objects;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.ui.BorderImage;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;
import unity.graphics.UnityPal;

public class ModularConstructorUI extends Element {
    static final Color[] colorPorts = new Color[100];
    static ImageButton prevChecked;
    static PartType currentCat;
    Point2 hover;
    final Seq<PartPlaceObj> partList = new Seq();
    final Seq<PartPlaceObj> rootList = new Seq();
    final GridMap<PartPlaceObj> grid = new GridMap();
    Runnable onTileAction;
    TextureRegion partsSprite;
    PartInfo partsSelect;
    KeyCode dragButton;
    float prefHeight = 100.0F;
    float costAccum = 1.0F;
    float costAccumRate = 0.2F;
    int gridW = 1;
    int gridH = 1;
    boolean isClickedRN;

    public static ModularConstructorUI getModularConstructorUI(float pHeight, TextureRegion partsSprite, PartInfo[] partsConfig, IntSeq preConfig, int maxW, int maxH, float cstacc) {
        ModularConstructorUI pp = new ModularConstructorUI();
        pp.init();
        pp.prefHeight = pHeight;
        pp.partsSprite = partsSprite;
        pp.gridW = maxW;
        pp.gridH = maxH;
        pp.costAccum = cstacc;
        if (preConfig.isEmpty()) {
            for(PartInfo pinfo : partsConfig) {
                if (pinfo.prePlace != null) {
                    pp.placeTile(pinfo, pinfo.prePlace.x, pinfo.prePlace.y);
                }
            }
        } else {
            pp.loadSave(preConfig, partsConfig);
        }

        return pp;
    }

    public static ModularConstructorUI applyModularConstructorUI(Table table, TextureRegion partsSprite, int spriteW, int spriteH, PartInfo[] partsConfig, int maxW, int maxH, IntSeq preConfig, PartType[] categories, float cstaccum) {
        PartInfo.preCalcConnection(partsConfig);
        PartInfo.assignPartSprites(partsConfig, partsSprite, spriteW, spriteH);
        ModularConstructorUI modElement = getModularConstructorUI(400.0F, partsSprite, partsConfig, preConfig, maxW, maxH, cstaccum);
        currentCat = null;
        Cons<Table> partSelectCons = (scrollTable) -> {
            float costInc = modElement.costAccum;
            scrollTable.clearChildren();
            scrollTable.top().left();

            for(PartInfo pinfo : partsConfig) {
                if (!pinfo.cannotPlace && pinfo.category == currentCat) {
                    scrollTable.row();
                    addConsButton(scrollTable, (butt) -> {
                        butt.top().left().margin(12.0F).defaults().left().top();
                        butt.add(pinfo.name).size(170.0F, 45.0F).row();
                        butt.table((topTable) -> {
                            topTable.add(new BorderImage(pinfo.texRegion, 2.0F)).size(36.0F).padTop(-4.0F).padLeft(-4.0F).padRight(4.0F);
                            ((ImageButton)topTable.button(Tex.whiteui, Styles.clearTransi, 50.0F, () -> displayPartInfo(pinfo)).size(50.0F).get()).getStyle().imageUp = Icon.infoSmall;
                        }).marginLeft(4.0F).row();
                        butt.add("[accent]Cost").padBottom(4.0F).row();
                        butt.table((botTable) -> {
                            int i = 0;

                            for(ItemStack cst : pinfo.cost) {
                                botTable.image(cst.item.uiIcon).size(24.0F).left();
                                botTable.add("[gray]" + Mathf.floor((float)cst.amount * costInc)).padLeft(2.0F).left().padRight(4.0F);
                                if (i++ % 2 == 1) {
                                    botTable.row();
                                }
                            }

                        });
                    }, Styles.defaultb, () -> modElement.partsSelect = pinfo).minWidth(150.0F).padBottom(8.0F);
                }
            }

        };
        Table parts = new Table();
        Runnable rebuildParts = () -> partSelectCons.get(parts);
        ScrollPane pane = new ScrollPane(parts, Styles.defaultPane);
        prevChecked = null;
        Table catTable = new Table();
        catTable.margin(12.0F).top().left();

        for(PartType i : categories) {
            ImageButton catButt = new ImageButton(i.region, Styles.clearToggleTransi);
            catButt.clicked(() -> {
                currentCat = i;
                rebuildParts.run();
                catButt.setChecked(true);
                if (prevChecked != null) {
                    prevChecked.setChecked(false);
                }

                prevChecked = catButt;
            });
            catTable.add(catButt);
        }

        rebuildParts.run();
        Table leftSide = new Table();
        leftSide.add(catTable).align(8).row();
        ((ScrollPane)leftSide.add(pane).minWidth(200.0F).maxHeight(400.0F).align(2).get()).setScrollingDisabled(true, false);
        Cons<Table> costCons = (cstTable) -> {
            cstTable.clearChildren();
            cstTable.add("[accent]Total Cost").padBottom(4.0F).row();
            cstTable.table((botTable) -> {
                OrderedMap<Item, Integer> csTot = modElement.getTotalCost();
                ObjectMap.Entries var3 = csTot.iterator();

                while(var3.hasNext()) {
                    ObjectMap.Entry<Item, Integer> cost = (ObjectMap.Entry)var3.next();
                    botTable.image(((Item)cost.key).uiIcon).size(24.0F).left();
                    botTable.add("[gray]" + cost.value).padLeft(2.0F).left().padRight(4.0F).row();
                }

            });
        };
        Table totals = new Table();
        Runnable rebuildTotals = () -> costCons.get(totals);
        table.add(leftSide).minWidth(150.0F).align(2);
        table.add(modElement).size(750.0F, 400.0F);
        table.add(totals).minWidth(100.0F).maxHeight(400.0F).align(2);
        modElement.onTileAction = () -> {
            rebuildParts.run();
            rebuildTotals.run();
        };
        rebuildTotals.run();
        return modElement;
    }

    static Cell<Button> addConsButton(Table table, Cons<Button> consFunc, Button.ButtonStyle style, Runnable runnable) {
        Button button = new Button(style);
        button.clearChildren();
        button.clicked(runnable);
        consFunc.get(button);
        return table.add(button);
    }

    static void displayPartInfo(PartInfo part) {
        BaseDialog dialog = new BaseDialog("Part:" + part.name);
        dialog.setFillParent(false);
        Table cont = dialog.cont;
        cont.add("[lightgray]Name:[white]" + part.name).left().row();
        cont.add("[lightgray]Description:").left().row();
        ((Label)cont.add("[white]" + part.desc).wrap().fillX().left().width(500.0F).maxWidth(500.0F).get()).setWrap(true);
        cont.row();
        cont.add("[accent] Stats");
        ObjectMap.Entries var3 = part.stats.iterator();

        while(var3.hasNext()) {
            ObjectMap.Entry<PartStatType, PartStat> stat = (ObjectMap.Entry)var3.next();
            cont.row();
            cont.add("[lightgray]" + Core.bundle.get(((PartStatType)stat.key).name) + ": [white]" + ((PartStat)stat.value).value.toString()).left();
        }

        Table var10000 = dialog.buttons;
        Objects.requireNonNull(dialog);
        var10000.button("@ok", dialog::hide).size(130.0F, 60.0F);
        dialog.update(() -> {
        });
        dialog.show();
    }

    public void draw() {
        float amx = this.x + this.width * 0.5F;
        float amy = this.y + this.height * 0.5F;
        int gw = this.gridW * 32;
        int gh = this.gridH * 32;
        float gamx = amx - (float)gw * 0.5F;
        float gamy = amy - (float)gh * 0.5F;
        Draw.color(UnityPal.bgCol);
        Fill.rect(amx, amy, this.width, this.height);
        Draw.color(UnityPal.blueprintCol);
        Fill.rect(amx, amy, (float)gw, (float)gh);
        Draw.color();

        for(PartPlaceObj p : this.partList) {
            if (!p.valid) {
                Draw.color(p.flash % 10 < 5 ? Color.pink : Color.white);
                ++p.flash;
            } else {
                Draw.color();
            }

            Draw.rect(p.part.texRegion, (float)p.x * 32.0F + gamx + (float)p.part.tw * 16.0F, (float)p.y * 32.0F + gamy + (float)p.part.th * 16.0F, (float)p.part.tw * 32.0F, (float)p.part.th * 32.0F);
            this.drawOpenConnectionPorts(p.part, p.x, p.y, gamx, gamy);
        }

        Draw.color(Color.black);
        Fill.rect(this.x + 20.0F, this.y + 20.0F, 40.0F, 40.0F);
        Draw.color();
        if (this.partsSelect != null) {
            Draw.rect(this.partsSelect.texRegion, this.x + 20.0F, this.y + 20.0F, 32.0F, 32.0F);
            if (this.hover != null) {
                Draw.color(this.canPlace(this.partsSelect, this.hover.x, this.hover.y) ? Color.white : Color.red, 0.3F);
                Draw.rect(this.partsSelect.texRegion, (float)this.hover.x * 32.0F + gamx + (float)this.partsSelect.tw * 16.0F, (float)this.hover.y * 32.0F + gamy + (float)this.partsSelect.th * 16.0F, (float)this.partsSelect.tw * 32.0F, (float)this.partsSelect.th * 32.0F);
                this.drawOpenConnectionPorts(this.partsSelect, this.hover.x, this.hover.y, gamx, gamy);
            }
        }

    }

    void drawOpenConnectionPorts(PartInfo ps, int x, int y, float offx, float offy) {
        for(ConnectData conout : ps.connInList) {
            int opcx = x + conout.x + conout.dir.x;
            int opcy = y + conout.y + conout.dir.y;
            if (this.getPartAt(opcx, opcy) == null) {
                float brcx = ((float)opcx - (float)conout.dir.x * 0.5F + 0.5F) * 32.0F + offx;
                float brcy = ((float)opcy - (float)conout.dir.y * 0.5F + 0.5F) * 32.0F + offy;
                Draw.color(Color.black);
                Fill.square(brcx, brcy, 6.0F, 45.0F);
                Draw.color(colorPorts[conout.id - 1]);
                Fill.square(brcx, brcy, 2.0F, 45.0F);
                Draw.color();
            }
        }

        for(ConnectData conout : ps.connOutList) {
            int opcx = x + conout.x + conout.dir.x;
            int opcy = y + conout.y + conout.dir.y;
            if (this.getPartAt(opcx, opcy) == null) {
                float brcx = ((float)opcx - (float)conout.dir.x * 0.5F + 0.5F) * 32.0F + offx;
                float brcy = ((float)opcy - (float)conout.dir.y * 0.5F + 0.5F) * 32.0F + offy;
                Draw.color(Color.black);
                Fill.square(brcx, brcy, 6.0F, 45.0F);
                Draw.color(colorPorts[conout.id - 1]);
                Lines.stroke(2.0F);
                Lines.poly(brcx, brcy, 4, 3.0F, 0.0F);
                Draw.color();
            }
        }

    }

    public float getPrefHeight() {
        return this.prefHeight;
    }

    boolean inBounds(PartInfo partType, int x, int y) {
        return partType != null && this.inBoundsRect(x, y, partType.tw, partType.th);
    }

    boolean inBoundsRect(int x, int y, int w, int h) {
        return x >= 0 && x + w <= this.gridW && y >= 0 && y + h <= this.gridH;
    }

    boolean canPlace(PartInfo partType, int x, int y) {
        return this.canPlaceConn(partType, x, y, true);
    }

    boolean canPlaceConn(PartInfo partType, int x, int y, boolean chkConnection) {
        if (!this.inBounds(partType, x, y)) {
            return false;
        } else {
            int px = 0;

            for(int lenX = partType.tw; px < lenX; ++px) {
                int py = 0;

                for(int lenY = partType.th; py < lenY; ++py) {
                    if (this.getPartAt(x + px, y + py) != null) {
                        return false;
                    }
                }
            }

            if (!chkConnection) {
                return true;
            } else {
                px = partType.connInList.isEmpty();

                for(ConnectData i : partType.connInList) {
                    PartPlaceObj fromPart = this.getPartAt(x + i.x + i.dir.x, y + i.y + i.dir.y);
                    if (fromPart != null) {
                        px |= this.partCanConnectOut(fromPart, i.x + x, i.y + y, i.id);
                    }
                }

                if (!px) {
                    for(ConnectData i : partType.connOutList) {
                        PartPlaceObj fromPart = this.getPartAt(x + i.x + i.dir.x, y + i.y + i.dir.y);
                        if (fromPart != null) {
                            px |= this.partCanConnectIn(fromPart, i.x + x, i.y + y, i.id);
                        }
                    }
                }

                return (boolean)px;
            }
        }
    }

    OrderedSet<PartPlaceObj> floodFrom(PartPlaceObj part) {
        OrderedSet<PartPlaceObj> visited = new OrderedSet(12);
        visited.add(part);
        Seq<PartPlaceObj> toVisit = new Seq();
        OrderedSet.OrderedSetIterator var4 = part.parents.iterator();

        while(var4.hasNext()) {
            PartPlaceObj i = (PartPlaceObj)var4.next();
            toVisit.add(i);
        }

        var4 = part.children.iterator();

        while(var4.hasNext()) {
            PartPlaceObj i = (PartPlaceObj)var4.next();
            toVisit.add(i);
        }

        for(int index = 0; index < toVisit.size; ++index) {
            PartPlaceObj cPart = (PartPlaceObj)toVisit.get(index);
            visited.add(cPart);
            OrderedSet.OrderedSetIterator var6 = cPart.parents.iterator();

            while(var6.hasNext()) {
                PartPlaceObj i = (PartPlaceObj)var6.next();
                if (!visited.contains(i)) {
                    toVisit.add(i);
                }
            }

            var6 = cPart.children.iterator();

            while(var6.hasNext()) {
                PartPlaceObj i = (PartPlaceObj)var6.next();
                if (!visited.contains(i)) {
                    toVisit.add(i);
                }
            }
        }

        return visited;
    }

    void rebuildFromRoots() {
        int i = 0;

        for(int len = this.partList.size; i < len; ++i) {
            ((PartPlaceObj)this.partList.get(i)).valid = false;
        }

        i = 0;

        for(int len = this.rootList.size; i < len; ++i) {
            OrderedSet<PartPlaceObj> k = this.floodFrom((PartPlaceObj)this.rootList.get(i));

            PartPlaceObj part;
            for(OrderedSet.OrderedSetIterator var4 = k.iterator(); var4.hasNext(); part.valid = true) {
                part = (PartPlaceObj)var4.next();
            }
        }

    }

    boolean removeTile(PartPlaceObj part) {
        if (part == null) {
            return false;
        } else {
            PartInfo prt = part.part;
            if (prt.isRoot) {
                return false;
            } else {
                OrderedSet.OrderedSetIterator var3 = part.parents.iterator();

                while(var3.hasNext()) {
                    PartPlaceObj i = (PartPlaceObj)var3.next();
                    i.children.remove(part);
                }

                int lenX = prt.tw;
                int lenY = prt.th;

                for(int px = 0; px < lenX; ++px) {
                    for(int py = 0; py < lenY; ++py) {
                        this.grid.remove(part.x + px, part.y + py);
                    }
                }

                this.partList.remove(part);
                this.rebuildFromRoots();
                this.costAccum -= this.costAccumRate * (float)lenX * (float)lenY;
                return true;
            }
        }
    }

    boolean placeTile(PartInfo partType, int x, int y) {
        if (!this.canPlace(partType, x, y)) {
            return false;
        } else {
            this.placeTileDirect(partType, x, y);
            return true;
        }
    }

    boolean placeTileNoConn(PartInfo partType, int x, int y) {
        if (!this.canPlaceConn(partType, x, y, true)) {
            return false;
        } else {
            this.placeTileDirect(partType, x, y);
            return true;
        }
    }

    boolean placeTileDirect(PartInfo partType, int x, int y) {
        PartPlaceObj partPlaceObj = new PartPlaceObj(x, y, partType);

        for(ConnectData i : partType.connInList) {
            PartPlaceObj fromPart = this.getPartAt(x + i.x + i.dir.x, y + i.y + i.dir.y);
            if (fromPart != null && this.partCanConnectOut(fromPart, i.x + x, i.y + y, i.id)) {
                partPlaceObj.parents.add(fromPart);
                fromPart.children.add(partPlaceObj);
            }
        }

        for(ConnectData i : partType.connOutList) {
            PartPlaceObj fromPart = this.getPartAt(x + i.x + i.dir.x, y + i.y + i.dir.y);
            if (fromPart != null && this.partCanConnectOut(fromPart, i.x + x, i.y + y, i.id)) {
                partPlaceObj.children.add(fromPart);
                fromPart.parents.add(partPlaceObj);
            }
        }

        int xLen = partType.tw;
        int yLen = partType.th;

        for(int px = 0; px < xLen; ++px) {
            for(int py = 0; py < yLen; ++py) {
                this.grid.put(x + px, y + py, partPlaceObj);
            }
        }

        if (partType.isRoot) {
            this.rootList.add(partPlaceObj);
        }

        this.partList.add(partPlaceObj);
        this.rebuildFromRoots();
        this.costAccum += this.costAccumRate * (float)xLen * (float)yLen;
        return true;
    }

    void onIsClicked(InputEvent event, float x, float y, int point, KeyCode butt) {
        this.isClickedRN = true;
        Point2 gPos = this.uiToGridPos(x, y);
        boolean success;
        if (butt == KeyCode.mouseRight) {
            success = this.removeTile(this.getPartAt(gPos.x, gPos.y));
        } else {
            success = this.placeTile(this.partsSelect, gPos.x, gPos.y);
        }

        this.dragButton = butt;
        if (this.onTileAction != null && success) {
            this.onTileAction.run();
        }

    }

    void onIsDragged(InputEvent event, float x, float y, int point) {
        if (this.isClickedRN) {
            Point2 gPos = this.uiToGridPos(x, y);
            boolean success;
            if (this.dragButton == KeyCode.mouseRight) {
                success = this.removeTile(this.getPartAt(gPos.x, gPos.y));
            } else {
                success = this.placeTile(this.partsSelect, gPos.x, gPos.y);
            }

            if (this.onTileAction != null && success) {
                this.onTileAction.run();
            }
        }

    }

    boolean onIsHovering(InputEvent event, float x, float y) {
        if (!(x < 0.0F) && !(x > this.width) && !(y < 0.0F) && !(y > this.height)) {
            this.hover = this.uiToGridPos(x, y);
            return true;
        } else {
            this.hover = null;
            return false;
        }
    }

    void init() {
        this.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, KeyCode button) {
                ModularConstructorUI.this.onIsClicked(event, x, y, pointer, button);
                return true;
            }

            public boolean mouseMoved(InputEvent event, float x, float y) {
                return ModularConstructorUI.this.onIsHovering(event, x, y);
            }

            public void touchUp(InputEvent event, float x, float y, int pointer, KeyCode button) {
                ModularConstructorUI.this.isClickedRN = false;
            }

            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                ModularConstructorUI.this.onIsDragged(event, x, y, pointer);
            }
        });
    }

    OrderedMap<Item, Integer> getTotalCost() {
        OrderedMap<Item, Integer> cst = new OrderedMap(this.partList.size);

        for(PartPlaceObj i : this.partList) {
            for(ItemStack p : i.part.cost) {
                int cur = (Integer)cst.get(p.item, 0);
                cst.put(p.item, cur + Mathf.floor((float)p.amount * (this.costAccum - this.costAccumRate)));
            }
        }

        return cst;
    }

    PartPlaceObj getPartAt(int x, int y) {
        return !this.inBoundsRect(x, y, 1, 1) ? null : (PartPlaceObj)this.grid.get(x, y);
    }

    boolean partCanConnectOut(PartPlaceObj part, int x, int y, byte portId) {
        for(ConnectData i : part.part.connOutList) {
            if (i.id == portId && x == part.x + i.x + i.dir.x && y == part.y + i.y + i.dir.y) {
                return true;
            }
        }

        return false;
    }

    boolean partCanConnectIn(PartPlaceObj part, int x, int y, byte portId) {
        for(ConnectData i : part.part.connInList) {
            if (i.id == portId && x == part.x + i.x + i.dir.x && y == part.y + i.y + i.dir.y) {
                return true;
            }
        }

        return false;
    }

    Point2 uiToGridPos(float x, float y) {
        int gw = this.gridW * 32;
        int gh = this.gridH * 32;
        float gamx = (this.width - (float)gw) * 0.5F;
        float gamy = (this.height - (float)gh) * 0.5F;
        return new Point2(Mathf.floor((x - gamx) / 32.0F), Mathf.floor((y - gamy) / 32.0F));
    }

    public String getPackedSave() {
        IntPacker packer = new IntPacker();

        for(int px = 0; px < this.gridW; ++px) {
            for(int py = 0; py < this.gridH; ++py) {
                PartPlaceObj p = this.getPartAt(px, py);
                if (p != null && p.x == px && p.y == py && p.valid) {
                    packer.add(p.part.id + 1);
                } else {
                    packer.add(0);
                }
            }
        }

        packer.end();
        return packer.toStringPack();
    }

    void loadSave(IntSeq array, PartInfo[] partList) {
        int i = 0;

        for(int len = array.size; i < len; ++i) {
            int temp = array.get(i);
            if (temp != 0) {
                int px = i / this.gridH;
                int py = i % this.gridH;
                this.placeTileNoConn(partList[temp - 1], px, py);
            }
        }

    }

    static {
        for(int i = 0; i < 100; ++i) {
            colorPorts[i] = Color.HSVtoRGB(360.0F * Mathf.random(), 100.0F * Mathf.random(0.3F, 1.0F), 100.0F * Mathf.random(0.9F, 1.0F), 1.0F);
        }

    }

    static class PartPlaceObj {
        final PartInfo part;
        final int x;
        final int y;
        int flash;
        boolean valid;
        final OrderedSet<PartPlaceObj> parents = new OrderedSet(12);
        final OrderedSet<PartPlaceObj> children = new OrderedSet(12);

        PartPlaceObj(int x, int y, PartInfo part) {
            this.x = x;
            this.y = y;
            this.part = part;
        }
    }
}
