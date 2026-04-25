package unity.map.cinematic;

import arc.func.Boolp;
import arc.func.Cons;
import arc.math.Mathf;
import arc.struct.ObjectMap;
import arc.struct.ObjectSet;
import arc.struct.Seq;
import arc.struct.StringMap;
import arc.util.serialization.Json;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.game.EventType.Trigger;
import mindustry.gen.Building;
import mindustry.gen.Groups;
import mindustry.gen.Posc;
import mindustry.io.JsonIO;
import mindustry.world.Tile;
import unity.Unity;
import unity.mod.Triggers;

public class Cinematics {
    public final Boolp validator;
    public final Seq<StoryNode> nodes = new Seq();
    private boolean bound = false;
    public final ObjectMap<String, Object> tagToObject = new ObjectMap();
    public final ObjectMap<Object, ObjectSet<String>> objectToTag = new ObjectMap();
    private final Cons<EventType.Trigger> updater = Triggers.cons(this::update);
    private final Cons<EventType.Trigger> drawer = Triggers.cons(this::draw);

    public Cinematics(Boolp validator) {
        this.validator = validator;
    }

    public void bind() {
        if (Unity.cinematic != null) {
            throw new IllegalStateException("There is already a cinematic core ran!");
        } else {
            Unity.cinematic = this;
            this.bound = true;
            Triggers.listen(Trigger.update, this.updater);
            Triggers.listen(Trigger.drawOver, this.drawer);
            this.nodes.each(StoryNode::init);
        }
    }

    public void detach() {
        if (Unity.cinematic == this) {
            Unity.cinematic = null;
        }

        this.bound = false;
        Triggers.detach(Trigger.update, this.updater);
        Triggers.detach(Trigger.drawOver, this.drawer);
        this.nodes.each(StoryNode::reset);
    }

    public void update() {
        if (this.bound && !this.valid()) {
            this.detach();
        } else {
            this.nodes.each(StoryNode::update);
        }
    }

    public boolean valid() {
        return this.validator.get();
    }

    public void save(StringMap map) {
        map.put("nodes", JsonIO.json.toJson(this.saveNodes(), StringMap.class, String.class));
        map.put("object-tags", JsonIO.json.toJson(this.saveTags(), Seq.class, String.class));
    }

    public StringMap saveNodes() {
        StringMap map = new StringMap();

        for(StoryNode node : this.nodes) {
            StringMap child = new StringMap();
            node.save(child);
            map.put(node.name, JsonIO.json.toJson(child, StringMap.class, String.class));
        }

        return map;
    }

    public Seq<String> saveTags() {
        Seq<String> tagArray = new Seq();
        StringMap valueMap = StringMap.of(new Object[]{"type", 0, "value", null, "tags", "[]"});
        ObjectMap.Entries var3 = this.objectToTag.entries().iterator();

        while(var3.hasNext()) {
            ObjectMap.Entry<Object, ObjectSet<String>> e = (ObjectMap.Entry)var3.next();
            if (!((ObjectSet)e.value).isEmpty()) {
                Object obj = e.key;
                Class<?> type = obj.getClass();
                if (type.isAnonymousClass()) {
                    type = type.getSuperclass();
                }

                if (!(obj instanceof Json.JsonSerializable) && JsonIO.json.getSerializer(type) == null) {
                    if (obj instanceof Building) {
                        Building build = (Building)obj;
                        valueMap.put("type", "1");
                        valueMap.put("value", String.valueOf(build.pos()));
                    } else if (obj instanceof Tile) {
                        Tile tile = (Tile)obj;
                        valueMap.put("type", "2");
                        valueMap.put("value", String.valueOf(tile.pos()));
                    } else {
                        if (!(obj instanceof Posc)) {
                            throw new IllegalArgumentException("Un-serializable tagged object: " + obj);
                        }

                        Posc pos = (Posc)obj;
                        valueMap.put("type", "3");
                        valueMap.put("value", JsonIO.json.toJson(new float[]{pos.getX(), pos.getY()}, float[].class, Float.TYPE));
                    }
                } else {
                    valueMap.put("type", "0");
                    valueMap.put("value", JsonIO.json.toJson(obj));
                }

                valueMap.put("tags", JsonIO.json.toJson(e.value, ObjectSet.class, String.class));
                tagArray.add(JsonIO.json.toJson(valueMap, StringMap.class, String.class));
            }
        }

        return tagArray;
    }

