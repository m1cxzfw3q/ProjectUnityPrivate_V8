package unity.world.blocks.power;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import unity.graphics.UnityDrawf;

public class ElectricMotor extends TorqueGenerator {
    public final TextureRegion[] overlayRegions = new TextureRegion[2];
    public final TextureRegion[] baseRegions = new TextureRegion[2];
    public final TextureRegion[] coilRegions = new TextureRegion[2];
    public TextureRegion topRegion;
    public TextureRegion movingRegion;
    public TextureRegion bottomRegion;
    public TextureRegion mbaseRegion;

    public ElectricMotor(String name) {
        super(name);
        this.solid = true;
    }

    public void load() {
        super.load();
        this.topRegion = Core.atlas.find(this.name + "-top");
        this.movingRegion = Core.atlas.find(this.name + "-moving");
        this.bottomRegion = Core.atlas.find(this.name + "-bottom");
        this.mbaseRegion = Core.atlas.find(this.name + "-mbase");

        for(int i = 0; i < 2; ++i) {
            this.baseRegions[i] = Core.atlas.find(this.name + "-base" + (i + 1));
            this.overlayRegions[i] = Core.atlas.find(this.name + "-overlay" + (i + 1));
            this.coilRegions[i] = Core.atlas.find(this.name + "-coil" + (i + 1));
        }

    }

    public class ElectricMotorBuild extends TorqueGenerator.TorqueGeneratorBuild {
        public ElectricMotorBuild() {
            super(ElectricMotor.this);
        }

        protected float generateTorque() {
            return this.power.graph.getSatisfaction();
        }

        public void draw() {
            int variant = (this.rotation + 1) % 4 >= 2 ? 1 : 0;
            int rotVar = this.rotation % 2 == 1 ? 1 : 0;
            float shaftRot = this.torque().getRotation();
            if (variant == 1) {
                shaftRot = 360.0F - shaftRot;
            }

            Draw.rect(ElectricMotor.this.bottomRegion, this.x, this.y);
            Draw.rect(ElectricMotor.this.baseRegions[rotVar], this.x, this.y);
            Draw.rect(ElectricMotor.this.coilRegions[rotVar], this.x, this.y);
            Draw.rect(ElectricMotor.this.mbaseRegion, this.x, this.y, this.rotdeg());
            UnityDrawf.drawRotRect(ElectricMotor.this.movingRegion, this.x, this.y, 24.0F, 3.5F, 24.0F, this.rotdeg(), shaftRot, shaftRot + 180.0F);
            Draw.rect(ElectricMotor.this.overlayRegions[variant], this.x, this.y, this.rotdeg());
            Draw.rect(ElectricMotor.this.topRegion, this.x, this.y);
            this.drawTeamTop();
        }
    }
}
