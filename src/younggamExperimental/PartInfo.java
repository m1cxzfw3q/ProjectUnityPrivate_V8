package younggamExperimental;

import arc.graphics.g2d.TextureRegion;
import arc.math.geom.Point2;
import arc.struct.OrderedMap;
import arc.struct.Seq;
import mindustry.type.ItemStack;
import unity.util.GraphicUtils;

public class PartInfo {
    public final String name;
    public final String desc;
    public final PartType category;
    public final int tx;
    public final int ty;
    public final int tw;
    public final int th;
    public final boolean cannotPlace;
    public final boolean isRoot;
    public final Point2 prePlace;
    public final ItemStack[] cost;
    public final byte[] connectOut;
    public final byte[] connectIn;
    public final OrderedMap<PartStatType, PartStat> stats;
    public TextureRegion sprite;
    public TextureRegion sprite2;
    public TextureRegion texRegion;
    final Seq<ConnectData> connInList;
    final Seq<ConnectData> connOutList;
    int id;

    public PartInfo(String name, String desc, PartType category, int tx, int ty, int tw, int th, boolean cannotPlace, boolean isRoot, Point2 prePlace, ItemStack[] cost, byte[] connectOut, byte[] connectIn, PartStat... stats) {
        this.stats = new OrderedMap(12);
        this.connInList = new Seq();
        this.connOutList = new Seq();
        this.name = name;
        this.desc = desc;
        this.category = category;
        this.tx = tx;
        this.ty = ty;
        this.tw = tw;
        this.th = th;
        this.cannotPlace = cannotPlace;
        this.isRoot = isRoot;
        this.prePlace = prePlace;
        this.cost = cost;
        this.connectOut = connectOut;
        this.connectIn = connectIn;

        for(PartStat i : stats) {
            this.stats.put(i.category, i);
        }

    }

    public PartInfo(String name, String desc, PartType category, int tx, int ty, int tw, int th, ItemStack[] cost, byte[] connectOut, byte[] connectIn, PartStat... stats) {
        this(name, desc, category, tx, ty, tw, th, false, false, (Point2)null, cost, connectOut, connectIn, stats);
    }

    public static void preCalcConnection(PartInfo[] partsConfig) {
        int i = 0;

        for(int len = partsConfig.length; i < len; ++i) {
            PartInfo pInfo = partsConfig[i];
            if (pInfo.connInList.isEmpty()) {
                int j = 0;

                for(int iLen = pInfo.connectIn.length; j < iLen; ++j) {
                    if (pInfo.connectIn[j] != 0) {
                        pInfo.connInList.add(ConnectData.getConnectSidePos(j, pInfo.tw, pInfo.th).id(pInfo.connectIn[j]));
                    }
                }
            }

            if (pInfo.connOutList.isEmpty()) {
                int j = 0;

                for(int iLen = pInfo.connectOut.length; j < iLen; ++j) {
                    if (pInfo.connectOut[j] != 0) {
                        pInfo.connOutList.add(ConnectData.getConnectSidePos(j, pInfo.tw, pInfo.th).id(pInfo.connectOut[j]));
                    }
                }
            }
        }

    }

    public static void assignPartSprites(PartInfo[] partsConfig, TextureRegion partsSprite, int spriteW, int spriteH) {
        int i = 0;

        for(int len = partsConfig.length; i < len; ++i) {
            PartInfo pinfo = partsConfig[i];
            pinfo.id = i;
            pinfo.texRegion = GraphicUtils.getRegionRect(partsSprite, (float)pinfo.tx, (float)pinfo.ty, pinfo.tw, pinfo.th, spriteW, spriteH);
        }

    }
}
