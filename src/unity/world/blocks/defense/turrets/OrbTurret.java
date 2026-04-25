package unity.world.blocks.defense.turrets;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.Rand;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.Vars;
import mindustry.entities.bullet.BulletType;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.graphics.Pal;
import mindustry.graphics.Trail;
import mindustry.world.Tile;
import mindustry.world.blocks.defense.turrets.PowerTurret;
import unity.content.UnityFx;
import unity.graphics.TexturedTrail;

public class OrbTurret extends PowerTurret {
    public int orbsPerLayer = 3;
    public int layers = 2;
    public float bulletWidth = 2.0F;
    public Color bulletHeadColor;
    public Color bulletTrailColor;
    public float layerSpeedMultiplier;
    public float layerDamageMultiplier;

    public OrbTurret(String name) {
        super(name);
        this.bulletHeadColor = Color.white;
        this.bulletTrailColor = Pal.accent;
        this.layerSpeedMultiplier = 1.0F;
        this.layerDamageMultiplier = 1.0F;
        this.solid = true;
        this.update = true;
    }

    public class OrbTurretBuild extends PowerTurret.PowerTurretBuild {
        public Seq<TexturedTrail> trails = new Seq();
        public Rand rand = new Rand();
        public Seq<Float> offsets = new Seq();
        public float loader = 0.0F;

        public OrbTurretBuild() {
            super(OrbTurret.this);
        }

        public float getX(int i) {
            return this.x + Mathf.cosDeg(360.0F / (float)OrbTurret.this.orbsPerLayer * (float)(i % OrbTurret.this.orbsPerLayer) + Time.time * 5.0F + (Float)this.offsets.get(i / OrbTurret.this.orbsPerLayer)) * (OrbTurret.this.bulletWidth * 3.0F + OrbTurret.this.bulletWidth * 2.0F) * (float)(1 + i / OrbTurret.this.orbsPerLayer) * Mathf.cosDeg(90.0F + Time.time * 5.0F + (Float)this.offsets.get(i / OrbTurret.this.orbsPerLayer));
        }

        public float getY(int i) {
            return this.y + Mathf.sinDeg(360.0F / (float)OrbTurret.this.orbsPerLayer * (float)(i % OrbTurret.this.orbsPerLayer) + Time.time * 5.0F + (Float)this.offsets.get(i / OrbTurret.this.orbsPerLayer)) * (OrbTurret.this.bulletWidth * 3.0F + OrbTurret.this.bulletWidth * 2.0F) * (float)(1 + i / OrbTurret.this.orbsPerLayer);
        }

        public void addTrail(int i) {
            TexturedTrail trail = new TexturedTrail((TextureRegion)null, 3 + (1 + i / OrbTurret.this.layers) * 6);
            trail.mixAlpha = 1.0F;
            trail.baseWidth = OrbTurret.this.bulletWidth;
            this.trails.add(trail);
        }

        public Building init(Tile tile, Team team, boolean shouldAdd, int rotation) {
            for(int i = 0; i < OrbTurret.this.layers * OrbTurret.this.orbsPerLayer; ++i) {
                this.addTrail(i);
            }

            return super.init(tile, team, shouldAdd, rotation);
        }

        public void placed() {
            super.placed();

            for(int i = 0; i < OrbTurret.this.layers; ++i) {
                this.rand.setSeed((long)(this.pos() + i * 69));
                this.offsets.add(this.rand.nextFloat() * 420.0F);
            }

        }

        protected boolean validateTarget() {
            return super.validateTarget() && this.trails.size > 0;
        }

        protected void bullet(BulletType type, float angle) {
            int l = (int)Math.ceil((double)((float)this.trails.size / (float)OrbTurret.this.layers));
            float xP = this.getX(this.trails.size - 1) + OrbTurret.this.tr.x;
            float yP = this.getY(this.trails.size - 1) + OrbTurret.this.tr.y;
            Bullet bullet = type.create(this, this.team, xP, yP, Angles.angle(xP, yP, this.targetPos.x, this.targetPos.y), (1.0F + Mathf.range(OrbTurret.this.velocityInaccuracy)) * (1.0F + (float)l * OrbTurret.this.layerSpeedMultiplier), 1.0F);
            bullet.damage *= 1.0F + (float)l * OrbTurret.this.layerDamageMultiplier;
            if (!Vars.headless) {
                UnityFx.orbShot.at(xP, yP, this.team.color);
            }

            bullet.trail = (Trail)this.trails.pop();
            bullet.data = new Color[]{OrbTurret.this.bulletHeadColor, OrbTurret.this.bulletTrailColor};
        }

        public void update() {
            super.update();
            if (this.offsets.size == 0) {
                for(int i = 0; i < OrbTurret.this.layers; ++i) {
                    this.rand.setSeed((long)(this.pos() + i * 69));
                    this.offsets.add(this.rand.nextFloat() * 420.0F);
                }
            }

            for(int i = 0; i < this.trails.size; ++i) {
                ((TexturedTrail)this.trails.get(i)).update(this.getX(i), this.getY(i));
            }

            if (this.trails.size < OrbTurret.this.layers * OrbTurret.this.orbsPerLayer) {
                if (this.loader >= 1.0F) {
                    this.addTrail(this.trails.size);
                    this.loader = 0.0F;
                }

                this.loader += 0.05F;
            } else {
                this.loader = 0.0F;
            }

        }

        public void draw() {
            Draw.rect(OrbTurret.this.baseRegion, this.x, this.y);
            Draw.z(110.0F);
            this.trails.each((e) -> {
                Draw.color(OrbTurret.this.bulletTrailColor);
                e.draw(OrbTurret.this.bulletHeadColor, 1.0F);
                e.drawCap(OrbTurret.this.bulletHeadColor, 1.0F);
            });
            Draw.color();
        }

        protected void effects() {
            OrbTurret.this.shootSound.at(this.x + OrbTurret.this.tr.x, this.y + OrbTurret.this.tr.y, Mathf.random(0.9F, 1.1F));
            this.recoil = OrbTurret.this.recoilAmount;
        }
    }
}
