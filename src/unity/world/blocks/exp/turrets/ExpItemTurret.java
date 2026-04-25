package unity.world.blocks.exp.turrets;

import arc.Events;
import arc.func.Boolf;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectMap;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.content.Items;
import mindustry.entities.bullet.BulletType;
import mindustry.game.EventType.Trigger;
import mindustry.gen.Building;
import mindustry.gen.Teamc;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.ui.Bar;
import mindustry.ui.ItemImage;
import mindustry.ui.MultiReqImage;
import mindustry.ui.ReqImage;
import mindustry.world.blocks.defense.turrets.Turret;
import mindustry.world.consumers.ConsumeItemFilter;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatValues;
import mindustry.world.meta.Stats;
import unity.world.blocks.exp.ExpTurret;

public class ExpItemTurret extends ExpTurret {
    public ObjectMap<Item, BulletType> ammoTypes = new ObjectMap();

    public ExpItemTurret(String name) {
        super(name);
        this.hasItems = true;
    }

    public void ammo(Object... objects) {
        this.ammoTypes = ObjectMap.of(objects);
    }

    public void limitRange() {
        this.limitRange(1.0F);
    }

    public void limitRange(float margin) {
        ObjectMap.Entries var2 = this.ammoTypes.copy().entries().iterator();

        while(var2.hasNext()) {
            ObjectMap.Entry<Item, BulletType> entry = (ObjectMap.Entry)var2.next();
            BulletType copy = ((BulletType)entry.value).copy();
            copy.lifetime = (this.range + margin) / copy.speed;
            this.ammoTypes.put((Item)entry.key, copy);
        }

    }

    public void setStats() {
        super.setStats();
        this.stats.remove(Stat.itemCapacity);
        this.stats.add(Stat.ammo, StatValues.ammo(this.ammoTypes));
    }

    public void init() {
        this.consumes.add(new ConsumeItemFilter((i) -> this.ammoTypes.containsKey(i)) {
            public void build(Building tile, Table table) {
                MultiReqImage image = new MultiReqImage();
                Vars.content.items().each((i) -> this.filter.get(i) && i.unlockedNow(), (item) -> image.add(new ReqImage(new ItemImage(item.uiIcon), () -> {
                    boolean var10000;
                    if (tile instanceof ExpItemTurretBuild) {
                        ExpItemTurretBuild it = (ExpItemTurretBuild)tile;
                        if (!it.ammo.isEmpty() && ((ItemEntry)it.ammo.peek()).item == item) {
                            var10000 = true;
                            return var10000;
                        }
                    }

                    var10000 = false;
                    return var10000;
                })));
                table.add(image).size(32.0F);
            }

            public boolean valid(Building entity) {
                boolean var10000;
                if (entity instanceof ExpItemTurretBuild) {
                    ExpItemTurretBuild it = (ExpItemTurretBuild)entity;
                    if (!it.ammo.isEmpty()) {
                        var10000 = true;
                        return var10000;
                    }
                }

                var10000 = false;
                return var10000;
            }

            public void display(Stats stats) {
            }
        });
        super.init();
    }

    public class ExpItemTurretBuild extends ExpTurret.ExpTurretBuild {
        public ExpItemTurretBuild() {
            super(ExpItemTurret.this);
        }

        public void onProximityAdded() {
            super.onProximityAdded();
            if (this.cheating() && this.ammo.size > 0) {
                this.handleItem(this, (Item)ExpItemTurret.this.ammoTypes.entries().next().key);
            }

        }

        public void updateTile() {
            this.unit.ammo((float)this.unit.type().ammoCapacity * (float)this.totalAmmo / (float)ExpItemTurret.this.maxAmmo);
            super.updateTile();
        }

        public void displayBars(Table bars) {
            super.displayBars(bars);
            bars.add(new Bar("stat.ammo", Pal.ammo, () -> (float)this.totalAmmo / (float)ExpItemTurret.this.maxAmmo)).growX();
            bars.row();
        }

        public int acceptStack(Item item, int amount, Teamc source) {
            BulletType type = (BulletType)ExpItemTurret.this.ammoTypes.get(item);
            return type == null ? 0 : Math.min((int)((float)(ExpItemTurret.this.maxAmmo - this.totalAmmo) / ((BulletType)ExpItemTurret.this.ammoTypes.get(item)).ammoMultiplier), amount);
        }

        public void handleStack(Item item, int amount, Teamc source) {
            for(int i = 0; i < amount; ++i) {
                this.handleItem((Building)null, item);
            }

        }

        public int removeStack(Item item, int amount) {
            return 0;
        }

        public void handleItem(Building source, Item item) {
            if (item == Items.pyratite) {
                Events.fire(Trigger.flameAmmo);
            }

            BulletType type = (BulletType)ExpItemTurret.this.ammoTypes.get(item);
            if (type != null) {
                this.totalAmmo = (int)((float)this.totalAmmo + type.ammoMultiplier);

                for(int i = 0; i < this.ammo.size; ++i) {
                    ItemEntry entry = (ItemEntry)this.ammo.get(i);
                    if (entry.item == item) {
                        entry.amount = (int)((float)entry.amount + type.ammoMultiplier);
                        this.ammo.swap(i, this.ammo.size - 1);
                        return;
                    }
                }

                this.ammo.add(ExpItemTurret.this.new ItemEntry(item, (int)type.ammoMultiplier));
            }
        }

        public boolean acceptItem(Building source, Item item) {
            return ExpItemTurret.this.ammoTypes.get(item) != null && (float)this.totalAmmo + ((BulletType)ExpItemTurret.this.ammoTypes.get(item)).ammoMultiplier <= (float)ExpItemTurret.this.maxAmmo;
        }

        public byte version() {
            return 2;
        }

        public void write(Writes write) {
            super.write(write);
            write.b(this.ammo.size);

            for(Turret.AmmoEntry entry : this.ammo) {
                ItemEntry i = (ItemEntry)entry;
                write.s(i.item.id);
                write.s(i.amount);
            }

        }

        public void read(Reads read, byte revision) {
            super.read(read, revision);
            this.ammo.clear();
            this.totalAmmo = 0;
            int amount = read.ub();

            for(int i = 0; i < amount; ++i) {
                Item item = Vars.content.item(revision < 2 ? read.ub() : read.s());
                short a = read.s();
                if (item != null && ExpItemTurret.this.ammoTypes.containsKey(item)) {
                    this.totalAmmo += a;
                    this.ammo.add(ExpItemTurret.this.new ItemEntry(item, a));
                }
            }

        }
    }

    public class ItemEntry extends Turret.AmmoEntry {
        public Item item;

        ItemEntry(Item item, int amount) {
            this.item = item;
            this.amount = amount;
        }

        public BulletType type() {
            return (BulletType)ExpItemTurret.this.ammoTypes.get(this.item);
        }
    }
}
