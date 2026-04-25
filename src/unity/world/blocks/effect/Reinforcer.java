package unity.world.blocks.effect;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.geom.Vec2;
import arc.util.Time;
import mindustry.entities.Units;
import mindustry.gen.Building;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.world.Block;
import unity.content.UnityStatusEffects;

public class Reinforcer extends Block {
    public float range = 60.0F;
    public TextureRegion baseRegion;
    public TextureRegion laserRegion;
    public TextureRegion laserEndRegion;
    public float laserLength = -1.0F;
    public Color laserColor;
    public float rotateSpeed;
    public float loadThreshold;
    public float cone;

    public Reinforcer(String name) {
        super(name);
        this.laserColor = Pal.thoriumPink;
        this.rotateSpeed = 2.0F;
        this.loadThreshold = 1.0F;
        this.cone = 2.0F;
        this.update = true;
        this.acceptsItems = true;
        this.hasItems = true;
        this.outlineIcon = true;
    }

    public void load() {
        super.load();
        this.baseRegion = Core.atlas.find("block-" + this.size);
        this.laserRegion = Core.atlas.find("unity-pointy-laser");
        this.laserEndRegion = Core.atlas.find("unity-pointy-laser-end");
    }

    public TextureRegion[] icons() {
        return new TextureRegion[]{this.baseRegion, this.region};
    }

    public void init() {
        super.init();
        if (this.laserLength < 0.0F) {
            this.laserLength = (float)(this.size * 8) / 2.0F;
        }

    }

    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);
        Drawf.dashCircle((float)(x * 8) + this.offset, (float)(y * 8) + this.offset, this.range, Pal.placing);
    }

    public class ReinforcerBuilding extends Building {
        public float load;
        public float laserWidth;
        public float rotation;
        public float targetRot;
        public Vec2 posOffset;
        public Unit unit;
        public boolean canReinforce;
        public Unit prevUnit;

        public ReinforcerBuilding() {
            this.load = Reinforcer.this.loadThreshold;
            this.laserWidth = 0.3F;
            this.rotation = 90.0F;
            this.targetRot = 90.0F;
            this.posOffset = new Vec2(0.0F, 0.0F);
            this.canReinforce = false;
        }

        public void updateTile() {
            if (this.consValid()) {
                this.unit = Units.closest(this.team, this.x, this.y, Reinforcer.this.range, (u) -> u != null && !u.hasEffect(UnityStatusEffects.plated) && !u.spawnedByCore);
                if (this.unit != null) {
                    this.prevUnit = this.unit;
                    this.turnToTarget(Angles.angle(this.x, this.y, this.unit.x, this.unit.y));
                    this.targetRot = Angles.angle(this.x, this.y, this.unit.x, this.unit.y);
                    this.canReinforce = Angles.angleDist(this.rotation, this.targetRot) <= Reinforcer.this.cone;
                    if (this.canReinforce && this.load >= Reinforcer.this.loadThreshold) {
                        this.unit.apply(UnityStatusEffects.plated);
                        this.load = 0.0F;
                        this.laserWidth = 0.0F;
                        this.items.remove(Reinforcer.this.consumes.getItem().items);
                    }
                }
            }

            this.load += 0.01F * Time.delta;
            this.laserWidth += 0.01F * Time.delta;
            if (this.load > Reinforcer.this.loadThreshold) {
                this.load = Reinforcer.this.loadThreshold;
            }

            if (this.laserWidth > 0.3F) {
                this.laserWidth = 0.3F;
            }

        }

        public void drawSelect() {
            Drawf.dashCircle(this.x, this.y, Reinforcer.this.range, this.team.color);
        }

        public void draw() {
            Draw.rect(Reinforcer.this.baseRegion, this.x, this.y);
            Draw.z(30.0F);
            Draw.rect(Reinforcer.this.region, this.x, this.y, this.rotation - 90.0F);
            if (this.prevUnit != null && this.laserWidth < 0.3F) {
                Draw.color(Reinforcer.this.laserColor);
                Draw.z(110.0F);
                if (Reinforcer.this.laserLength > 0.0F) {
                    this.posOffset.trns(this.rotation, Reinforcer.this.laserLength);
                }

                Drawf.laser(this.team, Reinforcer.this.laserRegion, Reinforcer.this.laserEndRegion, this.x + this.posOffset.x, this.y + this.posOffset.y, this.prevUnit.x, this.prevUnit.y, (0.3F - this.laserWidth) / 0.3F);
                Draw.color();
            }

        }

        public void turnToTarget(float targetRot) {
            this.rotation = Angles.moveToward(this.rotation, targetRot, Reinforcer.this.rotateSpeed * Time.delta);
        }
    }
}
