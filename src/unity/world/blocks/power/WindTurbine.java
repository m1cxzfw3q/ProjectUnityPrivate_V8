package unity.world.blocks.power;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Time;
import unity.graphics.UnityDrawf;

public class WindTurbine extends TorqueGenerator {
    public final TextureRegion[] overlayRegions = new TextureRegion[2];
    public final TextureRegion[] baseRegions = new TextureRegion[4];
    public final TextureRegion[] rotorRegions = new TextureRegion[2];
    public TextureRegion topRegion;
    public TextureRegion movingRegion;
    public TextureRegion bottomRegion;
    public TextureRegion mbaseRegion;

    public WindTurbine(String name) {
        super(name);
        this.solid = true;
    }

    public void load() {
        super.load();
        this.topRegion = Core.atlas.find(this.name + "-top");
        this.movingRegion = Core.atlas.find(this.name + "-moving");
        this.bottomRegion = Core.atlas.find(this.name + "-bottom");
        this.mbaseRegion = Core.atlas.find(this.name + "-mbase");

        for(int i = 0; i < 4; ++i) {
            this.baseRegions[i] = Core.atlas.find(this.name + "-base" + (i + 1));
        }

        for(int i = 0; i < 2; ++i) {
            this.overlayRegions[i] = Core.atlas.find(this.name + "-overlay" + (i + 1));
            this.rotorRegions[i] = Core.atlas.find(this.name + "-rotor" + (i + 1));
        }

    }

    public class WindTurbineBuild extends TorqueGenerator.TorqueGeneratorBuild {
        public WindTurbineBuild() {
            super(WindTurbine.this);
        }

        protected float generateTorque() {
            float x = Time.time * 0.001F;
            float mul = 0.4F * Math.max(0.0F, Mathf.sin(x) + 0.5F * Mathf.sin(2.0F * x + 50.0F) + 0.2F * Mathf.sin(7.0F * x + 90.0F) + 0.1F * Mathf.sin(23.0F * x + 10.0F) + 0.55F) + 0.15F;
            return mul;
        }

        public void draw() {
            float shaftRotog = this.torque().getRotation();
            int variant = (this.rotation + 1) % 4 >= 2 ? 1 : 0;
            float shaftRot = variant == 1 ? 360.0F - shaftRotog : shaftRotog;
            Draw.rect(WindTurbine.this.bottomRegion, this.x, this.y);
            Draw.rect(WindTurbine.this.baseRegions[this.rotation], this.x, this.y);
            Draw.rect(WindTurbine.this.mbaseRegion, this.x, this.y, this.rotdeg());
            UnityDrawf.drawRotRect(WindTurbine.this.movingRegion, this.x, this.y, 24.0F, 3.5F, 24.0F, this.rotdeg(), shaftRot, shaftRot + 180.0F);
            Draw.rect(WindTurbine.this.rotorRegions[1], this.x, this.y, shaftRotog);
            Draw.rect(WindTurbine.this.rotorRegions[0], this.x, this.y, shaftRotog * 2.0F);
            Draw.rect(WindTurbine.this.topRegion, this.x, this.y);
            this.drawTeamTop();
        }
    }
}