    public void load(StringMap map) {
        this.nodes.each(StoryNode::createObjectives);
        this.loadNodes((StringMap)JsonIO.json.fromJson(StringMap.class, String.class, (String)map.get("nodes", "{}")));
        this.loadTags((Seq)JsonIO.json.fromJson(Seq.class, String.class, (String)map.get("object-tags", "[]")));
    }

    public void loadNodes(StringMap map) {
        ObjectMap.Entries var2 = map.entries().iterator();

        while(var2.hasNext()) {
            ObjectMap.Entry<String, String> e = (ObjectMap.Entry)var2.next();
            StoryNode node = (StoryNode)this.nodes.find((n) -> n.name.equals(e.key));
            if (node == null) {
                throw new IllegalStateException("Node '" + (String)e.key + "' not found!");
            }

            node.load((StringMap)JsonIO.json.fromJson(StringMap.class, String.class, (String)e.value));
        }

    }

    public void loadTags(Seq<String> array) {
        this.objectToTag.clear();
        this.tagToObject.clear();

        for(String e : array) {
            StringMap valueMap = (StringMap)JsonIO.json.fromJson(StringMap.class, String.class, e);
            ObjectSet<String> tagsArray = (ObjectSet)JsonIO.json.fromJson(ObjectSet.class, String.class, (String)valueMap.get("tags", "[]"));
            int type = valueMap.getInt("type");
            Object var10000;
            switch (type) {
                case 0:
                    var10000 = JsonIO.json.fromJson(Object.class, (String)valueMap.get("value"));
                    break;
                case 1:
                    var10000 = Vars.world.build(valueMap.getInt("value"));
                    break;
                case 2:
                    var10000 = Vars.world.tile(valueMap.getInt("value"));
                    break;
                case 3:
                    float[] pos = (float[])JsonIO.json.fromJson(float[].class, (String)valueMap.get("value"));
                    var10000 = Groups.all.find((ent) -> {
                        boolean var10000;
                        if (ent instanceof Posc) {
                            Posc p = (Posc)ent;
                            if (Mathf.equal(p.getX(), pos[0]) && Mathf.equal(p.getY(), pos[1])) {
                                var10000 = true;
                                return var10000;
                            }
                        }

                        var10000 = false;
                        return var10000;
                    });
                    break;
                default:
                    throw new IllegalArgumentException("Unknown tagged object type: " + type);
            }

            Object obj = var10000;
            ObjectSet.ObjectSetIterator var10 = tagsArray.iterator();

            while(var10.hasNext()) {
                String tag = (String)var10.next();
                this.tag(obj, tag);
            }
        }

    }

    public void tag(Object object, String tag) {
        if (object != null) {
            if (this.byTag(tag) != null && this.byTag(tag) != object) {
                throw new IllegalArgumentException("'" + tag + "' tag is already taken!");
            } else {
                ((ObjectSet)this.objectToTag.get(object, ObjectSet::new)).add(tag);
                this.tagToObject.put(tag, object);
            }
        }
    }

    public void untag(Object object, String tag) {
        if (this.tagToObject.get(tag) == object) {
            this.tagToObject.remove(tag);
        }

        ObjectSet<String> set = (ObjectSet)this.objectToTag.get(object);
        if (set != null) {
            set.remove(tag);
        }

    }

    public ObjectSet<String> toTag(Object object) {
        return (ObjectSet)this.objectToTag.get(object);
    }

    public Object byTag(String tag) {
        return this.tagToObject.get(tag);
    }

    public void draw() {
        this.nodes.each(StoryNode::draw);
    }

    public boolean bound() {
        return this.bound;
    }

    public void setNodes(Seq<StoryNode> nodes) {
        this.nodes.set(nodes.select((node) -> node.parent == null));
        this.nodes.each(StoryNode::createObjectives);
    }

    public void setTags(ObjectMap<Object, ObjectSet<String>> tags) {
        this.objectToTag.clear();
        this.tagToObject.clear();
        ObjectMap.Entries var2 = tags.entries().iterator();

        while(var2.hasNext()) {
            ObjectMap.Entry<Object, ObjectSet<String>> e = (ObjectMap.Entry)var2.next();
            if (!((ObjectSet)e.value).isEmpty()) {
                ObjectSet.ObjectSetIterator var4 = ((ObjectSet)e.value).iterator();

                while(var4.hasNext()) {
                    String str = (String)var4.next();
                    this.tag(e.key, str);
                }
            }
        }

    }

    public void clearTags() {
        this.objectToTag.clear();
        this.tagToObject.clear();
    }
}
