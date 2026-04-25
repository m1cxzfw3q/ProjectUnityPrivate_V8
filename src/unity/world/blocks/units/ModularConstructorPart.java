package unity.world.blocks.units;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.TextureRegion;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.world.Block;
import unity.graphics.UnityPal;
import unity.world.modules.ModularConstructorModule;

public class ModularConstructorPart extends Block {
    public Color effectColor;
    public TextureRegion topRegion;
    public TextureRegion frontRegion;
    public TextureRegion backRegion;

    public ModularConstructorPart(String name) {
        super(name);
        this.effectColor = UnityPal.advance;
        this.rotate = true;
        this.solid = false;
        this.update = true;
        this.sync = true;
        this.hasPower = true;
        this.hasItems = true;
        this.consumes.power(120.0F);
    }

    public void load() {
        super.load();
        this.topRegion = Core.atlas.find(this.name + "-top");
        this.frontRegion = Core.atlas.find(this.name + "-front");
        this.backRegion = Core.atlas.find(this.name + "-back");
    }

    public class ModularConstructorPartBuild extends Building implements ModularConstructorModule.ModularConstructorModuleInterface {
        public ModularConstructorPartBuild back;
        public ModularConstructorPartBuild front;
        public ModularConstructorModule module = new ModularConstructorModule();

        public ModularConstructorModule consModule() {
            return this.module;
        }

        public boolean consConnected(Building other) {
            if (other.rotation == this.rotation && other.block == this.block) {
                Tmp.v1.trns(this.rotdeg() + 180.0F, (float)(ModularConstructorPart.this.size * 8)).add(this);
                Tmp.r1.setCentered(Tmp.v1.x, Tmp.v1.y, (float)(ModularConstructorPart.this.size * 8));
                other.hitbox(Tmp.r2);
                Tmp.r2.grow(-2.0F);
                return Tmp.r1.contains(Tmp.r2);
            } else {
                return false;
            }
        }

        public void draw() {
            Draw.rect(this.block.region, this.x, this.y, 0.0F);
            this.drawTeamTop();
            Draw.rect(ModularConstructorPart.this.frontRegion, this, this.rotdeg());
            if (this.back != null) {
                Draw.rect(ModularConstructorPart.this.backRegion, this, this.rotdeg());
            } else {
                Tmp.v1.trns(this.rotdeg() + 180.0F, (float)(ModularConstructorPart.this.size * 8)).add(this);
                Tmp.r1.setCentered(Tmp.v1.x, Tmp.v1.y, (float)(ModularConstructorPart.this.size * 8));
                Draw.color(Tmp.c1.set(ModularConstructorPart.this.effectColor).a(0.3F));
                Fill.crect(Tmp.r1.x, Tmp.r1.y, Tmp.r1.width, Tmp.r1.height);
                Draw.reset();
            }

            Draw.rect(ModularConstructorPart.this.topRegion, this, 0.0F);
        }

        public boolean shouldConsume() {
            return this.module.graph != null && this.module.graph.main != null ? this.module.graph.main.shouldConsume() : false;
        }

        public boolean acceptItem(Building source, Item item) {
            return this.module.graph.main != null ? this.module.graph.main.acceptItem(source, item) : super.acceptItem(source, item);
        }

        public void handleItem(Building source, Item item) {
            if (this.module.graph.main != null) {
                this.module.graph.main.handleItem(source, item);
            } else {
                super.handleItem(source, item);
            }
        }

        public void placed() {
            if (!Vars.net.client()) {
                super.placed();
                Building front = this.front();
                if (front instanceof ModularConstructor.ModularConstructorBuild) {
                    ModularConstructor.ModularConstructorBuild mod = (ModularConstructor.ModularConstructorBuild)front;
                    if (mod.consConnected(this)) {
                        this.module.graph = mod.consModule().graph;
                        this.module.graph.all.add(this);
                        Fx.healBlockFull.at(mod.x, mod.y, (float)mod.block.size, ModularConstructorPart.this.effectColor);
                        Fx.healBlockFull.at(this.x, this.y, (float)ModularConstructorPart.this.size, ModularConstructorPart.this.effectColor);
                    }
                }

                if (front instanceof ModularConstructorPartBuild) {
                    ModularConstructorPartBuild mod = (ModularConstructorPartBuild)front;
                    if (mod.module.graph != null && mod.consConnected(this)) {
                        this.module.graph = mod.module.graph;
                        this.module.graph.all.add(this);
                        mod.back = this;
                        this.front = mod;
                        Fx.healBlockFull.at(mod.x, mod.y, (float)mod.block.size, ModularConstructorPart.this.effectColor);
                        Fx.healBlockFull.at(this.x, this.y, (float)ModularConstructorPart.this.size, ModularConstructorPart.this.effectColor);
                    }
                }

                this.updateBack();
            }
        }

        public void updateBack() {
            Building back = this.back();
            if (back instanceof ModularConstructorPartBuild) {
                ModularConstructorPartBuild mod = (ModularConstructorPartBuild)back;
                if (this.consConnected(mod)) {
                    mod.module.graph = this.module.graph;
                    if (mod.module.graph != null) {
                        mod.module.graph.all.add(mod);
                    }

                    mod.front = this;
                    this.back = mod;
                    mod.updateBack();
                }
            }

        }

        public void removePart() {
            if (this.module.graph != null) {
                this.module.graph.remove(this);
                if (this.back != null) {
                    this.back.removePart();
                }
            }

        }

        public void remove() {
            super.remove();
            if (this.front != null) {
                this.front.back = null;
            }

        }
    }
}
